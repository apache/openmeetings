package org.openmeetings.servlet.outputhandler;

import http.utils.multipartrequest.ServletMultipartRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.openmeetings.app.data.basic.AuthLevelmanagement;
import org.openmeetings.app.data.basic.Sessionmanagement;
import org.openmeetings.app.data.user.Organisationmanagement;
import org.openmeetings.app.data.user.Statemanagement;
import org.openmeetings.app.data.user.Usermanagement;
import org.openmeetings.app.data.user.dao.UsersDaoImpl;
import org.openmeetings.app.documents.GenerateImage;
import org.openmeetings.app.documents.GeneratePDF;
import org.openmeetings.app.documents.GenerateThumbs;
import org.openmeetings.app.hibernate.beans.adresses.Adresses;
import org.openmeetings.app.hibernate.beans.adresses.States;
import org.openmeetings.app.hibernate.beans.domain.Organisation;
import org.openmeetings.app.hibernate.beans.user.Users;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.openmeetings.utils.math.CalendarPatterns;
import org.openmeetings.utils.stringhandlers.StringComparer;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class BackupImport extends HttpServlet {

	private static final Logger log = Red5LoggerFactory.getLogger(BackupImport.class, "openmeetings");
	 
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void service(HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) throws ServletException,
			IOException {
		
		try {
      
			if (httpServletRequest.getContentLength() > 0) {


				String sid = httpServletRequest.getParameter("sid");
				if (sid == null) {
					sid = "default";
					throw new Exception("SID Missing");
				}
				
				String publicSID = httpServletRequest.getParameter("publicSID");
				if (publicSID == null) {
					publicSID = "default";
					throw new Exception("publicSID Missing");
				}
				

				log.debug("uploading........ sid: "+sid);
				
				Long users_id = Sessionmanagement.getInstance().checkSession(sid);
				Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);
				
				if (AuthLevelmanagement.getInstance().checkAdminLevel(user_level)) {
					
					String current_dir = getServletContext().getRealPath("/");
					String working_dir = current_dir + "upload"
							+ File.separatorChar + "import"
							+ File.separatorChar;
					File working_dirFile = new File(working_dir);
					if (!working_dirFile.exists()) {
						working_dirFile.mkdir();
					}
					
					ServletMultipartRequest upload = new ServletMultipartRequest(
							httpServletRequest, 104857600*10, "utf-8"); // max 1000 mb
					InputStream is = upload.getFileContents("Filedata");
					
					String fileSystemName = upload.getFileSystemName("Filedata");
					
					StringUtils.deleteWhitespace(fileSystemName);
					
					int dotidx=fileSystemName.lastIndexOf('.');
					String newFileSystemName = StringComparer.getInstance()
							.compareForRealPaths(
									fileSystemName.substring(0,
											dotidx));
					
					String completeName = working_dir + newFileSystemName;
					
					File f = new File(completeName + File.separatorChar);
					
					if (f.exists()) {
						int recursiveNumber = 0;
						String tempd = completeName + "_" + recursiveNumber;
						while (f.exists()) {
							recursiveNumber++;
							tempd = completeName + "_" + recursiveNumber;
							f = new File(tempd + File.separatorChar);

						}
						completeName = tempd;
					}
					
					f.mkdir();
					
					log.debug("##### WRITE FILE TO: " + completeName);
					
					ZipInputStream zis = new ZipInputStream(is);
					
					ZipEntry zipEntry = zis.getNextEntry();
					while (zipEntry != null) {
						
						String fileName = completeName + File.separatorChar + zipEntry.getName();
						
						FileOutputStream fos = new FileOutputStream(fileName);
						byte[] buffer = new byte[1024];
						int len = 0;
	
						while (len != (-1)) {
							len = zis.read(buffer, 0, 1024);
							if (len != (-1))
								fos.write(buffer, 0, len);
						}
	
						fos.close();
						
						zis.closeEntry();
						
						zipEntry = zis.getNextEntry();
						
					}
					
					is.close();
					zis.close();
					
					/* #####################
					 * Import Organizations
					 */
					String orgListXML = completeName + File.separatorChar + "organizations.xml";
					File orgFile = new File(orgListXML);
					if (!orgFile.exists()) {
						throw new Exception ("organizations.xml missing");
					}
					this.importOrganizsations(orgFile);
					
					/* #####################
					 * Import Users
					 */
					String userListXML = completeName + File.separatorChar + "users.xml";
					File userFile = new File(userListXML);
					if (!userFile.exists()) {
						throw new Exception ("users.xml missing");
					}
					this.importUsers(userFile);
					
					LinkedHashMap<String,Object> hs = new LinkedHashMap<String,Object>();
					hs.put("user", UsersDaoImpl.getInstance().getUser(users_id));
					hs.put("message", "library");
					hs.put("action", "import");
					hs.put("error", "");
					hs.put("fileName", completeName);	
					
					ScopeApplicationAdapter.getInstance().sendMessageWithClientByPublicSID(hs,publicSID);
					
				}
						
			}
			
		} catch (Exception e) {
			
			log.error("[ImportExport]",e);
			
			e.printStackTrace();
			throw new ServletException(e);
		}
		
		return;
	}
	
	private void importUsers(File userFile) throws Exception {
		
		List<Users> userList = this.getUsersByXML(userFile);
		
		for (Users us : userList) {
			
			Users storedUser = Usermanagement.getInstance().getUserById(us.getUser_id());
			
		}
		
	}
	
	private List<Users> getUsersByXML (File userFile) {
		try {
			
			List<Users> userList = new LinkedList<Users>();
			
			SAXReader reader = new SAXReader();
			Document document = reader.read(userFile);
			
			Element root = document.getRootElement();
			
			for (Iterator<Element> i = root.elementIterator(); i.hasNext(); ) {
	        	Element itemObject =  i.next();
	        	if (itemObject.getName().equals("users")) {
	        		
	        		for (Iterator<Element> innerIter = itemObject.elementIterator( "user" ); innerIter.hasNext(); ) {
	        			
	        			Element itemUsers = innerIter.next();
	        			
	        			Users us = new Users();

	        			us.setUser_id(Long.valueOf(itemUsers.element("user_id").getText()).longValue());
	                    us.setAge(CalendarPatterns.parseDate(itemUsers.element("age").getText()));
	                    us.setAvailible(Integer.valueOf(itemUsers.element("availible").getText()).intValue());
	        			us.setDeleted(itemUsers.element("deleted").getText());
	        			us.setFirstname(itemUsers.element("firstname").getText());
	        			us.setLastname(itemUsers.element("lastname").getText());
	        			us.setLogin(itemUsers.element("login").getText());
	        			us.setPassword(itemUsers.element("pass").getText());
	        			us.setDeleted(itemUsers.element("deleted").getText());
	        			
	        			us.setPictureuri(itemUsers.element("pictureuri").getText());
	        			if (itemUsers.element("language_id").getText().length()>0)
	        				us.setLanguage_id(Long.valueOf(itemUsers.element("language_id").getText()).longValue());
	        				
	        			us.setStatus(Integer.valueOf(itemUsers.element("status").getText()).intValue());
	        			us.setRegdate(CalendarPatterns.parseDate(itemUsers.element("regdate").getText()));
	        			us.setTitle_id(Integer.valueOf(itemUsers.element("title_id").getText()).intValue());
	        			us.setLevel_id(Long.valueOf(itemUsers.element("level_id").getText()).longValue());
	        			
	        			
	        			String additionalname = itemUsers.element("additionalname").getText();
	        			String comment = itemUsers.element("comment").getText();
	        			// A User can not have a deleted Adress, you cannot delete the
	        			// Adress of an User
	        			// String deleted = u.getAdresses().getDeleted()
	        			// Phone Number not done yet
	        			String fax = itemUsers.element("fax").getText();
	        			Long state_id = Long.valueOf(itemUsers.element("state_id").getText()).longValue();
	        			String street = itemUsers.element("street").getText();
	        			String town = itemUsers.element("town").getText();
	        			String zip = itemUsers.element("zip").getText();
	        			
	        			String phone = "";
	        			if (itemUsers.element("phone") != null) {
	        				phone = itemUsers.element("phone").getText();
	        			}
	        			
	        			String email = "";
	        			if (itemUsers.element("mail") != null) {
	        				email = itemUsers.element("mail").getText();
	        			}
	        			
	        			States st = Statemanagement.getInstance().getStateById(state_id);
	        			
	        			Adresses adr = new Adresses();
	        			adr.setAdditionalname(additionalname);
	        			adr.setComment(comment);
	        			adr.setStarttime(new Date());
	        			adr.setFax(fax);
	        			adr.setStreet(street);
	        			adr.setTown(town);
	        			adr.setZip(zip);
	        			adr.setStates(st);
	        			adr.setPhone(phone);
	        			adr.setEmail(email);
	        			
	        			us.setAdresses(adr);
	        			
	        			userList.add(us);
	        			
	        		}
	        		
	        	}
	        }
	        
	        return userList;
			
		} catch (Exception err) {
			log.error("[getUsersByXML]",err);
		}
		return null;
	}

	private void importOrganizsations(File orgFile) throws Exception {
		
		List<Organisation> orgList = this.getOrganisationsByXML(orgFile);
		
		for (Organisation org : orgList) {
			Organisation orgStored = Organisationmanagement.getInstance().getOrganisationByIdAndDeleted(org.getOrganisation_id());
			
			if (orgStored == null) {
				Organisationmanagement.getInstance().addOrganisationObj(org);
			}
			
		}
		
	}
	
	private List<Organisation> getOrganisationsByXML(File orgFile) {
		try {
			
			List<Organisation> orgList = new LinkedList<Organisation>();
			
			SAXReader reader = new SAXReader();
			Document document = reader.read(orgFile);
			
			Element root = document.getRootElement();
			
			for (Iterator<Element> i = root.elementIterator(); i.hasNext(); ) {
	        	Element itemObject =  i.next();
	        	if (itemObject.getName().equals("organisations")) {
	        		
	        		for (Iterator<Element> innerIter = itemObject.elementIterator( "organisation" ); innerIter.hasNext(); ) {
	        			
	        			Element orgObject = innerIter.next();
	        			
	        			Long organisation_id = Long.valueOf(orgObject.element("organisation_id").getText()).longValue();
	        			String name = orgObject.element("name").getText();
	        			String deleted = orgObject.element("deleted").getText();
	        			
	        			Organisation organisation = new Organisation();
	        			organisation.setOrganisation_id(organisation_id);
	        			organisation.setName(name);
	        			organisation.setDeleted(deleted);
	        			
	        			orgList.add(organisation);
	        			
	        		}
	        		
	        	}
	        }
	        
	        return orgList;
			
		} catch (Exception err) {
			log.error("[getOrganisationsByXML]",err);
		}
		return null;
	}
	
	
}
