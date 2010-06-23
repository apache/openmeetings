package org.red5.screen.webstart.tgui;

import javax.swing.event.MouseInputAdapter;

import org.red5.screen.webstart.ScreenShareRTMPT;

import java.awt.event.MouseEvent;
import java.awt.Cursor;


public class VirtualScreenMouseListener extends MouseInputAdapter  {

	private int x = 0;
	private int y = 0;

	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		ScreenShareRTMPT.instance.t.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) ) ;
	}

	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		ScreenShareRTMPT.instance.t.setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) ) ;
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
			ScreenShareRTMPT.instance.jVScreenXSpin.setValue(newXPosition);
		}
		if (newYPosition>=0) {
			ScreenShareRTMPT.instance.jVScreenYSpin.setValue(newYPosition);
		}
		
		VirtualScreen.instance.calcRescaleFactors();
	}

}
