package org.openmeetings.test.smoke;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.openmeetings.app.installation.ImportInitvalues;
import org.openmeetings.test.AbstractOpenmeetingsSpringTest;
import org.springframework.beans.factory.annotation.Autowired;

public class SmokeTest extends AbstractOpenmeetingsSpringTest {

	private static final Logger log = Logger.getLogger(SmokeTest.class);

	private static final String crypt_ClassName = "org.openmeetings.utils.crypt.MD5Implementation";

	private static final String configsmtpport = "25";

	private static final String configsmtp = "smtp.webbase-design.de";

	private static final String configmailuser = "xyz";

	private static final String configdefault = "";

	private static final String configmailpass = "qwertz";

	private static final String configreferer = "noreply@webbase-design.de";

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

	private static final String useremail = null;

	private static final String filePath = null;

	@Autowired
	private ImportInitvalues importInitvalues;

	@Test
	public void doInstallation() {
		try {

			importInitvalues.loadInitLanguages(url_feed);

			importInitvalues.loadMainMenu();

			importInitvalues.loadErrorMappingsFromXML(filePath);

			importInitvalues.loadSalutations();

			importInitvalues.loadConfiguration(crypt_ClassName, configdefault,
					configsmtp, configsmtpport, configreferer, configmailuser,
					configmailpass, mailusetls, configdefaultLang, swf_path,
					im_path, url_feed, url_feed2, sendEmailAtRegister,
					sendEmailWithVerficationCode, default_export_font,
					screen_viewer, ffmpeg_path, sox_path, sip_enable,
					sip_realm, sip_port, sip_proxyname, sip_tunnel,
					sip_codebase, sip_forcetunnel, sip_openxg_enable,
					openxg_wrapper_url, openxg_client_id, openxg_client_secret,
					openxg_client_domain, openxg_community_code,
					openxg_language_code, openxg_adminid,
					sip_language_phonecode, sip_phonerange_start,
					sip_phonerange);

			importInitvalues.loadInitUserAndOrganisation(username, userpass,
					useremail, orgname, timeZone);

			importInitvalues.loadDefaultRooms();

			// AppointMent Categories
			importInitvalues.loadInitAppointmentCategories();

			// Appointment Remindertypes
			importInitvalues.loadInitAppointmentReminderTypes();

		} catch (Exception err) {
			log.error("[doInstallation]", err);
		}
	}

}
