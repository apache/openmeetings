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

import static org.apache.openmeetings.screen.webstart.gui.ScreenDimensions.quality;
import static org.apache.openmeetings.screen.webstart.gui.ScreenDimensions.spinnerHeight;
import static org.apache.openmeetings.screen.webstart.gui.ScreenDimensions.spinnerWidth;
import static org.apache.openmeetings.screen.webstart.gui.ScreenDimensions.spinnerX;
import static org.apache.openmeetings.screen.webstart.gui.ScreenDimensions.spinnerY;
import static org.slf4j.LoggerFactory.getLogger;

import java.awt.Rectangle;
import java.awt.Robot;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.mina.core.buffer.IoBuffer;
import org.red5.server.net.rtmp.event.VideoData;
import org.red5.server.stream.message.RTMPMessage;
import org.slf4j.Logger;

final class CaptureScreen extends Thread {
	private static final Logger log = getLogger(CaptureScreen.class);
	private CoreScreenShare core;
	private int timeBetweenFrames;
	private volatile long timestamp = 0;
	private volatile boolean active = true;
	private IScreenEncoder se;
	private IScreenShare client;
	private IoBuffer buffer;
	private String host = null;
	private String app = null;
	private int port = -1;
	private int streamId;
	private boolean startPublish = false;
	private boolean sendCursor = false;
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(20);
	private final ScheduledExecutorService cursorScheduler = Executors.newScheduledThreadPool(1);

	public CaptureScreen(CoreScreenShare coreScreenShare, IScreenShare client, String host, String app, int port) {
		core = coreScreenShare;
		this.client = client;
		this.host = host;
		this.app = app;
		this.port = port;
		int tbf;
		switch (quality) {
			case VeryHigh:
				tbf = 50;
				break;
			case High:
				tbf = 100;
				break;
			case Low:
			case Medium:
			default:
				tbf = 500;
				break;
			
		}
		timeBetweenFrames = tbf * 1000 * 1000; // nano time
		se = new ScreenV1Encoder(); //NOTE get image should be changed in the code below
	}

	public void release() {
		active = false;
		try {
			scheduler.shutdownNow();
		} catch (Exception e) {
			//no-op
		}
		try {
			cursorScheduler.shutdownNow();
		} catch (Exception e) {
			//no-op
		}
	}

	public void resetBuffer() {
		se.reset();
	}

	public void run() {
		try {
			while (active && !core.isReadyToRecord()) {
				Thread.sleep(60);
			}
			scheduler.scheduleWithFixedDelay(new Runnable() {
				Robot robot = new Robot();
				Rectangle screen = new Rectangle(spinnerX, spinnerY, spinnerWidth, spinnerHeight);
				int[][] image = null;
				
				public void run() {
					long start = System.currentTimeMillis();
					image = ScreenV1Encoder.getImage(screen, robot);
					if (log.isTraceEnabled()) {
						log.trace(String.format("Image was captured in %s ms", System.currentTimeMillis() - start));
					}
					start = System.currentTimeMillis();
					try {
						byte[] data = se.encode(screen, image);
						if (log.isTraceEnabled()) {
							log.trace(String.format("Image was encoded in %s ms", System.currentTimeMillis() - start));
						}
						pushVideo(data, timestamp);
					} catch (IOException e) {
						log.error("Error while encoding/sending: ", e);
					}
				}
			}, 0, timeBetweenFrames, TimeUnit.NANOSECONDS);
			if (sendCursor) {
				cursorScheduler.scheduleWithFixedDelay(new Runnable() {
					public void run() {
						core.sendCursorStatus();
					}
				}, 0, timeBetweenFrames, TimeUnit.NANOSECONDS);
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
	
	private void pushVideo(byte[] video, long ts) throws IOException {
		if (startPublish) {
			if (buffer == null || (buffer.capacity() < video.length && !buffer.isAutoExpand())) {
				buffer = IoBuffer.allocate(video.length);
				buffer.setAutoExpand(true);
			}
	
			buffer.clear();
			buffer.put(video);
			buffer.flip();
	
			RTMPMessage rtmpMsg = RTMPMessage.build(new VideoData(buffer), (int) ts);
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
