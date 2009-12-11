package org.red5.screen.webstart.tgui;

import java.awt.Cursor;
import java.awt.event.MouseEvent;
import javax.swing.event.MouseInputAdapter;

import org.red5.screen.webstart.ScreenShareRTMPT;


public class VirtualScreenWidthMouseListener extends MouseInputAdapter  {

	private double x = 0;

	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		ScreenShareRTMPT.instance.t.setCursor( Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR) ) ;
	}

	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		ScreenShareRTMPT.instance.t.setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) ) ;
	}

	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		VirtualScreen.instance.showWarning=false;
		this.x = e.getX();
//		System.out.println(this.x+" "+this.y);
	}

	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		VirtualScreen.instance.showWarning=true;
	}

	public void mouseDragged(MouseEvent e) {
		double newX = e.getX();

		int newWidth = VirtualScreenBean.vScreenSpinnerWidth-Long.valueOf(Math.round(this.x-newX)).intValue();


		//System.out.println(newX+"  "+newWidth);
		if ((VirtualScreenBean.vScreenSpinnerX+newWidth)<=VirtualScreenBean.screenWidthMax) {
			VirtualScreen.instance.doUpdateBounds=false;
			ScreenShareRTMPT.instance.jVScreenWidthSpin.setValue(newWidth);
			VirtualScreen.instance.doUpdateBounds=true;
			VirtualScreen.instance.updateVScreenBounds();

		}

	}

}
