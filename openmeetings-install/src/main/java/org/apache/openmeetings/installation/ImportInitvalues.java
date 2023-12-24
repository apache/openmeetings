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

import static org.apache.openmeetings.db.dao.user.UserDao.getNewUserInstance;
import static org.apache.openmeetings.db.dto.user.OAuthUser.PARAM_EMAIL;
import static org.apache.openmeetings.db.dto.user.OAuthUser.PARAM_FNAME;
import static org.apache.openmeetings.db.dto.user.OAuthUser.PARAM_LNAME;
import static org.apache.openmeetings.db.dto.user.OAuthUser.PARAM_LOGIN;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_APPLICATION_BASE_URL;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_APPLICATION_NAME;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_APPOINTMENT_PRE_START_MINUTES;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_APPOINTMENT_REMINDER_MINUTES;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_AUTO_OPEN_SHARING;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_CALENDAR_ROOM_CAPACITY;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_CAM_FPS;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_CHAT_SEND_ON_ENTER;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_CRYPT;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_CSP_ENABLED;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_CSP_FONT;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_CSP_FRAME;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_CSP_IMAGE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_CSP_MEDIA;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_CSP_SCRIPT;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_CSP_STYLE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DASHBOARD_RSS_FEED1;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DASHBOARD_RSS_FEED2;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DASHBOARD_SHOW_CHAT;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DASHBOARD_SHOW_MYROOMS;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DASHBOARD_SHOW_RSS;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DEFAULT_GROUP_ID;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DEFAULT_LANDING_ZONE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DEFAULT_LANG;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DEFAULT_LDAP_ID;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DEFAULT_TIMEZONE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DISPLAY_NAME_EDITABLE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DOCUMENT_DPI;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DOCUMENT_QUALITY;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_EMAIL_AT_REGISTER;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_EMAIL_VERIFICATION;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_EXT_PROCESS_TTL;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_FNAME_MIN_LENGTH;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_GOOGLE_ANALYTICS_CODE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_IGNORE_BAD_SSL;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_KEYCODE_ARRANGE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_KEYCODE_ARRANGE_RESIZE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_KEYCODE_MUTE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_KEYCODE_MUTE_OTHERS;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_KEYCODE_QUICKPOLL;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_LNAME_MIN_LENGTH;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_LOGIN_MIN_LENGTH;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_MAX_UPLOAD_SIZE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_MIC_ECHO;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_MIC_NOISE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_MIC_RATE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_MP4_AUDIO_BITRATE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_MP4_AUDIO_RATE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_MP4_VIDEO_PRESET;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_MYROOMS_ENABLED;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_PASS_CHECK_DIGIT;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_PASS_CHECK_SPECIAL;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_PASS_CHECK_UPPER;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_PASS_MIN_LENGTH;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_PATH_FFMPEG;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_PATH_IMAGEMAGIC;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_PATH_OFFICE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_PATH_SOX;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_RECORDING_ENABLED;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_REDIRECT_URL_FOR_EXTERNAL;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_REGISTER_FRONTEND;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_REGISTER_OAUTH;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_REGISTER_SOAP;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_REMINDER_MESSAGE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_REPLY_TO_ORGANIZER;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_REST_ALLOW_ORIGIN;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_SCREENSHARING_ALLOW_REMOTE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_SCREENSHARING_FPS;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_SCREENSHARING_FPS_SHOW;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_SCREENSHARING_QUALITY;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_SIP_ENABLED;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_SIP_EXTEN_CONTEXT;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_SIP_ROOM_PREFIX;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_SMTP_PASS;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_SMTP_PORT;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_SMTP_SERVER;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_SMTP_SSL;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_SMTP_SYSTEM_EMAIL;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_SMTP_TIMEOUT;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_SMTP_TIMEOUT_CON;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_SMTP_TLS;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_SMTP_USER;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_THEME;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_OTP_ENABLED;
import static org.apache.openmeetings.util.OpenmeetingsVariables.DEFAULT_APP_NAME;
import static org.apache.openmeetings.util.OpenmeetingsVariables.DEFAULT_CSP_DATA;
import static org.apache.openmeetings.util.OpenmeetingsVariables.DEFAULT_CSP_FONT;
import static org.apache.openmeetings.util.OpenmeetingsVariables.DEFAULT_CSP_STYLE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.DEFAULT_MAX_UPLOAD_SIZE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.USER_LOGIN_MINIMUM_LENGTH;
import static org.apache.openmeetings.util.OpenmeetingsVariables.USER_PASSWORD_MINIMUM_LENGTH;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getAppointmentPreStartMinutes;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getAppointmentReminderMinutes;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getAudioBitrate;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getAudioRate;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getDefaultGroup;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getExtProcessTtl;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getTheme;
import static org.apache.wicket.csp.CSPDirectiveSrcValue.SELF;
import static org.apache.wicket.csp.CSPDirectiveSrcValue.STRICT_DYNAMIC;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.apache.openmeetings.core.sip.SipManager;
import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dao.label.LabelDao;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.dao.server.OAuth2Dao;
import org.apache.openmeetings.db.dao.user.GroupDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.basic.Configuration;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.room.Room.RoomElement;
import org.apache.openmeetings.db.entity.room.Room.Type;
import org.apache.openmeetings.db.entity.server.OAuthServer;
import org.apache.openmeetings.db.entity.server.OAuthServer.RequestInfoMethod;
import org.apache.openmeetings.db.entity.server.OAuthServer.RequestTokenMethod;
import org.apache.openmeetings.db.entity.user.Group;
import org.apache.openmeetings.db.entity.user.GroupUser;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.entity.user.User.Right;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

