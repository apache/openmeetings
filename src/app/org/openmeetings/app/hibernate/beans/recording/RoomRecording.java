package org.openmeetings.app.hibernate.beans.recording;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.openmeetings.app.hibernate.beans.rooms.Rooms;

/**
 * 
 * @hibernate.class table="roomrecording"
 * lazy="false"
 *
 */
public class RoomRecording {

	private Long roomrecordingId;
	private String conferenceType;
	private Rooms room_setup;
	private String roomRecordingsTableString;
	private String comment;
	private Object initwhiteboardvars;
	private String initwhiteboardvarsInXml;
	private String recordingName;
	private Date starttime;
	private RoomClient startedby;
	private List<RecordingClient> roomClients;
	private List<RoomStream> roomStreams;
	private List<WhiteBoardEvent> whiteboard;
	private List<ChatvaluesEvent> chatvalues;
	private Date endtime;
	private RoomClient enduser;
	private String recordname;
	
	/**
     * 
     * @hibernate.id
     *  column="roomrecording_id"
     *  generator-class="increment"
     */
	public Long getRoomrecordingId() {
		return roomrecordingId;
	}
	public void setRoomrecordingId(Long roomrecordingId) {
		this.roomrecordingId = roomrecordingId;
	}
	
	/**
     * @hibernate.property
     *  column="conferencetype"
     *  type="string"
     */	
	public String getConferenceType() {
		return conferenceType;
	}
	public void setConferenceType(String conferenceType) {
		this.conferenceType = conferenceType;
	}
	
	/**
     * @hibernate.many-to-one
     *  cascade="none"
     *  column="room_setup"
     *  lazy="false"
     *  class="org.openmeetings.app.hibernate.beans.rooms.Rooms"
     *  not-null="false"
     *  outer-join="true"
     */
	public Rooms getRoom_setup() {
		return room_setup;
	}
	public void setRoom_setup(Rooms room_setup) {
		this.room_setup = room_setup;
	}
	
	/**
     * @hibernate.property
     *  column="roomrecordingstablestring"
     *  type="string"
     */
	public String getRoomRecordingsTableString() {
		return roomRecordingsTableString;
	}
	public void setRoomRecordingsTableString(String roomRecordingsTableString) {
		this.roomRecordingsTableString = roomRecordingsTableString;
	}
	
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
	
	public Object getInitwhiteboardvars() {
		return initwhiteboardvars;
	}
	public void setInitwhiteboardvars(Object initwhiteboardvars) {
		this.initwhiteboardvars = initwhiteboardvars;
	}
	
	/**
     * @hibernate.property
     *  column="initwhiteboardvars_in_xml"
     *  type="text"
     */
	public String getInitwhiteboardvarsInXml() {
		return initwhiteboardvarsInXml;
	}
	public void setInitwhiteboardvarsInXml(String initwhiteboardvarsInXml) {
		this.initwhiteboardvarsInXml = initwhiteboardvarsInXml;
	}
	
	/**
     * @hibernate.property
     *  column="recordingname"
     *  type="string"
     */
	public String getRecordingName() {
		return recordingName;
	}
	public void setRecordingName(String recordingName) {
		this.recordingName = recordingName;
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
	 * @hibernate.many-to-one
	 * column = "startedby_roomclient_id"
	 * class = "org.openmeetings.app.hibernate.beans.recording.RoomClient"
	 * insert="true"
	 * update="true"
	 * outer-join="true"
	 * lazy="false"
     */
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
	
	/**
     * @hibernate.property
     *  column="endtime"
     *  type="java.util.Date"
     */
	public Date getEndtime() {
		return endtime;
	}
	public void setEndtime(Date endtime) {
		this.endtime = endtime;
	}
	
    /**
	 * @hibernate.many-to-one
	 * column = "enduser_roomclient_id"
	 * class = "org.openmeetings.app.hibernate.beans.recording.RoomClient"
	 * insert="true"
	 * update="true"
	 * outer-join="true"
	 * lazy="false"
     */	
	public RoomClient getEnduser() {
		return enduser;
	}
	public void setEnduser(RoomClient enduser) {
		this.enduser = enduser;
	}
	
	/**
     * @hibernate.property
     *  column="recordname"
     *  type="string"
     */
	public String getRecordname() {
		return recordname;
	}
	public void setRecordname(String recordname) {
		this.recordname = recordname;
	}
	
	
}
