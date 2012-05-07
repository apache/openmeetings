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
import java.io.IOException;
import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.Template;
import org.apache.velocity.context.Context;
import org.apache.velocity.tools.view.VelocityViewServlet;
import org.openmeetings.app.OpenmeetingsVariables;
import org.openmeetings.app.data.basic.Configurationmanagement;
import org.openmeetings.app.documents.InstallationDocumentHandler;
import org.openmeetings.app.installation.ImportInitvalues;
import org.openmeetings.app.installation.InstallationConfig;
import org.openmeetings.app.persistence.beans.basic.OmTimeZone;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.openmeetings.utils.ImportHelper;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class Install extends VelocityViewServlet {
	private static final long serialVersionUID = 3684381243236013771L;

	private Configurationmanagement getConfigurationmanagement() {
		try {
			if (!ScopeApplicationAdapter.initComplete) {
				return null;
			}
			ApplicationContext context = WebApplicationContextUtils
					.getWebApplicationContext(getServletContext());
			return (Configurationmanagement) context.getBean("cfgManagement");
		} catch (Exception err) {
			log.error("[getConfigurationmanagement]", err);
		}
		return null;
	}

	private ImportInitvalues getImportInitvalues() {
		try {
			if (!ScopeApplicationAdapter.initComplete) {
				return null;
			}
			ApplicationContext context = WebApplicationContextUtils
					.getWebApplicationContext(getServletContext());
			return (ImportInitvalues) context.getBean("importInitvalues");
		} catch (Exception err) {
			log.error("[getImportInitvalues]", err);
		}
		return null;
	}

	private static final Logger log = Red5LoggerFactory.getLogger(
			Install.class, OpenmeetingsVariables.webAppRootKey);

	private Template getStep2Template(HttpServletRequest httpServletRequest, Context ctx, String lang) throws Exception {
		String header = httpServletRequest.getHeader("Accept-Language");
		String[] headerList = header != null ? header.split(",") : new String[0];
		String headCode = headerList.length > 0 ? headerList[0] : "en";
		
		String filePath = getServletContext().getRealPath("/")
				+ ImportInitvalues.languageFolderName;
		LinkedHashMap<Integer, LinkedHashMap<String, Object>> allLanguagesAll = getImportInitvalues()
				.getLanguageFiles(filePath);
		LinkedHashMap<Integer, String> allLanguages = new LinkedHashMap<Integer, String>();
		//first iteration for preferred language
		Integer prefKey = -1;
		String prefName = null;
		for (Integer key : allLanguagesAll.keySet()) {
			String langName = (String) allLanguagesAll.get(key).get("name");
			String langCode = (String) allLanguagesAll.get(key).get("code");
			if (langCode != null) {
				if (headCode.equals(langCode)) {
					prefKey = key;
					prefName = langName;
					break;
				} else if (headCode.startsWith(langCode)) {
					prefKey = key;
					prefName = langName;
				}
			}
		}
		allLanguages.put(prefKey, prefName);
		for (Integer key : allLanguagesAll.keySet()) {
			String langName = (String) allLanguagesAll.get(key).get("name");
			if (key != prefKey) {
				allLanguages.put(key, langName);
			}
		}

		LinkedHashMap<String, String> allFonts = new LinkedHashMap<String, String>();
		allFonts.put("TimesNewRoman", "TimesNewRoman");
		allFonts.put("Verdana", "Verdana");
		allFonts.put("Arial", "Arial");

		List<OmTimeZone> omTimeZoneList = getImportInitvalues()
				.getTimeZones(filePath);

		Template tpl = super.getTemplate("install_step1_"
				+ lang + ".vm");
		ctx.put("allLanguages", allLanguages);
		ctx.put("allFonts", allFonts);
		ctx.put("allTimeZones", ImportHelper.getAllTimeZones(omTimeZoneList));
		StringWriter writer = new StringWriter();
		tpl.merge(ctx, writer);

		return tpl;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest
	 * , javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public Template handleRequest(HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, Context ctx) {

		try {
			ctx.put("APP_ROOT", OpenmeetingsVariables.webAppRootKey);

			if (getImportInitvalues() == null || getConfigurationmanagement() == null) {
				return getVelocityView().getVelocityEngine().getTemplate(
						"booting_install.vm");
			}

			ctx.put("APP_NAME", getConfigurationmanagement().getAppName());
			String command = httpServletRequest.getParameter("command");

			String lang = httpServletRequest.getParameter("lang");
			if (lang == null)
				lang = "EN";

			String working_dir = getServletContext().getRealPath("/")
					+ ScopeApplicationAdapter.configDirName
					+ File.separatorChar;

			File installerFile = new File(working_dir, InstallationDocumentHandler.installFileName);

			if (command == null || !installerFile.exists()) {
				log.debug("command equals null");

				if (!installerFile.exists()) {

					File installerdir = new File(working_dir);

					log.debug("bb " + installerFile);

					boolean b = installerdir.canWrite();

					if (!b) {
						// File could not be created so throw an error
						ctx.put("error",
								"Could not Create File, Permission set? ");
						ctx.put("path", working_dir);
						return getVelocityView().getVelocityEngine()
								.getTemplate("install_error_" + lang + ".vm");
					} else {
						InstallationDocumentHandler
								.getInstance()
								.createDocument(
										working_dir
												+ InstallationDocumentHandler.installFileName,
										0);
						// File has been created so follow first step of
						// Installation
						return getVelocityView().getVelocityEngine()
								.getTemplate("install_welcome_" + lang + ".vm");
					}

				} else {
					int i = InstallationDocumentHandler.getInstance()
							.getCurrentStepNumber(working_dir);
					if (i == 0) {
						return getStep2Template(httpServletRequest, ctx, lang);
					} else {
						return getVelocityView().getVelocityEngine()
								.getTemplate("install_step2_" + lang + ".vm");
					}
				}

			} else if (command.equals("step1")) {

				int i = InstallationDocumentHandler.getInstance()
						.getCurrentStepNumber(working_dir);
				if (i == 0) {
					return getStep2Template(httpServletRequest, ctx, lang);
				} else {
					ctx.put("error",
							"This Step of the installation has already been done. continue with step 2 <A HREF='?command=step2'>continue with step 2</A>");
					return getVelocityView().getVelocityEngine().getTemplate(
							"install_exception_" + lang + ".vm");
				}

			} else if (command.equals("step2")) {

				int i = InstallationDocumentHandler.getInstance()
						.getCurrentStepNumber(working_dir);
				if (i == 0) {

					log.debug("do init installation");

					String username = httpServletRequest.getParameter("username");
					String userpass = httpServletRequest.getParameter("userpass");
					String useremail = httpServletRequest.getParameter("useremail");
					String orgname = httpServletRequest.getParameter("orgname");
					InstallationConfig cfg = new InstallationConfig();
					cfg.allowFrontendRegister = httpServletRequest.getParameter("configdefault");

					cfg.mailReferer = httpServletRequest.getParameter("configreferer");
					cfg.smtpServer = httpServletRequest.getParameter("configsmtp");
					cfg.smtpPort = httpServletRequest.getParameter("configsmtpport");
					cfg.mailAuthName = httpServletRequest.getParameter("configmailuser");
					cfg.mailAuthPass = httpServletRequest.getParameter("configmailpass");
					cfg.mailUseTls = httpServletRequest.getParameter("mailusetls");
					cfg.replyToOrganizer = httpServletRequest.getParameter("replyToOrganizer");

					cfg.defaultLangId = httpServletRequest.getParameter("configdefaultLang");
					cfg.swfZoom = httpServletRequest.getParameter("swftools_zoom");
					cfg.swfJpegQuality = httpServletRequest.getParameter("swftools_jpegquality");
					cfg.swfPath = httpServletRequest.getParameter("swftools_path");
					cfg.imageMagicPath = httpServletRequest.getParameter("imagemagick_path");
					cfg.sendEmailAtRegister = httpServletRequest.getParameter("sendEmailAtRegister");
					cfg.sendEmailWithVerficationCode = httpServletRequest.getParameter("sendEmailWithVerficationCode");
					cfg.createDefaultRooms = httpServletRequest.getParameter("createDefaultRooms");

					cfg.defaultExportFont = httpServletRequest.getParameter("default_export_font");

					cfg.cryptClassName = httpServletRequest.getParameter("crypt_ClassName");

					cfg.ffmpegPath = httpServletRequest.getParameter("ffmpeg_path");

					cfg.soxPath = httpServletRequest.getParameter("sox_path");

					cfg.screenViewer = httpServletRequest.getParameter("screen_viewer");

                    // red5sip integration config
                    cfg.red5SipEnable = httpServletRequest.getParameter("red5sip_enable");
                    cfg.red5SipRoomPrefix = httpServletRequest.getParameter("red5sip_room_prefix");
                    cfg.red5SipExtenContext = httpServletRequest.getParameter("red5sip_exten_context");

					// SIP Applet Configuration
					cfg.sipEnable = httpServletRequest.getParameter("sip_enable");
					cfg.sipRealm = httpServletRequest.getParameter("sip_realm");
					cfg.sipPort = httpServletRequest.getParameter("sip_port");
					cfg.sipProxyName = httpServletRequest.getParameter("sip_proxyname");
					cfg.sipTunnel = httpServletRequest.getParameter("sip_tunnel");
					cfg.sipCodebase = httpServletRequest.getParameter("sip_codebase");
					cfg.sipForceTunnel = httpServletRequest.getParameter("sip_forcetunnel");

					// OpenXG / OpenSIPg Configuration
					cfg.sipOpenxgEnable = httpServletRequest.getParameter("sip_openxg_enable");
					cfg.openxgWrapperUrl = httpServletRequest.getParameter("openxg_wrapper_url");
					cfg.openxgClientId = httpServletRequest.getParameter("openxg_client_id");
					cfg.openxgClientSecret = httpServletRequest.getParameter("openxg_client_secret");
					cfg.openxgClientDomain = httpServletRequest.getParameter("openxg_client_domain");
					cfg.openxgCommunityCode = httpServletRequest.getParameter("openxg_community_code");
					cfg.openxgLanguageCode = httpServletRequest.getParameter("openxg_language_code");
					cfg.openxgAdminId = httpServletRequest.getParameter("openxg_adminid");

					// SIP Phone Range Configuration
					cfg.sipLanguagePhoneCode = httpServletRequest.getParameter("sip_language_phonecode");
					cfg.sipPhoneRangeStart = httpServletRequest.getParameter("sip_phonerange_start");
					cfg.sipPhoneRange = httpServletRequest.getParameter("sip_phonerange");

					String timeZone = httpServletRequest.getParameter("timeZone");
					cfg.ical_timeZone = timeZone;
					
					cfg.jodPath = httpServletRequest.getParameter("jod_path");

					log.debug("step 0+ start init with values. " + username
							+ " ***** " + useremail + " " + orgname + " "
							+ cfg);

					String filePath = getServletContext().getRealPath("/")
							+ ImportInitvalues.languageFolderName;

					cfg.urlFeed = getServletContext().getInitParameter(
							"url_feed");
					cfg.urlFeed2 = getServletContext().getInitParameter(
							"url_feed2");
					
					getImportInitvalues().loadAll(filePath, cfg, username,
							userpass, useremail, orgname, timeZone);

					// update to next step
					log.debug("add level to install file");
					InstallationDocumentHandler
							.getInstance()
							.createDocument(
									working_dir
											+ InstallationDocumentHandler.installFileName,
									1);

					// return
					// getVelocityEngine().getTemplate("install_complete_"+lang+".vm");
					return getVelocityView().getVelocityEngine().getTemplate(
							"install_step2_" + lang + ".vm");
				} else {
					ctx.put("error",
							"This Step of the installation has already been done. continue with step 2 <A HREF='?command=step2'>continue with step 2</A>");
					return getVelocityView().getVelocityEngine().getTemplate(
							"install_exception_" + lang + ".vm");
				}

			} else if (command.equals("step")) {

				int i = InstallationDocumentHandler.getInstance()
						.getCurrentStepNumber(working_dir);
				if (i == 0) {

				}

			}

		} catch (IOException err) {
			log.error("Install: ", err);
		} catch (Exception err2) {
			log.error("Install: ", err2);
		}

		return null;
	}

}
