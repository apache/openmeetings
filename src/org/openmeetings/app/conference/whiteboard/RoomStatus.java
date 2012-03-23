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
package org.openmeetings.app.conference.whiteboard;

import java.util.HashMap;

import org.openmeetings.app.conference.session.RoomClient;

public class RoomStatus {
	
	HashMap<String,RoomClient> clientMap;
	BrowserStatus browserStatus;
	Boolean roomFull = false;
	
	public HashMap<String, RoomClient> getClientMap() {
		return clientMap;
	}
	public void setClientMap(HashMap<String, RoomClient> clientMap) {
		this.clientMap = clientMap;
	}
	public BrowserStatus getBrowserStatus() {
		return browserStatus;
	}
	public void setBrowserStatus(BrowserStatus browserStatus) {
		this.browserStatus = browserStatus;
	}
	public Boolean getRoomFull() {
		return roomFull;
	}
	public void setRoomFull(Boolean roomFull) {
		this.roomFull = roomFull;
	}
	
}
