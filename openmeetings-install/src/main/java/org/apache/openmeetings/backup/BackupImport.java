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

import static org.apache.openmeetings.db.entity.user.PrivateMessage.INBOX_FOLDER_ID;
import static org.apache.openmeetings.db.entity.user.PrivateMessage.SENT_FOLDER_ID;
import static org.apache.openmeetings.db.entity.user.PrivateMessage.TRASH_FOLDER_ID;
import static org.apache.openmeetings.db.util.TimezoneUtil.getTimeZone;
import static org.apache.openmeetings.util.OmFileHelper.BCKP_RECORD_FILES;
import static org.apache.openmeetings.util.OmFileHelper.BCKP_ROOM_FILES;
import static org.apache.openmeetings.util.OmFileHelper.CSS_DIR;
import static org.apache.openmeetings.util.OmFileHelper.EXTENSION_FLV;
import static org.apache.openmeetings.util.OmFileHelper.EXTENSION_JPG;
import static org.apache.openmeetings.util.OmFileHelper.EXTENSION_MP4;
import static org.apache.openmeetings.util.OmFileHelper.EXTENSION_PNG;
import static org.apache.openmeetings.util.OmFileHelper.FILES_DIR;
import static org.apache.openmeetings.util.OmFileHelper.FILE_NAME_FMT;
import static org.apache.openmeetings.util.OmFileHelper.GROUP_LOGO_DIR;
import static org.apache.openmeetings.util.OmFileHelper.GROUP_LOGO_PREFIX;
import static org.apache.openmeetings.util.OmFileHelper.PROFILES_DIR;
import static org.apache.openmeetings.util.OmFileHelper.PROFILES_PREFIX;
import static org.apache.openmeetings.util.OmFileHelper.RECORDING_FILE_NAME;
import static org.apache.openmeetings.util.OmFileHelper.THUMB_IMG_PREFIX;
import static org.apache.openmeetings.util.OmFileHelper.getCssDir;
import static org.apache.openmeetings.util.OmFileHelper.getFileExt;
import static org.apache.openmeetings.util.OmFileHelper.getFileName;
import static org.apache.openmeetings.util.OmFileHelper.getStreamsHibernateDir;
import static org.apache.openmeetings.util.OmFileHelper.getUploadDir;
import static org.apache.openmeetings.util.OmFileHelper.getUploadFilesDir;
import static org.apache.openmeetings.util.OmFileHelper.getUploadProfilesUserDir;
import static org.apache.openmeetings.util.OmFileHelper.getUploadRoomDir;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_APPOINTMENT_REMINDER_MINUTES;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_CALENDAR_ROOM_CAPACITY;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_CRYPT;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DASHBOARD_RSS_FEED1;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DASHBOARD_RSS_FEED2;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DASHBOARD_SHOW_CHAT;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DASHBOARD_SHOW_MYROOMS;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DASHBOARD_SHOW_RSS;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DEFAULT_GROUP_ID;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DEFAULT_LANG;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DEFAULT_LDAP_ID;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DEFAULT_TIMEZONE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DOCUMENT_DPI;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DOCUMENT_QUALITY;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_EMAIL_AT_REGISTER;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_EMAIL_VERIFICATION;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_EXT_PROCESS_TTL;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_FLASH_CAM_QUALITY;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_FLASH_ECHO_PATH;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_FLASH_MIC_RATE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_FLASH_SECURE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_FLASH_VIDEO_BANDWIDTH;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_FLASH_VIDEO_FPS;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_IGNORE_BAD_SSL;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_KEYCODE_ARRANGE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_KEYCODE_EXCLUSIVE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_KEYCODE_MUTE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_LOGIN_MIN_LENGTH;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_MAX_UPLOAD_SIZE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_MYROOMS_ENABLED;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_PASS_MIN_LENGTH;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_PATH_FFMPEG;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_PATH_IMAGEMAGIC;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_PATH_OFFICE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_PATH_SOX;
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
import static org.apache.openmeetings.util.OpenmeetingsVariables.getMinLoginLength;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getWebAppRootKey;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.openmeetings.backup.converter.AppointmentConverter;
import org.apache.openmeetings.backup.converter.AppointmentReminderTypeConverter;
import org.apache.openmeetings.backup.converter.BaseFileItemConverter;
import org.apache.openmeetings.backup.converter.DateConverter;
import org.apache.openmeetings.backup.converter.GroupConverter;
import org.apache.openmeetings.backup.converter.OmCalendarConverter;
import org.apache.openmeetings.backup.converter.PollTypeConverter;
import org.apache.openmeetings.backup.converter.RecordingStatusConverter;
import org.apache.openmeetings.backup.converter.RoomConverter;
import org.apache.openmeetings.backup.converter.RoomTypeConverter;
import org.apache.openmeetings.backup.converter.SalutationConverter;
import org.apache.openmeetings.backup.converter.UserConverter;
import org.apache.openmeetings.backup.converter.WbConverter;
import org.apache.openmeetings.core.converter.DocumentConverter;
import org.apache.openmeetings.db.dao.basic.ChatDao;
import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dao.calendar.AppointmentDao;
import org.apache.openmeetings.db.dao.calendar.MeetingMemberDao;
import org.apache.openmeetings.db.dao.calendar.OmCalendarDao;
import org.apache.openmeetings.db.dao.file.FileItemDao;
import org.apache.openmeetings.db.dao.record.RecordingDao;
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
import org.apache.openmeetings.db.dto.user.OAuthUser;
import org.apache.openmeetings.db.entity.basic.ChatMessage;
import org.apache.openmeetings.db.entity.basic.Configuration;
import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.db.entity.calendar.MeetingMember;
import org.apache.openmeetings.db.entity.calendar.OmCalendar;
import org.apache.openmeetings.db.entity.file.BaseFileItem;
import org.apache.openmeetings.db.entity.file.FileItem;
import org.apache.openmeetings.db.entity.record.Recording;
import org.apache.openmeetings.db.entity.record.RecordingMetaData;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.room.Room.RoomElement;
import org.apache.openmeetings.db.entity.room.RoomFile;
import org.apache.openmeetings.db.entity.room.RoomGroup;
import org.apache.openmeetings.db.entity.room.RoomModerator;
import org.apache.openmeetings.db.entity.room.RoomPoll;
import org.apache.openmeetings.db.entity.room.RoomPollAnswer;
import org.apache.openmeetings.db.entity.server.LdapConfig;
import org.apache.openmeetings.db.entity.server.OAuthServer;
import org.apache.openmeetings.db.entity.server.OAuthServer.RequestInfoMethod;
import org.apache.openmeetings.db.entity.user.Address;
import org.apache.openmeetings.db.entity.user.Group;
import org.apache.openmeetings.db.entity.user.GroupUser;
import org.apache.openmeetings.db.entity.user.PrivateMessage;
import org.apache.openmeetings.db.entity.user.PrivateMessageFolder;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.entity.user.User.Right;
import org.apache.openmeetings.db.entity.user.User.Salutation;
import org.apache.openmeetings.db.entity.user.UserContact;
import org.apache.openmeetings.db.util.AuthLevelUtil;
import org.apache.openmeetings.util.CalendarPatterns;
import org.apache.openmeetings.util.OmFileHelper;
import org.apache.openmeetings.util.StoredFile;
import org.apache.openmeetings.util.crypt.SCryptImplementation;
import org.apache.wicket.util.string.Strings;
import org.red5.logging.Red5LoggerFactory;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.convert.Registry;
import org.simpleframework.xml.convert.RegistryStrategy;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.strategy.Strategy;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.NodeBuilder;
import org.simpleframework.xml.transform.RegistryMatcher;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

