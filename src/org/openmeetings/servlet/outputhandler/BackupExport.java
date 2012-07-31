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
package org.openmeetings.servlet.outputhandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.transaction.util.FileHelper;
import org.openmeetings.app.OpenmeetingsVariables;
import org.openmeetings.app.backup.AppointmentCategoryConverter;
import org.openmeetings.app.backup.AppointmentConverter;
import org.openmeetings.app.backup.AppointmentReminderTypeConverter;
import org.openmeetings.app.backup.DateConverter;
import org.openmeetings.app.backup.OmTimeZoneConverter;
import org.openmeetings.app.backup.OrganisationConverter;
import org.openmeetings.app.backup.PollTypeConverter;
import org.openmeetings.app.backup.RoomConverter;
import org.openmeetings.app.backup.RoomTypeConverter;
import org.openmeetings.app.backup.StateConverter;
import org.openmeetings.app.backup.UserConverter;
import org.openmeetings.app.data.basic.AuthLevelmanagement;
import org.openmeetings.app.data.basic.Configurationmanagement;
import org.openmeetings.app.data.basic.Sessionmanagement;
import org.openmeetings.app.data.basic.dao.LdapConfigDaoImpl;
import org.openmeetings.app.data.calendar.daos.AppointmentDaoImpl;
import org.openmeetings.app.data.calendar.daos.MeetingMemberDaoImpl;
import org.openmeetings.app.data.conference.PollManagement;
import org.openmeetings.app.data.conference.Roommanagement;
import org.openmeetings.app.data.conference.dao.RoomModeratorsDaoImpl;
import org.openmeetings.app.data.file.dao.FileExplorerItemDaoImpl;
import org.openmeetings.app.data.flvrecord.FlvRecordingDaoImpl;
import org.openmeetings.app.data.flvrecord.FlvRecordingMetaDataDaoImpl;
import org.openmeetings.app.data.user.Organisationmanagement;
import org.openmeetings.app.data.user.Usermanagement;
import org.openmeetings.app.data.user.dao.PrivateMessageFolderDaoImpl;
import org.openmeetings.app.data.user.dao.PrivateMessagesDaoImpl;
import org.openmeetings.app.data.user.dao.UserContactsDaoImpl;
import org.openmeetings.app.data.user.dao.UsersDaoImpl;
import org.openmeetings.app.persistence.beans.adresses.States;
import org.openmeetings.app.persistence.beans.basic.Configuration;
import org.openmeetings.app.persistence.beans.basic.OmTimeZone;
import org.openmeetings.app.persistence.beans.calendar.Appointment;
import org.openmeetings.app.persistence.beans.calendar.AppointmentCategory;
import org.openmeetings.app.persistence.beans.calendar.AppointmentReminderTyps;
import org.openmeetings.app.persistence.beans.domain.Organisation;
import org.openmeetings.app.persistence.beans.files.FileExplorerItem;
import org.openmeetings.app.persistence.beans.flvrecord.FlvRecording;
import org.openmeetings.app.persistence.beans.poll.PollType;
import org.openmeetings.app.persistence.beans.poll.RoomPoll;
import org.openmeetings.app.persistence.beans.rooms.RoomTypes;
import org.openmeetings.app.persistence.beans.rooms.Rooms;
import org.openmeetings.app.persistence.beans.user.PrivateMessages;
import org.openmeetings.app.persistence.beans.user.Users;
import org.openmeetings.app.sip.api.impl.asterisk.dao.AsteriskDAOImpl;
import org.openmeetings.utils.OmFileHelper;
import org.openmeetings.utils.math.CalendarPatterns;
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

/**
 * 
 * @author sebastianwagner
 * 
 */
public class BackupExport {

	private static final Logger log = Red5LoggerFactory.getLogger(
			BackupExport.class, OpenmeetingsVariables.webAppRootKey);
	private static final String BACKUP_COMMENT = 
			"###############################################\n"
			+ "This File is auto-generated by the Backup Tool \n"
			+ "you should use the BackupPanel to modify or change this file \n"
			+ "see http://incubator.apache.org/openmeetings/Upgrade.html for Details \n"
			+ "###############################################\n";

