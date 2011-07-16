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
@Table(name = "recording_chatvaluesevent")
public class ChatvaluesEvent implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3651904977310257437L;
	//see WhiteboardEvent for Documentation, Comments
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	
	@Column(name="recording_chatvaluesevent_id")
	private Long chatvaluesEventId;
	@Column(name="starttime")
	private Long starttime;
	@Lob
	@Column(name="action")
	private String action;
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="roomrecording_id", updatable=true, insertable=true)
	private RoomRecording roomRecording;
	
	//this is only Filled if send to client
	@Transient
	private Object actionObj;

	public Long getChatvaluesEventId() {
		return chatvaluesEventId;
	}
	public void setChatvaluesEventId(Long chatvaluesEventId) {
		this.chatvaluesEventId = chatvaluesEventId;
	}

	public Long getStarttime() {
		return starttime;
	}
	public void setStarttime(Long starttime) {
		this.starttime = starttime;
	}

	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}

	public RoomRecording getRoomRecording() {
		return roomRecording;
	}
	public void setRoomRecording(RoomRecording roomRecording) {
		this.roomRecording = roomRecording;
	}
	
	public Object getActionObj() {
		return actionObj;
	}
	public void setActionObj(Object actionObj) {
		this.actionObj = actionObj;
	}
	
	

}
