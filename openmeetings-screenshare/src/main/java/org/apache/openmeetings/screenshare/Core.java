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
package org.apache.openmeetings.screenshare;

import static java.lang.Boolean.TRUE;
import static org.apache.openmeetings.screenshare.util.Util.getQurtzProps;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.slf4j.LoggerFactory.getLogger;

import java.awt.MouseInfo;
import java.awt.Point;
import java.net.ConnectException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.openmeetings.screenshare.gui.ScreenDimensions;
import org.apache.openmeetings.screenshare.gui.ScreenSharerFrame;
import org.apache.openmeetings.screenshare.job.RemoteJob;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.red5.client.net.rtmp.INetStreamEventHandler;
import org.red5.io.utils.ObjectMap;
import org.red5.server.api.Red5;
import org.red5.server.api.event.IEvent;
import org.red5.server.api.service.IPendingServiceCall;
import org.red5.server.api.service.IPendingServiceCallback;
import org.red5.server.net.ICommand;
import org.red5.server.net.rtmp.RTMPConnection;
import org.red5.server.net.rtmp.event.Notify;
import org.red5.server.net.rtmp.status.StatusCodes;
import org.slf4j.Logger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

public class Core implements IPendingServiceCallback, INetStreamEventHandler {
	private static final Logger log = getLogger(Core.class);
	static final String QUARTZ_GROUP_NAME = "ScreenShare";
	static final String QUARTZ_REMOTE_JOB_NAME = "RemoteJob";
	static final String QUARTZ_REMOTE_TRIGGER_NAME = "RemoteTrigger";
	private static final String CONNECT_REJECTED = "NetConnection.Connect.Rejected";
	private static final String CONNECT_FAILED = "NetConnection.Connect.Failed";

	enum Protocol {
		rtmp, rtmpt, rtmpe, rtmps
	}
	private IScreenShare instance = null;
	private URI url;
	private URI fallback;
	private boolean fallbackUsed = false;
	private String host;
	private String app;
	private int port;

	private String sid;
	private CaptureScreen _capture = null;
	private RTMPClientPublish publishClient = null;

	private ScreenSharerFrame frame;

	private int defaultQuality = 1;
	private int defaultFps = 10;
	private boolean showFps = true;

	private boolean allowRecording = true;
	private boolean allowPublishing = true;

	private boolean startSharing = false;
	private boolean startRecording = false;
	private boolean startPublishing = false;
	private boolean connected = false;
	private boolean readyToRecord = false;
	private boolean audioNotify = false;
	private boolean remoteEnabled = true;
	private boolean nativeSsl = false;
	private SchedulerFactory schdlrFactory;
	private Scheduler schdlr;
	private LinkedBlockingQueue<Map<String, Object>> remoteEvents = new LinkedBlockingQueue<>();
	private final ScreenDimensions dim;

	public Core(String[] args) {
		dim = new ScreenDimensions();
		try {
			System.setProperty("org.terracotta.quartz.skipUpdateCheck", "true");
			for (String arg : args) {
				log.debug("arg: " + arg);
			}
			String[] textArray = null;
			if (args.length > 8) {
				url = new URI(args[0]);
				fallback = new URI(args[1]);
				sid = args[2];
				String labelTexts = args[3];
				defaultQuality = Integer.parseInt(args[4]);
				defaultFps = Integer.parseInt(args[5]);
				showFps = bool(args[6]);
				remoteEnabled = bool(args[7]);
				allowRecording = bool(args[8]);
				allowPublishing = bool(args[9]);
				nativeSsl = bool(args[10]);

				if (labelTexts.length() > 0) {
					textArray = labelTexts.split(";");

					log.debug("labelTexts :: " + labelTexts);
					log.debug("textArray Length " + textArray.length);
					for (int i = 0; i < textArray.length; i++) {
						log.debug(i + " :: " + textArray[i]);
					}
				}
			} else {
				System.exit(0);
			}
			schdlrFactory = new StdSchedulerFactory(getQurtzProps("CoreScreenShare"));
			schdlr = schdlrFactory.getScheduler();
			JobDetail remoteJob = JobBuilder.newJob(RemoteJob.class).withIdentity(QUARTZ_REMOTE_JOB_NAME, QUARTZ_GROUP_NAME).build();
			Trigger cursorTrigger = TriggerBuilder.newTrigger()
					.withIdentity(QUARTZ_REMOTE_TRIGGER_NAME, QUARTZ_GROUP_NAME)
					.withSchedule(simpleSchedule().withIntervalInMilliseconds(50).repeatForever())
					.build();
			remoteJob.getJobDataMap().put(RemoteJob.CORE_KEY, this);
			schdlr.scheduleJob(remoteJob, cursorTrigger);

			createWindow(textArray);
		} catch (Exception err) {
			log.error("", err);
		}
	}

