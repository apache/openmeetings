package org.openmeetings.test.calendar;


import org.apache.log4j.Logger;
import org.junit.Test;
import org.openmeetings.app.data.calendar.daos.MeetingMemberDaoImpl;
import org.openmeetings.test.AbstractOpenmeetingsSpringTest;
import org.springframework.beans.factory.annotation.Autowired;


public class TestDatabaseStructureMeetingMember extends AbstractOpenmeetingsSpringTest {

	private static final Logger log = Logger.getLogger(TestDatabaseStructureMeetingMember.class);
	@Autowired
	private MeetingMemberDaoImpl meetingMemberDao;

	@Test
	public void testAddingGroup(){

		try {
			
			
			//MeetingMemberDaoImpl.getInstance().addMeetingMember("Adddd", "dir", "1", "2", 1L, 1L, "test");	
			//MeetingMemberDaoImpl.getInstance().addMeetingMember(firstname, lastname, memberStatus, appointmentStatus, appointmentId, userid, email)
			
			
			//MeetingMemberDaoImpl.getInstance().getMeetingMemberById(1L);
			//MeetingMemberDaoImpl.getInstance().deleteMeetingMember(2L);
			meetingMemberDao.updateMeetingMember(1l,"bbbbbb", "dir", "1", "2", 1L, 1L, "test"); 
		} catch (Exception err) {

			log.error("[testAddingMeeting]",err);

		}

		

		

	}



}

