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
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.SystemUtils;
import org.apache.openmeetings.screenshare.Core;
import org.apache.openmeetings.screenshare.gui.ScreenDimensions;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;

@DisallowConcurrentExecution
public class RemoteJob implements Job {
	private static final Logger log = getLogger(RemoteJob.class);
	public static final String CORE_KEY = "core";
	private Robot robot = null;
	private ScreenDimensions dim = null;

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
		if (dim == null) {
			dim = core.getDim();
		}
		try {
			Map<String, Object> obj;
			while ((obj = core.getRemoteEvents().poll(1, TimeUnit.MILLISECONDS)) != null) {
				String action = String.valueOf(obj.get("action"));
				log.trace("Action polled:: {}, count: {}", action, core.getRemoteEvents().size());

				switch (action) {
					case "mouseUp":
					{
						Point p = getCoordinates(obj);
						robot.mouseMove(p.x, p.y);
						robot.mouseRelease(InputEvent.BUTTON1_MASK);
					}
						break;
					case "mouseDown":
					{
						Point p = getCoordinates(obj);
						robot.mouseMove(p.x, p.y);
						robot.mousePress(InputEvent.BUTTON1_MASK);
					}
						break;
					case "mousePos":
					{
						Point p = getCoordinates(obj);
						robot.mouseMove(p.x, p.y);
					}
						break;
					case "keyDown":
						new OmKeyEvent(obj).press(this);
						break;
					case "paste":
						paste(String.valueOf(obj.get("paste")));
						break;
					case "copy":
					{
						String paste = getHighlightedText();

						Map<Integer, String> map = new HashMap<>();
						map.put(0, "copiedText");
						map.put(1, paste);

						String uid = String.valueOf(obj.get("uid"));

						core.getInstance().invoke("sendMessageToClient", new Object[]{uid, map}, core);
					}
						break;
				}
			}
		} catch (Exception err) {
			log.error("[sendRemoteCursorEvent]", err);
		}
	}

	public void press(List<Integer> codes) {
		log.debug("sequence:: codes {}", codes);
		press(codes.stream().mapToInt(Integer::intValue).toArray());
	}

	public void press(int... codes) {
		for (int i = 0; i < codes.length; ++i) {
			robot.keyPress(codes[i]);
		}
		for (int i = codes.length - 1; i > -1; --i) {
			robot.keyRelease(codes[i]);
		}
	}

	private String getHighlightedText() {
		try {
			if (SystemUtils.IS_OS_MAC) {
				// Macintosh simulate Copy
				press(157, 67);
			} else {
				// pressing CTRL+C == copy
				press(KeyEvent.VK_CONTROL, KeyEvent.VK_C);
			}
			return getClipboardText();
		} catch (Exception e) {
			log.error("Unexpected exception while getting highlighted text", e);
		}
		return "";
	}

	public String getClipboardText() {
		try {
			// get the contents on the clipboard in a transferable object
			Transferable data = getDefaultToolkit().getSystemClipboard().getContents(null);
			// check if clipboard is empty
			if (data == null) {
				// Clipboard is empty!!!
			} else if (data.isDataFlavorSupported(stringFlavor)) {
				// see if DataFlavor of DataFlavor.stringFlavor is supported return text content
				return (String) data.getTransferData(stringFlavor);
			}
		} catch (Exception e) {
			log.error("Unexpected exception while getting clipboard text", e);
		}
		return "";
	}

	private void paste(String charValue) {
		Clipboard clippy = getDefaultToolkit().getSystemClipboard();
		try {
			Transferable transferableText = new StringSelection(charValue);
			clippy.setContents(transferableText, null);

			if (SystemUtils.IS_OS_MAC) {
				// Macintosh simulate Insert
				press(157, 86);
			} else {
				// pressing CTRL+V == insert-mode
				press(KeyEvent.VK_CONTROL, KeyEvent.VK_V);
			}
		} catch (Exception e) {
			log.error("Unexpected exception while pressSpecialSign", e);
		}
	}

	private Point getCoordinates(Map<String, Object> obj) {
		float scaleFactorX = ((float)dim.getSpinnerWidth()) / dim.getResizeX();
		float scaleFactorY = ((float)dim.getSpinnerHeight()) / dim.getResizeY();

		int x = Math.round(scaleFactorX * getFloat(obj, "x") + dim.getSpinnerX());
		int y = Math.round(scaleFactorY * getFloat(obj, "y") + dim.getSpinnerY());
		return new Point(x, y);
	}
}
