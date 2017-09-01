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
package org.apache.openmeetings.db.entity.room;

import java.util.Date;

import org.apache.openmeetings.db.entity.basic.IClient;
import org.apache.wicket.util.string.StringValue;

/**
 * Can be configured to be stored in memory or in database
 *
 * @author sebawagner
 */
public class StreamClient implements IClient {
	private static final long serialVersionUID = 1L;

	private String scope = "";
	private int width = 0;
	private int height = 0;
	private String uid = null;
	private String sid = null;
	private boolean mod = false;
	private boolean superMod = false;
	private boolean canGiveAudio = false;
	private boolean canVideo = false;
	private Date connectedSince;
	private String remoteAddress;
	private int userport;
	private Date roomEnter = null;
	private String broadcastId = null;
	private String login = "";
	private Long userId = null;
	private String firstname = "";
	private String lastname = "";
	private String email;
	private String lastLogin;
	private String picture_uri;
	private String language = "";
	private String avsettings = "";
	private String swfurl;
	private String tcUrl;
	private boolean nativeSsl = false;
	private boolean recordingStarted = false;
	private boolean sharingStarted = false;
	private boolean publishStarted = false;
	private boolean broadcasting = false;
	private Long recordingId;
	private Long metaId;
	private String externalUserId;
	private String externalUserType;
	private Integer interviewPodId = null;
	private boolean allowRecording = true;
	private boolean micMuted = false;
	private String serverId;
	private Long roomId;
	private Type type = Type.video;

	public StreamClient() {}

	@Override
	public Long getId() {
		return null;
	}

	@Override
	public void setId(Long id) {
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
		StringValue scn = StringValue.valueOf(scope);
		long roomId = scn.toLong(Long.MIN_VALUE);
		if (roomId > 0) {
			this.roomId = roomId;
		}
	}

	@Override
	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	@Override
	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	@Override
	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public boolean isMod() {
		return mod;
	}

	public void setMod(boolean mod) {
		this.mod = mod;
	}

	public boolean isSuperMod() {
		return superMod;
	}

	public void setSuperMod(boolean superMod) {
		this.superMod = superMod;
	}

	public boolean isCanGiveAudio() {
		return canGiveAudio;
	}

	public void setCanGiveAudio(boolean canGiveAudio) {
		this.canGiveAudio = canGiveAudio;
	}

	public boolean isCanVideo() {
		return canVideo;
	}

	public void setCanVideo(boolean canVideo) {
		this.canVideo = canVideo;
	}

	public Date getConnectedSince() {
		return connectedSince;
	}

	public void setConnectedSince(Date connectedSince) {
		this.connectedSince = connectedSince;
	}

	@Override
	public String getRemoteAddress() {
		return remoteAddress;
	}

	public void setRemoteAddress(String remoteAddress) {
		this.remoteAddress = remoteAddress;
	}

	public int getUserport() {
		return userport;
	}

	public void setUserport(int userport) {
		this.userport = userport;
	}

	public Date getRoomEnter() {
		return roomEnter;
	}

	public void setRoomEnter(Date roomEnter) {
		this.roomEnter = roomEnter;
	}

	public String getBroadcastId() {
		return broadcastId;
	}

	public void setBroadcastId(String broadcastId) {
		this.broadcastId = broadcastId;
	}

	@Override
	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	@Override
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(String lastLogin) {
		this.lastLogin = lastLogin;
	}

	public String getPicture_uri() {
		return picture_uri;
	}

	public void setPicture_uri(String picture_uri) {
		this.picture_uri = picture_uri;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getAvsettings() {
		return avsettings;
	}

	public void setAvsettings(String avsettings) {
		this.avsettings = avsettings;
	}

	public String getSwfurl() {
		return swfurl;
	}

	public void setSwfurl(String swfurl) {
		this.swfurl = swfurl;
	}

	public String getTcUrl() {
		return tcUrl;
	}

	public void setTcUrl(String tcUrl) {
		this.tcUrl = tcUrl;
	}

	public boolean isNativeSsl() {
		return nativeSsl;
	}

	public void setNativeSsl(boolean nativeSsl) {
		this.nativeSsl = nativeSsl;
	}

	public boolean isRecordingStarted() {
		return recordingStarted;
	}

	@Override
	public void setRecordingStarted(boolean recordingStarted) {
		this.recordingStarted = recordingStarted;
	}

	public boolean isSharingStarted() {
		return sharingStarted;
	}

	public void setSharingStarted(boolean sharingStarted) {
		this.sharingStarted = sharingStarted;
	}

	public boolean isPublishStarted() {
		return publishStarted;
	}

	public void setPublishStarted(boolean publishStarted) {
		this.publishStarted = publishStarted;
	}

	public boolean isBroadcasting() {
		return broadcasting;
	}

	public void setBroadcasting(boolean isBroadcasting) {
		this.broadcasting = isBroadcasting;
	}

	@Override
	public Long getRecordingId() {
		return recordingId;
	}

	@Override
	public void setRecordingId(Long recordingId) {
		this.recordingId = recordingId;
	}

	public Long getMetaId() {
		return metaId;
	}

	public void setMetaId(Long metaId) {
		this.metaId = metaId;
	}

	public String getExternalUserId() {
		return externalUserId;
	}

	public void setExternalUserId(String externalUserId) {
		this.externalUserId = externalUserId;
	}

	public String getExternalUserType() {
		return externalUserType;
	}

	public void setExternalUserType(String externalUserType) {
		this.externalUserType = externalUserType;
	}

	public Integer getInterviewPodId() {
		return interviewPodId;
	}

	public void setInterviewPodId(Integer interviewPodId) {
		this.interviewPodId = interviewPodId;
	}

	public boolean isAllowRecording() {
		return allowRecording;
	}

	public void setAllowRecording(boolean allowRecording) {
		this.allowRecording = allowRecording;
	}

	public boolean isMicMuted() {
		return micMuted;
	}

	public void setMicMuted(boolean micMuted) {
		this.micMuted = micMuted;
	}

	@Override
	public String getServerId() {
		return serverId;
	}

	public void setServerId(String serverId) {
		this.serverId = serverId;
	}

	@Override
	public Long getRoomId() {
		return roomId;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "StreamClient [scope=" + scope + ", uid=" + uid + ", sid=" + sid + ", broadCastId="
				+ broadcastId + ", login=" + login + ", userId=" + userId + ", avsettings=" + avsettings + ", type=" + type
				+ ", isBroadcasting=" + broadcasting + "]";
	}
}
