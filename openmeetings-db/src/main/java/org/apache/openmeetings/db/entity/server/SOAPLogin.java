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
package org.apache.openmeetings.db.entity.server;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "soaplogin")
public class SOAPLogin implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	private Long id;
	
	@Column(name="hash")
	private String hash;
	
	@Column(name="room_id")
	private Long roomId;
	
	@Column(name="session_hash")
	private String sessionHash;
	
	@Column(name="created")
	private Date created;
	
	@Column(name="used")
	private boolean used;
	
	@Column(name="use_date")
	private Date useDate;
	
	@Column(name="moderator")
	private boolean moderator;
	
	@Column(name="showaudiovideotest")
	private boolean showAudioVideoTest;
	
	@Column(name="allow_same_url_multiple_times")
	private boolean allowSameURLMultipleTimes;
	
	@Column(name="client_url")
	private String clientURL;
	
	@Column(name="recording_id")
	private Long recordingId;
	
	@Column(name="landing_zone")
	private String landingZone;
	
	@Column(name="allow_recording")
	private boolean allowRecording;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getHash() {
		return hash;
	}
	public void setHash(String hash) {
		this.hash = hash;
	}
	
	public Long getRoomId() {
		return roomId;
	}
	public void setRoomId(Long roomId) {
		this.roomId = roomId;
	}
	
	public String getSessionHash() {
		return sessionHash;
	}
	public void setSessionHash(String sessionHash) {
		this.sessionHash = sessionHash;
	}
	
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	
	public boolean isUsed() {
		return used;
	}
	public void setUsed(boolean used) {
		this.used = used;
	}
	
	public Date getUseDate() {
		return useDate;
	}
	public void setUseDate(Date useDate) {
		this.useDate = useDate;
	}
	
	public boolean isModerator() {
		return moderator;
	}
	public void setModerator(boolean moderator) {
		this.moderator = moderator;
	}
	
	public boolean getShowAudioVideoTest() {
		return showAudioVideoTest;
	}
	public void setShowAudioVideoTest(boolean showAudioVideoTest) {
		this.showAudioVideoTest = showAudioVideoTest;
	}
	
	public boolean getAllowSameURLMultipleTimes() {
		return allowSameURLMultipleTimes;
	}
	public void setAllowSameURLMultipleTimes(boolean allowSameURLMultipleTimes) {
		this.allowSameURLMultipleTimes = allowSameURLMultipleTimes;
	}

	public String getClientURL() {
		return clientURL;
	}
	public void setClientURL(String clientURL) {
		this.clientURL = clientURL;
	}
	
	public Long getRecordingId() {
		return recordingId;
	}
	public void setRecordingId(Long recordingId) {
		this.recordingId = recordingId;
	}
	
	public String getLandingZone() {
		return landingZone;
	}
	public void setLandingZone(String landingZone) {
		this.landingZone = landingZone;
	}
	
	public boolean isAllowRecording() {
		return allowRecording;
	}
	public void setAllowRecording(boolean allowRecording) {
		this.allowRecording = allowRecording;
	}
	
}
