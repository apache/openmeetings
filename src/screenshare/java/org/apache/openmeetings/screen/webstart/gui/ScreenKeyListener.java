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
package org.apache.openmeetings.screen.webstart.gui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScreenKeyListener implements KeyListener {
	private static final Logger logger = LoggerFactory.getLogger(ScreenKeyListener.class);

	public void keyPressed(KeyEvent kEvent) {
		logger.debug("keyPressed :Code: " + kEvent.getKeyCode());
	}

	public void keyReleased(KeyEvent kEvent) {
		logger.debug("keyReleased :Code: " + kEvent.getKeyCode());
	}

	public void keyTyped(KeyEvent arg0) {
	}
}
