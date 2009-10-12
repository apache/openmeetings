package org.openmeetings.screen.codec.beans;

import java.awt.Rectangle;
import java.util.LinkedList;
import java.util.List;

/**
 * @author sebastianwagner
 *
 */
public class ClientFrameBuffer {
 
	private List<ClientImageFrameModus> buffer = new LinkedList<ClientImageFrameModus>();
	
	private Rectangle rect;
	private byte index;
	
	
	public List<ClientImageFrameModus> getBuffer() {
		return buffer;
	}
	public void setBuffer(List<ClientImageFrameModus> buffer) {
		this.buffer = buffer;
	}
	
	public Rectangle getRect() {
		return rect;
	}
	public void setRect(Rectangle rect) {
		this.rect = rect;
	}
	public byte getIndex() {
		return index;
	}
	public void setIndex(byte index) {
		this.index = index;
	}
	
}
