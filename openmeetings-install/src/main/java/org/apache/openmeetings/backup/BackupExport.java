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

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.openmeetings.util.OmFileHelper.BACKUP_DIR;
import static org.apache.openmeetings.util.OmFileHelper.BCKP_RECORD_FILES;
import static org.apache.openmeetings.util.OmFileHelper.BCKP_ROOM_FILES;
import static org.apache.openmeetings.util.OmFileHelper.CSS_DIR;
import static org.apache.openmeetings.util.OmFileHelper.IMPORT_DIR;
import static org.apache.openmeetings.util.OmFileHelper.getCustomCss;
import static org.apache.openmeetings.util.OmFileHelper.getStreamsHibernateDir;
import static org.apache.openmeetings.util.OmFileHelper.getUploadDir;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getWebAppRootKey;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.openmeetings.backup.converter.AppointmentConverter;
import org.apache.openmeetings.backup.converter.AppointmentReminderTypeConverter;
import org.apache.openmeetings.backup.converter.BaseFileItemConverter;
import org.apache.openmeetings.backup.converter.DateConverter;
import org.apache.openmeetings.backup.converter.GroupConverter;
import org.apache.openmeetings.backup.converter.PollTypeConverter;
import org.apache.openmeetings.backup.converter.RoomConverter;
import org.apache.openmeetings.backup.converter.RoomTypeConverter;
import org.apache.openmeetings.backup.converter.SalutationConverter;
import org.apache.openmeetings.backup.converter.UserConverter;
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
import org.apache.openmeetings.db.entity.HistoricalEntity;
import org.apache.openmeetings.db.entity.basic.ChatMessage;
import org.apache.openmeetings.db.entity.basic.Configuration;
import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.db.entity.calendar.OmCalendar;
import org.apache.openmeetings.db.entity.file.FileItem;
import org.apache.openmeetings.db.entity.record.Recording;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.room.RoomPoll;
import org.apache.openmeetings.db.entity.server.LdapConfig;
import org.apache.openmeetings.db.entity.server.OAuthServer;
import org.apache.openmeetings.db.entity.user.Group;
import org.apache.openmeetings.db.entity.user.PrivateMessage;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.entity.user.User.Salutation;
import org.apache.openmeetings.installation.ImportInitvalues;
import org.apache.openmeetings.installation.InstallationConfig;
import org.red5.logging.Red5LoggerFactory;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.convert.Registry;
import org.simpleframework.xml.convert.RegistryStrategy;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.strategy.Strategy;
import org.simpleframework.xml.stream.Format;
import org.simpleframework.xml.stream.NodeBuilder;
import org.simpleframework.xml.stream.OutputNode;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author sebastianwagner
 *
 */
@Component
public class BackupExport {
	private static final Logger log = Red5LoggerFactory.getLogger(BackupExport.class, getWebAppRootKey());
	private static final String BACKUP_COMMENT =
			"###############################################\n"
			+ "This File is auto-generated by the Backup Tool \n"
			+ "you should use the BackupPanel to modify or change this file \n"
			+ "see https://openmeetings.apache.org/Upgrade.html for Details \n"
			+ "###############################################\n";

	@Autowired
	private AppointmentDao appointmentDao;
	@Autowired
	private OmCalendarDao calendarDao;
	@Autowired
	private FileItemDao fileItemDao;
	@Autowired
	private RecordingDao recordingDao;
	@Autowired
	private UserDao userDao;
	@Autowired
	private MeetingMemberDao meetingMemberDao;
	@Autowired
	private LdapConfigDao ldapConfigDao;
	@Autowired
	private PrivateMessageDao privateMessageDao;
	@Autowired
	private PrivateMessageFolderDao privateMessageFolderDao;
	@Autowired
	private UserContactDao userContactDao;
	@Autowired
	private PollDao pollManager;
	@Autowired
	private ConfigurationDao configurationDao;
	@Autowired
	private ChatDao chatDao;
	@Autowired
	private OAuth2Dao auth2Dao;
	@Autowired
	private GroupDao groupDao;
	@Autowired
	private RoomDao roomDao;

