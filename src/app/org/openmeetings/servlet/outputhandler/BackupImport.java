package org.openmeetings.servlet.outputhandler;

import http.utils.multipartrequest.ServletMultipartRequest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
import org.openmeetings.app.data.basic.Configurationmanagement;
import org.openmeetings.app.data.basic.Sessionmanagement;
import org.openmeetings.app.data.basic.dao.LdapConfigDaoImpl;
import org.openmeetings.app.data.basic.dao.OmTimeZoneDaoImpl;
import org.openmeetings.app.data.calendar.daos.AppointmentCategoryDaoImpl;
import org.openmeetings.app.data.calendar.daos.AppointmentDaoImpl;
import org.openmeetings.app.data.calendar.daos.AppointmentReminderTypDaoImpl;
import org.openmeetings.app.data.calendar.daos.MeetingMemberDaoImpl;
import org.openmeetings.app.data.conference.Roommanagement;
import org.openmeetings.app.data.conference.dao.RoomModeratorsDaoImpl;
import org.openmeetings.app.data.user.Organisationmanagement;
import org.openmeetings.app.data.user.Statemanagement;
import org.openmeetings.app.data.user.Usermanagement;
import org.openmeetings.app.data.user.dao.UsersDaoImpl;
import org.openmeetings.app.documents.GenerateImage;
import org.openmeetings.app.documents.GeneratePDF;
import org.openmeetings.app.documents.GenerateThumbs;
import org.openmeetings.app.hibernate.beans.adresses.Adresses;
import org.openmeetings.app.hibernate.beans.adresses.States;
import org.openmeetings.app.hibernate.beans.basic.Configuration;
import org.openmeetings.app.hibernate.beans.basic.LdapConfig;
import org.openmeetings.app.hibernate.beans.basic.OmTimeZone;
import org.openmeetings.app.hibernate.beans.calendar.Appointment;
import org.openmeetings.app.hibernate.beans.calendar.MeetingMember;
import org.openmeetings.app.hibernate.beans.domain.Organisation;
import org.openmeetings.app.hibernate.beans.domain.Organisation_Users;
import org.openmeetings.app.hibernate.beans.rooms.RoomModerators;
import org.openmeetings.app.hibernate.beans.rooms.Rooms;
import org.openmeetings.app.hibernate.beans.rooms.Rooms_Organisation;
import org.openmeetings.app.hibernate.beans.user.Users;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.openmeetings.utils.math.CalendarPatterns;
import org.openmeetings.utils.stringhandlers.StringComparer;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class BackupImport extends HttpServlet {

	private static final Logger log = Red5LoggerFactory.getLogger(BackupImport.class, ScopeApplicationAdapter.webAppRootKey);
	 
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
					
					ZipInputStream zipinputstream = new ZipInputStream(is);
					byte[] buf = new byte[1024];
					
					ZipEntry zipentry = zipinputstream.getNextEntry();
					
					while (zipentry != null) {
			            //for each entry to be extracted
			            String entryName = completeName + File.separatorChar + zipentry.getName();
			            entryName = entryName.replace('/', File.separatorChar);
			            entryName = entryName.replace('\\', File.separatorChar);
			            
			            //log.debug("entryname " + entryName);
			            
			            //zipentry.get
			            
			            int n;
			            FileOutputStream fileoutputstream;
			            File newFile = new File(entryName);
			            
			            
			            if (zipentry.isDirectory()) {
			                if (!newFile.mkdir()) {
			                    break;
			                }
			                zipentry = zipinputstream.getNextEntry();
			                continue;
			            }
			            
			            File fentryName = new File(entryName);
			            
			            File fparent = new File(fentryName.getParent());
			            
			            if (!fparent.exists()) {
			            	
			            	File fparentparent = new File(fparent.getParent());
			            	
			            	if (!fparentparent.exists()) {
			            		
			            		File fparentparentparent = new File(fparentparent.getParent());
			            		
			            		if (!fparentparentparent.exists()) {
			            			
			            			fparentparentparent.mkdir();
			            			fparentparent.mkdir();
			            			fparent.mkdir();
			            			
			            		} else {
			            			
			            			fparentparent.mkdir();
			            			fparent.mkdir();
			            			
			            		}
			            		
			            	} else {
			            		
			            		fparent.mkdir();
			            		
			            	}
			            	
			            }
			            

			            fileoutputstream = new FileOutputStream(entryName);

			            while ((n = zipinputstream.read(buf, 0, 1024)) > -1) {
			                fileoutputstream.write(buf, 0, n);
			            }

			            fileoutputstream.close();
			            zipinputstream.closeEntry();
			            zipentry = zipinputstream.getNextEntry();

			        }//while

		            zipinputstream.close();
		            
		            //Now check the room files and import them
		            
		            File roomFilesFolder = new File(completeName + File.separatorChar + "roomFiles");
		            
		            
		            String library_dir = current_dir + "upload"
												+ File.separatorChar;
		            
		            log.debug("roomFilesFolder PATH " + roomFilesFolder.getAbsolutePath());
		            
		            if (roomFilesFolder.exists()) {
		            	
		            	File[] files = roomFilesFolder.listFiles();
		            	for (File file : files) {
		            		if (file.isDirectory()) {
		            			
								String parentFolderName = file.getName();
		            				
	            				//Is a room folder or the profiles folder
	            				String parentPath = library_dir + parentFolderName + File.separatorChar;
		            			
	            				File parentPathFile = new File(parentPath);
	            				
	            				if (!parentPathFile.exists()) {
	            					parentPathFile.mkdir();
	            				}
	            				
	            				File[] roomOrProfileFiles = file.listFiles();
	            				for (File roomOrProfileFileOrFolder : roomOrProfileFiles) {
	            					
	            					
	            					if (roomOrProfileFileOrFolder.isDirectory()) {
	            						
	            						String roomDocumentFolderName = parentPath + roomOrProfileFileOrFolder.getName() + File.separatorChar;
	            						
	            						File roomDocumentFolder = new File(roomDocumentFolderName);
	    	            				
	    	            				if (!roomDocumentFolder.exists()) {
	    	            					roomDocumentFolder.mkdir();
	    	            					
	    	            					File[] roomDocumentFiles = roomOrProfileFileOrFolder.listFiles();
		    	            				
		    	            				for (File roomDocumentFile : roomDocumentFiles) {
		    	            					
		    	            					if (roomDocumentFile.isDirectory()) {
		    	            						log.error("Folder detected in Documents space! Folder "+roomDocumentFolderName);
		    	            					} else {
		    	            						
		    	            						String roomDocumentFileName = roomDocumentFolderName + roomDocumentFile.getName();
		    	            						
		    	            						this.copyFile(roomDocumentFile, new File(roomDocumentFileName));
		    	            						
		    	            					}
		    	            					
		    	            				}
	    	            					
	    	            				} else {
	    	            					
	    	            					log.debug("Document already exists :: ",roomDocumentFolderName);
	    	            					
	    	            				}
	    	            				
	    	            				
	            						
	            					} else {
	            						
	            						String roomFileOrProfileName = parentPath + roomOrProfileFileOrFolder.getName();
	            						
	            						File roomFileOrProfileFile = new File(roomFileOrProfileName);
	            						
	            						if (!roomFileOrProfileFile.exists()) {
	            							
	            							this.copyFile(roomOrProfileFileOrFolder, roomFileOrProfileFile);
	            							
	            						} else {
	            							
	            							log.debug("File does already exist :: ",roomFileOrProfileName);
	            							
	            						}
	            						
	            						
	            					}
	            					
	            					
	            					
	            				}
		            			
		            		}
		            	}
		            	
		            }
					
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
					
					/* #####################
					 * Import Rooms
					 */
					String roomListXML = completeName + File.separatorChar + "rooms.xml";
					File roomFile = new File(roomListXML);
					if (!roomFile.exists()) {
						throw new Exception ("rooms.xml missing");
					}
					this.importRooms(roomFile);
					
					/* #####################
					 * Import Room Organisations
					 */
					String orgRoomListXML = completeName + File.separatorChar + "rooms_organisation.xml";
					File orgRoomListFile = new File(orgRoomListXML);
					if (!orgRoomListFile.exists()) {
						throw new Exception ("rooms_organisation.xml missing");
					}
					this.importOrgRooms(orgRoomListFile);
					
					/* #####################
					 * Import Appointements
					 */
					String appointementListXML = completeName + File.separatorChar + "appointements.xml";
					File appointementListFile = new File(appointementListXML);
					if (!appointementListFile.exists()) {
						throw new Exception ("appointements.xml missing");
					}
					this.importAppointements(appointementListFile);

					/* #####################
					 * Import MeetingMembers
					 * 
					 * Reminder Invitations will be NOT send!
					 * 
					 */
					String meetingmembersListXML = completeName + File.separatorChar + "meetingmembers.xml";
					File meetingmembersListFile = new File(meetingmembersListXML);
					if (!meetingmembersListFile.exists()) {
						throw new Exception ("meetingmembersListFile missing");
					}
					this.importMeetingmembers(meetingmembersListFile);
					
					/* #####################
					 * Import LDAP Configs
					 * 
					 */
					String ldapConfigListXML = completeName + File.separatorChar + "ldapconfigs.xml";
					File ldapConfigListFile = new File(ldapConfigListXML);
					if (!ldapConfigListFile.exists()) {
						log.debug("meetingmembersListFile missing");
						//throw new Exception ("meetingmembersListFile missing");
					} else {
						this.importLdapConfig(ldapConfigListFile);
					}
					
					
					this.deleteDirectory(f);
					
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
	
	public void copyFile(File sourceLocation , File targetLocation) throws IOException {
            
            InputStream in = new FileInputStream(sourceLocation);
            OutputStream out = new FileOutputStream(targetLocation);
            
            // Copy the bits from instream to outstream
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
    }

	public boolean deleteDirectory(File path) throws IOException {
		if (path.exists()) {
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDirectory(files[i]);
				} else {
					files[i].delete();
				}
			}
		}
		return (path.delete());
	}
	
	private void importUsers(File userFile) throws Exception {
		
		List<Users> userList = this.getUsersByXML(userFile);
		
		for (Users us : userList) {
			
			Users storedUser = Usermanagement.getInstance().getUserById(us.getUser_id());
			
			if (storedUser == null) {
				
				us.setStarttime(new Date());
				Usermanagement.getInstance().addUserBackup(us);
				
			}
			
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

	        			us.setUser_id(Long.valueOf(itemUsers.element("user_id").getText()));
	                    us.setAge(CalendarPatterns.parseDate(itemUsers.element("age").getText()));
	                    us.setAvailible(importIntegerType(itemUsers.element("availible").getText()));
	        			us.setDeleted(itemUsers.element("deleted").getText());
	        			us.setFirstname(itemUsers.element("firstname").getText());
	        			us.setLastname(itemUsers.element("lastname").getText());
	        			us.setLogin(itemUsers.element("login").getText());
	        			us.setPassword(itemUsers.element("pass").getText());
	        			us.setDeleted(itemUsers.element("deleted").getText());
	        			
	        			us.setPictureuri(itemUsers.element("pictureuri").getText());
	        			if (itemUsers.element("language_id").getText().length()>0)
	        				us.setLanguage_id(Long.valueOf(itemUsers.element("language_id").getText()));
	        				
	        			us.setStatus(importIntegerType(itemUsers.element("status").getText()));
	        			us.setRegdate(CalendarPatterns.parseDate(itemUsers.element("regdate").getText()));
	        			us.setTitle_id(importIntegerType(itemUsers.element("title_id").getText()));
	        			us.setLevel_id(importLongType(itemUsers.element("level_id").getText()));
	        			
	        			
	        			String additionalname = itemUsers.element("additionalname").getText();
	        			String comment = itemUsers.element("comment").getText();
	        			// A User can not have a deleted Adress, you cannot delete the
	        			// Adress of an User
	        			// String deleted = u.getAdresses().getDeleted()
	        			// Phone Number not done yet
	        			String fax = itemUsers.element("fax").getText();
	        			Long state_id = importLongType(itemUsers.element("state_id").getText());
	        			String street = itemUsers.element("street").getText();
	        			String town = itemUsers.element("town").getText();
	        			String zip = itemUsers.element("zip").getText();
	        			
	        			if (itemUsers.element("omTimeZone") != null) {
	        				OmTimeZone omTimeZone = OmTimeZoneDaoImpl.getInstance().getOmTimeZone(itemUsers.element("omTimeZone").getText());
	        				
	        				us.setOmTimeZone(omTimeZone);
	        				us.setForceTimeZoneCheck(false);
	        			} else {
	        				
	        				Configuration conf = Configurationmanagement.getInstance().getConfKey(3L, "default.timezone");
							if (conf != null) {
								String jNameTimeZone = conf.getConf_value();
								
								OmTimeZone omTimeZone = OmTimeZoneDaoImpl.getInstance().getOmTimeZone(jNameTimeZone);
		        				us.setOmTimeZone(omTimeZone);
		        				
							}
	        				
	        				
	        				us.setForceTimeZoneCheck(true);
	        			}
	        			
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
	        			
	        			us.setOrganisation_users(new HashSet<Organisation_Users>());
	        			
	        			for (Iterator<Element> organisationsIterator = itemUsers.elementIterator( "organisations" ); organisationsIterator.hasNext(); ) {
	        				
	        				Element organisations = organisationsIterator.next();
	        				
	        				for (Iterator<Element> organisationIterator = organisations.elementIterator( "user_organisation" ); organisationIterator.hasNext(); ) {
	    	        			
		        				Element organisationObject = organisationIterator.next();
		        				
		        				Long organisation_id = importLongType(organisationObject.element("organisation_id").getText());
		        				Long user_id = importLongType(organisationObject.element("user_id").getText());
		        				Boolean isModerator = importBooleanType(organisationObject.element("isModerator").getText());
		        				String commentOrg = organisationObject.element("comment").getText();
		        				String deleted = organisationObject.element("deleted").getText();
		        				
		        				Organisation_Users orgUser = new Organisation_Users();
		        				orgUser.setOrganisation(Organisationmanagement.getInstance().getOrganisationByIdBackup(organisation_id));
		        				orgUser.setUser_id(user_id);
		        				orgUser.setIsModerator(isModerator);
		        				orgUser.setComment(commentOrg);
		        				orgUser.setStarttime(new Date());
		        				orgUser.setDeleted(deleted);
		        				
		        				us.getOrganisation_users().add(orgUser);
		        				
	        				}
	        				
	        			}
	        			
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
	        			
	        			Long organisation_id = importLongType(orgObject.element("organisation_id").getText());
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
	

	private void importMeetingmembers(File meetingmembersListFile) throws Exception {
		
		List<MeetingMember> meetingmembersList = this.getMeetingmembersListByXML(meetingmembersListFile);
		
		for (MeetingMember ma : meetingmembersList) {
			MeetingMemberDaoImpl.getInstance().addMeetingMemberByObject(ma);
		}
		
	}

	private List<MeetingMember> getMeetingmembersListByXML(File meetingmembersListFile) {
		try {
			
			List<MeetingMember> meetingmembersList = new LinkedList<MeetingMember>();
			
			SAXReader reader = new SAXReader();
			Document document = reader.read(meetingmembersListFile);
			
			Element root = document.getRootElement();
			
			for (Iterator<Element> i = root.elementIterator(); i.hasNext(); ) {
	        	Element itemObject =  i.next();
	        	if (itemObject.getName().equals("meetingmembers")) {
	        		
        			for (Iterator<Element> innerIter = itemObject.elementIterator( "meetingmember" ); innerIter.hasNext(); ) {
	        			
	        			Element appointmentsObject = innerIter.next();
	        			
	        			Long meetingMemberId = importLongType(appointmentsObject.element("meetingMemberId").getText());
	        			Long userid = importLongType(appointmentsObject.element("userid").getText());
	        			Long appointment = importLongType(appointmentsObject.element("appointment").getText());
	        			String firstname = appointmentsObject.element("firstname").getText();
	        			String lastname = appointmentsObject.element("lastname").getText();
	        			String memberStatus = appointmentsObject.element("memberStatus").getText();
	        			String appointmentStatus = appointmentsObject.element("appointmentStatus").getText();
	        			String email = appointmentsObject.element("email").getText();
	        			Boolean deleted = importBooleanType(appointmentsObject.element("deleted").getText());
	        			String comment = appointmentsObject.element("comment").getText();
	        			Boolean invitor = importBooleanType(appointmentsObject.element("invitor").getText());
	        			
	        			
	        			MeetingMember meetingMember = new MeetingMember();
	        			meetingMember.setMeetingMemberId(meetingMemberId);
	        			meetingMember.setUserid(UsersDaoImpl.getInstance().getUser(userid));
	        			meetingMember.setAppointment(AppointmentDaoImpl.getInstance().getAppointmentByIdBackup(appointment));
	        			meetingMember.setFirstname(firstname);
	        			meetingMember.setLastname(lastname);
	        			meetingMember.setMemberStatus(memberStatus);
	        			meetingMember.setAppointmentStatus(appointmentStatus);
	        			meetingMember.setEmail(email);
	        			meetingMember.setDeleted(deleted);
	        			meetingMember.setComment(comment);
	        			meetingMember.setInvitor(invitor);
	        			
	        			meetingmembersList.add(meetingMember);
	        			
	        		}
	        		
	        	}
			}
			
			return meetingmembersList;
			
		} catch (Exception err) {
			log.error("[meetingmembersList]",err);
		}
		return null;
	}
	

	private void importLdapConfig(File ldapConfigListFile) throws Exception {
		
		List<LdapConfig> ldapConfigList = this.getLdapConfigListByXML(ldapConfigListFile);		
		
		for (LdapConfig ldapConfig : ldapConfigList) {
			
			LdapConfigDaoImpl.getInstance().addLdapConfigByObject(ldapConfig);
			
		}
		
	}
	
		
	private List<LdapConfig> getLdapConfigListByXML(File ldapConfigListFile) {
		try {
			
			List<LdapConfig> ldapConfigsList = new LinkedList<LdapConfig>();
			
			SAXReader reader = new SAXReader();
			Document document = reader.read(ldapConfigListFile);
			
			Element root = document.getRootElement();
			
			for (Iterator<Element> i = root.elementIterator(); i.hasNext(); ) {
	        	Element itemObject =  i.next();
	        	if (itemObject.getName().equals("ldapconfigs")) {
	        		
        			for (Iterator<Element> innerIter = itemObject.elementIterator( "ldapconfig" ); innerIter.hasNext(); ) {
	        			
	        			Element ldapconfigObject = innerIter.next();
	        			
	        			String name = ldapconfigObject.element("name").getText();
	        			String configFileName = ldapconfigObject.element("configFileName").getText();
	        			Boolean addDomainToUserName = importBooleanType(ldapconfigObject.element("addDomainToUserName").getText());
	        			String domain = ldapconfigObject.element("domain").getText();
	        			Boolean isActive = importBooleanType(ldapconfigObject.element("isActive").getText());
	        			
	        			
	        			LdapConfig ldapConfig = new LdapConfig();
	        			ldapConfig.setName(name);
	        			ldapConfig.setConfigFileName(configFileName);
	        			ldapConfig.setAddDomainToUserName(addDomainToUserName);
	        			ldapConfig.setDomain(domain);
	        			ldapConfig.setIsActive(isActive);
	        			
	        			ldapConfigsList.add(ldapConfig);
	        			
	        		}
	        		
	        	}
			}
			
			return ldapConfigsList;
			
		} catch (Exception err) {
			log.error("[getLdapConfigListByXML]",err);
		}
		return null;
	}

	private void importAppointements(File appointementListFile) throws Exception {
		
		List<Appointment> appointmentList = this.getAppointmentListByXML(appointementListFile);
		
		for (Appointment appointment : appointmentList) {
			
			Appointment appointmentStored = AppointmentDaoImpl.getInstance().getAppointmentById(appointment.getAppointmentId());
			
			if (appointmentStored == null) {
				
				AppointmentDaoImpl.getInstance().addAppointmentObj(appointment);
				
			}
			
		}
		
		
	}
	
	
	private List<Appointment> getAppointmentListByXML(File appointementListFile) {
		try {
			
			List<Appointment> appointmentList = new LinkedList<Appointment>();
			
			SAXReader reader = new SAXReader();
			Document document = reader.read(appointementListFile);
			
			Element root = document.getRootElement();
			
			for (Iterator<Element> i = root.elementIterator(); i.hasNext(); ) {
	        	Element itemObject =  i.next();
	        	if (itemObject.getName().equals("appointments")) {
	        		
        			for (Iterator<Element> innerIter = itemObject.elementIterator( "appointment" ); innerIter.hasNext(); ) {
	        			
	        			Element appointmentsObject = innerIter.next();
	        			
	        			Long appointmentId = importLongType(appointmentsObject.element("appointmentId").getText());
	        			String appointmentName = appointmentsObject.element("appointmentName").getText();
	        			String appointmentLocation = appointmentsObject.element("appointmentLocation").getText();
	        			String appointmentDescription = appointmentsObject.element("appointmentDescription").getText();
	        			Long categoryId = importLongType(appointmentsObject.element("categoryId").getText());
	        			Date appointmentStarttime = CalendarPatterns.parseDateWithHour(appointmentsObject.element("appointmentStarttime").getText());
	        			Date appointmentEndtime = CalendarPatterns.parseDateWithHour(appointmentsObject.element("appointmentEndtime").getText());
	        			String deleted = appointmentsObject.element("deleted").getText();
	        			String comment = appointmentsObject.element("comment").getText();
	        			Long typId = importLongType(appointmentsObject.element("typId").getText());
	        			Boolean isDaily = importBooleanType(appointmentsObject.element("isDaily").getText());
	        			Boolean isWeekly = importBooleanType(appointmentsObject.element("isWeekly").getText());
	        			Boolean isMonthly = importBooleanType(appointmentsObject.element("isMonthly").getText());
	        			Boolean isYearly = importBooleanType(appointmentsObject.element("isYearly").getText());
	        			Long room_id = importLongType(appointmentsObject.element("room_id").getText());
	        			String icalId = appointmentsObject.element("icalId").getText();
	        			Long language_id = importLongType(appointmentsObject.element("language_id").getText());
	        			Boolean isPasswordProtected = importBooleanType(appointmentsObject.element("isPasswordProtected").getText());
	        			String password = appointmentsObject.element("password").getText();
	        			
	        			
	        			Appointment app = new Appointment();
	        			app.setAppointmentId(appointmentId);
	        			app.setAppointmentLocation(appointmentLocation);
	        			app.setAppointmentName(appointmentName);
	        			app.setAppointmentDescription(appointmentDescription);
	        			app.setAppointmentCategory(AppointmentCategoryDaoImpl.getInstance().getAppointmentCategoryById(categoryId));
	        			app.setAppointmentStarttime(appointmentStarttime);
	        			app.setAppointmentEndtime(appointmentEndtime);
	        			app.setDeleted(deleted);
	        			app.setComment(comment);
	        			app.setRemind(AppointmentReminderTypDaoImpl.getInstance().getAppointmentReminderTypById(typId));
	        			app.setIsDaily(isDaily);
	        			app.setIsWeekly(isWeekly);
	        			app.setIsMonthly(isMonthly);
	        			app.setIsYearly(isYearly);
	        			app.setRoom(Roommanagement.getInstance().getRoomById(room_id));
	        			app.setIcalId(icalId);
	        			app.setLanguage_id(language_id);
	        			app.setIsPasswordProtected(isPasswordProtected);
	        			app.setPassword(password);
	        			
	        			appointmentList.add(app);
	        			
	        		}
	        		
	        	}
			}
			
			return appointmentList;
			
		} catch (Exception err) {
			log.error("[getRoomListByXML]",err);
		}
		return null;
	}

	private void importOrgRooms(File orgRoomListFile) throws Exception {
		
		List<Rooms_Organisation> roomOrgList = this.getOrgRoomListByXML(orgRoomListFile);
			
		for (Rooms_Organisation rooms_Organisation :roomOrgList) {
			
			Rooms_Organisation roomsOrganisationStored = Roommanagement.getInstance().getRoomsOrganisationById(rooms_Organisation.getRooms_organisation_id());
			
			if (roomsOrganisationStored == null) {
				
				Roommanagement.getInstance().addRoomOrganisation(rooms_Organisation);
				
			}
			
		}
		
	}

	private List<Rooms_Organisation> getOrgRoomListByXML(File orgRoomListFile) {
		try {
			
			List<Rooms_Organisation> orgRoomList = new LinkedList<Rooms_Organisation>();
			
			SAXReader reader = new SAXReader();
			Document document = reader.read(orgRoomListFile);
			
			Element root = document.getRootElement();
			
			for (Iterator<Element> i = root.elementIterator(); i.hasNext(); ) {
	        	Element itemObject =  i.next();
	        	if (itemObject.getName().equals("room_organisations")) {
	        		
        			for (Iterator<Element> innerIter = itemObject.elementIterator( "room_organisation" ); innerIter.hasNext(); ) {
	        			
	        			Element orgRoomObject = innerIter.next();
	        			
	        			Long rooms_organisation_id = importLongType(orgRoomObject.element("rooms_organisation_id").getText());
	        			Long organisation_id = importLongType(orgRoomObject.element("organisation_id").getText());
	        			Long rooms_id = importLongType(orgRoomObject.element("rooms_id").getText());
	        			String deleted = orgRoomObject.element("deleted").getText();
	        			
	        			Rooms_Organisation rooms_Organisation = new Rooms_Organisation();
	        			rooms_Organisation.setRooms_organisation_id(rooms_organisation_id);
	        			rooms_Organisation.setOrganisation(Organisationmanagement.getInstance().getOrganisationById(organisation_id));
	        			rooms_Organisation.setRoom(Roommanagement.getInstance().getRoomById(rooms_id));
	        			rooms_Organisation.setDeleted(deleted);
	        			
	        			orgRoomList.add(rooms_Organisation);
	        			
	        		}
	        		
	        	}
			}
			
			return orgRoomList;
			
		} catch (Exception err) {
			log.error("[getRoomListByXML]",err);
		}
		return null;
	}

	private void importRooms(File roomFile) throws Exception {
		
		Map<String,List> returnObject = this.getRoomListByXML(roomFile);
		
		List<Rooms> roomList = returnObject.get("roomList");
		List<RoomModerators> roomModeratorList = returnObject.get("roomModeratorList");
		
		for (Rooms rooms : roomList) {
			
			Rooms roomStored = Roommanagement.getInstance().getRoomById(rooms.getRooms_id());
			
			if (roomStored == null) {
				Roommanagement.getInstance().addRoom(rooms);
			}
			
		}
		
		for (RoomModerators roomModerators : roomModeratorList) {
			
			List<RoomModerators> roomModeratorsStored = RoomModeratorsDaoImpl.getInstance().getRoomModeratorByUserAndRoomId(roomModerators.getRoomId(), roomModerators.getUser().getUser_id());
			
			if (roomModeratorsStored == null || roomModeratorsStored.size() == 0) {
				
				RoomModeratorsDaoImpl.getInstance().addRoomModeratorByObj(roomModerators);
				
			}
			
		}
		
		
		
	}
	
	private Map<String,List> getRoomListByXML(File roomFile) {
		try {
			
			List<Rooms> roomList = new LinkedList<Rooms>();
			
			List<RoomModerators> roomModeratorList = new LinkedList<RoomModerators>();
			
			SAXReader reader = new SAXReader();
			Document document = reader.read(roomFile);
			
			Element root = document.getRootElement();
			
			for (Iterator<Element> i = root.elementIterator(); i.hasNext(); ) {
	        	Element itemObject =  i.next();
	        	if (itemObject.getName().equals("rooms")) {
	        		
        			for (Iterator<Element> innerIter = itemObject.elementIterator( "room" ); innerIter.hasNext(); ) {
	        			
	        			Element roomObject = innerIter.next();
	        			
	        			Long rooms_id = importLongType(roomObject.element("rooms_id").getText());
	        			String name = roomObject.element("name").getText();
	        			String deleted = roomObject.element("deleted").getText();
	        			String comment = roomObject.element("comment").getText();
	        			Long numberOfPartizipants = importLongType(roomObject.element("numberOfPartizipants").getText());
	        			Boolean appointment = importBooleanType(roomObject.element("appointment").getText());
	        			Long externalRoomId = importLongType(roomObject.element("externalRoomId").getText());
	        			String externalRoomType = roomObject.element("externalRoomType").getText();
	        			Long roomtypes_id = importLongType(roomObject.element("roomtypeId").getText());
	        			Boolean isDemoRoom = importBooleanType(roomObject.element("isDemoRoom").getText());
	        			Integer demoTime = importIntegerType(roomObject.element("demoTime").getText());
	        			Boolean isModeratedRoom = importBooleanType(roomObject.element("isModeratedRoom").getText());
	        			Boolean allowUserQuestions = importBooleanType(roomObject.element("allowUserQuestions").getText());
	        			Boolean isAudioOnly = importBooleanType(roomObject.element("isAudioOnly").getText());
	        			String sipNumber = roomObject.element("sipNumber").getText();
	        			String conferencePin = roomObject.element("conferencePin").getText();
	        			
	        			Boolean ispublic = false;
	        			if (roomObject.element("ispublic") != null) {
	        				ispublic = importBooleanType(roomObject.element("ispublic").getText());
	        			}
	        			
	        			Boolean isClosed = false;
	        			if (roomObject.element("isClosed") != null) {
	        				isClosed = importBooleanType(roomObject.element("isClosed").getText());
	        			}
	        			
	        			String redirectURL = "";
	        			if (roomObject.element("redirectURL") != null) {
	        				redirectURL = roomObject.element("redirectURL").getText();
	        			}
	        			
	        			Rooms room = new Rooms();
	        			room.setRooms_id(rooms_id);
	        			room.setName(name);
	        			room.setDeleted(deleted);
	        			room.setComment(comment);
	        			room.setNumberOfPartizipants(numberOfPartizipants);
	        			room.setAppointment(appointment);
	        			room.setExternalRoomId(externalRoomId);
	        			room.setExternalRoomType(externalRoomType);
	        			room.setRoomtype(Roommanagement.getInstance().getRoomTypesById(roomtypes_id));
	        			room.setIsDemoRoom(isDemoRoom);
	        			room.setDemoTime(demoTime);
	        			room.setIsModeratedRoom(isModeratedRoom);
	        			room.setAllowUserQuestions(allowUserQuestions);
	        			room.setIsAudioOnly(isAudioOnly);
	        			room.setSipNumber(sipNumber);
	        			room.setConferencePin(conferencePin);
	        			room.setIspublic(ispublic);
	        			room.setIsClosed(isClosed);
	        			room.setRedirectURL(redirectURL);
	        			
	        			
	        			roomList.add(room);
	        			
	        			for (Iterator<Element> iterMods = roomObject.elementIterator( "room_moderators" ); iterMods.hasNext(); ) {
	        				
	        				Element room_moderators = iterMods.next();
	        				
	        				for (Iterator<Element> iterMod = room_moderators.elementIterator( "room_moderator" ); iterMod.hasNext(); ) {
	    	        			
	        					Element room_moderator = iterMod.next();
	        					
	        					RoomModerators roomModerators = new RoomModerators();
	        					
	        					Long user_id = importLongType(room_moderator.element("user_id").getText());
	        					Boolean is_supermoderator = importBooleanType(room_moderator.element("is_supermoderator").getText());
	        					
	        					roomModerators.setDeleted("false");
	        					roomModerators.setRoomId(rooms_id);
	        					roomModerators.setUser(Usermanagement.getInstance().getUserById(user_id));
	        					roomModerators.setIsSuperModerator(is_supermoderator);
	        					
	        					roomModeratorList.add(roomModerators);
	        					
	        				}
	        				
	        			}
	        			
	        		}
	        		
	        	}
			}
			
			Map<String,List> returnObject = new HashMap<String,List>();
			
			returnObject.put("roomList",roomList);
			returnObject.put("roomModeratorList", roomModeratorList);
			
			return returnObject;
			
		} catch (Exception err) {
			log.error("[getRoomListByXML]",err);
		}
		return null;
	}
	
	private Integer importIntegerType(String value) {
		
		if (value.equals("null") || value.equals("")) {
			return null;
		}
		
		return Integer.valueOf(value).intValue();
		
	}
	
	private Long importLongType(String value) {
		
		if (value.equals("null") || value.equals("")) {
			return null;
		}
		
		return Long.valueOf(value).longValue();
		
	}
	
	private Boolean importBooleanType(String value) {
		
		if (value.equals("null") || value.equals("")) {
			return null;
		}
		
		return Boolean.valueOf(value).booleanValue();
		
	}
	
	
}
