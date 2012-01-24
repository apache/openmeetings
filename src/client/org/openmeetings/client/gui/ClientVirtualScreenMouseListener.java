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
package org.openmeetings.client.gui;

import javax.swing.event.MouseInputAdapter;
import java.awt.event.MouseEvent;
import java.awt.Cursor;

import org.openmeetings.client.beans.ClientVirtualScreenBean;

public class ClientVirtualScreenMouseListener extends MouseInputAdapter  {
	
	private int x = 0;
	private int y = 0;

	public void mouseEntered(MouseEvent e) {
		if (ClientVirtualScreenBean.isActive) {
			ClientStartScreen.instance.t.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) ) ;
		}
	}

	public void mouseExited(MouseEvent e) {
		if (ClientVirtualScreenBean.isActive) {
			ClientStartScreen.instance.t.setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
		}
	}

	public void mousePressed(MouseEvent e) {
		if (ClientVirtualScreenBean.isActive) {
			ClientVirtualScreen.instance.showWarning=false;
			this.x = e.getX();
			this.y = e.getY();
			System.out.println(this.x+" "+this.y);
		}
	}

	public void mouseReleased(MouseEvent e) {
		if (ClientVirtualScreenBean.isActive) {
			ClientVirtualScreen.instance.showWarning=true;
		}
	}
	
	public void mouseDragged(MouseEvent e) {
		if (ClientVirtualScreenBean.isActive) {
			int newX = e.getX();
			int newY = e.getY();
			
			int newXPosition = ClientVirtualScreenBean.vScreenSpinnerX-(this.x-newX);
			int newYPosition = ClientVirtualScreenBean.vScreenSpinnerY-(this.y-newY);
			//System.out.println(newX+" "+newY+" "+newXPosition);
			if (newXPosition>=0) {
				ClientStartScreen.instance.jVScreenXSpin.setValue(newXPosition);
			}
			if (newYPosition>=0) {
				ClientStartScreen.instance.jVScreenYSpin.setValue(newYPosition);
			}
		}
	}

}
