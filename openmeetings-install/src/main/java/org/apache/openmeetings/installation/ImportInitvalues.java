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

import static org.apache.openmeetings.db.dao.basic.ConfigurationDao.DEFAULT_APP_NAME;
import static org.apache.openmeetings.db.dao.basic.ConfigurationDao.DEFAULT_MAX_UPLOAD_SIZE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_APPLICATION_BASE_URL;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_APPLICATION_NAME;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_APPOINTMENT_REMINDER_MINUTES;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_CALENDAR_FIRST_DAY;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_CRYPT_KEY;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DASHBOARD_SHOW_MYROOMS_KEY;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DASHBOARD_SHOW_RSS_KEY;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DEFAULT_GROUP_ID;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DEFAULT_LANDING_ZONE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DEFAULT_LANG_KEY;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DEFAULT_LDAP_ID;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_FRONTEND_REGISTER_KEY;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_GOOGLE_ANALYTICS_CODE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_IGNORE_BAD_SSL;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_LOGIN_MIN_LENGTH_KEY;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_MAX_UPLOAD_SIZE_KEY;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_OAUTH_REGISTER_KEY;
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
import java.util.Date;
import java.util.Iterator;

import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dao.basic.ErrorDao;
import org.apache.openmeetings.db.dao.basic.NavigationDao;
import org.apache.openmeetings.db.dao.label.LabelDao;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.dao.room.SipDao;
import org.apache.openmeetings.db.dao.server.OAuth2Dao;
import org.apache.openmeetings.db.dao.user.GroupDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.basic.ErrorValue;
import org.apache.openmeetings.db.entity.basic.Naviglobal;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.room.Room.Type;
import org.apache.openmeetings.db.entity.room.RoomGroup;
import org.apache.openmeetings.db.entity.server.OAuthServer;
import org.apache.openmeetings.db.entity.server.OAuthServer.RequestMethod;
import org.apache.openmeetings.db.entity.user.Group;
import org.apache.openmeetings.db.entity.user.GroupUser;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.entity.user.User.Right;
import org.apache.openmeetings.util.OmFileHelper;
import org.apache.wicket.util.string.StringValue;
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
	private UserDao userDao;
	@Autowired
	private NavigationDao navimanagement;
	@Autowired
	private ErrorDao errorDao;
	@Autowired
	private SipDao sipDao;
	@Autowired
	private OAuth2Dao oauthDao;
	@Autowired
	private GroupDao groupDao;
	@Autowired
	private RoomDao roomDao;
	@Autowired
	private LabelDao labelDao;
	
	private int progress = 0;

	public int getProgress() {
		return progress;
	}
	
	public void loadMainMenu() {
		/*
		 * ######################## Dashboard Menu Points
		 */
		Naviglobal home = navimanagement.addGlobalStructure(1, "124", 1, "home", "582");
		navimanagement.addMainStructure("dashboardModuleStartScreen", null, 1, "290", 1, "Dashboard", home.getId(), "1450");
		navimanagement.addMainStructure("dashboardModuleCalendar", null, 2, "291", 1, "Calendar", home.getId(), "1451");

		/*
		 * ######################## Conference Menu Points
		 */
		Naviglobal rooms = navimanagement.addGlobalStructure(2, "792", 1, "Conference Rooms", "793");
		navimanagement.addMainStructure("conferenceModuleRoomList", "publicTabButton", 1, "777", 1, "Public Rooms", rooms.getId(), "1506");
		navimanagement.addMainStructure("conferenceModuleRoomList", "privateTabButton", 2, "779", 1, "Private Rooms", rooms.getId(), "1507");
		navimanagement.addMainStructure("conferenceModuleRoomList", "myTabButton", 3, "781", 1, "My Rooms", rooms.getId(), "1508");

		/*
		 * ######################## Recording Menu Points
		 */
		Naviglobal rec = navimanagement.addGlobalStructure(3, "395", 1, "record", "583");
		navimanagement.addMainStructure("recordModule", null, 1, "395", 1, "Recordings", rec.getId(), "1452");

		/*
		 * ######################## Administration Menu Points
		 */
		Naviglobal admin = navimanagement.addGlobalStructure(6, "6", 3, "Administration Menu", "586");
		navimanagement.addMainStructure("adminModuleUser", null, 14, "125", 2, "Administration of Users", admin.getId(), "1454");
		navimanagement.addMainStructure("adminModuleConnections", null, 15, "597", 3, "Aministration of Connections", admin.getId(), "1455");
		navimanagement.addMainStructure("adminModuleOrg", null, 16, "127", 3, "Administration of Groups", admin.getId(), "1456");
		navimanagement.addMainStructure("adminModuleRoom", null, 17, "186", 3, "Administration of Rooms", admin.getId(), "1457");
		navimanagement.addMainStructure("adminModuleConfiguration", null, 18, "263", 3, "Administration of Configuration", admin.getId(), "1458");
		navimanagement.addMainStructure("adminModuleLanguages", null, 19, "348", 3, "Administration of Languages", admin.getId(), "1459");
		navimanagement.addMainStructure("adminModuleLDAP", null, 20, "1103", 3, "Administration of LDAP Configs", admin.getId(), "1460");
		navimanagement.addMainStructure("adminModuleOAuth", null, 21, "1571", 3, "Administration of OAuth2 servers", admin.getId(), "1572");
		navimanagement.addMainStructure("adminModuleBackup", null, 22, "367", 3, "Administration of Backups", admin.getId(), "1461");
		navimanagement.addMainStructure("adminModuleServers", null, 23, "1498", 3, "Administration of Servers", admin.getId(), "1499");
		navimanagement.addMainStructure("adminModuleEmail", null, 24, "main.menu.admin.email", 3, "Administration of Emails", admin.getId(), "main.menu.admin.email.desc");
		log.debug("MainMenu ADDED");
	}
	
	public void loadErrorMappingsFromXML() throws Exception {
		SAXReader reader = new SAXReader();
		Document document = reader.read(new File(OmFileHelper.getLanguagesDir(), OmFileHelper.nameOfErrorFile));

		Element root = document.getRootElement();

		for (@SuppressWarnings("unchecked")
		Iterator<Element> it = root.elementIterator("row"); it.hasNext();) {

			Element row = it.next();

			Long errorvalueId = null;
			Long labelId = null;
			ErrorValue.Type type = null;

			for (@SuppressWarnings("unchecked")
			Iterator<Element> itSub = row.elementIterator("field"); itSub.hasNext();) {
				Element field = itSub.next();

				String name = field.attributeValue("name");
				String text = field.getText();
				// System.out.println("NAME | TEXT "+name+" | "+text);
				if (name.equals("errorvalues_id")) {
					errorvalueId = Long.valueOf(text);
				}
				if (name.equals("fieldvalues_id")) {
					labelId = Long.valueOf(text);
				}
				if (name.equals("type")) {
					type = ErrorValue.Type.valueOf(text);
				}
			}

			errorDao.addErrorValues(errorvalueId, type, labelId);
		}
		log.debug("ErrorMappings ADDED");
	}

	public void loadConfiguration(InstallationConfig cfg) {
		cfgDao.add(CONFIG_CRYPT_KEY, cfg.cryptClassName, null,
				"This Class is used for Authentification-Crypting. "
						+ "Be carefull what you do here! If you change it while "
						+ "running previous Pass of users will not be workign anymore! "
						+ "for more Information see http://openmeetings.apache.org/CustomCryptMechanism.html");

		cfgDao.add(CONFIG_FRONTEND_REGISTER_KEY, cfg.allowFrontendRegister, null, "Is user register available on login screen");
		cfgDao.add(CONFIG_SOAP_REGISTER_KEY, "1", null, "Is user register available via SOAP/REST");
		cfgDao.add(CONFIG_OAUTH_REGISTER_KEY, "1", null, "Is user register available via OAuth");
		// this group_id is the Group of users who register through the frontend or SOAP
		cfgDao.add(CONFIG_DEFAULT_GROUP_ID, "1", null, "");

		cfgDao.add("smtp_server", cfg.smtpServer, null,
				"this is the smtp server to send messages");

		cfgDao.add("smtp_port", "" + cfg.smtpPort, null,
				"this is the smtp server port normally 25");

		cfgDao.add("system_email_addr", cfg.mailReferer, null, "all send e-mails by the system will have this address");

		cfgDao.add("email_username", cfg.mailAuthName, null, "System auth email username");

		cfgDao.add("email_userpass", cfg.mailAuthPass, null, "System auth email password");

		cfgDao.add("mail.smtp.starttls.enable", cfg.mailUseTls, null, "Enable TLS 1=true, 0=false");
		
		cfgDao.add("mail.smtp.connection.timeout", "30000", null,
				"Socket connection timeout value in milliseconds. Default is 30 seconds (30000).");
		
		cfgDao.add("mail.smtp.timeout", "30000", null,
				"Socket I/O timeout value in milliseconds. Default is 30 seconds (30000).");

		cfgDao.add(CONFIG_APPLICATION_NAME, DEFAULT_APP_NAME, null, "Name of the Browser Title window");

		// "1" == "EN"
		cfgDao.add(CONFIG_DEFAULT_LANG_KEY, cfg.defaultLangId, null, "Default System Language ID see languages.xml");

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

		cfgDao.add(CONFIG_DEFAULT_LANDING_ZONE, "user/dashboard", null
				, "Area to be shown to the user after login. Possible values are: "
					+ "user/dashboard, user/calendar, user/record, rooms/my, rooms/group, rooms/public, admin/user, admin/connection"
					+ ", admin/group, admin/room, admin/config, admin/lang, admin/ldap, admin/backup, admin/server, admin/oauth2");
		
		// oauth2 params
		cfgDao.add(CONFIG_IGNORE_BAD_SSL, "no", null, 
				"Set \"yes\" or \"no\" to enable/disable ssl certifications checking for OAuth2");

		cfgDao.add(CONFIG_REDIRECT_URL_FOR_EXTERNAL_KEY, "", null,
				"Users entered the room via invitationHash or secureHash will be redirected to this URL on connection lost");
		cfgDao.add(CONFIG_CALENDAR_FIRST_DAY, "0", null, "The day that each week begins. The value must be a number that represents the day of the week. Sunday=0, Monday=1, Tuesday=2, etc.");
		cfgDao.add(CONFIG_GOOGLE_ANALYTICS_CODE, null, null, "Code for Google Analytics");
		
		log.debug("Configurations ADDED");
	}

	private Room createRoom(String name, Room.Type type, long capacity, boolean isPublic, Long groupId) {
		Room r = new Room();
		r.setName(name);
		r.setComment("");
		r.setInserted(new Date());
		r.setNumberOfPartizipants(capacity);
		r.setType(type);
		r.setIspublic(isPublic);
		r.setAllowUserQuestions(true);
		r.setAudioOnly(false);
		r.setAllowFontStyles(true);

		r.setAppointment(false);

		r.setDemoRoom(false);
		r.setDemoTime(null);

		r.setModerated(false);
		r.setHideTopBar(false);

		r.setDeleted(false);

		r.setClosed(false);
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
		if (groupId != null) {
			RoomGroup ro = new RoomGroup();
			ro.setRoom(r);
			ro.setGroup(groupDao.get(groupId));
			ro.setInserted(new Date());
			r.getRoomGroups().add(ro);
		}
		r = roomDao.update(r, null);
		return r;
	}
	
	public void loadDefaultRooms(boolean createRooms, long langId) {
		if (createRooms) {
			createRoom(labelDao.getString("install.room.public.interview", langId), Type.interview, 16L, true, null);
			createRoom(labelDao.getString("install.room.public.conference", langId), Type.conference, 32L, true, null);
			Room r = createRoom(labelDao.getString("install.room.public.video.only", langId), Type.conference, 32L, true, null);
			r.setHideWhiteboard(true);
			roomDao.update(r, null);
			createRoom(labelDao.getString("install.room.public.video.wb", langId), Type.conference, 32L, true, null);
			createRoom(labelDao.getString("install.room.public.restricted", langId), Type.restricted, 100L, true, null);
			r = createRoom(labelDao.getString("install.room.restricted.micro", langId), Type.restricted, 100L, true, null);
			r.setShowMicrophoneStatus(true);
			roomDao.update(r, null);

			r = createRoom(labelDao.getString("install.room.conference.micro", langId), Type.conference, 32L, true, null);
			r.setShowMicrophoneStatus(true);
			roomDao.update(r, null);

			createRoom(labelDao.getString("install.room.private.conference", langId), Type.conference, 32L, false, 1L);
		}
	}

	public void loadInitUserAndGroup(InstallationConfig cfg) throws Exception {
		// Add default group
		Group org = new Group();
		org.setName(cfg.group);
		org.setInsertedby(1L);
		org.setDeleted(false);
		org.setInserted(new Date());
		org = groupDao.update(org, null);

		User u = userDao.getNewUserInstance(null);
		u.setType(User.Type.user);
		u.getRights().add(Right.Admin);
		u.getRights().add(Right.Soap);
		u.setLogin(cfg.username);
		u.setFirstname("firstname");
		u.setLastname("lastname");
		u.getAddress().setEmail(cfg.email);
		u.getGroupUsers().add(new GroupUser(org));

		u = userDao.update(u, cfg.password, -1L);

		log.debug("Installation - User Added user-Id " + u.getId());

		if (u.getId() == null) {
			throw new Exception("Unable to add user");
		}
	}

	public void loadInitialOAuthServers() throws Exception {
		// Yandex
		OAuthServer yandexServer = new OAuthServer();
		yandexServer.setName("Yandex");
		yandexServer.setIconUrl("http://yandex.st/morda-logo/i/favicon.ico");
		yandexServer.setClientId("<put your client_id>");
		yandexServer.setClientSecret("<put your client_secret>");
		yandexServer.setEnabled(false);
		yandexServer.setRequestInfoUrl("https://login.yandex.ru/info?format=json&oauth_token={$access_token}");
		yandexServer.setRequestTokenUrl("https://oauth.yandex.ru/token");
		yandexServer.setRequestKeyUrl("https://oauth.yandex.ru/authorize?response_type=code&client_id={$client_id}");
		yandexServer.setRequestTokenAttributes("grant_type=authorization_code&code={$code}&client_id={$client_id}&client_secret={$client_secret}");
		yandexServer.setRequestTokenMethod(RequestMethod.POST);
		yandexServer.setLoginParamName("login");
		yandexServer.setEmailParamName("default_email");
		yandexServer.setFirstnameParamName("first_name");
		yandexServer.setLastnameParamName("last_name");
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
		facebookServer.setRequestTokenUrl("https://graph.facebook.com/v2.3/oauth/access_token");
		facebookServer.setRequestTokenMethod(RequestMethod.POST);
		facebookServer.setRequestTokenAttributes("client_id={$client_id}&redirect_uri={$redirect_uri}&client_secret={$client_secret}&code={$code}");
		facebookServer.setRequestInfoUrl("https://graph.facebook.com/me?access_token={$access_token}&fields=id,first_name,last_name,email");
		facebookServer.setLoginParamName("id");
		facebookServer.setEmailParamName("email");
		facebookServer.setFirstnameParamName("first_name");
		facebookServer.setLastnameParamName("last_name");
		oauthDao.update(facebookServer, null);
	}
	
	// ------------------------------------------------------------------------------

	public void loadSystem(InstallationConfig cfg, boolean force) throws Exception {
		// FIXME dummy check if installation was performed before
		if (!force && userDao.count() > 0) {
			log.debug("System contains users, no need to install data one more time.");
		}
		sipDao.delete();
		progress = 14;
		loadMainMenu();
		progress = 28;
		loadErrorMappingsFromXML();
		progress = 42;
		loadConfiguration(cfg);
		progress = 56;
		loadInitialOAuthServers();
		progress = 70;
	}

	public void loadAll(InstallationConfig cfg, boolean force) throws Exception {
		// FIXME dummy check if installation was performed before
		if (!force && userDao.count() > 0) {
			log.debug("System contains users, no need to install data one more time.");
			return;
		}
		loadSystem(cfg, force);
		loadInitUserAndGroup(cfg);
		progress = 84;

		loadDefaultRooms("1".equals(cfg.createDefaultRooms), StringValue.valueOf(cfg.defaultLangId).toLong(1L));
		progress = 100;
	}
}
