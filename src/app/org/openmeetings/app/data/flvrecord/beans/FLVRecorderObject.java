package org.openmeetings.app.data.flvrecord.beans;

import java.util.List;

import org.openmeetings.app.persistence.beans.flvrecord.FlvRecording;

public class FLVRecorderObject {

	private List<FlvRecording> userHome;
	private List<FlvRecording> roomHome;
	private Long userHomeSize;
	private Long roomHomeSize;
	
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
	public Long getUserHomeSize() {
		return userHomeSize;
	}
	public void setUserHomeSize(Long userHomeSize) {
		this.userHomeSize = userHomeSize;
	}
	public Long getRoomHomeSize() {
		return roomHomeSize;
	}
	public void setRoomHomeSize(Long roomHomeSize) {
		this.roomHomeSize = roomHomeSize;
	}
	
}
