/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License") +  you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.openmeetings.installation;

import static org.apache.openmeetings.OpenmeetingsVariables.webAppRootKey;
import static org.apache.openmeetings.installation.InstallationConfig.USER_PASSWORD_MINIMUM_LENGTH;
import static org.apache.openmeetings.persistence.beans.basic.Configuration.CRYPT_KEY;
import static org.apache.openmeetings.persistence.beans.basic.Configuration.DASHBOARD_SHOW_MYROOMS_KEY;
import static org.apache.openmeetings.persistence.beans.basic.Configuration.DASHBOARD_SHOW_RSS_KEY;
import static org.apache.openmeetings.persistence.beans.basic.Configuration.DEFAUT_LANG_KEY;
import static org.apache.openmeetings.persistence.beans.basic.Configuration.FRONTEND_REGISTER_KEY;
import static org.apache.openmeetings.persistence.beans.basic.Configuration.LOGIN_MIN_LENGTH_KEY;
import static org.apache.openmeetings.persistence.beans.basic.Configuration.MAX_UPLOAD_SIZE_KEY;
import static org.apache.openmeetings.persistence.beans.basic.Configuration.PASS_MIN_LENGTH_KEY;
import static org.apache.openmeetings.persistence.beans.basic.Configuration.RSS_FEED1_KEY;
import static org.apache.openmeetings.persistence.beans.basic.Configuration.RSS_FEED2_KEY;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.openmeetings.data.basic.FieldLanguageDao;
import org.apache.openmeetings.data.basic.FieldManager;
import org.apache.openmeetings.data.basic.NaviBuilder;
import org.apache.openmeetings.data.basic.dao.ConfigurationDao;
import org.apache.openmeetings.data.basic.dao.ErrorDao;
import org.apache.openmeetings.data.calendar.daos.AppointmentCategoryDao;
import org.apache.openmeetings.data.calendar.daos.AppointmentReminderTypDao;
import org.apache.openmeetings.data.conference.PollManager;
import org.apache.openmeetings.data.conference.RoomManager;
import org.apache.openmeetings.data.conference.dao.SipDao;
import org.apache.openmeetings.data.oauth.OAuth2Dao;
import org.apache.openmeetings.data.user.OrganisationManager;
import org.apache.openmeetings.data.user.UserManager;
import org.apache.openmeetings.data.user.dao.AdminUserDao;
import org.apache.openmeetings.data.user.dao.SalutationDao;
import org.apache.openmeetings.data.user.dao.StateDao;
import org.apache.openmeetings.persistence.beans.lang.FieldLanguage;
import org.apache.openmeetings.persistence.beans.lang.Fieldlanguagesvalues;
import org.apache.openmeetings.persistence.beans.lang.Fieldvalues;
import org.apache.openmeetings.persistence.beans.user.oauth.OAuthServer;
import org.apache.openmeetings.persistence.beans.user.oauth.OAuthServer.RequestMethod;
import org.apache.openmeetings.utils.ImportHelper;
import org.apache.openmeetings.utils.OmFileHelper;
import org.apache.openmeetings.utils.TimezoneUtil;
import org.apache.openmeetings.web.pages.auth.SignInPage;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class ImportInitvalues {
	private static final Logger log = Red5LoggerFactory.getLogger(ImportInitvalues.class, webAppRootKey);

	@Autowired
	private ConfigurationDao configurationDao;
	@Autowired
	private UserManager userManager;
	@Autowired
	private AdminUserDao usersDao;
	@Autowired
	private FieldManager fieldManager;
	@Autowired
	private FieldLanguageDao fieldLanguageDaoImpl;
	@Autowired
	private StateDao statemanagement;
	@Autowired
	private NaviBuilder navimanagement;
	@Autowired
	private ErrorDao errorManagement;
	@Autowired
	private SalutationDao salutationmanagement;
	@Autowired
	private OrganisationManager organisationManager;
	@Autowired
	private RoomManager roomManager;
	@Autowired
	private AppointmentCategoryDao appointmentCategoryDaoImpl;
	@Autowired
	private AppointmentReminderTypDao appointmentReminderTypDaoImpl;
	@Autowired
	private PollManager pollManager;
	@Autowired
	private SipDao sipDao;
	@Autowired
	private OAuth2Dao oauthDao;
	@Autowired
	private TimezoneUtil timezoneUtil;
	
	
	private int progress = 0;

	public int getProgress() {
		return progress;
	}
	
	public void loadUserLevels() {
		userManager.addUserLevel("User", 1);
		userManager.addUserLevel("Moderator", 2);
		userManager.addUserLevel("Admin", 3);
		userManager.addUserLevel("Web-Service (only access via SOAP)", 4);
		log.debug("UserLevels ADDED");
	}
	
	public void loadMainMenu() {
		/*
		 * ######################## Dashboard Menu Points
		 */
		navimanagement.addGlobalStructure("home", 1, 124, false, true, 1,
				"home", false, 582L);
		navimanagement.addMainStructure("dashboardModuleStartScreen", null, 1,
				290, true, false, 1, "Dashboard Module", 1, false, 1450L);
		navimanagement.addMainStructure("dashboardModuleCalendar", null, 2,
				291, true, false, 1, "Module Calendar", 1, false, 1451L);

		/*
		 * ######################## Conference Menu Points
		 */

		navimanagement.addGlobalStructure("meetings", 2, 792, false, true, 1,
				"List of Conference Rooms Global Navi", false, 793L);

		navimanagement.addMainStructure("conferenceModuleRoomList",
				"publicTabButton", 1, 777, true, false, 1,
				"List of Conference Rooms Main Navi", 2, false, 1506L);

		navimanagement.addMainStructure("conferenceModuleRoomList",
				"privateTabButton", 2, 779, true, false, 1,
				"List of Conference Rooms Main Navi", 2, false, 1507L);

		navimanagement.addMainStructure("conferenceModuleRoomList",
				"myTabButton", 3, 781, true, false, 1,
				"List of Conference Rooms Main Navi", 2, false, 1508L);

		/*
		 * ######################## Recording Menu Points - disabled by default
		 */

		navimanagement.addGlobalStructure("record", 3, 395, false, true, 1,
				"record", false, 583L);
		navimanagement.addMainStructure("recordModule", null, 1, 395, true,
				false, 1, "Module Recording Player", 3, false, 1452L);

		/*
		 * ######################## Event Menu Points
		 */

		navimanagement.addGlobalStructure("events", 4, 3, false, true, 1,
				"List of Event Rooms Global Navi", true, 585L);

		navimanagement
				.addMainStructure("eventModuleRoomList", null, 1, 3, true,
						false, 1, "List of Event Rooms Main Navi", 4, false,
						null);

		/*
		 * ######################## Moderation Menu Points - disabled by default
		 */
		navimanagement.addGlobalStructure("moderate", 5, 649L, false, true, 2,
				"Moderation Menu", true, 660L);

		navimanagement.addMainStructure("moderatorModuleUser", null, 1, 650,
				true, false, 1, "Moderate Users", 5, false, null);

		navimanagement.addMainStructure("moderatorModuleRoom", null, 1, 651,
				true, false, 1, "Moderate Rooms", 5, false, null);

		/*
		 * ######################## Administration Menu Points
		 */

		navimanagement.addGlobalStructure("admin", 6, 6, false, true, 3,
				"Administration Menu", false, 586L);

		navimanagement.addMainStructure("adminModuleUser", null, 14, 125, true,
				false, 2, "Administration of Users", 6, false, 1454L);

		navimanagement.addMainStructure("adminModuleConnections", null, 15,
				597, true, false, 3, "Aministration of Connections", 6, false,
				1455L);

		navimanagement.addMainStructure("adminModuleOrg", null, 16, 127, true,
				false, 3, "Administration of Organizations", 6, false, 1456L);

		navimanagement.addMainStructure("adminModuleRoom", null, 17, 186, true,
				false, 3, "Administration of Rooms", 6, false, 1457L);

		navimanagement.addMainStructure("adminModuleConfiguration", null, 18,
				263, true, false, 3, "Administration of Configuration", 6,
				false, 1458L);

		navimanagement.addMainStructure("adminModuleLanguages", null, 19, 348,
				true, false, 3, "Administration of Languages", 6, false, 1459L);

		navimanagement.addMainStructure("adminModuleLDAP", null, 20, 1103,
				true, false, 3, "Administration of LDAP Configs", 6, false,
				1460L);

		navimanagement.addMainStructure("adminModuleOAuth", null, 21, 1571, 
				true, false, 3, "Administration of OAuth2 servers", 6, false, 1572L);

		navimanagement.addMainStructure("adminModuleBackup", null, 22, 367,
				true, false, 3, "Administration of Backups", 6, false, 1461L);

		navimanagement.addMainStructure("adminModuleServers", null, 23, 1498,
				true, false, 3, "Administration of Servers", 6, false, 1499L);
		log.debug("MainMenu ADDED");
	}
	
	public void loadErrorTypes() {
		errorManagement.addErrorType(new Long(1), new Long(322));
		errorManagement.addErrorType(new Long(2), new Long(323));
		log.debug("Error types ADDED");
	}

	public void loadErrorMappingsFromXML() throws Exception {
		SAXReader reader = new SAXReader();
		Document document = reader.read(new File(
				OmFileHelper.getLanguagesDir(), OmFileHelper.nameOfErrorFile));

		Element root = document.getRootElement();

		for (@SuppressWarnings("unchecked")
		Iterator<Element> it = root.elementIterator("row"); it.hasNext();) {

			Element row = it.next();

			Long errorvalues_id = null;
			Long fieldvalues_id = null;
			Long errortype_id = null;

			for (@SuppressWarnings("unchecked")
			Iterator<Element> itSub = row.elementIterator("field"); itSub
					.hasNext();) {

				Element field = itSub.next();

				String name = field.attributeValue("name");
				String text = field.getText();
				// System.out.println("NAME | TEXT "+name+" | "+text);
				if (name.equals("errorvalues_id"))
					errorvalues_id = Long.valueOf(text).longValue();
				if (name.equals("fieldvalues_id"))
					fieldvalues_id = Long.valueOf(text).longValue();
				if (name.equals("errortype_id"))
					errortype_id = Long.valueOf(text).longValue();
			}

			errorManagement.addErrorValues(errorvalues_id, errortype_id,
					fieldvalues_id);
		}
		log.debug("ErrorMappings ADDED");
	}

	public void loadSalutations() {
		salutationmanagement.addUserSalutation("Mr", 261);
		salutationmanagement.addUserSalutation("Ms", 262);
		salutationmanagement.addUserSalutation("Mrs", 841);
		salutationmanagement.addUserSalutation("Dr", 842);
		salutationmanagement.addUserSalutation("Prof", 1464);
		log.debug("Salutations ADDED");
	}

	public void loadConfiguration(InstallationConfig cfg) {
		configurationDao
				.add(
						CRYPT_KEY,
						cfg.cryptClassName,
						null,
						"This Class is used for Authentification-Crypting. "
								+ "Be carefull what you do here! If you change it while "
								+ "running previous Pass of users will not be workign anymore! "
								+ "for more Information see http://openmeetings.apache.org/CustomCryptMechanism.html");

		configurationDao.add(FRONTEND_REGISTER_KEY,
				cfg.allowFrontendRegister, null, "");

		configurationDao.add("default_group_id", "1", null, "");

		// this domain_id is the Organisation of users who register through the
		// frontend
		configurationDao.add("default_domain_id", "1", null, "");


		configurationDao.add("smtp_server", cfg.smtpServer, null,
				"this is the smtp server to send messages");

		configurationDao.add("smtp_port", "" + cfg.smtpPort, null,
				"this is the smtp server port normally 25");

		configurationDao.add("system_email_addr", cfg.mailReferer,
				null, "all send EMails by the system will have this address");

		configurationDao.add("email_username", cfg.mailAuthName,
				null, "System auth email username");

		configurationDao.add("email_userpass", cfg.mailAuthPass,
				null, "System auth email password");

		configurationDao.add("mail.smtp.starttls.enable",
				cfg.mailUseTls, null, "Enable TLS 1=true, 0=false");
		
		configurationDao.add("mail.smtp.connection.timeout", "30000", null,
				"Socket connection timeout value in milliseconds. Default is infinite timeout.");
		
		configurationDao.add("mail.smtp.timeout", "30000", null,
				"Socket I/O timeout value in milliseconds. Default is infinite timeout.");

		configurationDao.add("application.name",
				ConfigurationDao.DEFAULT_APP_NAME, null,
				"Name of the Browser Title window");

		// "1" == "EN"
		configurationDao.add(DEFAUT_LANG_KEY, cfg.defaultLangId,
				null, "Default System Language ID see languages.xml");

		configurationDao.add("swftools_zoom", "" + cfg.swfZoom, null,
				"dpi for conversion of PDF to SWF (should be an integer between 50 and  600 with a default value of 100 dpi)");

		configurationDao.add("swftools_jpegquality",
				"" + cfg.swfJpegQuality, null,
				"compression quality for conversion of PDF to SWF (should be an integer between 1 and 100, with a default value of 85)");

		configurationDao.add("swftools_path", cfg.swfPath, null,
				"Path To SWF-Tools");

		configurationDao.add("imagemagick_path",
				cfg.imageMagicPath, null, "Path to ImageMagick tools");

		configurationDao.add("sox_path", cfg.soxPath, null,
				"Path To SoX-Tools");

		configurationDao.add("ffmpeg_path", cfg.ffmpegPath, null,
				"Path To FFMPEG");
		configurationDao
				.add(
						"office.path",
						cfg.officePath,
						null,
						"The path to OpenOffice/LibreOffice (optional) please set this to the real path in case jodconverter is unable to find OpenOffice/LibreOffice installation automatically");
		configurationDao
				.add(
						"jod.path",
						cfg.jodPath,
						null,
						"The path to JOD library (http://code.google.com/p/jodconverter), configure the path to point to the lib directory of JOD that contains also the jodconverter-core-version.jar");

		configurationDao.add(RSS_FEED1_KEY, cfg.urlFeed, null,
				"Feed URL");

		configurationDao.add(RSS_FEED2_KEY, cfg.urlFeed2, null,
				"Feed URL 2");

		configurationDao
				.add("sendEmailAtRegister", cfg.sendEmailAtRegister,
						null,
						"User get a EMail with their Account data. Values: 0(No) or 1(Yes)");

		configurationDao
				.add(
						"sendEmailWithVerficationCode",
						cfg.sendEmailWithVerficationCode,
						null,
						"User must activate their account by clicking on the "
								+ "activation-link in the registering Email. Values: 0(No) or 1(Yes) "
								+ "It makes no sense to make this(sendEmailWithVerficationCode) 1(Yes) while "
								+ "sendEmailAtRegister is 0(No) cause you need"
								+ "to send a EMail.");
		configurationDao
				.add(
						"default_export_font",
						cfg.defaultExportFont,
						null,
						"The Name of the Font used for exporting/render Images from Whiteboard"
								+ "The Font has to exist on the Server which runs Red5");

		configurationDao.add("default.rpc.userid", "" + 1, null,
				"The User-Id of the Control User in OpenMeetings");

		// ***************************************
		// ***************************************
		// red5SIP Integration Coniguration Values
		// ***************************************

		configurationDao.add("red5sip.enable", cfg.red5SipEnable,
				null, "Enable to enable the red5SIP integration ");
		configurationDao.add("red5sip.room_prefix",
				cfg.red5SipRoomPrefix, null,
				"Numerical prefix for OM rooms created inside the SIP");
		configurationDao.add("red5sip.exten_context",
				cfg.red5SipExtenContext, null,
				"Enable to enable the red5SIP integration ");

		// ***************************************
		// ***************************************
		// Timezone settings
		// ***************************************

		configurationDao.add("default.timezone",
				cfg.ical_timeZone, null,
				"This is the default timezone if nothing is specified");

		// ***************************************
		// ***************************************
		// additional settings
		// ***************************************

		configurationDao.add("show.facebook.login", "" + 0, null,
				"Show Facebook Login");

		configurationDao
				.add(
						"default.quality.screensharing",
						"1",
						null,
						"Default selection in ScreenSharing Quality:\n 0 - bigger frame rate, no resize\n 1 - no resize\n 2 - size == 1/2 of selected area\n 3 - size == 3/8 of selected area");

		configurationDao.add("default.dashboard.tab", "0", null,
				"Default selection in Dashboard tabs as tab-index-id");

		configurationDao.add(DASHBOARD_SHOW_MYROOMS_KEY, "1", null, "Show My Rooms Tab");

		configurationDao.add("dashboard.show.chat", "1", null, "Show Chat Tab");

		configurationDao.add(DASHBOARD_SHOW_RSS_KEY, "0", null, "Show RSS Tab");

		configurationDao
				.add(
						"show.whiteboard.draw.status",
						"0",
						null,
						"Display name of the user who draw the current object (User Name auto-disapper after 3 seconds.");

		configurationDao.add(MAX_UPLOAD_SIZE_KEY, ""
				+ ImportHelper.DEFAULT_MAX_UPLOAD_SIZE, null,
				"Maximum size of upload file (bytes)"); // defaults to 1GB

		configurationDao
				.add(
						"number.minutes.reminder.send",
						"15",
						null,
						"The number of minutes before reminder emails are send. Set to 0 to disable reminder emails");

		configurationDao.add(LOGIN_MIN_LENGTH_KEY, ""
				+ InstallationConfig.USER_LOGIN_MINIMUM_LENGTH, null,
				"Number of chars needed in a user login");

		configurationDao.add(PASS_MIN_LENGTH_KEY, ""
				+ USER_PASSWORD_MINIMUM_LENGTH, null,
				"Number of chars needed in a user login");

		configurationDao
				.add("calendar.conference.rooms.default.size", "50",
						null,
						"Default number of participants conference room created via calendar");

		configurationDao
				.add(
						"use.old.style.ffmpeg.map.option",
						"0",
						null,
						"specify a 1 if you would like to use old FFMPEG -map option with 0.0 instead of 0:0");

		// give exclusive audio key code
		configurationDao
				.add(
						"exclusive.audio.keycode",
						"123",
						null,
						"A hot key code for the 'give exclusive audio' functionality. Keycode 123 is F12");
		// mute/unmute audio key code
		configurationDao
				.add(
						"mute.keycode",
						"118",
						null,
						"A hot key code for the 'mute/unmute audio' functionality. Keycode 118 is F7");
		
		// system-wide ldap params
		configurationDao.add("ldap_default_id", "0", null,
				"Ldap domain selected by default in the login screen");

		// set inviter's email address as ReplyTo in email invitations
		configurationDao
				.add(
						"inviter.email.as.replyto",
						cfg.replyToOrganizer,
						null,
						"Set inviter's email address as ReplyTo in email invitations (1 == set, 0 == NOT set)");

		configurationDao
				.add(
						"default.landing.zone",
						"dashboard.rooms",
						null,
						"Area to be shown to the user after login. Possible values are: "
								+ "dashboard.rooms, dashboard.chat, calendar, rooms.public, rooms.private, rooms.user");
		
		// oauth2 params
		configurationDao
				.add(
						SignInPage.CONF_IGNORE_BAD_SSL,
						"no", 
						null, 
						"Set \"yes\" or \"no\" to enable/disable ssl certifications checking for OAuth2");

		log.debug("Configurations ADDED");
	}

	public void loadRoomTypes() {
		long conference_Id = roomManager.addRoomType(
				"conference", 1541, false);
		log.debug("conference_Id: " + conference_Id);

		// Audience room type is not in use anymore
		roomManager.addRoomType("audience", -1, true);

		long restricted_Id = roomManager.addRoomType(
				"restricted", 1542, false);
		log.debug("restricted_Id: " + restricted_Id);

		long interview_Id = roomManager.addRoomType(
				"interview", 1543, false);
		log.debug("interview_Id: " + interview_Id);

		// Custom room type is not in use anymore
		roomManager.addRoomType("custom", -2, true);
		log.debug("RoomTypes ADDED");
	}

	public void loadDefaultRooms(boolean createRooms) {
		if (createRooms) {
			// hardcoded IDs (they are not intended to be changed)
			long conference_Id = 1;
			long restricted_Id = 3;
			long interview_Id = 4;

			roomManager.addRoom(3, "public Interview Room", interview_Id,
					"", new Long(16), true, null, false, false, null, false,
					null, true, false, true, false, "", "", null, null, null,
					false, // hideTopBar
					false, // hideChat
					false, // hideActivitiesAndActions
					false, // hideFilesExplorer
					false, // hideActionsMenu
					false, // hideScreenSharing
					false, // hideWhiteboard
					false, // showMicrophoneStatus
					false, // chatModerated
					false, // chatOpened
					false, // filesOpened
					false, // autoVideoSelect
					false //sipEnabled
					);

			roomManager.addRoom(3, "public Conference Room", conference_Id,
					"", new Long(32), true, null, false, false, null, false,
					null, true, false, true, false, "", "", null, null, null,
					false, // hideTopBar
					false, // hideChat
					false, // hideActivitiesAndActions
					false, // hideFilesExplorer
					false, // hideActionsMenu
					false, // hideScreenSharing
					false, // hideWhiteboard
					false, // showMicrophoneStatus
					false, // chatModerated
					false, // chatOpened
					false, // filesOpened
					false, // autoVideoSelect
					false //sipEnabled
					);

			roomManager.addRoom(3, "public Video Only Room", conference_Id,
					"", new Long(32), true, null, false, false, null, false,
					null, true, false, true, false, "", "", null, null, null,
					false, // hideTopBar
					false, // hideChat
					false, // hideActivitiesAndActions
					false, // hideFilesExplorer
					false, // hideActionsMenu
					false, // hideScreenSharing
					true, // hideWhiteboard
					false, // showMicrophoneStatus
					false, // chatModerated
					false, // chatOpened
					false, // filesOpened
					false, // autoVideoSelect
					false //sipEnabled
					);

			roomManager.addRoom(3, "public Video And Whiteboard Room",
					conference_Id, "", new Long(32), true, null, false, false,
					null, false, null, true, false, true, false, "", "", null,
					null, null, false, // hideTopBar
					false, // hideChat
					false, // hideActivitiesAndActions
					false, // hideFilesExplorer
					false, // hideActionsMenu
					false, // hideScreenSharing
					false, // hideWhiteboard
					false, // showMicrophoneStatus
					false, // chatModerated
					false, // chatOpened
					false, // filesOpened
					false, // autoVideoSelect
					false //sipEnabled
					);

			roomManager.addRoom(3, "public Restricted Room", restricted_Id,
					"", new Long(100), true, null, false, false, null, false,
					null, true, false, true, false, "", "", null, null, null,
					false, // hideTopBar
					false, // hideChat
					false, // hideActivitiesAndActions
					false, // hideFilesExplorer
					false, // hideActionsMenu
					false, // hideScreenSharing
					false, // hideWhiteboard
					false, // showMicrophoneStatus
					false, // chatModerated
					false, // chatOpened
					false, // filesOpened
					false, // autoVideoSelect
					false //sipEnabled
					);

			roomManager.addRoom(3, "restricted room with micro option set",
					restricted_Id, "", new Long(100), true, null, false, false,
					null, false, null, true, false, true, false, "", "", null,
					null, null, false, // hideTopBar
					false, // hideChat
					false, // hideActivitiesAndActions
					false, // hideFilesExplorer
					false, // hideActionsMenu
					false, // hideScreenSharing
					false, // hideWhiteboard
					true, // showMicrophoneStatus
					false, // chatModerated
					false, // chatOpened
					false, // filesOpened
					false, // autoVideoSelect
					false //sipEnabled
					);

			roomManager.addRoom(3, "conference room with micro option set",
					conference_Id, "", new Long(32), true, null, false, false,
					null, false, null, true, false, true, false, "", "", null,
					null, null, false, // hideTopBar
					false, // hideChat
					false, // hideActivitiesAndActions
					false, // hideFilesExplorer
					false, // hideActionsMenu
					false, // hideScreenSharing
					false, // hideWhiteboard
					true, // showMicrophoneStatus
					false, // chatModerated
					false, // chatOpened
					false, // filesOpened
					false, // autoVideoSelect
					false //sipEnabled
					);

			long room2 = roomManager.addRoom(3, "private Conference Room",
					conference_Id, "", new Long(32), false, null, false, false,
					null, false, null, true, false, true, false, "", "", null,
					null, null, false, // hideTopBar
					false, // hideChat
					false, // hideActivitiesAndActions
					false, // hideFilesExplorer
					false, // hideActionsMenu
					false, // hideScreenSharing
					false, // hideWhiteboard
					false, // showMicrophoneStatus
					false, // chatModerated
					false, // chatOpened
					false, // filesOpened
					false, // autoVideoSelect
					false //sipEnabled
					);

			roomManager.addRoomToOrganisation(3, room2, 1);

		}
	}

	public void loadInitUserAndOrganisation(InstallationConfig cfg) throws Exception {
		Long default_lang_id = Long.parseLong(cfg.defaultLangId);
		if (default_lang_id == null) {
			default_lang_id = 1L;
		}

		// Add default group
		Long organisation_id = organisationManager.addOrganisation(cfg.group, 1);

		// BaseUrl as param is empty as we do not send an EMAIL here
		Long user_id = userManager.registerUserInit(new Long(3), 3, 1,
				1, cfg.username, cfg.password, "lastname", "firstname", cfg.email,
				new java.util.Date(), "street", "no", "fax", "zip", 1,
				"town", default_lang_id, false,
				Arrays.asList(organisation_id), "phone", false, "", false,
				timezoneUtil.getTimeZone(cfg.ical_timeZone),
				false, "", "", false, true, null);

		log.debug("Installation - User Added user-Id " + user_id);

		if (user_id < 0) {
			throw new Exception(
					"Could not add user user returns a negative error message: "
							+ user_id);
		}
	}

	/**
	 * import all language Names from the xml file
	 * 
	 * @param filePath
	 * @throws Exception
	 */
	private void loadCountriesFiles() throws Exception {
		SAXReader reader = new SAXReader();
		Document document = reader.read(new File(
				OmFileHelper.getLanguagesDir(),
				OmFileHelper.nameOfCountriesFile));

		Element root = document.getRootElement();

		for (@SuppressWarnings("rawtypes")
		Iterator it = root.elementIterator("country"); it.hasNext();) {
			Element item = (Element) it.next();

			statemanagement.addState(item.attributeValue("name"),
					item.attributeValue("short"),
					Integer.parseInt(item.attributeValue("code")));
		}
		log.debug("Countries ADDED");
	}

	/**
	 * load all availible languages File names and language name's from the
	 * config file
	 * 
	 * @param filePath
	 * @return
	 * @throws Exception
	 */
	public static Map<Integer, Map<String, Object>> getLanguageFiles() throws Exception {
		Map<Integer, Map<String, Object>> languages = new LinkedHashMap<Integer, Map<String, Object>>();

		SAXReader reader = new SAXReader();
		Document document = reader
				.read(new File(OmFileHelper.getLanguagesDir(),
						OmFileHelper.nameOfLanguageFile));

		Element root = document.getRootElement();

		for (@SuppressWarnings("unchecked")
		Iterator<Element> it = root.elementIterator("lang"); it.hasNext();) {
			Element item = it.next();
			String country = item.getText();
			Integer id = Integer.valueOf(item.attribute("id").getValue())
					.intValue();

			String rtl = item.attribute("rightToLeft").getValue();
			String code = item.attribute("code").getValue();

			LinkedHashMap<String, Object> lang = new LinkedHashMap<String, Object>();
			lang.put("id", id);
			lang.put("name", country);
			lang.put("rtl", rtl);
			lang.put("code", code);
			// log.error("getLanguageFiles "+country);
			languages.put(id, lang);
		}
		log.debug("Language files are loaded");
		return languages;
	}

	/**
	 * @author o.becherer initial fillment of Appointmentcategories
	 */
	// ------------------------------------------------------------------------------
	public void loadInitAppointmentCategories() {
		log.debug("ImportInitValues.loadInitAppointmentCategories");

		appointmentCategoryDaoImpl.addAppointmentCategory(new Long(-1),
				"default", "default");
	}

	// ------------------------------------------------------------------------------

	/**
	 * @author o.becherer initial fillment of AppointMentReminderTypes
	 */
	// ------------------------------------------------------------------------------
	public void loadInitAppointmentReminderTypes() {
		log.debug("ImportInitValues.loadInitAppointmentReminderTypes");

		appointmentReminderTypDaoImpl.addAppointmentReminderTyps(-1L,
				"do not send notification", 1568);
		appointmentReminderTypDaoImpl.addAppointmentReminderTyps(-1L,
				"simple email", 1569);
		appointmentReminderTypDaoImpl.addAppointmentReminderTyps(-1L,
				"iCal email", 1570);
	}

	public void loadLanguagesFile(int langId) throws Exception {
		Map<Integer, Map<String, Object>> listlanguages = getLanguageFiles();
		loadLanguagesFile(listlanguages, new Hashtable<Long, Fieldvalues>(3000), langId);
	}

	public void loadLanguagesFile(String langName) throws Exception {
		Map<Integer, Map<String, Object>> listlanguages = getLanguageFiles();
		for (int langId : listlanguages.keySet()) {
			Map<String, Object> langMap = listlanguages.get(langId);
			if (langName.equals(langMap.get("name"))) {
				loadLanguagesFile(listlanguages, new Hashtable<Long, Fieldvalues>(3000), langId);
				break;
			}
		}
	}

	public void loadLanguagesFile(
			Map<Integer, Map<String, Object>> listlanguages,
			Map<Long, Fieldvalues> fieldCache, int langId)
			throws Exception {
		Map<String, Object> langMap = listlanguages.get(langId);
		log.debug("loadInitLanguages lang: " + langMap);

		String langName = (String) langMap.get("name");
		String rtl = (String) langMap.get("rtl");
		String code = (String) langMap.get("code");

		log.debug("loadInitLanguages rtl from xml: " + rtl);

		Boolean langRtl = false;

		if (rtl != null && rtl.equals("true"))
			langRtl = true;

		long ticks = System.currentTimeMillis();
		FieldLanguage lang = fieldLanguageDaoImpl.addLanguage(langId, langName,
				langRtl, code);

		SAXReader reader = new SAXReader();
		Document document = reader.read(new File(
				OmFileHelper.getLanguagesDir(), langName + ".xml"));

		Element root = document.getRootElement();

		Map<Long, Fieldlanguagesvalues> flvMap = lang.getLanguageValuesMap();
		for (@SuppressWarnings("rawtypes")
		Iterator it = root.elementIterator("string"); it.hasNext();) {
			Element item = (Element) it.next();
			// log.error(item.getName());

			Long id = Long.valueOf(item.attributeValue("id")).longValue();
			String name = item.attributeValue("name");
			String value = "";

			for (@SuppressWarnings("rawtypes")
			Iterator t2 = item.elementIterator("value"); t2.hasNext();) {
				Element val = (Element) t2.next();
				value = val.getText();
			}

			// log.error("result: "+langFieldIdIsInited+" "+id+" "+name+" "+value);

			if (flvMap.containsKey(id)) {
				Fieldlanguagesvalues flv = flvMap.get(id);
				flv.setUpdatetime(new Date());
				flv.setValue(value);
			} else {
				Fieldvalues fv = null;
				// Only do that for the first field-set
				if (!fieldCache.containsKey(id)) {
					fv = fieldManager.addFieldById(name, id);
					fieldCache.put(id, fv);
				} else {
					fv = fieldCache.get(id);
				}
				Fieldlanguagesvalues flv = new Fieldlanguagesvalues();
				flv.setStarttime(new Date());
				flv.setValue(value);
				flv.setLanguage_id(lang.getLanguage_id());
				flv.setFieldvalues(fv);
				flv.setDeleted(false);
				flvMap.put(id, flv);
			}
		}
		lang.setLanguageValues(flvMap.values());
		fieldLanguageDaoImpl.updateLanguage(lang);
		log.debug("Lang ADDED: " + lang + "; seconds passed: "
				+ (System.currentTimeMillis() - ticks) / 1000);
	}

	public void loadLanguagesFiles() throws Exception {
		Map<Integer, Map<String, Object>> listlanguages = getLanguageFiles();

		Map<Long, Fieldvalues> fieldCache = new Hashtable<Long, Fieldvalues>(3000);
		/** Read all languages files */
		for (int langId : listlanguages.keySet()) {
			loadLanguagesFile(listlanguages, fieldCache, langId);
		}
		log.debug("All languages are imported");
	}

	public void loadInitialOAuthServers() throws Exception {
		// Yandex
		OAuthServer yandexServer = new OAuthServer();
		yandexServer.setName("Yandex");
		yandexServer.setIconUrl("http://yandex.st/morda-logo/i/favicon.ico");
		yandexServer.setClientId("<put your client_id>");
		yandexServer.setClientSecret("<put your client_secret>");
		yandexServer.setEmailParamName("default_email");
		yandexServer.setEnabled(false);
		yandexServer.setLoginParamName("default_email");
		yandexServer.setRequestInfoUrl("https://login.yandex.ru/info?format=json&oauth_token={$access_token}");
		yandexServer.setRequestTokenUrl("https://oauth.yandex.ru/token");
		yandexServer.setRequestKeyUrl("https://oauth.yandex.ru/authorize?response_type=code&client_id={$client_id}");
		yandexServer.setRequestTokenAttributes("grant_type=authorization_code&code={$code}&client_id={$client_id}&client_secret={$client_secret}");
		yandexServer.setRequestTokenMethod(RequestMethod.POST);
		oauthDao.update(yandexServer, null);
		
		// Google
		OAuthServer googleServer = new OAuthServer();
		googleServer.setName("Google");
		googleServer.setIconUrl("https://www.google.ru/images/google_favicon_128.png");
		googleServer.setEnabled(false);
		googleServer.setClientId("<put your client_id>");
		googleServer.setClientSecret("<put your client_secret>");
		googleServer.setRequestKeyUrl("https://accounts.google.com/o/oauth2/auth?redirect_uri={$redirect_uri}&response_type=code&client_id={$client_id}"
				+ "&scope=https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.email+https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.profile");
		googleServer.setRequestTokenUrl("https://accounts.google.com/o/oauth2/token");
		googleServer.setRequestTokenMethod(RequestMethod.POST);
		googleServer.setRequestTokenAttributes("code={$code}&client_id={$client_id}&client_secret={$client_secret}&redirect_uri={$redirect_uri}&grant_type=authorization_code");
		googleServer.setRequestInfoUrl("https://www.googleapis.com/oauth2/v1/userinfo?access_token={$access_token}");
		googleServer.setLoginParamName("email");
		googleServer.setEmailParamName("email");
		googleServer.setFirstnameParamName("given_name");
		googleServer.setLastnameParamName("family_name");
		oauthDao.update(googleServer, null);
		
		// Facebook
		OAuthServer facebookServer = new OAuthServer();
		facebookServer.setName("Facebook");
		facebookServer.setIconUrl("https://www.facebook.com/images/fb_icon_325x325.png");
		facebookServer.setEnabled(false);
		facebookServer.setClientId("<put your client_id>");
		facebookServer.setClientSecret("<put your client_secret>");
		facebookServer.setRequestKeyUrl("https://www.facebook.com/dialog/oauth?client_id={$client_id}&redirect_uri={$redirect_uri}&scope=email");
		facebookServer.setRequestTokenUrl("https://graph.facebook.com/oauth/access_token");
		facebookServer.setRequestTokenMethod(RequestMethod.POST);
		facebookServer.setRequestTokenAttributes("client_id={$client_id}&redirect_uri={$redirect_uri}&client_secret={$client_secret}&code={$code}");
		facebookServer.setRequestInfoUrl("https://graph.facebook.com/me?access_token={$access_token}&fields=username,first_name,last_name,email");
		facebookServer.setLoginParamName("username");
		facebookServer.setEmailParamName("email");
		facebookServer.setFirstnameParamName("first_name");
		facebookServer.setLastnameParamName("last_name");
		oauthDao.update(facebookServer, null);
	}
	
	// ------------------------------------------------------------------------------

	/**
	 * Loading initial Language from xml Files into database
	 */
	// ------------------------------------------------------------------------------
	public void loadPollTypes() {
		pollManager.addPollType(26L, false);
		pollManager.addPollType(27L, true);
	}

	// ------------------------------------------------------------------------------

	public void loadSystem(InstallationConfig cfg, boolean force) throws Exception {
		// FIXME dummy check if installation was performed before
		if (!force && usersDao.count() > 0) {
			log.debug("System contains users, no need to install data one more time.");
		}
		sipDao.delete();
		progress = 6;
		loadUserLevels();
		progress = 13;
		loadMainMenu();
		progress = 19;
		loadErrorTypes();
		progress = 25;
		loadErrorMappingsFromXML();
		progress = 31;
		loadCountriesFiles();
		progress = 44;
		loadLanguagesFiles();
		progress = 50;
		loadSalutations();
		progress = 56;
		// AppointMent Categories
		loadInitAppointmentCategories();
		progress = 63;
		// Appointment Reminder types
		loadInitAppointmentReminderTypes();
		progress = 69;
		// Appointment poll types
		loadPollTypes();
		progress = 75;
		loadRoomTypes();
		progress = 81;

		loadConfiguration(cfg);
		progress = 88;
		loadInitialOAuthServers();
		progress = 91;
	}

	public void loadAll(InstallationConfig cfg, boolean force) throws Exception {
		// FIXME dummy check if installation was performed before
		if (!force && usersDao.count() > 0) {
			log.debug("System contains users, no need to install data one more time.");
			return;
		}
		loadSystem(cfg, force);
		loadInitUserAndOrganisation(cfg);
		progress = 94;

		loadDefaultRooms("1".equals(cfg.createDefaultRooms));
		progress = 100;
	}
}
