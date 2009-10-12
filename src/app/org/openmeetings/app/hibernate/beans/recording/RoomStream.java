package org.openmeetings.app.hibernate.beans.recording;

import java.util.Date;

/**
 * 
 * @hibernate.class table="recording_roomstream"
 *
 */
public class RoomStream {
	
	private Long roomStreamId;
	private String streamName;
	private Boolean streamstart;
	private Boolean avset;
	private String remoteAdress;
	private Date startdate;
	private Long starttime;
	private RoomClient rcl;
	private RoomRecording roomRecording;
	
	/**
     * 
     * @hibernate.id
     *  column="recording_roomstream_id"
     *  generator-class="increment"
     */
	public Long getRoomStreamId() {
		return roomStreamId;
	}
	public void setRoomStreamId(Long roomStreamId) {
		this.roomStreamId = roomStreamId;
	}
	
	/**
     * @hibernate.property
     *  column="streamname"
     *  type="string"
     */
	public String getStreamName() {
		return streamName;
	}
	public void setStreamName(String streamName) {
		this.streamName = streamName;
	}
	
	/**
     * @hibernate.property
     *  column="streamstart"
     *  type="boolean"
     */
	public Boolean getStreamstart() {
		return streamstart;
	}
	public void setStreamstart(Boolean streamstart) {
		this.streamstart = streamstart;
	}
	
	/**
     * @hibernate.property
     *  column="avset"
     *  type="boolean"
     */
	public Boolean getAvset() {
		return avset;
	}
	public void setAvset(Boolean avset) {
		this.avset = avset;
	}

	/**
     * @hibernate.property
     *  column="remoteaddress"
     *  type="string"
     */
	public String getRemoteAdress() {
		return remoteAdress;
	}
	public void setRemoteAdress(String remoteAdress) {
		this.remoteAdress = remoteAdress;
	}
	
	/**
     * @hibernate.property
     *  column="startdate"
     *  type="java.util.Date"
     */
	public Date getStartdate() {
		return startdate;
	}
	public void setStartdate(Date startdate) {
		this.startdate = startdate;
	}
	
	/**
     * @hibernate.property
     *  column="starttime"
     *  type="long"
     */
	public Long getStarttime() {
		return starttime;
	}
	public void setStarttime(Long starttime) {
		this.starttime = starttime;
	}
	
	/**
	 * @hibernate.many-to-one
	 * column = "rcl"
	 * class = "org.openmeetings.app.hibernate.beans.recording.RoomClient"
	 * insert="true"
	 * update="true"
	 * outer-join="true"
	 * lazy="false"
     */
	public RoomClient getRcl() {
		return rcl;
	}
	public void setRcl(RoomClient rcl) {
		this.rcl = rcl;
	}
	
	/**
	 * @hibernate.many-to-one
	 * column = "roomrecording_id"
	 * class = "org.openmeetings.app.hibernate.beans.recording.RoomRecording"
	 * insert="true"
	 * update="true"
	 * outer-join="true"
	 * lazy="no-proxy"
     */
	public RoomRecording getRoomRecording() {
		return roomRecording;
	}
	public void setRoomRecording(RoomRecording roomRecording) {
		this.roomRecording = roomRecording;
	}
	
}
