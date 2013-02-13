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
package org.apache.openmeetings.axis.services;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedHashMap;

import org.apache.axis2.AxisFault;
import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.data.basic.AuthLevelUtil;
import org.apache.openmeetings.data.basic.SessiondataDao;
import org.apache.openmeetings.data.file.FileProcessor;
import org.apache.openmeetings.data.file.FileUtils;
import org.apache.openmeetings.data.file.dao.FileExplorerItemDao;
import org.apache.openmeetings.data.file.dto.FileExplorerObject;
import org.apache.openmeetings.data.file.dto.LibraryPresentation;
import org.apache.openmeetings.data.user.UserManager;
import org.apache.openmeetings.documents.LoadLibraryPresentation;
import org.apache.openmeetings.documents.beans.ConverterProcessResultList;
import org.apache.openmeetings.persistence.beans.files.FileExplorerItem;
import org.apache.openmeetings.persistence.beans.user.User;
import org.apache.openmeetings.utils.OmFileHelper;
import org.apache.openmeetings.utils.StoredFile;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * Contains methods to import and upload files into the Files section of the
 * conference room and the personal drive of any user
 * 
 * @author sebawagner
 * @webservice FileService
 * 
 */
public class FileWebService {

	private static final Logger log = Red5LoggerFactory.getLogger(
			FileWebService.class, OpenmeetingsVariables.webAppRootKey);
	@Autowired
	private SessiondataDao sessiondataDao;
	@Autowired
	private UserManager userManager;
	@Autowired
	private FileExplorerItemDao fileExplorerItemDao;
	@Autowired
	private AuthLevelUtil authLevelUtil;
	@Autowired
	private FileProcessor fileProcessor;
	@Autowired
	private FileUtils fileUtils;

	/**
	 * 
	 * Import file from external source
	 * 
	 * to upload a file to a room-drive you specify: externalUserId, user if of
	 * openmeetings user for which we upload the file room_id = openmeetings
	 * room id isOwner = 0 parentFolderId = 0
	 * 
	 * to upload a file to a private-drive you specify: externalUserId, user if
	 * of openmeetings user for which we upload the file room_id = openmeetings
	 * room id isOwner = 1 parentFolderId = -2
	 * 
	 * @param SID The logged in session id with minimum webservice level
	 * @param externalUserId
	 *            the external user id =&gt; If the file should goto a private
	 *            section of any user, this number needs to be set
	 * @param externalFileId
	 *            the external file-type to identify the file later
	 * @param externalType
	 *            the name of the external system
	 * @param room_id
	 *            the room Id, if the file goes to the private folder of an
	 *            user, you can set a random number here
	 * @param isOwner
	 *            specify a 1/true AND parentFolderId==-2 to make the file goto
	 *            the private section
	 * @param path
	 *            http-path where we can grab the file from, the file has to be
	 *            accessible from the OpenMeetings server
	 * @param parentFolderId
	 *            specify a parentFolderId==-2 AND isOwner == 1/true AND to make
	 *            the file goto the private section
	 * @param fileSystemName
	 *            the filename =&gt; Important WITH file extension!
	 *            
	 * @return - array of file import errors
	 * @throws AxisFault
	 */
	public FileImportError[] importFile(String SID, String externalUserId,
			Long externalFileId, String externalType, Long room_id,
			boolean isOwner, String path, Long parentFolderId,
			String fileSystemName) throws AxisFault {
		try {

			Long users_id = sessiondataDao.checkSession(SID);
			Long User_level = userManager.getUserLevelByID(users_id);

			if (authLevelUtil.checkWebServiceLevel(User_level)) {

				URL url = new URL(path);
				URLConnection uc = url.openConnection();
				InputStream inputstream = new BufferedInputStream(
						uc.getInputStream());

				User externalUser = userManager.getUserByExternalIdAndType(
						externalUserId, externalType);

				LinkedHashMap<String, Object> hs = new LinkedHashMap<String, Object>();
				hs.put("user", externalUser);

				ConverterProcessResultList returnError = fileProcessor
						.processFile(externalUser.getUser_id(), room_id,
								isOwner, inputstream, parentFolderId,
								fileSystemName,
								externalFileId, externalType);

				// Flash cannot read the response of an upload
				// httpServletResponse.getWriter().print(returnError);
				hs.put("message", "library");
				hs.put("action", "newFile");
				hs.put("fileExplorerItem", fileExplorerItemDao
						.getFileExplorerItemsById(returnError.getFileExplorerItemId()));
				hs.put("error", returnError.getLogMessage());
				hs.put("fileName", returnError.getCompleteName());
				
				//FIXME: Send event to UI that there is a new file

				return returnError.convertToFileImportErrors();

			}
		} catch (Exception err) {
			log.error("[importFile]", err);
		}
		return null;
	}

