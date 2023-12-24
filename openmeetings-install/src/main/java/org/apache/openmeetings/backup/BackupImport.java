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
package org.apache.openmeetings.backup;

import static java.util.Map.entry;
import static java.util.UUID.randomUUID;
import static org.apache.openmeetings.db.bind.Constants.APPOINTMENT_LIST_NODE;
import static org.apache.openmeetings.db.bind.Constants.APPOINTMENT_NODE;
import static org.apache.openmeetings.db.bind.Constants.CALENDAR_LIST_NODE;
import static org.apache.openmeetings.db.bind.Constants.CALENDAR_NODE;
import static org.apache.openmeetings.db.bind.Constants.CFG_LIST_NODE;
import static org.apache.openmeetings.db.bind.Constants.CFG_NODE;
import static org.apache.openmeetings.db.bind.Constants.CHAT_LIST_NODE;
import static org.apache.openmeetings.db.bind.Constants.CHAT_NODE;
import static org.apache.openmeetings.db.bind.Constants.CONTACT_LIST_NODE;
import static org.apache.openmeetings.db.bind.Constants.CONTACT_NODE;
import static org.apache.openmeetings.db.bind.Constants.EXTRA_MENU_LIST_NODE;
import static org.apache.openmeetings.db.bind.Constants.EXTRA_MENU_NODE;
import static org.apache.openmeetings.db.bind.Constants.FILE_LIST_NODE;
import static org.apache.openmeetings.db.bind.Constants.FILE_NODE;
import static org.apache.openmeetings.db.bind.Constants.GROUP_LIST_NODE;
import static org.apache.openmeetings.db.bind.Constants.GROUP_NODE;
import static org.apache.openmeetings.db.bind.Constants.MMEMBER_LIST_NODE;
import static org.apache.openmeetings.db.bind.Constants.MMEMBER_NODE;
import static org.apache.openmeetings.db.bind.Constants.MSG_FOLDER_LIST_NODE;
import static org.apache.openmeetings.db.bind.Constants.MSG_FOLDER_NODE;
import static org.apache.openmeetings.db.bind.Constants.MSG_LIST_NODE;
import static org.apache.openmeetings.db.bind.Constants.MSG_NODE;
import static org.apache.openmeetings.db.bind.Constants.OAUTH_LIST_NODE;
import static org.apache.openmeetings.db.bind.Constants.OAUTH_NODE;
import static org.apache.openmeetings.db.bind.Constants.POLL_LIST_NODE;
import static org.apache.openmeetings.db.bind.Constants.POLL_NODE;
import static org.apache.openmeetings.db.bind.Constants.RECORDING_LIST_NODE;
import static org.apache.openmeetings.db.bind.Constants.RECORDING_NODE;
import static org.apache.openmeetings.db.bind.Constants.ROOM_FILE_LIST_NODE;
import static org.apache.openmeetings.db.bind.Constants.ROOM_FILE_NODE;
import static org.apache.openmeetings.db.bind.Constants.ROOM_GRP_LIST_NODE;
import static org.apache.openmeetings.db.bind.Constants.ROOM_GRP_NODE;
import static org.apache.openmeetings.db.bind.Constants.ROOM_LIST_NODE;
import static org.apache.openmeetings.db.bind.Constants.ROOM_NODE;
import static org.apache.openmeetings.db.bind.Constants.USER_LIST_NODE;
import static org.apache.openmeetings.db.bind.Constants.USER_NODE;
import static org.apache.openmeetings.db.bind.Constants.VERSION_LIST_NODE;
import static org.apache.openmeetings.db.bind.Constants.VERSION_NODE;
import static org.apache.openmeetings.db.entity.user.PrivateMessage.INBOX_FOLDER_ID;
import static org.apache.openmeetings.db.entity.user.PrivateMessage.SENT_FOLDER_ID;
import static org.apache.openmeetings.db.entity.user.PrivateMessage.TRASH_FOLDER_ID;
import static org.apache.openmeetings.util.OmFileHelper.BCKP_RECORD_FILES;
import static org.apache.openmeetings.util.OmFileHelper.BCKP_ROOM_FILES;
import static org.apache.openmeetings.util.OmFileHelper.CSS_DIR;
import static org.apache.openmeetings.util.OmFileHelper.EXTENSION_CSS;
import static org.apache.openmeetings.util.OmFileHelper.EXTENSION_JPG;
import static org.apache.openmeetings.util.OmFileHelper.EXTENSION_MP4;
import static org.apache.openmeetings.util.OmFileHelper.EXTENSION_PNG;
import static org.apache.openmeetings.util.OmFileHelper.FILES_DIR;
import static org.apache.openmeetings.util.OmFileHelper.FILE_NAME_FMT;
import static org.apache.openmeetings.util.OmFileHelper.GROUP_CSS_PREFIX;
import static org.apache.openmeetings.util.OmFileHelper.GROUP_LOGO_DIR;
import static org.apache.openmeetings.util.OmFileHelper.GROUP_LOGO_PREFIX;
import static org.apache.openmeetings.util.OmFileHelper.PROFILES_DIR;
import static org.apache.openmeetings.util.OmFileHelper.PROFILES_PREFIX;
import static org.apache.openmeetings.util.OmFileHelper.RECORDING_FILE_NAME;
import static org.apache.openmeetings.util.OmFileHelper.WML_DIR;
import static org.apache.openmeetings.util.OmFileHelper.getCssDir;
import static org.apache.openmeetings.util.OmFileHelper.getFileExt;
import static org.apache.openmeetings.util.OmFileHelper.getFileName;
import static org.apache.openmeetings.util.OmFileHelper.getGroupCss;
import static org.apache.openmeetings.util.OmFileHelper.getGroupLogo;
import static org.apache.openmeetings.util.OmFileHelper.getName;
import static org.apache.openmeetings.util.OmFileHelper.getStreamsHibernateDir;
import static org.apache.openmeetings.util.OmFileHelper.getUploadFilesDir;
import static org.apache.openmeetings.util.OmFileHelper.getUploadProfilesUserDir;
import static org.apache.openmeetings.util.OmFileHelper.getUploadWmlDir;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_APPOINTMENT_REMINDER_MINUTES;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_CALENDAR_ROOM_CAPACITY;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_CAM_FPS;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_CRYPT;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_CSP_FRAME;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DASHBOARD_RSS_FEED1;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DASHBOARD_RSS_FEED2;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DASHBOARD_SHOW_CHAT;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DASHBOARD_SHOW_MYROOMS;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DASHBOARD_SHOW_RSS;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DEFAULT_GROUP_ID;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DEFAULT_LANG;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DEFAULT_LDAP_ID;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DOCUMENT_DPI;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DOCUMENT_QUALITY;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_EMAIL_AT_REGISTER;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_EMAIL_VERIFICATION;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_EXT_PROCESS_TTL;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_IGNORE_BAD_SSL;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_KEYCODE_ARRANGE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_KEYCODE_MUTE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_KEYCODE_MUTE_OTHERS;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_LOGIN_MIN_LENGTH;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_MAX_UPLOAD_SIZE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_MIC_ECHO;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_MIC_NOISE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_MIC_RATE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_MYROOMS_ENABLED;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_PASS_MIN_LENGTH;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_PATH_FFMPEG;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_PATH_IMAGEMAGIC;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_PATH_OFFICE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_PATH_SOX;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_RECORDING_ENABLED;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_REGISTER_FRONTEND;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_REGISTER_OAUTH;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_REGISTER_SOAP;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_REMINDER_MESSAGE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_REPLY_TO_ORGANIZER;
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
import static org.apache.openmeetings.util.OpenmeetingsVariables.getDefaultTimezone;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getMinLoginLength;

