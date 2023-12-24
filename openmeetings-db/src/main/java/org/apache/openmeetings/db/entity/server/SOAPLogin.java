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

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

@Entity
@Table(name = "soaplogin", indexes = {
		@Index(name = "soap_hash_idx", columnList = "hash", unique = true)
})
@NamedQuery(name = "getSoapLoginByHash", query = "SELECT s FROM SOAPLogin s WHERE s.hash LIKE :hash")
public class SOAPLogin implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "hash")
	private String hash;

	@Column(name = "room_id")
	private Long roomId;

	@Column(name = "external_room_id")
	private String externalRoomId;

	@Column(name = "external_type")
	private String externalType;

	@Column(name = "session_hash")
	private String sessionHash;

	@Column(name = "created")
	private Date created;

	@Column(name = "used", nullable = false)
	private boolean used;

	@Column(name = "use_date")
	private Date useDate;

	@Column(name = "moderator", nullable = false)
	private boolean moderator;

	@Column(name = "showaudiovideotest", nullable = false)
	private boolean showAudioVideoTest;

	@Column(name = "allow_same_url_multiple_times", nullable = false)
	private boolean allowSameURLMultipleTimes;

	@Column(name = "client_url")
	private String clientURL;

	@Column(name = "recording_id")
	private Long recordingId;

	@Column(name = "allow_recording", nullable = false)
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

	public boolean isAllowRecording() {
		return allowRecording;
	}

	public void setAllowRecording(boolean allowRecording) {
		this.allowRecording = allowRecording;
	}
}
