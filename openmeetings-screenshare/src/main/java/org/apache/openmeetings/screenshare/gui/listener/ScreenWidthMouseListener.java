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

public class ScreenWidthMouseListener extends OmMouseInputAdapter {
	private double x = 0;

	public ScreenWidthMouseListener(ScreenSharerFrame frame) {
		super(frame, Cursor.W_RESIZE_CURSOR);
		cons = e -> x = e.getX();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (!((Component)e.getSource()).isEnabled()) {
			return;
		}
		double newX = e.getX();

		int newWidth = frame.getDim().getSpinnerWidth() - (int)(x - newX);
		int newSpinnerX = frame.getDim().getSpinnerX() + newWidth;

		if (0 <= newSpinnerX && newSpinnerX <= frame.getDim().getWidthMax()) {
			frame.setDoUpdateBounds(false);
			frame.setSpinnerWidth(newWidth);
			frame.setDoUpdateBounds(true);
			frame.updateVScreenBounds();
			frame.calcRescaleFactors();
		}
	}
}