import jakarta.inject.Inject;

@Component
public class ImportInitvalues {
	private static final Logger log = LoggerFactory.getLogger(ImportInitvalues.class);
	private static final String VER_1_8 = "1.8.x";
	private static final String VER_1_9 = "1.9.x";
	private static final String VER_2_0 = "2.0.x";
	private static final String VER_3_0 = "3.0.x";
	private static final String VER_3_0_3 = "3.0.3";
	private static final String VER_3_3_0 = "3.3.0";
	private static final String VER_5_0_0 = "5.0.0";
	private static final String VER_5_0_1 = "5.0.1";
	private static final String CLIENT_PLACEHOLDER = "<put your client_id>";
	private static final String SECRET_PLACEHOLDER = "<put your client_secret>";
	private static final String EMAIL_PARAM = "email";
	private static final String FNAME_PARAM = "first_name";
	private static final String LNAME_PARAM = "last_name";

	@Inject
	private ConfigurationDao cfgDao;
	@Inject
	private UserDao userDao;
	@Inject
	private SipManager sipDao;
	@Inject
	private OAuth2Dao oauthDao;
	@Inject
	private GroupDao groupDao;
	@Inject
	private RoomDao roomDao;

	private int progress = 0;

	public int getProgress() {
		return progress;
	}

	private static void addCfg(List<Configuration> list
			, String key
			, String value
			, Configuration.Type type
			, String comment
			, String fromVersion)
	{
		Configuration c = new Configuration();
		c.setType(type);
		c.setKey(key);
		c.setValue(value);
		c.setComment(comment);
		c.setFromVersion(fromVersion);
		list.add(c);
	}

