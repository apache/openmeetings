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

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openmeetings.app.OpenmeetingsVariables;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * 
 * @author sebastianwagner
 * 
 */
public class BackupExportFacade extends HttpServlet {

	private static final long serialVersionUID = -928315730609302260L;

	private static final Logger log = Red5LoggerFactory.getLogger(
			BackupExportFacade.class, OpenmeetingsVariables.webAppRootKey);

	private BackupExport getBackupExport() {
		try {
			if (!ScopeApplicationAdapter.initComplete) {
				return null;
			}
			ApplicationContext context = WebApplicationContextUtils
					.getWebApplicationContext(getServletContext());
			return (BackupExport) context.getBean("backupExport");
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

			if (getBackupExport() == null) {
				OutputStream out = httpServletResponse.getOutputStream();

				String msg = "Server is not booted yet";

				out.write(msg.getBytes());

				out.flush();
				out.close();

				return;
			}

			getBackupExport().service(httpServletRequest, httpServletResponse,
					getServletContext());

		} catch (Exception er) {
			log.error("ERROR ", er);
			log.debug("Error exporting: " + er);
			er.printStackTrace();
		}
	}

}
