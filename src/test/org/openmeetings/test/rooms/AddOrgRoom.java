package org.openmeetings.test.rooms;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.openmeetings.app.data.conference.Roommanagement;


public class AddOrgRoom extends TestCase {
	
	private static final Logger log = Logger.getLogger(AddOrgRoom.class);
	
	public AddOrgRoom(String testname){
		super(testname);
	}
	
	public void testAddOrgRoom(){
		
		long room = Roommanagement.getInstance().addRoom(3,"roomOrg", 1, "", new Long(4), true,null,
				false, false, null, false, null, true, false);
		Roommanagement.getInstance().addRoomToOrganisation(3,room, 1);
		
	}
	

}
