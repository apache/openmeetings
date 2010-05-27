package org.openmeetings.axis.services;

public class RoomUser {
	
	private String firstname;
	private String lastname;
	private String publicSID;
	private long broadcastId;
	private Boolean isBroadCasting;
	private String avsettings;
	
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	public Boolean getIsBroadCasting() {
		return isBroadCasting;
	}
	public void setIsBroadCasting(Boolean isBroadCasting) {
		this.isBroadCasting = isBroadCasting;
	}
	public String getAvsettings() {
		return avsettings;
	}
	public void setAvsettings(String avsettings) {
		this.avsettings = avsettings;
	}
	public String getPublicSID() {
		return publicSID;
	}
	public void setPublicSID(String publicSID) {
		this.publicSID = publicSID;
	}
	public long getBroadcastId() {
		return broadcastId;
	}
	public void setBroadcastId(long broadcastId) {
		this.broadcastId = broadcastId;
	}
	
}