	private CaptureScreen getCapture() {
		if (_capture == null) {
			_capture = new CaptureScreen(this, instance, host, app, port);
		}
		return _capture;
	}

	private void setInstance(URI uri) {
		Protocol protocol = Protocol.valueOf(uri.getScheme());
		host = uri.getHost();
		port = uri.getPort();
		app = uri.getPath().substring(1);

		switch (protocol) {
			case rtmp:
				instance = new RTMPScreenShare(this);
				break;
			case rtmpt:
				instance = new RTMPTScreenShare(this);
				break;
			case rtmps:
				if (nativeSsl) {
					RTMPSScreenShare client = new RTMPSScreenShare(this);
					instance = client;
				} else {
					instance = new RTMPTSScreenShare(this);
				}
				break;
			case rtmpe:
			default:
				throw new RuntimeException("Unsupported protocol");
		}
		instance.setServiceProvider(this);
		log.debug(String.format("host: %s, port: %s, app: %s, publish: %s", host, port, app, sid));
	}

	// ------------------------------------------------------------------------
	//
	// Main
	//
	// ------------------------------------------------------------------------
	public static void main(String[] args) {
		new Core(args);
	}

	// ------------------------------------------------------------------------
	//
	// GUI
	//
	// ------------------------------------------------------------------------
	public void createWindow(String[] textArray) {
		try {
			frame = new ScreenSharerFrame(this, textArray);
			frame.setVisible(true);
			frame.setRecordingTabEnabled(allowRecording);
			frame.setPublishingTabEnabled(allowPublishing);
			log.debug("initialized");
		} catch (Exception err) {
			log.error("createWindow Exception: ", err);
		}
	}

	public void sendCursorStatus() {
		try {
			Point mouseP = MouseInfo.getPointerInfo().getLocation();

			float scaleFactor = (1.0f * dim.getResizeX()) / dim.getSpinnerWidth();

			// Real size: Real mouse position = Resize : X
			int x = (int)((mouseP.getX() - dim.getSpinnerX()) * scaleFactor);
			int y = (int)((mouseP.getY() - dim.getSpinnerY()) * scaleFactor);

			if (instance.getConnection() != null) {
				if (Red5.getConnectionLocal() == null) {
					Red5.setConnectionLocal(instance.getConnection());
				}
				instance.invoke("setNewCursorPosition", new Object[] { x, y }, this);
			}
		} catch (NullPointerException npe) {
			//noop
		} catch (Exception err) {
			frame.setStatus("Exception: " + err);
			log.error("[sendCursorStatus]", err);
		}
	}

	/**
	 * @param id The streamid sent by server
	 */
	public void setId(String id) {
		//no-op
	}

	public void setConnectionAsSharingClient() {
		log.debug("########## setConnectionAsSharingClient");
		try {
			if (Red5.getConnectionLocal() == null) {
				Red5.setConnectionLocal(instance.getConnection());
			}
			Map<String, Object> map = new HashMap<>();

			int scaledWidth = dim.getResizeX();
			int scaledHeight = dim.getResizeY();

			map.put("screenWidth", scaledWidth);
			map.put("screenHeight", scaledHeight);
			map.put("startRecording", startRecording);
			map.put("startStreaming", startSharing);
			map.put("startPublishing", startPublishing);
			map.put("publishingHost", frame.getPublishHost());
			map.put("publishingApp", frame.getPublishApp());
			map.put("publishingId", frame.getPublishId());
			if (Red5.getConnectionLocal() == null) {
				Red5.setConnectionLocal(instance.getConnection());
			}
			instance.invoke("setConnectionAsSharingClient", new Object[] { map }, this);
		} catch (Exception err) {
			frame.setStatus("Error: " + err.getLocalizedMessage());
			log.error("[setConnectionAsSharingClient]", err);
		}
	}

	public void sharingStart() {
		try {
			schdlr.start();
		} catch (SchedulerException e) {
			log.error("[schdlr.start]", e);
		}
		startSharing = true;
		captureScreenStart();
	}

	public void recordingStart() {
		startRecording= true;
		captureScreenStart();
	}

	public void publishingStart() {
		startPublishing = true;
		captureScreenStart();
	}

	private void connect(String sid) {
		setInstance(fallbackUsed ? fallback : url);
		Map<String, Object> map = instance.makeDefaultConnectionParams(host, port, app);
		map.put("screenClient", true);
		Map<String, Object> params = new HashMap<>();
		params.put("sid", sid);
		instance.connect(host, port, map, this, new Object[]{params});
	}

	void handleException(Throwable e) {
		frame.setStatus("Exception: " + e);
		if (e instanceof ConnectException) {
			fallbackUsed = true;
			connect(sid);
		}
	}

