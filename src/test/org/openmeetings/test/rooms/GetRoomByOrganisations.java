package org.openmeetings.test.rooms;

import junit.framework.TestCase;
import java.util.List;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.openmeetings.app.data.conference.Roommanagement;
import org.openmeetings.app.hibernate.beans.rooms.*;

public class GetRoomByOrganisations extends TestCase {
	
	private static final Logger log = Logger.getLogger(GetRoomByOrganisations.class);
	
	public GetRoomByOrganisations(String testname){
		super(testname);
	}
	
	public void testAddOrgRoom(){
		
		List rooms = Roommanagement.getInstance().getRoomsOrganisationByOrganisationId(3,1);
		
		for (Iterator it = rooms.iterator(); it.hasNext();){
			
			Rooms_Organisation rOrg = (Rooms_Organisation) it.next();
			log.error(rOrg.getRoom().getName());
			
		}
		
	}

}
