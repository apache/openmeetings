package org.openmeetings.webstart.gui;

import javax.swing.event.MouseInputAdapter;
import java.awt.event.MouseEvent;
import java.awt.Cursor;

import org.openmeetings.webstart.beans.VirtualScreenBean;

public class VirtualScreenMouseListener extends MouseInputAdapter  {
	
	private int x = 0;
	private int y = 0;

	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		StartScreen.instance.t.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) ) ;
	}

	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		StartScreen.instance.t.setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) ) ;
	}

	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		VirtualScreen.instance.showWarning=false;
		this.x = e.getX();
		this.y = e.getY();
		System.out.println(this.x+" "+this.y);
	}

	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		VirtualScreen.instance.showWarning=true;
	}
	
	public void mouseDragged(MouseEvent e) {
		int newX = e.getX();
		int newY = e.getY();
		
		int newXPosition = VirtualScreenBean.vScreenSpinnerX-(this.x-newX);
		int newYPosition = VirtualScreenBean.vScreenSpinnerY-(this.y-newY);
		//System.out.println(newX+" "+newY+" "+newXPosition);
		if (newXPosition>=0) {
			StartScreen.instance.jVScreenXSpin.setValue(newXPosition);
		}
		if (newYPosition>=0) {
			StartScreen.instance.jVScreenYSpin.setValue(newYPosition);
		}
	}

}