	@Autowired
	private AppointmentDaoImpl appointmentDao;
	@Autowired
	private Sessionmanagement sessionManagement;
	@Autowired
	private Usermanagement userManagement;
	@Autowired
	private Organisationmanagement organisationmanagement;
	@Autowired
	private Roommanagement roommanagement;
	@Autowired
	private FileExplorerItemDaoImpl fileExplorerItemDao;
	@Autowired
	private FlvRecordingDaoImpl flvRecordingDao;
	@Autowired
	private FlvRecordingMetaDataDaoImpl flvRecordingMetaDataDao;
	@Autowired
	private RoomModeratorsDaoImpl roomModeratorsDao;
	@Autowired
	private UsersDaoImpl usersDao;
	@Autowired
	private MeetingMemberDaoImpl meetingMemberDao;
	@Autowired
	private LdapConfigDaoImpl ldapConfigDao;
	@Autowired
	private PrivateMessagesDaoImpl privateMessagesDao;
	@Autowired
	private PrivateMessageFolderDaoImpl privateMessageFolderDao;
	@Autowired
	private UserContactsDaoImpl userContactsDao;
	@Autowired
	private AuthLevelmanagement authLevelManagement;
	@Autowired
	private PollManagement pollManagement;
	@Autowired
	private Configurationmanagement cfgManagement;
	@Autowired
	private AsteriskDAOImpl asteriskDAOImpl;

