package org.openmeetings.app.data.flvrecord.beans;

import java.util.List;

import org.openmeetings.app.hibernate.beans.flvrecord.FlvRecording;

public class FLVRecorderObject {

	private List<FlvRecording> userHome;
	private List<FlvRecording> roomHome;
	
	public List<FlvRecording> getUserHome() {
		return userHome;
	}
	public void setUserHome(List<FlvRecording> userHome) {
		this.userHome = userHome;
	}
	public List<FlvRecording> getRoomHome() {
		return roomHome;
	}
	public void setRoomHome(List<FlvRecording> roomHome) {
		this.roomHome = roomHome;
	}
	
}
