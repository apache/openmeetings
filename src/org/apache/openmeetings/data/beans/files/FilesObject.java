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
