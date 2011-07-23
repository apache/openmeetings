package org.openmeetings.app.persistence.beans.recording;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;




@Entity
@Table(name = "recordingclient")
public class RecordingClient implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 9016258240163046235L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	
	@Column(name="recordingclient_id")
	private Long recordingclient_id;
	@Column(name="roomrecording_id")
	private Long roomRecordingId;
	@Column(name="remoteaddress")
	private String remoteAdress;
	@Column(name="roomenter")
	private Boolean roomenter;
	@Column(name="startdate")
	private Date startdate;
	@Column(name="starttime")
	private Long starttime;
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="roomclient_id", insertable=true, updatable=true)
	private RoomClient rcl;
	
	
	public Long getRecordingclient_id() {
		return recordingclient_id;
	}
	public void setRecordingclient_id(Long recordingclient_id) {
		this.recordingclient_id = recordingclient_id;
	}
	
	public Long getRoomRecordingId() {
		return roomRecordingId;
	}
	public void setRoomRecordingId(Long roomRecordingId) {
		this.roomRecordingId = roomRecordingId;
	}
	
	public String getRemoteAdress() {
		return remoteAdress;
	}
	public void setRemoteAdress(String remoteAdress) {
		this.remoteAdress = remoteAdress;
	}
	
	public Boolean getRoomenter() {
		return roomenter;
	}
	public void setRoomenter(Boolean roomenter) {
		this.roomenter = roomenter;
	}
	
	public Date getStartdate() {
		return startdate;
	}
	public void setStartdate(Date startdate) {
		this.startdate = startdate;
	}
	
	public Long getStarttime() {
		return starttime;
	}
	public void setStarttime(Long starttime) {
		this.starttime = starttime;
	}
	
	public RoomClient getRcl() {
		return rcl;
	}
	public void setRcl(RoomClient rcl) {
		this.rcl = rcl;
	}
	

}
