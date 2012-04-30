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
package org.openmeetings.app.installation;

import java.io.File;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.openmeetings.app.OpenmeetingsVariables;
import org.openmeetings.app.data.basic.Configurationmanagement;
import org.openmeetings.app.data.basic.ErrorManagement;
import org.openmeetings.app.data.basic.FieldLanguageDaoImpl;
import org.openmeetings.app.data.basic.Fieldmanagment;
import org.openmeetings.app.data.basic.Navimanagement;
import org.openmeetings.app.data.basic.dao.OmTimeZoneDaoImpl;
import org.openmeetings.app.data.calendar.daos.AppointmentCategoryDaoImpl;
import org.openmeetings.app.data.calendar.daos.AppointmentReminderTypDaoImpl;
import org.openmeetings.app.data.conference.PollManagement;
import org.openmeetings.app.data.conference.Roommanagement;
import org.openmeetings.app.data.user.Organisationmanagement;
import org.openmeetings.app.data.user.Salutationmanagement;
import org.openmeetings.app.data.user.Statemanagement;
import org.openmeetings.app.data.user.Usermanagement;
import org.openmeetings.app.persistence.beans.basic.OmTimeZone;
import org.openmeetings.utils.ImportHelper;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class ImportInitvalues {

	private static final Logger log = Red5LoggerFactory.getLogger(
			ImportInitvalues.class, OpenmeetingsVariables.webAppRootKey);

	public static final String languageFolderName = "languages/";
	private static final String nameOfLanguageFile = "languages.xml";
	private static final String nameOfCountriesFile = "countries.xml";
	private static final String nameOfTimeZoneFile = "timezones.xml";
	private static final String nameOfErrorFile = "errorvalues.xml";

	@Autowired
	private Configurationmanagement cfgManagement;
	@Autowired
	private Usermanagement userManagement;
	@Autowired
	private Fieldmanagment fieldmanagment;
	@Autowired
	private FieldLanguageDaoImpl fieldLanguageDaoImpl;
	@Autowired
	private Statemanagement statemanagement;
	@Autowired
	private OmTimeZoneDaoImpl omTimeZoneDaoImpl;
	@Autowired
	private Navimanagement navimanagement;
	@Autowired
	private ErrorManagement errorManagement;
	@Autowired
	private Salutationmanagement salutationmanagement;
	@Autowired
	private Organisationmanagement organisationmanagement;
	@Autowired
	private Roommanagement roommanagement;
	@Autowired
	private AppointmentCategoryDaoImpl appointmentCategoryDaoImpl;
	@Autowired
	private AppointmentReminderTypDaoImpl appointmentReminderTypDaoImpl;
	@Autowired
	private PollManagement pollManagement;

	public void loadMainMenu() {

		userManagement.addUserLevel("User", 1);
		userManagement.addUserLevel("Moderator", 2);
		userManagement.addUserLevel("Admin", 3);
		userManagement.addUserLevel("Web-Service (only access via SOAP)", 4);
		log.debug("UserLevels ADDED");

		/*
		 * ######################## Dashboard Menu Points
		 */
		navimanagement.addGlobalStructure("home", 1, 124, false, true, 1,
				"home", "false", 582L);
		navimanagement.addMainStructure("dashboardModuleStartScreen", 1, 290,
				true, false, 1, "Dashboard Module", 1, "false", 1450L);
		navimanagement.addMainStructure("dashboardModuleCalendar", 2, 291,
				true, false, 1, "Module Calendar", 1, "false", 1451L);

		/*
		 * ######################## Recording Menu Points - disabled by default
		 */

		navimanagement.addGlobalStructure("record", 2, 395, false, true, 1,
				"record", "false", 583L);
		navimanagement.addMainStructure("recordModule", 1, 395, true, false, 1,
				"Module Recording Player", 2, "false", 1452L);

		/*
		 * String action, int naviorder, long fieldvalues_id, boolean isleaf,
		 * boolean isopen, long level_id, String name, long global_id
		 */

		/*
		 * ######################## Conference Menu Points
		 */

		navimanagement.addGlobalStructure("meetings", 3, 792, false, true, 1,
				"List of Conference Rooms Global Navi", "false", 793L);

		navimanagement.addMainStructure("conferenceModuleRoomList", 1, 792,
				true, false, 1, "List of Conference Rooms Main Navi", 3,
				"false", 1453L);

		/*
		 * ######################## Event Menu Points
		 */

		navimanagement.addGlobalStructure("events", 4, 3, false, true, 1,
				"List of Event Rooms Global Navi", "true", 585L);

		navimanagement.addMainStructure("eventModuleRoomList", 1, 3, true,
				false, 1, "List of Event Rooms Main Navi", 4, "false", null);

		/*
		 * ######################## Moderation Menu Points - disabled by default
		 */
		navimanagement.addGlobalStructure("moderate", 5, 649L, false, true, 2,
				"Moderation Menu", "true", 660L);

		navimanagement.addMainStructure("moderatorModuleUser", 1, 650, true,
				false, 1, "Moderate Users", 5, "false", null);

		navimanagement.addMainStructure("moderatorModuleRoom", 1, 651, true,
				false, 1, "Moderate Rooms", 5, "false", null);

		/*
		 * ######################## Administration Menu Points
		 */

		navimanagement.addGlobalStructure("admin", 6, 6, false, true, 3,
				"Administration Menu", "false", 586L);

		navimanagement.addMainStructure("adminModuleUser", 14, 125, true,
				false, 2, "Administration of Users", 6, "false", 1454L);

		navimanagement.addMainStructure("adminModuleConnections", 15, 597,
				true, false, 3, "Aministration of Connections", 6, "false",
				1455L);

		navimanagement.addMainStructure("adminModuleOrg", 16, 127, true, false,
				3, "Administration of Organizations", 6, "false", 1456L);

		navimanagement.addMainStructure("adminModuleRoom", 17, 186, true,
				false, 3, "Administration of Rooms", 6, "false", 1457L);

		navimanagement.addMainStructure("adminModuleConfiguration", 18, 263,
				true, false, 3, "Administration of Configuration", 6, "false",
				1458L);

		navimanagement.addMainStructure("adminModuleLanguages", 19, 348, true,
				false, 3, "Administration of Languages", 6, "false", 1459L);

		navimanagement.addMainStructure("adminModuleLDAP", 20, 1103, true,
				false, 3, "Administration of LDAP Configs", 6, "false", 1460L);

		navimanagement.addMainStructure("adminModuleBackup", 21, 367, true,
				false, 3, "Administration of Backups", 6, "false", 1461L);
		log.debug("MainMenu ADDED");

		errorManagement.addErrorType(new Long(1), new Long(322));
		errorManagement.addErrorType(new Long(2), new Long(323));
		log.debug("Error types ADDED");
	}

	public void loadErrorMappingsFromXML(String filePath) throws Exception {

		SAXReader reader = new SAXReader();
		Document document = reader.read(new File(filePath, ImportInitvalues.nameOfErrorFile));

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
		log.debug("Salutations ADDED");
	}

	public void loadConfiguration(InstallationConfig cfg) {
		cfgManagement
				.addConfByKey(
						3,
						"crypt_ClassName",
						cfg.cryptClassName,
						null,
						"This Class is used for Authentification-Crypting. "
								+ "Be carefull what you do here! If you change it while "
								+ "running previous Pass of users will not be workign anymore! "
								+ "for more Information see http://code.google.com/p/openmeetings/wiki/CustomCryptMechanism");

		cfgManagement.addConfByKey(3, "screen_viewer", cfg.screenViewer, null,
				"ScreenViewer Type(0==standard, 1== jrdesktop)");

		cfgManagement.addConfByKey(3, "allow_frontend_register",
				cfg.allowFrontendRegister, null, "");

		cfgManagement.addConfByKey(3, "default_group_id", "1", null, "");

		// this domain_id is the Organisation of users who register through the
		// frontend
		cfgManagement.addConfByKey(3, "default_domain_id", "1", null, "");

		// "smtp.xmlcrm.org"
		cfgManagement.addConfByKey(3, "smtp_server", cfg.smtpServer, null,
				"this is the smtp server to send messages");
		// 25
		cfgManagement.addConfByKey(3, "smtp_port", cfg.smtpPort, null,
				"this is the smtp server port normally 25");
		// "openmeetings@xmlcrm.org"
		cfgManagement.addConfByKey(3, "system_email_addr", cfg.mailReferer, null,
				"all send EMails by the system will have this address");
		// "openmeetings@xmlcrm.org"
		cfgManagement.addConfByKey(3, "email_username", cfg.mailAuthName, null,
				"System auth email username");
		//
		cfgManagement.addConfByKey(3, "email_userpass", cfg.mailAuthPass, null,
				"System auth email password");

		cfgManagement.addConfByKey(3, "mail.smtp.starttls.enable", cfg.mailUseTls,
				null, "Enable TLS 1=true, 0=false");

		cfgManagement.addConfByKey(3, "application.name",
				Configurationmanagement.DEFAULT_APP_NAME, null,
				"Name of the Browser Title window");

		// "1" == "EN"
		cfgManagement.addConfByKey(3, "default_lang_id", cfg.defaultLangId, null,
				"Default System Language ID see language.xml");

		cfgManagement.addConfByKey(3, "swftools_path", cfg.swfPath, null,
				"Path To SWF-Tools");

		cfgManagement.addConfByKey(3, "imagemagick_path", cfg.imageMagicPath, null,
				"Path to ImageMagick tools");

		cfgManagement.addConfByKey(3, "sox_path", cfg.soxPath, null,
				"Path To SoX-Tools");

		cfgManagement.addConfByKey(3, "ffmpeg_path", cfg.ffmpegPath, null,
				"Path To FFMPEG");
		cfgManagement.addConfByKey(3, "office.path", cfg.officePath, null,
				"The path to OpenOffice/LibreOffice (optional) please set this to the real path in case jodconverter is unable to find OpenOffice/LibreOffice installation automatically");
		cfgManagement
			.addConfByKey(
					3,
					"jod.path",
					cfg.jodPath,
					null,
					"The path to JOD library (http://code.google.com/p/jodconverter), configure the path to point to the lib directory of JOD that contains also the jodconverter-core-version.jar");

		
		cfgManagement.addConfByKey(3, "rss_feed1", cfg.urlFeed, null, "Feed URL");

		cfgManagement.addConfByKey(3, "rss_feed2", cfg.urlFeed2, null,
				"Feed URL 2");

		cfgManagement
				.addConfByKey(3, "sendEmailAtRegister", cfg.sendEmailAtRegister,
						null,
						"User get a EMail with their Account data. Values: 0(No) or 1(Yes)");

		cfgManagement
				.addConfByKey(
						3,
						"sendEmailWithVerficationCode",
						cfg.sendEmailWithVerficationCode,
						null,
						"User must activate their account by clicking on the "
								+ "activation-link in the registering Email. Values: 0(No) or 1(Yes) "
								+ "It makes no sense to make this(sendEmailWithVerficationCode) 1(Yes) while "
								+ "sendEmailAtRegister is 0(No) cause you need"
								+ "to send a EMail.");
		cfgManagement
				.addConfByKey(
						3,
						"default_export_font",
						cfg.defaultExportFont,
						null,
						"The Name of the Font used for exporting/render Images from Whiteboard"
								+ "The Font has to exist on the Server which runs Red5");

		cfgManagement.addConfByKey(3, "default.rpc.userid", "" + 1, null,
				"The User-Id of the Control User in OpenMeetings");

        // ***************************************
        // ***************************************
        // red5SIP Integration Coniguration Values
        // ***************************************

        cfgManagement.addConfByKey(3, "red5sip.enable", cfg.red5SipEnable, null,
				"Enable to enable the red5SIP integration ");
        cfgManagement.addConfByKey(3, "red5sip.room_prefix", cfg.red5SipRoomPrefix, null,
				"Enable to enable the red5SIP integration ");
        cfgManagement.addConfByKey(3, "red5sip.exten_context", cfg.red5SipExtenContext, null,
				"Enable to enable the red5SIP integration ");

		// ***************************************
		// ***************************************
		// SIP Applet Configuration Values
		// ***************************************

		cfgManagement.addConfByKey(3, "sip.enable", cfg.sipEnable, null,
				"Enable to load the SIP Applet in the Client and "
						+ "call the SIP Applet whenever you enter a Room");

		cfgManagement.addConfByKey(3, "sip.realm", cfg.sipRealm, null,
				"So called *Domain of the SIP Provider*");

		cfgManagement.addConfByKey(3, "sip.port", cfg.sipPort, null, "SIP Port");

		cfgManagement.addConfByKey(3, "sip.proxyname", cfg.sipProxyName, null,
				"SIP Proxy name (this is the outbound proxy)");

		cfgManagement
				.addConfByKey(3, "sip.tunnel", cfg.sipTunnel, null,
						"SIP Tunnel IP + Port, format domain:port, for example 10.0.0.0:443");

		cfgManagement.addConfByKey(3, "sip.codebase", cfg.sipCodebase, null,
				"The Base-URL to load the Ringtone from");

		cfgManagement.addConfByKey(3, "sip.forcetunnel", cfg.sipForceTunnel, null,
				"Force usage of the tunnel");

		// ***************************************
		// ***************************************
		// OpenXG Configuration Values
		// ***************************************

		cfgManagement.addConfByKey(3, "sip.openxg.enable", cfg.sipOpenxgEnable,
				null,
				"Enable the OpenXG XML-RPC Gateway through the Wrapper URL");

		cfgManagement
				.addConfByKey(
						3,
						"openxg.wrapper.url",
						cfg.openxgWrapperUrl,
						null,
						"openxg_wrapper_url, null, OpenXG XML-RPC Wrapper URL, the Wrapper can only "
								+ "be located on 127.0.01 by default, "
								+ "for example http://127.0.0.1:5080/rpc_client/rpc_gateway_wrapper.php");

		cfgManagement.addConfByKey(3, "openxg.client.id", cfg.openxgClientId,
				null, "OpenXG XML-RPC Client ID");

		cfgManagement.addConfByKey(3, "openxg.client.secret",
				cfg.openxgClientSecret, null, "OpenXG XML-RPC Client Secret");

		cfgManagement.addConfByKey(3, "openxg.client.domain",
				cfg.openxgClientDomain, null, "OpenXG Domain");

		cfgManagement.addConfByKey(3, "openxg.community.code",
				cfg.openxgCommunityCode, null, "OpenXG Community Code");

		cfgManagement.addConfByKey(3, "openxg.language.code",
				cfg.openxgLanguageCode, null, "OpenXG Language Code");

		cfgManagement.addConfByKey(3, "openxg.adminid", cfg.openxgAdminId, null,
				"OpenXG Admin ID");

		// ***************************************
		// ***************************************
		// Phone Range Configuration Values
		// ***************************************

		cfgManagement.addConfByKey(3, "sip.language.phonecode",
				cfg.sipLanguagePhoneCode, null, "For example +358 for Finland");

		cfgManagement
				.addConfByKey(3, "sip.phonerange.start", cfg.sipPhoneRangeStart,
						null,
						"The first number in the Range of Phone Numbers in national format");

		cfgManagement.addConfByKey(3, "sip.phonerange", cfg.sipPhoneRange, null,
				"Amount of numbers in the Phone Range available");

		cfgManagement.addConfByKey(3, "sip.phonerange.currentindex", "" + 0,
				null, "Number of used Phone Numbers in the sip.phonerange");

		cfgManagement
				.addConfByKey(3, "sip.phonerange.conference.currentindex",
						"" + 0, null,
						"Number of used Phone Numbers in the sip.phonerange for the conferences");

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

		cfgManagement.addConfByKey(3, "default.quality.screensharing", "1",
						null,
						"Default selection in ScreenSharing Quality:\n 0 - bigger frame rate, no resize\n 1 - no resize\n 2 - size == 1/2 of selected area\n 3 - size == 3/8 of selected area");

		cfgManagement.addConfByKey(3, "default.dashboard.tab", "0", null,
				"Default selection in Dashboard tabs as tab-index-id");

		cfgManagement.addConfByKey(3, "dashboard.show.myrooms", "1", null,
				"Show My Rooms Tab");

		cfgManagement.addConfByKey(3, "dashboard.show.chat", "1", null,
				"Show Chat Tab");

		cfgManagement.addConfByKey(3, "dashboard.show.rssfeed", "0", null,
				"Show RSS Tab");

		cfgManagement
				.addConfByKey(
						3,
						"show.whiteboard.draw.status",
						"0",
						null,
						"Display name of the user who draw the current object (User Name auto-disapper after 3 seconds.");

		cfgManagement.addConfByKey(3, "max_upload_size", new Integer(
				ImportHelper.DEFAULT_MAX_UPLOAD_SIZE).toString(), null,
				"Maximum size of upload file (bytes)"); // defaults to 1GB

		cfgManagement
				.addConfByKey(
						3,
						"number.minutes.reminder.send",
						"15",
						null,
						"The number of minutes before reminder emails are send. Set to 0 to disable reminder emails");

		cfgManagement.addConfByKey(3, "user.login.minimum.length", "" + InstallationConfig.USER_LOGIN_MINIMUM_LENGTH, null,
				"Number of chars needed in a user login");

		cfgManagement.addConfByKey(3, "user.pass.minimum.length", "" + InstallationConfig.USER_PASSWORD_MINIMUM_LENGTH, null,
				"Number of chars needed in a user login");

		cfgManagement
				.addConfByKey(3, "calendar.conference.rooms.default.size",
						"50", null,
						"Default number of participants conference room created via calendar");

		cfgManagement
				.addConfByKey(
						3,
						"use.old.style.ffmpeg.map.option",
						"0",
						null,
						"specify a 1 if you would like to use old FFMPEG -map option with 0.0 instead of 0:0");

		// give exclusive audio key code
		cfgManagement
				.addConfByKey(
						3,
						"exclusive.audio.keycode",
						"123",
						null,
						"A hot key code for the 'give exclusive audio' functionality. Keycode 123 is F12");

		// system-wide ldap params
		cfgManagement.addConfByKey(
						3, 
						"ldap_default_id", 
						"0", 
						null,
						"Ldap domain selected by default in the login screen");
		
		// set inviter's email address as ReplyTo in email invitations
		cfgManagement.addConfByKey(
						3, 
						"inviter.email.as.replyto", 
						cfg.replyToOrganizer, 
						null,
						"Set inviter's email address as ReplyTo in email invitations (1 == set, 0 == NOT set)");
		
		log.debug("Configuration ADDED");
	}

	public void loadRoomTypes() {
		long conference_Id = roommanagement
				.addRoomType("conference (1-25 users)", false);
		log.debug("conference_Id: " + conference_Id);
		
		//Audience room type is not in use anymore
		roommanagement.addRoomType("audience (1-50 users)", true);

		long restricted_Id = roommanagement
				.addRoomType("restricted  (1-150 users)", false);
		log.debug("restricted_Id: " + restricted_Id);

		long interview_Id = roommanagement
				.addRoomType("interview  (1:1 meeting with recording)", false);
		log.debug("interview_Id: " + interview_Id);
		
		//Custom room type is not in use anymore
		roommanagement.addRoomType("custom (extension point for your plugin)", true);
		log.debug("RoomTypes ADDED");
	}
	
	public void loadDefaultRooms(boolean createRooms) {
		if (createRooms) {
			//hardcoded IDs (they are not intended to be changed)
			long conference_Id = 1;
			long restricted_Id = 3;
			long interview_Id = 4;
			
			roommanagement.addRoom(3, "public Interview Room", interview_Id,
					"", new Long(16), true, null, false, false, null, false,
					null, true, false, false, "", "", "", null, null, null,
					false, // hideTopBar
					false, // hideChat
					false, // hideActivitiesAndActions
					false, // hideFilesExplorer
					false, // hideActionsMenu
					false, // hideScreenSharing
					false, // hideWhiteboard
					false // showMicrophoneStatus
					);

			roommanagement.addRoom(3, "public Conference Room", conference_Id,
					"", new Long(32), true, null, false, false, null, false,
					null, true, false, false, "", "", "", null, null, null,
					false, // hideTopBar
					false, // hideChat
					false, // hideActivitiesAndActions
					false, // hideFilesExplorer
					false, // hideActionsMenu
					false, // hideScreenSharing
					false, // hideWhiteboard
					false // showMicrophoneStatus
					);

			roommanagement.addRoom(3, "public Video Only Room", conference_Id,
					"", new Long(32), true, null, false, false, null, false,
					null, true, false, false, "", "", "", null, null, null,
					false, // hideTopBar
					false, // hideChat
					false, // hideActivitiesAndActions
					false, // hideFilesExplorer
					false, // hideActionsMenu
					false, // hideScreenSharing
					false, // hideWhiteboard
					false // showMicrophoneStatus
					);

			roommanagement.addRoom(3, "public Video And Whiteboard Room",
					conference_Id, "", new Long(32), true, null, false, false,
					null, false, null, true, false, false, "", "", "", null,
					null, null, false, // hideTopBar
					false, // hideChat
					false, // hideActivitiesAndActions
					false, // hideFilesExplorer
					false, // hideActionsMenu
					false, // hideScreenSharing
					false, // hideWhiteboard
					false // showMicrophoneStatus
					);

			roommanagement.addRoom(3, "public Restricted Room", restricted_Id,
					"", new Long(100), true, null, false, false, null, false,
					null, true, false, false, "", "", "", null, null, null,
					false, // hideTopBar
					false, // hideChat
					false, // hideActivitiesAndActions
					false, // hideFilesExplorer
					false, // hideActionsMenu
					false, // hideScreenSharing
					false, // hideWhiteboard
					false // showMicrophoneStatus
					);

			roommanagement.addRoom(3, "restricted room with micro option set",
					restricted_Id, "", new Long(100), true, null, false, false,
					null, false, null, true, false, false, "", "", "", null,
					null, null, false, // hideTopBar
					false, // hideChat
					false, // hideActivitiesAndActions
					false, // hideFilesExplorer
					false, // hideActionsMenu
					false, // hideScreenSharing
					false, // hideWhiteboard
					true // showMicrophoneStatus
					);

			roommanagement.addRoom(3, "conference room with micro option set",
					conference_Id, "", new Long(32), true, null, false, false,
					null, false, null, true, false, false, "", "", "", null,
					null, null, false, // hideTopBar
					false, // hideChat
					false, // hideActivitiesAndActions
					false, // hideFilesExplorer
					false, // hideActionsMenu
					false, // hideScreenSharing
					false, // hideWhiteboard
					true // showMicrophoneStatus
					);

			long room2 = roommanagement.addRoom(3, "private Conference Room",
					conference_Id, "", new Long(32), false, null, false, false,
					null, false, null, true, false, false, "", "", "", null,
					null, null, false, // hideTopBar
					false, // hideChat
					false, // hideActivitiesAndActions
					false, // hideFilesExplorer
					false, // hideActionsMenu
					false, // hideScreenSharing
					false, // hideWhiteboard
					false // showMicrophoneStatus
					);

			roommanagement.addRoomToOrganisation(3, room2, 1);
			
		}
	}

	public void loadInitUserAndOrganisation(String username, String userpass,
			String email, String defaultOrganisationName, String ical_timeZone,
			String configdefaultLang) {
		// Add user
		try {

			Long default_lang_id = Long.parseLong(configdefaultLang);
			if (default_lang_id == null)
				default_lang_id = 1L;

			// Add default group
			Long organisation_id = organisationmanagement.addOrganisation(
					defaultOrganisationName, 1);

			// BaseUrl as param is empty as we do not send an EMAIL here
			Long user_id = userManagement.registerUserInit(new Long(3), 3, 1,
					1, username, userpass, "lastname", "firstname", email,
					new java.util.Date(), "street", "no", "fax", "zip", 1,
					"town", default_lang_id, false,
					Arrays.asList(organisation_id), "phone", "", false, "", "",
					"", false, omTimeZoneDaoImpl.getOmTimeZoneByIcal(ical_timeZone), false, "", "", false, true);

			log.debug("Installation - User Added user-Id " + user_id);

			if (user_id < 0) {
				throw new Exception(
						"Could not add user user returns a negative error message: "
								+ user_id);
			}
		} catch (Exception e) {
			log.error("[loadInitUserAndOrganisation] ", e);
		}
	}

	/**
	 * import all language Names from the xml file
	 * 
	 * @param filePath
	 * @throws Exception
	 */
	private void loadCountriesFiles(String filePath) throws Exception {
		SAXReader reader = new SAXReader();
		Document document = reader.read(new File(filePath, ImportInitvalues.nameOfCountriesFile));

		Element root = document.getRootElement();

		for (@SuppressWarnings("rawtypes")
		Iterator it = root.elementIterator("country"); it.hasNext();) {
			Element item = (Element) it.next();
			String country = item.attributeValue("name");

			statemanagement.addState(country);
		}
		log.debug("Countries ADDED");
	}

	private void loadTimeZoneFiles(String filePath) throws Exception {
		SAXReader reader = new SAXReader();
		Document document = reader.read(new File(filePath, ImportInitvalues.nameOfTimeZoneFile));

		Element root = document.getRootElement();

		for (@SuppressWarnings("rawtypes")
		Iterator it = root.elementIterator("timezone"); it.hasNext();) {
			Element item = (Element) it.next();
			String timeZoneName = item.attributeValue("name");
			String timeZoneLabel = item.attributeValue("label");
			String iCal = item.attributeValue("iCal");
			Integer orderId = Integer.valueOf(item.attributeValue("orderId"));

			omTimeZoneDaoImpl.addOmTimeZone(timeZoneName, timeZoneLabel, iCal,
					orderId);
		}
		log.debug("TimeZones ADDED");
	}

	public List<OmTimeZone> getTimeZones(String filePath) throws Exception {
		log.debug(":: getTimeZones ::");

		List<OmTimeZone> omTimeZones = new LinkedList<OmTimeZone>();

		SAXReader reader = new SAXReader();
		Document document = reader.read(new File(filePath, ImportInitvalues.nameOfTimeZoneFile));

		Element root = document.getRootElement();

		for (@SuppressWarnings("rawtypes")
		Iterator it = root.elementIterator("timezone"); it.hasNext();) {
			Element item = (Element) it.next();
			String timeZoneName = item.attributeValue("name");
			String timeZoneLabel = item.attributeValue("label");
			Integer orderId = Integer.valueOf(item.attributeValue("orderId"));
			String iCal = item.attributeValue("iCal");

			OmTimeZone omTimeZone = new OmTimeZone();
			omTimeZone.setIcal(iCal);
			omTimeZone.setJname(timeZoneName);
			omTimeZone.setLabel(timeZoneLabel);
			omTimeZone.setOrderId(orderId);

			omTimeZones.add(omTimeZone);
		}
		
		return omTimeZones;
	}

	/**
	 * load all availible languages File names and language name's from the
	 * config file
	 * 
	 * @param filePath
	 * @return
	 * @throws Exception
	 */
	public LinkedHashMap<Integer, LinkedHashMap<String, Object>> getLanguageFiles(
			String filePath) throws Exception {

		LinkedHashMap<Integer, LinkedHashMap<String, Object>> languages = new LinkedHashMap<Integer, LinkedHashMap<String, Object>>();

		SAXReader reader = new SAXReader();
		Document document = reader.read(new File(filePath, ImportInitvalues.nameOfLanguageFile));

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
		log.debug("Languages ADDED ");
		return languages;
	}

	/**
	 * @author o.becherer initial fillment of Appointmentcategories
	 */
	// ------------------------------------------------------------------------------
	public void loadInitAppointmentCategories() {
		log.debug("ImportInitValues.loadInitAppointmentCategories");

		try {
			appointmentCategoryDaoImpl.addAppointmentCategory(new Long(-1),
					"default", "default");
		} catch (Exception e) {
			log.error("Could not create AppointMentcategories");
			return;
		}

	}

	// ------------------------------------------------------------------------------

	/**
	 * @author o.becherer initial fillment of AppointMentReminderTypes
	 */
	// ------------------------------------------------------------------------------
	public void loadInitAppointmentReminderTypes() {

		log.debug("ImportInitValues.loadInitAppointmentReminderTypes");

		try {
			appointmentReminderTypDaoImpl.addAppointmentReminderTyps(-1L,
					"do not send notification");
			appointmentReminderTypDaoImpl.addAppointmentReminderTyps(-1L,
					"simple email");
			appointmentReminderTypDaoImpl.addAppointmentReminderTyps(-1L,
					"iCal email");

		} catch (Exception e) {
			log.error("Could not create ReminderType");
			return;
		}
	}

	// ------------------------------------------------------------------------------

	/**
	 * Loading initial Language from xml Files into database
	 */
	// ------------------------------------------------------------------------------
	public void loadInitLanguages(String filePath) throws Exception {

		loadCountriesFiles(filePath);

		loadTimeZoneFiles(filePath);

		LinkedHashMap<Integer, LinkedHashMap<String, Object>> listlanguages = this
				.getLanguageFiles(filePath);

		boolean langFieldIdIsInited = false;

		/** Read all languages files */
		for (Iterator<Integer> itLang = listlanguages.keySet().iterator(); itLang
				.hasNext();) {
			Integer langId = itLang.next();
			LinkedHashMap<String, Object> lang = listlanguages.get(langId);
			log.debug("loadInitLanguages lang: " + lang);

			String langName = (String) lang.get("name");
			String rtl = (String) lang.get("rtl");
			String code = (String) lang.get("code");

			log.debug("loadInitLanguages rtl from xml: " + rtl);

			Boolean langRtl = false;

			if (rtl != null && rtl.equals("true"))
				langRtl = true;

			Long languages_id = fieldLanguageDaoImpl.addLanguage(langName,
					langRtl, code);

			SAXReader reader = new SAXReader();
			Document document = reader.read(new File(filePath, langName + ".xml"));

			Element root = document.getRootElement();

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

				// Only do that for the first field-set
				if (!langFieldIdIsInited) {
					fieldmanagment.addField(name);
				}

				fieldmanagment.addFieldValueByFieldAndLanguage(id,
						languages_id, value);

			}
			log.debug("Lang ADDED: " + lang);
			if (!langFieldIdIsInited)
				langFieldIdIsInited = true;
		}

	}

	// ------------------------------------------------------------------------------

	/**
	 * Loading initial Language from xml Files into database
	 */
	// ------------------------------------------------------------------------------
	public void loadPollTypes() {
		pollManagement.addPollType(26L, false);
		pollManagement.addPollType(27L, true);
	}
	// ------------------------------------------------------------------------------

	public void loadSystem(String filePath, InstallationConfig cfg) throws Exception {
		loadMainMenu();
		loadErrorMappingsFromXML(filePath);
		loadInitLanguages(filePath);
		loadSalutations();
		// AppointMent Categories
		loadInitAppointmentCategories();
		// Appointment Reminder types
		loadInitAppointmentReminderTypes();
		// Appointment poll types
		loadPollTypes();
		loadRoomTypes();
		
		loadConfiguration(cfg);
	}
	
	public void loadAll(String filePath, InstallationConfig cfg, String username,
			String userpass, String useremail, String groupame, String timeZone) throws Exception {
		
		loadSystem(filePath, cfg);
		loadInitUserAndOrganisation(username,
				userpass, useremail, groupame, timeZone, cfg.defaultLangId);
		
		loadDefaultRooms("1".equals(cfg.createDefaultRooms));
	}
}
