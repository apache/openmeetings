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

import static org.apache.openmeetings.screenshare.Core.QUARTZ_GROUP_NAME;
import static org.apache.openmeetings.screenshare.gui.ScreenDimensions.FPS;
import static org.apache.openmeetings.screenshare.util.Util.getQurtzProps;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.openmeetings.screenshare.job.CursorJob;
import org.apache.openmeetings.screenshare.job.EncodeJob;
import org.apache.openmeetings.screenshare.job.SendJob;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.red5.server.api.Red5;
import org.red5.server.net.rtmp.event.VideoData;
import org.red5.server.stream.message.RTMPMessage;
import org.slf4j.Logger;

public class CaptureScreen extends Thread {
	private static final Logger log = getLogger(CaptureScreen.class);
	private final static String QUARTZ_CURSOR_TRIGGER_NAME = "CursorTrigger";
	private final static String QUARTZ_CURSOR_JOB_NAME = "CursorJob";
	private Core core;
	private int timestampDelta;
	private volatile AtomicInteger timestamp = new AtomicInteger(0);
	private volatile AtomicBoolean sendFrameGuard = new AtomicBoolean(false);
	private long startTime = 0;
	private volatile boolean active = true;
	private IScreenEncoder se;
	private IScreenShare client;
	private ArrayBlockingQueue<VideoData> frames = new ArrayBlockingQueue<VideoData>(2);
	private String host = null;
	private String app = null;
	private int port = -1;
	private Number streamId;
	private boolean startPublish = false;
	private Scheduler _scheduler;

	public CaptureScreen(Core coreScreenShare, IScreenShare client, String host, String app, int port) {
		core = coreScreenShare;
		this.client = client;
		this.host = host;
		this.app = app;
		this.port = port;
	}

	private Scheduler getScheduler() {
		if (_scheduler == null) {
			try {
				SchedulerFactory schdlrFactory = new StdSchedulerFactory(getQurtzProps("CaptureScreen"));
				_scheduler = schdlrFactory.getScheduler();
			} catch (SchedulerException e) {
				log.error("Unexpected error while creating scheduler", e);
			}
		}
		return _scheduler;
	}

	public void release() {
		try {
			if (_scheduler != null) {
				_scheduler.shutdown(true);
				_scheduler = null;
			}
		} catch (Exception e) {
			log.error("Unexpected error while shutting down scheduler", e);
		}
		active = false;
		timestamp = new AtomicInteger(0);
		startTime = 0;
	}

	@Override
	public void run() {
		try {
			while (active && !core.isReadyToRecord()) {
				Thread.sleep(60);
			}

			timestampDelta = 1000 / FPS;
			se = new ScreenV1Encoder(3 * FPS); //send keyframe every 3 seconds
			startTime = System.currentTimeMillis();

			JobDetail encodeJob = JobBuilder.newJob(EncodeJob.class).withIdentity("EncodeJob", QUARTZ_GROUP_NAME).build();
			encodeJob.getJobDataMap().put(EncodeJob.CAPTURE_KEY, this);
			Trigger encodeTrigger = TriggerBuilder.newTrigger()
					.withIdentity("EncodeTrigger", QUARTZ_GROUP_NAME)
					.withSchedule(simpleSchedule().withIntervalInMilliseconds(timestampDelta).repeatForever())
					.build();
			JobDetail sendJob = JobBuilder.newJob(SendJob.class).withIdentity("SendJob", QUARTZ_GROUP_NAME).build();
			Trigger sendTrigger = TriggerBuilder.newTrigger()
					.withIdentity("SendTrigger", QUARTZ_GROUP_NAME)
					.withSchedule(simpleSchedule().withIntervalInMilliseconds(timestampDelta).repeatForever())
					.build();
			sendJob.getJobDataMap().put(SendJob.CAPTURE_KEY, this);

			Scheduler s = getScheduler();
			s.scheduleJob(encodeJob, encodeTrigger);
			s.scheduleJob(sendJob, sendTrigger);
			s.start();
		} catch (Exception e) {
			log.error("Error while running: ", e);
		}
	}
	
	/*
	private void pushAudio(byte[] audio, long ts) {
		if (startPublish) {
			buffer.put((byte) 6);
			buffer.put(audio);
			buffer.flip();
	
			// I can stream audio
			//packets successfully using linear PCM at 11025Hz. For those packets I
			//push one byte (0x06) which specifies the format of audio data in a
			//ByteBuffer, and then real audio data:
			RTMPMessage rtmpMsg = RTMPMessage.build(new AudioData(buffer), (int) ts);
			client.publishStreamData(streamId, rtmpMsg);
		}
	}
	*/
	
	public void pushVideo(VideoData data, int ts) throws IOException {
		if (startPublish) {
			if (Red5.getConnectionLocal() == null) {
				Red5.setConnectionLocal(client.getConnection());
			}
			RTMPMessage rtmpMsg = RTMPMessage.build(data, ts);
			client.publishStreamData(streamId, rtmpMsg);
		}
	}

	public String getHost() {
		return host;
	}

	public String getApp() {
		return app;
	}

	public int getPort() {
		return port;
	}

	public Number getStreamId() {
		return streamId;
	}

	public void setStreamId(Number streamId) {
		this.streamId = streamId;
	}

	public void setStartPublish(boolean startPublish) {
		this.startPublish = startPublish;
	}

	public IScreenEncoder getEncoder() {
		return se;
	}

	public ArrayBlockingQueue<VideoData> getFrames() {
		return frames;
	}

	public void setSendFrameGuard(boolean b) {
		sendFrameGuard.set(b);
	}

	public boolean getSendFrameGuard() {
		return sendFrameGuard.get();
	}

	public AtomicInteger getTimestamp() {
		return timestamp;
	}

	public long getStartTime() {
		return startTime;
	}

	public int getTimestampDelta() {
		return timestampDelta;
	}

	public void sendCursorStatus() {
		core.sendCursorStatus();
	}

	public void setSendCursor(boolean sendCursor) {
		try {
			Scheduler s = getScheduler();
			if (sendCursor) {
				JobDetail cursorJob = JobBuilder.newJob(CursorJob.class).withIdentity(QUARTZ_CURSOR_JOB_NAME, QUARTZ_GROUP_NAME).build();
				Trigger cursorTrigger = TriggerBuilder.newTrigger()
						.withIdentity(QUARTZ_CURSOR_TRIGGER_NAME, QUARTZ_GROUP_NAME)
						.withSchedule(simpleSchedule().withIntervalInMilliseconds(1000 / Math.min(5, FPS)).repeatForever())
						.build();
				cursorJob.getJobDataMap().put(CursorJob.CAPTURE_KEY, this);
				s.scheduleJob(cursorJob, cursorTrigger);
			} else {
				s.deleteJob(JobKey.jobKey(QUARTZ_CURSOR_JOB_NAME, QUARTZ_GROUP_NAME));
			}
		} catch (SchedulerException e) {
			log.error("Unexpected Error schedule/unschedule cursor job", e);
		}
	}
}
