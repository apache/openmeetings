package org.openmeetings.app.data.basic.files;
/**
 * Response of one Library-Files-Request
 * @author sebastianwagner
 *
 */
import java.util.LinkedList;

public class LiberaryObject {
	
	//These Objects here should only be initialized if needed, 
	//an empty LinkedList thorws a ServiceInvokationTarget-Exception (makes no sense to me but it
	//is the way in Axis2 1.3
	private LinkedList<FilesObject> filesList;
	private LinkedList<FoldersObject> foldersList;
	private PresentationObject presentationObject;
	private String error;
	public LinkedList<FilesObject> getFilesList() {
		return filesList;
	}
	public void setFilesList(LinkedList<FilesObject> filesList) {
		this.filesList = filesList;
	}
	public LinkedList<FoldersObject> getFoldersList() {
		return foldersList;
	}
	public void setFoldersList(LinkedList<FoldersObject> foldersList) {
		this.foldersList = foldersList;
	}
	public PresentationObject getPresentationObject() {
		return presentationObject;
	}
	public void setPresentationObject(PresentationObject presentationObject) {
		this.presentationObject = presentationObject;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}

}
