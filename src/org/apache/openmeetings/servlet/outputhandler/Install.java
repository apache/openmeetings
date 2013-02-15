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
import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.data.basic.dao.ConfigurationDao;
import org.apache.openmeetings.documents.InstallationDocumentHandler;
import org.apache.openmeetings.installation.ImportInitvalues;
import org.apache.openmeetings.installation.InstallationConfig;
import org.apache.openmeetings.persistence.beans.basic.OmTimeZone;
import org.apache.openmeetings.servlet.BaseVelocityViewServlet;
import org.apache.openmeetings.servlet.ServerNotInitializedException;
import org.apache.openmeetings.utils.ImportHelper;
import org.apache.openmeetings.utils.OmFileHelper;
import org.apache.velocity.Template;
import org.apache.velocity.context.Context;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class Install extends BaseVelocityViewServlet {
	
	private static final long serialVersionUID = 3684381243236013771L;

	private static final Logger log = Red5LoggerFactory.getLogger(
			Install.class, OpenmeetingsVariables.webAppRootKey);

	private Template getStep2Template(HttpServletRequest httpServletRequest, Context ctx, String lang) throws Exception {
		String header = httpServletRequest.getHeader("Accept-Language");
		String[] headerList = header != null ? header.split(",") : new String[0];
		String headCode = headerList.length > 0 ? headerList[0] : "en";
		
		LinkedHashMap<Integer, LinkedHashMap<String, Object>> allLanguagesAll = getBean(ImportInitvalues.class)
				.getLanguageFiles();
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

		List<OmTimeZone> omTimeZoneList = getBean(ImportInitvalues.class)
				.getTimeZones();

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
	public Template handleRequest(HttpServletRequest request,
			HttpServletResponse response, Context ctx) {

		try {
			ctx.put("APP_ROOT", OpenmeetingsVariables.webAppRootKey);
			ctx.put("APP_NAME", getBean(ConfigurationDao.class).getAppName());
			String command = request.getParameter("command");

			String lang = request.getParameter("lang");
			if (lang == null)
				lang = "EN";

			File installerFile = OmFileHelper.getInstallFile();

			if (command == null || !installerFile.exists()) {
				log.debug("command equals null");

				if (!installerFile.exists()) {
					File confDir = OmFileHelper.getConfDir();
					boolean b = confDir.canWrite();

					if (!b) {
						// File could not be created so throw an error
						ctx.put("error",
								"Could not Create File, Permission set? ");
						ctx.put("path", confDir.getCanonicalPath());
						return getVelocityView().getVelocityEngine()
								.getTemplate("install_error_" + lang + ".vm");
					} else {
						InstallationDocumentHandler.createDocument(0);
						// File has been created so follow first step of
						// Installation
						return getVelocityView().getVelocityEngine()
								.getTemplate("install_welcome_" + lang + ".vm");
					}

				} else {
					int i = InstallationDocumentHandler.getCurrentStepNumber();
					if (i == 0) {
						return getStep2Template(request, ctx, lang);
					} else {
						return getVelocityView().getVelocityEngine()
								.getTemplate("install_step2_" + lang + ".vm");
					}
				}

			} else if (command.equals("step1")) {

				int i = InstallationDocumentHandler.getCurrentStepNumber();
				if (i == 0) {
					return getStep2Template(request, ctx, lang);
				} else {
					ctx.put("error",
							"This Step of the installation has already been done. continue with step 2 <A HREF='?command=step2'>continue with step 2</A>");
					return getVelocityView().getVelocityEngine().getTemplate(
							"install_exception_" + lang + ".vm");
				}

			} else if (command.equals("step2")) {

				int i = InstallationDocumentHandler.getCurrentStepNumber();
				if (i == 0) {

					log.debug("do init installation");

					String username = request.getParameter("username");
					String userpass = request.getParameter("userpass");
					String useremail = request.getParameter("useremail");
					String orgname = request.getParameter("orgname");
					InstallationConfig cfg = new InstallationConfig();
					cfg.allowFrontendRegister = request.getParameter("configdefault");

					cfg.mailReferer = request.getParameter("configreferer");
					cfg.smtpServer = request.getParameter("configsmtp");
					cfg.smtpPort = request.getParameter("configsmtpport");
					cfg.mailAuthName = request.getParameter("configmailuser");
					cfg.mailAuthPass = request.getParameter("configmailpass");
					cfg.mailUseTls = request.getParameter("mailusetls");
					cfg.replyToOrganizer = request.getParameter("replyToOrganizer");

					cfg.defaultLangId = request.getParameter("configdefaultLang");
					cfg.swfZoom = request.getParameter("swftools_zoom");
					cfg.swfJpegQuality = request.getParameter("swftools_jpegquality");
					cfg.swfPath = request.getParameter("swftools_path");
					cfg.imageMagicPath = request.getParameter("imagemagick_path");
					cfg.sendEmailAtRegister = request.getParameter("sendEmailAtRegister");
					cfg.sendEmailWithVerficationCode = request.getParameter("sendEmailWithVerficationCode");
					cfg.createDefaultRooms = request.getParameter("createDefaultRooms");

					cfg.defaultExportFont = request.getParameter("default_export_font");

					cfg.cryptClassName = request.getParameter("crypt_ClassName");

					cfg.ffmpegPath = request.getParameter("ffmpeg_path");

					cfg.soxPath = request.getParameter("sox_path");

                    // red5sip integration config
                    cfg.red5SipEnable = request.getParameter("red5sip_enable");
                    cfg.red5SipRoomPrefix = request.getParameter("red5sip_room_prefix");
                    cfg.red5SipExtenContext = request.getParameter("red5sip_exten_context");

					String timeZone = request.getParameter("timeZone");
					cfg.ical_timeZone = timeZone;
					
					cfg.jodPath = request.getParameter("jod_path");

					log.debug("step 0+ start init with values. " + username
							+ " ***** " + useremail + " " + orgname + " "
							+ cfg);

					cfg.urlFeed = getServletContext().getInitParameter(
							"url_feed");
					cfg.urlFeed2 = getServletContext().getInitParameter(
							"url_feed2");
					
					getBean(ImportInitvalues.class).loadAll(cfg, username,
							userpass, useremail, orgname, timeZone, false);

					// update to next step
					log.debug("add level to install file");
					InstallationDocumentHandler.createDocument(1);

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

				int i = InstallationDocumentHandler.getCurrentStepNumber();
				if (i == 0) {

				}

			}
		} catch (ServerNotInitializedException err) {
			return getBooting();
		} catch (Exception err2) {
			log.error("Install: ", err2);
		}

		return null;
	}

}
