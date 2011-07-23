package org.openmeetings.app.persistence.beans.recording;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

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


import org.openmeetings.app.persistence.beans.rooms.Rooms;

@Entity
@Table(name = "roomrecording")
public class RoomRecording implements Serializable {

	private static final long serialVersionUID = 1528422565551476163L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	
	@Column(name="roomrecording_id")
	private Long roomrecordingId;
	@Column(name="conferencetype")
	private String conferenceType;
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="room_setup")
	private Rooms room_setup;
	@Column(name="roomrecordingstablestring")
	private String roomRecordingsTableString;
	@Column(name="comment_field")
	private String comment;
	@Transient
	private Object initwhiteboardvars;
	@Lob
	@Column(name="initwhiteboardvars_in_xml")
	private String initwhiteboardvarsInXml;
	@Column(name="recordingname")
	private String recordingName;
	@Column(name="starttime")
	private Date starttime;
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="startedby_roomclient_id", insertable=true, updatable=true)
	private RoomClient startedby;
	@Transient
	private List<RecordingClient> roomClients;
	@Transient
	private List<RoomStream> roomStreams;
	@Transient
	private List<WhiteBoardEvent> whiteboard;
	@Transient
	private List<ChatvaluesEvent> chatvalues;
	@Column(name="endtime")
	private Date endtime;
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="enduser_roomclient_id", insertable=true, updatable=true)
	private RoomClient enduser;
	@Column(name="recordname")
	private String recordname;
	
	public Long getRoomrecordingId() {
		return roomrecordingId;
	}
	public void setRoomrecordingId(Long roomrecordingId) {
		this.roomrecordingId = roomrecordingId;
	}
	
	public String getConferenceType() {
		return conferenceType;
	}
	public void setConferenceType(String conferenceType) {
		this.conferenceType = conferenceType;
	}
	
	public Rooms getRoom_setup() {
		return room_setup;
	}
	public void setRoom_setup(Rooms room_setup) {
		this.room_setup = room_setup;
	}
	
	public String getRoomRecordingsTableString() {
		return roomRecordingsTableString;
	}
	public void setRoomRecordingsTableString(String roomRecordingsTableString) {
		this.roomRecordingsTableString = roomRecordingsTableString;
	}
	
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public Object getInitwhiteboardvars() {
		return initwhiteboardvars;
	}
	public void setInitwhiteboardvars(Object initwhiteboardvars) {
		this.initwhiteboardvars = initwhiteboardvars;
	}
	
	public String getInitwhiteboardvarsInXml() {
		return initwhiteboardvarsInXml;
	}
	public void setInitwhiteboardvarsInXml(String initwhiteboardvarsInXml) {
		this.initwhiteboardvarsInXml = initwhiteboardvarsInXml;
	}
	
	public String getRecordingName() {
		return recordingName;
	}
	public void setRecordingName(String recordingName) {
		this.recordingName = recordingName;
	}
	
	public Date getStarttime() {
		return starttime;
	}
	public void setStarttime(Date starttime) {
		this.starttime = starttime;
	}
	
	public RoomClient getStartedby() {
		return startedby;
	}
	public void setStartedby(RoomClient startedby) {
		this.startedby = startedby;
	}

	public List<RecordingClient> getRoomClients() {
		return roomClients;
	}
	public void setRoomClients(List<RecordingClient> roomClients) {
		this.roomClients = roomClients;
	}
	
	public List<RoomStream> getRoomStreams() {
		return roomStreams;
	}
	public void setRoomStreams(List<RoomStream> roomStreams) {
		this.roomStreams = roomStreams;
	}
	
	public List<WhiteBoardEvent> getWhiteboard() {
		return whiteboard;
	}
	public void setWhiteboard(List<WhiteBoardEvent> whiteboard) {
		this.whiteboard = whiteboard;
	}
	
	public List<ChatvaluesEvent> getChatvalues() {
		return chatvalues;
	}
	public void setChatvalues(List<ChatvaluesEvent> chatvalues) {
		this.chatvalues = chatvalues;
	}
	
	public Date getEndtime() {
		return endtime;
	}
	public void setEndtime(Date endtime) {
		this.endtime = endtime;
	}
	
	public RoomClient getEnduser() {
		return enduser;
	}
	public void setEnduser(RoomClient enduser) {
		this.enduser = enduser;
	}
	
	public String getRecordname() {
		return recordname;
	}
	public void setRecordname(String recordname) {
		this.recordname = recordname;
	}
	
	
}
