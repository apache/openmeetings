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
package org.apache.openmeetings.persistence.beans.basic;

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
	private static final long serialVersionUID = 5101010700038221434L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	private long soapLoginId;
	
	@Column(name="hash")
	private String hash;
	
	@Column(name="room_id")
	private Long room_id;
	
	@Column(name="session_hash")
	private String sessionHash;
	
	@Column(name="created")
	private Date created;
	
	@Column(name="used")
	private Boolean used;
	
	@Column(name="use_date")
	private Date useDate;
	
	@Column(name="becomemoderator")
	private Boolean becomemoderator;
	
	@Column(name="showaudiovideotest")
	private Boolean showAudioVideoTest;
	
	@Column(name="allow_same_url_multiple_times")
	private Boolean allowSameURLMultipleTimes;
	
	@Column(name="show_nick_name_dialog")
	private Boolean showNickNameDialog;
	
	@Column(name="client_url")
	private String clientURL;
	
	@Column(name="room_recording_id")
	private Long roomRecordingId;
	
	@Column(name="landing_zone")
	private String landingZone;
	
	@Column(name="allow_recording")
	private Boolean allowRecording;
	
	public long getSoapLoginId() {
		return soapLoginId;
	}
	public void setSoapLoginId(long soapLoginId) {
		this.soapLoginId = soapLoginId;
	}
	
	public String getHash() {
		return hash;
	}
	public void setHash(String hash) {
		this.hash = hash;
	}
	
	public Long getRoom_id() {
		return room_id;
	}
	public void setRoom_id(Long room_id) {
		this.room_id = room_id;
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
	
	public Boolean getUsed() {
		return used;
	}
	public void setUsed(Boolean used) {
		this.used = used;
	}
	
	public Date getUseDate() {
		return useDate;
	}
	public void setUseDate(Date useDate) {
		this.useDate = useDate;
	}
	
	public Boolean getBecomemoderator() {
		return becomemoderator;
	}
	public void setBecomemoderator(Boolean becomemoderator) {
		this.becomemoderator = becomemoderator;
	}
	
	public Boolean getShowAudioVideoTest() {
		return showAudioVideoTest;
	}
	public void setShowAudioVideoTest(Boolean showAudioVideoTest) {
		this.showAudioVideoTest = showAudioVideoTest;
	}
	
	public Boolean getAllowSameURLMultipleTimes() {
		return allowSameURLMultipleTimes;
	}
	public void setAllowSameURLMultipleTimes(Boolean allowSameURLMultipleTimes) {
		this.allowSameURLMultipleTimes = allowSameURLMultipleTimes;
	}

	public Boolean getShowNickNameDialog() {
		return showNickNameDialog;
	}
	public void setShowNickNameDialog(Boolean showNickNameDialog) {
		this.showNickNameDialog = showNickNameDialog;
	}
	
	public String getClientURL() {
		return clientURL;
	}
	public void setClientURL(String clientURL) {
		this.clientURL = clientURL;
	}
	
	public Long getRoomRecordingId() {
		return roomRecordingId;
	}
	public void setRoomRecordingId(Long roomRecordingId) {
		this.roomRecordingId = roomRecordingId;
	}
	
	public String getLandingZone() {
		return landingZone;
	}
	public void setLandingZone(String landingZone) {
		this.landingZone = landingZone;
	}
	
	public Boolean getAllowRecording() {
		return allowRecording;
	}
	public void setAllowRecording(Boolean allowRecording) {
		this.allowRecording = allowRecording;
	}
	
}
