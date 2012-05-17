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
package org.openmeetings.screen.webstart;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.mina.core.buffer.IoBuffer;
import org.openmeetings.screen.webstart.gui.ScreenDimensions;
import org.openmeetings.screen.webstart.gui.ScreenDimensions.ScreenQuality;
import org.openmeetings.screen.webstart.gui.ScreenSharerFrame;
import org.red5.io.ITagReader;
import org.red5.io.ITagWriter;
import org.red5.io.utils.ObjectMap;
import org.red5.server.api.event.IEvent;
import org.red5.server.api.service.IPendingServiceCall;
import org.red5.server.net.rtmp.Channel;
import org.red5.server.net.rtmp.RTMPConnection;
import org.red5.server.net.rtmp.codec.RTMP;
import org.red5.server.net.rtmp.event.Notify;
import org.red5.server.net.rtmp.event.VideoData;
import org.red5.server.net.rtmp.message.Header;
import org.red5.server.net.rtmp.status.StatusCodes;
import org.red5.server.stream.message.RTMPMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoreScreenShare {

	private static final Logger logger = LoggerFactory
			.getLogger(CoreScreenShare.class);

	private IScreenShare instance = null;
	
	public boolean startPublish = false;
	public Integer playStreamId;
	public Integer publishStreamId;
	public String publishName;
	public ITagWriter writer;
	public ITagReader reader;
	public IoBuffer buffer;
	public CaptureScreen capture = null;
	public Thread thread = null;

	public ScreenSharerFrame frame;

	public String host = "btg199251";
	public String app = "oflaDemo";
	public int port = 1935;
	public int defaultQuality = 1;

	public Long organization_id = 0L;
	public Long user_id = null;
	public Boolean allowRecording = true;

	private boolean startStreaming = false;
	private boolean startRecording = false;
	private boolean startPublishing = false;
	public float Ampl_factor = 1f;
	public boolean isConnected = false;

	public Map<Integer, Boolean> currentPressedKeys = new HashMap<Integer, Boolean>();

	// ------------------------------------------------------------------------
	//
	// Main
	//
	// ------------------------------------------------------------------------

	public CoreScreenShare(IScreenShare instance) {
		this.instance = instance;
	}

	public void main(String[] args) {
		try {
			for (String arg : args) {
				logger.debug("arg: " + arg);
			}
			String[] textArray = null;
			if (args.length == 9) {
				host = args[0];
				app = args[1];
				port = Integer.parseInt(args[2]);
				publishName = args[3];

				String labelTexts = args[4];

				organization_id = Long.parseLong(args[5]);

				defaultQuality = Integer.parseInt(args[6]);
				user_id = Long.parseLong(args[7]);
				allowRecording = Boolean.parseBoolean(args[8]);

				if (labelTexts.length() > 0) {
					textArray = labelTexts.split(";");

					logger.debug("labelTexts :: " + labelTexts);

					logger.debug("textArray Length " + textArray.length);

					for (int i = 0; i < textArray.length; i++) {
						logger.debug(i + " :: " + textArray[i]);
					}
				}
			} else {
				System.exit(0);
			}

			logger.debug("host: " + host + ", app: "
					+ app + ", port: " + port + ", publish: "
					+ publishName);

			createWindow(textArray);
		} catch (Exception err) {
			logger.error("", err);
		}
	}

	// ------------------------------------------------------------------------
	//
	// GUI
	//
	// ------------------------------------------------------------------------
	public void createWindow(String[] textArray) {
		try {
			frame = new ScreenSharerFrame(this, textArray);
			frame.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					frame.setVisible(false);
					System.exit(0);
				}
			});
			frame.setVisible(true);
			frame.setRecordingTabEnabled(allowRecording);

			logger.debug("initialized");

		} catch (Exception err) {
			logger.error("createWindow Exception: ", err);
			err.printStackTrace();
		}
	}

	synchronized public void sendCursorStatus() {
		try {
			PointerInfo a = MouseInfo.getPointerInfo();
			Point mouseP = a.getLocation();

			float scaleFactor = (1.0f * ScreenDimensions.resizeX) / ScreenDimensions.spinnerWidth;

			// Real size: Real mouse position = Resize : X
			int x = (int)(Ampl_factor * (mouseP.getX() - ScreenDimensions.spinnerX) * scaleFactor);
			int y = (int)(Ampl_factor * (mouseP.getY() - ScreenDimensions.spinnerY) * scaleFactor);

			HashMap<String, Object> cursorPosition = new HashMap<String, Object>();
			cursorPosition.put("publicSID", this.publishName);
			cursorPosition.put("cursor_x", x);
			cursorPosition.put("cursor_y", y);

			if (instance.getConnection() != null) {
				instance.invoke("setNewCursorPosition", new Object[] { cursorPosition }, instance);
			}
		} catch (NullPointerException npe) {
			//noop
		} catch (Exception err) {
			System.out.println("captureScreenStart Exception: ");
			System.err.println(err);
			frame.setStatus("Exception: " + err);
			logger.error("[sendCursorStatus]", err);
		}
	}

	synchronized public void setConnectionAsSharingClient() {
		try {
			logger.debug("########## setConnectionAsSharingClient");

			HashMap<Object, Object> map = new HashMap<Object, Object>();
			map.put("screenX", ScreenDimensions.spinnerX);
			map.put("screenY", ScreenDimensions.spinnerY);

			int scaledWidth = (int)(Ampl_factor * ScreenDimensions.resizeX);
			int scaledHeight = (int)(Ampl_factor * ScreenDimensions.resizeY);

			map.put("screenWidth", scaledWidth);
			map.put("screenHeight", scaledHeight);

			map.put("publishName", this.publishName);
			map.put("startRecording", startRecording);
			map.put("startStreaming", startStreaming);
			map.put("startPublishing", startPublishing);
			map.put("publishingHost", frame.getPublishHost());
			map.put("publishingApp", frame.getPublishApp());
			map.put("publishingId", frame.getPublishId());

			map.put("organization_id", this.organization_id);
			map.put("user_id", this.user_id);

			instance.invoke("setConnectionAsSharingClient", new Object[] { map }, instance);
		} catch (Exception err) {
			logger.error("setConnectionAsSharingClient Exception: ", err);
			frame.setStatus("Error: " + err.getLocalizedMessage());
			logger.error("[setConnectionAsSharingClient]", err);
		}
	}

	/**
	 * @param startStreaming flag denoting the streaming is started
	 * @param startRecording flag denoting the recording is started
	 */
	public void captureScreenStart(boolean startStreaming, boolean startRecording) {
		captureScreenStart(startStreaming, startRecording, false);
	}
	
	public void captureScreenStart(boolean startStreaming, boolean startRecording, boolean startPublishing) {
		try {
			logger.debug("captureScreenStart");
			this.startStreaming = startStreaming;
			this.startRecording= startRecording;
			this.startPublishing = startPublishing;
			
			if (!isConnected) {
				instance.connect(host, port, app, instance);
			} else {
				setConnectionAsSharingClient();
			}
		} catch (Exception err) {
			logger.error("captureScreenStart Exception: ", err);
			frame.setStatus("Exception: " + err);
		}
	}

	public void captureScreenStop(boolean stopStreaming, boolean stopRecording) {
		captureScreenStop(stopStreaming, stopRecording, false);
	}
	
	public void captureScreenStop(boolean stopStreaming, boolean stopRecording, boolean stopPublishing) {
		try {
			logger.debug("INVOKE screenSharerAction" );

			Map<String, Object> map = new HashMap<String, Object>();
			map.put("stopStreaming", stopStreaming);
			map.put("stopRecording", stopRecording);
			map.put("stopPublishing", stopPublishing);

			instance.invoke("screenSharerAction", new Object[] { map }, instance);

			if (stopStreaming) {
				frame.setSharingStatus(false);
			} else if (stopRecording) {
				frame.setRecordingStatus(false);
			} else if (stopPublishing) {
				frame.setPublishingStatus(false);
			}
		} catch (Exception err) {
			logger.error("captureScreenStop Exception: ", err);
			frame.setStatus("Exception: " + err);
		}
	}

	// ------------------------------------------------------------------------
	//
	// Public
	//
	// ------------------------------------------------------------------------

	protected void onInvoke(RTMPConnection conn, Channel channel,
			Header source, Notify invoke, RTMP rtmp) {

		if (invoke.getType() == IEvent.Type.STREAM_DATA) {
			return;
		}

		String method = invoke.getCall().getServiceMethodName();
		if ("sendRemoteCursorEvent".equals(method)) {
			sendRemoteCursorEvent(invoke.getCall().getArguments()[0]);
		} else if ("screenSharerAction".equals(method)) {
			Object[] args = invoke.getCall().getArguments();
			if (args != null) {
				@SuppressWarnings("unchecked")
				HashMap<String, Object> params = (HashMap<String, Object>)args[0];
				if (params.containsKey("stopPublishing")
					&& Boolean.parseBoolean("" + params.get("stopPublishing"))) {
					frame.setPublishingStatus(false);
				}
				if (params.containsKey("error")) {
					frame.setStatus("" + params.get("error"));
				}
			}
		}
	}

	public void stopStream() {
		try {
			logger.debug("ScreenShare stopStream");

			isConnected = false;

			instance.disconnect();
			capture.stop();
			capture.release();
			thread = null;
		} catch (Exception e) {
			logger.error("ScreenShare stopStream exception " + e);
		}

	}

	public void onStreamEvent(Notify notify) {

		logger.debug( "onStreamEvent " + notify );

		@SuppressWarnings("rawtypes")
		ObjectMap map = (ObjectMap) notify.getCall().getArguments()[0];
		String code = (String) map.get("code");

		if (StatusCodes.NS_PUBLISH_START.equals(code)) {
			logger.debug( "onStreamEvent Publish start" );
			startPublish = true;
		}
	}

	public void sendRemoteCursorEvent(Object obj) {
		try {

			// logger.debug("#### sendRemoteCursorEvent ");

			// logger.debug("Result Map Type "+obj.getClass().getName());

			@SuppressWarnings("rawtypes")
			Map returnMap = (Map) obj;

			// logger.debug("result "+returnMap.get("result"));

			String action = returnMap.get("action").toString();

			if (action.equals("onmouseup")) {

				Robot robot = new Robot();

				// VirtualScreenBean

				Float scaleFactor = Float
						.valueOf(ScreenDimensions.spinnerWidth)
						/ Float.valueOf(ScreenDimensions.resizeX);

				// logger.debug("x 1 scaleFactor "+scaleFactor);

				Float part_x1 = ((Float.valueOf(returnMap.get("x").toString())
						.floatValue() * scaleFactor) / Float
						.valueOf(Ampl_factor));

				// logger.debug("x 1 part_x1 "+part_x1);

				Integer x = Math.round(part_x1
						+ ScreenDimensions.spinnerX);

				Integer y = Math
						.round(((Float.valueOf(returnMap.get("y").toString())
								.floatValue()
								* ScreenDimensions.spinnerHeight / ScreenDimensions.resizeY) / Ampl_factor)
								+ ScreenDimensions.spinnerY);

				// logger.debug("x|y "+x+" || "+y);

				robot.mouseMove(x, y);
				robot.mouseRelease(InputEvent.BUTTON1_MASK);

			} else if (action.equals("onmousedown")) {

				Robot robot = new Robot();

				Float scaleFactor = Float
						.valueOf(ScreenDimensions.spinnerWidth)
						/ Float.valueOf(ScreenDimensions.resizeX);
				Float part_x1 = ((Float.valueOf(returnMap.get("x").toString())
						.floatValue() * scaleFactor) / Float
						.valueOf(Ampl_factor));
				Integer x = Math.round(part_x1
						+ ScreenDimensions.spinnerX);
				Integer y = Math
						.round(((Float.valueOf(returnMap.get("y").toString())
								.floatValue()
								* ScreenDimensions.spinnerHeight / ScreenDimensions.resizeY) / Ampl_factor)
								+ ScreenDimensions.spinnerY);

				robot.mouseMove(x, y);
				robot.mousePress(InputEvent.BUTTON1_MASK);

			} else if (action.equals("mousePos")) {

				Robot robot = new Robot();

				Float scaleFactor = Float
						.valueOf(ScreenDimensions.spinnerWidth)
						/ Float.valueOf(ScreenDimensions.resizeX);

				Float part_x1 = ((Float.valueOf(returnMap.get("x").toString())
						.floatValue() * scaleFactor) / Float
						.valueOf(Ampl_factor));

				Integer x = Math.round(part_x1
						+ ScreenDimensions.spinnerX);

				Integer y = Math
						.round(((Float.valueOf(returnMap.get("y").toString())
								.floatValue()
								* ScreenDimensions.spinnerHeight / ScreenDimensions.resizeY) / Ampl_factor)
								+ ScreenDimensions.spinnerY);

				robot.mouseMove(x, y);

			} else if (action.equals("onkeydown")) {

				Robot robot = new Robot();

				Integer key = Integer.valueOf(returnMap.get("k").toString())
						.intValue();

				// logger.debug("key onkeydown -1 "+key);
				boolean doAction = true;

				if (key == 221) {
					key = 61;
				} else if (key == -1) {

					String charValue = returnMap.get("c").toString();

					// key = KeyEvent.VK_ADD;
					doAction = false;

					for (Iterator<Integer> iter = this.currentPressedKeys
							.keySet().iterator(); iter.hasNext();) {
						Integer storedKey = iter.next();

						robot.keyRelease(storedKey);

					}

					this.currentPressedKeys = new HashMap<Integer, Boolean>();

					this.pressSpecialSign(charValue, robot);
				} else if (key == 188) {
					key = 44;
				} else if (key == 189) {
					key = 109;
				} else if (key == 190) {
					key = 46;
				} else if (key == 191) {
					key = 47;
				} else if (key == 13) {
					key = KeyEvent.VK_ENTER;
				}

				// logger.debug("key onkeydown -2 "+key);

				if (doAction) {

					this.currentPressedKeys.put(key, true);

					robot.keyPress(key);
				}

			} else if (action.equals("onkeyup")) {

				Robot robot = new Robot();

				Integer key = Integer.valueOf(returnMap.get("k").toString())
						.intValue();

				// logger.debug("key onkeyup 1- "+key);

				boolean doAction = true;

				if (key == 221) {
					key = 61;
				} else if (key == -1) {
					doAction = false;
				} else if (key == 188) {
					key = 44;
				} else if (key == 189) {
					key = 109;
				} else if (key == 190) {
					key = 46;
				} else if (key == 191) {
					key = 47;
				} else if (key == 13) {
					key = KeyEvent.VK_ENTER;
				}

				// logger.debug("key onkeyup 2- "+key);

				if (doAction) {

					if (this.currentPressedKeys.containsKey(key)) {
						this.currentPressedKeys.remove(key);

						robot.keyRelease(key);

					}

				}

			} else if (action.equals("paste")) {

				Robot robot = new Robot();

				String paste = returnMap.get("paste").toString();

				this.pressSpecialSign(paste, robot);

			} else if (action.equals("copy")) {

				Robot robot = new Robot();

				String paste = this.getHighlightedText(robot);

				HashMap<Integer, String> map = new HashMap<Integer, String>();
				map.put(0, "copiedText");
				map.put(1, paste);

				String clientId = returnMap.get("clientId").toString();

				// public synchronized int sendMessageWithClientById(Object
				// newMessage, String clientId)

				instance.invoke("sendMessageWithClientById", new Object[] { map,
						clientId }, instance);

			} else if (action.equals("show")) {

				String paste = this.getClipboardText();

				HashMap<Integer, String> map = new HashMap<Integer, String>();
				map.put(0, "copiedText");
				map.put(1, paste);

				String clientId = returnMap.get("clientId").toString();

				// public synchronized int sendMessageWithClientById(Object
				// newMessage, String clientId)

				instance.invoke("sendMessageWithClientById", new Object[] { map,
						clientId }, instance);

			}

			// KeyEvent.VK
			// KeyEvent.

		} catch (Exception err) {
			logger.error("[sendRemoteCursorEvent]", err);
		}
	}

	public String getClipboardText() {
		try {
			// get the system clipboard
			Clipboard systemClipboard = Toolkit.getDefaultToolkit()
					.getSystemClipboard();

			// get the contents on the clipboard in a
			// transferable object
			Transferable clipboardContents = systemClipboard.getContents(null);

			// check if clipboard is empty
			if (clipboardContents == null) {

				// Clipboard is empty!!!
				return ("");

				// see if DataFlavor of
				// DataFlavor.stringFlavor is supported
			} else if (clipboardContents
					.isDataFlavorSupported(DataFlavor.stringFlavor)) {

				// return text content
				String returnText = (String) clipboardContents
						.getTransferData(DataFlavor.stringFlavor);

				return returnText;
			}

			return "";
		} catch (UnsupportedFlavorException ufe) {
			ufe.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return "";
	}

	private String getHighlightedText(Robot instance) {

		try {

			// clippy.setContents( selection,selection );

			// logger.debug("os.name :: "+System.getProperty("os.name"));

			if (System.getProperty("os.name").toUpperCase().indexOf("WINDOWS") >= 0) {

				// logger.debug("IS WINDOWS");

				// pressing STRG+C == copy
				instance.keyPress(KeyEvent.VK_CONTROL);
				Thread.sleep(200);
				instance.keyPress(KeyEvent.VK_C);
				Thread.sleep(200);
				instance.keyRelease(KeyEvent.VK_C);
				Thread.sleep(200);
				instance.keyRelease(KeyEvent.VK_CONTROL);
				Thread.sleep(200);

			} else {

				// logger.debug("IS MAC");

				// Macintosh simulate Copy
				instance.keyPress(157);
				Thread.sleep(200);
				instance.keyPress(67);
				Thread.sleep(200);
				instance.keyRelease(67);
				Thread.sleep(200);
				instance.keyRelease(157);
				Thread.sleep(200);

			}

			String charValue = this.getClipboardText();

			return charValue;

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return "";
	}

	private void pressSpecialSign(String charValue, Robot instance) {
		Clipboard clippy = Toolkit.getDefaultToolkit().getSystemClipboard();
		try {

			Transferable transferableText = new StringSelection(charValue);
			clippy.setContents(transferableText, null);

			// logger.debug("os.name :: "+System.getProperty("os.name"));

			if (System.getProperty("os.name").toUpperCase().indexOf("WINDOWS") >= 0) {

				// logger.debug("IS WINDOWS");

				// pressing STRG+V == insert-mode
				instance.keyPress(KeyEvent.VK_CONTROL);
				Thread.sleep(100);
				instance.keyPress(KeyEvent.VK_V);
				Thread.sleep(100);
				instance.keyRelease(KeyEvent.VK_V);
				Thread.sleep(100);
				instance.keyRelease(KeyEvent.VK_CONTROL);
				Thread.sleep(100);

			} else {

				// logger.debug("IS MAC");

				// Macintosh simulate Insert
				instance.keyPress(157);
				Thread.sleep(100);
				instance.keyPress(86);
				Thread.sleep(100);
				instance.keyRelease(86);
				Thread.sleep(100);
				instance.keyRelease(157);
				Thread.sleep(100);

			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void resultReceived(IPendingServiceCall call) {
		try {

			logger.debug( "service call result: " + call );

			if (call.getServiceMethodName().equals("connect")) {
				isConnected = true;
				setConnectionAsSharingClient();
			} else if (call.getServiceMethodName().equals(
					"setConnectionAsSharingClient")) {

				Object o = call.getResult();

				@SuppressWarnings("rawtypes")
				Map returnMap = (Map) o;

				if (o == null || !Boolean.valueOf("" + returnMap.get("alreadyPublished")).booleanValue()) {
					logger.debug("Stream not yet started - do it ");

					instance.createStream(instance);
				} else {
					if (this.capture != null) {
						this.capture.resetBuffer();
					}

					logger.debug("The Stream was already started ");
				}

				if (returnMap != null) {
					Object modus = returnMap.get("modus");
					if ("startStreaming".equals(modus)) {
						frame.setSharingStatus(true);
					} else if ("startRecording".equals(modus)) {
						frame.setRecordingStatus(true);
					} else if ("startPublishing".equals(modus)) {
						frame.setPublishingStatus(true);
					}
				} else {
					throw new Exception(
							"Could not aquire modus for event setConnectionAsSharingClient");
				}

			} else if (call.getServiceMethodName().equals("createStream")) {

				publishStreamId = (Integer) call.getResult();
				logger.debug("createPublishStream result stream id: "
						+ publishStreamId);
				logger.debug("publishing video by name: " + publishName);
				instance.publish(publishStreamId, publishName, "live", instance);

				logger.debug("setup capture thread");

				logger.debug("setup capture thread vScreenSpinnerWidth "
						+ ScreenDimensions.spinnerWidth);
				logger.debug("setup capture thread vScreenSpinnerHeight "
						+ ScreenDimensions.spinnerHeight);

				capture = new CaptureScreen();

				if (thread == null) {
					thread = new Thread(capture);
					thread.start();
				}
				capture.start();

			} else if (call.getServiceMethodName().equals("screenSharerAction")) {
				logger.debug("call ### get Method Name "
						+ call.getServiceMethodName());

				Object o = call.getResult();

				logger.debug("Result Map Type " + o.getClass().getName());

				@SuppressWarnings("unchecked")
				Map<String, Object> returnMap = (Map<String, Object>)o;
				Object result = returnMap.get("result");
				if ("stopAll".equals(result)) {
					logger.debug("Stopping to stream, there is neither a Desktop Sharing nor Recording anymore");
					stopStream();
				} else if ("stopSharingOnly".equals(result)) {
					//no op
				} else if ("stopRecordingOnly".equals(result)) {
					//no op
				} else if ("stopPublishingOnly".equals(result)) {
					frame.setPublishingStatus(false);
				}
			} else if (call.getServiceMethodName().equals(
					"setNewCursorPosition")) {

				// Do not do anything

			} else {

				logger.debug("Unknown method " + call.getServiceMethodName());

			}

		} catch (Exception err) {
			logger.error("[resultReceived]", err);
		}
	}

	public void pushVideo(int len, byte[] video, long ts) throws IOException {

		if (!startPublish)
			return;

		if (buffer == null || (buffer.capacity() < video.length && !buffer.isAutoExpand())) {
			buffer = IoBuffer.allocate(video.length);
			buffer.setAutoExpand(true);
		}

		buffer.clear();
		buffer.put(video);
		buffer.flip();

		VideoData videoData = new VideoData(buffer);
		videoData.setTimestamp((int) ts);

		RTMPMessage rtmpMsg = RTMPMessage.build(videoData);
		instance.publishStreamData(publishStreamId, rtmpMsg);
	}

	// ------------------------------------------------------------------------
	//
	// CaptureScreen
	//
	// ------------------------------------------------------------------------

	private final class CaptureScreen extends Object implements Runnable {
		private int timeBetweenFrames = 500; // frameRate
		private volatile long timestamp = 0;
		private volatile boolean active = true;
		@SuppressWarnings("unused")
		private volatile boolean stopped = false;
		private IScreenEncoder se;

		// ------------------------------------------------------------------------
		//
		// Constructor
		//
		// ------------------------------------------------------------------------

		public CaptureScreen() {
			timeBetweenFrames = (ScreenDimensions.quality == ScreenQuality.VeryHigh) ? 100 : 500;
			se = new ScreenV1Encoder();
		}

		// ------------------------------------------------------------------------
		//
		// Public
		//
		// ------------------------------------------------------------------------
		public void start() {
			stopped = false;
		}

		public void stop() {
			stopped = true;
		}

		public void release() {
			active = false;
		}

		public void resetBuffer() {
			se.reset();
		}

		// ------------------------------------------------------------------------
		//
		// Thread loop
		//
		// ------------------------------------------------------------------------
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

						pushVideo(data.length, data, timestamp);
					} catch (Exception e) {
						e.printStackTrace();
					}
					final int spent = (int) (System.currentTimeMillis() - ctime);

					sendCursorStatus();

					Thread.sleep(Math.max(0, timeBetweenFrames - spent));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
