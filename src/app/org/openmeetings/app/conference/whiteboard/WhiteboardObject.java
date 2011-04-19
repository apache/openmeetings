package org.openmeetings.app.conference.whiteboard;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class WhiteboardObject {

	private Long whiteBoardId;
	private Integer x = 0;
	private Integer y = 0;
	private Integer zoom = 100;
    private Boolean fullFit = true;
	private Map<String,List> roomItems = new HashMap<String,List>();
	private Date created = new Date();
	
	public Long getWhiteBoardId() {
		return whiteBoardId;
	}
	public void setWhiteBoardId(Long whiteBoardId) {
		this.whiteBoardId = whiteBoardId;
	}
	public Integer getX() {
		return x;
	}
	public void setX(Integer x) {
		this.x = x;
	}
	public Integer getY() {
		return y;
	}
	public void setY(Integer y) {
		this.y = y;
	}
	public Map<String,List> getRoomItems() {
		return roomItems;
	}
	public void setRoomItems(Map<String,List> roomItems) {
		this.roomItems = roomItems;
	}
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	public Integer getZoom() {
        return zoom;
	}
	public void setZoom(Integer zoom) {
	        this.zoom = zoom;
	}
	public Boolean getFullFit() {
	        return fullFit;
	}
	public void setFullFit(Boolean fullFit) {
	        this.fullFit = fullFit;
	}

	
}
