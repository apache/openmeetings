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
package org.apache.openmeetings.session;

/**
 * Session object to store client values
 * 
 * @author sebawagner
 */
import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.openmeetings.utils.math.CalendarPatterns;

public class Client implements Serializable, IClientSession {
	
	private static final long serialVersionUID = 1831858089607111565L;
	
	/**
	 * login name
	 */
	private String username = "";
	
	/**
	 * a unique id
	 */
	private String streamid = "";
	private String scope = "";
	private int vWidth = 0;
	private int vHeight = 0;
	private int vX = 0;
	private int vY = 0;
	/**
	 * StreamPublishName is used in the screen sharing client to publish the stream
	 */
	private String streamPublishName = "";
	
	/**
	 * an unique PUBLIC id,
	 * this ID is needed as people can reconnect and will get a new 
	 * streamid, but we need to know if this is still the same user
	 * this Public ID can be changing also if the user does change the 
	 * security token (private SID)
	 * the private  Session ID is not written to the RoomClient-Class
	 * as every instance of the RoomClient is send to all connected users
	 * 
	 * publicSID can be empty if a audio/video user is connected but 
	 * didn't choose any device settings or the connection really just
	 * has been initialized
	 */
	private String publicSID = "";
	
	/**
	 * true indicates that this user is Moderating
	 * in Events rooms (only 1 Video) this means that this user is currently 
	 * sharing its video/audio
	 * 
	 */
	private Boolean isMod = false;
	private Boolean isSuperModerator = false;
	private Boolean canDraw = false;
	private Boolean canShare = false;
	private Boolean canRemote = false;
    private Boolean canGiveAudio = false;
	private Date connectedSince;
	private String formatedDate;
	private Boolean isScreenClient = false;
	/**
	 * If true this client is only used to stream audio/video events, 
	 * he should not receive any sync events / push messages <br/>
	 * <br/>
	 * true the user is an audio/video connection<br/>
	 * false the user is a regular user with full session object<br/>
	 * 
	 */
	private boolean isAVClient = false;
	
	/**
	 * the color of the user, only needed in 4x4 Conference, in these rooms each user has its own
	 * color 
	 */
	private String usercolor;
	/*
	 * no longer needed since broadCastId is now the new unique id
	 */
	@Deprecated
	private Integer userpos;
	/**
	 * client IP
	 */
	// FIXME: Move to {@link ClientSession}
	private String userip;
	/**
	 * client Port
	 */
	private int userport;
	/**
	 * current room id while conferencing
	 */
	private Long room_id;
	private Date roomEnter = null;
	
	/**
	 * this is the id this user is currently using to broadcast a stream
	 * default value is -2 cause otherwise this can due to disconnect
	 */
	private long broadCastID = -2;
	
	/**
	 * some vars _not_ directly connected to the user-record from the database
	 * cause a user is not _forced_ to login he can also be an invited user, so user_id
	 * might be null or 0 even if somebody is already in a conference room
	 * 
	 */
	private Long user_id = null;
	private String firstname = "";
	private String lastname = "";
	private String mail;
	private String lastLogin;
	private String official_code;
	private String picture_uri;
	private String language = "";
	
	/**
	 * avsettings can be:
	 * av - video and audio
	 * a - audio only
	 * v - video only
	 * n - no av only static Image
	 */
	private String avsettings = "";
	// FIXME: Move to {@link ClientSession}
	private String swfurl;
	private Boolean isRecording = false;
	private String roomRecordingName;
	private Long flvRecordingId;
	private Long flvRecordingMetaDataId;
	private Long organization_id;
	private boolean startRecording = false;
	private boolean startStreaming = false;
	private boolean screenPublishStarted = false;
	private boolean streamPublishStarted = false;
	
	/**
	 * Indicates if this User is broadcasting his stream at all
	 * Only interesting in the Event Modus
	 */
	private Boolean isBroadcasting = false;
	