	public void performExport(File filePath, File backup_dir,
			boolean includeFiles) throws Exception {

		if (!backup_dir.exists()) {
			backup_dir.mkdirs();
		}
		Serializer simpleSerializer = new Persister();
		
		/*
		 * ##################### Backup Organizations
		 */
		writeList(simpleSerializer, backup_dir, "organizations.xml",
				"organisations", organisationmanagement.getOrganisations(3L));

		/*
		 * ##################### Backup Users
		 */
		{
			List<Users> list = usersDao.getAllUsersDeleted();
			Registry registry = new Registry();
			Strategy strategy = new RegistryStrategy(registry);
			Serializer serializer = new Persister(strategy);
	
			registry.bind(Organisation.class, OrganisationConverter.class);
			registry.bind(OmTimeZone.class, OmTimeZoneConverter.class);
			registry.bind(States.class, StateConverter.class);
			if (list != null && list.size() > 0) {
				registry.bind(list.get(0).getRegdate().getClass(), DateConverter.class);
			}
			
			writeList(serializer, backup_dir, "users.xml", "users", list);
		}

		/*
		 * ##################### Backup Room
		 */
		{
			Registry registry = new Registry();
			Strategy strategy = new RegistryStrategy(registry);
			Serializer serializer = new Persister(strategy);
	
			registry.bind(Users.class, UserConverter.class);
			registry.bind(RoomTypes.class, RoomTypeConverter.class);
			
			writeList(serializer, backup_dir, "rooms.xml",
					"rooms", roommanagement.getBackupRooms());
		}

		/*
		 * ##################### Backup Room Organizations
		 */
		{
			Registry registry = new Registry();
			Strategy strategy = new RegistryStrategy(registry);
			Serializer serializer = new Persister(strategy);
	
			registry.bind(Organisation.class, OrganisationConverter.class);
			registry.bind(Rooms.class, RoomConverter.class);
			
			writeList(serializer, backup_dir, "rooms_organisation.xml",
					"room_organisations", roommanagement.getRoomsOrganisations());
		}

		/*
		 * ##################### Backup Appointements
		 */
		{
			List<Appointment> list = appointmentDao.getAppointments();
			Registry registry = new Registry();
			Strategy strategy = new RegistryStrategy(registry);
			Serializer serializer = new Persister(strategy);
	
			registry.bind(AppointmentCategory.class, AppointmentCategoryConverter.class);
			registry.bind(Users.class, UserConverter.class);
			registry.bind(AppointmentReminderTyps.class, AppointmentReminderTypeConverter.class);
			registry.bind(Rooms.class, RoomConverter.class);
			if (list != null && list.size() > 0) {
				registry.bind(list.get(0).getAppointmentStarttime().getClass(), DateConverter.class);
			}
			
			writeList(serializer, backup_dir, "appointements.xml", "appointments", list);
		}

		/*
		 * ##################### Backup Meeting Members
		 */
		{
			Registry registry = new Registry();
			Strategy strategy = new RegistryStrategy(registry);
			Serializer serializer = new Persister(strategy);
	
			registry.bind(Users.class, UserConverter.class);
			registry.bind(Appointment.class, AppointmentConverter.class);
			
			writeList(serializer, backup_dir, "meetingmembers.xml",
					"meetingmembers", meetingMemberDao.getMeetingMembers());
		}

		/*
		 * ##################### LDAP Configs
		 */
		writeList(simpleSerializer, backup_dir, "ldapconfigs.xml",
				"ldapconfigs", ldapConfigDao.getLdapConfigs());

		/*
		 * ##################### Private Messages
		 */
		{
			List<PrivateMessages> list = privateMessagesDao.getPrivateMessages();
			Registry registry = new Registry();
			Strategy strategy = new RegistryStrategy(registry);
			Serializer serializer = new Persister(strategy);
	
			registry.bind(Users.class, UserConverter.class);
			registry.bind(Rooms.class, RoomConverter.class);
			if (list != null && list.size() > 0) {
				registry.bind(list.get(0).getInserted().getClass(), DateConverter.class);
			}
			
			writeList(serializer, backup_dir, "privateMessages.xml",
					"privatemessages", list);
		}

		/*
		 * ##################### Private Message Folders
		 */
		writeList(simpleSerializer, backup_dir, "privateMessageFolder.xml",
				"privatemessagefolders", privateMessageFolderDao.getPrivateMessageFolders());

		/*
		 * ##################### User Contacts
		 */
		{
			Registry registry = new Registry();
			Strategy strategy = new RegistryStrategy(registry);
			Serializer serializer = new Persister(strategy);
	
			registry.bind(Users.class, UserConverter.class);
			
			writeList(serializer, backup_dir, "userContacts.xml",
					"usercontacts", userContactsDao.getUserContacts());
		}

		/*
		 * ##################### File-Explorer
		 */
		{
			List<FileExplorerItem> list = fileExplorerItemDao.getFileExplorerItems();
			Registry registry = new Registry();
			Strategy strategy = new RegistryStrategy(registry);
			Serializer serializer = new Persister(strategy);
	
			if (list != null && list.size() > 0) {
				registry.bind(list.get(0).getInserted().getClass(), DateConverter.class);
			}
			
			writeList(serializer, backup_dir, "fileExplorerItems.xml",
					"fileExplorerItems", list);
		}

		/*
		 * ##################### Recordings
		 */
		{
			List<FlvRecording> list = flvRecordingDao.getAllFlvRecordings();
			Registry registry = new Registry();
			Strategy strategy = new RegistryStrategy(registry);
			Serializer serializer = new Persister(strategy);
	
			if (list != null && list.size() > 0) {
				registry.bind(list.get(0).getInserted().getClass(), DateConverter.class);
			}
			
			writeList(serializer, backup_dir, "flvRecordings.xml",
					"flvrecordings", list);
		}

		/*
		 * ##################### Polls
		 */
		{
			List<RoomPoll> list = pollManagement.getPollListBackup();
			Registry registry = new Registry();
			Strategy strategy = new RegistryStrategy(registry);
			Serializer serializer = new Persister(strategy);
	
			registry.bind(Users.class, UserConverter.class);
			registry.bind(Rooms.class, RoomConverter.class);
			registry.bind(PollType.class, PollTypeConverter.class);
			if (list != null && list.size() > 0) {
				registry.bind(list.get(0).getCreated().getClass(), DateConverter.class);
			}
			
			writeList(serializer, backup_dir, "roompolls.xml", "roompolls", list);
		}

		/*
		 * ##################### Config
		 */
		{
			List<Configuration> list = cfgManagement.getConfigurations(0, Integer.MAX_VALUE, "conf_key", true);
			Registry registry = new Registry();
			Strategy strategy = new RegistryStrategy(registry);
			Serializer serializer = new Persister(strategy);
	
			if (list != null && list.size() > 0) {
				registry.bind(list.get(0).getStarttime().getClass(), DateConverter.class);
			}
			
			writeList(serializer, backup_dir, "configs.xml", "configs", list);
		}
		
		/*
		 * ##################### Asterisk SIP Data
		 */
		writeList(simpleSerializer, backup_dir, "asterisksipusers.xml",
				"asterisksipusers", asteriskDAOImpl.getAsteriskSipUsers());
		
		/*
		 * ##################### Asterisk SIP Extensions
		 */
		writeList(simpleSerializer, backup_dir, "extensions.xml",
				"extensions", asteriskDAOImpl.getExtensions());
		
		/*
		 * ##################### Asterisk SIP Meetme
		 */
		writeList(simpleSerializer, backup_dir, "members.xml",
				"members", asteriskDAOImpl.getMembers());
		

		if (includeFiles) {
			/*
			 * ##################### Backup Room Files
			 */
			File targetRootDir = new File(backup_dir, "roomFiles");

			if (!targetRootDir.exists()) {
				targetRootDir.mkdir();
			}

			File sourceDir = OmFileHelper.getUploadDir();

			File[] files = sourceDir.listFiles();
			for (File file : files) {
				if (file.isDirectory()) {
					if (!file.getName().equals("backup")
							&& !file.getName().equals("import")) {

						log.debug("### " + file.getName());

						FileHelper.copyRec(file, new File(targetRootDir, file.getName()));
					}
				}
			}

			/*
			 * ##################### Backup Recording Files
			 */
			File targetDirRec = new File(backup_dir, "recordingFiles");

			if (!targetDirRec.exists()) {
				targetDirRec.mkdir();
			}

			File sourceDirRec = OmFileHelper.getStreamsHibernateDir();

			FileHelper.copyRec(sourceDirRec, targetDirRec);
		}

		List<File> fileList = new ArrayList<File>();
		log.debug("---Getting references to all files in: "
				+ backup_dir.getCanonicalPath());
		getAllFiles(backup_dir, fileList);
		log.debug("---Creating zip file");
		writeZipFile(backup_dir, fileList, new FileOutputStream(filePath));
		log.debug("---Done");
	}
	
