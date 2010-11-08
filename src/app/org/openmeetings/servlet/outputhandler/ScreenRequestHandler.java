package org.openmeetings.servlet.outputhandler;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.Template;
import org.apache.velocity.context.Context; 
import org.apache.velocity.tools.view.servlet.VelocityViewServlet;

import org.apache.commons.lang.StringUtils;
import org.openmeetings.app.hibernate.beans.basic.Configuration;
import org.slf4j.Logger;
import org.red5.logging.Red5LoggerFactory;
import org.openmeetings.app.data.basic.Configurationmanagement;
import org.openmeetings.app.data.basic.Fieldmanagment;
import org.openmeetings.app.data.basic.Sessionmanagement;
import org.openmeetings.app.data.user.Usermanagement;
import org.openmeetings.app.hibernate.beans.lang.Fieldlanguagesvalues;
import org.openmeetings.app.hibernate.beans.recording.RoomClient;
import org.openmeetings.app.remote.red5.ClientListManager;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.openmeetings.app.rtp.RTPScreenSharingSession;
import org.openmeetings.app.rtp.RTPStreamingHandler;
import org.openmeetings.app.templates.ScreenCastTemplate;
import org.openmeetings.server.socket.ServerSocketMinaProcess;

public class ScreenRequestHandler extends VelocityViewServlet {
	
	private static final Logger log = Red5LoggerFactory.getLogger(ScreenRequestHandler.class, ScopeApplicationAdapter.webAppRootKey);
	
