package org.openmeetings.test.calendar;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.openmeetings.app.data.user.dao.UsersDaoImpl;
import org.openmeetings.test.AbstractOpenmeetingsSpringTest;
import org.springframework.beans.factory.annotation.Autowired;

public class TestDatabaseStructureGetUserStart extends AbstractOpenmeetingsSpringTest {
	private static final Logger log = Logger.getLogger(TestDatabaseStructureGetUserStart.class);
	@Autowired
	private UsersDaoImpl usersDao;
	
	@Test
	public void testAddingGroup() {
		try {
			usersDao.getUser(new Long(1));
		} catch (Exception err) {
			log.error("[testAddingGroup]", err);
		}
	}
}