import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import jakarta.inject.Inject;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.text.WordUtils;
import org.apache.openmeetings.backup.converter.WbConverter;
import org.apache.openmeetings.core.converter.DocumentConverter;
import org.apache.openmeetings.db.bind.adapter.AppointmentAdapter;
import org.apache.openmeetings.db.bind.adapter.FileAdapter;
import org.apache.openmeetings.db.bind.adapter.GroupAdapter;
import org.apache.openmeetings.db.bind.adapter.OmCalendarAdapter;
import org.apache.openmeetings.db.bind.adapter.RoomAdapter;
import org.apache.openmeetings.db.bind.adapter.UserAdapter;
import org.apache.openmeetings.db.dao.basic.ChatDao;
import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dao.calendar.AppointmentDao;
import org.apache.openmeetings.db.dao.calendar.MeetingMemberDao;
import org.apache.openmeetings.db.dao.calendar.OmCalendarDao;
import org.apache.openmeetings.db.dao.file.BaseFileItemDao;
import org.apache.openmeetings.db.dao.file.FileItemDao;
import org.apache.openmeetings.db.dao.record.RecordingDao;
import org.apache.openmeetings.db.dao.room.ExtraMenuDao;
import org.apache.openmeetings.db.dao.room.PollDao;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.dao.server.LdapConfigDao;
import org.apache.openmeetings.db.dao.server.OAuth2Dao;
import org.apache.openmeetings.db.dao.user.GroupDao;
import org.apache.openmeetings.db.dao.user.PrivateMessageDao;
import org.apache.openmeetings.db.dao.user.PrivateMessageFolderDao;
import org.apache.openmeetings.db.dao.user.UserContactDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.dto.room.Whiteboard;
import org.apache.openmeetings.db.entity.basic.ChatMessage;
import org.apache.openmeetings.db.entity.basic.Configuration;
import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.db.entity.calendar.MeetingMember;
import org.apache.openmeetings.db.entity.calendar.OmCalendar;
import org.apache.openmeetings.db.entity.file.BaseFileItem;
import org.apache.openmeetings.db.entity.file.FileItem;
import org.apache.openmeetings.db.entity.record.Recording;
import org.apache.openmeetings.db.entity.record.RecordingChunk;
import org.apache.openmeetings.db.entity.room.ExtraMenu;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.room.RoomFile;
import org.apache.openmeetings.db.entity.room.RoomGroup;
import org.apache.openmeetings.db.entity.room.RoomModerator;
import org.apache.openmeetings.db.entity.room.RoomPoll;
import org.apache.openmeetings.db.entity.room.RoomPollAnswer;
import org.apache.openmeetings.db.entity.server.LdapConfig;
import org.apache.openmeetings.db.entity.server.OAuthServer;
import org.apache.openmeetings.db.entity.user.Group;
import org.apache.openmeetings.db.entity.user.GroupUser;
import org.apache.openmeetings.db.entity.user.PrivateMessage;
import org.apache.openmeetings.db.entity.user.PrivateMessageFolder;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.entity.user.UserContact;
import org.apache.openmeetings.db.util.AuthLevelUtil;
import org.apache.openmeetings.db.util.XmlHelper;
import org.apache.openmeetings.util.OmFileHelper;
import org.apache.openmeetings.util.StoredFile;
import org.apache.openmeetings.util.crypt.SCryptImplementation;
import org.apache.wicket.util.string.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

