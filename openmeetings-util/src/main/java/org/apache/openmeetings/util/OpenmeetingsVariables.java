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
package org.apache.openmeetings.util;

import static org.apache.wicket.csp.CSPDirectiveSrcValue.SELF;
import static org.apache.wicket.csp.CSPDirectiveSrcValue.STRICT_DYNAMIC;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.openjson.JSONObject;

public class OpenmeetingsVariables {
	private static final Logger log = LoggerFactory.getLogger(OpenmeetingsVariables.class);
	public static final String ATTR_CLASS = "class";
	public static final String ATTR_TITLE = "title";
	public static final String ATTR_VALUE = "value";
	public static final String ATTR_DISABLED = "disabled";
	public static final String PARAM_USER_ID = "userId";
	public static final String PARAM_STATUS = "status";
	public static final String PARAM_SRC = "src";
	public static final String PARAM_SRC_UND = "_src";
	public static final String CONFIG_CRYPT = "crypt.class.name";
	public static final String CONFIG_DASHBOARD_SHOW_CHAT = "dashboard.show.chat";
	public static final String CONFIG_DASHBOARD_SHOW_MYROOMS = "dashboard.show.myrooms";
	public static final String CONFIG_DASHBOARD_SHOW_RSS = "dashboard.show.rssfeed";
	public static final String CONFIG_DASHBOARD_RSS_FEED1 = "dashboard.rss.feed1";
	public static final String CONFIG_DASHBOARD_RSS_FEED2 = "dashboard.rss.feed2";
	public static final String CONFIG_DEFAULT_LANG = "default.lang.id";
	public static final String CONFIG_DEFAULT_LANDING_ZONE = "default.landing.zone";
	public static final String CONFIG_DEFAULT_LDAP_ID = "default.ldap.id";
	public static final String CONFIG_DEFAULT_GROUP_ID = "default.group.id";
	public static final String CONFIG_DEFAULT_TIMEZONE = "default.timezone";
	public static final String CONFIG_REGISTER_FRONTEND = "allow.frontend.register";
	public static final String CONFIG_REGISTER_SOAP = "allow.soap.register";
	public static final String CONFIG_REGISTER_OAUTH = "allow.oauth.register";
	public static final String CONFIG_MAX_UPLOAD_SIZE = "max.upload.size";
	public static final String CONFIG_SIP_ENABLED = "sip.enable";
	public static final String CONFIG_SIP_ROOM_PREFIX = "sip.room.prefix";
	public static final String CONFIG_SIP_EXTEN_CONTEXT = "sip.exten.context";
	public static final String CONFIG_LOGIN_MIN_LENGTH = "user.login.minimum.length";
	public static final String CONFIG_PASS_MIN_LENGTH = "user.pass.minimum.length";
	public static final String CONFIG_PASS_CHECK_UPPER = "user.pass.check.upper";
	public static final String CONFIG_PASS_CHECK_DIGIT = "user.pass.check.digit";
	public static final String CONFIG_PASS_CHECK_SPECIAL = "user.pass.check.special";
	public static final String CONFIG_IGNORE_BAD_SSL = "oauth2.ignore.bad.ssl";
	public static final String CONFIG_REDIRECT_URL_FOR_EXTERNAL = "redirect.url.for.external.users";
	public static final String CONFIG_APPOINTMENT_REMINDER_MINUTES = "number.minutes.reminder.send";
	public static final String CONFIG_APPOINTMENT_PRE_START_MINUTES = "appointment.pre.start.minutes";
	public static final String CONFIG_APPLICATION_NAME = "application.name";
	public static final String CONFIG_APPLICATION_BASE_URL = "application.base.url";
	public static final String CONFIG_SCREENSHARING_QUALITY = "screensharing.default.quality";
	public static final String CONFIG_SCREENSHARING_FPS = "screensharing.default.fps";
	public static final String CONFIG_SCREENSHARING_FPS_SHOW = "screensharing.fps.show";
	public static final String CONFIG_SCREENSHARING_ALLOW_REMOTE = "screensharing.allow.remote";
	public static final String CONFIG_GOOGLE_ANALYTICS_CODE = "google.analytics.code";
	public static final String CONFIG_SMTP_SERVER = "mail.smtp.server";
	public static final String CONFIG_SMTP_PORT = "mail.smtp.port";
	public static final String CONFIG_SMTP_USER = "mail.smtp.user";
	public static final String CONFIG_SMTP_PASS = "mail.smtp.pass";
	public static final String CONFIG_SMTP_SYSTEM_EMAIL = "mail.smtp.system.email";
	public static final String CONFIG_SMTP_TLS = "mail.smtp.starttls.enable";
	public static final String CONFIG_SMTP_SSL = "mail.smtp.ssl.enable";
	public static final String CONFIG_SMTP_TIMEOUT_CON = "mail.smtp.connection.timeout";
	public static final String CONFIG_SMTP_TIMEOUT = "mail.smtp.timeout";
	public static final String CONFIG_PATH_IMAGEMAGIC = "path.imagemagick";
	public static final String CONFIG_PATH_SOX = "path.sox";
	public static final String CONFIG_PATH_FFMPEG = "path.ffmpeg";
	public static final String CONFIG_PATH_OFFICE = "path.office";
	public static final String CONFIG_DOCUMENT_DPI = "document.dpi";
	public static final String CONFIG_DOCUMENT_QUALITY = "document.quality";
	public static final String CONFIG_CAM_FPS = "cam.fps";
	public static final String CONFIG_MIC_RATE = "mic.rate";
	public static final String CONFIG_MIC_ECHO = "mic.echo.cancellation";
	public static final String CONFIG_MIC_NOISE = "mic.noise.suppression";
	public static final String CONFIG_EXT_PROCESS_TTL = "external.process.ttl";
	public static final String CONFIG_EMAIL_AT_REGISTER = "send.email.at.register";
	public static final String CONFIG_EMAIL_VERIFICATION = "send.email.with.verfication";
	public static final String CONFIG_CALENDAR_ROOM_CAPACITY = "calendar.conference.rooms.default.size";
	public static final String CONFIG_REPLY_TO_ORGANIZER = "inviter.email.as.replyto";
	public static final String CONFIG_KEYCODE_ARRANGE = "video.arrange.keycode";
	public static final String CONFIG_KEYCODE_MUTE_OTHERS = "mute.others.keycode";
	public static final String CONFIG_KEYCODE_MUTE = "mute.keycode";
	public static final String CONFIG_MYROOMS_ENABLED = "personal.rooms.enabled";
	public static final String CONFIG_REMINDER_MESSAGE = "reminder.message";
	public static final String CONFIG_MP4_AUDIO_RATE = "mp4.audio.rate";
	public static final String CONFIG_MP4_AUDIO_BITRATE = "mp4.audio.bitrate";
	public static final String CONFIG_MP4_VIDEO_PRESET = "mp4.video.preset";
	public static final String CONFIG_REST_ALLOW_ORIGIN = "rest.allow.origin";
	public static final String CONFIG_FNAME_MIN_LENGTH = "user.fname.minimum.length";
	public static final String CONFIG_LNAME_MIN_LENGTH = "user.lname.minimum.length";
	public static final String CONFIG_CHAT_SEND_ON_ENTER = "chat.send.on.enter";
	public static final String CONFIG_DISPLAY_NAME_EDITABLE = "display.name.editable";
	public static final String CONFIG_KEYCODE_QUICKPOLL = "start.quickpoll.keycode";
	public static final String CONFIG_AUTO_OPEN_SHARING = "auto.open.sharing";
	public static final String CONFIG_KEYCODE_ARRANGE_RESIZE = "video.arrange.resize.keycode";
	public static final String CONFIG_CSP_FONT = "header.csp.font";
	public static final String CONFIG_CSP_FRAME = "header.csp.frame";
	public static final String CONFIG_CSP_IMAGE = "header.csp.image";
	public static final String CONFIG_CSP_MEDIA = "header.csp.media";
	public static final String CONFIG_CSP_SCRIPT = "header.csp.script";
	public static final String CONFIG_CSP_STYLE = "header.csp.style";
	public static final String CONFIG_CSP_ENABLED = "header.csp.enabled";
	public static final String CONFIG_RECORDING_ENABLED = "recording.enabled";
	public static final String CONFIG_THEME = "ui.theme";
	public static final String CONFIG_OTP_ENABLED = "otp.enabled";

