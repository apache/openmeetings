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

public class RoomOptionsDTO {
	private Long roomId;
	private Long recordingId;
	private boolean moderator;
	private boolean showAudioVideoTest;
	private boolean showNickNameDialog;
	private boolean allowSameURLMultipleTimes;
	private boolean allowRecording;
	
	public Long getRoomId() {
		return roomId;
	}
	public void setRoomId(Long roomId) {
		this.roomId = roomId;
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
	public void setModerator(boolean moderator) {
		this.moderator = moderator;
	}
	public boolean isShowAudioVideoTest() {
		return showAudioVideoTest;
	}
	public void setShowAudioVideoTest(boolean showAudioVideoTest) {
		this.showAudioVideoTest = showAudioVideoTest;
	}
	public boolean isShowNickNameDialog() {
		return showNickNameDialog;
	}
	public void setShowNickNameDialog(boolean showNickNameDialog) {
		this.showNickNameDialog = showNickNameDialog;
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
}
