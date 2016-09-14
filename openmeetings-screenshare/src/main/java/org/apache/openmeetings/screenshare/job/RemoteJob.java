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
package org.apache.openmeetings.screenshare.job;

import static java.awt.Toolkit.getDefaultToolkit;
import static java.awt.datatransfer.DataFlavor.stringFlavor;
import static org.apache.openmeetings.screenshare.Core.Ampl_factor;
import static org.apache.openmeetings.screenshare.gui.ScreenDimensions.resizeX;
import static org.apache.openmeetings.screenshare.gui.ScreenDimensions.resizeY;
import static org.apache.openmeetings.screenshare.gui.ScreenDimensions.spinnerHeight;
import static org.apache.openmeetings.screenshare.gui.ScreenDimensions.spinnerWidth;
import static org.apache.openmeetings.screenshare.gui.ScreenDimensions.spinnerX;
import static org.apache.openmeetings.screenshare.gui.ScreenDimensions.spinnerY;
import static org.apache.openmeetings.screenshare.util.Util.getFloat;
import static org.slf4j.LoggerFactory.getLogger;

import java.awt.AWTException;
import java.awt.Point;
import java.awt.Robot;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.openmeetings.screenshare.Core;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;

@DisallowConcurrentExecution
public class RemoteJob implements Job {
	private static final Logger log = getLogger(RemoteJob.class);
	private static final boolean isWindows = System.getProperty("os.name").toUpperCase().indexOf("WINDOWS") > -1;
	public static final String CORE_KEY = "core";
	private Robot robot = null;
	
	public RemoteJob() {
		try {
			robot = new Robot();
			robot.setAutoDelay(5);
		} catch (AWTException e) {
			log.error("Unexpected error while creating Robot", e);
		}
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		JobDataMap data = context.getJobDetail().getJobDataMap();
		Core core = (Core)data.get(CORE_KEY);
		try {
			Map<String, Object> obj = null;
			while ((obj = core.getRemoteEvents().poll(1, TimeUnit.MILLISECONDS)) != null) { 
				String action = "" + obj.get("action");
				log.trace("Action polled:: {}, count: {}", action, core.getRemoteEvents().size());

				if (action.equals("onmouseup")) {
					Point p = getCoordinates(obj);
					robot.mouseMove(p.x, p.y);
					robot.mouseRelease(InputEvent.BUTTON1_MASK);
				} else if (action.equals("onmousedown")) {
					Point p = getCoordinates(obj);
					robot.mouseMove(p.x, p.y);
					robot.mousePress(InputEvent.BUTTON1_MASK);
				} else if (action.equals("mousePos")) {
					Point p = getCoordinates(obj);
					robot.mouseMove(p.x, p.y);
				} else if (action.equals("keyDown")) {
					pressSequence(new OmKeyEvent(obj).sequence());
				} else if (action.equals("paste")) {
					String paste = obj.get("paste").toString();
					pressSpecialSign(paste);
				} else if (action.equals("copy")) {
					String paste = getHighlightedText();

					Map<Integer, String> map = new HashMap<Integer, String>();
					map.put(0, "copiedText");
					map.put(1, paste);

					String clientId = obj.get("clientId").toString();

					core.getInstance().invoke("sendMessageWithClientById", new Object[]{map, clientId}, core);
				} else if (action.equals("show")) {
					String paste = getClipboardText();

					Map<Integer, String> map = new HashMap<Integer, String>();
					map.put(0, "copiedText");
					map.put(1, paste);

					String clientId = obj.get("clientId").toString();

					core.getInstance().invoke("sendMessageWithClientById", new Object[]{map, clientId}, core);
				}
			}
		} catch (Exception err) {
			log.error("[sendRemoteCursorEvent]", err);
		}
	}

	private void pressSequence(int... codes) throws InterruptedException {
		for (int i = 0; i < codes.length; ++i) {
			robot.keyPress(codes[i]);
		}
		for (int i = codes.length - 1; i > -1; --i) {
			robot.keyRelease(codes[i]);
		}
	}

	private String getHighlightedText() {
		try {
			if (isWindows) {
				// pressing STRG+C == copy
				pressSequence(KeyEvent.VK_CONTROL, KeyEvent.VK_C);
			} else {
				// Macintosh simulate Copy
				pressSequence(157, 67);
			}
			return getClipboardText();
		} catch (Exception e) {
			log.error("Unexpected exception while getting highlighted text", e);
		}
		return "";
	}

	public String getClipboardText() {
		try {
			// get the system clipboard
			Clipboard systemClipboard = getDefaultToolkit().getSystemClipboard();
			// get the contents on the clipboard in a transferable object
			Transferable clipboardContents = systemClipboard.getContents(null);
			// check if clipboard is empty
			if (clipboardContents == null) {
				// Clipboard is empty!!!
			} else if (clipboardContents.isDataFlavorSupported(stringFlavor)) {
				// see if DataFlavor of DataFlavor.stringFlavor is supported
				// return text content
				String returnText = (String) clipboardContents.getTransferData(stringFlavor);
				return returnText;
			}
		} catch (Exception e) {
			log.error("Unexpected exception while getting clipboard text", e);
		}
		return "";
	}

	private void pressSpecialSign(String charValue) {
		Clipboard clippy = getDefaultToolkit().getSystemClipboard();
		try {
			Transferable transferableText = new StringSelection(charValue);
			clippy.setContents(transferableText, null);

			if (isWindows) {
				// pressing STRG+V == insert-mode
				pressSequence(KeyEvent.VK_CONTROL, KeyEvent.VK_V);
			} else {
				// Macintosh simulate Insert
				pressSequence(157, 86);
			}
		} catch (Exception e) {
			log.error("Unexpected exception while pressSpecialSign", e);
		}
	}

	private Point getCoordinates(Map<String, Object> obj) {
		float scaleFactorX = spinnerWidth / (Ampl_factor * resizeX);
		float scaleFactorY = spinnerHeight / (Ampl_factor * resizeY);

		int x = Math.round(scaleFactorX * getFloat(obj, "x") + spinnerX);
		int y = Math.round(scaleFactorY * getFloat(obj, "y") + spinnerY);
		return new Point(x, y);
	}
}