	@Override
	public Template handleRequest(HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, Context ctx) throws ServletException,
			IOException {

		try {
			
			String sid = httpServletRequest.getParameter("sid");
			if (sid == null) {
				sid = "default";
			}
			log.debug("sid: " + sid);

			Long users_id = Sessionmanagement.getInstance().checkSession(sid);
			Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);

			//if (user_level > 0) {
				
				String publicSID = httpServletRequest.getParameter("publicSID");
				if (publicSID == null) {
					new Exception("publicSID is empty: "+publicSID);
					return null;
				}
				
				String room = httpServletRequest.getParameter("room");
				if(room == null) room = "default";

				String domain = httpServletRequest.getParameter("domain");
				if(domain == null) {
					new Exception("domain is empty: "+domain);
					return null;
				}
				
				String languageAsString = httpServletRequest.getParameter("languageAsString");
				if(languageAsString == null) {
					new Exception("languageAsString is empty: "+domain);
					return null;
				}
				Long language_id = Long.parseLong(languageAsString);
				
				String rtmphostlocal = httpServletRequest.getParameter("rtmphostlocal");
				if (rtmphostlocal == null) {
					new Exception("rtmphostlocal is empty: "+rtmphostlocal);
					return null;
				}
				
				String red5httpport = httpServletRequest.getParameter("red5httpport");
				if (red5httpport == null) {
					new Exception("red5httpport is empty: "+red5httpport);
					return null;
				}
				
				String record = httpServletRequest.getParameter("record");
				if (record == null) {
					new Exception("recorder is empty: ");
					record = "no";
				}
				
				String mode = httpServletRequest.getParameter("mode");
				if (mode == null) {
					new Exception("mode is empty: ");
					mode = "sharer";
				}
				
				String httpRootKey = httpServletRequest.getParameter("httpRootKey");
				if (httpRootKey == null) {
					new Exception("httpRootKey is empty could not start sharer");
					return null;
				}
				
				//make a complete name out of domain(organisation) + roomname
				String roomName = domain+"_"+room;
				//trim whitespaces cause it is a directory name
				roomName = StringUtils.deleteWhitespace(roomName);
				
				String current_dir = getServletContext().getRealPath("/");
				log.debug("Current_dir: "+current_dir);			
				
				//String jnlpString = ScreenCastTemplate.getInstance(current_dir).getScreenTemplate(rtmphostlocal, red5httpport, sid, room, domain);
				ctx.put("rtmphostlocal", rtmphostlocal); //rtmphostlocal
			    ctx.put("red5httpport", red5httpport); //red5httpport
			    
			    log.debug("httpRootKey "+httpRootKey);
			    
			    String codebase = "http://"+rtmphostlocal+":"+red5httpport+httpRootKey+"screen";
			    
			    ctx.put("codebase", codebase);
			    
			    String httpSharerURL = "http://"+rtmphostlocal+":"+red5httpport + httpRootKey + "ScreenServlet";
			    
			    ctx.put("webAppRootKey", httpRootKey); 
			    ctx.put("httpSharerURL", httpSharerURL);
			    
			    ctx.put("SID", sid);
			    ctx.put("ROOM", room);
			    ctx.put("DOMAIN", domain);
			    ctx.put("PUBLIC_SID", publicSID);
			    ctx.put("RECORDER", record);
		        
		        String requestedFile = roomName+".jnlp";
				httpServletResponse.setContentType("application/x-java-jnlp-file");
				httpServletResponse.setHeader("Content-Disposition","Inline; filename=\"" + requestedFile + "\"");
		        
				// Check , which screenviewer is to be used
				org.openmeetings.app.hibernate.beans.basic.Configuration conf = Configurationmanagement.getInstance().getConfKey(3L, "screen_viewer");
				
				String template = "screencast_odsp_sharertemplate.vm";
				//template = "screencast_odsp_sharertemplate.vm";
				ctx.put("PORT", ServerSocketMinaProcess.port);
				
				log.debug("language_id :: "+language_id);
				
				Fieldlanguagesvalues fValue728 = Fieldmanagment.getInstance().getFieldByIdAndLanguage(728L, language_id);
				Fieldlanguagesvalues fValue729 = Fieldmanagment.getInstance().getFieldByIdAndLanguage(729L, language_id);
				Fieldlanguagesvalues fValue730 = Fieldmanagment.getInstance().getFieldByIdAndLanguage(730L, language_id);
				Fieldlanguagesvalues fValue731 = Fieldmanagment.getInstance().getFieldByIdAndLanguage(731L, language_id);
				Fieldlanguagesvalues fValue732 = Fieldmanagment.getInstance().getFieldByIdAndLanguage(732L, language_id);
				Fieldlanguagesvalues fValue733 = Fieldmanagment.getInstance().getFieldByIdAndLanguage(733L, language_id);
				Fieldlanguagesvalues fValue734 = Fieldmanagment.getInstance().getFieldByIdAndLanguage(734L, language_id);
				Fieldlanguagesvalues fValue735 = Fieldmanagment.getInstance().getFieldByIdAndLanguage(735L, language_id);
				Fieldlanguagesvalues fValue736 = Fieldmanagment.getInstance().getFieldByIdAndLanguage(736L, language_id);
				Fieldlanguagesvalues fValue737 = Fieldmanagment.getInstance().getFieldByIdAndLanguage(737L, language_id);
				Fieldlanguagesvalues fValue738 = Fieldmanagment.getInstance().getFieldByIdAndLanguage(738L, language_id);
				Fieldlanguagesvalues fValue739 = Fieldmanagment.getInstance().getFieldByIdAndLanguage(739L, language_id);
				Fieldlanguagesvalues fValue740 = Fieldmanagment.getInstance().getFieldByIdAndLanguage(740L, language_id);
				Fieldlanguagesvalues fValue741 = Fieldmanagment.getInstance().getFieldByIdAndLanguage(741L, language_id);
				Fieldlanguagesvalues fValue742 = Fieldmanagment.getInstance().getFieldByIdAndLanguage(742L, language_id);
				Fieldlanguagesvalues fValue844 = Fieldmanagment.getInstance().getFieldByIdAndLanguage(844L, language_id);
				
				Fieldlanguagesvalues fValue869 = Fieldmanagment.getInstance().getFieldByIdAndLanguage(869L, language_id);
				Fieldlanguagesvalues fValue870 = Fieldmanagment.getInstance().getFieldByIdAndLanguage(870L, language_id);
				Fieldlanguagesvalues fValue871 = Fieldmanagment.getInstance().getFieldByIdAndLanguage(871L, language_id);
				Fieldlanguagesvalues fValue872 = Fieldmanagment.getInstance().getFieldByIdAndLanguage(872L, language_id);
				Fieldlanguagesvalues fValue878 = Fieldmanagment.getInstance().getFieldByIdAndLanguage(878L, language_id);
				
				Fieldlanguagesvalues fValue1089 = Fieldmanagment.getInstance().getFieldByIdAndLanguage(1089L, language_id);
				Fieldlanguagesvalues fValue1090 = Fieldmanagment.getInstance().getFieldByIdAndLanguage(1090L, language_id);
				Fieldlanguagesvalues fValue1091 = Fieldmanagment.getInstance().getFieldByIdAndLanguage(1091L, language_id);
				Fieldlanguagesvalues fValue1092 = Fieldmanagment.getInstance().getFieldByIdAndLanguage(1092L, language_id);
				Fieldlanguagesvalues fValue1093 = Fieldmanagment.getInstance().getFieldByIdAndLanguage(1093L, language_id);
				
				String label_viewer = "Viewer";
				String label_sharer = "Sharer";
				
				try{
				 label_viewer = fValue728.getValue()+";"+
										fValue729.getValue()+";"+
										fValue736.getValue()+";"+
										fValue742.getValue();
				
				 label_sharer = fValue730.getValue()+";"+ //0
										fValue731.getValue()+";"+ //1
										fValue732.getValue()+";"+ //2
										fValue733.getValue()+";"+ //3
										fValue734.getValue()+";"+ //4
										fValue735.getValue()+";"+ //5
										fValue737.getValue()+";"+ //6
										fValue738.getValue()+";"+ //7
										fValue739.getValue()+";"+ //8
										fValue740.getValue()+";"+ //9
										fValue741.getValue()+";"+ //10
										fValue742.getValue()+";"+ //11
										fValue844.getValue()+";"+ //12
										fValue869.getValue()+";"+ //13
										fValue870.getValue()+";"+ //14
										fValue871.getValue()+";"+ //15
										fValue872.getValue()+";"+  //16
										fValue878.getValue()+";"+  //17
										fValue1089.getValue()+";"+  //18
										fValue1090.getValue()+";"+  //19
										fValue1091.getValue()+";"+  //20
										fValue1092.getValue()+";"+  //21
										fValue1093.getValue()+";"  //22
										;
				
				}catch(Exception e){
					log.error("Error resolving Language labels : " , e);
				}
				
				ctx.put("LABELVIEWER", label_viewer);
				ctx.put("LABELSHARER", label_sharer);
				
				log.debug("Creating JNLP Template for TCP solution");
				
				if(mode.equals("sharer") && conf != null){
					String confVal = conf.getConf_value();
					
					try{
						int conf_i = Integer.parseInt(confVal);
						
						if (conf_i == 0) {
							
							//I've disabled the HTTP-Sharer Option for now as it makes
							//trouble for external Users to use it
							template = "screencast_odsp_sharertemplate.vm";
							
						} else if(conf_i == 1){
							template = "screencast_jrdesktop.vm";
							log.debug("Creating JNLP Template for jrdesktop solution");
						} else if(conf_i == 2){
							
							// Storing Session data
							RTPScreenSharingSession session = RTPStreamingHandler.storeSessionForRoom(room, users_id, publicSID, rtmphostlocal, -1);
							
							// For the RTP Sharer, we need some additional information
							ctx.put("HOST", rtmphostlocal);
							ctx.put("PORT", session.getIncomingRTPPort());
							template = "screencast_rtp.vm";
							log.debug("Creating JNLP Template for RTP solution");
							
							log.debug("Stored RTPSession Data for Room " + room);
							
							
						}
						else if(conf_i == 3){
							
							// For the UDP Sharer, we need some additional information
							
							template = "screencast_odsp_sharertemplate.vm";
							ctx.put("PORT", ServerSocketMinaProcess.port);
							log.debug("Creating JNLP Template for TCP solution");
							
						}
						else if (conf_i == 4) {
							
							log.debug("RTMP Sharer labels :: "+label_sharer);
							
							// Red5-Screen Share with RTMP Client
							template = "screenshare.vm";
							
							codebase = "http://"+rtmphostlocal+":"+red5httpport+httpRootKey+"red5-screenshare";
						    
							String connectionType = httpServletRequest.getParameter("connectionType");
							if (connectionType == null) {
								new Exception("No connectionType ");
							}
							
							String startUpClass = "org.red5.screen.webstart.ScreenShare";
							if (connectionType.equals("rtmpt")) {
								startUpClass = "org.red5.screen.webstart.ScreenShareRTMPT";
							}
							
							String orgIdAsString = httpServletRequest.getParameter("organization_id");
							if (orgIdAsString == null) {
								new Exception("orgIdAsString is empty could not start sharer");
								return null;
							}
							
							ctx.put("organization_id", orgIdAsString);
							
							ctx.put("startUpClass", startUpClass);
						    ctx.put("codebase", codebase);
							ctx.put("red5-host", rtmphostlocal);
							ctx.put("red5-app", ScopeApplicationAdapter.webAppRootKey+"/"+room);
							
							Configuration configuration = Configurationmanagement.getInstance().getConfKey(3L, "default.quality.screensharing");
							String default_quality_screensharing = "1";
							if (configuration != null) {
								default_quality_screensharing = configuration.getConf_value();
							}
							
							ctx.put("default_quality_screensharing", default_quality_screensharing);
						    
							String port = httpServletRequest.getParameter("port");
							if (port == null) {
								new Exception("port is empty: ");
								return null;
							}
							ctx.put("port", port);
						    
						}
						else
							log.debug("Creating JNLP Template for default solution");
					}
					catch(Exception e){
						log.error("invalid configuration value for key screen_viewer!");
					}
				} else if (mode.equals("viewer")) {
					template = "screencast_odsp_viewertemplate.vm";
					ctx.put("PORT", ServerSocketMinaProcess.port);
					log.debug("Creating JNLP Template for UDP solution");
				}
				
				log.debug("template "+template);
				
				return getVelocityEngine().getTemplate(template);
			
			//}
			
			//return null;
			
		} catch (Exception er) {
			log.error("[ScreenRequestHandler]",er);
			System.out.println("Error downloading: " + er);
		}
		return null;
	}
	
}