	/**
	 * Import file from external source
	 * 
	 * to upload a file to a room-drive you specify: internalUserId, user if of
	 * openmeetings user for which we upload the file room_id = openmeetings
	 * room id isOwner = 0 parentFolderId = 0
	 * 
	 * to upload a file to a private-drive you specify: internalUserId, user if
	 * of openmeetings user for which we upload the file room_id = openmeetings
	 * room id isOwner = 1 parentFolderId = -2
	 * 
	 * @param SID
	 *            The SID of the User. This SID must be marked as logged in
	 * @param internalUserId
	 *            the openmeetings user id =&gt; If the file should goto a private
	 *            section of any user, this number needs to be se
	 * @param externalFileId
	 *            the external file-type to identify the file later
	 * @param externalType
	 *            the name of the external system
	 * @param room_id
	 *            the room Id, if the file goes to the private folder of an
	 *            user, you can set a random number here
	 * @param isOwner
	 *            specify a 1/true AND parentFolderId==-2 to make the file goto
	 *            the private section
	 * @param path
	 *            http-path where we can grab the file from, the file has to be
	 *            accessible from the OpenMeetings server
	 * @param parentFolderId
	 *            specify a parentFolderId==-2 AND isOwner == 1/true AND to make
	 *            the file goto the private section
	 * @param fileSystemName
	 *            the filename =&gt; Important WITH file extension!
	 *            
	 * @return - array of file import errors
	 * @throws AxisFault
	 */
	public FileImportError[] importFileByInternalUserId(String SID,
			Long internalUserId, Long externalFileId, String externalType,
			Long room_id, boolean isOwner, String path, Long parentFolderId,
			String fileSystemName) throws AxisFault {
		try {

			Long users_id = sessiondataDao.checkSession(SID);
			Long User_level = userManager.getUserLevelByID(users_id);

			if (authLevelUtil.checkWebServiceLevel(User_level)) {

				URL url = new URL(path);
				URLConnection uc = url.openConnection();
				InputStream inputstream = new BufferedInputStream(
						uc.getInputStream());

				User internalUser = userManager.getUserById(internalUserId);

				LinkedHashMap<String, Object> hs = new LinkedHashMap<String, Object>();
				hs.put("user", internalUser);

				ConverterProcessResultList returnError = fileProcessor
						.processFile(internalUser.getUser_id(), room_id,
								isOwner, inputstream, parentFolderId,
								fileSystemName, 
								externalFileId, externalType);

				// Flash cannot read the response of an upload
				// httpServletResponse.getWriter().print(returnError);
				hs.put("message", "library");
				hs.put("action", "newFile");
				hs.put("fileExplorerItem", fileExplorerItemDao
						.getFileExplorerItemsById(returnError.getFileExplorerItemId()));
				hs.put("error", returnError);
				hs.put("fileName", returnError.getCompleteName());
				
				//FIXME: Notificate UI of clients of new file

				return returnError.convertToFileImportErrors();

			}
		} catch (Exception err) {
			log.error("[importFileByInternalUserId]", err);
		}
		return null;
	}