@Component
public class BackupImport {
	private static final Logger log = Red5LoggerFactory.getLogger(BackupImport.class, getWebAppRootKey());
	private static final String LDAP_EXT_TYPE = "LDAP";
	private static final Properties countries = new Properties();
	private static final Map<String, String> outdatedConfigKeys = new HashMap<>();
	private static final Map<String, Configuration.Type> configTypes = new HashMap<>();
	static {
		outdatedConfigKeys.put("crypt_ClassName", CONFIG_CRYPT);
		outdatedConfigKeys.put("system_email_addr", CONFIG_SMTP_SYSTEM_EMAIL);
		outdatedConfigKeys.put("smtp_server", CONFIG_SMTP_SERVER);
		outdatedConfigKeys.put("smtp_port", CONFIG_SMTP_PORT);
		outdatedConfigKeys.put("email_username", CONFIG_SMTP_USER);
		outdatedConfigKeys.put("email_userpass", CONFIG_SMTP_PASS);
		outdatedConfigKeys.put("default_lang_id", CONFIG_DEFAULT_LANG);
		outdatedConfigKeys.put("allow_frontend_register", CONFIG_REGISTER_FRONTEND);
		outdatedConfigKeys.put("max_upload_size", CONFIG_MAX_UPLOAD_SIZE);
		outdatedConfigKeys.put("rss_feed1", CONFIG_DASHBOARD_RSS_FEED1);
		outdatedConfigKeys.put("rss_feed2", CONFIG_DASHBOARD_RSS_FEED2);
		outdatedConfigKeys.put("oauth2.ignore_bad_ssl", CONFIG_IGNORE_BAD_SSL);
		outdatedConfigKeys.put("default.quality.screensharing", CONFIG_SCREENSHARING_QUALITY);
		outdatedConfigKeys.put("default.fps.screensharing", CONFIG_SCREENSHARING_FPS);
		outdatedConfigKeys.put("ldap_default_id", CONFIG_DEFAULT_LDAP_ID);
		outdatedConfigKeys.put("default_group_id", CONFIG_DEFAULT_GROUP_ID);
		outdatedConfigKeys.put("imagemagick_path", CONFIG_PATH_IMAGEMAGIC);
		outdatedConfigKeys.put("sox_path", CONFIG_PATH_SOX);
		outdatedConfigKeys.put("ffmpeg_path", CONFIG_PATH_FFMPEG);
		outdatedConfigKeys.put("office.path", CONFIG_PATH_OFFICE);
		outdatedConfigKeys.put("red5sip.enable", CONFIG_SIP_ENABLED);
		outdatedConfigKeys.put("red5sip.room_prefix", CONFIG_SIP_ROOM_PREFIX);
		outdatedConfigKeys.put("red5sip.exten_context", CONFIG_SIP_EXTEN_CONTEXT);
		outdatedConfigKeys.put("sendEmailAtRegister", CONFIG_EMAIL_AT_REGISTER);
		outdatedConfigKeys.put("sendEmailWithVerficationCode", CONFIG_EMAIL_VERIFICATION);
		outdatedConfigKeys.put("swftools_zoom", CONFIG_DOCUMENT_DPI);
		outdatedConfigKeys.put("swftools_jpegquality", CONFIG_DOCUMENT_QUALITY);
		outdatedConfigKeys.put("sms.subject", CONFIG_REMINDER_MESSAGE);
		configTypes.put(CONFIG_REGISTER_FRONTEND, Configuration.Type.bool);
		configTypes.put(CONFIG_REGISTER_SOAP, Configuration.Type.bool);
		configTypes.put(CONFIG_REGISTER_OAUTH, Configuration.Type.bool);
		configTypes.put(CONFIG_SMTP_TLS, Configuration.Type.bool);
		configTypes.put(CONFIG_EMAIL_AT_REGISTER, Configuration.Type.bool);
		configTypes.put(CONFIG_EMAIL_VERIFICATION, Configuration.Type.bool);
		configTypes.put(CONFIG_SIP_ENABLED, Configuration.Type.bool);
		configTypes.put(CONFIG_SCREENSHARING_FPS_SHOW, Configuration.Type.bool);
		configTypes.put(CONFIG_SCREENSHARING_ALLOW_REMOTE, Configuration.Type.bool);
		configTypes.put(CONFIG_DASHBOARD_SHOW_MYROOMS, Configuration.Type.bool);
		configTypes.put(CONFIG_DASHBOARD_SHOW_CHAT, Configuration.Type.bool);
		configTypes.put(CONFIG_DASHBOARD_SHOW_RSS, Configuration.Type.bool);
		configTypes.put(CONFIG_REPLY_TO_ORGANIZER, Configuration.Type.bool);
		configTypes.put(CONFIG_IGNORE_BAD_SSL, Configuration.Type.bool);
		configTypes.put(CONFIG_FLASH_SECURE, Configuration.Type.bool);
		configTypes.put(CONFIG_MYROOMS_ENABLED, Configuration.Type.bool);
		configTypes.put(CONFIG_DEFAULT_GROUP_ID, Configuration.Type.number);
		configTypes.put(CONFIG_SMTP_PORT, Configuration.Type.number);
		configTypes.put(CONFIG_SMTP_TIMEOUT_CON, Configuration.Type.number);
		configTypes.put(CONFIG_SMTP_TIMEOUT, Configuration.Type.number);
		configTypes.put(CONFIG_DEFAULT_LANG, Configuration.Type.number);
		configTypes.put(CONFIG_DOCUMENT_DPI, Configuration.Type.number);
		configTypes.put(CONFIG_DOCUMENT_QUALITY, Configuration.Type.number);
		configTypes.put(CONFIG_SCREENSHARING_QUALITY, Configuration.Type.number);
		configTypes.put(CONFIG_SCREENSHARING_FPS, Configuration.Type.number);
		configTypes.put(CONFIG_MAX_UPLOAD_SIZE, Configuration.Type.number);
		configTypes.put(CONFIG_APPOINTMENT_REMINDER_MINUTES, Configuration.Type.number);
		configTypes.put(CONFIG_LOGIN_MIN_LENGTH, Configuration.Type.number);
		configTypes.put(CONFIG_PASS_MIN_LENGTH, Configuration.Type.number);
		configTypes.put(CONFIG_CALENDAR_ROOM_CAPACITY, Configuration.Type.number);
		configTypes.put(CONFIG_KEYCODE_ARRANGE, Configuration.Type.number);
		configTypes.put(CONFIG_KEYCODE_EXCLUSIVE, Configuration.Type.number);
		configTypes.put(CONFIG_KEYCODE_MUTE, Configuration.Type.number);
		configTypes.put(CONFIG_DEFAULT_LDAP_ID, Configuration.Type.number);
		configTypes.put(CONFIG_FLASH_VIDEO_FPS, Configuration.Type.number);
		configTypes.put(CONFIG_FLASH_VIDEO_BANDWIDTH, Configuration.Type.number);
		configTypes.put(CONFIG_FLASH_CAM_QUALITY, Configuration.Type.number);
		configTypes.put(CONFIG_FLASH_MIC_RATE, Configuration.Type.number);
		configTypes.put(CONFIG_FLASH_ECHO_PATH, Configuration.Type.number);
		configTypes.put(CONFIG_EXT_PROCESS_TTL, Configuration.Type.number);
	}