@Component
public class BackupImport {
	private static final Logger log = LoggerFactory.getLogger(BackupImport.class);
	private static final Map<String, String> outdatedConfigKeys = Map.ofEntries(
			entry("crypt_ClassName", CONFIG_CRYPT)
			, entry("system_email_addr", CONFIG_SMTP_SYSTEM_EMAIL)
			, entry("smtp_server", CONFIG_SMTP_SERVER)
			, entry("smtp_port", CONFIG_SMTP_PORT)
			, entry("email_username", CONFIG_SMTP_USER)
			, entry("email_userpass", CONFIG_SMTP_PASS)
			, entry("default_lang_id", CONFIG_DEFAULT_LANG)
			, entry("allow_frontend_register", CONFIG_REGISTER_FRONTEND)
			, entry("max_upload_size", CONFIG_MAX_UPLOAD_SIZE)
			, entry("rss_feed1", CONFIG_DASHBOARD_RSS_FEED1)
			, entry("rss_feed2", CONFIG_DASHBOARD_RSS_FEED2)
			, entry("oauth2.ignore_bad_ssl", CONFIG_IGNORE_BAD_SSL)
			, entry("default.quality.screensharing", CONFIG_SCREENSHARING_QUALITY)
			, entry("default.fps.screensharing", CONFIG_SCREENSHARING_FPS)
			, entry("ldap_default_id", CONFIG_DEFAULT_LDAP_ID)
			, entry("default_group_id", CONFIG_DEFAULT_GROUP_ID)
			, entry("imagemagick_path", CONFIG_PATH_IMAGEMAGIC)
			, entry("sox_path", CONFIG_PATH_SOX)
			, entry("ffmpeg_path", CONFIG_PATH_FFMPEG)
			, entry("office.path", CONFIG_PATH_OFFICE)
			, entry("red5sip.enable", CONFIG_SIP_ENABLED)
			, entry("red5sip.room_prefix", CONFIG_SIP_ROOM_PREFIX)
			, entry("red5sip.exten_context", CONFIG_SIP_EXTEN_CONTEXT)
			, entry("sendEmailAtRegister", CONFIG_EMAIL_AT_REGISTER)
			, entry("sendEmailWithVerficationCode", CONFIG_EMAIL_VERIFICATION)
			, entry("swftools_zoom", CONFIG_DOCUMENT_DPI)
			, entry("swftools_jpegquality", CONFIG_DOCUMENT_QUALITY)
			, entry("sms.subject", CONFIG_REMINDER_MESSAGE)
			, entry("exclusive.audio.keycode", CONFIG_KEYCODE_MUTE_OTHERS)
			, entry("header.csp.frame.options", CONFIG_CSP_FRAME)
			);
	private static final Map<String, Configuration.Type> configTypes = Map.ofEntries(
			entry(CONFIG_REGISTER_FRONTEND, Configuration.Type.BOOL)
			, entry(CONFIG_REGISTER_SOAP, Configuration.Type.BOOL)
			, entry(CONFIG_REGISTER_OAUTH, Configuration.Type.BOOL)
			, entry(CONFIG_SMTP_TLS, Configuration.Type.BOOL)
			, entry(CONFIG_EMAIL_AT_REGISTER, Configuration.Type.BOOL)
			, entry(CONFIG_EMAIL_VERIFICATION, Configuration.Type.BOOL)
			, entry(CONFIG_SIP_ENABLED, Configuration.Type.BOOL)
			, entry(CONFIG_SCREENSHARING_FPS_SHOW, Configuration.Type.BOOL)
			, entry(CONFIG_SCREENSHARING_ALLOW_REMOTE, Configuration.Type.BOOL)
			, entry(CONFIG_DASHBOARD_SHOW_MYROOMS, Configuration.Type.BOOL)
			, entry(CONFIG_DASHBOARD_SHOW_CHAT, Configuration.Type.BOOL)
			, entry(CONFIG_DASHBOARD_SHOW_RSS, Configuration.Type.BOOL)
			, entry(CONFIG_REPLY_TO_ORGANIZER, Configuration.Type.BOOL)
			, entry(CONFIG_IGNORE_BAD_SSL, Configuration.Type.BOOL)
			, entry(CONFIG_MYROOMS_ENABLED, Configuration.Type.BOOL)
			, entry(CONFIG_DEFAULT_GROUP_ID, Configuration.Type.NUMBER)
			, entry(CONFIG_SMTP_PORT, Configuration.Type.NUMBER)
			, entry(CONFIG_SMTP_TIMEOUT_CON, Configuration.Type.NUMBER)
			, entry(CONFIG_SMTP_TIMEOUT, Configuration.Type.NUMBER)
			, entry(CONFIG_DEFAULT_LANG, Configuration.Type.NUMBER)
			, entry(CONFIG_DOCUMENT_DPI, Configuration.Type.NUMBER)
			, entry(CONFIG_DOCUMENT_QUALITY, Configuration.Type.NUMBER)
			, entry(CONFIG_SCREENSHARING_QUALITY, Configuration.Type.NUMBER)
			, entry(CONFIG_SCREENSHARING_FPS, Configuration.Type.NUMBER)
			, entry(CONFIG_MAX_UPLOAD_SIZE, Configuration.Type.NUMBER)
			, entry(CONFIG_APPOINTMENT_REMINDER_MINUTES, Configuration.Type.NUMBER)
			, entry(CONFIG_LOGIN_MIN_LENGTH, Configuration.Type.NUMBER)
			, entry(CONFIG_PASS_MIN_LENGTH, Configuration.Type.NUMBER)
			, entry(CONFIG_CALENDAR_ROOM_CAPACITY, Configuration.Type.NUMBER)
			, entry(CONFIG_KEYCODE_ARRANGE, Configuration.Type.HOTKEY)
			, entry(CONFIG_KEYCODE_MUTE_OTHERS, Configuration.Type.HOTKEY)
			, entry(CONFIG_KEYCODE_MUTE, Configuration.Type.HOTKEY)
			, entry(CONFIG_DEFAULT_LDAP_ID, Configuration.Type.NUMBER)
			, entry(CONFIG_CAM_FPS, Configuration.Type.NUMBER)
			, entry(CONFIG_MIC_RATE, Configuration.Type.NUMBER)
			, entry(CONFIG_MIC_ECHO, Configuration.Type.BOOL)
			, entry(CONFIG_MIC_NOISE, Configuration.Type.BOOL)
			, entry(CONFIG_EXT_PROCESS_TTL, Configuration.Type.NUMBER)
			, entry(CONFIG_RECORDING_ENABLED, Configuration.Type.BOOL)
			// ConfigForm.PATHS should also be updated
			, entry(CONFIG_PATH_FFMPEG, Configuration.Type.PATH)
			, entry(CONFIG_PATH_IMAGEMAGIC, Configuration.Type.PATH)
			, entry(CONFIG_PATH_OFFICE, Configuration.Type.PATH)
			, entry(CONFIG_PATH_SOX, Configuration.Type.PATH)
			);
	private static final Pattern UUID_PATTERN = Pattern.compile("^[\\da-f]{8}(?:-[\\da-f]{4}){3}-[\\da-f]{12}$");

	@Inject
	private AppointmentDao appointmentDao;
	@Inject
	private OmCalendarDao calendarDao;
	@Inject
	private RoomDao roomDao;
	@Inject
	private UserDao userDao;
	@Inject
	private RecordingDao recordingDao;
	@Inject
	private PrivateMessageFolderDao privateMessageFolderDao;
	@Inject
	private PrivateMessageDao privateMessageDao;
	@Inject
	private MeetingMemberDao meetingMemberDao;
	@Inject
	private LdapConfigDao ldapConfigDao;
	@Inject
	private FileItemDao fileItemDao;
	@Inject
	private UserContactDao userContactDao;
	@Inject
	private PollDao pollDao;
	@Inject
	private ConfigurationDao cfgDao;
	@Inject
	private ChatDao chatDao;
	@Inject
	private OAuth2Dao auth2Dao;
	@Inject
	private GroupDao groupDao;
	@Inject
	private ExtraMenuDao menuDao;
	@Inject
	private DocumentConverter docConverter;

	private final Map<Long, Long> ldapMap = new HashMap<>();
	private final Map<Long, Long> oauthMap = new HashMap<>();
	private final Map<Long, Long> userMap = new HashMap<>();
	private final Map<Long, Long> groupMap = new HashMap<>();
	private final Map<Long, Long> calendarMap = new HashMap<>();
	private final Map<Long, Long> appointmentMap = new HashMap<>();
	private final Map<Long, Long> roomMap = new HashMap<>();
	private final Map<Long, Long> fileItemMap = new HashMap<>();
	private final Map<Long, Long> messageFolderMap = new HashMap<>();
	private final Map<Long, Long> userContactMap = new HashMap<>();
	private final Map<String, String> fileMap = new HashMap<>();
	private final Map<String, String> hashMap = new HashMap<>();

	private static File validate(String ename, File intended) throws IOException {
		final String intendedPath = intended.getCanonicalPath();
		// for each entry to be extracted
		File fentry = new File(intended, ename);
		final String canonicalPath = fentry.getCanonicalPath();

		if (canonicalPath.startsWith(intendedPath)) {
			return fentry;
		} else {
			throw new IllegalStateException("File is outside extraction target directory.");
		}
	}

	private static File unzip(InputStream is) throws IOException  {
		File f = OmFileHelper.getNewDir(OmFileHelper.getUploadImportDir(), randomUUID().toString());
		log.debug("##### EXTRACTING BACKUP TO: {}", f);

		try (ZipInputStream zis = new ZipInputStream(is)) {
			ZipEntry zipentry = null;
			while ((zipentry = zis.getNextEntry()) != null) {
				// for each entry to be extracted
				File fentry = validate(zipentry.getName(), f);
				File dir = zipentry.isDirectory() ? fentry : fentry.getParentFile();
				if (!dir.exists() && !dir.mkdirs()) {
					log.warn("Failed to create folders: {}", dir);
				}
				if (!fentry.isDirectory()) {
					try (FileOutputStream fos = FileUtils.openOutputStream(fentry)) {
						IOUtils.copy(zis, fos);
					}
					zis.closeEntry();
				}
			}
		}
		return f;
	}