	public void performExport(File zip, boolean includeFiles, ProgressHolder progressHolder) throws Exception {
		if (zip.getParentFile() != null && !zip.getParentFile().exists()) {
			zip.getParentFile().mkdirs();
		}
		try (FileOutputStream fos = new FileOutputStream(zip); ZipOutputStream zos = new ZipOutputStream(fos)) {
			Serializer ser = new Persister();

			progressHolder.setProgress(0);
			/*
			 * ##################### Backup Groups
			 */
			writeList(ser, zos, "version.xml", "version", Arrays.asList(BackupVersion.get()));
			progressHolder.setProgress(2);
			exportGroups(zos, progressHolder);
			exportUsers(zos, progressHolder);
			exportRoom(zos, progressHolder);
			exportRoomGroup(zos, progressHolder);
			exportRoomFile(zos, progressHolder);
			exportCalendar(zos, progressHolder);
			exportAppointment(zos, progressHolder);
			exportMeetingMember(zos, progressHolder);
			exportLdap(zos, progressHolder, ser);
			exportOauth(zos, progressHolder);
			exportPrivateMsg(zos, progressHolder);
			exportPrivateMsgFolder(zos, progressHolder, ser);
			exportContacts(zos, progressHolder);
			exportFile(zos, progressHolder);
			exportRecording(zos, progressHolder);
			exportPoll(zos, progressHolder);
			exportConfig(zos, progressHolder);
			exportChat(zos, progressHolder);

			if (includeFiles) {
				//##################### Backup Room Files
				for (File file : getUploadDir().listFiles()) {
					String fName = file.getName();
					if (file.isDirectory() && !IMPORT_DIR.equals(fName) && !BACKUP_DIR.equals(fName)) {
						log.debug("### " + file.getName());
						writeZipDir(BCKP_ROOM_FILES, file.getParentFile().toURI(), file, zos);
					}
				}

				//##################### Backup Recording Files
				final File recDir = getStreamsHibernateDir();
				writeZipDir(BCKP_RECORD_FILES, recDir.toURI(), recDir, zos);
				progressHolder.setProgress(90);

				final File customCss = getCustomCss();
				if (customCss != null && customCss.exists() && customCss.isFile()) {
					writeZip(CSS_DIR, customCss.getParentFile().toURI(), customCss, zos);
				}
			}
		}
		progressHolder.setProgress(100);
		log.debug("---Done");
	}

	private static <T extends HistoricalEntity> void bindDate(Registry registry, List<T> list) throws Exception {
		bindDate(registry, list, HistoricalEntity::getInserted);
	}

	private static <T> void bindDate(Registry registry, List<T> list, Function<T, ?> func) throws Exception {
		if (list != null) {
			for (T e : list) {
				Object d = func.apply(e);
				if (d != null) {
					Class<?> dateClass = d.getClass();
					registry.bind(dateClass, DateConverter.class);
					break;
				}
			}
		}
	}
	/*
	 * ##################### Backup  Groups
	 */
	private void exportGroups(ZipOutputStream zos, ProgressHolder progressHolder) throws Exception {
		Registry registry = new Registry();
		Strategy strategy = new RegistryStrategy(registry);
		Serializer ser = new Persister(strategy);
		List<Group> list = groupDao.get(0, Integer.MAX_VALUE);
		bindDate(registry, list);
		writeList(ser, zos, "organizations.xml", "organisations", list);
		progressHolder.setProgress(5);
	}

	/*
	 * ##################### Backup Users
	 */
	private void exportUsers(ZipOutputStream zos, ProgressHolder progressHolder) throws Exception {
		Registry registry = new Registry();
		Strategy strategy = new RegistryStrategy(registry);
		Serializer ser = new Persister(strategy);

		registry.bind(Group.class, GroupConverter.class);
		registry.bind(Salutation.class, SalutationConverter.class);
		List<User> list = userDao.getAllBackupUsers();
		bindDate(registry, list);

		writeList(ser, zos, "users.xml", "users", list);
		progressHolder.setProgress(10);
	}

