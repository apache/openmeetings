package org.openmeetings.server.beans;

/**
 * @author sebastianwagner
 *
 */
public class ServerSharingViewerBean {
	
	public Long sessionId;
	public String publicSID;
	
	/**
	 * @param sessionId
	 * @param publicSID
	 *
	 * 20.09.2009 15:48:55
	 * sebastianwagner
	 * 
	 * 
	 */
	public ServerSharingViewerBean(long sessionId, String publicSID) {
		super();
		this.sessionId = sessionId;
		this.publicSID = publicSID;
	}
	
	public Long getSessionId() {
		return sessionId;
	}
	public void setSessionId(Long sessionId) {
		this.sessionId = sessionId;
	}
	public String getPublicSID() {
		return publicSID;
	}
	public void setPublicSID(String publicSID) {
		this.publicSID = publicSID;
	}
	

}