	public static final int RECENT_ROOMS_COUNT = 5;
	public static final int USER_LOGIN_MINIMUM_LENGTH = 4;
	public static final int USER_PASSWORD_MINIMUM_LENGTH = 8;
	public static final String DEFAULT_APP_NAME = "OpenMeetings";
	public static final String DEFAULT_CONTEXT_NAME = "openmeetings";
	public static final long DEFAULT_MAX_UPLOAD_SIZE = 100 * 1024 * 1024L; // 100MB
	public static final String DEFAULT_BASE_URL = "https://localhost:5443/openmeetings/";
	public static final String DEFAULT_SIP_CONTEXT = "rooms";
	public static final String DEFAULT_CSP_FONT = "https://fonts.gstatic.com";
	public static final String DEFAULT_CSP_STYLE = SELF.getValue() + ",https://fonts.googleapis.com/css2";
	public static final String DEFAULT_CSP_DATA = SELF.getValue() + ",data:";

	private static String cryptClassName = null;
	private static String wicketApplicationName = null;
	private static String applicationName = null;
	private static int extProcessTtl = 20;
	private static int minLoginLength = USER_LOGIN_MINIMUM_LENGTH;
	private static int minPasswdLength = USER_PASSWORD_MINIMUM_LENGTH;
	private static boolean pwdCheckUpper = true;
	private static boolean pwdCheckDigit = true;
	private static boolean pwdCheckSpecial = true;
	private static JSONObject roomSettings = new JSONObject();
	private static boolean initComplete = false;
	private static long maxUploadSize = DEFAULT_MAX_UPLOAD_SIZE;
	private static String baseUrl = DEFAULT_BASE_URL;
	private static boolean sipEnabled = false;
	private static String gaCode = null;
	private static Long defaultLang = 1L;
	private static Long defaultGroup = 1L;
	private static int audioRate = 22050;
	private static String audioBitrate = "32k";
	private static String videoPreset = "medium";
	private static String defaultTimezone = "Europe/Berlin";
	private static String restAllowOrigin = null;
	private static Class<?> appClass = null;
	private static String sipContext = DEFAULT_SIP_CONTEXT;
	private static int minFnameLength = USER_LOGIN_MINIMUM_LENGTH;
	private static int minLnameLength = USER_LOGIN_MINIMUM_LENGTH;
	private static boolean chatSendOnEnter = false;
	private static boolean allowRegisterFrontend = false;
	private static boolean allowRegisterSoap = false;
	private static boolean allowRegisterOauth = false;
	private static boolean sendVerificationEmail = false;
	private static boolean sendRegisterEmail = false;
	private static boolean displayNameEditable = false;
	private static boolean myRoomsEnabled = true;
	private static String cspFontSrc = DEFAULT_CSP_FONT;
	private static String cspFrameSrc = SELF.getValue();
	private static String cspImageSrc = DEFAULT_CSP_DATA;
	private static String cspMediaSrc = DEFAULT_CSP_DATA;
	private static String cspScriptSrc = STRICT_DYNAMIC.getValue();
	private static String cspStyleSrc = DEFAULT_CSP_STYLE;
	private static String smtpServer;
	private static int smtpPort;
	private static boolean smtpUseTls;
	private static boolean smtpUseSsl;
	private static String smtpUser;
	private static String smtpPass;
	private static int smtpTimeOut;
	private static int smtpConnectionTimeOut;
	private static String mailFrom;
	private static boolean mailAddReplyTo;
	private static int appointmentReminderMinutes = 15;
	private static int appointmentPreStartMinutes = 5;
	private static boolean recordingsEnabled = true;
	private static String theme = "Sandstone";
	private static boolean otpEnabled = false;

