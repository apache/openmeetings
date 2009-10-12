package org.openmeetings.client.beans;

import java.awt.Rectangle;

/**
 * @author sebastianwagner
 *
 */
public class ClientImageFrame {
	
	private byte[] payload;
	private Rectangle rect;
	
	/**
	 * @param rect
	 * @param payload
	 *
	 * 12.09.2009 11:19:43
	 * sebastianwagner
	 * 
	 */
	public ClientImageFrame(Rectangle rect, byte[] payload) {
		super();
		this.payload = payload;
		this.rect = rect;
	}
	
	
	public byte[] getPayload() {
		return payload;
	}
	public void setPayload(byte[] payload) {
		this.payload = payload;
	}
	public Rectangle getRect() {
		return rect;
	}
	public void setRect(Rectangle rect) {
		this.rect = rect;
	}

}
