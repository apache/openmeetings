package org.openmeetings.app.data.file.dto;

import org.openmeetings.app.persistence.beans.files.FileExplorerItem;

/**
 * @author sebastianwagner
 *
 */
public class FileExplorerObject {

	private FileExplorerItem[] userHome;
	private FileExplorerItem[] roomHome;
	private Long userHomeSize;
	private Long roomHomeSize;
	
	public FileExplorerItem[] getUserHome() {
		return userHome;
	}
	public void setUserHome(FileExplorerItem[] userHome) {
		this.userHome = userHome;
	}
	public FileExplorerItem[] getRoomHome() {
		return roomHome;
	}
	public void setRoomHome(FileExplorerItem[] roomHome) {
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
