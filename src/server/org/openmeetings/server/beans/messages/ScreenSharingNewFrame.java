package org.openmeetings.server.beans.messages;

/**
 * @author sebastianwagner
 *
 */
public class ScreenSharingNewFrame {

	private String s;//publicSID
	private int x;
	private int y;
	private int w;//width
	private int h;//height
	
	public String getS() {
		return s;
	}
	public void setS(String s) {
		this.s = s;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public int getW() {
		return w;
	}
	public void setW(int w) {
		this.w = w;
	}
	public int getH() {
		return h;
	}
	public void setH(int h) {
		this.h = h;
	}
	
}
