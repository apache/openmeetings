package org.openmeetings.servlet.outputhandler;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.Template;
import org.apache.velocity.context.Context;
import org.apache.velocity.tools.view.VelocityViewServlet;
import org.openmeetings.app.documents.InstallationDocumentHandler;
import org.openmeetings.app.installation.ImportInitvalues;
import org.openmeetings.app.persistence.beans.basic.OmTimeZone;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class Install extends VelocityViewServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3684381243236013771L;

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
			Install.class, ScopeApplicationAdapter.webAppRootKey);

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

			if (getImportInitvalues() == null) {
				return getVelocityView().getVelocityEngine().getTemplate(
						"booting_install.vm");
			}

			ctx.put("APPLICATION_NAME", getServletContext()
					.getServletContextName());
			ctx.put("APPLICATION_ROOT",
					getServletContext().getInitParameter("webAppRootKey"));
			String command = httpServletRequest.getParameter("command");

			String lang = httpServletRequest.getParameter("lang");
			if (lang == null)
				lang = "EN";

			String working_dir = getServletContext().getRealPath("/")
					+ ScopeApplicationAdapter.configDirName
					+ File.separatorChar;

			if (command == null) {
				log.debug("command equals null");

				File installerFile = new File(working_dir
						+ InstallationDocumentHandler.installFileName);

				if (!installerFile.exists()) {

					File installerdir = new File(working_dir);

					log.debug("bb " + installerFile);
					log.debug("bb " + working_dir
							+ InstallationDocumentHandler.installFileName);

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
						String filePath = getServletContext().getRealPath("/")
								+ ImportInitvalues.languageFolderName;
						LinkedHashMap<Integer, LinkedHashMap<String, Object>> allLanguagesAll = getImportInitvalues()
								.getLanguageFiles(filePath);
						LinkedHashMap<Integer, String> allLanguages = new LinkedHashMap<Integer, String>();
						for (Iterator<Integer> iter = allLanguagesAll.keySet()
								.iterator(); iter.hasNext();) {
							Integer key = iter.next();
							String langName = (String) allLanguagesAll.get(key)
									.get("name");
							allLanguages.put(key, langName);
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
					} else {
						return getVelocityView().getVelocityEngine()
								.getTemplate("install_step2_" + lang + ".vm");
					}
				}

			} else if (command.equals("step1")) {

				int i = InstallationDocumentHandler.getInstance()
						.getCurrentStepNumber(working_dir);
				if (i == 0) {

					log.debug("do init installation");

					// update to next step
					// InstallationDocumentHandler.getInstance().createDocument(working_dir+InstallationDocumentHandler.installFileName,1);

					String filePath = getServletContext().getRealPath("/")
							+ ImportInitvalues.languageFolderName;
					LinkedHashMap<Integer, LinkedHashMap<String, Object>> allLanguagesAll = getImportInitvalues()
							.getLanguageFiles(filePath);
					LinkedHashMap<Integer, String> allLanguages = new LinkedHashMap<Integer, String>();
					for (Iterator<Integer> iter = allLanguagesAll.keySet()
							.iterator(); iter.hasNext();) {
						Integer key = iter.next();
						String langName = (String) allLanguagesAll.get(key)
								.get("name");
						allLanguages.put(key, langName);
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

					Template tpl = super.getTemplate("install_step1_" + lang
							+ ".vm");
					ctx.put("allLanguages", allLanguages);
					ctx.put("allFonts", allFonts);
					ctx.put("allTimeZones", allTimeZones);
					StringWriter writer = new StringWriter();
					tpl.merge(ctx, writer);

					return tpl;

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

					String username = httpServletRequest
							.getParameter("username");
					String userpass = httpServletRequest
							.getParameter("userpass");
					String useremail = httpServletRequest
							.getParameter("useremail");
					String orgname = httpServletRequest.getParameter("orgname");
					String configdefault = httpServletRequest
							.getParameter("configdefault");

					String configreferer = httpServletRequest
							.getParameter("configreferer");
					String configsmtp = httpServletRequest
							.getParameter("configsmtp");
					String configsmtpport = httpServletRequest
							.getParameter("configsmtpport");
					String configmailuser = httpServletRequest
							.getParameter("configmailuser");
					String configmailpass = httpServletRequest
							.getParameter("configmailpass");
					String mailusetls = httpServletRequest
							.getParameter("mailusetls");

					String configdefaultLang = httpServletRequest
							.getParameter("configdefaultLang");
					String swf_path = httpServletRequest
							.getParameter("swftools_path");
					String im_path = httpServletRequest
							.getParameter("imagemagick_path");
					String sendEmailAtRegister = httpServletRequest
							.getParameter("sendEmailAtRegister");
					String sendEmailWithVerficationCode = httpServletRequest
							.getParameter("sendEmailWithVerficationCode");

					String default_export_font = httpServletRequest
							.getParameter("default_export_font");

					String crypt_ClassName = httpServletRequest
							.getParameter("crypt_ClassName");

					String ffmpeg_path = httpServletRequest
							.getParameter("ffmpeg_path");

					String sox_path = httpServletRequest
							.getParameter("sox_path");

					String screen_viewer = httpServletRequest
							.getParameter("screen_viewer");

					// SIP Applet Configuration
					String sip_enable = httpServletRequest
							.getParameter("sip_enable");
					String sip_realm = httpServletRequest
							.getParameter("sip_realm");
					String sip_port = httpServletRequest
							.getParameter("sip_port");
					String sip_proxyname = httpServletRequest
							.getParameter("sip_proxyname");
					String sip_tunnel = httpServletRequest
							.getParameter("sip_tunnel");
					String sip_codebase = httpServletRequest
							.getParameter("sip_codebase");
					String sip_forcetunnel = httpServletRequest
							.getParameter("sip_forcetunnel");

					// OpenXG / OpenSIPg Configuration
					String sip_openxg_enable = httpServletRequest
							.getParameter("sip_openxg_enable");
					String openxg_wrapper_url = httpServletRequest
							.getParameter("openxg_wrapper_url");
					String openxg_client_id = httpServletRequest
							.getParameter("openxg_client_id");
					String openxg_client_secret = httpServletRequest
							.getParameter("openxg_client_secret");
					String openxg_client_domain = httpServletRequest
							.getParameter("openxg_client_domain");
					String openxg_community_code = httpServletRequest
							.getParameter("openxg_community_code");
					String openxg_language_code = httpServletRequest
							.getParameter("openxg_language_code");
					String openxg_adminid = httpServletRequest
							.getParameter("openxg_adminid");

					// SIP Phone Range Configuration
					String sip_language_phonecode = httpServletRequest
							.getParameter("sip_language_phonecode");
					String sip_phonerange_start = httpServletRequest
							.getParameter("sip_phonerange_start");
					String sip_phonerange = httpServletRequest
							.getParameter("sip_phonerange");

					String timeZone = httpServletRequest
							.getParameter("timeZone");

					log.debug("step 0+ start init with values. " + username
							+ " ***** " + useremail + " " + orgname + " "
							+ configdefault + " " + configreferer + " "
							+ configsmtp + " " + configmailuser + " "
							+ configmailpass + " " + configdefaultLang + " "
							+ swf_path + " " + im_path + " " + screen_viewer);

					String filePath = getServletContext().getRealPath("/")
							+ ImportInitvalues.languageFolderName;

					String url_feed = getServletContext().getInitParameter(
							"url_feed");
					String url_feed2 = getServletContext().getInitParameter(
							"url_feed2");
					getImportInitvalues().loadInitLanguages(filePath);

					getImportInitvalues().loadMainMenu();

					getImportInitvalues().loadErrorMappingsFromXML(filePath);

					getImportInitvalues().loadSalutations();

					getImportInitvalues().loadConfiguration(crypt_ClassName,
							configdefault, configsmtp, configsmtpport,
							configreferer, configmailuser, configmailpass,
							mailusetls, configdefaultLang, swf_path, im_path,
							url_feed, url_feed2, sendEmailAtRegister,
							sendEmailWithVerficationCode, default_export_font,
							screen_viewer, ffmpeg_path, sox_path, sip_enable,
							sip_realm, sip_port, sip_proxyname, sip_tunnel,
							sip_codebase, sip_forcetunnel, sip_openxg_enable,
							openxg_wrapper_url, openxg_client_id,
							openxg_client_secret, openxg_client_domain,
							openxg_community_code, openxg_language_code,
							openxg_adminid, sip_language_phonecode,
							sip_phonerange_start, sip_phonerange);

					getImportInitvalues().loadInitUserAndOrganisation(username,
							userpass, useremail, orgname, timeZone);

					getImportInitvalues().loadDefaultRooms();

					// AppointMent Categories
					getImportInitvalues().loadInitAppointmentCategories();

					// Appointment Remindertypes
					getImportInitvalues().loadInitAppointmentReminderTypes();

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
