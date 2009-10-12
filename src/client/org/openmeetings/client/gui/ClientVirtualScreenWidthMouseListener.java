package org.openmeetings.client.gui;

import java.awt.Cursor;
import java.awt.event.MouseEvent;

import javax.swing.event.MouseInputAdapter;

import org.openmeetings.client.beans.ClientVirtualScreenBean;

public class ClientVirtualScreenWidthMouseListener extends MouseInputAdapter  {
	
	private double x = 0;
	
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		ClientStartScreen.instance.t.setCursor( Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR) ) ;
	}

	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		ClientStartScreen.instance.t.setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) ) ;
	}
	
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		ClientVirtualScreen.instance.showWarning=false;
		this.x = e.getX();
//		System.out.println(this.x+" "+this.y);
	}

	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		ClientVirtualScreen.instance.showWarning=true;
	}
	
	public void mouseDragged(MouseEvent e) {
		double newX = e.getX();

		int newWidth = ClientVirtualScreenBean.vScreenSpinnerWidth-Long.valueOf(Math.round(this.x-newX)).intValue();

		
		//System.out.println(newX+"  "+newWidth);
		if ((ClientVirtualScreenBean.vScreenSpinnerX+newWidth)<=ClientVirtualScreenBean.screenWidthMax) {
			ClientVirtualScreen.instance.doUpdateBounds=false;
			ClientStartScreen.instance.jVScreenWidthSpin.setValue(newWidth);
			ClientVirtualScreen.instance.doUpdateBounds=true;
			ClientVirtualScreen.instance.updateVScreenBounds();
			
		}

	}	

}
