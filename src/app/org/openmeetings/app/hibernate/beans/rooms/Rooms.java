package org.openmeetings.app.hibernate.beans.rooms;

import java.util.Date;
import java.util.List;

import org.openmeetings.app.hibernate.beans.recording.RoomClient;

/**
 * 
 * @hibernate.class table="rooms"
 * lazy="false"
 *
 */
public class Rooms {
	
	private Long rooms_id;
	private String name;
	private String comment;
	private RoomTypes roomtype;
	private Date starttime;
	private Date updatetime;
	private String deleted;
	private Boolean ispublic;
	private Long numberOfPartizipants = new Long(4);
	
	private Boolean appointment;

	//Vars to simulate external Rooms
	private Long externalRoomId;
	private String externalRoomType;
	
	/**
	 * Layout of Room
	 */
//	private Integer videoPodWidth = new Integer(290);
//	private Integer videoPodHeight = new Integer(280);
//	private Integer videoPodXPosition = new Integer(2);
//	private Integer videoPodYPosition = new Integer(2);
//	
//	private Integer moderationPanelXPosition = new Integer(400);
//	
//	private Boolean showWhiteBoard = new Boolean(true);
//	private Integer whiteBoardPanelXPosition = new Integer(296);
//	private Integer whiteBoardPanelYPosition = new Integer(2);
//	private Integer whiteBoardPanelHeight = new Integer(592);
//	private Integer whiteBoardPanelWidth = new Integer(660);
//	
//	private Boolean showFilesPanel = new Boolean(true);
//	private Integer filesPanelXPosition = new Integer(2);
//	private Integer filesPanelYPosition = new Integer(284);
//	private Integer filesPanelHeight = new Integer(310);
//	private Integer filesPanelWidth = new Integer(290);	
	
	private List<RoomClient> currentusers;
	
	private Boolean isDemoRoom;
	private Integer demoTime; //In Seconds
	
	//If this is true all participants of a meeting have to wait for the moderator to come into the room
	private Boolean isModeratedRoom;
	
	private Boolean allowUserQuestions;
	
	private String sipNumber;
	private String conferencePin;
	
	private Boolean isAudioOnly;
	
	private Boolean isClosed;
	private String redirectURL;
	
	private Long ownerId; //Those are the rooms from the myrooms section
	private Boolean waitForRecording; //Show warning that user has to start recording
	private Boolean allowRecording; //Show or show not the recording option in a conference room
    
	private Boolean hideTopBar = false;
	
    /**
     * @hibernate.property
     *  column="comment_field"
     *  type="text"
     */ 
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	
    /**
     * @hibernate.property
     *  column="name"
     *  type="string"
     */	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
    
    /**
     * 
     * @hibernate.id
     *  column="rooms_id"
     *  generator-class="increment"
     */ 
	public Long getRooms_id() {
		return rooms_id;
	}
	public void setRooms_id(Long rooms_id) {
		this.rooms_id = rooms_id;
	}
    
    /**
     * @hibernate.many-to-one
     *  cascade="none"
     *  column="roomtypes_id"
     *  lazy="false"
     *  class="org.openmeetings.app.hibernate.beans.rooms.RoomTypes"
     *  not-null="false"
     *  outer-join="true"
     */ 
	public RoomTypes getRoomtype() {
		return roomtype;
	}
	public void setRoomtype(RoomTypes roomtype) {
		this.roomtype = roomtype;
	}
    
    /**
     * @hibernate.property
     *  column="starttime"
     *  type="java.util.Date"
     */	
	public Date getStarttime() {
		return starttime;
	}
	public void setStarttime(Date starttime) {
		this.starttime = starttime;
	}
    
    /**
     * @hibernate.property
     *  column="updatetime"
     *  type="java.util.Date"
     */	
	public Date getUpdatetime() {
		return updatetime;
	}
	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}	
	
    /**
     * @hibernate.property
     *  column="deleted"
     *  type="string"
     */	
	public String getDeleted() {
		return deleted;
	}
	public void setDeleted(String deleted) {
		this.deleted = deleted;
	}
	
    /**
     * @hibernate.property
     *  column="ispublic"
     *  type="boolean"
     */	
	public Boolean getIspublic() {
		return ispublic;
	}
	public void setIspublic(Boolean ispublic) {
		this.ispublic = ispublic;
	}
	
	public List<RoomClient> getCurrentusers() {
		return currentusers;
	}
	public void setCurrentusers(List<RoomClient> currentusers) {
		this.currentusers = currentusers;
	}
	
	/**
     * @hibernate.property
     *  column="numberOfPartizipants"
     *  type="long"
     */	
	public Long getNumberOfPartizipants() {
		return numberOfPartizipants;
	}
	public void setNumberOfPartizipants(Long numberOfPartizipants) {
		this.numberOfPartizipants = numberOfPartizipants;
	}
	
	
