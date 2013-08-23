/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License") +  you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.openmeetings.data.beans.files;
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
