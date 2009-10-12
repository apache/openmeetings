package org.openmeetings.app.data.basic.files;
/**
 * Represents a Folder in the File System
 * @author sebastianwagner
 *
 */
public class FoldersObject {
	
	private String folderName;
	private String lastModified;
	
	public String getFolderName() {
		return folderName;
	}
	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}
	public String getLastModified() {
		return lastModified;
	}
	public void setLastModified(String lastModified) {
		this.lastModified = lastModified;
	}

}