	private OpenmeetingsVariables() {}

	public static String getApplicationName() {
		return applicationName;
	}

	public static void setApplicationName(String name) {
		applicationName = name;
	}

	public static JSONObject getRoomSettings() {
		return new JSONObject(roomSettings.toString());
	}

	public static void setRoomSettings(JSONObject s) {
		roomSettings = s;
	}

	public static String getCryptClassName() {
		return cryptClassName;
	}

	public static void setCryptClassName(String name) {
		cryptClassName = name;
	}

	public static int getExtProcessTtl() {
		return extProcessTtl;
	}

	public static void setExtProcessTtl(int ttl) {
		extProcessTtl = ttl;
	}

	public static boolean isInitComplete() {
		return initComplete;
	}

	public static void setInitComplete(boolean complete) {
		initComplete = complete;
	}

	public static String getWicketApplicationName() {
		return wicketApplicationName;
	}

	public static void setWicketApplicationName(String name) {
		wicketApplicationName = name;
	}

	/**
	 *
	 * @return returns the max upload size configured by max_upload_size config key
	 */
	public static long getMaxUploadSize() {
		return maxUploadSize;
	}

	public static void setMaxUploadSize(long size) {
		maxUploadSize = size;
	}

	public static String getBaseUrl() {
		return baseUrl;
	}