	private void captureScreenStart() {
		try {
			log.debug("captureScreenStart");

			if (!connected) {
				connect(sid);
			} else {
				setConnectionAsSharingClient();
			}
		} catch (Exception err) {
			log.error("captureScreenStart Exception: ", err);
			frame.setStatus("Exception: " + err);
		}
	}

	public void sharingStop() {
		startSharing = false;
		captureScreenStop("stopStreaming");
	}

	public void recordingStop() {
		startRecording = false;
		captureScreenStop("stopRecording");
	}

	public void publishingStop() {
		startPublishing = false;
		captureScreenStop("stopPublishing");
	}

	private void captureScreenStop(String action) {
		try {
			log.debug("INVOKE screenSharerAction" );

			Map<String, Object> map = new HashMap<>();
			map.put(action, true);

			if (Red5.getConnectionLocal() == null) {
				Red5.setConnectionLocal(instance.getConnection());
			}
			instance.invoke("screenSharerAction", new Object[] { map }, this);
		} catch (Exception err) {
			log.error("captureScreenStop Exception: ", err);
			frame.setStatus("Exception: " + err);
		}
	}

	public void stopSharing() {
		try {
			schdlr.standby();
		} catch (SchedulerException e) {
			log.error("[schdlr.standby]", e);
		}
		frame.setSharingStatus(false, !startPublishing && !startRecording && !startSharing);
		startSharing = false;
	}

	public void stopRecording() {
		frame.setRecordingStatus(false, !startPublishing && !startRecording && !startSharing);
		startRecording = false;
	}

	public void stopPublishing() {
		frame.setPublishingStatus(false, !startPublishing && !startRecording && !startSharing);
		startPublishing = false;
		if (publishClient != null) {
			publishClient.disconnect();
			publishClient = null;
		}
	}

	public synchronized boolean isReadyToRecord() {
		return readyToRecord;
	}

	private synchronized void setReadyToRecord(boolean readyToRecord) {
		this.readyToRecord = readyToRecord;
	}

	/**
	 * @param command - command to be processed
	 */
	protected void onCommand(ICommand command) {
		if (!(command instanceof Notify)) {
			return;
		}
		Notify invoke = (Notify)command;
		if (invoke.getType() == IEvent.Type.STREAM_DATA) {
			return;
		}

		String method = invoke.getCall().getServiceMethodName();
		if ("screenSharerAction".equals(method)) {
			Object[] args = invoke.getCall().getArguments();
			if (args != null && args.length > 0) {
				@SuppressWarnings("unchecked")
				Map<String, Object> params = (Map<String, Object>)args[0];
				if (bool(params.get("stopPublishing"))) {
					stopPublishing();
				}
				if (params.containsKey("error")) {
					frame.setStatus("" + params.get("error"));
				}
			}
		}
	}

	/**
	 * Will stop any activity and disconnect
	 *
	 * @param obj - dummy unused param to perform the call
	 */
	public void stopStream(Object obj) {
		try {
			log.debug("ScreenShare stopStream");

			stopSharing();
			stopRecording();
			stopPublishing();
			connected = false;

			if (instance != null) {
				instance.disconnect();
			}
			setReadyToRecord(false);
			getCapture().setStartPublish(false);
			getCapture().release();
			_capture = null;
		} catch (Exception e) {
			log.error("ScreenShare stopStream exception " + e);
		}
	}

	@Override
	public void onStreamEvent(Notify notify) {
		log.debug( "onStreamEvent " + notify );

		@SuppressWarnings("rawtypes")
		ObjectMap map = (ObjectMap) notify.getCall().getArguments()[0];
		String code = (String) map.get("code");

		if (StatusCodes.NS_PUBLISH_START.equals(code)) {
			log.debug( "onStreamEvent Publish start" );
			getCapture().setStartPublish(true);
			setReadyToRecord(true);
		}
	}

	private static boolean bool(Object b) {
		return TRUE.equals(Boolean.valueOf("" + b));
	}

	public void sendRemoteCursorEvent(Map<String, Object> obj) {
		if (!remoteEnabled) {
			return;
		}
		log.trace("#### sendRemoteCursorEvent ");
		log.trace("Result Map Type "+ obj);

		if (obj != null) {
			remoteEvents.offer(obj);
			log.trace("Action offered:: {}, count: {}", obj, remoteEvents.size());
		}
	}

