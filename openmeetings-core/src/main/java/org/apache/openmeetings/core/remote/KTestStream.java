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

import static org.apache.openmeetings.core.remote.KurentoHandler.newTestKurentoMsg;
import static org.apache.openmeetings.core.remote.KurentoHandler.sendError;
import static org.apache.openmeetings.util.OmFileHelper.TEST_SETUP_PREFIX;
import static org.apache.openmeetings.util.OmFileHelper.getStreamsDir;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.openmeetings.core.util.WebSocketHelper;
import org.apache.openmeetings.db.entity.basic.IWsClient;
import org.kurento.client.Continuation;
import org.kurento.client.EndOfStreamEvent;
import org.kurento.client.ErrorEvent;
import org.kurento.client.EventListener;
import org.kurento.client.IceCandidate;
import org.kurento.client.IceCandidateFoundEvent;
import org.kurento.client.MediaPipeline;
import org.kurento.client.MediaProfileSpecType;
import org.kurento.client.MediaSessionStartedEvent;
import org.kurento.client.MediaType;
import org.kurento.client.PlayerEndpoint;
import org.kurento.client.RecorderEndpoint;
import org.kurento.client.RecordingEvent;
import org.kurento.client.StoppedEvent;
import org.kurento.client.WebRtcEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.openjson.JSONObject;

public class KTestStream implements IKStream {
	private final static Logger log = LoggerFactory.getLogger(KTestStream.class);
	private MediaPipeline pipeline;
	private WebRtcEndpoint webRtcEndpoint;
	private PlayerEndpoint player;
	private RecorderEndpoint recorder;
	private String recPath = null;
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	private final String uid;
	private ScheduledFuture<?> recHandle;
	private int recTime;

	public KTestStream(final KurentoHandler h, IWsClient _c, JSONObject msg, MediaPipeline pipeline) {
		this.pipeline = pipeline;
		this.uid = _c.getUid();
		webRtcEndpoint = new WebRtcEndpoint.Builder(pipeline).build();
		webRtcEndpoint.connect(webRtcEndpoint);

		MediaProfileSpecType profile = getProfile(msg);
		initRecPath();
		recorder = new RecorderEndpoint.Builder(pipeline, recPath)
				.stopOnEndOfStream()
				.withMediaProfile(profile).build();

		recorder.addRecordingListener(new EventListener<RecordingEvent>() {
			@Override
			public void onEvent(RecordingEvent event) {
				recTime = 0;
				recHandle = scheduler.scheduleAtFixedRate(
						() -> WebSocketHelper.sendClient(_c, newTestKurentoMsg().put("id", "recording").put("time", recTime++))
						, 0, 1, TimeUnit.SECONDS);
				scheduler.schedule(() -> {
						recorder.stop();
						recHandle.cancel(true);
					}, 5, TimeUnit.SECONDS);
			}
		});
		recorder.addStoppedListener(new EventListener<StoppedEvent>() {
			@Override
			public void onEvent(StoppedEvent event) {
				WebSocketHelper.sendClient(_c, newTestKurentoMsg().put("id", "recStopped"));
				release(h);
			}
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

		addIceListener(_c);

		WebSocketHelper.sendClient(_c, newTestKurentoMsg()
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
				sendError(_c, "Failed to start recording");
				log.error("Failed to start recording", cause);
			}
		});
	}

	public void play(final KurentoHandler h, final IWsClient _c, JSONObject msg, MediaPipeline pipeline) {
		this.pipeline = pipeline;
		webRtcEndpoint = new WebRtcEndpoint.Builder(pipeline).build();
		player = new PlayerEndpoint.Builder(pipeline, recPath).build();
		player.connect(webRtcEndpoint);
		webRtcEndpoint.addMediaSessionStartedListener(new EventListener<MediaSessionStartedEvent>() {
			@Override
			public void onEvent(MediaSessionStartedEvent event) {
				log.info("Media session started {}", event);
				player.addErrorListener(new EventListener<ErrorEvent>() {
					@Override
					public void onEvent(ErrorEvent event) {
						log.info("ErrorEvent for player with uid '{}': {}", _c.getUid(), event.getDescription());
						sendPlayEnd(h, _c);
					}
				});
				player.addEndOfStreamListener(new EventListener<EndOfStreamEvent>() {
					@Override
					public void onEvent(EndOfStreamEvent event) {
						log.info("EndOfStreamEvent for player with uid '{}'", _c.getUid());
						sendPlayEnd(h, _c);
					}
				});
				player.play();
			}
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
		webRtcEndpoint.addIceCandidateFoundListener(new EventListener<IceCandidateFoundEvent>() {
			@Override
			public void onEvent(IceCandidateFoundEvent event) {
				IceCandidate cand = event.getCandidate();
				WebSocketHelper.sendClient(_c, newTestKurentoMsg()
						.put("id", "iceCandidate")
						.put("candidate", new JSONObject()
								.put("candidate", cand.getCandidate())
								.put("sdpMid", cand.getSdpMid())
								.put("sdpMLineIndex", cand.getSdpMLineIndex())));
			}
		});
	}

	private void sendPlayEnd(final KurentoHandler h, IWsClient _c) {
		WebSocketHelper.sendClient(_c, newTestKurentoMsg().put("id", "playStopped"));
		release(h);
	}

	private static MediaProfileSpecType getProfile(JSONObject msg) {
		boolean a  = msg.getBoolean("audio"), v = msg.getBoolean("video");
		if (a && v) {
			return MediaProfileSpecType.WEBM;
		} else if (v) {
			return MediaProfileSpecType.WEBM_VIDEO_ONLY;
		} else {
			return MediaProfileSpecType.WEBM_AUDIO_ONLY;
		}
	}

	private void initRecPath() {
		try {
			File f = new File(getStreamsDir(), String.format("%s%s.webm", TEST_SETUP_PREFIX, UUID.randomUUID()));
			recPath = String.format("file://%s", f.getCanonicalPath());
			log.info("Configured to record to {}", recPath);
		} catch (IOException e) {
			log.error("Uexpected error while creating recording URI", e);
		}
	}

	@Override
	public void release(KurentoHandler h) {
		if (webRtcEndpoint != null) {
			webRtcEndpoint.release();
			webRtcEndpoint = null;
		}
		if (pipeline != null) {
			pipeline.release(new Continuation<Void>() {
				@Override
				public void onSuccess(Void result) throws Exception {
					log.info("Pipeline released successfully");
				}

				@Override
				public void onError(Throwable cause) throws Exception {
					log.info("Error releasing pipeline ", cause);
				}
			});
			pipeline = null;
		}
		if (player != null) {
			player.release(new Continuation<Void>() {
				@Override
				public void onSuccess(Void result) throws Exception {
					log.info("Pipeline released successfully");
				}

				@Override
				public void onError(Throwable cause) throws Exception {
					log.info("Error releasing pipeline ", cause);
				}
			});
			player = null;
		}
		if (recorder != null) {
			recorder.release(new Continuation<Void>() {
				@Override
				public void onSuccess(Void result) throws Exception {
					log.info("Pipeline released successfully");
				}

				@Override
				public void onError(Throwable cause) throws Exception {
					log.info("Error releasing pipeline ", cause);
				}
			});
			recorder = null;
		}
		h.testsByUid.remove(uid);
	}
}
