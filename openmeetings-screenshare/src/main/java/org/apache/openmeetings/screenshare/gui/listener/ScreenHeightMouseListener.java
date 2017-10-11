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

public class ScreenHeightMouseListener extends OmMouseInputAdapter {
	private double y = 0;

	public ScreenHeightMouseListener(ScreenSharerFrame frame) {
		super(frame, Cursor.N_RESIZE_CURSOR);
		cons = e -> y = e.getY();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (!((Component)e.getSource()).isEnabled()) {
			return;
		}
		double newY = e.getY();

		int newHeight = frame.getDim().getSpinnerHeight() - (int)(y - newY);
		int newSpinnerY = frame.getDim().getSpinnerY() + newHeight;

		if (newSpinnerY >= 0 && newSpinnerY <= frame.getDim().getHeightMax()) {
			frame.setDoUpdateBounds(false);
			frame.setSpinnerHeight(newHeight);
			frame.setDoUpdateBounds(true);
			frame.updateVScreenBounds();
			frame.calcRescaleFactors();
		}
	}
}
