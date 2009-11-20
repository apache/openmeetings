package org.openmeetings.client.beans;

public class ClientCursorStatus {
	
	private Integer mode = 6;
	private Integer sequenceNumber = null;
	private Integer lengthSecurityToken = null;
	private Integer x;
	private Integer y;
	private String publicSID = null;
	
	public Integer getMode() {
		return mode;
	}
	public void setMode(Integer mode) {
		this.mode = mode;
	}
	public Integer getX() {
		return x;
	}
	public void setX(Integer x) {
		this.x = x;
	}
	public Integer getY() {
		return y;
	}
	public void setY(Integer y) {
		this.y = y;
	}
	public String getPublicSID() {
		return publicSID;
	}
	public void setPublicSID(String publicSID) {
		this.publicSID = publicSID;
	}
	public Integer getLengthSecurityToken() {
		return lengthSecurityToken;
	}
	public void setLengthSecurityToken(Integer lengthSecurityToken) {
		this.lengthSecurityToken = lengthSecurityToken;
	}
	public Integer getSequenceNumber() {
		return sequenceNumber;
	}
	public void setSequenceNumber(Integer sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}
	

}