	private <T> void writeList(Serializer ser, File backup_dir,
			String fileName, String listElement, List<T> list) throws Exception {
		FileOutputStream fos = new FileOutputStream(new File(backup_dir,
				fileName));
		Format format = new Format("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		OutputNode doc = NodeBuilder.write(new OutputStreamWriter(fos, "UTF-8"), format);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest
	 * , javax.servlet.http.HttpServletResponse)
	 */
	public void service(HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, ServletContext servletCtx)
			throws ServletException, IOException {

		String sid = httpServletRequest.getParameter("sid");
		if (sid == null) {
			sid = "default";
		}
		log.debug("sid: " + sid);

		Long users_id = sessionManagement.checkSession(sid);
		Long user_level = userManagement.getUserLevelByID(users_id);

		log.debug("users_id: " + users_id);
		log.debug("user_level: " + user_level);

		if (authLevelManagement.checkAdminLevel(user_level)) {
			// if (true) {

			String includeFileOption = httpServletRequest
					.getParameter("includeFileOption");
			boolean includeFiles = includeFileOption == null || "yes".equals(includeFileOption);

			String moduleName = httpServletRequest
					.getParameter("moduleName");
			if (moduleName == null) {
				moduleName = "moduleName";
			}
			log.debug("moduleName: " + moduleName);

			if (moduleName.equals("backup")) {

				/*
				 * ##################### Create Base Folder structure
				 */

				File working_dir = OmFileHelper.getUploadBackupDir();

				String dateString = "backup_"
						+ CalendarPatterns.getTimeForStreamId(new Date());

				File backup_dir = new File(working_dir, dateString);
				String requestedFile = dateString + ".zip";
				File backupFile = new File(backup_dir, requestedFile);

				try {
					performExport(backupFile, backup_dir, includeFiles);

					httpServletResponse.reset();
					httpServletResponse.resetBuffer();
					httpServletResponse
					.setContentType("APPLICATION/OCTET-STREAM");
					httpServletResponse.setHeader("Content-Disposition",
							"attachment; filename=\"" + requestedFile + "\"");
					httpServletResponse.setHeader("Content-Length",
							"" + backupFile.length());

					OutputStream out = httpServletResponse.getOutputStream();
					OmFileHelper.copyFile(backupFile, out);

					out.flush();
					out.close();
				} catch (Exception er) {
					log.error("Error exporting: ", er);
				}

				if (backupFile.exists()) {
					// log.debug("DELETE :1: "+backupFile.getCanonicalPath());
					backupFile.delete();
				}

				FileHelper.removeRec(backup_dir);
			}
		} else {
			log.debug("ERROR LangExport: not authorized FileDownload "
					+ (new Date()));
		}
	}

	public void getAllFiles(File dir, List<File> fileList) throws IOException {
		try {
			File[] files = dir.listFiles();
			for (File file : files) {
				fileList.add(file);
				if (file.isDirectory()) {
					// log.debug("directory:" + file.getCanonicalPath());
					getAllFiles(file, fileList);
				} else {
					// log.debug("     file:" + file.getCanonicalPath());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void writeZipFile(File directoryToZip, List<File> fileList,
			FileOutputStream fos) {

		try {
			ZipOutputStream zos = new ZipOutputStream(fos);

			for (File file : fileList) {
				if (!file.isDirectory()) { // we only zip files, not directories
					addToZip(directoryToZip, file, zos);
				}
			}

			zos.close();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void addToZip(File directoryToZip, File file, ZipOutputStream zos)
			throws FileNotFoundException, IOException {

		// we want the zipEntry's path to be a relative path that is relative
		// to the directory being zipped, so chop off the rest of the path
		String zipFilePath = file.getCanonicalPath().substring(
				directoryToZip.getCanonicalPath().length() + 1,
				file.getCanonicalPath().length());
		log.debug("Writing '" + zipFilePath + "' to zip file");
		ZipEntry zipEntry = new ZipEntry(zipFilePath);
		zos.putNextEntry(zipEntry);

		OmFileHelper.copyFile(file, zos);
		zos.closeEntry();
	}
}
