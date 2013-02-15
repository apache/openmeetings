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

import org.apache.axis2.AxisFault;
import org.apache.openmeetings.data.file.dto.FileExplorerObject;
import org.apache.openmeetings.data.file.dto.LibraryPresentation;
import org.apache.openmeetings.persistence.beans.files.FileExplorerItem;

public class FileWebServiceFacade extends BaseWebService {

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
	 * @param SID
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
	 * @return
	 * @throws AxisFault
	 */
	public FileImportError[] importFile(String SID, String externalUserId,
			Long externalFileId, String externalType, Long room_id,
			boolean isOwner, String path, Long parentFolderId,
			String fileSystemName) throws AxisFault {
		return getBean(FileWebService.class).importFile(SID, externalUserId,
				externalFileId, externalType, room_id, isOwner, path,
				parentFolderId, fileSystemName);
	}

	/**
	 * 
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
	 * @param internalUserId
	 *            the openmeetings user id =&gt; If the file should goto a private
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
	 * @return
	 * @throws AxisFault
	 */
	public FileImportError[] importFileByInternalUserId(String SID,
			Long internalUserId, Long externalFileId, String externalType,
			Long room_id, boolean isOwner, String path, Long parentFolderId,
			String fileSystemName) throws AxisFault {
		return getBean(FileWebService.class).importFileByInternalUserId(SID,
				internalUserId, externalFileId, externalType, room_id, isOwner,
				path, parentFolderId, fileSystemName);
	}

	/**
	 * 
	 * to add a folder to the private drive, set parentFileExplorerItemId = 0
	 * and isOwner to 1/true and externalUserId/externalUserType to a valid user
	 * 
	 * @param SID
	 * @param externalUserId
	 * @param externalUserType
	 * @param parentFileExplorerItemId
	 * @param fileName
	 * @param room_id
	 * @param isOwner
	 * @param externalFilesid
	 * @param externalType
	 * @return
	 * @throws AxisFault
	 */
	public Long addFolderByExternalUserIdAndType(String SID,
			String externalUserId, Long parentFileExplorerItemId,
			String folderName, Long room_id, Boolean isOwner,
			Long externalFilesid, String externalType) throws AxisFault {
		return getBean(FileWebService.class).addFolderByExternalUserIdAndType(SID,
				externalUserId, parentFileExplorerItemId, folderName, room_id,
				isOwner, externalFilesid, externalType);

	}

	/**
	 * 
	 * to add a folder to the private drive, set parentFileExplorerItemId = 0
	 * and isOwner to 1/true and userId to a valid user
	 * 
	 * @param SID
	 * @param userId
	 * @param parentFileExplorerItemId
	 * @param fileName
	 * @param room_id
	 * @param isOwner
	 * @param externalFilesid
	 * @param externalType
	 * @return
	 * @throws AxisFault
	 */
	public Long addFolderByUserId(String SID, Long userId,
			Long parentFileExplorerItemId, String folderName, Long room_id,
			Boolean isOwner, Long externalFilesid, String externalType)
			throws AxisFault {
		return getBean(FileWebService.class).addFolderByUserId(SID, userId,
				parentFileExplorerItemId, folderName, room_id, isOwner,
				externalFilesid, externalType);
	}

	/**
	 * 
	 * Add a folder by the current user - similar to RTMP Call
	 * 
	 * @param SID
	 * @param parentFileExplorerItemId
	 * @param fileName
	 * @param room_id
	 * @param isOwner
	 * @return
	 */
	public Long addFolderSelf(String SID, Long parentFileExplorerItemId,
			String fileName, Long room_id, Boolean isOwner) throws AxisFault {
		return getBean(FileWebService.class).addFolderSelf(SID,
				parentFileExplorerItemId, fileName, room_id, isOwner);
	}
	
	public Long addFolderSelfInternal(String SID, Long parentFileExplorerItemId,
			String fileName, Long roomId, Boolean isOwner) throws AxisFault {
		return getBean(FileWebService.class).addFolderSelf(SID,
				parentFileExplorerItemId, fileName, roomId, isOwner);
	}
	

	/**
	 * 
	 * deletes a file by its external Id and type
	 * 
	 * @param SID
	 * @param externalFilesid
	 * @param externalType
	 * @return
	 */
	public Long deleteFileOrFolderByExternalIdAndType(String SID,
			Long externalFilesid, String externalType) throws AxisFault {
		return getBean(FileWebService.class).deleteFileOrFolderByExternalIdAndType(
				SID, externalFilesid, externalType);
	}

