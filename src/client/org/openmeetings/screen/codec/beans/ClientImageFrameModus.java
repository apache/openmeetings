package org.openmeetings.screen.codec.beans;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

/**
 * @author sebastianwagner
 *
 */
public class ClientImageFrameModus {
	 
	private Image image;
	private byte[] payload;
	private Rectangle rect;
	private int originalRectWidth;
	private int originalRectHeight;
	
	
	
	/**
	 * @param image
	 * @param payload
	 * @param rect
	 * @param originalRectWidth
	 * @param originalRectHeight
	 *
	 * 27.09.2009 17:25:52
	 * sebastianwagner
	 * 
	 * 
	 */
	public ClientImageFrameModus(Image image, byte[] payload, Rectangle rect,
			int originalRectWidth, int originalRectHeight) {
		super();
		this.image = image;
		this.payload = payload;
		this.rect = rect;
		this.originalRectWidth = originalRectWidth;
		this.originalRectHeight = originalRectHeight;
	}
	public int getOriginalRectWidth() {
		return originalRectWidth;
	}
	public void setOriginalRectWidth(int originalRectWidth) {
		this.originalRectWidth = originalRectWidth;
	}
	public int getOriginalRectHeight() {
		return originalRectHeight;
	}
	public void setOriginalRectHeight(int originalRectHeight) {
		this.originalRectHeight = originalRectHeight;
	}
	public Image getImage() {
		return image;
	}
	public void setImage(Image image) {
		this.image = image;
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
