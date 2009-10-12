package org.openmeetings.app.data.basic.files;

/**
 * This Object will represent a File on the File-System
 * @author sebastianwagner
 *
 */
public class FilesObject {
	
	private String fileName;
	private String fileNamePure;
	private String fileNameExt;
	private String lastModified;
	private String fileBytes;
	private String isimage;
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFileNamePure() {
		return fileNamePure;
	}
	public void setFileNamePure(String fileNamePure) {
		this.fileNamePure = fileNamePure;
	}
	public String getFileNameExt() {
		return fileNameExt;
	}
	public void setFileNameExt(String fileNameExt) {
		this.fileNameExt = fileNameExt;
	}
	public String getLastModified() {
		return lastModified;
	}
	public void setLastModified(String lastModified) {
		this.lastModified = lastModified;
	}
	public String getFileBytes() {
		return fileBytes;
	}
	public void setFileBytes(String fileBytes) {
		this.fileBytes = fileBytes;
	}
	public String getIsimage() {
		return isimage;
	}
	public void setIsimage(String isimage) {
		this.isimage = isimage;
	}
	
	
}
