package org.openmeetings.app.conference.whiteboard;

import java.util.HashMap;
import java.util.Map;

public class WhiteboardObjectList {

	private Long room_id;
	private Map<Long,WhiteboardObject> whiteboardObjects = new HashMap<Long,WhiteboardObject>();
	
	public Long getRoom_id() {
		return room_id;
	}
	public void setRoom_id(Long roomId) {
		room_id = roomId;
	}
	public Map<Long, WhiteboardObject> getWhiteboardObjects() {
		return whiteboardObjects;
	}
	public void setWhiteboardObjects(Map<Long, WhiteboardObject> whiteboardObjects) {
		this.whiteboardObjects = whiteboardObjects;
	}
	
}
