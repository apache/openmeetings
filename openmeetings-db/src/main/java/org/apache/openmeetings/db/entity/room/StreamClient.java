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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.db.entity.basic.IClient;
import org.apache.openmeetings.db.entity.server.Server;
import org.apache.wicket.util.string.StringValue;

/**
 * Can be configured to be stored in memory or in database
 *
 * @author sebawagner
 */
@Entity
@NamedQueries({
	@NamedQuery(name = "deleteClientAll", query = "DELETE FROM StreamClient"),
	@NamedQuery(name = "deleteClientById", query = "DELETE FROM StreamClient c WHERE c.id = :id"),
	@NamedQuery(name = "getClientById", query = "SELECT c FROM StreamClient c WHERE c.id = :id"),
	@NamedQuery(name = "deleteClientsByServer", query = "DELETE FROM StreamClient c WHERE c.server = :server"),
	@NamedQuery(name = "countClients", query = "SELECT count(c) FROM StreamClient c"),
	@NamedQuery(name = "countClientsByServer", query = "SELECT count(c) FROM StreamClient c WHERE c.server = :server"),
	@NamedQuery(name = "getClientsByUidAndServer", query = "SELECT c FROM StreamClient c WHERE c.uid = :uid AND c.server = :server"),
	@NamedQuery(name = "getClientsByUid", query = "SELECT c FROM StreamClient c WHERE c.uid = :uid"),
	@NamedQuery(name = "getClientsByServer", query = "SELECT c FROM StreamClient c WHERE c.server = :server"),
	@NamedQuery(name = "getClients", query = "SELECT c FROM StreamClient c"),
	@NamedQuery(name = "getClientsWithServer", query = "SELECT c FROM StreamClient c LEFT JOIN FETCH c.server"),
	@NamedQuery(name = "getClientsByUserId", query = "SELECT c FROM StreamClient c WHERE c.server = :server AND c.userId = :userId"),
	@NamedQuery(name = "getClientsByRoomId", query = "SELECT c FROM StreamClient c WHERE c.scope = :roomId"),
	@NamedQuery(name = "getRoomsIdsByServer", query = "SELECT c.roomId FROM StreamClient c WHERE c.server = :server GROUP BY c.scope")
})
@Table(name = "client")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class StreamClient implements IClient {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	/**
	 * Red5 scope, can be roomId or 'hibernate'
	 */
	@Column(name = "scope")
	private String scope = "";

	/**
	 * The width of the video
	 */
	@Column(name = "width")
	private int width = 0;

	/**
	 * The height of the video
	 */
	@Column(name = "height")
	private int height = 0;

	/**
	 * {@link Client#getUid()} of the client this stream is originated from
	 *
	 */
	@Column(name = "uid")
	private String uid = null;

	/**
	 * {@link Client#getSid()} of the client who initiated the connection
	 */
	@Column(name = "owner_sid")
	private String ownerSid = null;

	/**
	 * Is this user moderator
	 */
	@Column(name = "is_mod", nullable = false)
	private boolean mod = false;

	/**
	 * Is this user "super moderator"
	 */
	@Column(name = "super_mod", nullable = false)
	private boolean superMod = false;

	/**
	 * @see StreamClient#getCanGiveAudio()
	 */
	@Column(name = "can_giveaudio", nullable = false)
	private boolean canGiveAudio = false;

	@Column(name = "can_video", nullable = false)
	private boolean canVideo = false;

	/**
	 * @see StreamClient#getConnectedSince()
	 */
	@Column(name = "connected_since")
	private Date connectedSince;

	/**
	 * @see StreamClient#getUserip()
	 */
	@Column(name = "userip")
	private String userip;

	/**
	 * @see StreamClient#getUserport()
	 */
	@Column(name = "userport")
	private int userport;

	/**
	 * @see StreamClient#getRoomEnter()
	 */
	@Column(name = "room_enter")
	private Date roomEnter = null;

	/**
	 * @see StreamClient#getBroadCastID()
	 */
	@Column(name = "broadcast_id")
	private String broadCastId = null;

	@Column(name = "username")
	private String username = "";

	@Column(name = "user_id")
	private Long userId = null;

	@Column(name = "firstname")
	private String firstname = "";

	@Column(name = "lastname")
	private String lastname = "";

	@Column(name = "email")
	private String email;

	@Column(name = "last_login")
	private String lastLogin;

	@Column(name = "picture_uri")
	private String picture_uri;

	@Column(name = "language")
	private String language = "";

	@Column(name = "avsettings")
	private String avsettings = "";

	@Column(name = "swfurl", length=2048)
	private String swfurl;

	@Column(name = "tcurl", length=2048)
	private String tcUrl;

	@Column(name = "nativeSsl", nullable = false)
	private boolean nativeSsl = false;

	/**
	 * Is this client connect via mobile application
	 */
	@Column(name = "mobile", nullable = false)
	private boolean mobile = false;

	/**
	 * Is this client performs screen sharing
	 */
	@Column(name = "sharing", nullable = false)
	private boolean sharing = false;

	@Column(name = "recording_started", nullable = false)
	private boolean recordingStarted = false;

	@Column(name = "sharing_started", nullable = false)
	private boolean sharingStarted = false;

	@Column(name = "publish_started", nullable = false)
	private boolean publishStarted = false;

	@Column(name = "broadcasting", nullable = false)
	private boolean broadcasting = false;

	@Column(name = "recording_id")
	private Long recordingId;

	@Column(name = "meta_id")
	private Long metaId;

	@Column(name = "external_user_id")
	private String externalUserId;

	@Column(name = "external_user_type")
	private String externalUserType;

	/**
	 * @see StreamClient#getInterviewPodId()
	 */
	@Column(name = "interview_pod_id")
	private Integer interviewPodId = null;

	/**
	 * @see StreamClient#isAllowRecording()
	 */
	@Column(name = "allow_recording", nullable = false)
	private boolean allowRecording = true;

	/**
	 * @see StreamClient#getMicMuted()
	 */
	@Column(name = "mic_muted", nullable = false)
	private boolean micMuted = false;

	/**
	 * @see StreamClient#isSipTransport()
	 */
	@Column(name = "sip_transport", nullable = false)
	private boolean sipTransport = false;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "server_id")
	private Server server;

	@Transient
	private Long roomId;

	public StreamClient() {}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
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

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getOwnerSid() {
		return ownerSid;
	}

	public void setOwnerSid(String ownerSid) {
		this.ownerSid = ownerSid;
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

	public String getUserip() {
		return userip;
	}

	public void setUserip(String userip) {
		this.userip = userip;
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

	public String getBroadCastId() {
		return broadCastId;
	}

	public void setBroadCastId(String broadCastId) {
		this.broadCastId = broadCastId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

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

	public boolean isMobile() {
		return mobile;
	}

	public void setMobile(boolean mobile) {
		this.mobile = mobile;
	}

	public boolean isSharing() {
		return sharing;
	}

	public void setSharing(boolean sharing) {
		this.sharing = sharing;
	}

	public boolean isRecordingStarted() {
		return recordingStarted;
	}

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

	public Long getRecordingId() {
		return recordingId;
	}

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

	public boolean isSipTransport() {
		return sipTransport;
	}

	public void setSipTransport(boolean sipTransport) {
		this.sipTransport = sipTransport;
	}

	public Server getServer() {
		return server;
	}

	public void setServer(Server server) {
		this.server = server;
	}

	public Long getRoomId() {
		return roomId;
	}

	@Override
	public String toString() {
		return "StreamClient [scope=" + scope + ", uid=" + uid + ", ownerSid=" + ownerSid + ", broadCastId="
				+ broadCastId + ", username=" + username + ", userId=" + userId + ", avsettings=" + avsettings + ", sharing=" + sharing
				+ ", isBroadcasting=" + broadcasting + "]";
	}

}
