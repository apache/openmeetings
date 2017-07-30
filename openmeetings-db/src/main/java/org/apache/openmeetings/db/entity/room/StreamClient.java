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
import org.apache.openmeetings.util.CalendarPatterns;
import org.apache.wicket.util.string.StringValue;

/**
 * Can be configured to be stored in memory or in database
 *
 * @author sebawagner
 */
public class StreamClient implements IClient {
	private static final long serialVersionUID = 1L;

	private String username = "";
	private String streamid = "";
	private String scope = "";
	private int vWidth = 0;
	private int vHeight = 0;
	private int vX = 0;
	private int vY = 0;
	private String streamPublishName = "";
	private String publicSID = "";
	private String ownerSid = null;
	private boolean isMod = false;
	private boolean isSuperModerator = false;
	private boolean canDraw = false;
	private boolean canShare = false;
	private boolean canRemote = false;
	private boolean canGiveAudio = false;
	private boolean canVideo = false;
	private Date connectedSince;
	private String formatedDate;
	private boolean screenClient;
	private String usercolor;
	private Integer userpos;
	private String userip;
	private int userport;
	private Long roomId;
	private Date roomEnter = null;
	private long broadCastID = -2;
	private Long userId = null;
	private String firstname = "";
	private String lastname = "";
	private String email;
	private String lastLogin;
	private String securityCode;
	private String picture_uri;
	private String language = "";
	private String avsettings = "";
	private String swfurl;
	private String tcUrl;
	private boolean nativeSsl = false;
	private boolean isRecording = false;
	private String roomRecordingName;
	private Long recordingId;
	private Long recordingMetaDataId;
	private boolean startRecording = false;
	private boolean startStreaming = false;
	private boolean screenPublishStarted = false;
	private boolean streamPublishStarted = false;
	private boolean isBroadcasting = false;
	private String externalUserId;
	private String externalUserType;
	private Integer interviewPodId = null;
	private boolean allowRecording = true;
	private boolean zombieCheckFlag = false;
	private boolean micMuted = false;
	private boolean sipTransport = false;
	private boolean mobile = false;
	private String serverId = null;

	public StreamClient() {}

	public StreamClient(String streamid, String publicSID, Long roomId,
			Long userId, String firstname, String lastname,
			String username, String connectedSince, String scope) {
		super();
		this.streamid = streamid;
		this.publicSID = publicSID;
		this.roomId = roomId;
		this.userId = userId;
		this.firstname = firstname;
		this.lastname = lastname;
		this.username = username;
		this.connectedSince = CalendarPatterns.parseDateWithHour(connectedSince);
		this.scope = scope;
	}

	public void setUserObject(Long userId, String username, String firstname, String lastname) {
		this.userId = userId;
		this.username = username;
		this.firstname = firstname;
		this.lastname = lastname;
	}

	public void setUserObject(String username, String firstname, String lastname) {
		this.username = username;
		this.firstname = firstname;
		this.lastname = lastname;
	}

	@Override
	public Long getId() {
		return null;
	}

	@Override
	public void setId(Long id) {
	}

	@Override
	public String getUid() {
		return publicSID;
	}

	public Date getConnectedSince() {
		return connectedSince;
	}

	public void setConnectedSince(Date connectedSince) {
		this.connectedSince = connectedSince;
	}

	public boolean getIsMod() {
		return isMod;
	}