	/*
	 * This method is created to be used to generate GeneralConfiguration.xml
	 */
	public static List<Configuration> initialCfgs(InstallationConfig cfg) {
		List<Configuration> list = new ArrayList<>();
		addCfg(list, CONFIG_CRYPT, cfg.getCryptClassName(), Configuration.Type.STRING,
				"This Class is used for Authentification-Crypting. "
						+ "Be carefull what you do here! If you change it while "
						+ "running previous Pass of users will not be workign anymore! "
						+ "for more Information see https://openmeetings.apache.org/CustomCryptMechanism.html"
						, VER_1_9);

		addCfg(list, CONFIG_REGISTER_FRONTEND, String.valueOf(cfg.isAllowFrontendRegister()), Configuration.Type.BOOL
				, "Is USER register available on login screen", VER_1_8);
		addCfg(list, CONFIG_REGISTER_SOAP, String.valueOf(true), Configuration.Type.BOOL, "Is user register available via SOAP/REST", VER_3_0);
		addCfg(list, CONFIG_REGISTER_OAUTH, String.valueOf(true), Configuration.Type.BOOL, "Is user register available via OAuth", VER_3_0);
		// this group_id is the Group of users who register through the frontend or SOAP
		addCfg(list, CONFIG_DEFAULT_GROUP_ID, String.valueOf(getDefaultGroup()), Configuration.Type.NUMBER, "", VER_1_8);

		addCfg(list, CONFIG_SMTP_SERVER, cfg.getSmtpServer(), Configuration.Type.STRING, "this is the smtp server to send messages", VER_1_9);

		addCfg(list, CONFIG_SMTP_PORT, String.valueOf(cfg.getSmtpPort()), Configuration.Type.NUMBER, "this is the smtp server port normally 25", VER_1_9);

		addCfg(list, CONFIG_SMTP_SYSTEM_EMAIL, cfg.getMailReferer(), Configuration.Type.STRING, "all send e-mails by the system will have this address", VER_1_9);

		addCfg(list, CONFIG_SMTP_USER, cfg.getMailAuthName(), Configuration.Type.STRING, "System auth email username", VER_1_9);

		addCfg(list, CONFIG_SMTP_PASS, cfg.getMailAuthPass(), Configuration.Type.STRING, "System auth email password", VER_1_9);

		addCfg(list, CONFIG_SMTP_TLS, String.valueOf(cfg.isMailUseTls()), Configuration.Type.BOOL, "Enable TLS", VER_1_9);

		addCfg(list, CONFIG_SMTP_TIMEOUT_CON, "30000", Configuration.Type.NUMBER,
				"Socket connection timeout value in milliseconds. Default is 30 seconds (30000).", VER_1_9);

		addCfg(list, CONFIG_SMTP_TIMEOUT, "30000", Configuration.Type.NUMBER,
				"Socket I/O timeout value in milliseconds. Default is 30 seconds (30000).", VER_1_9);

		addCfg(list, CONFIG_APPLICATION_NAME, DEFAULT_APP_NAME, Configuration.Type.STRING, "Name of the Browser Title window", VER_3_0);

		// "1" == "EN"
		addCfg(list, CONFIG_DEFAULT_LANG, String.valueOf(cfg.getDefaultLangId()), Configuration.Type.NUMBER, "Default System Language ID see languages.xml", VER_1_8);

		addCfg(list, CONFIG_DOCUMENT_DPI, String.valueOf(cfg.getDocDpi()), Configuration.Type.NUMBER,
				"dpi for conversion of PDF to images (should be an integer between 50 and  600 with a default value of 150 dpi)", VER_2_0);

		addCfg(list, CONFIG_DOCUMENT_QUALITY, String.valueOf(cfg.getDocQuality()), Configuration.Type.NUMBER,
				"compression quality for conversion of PDF to images (should be an integer between 1 and 100, with a default value of 90)", VER_2_0);

		addCfg(list, CONFIG_PATH_IMAGEMAGIC, cfg.getImageMagicPath(), Configuration.Type.PATH, "Path to ImageMagick tools", VER_2_0);

		addCfg(list, CONFIG_PATH_SOX, cfg.getSoxPath(), Configuration.Type.PATH, "Path To SoX-Tools", VER_2_0);

		addCfg(list, CONFIG_PATH_FFMPEG, cfg.getFfmpegPath(), Configuration.Type.PATH, "Path To FFMPEG", VER_2_0);
		addCfg(list, CONFIG_PATH_OFFICE, cfg.getOfficePath(), Configuration.Type.PATH,
				"The path to OpenOffice/LibreOffice (optional) please set this to the real path in case jodconverter is unable to find OpenOffice/LibreOffice installation automatically", VER_2_0);

		addCfg(list, CONFIG_DASHBOARD_RSS_FEED1, cfg.getUrlFeed(), Configuration.Type.STRING, "Feed URL 1", VER_1_9);

		addCfg(list, CONFIG_DASHBOARD_RSS_FEED2, cfg.getUrlFeed2(), Configuration.Type.STRING, "Feed URL 2", VER_1_9);

		addCfg(list, CONFIG_EMAIL_AT_REGISTER, String.valueOf(cfg.isSendEmailAtRegister()), Configuration.Type.BOOL,
				"User get a EMail with their Account data.", VER_2_0);

		addCfg(list, CONFIG_EMAIL_VERIFICATION, String.valueOf(cfg.isSendEmailWithVerficationCode()), Configuration.Type.BOOL,
				String.format("User must activate their account by clicking on the "
						+ "activation-link in the registering Email "
						+ "It makes no sense to make this(%s) 'true' while "
						+ "%s is 'false' cause you need to send a EMail.", CONFIG_EMAIL_VERIFICATION, CONFIG_EMAIL_AT_REGISTER), VER_2_0);

		addCfg(list, CONFIG_APPLICATION_BASE_URL, cfg.getBaseUrl(), Configuration.Type.STRING, "Base URL your Openmeetings installation will be accessible at.", "3.0.2");

		// ***************************************
		// ***************************************
		// SIP Integration Coniguration Values
		// ***************************************

		addCfg(list, CONFIG_SIP_ENABLED, String.valueOf(cfg.isSipEnable()), Configuration.Type.BOOL, "Enable to enable the red5SIP integration ", VER_1_9);
		addCfg(list, CONFIG_SIP_ROOM_PREFIX, cfg.getSipRoomPrefix(), Configuration.Type.STRING, "Numerical prefix for OM rooms created inside the SIP", VER_1_9);
		addCfg(list, CONFIG_SIP_EXTEN_CONTEXT, cfg.getSipExtenContext(), Configuration.Type.STRING, "Enable to enable the red5SIP integration ", VER_1_9);

		// ***************************************
		// ***************************************
		// Timezone settings
		// ***************************************

		addCfg(list, CONFIG_DEFAULT_TIMEZONE, cfg.getTimeZone(), Configuration.Type.STRING, "This is the default timezone if nothing is specified", VER_1_9);

		// ***************************************
		// ***************************************
		// additional settings
		// ***************************************

		addCfg(list, CONFIG_SCREENSHARING_QUALITY, "1", Configuration.Type.NUMBER, """
				Default selection in ScreenSharing Quality:
				 0 - bigger frame rate, no resize
				 1 - no resize
				 2 - size == 1/2 of selected area
				 3 - size == 3/8 of selected area""", VER_3_0_3);

		addCfg(list, CONFIG_SCREENSHARING_FPS, "10", Configuration.Type.NUMBER, "Default selection in ScreenSharing FPS", VER_3_0_3);
		addCfg(list, CONFIG_SCREENSHARING_FPS_SHOW, String.valueOf(true), Configuration.Type.BOOL, "Is screensharing FPS should be displayed or not", VER_3_0_3);
		addCfg(list, CONFIG_SCREENSHARING_ALLOW_REMOTE, String.valueOf(true), Configuration.Type.BOOL
				, "Is remote control will be enabled while screensharing. Allowing remote control will be not possible in case it is set to 'false'", "3.0.4");

		addCfg(list, CONFIG_DASHBOARD_SHOW_MYROOMS, String.valueOf(true), Configuration.Type.BOOL, "Show 'My Rooms' widget on dashboard", VER_1_9);

		addCfg(list, CONFIG_DASHBOARD_SHOW_CHAT, String.valueOf(true), Configuration.Type.BOOL, "Show 'Global Chat' outside the room", VER_1_9);

		addCfg(list, CONFIG_DASHBOARD_SHOW_RSS, String.valueOf(false), Configuration.Type.BOOL, "Show RSS widget on dashboard", VER_1_9);

		addCfg(list, CONFIG_MAX_UPLOAD_SIZE, String.valueOf(DEFAULT_MAX_UPLOAD_SIZE), Configuration.Type.NUMBER,
				"Maximum size of upload file (bytes)", VER_1_8);

		addCfg(list, CONFIG_APPOINTMENT_REMINDER_MINUTES, String.valueOf(getAppointmentReminderMinutes()), Configuration.Type.NUMBER,
				"The number of minutes before reminder emails are send. Set to 0 to disable reminder emails", VER_1_9);

		addCfg(list, CONFIG_LOGIN_MIN_LENGTH, String.valueOf(USER_LOGIN_MINIMUM_LENGTH), Configuration.Type.NUMBER,
				"Number of chars needed in a user login", VER_1_9);

		addCfg(list, CONFIG_PASS_MIN_LENGTH, String.valueOf(USER_PASSWORD_MINIMUM_LENGTH), Configuration.Type.NUMBER,
				"Number of chars needed in a user password", VER_1_9);

		addCfg(list, CONFIG_CALENDAR_ROOM_CAPACITY, "50", Configuration.Type.NUMBER,
				"Default number of participants conference room created via calendar", VER_1_9);

		addCfg(list, CONFIG_KEYCODE_ARRANGE, "Shift+F8", Configuration.Type.HOTKEY
				, "A hot key code for arrange video windows functionality", VER_2_0);
		addCfg(list, CONFIG_KEYCODE_MUTE_OTHERS, "Shift+F12", Configuration.Type.HOTKEY
				, "A hot key code for the 'mute others' functionality", VER_2_0);
		addCfg(list, CONFIG_KEYCODE_MUTE, "Shift+F7", Configuration.Type.HOTKEY
				, "A hot key code for the 'mute/unmute audio' functionality", VER_2_0);

		// system-wide ldap params
		addCfg(list, CONFIG_DEFAULT_LDAP_ID, "0", Configuration.Type.NUMBER, "Ldap domain selected by default in the login screen", VER_1_9);

		// set inviter's email address as ReplyTo in email invitations
		addCfg(list, CONFIG_REPLY_TO_ORGANIZER, String.valueOf(cfg.isReplyToOrganizer()), Configuration.Type.BOOL,
				"Set inviter's email address as ReplyTo in email invitations", VER_2_0);

		addCfg(list, CONFIG_DEFAULT_LANDING_ZONE, "user/dashboard", Configuration.Type.STRING
				, "Area to be shown to the user after login. Possible values are: "
						+ "user/dashboard, user/calendar, user/record, rooms/my, rooms/group, rooms/public"
						+ ", profile/messages, profile/edit, profile/search, profile/invitation, profile/widget"
						+ ", admin/user, admin/connection"
						+ ", admin/group, admin/room, admin/config, admin/lang, admin/ldap, admin/oauth2, admin/backup, admin/email", "2.1.x");

		// oauth2 params
		addCfg(list, CONFIG_IGNORE_BAD_SSL, String.valueOf(false), Configuration.Type.BOOL, """
				Set "yes" or "no" to enable/disable ssl certifications checking for OAuth2
				WARNING: it is not secure to ignore bad SSL""", VER_3_0);

		addCfg(list, CONFIG_REDIRECT_URL_FOR_EXTERNAL, "", Configuration.Type.STRING,
				"Users entered the room via invitationHash or secureHash will be redirected to this URL on connection lost", VER_3_0);
		addCfg(list, CONFIG_GOOGLE_ANALYTICS_CODE, null, Configuration.Type.STRING, "Code for Google Analytics", "3.1.0");
		addCfg(list, CONFIG_EXT_PROCESS_TTL, String.valueOf(getExtProcessTtl()), Configuration.Type.NUMBER, String.format("Time to live in minutes for external processes such as conversion via ffmpeg (default %s minutes)", getExtProcessTtl()), VER_3_3_0);
		addCfg(list, CONFIG_MYROOMS_ENABLED, String.valueOf(true), Configuration.Type.BOOL, "Users are allowed to create personal rooms", "3.3.2");
		addCfg(list, CONFIG_REMINDER_MESSAGE, null, Configuration.Type.STRING, "Reminder message to notify about upcoming appointment, generated message will be used if not set", VER_2_0);
		addCfg(list, CONFIG_MP4_AUDIO_RATE, String.valueOf(getAudioRate()), Configuration.Type.NUMBER, "Audio sampling rate (in Hz) for MP4 video", "4.0.1");
		addCfg(list, CONFIG_MP4_AUDIO_BITRATE, String.valueOf(getAudioBitrate()), Configuration.Type.STRING, "Audio bitrate for MP4 video", "4.0.1");
		addCfg(list, CONFIG_REST_ALLOW_ORIGIN, null, Configuration.Type.STRING, "List of addresses browser Ajax REST requests are allowed from", "4.0.2");
		addCfg(list, CONFIG_FNAME_MIN_LENGTH, String.valueOf(USER_LOGIN_MINIMUM_LENGTH), Configuration.Type.NUMBER,
				"Number of chars needed in a user first name", "4.0.4");
		addCfg(list, CONFIG_LNAME_MIN_LENGTH, String.valueOf(USER_LOGIN_MINIMUM_LENGTH), Configuration.Type.NUMBER,
				"Number of chars needed in a user last name", "4.0.4");
		addCfg(list, CONFIG_CHAT_SEND_ON_ENTER, String.valueOf(false), Configuration.Type.BOOL,
				"Controls if chat message will be set on Enter (default: send on Ctrl+Enter)", "4.0.5");
		addCfg(list, CONFIG_MP4_VIDEO_PRESET, "medium", Configuration.Type.STRING,
				"Preset (encoder optimization settings) to be used while performing mp4 conversion."
					+ "Valid values are: ultrafast, superfast, veryfast, faster, fast, medium, slow, slower, veryslow", "4.0.5");
		addCfg(list, CONFIG_CAM_FPS, "30", Configuration.Type.NUMBER, "Camera FPS, should be positive number in range (0, 60]", VER_5_0_0);
		addCfg(list, CONFIG_MIC_RATE, "22", Configuration.Type.NUMBER, "The rate at which the microphone should capture sound, in kHz. The default value is 22 kHz.", VER_5_0_0);
		addCfg(list, CONFIG_MIC_ECHO, String.valueOf(true), Configuration.Type.BOOL, "Whether or not echo cancellation is preferred and/or required.", VER_5_0_0);
		addCfg(list, CONFIG_MIC_NOISE, String.valueOf(true), Configuration.Type.BOOL, "Whether noise suppression is preferred and/or required.", VER_5_0_0);
		addCfg(list, CONFIG_DISPLAY_NAME_EDITABLE, String.valueOf(false), Configuration.Type.BOOL, "Is user will be able to edit his/her display name (default false).", "4.0.7");
		addCfg(list, CONFIG_KEYCODE_QUICKPOLL, "Ctrl+Alt+KeyQ", Configuration.Type.HOTKEY
				, "A hot key code to start quick poll", "4.0.10");
		addCfg(list, CONFIG_AUTO_OPEN_SHARING, String.valueOf(false), Configuration.Type.BOOL, "Whether shared screen should be auto-opened.", VER_5_0_0);
		addCfg(list, CONFIG_KEYCODE_ARRANGE_RESIZE, "Ctrl+Shift+KeyA", Configuration.Type.HOTKEY
				, "A hot key code to arrange video windows bottom-to-top with resize to 120x90", VER_5_0_0);
		final String cspMore = ", more info: https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Content-Security-Policy";
		addCfg(list, CONFIG_CSP_FONT, DEFAULT_CSP_FONT, Configuration.Type.STRING, "Value for 'font-src' directive of 'Content-Security-Policy' header (default: "
				+ DEFAULT_CSP_FONT + ")" + cspMore, VER_5_0_0);
		addCfg(list, CONFIG_CSP_FRAME, SELF.getValue(), Configuration.Type.STRING, "Value for 'frame-src' directive of 'Content-Security-Policy' header (default: "
				+ SELF + ")" + cspMore, VER_5_0_0);
		addCfg(list, CONFIG_CSP_IMAGE, DEFAULT_CSP_DATA, Configuration.Type.STRING, "Value for 'image-src' directive of 'Content-Security-Policy' header (default: "
				+ DEFAULT_CSP_DATA + ")" + cspMore, VER_5_0_0);
		addCfg(list, CONFIG_CSP_MEDIA, DEFAULT_CSP_DATA, Configuration.Type.STRING, "Value for 'media-src' directive of 'Content-Security-Policy' header (default: "
				+ DEFAULT_CSP_DATA + ")" + cspMore, VER_5_0_0);
		addCfg(list, CONFIG_CSP_SCRIPT, STRICT_DYNAMIC.getValue(), Configuration.Type.STRING, "Value for 'script-src' directive of 'Content-Security-Policy' header (default: "
				+ STRICT_DYNAMIC + ")" + cspMore, VER_5_0_0);
		addCfg(list, CONFIG_CSP_STYLE, DEFAULT_CSP_STYLE, Configuration.Type.STRING, "Value for 'style-src' directive of 'Content-Security-Policy' header (default: "
				+ DEFAULT_CSP_STYLE + ")" + cspMore, VER_5_0_0);
		addCfg(list, CONFIG_SMTP_SSL, String.valueOf(false), Configuration.Type.BOOL, "Enable SSL", VER_5_0_0);
		addCfg(list, CONFIG_CSP_ENABLED, String.valueOf(true), Configuration.Type.BOOL, "Whether or not CSP secure headers are enabled", VER_5_0_0);
		addCfg(list, CONFIG_PASS_CHECK_UPPER, String.valueOf(true), Configuration.Type.BOOL, "Whether or not Password MUST contain uppercase characters", VER_5_0_1);
		addCfg(list, CONFIG_PASS_CHECK_DIGIT, String.valueOf(true), Configuration.Type.BOOL, "Whether or not Password MUST contain numeric", VER_5_0_1);
		addCfg(list, CONFIG_PASS_CHECK_SPECIAL, String.valueOf(true), Configuration.Type.BOOL, "Whether or not Password MUST contain special character", VER_5_0_1);
		addCfg(list, CONFIG_APPOINTMENT_PRE_START_MINUTES, String.valueOf(getAppointmentPreStartMinutes()), Configuration.Type.NUMBER
				, "How many minutes before the start the room should be open (default: " + getAppointmentPreStartMinutes() + ")", VER_5_0_1);
		addCfg(list, CONFIG_RECORDING_ENABLED, String.valueOf(true), Configuration.Type.BOOL, "Whether or not recording functionality is enabled", "6.0.0");
		addCfg(list, CONFIG_THEME, getTheme(), Configuration.Type.STRING, "UI theme, possible values are Cerulean, Cosmo, Cyborg, Darkly, Flatly, "
				+ "Journal, Litera, Lumen, Lux, Materia, Minty, Pulse, Sandstone, Simplex, Sketchy, Slate, Solar, Spacelab, Superhero, "
				+ "United, Yeti", "6.1.0");

		addCfg(list, CONFIG_OTP_ENABLED, String.valueOf(false), Configuration.Type.BOOL, "Whether or not Time-based One Time Passwords are enabled", "6.3.0");
		return list;
	}

