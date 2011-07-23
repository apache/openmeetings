package org.openmeetings.app.data.basic.rooms;

import org.openmeetings.app.persistence.beans.rooms.Rooms;

public class RoomsList {
	
	Rooms[] roomList = null;

	public Rooms[] getRoomList() {
		return roomList;
	}
	public void setRoomList(Rooms[] roomList) {
		this.roomList = roomList;
	}

}