	public void setIsMod(boolean isMod) {
		this.isMod = isMod;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getStreamid() {
		return streamid;
	}

	public void setStreamid(String streamid) {
		this.streamid = streamid;
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

	public String getFormatedDate() {
		return formatedDate;
	}

	public void setFormatedDate(String formatedDate) {
		this.formatedDate = formatedDate;
	}

	public String getUsercolor() {
		return usercolor;
	}

	public void setUsercolor(String usercolor) {
		this.usercolor = usercolor;
	}

	public Integer getUserpos() {
		return userpos;
	}

	public void setUserpos(Integer userpos) {
		this.userpos = userpos;
	}

	public String getUserip() {
		return userip;
	}

	public void setUserip(String userip) {
		this.userip = userip;
	}

	public String getSwfurl() {
		return swfurl;
	}

	public void setSwfurl(String swfurl) {
		this.swfurl = swfurl;
	}

	public int getUserport() {
		return userport;
	}

	public void setUserport(int userport) {
		this.userport = userport;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(String lastLogin) {
		this.lastLogin = lastLogin;
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

	public String getSecurityCode() {
		return securityCode;
	}

	public void setSecurityCode(String securityCode) {
		this.securityCode = securityCode;
	}

	public String getPicture_uri() {
		return picture_uri;
	}

	public void setPicture_uri(String picture_uri) {
		this.picture_uri = picture_uri;
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

	public Date getRoomEnter() {
		return roomEnter;
	}

	public void setRoomEnter(Date roomEnter) {
		this.roomEnter = roomEnter;
	}

	public boolean getIsRecording() {
		return isRecording;
	}

	public void setIsRecording(boolean isRecording) {
		this.isRecording = isRecording;
	}

	public String getRoomRecordingName() {
		return roomRecordingName;
	}

	public void setRoomRecordingName(String roomRecordingName) {
		this.roomRecordingName = roomRecordingName;
	}

	public String getAvsettings() {
		return avsettings;
	}

	public void setAvsettings(String avsettings) {
		this.avsettings = avsettings;
	}

	public long getBroadCastID() {
		return broadCastID;
	}

	public void setBroadCastID(long broadCastID) {
		this.broadCastID = broadCastID;
	}

	public String getPublicSID() {
		return publicSID;
	}

	public void setPublicSID(String publicSID) {
		this.publicSID = publicSID;
	}

	public String getOwnerSid() {
		return ownerSid;
	}

	public void setOwnerSid(String ownerSid) {
		this.ownerSid = ownerSid;
	}

	public boolean getZombieCheckFlag() {
		return zombieCheckFlag;
	}

	public void setZombieCheckFlag(boolean zombieCheckFlag) {
		this.zombieCheckFlag = zombieCheckFlag;
	}

	public boolean getMicMuted() {
		return micMuted;
	}

	public void setMicMuted(boolean micMuted) {
		this.micMuted = micMuted;
	}

	public boolean getCanDraw() {
		return canDraw;
	}

	public void setCanDraw(boolean canDraw) {
		this.canDraw = canDraw;
	}

	public boolean getIsBroadcasting() {
		return isBroadcasting;
	}

	public void setIsBroadcasting(boolean isBroadcasting) {
		this.isBroadcasting = isBroadcasting;
	}

	public boolean getCanShare() {
		return canShare;
	}

	public void setCanShare(boolean canShare) {
		this.canShare = canShare;
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

	public boolean getIsSuperModerator() {
		return isSuperModerator;
	}

	public void setIsSuperModerator(boolean isSuperModerator) {
		this.isSuperModerator = isSuperModerator;
	}

	public boolean isScreenClient() {
		return screenClient;
	}

	public void setScreenClient(boolean screenClient) {
		this.screenClient = screenClient;
	}

	public int getVWidth() {
		return vWidth;
	}

	public void setVWidth(int width) {
		vWidth = width;
	}

	public int getVHeight() {
		return vHeight;
	}

	public void setVHeight(int height) {
		vHeight = height;
	}

	public int getVX() {
		return vX;
	}

	public void setVX(int vx) {
		vX = vx;
	}

	public int getVY() {
		return vY;
	}

	public void setVY(int vy) {
		vY = vy;
	}

	public String getStreamPublishName() {
		return streamPublishName;
	}

	public void setStreamPublishName(String streamPublishName) {
		this.streamPublishName = streamPublishName;
	}

	public Long getRecordingId() {
		return recordingId;
	}

	public void setRecordingId(Long recordingId) {
		this.recordingId = recordingId;
	}

	public Long getRecordingMetaDataId() {
		return recordingMetaDataId;
	}

	public void setRecordingMetaDataId(Long recordingMetaDataId) {
		this.recordingMetaDataId = recordingMetaDataId;
	}

	public boolean isScreenPublishStarted() {
		return screenPublishStarted;
	}

	public void setScreenPublishStarted(boolean screenPublishStarted) {
		this.screenPublishStarted = screenPublishStarted;
	}

	public boolean isStartRecording() {
		return startRecording;
	}

	public void setStartRecording(boolean startRecording) {
		this.startRecording = startRecording;
	}

	public boolean isStartStreaming() {
		return startStreaming;
	}

	public void setStartStreaming(boolean startStreaming) {
		this.startStreaming = startStreaming;
	}

	public Integer getInterviewPodId() {
		return interviewPodId;
	}

	public void setInterviewPodId(Integer interviewPodId) {
		this.interviewPodId = interviewPodId;
	}

	public boolean getCanRemote() {
		return canRemote;
	}

	public void setCanRemote(boolean canRemote) {
		this.canRemote = canRemote;
	}

	public boolean getCanGiveAudio() {
		return canGiveAudio;
	}

	public void setCanGiveAudio(boolean canGiveAudio) {
		this.canGiveAudio = canGiveAudio;
	}

	public boolean getCanVideo() {
		return canVideo;
	}

	public void setCanVideo(boolean canVideo) {
		this.canVideo = canVideo;
	}

	public boolean isAllowRecording() {
		return allowRecording;
	}

	public void setAllowRecording(boolean allowRecording) {
		this.allowRecording = allowRecording;
	}

	public boolean isStreamPublishStarted() {
		return streamPublishStarted;
	}

	public void setStreamPublishStarted(boolean streamPublishStarted) {
		this.streamPublishStarted = streamPublishStarted;
	}

	public boolean isSipTransport() {
		return sipTransport;
	}

	public void setSipTransport(boolean sipTransport) {
		this.sipTransport = sipTransport;
	}

	@Override
	public String getServerId() {
		return serverId;
	}

	public void setServerId(String serverId) {
		this.serverId = serverId;
	}

	public boolean isMobile() {
		return mobile;
	}

	public void setMobile(boolean mobile) {
		this.mobile = mobile;
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

	@Override
	public String toString() {
		return "Client [streamid=" + streamid + ", publicSID=" + publicSID + ", isScreenClient=" + screenClient
				+ ", isMobile = " + mobile + ", roomId=" + roomId + ", broadCastID=" + broadCastID + ", userId="
				+ userId + ", avsettings=" + avsettings + ", isRecording=" + isRecording + ", recordingId="
				+ recordingId + ", recordingMetaDataId=" + recordingMetaDataId + ", screenPublishStarted="
				+ screenPublishStarted + ", interviewPodId=" + interviewPodId + ", server=" + serverId + "]";
	}
}
