package org.openmeetings.server.beans.messages;

import java.util.List;

/**
 * @author sebastianwagner
 *
 */
public class ScreenSharingMessage {
	
	private String s = null; //publicSID
	private int w;//width
	private int h;//height
	private List<ScreenSharingNewFrame> l;//list
	
	private boolean deleted;
	
	public String getS() {
		return s;
	}
	public void setS(String s) {
		this.s = s;
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
	public List<ScreenSharingNewFrame> getL() {
		return l;
	}
	public void setL(List<ScreenSharingNewFrame> l) {
		this.l = l;
	}
	public boolean isDeleted() {
		return deleted;
	}
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
	
}
