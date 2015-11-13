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
package org.apache.openmeetings.db.dto.file;

import java.util.List;

import org.apache.openmeetings.db.entity.file.FileExplorerItem;

/**
 * @author sebastianwagner
 *
 */
public class FileExplorerObject {

	private List<FileExplorerItem> userHome;
	private List<FileExplorerItem> roomHome;
	private Long userHomeSize;
	private Long roomHomeSize;
	
	public FileExplorerObject() {}
	
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
