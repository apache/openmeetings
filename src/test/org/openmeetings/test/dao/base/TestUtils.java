package org.openmeetings.test.dao.base;

import java.util.Calendar;
import java.util.Date;

import org.openmeetings.app.data.basic.Configurationmanagement;
import org.openmeetings.app.data.basic.ErrorManagement;
import org.openmeetings.app.data.basic.Navimanagement;
import org.openmeetings.app.data.calendar.daos.AppointmentDaoImpl;
import org.openmeetings.app.data.conference.Roommanagement;
import org.openmeetings.app.data.user.Salutationmanagement;
import org.openmeetings.app.data.user.Usermanagement;
import org.openmeetings.app.data.user.dao.UsersDaoImpl;
import org.openmeetings.app.hibernate.beans.calendar.Appointment;
import org.openmeetings.app.hibernate.beans.user.Users;

import junit.framework.Assert;

public class TestUtils extends Assert {

	public static Appointment createAppointment()
	{
		AppointmentDaoImpl appDaoImpl = AppointmentDaoImpl.getInstance();
		assertNotNull("Cann't access to appointment dao implimentation", appDaoImpl);
		
		// add new appointment
		Appointment ap = new Appointment();
		
		ap.setAppointmentName("appointmentName");
		ap.setAppointmentLocation("appointmentLocation");
		
		Date appointmentstart = new Date();
		Date appointmentend = new Date();
		appointmentend.setTime(appointmentstart.getTime() + 3600);
		
		ap.setAppointmentStarttime(appointmentstart);
	 	ap.setAppointmentEndtime(appointmentend);
		ap.setAppointmentDescription("appointmentDescription");
		ap.setStarttime(new Date());
		ap.setDeleted("false");
		ap.setIsDaily(false);
		ap.setIsWeekly(false);
		ap.setIsMonthly(false);
		ap.setIsYearly(false);
		ap.setIsPasswordProtected(false);
	
		ap.setUserId(UsersDaoImpl.getInstance().getUser(1L));
		ap.setIsConnectedEvent(false);
		Long id = appDaoImpl.addAppointmentObj(ap);
		assertNotNull("Cann't add appointment", id);
		return ap;
	}
	
	public static Users createUser(int rnd) throws Exception{
		Users users = new Users();
		// add user
		users.setFirstname("firstname" + rnd);
		users.setLastname("lastname" + rnd);
		users.setLogin("login");
		users.setPassword("pass" + rnd);
		users.setLanguage_id(1L);
		Long user_id = Usermanagement.getInstance().addUser(users);
		assertTrue("Cann't add user", user_id > 0);
		users = Usermanagement.getInstance().getUserByIdAndDeleted(user_id);
		assertNotNull("User should not be null", users);
		return users;
	}
	
	public static void loadSalutations() {

		Salutationmanagement.getInstance().addUserSalutation("Mr", 261);
		Salutationmanagement.getInstance().addUserSalutation("Ms", 262);
		Salutationmanagement.getInstance().addUserSalutation("Mrs", 841);
		Salutationmanagement.getInstance().addUserSalutation("Dr", 842);

	}
	
	public static void loadConfigurations() {
		
		// ***************************************
		// ***************************************
		// Timezone settings
		// ***************************************
		Calendar cal = Calendar.getInstance();
		int offset = cal.get(Calendar.ZONE_OFFSET);
		
		offset = offset/1000/60/60;
		
		String timeZoneJavaFormat = "Etc/GMT";
		
		if (offset > 0) {
			timeZoneJavaFormat += "+"+offset;
		} else {
			timeZoneJavaFormat += "-"+offset;
		}
		
		Configurationmanagement.getInstance().addConfByKey(3, "default.timezone",
				timeZoneJavaFormat, null, "This is the default timezone if nothing is specified");
		
		// ***************************************
		// ***************************************
		// additional settings
		// ***************************************
		
		Configurationmanagement.getInstance().addConfByKey(3, "show.facebook.login",
				""+0, null, "Show Facebook Login");
		
		Configurationmanagement.getInstance().addConfByKey(3, "default.quality.screensharing",
				"0", null, "Default selection in ScreenSharing Quality");
		
		Configurationmanagement.getInstance().addConfByKey(3, "default.dashboard.tab",
				"0", null, "Default selection in Dashboard tabs as tab-index-id");
		
		Configurationmanagement.getInstance().addConfByKey(3, "dashboard.show.myrooms",
				"1", null, "Show My Rooms Tab");
		
		Configurationmanagement.getInstance().addConfByKey(3, "dashboard.show.chat",
				"1", null, "Show Chat Tab");
		
		Configurationmanagement.getInstance().addConfByKey(3, "dashboard.show.rssfeed",
				"0", null, "Show RSS Tab");
		
	}
	
