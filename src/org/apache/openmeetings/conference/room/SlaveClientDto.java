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
package org.apache.openmeetings.conference.room;

/**
 * 
 * Transfer object to send the master in a cluster the user load of a single
 * user session
 * 
 * @author sebawagner
 * 
 */
public class SlaveClientDto {

	private String streamid;
	private String publicSID;
	private String firstName;
	private String lastName;
	private Long userId;
	private Long roomId;
	
	public SlaveClientDto(String streamid, String publicSID, String firstName,
			String lastName, Long userId, Long roomId) {
		super();
		this.streamid = streamid;
		this.publicSID = publicSID;
		this.firstName = firstName;
		this.lastName = lastName;
		this.userId = userId;
		this.roomId = roomId;
	}

	public SlaveClientDto(RoomClient roomClient) {
		this.streamid = roomClient.getStreamid();
		this.publicSID = roomClient.getPublicSID();
		this.firstName = roomClient.getFirstname();
		this.lastName = roomClient.getLastname();
		this.userId = roomClient.getUser_id();
		this.roomId = roomClient.getRoom_id();
	}

	public String getStreamid() {
		return streamid;
	}

	public void setStreamid(String streamid) {
		this.streamid = streamid;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getRoomId() {
		return roomId;
	}

	public void setRoomId(Long roomId) {
		this.roomId = roomId;
	}

	public String getPublicSID() {
		return publicSID;
	}

	public void setPublicSID(String publicSID) {
		this.publicSID = publicSID;
	}

}
