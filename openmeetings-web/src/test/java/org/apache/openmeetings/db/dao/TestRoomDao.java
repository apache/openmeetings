package org.apache.openmeetings.db.dao;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.openmeetings.AbstractJUnitDefaults;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.room.Room.RoomElement;
import org.apache.openmeetings.db.entity.user.User;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


public class TestRoomDao extends AbstractJUnitDefaults {
	@Autowired
	protected RoomDao roomDao;

	@Test
	public void testMicStatusHidden() {
		Room r = roomDao.get(1);
		assertTrue("Default interview room should have mic status hidden", r.isHidden(RoomElement.MicrophoneStatus));
		r = roomDao.get(5);
		assertTrue("Default presentation room should have mic status hidden", r.isHidden(RoomElement.MicrophoneStatus));
		r = roomDao.get(6);
		assertFalse("Default Mic room should have mic status visible", r.isHidden(RoomElement.MicrophoneStatus));

		User u = userDao.getByLogin(adminUsername, User.Type.user, null);
		assertNotNull("Admin user should exist", u);
		r = roomDao.getUserRoom(u.getId(), Room.Type.presentation, "bla");
		assertTrue("User presentation room should have mic status hidden", r.isHidden(RoomElement.MicrophoneStatus));
	}
}
