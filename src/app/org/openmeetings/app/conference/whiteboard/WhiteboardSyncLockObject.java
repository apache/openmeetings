package org.openmeetings.app.conference.whiteboard;

import java.util.Date;

import org.openmeetings.app.persistence.beans.recording.RoomClient;

public class WhiteboardSyncLockObject {
	
	private String publicSID;
	private boolean isInitialLoaded = false;
	private boolean isCurrentLoadingItem = false;
	
	private Date addtime;
	private Date starttime;

	public String getPublicSID() {
		return publicSID;
	}

	public void setPublicSID(String publicSID) {
		this.publicSID = publicSID;
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
