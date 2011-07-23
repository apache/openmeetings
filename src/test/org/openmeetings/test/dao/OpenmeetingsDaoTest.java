package org.openmeetings.test.dao;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.openmeetings.app.data.basic.AuthLevelmanagement;
import org.openmeetings.app.data.basic.Configurationmanagement;
import org.openmeetings.app.data.basic.Sessionmanagement;
import org.openmeetings.app.data.conference.Roommanagement;
import org.openmeetings.app.data.conference.dao.RoomModeratorsDaoImpl;
import org.openmeetings.app.data.user.Organisationmanagement;
import org.openmeetings.app.data.user.Usermanagement;
import org.openmeetings.app.persistence.beans.basic.Configuration;
import org.openmeetings.app.persistence.beans.rooms.RoomModerators;
import org.openmeetings.app.persistence.beans.rooms.Rooms;
import org.openmeetings.app.persistence.beans.user.Users;
import org.openmeetings.test.dao.base.AbstractTestCase;
import org.openmeetings.test.dao.base.TestUtils;

public class OpenmeetingsDaoTest extends AbstractTestCase {

	public OpenmeetingsDaoTest(String name){
		super(name);
	}
	
	final public void testRegistrationUser() throws Exception {
	
	Random rd = new Random();
	Users user = TestUtils.createUser(rd.nextInt());

		Configuration conf = Configurationmanagement.getInstance().getConfKey(3L, "default.timezone");
		String jNameTimeZone = "";
		if (conf != null) {
			jNameTimeZone = conf.getConf_value();
		}

		Long user_id = Usermanagement.getInstance().registerUser(
    			"username", user.getPassword(), 
    			user.getLastname(), user.getLastname(), user.getLastname() + "@mail.com",
    			new Date(), "", "", 
    			"", "", 
    			0, 
    			"Novosibirsk", 
    			user.getLanguage_id(),	//language_id 
    			"",
    			"",
    			true, //generate SIP Data if the config is enabled
    			jNameTimeZone);

		assertTrue("New user cann't registred", user_id > 0);

		user = Usermanagement.getInstance().getUserById(user_id);
		assertNotNull("Can get user by ID", user);

		// activate the User
		user.setStatus(1);
		user.setUpdatetime(new Date());

		Usermanagement.getInstance().updateUser(user);
			
	}
	
	final public void testInitRegistrstionUser() throws Exception {
		Random rnd = new java.util.Random();
		int key = rnd.nextInt(); 
		String userpass = "pass" + key;
		String userlogin = "login" + key;
		String email = "email" + key;
		Configuration conf = Configurationmanagement.getInstance().getConfKey(3L, "default.timezone");
		String jNameTimeZone = "";
		if (conf != null) {
			jNameTimeZone = conf.getConf_value();
		}
		Long user_id = Usermanagement.getInstance().registerUserInit(
				new Long(3), 3, 1, 1, userlogin, userpass, "lastname",
				"firstname", email, new java.util.Date(), "street", "no",
				"fax", "zip", 1, "town", 0, false, null, "phone", "", false,
				"","","", false, jNameTimeZone, false,
				"",
				"",
				false,
				true);

		
		assertTrue("cann't register new user", user_id > 0);
		
		//Add default group
		Long organisation_id = Organisationmanagement.getInstance()
				.addOrganisation("", user_id);

		//Add user to default group
		Organisationmanagement.getInstance().addUserToOrganisation(
				user_id, organisation_id, null, "");
		
		
	}

	final public void testRoomModeratorsDaoImpl() throws Exception {

		Long userId = 1L;
		Users user = Usermanagement.getInstance().getUserById(userId);
		assertNotNull("Cann't get default user", user);
		
		List<Rooms> rooms = Roommanagement.getInstance().getPublicRooms(user.getLevel_id());
		assertNotNull("Cann't get public rooms fo default user", rooms);

		Rooms room = null;
		for (Iterator<Rooms> iter = rooms.iterator(); iter.hasNext();){
    		room = iter.next();
    		break;
    	}
		assertNotNull("Cann't get room for default user", room);
		
		Long rmId = RoomModeratorsDaoImpl.getInstance().addRoomModeratorByUserId(user, true, room.getRooms_id());
		assertNotNull("Cann't add room moderator", rmId);

    	Long rooms_id =  Roommanagement.getInstance().addExternalRoom(
    			"ExternalRoom",
    			room.getRooms_id(), "comment", 10L, true,
				null, false, false, 0, false,
				null, null, null, 
				false, //allowUserQuestions
				false, //isAudioOnly
				false, //isClosed
				room.getRedirectURL(),
				false, true, false);
		assertNotNull("Cann't add external room ", rooms_id);
		
		RoomModerators rm = RoomModeratorsDaoImpl.getInstance().getRoomModeratorById(rmId); 
		assertNotNull("Cann't get room moderator", rm);
		
		RoomModeratorsDaoImpl.getInstance().removeRoomModeratorByUserId(rmId);
	}
}