	public void performImport(InputStream is, AtomicInteger progress) throws Exception {
		File f = null;
		boolean success = false;
		try {
			progress.set(0);
			cleanup();
			messageFolderMap.put(INBOX_FOLDER_ID, INBOX_FOLDER_ID);
			messageFolderMap.put(SENT_FOLDER_ID, SENT_FOLDER_ID);
			messageFolderMap.put(TRASH_FOLDER_ID, TRASH_FOLDER_ID);

			f = unzip(is);

			BackupVersion ver = getVersion(f);
			progress.set(2);
			importConfigs(f);
			progress.set(7);
			importGroups(f);
			progress.set(12);
			importLdap(f);
			progress.set(17);
			importOauth(f);
			progress.set(22);
			importUsers(f);
			progress.set(27);
			importRooms(f);
			progress.set(32);
			importRoomGroups(f);
			progress.set(37);
			importChat(f);
			progress.set(42);
			importCalendars(f);
			progress.set(47);
			importAppointments(f);
			progress.set(52);
			importMeetingMembers(f);
			progress.set(57);
			importRecordings(f);
			progress.set(62);
			importPrivateMsgFolders(f);
			progress.set(67);
			importContacts(f);
			progress.set(72);
			importPrivateMsgs(f);
			progress.set(77);
			List<FileItem> files = importFiles(f);
			progress.set(82);
			importPolls(f);
			progress.set(87);
			importRoomFiles(f);
			progress.set(92);
			importExtraMenus(f);
			progress.set(95);

			log.info("Extra menus import complete, starting copy of files and folders");
			/*
			 * ##################### Import real files and folders
			 */
			importFolders(f);
			progress.set(97);

			if (ver.compareTo(BackupVersion.get("4.0.0")) < 0) {
				for (FileItem bfi : files) {
					if (bfi.isDeleted()) {
						continue;
					}
					if (BaseFileItem.Type.PRESENTATION == bfi.getType()) {
						convertOldPresentation(bfi);
						fileItemDao.updateBase(bfi);
					}
					if (BaseFileItem.Type.WML_FILE == bfi.getType()) {
						convertWb(bfi);
					}
				}
			}
			log.info("File explorer item import complete");
			success = true;
		} finally {
			if (f != null) {
				log.info("Clearing temp files ...");
				FileUtils.deleteDirectory(f);
			}
			cleanup();
			if (success) {
				progress.set(100);
			}
		}
	}

	private void convertWb(FileItem bfi) {
		try {
			Whiteboard wb = WbConverter.convert(bfi);
			wb.save(bfi.getFile().toPath());
		} catch (Exception e) {
			log.error("Unexpected error while converting WB", e);
		}
	}

	public void cleanup() {
		ldapMap.clear();
		oauthMap.clear();
		userMap.clear();
		groupMap.clear();
		calendarMap.clear();
		appointmentMap.clear();
		roomMap.clear();
		messageFolderMap.clear();
		userContactMap.clear();
		fileMap.clear();
		hashMap.clear();
		messageFolderMap.put(INBOX_FOLDER_ID, INBOX_FOLDER_ID);
		messageFolderMap.put(SENT_FOLDER_ID, SENT_FOLDER_ID);
		messageFolderMap.put(TRASH_FOLDER_ID, TRASH_FOLDER_ID);
	}

	// public for testing
	public static BackupVersion getVersion(File base) {
		List<BackupVersion> list = new ArrayList<>(1);
		readList(base, "version.xml", VERSION_LIST_NODE, VERSION_NODE, BackupVersion.class, list::add, true);
		return list.isEmpty() ? new BackupVersion() : list.get(0);
	}

	/*
	 * ##################### Import Configs
	 */
	// public for testing
	public void importConfigs(File base) throws Exception {
		final Map<Integer, String> keyMap = new HashMap<>();
		Arrays.stream(KeyEvent.class.getDeclaredFields())
				.filter(fld -> fld.getName().startsWith("VK_"))
				.forEach(fld -> {
					try {
						keyMap.put(fld.getInt(null), "Shift+" + WordUtils.capitalizeFully(fld.getName().substring(3)));
					} catch (IllegalArgumentException|IllegalAccessException e) {
						log.error("Unexpected exception while building KEY map {}", fld);
					}
				});

		Class<Configuration> eClazz = Configuration.class;
		JAXBContext jc = JAXBContext.newInstance(eClazz);
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		unmarshaller.setAdapter(new UserAdapter(userDao, userMap));

		readList(unmarshaller, base, "configs.xml", CFG_LIST_NODE, CFG_NODE, eClazz, c -> {
			if (c.getKey() == null || c.isDeleted()) {
				return;
			}
			String newKey = outdatedConfigKeys.get(c.getKey());
			if (newKey != null) {
				c.setKey(newKey);
			}
			Configuration.Type type = configTypes.get(c.getKey());
			if (type != null) {
				c.setType(type);
				if (Configuration.Type.BOOL == type) {
					c.setValue(String.valueOf("1".equals(c.getValue()) || "yes".equals(c.getValue()) || "true".equals(c.getValue())));
				} else if (Configuration.Type.HOTKEY == type) {
					try {
						int val = c.getValueN().intValue();
						c.setValue(keyMap.get(val));
					} catch(Exception e) {
						//no-op, value is already HOTKEY
					}
				}
			}
			Configuration cfg = cfgDao.forceGet(c.getKey());
			if (cfg != null && !cfg.isDeleted()) {
				log.warn("Non deleted configuration with same key is found! old value: {}, new value: {}", cfg.getValue(), c.getValue());
			}
			c.setId(cfg == null ? null : cfg.getId());
			if (c.getUser() != null && c.getUser().getId() == null) {
				c.setUser(null);
			}
			if (CONFIG_CRYPT.equals(c.getKey())) {
				try {
					Class<?> clazz = Class.forName(c.getValue());
					clazz.getDeclaredConstructor().newInstance();
				} catch (Exception e) {
					log.warn("Not existing Crypt class found {}, replacing with SCryptImplementation", c.getValue());
					c.setValue(SCryptImplementation.class.getCanonicalName());
				}
			}
			cfgDao.update(c, null);
		});
	}

	/*
	 * ##################### Import Groups
	 */
	// public for testing
	public void importGroups(File base) {
		log.info("Configs import complete, starting group import");
		readList(base, "organizations.xml", GROUP_LIST_NODE, GROUP_NODE, Group.class, g -> {
			Long oldId = g.getId();
			g.setId(null);
			g = groupDao.update(g, null);
			groupMap.put(oldId, g.getId());
		});
	}

	/*
	 * ##################### Import LDAP Configs
	 */
	// public for testing
	public Long importLdap(File base) {
		log.info("Groups import complete, starting LDAP config import");
		Long[] defaultLdapId = {cfgDao.getLong(CONFIG_DEFAULT_LDAP_ID, null)};
		readList(base, "ldapconfigs.xml", "ldapconfigs", "ldapconfig", LdapConfig.class, c -> {
			if (Strings.isEmpty(c.getName()) || "local DB [internal]".equals(c.getName())) {
				return;
			}
			Long oldId = c.getId();
			c.setId(null);
			c = ldapConfigDao.update(c, null);
			if (defaultLdapId[0] == null) {
				defaultLdapId[0] = c.getId();
			}
			if (oldId != null) {
				ldapMap.put(oldId, c.getId());
			}
		});
		return defaultLdapId[0];
	}

