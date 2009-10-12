package org.openmeetings.client.gui;

import java.awt.Cursor;
import java.awt.event.MouseEvent;

import javax.swing.event.MouseInputAdapter;

import org.openmeetings.client.beans.ClientVirtualScreenBean;

public class ClientVirtualScreenYMouseListener extends MouseInputAdapter  {

	private double y = 0;
	
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		ClientStartScreen.instance.t.setCursor( Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR) ) ;
	}

	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		ClientStartScreen.instance.t.setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) ) ;
	}
	
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		ClientVirtualScreen.instance.showWarning=false;
		this.y = e.getY();
//		System.out.println(this.x+" "+this.y);
	}

	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		ClientVirtualScreen.instance.showWarning=true;
	}
	
	public void mouseDragged(MouseEvent e) {
		double newY = e.getY();
		
		int delta = Long.valueOf(Math.round(this.y-newY)).intValue();
		int newYPosition = ClientVirtualScreenBean.vScreenSpinnerY-delta;
		int newHeight = ClientVirtualScreenBean.vScreenSpinnerHeight+delta;
		
//		System.out.println(delta+" "+newYPosition+" "+newHeight);
		if (newYPosition>=0 && newHeight>=0) {
			ClientVirtualScreen.instance.doUpdateBounds=false;
			ClientStartScreen.instance.jVScreenYSpin.setValue(newYPosition);
			ClientStartScreen.instance.jVScreenHeightSpin.setValue(newHeight);
			ClientVirtualScreen.instance.doUpdateBounds=true;
			ClientVirtualScreen.instance.updateVScreenBounds();
			
		}

	}	

}
