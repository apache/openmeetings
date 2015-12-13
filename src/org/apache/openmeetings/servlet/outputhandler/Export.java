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

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.data.basic.AuthLevelUtil;
import org.apache.openmeetings.data.basic.SessiondataDao;
import org.apache.openmeetings.data.user.OrganisationManager;
import org.apache.openmeetings.data.user.UserManager;
import org.apache.openmeetings.data.user.dao.UsersDao;
import org.apache.openmeetings.persistence.beans.domain.Organisation;
import org.apache.openmeetings.persistence.beans.user.User;
import org.apache.openmeetings.servlet.BaseHttpServlet;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

/**
 * 
 * @author sebastianwagner
 * 
 */
public class Export extends BaseHttpServlet {
	
	private static final long serialVersionUID = 8527093674786692472L;
	
	private static final Logger log = Red5LoggerFactory.getLogger(Export.class,
			OpenmeetingsVariables.webAppRootKey);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest
	 * , javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void service(HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) throws ServletException,
			IOException {

		try {

			if (getBean(UserManager.class) == null || getBean(SessiondataDao.class) == null
					|| getBean(UsersDao.class) == null) {
				return;
			}

			String sid = httpServletRequest.getParameter("sid");
			if (sid == null) {
				sid = "default";
			}
			System.out.println("sid: " + sid);

			Long users_id = getBean(SessiondataDao.class).checkSession(sid);
			Long user_level = getBean(UserManager.class).getUserLevelByID(users_id);

			System.out.println("users_id: " + users_id);
			System.out.println("user_level: " + user_level);

			// if (user_level!=null && user_level > 0) {
			if (getBean(AuthLevelUtil.class).checkUserLevel(user_level)) {

				String moduleName = httpServletRequest
						.getParameter("moduleName");
				if (moduleName == null) {
					moduleName = "moduleName";
				}
				System.out.println("moduleName: " + moduleName);

				if (moduleName.equals("users")
						|| moduleName.equals("userorganisations")) {
					String organisation = httpServletRequest
							.getParameter("organisation");
					if (organisation == null) {
						organisation = "0";
					}
					Long organisation_id = Long.valueOf(organisation)
							.longValue();
					System.out.println("organisation_id: " + organisation_id);

					List<User> uList = null;
					String downloadName = "users";
					if (moduleName.equals("userorganisations")) {
						Organisation orga = getBean(OrganisationManager.class)
								.getOrganisationById(organisation_id);
						downloadName += "_" + orga.getName();
						uList = getBean(OrganisationManager.class)
								.getUsersByOrganisationId(organisation_id);
					} else {
						uList = getBean(UsersDao.class).getAllUsers();
					}

					if (uList != null) {
						httpServletResponse.reset();
						httpServletResponse.resetBuffer();
						OutputStream out = httpServletResponse
								.getOutputStream();
						httpServletResponse
								.setContentType("APPLICATION/OCTET-STREAM");
						httpServletResponse.setHeader("Content-Disposition",
								"attachment; filename=\"" + downloadName
										+ ".xml\"");
						getBean(BackupExport.class).exportUsers(out, uList);

						out.flush();
						out.close();
					}

				}
			} else {
				System.out
						.println("ERROR LangExport: not authorized FileDownload "
								+ (new Date()));
			}
		} catch (Exception er) {
			log.error("ERROR ", er);
			System.out.println("Error exporting: " + er);
			er.printStackTrace();
		}
	}
}
