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
import java.io.UnsupportedEncodingException;
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

		LinkedHashMap<String, String> allTimeZones = new LinkedHashMap<String, String>();
		List<OmTimeZone> omTimeZoneList = getImportInitvalues()
				.getTimeZones(filePath);
		log.debug("omTimeZoneList :: " + omTimeZoneList.size());
		for (OmTimeZone omTimeZone : omTimeZoneList) {
			String labelName = omTimeZone.getJname() + " ("
					+ omTimeZone.getLabel() + ")";
			log.debug("labelName :: " + labelName);
			allTimeZones.put(omTimeZone.getJname(), labelName);
		}

		Template tpl = super.getTemplate("install_step1_"
				+ lang + ".vm");
		ctx.put("allLanguages", allLanguages);
		ctx.put("allFonts", allFonts);
		ctx.put("allTimeZones", allTimeZones);
		StringWriter writer = new StringWriter();
		tpl.merge(ctx, writer);

		return tpl;
	}
	
	private String encodeUTF8(HttpServletRequest httpServletRequest, String param) 
							throws UnsupportedEncodingException {
		return new String(httpServletRequest
				.getParameter(param).getBytes("8859_1"), "UTF-8");
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

					String username = encodeUTF8(httpServletRequest, "username");
					String userpass = encodeUTF8(httpServletRequest, "userpass");
					String useremail = encodeUTF8(httpServletRequest,"useremail");
					String orgname = encodeUTF8(httpServletRequest,"orgname");
					InstallationConfig cfg = new InstallationConfig();
					cfg.allowFrontendRegister = encodeUTF8(httpServletRequest,"configdefault");

					cfg.mailReferer = encodeUTF8(httpServletRequest,"configreferer");
					cfg.smtpServer = encodeUTF8(httpServletRequest,"configsmtp");
					cfg.smtpPort = encodeUTF8(httpServletRequest,"configsmtpport");
					cfg.mailAuthName = encodeUTF8(httpServletRequest,"configmailuser");
					cfg.mailAuthPass = encodeUTF8(httpServletRequest,"configmailpass");
					cfg.mailUseTls = encodeUTF8(httpServletRequest,"mailusetls");
					cfg.replyToOrganizer = httpServletRequest.getParameter("replyToOrganizer");

					cfg.defaultLangId = encodeUTF8(httpServletRequest,"configdefaultLang");
					cfg.swfPath = encodeUTF8(httpServletRequest,"swftools_path");
					cfg.imageMagicPath = encodeUTF8(httpServletRequest,"imagemagick_path");
					cfg.sendEmailAtRegister = encodeUTF8(httpServletRequest,"sendEmailAtRegister");
					cfg.sendEmailWithVerficationCode = encodeUTF8(httpServletRequest,"sendEmailWithVerficationCode");
					cfg.createDefaultRooms = encodeUTF8(httpServletRequest,"createDefaultRooms");

					cfg.defaultExportFont = encodeUTF8(httpServletRequest,"default_export_font");

					cfg.cryptClassName = encodeUTF8(httpServletRequest,"crypt_ClassName");

					cfg.ffmpegPath = encodeUTF8(httpServletRequest,"ffmpeg_path");

					cfg.soxPath = encodeUTF8(httpServletRequest,"sox_path");

					cfg.screenViewer = encodeUTF8(httpServletRequest,"screen_viewer");

                    // red5sip integration config
                    cfg.red5SipEnable = encodeUTF8(httpServletRequest,"red5sip_enable");
                    cfg.red5SipRoomPrefix = encodeUTF8(httpServletRequest,"red5sip_room_prefix");
                    cfg.red5SipExtenContext = encodeUTF8(httpServletRequest,"red5sip_exten_context");

					// SIP Applet Configuration
					cfg.sipEnable = encodeUTF8(httpServletRequest,"sip_enable");
					cfg.sipRealm = encodeUTF8(httpServletRequest,"sip_realm");
					cfg.sipPort = encodeUTF8(httpServletRequest,"sip_port");
					cfg.sipProxyName = encodeUTF8(httpServletRequest,"sip_proxyname");
					cfg.sipTunnel = encodeUTF8(httpServletRequest,"sip_tunnel");
					cfg.sipCodebase = encodeUTF8(httpServletRequest,"sip_codebase");
					cfg.sipForceTunnel = encodeUTF8(httpServletRequest,"sip_forcetunnel");

					// OpenXG / OpenSIPg Configuration
					cfg.sipOpenxgEnable = encodeUTF8(httpServletRequest,"sip_openxg_enable");
					cfg.openxgWrapperUrl = encodeUTF8(httpServletRequest,"openxg_wrapper_url");
					cfg.openxgClientId = encodeUTF8(httpServletRequest,"openxg_client_id");
					cfg.openxgClientSecret = encodeUTF8(httpServletRequest,"openxg_client_secret");
					cfg.openxgClientDomain = encodeUTF8(httpServletRequest,"openxg_client_domain");
					cfg.openxgCommunityCode = encodeUTF8(httpServletRequest,"openxg_community_code");
					cfg.openxgLanguageCode = encodeUTF8(httpServletRequest,"openxg_language_code");
					cfg.openxgAdminId = encodeUTF8(httpServletRequest,"openxg_adminid");

					// SIP Phone Range Configuration
					cfg.sipLanguagePhoneCode = encodeUTF8(httpServletRequest,"sip_language_phonecode");
					cfg.sipPhoneRangeStart = encodeUTF8(httpServletRequest,"sip_phonerange_start");
					cfg.sipPhoneRange = encodeUTF8(httpServletRequest,"sip_phonerange");

					String timeZone = encodeUTF8(httpServletRequest,"timeZone");
					
					cfg.jodPath = encodeUTF8(httpServletRequest,"jod_path");

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
