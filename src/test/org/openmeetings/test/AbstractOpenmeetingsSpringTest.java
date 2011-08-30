package org.openmeetings.test;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.openmeetings.app.data.basic.Configurationmanagement;
import org.openmeetings.app.data.basic.ErrorManagement;
import org.openmeetings.app.data.basic.Navimanagement;
import org.openmeetings.app.data.calendar.daos.AppointmentDaoImpl;
import org.openmeetings.app.data.conference.Roommanagement;
import org.openmeetings.app.data.user.Salutationmanagement;
import org.openmeetings.app.data.user.Usermanagement;
import org.openmeetings.app.data.user.dao.UsersDaoImpl;
import org.openmeetings.app.installation.ImportInitvalues;
import org.openmeetings.app.persistence.beans.calendar.Appointment;
import org.openmeetings.app.persistence.beans.user.Users;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners({})
@ContextConfiguration(locations={"classpath:/openmeetings-applicationContext.xml"}, inheritLocations = true)
public abstract class AbstractOpenmeetingsSpringTest extends AbstractJUnit4SpringContextTests {
	private static final Logger log = Red5LoggerFactory.getLogger(AbstractOpenmeetingsSpringTest.class);
	
	@Autowired
	private Configurationmanagement cfgManagement;
	@Autowired
	private AppointmentDaoImpl appointmentDao;
	@Autowired
	private Usermanagement userManagement;
	@Autowired
	private Navimanagement navimanagement;
	@Autowired
	private ErrorManagement errorManagement;
	@Autowired
	private Salutationmanagement salutationmanagement;
	@Autowired
	private Roommanagement roommanagement;
	@Autowired
	private UsersDaoImpl usersDao;
	@Autowired
	private ImportInitvalues importInitvalues;