	/*
	 * ##################### Backup Room
	 */
	private void exportRoom(ZipOutputStream zos, ProgressHolder progressHolder) throws Exception {
		Registry registry = new Registry();
		Strategy strategy = new RegistryStrategy(registry);
		Serializer serializer = new Persister(strategy);

		registry.bind(User.class, UserConverter.class);
		registry.bind(Room.Type.class, RoomTypeConverter.class);
		List<Room> list = roomDao.get();
		bindDate(registry, list);
		writeList(serializer, zos, "rooms.xml", "rooms", list);
		progressHolder.setProgress(15);
	}

	/*
	 * ##################### Backup Room Groups
	 */
	private void exportRoomGroup(ZipOutputStream zos, ProgressHolder progressHolder) throws Exception {
		Registry registry = new Registry();
		Strategy strategy = new RegistryStrategy(registry);
		Serializer serializer = new Persister(strategy);

		registry.bind(Group.class, GroupConverter.class);
		registry.bind(Room.class, RoomConverter.class);

		writeList(serializer, zos, "rooms_organisation.xml", "room_organisations", roomDao.getGroups());
		progressHolder.setProgress(17);
	}

	/*
	 * ##################### Backup Room Files
	 */
	private void exportRoomFile(ZipOutputStream zos, ProgressHolder progressHolder) throws Exception {
		Registry registry = new Registry();
		Strategy strategy = new RegistryStrategy(registry);
		Serializer serializer = new Persister(strategy);

		registry.bind(FileItem.class, BaseFileItemConverter.class);
		registry.bind(Recording.class, BaseFileItemConverter.class);

		writeList(serializer, zos, "roomFiles.xml", "RoomFiles", roomDao.getFiles());
		progressHolder.setProgress(17);
	}

	/*
	 * ##################### Backup Calendars
	 */
	private void exportCalendar(ZipOutputStream zos, ProgressHolder progressHolder) throws Exception {
		List<OmCalendar> list = calendarDao.get();
		Registry registry = new Registry();
		Strategy strategy = new RegistryStrategy(registry);
		Serializer serializer = new Persister(strategy);
		registry.bind(User.class, UserConverter.class);

		writeList(serializer, zos, "calendars.xml", "calendars", list);
		progressHolder.setProgress(22);
	}

	/*
	 * ##################### Backup Appointments
	 */
	private void exportAppointment(ZipOutputStream zos, ProgressHolder progressHolder) throws Exception {
		List<Appointment> list = appointmentDao.get();
		Registry registry = new Registry();
		Strategy strategy = new RegistryStrategy(registry);
		Serializer serializer = new Persister(strategy);

		registry.bind(User.class, UserConverter.class);
		registry.bind(Appointment.Reminder.class, AppointmentReminderTypeConverter.class);
		registry.bind(Room.class, RoomConverter.class);
		bindDate(registry, list);

		writeList(serializer, zos, "appointements.xml", "appointments", list);
		progressHolder.setProgress(25);
	}

	/*
	 * ##################### Backup Meeting Members
	 */
	private void exportMeetingMember(ZipOutputStream zos, ProgressHolder progressHolder) throws Exception {
		Registry registry = new Registry();
		Strategy strategy = new RegistryStrategy(registry);
		Serializer serializer = new Persister(strategy);

		registry.bind(User.class, UserConverter.class);
		registry.bind(Appointment.class, AppointmentConverter.class);

		writeList(serializer, zos, "meetingmembers.xml",
				"meetingmembers", meetingMemberDao.getMeetingMembers());
		progressHolder.setProgress(30);
	}