	 //Vars to simulate external Users
    private String externalUserId;
    private String externalUserType;
    private List<String> sharerSIDs = new LinkedList<String>();
    
    //Session values for handling the Interviwe Room Type
    private Integer interviewPodId = null;
    private Boolean allowRecording = true;
	
	/*
	 * Zombie Flag
	 */
	private Boolean zombieCheckFlag = false;
    private Boolean micMuted = false;
    private boolean sipTransport = false;
    
    public Client() {
    	
    }
    
	public Client(String streamid, String publicSID, Long room_id,
			Long user_id, String firstname, String lastname, boolean isAVClient,
			String username, String connectedSince, String scope) {
		super();
		this.streamid = streamid;
		this.publicSID = publicSID;
		this.room_id = room_id;
		this.user_id = user_id;
		this.firstname = firstname;
		this.lastname = lastname;
		this.isAVClient = isAVClient;
		this.username = username;
		this.connectedSince = CalendarPatterns.parseDateWithHour(connectedSince);
		this.scope = scope;
	}

	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#setUserObject(java.lang.Long, java.lang.String, java.lang.String, java.lang.String)
	 */
	public void setUserObject(Long user_id, String username, String firstname, String lastname) {
		this.user_id = user_id;
		this.username = username;
		this.firstname = firstname;
		this.lastname = lastname;
	}
	
	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#setUserObject(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void setUserObject(String username, String firstname, String lastname) {
		this.username = username;
		this.firstname = firstname;
		this.lastname = lastname;
	}

	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#getConnectedSince()
	 */
	public Date getConnectedSince() {
		return connectedSince;
	}
	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#setConnectedSince(java.util.Date)
	 */
	public void setConnectedSince(Date connectedSince) {
		this.connectedSince = connectedSince;
	}
	
	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#getIsMod()
	 */
	public Boolean getIsMod() {
		return isMod;
	}
	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#setIsMod(java.lang.Boolean)
	 */
	public void setIsMod(Boolean isMod) {
		this.isMod = isMod;
	}
	
	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#getUsername()
	 */
	public String getUsername() {
		return username;
	}
	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#setUsername(java.lang.String)
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#getStreamid()
	 */
	public String getStreamid() {
		return streamid;
	}
	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#setStreamid(java.lang.String)
	 */
	public void setStreamid(String streamid) {
		this.streamid = streamid;
	}
	
	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#getScope()
	 */
	public String getScope() {
		return scope;
	}
	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#setScope(java.lang.String)
	 */
	public void setScope(String scope) {
		this.scope = scope;
	}

	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#getFormatedDate()
	 */
	public String getFormatedDate() {
		return formatedDate;
	}
	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#setFormatedDate(java.lang.String)
	 */
	public void setFormatedDate(String formatedDate) {
		this.formatedDate = formatedDate;
	}

	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#getUsercolor()
	 */
	public String getUsercolor() {
		return usercolor;
	}
	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#setUsercolor(java.lang.String)
	 */
	public void setUsercolor(String usercolor) {
		this.usercolor = usercolor;
	}

	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#getUserpos()
	 */
	public Integer getUserpos() {
		return userpos;
	}
	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#setUserpos(java.lang.Integer)
	 */
	public void setUserpos(Integer userpos) {
		this.userpos = userpos;
	}

	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#getUserip()
	 */
	public String getUserip() {
		return userip;
	}
	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#setUserip(java.lang.String)
	 */
	public void setUserip(String userip) {
		this.userip = userip;
	}

	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#getSwfurl()
	 */
	public String getSwfurl() {
		return swfurl;
	}
	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#setSwfurl(java.lang.String)
	 */
	public void setSwfurl(String swfurl) {
		this.swfurl = swfurl;
	}
	
	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#getUserport()
	 */
	public int getUserport() {
		return userport;
	}
	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#setUserport(int)
	 */
	public void setUserport(int userport) {
		this.userport = userport;
	}
	
	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#getFirstname()
	 */
	public String getFirstname() {
		return firstname;
	}
	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#setFirstname(java.lang.String)
	 */
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#getLanguage()
	 */
	public String getLanguage() {
		return language;
	}
	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#setLanguage(java.lang.String)
	 */
	public void setLanguage(String language) {
		this.language = language;
	}

	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#getLastLogin()
	 */
	public String getLastLogin() {
		return lastLogin;
	}
	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#setLastLogin(java.lang.String)
	 */
	public void setLastLogin(String lastLogin) {
		this.lastLogin = lastLogin;
	}

	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#getLastname()
	 */
	public String getLastname() {
		return lastname;
	}
	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#setLastname(java.lang.String)
	 */
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#getMail()
	 */
	public String getMail() {
		return mail;
	}
	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#setMail(java.lang.String)
	 */
	public void setMail(String mail) {
		this.mail = mail;
	}

	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#getOfficial_code()
	 */
	public String getOfficial_code() {
		return official_code;
	}
	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#setOfficial_code(java.lang.String)
	 */
	public void setOfficial_code(String official_code) {
		this.official_code = official_code;
	}

	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#getPicture_uri()
	 */
	public String getPicture_uri() {
		return picture_uri;
	}
	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#setPicture_uri(java.lang.String)
	 */
	public void setPicture_uri(String picture_uri) {
		this.picture_uri = picture_uri;
	}

	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#getUser_id()
	 */
	public Long getUser_id() {
		return user_id;
	}
	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#setUser_id(java.lang.Long)
	 */
	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}

	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#getRoom_id()
	 */
	public Long getRoom_id() {
		return room_id;
	}
	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#setRoom_id(java.lang.Long)
	 */
	public void setRoom_id(Long room_id) {
		this.room_id = room_id;
	}

	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#getRoomEnter()
	 */
	public Date getRoomEnter() {
		return roomEnter;
	}
	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#setRoomEnter(java.util.Date)
	 */
	public void setRoomEnter(Date roomEnter) {
		this.roomEnter = roomEnter;
	}

	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#getIsRecording()
	 */
	public Boolean getIsRecording() {
		return isRecording;
	}
	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#setIsRecording(java.lang.Boolean)
	 */
	public void setIsRecording(Boolean isRecording) {
		this.isRecording = isRecording;
	}

	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#getRoomRecordingName()
	 */
	public String getRoomRecordingName() {
		return roomRecordingName;
	}
	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#setRoomRecordingName(java.lang.String)
	 */
	public void setRoomRecordingName(String roomRecordingName) {
		this.roomRecordingName = roomRecordingName;
	}

	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#getAvsettings()
	 */
	public String getAvsettings() {
		return avsettings;
	}
	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#setAvsettings(java.lang.String)
	 */
	public void setAvsettings(String avsettings) {
		this.avsettings = avsettings;
	}

	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#getBroadCastID()
	 */
	public long getBroadCastID() {
		return broadCastID;
	}
	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#setBroadCastID(long)
	 */
	public void setBroadCastID(long broadCastID) {
		this.broadCastID = broadCastID;
	}

	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#getPublicSID()
	 */
	public String getPublicSID() {
		return publicSID;
	}
	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#setPublicSID(java.lang.String)
	 */
	public void setPublicSID(String publicSID) {
		this.publicSID = publicSID;
	}

	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#getZombieCheckFlag()
	 */
	public Boolean getZombieCheckFlag() {
		return zombieCheckFlag;
	}
	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#setZombieCheckFlag(java.lang.Boolean)
	 */
	public void setZombieCheckFlag(Boolean zombieCheckFlag) {
		this.zombieCheckFlag = zombieCheckFlag;
	}

	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#getMicMuted()
	 */
	public Boolean getMicMuted() {
		return micMuted;
	}
	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#setMicMuted(java.lang.Boolean)
	 */
	public void setMicMuted(Boolean micMuted) {
		this.micMuted = micMuted;
	}

	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#getCanDraw()
	 */
	public Boolean getCanDraw() {
		return canDraw;
	}
	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#setCanDraw(java.lang.Boolean)
	 */
	public void setCanDraw(Boolean canDraw) {
		this.canDraw = canDraw;
	}

	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#getIsBroadcasting()
	 */
	public Boolean getIsBroadcasting() {
		return isBroadcasting;
	}
	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#setIsBroadcasting(java.lang.Boolean)
	 */
	public void setIsBroadcasting(Boolean isBroadcasting) {
		this.isBroadcasting = isBroadcasting;
	}
	
	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#getCanShare()
	 */
	public Boolean getCanShare() {
		return canShare;
	}

	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#setCanShare(java.lang.Boolean)
	 */
	public void setCanShare(Boolean canShare) {
		this.canShare = canShare;
	}

	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#getExternalUserId()
	 */
	public String getExternalUserId() {
		return externalUserId;
	}

	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#setExternalUserId(java.lang.String)
	 */
	public void setExternalUserId(String externalUserId) {
		this.externalUserId = externalUserId;
	}

	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#getExternalUserType()
	 */
	public String getExternalUserType() {
		return externalUserType;
	}

	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#setExternalUserType(java.lang.String)
	 */
	public void setExternalUserType(String externalUserType) {
		this.externalUserType = externalUserType;
	}

	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#getSharerSIDs()
	 */
	public List<String> getSharerSIDs() {
		return sharerSIDs;
	}
	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#setSharerSIDs(java.util.List)
	 */
	public void setSharerSIDs(List<String> sharerSIDs) {
		this.sharerSIDs = sharerSIDs;
	}

	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#getIsSuperModerator()
	 */
	public Boolean getIsSuperModerator() {
		return isSuperModerator;
	}
	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#setIsSuperModerator(java.lang.Boolean)
	 */
	public void setIsSuperModerator(Boolean isSuperModerator) {
		this.isSuperModerator = isSuperModerator;
	}

	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#getIsScreenClient()
	 */
	public Boolean getIsScreenClient() {
		return isScreenClient;
	}
	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#setIsScreenClient(java.lang.Boolean)
	 */
	public void setIsScreenClient(Boolean isScreenClient) {
		this.isScreenClient = isScreenClient;
	}

	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#getVWidth()
	 */
	public int getVWidth() {
		return vWidth;
	}
	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#setVWidth(int)
	 */
	public void setVWidth(int width) {
		vWidth = width;
	}

	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#getVHeight()
	 */
	public int getVHeight() {
		return vHeight;
	}
	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#setVHeight(int)
	 */
	public void setVHeight(int height) {
		vHeight = height;
	}

	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#getVX()
	 */
	public int getVX() {
		return vX;
	}
	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#setVX(int)
	 */
	public void setVX(int vx) {
		vX = vx;
	}

	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#getVY()
	 */
	public int getVY() {
		return vY;
	}
	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#setVY(int)
	 */
	public void setVY(int vy) {
		vY = vy;
	}

	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#getStreamPublishName()
	 */
	public String getStreamPublishName() {
		return streamPublishName;
	}
	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#setStreamPublishName(java.lang.String)
	 */
	public void setStreamPublishName(String streamPublishName) {
		this.streamPublishName = streamPublishName;
	}

	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#getFlvRecordingId()
	 */
	public Long getFlvRecordingId() {
		return flvRecordingId;
	}
	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#setFlvRecordingId(java.lang.Long)
	 */
	public void setFlvRecordingId(Long flvRecordingId) {
		this.flvRecordingId = flvRecordingId;
	}

	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#getFlvRecordingMetaDataId()
	 */
	public Long getFlvRecordingMetaDataId() {
		return flvRecordingMetaDataId;
	}
	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#setFlvRecordingMetaDataId(java.lang.Long)
	 */
	public void setFlvRecordingMetaDataId(Long flvRecordingMetaDataId) {
		this.flvRecordingMetaDataId = flvRecordingMetaDataId;
	}

	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#isScreenPublishStarted()
	 */
	public boolean isScreenPublishStarted() {
		return screenPublishStarted;
	}
	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#setScreenPublishStarted(boolean)
	 */
	public void setScreenPublishStarted(boolean screenPublishStarted) {
		this.screenPublishStarted = screenPublishStarted;
	}

	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#getOrganization_id()
	 */
	public Long getOrganization_id() {
		return organization_id;
	}
	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#setOrganization_id(java.lang.Long)
	 */
	public void setOrganization_id(Long organization_id) {
		this.organization_id = organization_id;
	}

	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#isStartRecording()
	 */
	public boolean isStartRecording() {
		return startRecording;
	}
	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#setStartRecording(boolean)
	 */
	public void setStartRecording(boolean startRecording) {
		this.startRecording = startRecording;
	}

	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#isStartStreaming()
	 */
	public boolean isStartStreaming() {
		return startStreaming;
	}
	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#setStartStreaming(boolean)
	 */
	public void setStartStreaming(boolean startStreaming) {
		this.startStreaming = startStreaming;
	}

	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#getInterviewPodId()
	 */
	public Integer getInterviewPodId() {
		return interviewPodId;
	}
	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#setInterviewPodId(java.lang.Integer)
	 */
	public void setInterviewPodId(Integer interviewPodId) {
		this.interviewPodId = interviewPodId;
	}

	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#getCanRemote()
	 */
	public Boolean getCanRemote() {
		return canRemote;
	}
	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#setCanRemote(java.lang.Boolean)
	 */
	public void setCanRemote(Boolean canRemote) {
		this.canRemote = canRemote;
	}

    /* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#getCanGiveAudio()
	 */
    public Boolean getCanGiveAudio() {
		return canGiveAudio;
	}
	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#setCanGiveAudio(java.lang.Boolean)
	 */
	public void setCanGiveAudio(Boolean canGiveAudio) {
		this.canGiveAudio = canGiveAudio;
	}

	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#getAllowRecording()
	 */
	public Boolean getAllowRecording() {
		return allowRecording;
	}

	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#setAllowRecording(java.lang.Boolean)
	 */
	public void setAllowRecording(Boolean allowRecording) {
		this.allowRecording = allowRecording;
	}

	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#getIsAVClient()
	 */
	public boolean getIsAVClient() {
		return isAVClient;
	}

	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#setIsAVClient(boolean)
	 */
	public void setIsAVClient(boolean isAVClient) {
		this.isAVClient = isAVClient;
	}

	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#isStreamPublishStarted()
	 */
	public boolean isStreamPublishStarted() {
		return streamPublishStarted;
	}

	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#setStreamPublishStarted(boolean)
	 */
	public void setStreamPublishStarted(boolean streamPublishStarted) {
		this.streamPublishStarted = streamPublishStarted;
	}

	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#toString()
	 */
	@Override
	public String toString() {

		return super.toString() //
				+ " StreamId: " + this.getStreamid() //
				+ " PublicSID: " + this.getPublicSID() //
				+ " UserId: " + this.getUser_id() //
				+ " isScreenClient: " + this.getIsScreenClient() //
				+ " flvRecordingId: " + this.getFlvRecordingId() //
				+ " screenPublishStarted: " + this.isScreenPublishStarted() //
				+ " flvRecordingMetaDataId: " + this.getFlvRecordingMetaDataId() //
				+ " isRecording: " + this.getIsRecording() //
				+ " isAVClient: " + this.getIsAVClient() //
				+ " broadCastID: " + this.getBroadCastID() //
				+ " avsettings: " + this.getAvsettings() //
				;
	}

	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#isSipTransport()
	 */
	public boolean isSipTransport() {
		return sipTransport;
	}

	/* (non-Javadoc)
	 * @see org.apache.openmeetings.conference.room.IRoomClient#setSipTransport(boolean)
	 */
	public void setSipTransport(boolean sipTransport) {
		this.sipTransport = sipTransport;
	}
	
	
	
}