	/**
	 * to add a folder to the private drive, set parentFileExplorerItemId = 0
	 * and isOwner to 1/true and externalUserId/externalUserType to a valid user
	 * 
	 * @param SID
	 *            The SID of the User. This SID must be marked as logged in
	 * @param externalUserId
	 *            the external file-type to identify the file later
	 * @param parentFileExplorerItemId
	 * @param folderName
	 *            the name of the folder
	 * @param room_id
	 *            the room Id, if the file goes to the private folder of an
	 *            user, you can set a random number here
	 * @param isOwner
	 *            specify a 1/true AND parentFolderId==-2 to make the file goto
	 *            the private section
	 * @param externalFilesid
	 *            the external file-type to identify the file later
	 * @param externalType
	 *            the name of the external system
	 *            
	 * @return - id of folder added
	 * @throws AxisFault
	 */
	public Long addFolderByExternalUserIdAndType(String SID,
			String externalUserId, Long parentFileExplorerItemId,
			String folderName, Long room_id, Boolean isOwner,
			Long externalFilesid, String externalType) throws AxisFault {
		try {

			Long users_id = sessiondataDao.checkSession(SID);
			Long User_level = userManager.getUserLevelByID(users_id);

			if (authLevelUtil.checkWebServiceLevel(User_level)) {

				User userExternal = userManager.getUserByExternalIdAndType(
						externalUserId, externalType);

				Long userId = userExternal.getUser_id();

				log.debug("addFolder " + parentFileExplorerItemId);

				if (parentFileExplorerItemId == -2 && isOwner) {
					// users_id (OwnerID) => only set if its directly root in
					// Owner Directory,
					// other Folders and Files maybe are also in a Home
					// directory
					// but just because their parent is
					return fileExplorerItemDao.add(folderName, "", 0L, userId,
							room_id, userId, true, // isFolder
							false, // isImage
							false, // isPresentation
							"", // WML Path
							false, // isStoredWML file
							false, // isXmlFile
							externalFilesid, externalType);
				} else {
					return fileExplorerItemDao.add(folderName, "",
							parentFileExplorerItemId, null, room_id, userId,
							true, // isFolder
							false, // isImage
							false, // isPresentation
							"", // WML Path
							false, // isStoredWML file
							false, // isXmlFile
							externalFilesid, externalType);
				}
			}

		} catch (Exception err) {
			log.error("[addFolderByExternalUserIdAndType]", err);
		}
		return null;
	}

	/**
	 * to add a folder to the private drive, set parentFileExplorerItemId = 0
	 * and isOwner to 1/true and userId to a valid user
	 * 
	 * @param SID The SID of the User. This SID must be marked as logged in
	 * @param userId the openmeetings user id 
	 * @param parentFileExplorerItemId specify a parentFolderId==-2 AND isOwner == 1/true AND to make the file goto the private section  
	 * @param folderName the name of the folder 
	 * @param room_id the room Id, if the file goes to the private folder of an user, you can set a random number here 
	 * @param isOwner specify a 1/true AND parentFolderId==-2 to make the file goto the private section 
	 * @param externalFilesid the external file-type to identify the file later 
	 * @param externalType the name of the external system
	 *  
	 * @return - id of the folder
	 * @throws AxisFault
	 */
	public Long addFolderByUserId(String SID, Long userId,
			Long parentFileExplorerItemId, String folderName, Long room_id,
			Boolean isOwner, Long externalFilesid, String externalType)
			throws AxisFault {
		try {

			Long users_id = sessiondataDao.checkSession(SID);
			Long User_level = userManager.getUserLevelByID(users_id);

			if (authLevelUtil.checkWebServiceLevel(User_level)) {

				log.debug("addFolder " + parentFileExplorerItemId);

				if (parentFileExplorerItemId == -2 && isOwner) {
					// users_id (OwnerID) => only set if its directly root in
					// Owner Directory,
					// other Folders and Files maybe are also in a Home
					// directory
					// but just because their parent is
					return fileExplorerItemDao.add(folderName, "", 0L, userId,
							room_id, userId, true, // isFolder
							false, // isImage
							false, // isPresentation
							"", // WML Path
							false, // isStoredWML file
							false, // isXmlFile
							externalFilesid, externalType);
				} else {
					return fileExplorerItemDao.add(folderName, "",
							parentFileExplorerItemId, null, room_id, userId,
							true, // isFolder
							false, // isImage
							false, // isPresentation
							"", // WML Path
							false, // isStoredWML file
							false, // isXmlFile
							externalFilesid, externalType);
				}
			}

		} catch (Exception err) {
			log.error("[addFolderByUserId]", err);
		}
		return null;
	}