	@Autowired
	private AppointmentDao appointmentDao;
	@Autowired
	private OmCalendarDao calendarDao;
	@Autowired
	private RoomDao roomDao;
	@Autowired
	private UserDao userDao;
	@Autowired
	private RecordingDao recordingDao;
	@Autowired
	private PrivateMessageFolderDao privateMessageFolderDao;
	@Autowired
	private PrivateMessageDao privateMessageDao;
	@Autowired
	private MeetingMemberDao meetingMemberDao;
	@Autowired
	private LdapConfigDao ldapConfigDao;
	@Autowired
	private FileItemDao fileItemDao;
	@Autowired
	private UserContactDao userContactDao;
	@Autowired
	private PollDao pollDao;
	@Autowired
	private ConfigurationDao cfgDao;
	@Autowired
	private ChatDao chatDao;
	@Autowired
	private OAuth2Dao auth2Dao;
	@Autowired
	private GroupDao groupDao;
	@Autowired
	private DocumentConverter docConverter;

	private final Map<Long, Long> userMap = new HashMap<>();
	private final Map<Long, Long> groupMap = new HashMap<>();
	private final Map<Long, Long> calendarMap = new HashMap<>();
	private final Map<Long, Long> appointmentMap = new HashMap<>();
	private final Map<Long, Long> roomMap = new HashMap<>();
	private final Map<Long, Long> fileItemMap = new HashMap<>();
	private final Map<Long, Long> messageFolderMap = new HashMap<>();
	private final Map<Long, Long> userContactMap = new HashMap<>();
	private final Map<String, String> fileMap = new HashMap<>();

