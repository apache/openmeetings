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

import static org.apache.commons.transaction.util.FileHelper.copyRec;
import static org.apache.openmeetings.db.entity.user.PrivateMessage.INBOX_FOLDER_ID;
import static org.apache.openmeetings.db.entity.user.PrivateMessage.SENT_FOLDER_ID;
import static org.apache.openmeetings.db.entity.user.PrivateMessage.TRASH_FOLDER_ID;
import static org.apache.openmeetings.db.util.UserHelper.getMinLoginLength;
import static org.apache.openmeetings.util.OmFileHelper.getStreamsHibernateDir;
import static org.apache.openmeetings.util.OmFileHelper.getUploadDir;
import static org.apache.openmeetings.util.OmFileHelper.getUploadProfilesUserDir;
import static org.apache.openmeetings.util.OmFileHelper.getUploadRoomDir;
import static org.apache.openmeetings.util.OmFileHelper.profilesPrefix;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_CRYPT_KEY;
import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

import java.io.File;
import java.io.FileInputStream;
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
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.transaction.util.FileHelper;
import org.apache.openmeetings.db.dao.basic.ChatDao;
import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dao.calendar.AppointmentCategoryDao;
import org.apache.openmeetings.db.dao.calendar.AppointmentDao;
import org.apache.openmeetings.db.dao.calendar.AppointmentReminderTypDao;
import org.apache.openmeetings.db.dao.calendar.MeetingMemberDao;
import org.apache.openmeetings.db.dao.file.FileExplorerItemDao;
import org.apache.openmeetings.db.dao.record.FlvRecordingDao;
import org.apache.openmeetings.db.dao.room.PollDao;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.dao.room.RoomOrganisationDao;
import org.apache.openmeetings.db.dao.room.RoomTypeDao;
import org.apache.openmeetings.db.dao.server.LdapConfigDao;
import org.apache.openmeetings.db.dao.server.OAuth2Dao;
import org.apache.openmeetings.db.dao.server.ServerDao;
import org.apache.openmeetings.db.dao.user.OrganisationDao;
import org.apache.openmeetings.db.dao.user.PrivateMessageFolderDao;
import org.apache.openmeetings.db.dao.user.PrivateMessagesDao;
import org.apache.openmeetings.db.dao.user.StateDao;
import org.apache.openmeetings.db.dao.user.UserContactsDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.basic.ChatMessage;
import org.apache.openmeetings.db.entity.basic.Configuration;
import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.db.entity.calendar.AppointmentCategory;
import org.apache.openmeetings.db.entity.calendar.AppointmentReminderTyps;
import org.apache.openmeetings.db.entity.calendar.MeetingMember;
import org.apache.openmeetings.db.entity.file.FileItem;
import org.apache.openmeetings.db.entity.file.FileExplorerItem;
import org.apache.openmeetings.db.entity.file.FileItem.Type;
import org.apache.openmeetings.db.entity.record.FlvRecording;
import org.apache.openmeetings.db.entity.record.FlvRecordingMetaData;
import org.apache.openmeetings.db.entity.room.PollType;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.room.RoomModerator;
import org.apache.openmeetings.db.entity.room.RoomOrganisation;
import org.apache.openmeetings.db.entity.room.RoomPoll;
import org.apache.openmeetings.db.entity.room.RoomPollAnswer;
import org.apache.openmeetings.db.entity.room.RoomType;
import org.apache.openmeetings.db.entity.server.LdapConfig;
import org.apache.openmeetings.db.entity.server.OAuthServer;
import org.apache.openmeetings.db.entity.server.Server;
import org.apache.openmeetings.db.entity.user.Address;
import org.apache.openmeetings.db.entity.user.Organisation;
import org.apache.openmeetings.db.entity.user.PrivateMessage;
import org.apache.openmeetings.db.entity.user.PrivateMessageFolder;
import org.apache.openmeetings.db.entity.user.State;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.entity.user.User.Right;
import org.apache.openmeetings.db.entity.user.UserContact;
import org.apache.openmeetings.db.util.TimezoneUtil;
import org.apache.openmeetings.util.CalendarPatterns;
import org.apache.openmeetings.util.OmFileHelper;
import org.apache.openmeetings.util.crypt.MD5Implementation;
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
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class BackupImport {
	private static final Logger log = Red5LoggerFactory.getLogger(BackupImport.class, webAppRootKey);

	@Autowired
	private AppointmentDao appointmentDao;
	@Autowired
	private StateDao statemanagement;
	@Autowired
	private OrganisationDao orgDao;
	@Autowired
	private RoomDao roomDao;
	@Autowired
	private AppointmentCategoryDao appointmentCategoryDaoImpl;
	@Autowired
	private AppointmentReminderTypDao appointmentReminderTypDaoImpl;
	@Autowired
	private UserDao usersDao;
	@Autowired
	private FlvRecordingDao flvRecordingDao;
	@Autowired
	private PrivateMessageFolderDao privateMessageFolderDao;
	@Autowired
	private PrivateMessagesDao privateMessagesDao;
	@Autowired
	private MeetingMemberDao meetingMemberDao;
	@Autowired
	private LdapConfigDao ldapConfigDao;
	@Autowired
	private FileExplorerItemDao fileExplorerItemDao;
	@Autowired
	private UserContactsDao userContactsDao;
	@Autowired
	private PollDao pollManager;
	@Autowired
	private ConfigurationDao configurationDao;
	@Autowired
	private TimezoneUtil tzUtil;
	@Autowired
	private ChatDao chatDao;
	@Autowired
	private ServerDao serverDao;
	@Autowired
	private OAuth2Dao auth2Dao;
	@Autowired
	private OrganisationDao organisationDao;
	@Autowired
	private RoomTypeDao roomTypeDao;
	@Autowired
	private RoomOrganisationDao roomOrganisationDao;

	private final Map<Long, Long> usersMap = new HashMap<Long, Long>();
	private final Map<Long, Long> organisationsMap = new HashMap<Long, Long>();
	private final Map<Long, Long> appointmentsMap = new HashMap<Long, Long>();
	private final Map<Long, Long> roomsMap = new HashMap<Long, Long>();
	private final Map<Long, Long> messageFoldersMap = new HashMap<Long, Long>();
	private final Map<Long, Long> userContactsMap = new HashMap<Long, Long>();
	private final Map<String, Integer> userEmailMap = new HashMap<String, Integer>();

	private enum Maps {
		USERS, ORGANISATIONS, APPOINTMENTS, ROOMS, MESSAGEFOLDERS, USERCONTACTS
	};

	public void performImport(InputStream is) throws Exception {
		File working_dir = OmFileHelper.getUploadImportDir();
		if (!working_dir.exists()) {
			working_dir.mkdir();
		}
		usersMap.clear();
		organisationsMap.clear();
		appointmentsMap.clear();
		roomsMap.clear();
		messageFoldersMap.clear();
		userContactsMap.clear();
		userEmailMap.clear();
		messageFoldersMap.put(INBOX_FOLDER_ID, INBOX_FOLDER_ID);
		messageFoldersMap.put(SENT_FOLDER_ID, SENT_FOLDER_ID);
		messageFoldersMap.put(TRASH_FOLDER_ID, TRASH_FOLDER_ID);
		File f = OmFileHelper.getNewDir(working_dir, "import_" + CalendarPatterns.getTimeForStreamId(new Date()));

		log.debug("##### WRITE FILE TO: " + f);
		
		ZipInputStream zipinputstream = new ZipInputStream(is);
		ZipEntry zipentry = zipinputstream.getNextEntry();
		while (zipentry != null) {
			String fName = zipentry.getName();
			if (File.pathSeparatorChar != '\\' && fName.indexOf('\\') > -1) {
				fName = fName.replace('\\', '/');
			}
			// for each entry to be extracted
			File fentry = new File(f, fName);
			File dir = fentry.isDirectory() ? fentry : fentry.getParentFile();
			dir.mkdirs();
			if (fentry.isDirectory()) {
				zipentry = zipinputstream.getNextEntry();
				continue;
			}

			FileHelper.copy(zipinputstream, fentry);
			zipinputstream.closeEntry();
			zipentry = zipinputstream.getNextEntry();

		}
		zipinputstream.close();

		/*
		 * ##################### Import Configs
		 */
		{
			Registry registry = new Registry();
			Strategy strategy = new RegistryStrategy(registry);
			RegistryMatcher matcher = new RegistryMatcher(); //TODO need to be removed in the later versions
			Serializer serializer = new Persister(strategy, matcher);

			matcher.bind(Long.class, LongTransform.class);
			registry.bind(Date.class, DateConverter.class);
			registry.bind(User.class, new UserConverter(usersDao, usersMap));
			
			List<Configuration> list = readList(serializer, f, "configs.xml", "configs", Configuration.class, true);
			for (Configuration c : list) {
				if (c.getConf_key() == null || c.isDeleted()) {
					continue;
				}
				Configuration cfg = configurationDao.forceGet(c.getConf_key());
				if (cfg != null && !cfg.isDeleted()) {
					log.warn("Non deleted configuration with same key is found! old value: {}, new value: {}", cfg.getConf_value(), c.getConf_value());
				}
				c.setId(cfg == null ? null : cfg.getId());
				if (c.getUser() != null && c.getUser().getId() == null) {
					c.setUser(null);
				}
				if (CONFIG_CRYPT_KEY.equals(c.getConf_key())) {
					try {
						Class.forName(c.getConf_value());
					} catch (ClassNotFoundException e) {
						c.setConf_value(MD5Implementation.class.getCanonicalName());
					}
				}
				configurationDao.update(c, null);
			}
		}

		log.info("Configs import complete, starting organization import");
		/*
		 * ##################### Import Organizations
		 */
		Serializer simpleSerializer = new Persister();
		{
			List<Organisation> list = readList(simpleSerializer, f, "organizations.xml", "organisations", Organisation.class);
			for (Organisation o : list) {
				long oldId = o.getId();
				o.setId(null);
				o = organisationDao.update(o, null);
				organisationsMap.put(oldId, o.getId());
			}
		}

		log.info("Organizations import complete, starting user import");
		/*
		 * ##################### Import Users
		 */
		{
			String jNameTimeZone = configurationDao.getConfValue("default.timezone", String.class, "Europe/Berlin");
			List<User> list = readUserList(f, "users.xml", "users");
			int minLoginLength = getMinLoginLength(configurationDao);
			for (User u : list) {
				if (u.getLogin() == null) {
					continue;
				}
				if (u.getType() == User.Type.contact && u.getLogin().length() < minLoginLength) {
					u.setLogin(UUID.randomUUID().toString());
				}
				//FIXME: OPENMEETINGS-750
				//Convert old Backups with OmTimeZone to new schema
				
				String tz = u.getTimeZoneId();
				if (tz == null) {
					u.setTimeZoneId(jNameTimeZone);
					u.setForceTimeZoneCheck(true);
				} else {
					u.setForceTimeZoneCheck(false);
				}
				
				u.setStarttime(new Date());
				long userId = u.getId();
				u.setId(null);
				if (u.getSipUser() != null && u.getSipUser().getId() != 0) {
					u.getSipUser().setId(0);
				}
				usersDao.update(u, -1L);
				usersMap.put(userId, u.getId());
			}
		}

		log.info("Users import complete, starting room import");
		/*
		 * ##################### Import Rooms
		 */
		{
			Registry registry = new Registry();
			Strategy strategy = new RegistryStrategy(registry);
			RegistryMatcher matcher = new RegistryMatcher(); //TODO need to be removed in the later versions
			Serializer serializer = new Persister(strategy, matcher);

			matcher.bind(Long.class, LongTransform.class);
			matcher.bind(Integer.class, IntegerTransform.class);
			registry.bind(User.class, new UserConverter(usersDao, usersMap));
			registry.bind(RoomType.class, new RoomTypeConverter(roomTypeDao));
			
			List<Room> list = readList(serializer, f, "rooms.xml", "rooms", Room.class);
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
				roomsMap.put(roomId, r.getId());
			}
		}

		log.info("Room import complete, starting room organizations import");
		/*
		 * ##################### Import Room Organisations
		 */
		{
			Registry registry = new Registry();
			Strategy strategy = new RegistryStrategy(registry);
			Serializer serializer = new Persister(strategy);
	
			registry.bind(Organisation.class, new OrganisationConverter(orgDao, organisationsMap));
			registry.bind(Room.class, new RoomConverter(roomDao, roomsMap));
			
			List<RoomOrganisation> list = readList(serializer, f, "rooms_organisation.xml", "room_organisations", RoomOrganisation.class);
			for (RoomOrganisation ro : list) {
				if (!ro.getDeleted()) {
					// We need to reset this as openJPA reject to store them otherwise
					ro.setId(null);
					roomOrganisationDao.update(ro, null);
				}
			}
		}

		log.info("Room organizations import complete, starting chat messages import");
		/*
		 * ##################### Import Chat messages
		 */
		{
			Registry registry = new Registry();
			Strategy strategy = new RegistryStrategy(registry);
			Serializer serializer = new Persister(strategy);
	
			registry.bind(User.class, new UserConverter(usersDao, usersMap));
			registry.bind(Room.class, new RoomConverter(roomDao, roomsMap));
			registry.bind(Date.class, DateConverter.class);
			
			List<ChatMessage> list = readList(serializer, f, "chat_messages.xml", "chat_messages", ChatMessage.class, true);
			for (ChatMessage m : list) {
				chatDao.update(m);
			}
		}
		
		log.info("Chat messages import complete, starting appointement import");
		/*
		 * ##################### Import Appointements
		 */
		{
			Registry registry = new Registry();
			Strategy strategy = new RegistryStrategy(registry);
			Serializer serializer = new Persister(strategy);
	
			registry.bind(AppointmentCategory.class, new AppointmentCategoryConverter(appointmentCategoryDaoImpl));
			registry.bind(User.class, new UserConverter(usersDao, usersMap));
			registry.bind(AppointmentReminderTyps.class, new AppointmentReminderTypeConverter(appointmentReminderTypDaoImpl));
			registry.bind(Room.class, new RoomConverter(roomDao, roomsMap));
			registry.bind(Date.class, DateConverter.class);
			
			List<Appointment> list = readList(serializer, f, "appointements.xml", "appointments", Appointment.class);
			for (Appointment a : list) {
				Long appId = a.getId();

				// We need to reset this as openJPA reject to store them otherwise
				a.setId(null);
				if (a.getOwner() != null && a.getOwner().getId() == null) {
					a.setOwner(null);
				}
				if (a.getRoom() != null && a.getRoom().getId() == null) {
					a.setRoom(null);
				}
				Long newAppId = appointmentDao.addAppointmentObj(a);
				appointmentsMap.put(appId, newAppId);
			}
		}

		log.info("Appointement import complete, starting meeting members import");
		/*
		 * ##################### Import MeetingMembers
		 * 
		 * Reminder Invitations will be NOT send!
		 */
		{
			List<MeetingMember> list = readMeetingMemberList(f, "meetingmembers.xml", "meetingmembers");
			for (MeetingMember ma : list) {
				meetingMemberDao.update(ma);
			}
		}

		log.info("Meeting members import complete, starting ldap config import");
		/*
		 * ##################### Import LDAP Configs
		 */
		{
			List<LdapConfig> list = readList(simpleSerializer, f, "ldapconfigs.xml", "ldapconfigs", LdapConfig.class, true);
			for (LdapConfig c : list) {
				if (!"local DB [internal]".equals(c.getName())) {
					ldapConfigDao.addLdapConfigByObject(c);
				}
			}
		}

		log.info("Ldap config import complete, starting cluster servers import");
		/*
		 * ##################### Cluster servers
		 */
		{
			List<Server> list = readList(simpleSerializer, f, "servers.xml", "servers", Server.class, true);
			for (Server s : list) {
				serverDao.update(s, null);
			}
		}

		log.info("Cluster servers import complete, starting OAuth2 servers import");
		/*
		 * ##################### OAuth2 servers
		 */
		{
			List<OAuthServer> list = readList(simpleSerializer, f, "oauth2servers.xml", "oauth2servers", OAuthServer.class, true);
			for (OAuthServer s : list) {
				auth2Dao.update(s, null);
			}
		}

		log.info("OAuth2 servers import complete, starting recordings import");
		/*
		 * ##################### Import Recordings
		 */
		{
			List<FlvRecording> list = readRecordingList(f, "flvRecordings.xml", "flvrecordings");
			for (FlvRecording fr : list) {
				fr.setId(null);
				if (fr.getRoomId() != null) {
					fr.setRoomId(roomsMap.get(fr.getRoomId()));
				}
				if (fr.getOwnerId() != null) {
					fr.setOwnerId(usersMap.get(fr.getOwnerId()));
				}
				if (fr.getFlvRecordingMetaData() != null) {
					for (FlvRecordingMetaData meta : fr.getFlvRecordingMetaData()) {
						meta.setId(null);
						meta.setFlvRecording(fr);
					}
				}
				flvRecordingDao.update(fr);
			}
		}

		log.info("FLVrecording import complete, starting private message folder import");
		/*
		 * ##################### Import Private Message Folders
		 */
		{
			List<PrivateMessageFolder> list = readList(simpleSerializer, f, "privateMessageFolder.xml"
				, "privatemessagefolders", PrivateMessageFolder.class, true);
			for (PrivateMessageFolder p : list) {
				Long folderId = p.getId();
				PrivateMessageFolder storedFolder = privateMessageFolderDao.get(folderId);
				if (storedFolder == null) {
					p.setId(null);
					Long newFolderId = privateMessageFolderDao.addPrivateMessageFolderObj(p);
					messageFoldersMap.put(folderId, newFolderId);
				}
			}
		}

		log.info("Private message folder import complete, starting user contacts import");
		/*
		 * ##################### Import User Contacts
		 */
		{
			Registry registry = new Registry();
			Strategy strategy = new RegistryStrategy(registry);
			Serializer serializer = new Persister(strategy);
	
			registry.bind(User.class, new UserConverter(usersDao, usersMap));
			
			List<UserContact> list = readList(serializer, f, "userContacts.xml", "usercontacts", UserContact.class, true);
			for (UserContact uc : list) {
				Long ucId = uc.getUserContactId();
				UserContact storedUC = userContactsDao.get(ucId);

				if (storedUC == null && uc.getContact() != null && uc.getContact().getId() != null) {
					uc.setUserContactId(0);
					if (uc.getOwner() != null && uc.getOwner().getId() == null) {
						uc.setOwner(null);
					}
					Long newId = userContactsDao.addUserContactObj(uc);
					userContactsMap.put(ucId, newId);
				}
			}
		}

		log.info("Usercontact import complete, starting private messages item import");
		/*
		 * ##################### Import Private Messages
		 */
		{
			Registry registry = new Registry();
			Strategy strategy = new RegistryStrategy(registry);
			Serializer serializer = new Persister(strategy);
	
			registry.bind(User.class, new UserConverter(usersDao, usersMap));
			registry.bind(Room.class, new RoomConverter(roomDao, roomsMap));
			registry.bind(Date.class, DateConverter.class);
			
			List<PrivateMessage> list = readList(serializer, f, "privateMessages.xml", "privatemessages", PrivateMessage.class, true);
			boolean oldBackup = true;
			for (PrivateMessage p : list) {
				if (p.getFolderId() < 0) {
					oldBackup = false;
					break;
				}
				
			}
			for (PrivateMessage p : list) {
				p.setId(null);
				p.setFolderId(getNewId(p.getFolderId(), Maps.MESSAGEFOLDERS));
				p.setUserContactId(getNewId(p.getUserContactId(), Maps.USERCONTACTS));
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
				privateMessagesDao.update(p, null);
			}
		}

		log.info("Private message import complete, starting file explorer item import");
		/*
		 * ##################### Import File-Explorer Items
		 */
		{
			List<FileExplorerItem> list = readFileExplorerItemList(f, "fileExplorerItems.xml", "fileExplorerItems");
			for (FileExplorerItem file : list) {
				// We need to reset this as openJPA reject to store them otherwise
				file.setId(null);
				Long roomId = file.getRoomId();
				file.setRoomId(roomsMap.containsKey(roomId) ? roomsMap.get(roomId) : null);
				if (file.getOwnerId() != null) {
					file.setOwnerId(usersMap.get(file.getOwnerId()));
				}
				if (file.getParentItemId() != null && file.getParentItemId() <= 0L) {
					file.setParentItemId(null);
				}
				fileExplorerItemDao.addFileExplorerItem(file);
			}
		}

		log.info("File explorer item import complete, starting room poll import");
		/*
		 * ##################### Import Room Polls
		 */
		{
			Registry registry = new Registry();
			Strategy strategy = new RegistryStrategy(registry);
			RegistryMatcher matcher = new RegistryMatcher(); //TODO need to be removed in the later versions
			Serializer serializer = new Persister(strategy, matcher);
	
			matcher.bind(Integer.class, IntegerTransform.class);
			registry.bind(User.class, new UserConverter(usersDao, usersMap));
			registry.bind(Room.class, new RoomConverter(roomDao, roomsMap));
			registry.bind(PollType.class, new PollTypeConverter(pollManager));
			registry.bind(Date.class, DateConverter.class);
			
			List<RoomPoll> list = readList(serializer, f, "roompolls.xml", "roompolls", RoomPoll.class, true);
			for (RoomPoll rp : list) {
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
				pollManager.update(rp);
			}
		}
		
		log.info("Poll import complete, starting copy of files and folders");
		/*
		 * ##################### Import real files and folders
		 */
		importFolders(f);

		log.info("File explorer item import complete, clearing temp files");
		
		FileHelper.removeRec(f);
	}
	
	private <T> List<T> readList(Serializer ser, File baseDir, String fileName, String listNodeName, Class<T> clazz) throws Exception {
		return readList(ser, baseDir, fileName, listNodeName, clazz, false);
	}
	
	private <T> List<T> readList(Serializer ser, File baseDir, String fileName, String listNodeName, Class<T> clazz, boolean notThow) throws Exception {
		List<T> list = new ArrayList<T>();
		File xml = new File(baseDir, fileName);
		if (!xml.exists()) {
			final String msg = fileName + " missing";
			if (notThow) {
				log.debug(msg);
			} else {
				throw new Exception(msg);
			}
		} else {
			InputNode root = NodeBuilder.read(new FileInputStream(xml));
			InputNode listNode = root.getNext();
			if (listNodeName.equals(listNode.getName())) {
				InputNode item = listNode.getNext();
				while (item != null) {
					T o = ser.read(clazz, item, false);
					list.add(o);
					item = listNode.getNext();
				}
			}
		}
		return list;
	}
	
	private Node getNode(Node doc, String name) {
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
	
	public List<FileExplorerItem> readFileExplorerItemList(File baseDir, String fileName, String listNodeName) throws Exception {
		List<FileExplorerItem> list = new ArrayList<FileExplorerItem>();
		File xml = new File(baseDir, fileName);
		if (xml.exists()) {
			Registry registry = new Registry();
			Strategy strategy = new RegistryStrategy(registry);
			RegistryMatcher matcher = new RegistryMatcher(); //TODO need to be removed in the later versions
			Serializer ser = new Persister(strategy, matcher);

			matcher.bind(Long.class, LongTransform.class);
			matcher.bind(Integer.class, IntegerTransform.class);
			registry.bind(Date.class, DateConverter.class);
			
			InputNode root = NodeBuilder.read(new FileInputStream(xml));
			InputNode root1 = NodeBuilder.read(new FileInputStream(xml)); //HACK to handle old isFolder, isImage, isVideo, isRecording, isPresentation, isStoredWmlFile, isChart
			InputNode listNode = root.getNext();
			InputNode listNode1 = root1.getNext(); //HACK to handle old isFolder, isImage, isVideo, isRecording, isPresentation, isStoredWmlFile, isChart
			if (listNodeName.equals(listNode.getName())) {
				InputNode item = listNode.getNext();
				InputNode item1 = listNode1.getNext(); //HACK to handle old isFolder, isImage, isVideo, isRecording, isPresentation, isStoredWmlFile, isChart
				while (item != null) {
					FileExplorerItem f = ser.read(FileExplorerItem.class, item, false);
					
					boolean isFolder = false, isImage = false, isVideo = false, isPresentation = false, isStoredWmlFile = false, isChart = false;
					//HACK to handle old isFolder, isImage, isVideo, isRecording, isPresentation, isStoredWmlFile, isChart
					do {
						if ("isChart".equals(item1.getName()) && "true".equals(item1.getValue())) {
							isChart = true;
						}
						if ("isImage".equals(item1.getName()) && "true".equals(item1.getValue())) {
							isImage = true;
						}
						if ("isVideo".equals(item1.getName()) && "true".equals(item1.getValue())) {
							isVideo = true;
						}
						if ("isRecording".equals(item1.getName()) && "true".equals(item1.getValue())) {
							log.warn("Recording is stored in FileExplorer Items");
							isVideo = true;
						}
						if ("isPresentation".equals(item1.getName()) && "true".equals(item1.getValue())) {
							isPresentation = true;
						}
						if ("isStoredWmlFile".equals(item1.getName()) && "true".equals(item1.getValue())) {
							isStoredWmlFile = true;
						}
						if ("isFolder".equals(item1.getName()) && "true".equals(item1.getValue())) {
							isFolder = true;
						}
						item1 = listNode1.getNext(); //HACK to handle Address inside user
					} while (item1 != null && !"fileExplorerItem".equals(item1.getName()));
					
					if (f.getType() == null) {
						if (isChart) {
							f.setType(Type.PollChart);
						}
						if (isImage) {
							f.setType(Type.Image);
						}
						if (isVideo) {
							f.setType(Type.Video);
						}
						if (isPresentation) {
							f.setType(Type.Presentation);
						}
						if (isStoredWmlFile) {
							f.setType(Type.WmlFile);
						}
						if (isFolder) {
							f.setType(Type.Folder);
						}
					}
					list.add(f);
					item = listNode.getNext();
				}
			}
		}
		return list;
	}
	
	public List<FlvRecording> readRecordingList(File baseDir, String fileName, String listNodeName) throws Exception {
		List<FlvRecording> list = new ArrayList<FlvRecording>();
		File xml = new File(baseDir, fileName);
		if (xml.exists()) {
			Registry registry = new Registry();
			Strategy strategy = new RegistryStrategy(registry);
			RegistryMatcher matcher = new RegistryMatcher(); //TODO need to be removed in the later versions
			Serializer ser = new Persister(strategy, matcher);
	
			matcher.bind(Long.class, LongTransform.class);
			matcher.bind(Integer.class, IntegerTransform.class);
			registry.bind(Date.class, DateConverter.class);
			
			InputNode root = NodeBuilder.read(new FileInputStream(xml));
			InputNode root1 = NodeBuilder.read(new FileInputStream(xml)); //HACK to handle old isFolder
			InputNode listNode = root.getNext();
			InputNode listNode1 = root1.getNext(); //HACK to handle old isFolder
			if (listNodeName.equals(listNode.getName())) {
				InputNode item = listNode.getNext();
				InputNode item1 = listNode1.getNext(); //HACK to handle old isFolder
				while (item != null) {
					FlvRecording r = ser.read(FlvRecording.class, item, false);
					
					boolean isFolder = false;
					//HACK to handle old isFolder
					do {
						if ("isFolder".equals(item1.getName()) && "true".equals(item1.getValue())) {
							isFolder = true;
						}
						item1 = listNode1.getNext(); //HACK to handle Address inside user
					} while (item1 != null && !"flvrecording".equals(item1.getName()));
					
					if (r.getType() == null) {
						r.setType(isFolder ? FileItem.Type.Folder : FileItem.Type.Recording);
					}
					list.add(r);
					item = listNode.getNext();
				}
			}
		}
		return list;
	}
	
	public List<User> readUserList(InputStream xml, String listNodeName) throws Exception {
		return readUserList(new InputSource(xml), listNodeName);
	}
	
	public List<User> readUserList(File baseDir, String fileName, String listNodeName) throws Exception {
		File xml = new File(baseDir, fileName);
		if (!xml.exists()) {
			throw new Exception(fileName + " missing");
		}
		
		return readUserList(new InputSource(xml.toURI().toASCIIString()), listNodeName);
	}
	
	//FIXME (need to be removed in later versions) HACK to add external attendees previously stored in MeetingMember structure
	private List<MeetingMember> readMeetingMemberList(File baseDir, String filename, String listNodeName) throws Exception {
		Registry registry = new Registry();
		Strategy strategy = new RegistryStrategy(registry);
		Serializer ser = new Persister(strategy);

		registry.bind(User.class, new UserConverter(usersDao, usersMap));
		registry.bind(Appointment.class, new AppointmentConverter(appointmentDao, appointmentsMap));
		
		File xml = new File(baseDir, filename);
		if (!xml.exists()) {
			throw new Exception(filename + " missing");
		}
		
		DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = dBuilder.parse(new InputSource(xml.toURI().toASCIIString()));
		
		StringWriter sw = new StringWriter();
		Transformer xformer = TransformerFactory.newInstance().newTransformer();
        xformer.transform(new DOMSource(doc), new StreamResult(sw));
        
		List<MeetingMember> list = new ArrayList<MeetingMember>();
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
						if (User.Type.contact == mm.getUser().getType() && "firstname".equals(item1.getName())) {
							mm.getUser().setFirstname(item1.getValue());
						}
						if (User.Type.contact == mm.getUser().getType() && "lastname".equals(item1.getName())) {
							mm.getUser().setLastname(item1.getValue());
						}
						if ("email".equals(item1.getName())) {
							String email = item1.getValue();
							if (mm.getAppointment() != null && mm.getAppointment().getOwner() != null) {
								mm.setUser(usersDao.getContact(email, mm.getAppointment().getOwner()));
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
	
	//FIXME (need to be removed in later versions) HACK to fix 2 deleted nodes in users.xml and inline Adresses and sipData
	private List<User> readUserList(InputSource xml, String listNodeName) throws Exception {
		Registry registry = new Registry();
		Strategy strategy = new RegistryStrategy(registry);
		Serializer ser = new Persister(strategy);

		registry.bind(Organisation.class, new OrganisationConverter(orgDao, organisationsMap));
		registry.bind(State.class, new StateConverter(statemanagement));
		registry.bind(Date.class, DateConverter.class);

		DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = dBuilder.parse(xml);
		userEmailMap.clear();
		//add existence email from database
		List<User>  users = usersDao.getAllUsers();
		for (User u : users){
			if (u.getAdresses() == null || u.getAdresses().getEmail() == null || User.Type.user != u.getType()) {
				continue;
			}
			userEmailMap.put(u.getAdresses().getEmail(), -1);
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
        
		List<User> list = new ArrayList<User>();
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
				if (u.getAdresses() == null) {
					Address a = ser.read(Address.class, item1, false);
					u.setAdresses(a);
					needToSkip1 = false;
				}
				if (needToSkip1) {
					do {
						item1 = listNode1.getNext(); //HACK to handle Address inside user
					} while (item1 != null && !"user".equals(item1.getName()));
				}
				String level_id = null, status = null;
				do {
					if (u.getTimeZoneId() == null && "omTimeZone".equals(item2.getName())) {
						String jName = item2.getValue();
						u.setTimeZoneId(jName == null ? null : tzUtil.getTimezoneByInternalJName(jName).getID());
					}
					if ("level_id".equals(item2.getName())) {
						level_id = item2.getValue();
					}
					if ("status".equals(item2.getName())) {
						status = item2.getValue();
					}
					item2 = listNode2.getNext(); //HACK to handle old om_time_zone, level_id, status
				} while (item2 != null && !"user".equals(item2.getName()));
				if (u.getRights().isEmpty()) {
					u.getRights().add(Right.Room);
					if ("1".equals(status)) {
						u.getRights().add(Right.Dashboard);
						u.getRights().add(Right.Login);
					}
					if ("3".equals(level_id)) {
						u.getRights().add(Right.Admin);
						u.getRights().add(Right.Soap);
					}
					if ("4".equals(level_id)) {
						u.getRights().add(Right.Soap);
					}
				}
				// check that email is unique
				if (u.getAdresses() != null && u.getAdresses().getEmail() != null && User.Type.user == u.getType()) {
					if (userEmailMap.containsKey(u.getAdresses().getEmail())) {
						log.warn("Email is duplicated for user " + u.toString());
						String updateEmail = "modified_by_import_<" + list.size() + ">" + u.getAdresses().getEmail();
						u.getAdresses().setEmail(updateEmail);
					}
					userEmailMap.put(u.getAdresses().getEmail(), userEmailMap.size());
				}
				list.add(u);
				item = listNode.getNext();
			}
		}
		return list;
	}
	
	private Long getProfileId(File f) {
		String n = f.getName();
		if (n.indexOf(profilesPrefix) > -1) {
			return importLongType(n.substring(profilesPrefix.length()));
		}
		return null;
	}
	
	private void importFolders(File importBaseDir) throws IOException {
		// Now check the room files and import them
		File roomFilesFolder = new File(importBaseDir, "roomFiles");

		File uploadDir = getUploadDir();

		log.debug("roomFilesFolder PATH " + roomFilesFolder.getCanonicalPath());

		if (roomFilesFolder.exists()) {
			for (File file : roomFilesFolder.listFiles()) {
				if (file.isDirectory()) {
					String fName = file.getName();
					if ("profiles".equals(fName)) {
						// profile should correspond to the new user id
						for (File profile : file.listFiles()) {
							Long oldId = getProfileId(profile);
							Long id = oldId != null ? getNewId(oldId, Maps.USERS) : null;
							if (id != null) {
								copyRec(profile, getUploadProfilesUserDir(id));
							}
						}
						continue;
					} else {
						// check if folder is room folder, store it under new id if necessary
						Long oldId = importLongType(fName);
						Long id = oldId != null ? getNewId(oldId, Maps.ROOMS) : null;
						if (id != null) {
							copyRec(file, getUploadRoomDir(id.toString()));
							continue;
						}
					}
					copyRec(file, new File(uploadDir, fName));
				}
			}
		}

		// Now check the recordings and import them

		File sourceDirRec = new File(importBaseDir, "recordingFiles");

		log.debug("sourceDirRec PATH " + sourceDirRec.getCanonicalPath());
		if (sourceDirRec.exists()) {
			copyRec(sourceDirRec, getStreamsHibernateDir());
		}
	}

	private Long importLongType(String value) {
		Long val = null;
		try {
			val = Long.valueOf(value);
		} catch (Exception e) {
			// no-op
		}
		return val;
	}

	private Long getNewId(Long oldId, Maps map) {
		Long newId = oldId;
		switch (map) {
			case USERS:
				if (usersMap.containsKey(oldId)) {
					newId = usersMap.get(oldId);
				}
				break;
			case ORGANISATIONS:
				if (organisationsMap.containsKey(oldId)) {
					newId = organisationsMap.get(oldId);
				}
				break;
			case APPOINTMENTS:
				if (appointmentsMap.containsKey(oldId)) {
					newId = appointmentsMap.get(oldId);
				}
				break;
			case ROOMS:
				if (roomsMap.containsKey(oldId)) {
					newId = roomsMap.get(oldId);
				}
				break;
			case MESSAGEFOLDERS:
				if (messageFoldersMap.containsKey(oldId)) {
					newId = messageFoldersMap.get(oldId);
				}
				break;
			case USERCONTACTS:
				if (userContactsMap.containsKey(oldId)) {
					newId = userContactsMap.get(oldId);
				}
				break;
			default:
				break;
		}
		return newId;
	}
}
