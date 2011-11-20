package org.openmeetings.app.persistence.beans.recording;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;


@Entity
@Table(name = "roomclient")
public class RoomClient implements Serializable {
	   
	private static final long serialVersionUID = 1831858089607111565L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="roomclient_id")
	private Long roomClientId = null;
	
	/*
	 * login name
	 */
	@Column(name="username")
	private String username = "";
	
	/*
	 * a unique id
	 */
	@Column(name="streamid")
	private String streamid = "";
	
	@Column(name="scope")
	private String scope = "";
	
	@Column(name="vWidth")
	private int vWidth = 0;
	@Column(name="vHeight")
	private int vHeight = 0;
	@Column(name="vX")
	private int vX = 0;
	@Column(name="vY")
	private int vY = 0;
	@Column(name="streamPublishName")
	private String streamPublishName = "";
	
	/*
	 * an unique PUBLIC id,
	 * this ID is needed as people can reconnect and will get a new 
	 * streamid, but we need to know if this is still the same user
	 * this Public ID can be changing also if the user does change the 
	 * security token (private SID)
	 * the private  Session ID is not written to the RoomClient-Class
	 * as every instance of the RoomClient is send to all connected users
	 * 
	 */
	@Column(name="public_sid")
	private String publicSID = "";
	
	/*
	 * true indicates that this user is Moderating
	 * in Events rooms (only 1 Video) this means that this user is currently 
	 * sharing its video/audio
	 * 
	 */
	@Column(name="is_mod")
	private Boolean isMod = false;
	@Column(name="is_super_mod")
	private Boolean isSuperModerator = false;
	@Column(name="canDraw")
	private Boolean canDraw = false;
	@Column(name="canShare")
	private Boolean canShare = false;
	@Column(name="canRemote")
	private Boolean canRemote = false;
	@Column(name="connected_since")
	private Date connectedSince;
	@Column(name="formated_date")
	private String formatedDate;
	@Column(name="isScreenClient")
	private Boolean isScreenClient = false;
	
	/*
	 * the color of the user, only needed in 4x4 Conference, in these rooms each user has its own
	 * color 
	 */
	@Column(name="usercolor")
	private String usercolor;
	/*
	 * no longer needed since broadCastId is now the new unique id
	 * 
	 * @deprecated
	 */
	@Column(name="userpos")
	private Integer userpos;
	/*
	 * client IP
	 */
	@Column(name="userip")
	private String userip;
	/*
	 * client Port
	 */
	@Column(name="userport")
	private int userport;
	/*
	 * current room idd while conferencing
	 */
	@Column(name="room_id")
	private Long room_id;
	
	@Column(name="room_enter")
	private Date roomEnter = null;
	
	/*
	 * this is the id this user is currently using to broadcast a stream
	 * default value is -2 cause otherwise this can due to disconnect
	 */
	@Column(name="broadcast_id")
	private long broadCastID = -2;
	
	/*
	 * some vars _not_ directly connected to the user-record from the database
	 * cause a user is not _forced_ to login he can also be an invited user, so user_id
	 * might be null or 0 even if somebody is already in a conference room
	 * 
	 */
	@Column(name="user_id")
	private Long user_id = null;
	@Column(name="firstname")
	private String firstname = "";
	@Column(name="lastname")
	private String lastname = "";
	@Column(name="mail")
	private String mail;
	@Column(name="last_login")
	private String lastLogin;
	@Column(name="official_code")
	private String official_code;
	@Column(name="picture_uri")
	private String picture_uri;
	@Column(name="language")
	private String language = "";
	
	/*
	 * these vars are necessary to send notifications from the chatroom of a 
	 * conference to outside of the conference room
	 */
	@Column(name="is_chat_notification")
	private Boolean isChatNotification = false;
	@Column(name="chat_user_room_id")
	private Long chatUserRoomId = null;
	
	/*
	 * avsettings can be:
	 * av - video and audio
	 * a - audio only
	 * v - video only
	 * n - no av only static Image
	 */
	@Column(name="avsettings")
	private String avsettings = "";
	
	@Column(name="swfurl")
	private String swfurl;
	@Column(name="is_recording")
	private Boolean isRecording = false;
	@Column(name="room_recording_name")
	private String roomRecordingName;
	
	@Column(name="flvRecordingId")
	private Long flvRecordingId;
	@Column(name="flvRecordingMetaDataId")
	private Long flvRecordingMetaDataId;
	@Column(name="organization_id") //FIXME need to add reference to org
	private Long organization_id;
	@Column(name="startRecording")
	boolean startRecording = false;
	@Column(name="startStreaming")
	boolean startStreaming = false;
	@Column(name="screenPublishStarted")
	private Boolean screenPublishStarted;
	
	/*
	 * Indicates if this User is broadcasting his stream at all
	 * Only interesting in the Event Modus
	 */
	@Column(name="isBroadcasting")
	private Boolean isBroadcasting = false;
	
	 //Vars to simulate external Users
	@Column(name="externalUserId")
    private Long externalUserId;
	@Column(name="externalUserType")
    private String externalUserType;
    
	@Transient
    private List<String> sharerSIDs = new LinkedList<String>();
    
    //Session values for handling the Interviwe Room Type
	@Column(name="interviewPodId")
    private Integer interviewPodId = null;
	@Column(name="allowRecording")
    private Boolean allowRecording = true;
	
	/*
	 * Zombie Flag
	 */
	@Column(name="zombieCheckFlag")
	private Boolean zombieCheckFlag = false;

    @Column(name="micMuted")
    private Boolean micMuted = false;
	
	public void setUserObject(Long user_id, String username, String firstname, String lastname) {
		this.user_id = user_id;
		this.username = username;
		this.firstname = firstname;
		this.lastname = lastname;
	}
	
	public void setUserObject(String username, String firstname, String lastname) {
		this.username = username;
		this.firstname = firstname;
		this.lastname = lastname;
	}
	
	/**
     * 
     * @hibernate.id
     *  column="roomclient_id"
     *  generator-class="increment"
     */
	public Long getRoomClientId() {
		return roomClientId;
	}
	public void setRoomClientId(Long roomClientId) {
		this.roomClientId = roomClientId;
	}

	/**
     * @hibernate.property
     *  column="connected_since"
     *  type="java.util.Date"
     */
	public Date getConnectedSince() {
		return connectedSince;
	}
	public void setConnectedSince(Date connectedSince) {
		this.connectedSince = connectedSince;
	}
	
	/**
     * @hibernate.property
     *  column="is_mod"
     *  type="boolean"
     */
	public Boolean getIsMod() {
		return isMod;
	}
	public void setIsMod(Boolean isMod) {
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

	public String getMail() {
		return mail;
	}
	public void setMail(String mail) {
		this.mail = mail;
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

	public Long getUser_id() {
		return user_id;
	}
	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}

	public Long getRoom_id() {
		return room_id;
	}
	public void setRoom_id(Long room_id) {
		this.room_id = room_id;
	}

	public Date getRoomEnter() {
		return roomEnter;
	}
	public void setRoomEnter(Date roomEnter) {
		this.roomEnter = roomEnter;
	}

	public Boolean getIsChatNotification() {
		return isChatNotification;
	}
	public void setIsChatNotification(Boolean isChatNotification) {
		this.isChatNotification = isChatNotification;
	} 

	public Long getChatUserRoomId() {
		return chatUserRoomId;
	}
	public void setChatUserRoomId(Long chatUserRoomId) {
		this.chatUserRoomId = chatUserRoomId;
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

	public Long getExternalUserId() {
		return externalUserId;
	}

	public void setExternalUserId(Long externalUserId) {
		this.externalUserId = externalUserId;
	}

	public String getExternalUserType() {
		return externalUserType;
	}

	public void setExternalUserType(String externalUserType) {
		this.externalUserType = externalUserType;
	}

	public List<String> getSharerSIDs() {
		return sharerSIDs;
	}
	public void setSharerSIDs(List<String> sharerSIDs) {
		this.sharerSIDs = sharerSIDs;
	}

	public Boolean getIsSuperModerator() {
		return isSuperModerator;
	}
	public void setIsSuperModerator(Boolean isSuperModerator) {
		this.isSuperModerator = isSuperModerator;
	}

	public Boolean getIsScreenClient() {
		return isScreenClient;
	}
	public void setIsScreenClient(Boolean isScreenClient) {
		this.isScreenClient = isScreenClient;
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

	public Long getFlvRecordingId() {
		return flvRecordingId;
	}
	public void setFlvRecordingId(Long flvRecordingId) {
		this.flvRecordingId = flvRecordingId;
	}

	public Long getFlvRecordingMetaDataId() {
		return flvRecordingMetaDataId;
	}
	public void setFlvRecordingMetaDataId(Long flvRecordingMetaDataId) {
		this.flvRecordingMetaDataId = flvRecordingMetaDataId;
	}

	public Boolean getScreenPublishStarted() {
		return screenPublishStarted;
	}
	public void setScreenPublishStarted(Boolean screenPublishStarted) {
		this.screenPublishStarted = screenPublishStarted;
	}

	public Long getOrganization_id() {
		return organization_id;
	}
	public void setOrganization_id(Long organization_id) {
		this.organization_id = organization_id;
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

	public Boolean getAllowRecording() {
		return allowRecording;
	}

	public void setAllowRecording(Boolean allowRecording) {
		this.allowRecording = allowRecording;
	}
	
	
}