	/*
	 * ##################### OAuth2 servers
	 */
	// public for testing
	public void importOauth(File base) {
		log.info("Ldap config import complete, starting OAuth2 server import");
		readList(base, "oauth2servers.xml", OAUTH_LIST_NODE, OAUTH_NODE, OAuthServer.class
				, s -> {
					Long oldId = s.getId();
					s.setId(null);
					s = auth2Dao.update(s, null);
					if (oldId != null) {
						oauthMap.put(oldId, s.getId());
					}
				}, false);
	}

	private void checkByType(User u) {
		if (u.getType() == User.Type.LDAP) {
			if (u.getDomainId() != null && ldapMap.containsKey(u.getDomainId())) {
				u.setDomainId(ldapMap.get(u.getDomainId()));
			} else {
				log.error("Unable to find Domain for ID: {}", u.getDomainId());
			}
		}
		if (u.getType() == User.Type.OAUTH) {
			if (u.getDomainId() != null && oauthMap.containsKey(u.getDomainId())) {
				u.setDomainId(oauthMap.get(u.getDomainId()));
			} else {
				log.error("Unable to find Domain for ID: {}", u.getDomainId());
			}
		}
	}

	private void checkLogin(User u, Set<UserKey> userLogins) {
		if (u.getType() == User.Type.CONTACT && u.getLogin().length() < getMinLoginLength()) {
			u.setLogin(randomUUID().toString());
		}
		if (userLogins.contains(new UserKey(u))) {
			log.warn("LOGIN is duplicated for USER {}", u);
			String updateLogin = String.format("modified_by_import_<%s>%s", randomUUID(), u.getLogin());
			u.setLogin(updateLogin);
		}
		userLogins.add(new UserKey(u));
	}

	/*
	 * ##################### Import Users
	 */
	// public for testing
	public void importUsers(File base) throws Exception {
		log.info("OAuth2 servers import complete, starting user import");
		String jNameTimeZone = getDefaultTimezone();
		//add existent emails from database
		final Set<String> userEmails = new HashSet<>();
		final Set<UserKey> userLogins = new HashSet<>();
		userDao.getAllUsers().stream().forEach(u -> {
			if (u.getAddress() != null && !Strings.isEmpty(u.getAddress().getEmail())) {
				userEmails.add(u.getAddress().getEmail());
			}
			userLogins.add(new UserKey(u));
		});
		Class<User> eClazz = User.class;
		JAXBContext jc = JAXBContext.newInstance(eClazz);
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		unmarshaller.setAdapter(new GroupAdapter(groupDao, groupMap));

		readList(unmarshaller, base, "users.xml", USER_LIST_NODE, USER_NODE, eClazz, u -> {
			if (u.getLogin() == null || u.isDeleted()) {
				return;
			}
			// check that email is unique
			if (u.getAddress() != null && u.getAddress().getEmail() != null && User.Type.USER == u.getType()) {
				if (userEmails.contains(u.getAddress().getEmail())) {
					log.warn("Email is duplicated for user {}", u);
					String updateEmail = String.format("modified_by_import_<%s>%s", randomUUID(), u.getAddress().getEmail());
					u.getAddress().setEmail(updateEmail);
				}
				userEmails.add(u.getAddress().getEmail());
			}
			checkByType(u);
			checkLogin(u, userLogins);
			if (u.getGroupUsers() != null) {
				for (Iterator<GroupUser> iter = u.getGroupUsers().iterator(); iter.hasNext();) {
					GroupUser gu = iter.next();
					if (gu.getGroup().getId() == null) {
						iter.remove();
						continue;
					}
					gu.setUser(u);
				}
			}

			String tz = u.getTimeZoneId();
			if (tz == null) {
				u.setTimeZoneId(jNameTimeZone);
			}

			Long userId = u.getId();
			u.setId(null);
			if (u.getSipUser() != null && u.getSipUser().getId() != 0) {
				u.getSipUser().setId(0);
			}
			if (AuthLevelUtil.hasLoginLevel(u.getRights()) && !Strings.isEmpty(u.getActivatehash())) {
				u.setActivatehash(null);
			}
			userDao.update(u, Long.valueOf(-1));
			userMap.put(userId, u.getId());
		});
	}

	/*
	 * ##################### Import Rooms
	 */
	// public for testing
	public void importRooms(File base) throws Exception {
		log.info("Users import complete, starting room import");
		Class<Room> eClazz = Room.class;
		JAXBContext jc = JAXBContext.newInstance(eClazz);
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		unmarshaller.setAdapter(new UserAdapter(userDao, userMap));

		readList(unmarshaller, base, "rooms.xml", ROOM_LIST_NODE, ROOM_NODE, eClazz, r -> {
			Long roomId = r.getId();

			if (r.getOwnerId() != null) {
				Long newOwnerId = userMap.get(r.getOwnerId());
				if (newOwnerId == null) {
					return; // owner was deleted
				}
				r.setOwnerId(newOwnerId);
			}
			// We need to reset ids as openJPA reject to store them otherwise
			r.setId(null);
			if (r.getModerators() != null) {
				for (Iterator<RoomModerator> i = r.getModerators().iterator(); i.hasNext();) {
					RoomModerator rm = i.next();
					if (rm.getUser().getId() == null) {
						i.remove();
					}
				}
			}
			r = roomDao.update(r, null);
			roomMap.put(roomId, r.getId());
		});
	}

	/*
	 * ##################### Import Room Groups
	 */
	// public for testing
	public void importRoomGroups(File base) throws Exception {
		log.info("Room import complete, starting room groups import");
		Class<RoomGroup> eClazz = RoomGroup.class;
		JAXBContext jc = JAXBContext.newInstance(eClazz);
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		unmarshaller.setAdapter(new RoomAdapter(roomDao, roomMap));
		unmarshaller.setAdapter(new GroupAdapter(groupDao, groupMap));

		readList(unmarshaller, base, "rooms_organisation.xml", ROOM_GRP_LIST_NODE, ROOM_GRP_NODE, eClazz, rg -> {
			if (rg.getRoom() == null || rg.getGroup() == null) {
				return;
			}
			Room r = roomDao.get(rg.getRoom().getId());
			if (r == null || rg.getGroup().getId() == null) {
				return;
			}
			if (r.getGroups() == null) {
				r.setGroups(new ArrayList<>());
			}
			rg.setId(null);
			rg.setRoom(r);
			r.getGroups().add(rg);
			roomDao.update(r, null);
		});
	}

	/*
	 * ##################### Import Chat messages
	 */
	// public for testing
	public void importChat(File base) throws Exception {
		log.info("Room groups import complete, starting chat messages import");
		Class<ChatMessage> eClazz = ChatMessage.class;
		JAXBContext jc = JAXBContext.newInstance(eClazz);
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		unmarshaller.setAdapter(new UserAdapter(userDao, userMap));
		unmarshaller.setAdapter(new RoomAdapter(roomDao, roomMap));

		readList(unmarshaller, base, "chat_messages.xml", CHAT_LIST_NODE, CHAT_NODE, eClazz, m -> {
			m.setId(null);
			if (m.getFromUser() == null || m.getFromUser().getId() == null
					|| (m.getToRoom() != null && m.getToRoom().getId() == null)
					|| (m.getToUser() != null && m.getToUser().getId() == null))
			{
				return;
			}
			chatDao.update(m, m.getSent());
		});
	}

