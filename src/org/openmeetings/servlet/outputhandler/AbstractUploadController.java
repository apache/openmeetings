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

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.openmeetings.app.OpenmeetingsVariables;
import org.openmeetings.app.data.basic.AuthLevelmanagement;
import org.openmeetings.app.data.basic.Configurationmanagement;
import org.openmeetings.app.data.basic.Sessionmanagement;
import org.openmeetings.app.data.user.Usermanagement;
import org.openmeetings.utils.ImportHelper;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

public abstract class AbstractUploadController implements ServletContextAware {
	private static final Logger log = Red5LoggerFactory.getLogger(
			AbstractUploadController.class, OpenmeetingsVariables.webAppRootKey);
	protected ServletContext context;
	@Autowired
	protected Sessionmanagement sessionManagement;
	@Autowired
	protected Usermanagement userManagement;
	@Autowired
	protected AuthLevelmanagement authLevelManagement;
	@Autowired
	protected Configurationmanagement cfgManagement;
	
	public void setServletContext(ServletContext arg0) {
		context = arg0;
	}
	
	protected class UploadInfo {
		MultipartFile file;
		Long userId;
		String sid;
		String publicSID;
		String filename;
	}
	
    protected UploadInfo validate(HttpServletRequest request, boolean admin) throws ServletException {
    	UploadInfo info = new UploadInfo();
		log.debug("Starting validate");
		try {
			String sid = request.getParameter("sid");
			if (sid == null) {
				throw new ServletException("SID Missing");
			}
			info.sid = sid;
			log.debug("sid: " + sid);

			Long userId = sessionManagement.checkSession(sid);
			Long userLevel = userManagement.getUserLevelByID(userId);
			log.debug("userId = " + userId + ", userLevel = " + userLevel);
			info.userId = userId;

			if ((admin && !authLevelManagement.checkAdminLevel(userLevel))
					|| (!admin && userLevel <= 0)) {
				throw new ServletException("Insufficient permissions "
						+ userLevel);
			}

			String publicSID = request.getParameter("publicSID");
			if (publicSID == null) {
				// Always ask for Public SID
				throw new ServletException("Missing publicSID");
			}
			log.debug("publicSID: " + publicSID);
			info.publicSID= publicSID;

			MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
			MultipartFile multipartFile = multipartRequest.getFile("Filedata");
			//FIXME encoding HACK
			info.filename = new String (multipartFile.getOriginalFilename().getBytes ("iso-8859-1"), "UTF-8");
			long fileSize = multipartFile.getSize();
			long maxSize = ImportHelper.getMaxUploadSize(cfgManagement);
			log.debug("uploading " + fileSize + " bytes");
			if (fileSize > maxSize) {
				throw new ServletException("Maximum upload size: " + maxSize + " exceeded: " + fileSize);
			}
			info.file = multipartFile;
		} catch (ServletException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception during upload: ", e);
			throw new ServletException(e);
		}
		return info;
    }
}
