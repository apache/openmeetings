package org.openmeetings.app.hibernate.beans.recording;

import java.util.Date;


/**
 * 
 * @hibernate.class table="recordingclient"
 * lazy="false"
 *
 */
public class RecordingClient {
	
	private Long recordingclient_id;
	private Long roomRecordingId;
	private String remoteAdress;
	private Boolean roomenter;
	private Date startdate;
	private Long starttime;
	private RoomClient rcl;
	
	
	/**
     * 
     * @hibernate.id
     *  column="recordingclient_id"
     *  generator-class="increment"
     */
	public Long getRecordingclient_id() {
		return recordingclient_id;
	}
	public void setRecordingclient_id(Long recordingclient_id) {
		this.recordingclient_id = recordingclient_id;
	}
	
	/**
     * @hibernate.property
     *  column="roomrecording_id"
     *  type="long"
     */
	public Long getRoomRecordingId() {
		return roomRecordingId;
	}
	public void setRoomRecordingId(Long roomRecordingId) {
		this.roomRecordingId = roomRecordingId;
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
     *  column="roomenter"
     *  type="boolean"
     */	
	public Boolean getRoomenter() {
		return roomenter;
	}
	public void setRoomenter(Boolean roomenter) {
		this.roomenter = roomenter;
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
	 * column = "roomclient_id"
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
	

}