	public void loadConfiguration(InstallationConfig cfg) {
		for (Configuration c : initialCfgs(cfg)) {
			cfgDao.update(c, null);
		}
		log.debug("Configurations ADDED");
	}

	private Room createRoom(String name, Room.Type type, long capacity, boolean isPublic, Long groupId) {
		Room r = new Room();
		r.setName(name);
		r.setComment("");
		r.setCapacity(capacity);
		r.setType(type);
		r.setIspublic(isPublic);
		r.setAllowUserQuestions(true);
		r.setAudioOnly(false);

		r.setAppointment(false);

		r.setDemoRoom(false);
		r.setDemoTime(null);

		r.hide(RoomElement.MICROPHONE_STATUS);
		r.setModerated(false);

		r.setDeleted(false);

		r.setClosed(false);
		r.setRedirectURL(null);

		r.setOwnerId(null);

		r.setWaitRecording(false);
		r.setAllowRecording(true);

		if (groupId != null) {
			r.addGroup(groupDao.get(groupId));
		}
		r = roomDao.update(r, null);
		return r;
	}

	public void loadDefaultRooms(boolean createRooms, long langId) {
		if (createRooms) {
			createRoom(LabelDao.getString("install.room.public.interview", langId), Type.INTERVIEW, 16L, true, null);
			createRoom(LabelDao.getString("install.room.public.conference", langId), Type.CONFERENCE, 32L, true, null);
			Room r = createRoom(LabelDao.getString("install.room.public.video.only", langId), Type.CONFERENCE, 32L, true, null);
			r.hide(RoomElement.WHITEBOARD);
			roomDao.update(r, null);
			createRoom(LabelDao.getString("install.room.public.video.wb", langId), Type.CONFERENCE, 32L, true, null);
			createRoom(LabelDao.getString("install.room.public.presentation", langId), Type.PRESENTATION, 100L, true, null);
			r = createRoom(LabelDao.getString("install.room.presentation.micro", langId), Type.PRESENTATION, 100L, true, null);
			r.getHiddenElements().clear();
			roomDao.update(r, null);

			r = createRoom(LabelDao.getString("install.room.conference.micro", langId), Type.CONFERENCE, 32L, true, null);
			r.getHiddenElements().clear();
			roomDao.update(r, null);

			createRoom(LabelDao.getString("install.room.private.conference", langId), Type.CONFERENCE, 32L, false, 1L);
		}
	}

