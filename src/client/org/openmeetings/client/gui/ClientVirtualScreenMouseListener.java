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
