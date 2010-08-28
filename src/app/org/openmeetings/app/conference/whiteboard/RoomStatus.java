package org.openmeetings.app.conference.whiteboard;

import java.util.HashMap;

import org.openmeetings.app.hibernate.beans.recording.RoomClient;

public class RoomStatus {
	
	HashMap<String,RoomClient> clientMap;
	BrowserStatus browserStatus;
	Boolean roomFull = false;
	
	public HashMap<String, RoomClient> getClientMap() {
		return clientMap;
	}
	public void setClientMap(HashMap<String, RoomClient> clientMap) {
		this.clientMap = clientMap;
	}
	public BrowserStatus getBrowserStatus() {
		return browserStatus;
	}
	public void setBrowserStatus(BrowserStatus browserStatus) {
		this.browserStatus = browserStatus;
	}
	public Boolean getRoomFull() {
		return roomFull;
	}
	public void setRoomFull(Boolean roomFull) {
		this.roomFull = roomFull;
	}
	
}
