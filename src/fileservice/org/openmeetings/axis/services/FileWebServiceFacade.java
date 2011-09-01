package org.openmeetings.axis.services;

import javax.servlet.ServletContext;

import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.transport.http.HTTPConstants;
import org.openmeetings.app.data.file.dto.FileExplorerObject;
import org.openmeetings.app.data.file.dto.LibraryPresentation;
import org.openmeetings.app.persistence.beans.files.FileExplorerItem;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class FileWebServiceFacade {

	private static final Logger log = Red5LoggerFactory.getLogger(
			FileWebServiceFacade.class, ScopeApplicationAdapter.webAppRootKey);

	private ServletContext getServletContext() throws Exception {
		MessageContext mc = MessageContext.getCurrentMessageContext();
		return (ServletContext) mc
				.getProperty(HTTPConstants.MC_HTTP_SERVLETCONTEXT);
	}

	private FileWebService geFileServiceProxy() {
		try {
			ApplicationContext context = WebApplicationContextUtils
					.getWebApplicationContext(getServletContext());
			return (FileWebService) context.getBean("fileWebService");
		} catch (Exception err) {
			log.error("[geRoomServiceProxy]", err);
		}
		return null;
	}

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
	 *            the external user id => If the file should goto a private
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
	 *            the filename => Important WITH file extension!
	 * @return
	 * @throws AxisFault
	 */
	public FileImportError[] importFile(String SID, Long externalUserId,
			Long externalFileId, String externalType, Long room_id,
			boolean isOwner, String path, Long parentFolderId,
			String fileSystemName) throws AxisFault {
		return this.geFileServiceProxy().importFile(SID, externalUserId,
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
	 *            the openmeetings user id => If the file should goto a private
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
	 *            the filename => Important WITH file extension!
	 * @return
	 * @throws AxisFault
	 */
	public FileImportError[] importFileByInternalUserId(String SID,
			Long internalUserId, Long externalFileId, String externalType,
			Long room_id, boolean isOwner, String path, Long parentFolderId,
			String fileSystemName) throws AxisFault {
		return this.geFileServiceProxy().importFileByInternalUserId(SID,
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
			Long externalUserId, Long parentFileExplorerItemId,
			String folderName, Long room_id, Boolean isOwner,
			Long externalFilesid, String externalType) throws AxisFault {
		return this.geFileServiceProxy().addFolderByExternalUserIdAndType(SID,
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
		return this.geFileServiceProxy().addFolderByUserId(SID, userId,
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
		return this.geFileServiceProxy().addFolderSelf(SID,
				parentFileExplorerItemId, fileName, room_id, isOwner);
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
		return this.geFileServiceProxy().deleteFileOrFolderByExternalIdAndType(
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
		return this.geFileServiceProxy().deleteFileOrFolder(SID,
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
		return this.geFileServiceProxy().deleteFileOrFolderSelf(SID,
				fileExplorerItemId);
	}

	public String[] getImportFileExtensions() throws AxisFault {
		return this.geFileServiceProxy().getImportFileExtensions();
	}

	public LibraryPresentation getPresentationPreviewFileExplorer(String SID,
			String parentFolder) throws AxisFault {
		return this.geFileServiceProxy().getPresentationPreviewFileExplorer(
				SID, parentFolder);
	}

	public FileExplorerObject getFileExplorerByRoom(String SID, Long room_id,
			Long owner_id) throws AxisFault {
		return this.geFileServiceProxy().getFileExplorerByRoom(SID, room_id,
				owner_id);
	}

	public FileExplorerObject getFileExplorerByRoomSelf(String SID, Long room_id)
			throws AxisFault {
		return this.geFileServiceProxy()
				.getFileExplorerByRoomSelf(SID, room_id);
	}

	public FileExplorerItem[] getFileExplorerByParent(String SID,
			Long parentFileExplorerItemId, Long room_id, Boolean isOwner,
			Long owner_id) throws AxisFault {
		return this.geFileServiceProxy().getFileExplorerByParent(SID,
				parentFileExplorerItemId, room_id, isOwner, owner_id);
	}

	public FileExplorerItem[] getFileExplorerByParentSelf(String SID,
			Long parentFileExplorerItemId, Long room_id, Boolean isOwner)
			throws AxisFault {
		return this.geFileServiceProxy().getFileExplorerByParentSelf(SID,
				parentFileExplorerItemId, room_id, isOwner);
	}

	public Long updateFileOrFolderName(String SID, Long fileExplorerItemId,
			String fileName) throws AxisFault {
		return this.geFileServiceProxy().updateFileOrFolderName(SID,
				fileExplorerItemId, fileName);
	}

	public Long updateFileOrFolderNameSelf(String SID, Long fileExplorerItemId,
			String fileName) throws AxisFault {
		return this.geFileServiceProxy().updateFileOrFolderNameSelf(SID,
				fileExplorerItemId, fileName);
	}

	public Long moveFile(String SID, Long fileExplorerItemId,
			Long newParentFileExplorerItemId, Long room_id, Boolean isOwner,
			Boolean moveToHome, Long owner_id) throws AxisFault {
		return this.geFileServiceProxy().moveFile(SID, fileExplorerItemId,
				newParentFileExplorerItemId, room_id, isOwner, moveToHome,
				owner_id);
	}

	public Long moveFileSelf(String SID, Long fileExplorerItemId,
			Long newParentFileExplorerItemId, Long room_id, Boolean isOwner,
			Boolean moveToHome) throws AxisFault {
		return this.geFileServiceProxy().moveFileSelf(SID, fileExplorerItemId,
				newParentFileExplorerItemId, room_id, isOwner, moveToHome);
	}

}
