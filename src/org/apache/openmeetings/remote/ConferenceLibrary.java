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
package org.apache.openmeetings.remote;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.transaction.util.FileHelper;
import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.data.basic.AuthLevelUtil;
import org.apache.openmeetings.data.basic.SessiondataDao;
import org.apache.openmeetings.data.file.dao.FileExplorerItemDao;
import org.apache.openmeetings.data.file.dto.LibraryPresentation;
import org.apache.openmeetings.data.user.UserManager;
import org.apache.openmeetings.data.whiteboard.WhiteboardManager;
import org.apache.openmeetings.documents.LibraryChartLoader;
import org.apache.openmeetings.documents.LibraryDocumentConverter;
import org.apache.openmeetings.documents.LibraryWmlLoader;
import org.apache.openmeetings.documents.LoadLibraryPresentation;
import org.apache.openmeetings.persistence.beans.files.FileExplorerItem;
import org.apache.openmeetings.persistence.beans.room.Client;
import org.apache.openmeetings.session.ISessionManager;
import org.apache.openmeetings.utils.OmFileHelper;
import org.apache.openmeetings.utils.crypt.MD5;
import org.red5.logging.Red5LoggerFactory;
import org.red5.server.api.IConnection;
import org.red5.server.api.Red5;
import org.red5.server.api.service.IPendingServiceCall;
import org.red5.server.api.service.IPendingServiceCallback;
import org.red5.server.api.service.IServiceCapableConnection;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * @author swagner
 * 
 */
public class ConferenceLibrary implements IPendingServiceCallback {

	private static final Logger log = Red5LoggerFactory.getLogger(
			ConferenceLibrary.class, OpenmeetingsVariables.webAppRootKey);

	@Autowired
	private ISessionManager sessionManager;
	@Autowired
	private SessiondataDao sessiondataDao;
	@Autowired
	private UserManager userManager;
	@Autowired
	private FileExplorerItemDao fileExplorerItemDao;
	@Autowired
	private AuthLevelUtil authLevelUtil;
	@Autowired
	private LibraryWmlLoader libraryWmlLoader;
	@Autowired
	private WhiteboardManager whiteboardManagement;

