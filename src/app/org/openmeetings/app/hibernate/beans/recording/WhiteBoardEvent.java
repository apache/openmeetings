package org.openmeetings.app.hibernate.beans.recording;

import java.io.Serializable;

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



@Entity
@Table(name = "recording_whiteboardevent")
public class WhiteBoardEvent implements Serializable {

	private static final long serialVersionUID = -1745738413294075264L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	
	@Column(name="recording_whiteboardevent_id")
	private Long whiteBoardEventId = null;
	@Column(name="starttime")
	private Long starttime;
	
	//This Object is made to a XML-Object using XStream to keep the
	//flexibility, otherwise a change in the Whiteboard 
	//Object (for example a new Font-Color) will need a change in the 
	//database scheme and or course big effort in maintaining the Recording
	@Lob
	@Column(name="action")
	private String action;
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="roomrecording_id", insertable=true, updatable=true)
	private RoomRecording roomRecording;
	
	//this is only Filled if send to client
	@Transient
	private Object actionObj;
	
	public Long getWhiteBoardEventId() {
		return whiteBoardEventId;
	}
	public void setWhiteBoardEventId(Long whiteBoardEventId) {
		this.whiteBoardEventId = whiteBoardEventId;
	}
	
	public Long getStarttime() {
		return starttime;
	}
	public void setStarttime(Long l) {
		this.starttime = l;
	}

	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	
	public Object getActionObj() {
		return actionObj;
	}
	public void setActionObj(Object actionObj) {
		this.actionObj = actionObj;
	}

	public RoomRecording getRoomRecording() {
		return roomRecording;
	}
	public void setRoomRecording(RoomRecording roomRecording) {
		this.roomRecording = roomRecording;
	}
	
}