	/**
	 * 
	 * deletes files or folders based on it id
	 * 
	 * @param SID
	 * @param fileExplorerItemId
	 * @return
	 */
	public Long deleteFileOrFolder(String SID, Long fileExplorerItemId)
			throws AxisFault {
		return getBean(FileWebService.class).deleteFileOrFolder(SID,
				fileExplorerItemId);
	}

	/**
	 * 
	 * deletes files or folders based on it id
	 * 
	 * @param SID
	 * @param fileExplorerItemId
	 * @return
	 */
	public Long deleteFileOrFolderSelf(String SID, Long fileExplorerItemId)
			throws AxisFault {
		return getBean(FileWebService.class).deleteFileOrFolderSelf(SID,
				fileExplorerItemId);
	}

	public String[] getImportFileExtensions() throws AxisFault {
		return getBean(FileWebService.class).getImportFileExtensions();
	}

	public LibraryPresentation getPresentationPreviewFileExplorer(String SID,
			String parentFolder) throws AxisFault {
		return getBean(FileWebService.class).getPresentationPreviewFileExplorer(
				SID, parentFolder);
	}

	public FileExplorerObject getFileExplorerByRoom(String SID, Long room_id,
			Long owner_id) throws AxisFault {
		return getBean(FileWebService.class).getFileExplorerByRoom(SID, room_id,
				owner_id);
	}

	public FileExplorerObject getFileExplorerByRoomSelf(String SID, Long room_id)
			throws AxisFault {
		return getBean(FileWebService.class)
				.getFileExplorerByRoomSelf(SID, room_id);
	}
	
	public FileExplorerObject getFileExplorerByRoomSelfInternal(String SID, Long roomId)
			throws AxisFault {
		return getBean(FileWebService.class)
				.getFileExplorerByRoomSelf(SID, roomId);
	}

	public FileExplorerItem[] getFileExplorerByParent(String SID,
			Long parentFileExplorerItemId, Long room_id, Boolean isOwner,
			Long owner_id) throws AxisFault {
		return getBean(FileWebService.class).getFileExplorerByParent(SID,
				parentFileExplorerItemId, room_id, isOwner, owner_id);
	}

	public FileExplorerItem[] getFileExplorerByParentSelf(String SID,
			Long parentFileExplorerItemId, Long room_id, Boolean isOwner)
			throws AxisFault {
		return getBean(FileWebService.class).getFileExplorerByParentSelf(SID,
				parentFileExplorerItemId, room_id, isOwner);
	}
	
	public FileExplorerItem[] getFileExplorerByParentSelfInternal(String SID,
			Long parentFileExplorerItemId, Long roomId, Boolean isOwner)
			throws AxisFault {
		return getBean(FileWebService.class).getFileExplorerByParentSelf(SID,
				parentFileExplorerItemId, roomId, isOwner);
	}

	public Long updateFileOrFolderName(String SID, Long fileExplorerItemId,
			String fileName) throws AxisFault {
		return getBean(FileWebService.class).updateFileOrFolderName(SID,
				fileExplorerItemId, fileName);
	}

	public Long updateFileOrFolderNameSelf(String SID, Long fileExplorerItemId,
			String fileName) throws AxisFault {
		return getBean(FileWebService.class).updateFileOrFolderNameSelf(SID,
				fileExplorerItemId, fileName);
	}

	public Long moveFile(String SID, Long fileExplorerItemId,
			Long newParentFileExplorerItemId, Long room_id, Boolean isOwner,
			Boolean moveToHome, Long owner_id) throws AxisFault {
		return getBean(FileWebService.class).moveFile(SID, fileExplorerItemId,
				newParentFileExplorerItemId, room_id, isOwner, moveToHome,
				owner_id);
	}

	public Long moveFileSelf(String SID, Long fileExplorerItemId,
			Long newParentFileExplorerItemId, Long room_id, Boolean isOwner,
			Boolean moveToHome) throws AxisFault {
		return getBean(FileWebService.class).moveFileSelf(SID, fileExplorerItemId,
				newParentFileExplorerItemId, room_id, isOwner, moveToHome);
	}
	
	public Long moveFileSelfInternal(String SID, Long fileExplorerItemId,
			Long newParentFileExplorerItemId, Long roomId, Boolean isOwner,
			Boolean moveToHome) throws AxisFault {
		return getBean(FileWebService.class).moveFileSelf(SID, fileExplorerItemId,
				newParentFileExplorerItemId, roomId, isOwner, moveToHome);
	}

}