	public static void loadMainMenu() {

		Usermanagement.getInstance().addUserLevel("User", 1);
		Usermanagement.getInstance().addUserLevel("Moderator", 2);
		Usermanagement.getInstance().addUserLevel("Admin", 3);
		Usermanagement.getInstance().addUserLevel("Web-Service (only access via SOAP)", 4);

		/* ########################
		 * Dashboard Menu Points
		 */
		Navimanagement.getInstance().addGlobalStructure("home", 1, 124, false,
				true, 1, "home", "false", 582L);
		Navimanagement.getInstance().addMainStructure("dashboardModuleStartScreen", 1, 290,
				true, false, 1, "Dashboard Module", 1, "false");
		Navimanagement.getInstance().addMainStructure("dashboardModuleCalendar", 2,
				291, true, false, 1, "Module Calendar", 1, "false");

		/* ########################
		 * Recording Menu Points - disabled by default
		 */
		
		Navimanagement.getInstance().addGlobalStructure("record", 2, 395,
				false, true, 1, "record", "false", 583L);
		Navimanagement.getInstance().addMainStructure("recordModule", 1, 395,
				true, false, 1, "Module Recording Player", 2, "false");
		
		
		/* ########################
		 * Conference Menu Points
		 */
		
		Navimanagement.getInstance().addGlobalStructure("meetings", 3, 792,
				false, true, 1, "List of Conference Rooms Global Navi", "false", 793L);
		
		Navimanagement.getInstance().addMainStructure("conferenceModuleRoomList", 1,
				792, true, false, 1, "List of Conference Rooms Main Navi", 3, "false");
		
		
		/* ########################
		 * Event Menu Points
		 */

		Navimanagement.getInstance().addGlobalStructure("events", 4, 3, false,
				true, 1, "List of Event Rooms Global Navi", "true", 585L);
		
		Navimanagement.getInstance().addMainStructure("eventModuleRoomList", 1, 3,
				true, false, 1, "List of Event Rooms Main Navi", 4, "false");
		
		
		
		/* ########################
		 * Moderation Menu Points - disabled by default
		 */
		Navimanagement.getInstance().addGlobalStructure("moderate", 5, 649L, false,
				true, 2, "Moderation Menu", "true", 660L);
		
		Navimanagement.getInstance().addMainStructure("moderatorModuleUser", 1, 650,
				true, false, 1, "Moderate Users", 5, "false");
		
		Navimanagement.getInstance().addMainStructure("moderatorModuleRoom", 1, 651,
				true, false, 1, "Moderate Rooms", 5, "false");
		
		
		/* ########################
		 * Administration Menu Points
		 */

		Navimanagement.getInstance().addGlobalStructure("admin", 6, 6, false,
				true, 3, "Administration Menu", "false", 586L);
		
		Navimanagement.getInstance().addMainStructure("adminModuleUser", 14, 125,
				true, false, 2, "Administration of Users", 6, "false");
		
		Navimanagement.getInstance().addMainStructure("adminModuleConnections", 15, 597,
				true, false, 3, "Aministration of Connections", 6, "false");
		
		Navimanagement.getInstance().addMainStructure("adminModuleOrg", 16, 127,
				true, false, 3, "Administration of Organizations", 6, "false");
		
		Navimanagement.getInstance().addMainStructure("adminModuleRoom", 17, 186,
				true, false, 3, "Administration of Rooms", 6, "false");
		
		Navimanagement.getInstance().addMainStructure("adminModuleConfiguration", 18, 263,
				true, false, 3, "Administration of Configuration", 6, "false");
		
		Navimanagement.getInstance().addMainStructure("adminModuleLanguages", 19,
				348, true, false, 3, "Administration of Languages", 6, "false");
		
		Navimanagement.getInstance().addMainStructure("adminModuleLDAP", 20, 1103,
				true, false, 3, "Administration of LDAP Configs", 6, "false");
		
		Navimanagement.getInstance().addMainStructure("adminModuleBackup", 21, 367,
				true, false, 3, "Administration of Backups", 6, "false");

		ErrorManagement.getInstance().addErrorType(new Long(1), new Long(322));
		ErrorManagement.getInstance().addErrorType(new Long(2), new Long(323));

	}
	
	public static void loadDefaultRooms() {

		long conference_Id = Roommanagement.getInstance().addRoomType(
				"conference");
		long audience_Id = Roommanagement.getInstance().addRoomType("audience");

		long restricted_Id = Roommanagement.getInstance().addRoomType("restricted");
		
		long interview_Id = Roommanagement.getInstance().addRoomType("interview");
		
		long custom_Id = Roommanagement.getInstance().addRoomType("custom");
		
		Roommanagement.getInstance().addRoom(3, "public Interview Room", interview_Id,
				"", new Long(16), true, null, false, false, null, false, null, true, 
				false, false, "", "", "", null,
    			null,
				null, false);
		
		Roommanagement.getInstance().addRoom(3, "public Conference Room", conference_Id,
				"", new Long(32), true, null, false, false, null, false, null, true, 
				false, false, "", "", "", null,
    			null,
				null, false);

		Roommanagement.getInstance().addRoom(3, "public Video Only Room", conference_Id,
				"", new Long(32), true, null, false, false, null, false, null, true, 
				false, false, "", "", "", null,
    			null,
				null, false);

		Roommanagement.getInstance().addRoom(3,
				"public Video And Whiteboard Room", conference_Id, "", new Long(32), true,
				null, false, false, null, false, null, true, false, false, "", "", "", null,
    			null,
				null, false);

		Roommanagement.getInstance().addRoom(3, "public Restricted Room", restricted_Id,
				"", new Long(100), true, null, false, false, null, false, null, true, 
				false, false, "", "", "", null,
    			null,
				null, false);
		
		long room2 = Roommanagement.getInstance().addRoom(3,
				"private Conference Room", conference_Id, "", new Long(32), false, null,
				false, false, null, false, null, true, false, false, "", "", "", null,
    			null,
				null, false);
		
		Roommanagement.getInstance().addRoomToOrganisation(3, room2, 1);

		Roommanagement.getInstance().addRoom(3, 
				"public Audience Room", audience_Id, "", new Long(32), true, null, 
				false, false, null, false, null, true, false, false, "", "", "", null,
    			null,
				null, false);

		long room4 = Roommanagement.getInstance().addRoom(3,
				"private Audience Room", audience_Id, "", new Long(32), false, null, 
				false, false, null, false, null, true, false, false, "", "", "", null,
    			null,
				null, false);
		
		Roommanagement.getInstance().addRoomToOrganisation(3, room4, 1);

	}
	
}