	/*
	 * ##################### Import Calendars
	 */
	// public for testing
	public void importCalendars(File base) throws Exception {
		log.info("Chat messages import complete, starting calendar import");
		Class<OmCalendar> eClazz = OmCalendar.class;
		JAXBContext jc = JAXBContext.newInstance(eClazz);
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		unmarshaller.setAdapter(new UserAdapter(userDao, userMap));

		readList(unmarshaller, base, "calendars.xml", CALENDAR_LIST_NODE, CALENDAR_NODE, eClazz, c -> {
			Long id = c.getId();
			c.setId(null);
			c = calendarDao.update(c);
			calendarMap.put(id, c.getId());
		}, true);
	}

	/*
	 * ##################### Import Appointments
	 */
	// public for testing
	public void importAppointments(File base) throws Exception {
		log.info("Calendar import complete, starting appointement import");
		Class<Appointment> eClazz = Appointment.class;
		JAXBContext jc = JAXBContext.newInstance(eClazz);
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		unmarshaller.setAdapter(new UserAdapter(userDao, userMap));
		unmarshaller.setAdapter(new RoomAdapter(roomDao, roomMap));
		unmarshaller.setAdapter(new OmCalendarAdapter(calendarDao, calendarMap));

		readList(unmarshaller, base, "appointements.xml", APPOINTMENT_LIST_NODE, APPOINTMENT_NODE, eClazz, a -> {
			Long appId = a.getId();

			// We need to reset this as openJPA reject to store them otherwise
			a.setId(null);
			if (a.getOwner() != null && a.getOwner().getId() == null) {
				a.setOwner(null);
			}
			if (a.getRoom() == null || a.getRoom().getId() == null) {
				log.warn("Appointment without room was found, skipping: {}", a);
				return;
			}
			if (a.getStart() == null || a.getEnd() == null) {
				log.warn("Appointment without start/end time was found, skipping: {}", a);
				return;
			}
			a = appointmentDao.update(a, null, false);
			appointmentMap.put(appId, a.getId());
		});
	}

	/*
	 * ##################### Import MeetingMembers
	 *
	 * Reminder Invitations will be NOT send!
	 */
	// public for testing
	public void importMeetingMembers(File base) throws Exception {
		log.info("Appointement import complete, starting meeting members import");
		Class<MeetingMember> eClazz = MeetingMember.class;
		JAXBContext jc = JAXBContext.newInstance(eClazz);
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		unmarshaller.setAdapter(new UserAdapter(userDao, userMap));
		unmarshaller.setAdapter(new AppointmentAdapter(appointmentDao, appointmentMap));

		readList(unmarshaller, base, "meetingmembers.xml", MMEMBER_LIST_NODE, MMEMBER_NODE, eClazz, ma -> {
			ma.setId(null);
			meetingMemberDao.update(ma);
		});
	}

	private boolean isInvalidFile(BaseFileItem file, final Map<Long, Long> folders) {
		if (file.isDeleted()) {
			return true;
		}
		if (file.getParentId() != null && file.getParentId() > 0) {
			Long newFolder = folders.get(file.getParentId());
			if (newFolder == null) {
				//folder was deleted
				return true;
			} else {
				file.setParentId(newFolder);
			}
		} else {
			file.setParentId(null);
		}
		if (file.getRoomId() != null) {
			Long newRoomId = roomMap.get(file.getRoomId());
			if (newRoomId == null) {
				return true; // room was deleted
			}
			file.setRoomId(newRoomId);
		}
		if (file.getOwnerId() != null) {
			Long newOwnerId = userMap.get(file.getOwnerId());
			if (newOwnerId == null) {
				return true; // owner was deleted
			}
			file.setOwnerId(newOwnerId);
		}
		if (file.getInsertedBy() != null) {
			Long newInsertedBy = userMap.get(file.getInsertedBy());
			file.setInsertedBy(newInsertedBy);
		}
		if (file.getGroupId() != null) {
			Long newGroupId = groupMap.get(file.getGroupId());
			if (newGroupId == null) {
				return true; // owner was deleted
			}
			file.setGroupId(newGroupId);
		}
		return false;
	}

	private <T extends BaseFileItem> void saveTree(
			File baseDir
			, String fileName
			, String listNodeName
			, String nodeName
			, Class<T> clazz
			, Map<Long, Long> folders
			, Consumer<T> save
			)
	{
		TreeMap<Long, T> items = new TreeMap<>();
		readList(baseDir, fileName, listNodeName, nodeName, clazz, f -> items.put(f.getId(), f), false);
		FileTree<T> tree = new FileTree<>();
		TreeMap<Long, T> remain = new TreeMap<>();
		int counter = items.size(); //max iterations
		while (counter > 0 && !items.isEmpty()) {
			Entry<Long, T> e = items.pollFirstEntry();
			if (e == null) {
				break;
			} else {
				if (!tree.add(e.getValue())) {
					remain.put(e.getKey(), e.getValue());
				}
			}
			if (items.isEmpty()) {
				counter = Math.min(counter - 1, remain.size());
				items.putAll(remain);
				remain.clear();
			}
		}
		remain.entrySet().forEach(e -> log.warn("Doungling file/recording: {}", e.getValue()));
		tree.process(f -> isInvalidFile(f, folders), save);
	}

	/*
	 * ##################### Import Recordings
	 */
	// public for testing
	public void importRecordings(File base) {
		log.info("Meeting members import complete, starting recordings server import");
		final Map<Long, Long> folders = new HashMap<>();
		saveTree(base, "flvRecordings.xml", RECORDING_LIST_NODE, RECORDING_NODE, Recording.class, folders, r -> {
			Long recId = r.getId();
			r.setId(null);
			if (r.getChunks() != null) {
				for (RecordingChunk chunk : r.getChunks()) {
					chunk.setId(null);
					chunk.setRecording(r);
				}
			}
			checkHash(r, recordingDao, (oldHash, newHash) -> {
				if (!Strings.isEmpty(oldHash) && oldHash.startsWith(RECORDING_FILE_NAME)) {
					String name = getFileName(oldHash);
					fileMap.put(String.format(FILE_NAME_FMT, name, EXTENSION_JPG), String.format(FILE_NAME_FMT, newHash, EXTENSION_PNG));
					fileMap.put(String.format("%s.%s.%s", name, "flv", EXTENSION_MP4), String.format(FILE_NAME_FMT, newHash, EXTENSION_MP4));
				}
			});
			r = recordingDao.update(r);
			if (BaseFileItem.Type.FOLDER == r.getType()) {
				folders.put(recId, r.getId());
			}
			fileItemMap.put(recId, r.getId());
		});
	}

	private void checkHash(BaseFileItem file, BaseFileItemDao dao, BiConsumer<String, String> consumer) {
		String oldHash = file.getHash();
		if (Strings.isEmpty(oldHash) || !UUID_PATTERN.matcher(oldHash).matches() || dao.get(oldHash, BaseFileItem.class) != null) {
			file.setHash(randomUUID().toString());
			hashMap.put(oldHash, file.getHash());
			if (consumer != null) {
				consumer.accept(oldHash, file.getHash());
			}
		} else {
			hashMap.put(file.getHash(), file.getHash());
		}
	}

