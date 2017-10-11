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
package org.apache.openmeetings.screenshare.gui.listener;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.MouseEvent;

import org.apache.openmeetings.screenshare.gui.ScreenSharerFrame;

public class ScreenYMouseListener extends OmMouseInputAdapter {
	private double y = 0;

	public ScreenYMouseListener(ScreenSharerFrame frame) {
		super(frame, Cursor.N_RESIZE_CURSOR);
		cons = e -> y = e.getY();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (!((Component)e.getSource()).isEnabled()) {
			return;
		}
		double newY = e.getY();

		int delta = (int) (y - newY);
		int newYPosition = frame.getDim().getSpinnerY() - delta;
		int newHeight = frame.getDim().getSpinnerHeight() + delta;

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
