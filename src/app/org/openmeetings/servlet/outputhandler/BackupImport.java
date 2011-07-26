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
import org.openmeetings.app.data.file.dao.FileExplorerItemDaoImpl;
import org.openmeetings.app.data.flvrecord.FlvRecordingDaoImpl;
import org.openmeetings.app.data.flvrecord.FlvRecordingMetaDataDaoImpl;
import org.openmeetings.app.data.user.Organisationmanagement;
import org.openmeetings.app.data.user.Statemanagement;
import org.openmeetings.app.data.user.Usermanagement;
import org.openmeetings.app.data.user.dao.PrivateMessageFolderDaoImpl;
import org.openmeetings.app.data.user.dao.PrivateMessagesDaoImpl;
import org.openmeetings.app.data.user.dao.UserContactsDaoImpl;
import org.openmeetings.app.data.user.dao.UsersDaoImpl;
import org.openmeetings.app.documents.GenerateImage;
import org.openmeetings.app.documents.GeneratePDF;
import org.openmeetings.app.documents.GenerateThumbs;
import org.openmeetings.app.persistence.beans.adresses.Adresses;
import org.openmeetings.app.persistence.beans.adresses.States;
import org.openmeetings.app.persistence.beans.basic.Configuration;
import org.openmeetings.app.persistence.beans.basic.LdapConfig;
import org.openmeetings.app.persistence.beans.basic.OmTimeZone;
import org.openmeetings.app.persistence.beans.calendar.Appointment;
import org.openmeetings.app.persistence.beans.calendar.MeetingMember;
import org.openmeetings.app.persistence.beans.domain.Organisation;
import org.openmeetings.app.persistence.beans.domain.Organisation_Users;
import org.openmeetings.app.persistence.beans.files.FileExplorerItem;
import org.openmeetings.app.persistence.beans.flvrecord.FlvRecording;
import org.openmeetings.app.persistence.beans.flvrecord.FlvRecordingMetaData;
import org.openmeetings.app.persistence.beans.rooms.RoomModerators;
import org.openmeetings.app.persistence.beans.rooms.Rooms;
import org.openmeetings.app.persistence.beans.rooms.Rooms_Organisation;
import org.openmeetings.app.persistence.beans.user.PrivateMessageFolder;
import org.openmeetings.app.persistence.beans.user.PrivateMessages;
import org.openmeetings.app.persistence.beans.user.UserContacts;
import org.openmeetings.app.persistence.beans.user.UserSipData;
import org.openmeetings.app.persistence.beans.user.Users;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.openmeetings.utils.math.CalendarPatterns;
import org.openmeetings.utils.stringhandlers.StringComparer;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class BackupImport extends HttpServlet {

	private static final Logger log = Red5LoggerFactory.getLogger(BackupImport.class, ScopeApplicationAdapter.webAppRootKey);

	private HashMap<Long, Long> usersMap = new HashMap <Long, Long>();
	private HashMap<Long, Long> organisationsMap = new HashMap <Long, Long>();
	private HashMap<Long, Long> appointmentsMap = new HashMap <Long, Long>();
	private HashMap<Long, Long> roomsMap = new HashMap <Long, Long>();
	private HashMap<Long, Long> messageFoldersMap = new HashMap <Long, Long>();
	private HashMap<Long, Long> userContactsMap = new HashMap <Long, Long>();
	private HashMap<Long, Long> fileExplorerItemsMap = new HashMap <Long, Long>();

	private enum Maps {USERS, ORGANISATIONS, APPOINTMENTS, ROOMS, MESSAGEFOLDERS, USERCONTACTS, FILEEXPLORERITEMS}; 

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
		            
		            
		            //Now check the recordings and import them
		            
		            File sourceDirRec = new File(completeName + File.separatorChar + "recordingFiles");
		            
		            
		            log.debug("sourceDirRec PATH " + sourceDirRec.getAbsolutePath());
		            
		            if (sourceDirRec.exists()) {
		            	
		            	File targetDirRec = new File(current_dir + "streams" + File.separatorChar 
												+ "hibernate" + File.separatorChar);
		            	
		            	copyDirectory(sourceDirRec,targetDirRec);
		            	
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
					
					/* #####################
					 * Import Recordings
					 * 
					 */
					String flvRecordingsListXML = completeName + File.separatorChar + "flvRecordings.xml";
					File flvRecordingsListFile = new File(flvRecordingsListXML);
					if (!flvRecordingsListFile.exists()) {
						log.debug("flvRecordingsListFile missing");
						//throw new Exception ("meetingmembersListFile missing");
					} else {
						this.importFlvRecordings(flvRecordingsListFile);
					}
					
					/* #####################
					 * Import Private Message Folders
					 * 
					 */
					String  privateMessageFoldersXML = completeName + File.separatorChar + "privateMessageFolder.xml";
					File privateMessageFoldersFile = new File(privateMessageFoldersXML);
					if (!privateMessageFoldersFile.exists()) {
						log.debug("privateMessageFoldersFile missing");
						//throw new Exception ("meetingmembersListFile missing");
					} else {
						this.importPrivateMessageFolders(privateMessageFoldersFile);
					}
					
					/* #####################
					 * Import Private Messages
					 * 
					 */
					String  privateMessagesXML = completeName + File.separatorChar + "privateMessages.xml";
					File privateMessagesFile = new File(privateMessagesXML);
					if (!privateMessagesFile.exists()) {
						log.debug("privateMessagesFile missing");
						//throw new Exception ("meetingmembersListFile missing");
					} else {
						this.importPrivateMessages(privateMessagesFile);
					}

					/* #####################
					 * Import User Contacts
					 * 
					 */
					String  userContactsXML = completeName + File.separatorChar + "userContacts.xml";
					File userContactsFile = new File(userContactsXML);
					if (!userContactsFile.exists()) {
						log.debug("userContactsFile missing");
						//throw new Exception ("meetingmembersListFile missing");
					} else {
						this.importUserContacts(userContactsFile);
					}
					
					/* #####################
					 * Import File-Explorer Items
					 * 
					 */
					String fileExplorerListXML = completeName + File.separatorChar + "fileExplorerItems.xml";
					File fileExplorerListFile = new File(fileExplorerListXML);
					if (!fileExplorerListFile.exists()) {
						log.debug("fileExplorerListFile missing");
						//throw new Exception ("meetingmembersListFile missing");
					} else {
						this.importFileExplorerItems(fileExplorerListFile);
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

	public void copyDirectory(File sourceLocation , File targetLocation)
    		throws IOException {
        
	 //log.debug("^^^^ "+sourceLocation.getName()+" || "+targetLocation.getName());
	 
        if (sourceLocation.isDirectory()) {
            if (!targetLocation.exists()) {
                targetLocation.mkdir();
            }
            
            String[] children = sourceLocation.list();
            for (int i=0; i<children.length; i++) {
                copyDirectory(new File(sourceLocation, children[i]),
                        new File(targetLocation, children[i]));
            }
        } else {
            
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
		
		this.getUsersByXML(userFile);
		
	}
	
	private void getUsersByXML (File userFile) {
		try {
			
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
	        			
	        			if (itemUsers.element("activatehash") != null) {
	        				us.setActivatehash(itemUsers.element("activatehash").getText());
	        			} else {
	        				us.setActivatehash("");
	        			}
	        			if (itemUsers.element("externalUserType") != null) {
	        				us.setExternalUserType(itemUsers.element("externalUserType").getText());
	        			} else {
	        				us.setExternalUserType("");
	        			}
	        			if (itemUsers.element("externalUserId") != null) {
	        				us.setExternalUserId(importLongType(itemUsers.element("externalUserId").getText()));
	        			} else {
	        				us.setExternalUserId(null);
	        			}
	        			if (itemUsers.element("resethash") != null) {
	        				us.setResethash(itemUsers.element("resethash").getText());
	        			} else {
	        				us.setResethash(null);
	        			}
	        			if (itemUsers.element("userOffers") != null) {
	        				us.setUserOffers(itemUsers.element("userOffers").getText());
	        			} else {
	        				us.setUserOffers("");
	        			}
	        			if (itemUsers.element("userSearchs") != null) {
	        				us.setUserSearchs(itemUsers.element("userSearchs").getText());
	        			} else {
	        				us.setUserSearchs("");
	        			}
	        			if (itemUsers.element("forceTimeZoneCheck") != null) {
	        				us.setForceTimeZoneCheck(importBooleanType(itemUsers.element("forceTimeZoneCheck").getText()));
	        			} else {
	        				us.setForceTimeZoneCheck(null);
	        			}
	        			if (itemUsers.element("lasttrans") != null) {
	        				us.setLasttrans(importLongType(itemUsers.element("lasttrans").getText()));
	        			} else {
	        				us.setLasttrans(null);
	        			}
	        			if (itemUsers.element("showContactData") != null) {
	        				us.setShowContactData(importBooleanType(itemUsers.element("showContactData").getText()));
	        			} else {
	        				us.setShowContactData(null);
	        			}
	        			if (itemUsers.element("showContactDataToContacts") != null) {
	        				us.setShowContactDataToContacts(importBooleanType(itemUsers.element("showContactDataToContacts").getText()));
	        			} else {
	        				us.setShowContactDataToContacts(null);
	        			}
	        			
	        			
	        			us.setPictureuri(itemUsers.element("pictureuri").getText());
	        			if (itemUsers.element("language_id").getText().length()>0)
	        				us.setLanguage_id(Long.valueOf(itemUsers.element("language_id").getText()));
	        				
	        			us.setStatus(importIntegerType(itemUsers.element("status").getText()));
	        			us.setRegdate(CalendarPatterns.parseDate(itemUsers.element("regdate").getText()));
	        			us.setTitle_id(importIntegerType(itemUsers.element("title_id").getText()));
	        			us.setLevel_id(importLongType(itemUsers.element("level_id").getText()));
	        			
	        			//UserSIP Data
	        			if (itemUsers.element("sip_username") != null 
	        					&& itemUsers.element("sip_userpass") != null
	        					&& itemUsers.element("sip_authid") != null) {
	        				UserSipData userSipData = new UserSipData();
	        				userSipData.setUsername(itemUsers.element("sip_username").getText());
	        				userSipData.setUsername(itemUsers.element("sip_userpass").getText());
	        				userSipData.setUsername(itemUsers.element("sip_authid").getText());
	        				us.setUserSipData(userSipData);
	        			}
	        				
	        			
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
	        			if (st == null) {
	        				Statemanagement.getInstance().getStateById(1L);
	        			}
	        			
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
	        			
	        			HashSet<Organisation_Users> orgUsers = new HashSet<Organisation_Users>();
	        			
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
		        				
		        				orgUsers.add(orgUser);
		        				
	        				}
	        				
	        			}
	        			
	        			//check if login does already exists
	        			Users storedUser = Usermanagement.getInstance().getUserByLoginOrEmail(us.getLogin());
	        			
	        			if (storedUser != null) {
	        				log.info("A user with the given login does already exist "+us.getLogin());
	        				return;
	        			}
	        			
	        			storedUser = Usermanagement.getInstance().getUserByLoginOrEmail(email);
	        			
	        			if (storedUser != null) {
	        				log.info("A user with the given email as login does already exist "+email);
	        				return;
	        			}
	        			
        				log.debug("Import User ID "+us.getUser_id());
        				us.setUser_id(null);
        				us.setStarttime(new Date());
        				Long actualNewUserId = Usermanagement.getInstance().addUserBackup(us);
	        			usersMap.put(us.getUser_id(), actualNewUserId);
        				
        				for (Iterator<Organisation_Users> orgUserIterator = orgUsers.iterator();orgUserIterator.hasNext();) {
        					
        					Organisation_Users organisationUsers = orgUserIterator.next();
        					
        					organisationUsers.setUser_id(actualNewUserId);
        					
        					Organisationmanagement.getInstance().addOrganisationUserObj(organisationUsers);
        					
        				}
	        			
	        		}
	        		
	        	}
	        }
	        
		} catch (Exception err) {
			log.error("[getUsersByXML]",err);
		}
	}
	

	private void importFlvRecordings(File flvRecordingsListFile) throws Exception {
		
		List<FlvRecording> flvRecordings = this.getFlvRecordings(flvRecordingsListFile);
		
		for (FlvRecording flvRecording : flvRecordings) {
			
			Long flvRecordingId = FlvRecordingDaoImpl.getInstance().addFlvRecordingObj(flvRecording);
			
			for (FlvRecordingMetaData flvRecordingMetaData : flvRecording.getFlvRecordingMetaData()) {
				
				FlvRecording flvRecordingSaved = FlvRecordingDaoImpl.getInstance().getFlvRecordingById(flvRecordingId);
				
				flvRecordingMetaData.setFlvRecording(flvRecordingSaved);
				
				FlvRecordingMetaDataDaoImpl.getInstance().addFlvRecordingMetaDataObj(flvRecordingMetaData);
				
			}
			
		}
		
	}

	private List<FlvRecording> getFlvRecordings(File flvRecordingsListFile) {
		try {

			List<FlvRecording> flvList = new LinkedList<FlvRecording>();
			
			SAXReader reader = new SAXReader();
			Document document = reader.read(flvRecordingsListFile);
			
			Element root = document.getRootElement();
			
			for (Iterator<Element> i = root.elementIterator(); i.hasNext(); ) {
				
	        	Element itemObject =  i.next();
	        	
	        	if (itemObject.getName().equals("flvrecordings")) {
	        		
	        		for (Iterator<Element> innerIter = itemObject.elementIterator( "flvrecording" ); innerIter.hasNext(); ) {
	        			
	        			Element flvObject = innerIter.next();
	        			
	        			String alternateDownload = flvObject.element("alternateDownload").getText();
	        			String comment = flvObject.element("comment").getText();
	        			String deleted = flvObject.element("deleted").getText();
	        			String fileHash = flvObject.element("fileHash").getText();
	        			String fileName = flvObject.element("fileName").getText();
	        			String previewImage = flvObject.element("previewImage").getText();
	        			String recorderStreamId = flvObject.element("recorderStreamId").getText();
	        			Long fileSize = importLongType(flvObject.element("fileSize").getText());
	        			Integer flvHeight = importIntegerType(flvObject.element("flvHeight").getText());
	        			Integer flvWidth = importIntegerType(flvObject.element("flvWidth").getText());
	        			Integer height = importIntegerType(flvObject.element("height").getText());
	        			Integer width = importIntegerType(flvObject.element("width").getText());
	        			Long insertedBy = getNewId(importLongType(flvObject.element("insertedBy").getText()), Maps.USERS);
	        			Long organization_id = getNewId(importLongType(flvObject.element("organization_id").getText()), Maps.ORGANISATIONS);
	        			Long ownerId = getNewId(importLongType(flvObject.element("ownerId").getText()), Maps.USERS);
	        			Long parentFileExplorerItemId = getNewId(importLongType(flvObject.element("parentFileExplorerItemId").getText()), Maps.FILEEXPLORERITEMS);
	        			Integer progressPostProcessing = importIntegerType(flvObject.element("progressPostProcessing").getText());
	        			Long room_id = getNewId(importLongType(flvObject.element("room_id").getText()), Maps.ROOMS);
	        			Date inserted = CalendarPatterns.parseDateWithHour(flvObject.element("inserted").getText());
	        			Boolean isFolder = importBooleanType(flvObject.element("isFolder").getText());
	        			Boolean isImage = importBooleanType(flvObject.element("isImage").getText());
	        			Boolean isInterview = importBooleanType(flvObject.element("isInterview").getText());
	        			Boolean isPresentation = importBooleanType(flvObject.element("isPresentation").getText());
	        			Boolean isRecording = importBooleanType(flvObject.element("isRecording").getText());
	        			Date recordEnd = CalendarPatterns.parseDateWithHour(flvObject.element("recordEnd").getText());
	        			Date recordStart = CalendarPatterns.parseDateWithHour(flvObject.element("recordStart").getText());
	        			
	        			
	        			FlvRecording flvRecording = new FlvRecording();
	        			flvRecording.setAlternateDownload(alternateDownload);
	        			flvRecording.setComment(comment);
	        			flvRecording.setFileHash(fileHash);
	        			flvRecording.setFileName(fileName);
	        			flvRecording.setPreviewImage(previewImage);
	        			flvRecording.setRecorderStreamId(recorderStreamId);
	        			flvRecording.setFileSize(fileSize);
	        			flvRecording.setFlvHeight(flvHeight);
	        			flvRecording.setFlvWidth(flvWidth);
	        			flvRecording.setHeight(height);
	        			flvRecording.setWidth(width);
	        			flvRecording.setInsertedBy(insertedBy);
	        			flvRecording.setOrganization_id(organization_id);
	        			flvRecording.setOwnerId(ownerId);
	        			flvRecording.setParentFileExplorerItemId(parentFileExplorerItemId);
	        			flvRecording.setProgressPostProcessing(progressPostProcessing);
	        			flvRecording.setRoom_id(room_id);
	        			flvRecording.setInserted(inserted);
	        			flvRecording.setIsFolder(isFolder);
	        			flvRecording.setIsImage(isImage);
	        			flvRecording.setIsInterview(isInterview);
	        			flvRecording.setIsPresentation(isPresentation);
	        			flvRecording.setIsRecording(isRecording);
	        			flvRecording.setRecordEnd(recordEnd);
	        			flvRecording.setRecordStart(recordStart);
	        			flvRecording.setDeleted(deleted);
	        			
	        			flvRecording.setFlvRecordingMetaData(new LinkedList<FlvRecordingMetaData>());
	        			
	        			
	        			Element flvrecordingmetadatas = flvObject.element("flvrecordingmetadatas");
	        			
	        			for (Iterator<Element> innerIterMetas = flvrecordingmetadatas.elementIterator( "flvrecordingmetadata" ); innerIterMetas.hasNext(); ) {
	        				
	        				Element flvrecordingmetadataObj = innerIterMetas.next();
	        				
	        				String freeTextUserName = flvrecordingmetadataObj.element("freeTextUserName").getText();
	        				String fullWavAudioData = flvrecordingmetadataObj.element("fullWavAudioData").getText();
	        				String streamName = flvrecordingmetadataObj.element("streamName").getText();
	        				String wavAudioData = flvrecordingmetadataObj.element("wavAudioData").getText();
	        				Integer initialGapSeconds = importIntegerType(flvrecordingmetadataObj.element("initialGapSeconds").getText());
	        				Long insertedBy1 = importLongType(flvrecordingmetadataObj.element("insertedBy").getText());
	        				Integer interiewPodId = importIntegerType(flvrecordingmetadataObj.element("interiewPodId").getText());
	        				Boolean audioIsValid = importBooleanType(flvrecordingmetadataObj.element("audioIsValid").getText());
	        				Date inserted1 = CalendarPatterns.parseDateWithHour(flvrecordingmetadataObj.element("inserted").getText());
	        				Boolean isAudioOnly = importBooleanType(flvrecordingmetadataObj.element("isAudioOnly").getText());
	        				Boolean isScreenData = importBooleanType(flvrecordingmetadataObj.element("isScreenData").getText());
	        				Boolean isVideoOnly = importBooleanType(flvrecordingmetadataObj.element("isVideoOnly").getText());
	        				Date recordEnd1 = CalendarPatterns.parseDateWithHour(flvrecordingmetadataObj.element("recordEnd").getText());
	        				Date recordStart1 = CalendarPatterns.parseDateWithHour(flvrecordingmetadataObj.element("recordStart").getText());
	        				Date updated = CalendarPatterns.parseDateWithHour(flvrecordingmetadataObj.element("updated").getText());
	        				
	        				FlvRecordingMetaData flvrecordingmetadata = new FlvRecordingMetaData();
	        				flvrecordingmetadata.setFreeTextUserName(freeTextUserName);
	        				flvrecordingmetadata.setFullWavAudioData(fullWavAudioData);
	        				flvrecordingmetadata.setStreamName(streamName);
	        				flvrecordingmetadata.setWavAudioData(wavAudioData);
	        				flvrecordingmetadata.setInitialGapSeconds(initialGapSeconds);
	        				flvrecordingmetadata.setInsertedBy(insertedBy1);
	        				flvrecordingmetadata.setInteriewPodId(interiewPodId);
	        				flvrecordingmetadata.setAudioIsValid(audioIsValid);
	        				flvrecordingmetadata.setInserted(inserted1);
	        				flvrecordingmetadata.setIsAudioOnly(isAudioOnly);
	        				flvrecordingmetadata.setIsScreenData(isScreenData);
	        				flvrecordingmetadata.setIsVideoOnly(isVideoOnly);
	        				flvrecordingmetadata.setRecordEnd(recordEnd1);
	        				flvrecordingmetadata.setRecordStart(recordStart1);
	        				flvrecordingmetadata.setUpdated(updated);
	        				flvrecordingmetadata.setDeleted("false");
	        				
	        				flvRecording.getFlvRecordingMetaData().add(flvrecordingmetadata);
	        				
	        			}
	        			
	        			flvList.add(flvRecording);
	        			
	        		}
	        		
	        	}
	        }
	        
	        return flvList;
			
		} catch (Exception err) {
			log.error("[getFlvRecordings]",err);
		}
		return null;
	}
	

	private void importPrivateMessageFolders(File privateMessageFoldersFile) throws Exception {
		
		List<PrivateMessageFolder> privateMessageFolders = this.getPrivateMessageFoldersByXML(privateMessageFoldersFile);
		
		for (PrivateMessageFolder privateMessageFolder : privateMessageFolders) {
			
			Long folderId = privateMessageFolder.getPrivateMessageFolderId();
			PrivateMessageFolder storedFolder = PrivateMessageFolderDaoImpl.getInstance().getPrivateMessageFolderById(folderId);
			if (storedFolder == null){
    			privateMessageFolder.setPrivateMessageFolderId(0);
				Long newFolderId = PrivateMessageFolderDaoImpl.getInstance().addPrivateMessageFolderObj(privateMessageFolder);
				messageFoldersMap.put(folderId, newFolderId);
			}
		}
		
	}

	private List<PrivateMessageFolder> getPrivateMessageFoldersByXML(
											File privateMessageFoldersFile) {
		try {
			
			List<PrivateMessageFolder> pmfList = new LinkedList<PrivateMessageFolder>();
			
			SAXReader reader = new SAXReader();
			Document document = reader.read(privateMessageFoldersFile);
			
			Element root = document.getRootElement();
			
			for (Iterator<Element> i = root.elementIterator(); i.hasNext(); ) {
	        	Element itemObject =  i.next();
	        	if (itemObject.getName().equals("privatemessagefolders")) {
	        		
	        		for (Iterator<Element> innerIter = itemObject.elementIterator( "privatemessagefolder" ); innerIter.hasNext(); ) {
	        			
	        			Element pmfObject = innerIter.next();
	        			
	        			String folderName = pmfObject.element("folderName").getText();
	        			Long userId = getNewId(importLongType(pmfObject.element("userId").getText()), Maps.USERS);
	        			Long privateMessageFolderId = importLongType(pmfObject.element("privateMessageFolderId").getText());
	        			
	        			PrivateMessageFolder privateMessageFolder = new PrivateMessageFolder();
	        			privateMessageFolder.setFolderName(folderName);
	        			privateMessageFolder.setUserId(userId);
	        			privateMessageFolder.setPrivateMessageFolderId(privateMessageFolderId);
	        			
	        			pmfList.add(privateMessageFolder);
	        			
	        		}
	        		
	        	}
	        }
	        
	        return pmfList;
			
		} catch (Exception err) {
			log.error("[getPrivateMessageFoldersByXML]",err);
		}
		return null;
	}
	

	private void importPrivateMessages(File privateMessagesFile) throws Exception {
		
		List<PrivateMessages> pmList = this.getPrivateMessagesByXML(privateMessagesFile);
		
		for (PrivateMessages pm : pmList) {
			
			PrivateMessagesDaoImpl.getInstance().addPrivateMessageObj(pm);
			
		}
		
	}
	
	private List<PrivateMessages> getPrivateMessagesByXML(
										File privateMessagesFile) {
		try {
			
			List<PrivateMessages> pmList = new LinkedList<PrivateMessages>();
			
			SAXReader reader = new SAXReader();
			Document document = reader.read(privateMessagesFile);
			
			Element root = document.getRootElement();
			
			for (Iterator<Element> i = root.elementIterator(); i.hasNext(); ) {
	        	Element itemObject =  i.next();
	        	if (itemObject.getName().equals("privatemessages")) {
	        		
	        		for (Iterator<Element> innerIter = itemObject.elementIterator( "privatemessage" ); innerIter.hasNext(); ) {
	        			
	        			Element pmObject = innerIter.next();
	        			
	        			String message = pmObject.element("message").getText();
	        			String subject = pmObject.element("subject").getText();
	        			Long privateMessageFolderId = getNewId(importLongType(pmObject.element("privateMessageFolderId").getText()), Maps.MESSAGEFOLDERS);
	        			Long userContactId = getNewId(importLongType(pmObject.element("userContactId").getText()), Maps.USERCONTACTS);
	        			Long parentMessage = importLongType(pmObject.element("parentMessage").getText());
	        			Boolean bookedRoom = importBooleanType(pmObject.element("bookedRoom").getText());
	        			Users from = Usermanagement.getInstance().getUserById(getNewId(importLongType(pmObject.element("from").getText()), Maps.USERS));
	        			Users to = Usermanagement.getInstance().getUserById(getNewId(importLongType(pmObject.element("to").getText()), Maps.USERS));
	        			Date inserted = CalendarPatterns.parseDateWithHour(pmObject.element("inserted").getText());
	        			Boolean isContactRequest = importBooleanType(pmObject.element("isContactRequest").getText());
	        			Boolean isRead = importBooleanType(pmObject.element("isRead").getText());
	        			Boolean isTrash = importBooleanType(pmObject.element("isTrash").getText());
	        			Users owner = Usermanagement.getInstance().getUserById(getNewId(importLongType(pmObject.element("owner").getText()), Maps.USERS));
	        			Rooms room = Roommanagement.getInstance().getRoomById(getNewId(importLongType(pmObject.element("room").getText()), Maps.ROOMS));
	        			
	        			PrivateMessages pm = new PrivateMessages();
	        			pm.setMessage(message);
	        			pm.setSubject(subject);
	        			pm.setPrivateMessageFolderId(privateMessageFolderId);
	        			pm.setUserContactId(userContactId);
	        			pm.setParentMessage(parentMessage);
	        			pm.setBookedRoom(bookedRoom);
	        			pm.setFrom(from);
	        			pm.setTo(to);
	        			pm.setInserted(inserted);
	        			pm.setIsContactRequest(isContactRequest);
	        			pm.setIsRead(isRead);
	        			pm.setIsTrash(isTrash);
	        			pm.setOwner(owner);
	        			pm.setRoom(room);
	        			
	        			pmList.add(pm);
	        			
	        		}
	        		
	        	}
	        }
	        
	        return pmList;
			
		} catch (Exception err) {
			log.error("[getPrivateMessagesByXML]",err);
		}
		return null;
	}
	

	private void importUserContacts(File userContactsFile) throws Exception {
		
		List<UserContacts> ucList = this.getUserContactsByXML(userContactsFile);
		
		for (UserContacts uc : ucList) {
			Long userContactId = uc.getUserContactId();
			UserContacts storedUC = UserContactsDaoImpl.getInstance().getUserContacts(userContactId);
			
			if (storedUC == null){
				uc.setUserContactId(0);
				Long newId = UserContactsDaoImpl.getInstance().addUserContactObj(uc);
				this.userContactsMap.put(userContactId, newId);
			}
		}
	}

	private List<UserContacts> getUserContactsByXML(File userContactsFile) {
		try {
			
			List<UserContacts> ucList = new LinkedList<UserContacts>();
			
			SAXReader reader = new SAXReader();
			Document document = reader.read(userContactsFile);
			
			Element root = document.getRootElement();
			
			for (Iterator<Element> i = root.elementIterator(); i.hasNext(); ) {
	        	Element itemObject =  i.next();
	        	if (itemObject.getName().equals("usercontacts")) {
	        		
	        		for (Iterator<Element> innerIter = itemObject.elementIterator( "usercontact" ); innerIter.hasNext(); ) {
	        			
	        			Element usercontact = innerIter.next();
	        			
	        			String hash = usercontact.element("hash").getText();
	        			Users contact = Usermanagement.getInstance().getUserById(getNewId(importLongType(usercontact.element("contact").getText()), Maps.USERS));
	        			Users owner = Usermanagement.getInstance().getUserById(getNewId(importLongType(usercontact.element("owner").getText()), Maps.USERS));
	        			Boolean pending = importBooleanType(usercontact.element("pending").getText());
	        			Boolean shareCalendar = importBooleanType(usercontact.element("shareCalendar").getText());
	        			Long userContactId = importLongType(usercontact.element("userContactId").getText());
	        			
	        			UserContacts userContacts = new UserContacts();
	        			userContacts.setHash(hash);
	        			userContacts.setContact(contact);
	        			userContacts.setOwner(owner);
	        			userContacts.setPending(pending);
	        			userContacts.setShareCalendar(shareCalendar);
	        			userContacts.setUserContactId(userContactId);
	        			
	        			ucList.add(userContacts);
	        			
	        		}
	        		
	        	}
	        }
	        
	        return ucList;
			
		} catch (Exception err) {
			log.error("[getUserContactsByXML]",err);
		}
		return null;
	}
	

	private void importOrganizsations(File orgFile) throws Exception {
		
		List<Organisation> orgList = this.getOrganisationsByXML(orgFile);
		
		for (Organisation org : orgList) {
			Long orgId = org.getOrganisation_id();
			
			org.setOrganisation_id(null);
			Long newOrgID = Organisationmanagement.getInstance().addOrganisationObj(org);
			organisationsMap.put(orgId, newOrgID);
			
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
			
			//We need to reset this as openJPA reject to store them otherwise
			ma.setMeetingMemberId(null);
			
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
	        			Long userid = getNewId(importLongType(appointmentsObject.element("userid").getText()), Maps.USERS);
	        			Long appointment = getNewId(importLongType(appointmentsObject.element("appointment").getText()), Maps.APPOINTMENTS);
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
			Long appId = appointment.getAppointmentId();
				
			//We need to reset this as openJPA reject to store them otherwise
			appointment.setAppointmentId(null);
			
			Long newAppId =  AppointmentDaoImpl.getInstance().addAppointmentObj(appointment);
			appointmentsMap.put(appId, newAppId);
			
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
	        			Long room_id = getNewId(importLongType(appointmentsObject.element("room_id").getText()), Maps.ROOMS);
	        			String icalId = appointmentsObject.element("icalId").getText();
	        			Long language_id = importLongType(appointmentsObject.element("language_id").getText());
	        			Boolean isPasswordProtected = importBooleanType(appointmentsObject.element("isPasswordProtected").getText());
	        			String password = appointmentsObject.element("password").getText();
	        			Long users_id = getNewId(importLongType(appointmentsObject.element("users_id").getText()), Maps.USERS);
	        			
	        			
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
	        			app.setUserId(Usermanagement.getInstance().getUserById(users_id));
	        			
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
			
			//We need to reset this as openJPA reject to store them otherwise
			rooms_Organisation.setRooms_organisation_id(null);
			
			Roommanagement.getInstance().addRoomOrganisation(rooms_Organisation);
				
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
	        			Long organisation_id = getNewId(importLongType(orgRoomObject.element("organisation_id").getText()), Maps.ORGANISATIONS);
	        			Long rooms_id = getNewId(importLongType(orgRoomObject.element("rooms_id").getText()), Maps.ROOMS);
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
		
		this.getRoomListByXML(roomFile);
		
	}
	
	private void getRoomListByXML(File roomFile) {
		try {
			
			//List<Rooms> roomList = new LinkedList<Rooms>();
			
			//List<RoomModerators> roomModeratorList = new LinkedList<RoomModerators>();
			
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
	        			
	        			Long ownerId = null;
	        			if (roomObject.element("ownerid") != null) {
	        				ownerId = getNewId(importLongType(roomObject.element("ownerid").getText()), Maps.USERS);
	        			}
	        			
	        			Boolean ispublic = false;
	        			if (roomObject.element("ispublic") != null) {
	        				ispublic = importBooleanType(roomObject.element("ispublic").getText());
	        			}
	        			
	        			Boolean waitForRecording = false;
	        			if (roomObject.element("waitForRecording") != null) {
	        				waitForRecording = importBooleanType(roomObject.element("waitForRecording").getText());
	        			}

	        			Boolean hideTopBar = false;
	        			if (roomObject.element("hideTopBar") != null) {
	        				hideTopBar = importBooleanType(roomObject.element("hideTopBar").getText());
	        			}
	        			
	        			Boolean isClosed = false;
	        			if (roomObject.element("isClosed") != null) {
	        				isClosed = importBooleanType(roomObject.element("isClosed").getText());
	        			}
	        			
	        			Boolean allowRecording = false;
	        			if (roomObject.element("allowRecording") != null) {
	        				allowRecording = importBooleanType(roomObject.element("allowRecording").getText());
	        			}
	        			
	        			String redirectURL = "";
	        			if (roomObject.element("redirectURL") != null) {
	        				redirectURL = roomObject.element("redirectURL").getText();
	        			}
	        			
	        			Rooms room = new Rooms();
	        			room.setRooms_id(rooms_id);
	        			room.setOwnerId(ownerId);
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
	        			room.setWaitForRecording(waitForRecording);
	        			room.setHideTopBar(hideTopBar);
	        			room.setAllowRecording(allowRecording);
	        			
	        			Long roomId = room.getRooms_id();
	        			
        				//We need to reset this as openJPA reject to store them otherwise
        				room.setRooms_id(null);
        				
        				Long newRoomId = Roommanagement.getInstance().addRoom(room);
        				roomsMap.put(roomId, newRoomId);
	        			
	        			for (Iterator<Element> iterMods = roomObject.elementIterator( "room_moderators" ); iterMods.hasNext(); ) {
	        				
	        				Element room_moderators = iterMods.next();
	        				
	        				for (Iterator<Element> iterMod = room_moderators.elementIterator( "room_moderator" ); iterMod.hasNext(); ) {
	    	        			
	        					Element room_moderator = iterMod.next();
	        					
	        					RoomModerators roomModerators = new RoomModerators();
	        					
	        					Long user_id = getNewId(importLongType(room_moderator.element("user_id").getText()), Maps.USERS);
	        					Boolean is_supermoderator = importBooleanType(room_moderator.element("is_supermoderator").getText());
	        					
	        					roomModerators.setDeleted("false");
	        					roomModerators.setRoomId(getNewId(rooms_id, Maps.ROOMS));
	        					roomModerators.setUser(Usermanagement.getInstance().getUserById(user_id));
	        					roomModerators.setIsSuperModerator(is_supermoderator);
	        					
        						RoomModeratorsDaoImpl.getInstance().addRoomModeratorByObj(roomModerators);
	        						
	        				}
	        				
	        			}
	        			
	        		}
	        		
	        	}
			}
			
		} catch (Exception err) {
			log.error("[getRoomListByXML]",err);
		}
	}
	
	
	private void importFileExplorerItems(File fileExplorerItemsListFile) throws Exception {
		
		List<FileExplorerItem> fileExplorerItems = this.getFileExplorerItems(fileExplorerItemsListFile);
		
		for (FileExplorerItem fileExplorerItem : fileExplorerItems) {
			
			//We need to reset this as openJPA reject to store them otherwise
			long itemId = fileExplorerItem.getFileExplorerItemId();
			
			fileExplorerItem.setFileExplorerItemId(0);
			long newItemId = FileExplorerItemDaoImpl.getInstance().addFileExplorerItem(fileExplorerItem);
			fileExplorerItemsMap.put(itemId, newItemId);
			
		}
		
	}
	
	private List<FileExplorerItem> getFileExplorerItems(File fileExplorerItemsListFile) {
		try {

			List<FileExplorerItem> fileExplorerItemsList = new LinkedList<FileExplorerItem>();
			
			SAXReader reader = new SAXReader();
			Document document = reader.read(fileExplorerItemsListFile);
			
			Element root = document.getRootElement();
			
			for (Iterator<Element> i = root.elementIterator(); i.hasNext(); ) {
				
	        	Element itemObject =  i.next();
	        	
	        	if (itemObject.getName().equals("fileExplorerItems")) {
	        		
	        		for (Iterator<Element> innerIter = itemObject.elementIterator( "fileExplorerItem" ); innerIter.hasNext(); ) {
	        			
	        			Element fileExplorerItemObj = innerIter.next();
	        			
	        			Long fileExplorerItemId = importLongType(fileExplorerItemObj.element("fileExplorerItemId").getText());
	        			String fileName = fileExplorerItemObj.element("fileName").getText();
	        			String fileHash = fileExplorerItemObj.element("fileHash").getText();
	        			Long parentFileExplorerItemId = importLongType(fileExplorerItemObj.element("parentFileExplorerItemId").getText());
	        			Long room_id = getNewId(importLongType(fileExplorerItemObj.element("room_id").getText()), Maps.ROOMS);
	        			Long ownerId = getNewId(importLongType(fileExplorerItemObj.element("ownerId").getText()), Maps.USERS);
	        			Boolean isFolder = importBooleanType(fileExplorerItemObj.element("isFolder").getText());
	        			Boolean isImage = importBooleanType(fileExplorerItemObj.element("isImage").getText());
	        			Boolean isPresentation = importBooleanType(fileExplorerItemObj.element("isPresentation").getText());
	        			Boolean isVideo = importBooleanType(fileExplorerItemObj.element("isVideo").getText());
	        			Long insertedBy = getNewId(importLongType(fileExplorerItemObj.element("insertedBy").getText()), Maps.USERS);
	        			Date inserted = CalendarPatterns.parseDateWithHour(fileExplorerItemObj.element("inserted").getText());
	        			Date updated = CalendarPatterns.parseDateWithHour(fileExplorerItemObj.element("updated").getText());
	        			String deleted = fileExplorerItemObj.element("deleted").getText();
	        			Long fileSize = importLongType(fileExplorerItemObj.element("fileSize").getText());
	        			Integer flvWidth = importIntegerType(fileExplorerItemObj.element("flvWidth").getText());
	        			Integer flvHeight = importIntegerType(fileExplorerItemObj.element("flvHeight").getText());
	        			String previewImage = fileExplorerItemObj.element("previewImage").getText();
	        			String wmlFilePath = fileExplorerItemObj.element("wmlFilePath").getText();
	        			Boolean isStoredWmlFile = importBooleanType(fileExplorerItemObj.element("isStoredWmlFile").getText());
	        			Boolean isChart = importBooleanType(fileExplorerItemObj.element("isChart").getText());
	        			
	        			FileExplorerItem fileExplorerItem = new FileExplorerItem();
	        			fileExplorerItem.setFileExplorerItemId(fileExplorerItemId);
	        			fileExplorerItem.setFileName(fileName);
	        			fileExplorerItem.setFileHash(fileHash);
	        			fileExplorerItem.setParentFileExplorerItemId(parentFileExplorerItemId);
	        			fileExplorerItem.setRoom_id(room_id);
	        			fileExplorerItem.setOwnerId(ownerId);
	        			fileExplorerItem.setIsFolder(isFolder);
	        			fileExplorerItem.setIsImage(isImage);
	        			fileExplorerItem.setIsPresentation(isPresentation);
	        			fileExplorerItem.setIsVideo(isVideo);
	        			fileExplorerItem.setInsertedBy(insertedBy);
	        			fileExplorerItem.setInserted(inserted);
	        			fileExplorerItem.setUpdated(updated);
	        			fileExplorerItem.setDeleted(deleted);
	        			fileExplorerItem.setFileSize(fileSize);
	        			fileExplorerItem.setFlvWidth(flvWidth);
	        			fileExplorerItem.setFlvHeight(flvHeight);
	        			fileExplorerItem.setPreviewImage(previewImage);
	        			fileExplorerItem.setWmlFilePath(wmlFilePath);
	        			fileExplorerItem.setIsStoredWmlFile(isStoredWmlFile);
	        			fileExplorerItem.setIsChart(isChart);
	        			
	        			fileExplorerItemsList.add(fileExplorerItem);
	        			
	        		}
	        		
	        	}
	        }
	        
	        return fileExplorerItemsList;
			
		} catch (Exception err) {
			log.error("[getFileExplorerItems]",err);
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

	
	private Long getNewId(Long oldId, Maps map){
		Long newId = oldId;
		switch (map) {
		case USERS: 
			if (usersMap.get(oldId) != null)
				newId =  usersMap.get(oldId);
			break;
		case ORGANISATIONS:
			if (organisationsMap.get(oldId) != null)
				newId = organisationsMap.get(oldId);
			break;
		case APPOINTMENTS:
			if (appointmentsMap.get(oldId) != null)
				newId = appointmentsMap.get(oldId);
			break;
		case ROOMS:
			if (roomsMap.get(oldId) != null)
				newId = roomsMap.get(oldId);
			break;
		case MESSAGEFOLDERS:
			if (messageFoldersMap.get(oldId) != null)
				newId = messageFoldersMap.get(oldId);
			break;
		case USERCONTACTS:
			if (userContactsMap.get(oldId) != null)
				newId = userContactsMap.get(oldId);
			break;
		case FILEEXPLORERITEMS:
			if (fileExplorerItemsMap.get(oldId) != null)
				newId = fileExplorerItemsMap.get(oldId);
			break;
		default:
			break;
		}
		return newId;
	}
	
}
