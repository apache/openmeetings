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
package org.apache.openmeetings.screen.webstart;

import static org.apache.openmeetings.screen.webstart.gui.ScreenDimensions.FPS;
import static org.apache.openmeetings.screen.webstart.gui.ScreenDimensions.spinnerHeight;
import static org.apache.openmeetings.screen.webstart.gui.ScreenDimensions.spinnerWidth;
import static org.apache.openmeetings.screen.webstart.gui.ScreenDimensions.spinnerX;
import static org.apache.openmeetings.screen.webstart.gui.ScreenDimensions.spinnerY;
import static org.slf4j.LoggerFactory.getLogger;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.red5.server.api.Red5;
import org.red5.server.net.rtmp.event.VideoData;
import org.red5.server.stream.message.RTMPMessage;
import org.slf4j.Logger;

class CaptureScreen extends Thread {
	private static final Logger log = getLogger(CaptureScreen.class);
	private static final int NANO_MULTIPLIER = 1000 * 1000;
	private CoreScreenShare core;
	private int timeBetweenFrames;
	private volatile int timestamp = 0;
	private long timeCaptureStarted = 0;
	private volatile boolean active = true;
	private IScreenEncoder se;
	private IScreenShare client;
	private ArrayBlockingQueue<VideoData> frames = new ArrayBlockingQueue<VideoData>(2);
	private String host = null;
	private String app = null;
	private int port = -1;
	private int streamId;
	private boolean startPublish = false;
	private boolean sendCursor = false;
	private final ScheduledExecutorService cursorScheduler = Executors.newScheduledThreadPool(1);
	private Scheduler scheduler;

	public CaptureScreen(CoreScreenShare coreScreenShare, IScreenShare client, String host, String app, int port) {
		core = coreScreenShare;
		this.client = client;
		this.host = host;
		this.app = app;
		this.port = port;
	}

	public void release() {
		try {
			if (scheduler != null) {
				scheduler.shutdown(true);
				scheduler = null;
			}
		} catch (Exception e) {
			log.error("Unexpected error while shutting down scheduler", e);
		}
		try {
			cursorScheduler.shutdownNow();
		} catch (Exception e) {
			log.error("Unexpected error while shutting down scheduler", e);
		}
		active = false;
		timestamp = 0;
		timeCaptureStarted = 0;
	}

	public void run() {
		try {
			while (active && !core.isReadyToRecord()) {
				Thread.sleep(60);
			}

			timeBetweenFrames = 1000 / FPS;
			se = new ScreenV1Encoder(3 * FPS); //send keyframe every 3 seconds
			timeCaptureStarted = System.currentTimeMillis();

			JobDetail encodeJob = JobBuilder.newJob(EncodeJob.class).withIdentity("EncodeJob", "ScreenShare").build();
			encodeJob.getJobDataMap().put(EncodeJob.CAPTURE_KEY, this);
			Trigger encodeTrigger = TriggerBuilder.newTrigger()
					.withIdentity("EncodeTrigger", "ScreenShare")
					.withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInMilliseconds(timeBetweenFrames).repeatForever())
					.build();
			JobDetail sendJob = JobBuilder.newJob(SendJob.class).withIdentity("SendJob", "ScreenShare").build();
			Trigger sendTrigger = TriggerBuilder.newTrigger()
					.withIdentity("SendTrigger", "ScreenShare")
					.withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInMilliseconds(timeBetweenFrames).repeatForever())
					.build();
			sendJob.getJobDataMap().put(SendJob.CAPTURE_KEY, this);

			SchedulerFactory sf = new StdSchedulerFactory();
			scheduler = sf.getScheduler();
			scheduler.scheduleJob(encodeJob, encodeTrigger);
			scheduler.scheduleJob(sendJob, sendTrigger);
			scheduler.start();
			
			if (sendCursor) {
				cursorScheduler.scheduleWithFixedDelay(new Runnable() {
					public void run() {
						core.sendCursorStatus();
					}
				}, 0, timeBetweenFrames * NANO_MULTIPLIER, TimeUnit.NANOSECONDS);
			}
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
	
	@DisallowConcurrentExecution
	public static class EncodeJob implements Job {
		private static final String CAPTURE_KEY = "capture";
		Robot robot;
		Rectangle screen = new Rectangle(spinnerX, spinnerY, spinnerWidth, spinnerHeight);
		int[][] image = null;
		
		public EncodeJob() {
			try {
				robot = new Robot();
			} catch (AWTException e) {
				log.error("Unexpected Error while creating robot", e);
			}
		}
		
		@Override
		public void execute(JobExecutionContext context) throws JobExecutionException {
			JobDataMap data = context.getJobDetail().getJobDataMap();
			CaptureScreen capture = (CaptureScreen)data.get(CAPTURE_KEY);
			
			long start = System.currentTimeMillis();
			image = ScreenV1Encoder.getImage(screen, robot);
			if (log.isTraceEnabled()) {
				log.trace(String.format("Image was captured in %s ms", System.currentTimeMillis() - start));
			}
			start = System.currentTimeMillis();
			try {
				VideoData vData = capture.se.encode(image);
				if (log.isTraceEnabled()) {
					long now = System.currentTimeMillis();
					log.trace(String.format("Image was encoded in %s ms, timestamp is %s", now - start, now - capture.timeCaptureStarted));
				}
				capture.frames.offer(vData);
				capture.se.createUnalteredFrame();
			} catch (Exception e) {
				log.error("Error while encoding: ", e);
			}
		}
	}
	
	public static class SendJob implements Job {
		private static final String CAPTURE_KEY = "capture";
		public SendJob() {}
		
		@Override
		public void execute(JobExecutionContext context) throws JobExecutionException {
			JobDataMap data = context.getJobDetail().getJobDataMap();
			CaptureScreen capture = (CaptureScreen)data.get(CAPTURE_KEY);
			if (log.isTraceEnabled()) {
				long real = System.currentTimeMillis() - capture.timeCaptureStarted;
				log.trace(String.format("Enter method, timestamp: %s, real: %s, diff: %s", capture.timestamp, real, real - capture.timestamp));
			}
			VideoData f = capture.frames.poll();
			if (log.isTraceEnabled()) {
				log.trace(String.format("Getting %s image", f == null ? "DUMMY" : "CAPTURED"));
			}
			f = f == null ? capture.se.getUnalteredFrame() : f;
			if (f != null) {
				try {
					capture.pushVideo(f, capture.timestamp);
					if (log.isTraceEnabled()) {
						long real = System.currentTimeMillis() - capture.timeCaptureStarted;
						log.trace(String.format("Sending video, timestamp: %s, real: %s, diff: %s", capture.timestamp, real, real - capture.timestamp));
					}
					capture.timestamp += capture.timeBetweenFrames;
				} catch (IOException e) {
					log.error("Error while sending: ", e);
				}
			}
		}
	}
	
	private void pushVideo(VideoData data, int ts) throws IOException {
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

	public int getStreamId() {
		return streamId;
	}

	public void setStreamId(int streamId) {
		this.streamId = streamId;
	}

	public void setStartPublish(boolean startPublish) {
		this.startPublish = startPublish;
	}

	public void setSendCursor(boolean sendCursor) {
		this.sendCursor = sendCursor;
	}
}
