package org.openmeetings.server.beans;

/**
 * @author sebastianwagner
 *
 */
public class ServerViewerRegisterBean {
	
	public int mode;//5
	private String publicSID;
	
	public int getMode() {
		return mode;
	}
	public void setMode(int mode) {
		this.mode = mode;
	}
	public String getPublicSID() {
		return publicSID;
	}
	public void setPublicSID(String publicSID) {
		this.publicSID = publicSID;
	}

}
