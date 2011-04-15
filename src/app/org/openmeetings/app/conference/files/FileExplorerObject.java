package org.openmeetings.app.conference.files;

import java.util.List;

import org.openmeetings.app.hibernate.beans.files.FileExplorerItem;

/**
 * @author sebastianwagner
 *
 */
public class FileExplorerObject {

	private List<FileExplorerItem> userHome;
	private List<FileExplorerItem> roomHome;
	private Long userHomeSize;
	private Long roomHomeSize;
	
	public List<FileExplorerItem> getUserHome() {
		return userHome;
	}
	public void setUserHome(List<FileExplorerItem> userHome) {
		this.userHome = userHome;
	}
	public List<FileExplorerItem> getRoomHome() {
		return roomHome;
	}
	public void setRoomHome(List<FileExplorerItem> roomHome) {
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
