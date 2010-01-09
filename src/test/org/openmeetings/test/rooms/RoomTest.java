package org.openmeetings.test.rooms;

import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;

import junit.framework.TestCase;

import org.openmeetings.app.data.conference.Roommanagement;

public class RoomTest extends TestCase {
	
	private static final Logger log = Logger.getLogger(RoomTest.class);
	
	public RoomTest(String testname){
		super(testname);
	}
	
	public void testRoomTest(){
		//Public Rooms
		long room1 = Roommanagement.getInstance().addRoom(3, "room1", 1, "", new Long(4), true, null,
				false, false, null, false, null, true);
		log.error("room1: "+room1);
		long room2 = Roommanagement.getInstance().addRoom(3, "room1", 2, "", new Long(4), true, null,
				true, false, null, false, null, true);
		log.error("room2: "+room2);
		
	}

}
