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

import static java.awt.Toolkit.getDefaultToolkit;
import static java.awt.datatransfer.DataFlavor.stringFlavor;
import static java.lang.Boolean.TRUE;
import static org.apache.openmeetings.screen.webstart.gui.ScreenDimensions.resizeX;
import static org.apache.openmeetings.screen.webstart.gui.ScreenDimensions.resizeY;
import static org.apache.openmeetings.screen.webstart.gui.ScreenDimensions.spinnerHeight;
import static org.apache.openmeetings.screen.webstart.gui.ScreenDimensions.spinnerWidth;
import static org.apache.openmeetings.screen.webstart.gui.ScreenDimensions.spinnerX;
import static org.apache.openmeetings.screen.webstart.gui.ScreenDimensions.spinnerY;
import static org.slf4j.LoggerFactory.getLogger;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

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

public class CoreScreenShare implements IPendingServiceCallback, INetStreamEventHandler {
	private static final Logger log = getLogger(CoreScreenShare.class);

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
	private boolean audioNotify = false;

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
				log.debug("arg: " + arg);
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
				allowRecording = bool(args[8]);
				allowPublishing = bool(args[9]);

				if (labelTexts.length() > 0) {
					textArray = labelTexts.split(";");

					log.debug("labelTexts :: " + labelTexts);

					log.debug("textArray Length " + textArray.length);

					for (int i = 0; i < textArray.length; i++) {
						log.debug(i + " :: " + textArray[i]);
					}
				}
				log.debug("host: " + host + ", app: "
						+ app + ", port: " + port + ", publish: "
						+ publishName);
			} else {
				System.exit(0);
			}

			createWindow(textArray);
		} catch (Exception err) {
			log.error("", err);
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
			log.debug("initialized");
		} catch (Exception err) {
			log.error("createWindow Exception: ", err);
		}
	}

	synchronized public void sendCursorStatus() {
		try {
			Point mouseP = MouseInfo.getPointerInfo().getLocation();

			float scaleFactor = (1.0f * resizeX) / spinnerWidth;

			// Real size: Real mouse position = Resize : X
			int x = (int)(Ampl_factor * (mouseP.getX() - spinnerX) * scaleFactor);
			int y = (int)(Ampl_factor * (mouseP.getY() - spinnerY) * scaleFactor);

			Map<String, Object> cursorPosition = new HashMap<String, Object>();
			cursorPosition.put("publicSID", publishName);
			cursorPosition.put("cursor_x", x);
			cursorPosition.put("cursor_y", y);

			if (instance.getConnection() != null) {
				instance.invoke("setNewCursorPosition", new Object[] { cursorPosition }, this);
			}
		} catch (NullPointerException npe) {
			//noop
		} catch (Exception err) {
			frame.setStatus("Exception: " + err);
			log.error("[sendCursorStatus]", err);
		}
	}

	synchronized public void setConnectionAsSharingClient() {
		try {
			log.debug("########## setConnectionAsSharingClient");

			Map<Object, Object> map = new HashMap<Object, Object>();
			map.put("screenX", spinnerX);
			map.put("screenY", spinnerY);

			int scaledWidth = (int)(Ampl_factor * resizeX);
			int scaledHeight = (int)(Ampl_factor * resizeY);

			map.put("screenWidth", scaledWidth);
			map.put("screenHeight", scaledHeight);

			map.put("publishName", publishName);
			map.put("startRecording", startRecording);
			map.put("startStreaming", startStreaming);
			map.put("startPublishing", startPublishing);
			map.put("publishingHost", frame.getPublishHost());
			map.put("publishingApp", frame.getPublishApp());
			map.put("publishingId", frame.getPublishId());

			map.put("organization_id", organization_id);
			map.put("user_id", user_id);

			instance.invoke("setConnectionAsSharingClient", new Object[] { map }, this);
		} catch (Exception err) {
			frame.setStatus("Error: " + err.getLocalizedMessage());
			log.error("[setConnectionAsSharingClient]", err);
		}
	}

	public void streamingStart() {
		startStreaming = true;
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
	
	private void captureScreenStart() {
		try {
			log.debug("captureScreenStart");
			
			if (!isConnected) {
				instance.connect(host, port, app, this);
			} else {
				setConnectionAsSharingClient();
			}
		} catch (Exception err) {
			log.error("captureScreenStart Exception: ", err);
			frame.setStatus("Exception: " + err);
		}
	}

	public void streamingStop() {
		startStreaming = false;
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

			Map<String, Object> map = new HashMap<String, Object>();
			map.put(action, true);

			instance.invoke("screenSharerAction", new Object[] { map }, this);
		} catch (Exception err) {
			log.error("captureScreenStop Exception: ", err);
			frame.setStatus("Exception: " + err);
		}
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
	
	protected void onInvoke(RTMPConnection conn, Channel channel, Header source, org.red5.server.net.rtmp.event.Notify invoke, RTMP rtmp) {
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

	public void stopStream() {
		try {
			log.debug("ScreenShare stopStream");

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
			log.error("ScreenShare stopStream exception " + e);
		}

	}

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

	private boolean bool(Object b) {
		return TRUE.equals(Boolean.valueOf("" + b));
	}
	
	private int getInt(Map<String, Object> returnMap, String key) {
		return Integer.valueOf(returnMap.get(key).toString()).intValue();
	}
	
	private float getFloat(Map<String, Object> returnMap, String key) {
		return Float.valueOf(returnMap.get(key).toString()).floatValue();
	}
	
	private Point getCoordinates(Map<String, Object> returnMap) {
		float scaleFactorX = spinnerWidth / (Ampl_factor * resizeX);
		float scaleFactorY = spinnerHeight / (Ampl_factor * resizeY);

		int x = Math.round(scaleFactorX * getFloat(returnMap, "x") + spinnerX);
		int y = Math.round(scaleFactorY * getFloat(returnMap, "y") + spinnerY);
		return new Point(x, y);
	}
	
	public void sendRemoteCursorEvent(Object obj) {
		try {
			log.trace("#### sendRemoteCursorEvent ");
			log.trace("Result Map Type "+obj.getClass().getName());

			@SuppressWarnings("unchecked")
			Map<String, Object> returnMap = (Map<String, Object>)obj;

			String action = "" + returnMap.get("action");

			if (action.equals("onmouseup")) {
				Robot robot = new Robot();
				
				Point p = getCoordinates(returnMap);
				robot.mouseMove(p.x, p.y);
				robot.mouseRelease(InputEvent.BUTTON1_MASK);
			} else if (action.equals("onmousedown")) {
				Robot robot = new Robot();

				Point p = getCoordinates(returnMap);
				robot.mouseMove(p.x, p.y);
				robot.mousePress(InputEvent.BUTTON1_MASK);
			} else if (action.equals("mousePos")) {
				Robot robot = new Robot();

				Point p = getCoordinates(returnMap);
				robot.mouseMove(p.x, p.y);
			} else if (action.equals("onkeydown")) {
				Robot robot = new Robot();

				int key = getInt(returnMap, "k");

				// logger.debug("key onkeydown -1 "+key);
				boolean doAction = true;

				if (key == 221) {
					key = 61;
				} else if (key == -1) {
					String charValue = returnMap.get("c").toString();

					// key = KeyEvent.VK_ADD;
					doAction = false;

					for (Integer storedKey : currentPressedKeys.keySet()) {
						robot.keyRelease(storedKey);
					}

					currentPressedKeys = new HashMap<Integer, Boolean>();

					pressSpecialSign(charValue, robot);
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

				if (doAction) {
					currentPressedKeys.put(key, true);

					robot.keyPress(key);
				}
			} else if (action.equals("onkeyup")) {
				Robot robot = new Robot();

				int key = getInt(returnMap, "k");

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
					if (currentPressedKeys.containsKey(key)) {
						currentPressedKeys.remove(key);

						robot.keyRelease(key);
					}
				}
			} else if (action.equals("paste")) {
				Robot robot = new Robot();

				String paste = returnMap.get("paste").toString();

				pressSpecialSign(paste, robot);
			} else if (action.equals("copy")) {
				Robot robot = new Robot();

				String paste = this.getHighlightedText(robot);

				Map<Integer, String> map = new HashMap<Integer, String>();
				map.put(0, "copiedText");
				map.put(1, paste);

				String clientId = returnMap.get("clientId").toString();

				instance.invoke("sendMessageWithClientById", new Object[]{map, clientId}, this);
			} else if (action.equals("show")) {
				String paste = getClipboardText();

				Map<Integer, String> map = new HashMap<Integer, String>();
				map.put(0, "copiedText");
				map.put(1, paste);

				String clientId = returnMap.get("clientId").toString();

				// public synchronized int sendMessageWithClientById(Object
				// newMessage, String clientId)

				instance.invoke("sendMessageWithClientById", new Object[]{map, clientId}, this);
			}
		} catch (Exception err) {
			log.error("[sendRemoteCursorEvent]", err);
		}
	}

	public String getClipboardText() {
		try {
			// get the system clipboard
			Clipboard systemClipboard = getDefaultToolkit().getSystemClipboard();

			// get the contents on the clipboard in a
			// transferable object
			Transferable clipboardContents = systemClipboard.getContents(null);

			// check if clipboard is empty
			if (clipboardContents == null) {
				// Clipboard is empty!!!
				return ("");

				// see if DataFlavor of
				// DataFlavor.stringFlavor is supported
			} else if (clipboardContents.isDataFlavorSupported(stringFlavor)) {
				// return text content
				String returnText = (String) clipboardContents.getTransferData(stringFlavor);

				return returnText;
			}

			return "";
		} catch (Exception e) {
			log.error("Unexpected exception while getting clipboard text", e);
		}
		return "";
	}

	private void pressSequence(Robot robot, long delay, int... codes) throws InterruptedException {
		for (int code : codes) {
			robot.keyPress(code);
			Thread.sleep(delay);
		}
	}
	
	private String getHighlightedText(Robot instance) {
		try {
			if (System.getProperty("os.name").toUpperCase().indexOf("WINDOWS") >= 0) {
				// pressing STRG+C == copy
				pressSequence(instance, 200, KeyEvent.VK_CONTROL, KeyEvent.VK_C, KeyEvent.VK_C, KeyEvent.VK_CONTROL);
			} else {
				// Macintosh simulate Copy
				pressSequence(instance, 200, 157, 67, 67, 157);
			}
			return getClipboardText();
		} catch (Exception e) {
			log.error("Unexpected exception while getting highlighted text", e);
		}
		return "";
	}

	private void pressSpecialSign(String charValue, Robot instance) {
		Clipboard clippy = getDefaultToolkit().getSystemClipboard();
		try {
			Transferable transferableText = new StringSelection(charValue);
			clippy.setContents(transferableText, null);

			if (System.getProperty("os.name").toUpperCase().indexOf("WINDOWS") >= 0) {
				// pressing STRG+V == insert-mode
				pressSequence(instance, 100, KeyEvent.VK_CONTROL, KeyEvent.VK_V, KeyEvent.VK_V, KeyEvent.VK_CONTROL);
			} else {
				// Macintosh simulate Insert
				pressSequence(instance, 100, 157, 86, 86, 157);
			}
		} catch (Exception e) {
			log.error("Unexpected exception while pressSpecialSign", e);
		}
	}

	public void resultReceived(IPendingServiceCall call) {
		try {

			log.trace( "service call result: " + call );

			String method = call == null ? null : call.getServiceMethodName();
			log.trace("call ### get Method Name " + method);
			if ("connect".equals(method)) {
				isConnected = true;
				setConnectionAsSharingClient();
			} else if ("setConnectionAsSharingClient".equals(method)) {

				Object o = call.getResult();

				@SuppressWarnings("unchecked")
				Map<String, Object> returnMap = (Map<String, Object>) o;

				if (o == null || !bool(returnMap.get("alreadyPublished"))) {
					log.trace("Stream not yet started - do it ");

					instance.createStream(this);
				} else {
					log.trace("The Stream was already started ");
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
					throw new Exception("Could not aquire modus for event setConnectionAsSharingClient");
				}

			} else if ("createStream".equals(method)) {
				if (startRecording || startStreaming) {
					if (call.getResult() != null) {
						getCapture().setStreamId((Integer)call.getResult());
					}
					log.debug("createPublishStream result stream id: {}; name: {}", getCapture().getStreamId(), publishName);
					instance.publish(getCapture().getStreamId(), publishName, "live", this);
	
					log.debug("setup capture thread spinnerWidth = {}; spinnerHeight = {];", spinnerWidth, spinnerHeight);
	
					getCapture().setSendCursor(true);
					getCapture().start();
				}
			} else if ("screenSharerAction".equals(method)) {
				Object o = call.getResult();

				log.trace("Result Map Type " + o.getClass().getName());

				@SuppressWarnings("unchecked")
				Map<String, Object> returnMap = (Map<String, Object>)o;
				Object result = returnMap.get("result");
				if ("stopAll".equals(result)) {
					log.trace("Stopping to stream, there is neither a Desktop Sharing nor Recording anymore");
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
}
