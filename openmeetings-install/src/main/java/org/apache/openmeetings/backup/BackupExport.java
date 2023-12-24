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

import static org.apache.openmeetings.db.bind.Constants.APPOINTMENT_LIST_NODE;
import static org.apache.openmeetings.db.bind.Constants.CALENDAR_LIST_NODE;
import static org.apache.openmeetings.db.bind.Constants.CFG_LIST_NODE;
import static org.apache.openmeetings.db.bind.Constants.CHAT_LIST_NODE;
import static org.apache.openmeetings.db.bind.Constants.CONTACT_LIST_NODE;
import static org.apache.openmeetings.db.bind.Constants.EXTRA_MENU_LIST_NODE;
import static org.apache.openmeetings.db.bind.Constants.FILE_LIST_NODE;
import static org.apache.openmeetings.db.bind.Constants.GROUP_LIST_NODE;
import static org.apache.openmeetings.db.bind.Constants.LDAP_LIST_NODE;
import static org.apache.openmeetings.db.bind.Constants.MMEMBER_LIST_NODE;
import static org.apache.openmeetings.db.bind.Constants.MSG_FOLDER_LIST_NODE;
import static org.apache.openmeetings.db.bind.Constants.MSG_LIST_NODE;
import static org.apache.openmeetings.db.bind.Constants.OAUTH_LIST_NODE;
import static org.apache.openmeetings.db.bind.Constants.POLL_LIST_NODE;
import static org.apache.openmeetings.db.bind.Constants.RECORDING_LIST_NODE;
import static org.apache.openmeetings.db.bind.Constants.ROOM_FILE_LIST_NODE;
import static org.apache.openmeetings.db.bind.Constants.ROOM_GRP_LIST_NODE;
import static org.apache.openmeetings.db.bind.Constants.ROOM_LIST_NODE;
import static org.apache.openmeetings.db.bind.Constants.USER_LIST_NODE;
import static org.apache.openmeetings.db.bind.Constants.VERSION_LIST_NODE;
import static org.apache.openmeetings.util.OmFileHelper.BACKUP_DIR;
import static org.apache.openmeetings.util.OmFileHelper.BCKP_RECORD_FILES;
import static org.apache.openmeetings.util.OmFileHelper.BCKP_ROOM_FILES;
import static org.apache.openmeetings.util.OmFileHelper.CSS_DIR;
import static org.apache.openmeetings.util.OmFileHelper.IMPORT_DIR;
import static org.apache.openmeetings.util.OmFileHelper.getCustomCss;
import static org.apache.openmeetings.util.OmFileHelper.getStreamsHibernateDir;
import static org.apache.openmeetings.util.OmFileHelper.getUploadDir;

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
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import jakarta.inject.Inject;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;

import org.apache.commons.io.FileUtils;
import org.apache.openmeetings.db.dao.basic.ChatDao;
import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dao.calendar.AppointmentDao;
import org.apache.openmeetings.db.dao.calendar.MeetingMemberDao;
import org.apache.openmeetings.db.dao.calendar.OmCalendarDao;
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
import org.apache.openmeetings.db.entity.basic.ChatMessage;
import org.apache.openmeetings.db.entity.basic.Configuration;
import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.db.entity.calendar.OmCalendar;
import org.apache.openmeetings.db.entity.file.FileItem;
import org.apache.openmeetings.db.entity.record.Recording;
import org.apache.openmeetings.db.entity.room.ExtraMenu;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.room.RoomPoll;
import org.apache.openmeetings.db.entity.server.LdapConfig;
import org.apache.openmeetings.db.entity.server.OAuthServer;
import org.apache.openmeetings.db.entity.user.Group;
import org.apache.openmeetings.db.entity.user.PrivateMessage;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.installation.ImportInitvalues;
import org.apache.openmeetings.installation.InstallationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

/**
 *
 * @author sebastianwagner
 *
 */
@Component
public class BackupExport {
	private static final Logger log = LoggerFactory.getLogger(BackupExport.class);
	private static final String BACKUP_COMMENT = """
###############################################
 This File is auto-generated by the Backup Tool
 you should use the BackupPanel to modify or change this file
 see https://openmeetings.apache.org/Upgrade.html for Details
 ###############################################
""";

	@Inject
	private AppointmentDao appointmentDao;
	@Inject
	private OmCalendarDao calendarDao;
	@Inject
	private FileItemDao fileItemDao;
	@Inject
	private RecordingDao recordingDao;
	@Inject
	private UserDao userDao;
	@Inject
	private MeetingMemberDao meetingMemberDao;
	@Inject
	private LdapConfigDao ldapConfigDao;
	@Inject
	private PrivateMessageDao privateMessageDao;
	@Inject
	private PrivateMessageFolderDao privateMessageFolderDao;
	@Inject
	private UserContactDao userContactDao;
	@Inject
	private PollDao pollManager;
	@Inject
	private ConfigurationDao configurationDao;
	@Inject
	private ChatDao chatDao;
	@Inject
	private OAuth2Dao auth2Dao;
	@Inject
	private GroupDao groupDao;
	@Inject
	private RoomDao roomDao;
	@Inject
	private ExtraMenuDao menuDao;

