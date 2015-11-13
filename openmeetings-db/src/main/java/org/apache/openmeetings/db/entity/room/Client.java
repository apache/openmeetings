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
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.openmeetings.db.entity.IDataProviderEntity;
import org.apache.openmeetings.db.entity.server.Server;
import org.apache.openmeetings.util.CalendarPatterns;

/**
 * Can be configured to be stored in memory or in database
 * 
 * @author sebawagner
 */
@Entity
@NamedQueries({
	@NamedQuery(name = "deleteAll", query = "DELETE FROM Client"),
	@NamedQuery(name = "deletedById", query = "DELETE FROM Client c WHERE c.id = :id"),
	@NamedQuery(name = "deleteClientsByServer", query = "DELETE FROM Client c WHERE c.server = :server"),
	@NamedQuery(name = "deletedByServerAndStreamId", query = "DELETE FROM Client c WHERE c.server = :server AND c.streamid LIKE :streamid"),
	@NamedQuery(name = "countClients", query = "SELECT count(c.id) FROM Client c"),
	@NamedQuery(name = "countClientsByServer", query = "SELECT count(c.id) FROM Client c WHERE c.server = :server"),
	@NamedQuery(name = "countClientsByServerAndStreamId", query = "SELECT count(c.id) FROM Client c WHERE c.streamid LIKE :streamid AND c.server = :server"),
	@NamedQuery(name = "getClientByServerAndStreamId", query = "SELECT c FROM Client c WHERE c.streamid LIKE :streamid AND c.server = :server"),
	@NamedQuery(name = "getClientsByPublicSIDAndServer", query = "SELECT c FROM Client c WHERE c.publicSID LIKE :publicSID AND c.server = :server"),
	@NamedQuery(name = "getClientsByPublicSID", query = "SELECT c FROM Client c WHERE c.publicSID LIKE :publicSID"),
	@NamedQuery(name = "getClientsByServer", query = "SELECT c FROM Client c WHERE c.server = :server"),
	@NamedQuery(name = "getClients", query = "SELECT c FROM Client c"),
	@NamedQuery(name = "getClientsWithServer", query = "SELECT c FROM Client c LEFT JOIN FETCH c.server"),
	@NamedQuery(name = "getClientsByUserId", query = "SELECT c FROM Client c WHERE c.server = :server AND c.userId = :userId"),
	@NamedQuery(name = "getClientsByRoomId", query = "SELECT c FROM Client c WHERE c.roomId = :roomId"),
	@NamedQuery(name = "getRoomsIdsByServer", query = "SELECT c.roomId FROM Client c WHERE c.server = :server GROUP BY c.roomId")
})
@Table(name = "client")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Client implements IDataProviderEntity {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	/**
	 * @see Client#getUsername()
	 */
	@Column(name = "username")
	private String username = "";
	
	/**
	 * @see Client#getStreamid()
	 */
	@Column(name = "streamid")
	private String streamid = "";
	
	/**
	 * @see Client#getScope()
	 */
	@Column(name = "scope")
	private String scope = "";
	
	/**
	 * @see Client#getVWidth()
	 */
	@Column(name = "vwidth")
	private int vWidth = 0;
	
	/**
	 * @see Client#getVHeight()
	 */
	@Column(name = "vheight")
	private int vHeight = 0;
	
	/**
	 * @see Client#getVX()
	 */
	@Column(name = "vx")
	private int vX = 0;
	
	/**
	 * @see Client#getVY()
	 */
	@Column(name = "vy")
	private int vY = 0;
	
	/**
	 * @see Client#getStreamPublishName()
	 */
	@Column(name = "stream_publish_name")
	private String streamPublishName = "";
	
	/**
	 * @see Client#getPublicSID()
	 */
	@Column(name = "public_sid")
	private String publicSID = "";
	
	/**
	 * @see Client#getIsMod()
	 */
	@Column(name = "is_mod")
	private boolean isMod = false;
	
	/**
	 * @see Client#getIsSuperModerator()
	 */
	@Column(name = "is_supermoderator")
	private Boolean isSuperModerator = false;
	
	/**
	 * @see Client#getCanDraw()
	 */
	@Column(name = "can_draw")
	private Boolean canDraw = false;
	
	/**
	 * @see Client#getCanShare()
	 */
	@Column(name = "can_share")
	private Boolean canShare = false;
	
	/**
	 * @see Client#getCanRemote()
	 */
	@Column(name = "can_remote")
	private Boolean canRemote = false;
	
	/**
	 * @see Client#getCanGiveAudio()
	 */
	@Column(name = "can_giveaudio")
	private Boolean canGiveAudio = false;

	@Column(name = "can_video")
	private boolean canVideo = false;

	/**
	 * @see Client#getConnectedSince()
	 */
	@Column(name = "connected_since")
	private Date connectedSince;
	
	/**
	 * @see Client#getFormatedDate()
	 */
	@Column(name = "formated_date")
	private String formatedDate;
	
	/**
	 * @see Client#isScreenClient()
	 */
	@Column(name = "is_screenclient")
	private boolean screenClient;
	
	/**
	 * @see Client#isAvClient()
	 */
	@Column(name = "is_avclient")
	private boolean avClient;
	
	/**
	 * @see Client#getUsercolor()
	 */
	@Column(name = "usercolor")
	private String usercolor;
	
	/**
	 * @see Client#getUserpos()
	 */
	@Column(name = "userpos")
	private Integer userpos;
	
	/**
	 * @see Client#getUserip()
	 */
	@Column(name = "userip")
	private String userip;
	
	/**
	 * @see Client#getUserport()
	 */
	@Column(name = "userport")
	private int userport;
	
	/**
	 * @see Client#getRoomId()
	 */
	@Column(name = "room_id")
	private Long roomId;
	
	/**
	 * @see Client#getRoomEnter()
	 */
	@Column(name = "room_enter")
	private Date roomEnter = null;
	
	/**
	 * @see Client#getBroadCastID()
	 */
	@Column(name = "broadcast_id")
	private long broadCastID = -2;
	
	/**
	 * @see Client#getUserId()
	 */
	@Column(name = "user_id")
	private Long userId = null;
	
	/**
	 * @see Client#getFirstname()
	 */
	@Column(name = "firstname")
	private String firstname = "";
	
	/**
	 * @see Client#getLastname()
	 */
	@Column(name = "lastname")
	private String lastname = "";
	
	/**
	 * @see Client#getMail()
	 */
	@Column(name = "email")
	private String email;
	
	/**
	 * @see Client#getLastLogin()
	 */
	@Column(name = "last_login")
	private String lastLogin;
	
	/**
	 * @see Client#getOfficial_code()
	 */
	@Column(name = "official_code")
	private String official_code;
	
	/**
	 * @see Client#getPicture_uri()
	 */
	@Column(name = "picture_uri")
	private String picture_uri;
	
	/**
	 * @see Client#getLanguage()
	 */
	@Column(name = "language")
	private String language = "";
	
	/**
	 * @see Client#getAvsettings()
	 */
	@Column(name = "avsettings")
	private String avsettings = "";
	
	/**
	 * @see Client#getSwfurl()
	 */
	// FIXME: Move to {@link ClientSession}
	@Column(name = "swfurl", length=2048)
	private String swfurl;
	
	/**
	 * @see Client#getIsRecording()
	 */
	@Column(name = "is_recording")
	private Boolean isRecording = false;
	
	/**
	 * @see Client#getRoomRecordingName()
	 */
	@Column(name = "room_recording_name")
	private String roomRecordingName;
	
	/**
	 * @see Client#getRecordingId()
	 */
	@Column(name = "recording_id")
	private Long recordingId;
	
	/**
	 * @see Client#getRecordingMetaDataId()
	 */
	@Column(name = "recording_metadata_id")
	private Long recordingMetaDataId;
	
	/**
	 * @see Client#isStartRecording()
	 */
	@Column(name = "start_recording")
	private boolean startRecording = false;
	
	/**
	 * @see Client#isStartStreaming()
	 */
	@Column(name = "start_streaming")
	private boolean startStreaming = false;
	
	/**
	 * @see Client#isScreenPublishStarted()
	 */
	@Column(name = "screen_publish_started")
	private boolean screenPublishStarted = false;
	
	/**
	 * @see Client#isStreamPublishStarted()
	 */
	@Column(name = "stream_publish_started")
	private boolean streamPublishStarted = false;
	
	/**
	 * @see Client#getIsBroadcasting()
	 */
	@Column(name = "is_broadcasting")
	private Boolean isBroadcasting = false;
	
	/**
	 * @see Client#getExternalUserId()
	 */
	@Column(name = "external_user_id")
	private String externalUserId;
	
	/**
	 * @see Client#getExternalUserType()
	 */
	@Column(name = "external_user_type")
	private String externalUserType;
	
	/**
	 * @see Client#getInterviewPodId()
	 */
	@Column(name = "interview_pod_id")
	private Integer interviewPodId = null;
	
	/**
	 * @see Client#isAllowRecording()
	 */
	@Column(name = "allow_recording")
	private boolean allowRecording = true;
	
	/**
	 * @see Client#getZombieCheckFlag()
	 */
	@Column(name = "zombie_check_flag")
	private Boolean zombieCheckFlag = false;
	
	/**
	 * @see Client#getMicMuted()
	 */
	@Column(name = "mic_muted")
	private Boolean micMuted = false;
	
	/**
	 * @see Client#isSipTransport()
	 */
	@Column(name = "sip_transport")
	private boolean sipTransport = false;
	
	@Column(name = "mobile")
	private boolean mobile = false;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "server_id")
	private Server server;
    
	public Client() {}
    
	public Client(String streamid, String publicSID, Long roomId,
			Long userId, String firstname, String lastname, boolean avClient,
			String username, String connectedSince, String scope) {
		super();
		this.streamid = streamid;
		this.publicSID = publicSID;
		this.roomId = roomId;
		this.userId = userId;
		this.firstname = firstname;
		this.lastname = lastname;
		this.avClient = avClient;
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
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getOfficial_code() {
		return official_code;
	}

	public void setOfficial_code(String official_code) {
		this.official_code = official_code;
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

	public Boolean getIsRecording() {
		return isRecording;
	}

	public void setIsRecording(Boolean isRecording) {
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

	public Boolean getZombieCheckFlag() {
		return zombieCheckFlag;
	}
	
	public void setZombieCheckFlag(Boolean zombieCheckFlag) {
		this.zombieCheckFlag = zombieCheckFlag;
	}

	public Boolean getMicMuted() {
		return micMuted;
	}

	public void setMicMuted(Boolean micMuted) {
		this.micMuted = micMuted;
	}

	public Boolean getCanDraw() {
		return canDraw;
	}

	public void setCanDraw(Boolean canDraw) {
		this.canDraw = canDraw;
	}

	public Boolean getIsBroadcasting() {
		return isBroadcasting;
	}

	public void setIsBroadcasting(Boolean isBroadcasting) {
		this.isBroadcasting = isBroadcasting;
	}

	public Boolean getCanShare() {
		return canShare;
	}

	public void setCanShare(Boolean canShare) {
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

	public Boolean getIsSuperModerator() {
		return isSuperModerator;
	}

	public void setIsSuperModerator(Boolean isSuperModerator) {
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

	public Boolean getCanRemote() {
		return canRemote;
	}

	public void setCanRemote(Boolean canRemote) {
		this.canRemote = canRemote;
	}

	public Boolean getCanGiveAudio() {
		return canGiveAudio;
	}

	public void setCanGiveAudio(Boolean canGiveAudio) {
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

	public boolean isAvClient() {
		return avClient;
	}

	public void setAvClient(boolean avClient) {
		this.avClient = avClient;
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
	
	public Server getServer() {
		return server;
	}

	public void setServer(Server server) {
		this.server = server;
	}

	public boolean isMobile() {
		return mobile;
	}

	public void setMobile(boolean mobile) {
		this.mobile = mobile;
	}

	@Override
	public String toString() {
		return "Client [streamid=" + streamid + ", publicSID=" + publicSID + ", isScreenClient=" + screenClient
				+ ", avClient=" + avClient + ", isMobile = " + mobile + ", roomId=" + roomId + ", broadCastID=" + broadCastID + ", userId="
				+ userId + ", avsettings=" + avsettings + ", isRecording=" + isRecording + ", recordingId="
				+ recordingId + ", recordingMetaDataId=" + recordingMetaDataId + ", screenPublishStarted="
				+ screenPublishStarted + ", interviewPodId=" + interviewPodId + ", server=" + server + "]";
	}
}