	/*
	 * ##################### Import Private Message Folders
	 */
	// public for testing
	public void importPrivateMsgFolders(File base) {
		log.info("Recording import complete, starting private message folder import");
		readList(base, "privateMessageFolder.xml", MSG_FOLDER_LIST_NODE, MSG_FOLDER_NODE, PrivateMessageFolder.class, p -> {
			Long folderId = p.getId();
			PrivateMessageFolder storedFolder = privateMessageFolderDao.get(folderId);
			if (storedFolder == null) {
				p.setId(null);
				Long newFolderId = privateMessageFolderDao.addPrivateMessageFolderObj(p);
				messageFolderMap.put(folderId, newFolderId);
			}
		});
	}

	/*
	 * ##################### Import User Contacts
	 */
	private void importContacts(File base) throws Exception {
		log.info("Private message folder import complete, starting user contacts import");
		Class<UserContact> eClazz = UserContact.class;
		JAXBContext jc = JAXBContext.newInstance(eClazz);
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		unmarshaller.setAdapter(new UserAdapter(userDao, userMap));

		readList(unmarshaller, base, "userContacts.xml", CONTACT_LIST_NODE, CONTACT_NODE, eClazz, uc -> {
			Long ucId = uc.getId();
			UserContact storedUC = userContactDao.get(ucId);

			if (storedUC == null && uc.getContact() != null && uc.getContact().getId() != null) {
				uc.setId(null);
				if (uc.getOwner() != null && uc.getOwner().getId() == null) {
					uc.setOwner(null);
				}
				uc = userContactDao.update(uc);
				userContactMap.put(ucId, uc.getId());
			}
		});
	}

	/*
	 * ##################### Import Private Messages
	 */
	private void importPrivateMsgs(File base) throws Exception {
		log.info("Usercontact import complete, starting private messages item import");
		Class<PrivateMessage> eClazz = PrivateMessage.class;
		JAXBContext jc = JAXBContext.newInstance(eClazz);
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		unmarshaller.setAdapter(new UserAdapter(userDao, userMap));
		unmarshaller.setAdapter(new RoomAdapter(roomDao, roomMap));

		readList(unmarshaller, base, "privateMessages.xml", MSG_LIST_NODE, MSG_NODE, eClazz, p -> {
			p.setId(null);
			p.setFolderId(messageFolderMap.get(p.getFolderId()));
			p.setUserContactId(userContactMap.get(p.getUserContactId()));
			if (p.getRoom() != null && p.getRoom().getId() == null) {
				p.setRoom(null);
			}
			if (p.getTo() != null && p.getTo().getId() == null) {
				p.setTo(null);
			}
			if (p.getFrom() != null && p.getFrom().getId() == null) {
				p.setFrom(null);
			}
			if (p.getOwner() != null && p.getOwner().getId() == null) {
				p.setOwner(null);
			}
			privateMessageDao.update(p, null);
		});
	}

	/*
	 * ##################### Import File-Explorer Items
	 */
	private List<FileItem> importFiles(File base) {
		log.info("Private message import complete, starting file explorer item import");
		List<FileItem> result = new ArrayList<>();
		final Map<Long, Long> folders = new HashMap<>();
		saveTree(base, "fileExplorerItems.xml", FILE_LIST_NODE, FILE_NODE, FileItem.class, folders, file -> {
			Long fId = file.getId();
			// We need to reset this as openJPA reject to store them otherwise
			file.setId(null);
			checkHash(file, fileItemDao, null);
			file = fileItemDao.update(file);
			if (BaseFileItem.Type.FOLDER == file.getType()) {
				folders.put(fId, file.getId());
			}
			result.add(file);
			fileItemMap.put(fId, file.getId());
		});
		return result;
	}

	/*
	 * ##################### Import Room Polls
	 */
	private void importPolls(File base) throws Exception {
		log.info("File explorer item import complete, starting room poll import");
		Class<RoomPoll> eClazz = RoomPoll.class;
		JAXBContext jc = JAXBContext.newInstance(eClazz);
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		unmarshaller.setAdapter(new UserAdapter(userDao, userMap));
		unmarshaller.setAdapter(new RoomAdapter(roomDao, roomMap));

		readList(unmarshaller, base, "roompolls.xml", POLL_LIST_NODE, POLL_NODE, eClazz, rp -> {
			rp.setId(null);
			if (rp.getRoom() == null || rp.getRoom().getId() == null) {
				//room was deleted
				return;
			}
			if (rp.getCreator() == null || rp.getCreator().getId() == null) {
				rp.setCreator(null);
			}
			for (RoomPollAnswer rpa : rp.getAnswers()) {
				if (rpa.getVotedUser() == null || rpa.getVotedUser().getId() == null) {
					rpa.setVotedUser(null);
				}
			}
			pollDao.update(rp);
		});
	}

	/*
	 * ##################### Import Room Files
	 */
	private void importRoomFiles(File base) throws Exception {
		log.info("Poll import complete, starting room files import");
		Class<RoomFile> eClazz = RoomFile.class;
		JAXBContext jc = JAXBContext.newInstance(eClazz);
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		unmarshaller.setAdapter(new FileAdapter(fileItemDao, fileItemMap));

		readList(unmarshaller, base, "roomFiles.xml", ROOM_FILE_LIST_NODE, ROOM_FILE_NODE, eClazz, rf -> {
			Room r = roomDao.get(roomMap.get(rf.getRoomId()));
			if (r == null || rf.getFile() == null || rf.getFile().getId() == null) {
				return;
			}
			if (r.getFiles() == null) {
				r.setFiles(new ArrayList<>());
			}
			rf.setId(null);
			rf.setRoomId(r.getId());
			r.getFiles().add(rf);
			roomDao.update(r, null);
		}, true);
	}

	// public for testing
	public void importExtraMenus(File base) throws Exception {
		log.info("Room files complete, starting extra menus import");
		Class<ExtraMenu> eClazz = ExtraMenu.class;
		JAXBContext jc = JAXBContext.newInstance(eClazz);
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		unmarshaller.setAdapter(new GroupAdapter(groupDao, groupMap));

		readList(unmarshaller, base, "extraMenus.xml", EXTRA_MENU_LIST_NODE, EXTRA_MENU_NODE, eClazz, m -> {
			if (Strings.isEmpty(m.getName()) || Strings.isEmpty(m.getLink())) {
				return;
			}
			m.setId(null);
			menuDao.update(m, null);
		}, true);
	}

	private static <T> void readList(File baseDir, String fileName, String listNodeName, String nodeName, Class<T> clazz, Consumer<T> consumer) {
		readList(baseDir, fileName, listNodeName, nodeName, clazz, consumer, false);
	}

	private static <T> void readList(Unmarshaller unmarshaller, File baseDir, String fileName, String listNodeName, String nodeName, Class<T> clazz, Consumer<T> consumer) {
		readList(unmarshaller, baseDir, fileName, listNodeName, nodeName, clazz, consumer, false);
	}

	private static <T> void readList(File baseDir, String fileName, String listNodeName, String nodeName, Class<T> clazz, Consumer<T> consumer, boolean notThow) {
		readList(null, baseDir, fileName, listNodeName, nodeName, clazz, consumer, notThow);
	}

