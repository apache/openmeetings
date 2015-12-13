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


/**
 * Helper bean that is send to client(s) once the servlet has completed the
 * upload
 * 
 * @author sebawagner
 * 
 */
public class UploadCompleteMessage {

	private Long userId;
	private String message;
	private String action;
	private String error;
	private boolean hasError = false;
	private String fileName;

	// Properties from the file explorerItem
	private String fileSystemName;
	private Boolean isPresentation = false;
	private Boolean isImage = false;
	private Boolean isVideo = false;
	private String fileHash;

	public UploadCompleteMessage() {
	}

	public UploadCompleteMessage(Long userId, String message, String action,
			String error, String fileName) {
		super();
		this.userId = userId;
		this.message = message;
		this.action = action;
		this.error = error;
		this.fileName = fileName;
	}

	public UploadCompleteMessage(Long userId, String message, String action,
			String error, boolean hasError, String fileName,
			String fileSystemName, boolean isPresentation, boolean isImage,
			boolean isVideo, String fileHash) {
		super();
		this.userId = userId;
		this.message = message;
		this.action = action;
		this.error = error;
		this.hasError = hasError;
		this.fileName = fileName;
		this.fileSystemName = fileSystemName;
		this.isPresentation = isPresentation;
		this.isImage = isImage;
		this.isVideo = isVideo;
		this.fileHash = fileHash;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
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

	public String getFileSystemName() {
		return fileSystemName;
	}

	public void setFileSystemName(String fileSystemName) {
		this.fileSystemName = fileSystemName;
	}

	public Boolean getIsPresentation() {
		return isPresentation;
	}

	public void setIsPresentation(Boolean isPresentation) {
		this.isPresentation = isPresentation;
	}

	public Boolean getIsImage() {
		return isImage;
	}

	public void setIsImage(Boolean isImage) {
		this.isImage = isImage;
	}

	public Boolean getIsVideo() {
		return isVideo;
	}

	public void setIsVideo(Boolean isVideo) {
		this.isVideo = isVideo;
	}

	public String getFileHash() {
		return fileHash;
	}

	public void setFileHash(String fileHash) {
		this.fileHash = fileHash;
	}

	public void setFileExplorerItem(FileExplorerItem fileExplorerItem) {
		if (fileExplorerItem.getIsImage() != null) {
			isImage = fileExplorerItem.getIsImage();
		}
		if (fileExplorerItem.getIsVideo() != null) {
			isVideo = fileExplorerItem.getIsVideo();
		}
		if (fileExplorerItem.getIsPresentation() != null) {
			isPresentation = fileExplorerItem.getIsPresentation();
		}
		fileSystemName = fileExplorerItem.getFileName();
		fileHash = fileExplorerItem.getFileHash();
	}

}