	public void loadInitUserAndGroup(InstallationConfig cfg) throws Exception {
		// Add default group
		Group g = new Group();
		g.setName(cfg.getGroup());
		g.setInsertedby(1L);
		g.setDeleted(false);
		g = groupDao.update(g, null);
		Configuration c = cfgDao.get(CONFIG_DEFAULT_GROUP_ID);
		c.setValueN(g.getId());
		cfgDao.update(c, null);

		User u = getNewUserInstance(null);
		u.setType(User.Type.USER);
		u.getRights().add(Right.ADMIN);
		u.getRights().add(Right.SOAP);
		u.setLogin(cfg.getUsername());
		u.setFirstname("firstname");
		u.setLastname("lastname");
		u.getAddress().setEmail(cfg.getEmail());
		u.addGroup(g);

		u = userDao.update(u, cfg.getPassword(), -1L);

		log.debug("Installation - User Added user-Id {}", u.getId());

		if (u.getId() == null) {
			throw new InstallException("Unable to add user");
		}
	}

	public User createSystemUser(User u, String group, String login, String pass, boolean groupAdmin, Consumer<User> postprocess) throws Exception {
		GroupUser gu = new GroupUser(groupDao.get(group), u);
		gu.setModerator(groupAdmin);
		u.getGroupUsers().add(gu);
		u.setLogin(login);
		if (postprocess != null) {
			postprocess.accept(u);
		}
		return userDao.update(u, pass, null);
	}

