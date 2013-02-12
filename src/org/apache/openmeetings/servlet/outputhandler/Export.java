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
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.data.basic.AuthLevelUtil;
import org.apache.openmeetings.data.basic.SessiondataDao;
import org.apache.openmeetings.data.user.Organisationmanagement;
import org.apache.openmeetings.data.user.Usermanagement;
import org.apache.openmeetings.data.user.dao.UsersDao;
import org.apache.openmeetings.persistence.beans.domain.Organisation;
import org.apache.openmeetings.persistence.beans.user.User;
import org.apache.openmeetings.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * 
 * @author sebastianwagner
 * 
 */
public class Export extends HttpServlet {
	private static final long serialVersionUID = 8527093674786692472L;
	private static final Logger log = Red5LoggerFactory.getLogger(Export.class,
			OpenmeetingsVariables.webAppRootKey);

	private SessiondataDao getSessionManagement() {
		try {
			if (ScopeApplicationAdapter.initComplete) {
				ApplicationContext context = WebApplicationContextUtils
						.getWebApplicationContext(getServletContext());
				return context.getBean("sessionManagement", SessiondataDao.class);
			}
		} catch (Exception err) {
			log.error("[getSessionManagement]", err);
		}
		return null;
	}

	private Usermanagement getUserManagement() {
		try {
			if (ScopeApplicationAdapter.initComplete) {
				ApplicationContext context = WebApplicationContextUtils
						.getWebApplicationContext(getServletContext());
				return context.getBean("userManagement", Usermanagement.class);
			}
		} catch (Exception err) {
			log.error("[getUserManagement]", err);
		}
		return null;
	}

	private Organisationmanagement getOrganisationmanagement() {
		try {
			if (ScopeApplicationAdapter.initComplete) {
				ApplicationContext context = WebApplicationContextUtils
						.getWebApplicationContext(getServletContext());
				return context.getBean("organisationmanagement", Organisationmanagement.class);
			}
		} catch (Exception err) {
			log.error("[getOrganisationmanagement]", err);
		}
		return null;
	}

	private UsersDao getUsersDao() {
		try {
			if (ScopeApplicationAdapter.initComplete) {
				ApplicationContext context = WebApplicationContextUtils
						.getWebApplicationContext(getServletContext());
				return context.getBean("usersDao", UsersDao.class);
			}
		} catch (Exception err) {
			log.error("[getUsersDao]", err);
		}
		return null;
	}

	private AuthLevelUtil getAuthLevelManagement() {
		try {
			if (ScopeApplicationAdapter.initComplete) {
				ApplicationContext context = WebApplicationContextUtils
						.getWebApplicationContext(getServletContext());
				return context.getBean("authLevelManagement", AuthLevelUtil.class);
			}
		} catch (Exception err) {
			log.error("[getAuthLevelManagement]", err);
		}
		return null;
	}

	private BackupExport getBackupExport() {
		try {
			if (ScopeApplicationAdapter.initComplete) {
				ApplicationContext context = WebApplicationContextUtils
						.getWebApplicationContext(getServletContext());
				return context.getBean("backupExport", BackupExport.class);
			}
		} catch (Exception err) {
			log.error("[getBackupExport]", err);
		}
		return null;
	}

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

			if (getUserManagement() == null || getSessionManagement() == null
					|| getUsersDao() == null) {
				return;
			}

			String sid = httpServletRequest.getParameter("sid");
			if (sid == null) {
				sid = "default";
			}
			System.out.println("sid: " + sid);

			Long users_id = getSessionManagement().checkSession(sid);
			Long user_level = getUserManagement().getUserLevelByID(users_id);

			System.out.println("users_id: " + users_id);
			System.out.println("user_level: " + user_level);

			// if (user_level!=null && user_level > 0) {
			if (getAuthLevelManagement().checkUserLevel(user_level)) {

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
						Organisation orga = getOrganisationmanagement()
								.getOrganisationById(organisation_id);
						downloadName += "_" + orga.getName();
						uList = getOrganisationmanagement()
								.getUsersByOrganisationId(organisation_id);
					} else {
						uList = getUsersDao().getAllUsers();
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
						// httpServletResponse.setHeader("Content-Length", ""+
						// rf.length());
						getBackupExport().exportUsers(out, uList);

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
