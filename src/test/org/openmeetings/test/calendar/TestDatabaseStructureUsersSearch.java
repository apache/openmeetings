package org.openmeetings.test.calendar;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.openmeetings.app.data.beans.basic.SearchResult;
import org.openmeetings.app.data.user.Usermanagement;
import org.openmeetings.test.AbstractOpenmeetingsSpringTest;
import org.springframework.beans.factory.annotation.Autowired;


public class TestDatabaseStructureUsersSearch extends AbstractOpenmeetingsSpringTest {
    @Autowired
    private Usermanagement userManagement;

	private static final Logger log = Logger.getLogger(TestDatabaseStructureUsersSearch.class);

	@Test
	public void testAddingGroup(){

		try {
			
			 SearchResult users = userManagement.getAllUserByRange("first", 0, 10, "orderby", true);
			 log.debug("[result]" + users.getResult().size());
			 log.debug("[records]"+ users.getRecords());
			 
			 //GroupMemberDaoImpl.getInstance().addGroupMember("Ad", "dir", Calendar.getInstance().getTime(), "1", "2", "interpol155", 1L, 1L);	
			//GroupMemberDaoImpl.getInstance().addGroupMember(firstname, lastname, age, memberStatus, appointmentStatus, password, adresses_id, appointmentId);
			
			//GroupMemberDaoImpl.getInstance().getGroupMemberById(1L);
			//GroupMemberDaoImpl.getInstance().deleteGroupMember(2L);
			//GroupMemberDaoImpl.getInstance().updateGroupMember(1L, "Eugen", "Schwert", Calendar.getInstance().getTime(), "1", "2", "interpol155", 1L, 1L);
		} catch (Exception err) {

			log.error("[testAddingGroup]",err);

		}

		

		

	}



}