	/*
	 * ##################### LDAP Configs
	 */
	private void exportLdap(ZipOutputStream zos, ProgressHolder progressHolder, Serializer ser) throws Exception {
		List<LdapConfig> ldapList = ldapConfigDao.get();
		if (!ldapList.isEmpty()) {
			ldapList.remove(0);
		}
		writeList(ser, zos, "ldapconfigs.xml", "ldapconfigs", ldapList);
		progressHolder.setProgress(35);
	}

	/*
	 * ##################### OAuth2 servers
	 */
	private void exportOauth(ZipOutputStream zos, ProgressHolder progressHolder) throws Exception {
		Registry registry = new Registry();
		Strategy strategy = new RegistryStrategy(registry);
		Serializer serializer = new Persister(strategy);
		List<OAuthServer> list = auth2Dao.get(0, Integer.MAX_VALUE);
		bindDate(registry, list);
		writeList(serializer, zos, "oauth2servers.xml", "oauth2servers", list);
		progressHolder.setProgress(45);
	}

	/*
	 * ##################### Private Messages
	 */
	private void exportPrivateMsg(ZipOutputStream zos, ProgressHolder progressHolder) throws Exception {
		List<PrivateMessage> list = privateMessageDao.get(0, Integer.MAX_VALUE);
		Registry registry = new Registry();
		Strategy strategy = new RegistryStrategy(registry);
		Serializer serializer = new Persister(strategy);

		registry.bind(User.class, UserConverter.class);
		registry.bind(Room.class, RoomConverter.class);
		bindDate(registry, list, PrivateMessage::getInserted);
		writeList(serializer, zos, "privateMessages.xml", "privatemessages", list);
		progressHolder.setProgress(50);
	}

	/*
	 * ##################### Private Message Folders
	 */
	private void exportPrivateMsgFolder(ZipOutputStream zos, ProgressHolder progressHolder, Serializer ser) throws Exception {
		writeList(ser, zos, "privateMessageFolder.xml",
				"privatemessagefolders", privateMessageFolderDao.get(0, Integer.MAX_VALUE));
		progressHolder.setProgress(55);
	}

	/*
	 * ##################### User Contacts
	 */
	private void exportContacts(ZipOutputStream zos, ProgressHolder progressHolder) throws Exception {
		Registry registry = new Registry();
		Strategy strategy = new RegistryStrategy(registry);
		Serializer serializer = new Persister(strategy);

		registry.bind(User.class, UserConverter.class);

		writeList(serializer, zos, "userContacts.xml", "usercontacts", userContactDao.get());
		progressHolder.setProgress(60);
	}

	/*
	 * ##################### File-Explorer
	 */
	private void exportFile(ZipOutputStream zos, ProgressHolder progressHolder) throws Exception {
		List<FileItem> list = fileItemDao.get();
		Registry registry = new Registry();
		Strategy strategy = new RegistryStrategy(registry);
		Serializer serializer = new Persister(strategy);

		bindDate(registry, list);
		writeList(serializer, zos, "fileExplorerItems.xml", "fileExplorerItems", list);
		progressHolder.setProgress(65);
	}

	/*
	 * ##################### Recordings
	 */
	private void exportRecording(ZipOutputStream zos, ProgressHolder progressHolder) throws Exception {
		List<Recording> list = recordingDao.get();
		Registry registry = new Registry();
		Strategy strategy = new RegistryStrategy(registry);
		Serializer serializer = new Persister(strategy);

		bindDate(registry, list);
		writeList(serializer, zos, "flvRecordings.xml", "flvrecordings", list);
		progressHolder.setProgress(70);
	}

	/*
	 * ##################### Polls
	 */
	private void exportPoll(ZipOutputStream zos, ProgressHolder progressHolder) throws Exception {
		List<RoomPoll> list = pollManager.get();
		Registry registry = new Registry();
		Strategy strategy = new RegistryStrategy(registry);
		Serializer serializer = new Persister(strategy);

		registry.bind(User.class, UserConverter.class);
		registry.bind(Room.class, RoomConverter.class);
		registry.bind(RoomPoll.Type.class, PollTypeConverter.class);
		bindDate(registry, list, RoomPoll::getCreated);
		writeList(serializer, zos, "roompolls.xml", "roompolls", list);
		progressHolder.setProgress(75);
	}