	public void loadInitialOAuthServers() {
		// Yandex
		oauthDao.update(new OAuthServer()
				.setName("Yandex")
				.setIconUrl("https://yandex.st/morda-logo/i/favicon.ico")
				.setEnabled(false)
				.setClientId(CLIENT_PLACEHOLDER)
				.setClientSecret(SECRET_PLACEHOLDER)
				.setRequestKeyUrl("https://oauth.yandex.ru/authorize?response_type=code&client_id={$client_id}")
				.setRequestTokenUrl("https://oauth.yandex.ru/token")
				.setRequestTokenMethod(RequestTokenMethod.POST)
				.setRequestTokenAttributes("grant_type=authorization_code&code={$code}&client_id={$client_id}&client_secret={$client_secret}")
				.setRequestInfoUrl("https://login.yandex.ru/info?format=json&oauth_token={$access_token}")
				.addMapping(PARAM_LOGIN, "login")
				.addMapping(PARAM_EMAIL, "default_email")
				.addMapping(PARAM_FNAME, FNAME_PARAM)
				.addMapping(PARAM_LNAME, LNAME_PARAM), null);

		// Google
		oauthDao.update(new OAuthServer()
				.setName("Google")
				.setIconUrl("https://www.google.com/images/google_favicon_128.png")
				.setEnabled(false)
				.setClientId(CLIENT_PLACEHOLDER)
				.setClientSecret(SECRET_PLACEHOLDER)
				.setRequestKeyUrl("https://accounts.google.com/o/oauth2/auth?redirect_uri={$redirect_uri}&response_type=code&client_id={$client_id}"
						+ "&scope=https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.email+https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.profile")
				.setRequestTokenUrl("https://accounts.google.com/o/oauth2/token")
				.setRequestTokenMethod(RequestTokenMethod.POST)
				.setRequestTokenAttributes("code={$code}&client_id={$client_id}&client_secret={$client_secret}&redirect_uri={$redirect_uri}&grant_type=authorization_code")
				.setRequestInfoUrl("https://www.googleapis.com/oauth2/v1/userinfo?access_token={$access_token}")
				.setRequestInfoMethod(RequestInfoMethod.GET)
				.addMapping(PARAM_LOGIN, EMAIL_PARAM)
				.addMapping(PARAM_EMAIL, EMAIL_PARAM)
				.addMapping(PARAM_FNAME, "given_name")
				.addMapping(PARAM_LNAME, "family_name"), null);

		// Facebook
		oauthDao.update(new OAuthServer()
				.setName("Facebook")
				.setIconUrl("https://www.facebook.com/images/fb_icon_325x325.png")
				.setEnabled(false)
				.setClientId(CLIENT_PLACEHOLDER)
				.setClientSecret(SECRET_PLACEHOLDER)
				.setRequestKeyUrl("https://www.facebook.com/v4.0/dialog/oauth?client_id={$client_id}&redirect_uri={$redirect_uri}&scope=email")
				.setRequestTokenUrl("https://graph.facebook.com/v4.0/oauth/access_token")
				.setRequestTokenMethod(RequestTokenMethod.POST)
				.setRequestTokenAttributes("client_id={$client_id}&redirect_uri={$redirect_uri}&client_secret={$client_secret}&code={$code}")
				.setRequestInfoUrl("https://graph.facebook.com/me?access_token={$access_token}&fields=id,first_name,last_name,email")
				.setRequestInfoMethod(RequestInfoMethod.GET)
				.addMapping(PARAM_LOGIN, "id")
				.addMapping(PARAM_EMAIL, EMAIL_PARAM)
				.addMapping(PARAM_FNAME, FNAME_PARAM)
				.addMapping(PARAM_LNAME, LNAME_PARAM), null);

		// VK
		oauthDao.update(new OAuthServer()
				.setName("VK")
				.setIconUrl("https://vk.com/images/icons/pwa/apple/default.png")
				.setEnabled(false)
				.setClientId(CLIENT_PLACEHOLDER)
				.setClientSecret(SECRET_PLACEHOLDER)
				.setRequestKeyUrl("https://oauth.vk.com/authorize?client_id={$client_id}&scope=email&redirect_uri={$redirect_uri}&response_type=code&v=5.68")
				.setRequestTokenUrl("https://oauth.vk.com/access_token")
				.setRequestTokenMethod(RequestTokenMethod.POST)
				.setRequestTokenAttributes("client_id={$client_id}&client_secret={$client_secret}&code={$code}&redirect_uri={$redirect_uri}")
				.setRequestInfoUrl("https://api.vk.com/method/users.get?&v=5.92&user_ids={$user_id}&access_token={$access_token}&fields=id,first_name,last_name,email&name_case=nom")
				.setRequestInfoMethod(RequestInfoMethod.GET)
				.addMapping(PARAM_LOGIN, "id")
				.addMapping(PARAM_EMAIL, EMAIL_PARAM)
				.addMapping(PARAM_FNAME, FNAME_PARAM)
				.addMapping(PARAM_LNAME, LNAME_PARAM), null);

		// keycloak
		oauthDao.update(new OAuthServer()
				.setName("keycloak")
				.setIconUrl("https://www.keycloak.org/resources/images/keycloak_icon_512px.svg")
				.setEnabled(false)
				.setClientId(CLIENT_PLACEHOLDER)
				.setClientSecret(SECRET_PLACEHOLDER)
				.setRequestKeyUrl("<KeycloakBaseUrl>/auth/realms/uweslan/protocol/openid-connect/auth?redirect_uri={$redirect_uri}&client_id={$client_id}&response_type=code&scope=email")
				.setRequestTokenUrl("<KeycloakBaseUrl>/auth/realms/<--custom-realm-->/protocol/openid-connect/token")
				.setRequestTokenMethod(RequestTokenMethod.POST)
				.setRequestTokenAttributes("code={$code}&client_id={$client_id}&client_secret={$client_secret}&redirect_uri={$redirect_uri}&grant_type=authorization_code")
				.setRequestInfoUrl("<KeycloakBaseUrl>/auth/realms/<--custom-realm-->/protocol/openid-connect/userinfo")
				.setRequestInfoMethod(RequestInfoMethod.HEADER)
				.addMapping(PARAM_LOGIN, EMAIL_PARAM)
				.addMapping(PARAM_EMAIL, EMAIL_PARAM)
				.addMapping(PARAM_FNAME, "given_name")
				.addMapping(PARAM_LNAME, "family_name"), null);
	}

	// ------------------------------------------------------------------------------
	private void checkInstalled(boolean force) {
		// dummy check if installation was performed before
		if (!force && userDao.count() > 0) {
			log.debug("System contains users, no need to install data one more time.");
		}
	}

	public void loadSystem(InstallationConfig cfg, boolean force) {
		checkInstalled(force);
		sipDao.delete();
		progress = 20;
		loadConfiguration(cfg);
		progress = 40;
		if (cfg.isCreateDefaultObjects()) {
			loadInitialOAuthServers();
		}
		progress = 60;
	}

	public void loadAll(InstallationConfig cfg, boolean force) throws Exception {
		checkInstalled(force);
		loadSystem(cfg, force);
		loadInitUserAndGroup(cfg);
		progress = 80;

		loadDefaultRooms(cfg.isCreateDefaultObjects(), cfg.getDefaultLangId());
		progress = 100;
	}
}
