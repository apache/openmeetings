package org.red5.screen.webstart.gui;

import java.awt.Cursor;
import java.awt.event.MouseEvent;
import javax.swing.event.MouseInputAdapter;

import org.red5.screen.webstart.ScreenShare;


public class VirtualScreenHeightMouseListener extends MouseInputAdapter  {

	private double y = 0;

	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		ScreenShare.instance.t.setCursor( Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR) ) ;
	}

	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		ScreenShare.instance.t.setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) ) ;
	}

	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		VirtualScreen.instance.showWarning=false;
		this.y = e.getY();
//		System.out.println(this.x+" "+this.y);
	}

	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		VirtualScreen.instance.showWarning=true;
	}

	public void mouseDragged(MouseEvent e) {
		double newY = e.getY();

		int delta = Long.valueOf(Math.round(this.y-newY)).intValue();
		int newHeight = VirtualScreenBean.vScreenSpinnerHeight-delta;

		//System.out.println(delta+" "+newHeight);
		if ((VirtualScreenBean.vScreenSpinnerY+newHeight)<=VirtualScreenBean.screenHeightMax) {
			VirtualScreen.instance.doUpdateBounds=false;
			ScreenShare.instance.jVScreenHeightSpin.setValue(newHeight);
			VirtualScreen.instance.doUpdateBounds=true;
			VirtualScreen.instance.updateVScreenBounds();

		}

	}

}