	@Before
	public void setUp() {
        try {
            if (userManagement.getUserById(1L) == null) {
                makeDefaultScheme();
                log.info("Default scheme created successfully");
            } else {
                log.info("Default scheme already created");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


	public Appointment createAppointment() throws Exception {
		assertNotNull("Cann't access to appointment dao implimentation", appointmentDao);

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

		ap.setUserId(usersDao.getUser(1L));
		ap.setIsConnectedEvent(false);
		Long id = appointmentDao.addAppointmentObj(ap);
		assertNotNull("Cann't add appointment", id);
		return ap;
	}

	public Users createUser(int rnd) throws Exception {
		Users users = new Users();
		// add user
		users.setFirstname("firstname" + rnd);
		users.setLastname("lastname" + rnd);
		users.setLogin("login");
		users.setPassword("pass" + rnd);
		users.setLanguage_id(1L);
		Long user_id = userManagement.addUser(users);
		assertTrue("Cann't add user", user_id > 0);
		users = userManagement.getUserByIdAndDeleted(user_id);
		assertNotNull("User should not be null", users);
		return users;
	}

	public void loadSalutations() {
		salutationmanagement.addUserSalutation("Mr", 261);
		salutationmanagement.addUserSalutation("Ms", 262);
		salutationmanagement.addUserSalutation("Mrs", 841);
		salutationmanagement.addUserSalutation("Dr", 842);
	}

	public void loadConfigurations() {

		// ***************************************
		// ***************************************
		// Timezone settings
		// ***************************************
		Calendar cal = Calendar.getInstance();
		int offset = cal.get(Calendar.ZONE_OFFSET);

		offset = offset / 1000 / 60 / 60;

		String timeZoneJavaFormat = "Etc/GMT";

		if (offset > 0) {
			timeZoneJavaFormat += "+" + offset;
		} else {
			timeZoneJavaFormat += "-" + offset;
		}

		cfgManagement.addConfByKey(3, "default.timezone", timeZoneJavaFormat,
				null, "This is the default timezone if nothing is specified");

		// ***************************************
		// ***************************************
		// additional settings
		// ***************************************

		cfgManagement.addConfByKey(3, "show.facebook.login", "" + 0, null,
				"Show Facebook Login");

		cfgManagement.addConfByKey(3, "default.quality.screensharing", "0",
				null, "Default selection in ScreenSharing Quality");

		cfgManagement.addConfByKey(3, "default.dashboard.tab", "0", null,
				"Default selection in Dashboard tabs as tab-index-id");

		cfgManagement.addConfByKey(3, "dashboard.show.myrooms", "1", null,
				"Show My Rooms Tab");

		cfgManagement.addConfByKey(3, "dashboard.show.chat", "1", null,
				"Show Chat Tab");

		cfgManagement.addConfByKey(3, "dashboard.show.rssfeed", "0", null,
				"Show RSS Tab");

	}

	public void loadMainMenu() {

		userManagement.addUserLevel("User", 1);
		userManagement.addUserLevel("Moderator", 2);
		userManagement.addUserLevel("Admin", 3);
		userManagement.addUserLevel("Web-Service (only access via SOAP)", 4);

		/*
		 * ######################## Dashboard Menu Points
		 */
		navimanagement.addGlobalStructure("home", 1, 124, false, true, 1,
				"home", "false", 582L);
		navimanagement.addMainStructure("dashboardModuleStartScreen", 1, 290,
				true, false, 1, "Dashboard Module", 1, "false");
		navimanagement.addMainStructure("dashboardModuleCalendar", 2, 291,
				true, false, 1, "Module Calendar", 1, "false");

		/*
		 * ######################## Recording Menu Points - disabled by default
		 */

		navimanagement.addGlobalStructure("record", 2, 395, false, true, 1,
				"record", "false", 583L);
		navimanagement.addMainStructure("recordModule", 1, 395, true, false, 1,
				"Module Recording Player", 2, "false");

		/*
		 * ######################## Conference Menu Points
		 */

		navimanagement.addGlobalStructure("meetings", 3, 792, false, true, 1,
				"List of Conference Rooms Global Navi", "false", 793L);

		navimanagement.addMainStructure("conferenceModuleRoomList", 1, 792,
				true, false, 1, "List of Conference Rooms Main Navi", 3,
				"false");

		/*
		 * ######################## Event Menu Points
		 */

		navimanagement.addGlobalStructure("events", 4, 3, false, true, 1,
				"List of Event Rooms Global Navi", "true", 585L);

		navimanagement.addMainStructure("eventModuleRoomList", 1, 3, true,
				false, 1, "List of Event Rooms Main Navi", 4, "false");

		/*
		 * ######################## Moderation Menu Points - disabled by default
		 */
		navimanagement.addGlobalStructure("moderate", 5, 649L, false, true, 2,
				"Moderation Menu", "true", 660L);

		navimanagement.addMainStructure("moderatorModuleUser", 1, 650, true,
				false, 1, "Moderate Users", 5, "false");

		navimanagement.addMainStructure("moderatorModuleRoom", 1, 651, true,
				false, 1, "Moderate Rooms", 5, "false");

		/*
		 * ######################## Administration Menu Points
		 */

		navimanagement.addGlobalStructure("admin", 6, 6, false, true, 3,
				"Administration Menu", "false", 586L);

		navimanagement.addMainStructure("adminModuleUser", 14, 125, true,
				false, 2, "Administration of Users", 6, "false");

		navimanagement.addMainStructure("adminModuleConnections", 15, 597,
				true, false, 3, "Aministration of Connections", 6, "false");

		navimanagement.addMainStructure("adminModuleOrg", 16, 127, true, false,
				3, "Administration of Organizations", 6, "false");

		navimanagement.addMainStructure("adminModuleRoom", 17, 186, true,
				false, 3, "Administration of Rooms", 6, "false");

		navimanagement.addMainStructure("adminModuleConfiguration", 18, 263,
				true, false, 3, "Administration of Configuration", 6, "false");

		navimanagement.addMainStructure("adminModuleLanguages", 19, 348, true,
				false, 3, "Administration of Languages", 6, "false");

		navimanagement.addMainStructure("adminModuleLDAP", 20, 1103, true,
				false, 3, "Administration of LDAP Configs", 6, "false");

		navimanagement.addMainStructure("adminModuleBackup", 21, 367, true,
				false, 3, "Administration of Backups", 6, "false");

		errorManagement.addErrorType(new Long(1), new Long(322));
		errorManagement.addErrorType(new Long(2), new Long(323));

	}

	public void loadDefaultRooms() {

		long conference_Id = roommanagement.addRoomType("conference");
		long audience_Id = roommanagement.addRoomType("audience");

		long restricted_Id = roommanagement.addRoomType("restricted");

		long interview_Id = roommanagement.addRoomType("interview");

		long custom_Id = roommanagement.addRoomType("custom");

		roommanagement.addRoom(3, "public Interview Room", interview_Id, "",
				new Long(16), true, null, false, false, null, false, null,
				true, false, false, "", "", "", null, null, null, false);

		roommanagement.addRoom(3, "public Conference Room", conference_Id, "",
				new Long(32), true, null, false, false, null, false, null,
				true, false, false, "", "", "", null, null, null, false);

		roommanagement.addRoom(3, "public Video Only Room", conference_Id, "",
				new Long(32), true, null, false, false, null, false, null,
				true, false, false, "", "", "", null, null, null, false);

		roommanagement.addRoom(3, "public Video And Whiteboard Room",
				conference_Id, "", new Long(32), true, null, false, false,
				null, false, null, true, false, false, "", "", "", null, null,
				null, false);

		roommanagement.addRoom(3, "public Restricted Room", restricted_Id, "",
				new Long(100), true, null, false, false, null, false, null,
				true, false, false, "", "", "", null, null, null, false);

		long room2 = roommanagement.addRoom(3, "private Conference Room",
				conference_Id, "", new Long(32), false, null, false, false,
				null, false, null, true, false, false, "", "", "", null, null,
				null, false);

		roommanagement.addRoomToOrganisation(3, room2, 1);

		roommanagement.addRoom(3, "public Audience Room", audience_Id, "",
				new Long(32), true, null, false, false, null, false, null,
				true, false, false, "", "", "", null, null, null, false);

		long room4 = roommanagement.addRoom(3, "private Audience Room",
				audience_Id, "", new Long(32), false, null, false, false, null,
				false, null, true, false, false, "", "", "", null, null, null,
				false);

		roommanagement.addRoomToOrganisation(3, room4, 1);

	}

	public void makeDefaultScheme() throws Exception {

		String username = ("username");
		String userpass = ("userpass");
		String useremail = ("useremail");
		String orgname = ("orgname");
		String configdefault = ("configdefault");

		String configreferer = ("configreferer");
		String configsmtp = ("configsmtp");
		String configsmtpport = ("configsmtpport");
		String configmailuser = ("configmailuser");
		String configmailpass = ("configmailpass");
		String mailusetls = ("mailusetls");

		String configdefaultLang = ("configdefaultLang");
		String swf_path = ("swftools_path");
		String im_path = ("imagemagick_path");
		String sendEmailAtRegister = ("sendEmailAtRegister");
		String sendEmailWithVerficationCode = ("sendEmailWithVerficationCode");

		String default_export_font = ("default_export_font");

		String crypt_ClassName = ("org.openmeetings.utils.crypt.MD5CryptImplementation");

		String ffmpeg_path = ("ffmpeg_path");

		String sox_path = ("sox_path");

		String screen_viewer = ("screen_viewer");

		// SIP Applet Configuration
		String sip_enable = ("sip_enable");
		String sip_realm = ("sip_realm");
		String sip_port = ("sip_port");
		String sip_proxyname = ("sip_proxyname");
		String sip_tunnel = ("sip_tunnel");
		String sip_codebase = ("sip_codebase");
		String sip_forcetunnel = ("sip_forcetunnel");

		// OpenXG / OpenSIPg Configuration
		String sip_openxg_enable = ("sip_openxg_enable");
		String openxg_wrapper_url = ("openxg_wrapper_url");
		String openxg_client_id = ("openxg_client_id");
		String openxg_client_secret = ("openxg_client_secret");
		String openxg_client_domain = ("openxg_client_domain");
		String openxg_community_code = ("openxg_community_code");
		String openxg_language_code = ("openxg_language_code");
		String openxg_adminid = ("openxg_adminid");

		// SIP Phone Range Configuration
		String sip_language_phonecode = ("sip_language_phonecode");
		String sip_phonerange_start = ("sip_phonerange_start");
		String sip_phonerange = ("sip_phonerange");

		String timeZone = ("timeZone");

		String filePath = ScopeApplicationAdapter.webAppPath + "/languages/";

		String url_feed = "http://groups.google.com/group/openmeetings-dev/feed/atom_v1_0_msgs.xml";
		String url_feed2 = "http://groups.google.com/group/openmeetings-user/feed/atom_v1_0_msgs.xml";

		importInitvalues.loadMainMenu();
		importInitvalues.loadErrorMappingsFromXML(filePath);
		importInitvalues.loadSalutations();

		importInitvalues.loadConfiguration(crypt_ClassName, configdefault,
				configsmtp, configsmtpport, configreferer, configmailuser,
				configmailpass, mailusetls, configdefaultLang, swf_path,
				im_path, url_feed, url_feed2, sendEmailAtRegister,
				sendEmailWithVerficationCode, default_export_font,
				screen_viewer, ffmpeg_path, sox_path, sip_enable, sip_realm,
				sip_port, sip_proxyname, sip_tunnel, sip_codebase,
				sip_forcetunnel, sip_openxg_enable, openxg_wrapper_url,
				openxg_client_id, openxg_client_secret, openxg_client_domain,
				openxg_community_code, openxg_language_code, openxg_adminid,
				sip_language_phonecode, sip_phonerange_start, sip_phonerange);

		importInitvalues.loadInitUserAndOrganisation(username, userpass,
				useremail, orgname, timeZone);
		importInitvalues.loadDefaultRooms();

		// AppointMent Categories
		importInitvalues.loadInitAppointmentCategories();

		// Appointment Remindertypes
		importInitvalues.loadInitAppointmentReminderTypes();
	}
}
