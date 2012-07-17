package org.openmeetings.screen.webstart;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.IOException;

import org.apache.mina.core.buffer.IoBuffer;
import org.openmeetings.screen.webstart.gui.ScreenDimensions;
import org.openmeetings.screen.webstart.gui.ScreenDimensions.ScreenQuality;
import org.red5.server.net.rtmp.event.AudioData;
import org.red5.server.net.rtmp.event.VideoData;
import org.red5.server.stream.message.RTMPMessage;

final class CaptureScreen extends Thread {
	private CoreScreenShare core;
	private int timeBetweenFrames = 500; // frameRate
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

	public CaptureScreen(CoreScreenShare coreScreenShare, IScreenShare client, String host, String app, int port) {
		core = coreScreenShare;
		this.client = client;
		this.host = host;
		this.app = app;
		this.port = port;
		timeBetweenFrames = (ScreenDimensions.quality == ScreenQuality.VeryHigh) ? 100 : 500;
		se = new ScreenV1Encoder();
	}

	public void release() {
		active = false;
	}

	public void resetBuffer() {
		se.reset();
	}

	public void run() {
		try {
			Robot robot = new Robot();
			BufferedImage image = null;
			while (active) {
				final long ctime = System.currentTimeMillis();
				Rectangle screen = new Rectangle(ScreenDimensions.spinnerX,
						ScreenDimensions.spinnerY,
						ScreenDimensions.spinnerWidth,
						ScreenDimensions.spinnerHeight);
				
				image = robot.createScreenCapture(screen);

				try {
					timestamp += timeBetweenFrames;
					byte[] data = se.encode(screen, image, new Rectangle(ScreenDimensions.resizeX,
							ScreenDimensions.resizeY));

					pushVideo(data, timestamp);
				} catch (Exception e) {
					e.printStackTrace();
				}
				final int spent = (int) (System.currentTimeMillis() - ctime);

				if (sendCursor) {
					core.sendCursorStatus();
				}
				Thread.sleep(Math.max(0, timeBetweenFrames - spent));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
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