package org.openmeetings.app.hibernate.beans.recording;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;


import org.openmeetings.app.hibernate.beans.rooms.Rooms;
import org.openmeetings.app.hibernate.beans.user.Users;

@Entity
@Table(name = "recording")
public class Recording implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5069803825408408918L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	
	@Column(name="recording_id")
	private Long recording_id;
	@Column(name="name")
	private String name;
	@Column(name="duration")
	private Long duration;	
	@Lob
	@Column(name="xmlString")
	private String xmlString;
	@Column(name="comment_field")
	private String comment;
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="rooms")
	private Rooms rooms;
	@Column(name="starttime")
	private Date starttime;
	@Column(name="updatetime")
	private Date updatetime;
	@Column(name="deleted")
	private String deleted;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="roomrecordingId")
	private RoomRecording roomRecording;
	
	@Transient
	private String starttimeAsString;
	//this ID is not mapped as it can be null (Invited Users)

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="recordedby")
	private Users recordedby = null;
	
	@Column(name="whiteboardconverted")
	private Boolean whiteBoardConverted;

	//set through Logic not through Hibernate
	@Transient
	private RecordingConversionJob recordingConversionJob;
	
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
    
    public Long getRecording_id() {
		return recording_id;
	}
	public void setRecording_id(Long recording_id) {
		this.recording_id = recording_id;
	}
    
	public Rooms getRooms() {
		return rooms;
	}
	public void setRooms(Rooms rooms) {
		this.rooms = rooms;
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
	
	public Long getDuration() {
		return duration;
	}
	public void setDuration(Long duration) {
		this.duration = duration;
	}
	
	public String getXmlString() {
		return xmlString;
	}
	public void setXmlString(String xmlString) {
		this.xmlString = xmlString;
	}
	
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
	
	public Users getRecordedby() {
		return recordedby;
	}
	public void setRecordedby(Users recordedby) {
		this.recordedby = recordedby;
	}

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