	/**
	 * 
	 * Add a folder by the current user - similar to RTMP Call
	 * 
	 * @param SID
	 *            The SID of the User. This SID must be marked as logged in
	 * @param parentFileExplorerItemId
	 *            parent folder id
	 * @param fileName
	 *            the file name
	 * @param room_id
	 *            the room id
	 * @param isOwner
	 * @return - id of the folder
	 */
	public Long addFolderSelf(String SID, Long parentFileExplorerItemId,
			String fileName, Long room_id, Boolean isOwner) throws AxisFault {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);
			if (authLevelUtil.checkUserLevel(user_level)) {

				log.debug("addFolder " + parentFileExplorerItemId);

				if (parentFileExplorerItemId == 0 && isOwner) {
					// users_id (OwnerID) => only set if its directly root in
					// Owner Directory,
					// other Folders and Files maybe are also in a Home
					// directory
					// but just because their parent is
					return fileExplorerItemDao.add(fileName, "",
							parentFileExplorerItemId, users_id, room_id,
							users_id, true, // isFolder
							false, // isImage
							false, // isPresentation
							"", // WML Path
							false, // isStoredWML file
							false // isXmlFile
							, 0L, "");
				} else {
					return fileExplorerItemDao.add(fileName, "",
							parentFileExplorerItemId, null, room_id, users_id,
							true, // isFolder
							false, // isImage
							false, // isPresentation
							"", // WML Paht
							false, // isStoredWML file
							false // isXmlFile
							, 0L, "");
				}
			}
		} catch (Exception err) {
			log.error("[getFileExplorerByParent] ", err);
		}
		return null;
	}

	/**
	 * 
	 * deletes a file by its external Id and type
	 * 
	 * @param SID
	 *            The SID of the User. This SID must be marked as logged in
	 * @param externalFilesid
	 *            the od of the file or folder
	 * @param externalType
	 *            the externalType
	 * @return - null
	 */
	public Long deleteFileOrFolderByExternalIdAndType(String SID,
			Long externalFilesid, String externalType) throws AxisFault {

		try {

			Long users_id = sessiondataDao.checkSession(SID);
			Long User_level = userManager.getUserLevelByID(users_id);

			if (authLevelUtil.checkWebServiceLevel(User_level)) {

				fileExplorerItemDao.deleteFileExplorerItemByExternalIdAndType(
						externalFilesid, externalType);

			}

		} catch (Exception err) {
			log.error("[deleteFileOrFolderByExternalIdAndType]", err);
		}
		return null;
	}

	/**
	 * 
	 * deletes files or folders based on it id
	 * 
	 * @param SID
	 *            The SID of the User. This SID must be marked as logged in
	 * @param fileExplorerItemId
	 *            the id of the file or folder
	 * @return - null
	 */
	public Long deleteFileOrFolder(String SID, Long fileExplorerItemId)
			throws AxisFault {

		try {

			Long users_id = sessiondataDao.checkSession(SID);
			Long User_level = userManager.getUserLevelByID(users_id);

			if (authLevelUtil.checkWebServiceLevel(User_level)) {

				fileExplorerItemDao.deleteFileExplorerItem(fileExplorerItemId);

			}

		} catch (Exception err) {
			log.error("[deleteFileOrFolder]", err);
		}
		return null;
	}

	/**
	 * 
	 * deletes files or folders based on it id
	 * 
	 * @param SID
	 *            The SID of the User. This SID must be marked as logged in
	 * @param fileExplorerItemId
	 *            the id of the file or folder
	 * @return - null
	 */
	public Long deleteFileOrFolderSelf(String SID, Long fileExplorerItemId)
			throws AxisFault {

		try {

			Long users_id = sessiondataDao.checkSession(SID);
			Long User_level = userManager.getUserLevelByID(users_id);

			if (authLevelUtil.checkUserLevel(User_level)) {

				// TODO: Check if user has access or not to the file

				fileExplorerItemDao.deleteFileExplorerItem(fileExplorerItemId);

			}

		} catch (Exception err) {
			log.error("[deleteFileOrFolder]", err);
		}
		return null;
	}

	/**
	 * Get available import File Extension allowed during import
	 * 
	 * @return the array of the import file extensions
	 * @throws AxisFault
	 */
	public String[] getImportFileExtensions() throws AxisFault {
		try {

			return StoredFile.getExtensions();

		} catch (Exception err) {
			log.error("[getImportFileExtensions]", err);
		}
		return null;
	}

	/**
	 * Get a LibraryPresentation-Object for a certain file
	 * 
	 * @param SID
	 * @param parentFolder
	 * 
	 * @return - LibraryPresentation-Object for a certain file
	 * @throws AxisFault
	 */
	public LibraryPresentation getPresentationPreviewFileExplorer(String SID,
			String parentFolder) throws AxisFault {

		try {

			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);

			if (authLevelUtil.checkWebServiceLevel(user_level)) {

				File working_dir = new File(OmFileHelper.getUploadProfilesDir(), parentFolder);
				log.debug("############# working_dir : " + working_dir);

				File file = new File(working_dir, OmFileHelper.libraryFileName);

				if (!file.exists()) {
					throw new Exception(file.getCanonicalPath() + ": does not exist ");
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
	 * Get a File Explorer Object by a given Room and owner id
	 * 
	 * @param SID
	 *            The SID of the User. This SID must be marked as logged in
	 * @param room_id
	 *            Room id
	 * @param owner_id
	 *            Owner id
	 * @return - File Explorer Object by a given Room and owner id
	 * @throws AxisFault
	 */
	public FileExplorerObject getFileExplorerByRoom(String SID, Long room_id,
			Long owner_id) throws AxisFault {

		try {

			Long webservice_users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager
					.getUserLevelByID(webservice_users_id);

			if (authLevelUtil.checkWebServiceLevel(user_level)) {

				log.debug("room_id " + room_id);

				FileExplorerObject fileExplorerObject = new FileExplorerObject();

				// Home File List
				FileExplorerItem[] fList = fileExplorerItemDao
						.getFileExplorerItemsByOwner(owner_id, 0L);

				long homeFileSize = 0;

				for (FileExplorerItem homeChildExplorerItem : fList) {
					log.debug("FileExplorerItem fList "
							+ homeChildExplorerItem.getFileName());
					homeFileSize += fileUtils
							.getSizeOfDirectoryAndSubs(homeChildExplorerItem);
				}

				fileExplorerObject.setUserHome(fList);
				fileExplorerObject.setUserHomeSize(homeFileSize);

				// Public File List
				FileExplorerItem[] rList = fileExplorerItemDao
						.getFileExplorerItemsByRoom(room_id, 0L);

				long roomFileSize = 0;

				for (FileExplorerItem homeChildExplorerItem : rList) {
					log.debug("FileExplorerItem rList "
							+ homeChildExplorerItem.getFileName());
					roomFileSize += fileUtils
							.getSizeOfDirectoryAndSubs(homeChildExplorerItem);
				}

				fileExplorerObject.setRoomHome(rList);
				fileExplorerObject.setRoomHomeSize(roomFileSize);

				return fileExplorerObject;

			} else {

				throw new Exception("not Authenticated");

			}

		} catch (Exception e) {
			log.error("[getFileExplorerByRoom]", e);
			return null;
		}
	}

	/**
	 * Get a File Explorer Object by a given Room
	 * 
	 * @param SID
	 *            The SID of the User. This SID must be marked as logged in
	 * @param room_id
	 *            Room Id
	 * @return - File Explorer Object by a given Room
	 * @throws AxisFault
	 */
	public FileExplorerObject getFileExplorerByRoomSelf(String SID, Long room_id)
			throws AxisFault {

		try {

			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);

			if (authLevelUtil.checkUserLevel(user_level)) {

				log.debug("room_id " + room_id);

				FileExplorerObject fileExplorerObject = new FileExplorerObject();

				// Home File List
				FileExplorerItem[] fList = fileExplorerItemDao
						.getFileExplorerItemsByOwner(users_id, 0L);

				long homeFileSize = 0;

				for (FileExplorerItem homeChildExplorerItem : fList) {
					log.debug("FileExplorerItem fList "
							+ homeChildExplorerItem.getFileName());
					homeFileSize += fileUtils
							.getSizeOfDirectoryAndSubs(homeChildExplorerItem);
				}

				fileExplorerObject.setUserHome(fList);
				fileExplorerObject.setUserHomeSize(homeFileSize);

				// Public File List
				FileExplorerItem[] rList = fileExplorerItemDao
						.getFileExplorerItemsByRoom(room_id, 0L);

				long roomFileSize = 0;

				for (FileExplorerItem homeChildExplorerItem : rList) {
					log.debug("FileExplorerItem rList "
							+ homeChildExplorerItem.getFileName());
					roomFileSize += fileUtils
							.getSizeOfDirectoryAndSubs(homeChildExplorerItem);
				}

				fileExplorerObject.setRoomHome(rList);
				fileExplorerObject.setRoomHomeSize(roomFileSize);

				return fileExplorerObject;

			} else {

				throw new Exception("not Authenticated");

			}

		} catch (Exception e) {
			log.error("[getFileExplorerByRoomSelf]", e);
			return null;
		}
	}

	/**
	 * Get FileExplorerItem list by parent folder
	 * 
	 * @param SID
	 *            The SID of the User. This SID must be marked as logged in
	 * @param parentFileExplorerItemId
	 *            the parent folder id
	 * @param room_id
	 *            the room id
	 * @param isOwner
	 *            true if its a private drive
	 * @param owner_id
	 *            the owner id
	 * @return - FileExplorerItem list by parent folder
	 * @throws AxisFault
	 */
	public FileExplorerItem[] getFileExplorerByParent(String SID,
			Long parentFileExplorerItemId, Long room_id, Boolean isOwner,
			Long owner_id) throws AxisFault {

		try {

			Long webservice_users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager
					.getUserLevelByID(webservice_users_id);

			if (authLevelUtil.checkWebServiceLevel(user_level)) {

				log.debug("parentFileExplorerItemId "
						+ parentFileExplorerItemId);

				if (parentFileExplorerItemId == 0) {
					if (isOwner) {
						return fileExplorerItemDao.getFileExplorerItemsByOwner(
								owner_id, parentFileExplorerItemId);
					} else {
						return fileExplorerItemDao.getFileExplorerItemsByRoom(
								room_id, parentFileExplorerItemId);
					}
				} else {
					return fileExplorerItemDao
							.getFileExplorerItemsByParent(parentFileExplorerItemId);
				}

			}
		} catch (Exception err) {
			log.error("[getFileExplorerByParent] ", err);
		}
		return null;
	}

	/**
	 * 
	 * Get FileExplorerItem[] by parent and owner id
	 * 
	 * @param SID
	 *            SID The SID of the User. This SID must be marked as logged in
	 * @param parentFileExplorerItemId
	 *            the parent folder id
	 * @param room_id
	 *            the room id
	 * @param isOwner
	 *            true to request private drive
	 * @return - list of file explorer items
	 * @throws AxisFault
	 */
	public FileExplorerItem[] getFileExplorerByParentSelf(String SID,
			Long parentFileExplorerItemId, Long room_id, Boolean isOwner)
			throws AxisFault {

		try {

			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);

			if (authLevelUtil.checkUserLevel(user_level)) {

				log.debug("parentFileExplorerItemId "
						+ parentFileExplorerItemId);

				if (parentFileExplorerItemId == 0) {
					if (isOwner) {
						return fileExplorerItemDao.getFileExplorerItemsByOwner(
								users_id, parentFileExplorerItemId);
					} else {
						return fileExplorerItemDao.getFileExplorerItemsByRoom(
								room_id, parentFileExplorerItemId);
					}
				} else {
					return fileExplorerItemDao
							.getFileExplorerItemsByParent(parentFileExplorerItemId);
				}

			}
		} catch (Exception err) {
			log.error("[getFileExplorerByParentSelf] ", err);
		}
		return null;
	}

	/**
	 * update a file or folder name
	 * 
	 * @param SID
	 *            SID The SID of the User. This SID must be marked as logged in
	 * @param fileExplorerItemId
	 *            file or folder id
	 * @param fileName
	 *            new file or folder name
	 * @return - null
	 * @throws AxisFault
	 */
	public Long updateFileOrFolderName(String SID, Long fileExplorerItemId,
			String fileName) throws AxisFault {

		try {

			Long webservice_users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager
					.getUserLevelByID(webservice_users_id);

			if (authLevelUtil.checkWebServiceLevel(user_level)) {

				log.debug("deleteFileOrFolder " + fileExplorerItemId);

				fileExplorerItemDao.updateFileOrFolderName(fileExplorerItemId,
						fileName);

			}
		} catch (Exception err) {
			log.error("[updateFileOrFolderName] ", err);
		}
		return null;
	}

	/**
	 * 
	 * update a file or folder name
	 * 
	 * @param SID
	 *            SID The SID of the User. This SID must be marked as logged in
	 * @param fileExplorerItemId
	 *            file or folder id
	 * @param fileName
	 *            new file or folder name
	 * @return - null
	 * @throws AxisFault
	 */
	public Long updateFileOrFolderNameSelf(String SID, Long fileExplorerItemId,
			String fileName) throws AxisFault {

		try {

			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);

			if (authLevelUtil.checkUserLevel(user_level)) {

				// TODO: check if this user is allowed to change this file
				/*
				 * FileExplorerItem fileExItem =
				 * fileExplorerItemDao.getFileExplorerItemsById
				 * (fileExplorerItemId);
				 * 
				 * if (fileExItem.getOwnerId() != null &&
				 * !fileExItem.getOwnerId().equals(users_id)) { throw new
				 * Exception(
				 * "This user is not the owner of the file and not allowed to edit its name"
				 * ); }
				 */

				log.debug("deleteFileOrFolder " + fileExplorerItemId);

				fileExplorerItemDao.updateFileOrFolderName(fileExplorerItemId,
						fileName);

			}
		} catch (Exception err) {
			log.error("[updateFileOrFolderNameSelf] ", err);
		}
		return null;
	}

	/**
	 * move a file or folder
	 * 
	 * @param SID
	 *            SID The SID of the User. This SID must be marked as logged in
	 * @param fileExplorerItemId
	 *            current file or folder id to be moved
	 * @param newParentFileExplorerItemId
	 *            new parent folder id
	 * @param room_id
	 *            room id
	 * @param isOwner
	 *            if true owner id will be set
	 * @param moveToHome
	 *            if true move to private drive
	 * @param owner_id
	 *            owner id
	 * @return - null
	 * @throws AxisFault
	 */
	public Long moveFile(String SID, Long fileExplorerItemId,
			Long newParentFileExplorerItemId, Long room_id, Boolean isOwner,
			Boolean moveToHome, Long owner_id) throws AxisFault {

		try {

			Long webservice_users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager
					.getUserLevelByID(webservice_users_id);

			if (authLevelUtil.checkWebServiceLevel(user_level)) {

				log.debug("deleteFileOrFolder " + fileExplorerItemId);

				fileExplorerItemDao
						.moveFile(fileExplorerItemId,
								newParentFileExplorerItemId, room_id, isOwner,
								owner_id);

				FileExplorerItem fileExplorerItem = fileExplorerItemDao
						.getFileExplorerItemsById(fileExplorerItemId);

				if (moveToHome) {
					// set this file and all subfiles and folders the ownerId
					fileUtils.setFileToOwnerOrRoomByParent(fileExplorerItem,
							owner_id, null);

				} else {
					// set this file and all subfiles and folders the room_id
					fileUtils.setFileToOwnerOrRoomByParent(fileExplorerItem,
							null, room_id);

				}

			}
		} catch (Exception err) {
			log.error("[moveFile] ", err);
		}
		return null;
	}

	/**
	 * move a file or folder
	 * 
	 * @param SID
	 *            SID The SID of the User. This SID must be marked as logged in
	 * @param fileExplorerItemId
	 *            current file or folder id to be moved
	 * @param newParentFileExplorerItemId
	 *            new parent folder id
	 * @param room_id
	 *            room id
	 * @param isOwner
	 *            if true owner id will be set
	 * @param moveToHome
	 *            move to private drive
	 * @return - null
	 * @throws AxisFault
	 */
	public Long moveFileSelf(String SID, Long fileExplorerItemId,
			Long newParentFileExplorerItemId, Long room_id, Boolean isOwner,
			Boolean moveToHome) throws AxisFault {

		try {

			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);

			if (authLevelUtil.checkUserLevel(user_level)) {
				
				// A test is required that checks if the user is allowed to move the file

				log.debug("moveFileSelf " + fileExplorerItemId);

				fileExplorerItemDao
						.moveFile(fileExplorerItemId,
								newParentFileExplorerItemId, room_id, isOwner,
								users_id);

				FileExplorerItem fileExplorerItem = fileExplorerItemDao
						.getFileExplorerItemsById(fileExplorerItemId);

				if (moveToHome) {
					// set this file and all subfiles and folders the ownerId
					fileUtils.setFileToOwnerOrRoomByParent(fileExplorerItem,
							users_id, null);

				} else {
					// set this file and all subfiles and folders the room_id
					fileUtils.setFileToOwnerOrRoomByParent(fileExplorerItem,
							null, room_id);

				}

			}
		} catch (Exception err) {
			log.error("[moveFile] ", err);
		}
		return null;
	}

}
