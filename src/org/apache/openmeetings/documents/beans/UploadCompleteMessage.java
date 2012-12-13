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
package org.apache.openmeetings.documents.beans;

import org.apache.openmeetings.persistence.beans.files.FileExplorerItem;
import org.apache.openmeetings.persistence.beans.user.Users;

/**
 * Helper bean that is send to client(s) once the servlet has completed the upload
 * 
 * @author sebawagner
 *
 */
public class UploadCompleteMessage {
	
	private Users user;
	private String message;
	private String action;
	private String error;
	private boolean hasError = false;
	private String fileName;
	private FileExplorerItem fileExplorerItem;
	
	public UploadCompleteMessage() {
	}
	
	public UploadCompleteMessage(Users user, String message, String action,
			String error, String fileName) {
		super();
		this.user = user;
		this.message = message;
		this.action = action;
		this.error = error;
		this.fileName = fileName;
	}

	public Users getUser() {
		return user;
	}
	public void setUser(Users user) {
		this.user = user;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public boolean isHasError() {
		return hasError;
	}
	public void setHasError(boolean hasError) {
		this.hasError = hasError;
	}
	public FileExplorerItem getFileExplorerItem() {
		return fileExplorerItem;
	}
	public void setFileExplorerItem(FileExplorerItem fileExplorerItem) {
		this.fileExplorerItem = fileExplorerItem;
	}

}
