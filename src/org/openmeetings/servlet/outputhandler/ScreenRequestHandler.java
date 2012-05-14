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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.context.Context;
import org.apache.velocity.tools.view.VelocityViewServlet;
import org.openmeetings.app.OpenmeetingsVariables;
import org.openmeetings.app.data.basic.Configurationmanagement;
import org.openmeetings.app.data.basic.Fieldmanagment;
import org.openmeetings.app.data.basic.Sessionmanagement;
import org.openmeetings.app.persistence.beans.basic.Configuration;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class ScreenRequestHandler extends VelocityViewServlet {
	private static final long serialVersionUID = 2381722235536488913L;
	private static final Logger log = Red5LoggerFactory.getLogger(
			ScreenRequestHandler.class, OpenmeetingsVariables.webAppRootKey);

	public Sessionmanagement getSessionManagement() {
		try {
			if (ScopeApplicationAdapter.initComplete) {
				ApplicationContext context = WebApplicationContextUtils
						.getWebApplicationContext(getServletContext());
				return (Sessionmanagement) context.getBean("sessionManagement");
			}
		} catch (Exception err) {
			log.error("[getSessionManagement]", err);
		}
		return null;
	}

	public Configurationmanagement getCfgManagement() {
		try {
			if (ScopeApplicationAdapter.initComplete) {
				ApplicationContext context = WebApplicationContextUtils
						.getWebApplicationContext(getServletContext());
				return (Configurationmanagement) context
						.getBean("cfgManagement");
			}
		} catch (Exception err) {
			log.error("[getCfgManagement]", err);
		}
		return null;
	}

	public Fieldmanagment getFieldmanagment() {
		try {
			if (ScopeApplicationAdapter.initComplete) {
				ApplicationContext context = WebApplicationContextUtils
						.getWebApplicationContext(getServletContext());
				return (Fieldmanagment) context.getBean("fieldmanagment");
			}
		} catch (Exception err) {
			log.error("[getFieldmanagment]", err);
		}
		return null;
	}

	private enum ConnectionType {
		rtmp
		, rtmps
		, rtmpt
	}
	
	private String getLabels(Long language_id, int ... ids) {
		Fieldmanagment fieldmanagment = getFieldmanagment();
		StringBuilder result = new StringBuilder();
		boolean delim = false;
		for (int id : ids) {
			if (delim) {
				result.append(';');
			}
			result.append(fieldmanagment.getFieldByIdAndLanguage((long)id, language_id).getValue());
			delim = true;
		}
		return result.toString();
	}
	
	@Override
	public Template handleRequest(HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, Context ctx) {

		try {
			if (getSessionManagement() == null) {
				return getVelocityView().getVelocityEngine().getTemplate(
						"booting.vm");
			}

			String sid = httpServletRequest.getParameter("sid");
			if (sid == null) {
				sid = "default";
			}
			log.debug("sid: " + sid);

			Long users_id = getSessionManagement().checkSession(sid);

			String publicSID = httpServletRequest.getParameter("publicSID");
			if (publicSID == null) {
				throw new Exception("publicSID is empty: " + publicSID);
			}

			String room = httpServletRequest.getParameter("room");
			if (room == null)
				room = "default";

			String domain = httpServletRequest.getParameter("domain");
			if (domain == null) {
				throw new Exception("domain is empty: " + domain);
			}

			String languageAsString = httpServletRequest
					.getParameter("languageAsString");
			if (languageAsString == null) {
				throw new Exception("languageAsString is empty: " + domain);
			}
			Long language_id = Long.parseLong(languageAsString);

			String rtmphostlocal = httpServletRequest
					.getParameter("rtmphostlocal");
			if (rtmphostlocal == null) {
				throw new Exception("rtmphostlocal is empty: " + rtmphostlocal);
			}

			String red5httpport = httpServletRequest
					.getParameter("red5httpport");
			if (red5httpport == null) {
				throw new Exception("red5httpport is empty: " + red5httpport);
			}

			String record = httpServletRequest.getParameter("record");
			if (record == null) {
				throw new Exception("recorder is empty: ");
			}

			String httpRootKey = httpServletRequest.getParameter("httpRootKey");
			if (httpRootKey == null) {
				throw new Exception("httpRootKey is empty could not start sharer");
			}

			String baseURL = httpServletRequest.getScheme() + "://" + rtmphostlocal + ":" + red5httpport
					+ httpRootKey;

			// make a complete name out of domain(organisation) + roomname
			String roomName = domain + "_" + room;
			// trim whitespaces cause it is a directory name
			roomName = StringUtils.deleteWhitespace(roomName);

			String current_dir = getServletContext().getRealPath("/");
			log.debug("Current_dir: " + current_dir);

			// String jnlpString =
			// ScreenCastTemplate.getInstance(current_dir).getScreenTemplate(rtmphostlocal,
			// red5httpport, sid, room, domain);
			ctx.put("rtmphostlocal", rtmphostlocal); // rtmphostlocal
			ctx.put("red5httpport", red5httpport); // red5httpport

			log.debug("httpRootKey " + httpRootKey);

			String codebase = baseURL + "screen";

			ctx.put("codebase", codebase);

			String httpSharerURL = baseURL + "ScreenServlet";

			ctx.put("webAppRootKey", httpRootKey);
			ctx.put("httpSharerURL", httpSharerURL);

			ctx.put("APP_NAME", getCfgManagement().getAppName());
			ctx.put("SID", sid);
			ctx.put("ROOM", room);
			ctx.put("DOMAIN", domain);
			ctx.put("PUBLIC_SID", publicSID);
			ctx.put("RECORDER", record);

			String requestedFile = roomName + ".jnlp";
			httpServletResponse.setContentType("application/x-java-jnlp-file");
			httpServletResponse.setHeader("Content-Disposition",
					"Inline; filename=\"" + requestedFile + "\"");


			log.debug("language_id :: " + language_id);
			String label_viewer = "Viewer";
			String label_sharer = "Sharer";

			try {
				label_viewer = getLabels(language_id, 728, 729, 736, 742);

				label_sharer = getLabels(language_id
					,  730,  731,  732,  733,  734
					,  735,  737,  738,  739,  740
					,  741,  742,  844,  869,  870
					,  871,  872,  878, 1089, 1090
					, 1091, 1092, 1093, 1465, 1466
					, 1467, 1468, 1469, 1470, 1471
					, 1472, 1473, 1474, 1475, 1476
					, 1477);
			} catch (Exception e) {
				log.error("Error resolving Language labels : ", e);
			}

			ctx.put("LABELVIEWER", label_viewer);
			ctx.put("LABELSHARER", label_sharer);

			log.debug("Creating JNLP Template for TCP solution");

			try {

				log.debug("RTMP Sharer labels :: " + label_sharer);

				codebase = baseURL + "red5-screenshare";

				ConnectionType conType = ConnectionType
						.valueOf(httpServletRequest
								.getParameter("connectionType"));

				String startUpClass;
				switch (conType) {
				case rtmp:
					startUpClass = "org.openmeetings.screen.webstart.RTMPScreenShare";
					break;
				case rtmps:
					startUpClass = "org.openmeetings.screen.webstart.RTMPSScreenShare";
					break;
				case rtmpt:
					startUpClass = "org.openmeetings.screen.webstart.RTMPTScreenShare";
					break;
				default:
					throw new Exception("Unknown connection type");
				}

				String orgIdAsString = httpServletRequest
						.getParameter("organization_id");
				if (orgIdAsString == null) {
					throw new Exception(
							"orgIdAsString is empty could not start sharer");
				}

				ctx.put("organization_id", orgIdAsString);

				ctx.put("startUpClass", startUpClass);
				ctx.put("codebase", codebase);
				ctx.put("red5-host", rtmphostlocal);
				ctx.put("red5-app", OpenmeetingsVariables.webAppRootKey + "/"
						+ room);

				Configuration configuration = getCfgManagement().getConfKey(3L,
						"default.quality.screensharing");
				String default_quality_screensharing = "1";
				if (configuration != null) {
					default_quality_screensharing = configuration
							.getConf_value();
				}

				ctx.put("default_quality_screensharing",
						default_quality_screensharing);

				ctx.put("user_id", users_id);

				String port = httpServletRequest.getParameter("port");
				if (port == null) {
					throw new Exception("port is empty: ");
				}
				ctx.put("port", port);

				String allowRecording = httpServletRequest
						.getParameter("allowRecording");
				if (allowRecording == null) {
					throw new Exception("allowRecording is empty: ");
				}
				ctx.put("allowRecording", allowRecording);

			} catch (Exception e) {
				log.error("invalid configuration value for key screen_viewer!");
			}

			String template = "screenshare.vm";

			return getVelocityView().getVelocityEngine().getTemplate(template);

		} catch (Exception er) {
			log.error("[ScreenRequestHandler]", er);
			System.out.println("Error downloading: " + er);
		}
		return null;
	}

}