	public void performExport(File zip, boolean includeFiles, AtomicInteger progress) throws Exception {
		if (zip.getParentFile() != null && !zip.getParentFile().exists()) {
			zip.getParentFile().mkdirs();
		}
		try (FileOutputStream fos = new FileOutputStream(zip); ZipOutputStream zos = new ZipOutputStream(fos)) {
			progress.set(0);
			/*
			 * ##################### Backup Groups
			 */
			writeList(zos, "version.xml", VERSION_LIST_NODE, List.of(BackupVersion.get()));
			progress.set(2);
			exportGroups(zos);
			progress.set(5);
			exportUsers(zos);
			progress.set(10);
			exportRoom(zos);
			progress.set(15);
			exportRoomGroup(zos);
			progress.set(17);
			exportRoomFile(zos);
			progress.set(17);
			exportCalendar(zos);
			progress.set(22);
			exportAppointment(zos);
			progress.set(25);
			exportMeetingMember(zos);
			progress.set(30);
			exportLdap(zos);
			progress.set(35);
			exportOauth(zos);
			progress.set(45);
			exportPrivateMsg(zos);
			progress.set(50);
			exportPrivateMsgFolder(zos);
			progress.set(55);
			exportContacts(zos);
			progress.set(60);
			exportFile(zos);
			progress.set(65);
			exportRecording(zos);
			progress.set(70);
			exportPoll(zos);
			progress.set(75);
			exportConfig(zos);
			progress.set(80);
			exportChat(zos);
			progress.set(85);
			exportExtraMenus(zos);
			progress.set(87);

			if (includeFiles) {
				exportFiles(progress, zos);
			}
		}
		progress.set(100);
		log.debug("---Done");
	}

	private void exportFiles(AtomicInteger progress, ZipOutputStream zos) throws IOException {
		//##################### Backup Room Files
		for (File file : getUploadDir().listFiles()) {
			String fName = file.getName();
			if (file.isDirectory() && !IMPORT_DIR.equals(fName) && !BACKUP_DIR.equals(fName)) {
				log.debug("### {}", file.getName());
				writeZipDir(BCKP_ROOM_FILES, file.getParentFile().toURI(), file, zos);
			}
		}

		//##################### Backup Recording Files
		final File recDir = getStreamsHibernateDir();
		writeZipDir(BCKP_RECORD_FILES, recDir.toURI(), recDir, zos);
		progress.set(90);

		final File customCss = getCustomCss();
		if (customCss != null && customCss.exists() && customCss.isFile()) {
			writeZip(CSS_DIR, customCss.getParentFile().toURI(), customCss, zos);
		}
	}

	/*
	 * ##################### Backup  Groups
	 */
	private void exportGroups(ZipOutputStream zos) throws Exception {
		List<Group> list = groupDao.get(0, Integer.MAX_VALUE);
		writeList(zos, "organizations.xml", GROUP_LIST_NODE, list);
	}

	/*
	 * ##################### Backup Users
	 */
	private void exportUsers(ZipOutputStream zos) throws Exception {
		List<User> list = userDao.getAllBackupUsers();
		writeList(zos, "users.xml", USER_LIST_NODE, list);
	}

	/*
	 * ##################### Backup Room
	 */
	private void exportRoom(ZipOutputStream zos) throws Exception {
		List<Room> list = roomDao.get();
		writeList(zos, "rooms.xml", ROOM_LIST_NODE, list);
	}

	/*
	 * ##################### Backup Room Groups
	 */
	private void exportRoomGroup(ZipOutputStream zos) throws Exception {
		writeList(zos, "rooms_organisation.xml", ROOM_GRP_LIST_NODE, roomDao.getGroups());
	}

	/*
	 * ##################### Backup Room Files
	 */
	private void exportRoomFile(ZipOutputStream zos) throws Exception {
		writeList(zos, "roomFiles.xml", ROOM_FILE_LIST_NODE, roomDao.getFiles());
	}

	/*
	 * ##################### Backup Calendars
	 */
	private void exportCalendar(ZipOutputStream zos) throws Exception {
		List<OmCalendar> list = calendarDao.get();
		writeList(zos, "calendars.xml", CALENDAR_LIST_NODE, list);
	}

	/*
	 * ##################### Backup Appointments
	 */
	private void exportAppointment(ZipOutputStream zos) throws Exception {
		List<Appointment> list = appointmentDao.get();
		writeList(zos, "appointements.xml", APPOINTMENT_LIST_NODE, list);
	}

	/*
	 * ##################### Backup Meeting Members
	 */
	private void exportMeetingMember(ZipOutputStream zos) throws Exception {
		writeList(zos, "meetingmembers.xml",
				MMEMBER_LIST_NODE, meetingMemberDao.get());
	}

	/*
	 * ##################### LDAP Configs
	 */
	private void exportLdap(ZipOutputStream zos) throws Exception {
		List<LdapConfig> ldapList = ldapConfigDao.get();
		if (!ldapList.isEmpty()) {
			ldapList.remove(0);
		}
		writeList(zos, "ldapconfigs.xml", LDAP_LIST_NODE, ldapList);
	}

