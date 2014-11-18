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

import static org.apache.openmeetings.db.dao.basic.ConfigurationDao.DEFAULT_MAX_UPLOAD_SIZE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_APPLICATION_BASE_URL;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_APPLICATION_NAME;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_APPOINTMENT_REMINDER_MINUTES;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_CALENDAR_FIRST_DAY;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_CRYPT_KEY;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DASHBOARD_SHOW_MYROOMS_KEY;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DASHBOARD_SHOW_RSS_KEY;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DEFAULT_LANDING_ZONE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DEFAULT_LDAP_ID;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DEFAUT_LANG_KEY;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_FLASH_PORT;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_FLASH_PROTOCOL;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_FRONTEND_REGISTER_KEY;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_IGNORE_BAD_SSL;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_LOGIN_MIN_LENGTH_KEY;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_MAX_UPLOAD_SIZE_KEY;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_PASS_MIN_LENGTH_KEY;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_REDIRECT_URL_FOR_EXTERNAL_KEY;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_RSS_FEED1_KEY;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_RSS_FEED2_KEY;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_SCREENSHARING_ALLOW_REMOTE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_SCREENSHARING_FPS;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_SCREENSHARING_FPS_SHOW;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_SCREENSHARING_QUALITY;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_SOAP_REGISTER_KEY;
import static org.apache.openmeetings.util.OpenmeetingsVariables.USER_LOGIN_MINIMUM_LENGTH;
import static org.apache.openmeetings.util.OpenmeetingsVariables.USER_PASSWORD_MINIMUM_LENGTH;
import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dao.basic.ErrorDao;
import org.apache.openmeetings.db.dao.basic.NavigationDao;
import org.apache.openmeetings.db.dao.calendar.AppointmentCategoryDao;
import org.apache.openmeetings.db.dao.calendar.AppointmentReminderTypDao;
import org.apache.openmeetings.db.dao.label.FieldLanguageDao;
import org.apache.openmeetings.db.dao.label.FieldValueDao;
import org.apache.openmeetings.db.dao.room.PollDao;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.dao.room.RoomTypeDao;
import org.apache.openmeetings.db.dao.room.SipDao;
import org.apache.openmeetings.db.dao.server.OAuth2Dao;
import org.apache.openmeetings.db.dao.user.IUserManager;
import org.apache.openmeetings.db.dao.user.OrganisationDao;
import org.apache.openmeetings.db.dao.user.SalutationDao;
import org.apache.openmeetings.db.dao.user.StateDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.label.FieldLanguage;
import org.apache.openmeetings.db.entity.label.Fieldlanguagesvalues;
import org.apache.openmeetings.db.entity.label.Fieldvalues;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.room.RoomOrganisation;
import org.apache.openmeetings.db.entity.server.OAuthServer;
import org.apache.openmeetings.db.entity.server.OAuthServer.RequestMethod;
import org.apache.openmeetings.db.entity.user.Organisation;
import org.apache.openmeetings.db.entity.user.User.Right;
import org.apache.openmeetings.db.util.TimezoneUtil;
import org.apache.openmeetings.util.OmFileHelper;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class ImportInitvalues {
	private static final Logger log = Red5LoggerFactory.getLogger(ImportInitvalues.class, webAppRootKey);

	@Autowired
	private ConfigurationDao cfgDao;
	@Autowired
	private UserDao usersDao;
	@Autowired
	private FieldLanguageDao fieldLanguageDaoImpl;
	@Autowired
	private FieldValueDao fieldValueDao;
	@Autowired
	private StateDao statemanagement;
	@Autowired
	private NavigationDao navimanagement;
	@Autowired
	private ErrorDao errorManagement;
	@Autowired
	private SalutationDao salutationmanagement;
	@Autowired
	private AppointmentCategoryDao appointmentCategoryDaoImpl;
	@Autowired
	private AppointmentReminderTypDao appointmentReminderTypDaoImpl;
	@Autowired
	private PollDao pollManager;
	@Autowired
	private SipDao sipDao;
	@Autowired
	private OAuth2Dao oauthDao;
	@Autowired
	private TimezoneUtil timezoneUtil;
	@Autowired
	private RoomTypeDao roomTypeDao;
	@Autowired
	private OrganisationDao organisationDao;
	@Autowired
	private IUserManager userManager;
	@Autowired
	private RoomDao roomDao;
	
	
	private int progress = 0;

	public int getProgress() {
		return progress;
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
		Document document = reader.read(new File(OmFileHelper.getLanguagesDir(), OmFileHelper.nameOfErrorFile));

		Element root = document.getRootElement();

		for (@SuppressWarnings("unchecked")
		Iterator<Element> it = root.elementIterator("row"); it.hasNext();) {

			Element row = it.next();

			Long errorvalues_id = null;
			Long fieldvalues_id = null;
			Long errortype_id = null;

			for (@SuppressWarnings("unchecked")
			Iterator<Element> itSub = row.elementIterator("field"); itSub.hasNext();) {
				Element field = itSub.next();

				String name = field.attributeValue("name");
				String text = field.getText();
				// System.out.println("NAME | TEXT "+name+" | "+text);
				if (name.equals("errorvalues_id")) {
					errorvalues_id = Long.valueOf(text);
				}
				if (name.equals("fieldvalues_id")) {
					fieldvalues_id = Long.valueOf(text);
				}
				if (name.equals("errortype_id")) {
					errortype_id = Long.valueOf(text);
				}
			}

			errorManagement.addErrorValues(errorvalues_id, errortype_id, fieldvalues_id);
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
		cfgDao.add(CONFIG_CRYPT_KEY, cfg.cryptClassName, null,
				"This Class is used for Authentification-Crypting. "
						+ "Be carefull what you do here! If you change it while "
						+ "running previous Pass of users will not be workign anymore! "
						+ "for more Information see http://openmeetings.apache.org/CustomCryptMechanism.html");

		cfgDao.add(CONFIG_FRONTEND_REGISTER_KEY, cfg.allowFrontendRegister, null, "Is user register available on login screen");
		cfgDao.add(CONFIG_SOAP_REGISTER_KEY, "1", null, "Is user register available via SOAP/REST");
		cfgDao.add("default_group_id", "1", null, "");

		// this domain_id is the Organisation of users who register through the
		// frontend
		cfgDao.add("default_domain_id", "1", null, "");


		cfgDao.add("smtp_server", cfg.smtpServer, null,
				"this is the smtp server to send messages");

		cfgDao.add("smtp_port", "" + cfg.smtpPort, null,
				"this is the smtp server port normally 25");

		cfgDao.add("system_email_addr", cfg.mailReferer, null, "all send EMails by the system will have this address");

		cfgDao.add("email_username", cfg.mailAuthName, null, "System auth email username");

		cfgDao.add("email_userpass", cfg.mailAuthPass, null, "System auth email password");

		cfgDao.add("mail.smtp.starttls.enable", cfg.mailUseTls, null, "Enable TLS 1=true, 0=false");
		
		cfgDao.add("mail.smtp.connection.timeout", "30000", null,
				"Socket connection timeout value in milliseconds. Default is infinite timeout.");
		
		cfgDao.add("mail.smtp.timeout", "30000", null,
				"Socket I/O timeout value in milliseconds. Default is infinite timeout.");

		cfgDao.add(CONFIG_APPLICATION_NAME, ConfigurationDao.DEFAULT_APP_NAME, null, "Name of the Browser Title window");

		// "1" == "EN"
		cfgDao.add(CONFIG_DEFAUT_LANG_KEY, cfg.defaultLangId, null, "Default System Language ID see languages.xml");

		cfgDao.add("swftools_zoom", "" + cfg.swfZoom, null,
				"dpi for conversion of PDF to SWF (should be an integer between 50 and  600 with a default value of 100 dpi)");

		cfgDao.add("swftools_jpegquality", "" + cfg.swfJpegQuality, null,
				"compression quality for conversion of PDF to SWF (should be an integer between 1 and 100, with a default value of 85)");

		cfgDao.add("swftools_path", cfg.swfPath, null, "Path To SWF-Tools");

		cfgDao.add("imagemagick_path", cfg.imageMagicPath, null, "Path to ImageMagick tools");

		cfgDao.add("sox_path", cfg.soxPath, null, "Path To SoX-Tools");

		cfgDao.add("ffmpeg_path", cfg.ffmpegPath, null, "Path To FFMPEG");
		cfgDao.add("office.path", cfg.officePath, null,
						"The path to OpenOffice/LibreOffice (optional) please set this to the real path in case jodconverter is unable to find OpenOffice/LibreOffice installation automatically");
		cfgDao.add("jod.path", cfg.jodPath, null,
				"The path to JOD library (http://code.google.com/p/jodconverter), configure the path to point to the lib directory of JOD that contains also the jodconverter-core-version.jar");

		cfgDao.add(CONFIG_RSS_FEED1_KEY, cfg.urlFeed, null, "Feed URL");

		cfgDao.add(CONFIG_RSS_FEED2_KEY, cfg.urlFeed2, null, "Feed URL 2");

		cfgDao.add("sendEmailAtRegister", cfg.sendEmailAtRegister, null,
				"User get a EMail with their Account data. Values: 0(No) or 1(Yes)");

		cfgDao.add("sendEmailWithVerficationCode", cfg.sendEmailWithVerficationCode, null,
				"User must activate their account by clicking on the "
					+ "activation-link in the registering Email. Values: 0(No) or 1(Yes) "
					+ "It makes no sense to make this(sendEmailWithVerficationCode) 1(Yes) while "
					+ "sendEmailAtRegister is 0(No) cause you need"
					+ "to send a EMail.");
		cfgDao.add("default_export_font", cfg.defaultExportFont, null,
				"The Name of the Font used for exporting/render Images from Whiteboard"
					+ "The Font has to exist on the Server which runs Red5");

		cfgDao.add("default.rpc.userid", "" + 1, null, "The User-Id of the Control User in OpenMeetings");

		cfgDao.add(CONFIG_APPLICATION_BASE_URL, cfg.baseUrl, null, "Base URL your OPenmeetings installation will be accessible at.");
		
		// ***************************************
		// ***************************************
		// red5SIP Integration Coniguration Values
		// ***************************************

		cfgDao.add("red5sip.enable", cfg.red5SipEnable, null, "Enable to enable the red5SIP integration ");
		cfgDao.add("red5sip.room_prefix", cfg.red5SipRoomPrefix, null,
				"Numerical prefix for OM rooms created inside the SIP");
		cfgDao.add("red5sip.exten_context", cfg.red5SipExtenContext, null,
				"Enable to enable the red5SIP integration ");

		// ***************************************
		// ***************************************
		// Timezone settings
		// ***************************************

		cfgDao.add("default.timezone", cfg.ical_timeZone, null, "This is the default timezone if nothing is specified");

		// ***************************************
		// ***************************************
		// additional settings
		// ***************************************

		cfgDao.add("show.facebook.login", "" + 0, null, "Show Facebook Login");

		cfgDao.add(CONFIG_SCREENSHARING_QUALITY, "1", null,
				"Default selection in ScreenSharing Quality:\n 0 - bigger frame rate, no resize\n 1 - no resize\n 2 - size == 1/2 of selected area\n 3 - size == 3/8 of selected area");

		cfgDao.add(CONFIG_SCREENSHARING_FPS, "10", null, "Default selection in ScreenSharing FPS");
		cfgDao.add(CONFIG_SCREENSHARING_FPS_SHOW, "true", null, "Is screensharing FPS should be displayed or not (true/false)");
		cfgDao.add(CONFIG_SCREENSHARING_ALLOW_REMOTE, "true", null, "Is remote control will be enabled while screensharing. Allowing remote control will be not possible in case it is set to 'false' (true/false)");

		cfgDao.add(CONFIG_DASHBOARD_SHOW_MYROOMS_KEY, "1", null, "Show My Rooms Tab");

		cfgDao.add("dashboard.show.chat", "1", null, "Show Chat Tab");

		cfgDao.add(CONFIG_DASHBOARD_SHOW_RSS_KEY, "0", null, "Show RSS Tab");

		cfgDao.add("show.whiteboard.draw.status", "0", null,
				"Display name of the user who draw the current object (User Name auto-disapper after 3 seconds.");

		cfgDao.add(CONFIG_MAX_UPLOAD_SIZE_KEY, "" + DEFAULT_MAX_UPLOAD_SIZE, null,
				"Maximum size of upload file (bytes)"); // defaults to 1GB

		cfgDao.add(CONFIG_APPOINTMENT_REMINDER_MINUTES, "15", null,
				"The number of minutes before reminder emails are send. Set to 0 to disable reminder emails");

		cfgDao.add(CONFIG_LOGIN_MIN_LENGTH_KEY, "" + USER_LOGIN_MINIMUM_LENGTH, null,
				"Number of chars needed in a user login");

		cfgDao.add(CONFIG_PASS_MIN_LENGTH_KEY, "" + USER_PASSWORD_MINIMUM_LENGTH, null,
				"Number of chars needed in a user login");

		cfgDao.add("calendar.conference.rooms.default.size", "50", null,
				"Default number of participants conference room created via calendar");

		cfgDao.add("use.old.style.ffmpeg.map.option", "0", null,
				"specify a 1 if you would like to use old FFMPEG -map option with 0.0 instead of 0:0");

		// give exclusive audio key code
		cfgDao.add("exclusive.audio.keycode", "123", null,
				"A hot key code for the 'give exclusive audio' functionality. Keycode 123 is F12");
		// mute/unmute audio key code
		cfgDao.add("mute.keycode", "118", null,
				"A hot key code for the 'mute/unmute audio' functionality. Keycode 118 is F7");
		
		// system-wide ldap params
		cfgDao.add(CONFIG_DEFAULT_LDAP_ID, "0", null, "Ldap domain selected by default in the login screen");

		// set inviter's email address as ReplyTo in email invitations
		cfgDao.add("inviter.email.as.replyto", cfg.replyToOrganizer, null,
				"Set inviter's email address as ReplyTo in email invitations (1 == set, 0 == NOT set)");

		cfgDao.add(CONFIG_DEFAULT_LANDING_ZONE, "user/dashboard" , null
				, "Area to be shown to the user after login. Possible values are: "
					+ "user/dashboard, user/calendar, user/record, rooms/my, rooms/group, rooms/public, admin/user, admin/connection"
					+ ", admin/group, admin/room, admin/config, admin/lang, admin/ldap, admin/backup, admin/server, admin/oauth2");
		
		// oauth2 params
		cfgDao.add(CONFIG_IGNORE_BAD_SSL, "no", null, 
				"Set \"yes\" or \"no\" to enable/disable ssl certifications checking for OAuth2");

		cfgDao.add(CONFIG_REDIRECT_URL_FOR_EXTERNAL_KEY, "", null,
				"Users entered the room via invitationHash or secureHash will be redirected to this URL on connection lost");
		
		cfgDao.add(CONFIG_FLASH_PROTOCOL, "rtmp", null, "Protocol for flash connections, can be rtmp, rtmpt, rtmpe, rtmps");
		
		cfgDao.add(CONFIG_FLASH_PORT, "1935", null, "Port for flash connections");
		cfgDao.add(CONFIG_CALENDAR_FIRST_DAY, "0", null, "The day that each week begins. The value must be a number that represents the day of the week. Sunday=0, Monday=1, Tuesday=2, etc.");
		
		log.debug("Configurations ADDED");
	}

	public void loadRoomTypes() {
		long conference_Id = roomTypeDao.addRoomType(
				"conference", 1541, false);
		log.debug("conference_Id: " + conference_Id);

		// Audience room type is not in use anymore
		roomTypeDao.addRoomType("audience", -1, true);

		long restricted_Id = roomTypeDao.addRoomType(
				"restricted", 1542, false);
		log.debug("restricted_Id: " + restricted_Id);

		long interview_Id = roomTypeDao.addRoomType(
				"interview", 1543, false);
		log.debug("interview_Id: " + interview_Id);

		// Custom room type is not in use anymore
		roomTypeDao.addRoomType("custom", -2, true);
		log.debug("RoomTypes ADDED");
	}

	private Room createRoom(String name, long typeId, long capacity, boolean isPublic, Long orgId) {
		Room r = new Room();
		r.setName(name);
		r.setComment("");
		r.setStarttime(new Date());
		r.setNumberOfPartizipants(capacity);
		r.setRoomtype(roomTypeDao.get(typeId));
		r.setIspublic(isPublic);
		r.setAllowUserQuestions(true);
		r.setIsAudioOnly(false);
		r.setAllowFontStyles(true);

		r.setAppointment(false);

		r.setIsDemoRoom(false);
		r.setDemoTime(null);

		r.setModerated(false);
		r.setHideTopBar(false);

		r.setDeleted(false);

		r.setIsClosed(false);
		r.setRedirectURL(null);

		r.setOwnerId(null);

		r.setWaitForRecording(false);
		r.setAllowRecording(true);
		
		r.setChatHidden(false);
		r.setActivitiesHidden(false);
		r.setHideActionsMenu(false);
		r.setHideFilesExplorer(false);
		r.setHideScreenSharing(false);	
		r.setHideWhiteboard(false);
		if (orgId != null) {
			RoomOrganisation ro = new RoomOrganisation();
			ro.setRoom(r);
			ro.setOrganisation(organisationDao.get(orgId));
			ro.setStarttime(new Date());
		}
		r = roomDao.update(r, null);
		return r;
	}
	public void loadDefaultRooms(boolean createRooms) {
		if (createRooms) {
			// hardcoded IDs (they are not intended to be changed)
			long conference_Id = 1;
			long restricted_Id = 3;
			long interview_Id = 4;

			createRoom("public Interview Room", interview_Id, 16L, true, null);
			createRoom("public Conference Room", conference_Id, 32L, true, null);
			Room r = createRoom("public Video Only Room", conference_Id, 32L, true, null);
			r.setHideWhiteboard(true);
			roomDao.update(r, null);
			createRoom("public Video And Whiteboard Room", conference_Id, 32L, true, null);
			createRoom("public Restricted Room", restricted_Id, 100L, true, null);
			r = createRoom("restricted room with micro option set", restricted_Id, 100L, true, null);
			r.setShowMicrophoneStatus(true);
			roomDao.update(r, null);

			r = createRoom("conference room with micro option set", conference_Id, 32L, true, null);
			r.setShowMicrophoneStatus(true);
			roomDao.update(r, null);

			createRoom("private Conference Room", conference_Id, 32L, false, 1L);
		}
	}

	public void loadInitUserAndOrganisation(InstallationConfig cfg) throws Exception {
		Long default_lang_id = Long.parseLong(cfg.defaultLangId);

		// Add default group
		Organisation org = new Organisation();
		org.setName(cfg.group);
		org.setInsertedby(1L);
		org.setDeleted(false);
		org.setStarttime(new Date());
		org = organisationDao.update(org, null);

		Set<Right> rights = UserDao.getDefaultRights();
		rights.add(Right.Admin);
		rights.add(Right.Soap);
		Long user_id = userManager.registerUserInit(rights, cfg.username, cfg.password, "lastname"
				, "firstname", cfg.email, new Date() /* age/birthday */, "street", "no", "fax", "zip", 1
				, "town", default_lang_id, false /* sendWelcomeMessage */
				, Arrays.asList(org.getId()), "phone", false, false, timezoneUtil.getTimeZone(cfg.ical_timeZone),
				false /* forceTimeZoneCheck */, "" /* userOffers */, "" /* userSearchs */, false /* showContactData */,
				true /* showContactDataToContacts */, null);

		log.debug("Installation - User Added user-Id " + user_id);

		if (user_id < 0) {
			throw new Exception("Could not add user user returns a negative error message: " + user_id);
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
		File langFile = new File(OmFileHelper.getLanguagesDir(), OmFileHelper.nameOfLanguageFile);
		log.debug("File to load languages from is: " + langFile.getCanonicalPath());
		
		Element root = reader.read(langFile).getRootElement();

		for (@SuppressWarnings("unchecked")
		Iterator<Element> it = root.elementIterator("lang"); it.hasNext();) {
			Element item = it.next();
			String country = item.getText();
			Integer id = Integer.valueOf(item.attribute("id").getValue());

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

		appointmentReminderTypDaoImpl.add(-1L, "do not send notification", 1568);
		appointmentReminderTypDaoImpl.add(-1L, "simple email", 1569);
		appointmentReminderTypDaoImpl.add(-1L, "iCal email", 1570);
	}

	public void loadLanguagesFile(int langId) throws Exception {
		Map<Integer, Map<String, Object>> listlanguages = getLanguageFiles();
		loadLanguagesFile(listlanguages, new Hashtable<Long, Fieldvalues>(3000), langId);
	}

	public void loadLanguagesFile(String langName) throws Exception {
		Map<Integer, Map<String, Object>> listlanguages = getLanguageFiles();
		log.debug("Number of languages found: " + listlanguages.size());
		for (Entry<Integer, Map<String, Object>> me : listlanguages.entrySet()) {
			Map<String, Object> langMap = me.getValue();
			if (langName.equals(langMap.get("name"))) {
				loadLanguagesFile(listlanguages, new Hashtable<Long, Fieldvalues>(3000), me.getKey());
				break;
			}
		}
	}

	public void loadLanguagesFile(Map<Integer, Map<String, Object>> listlanguages, Map<Long, Fieldvalues> fieldCache, int langId)
			throws Exception {
		Map<String, Object> langMap = listlanguages.get(langId);
		log.debug("loadInitLanguages lang: " + langMap);

		String langName = (String) langMap.get("name");
		String rtl = (String) langMap.get("rtl");
		String code = (String) langMap.get("code");

		log.debug("loadInitLanguages rtl from xml: " + rtl);

		boolean langRtl = Boolean.TRUE.toString().equals(rtl);

		long ticks = System.currentTimeMillis();
		FieldLanguage lang = fieldLanguageDaoImpl.addLanguage(langId, langName, langRtl, code);
		if (lang == null) {
			log.error("Failed to create language");
			throw new Exception("Failed to create language");
		}
		SAXReader reader = new SAXReader();
		Document document = reader.read(new File(
				OmFileHelper.getLanguagesDir(), langName + ".xml"));

		Element root = document.getRootElement();

		Map<Long, Fieldlanguagesvalues> flvMap = lang.getLanguageValuesMap();
		for (@SuppressWarnings("rawtypes")
		Iterator it = root.elementIterator("string"); it.hasNext();) {
			Element item = (Element) it.next();
			// log.error(item.getName());

			Long id = Long.valueOf(item.attributeValue("id"));
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
					fv = new Fieldvalues();
					fv.setId(id);
					fv.setStarttime(new Date());
					fv.setName(name);
					fv.setDeleted(false);
					fv = fieldValueDao.update(fv, null);
					fieldCache.put(id, fv);
				} else {
					fv = fieldCache.get(id);
				}
				Fieldlanguagesvalues flv = new Fieldlanguagesvalues();
				flv.setStarttime(new Date());
				flv.setValue(value);
				flv.setLanguage_id(lang.getId());
				flv.setFieldvalues(fv);
				flv.setDeleted(false);
				flvMap.put(id, flv);
			}
		}
		lang.setLanguageValues(flvMap.values());
		fieldLanguageDaoImpl.update(lang);
		log.debug("Lang ADDED: " + lang + "; seconds passed: " + (System.currentTimeMillis() - ticks) / 1000);
	}
	
	public void loadLanguagesFiles() throws Exception {
		Map<Integer, Map<String, Object>> listlanguages = getLanguageFiles();

		Map<Long, Fieldvalues> fieldCache = new Hashtable<Long, Fieldvalues>(3000);
		
		int initProgress = progress;
		int progressDelta = 76 - progress;
		double deltaProgressPerLanguageFile = ((double)progressDelta) / listlanguages.size();
		
		/** Read all languages files */
		for (int langId : listlanguages.keySet()) {
			loadLanguagesFile(listlanguages, fieldCache, langId);
			progress = (int)(initProgress + Math.round(deltaProgressPerLanguageFile * langId));
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
		loadMainMenu();
		progress = 9;
		loadErrorTypes();
		progress = 12;
		loadErrorMappingsFromXML();
		progress = 15;
		loadCountriesFiles();
		progress = 22;
		loadLanguagesFiles();
		progress = 76;
		loadSalutations();
		progress = 78;
		// AppointMent Categories
		loadInitAppointmentCategories();
		progress = 80;
		// Appointment Reminder types
		loadInitAppointmentReminderTypes();
		progress = 82;
		// Appointment poll types
		loadPollTypes();
		progress = 84;
		loadRoomTypes();
		progress = 86;

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
