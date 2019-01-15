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
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_APPOINTMENT_REMINDER_MINUTES;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_CALENDAR_ROOM_CAPACITY;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_CHAT_SEND_ON_ENTER;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_CRYPT;
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
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_FLASH_CAM_QUALITY;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_FLASH_ECHO_PATH;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_FLASH_MIC_RATE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_FLASH_SECURE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_FLASH_SECURE_PROXY;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_FLASH_VIDEO_BANDWIDTH;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_FLASH_VIDEO_CODEC;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_FLASH_VIDEO_FPS;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_FNAME_MIN_LENGTH;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_GOOGLE_ANALYTICS_CODE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_HEADER_CSP;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_HEADER_XFRAME;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_IGNORE_BAD_SSL;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_KEYCODE_ARRANGE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_KEYCODE_EXCLUSIVE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_KEYCODE_MUTE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_LNAME_MIN_LENGTH;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_LOGIN_MIN_LENGTH;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_MAX_UPLOAD_SIZE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_MP4_AUDIO_BITRATE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_MP4_AUDIO_RATE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_MP4_VIDEO_PRESET;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_MYROOMS_ENABLED;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_PASS_MIN_LENGTH;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_PATH_FFMPEG;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_PATH_IMAGEMAGIC;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_PATH_OFFICE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_PATH_SOX;
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
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_SMTP_SYSTEM_EMAIL;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_SMTP_TIMEOUT;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_SMTP_TIMEOUT_CON;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_SMTP_TLS;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_SMTP_USER;
import static org.apache.openmeetings.util.OpenmeetingsVariables.DEFAULT_APP_NAME;
import static org.apache.openmeetings.util.OpenmeetingsVariables.DEFAULT_MAX_UPLOAD_SIZE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.HEADER_CSP_SELF;
import static org.apache.openmeetings.util.OpenmeetingsVariables.HEADER_XFRAME_SAMEORIGIN;
import static org.apache.openmeetings.util.OpenmeetingsVariables.USER_LOGIN_MINIMUM_LENGTH;
import static org.apache.openmeetings.util.OpenmeetingsVariables.USER_PASSWORD_MINIMUM_LENGTH;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getAudioBitrate;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getAudioRate;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getDefaultGroup;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getExtProcessTtl;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getWebAppRootKey;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dao.label.LabelDao;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.dao.room.SipDao;
import org.apache.openmeetings.db.dao.server.OAuth2Dao;
import org.apache.openmeetings.db.dao.user.GroupDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.basic.Configuration;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.room.Room.RoomElement;
import org.apache.openmeetings.db.entity.room.Room.Type;
import org.apache.openmeetings.db.entity.room.RoomGroup;
import org.apache.openmeetings.db.entity.server.OAuthServer;
import org.apache.openmeetings.db.entity.server.OAuthServer.RequestInfoMethod;
import org.apache.openmeetings.db.entity.server.OAuthServer.RequestTokenMethod;
import org.apache.openmeetings.db.entity.user.Group;
import org.apache.openmeetings.db.entity.user.GroupUser;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.entity.user.User.Right;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ImportInitvalues {
	private static final Logger log = Red5LoggerFactory.getLogger(ImportInitvalues.class, getWebAppRootKey());
	private static final String VER_1_8 = "1.8.x";
	private static final String VER_1_9 = "1.9.x";
	private static final String VER_2_0 = "2.0.x";
	private static final String VER_3_0 = "3.0.x";
	private static final String VER_3_0_3 = "3.0.3";
	private static final String VER_3_3_0 = "3.3.0";
	private static final String VER_4_0_0 = "4.0.0";
	private static final String CLIENT_PLACEHOLDER = "<put your client_id>";
	private static final String SECRET_PLACEHOLDER = "<put your client_secret>";
	private static final String EMAIL_PARAM = "email";
	private static final String FNAME_PARAM = "first_name";
	private static final String LNAME_PARAM = "last_name";

	@Autowired
	private ConfigurationDao cfgDao;
	@Autowired
	private UserDao userDao;
	@Autowired
	private SipDao sipDao;
	@Autowired
	private OAuth2Dao oauthDao;
	@Autowired
	private GroupDao groupDao;
	@Autowired
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
		addCfg(list, CONFIG_CRYPT, cfg.getCryptClassName(), Configuration.Type.string,
				"This Class is used for Authentification-Crypting. "
						+ "Be carefull what you do here! If you change it while "
						+ "running previous Pass of users will not be workign anymore! "
						+ "for more Information see https://openmeetings.apache.org/CustomCryptMechanism.html"
						, VER_1_9);

		addCfg(list, CONFIG_REGISTER_FRONTEND, String.valueOf(cfg.isAllowFrontendRegister()), Configuration.Type.bool
				, "Is user register available on login screen", VER_1_8);
		addCfg(list, CONFIG_REGISTER_SOAP, String.valueOf(true), Configuration.Type.bool, "Is user register available via SOAP/REST", VER_3_0);
		addCfg(list, CONFIG_REGISTER_OAUTH, String.valueOf(true), Configuration.Type.bool, "Is user register available via OAuth", VER_3_0);
		// this group_id is the Group of users who register through the frontend or SOAP
		addCfg(list, CONFIG_DEFAULT_GROUP_ID, String.valueOf(getDefaultGroup()), Configuration.Type.number, "", VER_1_8);

		addCfg(list, CONFIG_SMTP_SERVER, cfg.getSmtpServer(), Configuration.Type.string, "this is the smtp server to send messages", VER_1_9);

		addCfg(list, CONFIG_SMTP_PORT, String.valueOf(cfg.getSmtpPort()), Configuration.Type.number, "this is the smtp server port normally 25", VER_1_9);

		addCfg(list, CONFIG_SMTP_SYSTEM_EMAIL, cfg.getMailReferer(), Configuration.Type.string, "all send e-mails by the system will have this address", VER_1_9);

		addCfg(list, CONFIG_SMTP_USER, cfg.getMailAuthName(), Configuration.Type.string, "System auth email username", VER_1_9);

		addCfg(list, CONFIG_SMTP_PASS, cfg.getMailAuthPass(), Configuration.Type.string, "System auth email password", VER_1_9);

		addCfg(list, CONFIG_SMTP_TLS, String.valueOf(cfg.isMailUseTls()), Configuration.Type.bool, "Enable TLS", VER_1_9);

		addCfg(list, CONFIG_SMTP_TIMEOUT_CON, "30000", Configuration.Type.number,
				"Socket connection timeout value in milliseconds. Default is 30 seconds (30000).", VER_1_9);

		addCfg(list, CONFIG_SMTP_TIMEOUT, "30000", Configuration.Type.number,
				"Socket I/O timeout value in milliseconds. Default is 30 seconds (30000).", VER_1_9);

		addCfg(list, CONFIG_APPLICATION_NAME, DEFAULT_APP_NAME, Configuration.Type.string, "Name of the Browser Title window", VER_3_0);

		// "1" == "EN"
		addCfg(list, CONFIG_DEFAULT_LANG, String.valueOf(cfg.getDefaultLangId()), Configuration.Type.number, "Default System Language ID see languages.xml", VER_1_8);

		addCfg(list, CONFIG_DOCUMENT_DPI, String.valueOf(cfg.getDocDpi()), Configuration.Type.number,
				"dpi for conversion of PDF to images (should be an integer between 50 and  600 with a default value of 150 dpi)", VER_2_0);

		addCfg(list, CONFIG_DOCUMENT_QUALITY, String.valueOf(cfg.getDocQuality()), Configuration.Type.number,
				"compression quality for conversion of PDF to images (should be an integer between 1 and 100, with a default value of 90)", VER_2_0);

		addCfg(list, CONFIG_PATH_IMAGEMAGIC, cfg.getImageMagicPath(), Configuration.Type.string, "Path to ImageMagick tools", VER_2_0);

		addCfg(list, CONFIG_PATH_SOX, cfg.getSoxPath(), Configuration.Type.string, "Path To SoX-Tools", VER_2_0);

		addCfg(list, CONFIG_PATH_FFMPEG, cfg.getFfmpegPath(), Configuration.Type.string, "Path To FFMPEG", VER_2_0);
		addCfg(list, CONFIG_PATH_OFFICE, cfg.getOfficePath(), Configuration.Type.string,
				"The path to OpenOffice/LibreOffice (optional) please set this to the real path in case jodconverter is unable to find OpenOffice/LibreOffice installation automatically", VER_2_0);

		addCfg(list, CONFIG_DASHBOARD_RSS_FEED1, cfg.getUrlFeed(), Configuration.Type.string, "Feed URL 1", VER_1_9);

		addCfg(list, CONFIG_DASHBOARD_RSS_FEED2, cfg.getUrlFeed2(), Configuration.Type.string, "Feed URL 2", VER_1_9);

		addCfg(list, CONFIG_EMAIL_AT_REGISTER, String.valueOf(cfg.isSendEmailAtRegister()), Configuration.Type.bool,
				"User get a EMail with their Account data.", VER_2_0);

		addCfg(list, CONFIG_EMAIL_VERIFICATION, String.valueOf(cfg.isSendEmailWithVerficationCode()), Configuration.Type.bool,
				String.format("User must activate their account by clicking on the "
						+ "activation-link in the registering Email "
						+ "It makes no sense to make this(%s) 'true' while "
						+ "%s is 'false' cause you need to send a EMail.", CONFIG_EMAIL_VERIFICATION, CONFIG_EMAIL_AT_REGISTER), VER_2_0);

		addCfg(list, CONFIG_APPLICATION_BASE_URL, cfg.getBaseUrl(), Configuration.Type.string, "Base URL your OPenmeetings installation will be accessible at.", "3.0.2");

		// ***************************************
		// ***************************************
		// SIP Integration Coniguration Values
		// ***************************************

		addCfg(list, CONFIG_SIP_ENABLED, String.valueOf(cfg.isSipEnable()), Configuration.Type.bool, "Enable to enable the red5SIP integration ", VER_1_9);
		addCfg(list, CONFIG_SIP_ROOM_PREFIX, cfg.getSipRoomPrefix(), Configuration.Type.string, "Numerical prefix for OM rooms created inside the SIP", VER_1_9);
		addCfg(list, CONFIG_SIP_EXTEN_CONTEXT, cfg.getSipExtenContext(), Configuration.Type.string, "Enable to enable the red5SIP integration ", VER_1_9);

		// ***************************************
		// ***************************************
		// Timezone settings
		// ***************************************

		addCfg(list, CONFIG_DEFAULT_TIMEZONE, cfg.getTimeZone(), Configuration.Type.string, "This is the default timezone if nothing is specified", VER_1_9);

		// ***************************************
		// ***************************************
		// additional settings
		// ***************************************

		addCfg(list, CONFIG_SCREENSHARING_QUALITY, "1", Configuration.Type.number,
				"Default selection in ScreenSharing Quality:\n 0 - bigger frame rate, no resize\n 1 - no resize\n 2 - size == 1/2 of selected area\n 3 - size == 3/8 of selected area", VER_3_0_3);

		addCfg(list, CONFIG_SCREENSHARING_FPS, "10", Configuration.Type.number, "Default selection in ScreenSharing FPS", VER_3_0_3);
		addCfg(list, CONFIG_SCREENSHARING_FPS_SHOW, String.valueOf(true), Configuration.Type.bool, "Is screensharing FPS should be displayed or not", VER_3_0_3);
		addCfg(list, CONFIG_SCREENSHARING_ALLOW_REMOTE, String.valueOf(true), Configuration.Type.bool
				, "Is remote control will be enabled while screensharing. Allowing remote control will be not possible in case it is set to 'false'", "3.0.4");

		addCfg(list, CONFIG_DASHBOARD_SHOW_MYROOMS, String.valueOf(true), Configuration.Type.bool, "Show 'My Rooms' widget on dashboard", VER_1_9);

		addCfg(list, CONFIG_DASHBOARD_SHOW_CHAT, String.valueOf(true), Configuration.Type.bool, "Show 'Global Chat' outside the room", VER_1_9);

		addCfg(list, CONFIG_DASHBOARD_SHOW_RSS, String.valueOf(false), Configuration.Type.bool, "Show RSS widget on dashboard", VER_1_9);

		addCfg(list, CONFIG_MAX_UPLOAD_SIZE, String.valueOf(DEFAULT_MAX_UPLOAD_SIZE), Configuration.Type.number,
				"Maximum size of upload file (bytes)", VER_1_8);

		addCfg(list, CONFIG_APPOINTMENT_REMINDER_MINUTES, "15", Configuration.Type.number,
				"The number of minutes before reminder emails are send. Set to 0 to disable reminder emails", VER_1_9);

		addCfg(list, CONFIG_LOGIN_MIN_LENGTH, String.valueOf(USER_LOGIN_MINIMUM_LENGTH), Configuration.Type.number,
				"Number of chars needed in a user login", VER_1_9);

		addCfg(list, CONFIG_PASS_MIN_LENGTH, String.valueOf(USER_PASSWORD_MINIMUM_LENGTH), Configuration.Type.number,
				"Number of chars needed in a user password", VER_1_9);

		addCfg(list, CONFIG_CALENDAR_ROOM_CAPACITY, "50", Configuration.Type.number,
				"Default number of participants conference room created via calendar", VER_1_9);

		addCfg(list, CONFIG_KEYCODE_ARRANGE, "119", Configuration.Type.number
				, "A hot key code for arrange video windows functionality. Should be used with Shift key. (Keycode 119 is F8)", VER_2_0);
		addCfg(list, CONFIG_KEYCODE_EXCLUSIVE, "123", Configuration.Type.number
				, "A hot key code for the 'give exclusive audio' functionality. Should be used with Shift key. (Keycode 123 is F12)", VER_2_0);
		addCfg(list, CONFIG_KEYCODE_MUTE, "118", Configuration.Type.number
				, "A hot key code for the 'mute/unmute audio' functionality. Should be used with Shift key. (Keycode 118 is F7)", VER_2_0);

		// system-wide ldap params
		addCfg(list, CONFIG_DEFAULT_LDAP_ID, "0", Configuration.Type.number, "Ldap domain selected by default in the login screen", VER_1_9);

		// set inviter's email address as ReplyTo in email invitations
		addCfg(list, CONFIG_REPLY_TO_ORGANIZER, String.valueOf(cfg.isReplyToOrganizer()), Configuration.Type.bool,
				"Set inviter's email address as ReplyTo in email invitations", VER_2_0);

		addCfg(list, CONFIG_DEFAULT_LANDING_ZONE, "user/dashboard", Configuration.Type.string
				, "Area to be shown to the user after login. Possible values are: "
						+ "user/dashboard, user/calendar, user/record, rooms/my, rooms/group, rooms/public, admin/user, admin/connection"
						+ ", admin/group, admin/room, admin/config, admin/lang, admin/ldap, admin/backup, admin/server, admin/oauth2", "2.1.x");

		// oauth2 params
		addCfg(list, CONFIG_IGNORE_BAD_SSL, String.valueOf(false), Configuration.Type.bool,
				"Set \"yes\" or \"no\" to enable/disable ssl certifications checking for OAuth2", VER_3_0);

		addCfg(list, CONFIG_REDIRECT_URL_FOR_EXTERNAL, "", Configuration.Type.string,
				"Users entered the room via invitationHash or secureHash will be redirected to this URL on connection lost", VER_3_0);
		addCfg(list, CONFIG_GOOGLE_ANALYTICS_CODE, null, Configuration.Type.string, "Code for Google Analytics", "3.1.0");
		addCfg(list, CONFIG_FLASH_SECURE, String.valueOf(false), Configuration.Type.bool, "Wether it should try to connect to rtmps first or not", VER_4_0_0);
		addCfg(list, CONFIG_FLASH_SECURE_PROXY, "none", Configuration.Type.string, "The setting for the NetConnection default settings is 'none'\n set to value 'best' if you are trying to use rtmp over native SSL", VER_4_0_0);
		addCfg(list, CONFIG_FLASH_VIDEO_CODEC, "h263", Configuration.Type.string, "Camera codecType, possible values: 'h263', 'h264'", VER_4_0_0);
		addCfg(list, CONFIG_FLASH_VIDEO_FPS, "30", Configuration.Type.number, "Camera FPS, should be positive number in range (0, 60]", VER_4_0_0);
		addCfg(list, CONFIG_FLASH_VIDEO_BANDWIDTH, "0", Configuration.Type.number, "An integer that specifies the maximum amount of bandwidth that the current outgoing video feed can use, in bytes per second. To specify that Flash video can use as much bandwidth as needed to maintain the value of frameQuality, pass 0 for bandwidth.", VER_4_0_0);
		addCfg(list, CONFIG_FLASH_CAM_QUALITY, "90", Configuration.Type.number, "An integer that specifies the required level of picture quality, as determined by the amount of compression being applied to each video frame. Acceptable values range from 1 (lowest quality, maximum compression) to 100 (highest quality, no compression). To specify that picture quality can vary as needed to avoid exceeding bandwidth, pass 0 for quality.", VER_4_0_0);
		addCfg(list, CONFIG_FLASH_MIC_RATE, "22", Configuration.Type.number, "The rate at which the microphone should capture sound, in kHz. Acceptable values are 5, 8, 11, 22, and 44. The default value is 22 kHz if your sound capture device supports this value.", VER_4_0_0);
		addCfg(list, CONFIG_FLASH_ECHO_PATH, "128", Configuration.Type.number, "Specifies the echo path length (in milliseconds). A longer echo path means better echo cancellation but also introduces longer delays and requires more processing power. The default value is 128; the only other possible value is 256. To disable AEC please specify 0.", VER_4_0_0);
		addCfg(list, CONFIG_HEADER_XFRAME, HEADER_XFRAME_SAMEORIGIN, Configuration.Type.string, "Value for 'X-Frame-Options' header (default: DENY), more info: https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/X-Frame-Options", VER_3_3_0);
		addCfg(list, CONFIG_HEADER_CSP, HEADER_CSP_SELF, Configuration.Type.string, String.format("Value for 'Content-Security-Policy' header (default: %s), have to be modified to enable Google analytics site: https://content-security-policy.com/", HEADER_CSP_SELF), VER_3_3_0);
		addCfg(list, CONFIG_EXT_PROCESS_TTL, String.valueOf(getExtProcessTtl()), Configuration.Type.number, String.format("Time to live in minutes for external processes such as conversion via ffmpeg (default %s minutes)", getExtProcessTtl()), VER_3_3_0);
		addCfg(list, CONFIG_MYROOMS_ENABLED, String.valueOf(true), Configuration.Type.bool, "Users are allowed to create personal rooms", "3.3.2");
		addCfg(list, CONFIG_REMINDER_MESSAGE, null, Configuration.Type.string, "Reminder message to notify about upcoming appointment, generated message will be used if not set", VER_2_0);
		addCfg(list, CONFIG_MP4_AUDIO_RATE, String.valueOf(getAudioRate()), Configuration.Type.number, "Audio sampling rate (in Hz) for MP4 video", "4.0.1");
		addCfg(list, CONFIG_MP4_AUDIO_BITRATE, String.valueOf(getAudioBitrate()), Configuration.Type.string, "Audio bitrate for MP4 video", "4.0.1");
		addCfg(list, CONFIG_REST_ALLOW_ORIGIN, null, Configuration.Type.string, "List of addresses browser Ajax REST requests are allowed from", "4.0.2");
		addCfg(list, CONFIG_FNAME_MIN_LENGTH, String.valueOf(USER_LOGIN_MINIMUM_LENGTH), Configuration.Type.number,
				"Number of chars needed in a user first name", "4.0.4");
		addCfg(list, CONFIG_LNAME_MIN_LENGTH, String.valueOf(USER_LOGIN_MINIMUM_LENGTH), Configuration.Type.number,
				"Number of chars needed in a user last name", "4.0.4");
		addCfg(list, CONFIG_CHAT_SEND_ON_ENTER, String.valueOf(false), Configuration.Type.bool,
				"Controls if chat message will be set on Enter (default: send on Ctrl+Enter)", "4.0.5");
		addCfg(list, CONFIG_MP4_VIDEO_PRESET, "medium", Configuration.Type.bool,
				"Preset (encoder optimization settings) to be used while performing mp4 conversion."
						+ "Valid values are: ultrafast, superfast, veryfast, faster, fast, medium, slow, slower, veryslow", "4.0.5");
		addCfg(list, CONFIG_DISPLAY_NAME_EDITABLE, String.valueOf(false), Configuration.Type.bool, "Is user will be able to edit his/her display name (default false).", "4.0.7");
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
		r.setInserted(new Date());
		r.setCapacity(capacity);
		r.setType(type);
		r.setIspublic(isPublic);
		r.setAllowUserQuestions(true);
		r.setAudioOnly(false);

		r.setAppointment(false);

		r.setDemoRoom(false);
		r.setDemoTime(null);

		r.hide(RoomElement.MicrophoneStatus);
		r.setModerated(false);

		r.setDeleted(false);

		r.setClosed(false);
		r.setRedirectURL(null);

		r.setOwnerId(null);

		r.setWaitForRecording(false);
		r.setAllowRecording(true);

		if (groupId != null) {
			RoomGroup ro = new RoomGroup();
			ro.setRoom(r);
			ro.setGroup(groupDao.get(groupId));
			r.getGroups().add(ro);
		}
		r = roomDao.update(r, null);
		return r;
	}

	public void loadDefaultRooms(boolean createRooms, long langId) {
		if (createRooms) {
			createRoom(LabelDao.getString("install.room.public.interview", langId), Type.interview, 16L, true, null);
			createRoom(LabelDao.getString("install.room.public.conference", langId), Type.conference, 32L, true, null);
			Room r = createRoom(LabelDao.getString("install.room.public.video.only", langId), Type.conference, 32L, true, null);
			r.hide(RoomElement.Whiteboard);
			roomDao.update(r, null);
			createRoom(LabelDao.getString("install.room.public.video.wb", langId), Type.conference, 32L, true, null);
			createRoom(LabelDao.getString("install.room.public.presentation", langId), Type.presentation, 100L, true, null);
			r = createRoom(LabelDao.getString("install.room.presentation.micro", langId), Type.presentation, 100L, true, null);
			r.getHiddenElements().clear();
			roomDao.update(r, null);

			r = createRoom(LabelDao.getString("install.room.conference.micro", langId), Type.conference, 32L, true, null);
			r.getHiddenElements().clear();
			roomDao.update(r, null);

			createRoom(LabelDao.getString("install.room.private.conference", langId), Type.conference, 32L, false, 1L);
		}
	}

	public void loadInitUserAndGroup(InstallationConfig cfg) throws Exception {
		// Add default group
		Group g = new Group();
		g.setName(cfg.getGroup());
		g.setInsertedby(1L);
		g.setDeleted(false);
		g.setInserted(new Date());
		g = groupDao.update(g, null);
		Configuration c = cfgDao.get(CONFIG_DEFAULT_GROUP_ID);
		c.setValueN(g.getId());
		cfgDao.update(c, null);

		User u = getNewUserInstance(null);
		u.setType(User.Type.user);
		u.getRights().add(Right.Admin);
		u.getRights().add(Right.Soap);
		u.setLogin(cfg.getUsername());
		u.setFirstname("firstname");
		u.setLastname("lastname");
		u.getAddress().setEmail(cfg.getEmail());
		u.getGroupUsers().add(new GroupUser(g, u));

		u = userDao.update(u, cfg.getPassword(), -1L);

		log.debug("Installation - User Added user-Id " + u.getId());

		if (u.getId() == null) {
			throw new InstallException("Unable to add user");
		}
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
				.setRequestKeyUrl("https://www.facebook.com/v2.10/dialog/oauth?client_id={$client_id}&redirect_uri={$redirect_uri}&scope=email")
				.setRequestTokenUrl("https://graph.facebook.com/v2.10/oauth/access_token")
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
				.setIconUrl("https://vk.com/images/safari_152.png")
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
