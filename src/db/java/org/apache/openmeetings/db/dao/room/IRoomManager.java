package org.apache.openmeetings.db.dao.room;

import org.apache.openmeetings.db.entity.room.Room;

//FIXME HACK to bypass cross project compilation
public interface IRoomManager {
	Room  getRoomByOwnerAndTypeId(Long ownerId, Long roomtypesId, String roomName);
}