	private static URI getWebappPath(String url) {
		return URI.create(URI.create(url + "/").normalize().getPath());
	}

	public static URI getWebappPath() {
		try {
			return getWebappPath(baseUrl);
		} catch (Exception e) {
			log.warn("Error getting baseURL");
			return getWebappPath(DEFAULT_BASE_URL);
		}
	}

	public static void setBaseUrl(String url) {
		baseUrl = url;
	}

	public static boolean isSipEnabled() {
		return sipEnabled;
	}

	public static void setSipEnabled(boolean enabled) {
		sipEnabled = enabled;
	}

	public static String getGaCode() {
		return gaCode;
	}

	public static void setGaCode(String code) {
		gaCode = code;
	}

	public static Long getDefaultLang() {
		return defaultLang;
	}

	public static void setDefaultLang(Long lang) {
		defaultLang = lang;
	}

	public static int getAudioRate() {
		return audioRate;
	}

	public static void setAudioRate(int rate) {
		audioRate = rate;
	}

	public static String getAudioBitrate() {
		return audioBitrate;
	}

	public static void setAudioBitrate(String bitrate) {
		audioBitrate = bitrate;
	}

	public static String getVideoPreset() {
		return videoPreset;
	}

	public static void setVideoPreset(String preset) {
		videoPreset = preset;
	}

	public static String getDefaultTimezone() {
		return defaultTimezone;
	}

	public static void setDefaultTimezone(String timezone) {
		defaultTimezone = timezone;
	}

	public static String getRestAllowOrigin() {
		return restAllowOrigin;
	}

	public static void setRestAllowOrigin(String allowOrigin) {
		restAllowOrigin = allowOrigin;
	}

	public static synchronized Class<?> getAppClass() throws ClassNotFoundException {
		if (appClass == null) {
			//HACK to resolve package dependencies
			appClass = Class.forName("org.apache.openmeetings.web.app.Application");
		}
		return appClass;
	}

	public static int getMinLoginLength() {
		return minLoginLength;
	}

	public static void setMinLoginLength(int length) {
		minLoginLength = length;
	}

	public static int getMinPasswdLength() {
		return minPasswdLength;
	}

	public static void setMinPasswdLength(int length) {
		minPasswdLength = length;
	}

	public static boolean isPwdCheckUpper() {
		return pwdCheckUpper;
	}

	public static void setPwdCheckUpper(boolean check) {
		pwdCheckUpper = check;
	}

	public static boolean isPwdCheckDigit() {
		return pwdCheckDigit;
	}

	public static void setPwdCheckDigit(boolean check) {
		pwdCheckDigit = check;
	}

	public static boolean isPwdCheckSpecial() {
		return pwdCheckSpecial;
	}

	public static void setPwdCheckSpecial(boolean check) {
		pwdCheckSpecial = check;
	}

	public static Long getDefaultGroup() {
		return defaultGroup;
	}

	public static void setDefaultGroup(Long group) {
		defaultGroup = group;
	}

	public static String getSipContext() {
		return sipContext;
	}

	public static void setSipContext(String context) {
		sipContext = context;
	}

	public static int getMinFnameLength() {
		return minFnameLength;
	}

	public static void setMinFnameLength(int length) {
		minFnameLength = length;
	}

	public static int getMinLnameLength() {
		return minLnameLength;
	}

	public static void setMinLnameLength(int length) {
		minLnameLength = length;
	}

	public static boolean isChatSendOnEnter() {
		return chatSendOnEnter;
	}

	public static void setChatSendOnEnter(boolean sendOnEnter) {
		chatSendOnEnter = sendOnEnter;
	}

	public static boolean isAllowRegisterFrontend() {
		return allowRegisterFrontend;
	}

	public static void setAllowRegisterFrontend(boolean allow) {
		allowRegisterFrontend = allow;
	}

	public static boolean isAllowRegisterSoap() {
		return allowRegisterSoap;
	}

	public static void setAllowRegisterSoap(boolean allow) {
		allowRegisterSoap = allow;
	}

	public static boolean isAllowRegisterOauth() {
		return allowRegisterOauth;
	}

	public static void setAllowRegisterOauth(boolean allow) {
		allowRegisterOauth = allow;
	}

