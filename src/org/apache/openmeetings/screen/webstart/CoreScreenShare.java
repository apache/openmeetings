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

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.openmeetings.screen.webstart.gui.ScreenDimensions;
import org.apache.openmeetings.screen.webstart.gui.ScreenSharerFrame;
import org.red5.client.net.rtmp.INetStreamEventHandler;
import org.red5.io.utils.ObjectMap;
import org.red5.server.api.event.IEvent;
import org.red5.server.api.service.IPendingServiceCall;
import org.red5.server.api.service.IPendingServiceCallback;
import org.red5.server.net.rtmp.Channel;
import org.red5.server.net.rtmp.RTMPConnection;
import org.red5.server.net.rtmp.codec.RTMP;
import org.red5.server.net.rtmp.event.Notify;
import org.red5.server.net.rtmp.message.Header;
import org.red5.server.net.rtmp.status.StatusCodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoreScreenShare implements IPendingServiceCallback, INetStreamEventHandler {
	private static final Logger logger = LoggerFactory.getLogger(CoreScreenShare.class);

	private IScreenShare instance = null;
	private String host;
	private String app;
	private int port;
	
	public String publishName;
	private CaptureScreen capture = null;
	private RTMPClientPublish publishClient = null;

	private ScreenSharerFrame frame;

	public int defaultQuality = 1;

	public Long organization_id = 0L;
	public Long user_id = null;
	private boolean allowRecording = true;
	private boolean allowPublishing = true;

	private boolean startStreaming = false;
	private boolean startRecording = false;
	private boolean startPublishing = false;
	public float Ampl_factor = 1f;
	public boolean isConnected = false;
	private boolean readyToRecord = false;

	public Map<Integer, Boolean> currentPressedKeys = new HashMap<Integer, Boolean>();

	private CaptureScreen getCapture() {
		if (capture == null) {
			capture = new CaptureScreen(this, instance, host, app, port);
		}
		return capture;
	}
	// ------------------------------------------------------------------------
	//
	// Main
	//
	// ------------------------------------------------------------------------

	public CoreScreenShare(IScreenShare instance, String[] args) {
		this.instance = instance;
		
		try {
			for (String arg : args) {
				logger.debug("arg: " + arg);
			}
			String[] textArray = null;
			if (args.length > 8) {
				host = args[0];
				app = args[1];
				port = Integer.parseInt(args[2]);
				publishName = args[3];

				String labelTexts = args[4];

				organization_id = Long.parseLong(args[5]);

				defaultQuality = Integer.parseInt(args[6]);
				user_id = Long.parseLong(args[7]);
				allowRecording = Boolean.valueOf(args[8]);
				allowPublishing = Boolean.valueOf(args[9]);

				if (labelTexts.length() > 0) {
					textArray = labelTexts.split(";");

					logger.debug("labelTexts :: " + labelTexts);

					logger.debug("textArray Length " + textArray.length);

					for (int i = 0; i < textArray.length; i++) {
						logger.debug(i + " :: " + textArray[i]);
					}
				}
				logger.debug("host: " + host + ", app: "
						+ app + ", port: " + port + ", publish: "
						+ publishName);
			} else {
				System.exit(0);
			}

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
			frame.setVisible(true);
			frame.setRecordingTabEnabled(allowRecording);
			frame.setPublishingTabEnabled(allowPublishing);
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
				instance.invoke("setNewCursorPosition", new Object[] { cursorPosition }, this);
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

			instance.invoke("setConnectionAsSharingClient", new Object[] { map }, this);
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
				instance.connect(host, port, app, this);
			} else {
				setConnectionAsSharingClient();
			}
		} catch (Exception err) {
			logger.error("captureScreenStart Exception: ", err);
			frame.setStatus("Exception: " + err);
		}
	}

	public void sendCaptureScreenStop(boolean stopStreaming, boolean stopRecording) {
		try {
			logger.debug("INVOKE screenSharerAction" );

			Map<String, Object> map = new HashMap<String, Object>();
			map.put("stopStreaming", stopStreaming);
			map.put("stopRecording", stopRecording);

			instance.invoke("screenSharerAction", new Object[] { map }, this);
		} catch (Exception err) {
			logger.error("captureScreenStop Exception: ", err);
			frame.setStatus("Exception: " + err);
		}
	}

	public void sendStopPublishing() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("stopPublishing", true);
		instance.invoke("screenSharerAction", new Object[] { map }, this);
	}
	
	public void stopStreaming() {
		startStreaming = false;
		frame.setSharingStatus(false, !startPublishing && !startRecording && !startStreaming);
	}
	
	public void stopRecording() {
		startRecording = false;
		frame.setRecordingStatus(false, !startPublishing && !startRecording && !startStreaming);
	}
	
	public void stopPublishing() {
		startPublishing = false;
		frame.setPublishingStatus(false, !startPublishing && !startRecording && !startStreaming);
		if (publishClient != null) {
			publishClient.disconnect();
			publishClient = null;
		}
	}
	
	synchronized public boolean isReadyToRecord() {
		return readyToRecord;
	}
	
	synchronized private void setReadyToRecord(boolean readyToRecord) {
		this.readyToRecord = readyToRecord;
	}
	
	protected void onInvoke(RTMPConnection conn, Channel channel,
			Header source, Notify invoke, RTMP rtmp) {

		if (invoke.getType() == IEvent.Type.STREAM_DATA) {
			return;
		}
		
		String method = invoke.getCall().getServiceMethodName();
		if ("stopStream".equals(method)) {
			stopStream();
		} else if ("sendRemoteCursorEvent".equals(method)) {
			sendRemoteCursorEvent(invoke.getCall().getArguments()[0]);
		} else if ("screenSharerAction".equals(method)) {
			Object[] args = invoke.getCall().getArguments();
			if (args != null && args.length > 0) {
				@SuppressWarnings("unchecked")
				HashMap<String, Object> params = (HashMap<String, Object>)args[0];
				if (params.containsKey("stopPublishing")
					&& Boolean.valueOf("" + params.get("stopPublishing"))) {
					stopPublishing();
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

			stopStreaming();
			stopRecording();
			stopPublishing();
			isConnected = false;

			instance.disconnect();
			setReadyToRecord(false);
			getCapture().setStartPublish(false);
			getCapture().release();
			capture = null;
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
			getCapture().setStartPublish(true);
			setReadyToRecord(true);
		}
	}

	public void sendRemoteCursorEvent(Object obj) {
		try {
			logger.trace("#### sendRemoteCursorEvent ");
			logger.trace("Result Map Type "+obj.getClass().getName());

			@SuppressWarnings("rawtypes")
			Map returnMap = (Map) obj;

			String action = "" + returnMap.get("action");

			if (action.equals("onmouseup")) {

				Robot robot = new Robot();

				Float scaleFactor = Float
						.valueOf(ScreenDimensions.spinnerWidth)
						/ Float.valueOf(ScreenDimensions.resizeX);

				Float part_x1 = ((Float.valueOf(returnMap.get("x").toString())
						.floatValue() * scaleFactor) / Float
						.valueOf(Ampl_factor));

				Integer x = Math.round(part_x1 + ScreenDimensions.spinnerX);

				Integer y = Math
						.round(((Float.valueOf(returnMap.get("y").toString())
								.floatValue()
								* ScreenDimensions.spinnerHeight / ScreenDimensions.resizeY) / Ampl_factor)
								+ ScreenDimensions.spinnerY);

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

				instance.invoke("sendMessageWithClientById", new Object[]{map, clientId}, this);

			} else if (action.equals("show")) {

				String paste = this.getClipboardText();

				HashMap<Integer, String> map = new HashMap<Integer, String>();
				map.put(0, "copiedText");
				map.put(1, paste);

				String clientId = returnMap.get("clientId").toString();

				// public synchronized int sendMessageWithClientById(Object
				// newMessage, String clientId)

				instance.invoke("sendMessageWithClientById", new Object[]{map, clientId}, this);

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

			logger.trace( "service call result: " + call );

			String method = call == null ? null : call.getServiceMethodName();
			logger.trace("call ### get Method Name " + method);
			if ("connect".equals(method)) {
				isConnected = true;
				setConnectionAsSharingClient();
			} else if ("setConnectionAsSharingClient".equals(method)) {

				Object o = call.getResult();

				@SuppressWarnings("unchecked")
				Map<String, Object> returnMap = (Map<String, Object>) o;

				if (o == null || !Boolean.valueOf("" + returnMap.get("alreadyPublished")).booleanValue()) {
					logger.trace("Stream not yet started - do it ");

					instance.createStream(this);
				} else {
					getCapture().resetBuffer();
					logger.trace("The Stream was already started ");
				}
				if (returnMap != null) {
					Object modus = returnMap.get("modus");
					if ("startStreaming".equals(modus)) {
						frame.setSharingStatus(true, false);
					}
					if ("startRecording".equals(modus)) {
						frame.setRecordingStatus(true, false);
					}
					if ("startPublishing".equals(modus)) {
						frame.setPublishingStatus(true, false);
						publishClient = new RTMPClientPublish(
							this
							, frame.getPublishHost()
							, frame.getPublishApp()
							, frame.getPublishId());
						publishClient.connect();
					}
				} else {
					throw new Exception(
							"Could not aquire modus for event setConnectionAsSharingClient");
				}

			} else if ("createStream".equals(method)) {
				if (startRecording || startStreaming) {
					if (call.getResult() != null) {
						getCapture().setStreamId((Integer)call.getResult());
					}
					logger.debug("createPublishStream result stream id: " + getCapture().getStreamId());
					logger.debug("publishing video by name: " + publishName);
					instance.publish(getCapture().getStreamId(), publishName, "live", this);
	
					logger.debug("setup capture thread");
	
					logger.debug("setup capture thread vScreenSpinnerWidth "
							+ ScreenDimensions.spinnerWidth);
					logger.debug("setup capture thread vScreenSpinnerHeight "
							+ ScreenDimensions.spinnerHeight);
	
					getCapture().setSendCursor(true);
					getCapture().start();
				}
			} else if ("screenSharerAction".equals(method)) {
				Object o = call.getResult();

				logger.trace("Result Map Type " + o.getClass().getName());

				@SuppressWarnings("unchecked")
				Map<String, Object> returnMap = (Map<String, Object>)o;
				Object result = returnMap.get("result");
				if ("stopAll".equals(result)) {
					logger.trace("Stopping to stream, there is neither a Desktop Sharing nor Recording anymore");
					stopStream();
				} else if ("stopSharingOnly".equals(result)) {
					stopStreaming();
				} else if ("stopRecordingOnly".equals(result)) {
					stopRecording();
				} else if ("stopPublishingOnly".equals(result)) {
					stopPublishing();
				}
			} else if ("setNewCursorPosition".equals(method)) {
				// Do not do anything
			} else {
				logger.debug("Unknown method " + method);
			}

		} catch (Exception err) {
			logger.error("[resultReceived]", err);
		}
	}
}