	/*
	 * ##################### Config
	 */
	private void exportConfig(ZipOutputStream zos, ProgressHolder progressHolder) throws Exception {
		List<Configuration> list = configurationDao.get(0, Integer.MAX_VALUE);
		Serializer serializer = getConfigSerializer(list);

		writeList(serializer, zos, "configs.xml", "configs", list);
		progressHolder.setProgress(80);
	}

	/*
	 * ##################### Chat
	 */
	private void exportChat(ZipOutputStream zos, ProgressHolder progressHolder) throws Exception {
		List<ChatMessage> list = chatDao.get(0, Integer.MAX_VALUE);
		Registry registry = new Registry();
		registry.bind(User.class, UserConverter.class);
		registry.bind(Room.class, RoomConverter.class);
		Strategy strategy = new RegistryStrategy(registry);
		Serializer serializer = new Persister(strategy);

		bindDate(registry, list, ChatMessage::getSent);
		writeList(serializer, zos, "chat_messages.xml", "chat_messages", list);
		progressHolder.setProgress(85);
	}

	private static Serializer getConfigSerializer(List<Configuration> list) throws Exception {
		Registry registry = new Registry();
		registry.bind(User.class, UserConverter.class);
		Strategy strategy = new RegistryStrategy(registry);
		Serializer serializer = new Persister(strategy);

		bindDate(registry, list);
		return serializer;
	}

	private static <T> ByteArrayOutputStream stream(Serializer ser, String listElement, List<T> list) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(10 * 1024); //10K
		writeList(ser, baos, listElement, list);
		return baos;
	}

	private static <T> void writeList(Serializer ser, ZipOutputStream zos, String fileName, String listElement, List<T> list) throws Exception {
		ZipEntry e = new ZipEntry(fileName);
		zos.putNextEntry(e);
		ByteArrayOutputStream baos = stream(ser, listElement, list);
		zos.write(baos.toByteArray());
		zos.closeEntry();
	}

	private static <T> void writeList(Serializer ser, OutputStream os, String listElement, List<T> list) throws Exception {
		Format format = new Format("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		OutputNode doc = NodeBuilder.write(new OutputStreamWriter(os, UTF_8), format);
		OutputNode root = doc.getChild("root");
		root.setComment(BACKUP_COMMENT);
		OutputNode listNode = root.getChild(listElement);

		if (list != null) {
			for (T t : list) {
				try {
					ser.write(t, listNode);
				} catch (Exception e) {
					log.debug("Exception While writing node of type: " + t.getClass(), e);
				}
			}
		}
		root.commit();
	}

	private static void writeZip(String prefix, URI base, File file, ZipOutputStream zos) throws IOException {
		String path = prefix + "/" + base.relativize(file.toURI()).toString();
		log.debug("Writing '" + path + "' to zip file");
		ZipEntry zipEntry = new ZipEntry(path);
		zos.putNextEntry(zipEntry);

		FileUtils.copyFile(file, zos);
		zos.closeEntry();
	}

	private void writeZipDir(String prefix, URI base, File dir, ZipOutputStream zos) throws IOException {
		for (File file : dir.listFiles()) {
			if (file.isDirectory()) {
				writeZipDir(prefix, base, file, zos);
			} else {
				writeZip(prefix, base, file, zos);
			}
		}
	}

	public static void main(String[] args) throws Exception {
		List<Configuration> list = ImportInitvalues.initialCfgs(new InstallationConfig());
		Serializer ser = getConfigSerializer(list);
		ByteArrayOutputStream baos = stream(ser, "configs", list);
		File f = new File(args[0]);
		if (!f.exists() && !f.getParentFile().exists()) {
			f.getParentFile().mkdirs();
		}
		Files.write(Paths.get(args[0]), baos.toByteArray(), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
	}
}
