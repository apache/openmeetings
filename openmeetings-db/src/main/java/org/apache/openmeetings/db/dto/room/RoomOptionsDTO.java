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
package org.apache.openmeetings.db.dto.room;

import static org.apache.openmeetings.db.util.DtoHelper.optLong;

import java.io.Serializable;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

import com.github.openjson.JSONObject;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class RoomOptionsDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long roomId;
	private String externalRoomId;
	private String externalType;
	private Long recordingId;
	private boolean moderator;
	private boolean showAudioVideoTest;
	private boolean allowSameURLMultipleTimes;
	private boolean allowRecording;

	public RoomOptionsDTO() {
		//def constructor
	}

	public Long getRoomId() {
		return roomId;
	}

	public RoomOptionsDTO setRoomId(Long roomId) {
		this.roomId = roomId;
		return this;
	}

	public String getExternalRoomId() {
		return externalRoomId;
	}

	public void setExternalRoomId(String externalRoomId) {
		this.externalRoomId = externalRoomId;
	}

	public String getExternalType() {
		return externalType;
	}

	public void setExternalType(String externalType) {
		this.externalType = externalType;
	}

	public Long getRecordingId() {
		return recordingId;
	}

	public void setRecordingId(Long recordingId) {
		this.recordingId = recordingId;
	}

	public boolean isModerator() {
		return moderator;
	}

	public RoomOptionsDTO setModerator(boolean moderator) {
		this.moderator = moderator;
		return this;
	}

	public boolean isShowAudioVideoTest() {
		return showAudioVideoTest;
	}

	public void setShowAudioVideoTest(boolean showAudioVideoTest) {
		this.showAudioVideoTest = showAudioVideoTest;
	}

	public boolean isAllowSameURLMultipleTimes() {
		return allowSameURLMultipleTimes;
	}

	public void setAllowSameURLMultipleTimes(boolean allowSameURLMultipleTimes) {
		this.allowSameURLMultipleTimes = allowSameURLMultipleTimes;
	}

	public boolean isAllowRecording() {
		return allowRecording;
	}

	public void setAllowRecording(boolean allowRecording) {
		this.allowRecording = allowRecording;
	}

	public static RoomOptionsDTO fromString(String s) {
		JSONObject o = new JSONObject(s);
		RoomOptionsDTO ro = new RoomOptionsDTO();
		ro.allowRecording = o.optBoolean("allowRecording", false);
		ro.allowSameURLMultipleTimes = o.optBoolean("allowSameURLMultipleTimes", false);
		ro.moderator = o.optBoolean("moderator", false);
		ro.recordingId = optLong(o, "recordingId");
		ro.roomId = optLong(o, "roomId");
		ro.externalRoomId = o.optString("externalRoomId");
		ro.externalType = o.optString("externalType");
		ro.showAudioVideoTest = o.optBoolean("showAudioVideoTest", false);
		return ro;
	}

	@Override
	public String toString() {
		return new JSONObject(this).toString();
	}
}
