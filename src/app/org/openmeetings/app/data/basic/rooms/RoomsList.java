package org.openmeetings.app.data.basic.rooms;

import java.util.LinkedList;

import org.openmeetings.app.hibernate.beans.rooms.Rooms;

public class RoomsList {
	
	LinkedList<Rooms> roomList = null;

	public LinkedList<Rooms> getRoomList() {
		return roomList;
	}
	public void setRoomList(LinkedList<Rooms> roomList) {
		this.roomList = roomList;
	}

}
