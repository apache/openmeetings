package org.openmeetings.app.hibernate.beans.recording;

/**
 * 
 * @hibernate.class table="recording_whiteboardevent"
 *
 */
public class WhiteBoardEvent {

	private Long whiteBoardEventId = null;
	private Long starttime;
	
	//This Object is made to a XML-Object using XStream to keep the
	//flexibility, otherwise a change in the Whiteboard 
	//Object (for example a new Font-Color) will need a change in the 
	//database scheme and or course big effort in maintaining the Recording
	private String action;
	private RoomRecording roomRecording;
	
	//this is only Filled if send to client
	private Object actionObj;
	
	/**
     * 
     * @hibernate.id
     *  column="recording_whiteboardevent_id"
     *  generator-class="increment"
     */
	public Long getWhiteBoardEventId() {
		return whiteBoardEventId;
	}
	public void setWhiteBoardEventId(Long whiteBoardEventId) {
		this.whiteBoardEventId = whiteBoardEventId;
	}
	
	/**
     * @hibernate.property
     *  column="starttime"
     *  type="long"
     */
	public Long getStarttime() {
		return starttime;
	}
	public void setStarttime(Long l) {
		this.starttime = l;
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
	
	public Object getActionObj() {
		return actionObj;
	}
	public void setActionObj(Object actionObj) {
		this.actionObj = actionObj;
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