//	/**
//     * @hibernate.property
//     *  column="videopodwidth"
//     *  type="integer"
//     */		
//	public Integer getVideoPodWidth() {
//		return videoPodWidth;
//	}
//	public void setVideoPodWidth(Integer videoPodWidth) {
//		this.videoPodWidth = videoPodWidth;
//	}
//	
//	/**
//     * @hibernate.property
//     *  column="videopodheight"
//     *  type="integer"
//     */		
//	public Integer getVideoPodHeight() {
//		return videoPodHeight;
//	}
//	public void setVideoPodHeight(Integer videoPodHeight) {
//		this.videoPodHeight = videoPodHeight;
//	}	
//	
//	/**
//     * @hibernate.property
//     *  column="videopodxposition"
//     *  type="integer"
//     */			
//	public Integer getVideoPodXPosition() {
//		return videoPodXPosition;
//	}
//	public void setVideoPodXPosition(Integer videoPodXPosition) {
//		this.videoPodXPosition = videoPodXPosition;
//	}
//	
//	/**
//     * @hibernate.property
//     *  column="videopodyposition"
//     *  type="integer"
//     */			
//	public Integer getVideoPodYPosition() {
//		return videoPodYPosition;
//	}
//	public void setVideoPodYPosition(Integer videoPodYPosition) {
//		this.videoPodYPosition = videoPodYPosition;
//	}
//	
//	/**
//     * @hibernate.property
//     *  column="moderationpanelxposition"
//     *  type="integer"
//     */		
//	public Integer getModerationPanelXPosition() {
//		return moderationPanelXPosition;
//	}
//	public void setModerationPanelXPosition(Integer moderationPanelXPosition) {
//		this.moderationPanelXPosition = moderationPanelXPosition;
//	}
//	
//	/**
//     * @hibernate.property
//     *  column="showwhiteboard"
//     *  type="boolean"
//     */		
//	public Boolean getShowWhiteBoard() {
//		return showWhiteBoard;
//	}
//	public void setShowWhiteBoard(Boolean showWhiteBoard) {
//		this.showWhiteBoard = showWhiteBoard;
//	}
//	
//	/**
//     * @hibernate.property
//     *  column="whiteboardpanelxposition"
//     *  type="integer"
//     */		
//	public Integer getWhiteBoardPanelXPosition() {
//		return whiteBoardPanelXPosition;
//	}
//	public void setWhiteBoardPanelXPosition(Integer whiteBoardPanelXPosition) {
//		this.whiteBoardPanelXPosition = whiteBoardPanelXPosition;
//	}
//	
//	/**
//     * @hibernate.property
//     *  column="whiteboardpanelyposition"
//     *  type="integer"
//     */		
//	public Integer getWhiteBoardPanelYPosition() {
//		return whiteBoardPanelYPosition;
//	}
//	public void setWhiteBoardPanelYPosition(Integer whiteBoardPanelYPosition) {
//		this.whiteBoardPanelYPosition = whiteBoardPanelYPosition;
//	}
//	
//	/**
//     * @hibernate.property
//     *  column="whiteboardpanelheight"
//     *  type="integer"
//     */		
//	public Integer getWhiteBoardPanelHeight() {
//		return whiteBoardPanelHeight;
//	}
//	public void setWhiteBoardPanelHeight(Integer whiteBoardPanelHeight) {
//		this.whiteBoardPanelHeight = whiteBoardPanelHeight;
//	}
//	
//	/**
//     * @hibernate.property
//     *  column="whiteboardpanelwidth"
//     *  type="integer"
//     */		
//	public Integer getWhiteBoardPanelWidth() {
//		return whiteBoardPanelWidth;
//	}
//	public void setWhiteBoardPanelWidth(Integer whiteBoardPanelWidth) {
//		this.whiteBoardPanelWidth = whiteBoardPanelWidth;
//	}
//	
//	/**
//     * @hibernate.property
//     *  column="showfilespanel"
//     *  type="boolean"
//     */			
//	public Boolean getShowFilesPanel() {
//		return showFilesPanel;
//	}
//	public void setShowFilesPanel(Boolean showFilesPanel) {
//		this.showFilesPanel = showFilesPanel;
//	}
//	
//	/**
//     * @hibernate.property
//     *  column="filespanelxposition"
//     *  type="integer"
//     */		
//	public Integer getFilesPanelXPosition() {
//		return filesPanelXPosition;
//	}
//	public void setFilesPanelXPosition(Integer filesPanelXPosition) {
//		this.filesPanelXPosition = filesPanelXPosition;
//	}
//	
//	/**
//     * @hibernate.property
//     *  column="filespanelyposition"
//     *  type="integer"
//     */		
//	public Integer getFilesPanelYPosition() {
//		return filesPanelYPosition;
//	}
//	public void setFilesPanelYPosition(Integer filesPanelYPosition) {
//		this.filesPanelYPosition = filesPanelYPosition;
//	}
//	
//	/**
//     * @hibernate.property
//     *  column="filespanelheight"
//     *  type="integer"
//     */		
//	public Integer getFilesPanelHeight() {
//		return filesPanelHeight;
//	}
//	public void setFilesPanelHeight(Integer filesPanelHeight) {
//		this.filesPanelHeight = filesPanelHeight;
//	}
//	
//	/**
//     * @hibernate.property
//     *  column="filespanelwidth"
//     *  type="integer"
//     */	
//	public Integer getFilesPanelWidth() {
//		return filesPanelWidth;
//	}
//	public void setFilesPanelWidth(Integer filesPanelWidth) {
//		this.filesPanelWidth = filesPanelWidth;
//	}
//	
	/**
     * @hibernate.property
     *  column="appointMent"
     *  type="boolean"
     */	
	public Boolean getAppointment() {
		return appointment;
	}
	public void setAppointment(Boolean appointment) {
		this.appointment = appointment;
	}
	
	/**
     * @hibernate.property
     *  column="isdemoroom"
     *  type="boolean"
     */	
	public Boolean getIsDemoRoom() {
		return isDemoRoom;
	}
	public void setIsDemoRoom(Boolean isDemoRoom) {
		this.isDemoRoom = isDemoRoom;
	}
	
	/**
     * @hibernate.property
     *  column="demo_time"
     *  type="integer"
     */
	public Integer getDemoTime() {
		return demoTime;
	}
	public void setDemoTime(Integer demoTime) {
		this.demoTime = demoTime;
	}
	
	/**
     * @hibernate.property
     *  column="ismoderatedroom"
     *  type="boolean"
     */	
	public Boolean getIsModeratedRoom() {
		return isModeratedRoom;
	}
	public void setIsModeratedRoom(Boolean isModeratedRoom) {
		this.isModeratedRoom = isModeratedRoom;
	}

    /**
 	 * @hibernate.property
     *  column="externalRoomId"
     *  type="long"
     */
    public Long getExternalRoomId() {
            return externalRoomId;
    }
    public void setExternalRoomId(Long externalRoomId) {
            this.externalRoomId = externalRoomId;
    }

    /**
     * @hibernate.property
     *  column="externalRoomType"
     *  type="string"
     */
    public String getExternalRoomType() {
            return externalRoomType;
    }
    public void setExternalRoomType(String externalRoomType) {
            this.externalRoomType = externalRoomType;
    }
	
	/**
     * @hibernate.property
     *  column="allow_user_questions"
     *  type="boolean"
     */    
	public Boolean getAllowUserQuestions() {
		return allowUserQuestions;
	}
	public void setAllowUserQuestions(Boolean allowUserQuestions) {
		this.allowUserQuestions = allowUserQuestions;
	}
	
	/**
     * @hibernate.property
     *  column="sip_number"
     *  type="string"
     */ 	
	public String getSipNumber() {
		return sipNumber;
	}
	public void setSipNumber(String sipNumber) {
		this.sipNumber = sipNumber;
	}
	
	/**
     * @hibernate.property
     *  column="conference_pin"
     *  type="string"
     */ 	
	public String getConferencePin() {
		return conferencePin;
	}
	public void setConferencePin(String conferencePin) {
		this.conferencePin = conferencePin;
	}
	
	/**
     * @hibernate.property
     *  column="is_audio_only"
     *  type="boolean"
     */
	public Boolean getIsAudioOnly() {
		return isAudioOnly;
	}
	public void setIsAudioOnly(Boolean isAudioOnly) {
		this.isAudioOnly = isAudioOnly;
	}
	
	/**
     * @hibernate.property
     *  column="is_closed"
     *  type="boolean"
     */
	public Boolean getIsClosed() {
		return isClosed;
	}
	public void setIsClosed(Boolean isClosed) {
		this.isClosed = isClosed;
	}
	
	/**
     * @hibernate.property
     *  column="redirect_url"
     *  type="string"
     */	
	public String getRedirectURL() {
		return redirectURL;
	}
	public void setRedirectURL(String redirectURL) {
		this.redirectURL = redirectURL;
	}
	
	/**
     * @hibernate.property
     *  column="owner_id"
     *  type="long"
     */
	public Long getOwnerId() {
		return ownerId;
	}
	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}
	
	/**
     * @hibernate.property
     *  column="wait_for_recording"
     *  type="boolean"
     */
	public Boolean getWaitForRecording() {
		return waitForRecording;
	}
	public void setWaitForRecording(Boolean waitForRecording) {
		this.waitForRecording = waitForRecording;
	}
	
	/**
     * @hibernate.property
     *  column="allow_recording"
     *  type="boolean"
     */
	public Boolean getAllowRecording() {
		return allowRecording;
	}
	public void setAllowRecording(Boolean allowRecording) {
		this.allowRecording = allowRecording;
	}
	
	/**
     * @hibernate.property
     *  column="hide_top_bar"
     *  type="boolean"
     */
	public Boolean getHideTopBar() {
		return hideTopBar;
	}
	public void setHideTopBar(Boolean hideTopBar) {
		this.hideTopBar = hideTopBar;
	}
	
    
}
