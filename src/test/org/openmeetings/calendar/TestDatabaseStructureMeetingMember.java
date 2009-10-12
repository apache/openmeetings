package org.openmeetings.calendar;


import junit.framework.TestCase;
import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;

import org.openmeetings.app.data.calendar.daos.MeetingMemberDaoImpl;


public class TestDatabaseStructureMeetingMember extends TestCase {

	private static final Logger log = Logger.getLogger(TestDatabaseStructureMeetingMember.class);

	public TestDatabaseStructureMeetingMember(String testname){

		super(testname);

	}

	

	public void testAddingGroup(){

		try {
			
			
			//MeetingMemberDaoImpl.getInstance().addMeetingMember("Adddd", "dir", "1", "2", 1L, 1L, "test");	
			//MeetingMemberDaoImpl.getInstance().addMeetingMember(firstname, lastname, memberStatus, appointmentStatus, appointmentId, userid, email)
			
			
			//MeetingMemberDaoImpl.getInstance().getMeetingMemberById(1L);
			//MeetingMemberDaoImpl.getInstance().deleteMeetingMember(2L);
			MeetingMemberDaoImpl.getInstance().updateMeetingMember(1l,"bbbbbb", "dir", "1", "2", 1L, 1L, "test"); 
		} catch (Exception err) {

			log.error("[testAddingMeeting]",err);

		}

		

		

	}



}