	/*
	 * ##################### OAuth2 servers
	 */
	private void exportOauth(ZipOutputStream zos) throws Exception {
		List<OAuthServer> list = auth2Dao.get(0, Integer.MAX_VALUE);
		writeList(zos, "oauth2servers.xml", OAUTH_LIST_NODE, list);
	}

	/*
	 * ##################### Private Messages
	 */
	private void exportPrivateMsg(ZipOutputStream zos) throws Exception {
		List<PrivateMessage> list = privateMessageDao.get(0, Integer.MAX_VALUE);
		writeList(zos, "privateMessages.xml", MSG_LIST_NODE, list);
	}

	/*
	 * ##################### Private Message Folders
	 */
	private void exportPrivateMsgFolder(ZipOutputStream zos) throws Exception {
		writeList(zos, "privateMessageFolder.xml",
				MSG_FOLDER_LIST_NODE, privateMessageFolderDao.get(0, Integer.MAX_VALUE));
	}

	/*
	 * ##################### User Contacts
	 */
	private void exportContacts(ZipOutputStream zos) throws Exception {
		writeList(zos, "userContacts.xml", CONTACT_LIST_NODE, userContactDao.get());
	}

	/*
	 * ##################### File-Explorer
	 */
	private void exportFile(ZipOutputStream zos) throws Exception {
		List<FileItem> list = fileItemDao.get();
		writeList(zos, "fileExplorerItems.xml", FILE_LIST_NODE, list);
	}

	/*
	 * ##################### Recordings
	 */
	private void exportRecording(ZipOutputStream zos) throws Exception {
		List<Recording> list = recordingDao.get();
		writeList(zos, "flvRecordings.xml", RECORDING_LIST_NODE, list);
	}

	/*
	 * ##################### Polls
	 */
	private void exportPoll(ZipOutputStream zos) throws Exception {
		List<RoomPoll> list = pollManager.get();
		writeList(zos, "roompolls.xml", POLL_LIST_NODE, list);
	}

	/*
	 * ##################### Config
	 */
	private void exportConfig(ZipOutputStream zos) throws Exception {
		List<Configuration> list = configurationDao.get(0, Integer.MAX_VALUE);
		writeList(zos, "configs.xml", CFG_LIST_NODE, list);
	}

	/*
	 * ##################### Chat
	 */
	private void exportChat(ZipOutputStream zos) throws Exception {
		List<ChatMessage> list = chatDao.get(0, Integer.MAX_VALUE);
		writeList(zos, "chat_messages.xml", CHAT_LIST_NODE, list);
	}

	private void exportExtraMenus(ZipOutputStream zos) throws Exception {
		List<ExtraMenu> list = menuDao.get(0, Integer.MAX_VALUE);
		writeList(zos, "extraMenus.xml", EXTRA_MENU_LIST_NODE, list);
	}

	public static <T> ByteArrayOutputStream stream(String listElement, List<T> list) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(10 * 1024); //10K
		writeList(baos, listElement, list);
		return baos;
	}

	private static <T> void writeList(ZipOutputStream zos, String fileName, String listElement, List<T> list) throws Exception {
		ZipEntry e = new ZipEntry(fileName);
		zos.putNextEntry(e);
		ByteArrayOutputStream baos = stream(listElement, list);
		zos.write(baos.toByteArray());
		zos.closeEntry();
	}

	private static <T> void writeList(OutputStream os, String listElement, List<T> list) throws Exception {
		try (OutputStreamWriter sw = new OutputStreamWriter(os)) {
			sw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
			sw.write("<!--");
			sw.write(BACKUP_COMMENT);
			sw.write("-->\n");
			sw.write("<root>\n");
			sw.write("<" + listElement + ">\n");

			if (list != null && !list.isEmpty()) {
				@SuppressWarnings("unchecked")
				Class<T> eClazz = (Class<T>)list.get(0).getClass();
				JAXBContext jc = JAXBContext.newInstance(eClazz);
				Marshaller marshaller = jc.createMarshaller();
				marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
				marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
				for (T t : list) {
					marshaller.marshal(t, sw);
					sw.write("\n");
				}
			}
			sw.write("</" + listElement + ">\n");
			sw.write("</root>\n");
		}
	}

	private static void writeZip(String prefix, URI base, File file, ZipOutputStream zos) throws IOException {
		String path = prefix + "/" + base.relativize(file.toURI()).toString();
		log.debug("Writing '{}' to zip file", path);
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

	/**
	 * Required during build `generate-configs-xml` goal
	 *
	 * @param args - path to GeneralConfiguration.xml
	 * @throws Exception - in case of any error
	 */
	public static void main(String[] args) throws Exception {
		List<Configuration> list = ImportInitvalues.initialCfgs(new InstallationConfig());
		ByteArrayOutputStream baos = stream("configs", list);
		File f = new File(args[0]);
		if (!f.exists() && !f.getParentFile().exists()) {
			f.getParentFile().mkdirs();
		}
		Files.write(Paths.get(args[0]), baos.toByteArray(), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
	}
}