	public static boolean isSendVerificationEmail() {
		return sendVerificationEmail;
	}

	public static void setSendVerificationEmail(boolean send) {
		sendVerificationEmail = send;
	}

	public static boolean isSendRegisterEmail() {
		return sendRegisterEmail;
	}

	public static void setSendRegisterEmail(boolean send) {
		sendRegisterEmail = send;
	}

	public static boolean isDisplayNameEditable() {
		return displayNameEditable;
	}

	public static void setDisplayNameEditable(boolean editable) {
		displayNameEditable = editable;
	}

	public static boolean isMyRoomsEnabled() {
		return myRoomsEnabled;
	}

	public static void setMyRoomsEnabled(boolean enabled) {
		myRoomsEnabled = enabled;
	}

	public static String getCspFontSrc() {
		return cspFontSrc;
	}

	public static void setCspFontSrc(String src) {
		cspFontSrc = src;
	}

	public static String getCspFrameSrc() {
		return cspFrameSrc;
	}

	public static void setCspFrameSrc(String src) {
		cspFrameSrc = src;
	}

	public static String getCspImageSrc() {
		return cspImageSrc;
	}

	public static void setCspImageSrc(String src) {
		cspImageSrc = src;
	}

	public static String getCspMediaSrc() {
		return cspMediaSrc;
	}

	public static void setCspMediaSrc(String src) {
		cspMediaSrc = src;
	}

	public static String getCspScriptSrc() {
		return cspScriptSrc;
	}

	public static void setCspScriptSrc(String src) {
		cspScriptSrc = src;
	}

	public static String getCspStyleSrc() {
		return cspStyleSrc;
	}

	public static void setCspStyleSrc(String src) {
		cspStyleSrc = src;
	}


	public static String getSmtpServer() {
		return smtpServer;
	}

	public static void setSmtpServer(String server) {
		smtpServer = server;
	}

	public static int getSmtpPort() {
		return smtpPort;
	}

	public static void setSmtpPort(int port) {
		smtpPort = port;
	}

	public static boolean isSmtpUseTls() {
		return smtpUseTls;
	}

	public static void setSmtpUseTls(boolean useTls) {
		smtpUseTls = useTls;
	}

	public static boolean isSmtpUseSsl() {
		return smtpUseSsl;
	}

	public static void setSmtpUseSsl(boolean useSsl) {
		smtpUseSsl = useSsl;
	}

	public static String getSmtpUser() {
		return smtpUser;
	}

	public static void setSmtpUser(String user) {
		smtpUser = user;
	}

	public static String getSmtpPass() {
		return smtpPass;
	}

	public static void setSmtpPass(String pass) {
		smtpPass = pass;
	}

	public static int getSmtpTimeOut() {
		return smtpTimeOut;
	}

	public static void setSmtpTimeOut(int timeOut) {
		smtpTimeOut = timeOut;
	}

	public static int getSmtpConnectionTimeOut() {
		return smtpConnectionTimeOut;
	}

	public static void setSmtpConnectionTimeOut(int timeOut) {
		smtpConnectionTimeOut = timeOut;
	}

	public static String getMailFrom() {
		return mailFrom;
	}

	public static void setMailFrom(String from) {
		mailFrom = from;
	}

	public static boolean isMailAddReplyTo() {
		return mailAddReplyTo;
	}

	public static void setMailAddReplyTo(boolean addReplyTo) {
		mailAddReplyTo = addReplyTo;
	}

	public static int getAppointmentPreStartMinutes() {
		return appointmentPreStartMinutes;
	}

	public static void setAppointmentPreStartMinutes(int minutes) {
		appointmentPreStartMinutes = minutes;
	}

	public static int getAppointmentReminderMinutes() {
		return appointmentReminderMinutes;
	}

	public static void setAppointmentReminderMinutes(int minutes) {
		appointmentReminderMinutes = minutes;
	}

	public static boolean isRecordingsEnabled() {
		return recordingsEnabled;
	}

	public static void setRecordingsEnabled(boolean enabled) {
		recordingsEnabled = enabled;
	}

	public static String getTheme() {
		return theme;
	}

	public static void setTheme(String inTheme) {
		theme = inTheme;
	}

	public static boolean isOtpEnabled() {
		return otpEnabled;
	}

	public static void setOtpEnabled(boolean enabled) {
		otpEnabled = enabled;
	}
}
