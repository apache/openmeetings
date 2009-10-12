package org.openmeetings.app.hibernate.beans.recording;

import java.util.Date;
import java.util.LinkedHashMap;

import org.openmeetings.app.hibernate.beans.rooms.Rooms;
import org.openmeetings.app.hibernate.beans.user.Users;

/**
 * 
 * @hibernate.class table="recording"
 * lazy="false"
 *
 */
public class Recording {
	
	private Long recording_id;
	private String name;
	private Long duration;	
	private String xmlString;
	private String comment;
	private Rooms rooms;
	private Date starttime;
	private Date updatetime;
	private String deleted;
	
	private RoomRecording roomRecording;
	
	private String starttimeAsString;
	//this ID is not mapped as it can be null (Invited Users)
	private Users recordedby = null;
	
	private Boolean whiteBoardConverted;

	//set through Logic not through Hibernate
	private RecordingConversionJob recordingConversionJob;
	
	/**
     * @hibernate.property
     *  column="comment"
     *  type="string"
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
     *  column="recording_id"
     *  generator-class="increment"
     */ 
    public Long getRecording_id() {
		return recording_id;
	}
	public void setRecording_id(Long recording_id) {
		this.recording_id = recording_id;
	}
    
    /**
     * @hibernate.many-to-one
     *  cascade="none"
     *  column="rooms"
     *  lazy="false"
     *  class="org.openmeetings.app.hibernate.beans.rooms.Rooms"
     *  not-null="false"
     *  outer-join="true"
     */ 
	public Rooms getRooms() {
		return rooms;
	}
	public void setRooms(Rooms rooms) {
		this.rooms = rooms;
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
     *  column="duration"
     *  type="long"
     */		
	public Long getDuration() {
		return duration;
	}
	public void setDuration(Long duration) {
		this.duration = duration;
	}
	
    /**
     * @hibernate.property
     *  column="xmlString"
     *  type="text"
     */		
	public String getXmlString() {
		return xmlString;
	}
	public void setXmlString(String xmlString) {
		this.xmlString = xmlString;
	}
	
	/**
     * @hibernate.many-to-one
     *  cascade="none"
     *  column="roomrecordingId"
     *  lazy="false"
     *  class="org.openmeetings.app.hibernate.beans.recording.RoomRecording"
     *  not-null="false"
     *  outer-join="true"
     */	
	public RoomRecording getRoomRecording() {
		return roomRecording;
	}
	public void setRoomRecording(RoomRecording roomRecording) {
		this.roomRecording = roomRecording;
	}
	
	public String getStarttimeAsString() {
		return starttimeAsString;
	}
	public void setStarttimeAsString(String starttimeAsString) {
		this.starttimeAsString = starttimeAsString;
	}
	
    /**
     * @hibernate.many-to-one
     *  cascade="none"
     *  column="recordedby"
     *  lazy="false"
     *  class="org.openmeetings.app.hibernate.beans.user.Users"
     *  not-null="false"
     *  outer-join="true"
     */		
	public Users getRecordedby() {
		return recordedby;
	}
	public void setRecordedby(Users recordedby) {
		this.recordedby = recordedby;
	}

    /**
     * @hibernate.property
     *  column="whiteboardconverted"
     *  type="boolean"
     */
	public Boolean getWhiteBoardConverted() {
		return whiteBoardConverted;
	}
	public void setWhiteBoardConverted(Boolean whiteBoardConverted) {
		this.whiteBoardConverted = whiteBoardConverted;
	}
	
	
	public RecordingConversionJob getRecordingConversionJob() {
		return recordingConversionJob;
	}
	public void setRecordingConversionJob(
			RecordingConversionJob recordingConversionJob) {
		this.recordingConversionJob = recordingConversionJob;
	}
	
	
}
