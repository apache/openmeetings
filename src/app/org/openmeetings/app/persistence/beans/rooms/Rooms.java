package org.openmeetings.app.persistence.beans.rooms;

import org.openmeetings.app.persistence.beans.recording.RoomClient;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "rooms")
public class Rooms implements Serializable {
	
	private static final long serialVersionUID = -2860312283159251568L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	
	@Column(name="rooms_id")
	private Long rooms_id;
	@Column(name="name")
	private String name;
	@Lob //@Basic(fetch=FetchType.LAZY)
	@Column(name="comment_field")
	private String comment;
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn (name="roomtypes_id")
	private RoomTypes roomtype;
	@Column(name = "starttime")
	private Date starttime;
	@Column(name = "updatetime")
	private Date updatetime;
	@Column(name = "deleted")
	private String deleted;
	@Column(name = "ispublic")
	private Boolean ispublic;
	@Column(name = "numberOfPartizipants")
	private Long numberOfPartizipants = new Long(4);
	
	@Column(name = "appointMent")
	private Boolean appointment;

	//Vars to simulate external Rooms
	@Column(name = "externalRoomId")
	private Long externalRoomId;
	@Column(name = "externalRoomType")
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
	
	@Transient
	private List<RoomClient> currentusers;
	
	@Column(name = "isdemoroom")
	private Boolean isDemoRoom;
	@Column(name = "demo_time")
	private Integer demoTime; //In Seconds
	
	//If this is true all participants of a meeting have to wait for the moderator to come into the room
	@Column(name = "ismoderatedroom")
	private Boolean isModeratedRoom;
	
	@Column(name = "allow_user_questions")
	private Boolean allowUserQuestions;
	
	@Column(name = "sip_number")
	private String sipNumber;
	@Column(name = "conference_pin")
	private String conferencePin;
	
	@Column(name = "is_audio_only")
	private Boolean isAudioOnly;
	
	@Column(name = "is_closed")
	private Boolean isClosed;
	@Column(name = "redirect_url")
	private String redirectURL;
	
	@Column(name = "owner_id")
	private Long ownerId; //Those are the rooms from the myrooms section
	@Column(name = "wait_for_recording")
	private Boolean waitForRecording; //Show warning that user has to start recording
	@Column(name = "allow_recording")
	private Boolean allowRecording; //Show or show not the recording option in a conference room
    
	@Column(name = "hide_top_bar")
	private Boolean hideTopBar = false;
    
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
    
	public Long getRooms_id() {
		return rooms_id;
	}
	public void setRooms_id(Long rooms_id) {
		this.rooms_id = rooms_id;
	}
    
	public RoomTypes getRoomtype() {
		return roomtype;
	}
	public void setRoomtype(RoomTypes roomtype) {
		this.roomtype = roomtype;
	}
    
	public Date getStarttime() {
		return starttime;
	}
	public void setStarttime(Date starttime) {
		this.starttime = starttime;
	}
    
	public Date getUpdatetime() {
		return updatetime;
	}
	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}	
	
	public String getDeleted() {
		return deleted;
	}
	public void setDeleted(String deleted) {
		this.deleted = deleted;
	}
	
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
	
	public Long getNumberOfPartizipants() {
		return numberOfPartizipants;
	}
	public void setNumberOfPartizipants(Long numberOfPartizipants) {
		this.numberOfPartizipants = numberOfPartizipants;
	}
	
	
	public Boolean getAppointment() {
		return appointment;
	}
	public void setAppointment(Boolean appointment) {
		this.appointment = appointment;
	}
	
	public Boolean getIsDemoRoom() {
		return isDemoRoom;
	}
	public void setIsDemoRoom(Boolean isDemoRoom) {
		this.isDemoRoom = isDemoRoom;
	}
	
	public Integer getDemoTime() {
		return demoTime;
	}
	public void setDemoTime(Integer demoTime) {
		this.demoTime = demoTime;
	}
	
	public Boolean getIsModeratedRoom() {
		return isModeratedRoom;
	}
	public void setIsModeratedRoom(Boolean isModeratedRoom) {
		this.isModeratedRoom = isModeratedRoom;
	}

    public Long getExternalRoomId() {
            return externalRoomId;
    }
    public void setExternalRoomId(Long externalRoomId) {
            this.externalRoomId = externalRoomId;
    }

    public String getExternalRoomType() {
            return externalRoomType;
    }
    public void setExternalRoomType(String externalRoomType) {
            this.externalRoomType = externalRoomType;
    }
	
	public Boolean getAllowUserQuestions() {
		return allowUserQuestions;
	}
	public void setAllowUserQuestions(Boolean allowUserQuestions) {
		this.allowUserQuestions = allowUserQuestions;
	}
	
	public String getSipNumber() {
		return sipNumber;
	}
	public void setSipNumber(String sipNumber) {
		this.sipNumber = sipNumber;
	}
	
	public String getConferencePin() {
		return conferencePin;
	}
	public void setConferencePin(String conferencePin) {
		this.conferencePin = conferencePin;
	}
	
	public Boolean getIsAudioOnly() {
		return isAudioOnly;
	}
	public void setIsAudioOnly(Boolean isAudioOnly) {
		this.isAudioOnly = isAudioOnly;
	}
	
	public Boolean getIsClosed() {
		return isClosed;
	}
	public void setIsClosed(Boolean isClosed) {
		this.isClosed = isClosed;
	}
	
	public String getRedirectURL() {
		return redirectURL;
	}
	public void setRedirectURL(String redirectURL) {
		this.redirectURL = redirectURL;
	}
	
	public Long getOwnerId() {
		return ownerId;
	}
	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}
	
	public Boolean getWaitForRecording() {
		return waitForRecording;
	}
	public void setWaitForRecording(Boolean waitForRecording) {
		this.waitForRecording = waitForRecording;
	}
	
	public Boolean getAllowRecording() {
		return allowRecording;
	}
	public void setAllowRecording(Boolean allowRecording) {
		this.allowRecording = allowRecording;
	}

	public Boolean getHideTopBar() {
		return hideTopBar;
	}
	public void setHideTopBar(Boolean hideTopBar) {
		this.hideTopBar = hideTopBar;
	}
	
	
    
}
