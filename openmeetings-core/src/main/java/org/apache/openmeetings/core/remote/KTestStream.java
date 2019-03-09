/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License") +  you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.openmeetings.core.remote;

import static java.util.UUID.randomUUID;
import static org.apache.openmeetings.core.remote.KurentoHandler.PARAM_CANDIDATE;
import static org.apache.openmeetings.core.remote.KurentoHandler.sendError;
import static org.apache.openmeetings.core.remote.TestStreamProcessor.newTestKurentoMsg;
import static org.apache.openmeetings.util.OmFileHelper.EXTENSION_WEBM;
import static org.apache.openmeetings.util.OmFileHelper.TEST_SETUP_PREFIX;
import static org.apache.openmeetings.util.OmFileHelper.getStreamsDir;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.openmeetings.core.util.WebSocketHelper;
import org.apache.openmeetings.db.entity.basic.IWsClient;
import org.apache.openmeetings.util.OmFileHelper;
import org.kurento.client.Continuation;
import org.kurento.client.IceCandidate;
import org.kurento.client.MediaPipeline;
import org.kurento.client.MediaProfileSpecType;
import org.kurento.client.MediaType;
import org.kurento.client.PlayerEndpoint;
import org.kurento.client.RecorderEndpoint;
import org.kurento.client.WebRtcEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.openjson.JSONObject;

public class KTestStream extends AbstractStream {
	private static final Logger log = LoggerFactory.getLogger(KTestStream.class);
	private MediaPipeline pipeline;
	private WebRtcEndpoint webRtcEndpoint;
	private PlayerEndpoint player;
	private RecorderEndpoint recorder;
	private String recPath = null;
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	private ScheduledFuture<?> recHandle;
	private int recTime;

	public KTestStream(IWsClient c, JSONObject msg, MediaPipeline pipeline) {
		super(null, c.getUid());
		this.pipeline = pipeline;
		webRtcEndpoint = createWebRtcEndpoint(pipeline);
		webRtcEndpoint.connect(webRtcEndpoint);

		MediaProfileSpecType profile = getProfile(msg);
		initRecPath();
		recorder = createRecorderEndpoint(pipeline, recPath, profile);

		recorder.addRecordingListener(evt -> {
				recTime = 0;
				recHandle = scheduler.scheduleAtFixedRate(
						() -> WebSocketHelper.sendClient(c, newTestKurentoMsg().put("id", "recording").put("time", recTime++))
						, 0, 1, TimeUnit.SECONDS);
				scheduler.schedule(() -> {
						recorder.stop();
						recHandle.cancel(true);
					}, 5, TimeUnit.SECONDS);
			});
		recorder.addStoppedListener(evt -> {
				WebSocketHelper.sendClient(c, newTestKurentoMsg().put("id", "recStopped"));
				releaseRecorder();
			});
		switch (profile) {
			case WEBM:
				webRtcEndpoint.connect(recorder, MediaType.AUDIO);
				webRtcEndpoint.connect(recorder, MediaType.VIDEO);
				break;
			case WEBM_AUDIO_ONLY:
				webRtcEndpoint.connect(recorder, MediaType.AUDIO);
				break;
			case WEBM_VIDEO_ONLY:
				webRtcEndpoint.connect(recorder, MediaType.VIDEO);
				break;
			default:
				//no-op
				break;
		}

		String sdpOffer = msg.getString("sdpOffer");
		String sdpAnswer = webRtcEndpoint.processOffer(sdpOffer);

		addIceListener(c);

		WebSocketHelper.sendClient(c, newTestKurentoMsg()
				.put("id", "startResponse")
				.put("sdpAnswer", sdpAnswer));
		webRtcEndpoint.gatherCandidates();
		recorder.record(new Continuation<Void>() {
			@Override
			public void onSuccess(Void result) throws Exception {
				log.info("Recording started successfully");
			}

			@Override
			public void onError(Throwable cause) throws Exception {
				sendError(c, "Failed to start recording");
				log.error("Failed to start recording", cause);
			}
		});
	}

	public void play(final IWsClient _c, JSONObject msg, MediaPipeline pipeline) {
		this.pipeline = pipeline;
		webRtcEndpoint = createWebRtcEndpoint(pipeline);
		player = createPlayerEndpoint(pipeline, recPath);
		player.connect(webRtcEndpoint);
		webRtcEndpoint.addMediaSessionStartedListener(evt -> {
				log.info("Media session started {}", evt);
				player.addErrorListener(event -> {
						log.info("ErrorEvent for player with uid '{}': {}", _c.getUid(), event.getDescription());
						sendPlayEnd(_c);
					});
				player.addEndOfStreamListener(event -> {
						log.info("EndOfStreamEvent for player with uid '{}'", _c.getUid());
						sendPlayEnd(_c);
					});
				player.play();
			});

		String sdpOffer = msg.getString("sdpOffer");
		String sdpAnswer = webRtcEndpoint.processOffer(sdpOffer);

		addIceListener(_c);

		WebSocketHelper.sendClient(_c, newTestKurentoMsg()
				.put("id", "playResponse")
				.put("sdpAnswer", sdpAnswer));

		webRtcEndpoint.gatherCandidates();
	}

	public void addCandidate(IceCandidate cand) {
		if (webRtcEndpoint != null) {
			webRtcEndpoint.addIceCandidate(cand);
		}
	}

	private void addIceListener(IWsClient _c) {
		webRtcEndpoint.addIceCandidateFoundListener(evt -> {
				IceCandidate cand = evt.getCandidate();
				WebSocketHelper.sendClient(_c, newTestKurentoMsg()
						.put("id", "iceCandidate")
						.put(PARAM_CANDIDATE, new JSONObject()
								.put(PARAM_CANDIDATE, cand.getCandidate())
								.put("sdpMid", cand.getSdpMid())
								.put("sdpMLineIndex", cand.getSdpMLineIndex())));
			});
	}

	private void sendPlayEnd(IWsClient _c) {
		WebSocketHelper.sendClient(_c, newTestKurentoMsg().put("id", "playStopped"));
		releasePlayer();
	}

	private static MediaProfileSpecType getProfile(JSONObject msg) {
		boolean a  = msg.getBoolean("audio")
				, v = msg.getBoolean("video");
		if (a && v) {
			return MediaProfileSpecType.WEBM;
		} else if (v) {
			return MediaProfileSpecType.WEBM_VIDEO_ONLY;
		} else {
			return MediaProfileSpecType.WEBM_AUDIO_ONLY;
		}
	}

	private void initRecPath() {
		File f = new File(getStreamsDir(), String.format("%s%s.%s", TEST_SETUP_PREFIX, randomUUID(), EXTENSION_WEBM));
		recPath = OmFileHelper.getRecUri(f);
	}

	private void releasePipeline() {
		if (pipeline != null) {
			pipeline.release();
			pipeline = null;
		}
	}

	private void releaseRecorder() {
		releasePipeline();
		if (recorder != null) {
			recorder.release();
			recorder = null;
		}
	}

	private void releasePlayer() {
		releasePipeline();
		if (player != null) {
			player.release();
			player = null;
		}
	}

	@Override
	public void release(IStreamProcessor processor, boolean remove) {
		if (webRtcEndpoint != null) {
			webRtcEndpoint.release();
			webRtcEndpoint = null;
		}
		releasePlayer();
		releaseRecorder();
		processor.release(this);
	}
}