	@Override
	public void resultReceived(IPendingServiceCall call) {
		try {
			log.trace("service call result: " + call);
			if (call == null) {
				return;
			}

			String method = call.getServiceMethodName();
			Object o = call.getResult();
			if (log.isTraceEnabled()) {
				log.trace("Result Map Type " + (o == null ? null : o.getClass().getName()));
				log.trace("" + o);
			}
			@SuppressWarnings("unchecked")
			Map<String, Object> returnMap = (o != null && o instanceof Map) ? (Map<String, Object>) o : new HashMap<>();
			log.trace("call ### get Method Name " + method);
			if ("connect".equals(method)) {
				Object code = returnMap.get("code");
				if (CONNECT_FAILED.equals(code) && !fallbackUsed) {
					fallbackUsed = true;
					connect(sid);
					frame.setStatus("Re-connecting using fallback");
					return;
				}
				if (CONNECT_FAILED.equals(code) || CONNECT_REJECTED.equals(code)) {
					frame.setStatus(String.format("Error: %s %s", code, returnMap.get("description")));
					return;
				}
				connected = true;
				setConnectionAsSharingClient();
			} else if ("setConnectionAsSharingClient".equals(method)) {
				if (!bool(returnMap.get("alreadyPublished"))) {
					log.trace("Stream not yet started - do it ");

					instance.createStream(this);
				} else {
					log.trace("The Stream was already started ");
				}
				if (o != null) {
					Object modus = returnMap.get("modus");
					if ("startStreaming".equals(modus)) {
						frame.setSharingStatus(true, false);
					} else if ("startRecording".equals(modus)) {
						frame.setRecordingStatus(true, false);
					} else if ("startPublishing".equals(modus)) {
						frame.setPublishingStatus(true, false);
						publishClient = new RTMPClientPublish(
							this
							, frame.getPublishHost()
							, frame.getPublishApp()
							, frame.getPublishId());
						publishClient.connect();
					}
				} else {
					String err = "Could not aquire modus for event setConnectionAsSharingClient";
					frame.setStatus(String.format("Error: %s", err));
					return;
				}
			} else if ("createStream".equals(method)) {
				if (startRecording || startSharing) {
					CaptureScreen capture = getCapture();
					if (o != null && o instanceof Number) {
						if (capture.getStreamId() != null) {
							instance.unpublish(capture.getStreamId());
						}
						capture.setStreamId((Number)o);
					}
					final String broadcastId = UUID.randomUUID().toString();
					log.debug("createPublishStream result stream id: {}; name: {}", capture.getStreamId(), broadcastId);
					instance.publish(capture.getStreamId(), broadcastId, "live", this);

					log.debug("setup capture thread spinnerWidth = {}; spinnerHeight = {};", dim.getSpinnerWidth(), dim.getSpinnerHeight());

					if (!capture.isStarted()) {
						capture.setSendCursor(startSharing);
						capture.start();
					}
				}
			} else if ("screenSharerAction".equals(method)) {
				Object result = returnMap.get("result");
				if ("stopAll".equals(result)) {
					log.trace("Stopping to stream, there is neither a Desktop Sharing nor Recording anymore");
					stopStream(null);
				} else if ("stopSharingOnly".equals(result)) {
					stopSharing();
				} else if ("stopRecordingOnly".equals(result)) {
					stopRecording();
				} else if ("stopPublishingOnly".equals(result)) {
					stopPublishing();
				}
			} else if ("setNewCursorPosition".equals(method)) {
				// Do not do anything
			} else {
				log.debug("Unknown method " + method);
			}

		} catch (Exception err) {
			log.error("[resultReceived]", err);
		}
	}

	public boolean isAudioNotify() {
		return audioNotify;
	}

	public void setAudioNotify(boolean audioNotify) {
		this.audioNotify = audioNotify;
	}

	public boolean isRemoteEnabled() {
		return remoteEnabled;
	}

	public void setRemoteEnabled(boolean remoteEnabled) {
		this.remoteEnabled = remoteEnabled;
	}

	public void setDeadlockGuard(RTMPConnection conn) {
		ThreadPoolTaskScheduler deadlockGuard = new ThreadPoolTaskScheduler();
		deadlockGuard.setPoolSize(16);
		deadlockGuard.setDaemon(false);
		deadlockGuard.setWaitForTasksToCompleteOnShutdown(true);
		deadlockGuard.setThreadNamePrefix("DeadlockGuardScheduler-");
		deadlockGuard.afterPropertiesSet();
		conn.setDeadlockGuardScheduler(deadlockGuard);
	}

	public IScreenShare getInstance() {
		return instance;
	}

	public LinkedBlockingQueue<Map<String, Object>> getRemoteEvents() {
		return remoteEvents;
	}

	public ScreenDimensions getDim() {
		return dim;
	}

	public int getDefaultQuality() {
		return defaultQuality;
	}

	public int getDefaultFps() {
		return defaultFps;
	}

	public boolean isShowFps() {
		return showFps;
	}
}
