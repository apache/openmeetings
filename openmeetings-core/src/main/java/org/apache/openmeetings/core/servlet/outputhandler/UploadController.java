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
package org.apache.openmeetings.core.servlet.outputhandler;

import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.openmeetings.core.data.file.FileProcessor;
import org.apache.openmeetings.core.remote.red5.ScopeApplicationAdapter;
import org.apache.openmeetings.db.dao.file.FileExplorerItemDao;
import org.apache.openmeetings.db.entity.file.FileExplorerItem;
import org.apache.openmeetings.db.entity.file.FileItem.Type;
import org.apache.openmeetings.util.OpenmeetingsVariables;
import org.apache.openmeetings.util.process.ConverterProcessResultList;
import org.apache.openmeetings.util.process.UploadCompleteMessage;
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
	private ScopeApplicationAdapter scopeApplicationAdapter;
	@Autowired
	private FileProcessor fileProcessor;
	@Autowired
	private FileExplorerItemDao fileExplorerItemDao;

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
	
			String parentFolderIdAsString = request.getParameter("parentFolderId");
			if (parentFolderIdAsString == null) {
				throw new ServletException("Missing parentFolderId ID");
			}
			Long parentFolderId = Long.parseLong(parentFolderIdAsString);
	
			MultipartFile multipartFile = info.file;
			InputStream is = multipartFile.getInputStream();
			log.debug("fileSystemName: " + info.filename);
	
			ConverterProcessResultList returnError = fileProcessor
					.processFile(info.userId, room_id_to_Store, isOwner, is,
							parentFolderId, info.filename, "", ""); // externalFilesId, externalType
	
			UploadCompleteMessage uploadCompleteMessage = new UploadCompleteMessage();
	    	uploadCompleteMessage.setUserId(info.userId);
	
			// Flash cannot read the response of an upload
			// httpServletResponse.getWriter().print(returnError);
	    	uploadCompleteMessage.setMessage("library");
	    	uploadCompleteMessage.setAction("newFile");
	    	
	    	setFileExplorerItem(uploadCompleteMessage,
					fileExplorerItemDao.get(
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
    
	public void setFileExplorerItem(UploadCompleteMessage msg, FileExplorerItem fileExplorerItem) {
		if (Type.Image == fileExplorerItem.getType()) {
			msg.setIsImage(true);
		}
		if (Type.Video == fileExplorerItem.getType()) {
			msg.setIsVideo(true);
		}
		if (Type.Presentation == fileExplorerItem.getType()) {
			msg.setIsPresentation(true);
		}
		msg.setFileSystemName(fileExplorerItem.getName());
		msg.setFileHash(fileExplorerItem.getHash());
	}
    
    private void sendMessage(UploadInfo info, UploadCompleteMessage uploadCompleteMessage) {
		scopeApplicationAdapter.sendUploadCompletMessageByPublicSID(
				uploadCompleteMessage, info.publicSID);
    }
}
