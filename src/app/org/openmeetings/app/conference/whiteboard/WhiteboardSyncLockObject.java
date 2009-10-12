package org.openmeetings.app.conference.whiteboard;

import java.util.Date;

import org.openmeetings.app.hibernate.beans.recording.RoomClient;

public class WhiteboardSyncLockObject {
	
	private RoomClient roomclient;
	private boolean isInitialLoaded = false;
	private boolean isCurrentLoadingItem = false;
	
	private Date addtime;
	private Date starttime;

	public RoomClient getRoomclient() {
		return roomclient;
	}

	public void setRoomclient(RoomClient roomclient) {
		this.roomclient = roomclient;
	}

	public boolean isInitialLoaded() {
		return isInitialLoaded;
	}

	public void setInitialLoaded(boolean isInitialLoaded) {
		this.isInitialLoaded = isInitialLoaded;
	}

	public Date getStarttime() {
		return starttime;
	}

	public void setStarttime(Date starttime) {
		this.starttime = starttime;
	}

	public boolean isCurrentLoadingItem() {
		return isCurrentLoadingItem;
	}

	public void setCurrentLoadingItem(boolean isCurrentLoadingItem) {
		this.isCurrentLoadingItem = isCurrentLoadingItem;
	}

	public Date getAddtime() {
		return addtime;
	}

	public void setAddtime(Date addtime) {
		this.addtime = addtime;
	}

}
