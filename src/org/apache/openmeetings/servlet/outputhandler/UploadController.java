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
package org.apache.openmeetings.servlet.outputhandler;

import java.io.File;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.transaction.util.FileHelper;
import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.data.file.FileProcessor;
import org.apache.openmeetings.data.file.dao.FileExplorerItemDao;
import org.apache.openmeetings.data.user.dao.UsersDao;
import org.apache.openmeetings.documents.GenerateImage;
import org.apache.openmeetings.documents.GeneratePDF;
import org.apache.openmeetings.documents.GenerateThumbs;
import org.apache.openmeetings.documents.beans.ConverterProcessResultList;
import org.apache.openmeetings.documents.beans.UploadCompleteMessage;
import org.apache.openmeetings.persistence.beans.user.User;
import org.apache.openmeetings.remote.red5.ScopeApplicationAdapter;
import org.apache.openmeetings.utils.OmFileHelper;
import org.apache.openmeetings.utils.StoredFile;
import org.apache.openmeetings.utils.stringhandlers.StringComparer;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class UploadController extends AbstractUploadController {
	private static final Logger log = Red5LoggerFactory.getLogger(
			UploadController.class, OpenmeetingsVariables.webAppRootKey);
	
	@Autowired
	private UsersDao usersDao;
	@Autowired
	private ScopeApplicationAdapter scopeApplicationAdapter;
	@Autowired
	private GeneratePDF generatePDF;
	@Autowired
	private GenerateThumbs generateThumbs;
	@Autowired
	private GenerateImage generateImage;
	@Autowired
	private FileProcessor fileProcessor;
	@Autowired
	private FileExplorerItemDao fileExplorerItemDao;

	private String filesString[] = null;
	
    @RequestMapping(value = "/file.upload", method = RequestMethod.POST)
    public void handleFileUpload(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException {
    	UploadInfo info = validate(request, false);
    	try {
			String room_idAsString = request.getParameter("room_id");
			if (room_idAsString == null) {
				throw new ServletException("Missing Room ID");
			}
	
			Long room_id_to_Store = Long.parseLong(room_idAsString);
	
			String isOwnerAsString = request.getParameter("isOwner");
			if (isOwnerAsString == null) {
				throw new ServletException("Missing isOwnerAsString");
			}
			boolean isOwner = false;
			if (isOwnerAsString.equals("1")) {
				isOwner = true;
			}
	
			String parentFolderIdAsString = request
					.getParameter("parentFolderId");
			if (parentFolderIdAsString == null) {
				throw new ServletException("Missing parentFolderId ID");
			}
			Long parentFolderId = Long.parseLong(parentFolderIdAsString);
	
			MultipartFile multipartFile = info.file;
			InputStream is = multipartFile.getInputStream();
			log.debug("fileSystemName: " + info.filename);
	
			ConverterProcessResultList returnError = fileProcessor
					.processFile(info.userId, room_id_to_Store, isOwner, is,
							parentFolderId, info.filename, 0L, ""); // externalFilesId, externalType
	
			UploadCompleteMessage uploadCompleteMessage = new UploadCompleteMessage();
	    	uploadCompleteMessage.setUserId(info.userId);
	
			// Flash cannot read the response of an upload
			// httpServletResponse.getWriter().print(returnError);
	    	uploadCompleteMessage.setMessage("library");
	    	uploadCompleteMessage.setAction("newFile");
	    	
	    	uploadCompleteMessage.setFileExplorerItem(
					fileExplorerItemDao.getFileExplorerItemsById(
							returnError.getFileExplorerItemId()));
			
			uploadCompleteMessage.setHasError(returnError.hasError());
			//we only send the complete log to the client if there is really something 
			//to show because of an error
			if (returnError.hasError()) {
				uploadCompleteMessage.setError(returnError.getLogMessage());
			}
			uploadCompleteMessage.setFileName(returnError.getCompleteName());
			
			sendMessage(info, uploadCompleteMessage);
		} catch (ServletException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception during upload: ", e);
			throw new ServletException(e);
    	}
    }

    @RequestMapping(value = "/upload.upload", method = RequestMethod.POST)
    public void handleFormUpload(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		try {
	    	UploadInfo info = validate(request, false);
	    	
			String room_id = request.getParameter("room_id");
			if (room_id == null) {
				room_id = "default";
			}
			String roomName = StringUtils.deleteWhitespace(room_id);
	
			String moduleName = request.getParameter("moduleName");
			if (moduleName == null) {
				moduleName = "nomodule";
			}
			if (moduleName.equals("nomodule")) {
				log.debug("module name missed");
				return;
			}
			boolean userProfile = moduleName.equals("userprofile");
	
			MultipartFile multipartFile = info.file;
			InputStream is = multipartFile.getInputStream();
			String fileSystemName = info.filename;
			fileSystemName = StringUtils.deleteWhitespace(fileSystemName);
	
			UploadCompleteMessage uploadCompleteMessage = new UploadCompleteMessage();
	    	uploadCompleteMessage.setUserId(info.userId);
			
			// Flash cannot read the response of an upload
			// httpServletResponse.getWriter().print(returnError);
			uploadFile(request, userProfile, info.userId, roomName, is, fileSystemName, uploadCompleteMessage);
			sendMessage(info, uploadCompleteMessage);
		} catch (ServletException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception during upload: ", e);
			throw new ServletException(e);
		}
    }

    
    private void sendMessage(UploadInfo info, UploadCompleteMessage uploadCompleteMessage) {
		scopeApplicationAdapter.sendUploadCompletMessageByPublicSID(
				uploadCompleteMessage, info.publicSID);
    }
    
	private void uploadFile(HttpServletRequest request, boolean userProfile, Long userId, String roomName,
			InputStream is, String fileSystemName, UploadCompleteMessage uploadCompleteMessage)
			throws Exception {
		ConverterProcessResultList returnError = new ConverterProcessResultList();

		// Check variable to see if this file is a presentation
		int dotidx = fileSystemName.lastIndexOf('.');
		String newFileName = StringComparer.getInstance().compareForRealPaths(
				fileSystemName.substring(0, dotidx));
		String newFileExtDot = fileSystemName.substring(dotidx,
				fileSystemName.length()).toLowerCase();
		String newFileExt = newFileExtDot.substring(1);

		// trim long names cause cannot output that
		final int MAX_FILE_NAME_LENGTH = 30;
		if (newFileName.length() >= MAX_FILE_NAME_LENGTH) {
			newFileName = newFileName.substring(0, MAX_FILE_NAME_LENGTH);
		}
		StoredFile storedFile = new StoredFile(newFileName, newFileExt);

		// check if this is a a file that can be converted by
		// openoffice-service
		boolean canBeConverted = storedFile.isConvertable();
		boolean isPdf = storedFile.isPdf();
		boolean isImage = storedFile.isImage();
		boolean isAsIs = storedFile.isAsIs();

		File workingDir = null;
		// add outputfolders for profiles
		if (userProfile) {
			// User Profile Update
			this.deleteUserProfileFilesStoreTemp(userId);
			newFileName = "profile"; //set unified file name to avoid any problems with national characters
			workingDir = OmFileHelper.getUploadProfilesUserDir(userId);
		}
		// if it is a presenation it will be copied to another
		// place
		if (isAsIs) {
			// check if this is a room file or UserProfile
			if (!userProfile) {
				workingDir = OmFileHelper.getUploadRoomDir(roomName);
			}
		} else if (canBeConverted || isPdf || isImage) {
			workingDir = OmFileHelper.getUploadTempProfilesUserDir(userId);
		} else {
			return;
		}

		File completeName = OmFileHelper.getNewFile(workingDir, newFileName, newFileExtDot);

		log.debug("write file to : " + completeName);

		FileHelper.copy(is, completeName);
		is.close();

		log.debug("canBeConverted: " + canBeConverted);
		if (canBeConverted) {
			// convert to pdf, thumbs, swf and xml-description
			returnError = generatePDF.convertPDF(newFileName, roomName, true, completeName);
		} else if (isPdf) {
			
			boolean isEncrypted = true; 
			
			log.debug("isEncrypted :: " + isEncrypted);

			if (isEncrypted) {
				// Do convert pdf to other pdf first
				File f_old = completeName;

				completeName = OmFileHelper.appendSuffix(completeName, "_N_E");
				newFileName += "_N_E";

				generateThumbs.decodePDF(f_old.getCanonicalPath(), completeName.getCanonicalPath());

				if (f_old.exists()) {
					f_old.delete();
				}

			}

			// convert to thumbs, swf and xml-description
			returnError = generatePDF.convertPDF(newFileName, roomName, false, completeName);

			// returnError.put("decodePDF", returnError2);

		} else if (isImage && !isAsIs) {

			log.debug("##### isImage! userProfilePic: " + userProfile);

			if (userProfile) {
				// User Profile Update
				this.deleteUserProfileFiles(userId);
				// convert it to JPG
				returnError = generateImage.convertImageUserProfile(
					newFileName, newFileExtDot, userId, newFileName, false);
			} else {
				// convert it to JPG
				log.debug("##### convert it to JPG: " + userProfile);
				returnError = generateImage.convertImage(
					newFileName, newFileExtDot, roomName, newFileName, false);
			}
		} else if (isAsIs) {
			if (userProfile) {
				// User Profile Update
				this.deleteUserProfileFiles(userId);
				// is UserProfile Picture
				returnError.addItem("processThumb1", generateThumbs
						.generateThumb("_chat_", completeName, 40));
				returnError.addItem("processThumb2", generateThumbs
						.generateThumb("_profile_", completeName, 126));
				returnError.addItem("processThumb3", generateThumbs
						.generateThumb("_big_", completeName, 240));

				String pictureuri = completeName.getName();
				User us = usersDao.get(userId);
				us.setUpdatetime(new java.util.Date());
				us.setPictureuri(pictureuri);
				usersDao.update(us, userId);

				//FIXME: After updating the picture url all other users should refresh
			} else {
				returnError.addItem("processThumb", generateThumbs
						.generateThumb("_thumb_", completeName, 50));
			}
		}

		uploadCompleteMessage.setMessage("library");
		uploadCompleteMessage.setAction("newFile");
		
		uploadCompleteMessage.setHasError(returnError.hasError());
		
		//we only send the complete log to the client if there is really something 
		//to show because of an error
		if (returnError.hasError()) {
			uploadCompleteMessage.setError(returnError.getLogMessage());
		}
		uploadCompleteMessage.setFileName(completeName.getName());
		
	}

	private void deleteUserProfileFilesStoreTemp(Long users_id) throws Exception {
		File f = OmFileHelper.getUploadProfilesUserDir(users_id);
		if (f.exists() && f.isDirectory()) {
			this.filesString = f.list();
		}
	}

	private void deleteUserProfileFiles(Long users_id) throws Exception {
		File working_imgdir = OmFileHelper.getUploadProfilesUserDir(users_id);

		for (int i = 0; i < this.filesString.length; i++) {
			String fileName = filesString[i];
			File subf = new File(working_imgdir, fileName);
			subf.delete();
		}
	}
}
