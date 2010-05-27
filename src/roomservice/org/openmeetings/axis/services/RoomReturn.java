package org.openmeetings.axis.services;

import java.util.Date;
import java.util.List;

public class RoomReturn {
	
	private Long room_id;
	private String name;
	private String creator;
	private Date created;
	private RoomUser[] roomUser;
	
	public Long getRoom_id() {
		return room_id;
	}
	public void setRoom_id(Long roomId) {
		room_id = roomId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	public RoomUser[] getRoomUser() {
		return roomUser;
	}
	public void setRoomUser(RoomUser[] roomUser) {
		this.roomUser = roomUser;
	}
	
}
