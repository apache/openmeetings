package org.openmeetings.test.dao;

import junit.framework.Test;
import junit.framework.TestSuite;

public class DaoTestSuite {
	public static Test suite() {
		TestSuite suite = new TestSuite("Dao test suite");
		suite.addTestSuite(AppointmentCategoryDaoImplTest.class);
		suite.addTestSuite(AppointmentDaoImplTest.class);
		suite.addTestSuite(AppointmentReminderTypDaoImplTest.class);
		suite.addTestSuite(FileExplorerItemDaoImplTest.class);
		suite.addTestSuite(MeetingMemberDaoImplTest.class);
		suite.addTestSuite(OmTimeZoneDaoImplTest.class);
		suite.addTestSuite(RoomModeratorsDaoImplTest.class);
		suite.addTestSuite(UserContactsDaoImplTest.class);
		suite.addTestSuite(UsersDaoImplTest.class);
        suite.addTestSuite(OpenmeetingsDaoTest.class);
		return suite;
	}
}