	public LibraryPresentation getPresentationPreviewFileExplorer(String SID,
			String parentFolder) {

		try {

			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);

			log.debug("#############users_id : " + users_id);
			log.debug("#############user_level : " + user_level);

			if (authLevelUtil.checkUserLevel(user_level)) {

				File working_dir = new File(OmFileHelper.getUploadFilesDir(), parentFolder);
				log.debug("############# working_dir : " + working_dir);

				File file = new File(working_dir, OmFileHelper.libraryFileName);

				if (!file.exists()) {
					throw new Exception(file.getCanonicalPath() + ": does not exist");
				}

				return LoadLibraryPresentation.parseLibraryFileToObject(file);

			} else {
				throw new Exception("not Authenticated");
			}

		} catch (Exception e) {
			log.error("[getListOfFilesByAbsolutePath]", e);
			return null;
		}

	}

	/**
	 * 
	 * Save an Object to the library and returns the file-explorer Id
	 * 
	 * @param SID
	 * @param room_id
	 * @param fileName
	 * @param tObjectRef
	 * @return - file-explorer Id in case of success, -1 otherwise
	 */
	public Long saveAsObject(String SID, Long room_id, String fileName,
			Object tObjectRef) {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);
			if (authLevelUtil.checkUserLevel(user_level)) {
				// LinkedHashMap tObject = (LinkedHashMap)t;
				// ArrayList tObject = (ArrayList)t;

				log.debug("saveAsObject :1: " + tObjectRef);
				log.debug("saveAsObject :2: " + tObjectRef.getClass().getName());

				@SuppressWarnings("rawtypes")
				ArrayList tObject = (ArrayList) tObjectRef;

				log.debug("saveAsObject" + tObject.size());

				String localFileName = MD5.do_checksum(new Date().toString()) + ".wml";

				LibraryDocumentConverter.writeToLocalFolder(localFileName, tObject);

				// String wmlPath = current_dir + File.separatorChar+fileName
				// +".xml";
				// OwnerID == null
				Long fileExplorerId = fileExplorerItemDao.add(fileName, "", 0L,
						null, room_id, users_id, false, // isFolder
						false, // isImage
						false, // isPresentation
						localFileName, // WML localFileName
						true, // isStoredWML file
						true, 0L, "");

				return fileExplorerId;
			}
		} catch (Exception err) {
			log.error("[saveAsObject] ", err);
		}
		return -1L;
	}

	/**
	 * 
	 * Loads a Object from the library into the whiteboard of all participant of
	 * the current room
	 * 
	 * @param SID
	 * @param room_id
	 * @param fileExplorerItemId
	 * @param whiteboardId
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void loadWmlObject(String SID, Long room_id,
			Long fileExplorerItemId, Long whiteboardId) {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);

			if (authLevelUtil.checkUserLevel(user_level)) {

				IConnection current = Red5.getConnectionLocal();
				Client currentClient = this.sessionManager
						.getClientByStreamId(current.getClient().getId(), null);

				if (currentClient == null) {
					return;
				}

				FileExplorerItem fileExplorerItem = fileExplorerItemDao
						.getFileExplorerItemsById(fileExplorerItemId);

				ArrayList roomItems = libraryWmlLoader.loadWmlFile(fileExplorerItem.getWmlFilePath());

				Map whiteboardObjClear = new HashMap();
				whiteboardObjClear.put(2, "clear");
				whiteboardObjClear.put(3, null);

				whiteboardManagement.addWhiteBoardObjectById(
						room_id, whiteboardObjClear, whiteboardId);

				for (int k = 0; k < roomItems.size(); k++) {

					ArrayList actionObject = (ArrayList) roomItems.get(k);

					Map whiteboardObj = new HashMap();
					whiteboardObj.put(2, "draw");
					whiteboardObj.put(3, actionObject);

					whiteboardManagement.addWhiteBoardObjectById(
							room_id, whiteboardObj, whiteboardId);

				}

				Map<String, Object> sendObject = new HashMap<String, Object>();
				sendObject.put("id", whiteboardId);
				sendObject.put("roomitems", roomItems);

				// Notify all Clients of that Scope (Room)
				Collection<Set<IConnection>> conCollection = current.getScope()
						.getConnections();
				for (Set<IConnection> conset : conCollection) {
					for (IConnection conn : conset) {
						if (conn != null) {
							if (conn instanceof IServiceCapableConnection) {
								Client rcl = this.sessionManager
										.getClientByStreamId(conn.getClient()
												.getId(), null);
								if ((rcl == null)
										|| (rcl.getIsScreenClient() != null && rcl
												.getIsScreenClient())) {
									continue;
								} else {
									((IServiceCapableConnection) conn).invoke(
											"loadWmlToWhiteboardById",
											new Object[] { sendObject }, this);
								}
							}
						}
					}
				}

			}
		} catch (Exception err) {
			log.error("[loadWmlObject] ", err);
		}
	}

	/**
	 * 
	 * Loads a chart object
	 * 
	 * @param SID
	 * @param room_id
	 * @param fileName
	 * @return - chart object
	 */
	@SuppressWarnings("rawtypes")
	public ArrayList loadChartObject(String SID, Long room_id, String fileName) {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);
			if (authLevelUtil.checkUserLevel(user_level)) {
				return LibraryChartLoader.getInstance().loadChart(OmFileHelper.getUploadRoomDir(room_id.toString()),
						fileName);
			}
		} catch (Exception err) {
			log.error("[loadChartObject] ", err);
		}
		return null;
	}

	/**
	 * @param SID
	 * @param flvFileExplorerId
	 * @return 1 in case of success, -1 otherwise
	 */
	public Long copyFileToCurrentRoom(String SID, Long flvFileExplorerId) {
		try {

			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);

			if (authLevelUtil.checkUserLevel(user_level)) {

				IConnection current = Red5.getConnectionLocal();
				String streamid = current.getClient().getId();

				Client currentClient = this.sessionManager
						.getClientByStreamId(streamid, null);

				Long room_id = currentClient.getRoom_id();

				if (room_id != null) {
					File outputFullFlvFile = new File(OmFileHelper.getStreamsHibernateDir()
						, "UPLOADFLV_" + flvFileExplorerId + ".flv");

					File targetFolder = OmFileHelper.getStreamsSubDir(room_id);

					File targetFullFlvFile = new File(targetFolder
						, "UPLOADFLV_" + flvFileExplorerId + ".flv");
					if (outputFullFlvFile.exists() && !targetFullFlvFile.exists()) {
						FileHelper.copy(outputFullFlvFile, targetFullFlvFile);
					}

					return 1L;
				}

			}

		} catch (Exception err) {
			log.error("[copyFileToCurrentRoom] ", err);
		}
		return -1L;
	}

	public void resultReceived(IPendingServiceCall arg0) {
		// TODO Auto-generated method stub

	}

}
