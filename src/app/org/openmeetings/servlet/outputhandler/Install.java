package org.openmeetings.servlet.outputhandler;

import java.util.LinkedHashMap;
import java.io.File;
import java.util.Iterator;
import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context; 
import org.apache.velocity.tools.view.servlet.VelocityViewServlet;

import org.slf4j.Logger;
import org.red5.logging.Red5LoggerFactory;
import org.openmeetings.app.documents.InstallationDocumentHandler;
import org.openmeetings.app.installation.ImportInitvalues;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;

public class Install extends VelocityViewServlet {

	private static final Logger log = Red5LoggerFactory.getLogger(DownloadHandler.class, "openmeetings");

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public Template handleRequest(HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, Context ctx) throws ServletException,
			IOException {

		try {
			
			String command = httpServletRequest.getParameter("command");
			
			String lang = httpServletRequest.getParameter("lang");
			if (lang == null) lang = "EN";

			String working_dir = getServletContext().getRealPath("/")+ScopeApplicationAdapter.configDirName+File.separatorChar;
			
			if (command == null){
				log.error("command equals null");
				
				File installerFile = new File(working_dir+InstallationDocumentHandler.installFileName);
				
				if (!installerFile.exists()){
					
					File installerdir = new File(working_dir);
					
					log.error("bb "+installerFile);
					log.error("bb "+working_dir+InstallationDocumentHandler.installFileName);
					
					boolean b = installerdir.canWrite();
					
					if (!b) {
						//File could not be created so throw an error
						ctx.put("error", "Could not Create File, Permission set? ");
						ctx.put("path", working_dir);
						return getVelocityEngine().getTemplate("install_error_"+lang+".vm");
					} else {
						InstallationDocumentHandler.getInstance().createDocument(working_dir+InstallationDocumentHandler.installFileName,0);
						//File has been created so follow first step of Installation					
						return getVelocityEngine().getTemplate("install_welcome_"+lang+".vm");
					}
					
				} else {
					int i = InstallationDocumentHandler.getInstance().getCurrentStepNumber(working_dir);
					if (i == 0){
						String filePath = getServletContext().getRealPath("/")+ImportInitvalues.languageFolderName;
						LinkedHashMap<Integer,LinkedHashMap<String,Object>> allLanguagesAll = ImportInitvalues.getInstance().getLanguageFiles(filePath);
						LinkedHashMap<Integer,String> allLanguages = new LinkedHashMap<Integer,String>();
						for (Iterator<Integer> iter=allLanguagesAll.keySet().iterator();iter.hasNext();) {
							Integer key = iter.next();
							String langName = (String) allLanguagesAll.get(key).get("name");
							allLanguages.put(key, langName);
						}
						
						LinkedHashMap<String,String> allFonts = new LinkedHashMap<String,String>();
						allFonts.put("TimesNewRoman", "TimesNewRoman");
						allFonts.put("Verdana", "Verdana");
						allFonts.put("Arial", "Arial");
							
						Template tpl = super.getTemplate ("install_step1_"+lang+".vm");
						ctx.put("allLanguages", allLanguages);
						ctx.put("allFonts", allFonts);
						StringWriter writer = new StringWriter(); 
						tpl.merge(ctx, writer);
						
						return tpl;
					} else {
						return getVelocityEngine().getTemplate("install_step2_"+lang+".vm");
					}
				}
				
			} else if (command.equals("step1")) {
				
				int i = InstallationDocumentHandler.getInstance().getCurrentStepNumber(working_dir);
				if (i == 0){
					
					log.error("do init installation");
					
					//update to next step
					//InstallationDocumentHandler.getInstance().createDocument(working_dir+InstallationDocumentHandler.installFileName,1);
					
					String filePath = getServletContext().getRealPath("/")+ImportInitvalues.languageFolderName;
					LinkedHashMap<Integer,LinkedHashMap<String,Object>> allLanguagesAll = ImportInitvalues.getInstance().getLanguageFiles(filePath);
					LinkedHashMap<Integer,String> allLanguages = new LinkedHashMap<Integer,String>();
					for (Iterator<Integer> iter=allLanguagesAll.keySet().iterator();iter.hasNext();) {
						Integer key = iter.next();
						String langName = (String) allLanguagesAll.get(key).get("name");
						allLanguages.put(key, langName);
					}
					
					LinkedHashMap<String,String> allFonts = new LinkedHashMap<String,String>();
					allFonts.put("TimesNewRoman", "TimesNewRoman");
					allFonts.put("Verdana", "Verdana");
					allFonts.put("Arial", "Arial");
						
					Template tpl = super.getTemplate ("install_step1_"+lang+".vm");
					ctx.put("allLanguages", allLanguages);
					ctx.put("allFonts", allFonts);
					StringWriter writer = new StringWriter(); 
					tpl.merge(ctx, writer);
					
					return tpl;
					
				} else {
					ctx.put("error", "This Step of the installation has already been done. continue with step 2 <A HREF='?command=step2'>continue with step 2</A>");
					return getVelocityEngine().getTemplate("install_exception_"+lang+".vm");
				}
				
			} else if (command.equals("step2")) {
				
				int i = InstallationDocumentHandler.getInstance().getCurrentStepNumber(working_dir);
				if (i == 0){
					
					log.debug("do init installation");
					
					String username = httpServletRequest.getParameter("username");
					String userpass = httpServletRequest.getParameter("userpass");
					String useremail = httpServletRequest.getParameter("useremail");
					String orgname = httpServletRequest.getParameter("orgname");
					String configdefault = httpServletRequest.getParameter("configdefault");
					String configreferer = httpServletRequest.getParameter("configreferer");
					String configsmtp = httpServletRequest.getParameter("configsmtp");
					String configsmtpport = httpServletRequest.getParameter("configsmtpport");
					String configmailuser = httpServletRequest.getParameter("configmailuser");
					String configmailpass = httpServletRequest.getParameter("configmailpass");
					String configdefaultLang = httpServletRequest.getParameter("configdefaultLang");
					String swf_path = httpServletRequest.getParameter("swftools_path");
					String im_path = httpServletRequest.getParameter("imagemagick_path");
					String sendEmailAtRegister = httpServletRequest.getParameter("sendEmailAtRegister");
					String sendEmailWithVerficationCode = httpServletRequest.getParameter("sendEmailWithVerficationCode");
					
					String default_export_font = httpServletRequest.getParameter("default_export_font");
					
					String crypt_ClassName = httpServletRequest.getParameter("crypt_ClassName");
					
					String ldap_auth_path = httpServletRequest.getParameter("ldap_config_path");
					String screen_viewer = httpServletRequest.getParameter("screen_viewer");
					
					log.error("step 0+ start init with values. "+username+" ***** "+useremail+" "+orgname+" "+configdefault+" "+configreferer+" "+
						configsmtp+" "+configmailuser+" "+configmailpass+" "+configdefaultLang + " " +
						swf_path+" "+im_path + " " + ldap_auth_path + " " + screen_viewer);
					
					String filePath = getServletContext().getRealPath("/")+ImportInitvalues.languageFolderName;
					
					String url_feed =  "http://groups.google.com/group/openmeetings-dev/feed/atom_v1_0_msgs.xml";
					String url_feed2 =  "http://groups.google.com/group/openmeetings-user/feed/atom_v1_0_msgs.xml";
						
					ImportInitvalues.getInstance().loadInitLanguages(filePath);
					ImportInitvalues.getInstance().loadMainMenu();
					ImportInitvalues.getInstance().loadErrorMappingsFromXML(filePath);	
					ImportInitvalues.getInstance().loadSalutations();
					ImportInitvalues.getInstance().loadConfiguration(crypt_ClassName, configdefault, configsmtp, configsmtpport, 
									configreferer, configmailuser, configmailpass, configdefaultLang, 
									swf_path, im_path, url_feed, url_feed2,
									sendEmailAtRegister, sendEmailWithVerficationCode, default_export_font, ldap_auth_path, screen_viewer);
					ImportInitvalues.getInstance().loadInitUserAndOrganisation(username, userpass, useremail, orgname);
					ImportInitvalues.getInstance().loadDefaultRooms();
					
					// AppointMent Categories
					ImportInitvalues.getInstance().loadInitAppointmentCategories();
					
					// Appointment Remindertypes
					ImportInitvalues.getInstance().loadInitAppointmentReminderTypes();
					
					//update to next step
					log.error("add level to install file");
					InstallationDocumentHandler.getInstance().createDocument(working_dir+InstallationDocumentHandler.installFileName,1);
					
					//return getVelocityEngine().getTemplate("install_complete_"+lang+".vm");
					return getVelocityEngine().getTemplate("install_step2_"+lang+".vm");
				} else {
					ctx.put("error", "This Step of the installation has already been done. continue with step 2 <A HREF='?command=step2'>continue with step 2</A>");
					return getVelocityEngine().getTemplate("install_exception_"+lang+".vm");
				}
				
			} else if (command.equals("step")){
				
				int i = InstallationDocumentHandler.getInstance().getCurrentStepNumber(working_dir);
				if (i == 0){
					
				}
				
			}
			
			
		} catch (IOException err) {
			log.error("Install: ",err);			
		} catch (Exception err2) {
			log.error("Install: ",err2);		
		}
		
		return null;
	}

}
