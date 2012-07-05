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

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Hex;
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
import org.openmeetings.utils.OmFileHelper;
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
				return context.getBean(Sessionmanagement.class);
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
				return context.getBean(Configurationmanagement.class);
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
				return context.getBean(Fieldmanagment.class);
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
			if (users_id == 0) {
				//checkSession will return 0 in case of invalid session
				throw new Exception("Request from invalid user " + users_id);
			}
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
				//libs
				StringBuilder libs = new StringBuilder();
				File screenShareDir = OmFileHelper.getScreenSharingDir();
				for (File jar : screenShareDir.listFiles(new FileFilter() {
					public boolean accept(File pathname) {
						return pathname.getName().endsWith(".jar");
					}
				})) {
					libs.append("\t\t<jar href=\"").append(jar.getName()).append("\"/>\n");
				}
				addKeystore(ctx);
				ctx.put("LIBRARIES", libs);
				log.debug("RTMP Sharer labels :: " + label_sharer);

				codebase = baseURL + OmFileHelper.SCREENSHARING_DIR;

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

				//invited guest does not have valid user_id (have user_id == -1)
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

	private StringBuilder addArgument(StringBuilder sb, Object arg) {
		return sb.append("\t\t<argument>").append(arg).append("</argument>\n");
	}
	
	private void addKeystore(Context ctx) {
		log.debug("RTMP Sharer Keystore :: start");
		StringBuilder sb = new StringBuilder();
		FileInputStream fis = null;
		try {
			File conf = new File(OmFileHelper.getRootDir(), "conf");

			File keyStore = new File(conf, "keystore.screen");
			if (keyStore.exists()) {
				Properties red5Props = new Properties();
				red5Props.load(new FileInputStream(new File(conf, "red5.properties")));
				
				byte keyBytes[] = new byte[(int)keyStore.length()];
				fis = new FileInputStream(keyStore);
				fis.read(keyBytes);
				
				sb = addArgument(addArgument(sb, Hex.encodeHexString(keyBytes)), red5Props.getProperty("rtmps.keystorepass"));
				
				/*
				KeyStore ksIn = KeyStore.getInstance(KeyStore.getDefaultType());
				ksIn.load(new FileInputStream(keyStore), red5Props.getProperty("rtmps.keystorepass").toCharArray());
				ByteArrayInputStream bin = new ByteArrayInputStream()
				
				byte fileContent[] = new byte[(int)file.length()];
				sb = addArgument(sb, Object arg)
				ctx.put("$KEYSTORE", users_id);
				/*
				KeyStore ksOut = KeyStore.getInstance(KeyStore.getDefaultType());
				for (Certificate cert : ksIn.getCertificateChain("red5")) {
					PublicKey pub = cert.getPublicKey();
					TrustedCertificateEntry tce = new TrustedCertificateEntry(cert);
					tce.
				}
				*/
			}
		} catch (Exception e) {
			//no op
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					// no op
				}
			}
		}
		ctx.put("KEYSTORE", sb);
	}
}
