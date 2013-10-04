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

import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.MouseEvent;

import javax.swing.event.MouseInputAdapter;

public class ScreenYMouseListener extends MouseInputAdapter {
	private ScreenSharerFrame frame;
	private double y = 0;

	public ScreenYMouseListener(ScreenSharerFrame frame) {
		this.frame = frame;
	}

	public void mouseEntered(MouseEvent e) {
		frame.setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
	}

	public void mouseExited(MouseEvent e) {
		frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}

	public void mousePressed(MouseEvent e) {
		frame.setShowWarning(false);
		this.y = e.getY();
	}

	public void mouseReleased(MouseEvent e) {
		frame.setShowWarning(true);
	}

	public void mouseDragged(MouseEvent e) {
		if (!((Component)e.getSource()).isEnabled()) {
			return;
		}
		double newY = e.getY();

		int delta = (int) (y - newY);
		int newYPosition = ScreenDimensions.spinnerY - delta;
		int newHeight = ScreenDimensions.spinnerHeight + delta;

		if (newYPosition >= 0 && newHeight >= 0) {
			frame.setDoUpdateBounds(false);
			frame.setSpinnerY(newYPosition);
			frame.setSpinnerHeight(newHeight);
			frame.setDoUpdateBounds(true);
			frame.updateVScreenBounds();
			frame.calcRescaleFactors();
		}
	}
}
