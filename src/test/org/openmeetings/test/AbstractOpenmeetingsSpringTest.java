package org.openmeetings.test;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import java.util.Date;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.openmeetings.app.data.calendar.daos.AppointmentDaoImpl;
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
	
	private static final String crypt_ClassName = "org.openmeetings.utils.crypt.MD5Implementation";
	private static final String configsmtpport = "25";
	private static final String configsmtp = "localhost";
	private static final String configmailuser = "xyz";
	private static final String configdefault = "";
	private static final String configmailpass = "qwertz";
	private static final String configreferer = "noreply@openmeetings.de";
	private static final String swf_path = "";
	private static final String im_path = "";
	private static final String configdefaultLang = "1";
	private static final String mailusetls = "0";
	private static final String sendEmailAtRegister = "0";
	private static final String url_feed = "http://groups.google.com/group/openmeetings-user/feed/atom_v1_0_msgs.xml";
	private static final String url_feed2 = "http://groups.google.com/group/openmeetings-dev/feed/atom_v1_0_msgs.xml";
	private static final String sendEmailWithVerficationCode = "0";
	private static final String default_export_font = "TimesNewRoman";
	private static final String screen_viewer = "4";
	private static final String ffmpeg_path = "";
	private static final String sip_enable = "0";
	private static final String sox_path = "";
	private static final String sip_proxyname = "";
	private static final String sip_port = "";
	private static final String sip_tunnel = "";
	private static final String sip_realm = "";
	private static final String sip_openxg_enable = "0";
	private static final String sip_forcetunnel = "";
	private static final String sip_codebase = "";
	private static final String openxg_client_secret = "";
	private static final String openxg_wrapper_url = "";
	private static final String openxg_client_id = "";
	private static final String openxg_client_domain = "";
	private static final String openxg_community_code = "";
	private static final String openxg_language_code = "";
	private static final String openxg_adminid = "";
	private static final String sip_language_phonecode = "";
	private static final String sip_phonerange_start = "";
	private static final String sip_phonerange = "";
	private static final String username = "junit";
	private static final String userpass = "test";
	private static final String orgname = "smoketest";
	private static final String timeZone = "";
	private static final String useremail = "junit@openmeetings.de";
	private static String filePath = null;
	
	@Autowired
	private AppointmentDaoImpl appointmentDao;
	@Autowired
	private Usermanagement userManagement;
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
		assertNotNull("Can't access to appointment dao implimentation", appointmentDao);

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

	private void makeDefaultScheme() throws Exception {
		filePath = System.getProperty("webapps.root") + "/" + ScopeApplicationAdapter.webAppPath + "/languages/";

		importInitvalues.loadMainMenu();
		importInitvalues.loadErrorMappingsFromXML(filePath);
		importInitvalues.loadInitLanguages(filePath);
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
				useremail, orgname, timeZone, configdefaultLang);
		importInitvalues.loadDefaultRooms(true);

		// AppointMent Categories
		importInitvalues.loadInitAppointmentCategories();

		// Appointment Remindertypes
		importInitvalues.loadInitAppointmentReminderTypes();
	}
}
