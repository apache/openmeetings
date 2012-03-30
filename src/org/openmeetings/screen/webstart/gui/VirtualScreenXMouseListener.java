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
package org.openmeetings.screen.webstart.gui;

import java.awt.Cursor;
import java.awt.event.MouseEvent;

import javax.swing.event.MouseInputAdapter;


public class VirtualScreenXMouseListener extends MouseInputAdapter  {
	private VirtualScreen vs;
	private double x = 0;

	public VirtualScreenXMouseListener(VirtualScreen vs) {
		this.vs = vs;
	}
	
	public void mouseEntered(MouseEvent e) {
		vs.css.t.setCursor( Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR) ) ;
	}

	public void mouseExited(MouseEvent e) {
		vs.css.t.setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) ) ;
	}

	public void mousePressed(MouseEvent e) {
		vs.showWarning=false;
		this.x = e.getX();
	}

	public void mouseReleased(MouseEvent e) {
		vs.showWarning=true;
	}

	public void mouseDragged(MouseEvent e) {
		double newX = e.getX();
		int delta = Long.valueOf(Math.round(this.x-newX)).intValue();
		int newXPosition = VirtualScreenBean.vScreenSpinnerX-delta;
		int newWidth = VirtualScreenBean.vScreenSpinnerWidth+delta;

		//System.out.println(newX+" "+newXPosition+" "+newWidth);
		if (newXPosition>=0 && newWidth>=0) {
			vs.doUpdateBounds=false;
			vs.css.jVScreenXSpin.setValue(newXPosition);
			vs.css.jVScreenWidthSpin.setValue(newWidth);
			vs.doUpdateBounds=true;
			vs.updateVScreenBounds();
			vs.calcRescaleFactors();
		}

	}

}
