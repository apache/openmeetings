package org.openmeetings.app.hibernate.beans.recording;

/**
 * 
 * @hibernate.class table="recording_chatvaluesevent"
 *
 */
public class ChatvaluesEvent {
	
	//see WhiteboardEvent for Documentation, Comments
	private Long chatvaluesEventId;
	private Long starttime;
	private String action;
	private RoomRecording roomRecording;
	
	//this is only Filled if send to client
	private Object actionObj;

	/**
     * 
     * @hibernate.id
     *  column="recording_chatvaluesevent_id"
     *  generator-class="increment"
     */
	public Long getChatvaluesEventId() {
		return chatvaluesEventId;
	}
	public void setChatvaluesEventId(Long chatvaluesEventId) {
		this.chatvaluesEventId = chatvaluesEventId;
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
     * @hibernate.property
     *  column="action"
     *  type="text"
     */
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
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
	
	public Object getActionObj() {
		return actionObj;
	}
	public void setActionObj(Object actionObj) {
		this.actionObj = actionObj;
	}
	
	

}