	private static <T> void readList(Unmarshaller inUnmarshaller, File baseDir, String fileName, String listNodeName, String nodeName, Class<T> clazz, Consumer<T> consumer, boolean notThow) {
		File xml = new File(baseDir, fileName);
		if (!xml.exists()) {
			final String msg = fileName + " missing";
			if (notThow) {
				log.debug(msg);
				return;
			} else {
				throw new BackupException(msg);
			}
		}
		try {
			Unmarshaller unmarshaller = inUnmarshaller;
			if (inUnmarshaller == null) {
				JAXBContext jc = JAXBContext.newInstance(clazz);
				unmarshaller = jc.createUnmarshaller();
			}
			XMLInputFactory xif = XmlHelper.createInputFactory();
			StreamSource xmlSource = new StreamSource(xml);
			XMLStreamReader xsr = xif.createXMLStreamReader(xmlSource);
			boolean listNodeFound = false;
			while (xsr.getEventType() != XMLStreamConstants.END_DOCUMENT) {
				if (xsr.isStartElement()) {
					if (!listNodeFound && listNodeName.equals(xsr.getLocalName())) {
						listNodeFound = true;
					} else if (nodeName.equals(xsr.getLocalName())) {
						T o = unmarshaller.unmarshal(xsr, clazz).getValue();
						consumer.accept(o);
					}
				}
				xsr.next();
			}
		} catch (Exception e) {
			throw new BackupException(e);
		}
	}

	private static Long getPrefixedId(String prefix, File f, Map<Long, Long> map) {
		String n = getFileName(f.getName());
		Long id = null;
		if (n.indexOf(prefix) > -1) {
			id = importLongType(n.substring(prefix.length(), n.length()));
		}
		return id == null ? null : map.get(id);
	}

	private void processGroupFiles(File baseDir) throws IOException {
		log.debug("Entered group logo folder");
		for (File f : baseDir.listFiles()) {
			String ext = getFileExt(f.getName());
			if (EXTENSION_PNG.equals(ext)) {
				Long id = getPrefixedId(GROUP_LOGO_PREFIX, f, groupMap);
				if (id != null) {
					FileUtils.copyFile(f, getGroupLogo(id, false));
				}
			} else if (EXTENSION_CSS.equals(ext)) {
				Long id = getPrefixedId(GROUP_CSS_PREFIX, f, groupMap);
				if (id != null) {
					FileUtils.copyFile(f, getGroupCss(id, false));
				}
			}
		}
	}

	private static void changeHash(File f, File dir, String hash, String inExt) throws IOException {
		String ext = inExt == null ? getFileExt(f.getName()) : inExt;
		FileUtils.copyFile(f, new File(dir, getName(hash, ext)));
	}

	private void processFiles(File baseDir) throws IOException {
		log.debug("Entered FILES folder");
		for (File rf : baseDir.listFiles()) {
			String oldHash = OmFileHelper.getFileName(rf.getName());
			String hash = hashMap.get(oldHash);
			if (hash == null) {
				continue;
			}
			File dir = new File(getUploadFilesDir(), hash);
			// going to fix images
			if (rf.isFile() && rf.getName().endsWith(EXTENSION_JPG)) {
				changeHash(rf, dir, hash, EXTENSION_JPG);
			} else {
				for (File f : rf.listFiles()) {
					FileUtils.copyFile(f, new File(dir
							, f.getName().startsWith(oldHash) ? getName(hash, getFileExt(f.getName())) : f.getName()));
				}
			}
		}
	}

	private void processProfiles(File baseDir) throws IOException {
		log.debug("Entered profiles folder");
		for (File profile : baseDir.listFiles()) {
			Long id = getPrefixedId(PROFILES_PREFIX, profile, userMap);
			if (id != null) {
				FileUtils.copyDirectory(profile, getUploadProfilesUserDir(id));
			}
		}
	}

	private void processWmls(File baseDir) throws IOException {
		log.debug("Entered WML folder");
		File dir = getUploadWmlDir();
		for (File wml : baseDir.listFiles()) {
			String oldHash = OmFileHelper.getFileName(wml.getName());
			String hash = hashMap.get(oldHash);
			if (hash == null) {
				continue;
			}
			changeHash(wml, dir, hash, null);
		}
	}

	private void processFilesRoot(File baseDir) throws IOException {
		// Now check the room files and import them
		final File roomFilesFolder = new File(baseDir, BCKP_ROOM_FILES);
		log.debug("roomFilesFolder PATH {} ", roomFilesFolder.getCanonicalPath());
		if (!roomFilesFolder.exists()) {
			return;
		}

		for (File file : roomFilesFolder.listFiles()) {
			if (file.isDirectory()) {
				String fName = file.getName();
				if (PROFILES_DIR.equals(fName)) {
					processProfiles(file);
				} else if (FILES_DIR.equals(fName)) {
					processFiles(file);
				} else if (GROUP_LOGO_DIR.equals(fName)) {
					processGroupFiles(file);
				} else if (WML_DIR.equals(fName)) {
					processWmls(file);
				}
			}
		}
	}

	private void importFolders(File baseDir) throws IOException {
		processFilesRoot(baseDir);

		// Now check the recordings and import them
		final File recDir = new File(baseDir, BCKP_RECORD_FILES);
		log.debug("sourceDirRec PATH {}", recDir.getCanonicalPath());
		if (recDir.exists()) {
			final File hiberDir = getStreamsHibernateDir();
			for (File r : recDir.listFiles()) {
				String n = fileMap.get(r.getName());
				if (n != null) {
					FileUtils.copyFile(r, new File(hiberDir, n));
				} else {
					String oldHash = OmFileHelper.getFileName(r.getName());
					String hash = hashMap.get(oldHash);
					if (hash == null) {
						FileUtils.copyFileToDirectory(r, hiberDir);
					} else {
						changeHash(r, hiberDir, hash, null);
					}
				}
			}
		}
		final File cssDir = new File(baseDir, CSS_DIR);
		if (cssDir.exists()) {
			final File wCssDir = getCssDir();
			for (File css : cssDir.listFiles()) {
				FileUtils.copyFileToDirectory(css, wCssDir);
			}
		}
	}

	private static Long importLongType(String value) {
		Long val = null;
		try {
			val = Long.valueOf(value);
		} catch (Exception e) {
			// no-op
		}
		return val;
	}

	private void convertOldPresentation(FileItem fi) {
		File f = fi.getOriginal();
		if (f != null && f.exists()) {
			try {
				StoredFile sf = new StoredFile(fi.getHash(), getFileExt(f.getName()), f);
				docConverter.convertPDF(fi, sf);
			} catch (Exception e) {
				log.error("Unexpected exception while converting OLD format presentations", e);
			}
		}
	}

	private static class UserKey {
		private final String login;
		private final User.Type type;
		private final Long domainId;

		UserKey(User u) {
			this.login = u.getLogin();
			this.type = u.getType();
			this.domainId = u.getDomainId();
		}

		@Override
		public int hashCode() {
			return Objects.hash(domainId, login, type);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			UserKey other = (UserKey) obj;
			return Objects.equals(domainId, other.domainId)
					&& Objects.equals(login, other.login)
					&& type == other.type;
		}
	}
}
