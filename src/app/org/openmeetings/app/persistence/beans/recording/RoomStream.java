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
@Table(name = "recording_roomstream")
public class RoomStream implements Serializable {
	
	private static final long serialVersionUID = -5168450841189553968L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="roomstream_id")
	private Long roomStreamId;
	
	@Column(name="streamname")
	private String streamName;
	@Column(name="streamstart")
	private Boolean streamstart;
	@Column(name="avset")
	private Boolean avset;
	@Column(name="remoteaddress")
	private String remoteAdress;
	@Column(name="startdate")
	private Date startdate;
	@Column(name="starttime")
	private Long starttime;
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="rcl", insertable=true, updatable=true)
	private RoomClient rcl;
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="roomrecording_id", insertable=true, updatable=true)
	private RoomRecording roomRecording;
	
	public Long getRoomStreamId() {
		return roomStreamId;
	}
	public void setRoomStreamId(Long roomStreamId) {
		this.roomStreamId = roomStreamId;
	}
	
	public String getStreamName() {
		return streamName;
	}
	public void setStreamName(String streamName) {
		this.streamName = streamName;
	}
	
	public Boolean getStreamstart() {
		return streamstart;
	}
	public void setStreamstart(Boolean streamstart) {
		this.streamstart = streamstart;
	}
	
	public Boolean getAvset() {
		return avset;
	}
	public void setAvset(Boolean avset) {
		this.avset = avset;
	}

	public String getRemoteAdress() {
		return remoteAdress;
	}
	public void setRemoteAdress(String remoteAdress) {
		this.remoteAdress = remoteAdress;
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
	
	public RoomRecording getRoomRecording() {
		return roomRecording;
	}
	public void setRoomRecording(RoomRecording roomRecording) {
		this.roomRecording = roomRecording;
	}
	
}
