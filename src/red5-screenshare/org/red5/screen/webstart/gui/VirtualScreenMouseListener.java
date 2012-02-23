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
package org.red5.screen.webstart.gui;

import java.awt.Cursor;
import java.awt.event.MouseEvent;

import javax.swing.event.MouseInputAdapter;


public class VirtualScreenMouseListener extends MouseInputAdapter  {
	private VirtualScreen vs;
	private int x = 0;
	private int y = 0;
	
	public VirtualScreenMouseListener(VirtualScreen vs) {
		this.vs = vs;
	}
	
	public void mouseEntered(MouseEvent e) {
		vs.css.t.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) ) ;
	}

	public void mouseExited(MouseEvent e) {
		vs.css.t.setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) ) ;
	}

	public void mousePressed(MouseEvent e) {
		vs.showWarning=false;
		this.x = e.getX();
		this.y = e.getY();
		System.out.println(this.x+" "+this.y);
	}

	public void mouseReleased(MouseEvent e) {
		vs.showWarning=true;
	}

	public void mouseDragged(MouseEvent e) {
		int newX = e.getX();
		int newY = e.getY();

		int newXPosition = VirtualScreenBean.vScreenSpinnerX-(this.x-newX);
		int newYPosition = VirtualScreenBean.vScreenSpinnerY-(this.y-newY);
		//System.out.println(newX+" "+newY+" "+newXPosition);
		if (newXPosition>=0) {
			vs.css.jVScreenXSpin.setValue(newXPosition);
		}
		if (newYPosition>=0) {
			vs.css.jVScreenYSpin.setValue(newYPosition);
		}
		
		vs.calcRescaleFactors();
	}

}