	private static File validate(String inEname, File intended) throws IOException {
		final String intendedPath = intended.getCanonicalPath();
		String ename = File.pathSeparatorChar != '\\' && inEname.indexOf('\\') > -1
				? inEname.replace('\\', '/') : inEname;
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
		File f = OmFileHelper.getNewDir(OmFileHelper.getUploadImportDir(), "import_" + CalendarPatterns.getTimeForStreamId(new Date()));
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

	public void performImport(InputStream is) throws Exception {
		userMap.clear();
		groupMap.clear();
		calendarMap.clear();
		appointmentMap.clear();
		roomMap.clear();
		messageFolderMap.clear();
		userContactMap.clear();
		fileMap.clear();
		messageFolderMap.put(INBOX_FOLDER_ID, INBOX_FOLDER_ID);
		messageFolderMap.put(SENT_FOLDER_ID, SENT_FOLDER_ID);
		messageFolderMap.put(TRASH_FOLDER_ID, TRASH_FOLDER_ID);

		File f = unzip(is);
		Registry registry = new Registry();
		Strategy strategy = new RegistryStrategy(registry);
		RegistryMatcher matcher = new RegistryMatcher();
		Serializer simpleSerializer = new Persister(strategy, matcher);

		matcher.bind(Long.class, LongTransform.class);
		registry.bind(Date.class, DateConverter.class);

		BackupVersion ver = getVersion(simpleSerializer, f);
		importConfigs(f);
		importGroups(f, simpleSerializer);
		Long defaultLdapId = importLdap(f, simpleSerializer);
		importOauth(f, simpleSerializer);
		importUsers(f, defaultLdapId);
		importRooms(f);
		importRoomGroups(f);
		importChat(f);
		importCalendars(f);
		importAppointments(f);
		importMeetingMembers(f);
		importRecordings(f);
		importPrivateMsgFolders(f, simpleSerializer);
		importContacts(f);
		importPrivateMsgs(f);
		List<FileItem> files = importFiles(f);
		importPolls(f);
		importRoomFiles(f);

		log.info("Room files import complete, starting copy of files and folders");
		/*
		 * ##################### Import real files and folders
		 */
		importFolders(f);

		if (ver.compareTo(BackupVersion.get("4.0.0")) < 0) {
			for (BaseFileItem bfi : files) {
				if (bfi.isDeleted()) {
					continue;
				}
				if (BaseFileItem.Type.Presentation == bfi.getType()) {
					convertOldPresentation((FileItem)bfi);
					fileItemDao._update(bfi);
				}
				if (BaseFileItem.Type.WmlFile == bfi.getType()) {
					try {
						Whiteboard wb = WbConverter.convert((FileItem)bfi);
						wb.save(bfi.getFile().toPath());
					} catch (Exception e) {
						log.error("Unexpected error while converting WB", e);
					}
				}
			}
		}
		log.info("File explorer item import complete, clearing temp files");

		FileUtils.deleteDirectory(f);
	}

	private static BackupVersion getVersion(Serializer ser, File f) throws Exception {
		List<BackupVersion> list = readList(ser, f, "version.xml", "version", BackupVersion.class, null, true);
		return list.isEmpty() ? new BackupVersion() : list.get(0);
	}

	/*
	 * ##################### Import Configs
	 */
	private void importConfigs(File f) throws Exception {
		Registry registry = new Registry();
		Strategy strategy = new RegistryStrategy(registry);
		RegistryMatcher matcher = new RegistryMatcher();
		Serializer serializer = new Persister(strategy, matcher);

		matcher.bind(Long.class, LongTransform.class);
		registry.bind(Date.class, DateConverter.class);
		registry.bind(User.class, new UserConverter(userDao, userMap));

		List<Configuration> list = readList(serializer, f, "configs.xml", "configs", Configuration.class, null, true);
		for (Configuration c : list) {
			if (c.getKey() == null || c.isDeleted()) {
				continue;
			}
			String newKey = outdatedConfigKeys.get(c.getKey());
			if (newKey != null) {
				c.setKey(newKey);
			}
			Configuration.Type type = configTypes.get(c.getKey());
			if (type != null) {
				c.setType(type);
				if (Configuration.Type.bool == type) {
					c.setValue(String.valueOf("1".equals(c.getValue()) || "yes".equals(c.getValue()) || "true".equals(c.getValue())));
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
		}
	}

	/*
	 * ##################### Import Groups
	 */
	private void importGroups(File f, Serializer simpleSerializer) throws Exception {
		log.info("Configs import complete, starting group import");
		List<Group> list = readList(simpleSerializer, f, "organizations.xml", "organisations", Group.class);
		for (Group o : list) {
			Long oldId = o.getId();
			o.setId(null);
			o = groupDao.update(o, null);
			groupMap.put(oldId, o.getId());
		}
	}

	/*
	 * ##################### Import LDAP Configs
	 */
	private Long importLdap(File f, Serializer simpleSerializer) throws Exception {
		log.info("Groups import complete, starting LDAP config import");
		Long defaultLdapId = cfgDao.getLong(CONFIG_DEFAULT_LDAP_ID, null);
		List<LdapConfig> list = readList(simpleSerializer, f, "ldapconfigs.xml", "ldapconfigs", LdapConfig.class, null, true);
		for (LdapConfig c : list) {
			if (Strings.isEmpty(c.getName())) {
				continue;
			}
			if ("local DB [internal]".equals(c.getName())) {
				continue;
			}
			c.setId(null);
			c = ldapConfigDao.update(c, null);
			if (defaultLdapId == null) {
				defaultLdapId = c.getId();
			}
		}
		return defaultLdapId;
	}

	/*
	 * ##################### OAuth2 servers
	 */
	private void importOauth(File f, Serializer simpleSerializer) throws Exception {
		log.info("Ldap config import complete, starting OAuth2 server import");
		List<OAuthServer> list = readList(simpleSerializer, f, "oauth2servers.xml", "oauth2servers", OAuthServer.class
				, (node, s) -> {
					try {
						InputNode item = node.getNext();
						do {
							if (item == null) {
								break;
							}
							String name = item.getName();
							String val = item.getValue();
							if ("loginParamName".equals(name) && !Strings.isEmpty(val)) {
								s.addMapping(OAuthUser.PARAM_LOGIN, val);
							}
							if ("emailParamName".equals(name) && !Strings.isEmpty(val)) {
								s.addMapping(OAuthUser.PARAM_EMAIL, val);
							}
							if ("firstnameParamName".equals(name) && !Strings.isEmpty(val)) {
								s.addMapping(OAuthUser.PARAM_FNAME, val);
							}
							if ("lastnameParamName".equals(name) && !Strings.isEmpty(val)) {
								s.addMapping(OAuthUser.PARAM_LNAME, val);
							}
							item = node.getNext(); //HACK to handle old mapping
						} while (item != null && !"OAuthServer".equals(item.getName()));
					} catch (Exception e) {
						log.error("Unexpected error while patching OAuthServer", e);
					}
					if (s.getRequestInfoMethod() == null) {
						s.setRequestInfoMethod(RequestInfoMethod.GET);
					}
				}
				, true);
		for (OAuthServer s : list) {
			s.setId(null);
			auth2Dao.update(s, null);
		}
	}

	/*
	 * ##################### Import Users
	 */
	private void importUsers(File f, Long defaultLdapId) throws Exception {
		log.info("OAuth2 servers import complete, starting user import");
		String jNameTimeZone = cfgDao.getString(CONFIG_DEFAULT_TIMEZONE, "Europe/Berlin");
		List<User> list = readUserList(f, "users.xml", "users");
		int minLoginLength = getMinLoginLength();
		for (User u : list) {
			if (u.getLogin() == null || u.isDeleted()) {
				continue;
			}
			if (u.getType() == User.Type.contact && u.getLogin().length() < minLoginLength) {
				u.setLogin(UUID.randomUUID().toString());
			}

			String tz = u.getTimeZoneId();
			if (tz == null) {
				u.setTimeZoneId(jNameTimeZone);
				u.setForceTimeZoneCheck(true);
			} else {
				u.setForceTimeZoneCheck(false);
			}

			Long userId = u.getId();
			u.setId(null);
			if (u.getSipUser() != null && u.getSipUser().getId() != 0) {
				u.getSipUser().setId(0);
			}
			if (LDAP_EXT_TYPE.equals(u.getExternalType()) && User.Type.external != u.getType()) {
				log.warn("Found LDAP user in 'old' format, external_type == 'LDAP':: " + u);
				u.setType(User.Type.ldap);
				u.setExternalType(null);
				if (u.getDomainId() == null) {
					u.setDomainId(defaultLdapId); //domainId was not supported in old versions of OM
				}
			}
			if (!Strings.isEmpty(u.getExternalType())) {
				u.setType(User.Type.external);
			}
			if (AuthLevelUtil.hasLoginLevel(u.getRights()) && !Strings.isEmpty(u.getActivatehash())) {
				u.setActivatehash(null);
			}
			userDao.update(u, Long.valueOf(-1));
			userMap.put(userId, u.getId());
		}
	}

	/*
	 * ##################### Import Rooms
	 */
	private void importRooms(File f) throws Exception {
		log.info("Users import complete, starting room import");
		List<Room> list = readRoomList(f, "rooms.xml", "rooms");
		for (Room r : list) {
			Long roomId = r.getId();

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
		}
	}

	/*
	 * ##################### Import Room Groups
	 */
	private void importRoomGroups(File f) throws Exception {
		log.info("Room import complete, starting room groups import");
		Registry registry = new Registry();
		Strategy strategy = new RegistryStrategy(registry);
		Serializer serializer = new Persister(strategy);

		registry.bind(Group.class, new GroupConverter(groupDao, groupMap));
		registry.bind(Room.class, new RoomConverter(roomDao, roomMap));

		List<RoomGroup> list = readList(serializer, f, "rooms_organisation.xml", "room_organisations", RoomGroup.class);
		for (RoomGroup ro : list) {
			Room r = roomDao.get(ro.getRoom().getId());
			if (r == null || ro.getGroup() == null || ro.getGroup().getId() == null) {
				continue;
			}
			if (r.getGroups() == null) {
				r.setGroups(new ArrayList<>());
			}
			ro.setId(null);
			ro.setRoom(r);
			r.getGroups().add(ro);
			roomDao.update(r, null);
		}
	}

	/*
	 * ##################### Import Chat messages
	 */
	private void importChat(File f) throws Exception {
		log.info("Room groups import complete, starting chat messages import");
		Registry registry = new Registry();
		Strategy strategy = new RegistryStrategy(registry);
		Serializer serializer = new Persister(strategy);

		registry.bind(User.class, new UserConverter(userDao, userMap));
		registry.bind(Room.class, new RoomConverter(roomDao, roomMap));
		registry.bind(Date.class, DateConverter.class);

		List<ChatMessage> list = readList(serializer, f, "chat_messages.xml", "chat_messages", ChatMessage.class, null, true);
		for (ChatMessage m : list) {
			m.setId(null);
			if (m.getFromUser() == null || m.getFromUser().getId() == null) {
				continue;
			}
			chatDao.update(m, m.getSent());
		}
	}

	/*
	 * ##################### Import Calendars
	 */
	private void importCalendars(File f) throws Exception {
		log.info("Chat messages import complete, starting calendar import");
		Registry registry = new Registry();
		Strategy strategy = new RegistryStrategy(registry);
		Serializer serializer = new Persister(strategy);
		registry.bind(User.class, new UserConverter(userDao, userMap));
		List<OmCalendar> list = readList(serializer, f, "calendars.xml", "calendars", OmCalendar.class, null, true);
		for (OmCalendar c : list) {
			Long id = c.getId();
			c.setId(null);
			c = calendarDao.update(c);
			calendarMap.put(id, c.getId());
		}
	}

	/*
	 * ##################### Import Appointements
	 */
	private void importAppointments(File f) throws Exception {
		log.info("Calendar import complete, starting appointement import");
		Registry registry = new Registry();
		Strategy strategy = new RegistryStrategy(registry);
		Serializer serializer = new Persister(strategy);

		registry.bind(User.class, new UserConverter(userDao, userMap));
		registry.bind(Appointment.Reminder.class, AppointmentReminderTypeConverter.class);
		registry.bind(Room.class, new RoomConverter(roomDao, roomMap));
		registry.bind(Date.class, DateConverter.class);
		registry.bind(OmCalendar.class, new OmCalendarConverter(calendarDao, calendarMap));

		List<Appointment> list = readList(serializer, f, "appointements.xml", "appointments", Appointment.class);
		for (Appointment a : list) {
			Long appId = a.getId();

			// We need to reset this as openJPA reject to store them otherwise
			a.setId(null);
			if (a.getOwner() != null && a.getOwner().getId() == null) {
				a.setOwner(null);
			}
			if (a.getRoom() == null || a.getRoom().getId() == null) {
				log.warn("Appointment without room was found, skipping: {}", a);
				continue;
			}
			if (a.getStart() == null || a.getEnd() == null) {
				log.warn("Appointment without start/end time was found, skipping: {}", a);
				continue;
			}
			a = appointmentDao.update(a, null, false);
			appointmentMap.put(appId, a.getId());
		}
	}

	/*
	 * ##################### Import MeetingMembers
	 *
	 * Reminder Invitations will be NOT send!
	 */
	private void importMeetingMembers(File f) throws Exception {
		log.info("Appointement import complete, starting meeting members import");
		List<MeetingMember> list = readMeetingMemberList(f, "meetingmembers.xml", "meetingmembers");
		for (MeetingMember ma : list) {
			ma.setId(null);
			meetingMemberDao.update(ma);
		}
	}

	/*
	 * ##################### Import Recordings
	 */
	private void importRecordings(File f) throws Exception {
		log.info("Meeting members import complete, starting recordings server import");
		List<Recording> list = readRecordingList(f, "flvRecordings.xml", "flvrecordings");
		for (Recording r : list) {
			Long recId = r.getId();
			r.setId(null);
			if (r.getRoomId() != null) {
				r.setRoomId(roomMap.get(r.getRoomId()));
			}
			if (r.getOwnerId() != null) {
				r.setOwnerId(userMap.get(r.getOwnerId()));
			}
			if (r.getMetaData() != null) {
				for (RecordingMetaData meta : r.getMetaData()) {
					meta.setId(null);
					meta.setRecording(r);
				}
			}
			if (!Strings.isEmpty(r.getHash()) && r.getHash().startsWith(RECORDING_FILE_NAME)) {
				String name = getFileName(r.getHash());
				r.setHash(UUID.randomUUID().toString());
				fileMap.put(String.format(FILE_NAME_FMT, name, EXTENSION_JPG), String.format(FILE_NAME_FMT, r.getHash(), EXTENSION_PNG));
				fileMap.put(String.format("%s.%s.%s", name, EXTENSION_FLV, EXTENSION_MP4), String.format(FILE_NAME_FMT, r.getHash(), EXTENSION_MP4));
			}
			if (Strings.isEmpty(r.getHash())) {
				r.setHash(UUID.randomUUID().toString());
			}
			r = recordingDao.update(r);
			fileItemMap.put(recId, r.getId());
		}
	}

	/*
	 * ##################### Import Private Message Folders
	 */
	private void importPrivateMsgFolders(File f, Serializer simpleSerializer) throws Exception {
		log.info("Recording import complete, starting private message folder import");
		List<PrivateMessageFolder> list = readList(simpleSerializer, f, "privateMessageFolder.xml"
				, "privatemessagefolders", PrivateMessageFolder.class, null, true);
		for (PrivateMessageFolder p : list) {
			Long folderId = p.getId();
			PrivateMessageFolder storedFolder = privateMessageFolderDao.get(folderId);
			if (storedFolder == null) {
				p.setId(null);
				Long newFolderId = privateMessageFolderDao.addPrivateMessageFolderObj(p);
				messageFolderMap.put(folderId, newFolderId);
			}
		}
	}

	/*
	 * ##################### Import User Contacts
	 */
	private void importContacts(File f) throws Exception {
		log.info("Private message folder import complete, starting user contacts import");
		Registry registry = new Registry();
		Strategy strategy = new RegistryStrategy(registry);
		Serializer serializer = new Persister(strategy);

		registry.bind(User.class, new UserConverter(userDao, userMap));

		List<UserContact> list = readList(serializer, f, "userContacts.xml", "usercontacts", UserContact.class, null, true);
		for (UserContact uc : list) {
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
		}
	}

	/*
	 * ##################### Import Private Messages
	 */
	private void importPrivateMsgs(File f) throws Exception {
		log.info("Usercontact import complete, starting private messages item import");
		Registry registry = new Registry();
		Strategy strategy = new RegistryStrategy(registry);
		Serializer serializer = new Persister(strategy);

		registry.bind(User.class, new UserConverter(userDao, userMap));
		registry.bind(Room.class, new RoomConverter(roomDao, roomMap));
		registry.bind(Date.class, DateConverter.class);

		List<PrivateMessage> list = readList(serializer, f, "privateMessages.xml", "privatemessages", PrivateMessage.class, null, true);
		boolean oldBackup = true;
		for (PrivateMessage p : list) {
			if (p.getFolderId() == null || p.getFolderId().longValue() < 0) {
				oldBackup = false;
				break;
			}
		}
		for (PrivateMessage p : list) {
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
			if (oldBackup && p.getOwner() != null && p.getOwner().getId() != null
					&& p.getFrom() != null && p.getFrom().getId() != null
					&& p.getOwner().getId() == p.getFrom().getId())
			{
				p.setFolderId(SENT_FOLDER_ID);
			}
			privateMessageDao.update(p, null);
		}
	}

	/*
	 * ##################### Import File-Explorer Items
	 */
	private List<FileItem> importFiles(File f) throws Exception {
		log.info("Private message import complete, starting file explorer item import");
		List<FileItem> result = new ArrayList<>();
		List<FileItem> list = readFileItemList(f, "fileExplorerItems.xml", "fileExplorerItems");
		for (FileItem file : list) {
			Long fId = file.getId();
			// We need to reset this as openJPA reject to store them otherwise
			file.setId(null);
			Long roomId = file.getRoomId();
			file.setRoomId(roomMap.containsKey(roomId) ? roomMap.get(roomId) : null);
			if (file.getOwnerId() != null) {
				file.setOwnerId(userMap.get(file.getOwnerId()));
			}
			if (file.getParentId() != null && file.getParentId().longValue() <= 0L) {
				file.setParentId(null);
			}
			if (Strings.isEmpty(file.getHash())) {
				file.setHash(UUID.randomUUID().toString());
			}
			file = fileItemDao.update(file);
			result.add(file);
			fileItemMap.put(fId, file.getId());
		}
		return result;
	}

	/*
	 * ##################### Import Room Polls
	 */
	private void importPolls(File f) throws Exception {
		log.info("File explorer item import complete, starting room poll import");
		Registry registry = new Registry();
		Strategy strategy = new RegistryStrategy(registry);
		RegistryMatcher matcher = new RegistryMatcher();
		Serializer serializer = new Persister(strategy, matcher);

		matcher.bind(Integer.class, IntegerTransform.class);
		registry.bind(User.class, new UserConverter(userDao, userMap));
		registry.bind(Room.class, new RoomConverter(roomDao, roomMap));
		registry.bind(RoomPoll.Type.class, PollTypeConverter.class);
		registry.bind(Date.class, DateConverter.class);

		List<RoomPoll> list = readList(serializer, f, "roompolls.xml", "roompolls", RoomPoll.class, null, true);
		for (RoomPoll rp : list) {
			rp.setId(null);
			if (rp.getRoom() == null || rp.getRoom().getId() == null) {
				//room was deleted
				continue;
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
		}
	}

	/*
	 * ##################### Import Room Files
	 */
	private void importRoomFiles(File f) throws Exception {
		log.info("Poll import complete, starting room files import");
		Registry registry = new Registry();
		Strategy strategy = new RegistryStrategy(registry);
		Serializer serializer = new Persister(strategy);

		registry.bind(BaseFileItem.class, new BaseFileItemConverter(fileItemDao, fileItemMap));

		List<RoomFile> list = readList(serializer, f, "roomFiles.xml", "RoomFiles", RoomFile.class, null, true);
		for (RoomFile rf : list) {
			Room r = roomDao.get(roomMap.get(rf.getRoomId()));
			if (r == null || rf.getFile() == null || rf.getFile().getId() == null) {
				continue;
			}
			if (r.getFiles() == null) {
				r.setFiles(new ArrayList<>());
			}
			rf.setId(null);
			rf.setRoomId(r.getId());
			r.getFiles().add(rf);
			roomDao.update(r, null);
		}
	}

	private static <T> List<T> readList(Serializer ser, File baseDir, String fileName, String listNodeName, Class<T> clazz) throws Exception {
		return readList(ser, baseDir, fileName, listNodeName, clazz, null, false);
	}

	private static <T> List<T> readList(Serializer ser, File baseDir, String fileName, String listNodeName, Class<T> clazz, BiConsumer<InputNode, T> consumer, boolean notThow) throws Exception {
		List<T> list = new ArrayList<>();
		File xml = new File(baseDir, fileName);
		if (!xml.exists()) {
			final String msg = fileName + " missing";
			if (notThow) {
				log.debug(msg);
				return list;
			} else {
				throw new BackupException(msg);
			}
		}
		try (InputStream rootIs1 = new FileInputStream(xml); InputStream rootIs2 = new FileInputStream(xml)) {
			InputNode root1 = NodeBuilder.read(rootIs1);
			InputNode root2 = NodeBuilder.read(rootIs2); // for various hacks
			InputNode listNode1 = root1.getNext();
			InputNode listNode2 = root2.getNext(); // for various hacks
			if (listNodeName.equals(listNode1.getName())) {
				InputNode item1 = listNode1.getNext();
				while (item1 != null) {
					T o = ser.read(clazz, item1, false);;
					if (consumer != null) {
						consumer.accept(listNode2, o);
					}
					list.add(o);
					item1 = listNode1.getNext();
				}
			}
		}
		return list;
	}

	private static Node getNode(Node doc, String name) {
		if (doc != null) {
			NodeList nl = doc.getChildNodes();
			for (int i = 0; i < nl.getLength(); ++i) {
				Node node = nl.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE && name.equals(node.getNodeName())) {
					return node;
				}
			}
		}
		return null;
	}

	public List<User> readUserList(InputStream xml, String listNodeName) throws Exception {
		return readUserList(new InputSource(xml), listNodeName);
	}

	public List<User> readUserList(File baseDir, String fileName, String listNodeName) throws Exception {
		File xml = new File(baseDir, fileName);
		if (!xml.exists()) {
			throw new BackupException(fileName + " missing");
		}

		return readUserList(new InputSource(xml.toURI().toASCIIString()), listNodeName);
	}

	private static Long getPrefixedId(String prefix, File f) {
		String n = f.getName();
		int dIdx = n.indexOf('.', prefix.length());
		if (n.indexOf(prefix) > -1) {
			return importLongType(n.substring(prefix.length(), dIdx > -1 ? dIdx : n.length()));
		}
		return null;
	}

	private void importFolders(File importBaseDir) throws IOException {
		// Now check the room files and import them
		final File roomFilesFolder = new File(importBaseDir, BCKP_ROOM_FILES);

		File uploadDir = getUploadDir();

		log.debug("roomFilesFolder PATH {} ", roomFilesFolder.getCanonicalPath());

		if (roomFilesFolder.exists()) {
			for (File file : roomFilesFolder.listFiles()) {
				if (file.isDirectory()) {
					String fName = file.getName();
					if (PROFILES_DIR.equals(fName)) {
						// profile should correspond to the new user id
						for (File profile : file.listFiles()) {
							Long oldId = getPrefixedId(PROFILES_PREFIX, profile);
							Long id = oldId != null ? userMap.get(oldId) : null;
							if (id != null) {
								FileUtils.copyDirectory(profile, getUploadProfilesUserDir(id));
							}
						}
					} else if (FILES_DIR.equals(fName)) {
						log.debug("Entered FILES folder");
						for (File rf : file.listFiles()) {
							// going to fix images
							if (rf.isFile() && rf.getName().endsWith(EXTENSION_JPG)) {
								FileUtils.copyFileToDirectory(rf, getImgDir(rf.getName()));
							} else {
								FileUtils.copyDirectory(rf, new File(getUploadFilesDir(), rf.getName()));
							}
						}
					} else if (GROUP_LOGO_DIR.equals(fName)) {
						log.debug("Entered group logo folder");
						for (File logo : file.listFiles()) {
							Long oldId = getPrefixedId(GROUP_LOGO_PREFIX, logo);
							Long id = oldId != null ? groupMap.get(oldId) : null;
							if (id != null) {
								FileUtils.moveFile(logo, OmFileHelper.getGroupLogo(id, false));
							}
						}
					} else {
						// check if folder is room folder, store it under new id if necessary
						Long oldId = importLongType(fName);
						Long id = oldId != null ? roomMap.get(oldId) : null;
						if (id != null) {
							FileUtils.copyDirectory(file, getUploadRoomDir(id.toString()));
						} else {
							FileUtils.copyDirectory(file, new File(uploadDir, fName));
						}
					}
				}
			}
		}

		// Now check the recordings and import them
		final File recDir = new File(importBaseDir, BCKP_RECORD_FILES);
		log.debug("sourceDirRec PATH {}", recDir.getCanonicalPath());
		if (recDir.exists()) {
			final File hiberDir = getStreamsHibernateDir();
			for (File r : recDir.listFiles()) {
				String n = fileMap.get(r.getName());
				if (n != null) {
					FileUtils.copyFile(r, new File(hiberDir, n));
				} else {
					FileUtils.copyFileToDirectory(r, hiberDir);
				}
			}
		}
		final File cssDir = new File(importBaseDir, CSS_DIR);
		if (cssDir.exists()) {
			final File wCssDir = getCssDir();
			for (File css : cssDir.listFiles()) {
				FileUtils.copyFileToDirectory(css, wCssDir);
			}
		}
	}

	private static File getImgDir(String name) {
		int start = name.startsWith(THUMB_IMG_PREFIX) ? THUMB_IMG_PREFIX.length() : 0;
		String hash = name.substring(start, name.length() - EXTENSION_PNG.length() - 1);
		return new File(getUploadFilesDir(), hash);
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

	private static String getCountry(String countryId) {
		if (countries.isEmpty()) {
			try (InputStream is = BackupImport.class.getResourceAsStream("countries.properties")) {
				countries.load(is);
			} catch (IOException e) {
				log.error("Unexpected exception during countries import", e);
			}
		}
		return countries.getProperty(String.format("country.%s", countryId));
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

	public List<FileItem> readFileItemList(File baseDir, String fileName, String listNodeName) throws Exception {
		List<FileItem> list = new ArrayList<>();
		File xml = new File(baseDir, fileName);
		if (xml.exists()) {
			Registry registry = new Registry();
			Strategy strategy = new RegistryStrategy(registry);
			RegistryMatcher matcher = new RegistryMatcher();
			Serializer ser = new Persister(strategy, matcher);

			matcher.bind(Long.class, LongTransform.class);
			matcher.bind(Integer.class, IntegerTransform.class);
			registry.bind(Date.class, DateConverter.class);

			try (InputStream rootIs1 = new FileInputStream(xml); InputStream rootIs2 = new FileInputStream(xml);) {
				InputNode root = NodeBuilder.read(rootIs1);
				InputNode root1 = NodeBuilder.read(rootIs2); //HACK to handle old isFolder, isImage, isVideo, isRecording, isPresentation, isStoredWmlFile, isChart
				InputNode listNode = root.getNext();
				InputNode listNode1 = root1.getNext(); //HACK to handle old isFolder, isImage, isVideo, isRecording, isPresentation, isStoredWmlFile, isChart
				if (listNodeName.equals(listNode.getName())) {
					InputNode item = listNode.getNext();
					InputNode item1 = listNode1.getNext(); //HACK to handle old isFolder, isImage, isVideo, isRecording, isPresentation, isStoredWmlFile, isChart
					while (item != null) {
						FileItem f = ser.read(FileItem.class, item, false);

						//HACK to handle old isFolder, isImage, isVideo, isRecording, isPresentation, isStoredWmlFile, isChart, wmlFilePath
						do {
							if (item1 == null) {
								break;
							}
							String name = item1.getName();
							String val = item1.getValue();
							if ("wmlFilePath".equals(name) && !Strings.isEmpty(val)) {
								f.setType(BaseFileItem.Type.WmlFile);
								f.setHash(val);
							}
							if (f.getType() == null && "isChart".equals(name) && "true".equals(val)) {
								f.setType(BaseFileItem.Type.PollChart);
							}
							if ("isImage".equals(name) && "true".equals(val)) {
								f.setType(BaseFileItem.Type.Image);
							}
							if ("isVideo".equals(name) && "true".equals(val)) {
								f.setType(BaseFileItem.Type.Video);
							}
							if ("isRecording".equals(name) && "true".equals(val)) {
								log.warn("Recording is stored in FileExplorer Items");
								f.setType(BaseFileItem.Type.Video);
							}
							if ("isPresentation".equals(name) && "true".equals(val)) {
								f.setType(BaseFileItem.Type.Presentation);
							}
							if ("isStoredWmlFile".equals(name) && "true".equals(val)) {
								f.setType(BaseFileItem.Type.WmlFile);
							}
							if (("folder".equals(name) || "isFolder".equals(name)) && "true".equals(val)) {
								f.setType(BaseFileItem.Type.Folder);
							}
							item1 = listNode1.getNext(); //HACK to handle old isFolder, isImage, isVideo, isRecording, isPresentation, isStoredWmlFile, isChart, wmlFilePath
						} while (item1 != null && !"fileExplorerItem".equals(item1.getName()));

						//Some hashes were stored with file extension
						int idx = f.getHash() == null ? -1 : f.getHash().indexOf('.');
						if (idx > -1) {
							String hash = f.getHash().substring(0, idx);
							if (BaseFileItem.Type.Image == f.getType()) {
								fileMap.put(f.getHash(), String.format("%s/%s", hash, f.getHash()));
							}
							f.setHash(hash);
						}
						list.add(f);
						item = listNode.getNext();
					}
				}
			}
		}
		return list;
	}

	public List<Recording> readRecordingList(File baseDir, String fileName, String listNodeName) throws Exception {
		List<Recording> list = new ArrayList<>();
		File xml = new File(baseDir, fileName);
		if (xml.exists()) {
			Registry registry = new Registry();
			Strategy strategy = new RegistryStrategy(registry);
			RegistryMatcher matcher = new RegistryMatcher();
			Serializer ser = new Persister(strategy, matcher);

			matcher.bind(Long.class, LongTransform.class);
			matcher.bind(Integer.class, IntegerTransform.class);
			registry.bind(Date.class, DateConverter.class);
			registry.bind(Recording.Status.class, RecordingStatusConverter.class);

			try (InputStream rootIs1 = new FileInputStream(xml); InputStream rootIs2 = new FileInputStream(xml);) {
				InputNode root = NodeBuilder.read(rootIs1);
				InputNode root1 = NodeBuilder.read(rootIs2); //HACK to handle old isFolder
				InputNode listNode = root.getNext();
				InputNode listNode1 = root1.getNext(); //HACK to handle old isFolder
				if (listNodeName.equals(listNode.getName())) {
					InputNode item = listNode.getNext();
					InputNode item1 = listNode1.getNext(); //HACK to handle old isFolder
					while (item != null) {
						Recording r = ser.read(Recording.class, item, false);

						boolean isFolder = false;
						//HACK to handle old isFolder
						do {
							String name = item1.getName();
							String val = item1.getValue();
							if (("folder".equals(name) || "isFolder".equals(name)) && "true".equals(val)) {
								isFolder = true;
							}
							item1 = listNode1.getNext(); //HACK to handle Address inside user
						} while (item1 != null && !"flvrecording".equals(item1.getName()));

						if (r.getType() == null) {
							r.setType(isFolder ? BaseFileItem.Type.Folder : BaseFileItem.Type.Recording);
						}
						list.add(r);
						item = listNode.getNext();
					}
				}
			}
		}
		return list;
	}

	private List<MeetingMember> readMeetingMemberList(File baseDir, String filename, String listNodeName) throws Exception {
		Registry registry = new Registry();
		Strategy strategy = new RegistryStrategy(registry);
		Serializer ser = new Persister(strategy);

		registry.bind(User.class, new UserConverter(userDao, userMap));
		registry.bind(Appointment.class, new AppointmentConverter(appointmentDao, appointmentMap));

		File xml = new File(baseDir, filename);
		if (!xml.exists()) {
			throw new BackupException(filename + " missing");
		}

		DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = dBuilder.parse(new InputSource(xml.toURI().toASCIIString()));

		StringWriter sw = new StringWriter();
		Transformer xformer = TransformerFactory.newInstance().newTransformer();
		xformer.transform(new DOMSource(doc), new StreamResult(sw));

		List<MeetingMember> list = new ArrayList<>();
		InputNode root = NodeBuilder.read(new StringReader(sw.toString()));
		InputNode root1 = NodeBuilder.read(new StringReader(sw.toString())); //HACK to handle external attendee's firstname, lastname, email
		InputNode listNode = root.getNext();
		InputNode listNode1 = root1.getNext(); //HACK to handle external attendee's firstname, lastname, email
		if (listNodeName.equals(listNode.getName())) {
			InputNode item = listNode.getNext();
			InputNode item1 = listNode1.getNext(); //HACK to handle external attendee's firstname, lastname, email
			while (item != null) {
				MeetingMember mm = ser.read(MeetingMember.class, item, false);

				boolean needToSkip1 = true;
				if (mm.getUser() == null) {
					mm.setUser(new User());
				}
				if (mm.getUser().getId() == null) {
					//HACK to handle external attendee's firstname, lastname, email
					boolean contactValid = false;
					do {
						String name = item1.getName();
						String val = item1.getValue();
						if (User.Type.contact == mm.getUser().getType() && "firstname".equals(name)) {
							mm.getUser().setFirstname(val);
						}
						if (User.Type.contact == mm.getUser().getType() && "lastname".equals(name)) {
							mm.getUser().setLastname(val);
						}
						if ("email".equals(name)) {
							if (mm.getAppointment() != null && mm.getAppointment().getOwner() != null) {
								mm.setUser(userDao.getContact(val, mm.getAppointment().getOwner()));
							}
							contactValid = true;
						}
						item1 = listNode1.getNext(); //HACK to handle old om_time_zone
					} while (item1 != null && !"meetingmember".equals(item1.getName()));
					if (!contactValid) {
						mm = null;
					}
					needToSkip1 = false;
				}
				if (needToSkip1) {
					do {
						item1 = listNode1.getNext(); //HACK to handle Address inside user
					} while (item1 != null && !"meetingmember".equals(item1.getName()));
				}
				item = listNode.getNext();
				if (mm != null && !mm.isDeleted() && mm.getUser() != null && mm.getAppointment() != null && mm.getAppointment().getId() != null) {
					mm.setId(null);
					list.add(mm);
				}
			}
		}
		return list;
	}

	private List<User> readUserList(InputSource xml, String listNodeName) throws Exception {
		Registry registry = new Registry();
		Strategy strategy = new RegistryStrategy(registry);
		Serializer ser = new Persister(strategy);

		registry.bind(Group.class, new GroupConverter(groupDao, groupMap));
		registry.bind(Salutation.class, SalutationConverter.class);
		registry.bind(Date.class, DateConverter.class);

		DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = dBuilder.parse(xml);
		final Map<String, Integer> userEmailMap = new HashMap<>();
		final Map<String, Integer> userLoginMap = new HashMap<>();
		//add existence email from database
		List<User>  users = userDao.getAllUsers();
		for (User u : users){
			if (u.getAddress() == null || u.getAddress().getEmail() == null || User.Type.user != u.getType()) {
				continue;
			}
			userEmailMap.put(u.getAddress().getEmail(), Integer.valueOf(-1));
			userLoginMap.put(u.getLogin(), Integer.valueOf(-1));
		}
		Node nList = getNode(getNode(doc, "root"), listNodeName);
		if (nList != null) {
			NodeList nl = nList.getChildNodes();
			// one of the old OM version created 2 nodes "deleted" this code block handles this
			for (int i = 0; i < nl.getLength(); ++i) {
				Node user = nl.item(i);
				NodeList nl1 = user.getChildNodes();
				boolean deletedFound = false;
				for (int j = 0; j < nl1.getLength(); ++j) {
					Node node = nl1.item(j);
					if (node.getNodeType() == Node.ELEMENT_NODE && "deleted".equals(node.getNodeName())) {
						if (deletedFound) {
							user.removeChild(node);
							break;
						}
						deletedFound = true;
					}
				}
			}
		}

		StringWriter sw = new StringWriter();
		Transformer xformer = TransformerFactory.newInstance().newTransformer();
		xformer.transform(new DOMSource(doc), new StreamResult(sw));

		List<User> list = new ArrayList<>();
		InputNode root = NodeBuilder.read(new StringReader(sw.toString()));
		InputNode root1 = NodeBuilder.read(new StringReader(sw.toString())); //HACK to handle Address inside user
		InputNode root2 = NodeBuilder.read(new StringReader(sw.toString())); //HACK to handle old om_time_zone, level_id, status
		InputNode listNode = root.getNext();
		InputNode listNode1 = root1.getNext(); //HACK to handle Address inside user
		InputNode listNode2 = root2.getNext(); //HACK to handle old om_time_zone
		if (listNodeName.equals(listNode.getName())) {
			InputNode item = listNode.getNext();
			InputNode item1 = listNode1.getNext(); //HACK to handle Address inside user
			InputNode item2 = listNode2.getNext(); //HACK to handle old om_time_zone, level_id, status
			while (item != null) {
				User u = ser.read(User.class, item, false);

				boolean needToSkip1 = true;
				//HACK to handle Address inside user
				if (u.getAddress() == null) {
					Address a = ser.read(Address.class, item1, false);
					u.setAddress(a);
					needToSkip1 = false;
				}
				if (needToSkip1) {
					do {
						item1 = listNode1.getNext(); //HACK to handle Address inside user
					} while (item1 != null && !"user".equals(item1.getName()));
				}
				String levelId = null, status = null, stateId = null;
				do {
					String name = item2.getName();
					String val = item2.getValue();
					if (u.getTimeZoneId() == null && "omTimeZone".equals(name)) {
						u.setTimeZoneId(val == null ? null : getTimeZone(val).getID());
					}
					if ("level_id".equals(name)) {
						levelId = val;
					}
					if ("status".equals(name)) {
						status = val;
					}
					if ("state_id".equals(name)) {
						stateId = val;
					}
					item2 = listNode2.getNext(); //HACK to handle old om_time_zone, level_id, status
				} while (item2 != null && !"user".equals(item2.getName()));
				if (u.getRights().isEmpty()) {
					u.getRights().add(Right.Room);
					if ("1".equals(status)) {
						u.getRights().add(Right.Dashboard);
						u.getRights().add(Right.Login);
					}
					if ("3".equals(levelId)) {
						u.getRights().add(Right.Admin);
						u.getRights().add(Right.Soap);
					}
					if ("4".equals(levelId)) {
						u.getRights().add(Right.Soap);
					}
				}
				// check that email is unique
				if (u.getAddress() != null && u.getAddress().getEmail() != null && User.Type.user == u.getType()) {
					if (userEmailMap.containsKey(u.getAddress().getEmail())) {
						log.warn("Email is duplicated for user " + u.toString());
						String updateEmail = String.format("modified_by_import_<%s>%s", UUID.randomUUID(), u.getAddress().getEmail());
						u.getAddress().setEmail(updateEmail);
					}
					userEmailMap.put(u.getAddress().getEmail(), Integer.valueOf(userEmailMap.size()));
				}
				if (userLoginMap.containsKey(u.getLogin())) {
					log.warn("Login is duplicated for user " + u.toString());
					String updateLogin = String.format("modified_by_import_<%s>%s", UUID.randomUUID(), u.getLogin());
					u.setLogin(updateLogin);
				}
				userLoginMap.put(u.getLogin(), Integer.valueOf(userLoginMap.size()));
				// check old stateId
				if (!Strings.isEmpty(stateId)) {
					String country = getCountry(stateId);
					if (!Strings.isEmpty(country)) {
						if (u.getAddress() == null) {
							u.setAddress(new Address());
						}
						u.getAddress().setCountry(country);
					}
				}
				if (u.getGroupUsers() != null) {
					for (GroupUser gu : u.getGroupUsers()) {
						gu.setUser(u);
					}
				}
				list.add(u);
				item = listNode.getNext();
			}
		}
		return list;
	}

	private List<Room> readRoomList(File baseDir, String fileName, String listNodeName) throws Exception {
		List<Room> list = new ArrayList<>();
		File xml = new File(baseDir, fileName);
		if (xml.exists()) {
			Registry registry = new Registry();
			Strategy strategy = new RegistryStrategy(registry);
			RegistryMatcher matcher = new RegistryMatcher();
			Serializer ser = new Persister(strategy, matcher);

			matcher.bind(Long.class, LongTransform.class);
			matcher.bind(Integer.class, IntegerTransform.class);
			registry.bind(User.class, new UserConverter(userDao, userMap));
			registry.bind(Room.Type.class, RoomTypeConverter.class);
			registry.bind(Date.class, DateConverter.class);

			try (InputStream rootIs1 = new FileInputStream(xml); InputStream rootIs2 = new FileInputStream(xml);) {
				InputNode root = NodeBuilder.read(rootIs1);
				InputNode root1 = NodeBuilder.read(rootIs2); //HACK to handle old hideTopBar, hideChat, hideActivitiesAndActions, hideFilesExplorer, hideActionsMenu, hideScreenSharing, hideWhiteboard, showMicrophoneStatus
				InputNode listNode = root.getNext();
				InputNode listNode1 = root1.getNext(); //HACK to handle old hideTopBar, hideChat, hideActivitiesAndActions, hideFilesExplorer, hideActionsMenu, hideScreenSharing, hideWhiteboard, showMicrophoneStatus
				if (listNodeName.equals(listNode.getName())) {
					InputNode item = listNode.getNext();
					InputNode item1 = listNode1.getNext(); //HACK to handle old hideTopBar, hideChat, hideActivitiesAndActions, hideFilesExplorer, hideActionsMenu, hideScreenSharing, hideWhiteboard, showMicrophoneStatus
					while (item != null) {
						Room r = ser.read(Room.class, item, false);

						Boolean showMicrophoneStatus = null;
						//HACK to handle old hideTopBar, hideChat, hideActivitiesAndActions, hideFilesExplorer, hideActionsMenu, hideScreenSharing, hideWhiteboard, showMicrophoneStatus
						do {
							String name = item1.getName();
							String val = item1.getValue();
							if ("hideTopBar".equals(name) && "true".equals(val)) {
								r.hide(RoomElement.TopBar);
							}
							if ("hideChat".equals(name) && "true".equals(val)) {
								r.hide(RoomElement.Chat);
							}
							if ("hideActivitiesAndActions".equals(name) && "true".equals(val)) {
								r.hide(RoomElement.Activities);
							}
							if ("hideFilesExplorer".equals(name) && "true".equals(val)) {
								r.hide(RoomElement.Files);
							}
							if ("hideActionsMenu".equals(name) && "true".equals(val)) {
								r.hide(RoomElement.ActionMenu);
							}
							if ("hideScreenSharing".equals(name) && "true".equals(val)) {
								r.hide(RoomElement.ScreenSharing);
							}
							if ("hideWhiteboard".equals(name) && "true".equals(val)) {
								r.hide(RoomElement.Whiteboard);
							}
							if ("showMicrophoneStatus".equals(name)) {
								showMicrophoneStatus = Boolean.valueOf(val);
							}
							item1 = listNode1.getNext(); //HACK to handle Address inside user
						} while (item1 != null && !"room".equals(item1.getName()));

						if (Boolean.FALSE.equals(showMicrophoneStatus)) {
							r.hide(RoomElement.MicrophoneStatus);
						}
						list.add(r);
						item = listNode.getNext();
					}
				}
			}
		}
		return list;
	}
}
