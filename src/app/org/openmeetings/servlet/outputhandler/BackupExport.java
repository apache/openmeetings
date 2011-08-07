package org.openmeetings.servlet.outputhandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openmeetings.app.data.user.dao.PrivateMessageFolderDaoImpl;
import org.openmeetings.app.data.user.dao.PrivateMessagesDaoImpl;
import org.openmeetings.app.data.user.dao.UserContactsDaoImpl;
import org.openmeetings.app.data.user.dao.UsersDaoImpl;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.openmeetings.app.data.basic.AuthLevelmanagement;
import org.openmeetings.app.data.basic.Sessionmanagement;
import org.openmeetings.app.data.basic.dao.LdapConfigDaoImpl;
import org.openmeetings.app.data.calendar.daos.AppointmentDaoImpl;
import org.openmeetings.app.data.calendar.daos.MeetingMemberDaoImpl;
import org.openmeetings.app.data.conference.Roommanagement;
import org.openmeetings.app.data.conference.dao.RoomModeratorsDaoImpl;
import org.openmeetings.app.data.file.dao.FileExplorerItemDaoImpl;
import org.openmeetings.app.data.flvrecord.FlvRecordingDaoImpl;
import org.openmeetings.app.data.flvrecord.FlvRecordingMetaDataDaoImpl;
import org.openmeetings.app.data.user.Organisationmanagement;
import org.openmeetings.app.data.user.Usermanagement;
import org.openmeetings.app.persistence.beans.basic.LdapConfig;
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
import org.openmeetings.app.persistence.beans.user.Users;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.openmeetings.utils.math.CalendarPatterns;
import org.slf4j.Logger;
import org.red5.logging.Red5LoggerFactory;

/**
 * 
 * @author sebastianwagner
 *
 */
public class BackupExport extends HttpServlet {

	private static final Logger log = Red5LoggerFactory.getLogger(BackupExport.class, ScopeApplicationAdapter.webAppRootKey);

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void service(HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) throws ServletException,
			IOException {

		try {
			String sid = httpServletRequest.getParameter("sid");
			if (sid == null) {
				sid = "default";
			}
			log.debug("sid: " + sid);
			
			Long users_id = Sessionmanagement.getInstance().checkSession(sid);
			Long user_level = Usermanagement.getInstance().getUserLevelByID(
					users_id);

			log.debug("users_id: " + users_id);
			log.debug("user_level: " + user_level);

			if (AuthLevelmanagement.getInstance().checkAdminLevel(user_level)) {
			//if (true) {
				
				String includeFileOption = httpServletRequest.getParameter("includeFileOption");
				if (includeFileOption == null) {
					includeFileOption = "yes";
				}
				
				String moduleName = httpServletRequest.getParameter("moduleName");
				if (moduleName == null) {
					moduleName = "moduleName";
				}
				log.debug("moduleName: " + moduleName);
				
				if (moduleName.equals("backup")) {
					
					/* #####################
					 * Create Base Folder structure
					 */
					
					String current_dir = getServletContext().getRealPath("/");
					String working_dir = current_dir + "upload"
							+ File.separatorChar + "backup"
							+ File.separatorChar;
					File working_dirFile = new File(working_dir);
					
					if (!working_dirFile.exists()) {
						working_dirFile.mkdir();
					}
					
					String dateString = "backup_"+CalendarPatterns.getTimeForStreamId(new Date());
					
					String backup_file = working_dir + File.separatorChar + dateString;
					
					String backup_dir = backup_file + File.separatorChar;
											
					File backup_dirFile = new File(backup_dir);
					
					if (!backup_dirFile.exists()) {
						backup_dirFile.mkdir();
					}
					
					
					/* #####################
					 * Backup Organizations
					 */
					List<Organisation> orgList = Organisationmanagement.getInstance().getOrganisations(3L);
					
					if (orgList != null) {
						Document doc = this.createOrgDocument(orgList);
						
						String orgListXML = backup_dir + "organizations.xml";

						FileOutputStream fos = new FileOutputStream(orgListXML);
						
						this.serializetoXML(fos, "UTF-8", doc);
						
					}
					
					
					/* #####################
					 * Backup Users
					 */
					
					List<Users> uList = UsersDaoImpl.getInstance().getAllUsersDeleted();
					
					if (uList != null) {
						
						log.debug("Number of Users to be deleted "+uList.size());
						
						Document doc = this.createDocument(uList);
						
						String userListXML = backup_dir + "users.xml";

						FileOutputStream fos = new FileOutputStream(userListXML);
						
						this.serializetoXML(fos, "UTF-8", doc);
						
					}
					
					
					/* #####################
					 * Backup Room
					 */
					List<Rooms> roomList = Roommanagement.getInstance().getBackupRooms();
					
					if (roomList != null) {
						Document doc = this.createRoomsDocument(roomList);
						
						String roomListXML = backup_dir + "rooms.xml";

						FileOutputStream fos = new FileOutputStream(roomListXML);
						
						this.serializetoXML(fos, "UTF-8", doc);
						
					}
					
					/* #####################
					 * Backup Room Organizations
					 */
					List<Rooms_Organisation> roomOrgList = Roommanagement.getInstance().getRoomsOrganisations();
					
					if (roomOrgList != null) {
						Document doc = this.createOrgRoomsDocument(roomOrgList);
						
						String roomListXML = backup_dir + "rooms_organisation.xml";

						FileOutputStream fos = new FileOutputStream(roomListXML);
						
						this.serializetoXML(fos, "UTF-8", doc);
						
					}
					
					/* #####################
					 * Backup Appointements
					 */
					List<Appointment> aList = AppointmentDaoImpl.getInstance().getAppointments();
					
					if (aList != null) {
						Document doc = this.createAppointementDocument(aList);
						
						String aListXML = backup_dir + "appointements.xml";

						FileOutputStream fos = new FileOutputStream(aListXML);
						
						this.serializetoXML(fos, "UTF-8", doc);
						
					}
					
					/* #####################
					 * Backup Meeting Members
					 */
					List<MeetingMember> membersList = MeetingMemberDaoImpl.getInstance().getMeetingMembers();
					
					if (membersList != null) {
						Document doc = this.createMeetingMemberDocument(membersList);
						
						String aListXML = backup_dir + "meetingmembers.xml";

						FileOutputStream fos = new FileOutputStream(aListXML);
						
						this.serializetoXML(fos, "UTF-8", doc);
						
					}
					
					/* #####################
					 * LDAP Configs
					 */
					List<LdapConfig> ldapConfigList = LdapConfigDaoImpl.getInstance().getLdapConfigs();
					
					if (ldapConfigList != null) {
						Document doc = this.createLdapConfigDocument(ldapConfigList);
						
						String aListXML = backup_dir + "ldapconfigs.xml";

						FileOutputStream fos = new FileOutputStream(aListXML);
						
						this.serializetoXML(fos, "UTF-8", doc);
						
					}
					
					/* #####################
					 * Private Messages
					 */
					List<PrivateMessages> privateMessages = PrivateMessagesDaoImpl.getInstance().getPrivateMessages();
					
					if (privateMessages != null) {
						Document doc = this.createPrivateMessagesDocument(privateMessages);
						
						String aListXML = backup_dir + "privateMessages.xml";

						FileOutputStream fos = new FileOutputStream(aListXML);
						
						this.serializetoXML(fos, "UTF-8", doc);
					}
					
					
					/* #####################
					 * Private Message Folders
					 */
					List<PrivateMessageFolder> privateMessageFolders = PrivateMessageFolderDaoImpl.getInstance().getPrivateMessageFolders();
					
					if (privateMessageFolders != null) {
						Document doc = this.createPrivateMessageFolderDocument(privateMessageFolders);
						
						String aListXML = backup_dir + "privateMessageFolder.xml";

						FileOutputStream fos = new FileOutputStream(aListXML);
						
						this.serializetoXML(fos, "UTF-8", doc);
					}
					
					
					/* #####################
					 * User Contacts
					 */
					List<UserContacts> userContacts = UserContactsDaoImpl.getInstance().getUserContacts();
					
					if (privateMessageFolders != null) {
						Document doc = this.createUserContactsDocument(userContacts);
						
						String aListXML = backup_dir + "userContacts.xml";

						FileOutputStream fos = new FileOutputStream(aListXML);
						
						this.serializetoXML(fos, "UTF-8", doc);
					}

					/* #####################
					 * File-Explorer
					 */
					List<FileExplorerItem> fileExplorerList = FileExplorerItemDaoImpl.getInstance().getFileExplorerItems();
					
					if (fileExplorerList != null)  {
						
						Document doc = this.createFileExplorerItemDocument(fileExplorerList);
						
						String aListXML = backup_dir + "fileExplorerItems.xml";

						FileOutputStream fos = new FileOutputStream(aListXML);
						
						this.serializetoXML(fos, "UTF-8", doc);
						
					}
					
					/* #####################
					 * Recordings
					 */
					List<FlvRecording> flvRecordings =  FlvRecordingDaoImpl.getInstance().getAllFlvRecordings();
					
					for (FlvRecording flvRecording : flvRecordings) {
						flvRecording.setFlvRecordingMetaData(FlvRecordingMetaDataDaoImpl.getInstance().getFlvRecordingMetaDataByRecording(flvRecording.getFlvRecordingId()));
					}
					
					if (privateMessageFolders != null) {
						Document doc = this.createFlvRecordingDocument(flvRecordings);
						
						String aListXML = backup_dir + "flvRecordings.xml";

						FileOutputStream fos = new FileOutputStream(aListXML);
						
						this.serializetoXML(fos, "UTF-8", doc);
					}
					
					if (includeFileOption.equals("yes")) {
					
						/* #####################
						 * Backup Room Files
						 */
						File targetDir = new File(backup_dir + File.separatorChar + "roomFiles");
						
						if (!targetDir.exists()) {
							targetDir.mkdir();
						}
						
						File sourceDir = new File(current_dir + "upload" + File.separatorChar);
						
						File[] files = sourceDir.listFiles();
						for (File file : files) {
							if (file.isDirectory()) {
								
								if (!file.getName().equals("backup") && !file.getName().equals("import")) {
									
									targetDir = new File(backup_dir + File.separatorChar 
															+ "roomFiles" + File.separatorChar + file.getName());
									
									log.debug("### "+file.getName());
									
									copyDirectory(file,targetDir);
									
								}
								
							}
						}
					
						/* #####################
						 * Backup Recording Files
						 */
						File targetDirRec = new File(backup_dir + File.separatorChar + "recordingFiles");
						
						if (!targetDirRec.exists()) {
							targetDirRec.mkdir();
						}
						
						
						File sourceDirRec = new File(current_dir + "streams" + File.separatorChar 
												+ "hibernate" + File.separatorChar);
						
						copyDirectory(sourceDirRec,targetDirRec);
					
					}
					
					String full_path = backup_file + ".zip";
					
//					ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(full_path));
//					
//					zipToDir(backup_dir, zos);
//					
//					zos.close();
					
					List<File> fileList = new ArrayList<File>();
					log.debug("---Getting references to all files in: " + backup_dirFile.getCanonicalPath());
					getAllFiles(backup_dirFile, fileList);
					log.debug("---Creating zip file");
					writeZipFile(backup_dirFile, fileList, new FileOutputStream(full_path));
					log.debug("---Done");
					
					RandomAccessFile rf = new RandomAccessFile(full_path, "r");
					
					String requestedFile = dateString+".zip";

					httpServletResponse.reset();
					httpServletResponse.resetBuffer();
					httpServletResponse.setContentType("APPLICATION/OCTET-STREAM");
					httpServletResponse.setHeader("Content-Disposition",
							"attachment; filename=\"" + requestedFile + "\"");
					httpServletResponse.setHeader("Content-Length", ""+ rf.length());
					

					OutputStream out = httpServletResponse.getOutputStream();
					
					byte[] buffer = new byte[1024];
					int readed = -1;

					while ((readed = rf.read(buffer, 0, buffer.length)) > -1) {
						out.write(buffer, 0, readed);
					}

					rf.close();

					out.flush();
					out.close();
					
					File backupFile = new File(full_path);
					
					if (backupFile.exists()) {
						//log.debug("DELETE :1: "+backupFile.getAbsolutePath());
						backupFile.delete();
					}
					
					this.deleteDirectory(backup_dirFile);

				}
			} else {
				log.debug("ERROR LangExport: not authorized FileDownload "+ (new Date()));
			}	
		} catch (Exception er) {
			log.error("ERROR ", er);
			log.debug("Error exporting: " + er);
			er.printStackTrace();
		}
	}

	public boolean deleteDirectory(File path) throws IOException {
		
		//log.debug("deleteDirectory :: "+path);
		
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
		
		//log.debug("DELETE :3: "+path.getAbsolutePath());
		
		return (path.delete());
	}

//	private void zipToDir(String backupDir, ZipOutputStream zos) throws Exception {
//		
//		File zipDir = new File(backupDir);
//		
//		String[] dirList = zipDir.list();
//		
//		byte[] readBuffer = new byte[1024];
//		
//		int bytesIn = 0;
//		
//		for (int i=0;i<dirList.length;i++) {
//			
//			File f = new File(zipDir, dirList[i]);
//			
//			if (f.isDirectory()) {
//				String filePath = f.getPath();
//				zipToDir(filePath, zos);
//			} else {
//				FileInputStream fis = new FileInputStream(f);
//				ZipEntry newEntry = new ZipEntry(f.getName());
//				
//				//log.debug("newEntry :: "+f.getPath());
//				//log.debug("newEntry :: "+f.getName());
//				
//				zos.putNextEntry(newEntry);
//				while((bytesIn = fis.read(readBuffer)) != -1 ) {
//					zos.write(readBuffer, 0, bytesIn);
//				}
//				fis.close();
//			}
//			
//			
//		}
//		
//	}
	
	public void getAllFiles(File dir, List<File> fileList) throws IOException {
		try {
			File[] files = dir.listFiles();
			for (File file : files) {
				fileList.add(file);
				if (file.isDirectory()) {
					//log.debug("directory:" + file.getCanonicalPath());
					getAllFiles(file, fileList);
				} else {
					//log.debug("     file:" + file.getCanonicalPath());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void writeZipFile(File directoryToZip, List<File> fileList, FileOutputStream fos) {

		try {
			//FileOutputStream fos = new FileOutputStream(directoryToZip.getName() + ".zip");
			ZipOutputStream zos = new ZipOutputStream(fos);

			for (File file : fileList) {
				if (!file.isDirectory()) { // we only zip files, not directories
					addToZip(directoryToZip, file, zos);
				} else {
//					String zipFilePath = file.getCanonicalPath().substring(directoryToZip.getCanonicalPath().length() + 1,
//							file.getCanonicalPath().length());
//					//log.debug("Writing '" + zipFilePath + "' to zip file");
//					ZipEntry zipEntry = new ZipEntry(zipFilePath);
//					zos.putNextEntry(zipEntry);
				}
			}

			zos.close();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
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

	public void addToZip(File directoryToZip, File file, ZipOutputStream zos) throws FileNotFoundException,
			IOException {

		FileInputStream fis = new FileInputStream(file);

		// we want the zipEntry's path to be a relative path that is relative
		// to the directory being zipped, so chop off the rest of the path
		String zipFilePath = file.getCanonicalPath().substring(directoryToZip.getCanonicalPath().length() + 1,
				file.getCanonicalPath().length());
		log.debug("Writing '" + zipFilePath + "' to zip file");
		ZipEntry zipEntry = new ZipEntry(zipFilePath);
		zos.putNextEntry(zipEntry);

		byte[] bytes = new byte[1024];
		int length;
		while ((length = fis.read(bytes)) >= 0) {
			zos.write(bytes, 0, length);
		}

		zos.closeEntry();
		fis.close();
	}


	public Document createAppointementDocument(List<Appointment> aList) throws Exception {
		Document document = DocumentHelper.createDocument();
		document.setXMLEncoding("UTF-8");
		document.addComment(
				"###############################################\n" +
				"This File is auto-generated by the Backup Tool \n" +
				"you should use the BackupPanel to modify or change this file \n" +
				"see http://code.google.com/p/openmeetings/wiki/BackupPanel for Details \n" +
				"###############################################");
		
		Element root = document.addElement("root");
		
		Element appointments = root.addElement("appointments");
		
		for (Iterator<Appointment> it = aList.iterator();it.hasNext();) {
			Appointment a = it.next();
			
			Element appointment = appointments.addElement("appointment");
			
			appointment.addElement("appointmentId").addCDATA(""+a.getAppointmentId());
			appointment.addElement("appointmentName").addCDATA(""+a.getAppointmentName());
			appointment.addElement("appointmentLocation").addCDATA(""+a.getAppointmentLocation());
			appointment.addElement("appointmentDescription").addCDATA(""+a.getAppointmentDescription());
			appointment.addElement("appointmentStarttime").addCDATA(CalendarPatterns.getDateWithTimeByMiliSeconds(a.getAppointmentStarttime()));
			appointment.addElement("appointmentEndtime").addCDATA(CalendarPatterns.getDateWithTimeByMiliSeconds(a.getAppointmentEndtime()));
			if (a.getAppointmentCategory() != null) {
				appointment.addElement("categoryId").addCDATA(""+a.getAppointmentCategory().getCategoryId());
			} else {
				appointment.addElement("categoryId").addCDATA(""+0);
			}
			if (a.getUserId() != null) {
				appointment.addElement("users_id").addCDATA(""+a.getUserId().getUser_id());
			} else {
				appointment.addElement("users_id").addCDATA(""+0);
			}
			appointment.addElement("deleted").addCDATA(""+a.getDeleted());
			appointment.addElement("comment").addCDATA(""+a.getComment());
			if (a.getRemind() != null) {
				appointment.addElement("typId").addCDATA(""+a.getRemind().getTypId());
			} else {
				appointment.addElement("typId").addCDATA(""+0);
			}
			appointment.addElement("isDaily").addCDATA(""+a.getIsDaily());
			appointment.addElement("isWeekly").addCDATA(""+a.getIsWeekly());
			appointment.addElement("isMonthly").addCDATA(""+a.getIsMonthly());
			appointment.addElement("isYearly").addCDATA(""+a.getIsYearly());
			if (a.getRoom() != null) {
				appointment.addElement("room_id").addCDATA(""+a.getRoom().getRooms_id());
			} else {
				appointment.addElement("room_id").addCDATA(""+0);
			}
			appointment.addElement("icalId").addCDATA(""+a.getIcalId());
			appointment.addElement("language_id").addCDATA(""+a.getLanguage_id());
			appointment.addElement("isPasswordProtected").addCDATA(""+a.getIsPasswordProtected());
			appointment.addElement("password").addCDATA(""+a.getPassword());
			
			//Separated XML File
			
//			Element meetingMembers = appointment.addElement("meetingMembers");
//			//List<String> organisations = new LinkedList();
//			for (Iterator<MeetingMember> iterObj = a.getMeetingMember().iterator();iterObj.hasNext(); ) {
//				MeetingMember m = iterObj.next();
//				Element meetingMember = meetingMembers.addElement("meetingMember");
//				meetingMember.addElement("meetingMemberId").addCDATA(""+m.getMeetingMemberId());
//				if (m.getUserid() != null) {
//					meetingMember.addElement("userid").addCDATA(""+m.getUserid().getUser_id());
//				} else {
//					meetingMember.addElement("userid").addCDATA("null");
//				}
//				meetingMember.addElement("firstname").addCDATA(""+m.getFirstname());
//				meetingMember.addElement("lastname").addCDATA(""+m.getLastname());
//				meetingMember.addElement("memberStatus").addCDATA(""+m.getMemberStatus());
//				meetingMember.addElement("appointmentStatus").addCDATA(""+m.getAppointmentStatus());
//				meetingMember.addElement("email").addCDATA(""+m.getEmail());
//				meetingMember.addElement("deleted").addCDATA(""+m.getDeleted());
//				meetingMember.addElement("comment").addCDATA(""+m.getComment());
//				meetingMember.addElement("invitor").addCDATA(""+m.getInvitor());
//			}
			
		}
	
		return document;
	}
	
	public Document createRoomsDocument(List<Rooms> roomList) throws Exception {
		Document document = DocumentHelper.createDocument();
		document.setXMLEncoding("UTF-8");
		document.addComment(
				"###############################################\n" +
				"This File is auto-generated by the Backup Tool \n" +
				"you should use the BackupPanel to modify or change this file \n" +
				"see http://code.google.com/p/openmeetings/wiki/BackupPanel for Details \n" +
				"###############################################");
		
		Element root = document.addElement("root");
		
		Element rooms = root.addElement("rooms");
		
		for (Iterator<Rooms> it = roomList.iterator();it.hasNext();) {
			Rooms r = it.next();
			
			Element room = rooms.addElement("room");
			
			room.addElement("name").addCDATA(""+r.getName());
			room.addElement("rooms_id").addCDATA(""+r.getRooms_id());
			room.addElement("deleted").addCDATA(""+r.getDeleted());
			room.addElement("comment").addCDATA(""+r.getComment());
			room.addElement("numberOfPartizipants").addCDATA(""+r.getNumberOfPartizipants());
			room.addElement("appointment").addCDATA(""+r.getAppointment());
			room.addElement("externalRoomId").addCDATA(""+r.getExternalRoomId());
			room.addElement("externalRoomType").addCDATA(""+r.getExternalRoomType());
			if (r.getRoomtype() != null) {
				room.addElement("roomtypeId").addCDATA(""+r.getRoomtype().getRoomtypes_id());
			} else {
				room.addElement("roomtypeId").addCDATA(""+0);
			}
			if (r.getOwnerId() != null) {
				room.addElement("ownerid").addCDATA(""+r.getOwnerId());
			} else {
				room.addElement("ownerid").addCDATA("");
			}
			if (r.getWaitForRecording() != null) {
				room.addElement("waitForRecording").addCDATA(""+r.getWaitForRecording());
			} else {
				room.addElement("waitForRecording").addCDATA("");
			}
			if (r.getHideTopBar() != null) {
				room.addElement("hideTopBar").addCDATA(""+r.getHideTopBar());
			} else {
				room.addElement("hideTopBar").addCDATA("");
			}
			if (r.getAllowRecording() != null) {
				room.addElement("allowRecording").addCDATA(""+r.getAllowRecording());
			} else {
				room.addElement("allowRecording").addCDATA("");
			}
			room.addElement("isDemoRoom").addCDATA(""+r.getIsDemoRoom());
			room.addElement("demoTime").addCDATA(""+r.getDemoTime());
			room.addElement("isModeratedRoom").addCDATA(""+r.getIsModeratedRoom());
			room.addElement("allowUserQuestions").addCDATA(""+r.getAllowUserQuestions());
			room.addElement("isAudioOnly").addCDATA(""+r.getIsAudioOnly());
			room.addElement("sipNumber").addCDATA(""+r.getSipNumber());
			room.addElement("conferencePin").addCDATA(""+r.getConferencePin());
			if (r.getIspublic() != null) {
				room.addElement("ispublic").addCDATA(""+r.getIspublic());
			} else {
				r.setIspublic(false);
				room.addElement("ispublic").addCDATA(""+r.getIspublic());
			}
			
			room.addElement("isClosed").addCDATA(""+r.getIsClosed());
			room.addElement("redirectURL").addCDATA(""+r.getRedirectURL());
			
			List<RoomModerators> roomModeratorsList = RoomModeratorsDaoImpl.getInstance().getRoomModeratorByRoomId(r.getRooms_id());
			
			Element room_moderators = room.addElement("room_moderators");
			
			for (RoomModerators roomModerator : roomModeratorsList) {
				
				Element room_moderator = room_moderators.addElement("room_moderator");
				
				if (roomModerator.getUser() != null) {
					room_moderator.addElement("user_id").addCDATA(""+roomModerator.getUser().getUser_id());
				} else {
					room_moderator.addElement("user_id").addCDATA("0");
				}
				
				room_moderator.addElement("is_supermoderator").addCDATA(""+roomModerator.getIsSuperModerator());
			}
			
		}
	
		return document;
	}
	
	public Document createOrgDocument(List<Organisation> orgList) throws Exception {
		Document document = DocumentHelper.createDocument();
		document.setXMLEncoding("UTF-8");
		document.addComment(
				"###############################################\n" +
				"This File is auto-generated by the Backup Tool \n" +
				"you should use the BackupPanel to modify or change this file \n" +
				"see http://code.google.com/p/openmeetings/wiki/BackupPanel for Details \n" +
				"###############################################");
		
		Element root = document.addElement("root");
		
		Element organisations = root.addElement("organisations");
		
		for (Iterator<Organisation> it = orgList.iterator();it.hasNext();) {
			Organisation org = it.next();
			
			Element organisation = organisations.addElement("organisation");
			
			organisation.addElement("name").addCDATA(formatString(""+org.getName()));
			organisation.addElement("organisation_id").addCDATA(formatString(""+org.getOrganisation_id()));
			organisation.addElement("deleted").addCDATA(formatString(""+org.getDeleted()));
			
		}
	
		return document;
	}	
	
	private String formatString(String str) {
		//Todo fix if there is sth. we can do to prevent unicode replacement with invalid chars
		//This is the place if you need to do special conversion on the strings
		return str;
	}
	

	private Document createPrivateMessagesDocument(
			List<PrivateMessages> privateMessages) throws Exception {
		Document document = DocumentHelper.createDocument();
		document.setXMLEncoding("UTF-8");
		document.addComment(
				"###############################################\n" +
				"This File is auto-generated by the Backup Tool \n" +
				"you should use the BackupPanel to modify or change this file \n" +
				"see http://code.google.com/p/openmeetings/wiki/BackupPanel for Details \n" +
				"###############################################");
		
		Element root = document.addElement("root");
		
		Element privatemessages = root.addElement("privatemessages");
		
		for (Iterator<PrivateMessages> it = privateMessages.iterator();it.hasNext();) {
			PrivateMessages pm = it.next();
			
			Element privateMessage = privatemessages.addElement("privatemessage");
			
			privateMessage.addElement("privateMessageId").addCDATA(formatString(""+pm.getPrivateMessageId()));
			privateMessage.addElement("message").addCDATA(formatString(""+pm.getMessage()));
			privateMessage.addElement("subject").addCDATA(formatString(""+pm.getSubject()));
			privateMessage.addElement("privateMessageFolderId").addCDATA(formatString(""+pm.getPrivateMessageFolderId()));
			privateMessage.addElement("userContactId").addCDATA(formatString(""+pm.getUserContactId()));
			privateMessage.addElement("parentMessage").addCDATA(formatString(""+pm.getParentMessage()));
			privateMessage.addElement("bookedRoom").addCDATA(formatString(""+pm.getBookedRoom()));
			if (pm.getFrom() != null) {
				privateMessage.addElement("from").addCDATA(formatString(""+pm.getFrom().getUser_id()));
			} else {
				privateMessage.addElement("from").addCDATA("0");
			}
			if (pm.getTo() != null) {
				privateMessage.addElement("to").addCDATA(formatString(""+pm.getTo().getUser_id()));
			} else {
				privateMessage.addElement("to").addCDATA("0");
			}
			privateMessage.addElement("inserted").addCDATA(formatString(""+CalendarPatterns.getDateWithTimeByMiliSeconds(pm.getInserted())));
			privateMessage.addElement("isContactRequest").addCDATA(formatString(""+pm.getIsContactRequest()));
			privateMessage.addElement("isRead").addCDATA(formatString(""+pm.getIsRead()));
			privateMessage.addElement("isTrash").addCDATA(formatString(""+pm.getIsTrash()));
			if (pm.getOwner() != null) {
				privateMessage.addElement("owner").addCDATA(formatString(""+pm.getOwner().getUser_id()));
			} else {
				privateMessage.addElement("owner").addCDATA("0");
			}
			
			if (pm.getRoom() != null) {
				privateMessage.addElement("room").addCDATA(formatString(""+pm.getRoom().getRooms_id()));
			} else {
				privateMessage.addElement("room").addCDATA("0");
			}
			
		}
	
		return document;
	}
	
	private Document createFileExplorerItemDocument(List<FileExplorerItem> fileExplorerItems) throws Exception {
		
		Document document = DocumentHelper.createDocument();
		document.setXMLEncoding("UTF-8");
		document.addComment(
				"###############################################\n" +
				"This File is auto-generated by the Backup Tool \n" +
				"you should use the BackupPanel to modify or change this file \n" +
				"see http://code.google.com/p/openmeetings/wiki/BackupPanel for Details \n" +
				"###############################################");
		
		Element root = document.addElement("root");
		
		Element fileExplorerItemsElement = root.addElement("fileExplorerItems");
		
		for (Iterator<FileExplorerItem> it = fileExplorerItems.iterator();it.hasNext();) {
			FileExplorerItem fileExplorerItem = it.next();
			
			Element fileExplorerItemElement = fileExplorerItemsElement.addElement("fileExplorerItem");
			
			fileExplorerItemElement.addElement("fileExplorerItemId").addCDATA(formatString(""+fileExplorerItem.getFileExplorerItemId()));
			fileExplorerItemElement.addElement("fileName").addCDATA(formatString(""+fileExplorerItem.getFileName()));
			fileExplorerItemElement.addElement("fileHash").addCDATA(formatString(""+fileExplorerItem.getFileHash()));
			fileExplorerItemElement.addElement("parentFileExplorerItemId").addCDATA(formatString(""+fileExplorerItem.getParentFileExplorerItemId()));
			fileExplorerItemElement.addElement("room_id").addCDATA(formatString(""+fileExplorerItem.getRoom_id()));
			fileExplorerItemElement.addElement("ownerId").addCDATA(formatString(""+fileExplorerItem.getOwnerId()));
			fileExplorerItemElement.addElement("isFolder").addCDATA(formatString(""+fileExplorerItem.getIsFolder()));
			fileExplorerItemElement.addElement("isImage").addCDATA(formatString(""+fileExplorerItem.getIsImage()));
			fileExplorerItemElement.addElement("isPresentation").addCDATA(formatString(""+fileExplorerItem.getIsPresentation()));
			fileExplorerItemElement.addElement("isVideo").addCDATA(formatString(""+fileExplorerItem.getIsVideo()));
			fileExplorerItemElement.addElement("insertedBy").addCDATA(formatString(""+fileExplorerItem.getInsertedBy()));
			fileExplorerItemElement.addElement("inserted").addCDATA(formatString(""+CalendarPatterns.getDateWithTimeByMiliSeconds(fileExplorerItem.getInserted())));
			fileExplorerItemElement.addElement("updated").addCDATA(formatString(""+CalendarPatterns.getDateWithTimeByMiliSeconds(fileExplorerItem.getUpdated())));
			fileExplorerItemElement.addElement("deleted").addCDATA(formatString(""+fileExplorerItem.getDeleted()));
			fileExplorerItemElement.addElement("fileSize").addCDATA(formatString(""+fileExplorerItem.getFileSize()));
			fileExplorerItemElement.addElement("flvWidth").addCDATA(formatString(""+fileExplorerItem.getFlvWidth()));
			fileExplorerItemElement.addElement("flvHeight").addCDATA(formatString(""+fileExplorerItem.getFlvHeight()));
			fileExplorerItemElement.addElement("previewImage").addCDATA(formatString(""+fileExplorerItem.getPreviewImage()));
			fileExplorerItemElement.addElement("wmlFilePath").addCDATA(formatString(""+fileExplorerItem.getWmlFilePath()));
			fileExplorerItemElement.addElement("isStoredWmlFile").addCDATA(formatString(""+fileExplorerItem.getIsStoredWmlFile()));
			fileExplorerItemElement.addElement("isChart").addCDATA(formatString(""+fileExplorerItem.getIsChart()));
		    
		}
		
		return document;
		
	}

	private Document createFlvRecordingDocument(List<FlvRecording> flvRecordings) throws Exception {
		Document document = DocumentHelper.createDocument();
		document.setXMLEncoding("UTF-8");
		document.addComment(
				"###############################################\n" +
				"This File is auto-generated by the Backup Tool \n" +
				"you should use the BackupPanel to modify or change this file \n" +
				"see http://code.google.com/p/openmeetings/wiki/BackupPanel for Details \n" +
				"###############################################");
		
		Element root = document.addElement("root");
		
		Element flvrecordings = root.addElement("flvrecordings");
		
		for (Iterator<FlvRecording> it = flvRecordings.iterator();it.hasNext();) {
			FlvRecording flvRec = it.next();
			
			Element flvrecording = flvrecordings.addElement("flvrecording");
			
			flvrecording.addElement("alternateDownload").addCDATA(formatString(""+flvRec.getAlternateDownload()));
			flvrecording.addElement("comment").addCDATA(formatString(""+flvRec.getComment()));
			flvrecording.addElement("deleted").addCDATA(formatString(""+flvRec.getDeleted()));
			flvrecording.addElement("fileHash").addCDATA(formatString(""+flvRec.getFileHash()));
			flvrecording.addElement("fileName").addCDATA(formatString(""+flvRec.getFileName()));
			flvrecording.addElement("flvRecordingId").addCDATA(formatString(""+flvRec.getFlvRecordingId()));
			flvrecording.addElement("previewImage").addCDATA(formatString(""+flvRec.getPreviewImage()));
			flvrecording.addElement("recorderStreamId").addCDATA(formatString(""+flvRec.getRecorderStreamId()));
			flvrecording.addElement("fileSize").addCDATA(formatString(""+flvRec.getFileSize()));
			flvrecording.addElement("flvHeight").addCDATA(formatString(""+flvRec.getFlvHeight()));
			flvrecording.addElement("flvWidth").addCDATA(formatString(""+flvRec.getFlvWidth()));
			flvrecording.addElement("height").addCDATA(formatString(""+flvRec.getHeight()));
			flvrecording.addElement("width").addCDATA(formatString(""+flvRec.getWidth()));
			flvrecording.addElement("insertedBy").addCDATA(formatString(""+flvRec.getInsertedBy()));
			flvrecording.addElement("organization_id").addCDATA(formatString(""+flvRec.getOrganization_id()));
			flvrecording.addElement("ownerId").addCDATA(formatString(""+flvRec.getOwnerId()));
			flvrecording.addElement("parentFileExplorerItemId").addCDATA(formatString(""+flvRec.getParentFileExplorerItemId()));
			flvrecording.addElement("progressPostProcessing").addCDATA(formatString(""+flvRec.getProgressPostProcessing()));
			flvrecording.addElement("room_id").addCDATA(formatString(""+flvRec.getRoom_id()));
			flvrecording.addElement("inserted").addCDATA(formatString(""+CalendarPatterns.getDateWithTimeByMiliSeconds(flvRec.getInserted())));
			flvrecording.addElement("isFolder").addCDATA(formatString(""+flvRec.getIsFolder()));
			flvrecording.addElement("isImage").addCDATA(formatString(""+flvRec.getIsImage()));
			flvrecording.addElement("isInterview").addCDATA(formatString(""+flvRec.getIsInterview()));
			flvrecording.addElement("isPresentation").addCDATA(formatString(""+flvRec.getIsPresentation()));
			flvrecording.addElement("isRecording").addCDATA(formatString(""+flvRec.getIsRecording()));
			flvrecording.addElement("recordEnd").addCDATA(formatString(""+CalendarPatterns.getDateWithTimeByMiliSeconds(flvRec.getRecordEnd())));
			flvrecording.addElement("recordStart").addCDATA(formatString(""+CalendarPatterns.getDateWithTimeByMiliSeconds(flvRec.getRecordStart())));
			
			
			Element flvrecordingmetadatas = flvrecording.addElement("flvrecordingmetadatas");
			
			for (Iterator<FlvRecordingMetaData> itMeta = flvRec.getFlvRecordingMetaData().iterator();itMeta.hasNext();) {
				FlvRecordingMetaData flvMeta = itMeta.next();
				
				Element flvrecordingmetadata = flvrecordingmetadatas.addElement("flvrecordingmetadata");
				
				flvrecordingmetadata.addElement("flvRecordingMetaDataId").addCDATA(formatString(""+flvMeta.getFlvRecordingMetaDataId()));
				flvrecordingmetadata.addElement("freeTextUserName").addCDATA(formatString(""+flvMeta.getFreeTextUserName()));
				flvrecordingmetadata.addElement("fullWavAudioData").addCDATA(formatString(""+flvMeta.getFullWavAudioData()));
				flvrecordingmetadata.addElement("streamName").addCDATA(formatString(""+flvMeta.getStreamName()));
				flvrecordingmetadata.addElement("wavAudioData").addCDATA(formatString(""+flvMeta.getWavAudioData()));
				flvrecordingmetadata.addElement("initialGapSeconds").addCDATA(formatString(""+flvMeta.getInitialGapSeconds()));
				flvrecordingmetadata.addElement("insertedBy").addCDATA(formatString(""+flvMeta.getInsertedBy()));
				flvrecordingmetadata.addElement("interiewPodId").addCDATA(formatString(""+flvMeta.getInteriewPodId()));
				flvrecordingmetadata.addElement("audioIsValid").addCDATA(formatString(""+flvMeta.getAudioIsValid()));
				flvrecordingmetadata.addElement("inserted").addCDATA(formatString(""+CalendarPatterns.getDateWithTimeByMiliSeconds(flvMeta.getInserted())));
				flvrecordingmetadata.addElement("isAudioOnly").addCDATA(formatString(""+flvMeta.getIsAudioOnly()));
				flvrecordingmetadata.addElement("isScreenData").addCDATA(formatString(""+flvMeta.getIsScreenData()));
				flvrecordingmetadata.addElement("isVideoOnly").addCDATA(formatString(""+flvMeta.getIsVideoOnly()));
				flvrecordingmetadata.addElement("recordEnd").addCDATA(formatString(""+CalendarPatterns.getDateWithTimeByMiliSeconds(flvMeta.getRecordEnd())));
				flvrecordingmetadata.addElement("recordStart").addCDATA(formatString(""+CalendarPatterns.getDateWithTimeByMiliSeconds(flvMeta.getRecordStart())));
				flvrecordingmetadata.addElement("updated").addCDATA(formatString(""+CalendarPatterns.getDateWithTimeByMiliSeconds(flvMeta.getUpdated())));
				
			}
			
		}
	
		return document;
	}
	
	private Document createPrivateMessageFolderDocument(
			List<PrivateMessageFolder> privateMessageFolders) throws Exception {
		Document document = DocumentHelper.createDocument();
		document.setXMLEncoding("UTF-8");
		document.addComment(
				"###############################################\n" +
				"This File is auto-generated by the Backup Tool \n" +
				"you should use the BackupPanel to modify or change this file \n" +
				"see http://code.google.com/p/openmeetings/wiki/BackupPanel for Details \n" +
				"###############################################");
		
		Element root = document.addElement("root");
		
		Element privatemessagefolders = root.addElement("privatemessagefolders");
		
		for (Iterator<PrivateMessageFolder> it = privateMessageFolders.iterator();it.hasNext();) {
			PrivateMessageFolder pmf = it.next();
			
			Element privateMessageFolder = privatemessagefolders.addElement("privatemessagefolder");
			
			privateMessageFolder.addElement("privateMessageFolderId").addCDATA(formatString(""+pmf.getPrivateMessageFolderId()));
			privateMessageFolder.addElement("folderName").addCDATA(formatString(""+pmf.getFolderName()));
			privateMessageFolder.addElement("userId").addCDATA(formatString(""+pmf.getUserId()));
			
		}
	
		return document;
	}
	
	private Document createUserContactsDocument(List<UserContacts> userContacts) throws Exception {
		Document document = DocumentHelper.createDocument();
		document.setXMLEncoding("UTF-8");
		document.addComment(
				"###############################################\n" +
				"This File is auto-generated by the Backup Tool \n" +
				"you should use the BackupPanel to modify or change this file \n" +
				"see http://code.google.com/p/openmeetings/wiki/BackupPanel for Details \n" +
				"###############################################");
		
		Element root = document.addElement("root");
		
		Element usercontacts = root.addElement("usercontacts");
		
		for (Iterator<UserContacts> it = userContacts.iterator();it.hasNext();) {
			UserContacts uc = it.next();
			
			Element usercontact = usercontacts.addElement("usercontact");
			
			usercontact.addElement("userContactId").addCDATA(formatString(""+uc.getUserContactId()));
			usercontact.addElement("hash").addCDATA(formatString(""+uc.getHash()));
			if (uc.getContact() != null) {
				usercontact.addElement("contact").addCDATA(formatString(""+uc.getContact().getUser_id()));
			} else {
				usercontact.addElement("contact").addCDATA("0");
			}
			if (uc.getOwner() != null) {
				usercontact.addElement("owner").addCDATA(formatString(""+uc.getOwner().getUser_id()));
			} else {
				usercontact.addElement("owner").addCDATA("0");
			}
			usercontact.addElement("pending").addCDATA(formatString(""+uc.getPending()));
			usercontact.addElement("shareCalendar").addCDATA(formatString(""+uc.getShareCalendar()));
			
		}
	
		return document;
	}
	
	private Document createOrgRoomsDocument(List<Rooms_Organisation> roomOrgList) throws Exception {
		Document document = DocumentHelper.createDocument();
		document.setXMLEncoding("UTF-8");
		document.addComment(
				"###############################################\n" +
				"This File is auto-generated by the Backup Tool \n" +
				"you should use the BackupPanel to modify or change this file \n" +
				"see http://code.google.com/p/openmeetings/wiki/BackupPanel for Details \n" +
				"###############################################");
		
		Element root = document.addElement("root");
		
		Element organisations = root.addElement("room_organisations");
		
		for (Iterator<Rooms_Organisation> it = roomOrgList.iterator();it.hasNext();) {
			Rooms_Organisation roomOrg = it.next();
			
			Element room_organisation = organisations.addElement("room_organisation");
			
			room_organisation.addElement("rooms_organisation_id").addCDATA(formatString(""+roomOrg.getRooms_organisation_id()));
			room_organisation.addElement("organisation_id").addCDATA(formatString(""+roomOrg.getOrganisation().getOrganisation_id()));
			room_organisation.addElement("rooms_id").addCDATA(formatString(""+roomOrg.getRoom().getRooms_id()));
			room_organisation.addElement("deleted").addCDATA(formatString(""+roomOrg.getDeleted()));
			
		}
	
		return document;
	}
	
	private Document createMeetingMemberDocument(List<MeetingMember> memberList) throws Exception {
		Document document = DocumentHelper.createDocument();
		document.setXMLEncoding("UTF-8");
		document.addComment(
				"###############################################\n" +
				"This File is auto-generated by the Backup Tool \n" +
				"you should use the BackupPanel to modify or change this file \n" +
				"see http://code.google.com/p/openmeetings/wiki/BackupPanel for Details \n" +
				"###############################################");
		
		Element root = document.addElement("root");
		
		Element meetingmembers = root.addElement("meetingmembers");
		
		for (Iterator<MeetingMember> it = memberList.iterator();it.hasNext();) {
			MeetingMember meetMember = it.next();
			
			Element meetingmember = meetingmembers.addElement("meetingmember");
			
			meetingmember.addElement("meetingMemberId").addCDATA(formatString(""+meetMember.getMeetingMemberId()));
			if (meetMember.getUserid() != null) {
				meetingmember.addElement("userid").addCDATA(formatString(""+meetMember.getUserid().getUser_id()));
			} else {
				meetingmember.addElement("userid").addCDATA("0");
			}
			if (meetMember.getAppointment() != null) {
				meetingmember.addElement("appointment").addCDATA(formatString(""+meetMember.getAppointment().getAppointmentId()));
			} else {
				meetingmember.addElement("appointment").addCDATA("0");
			}
			meetingmember.addElement("firstname").addCDATA(formatString(""+meetMember.getFirstname()));
			meetingmember.addElement("lastname").addCDATA(formatString(""+meetMember.getLastname()));
			meetingmember.addElement("memberStatus").addCDATA(formatString(""+meetMember.getMemberStatus()));
			meetingmember.addElement("appointmentStatus").addCDATA(formatString(""+meetMember.getAppointmentStatus()));
			meetingmember.addElement("email").addCDATA(formatString(""+meetMember.getEmail()));
			meetingmember.addElement("deleted").addCDATA(formatString(""+meetMember.getDeleted()));
			meetingmember.addElement("comment").addCDATA(formatString(""+meetMember.getComment()));
			meetingmember.addElement("invitor").addCDATA(formatString(""+meetMember.getInvitor()));
			
		}
	
		return document;
	}

	private Document createLdapConfigDocument(List<LdapConfig> ldapConfigList) throws Exception {
		Document document = DocumentHelper.createDocument();
		document.setXMLEncoding("UTF-8");
		document.addComment(
				"###############################################\n" +
				"This File is auto-generated by the Backup Tool \n" +
				"you should use the BackupPanel to modify or change this file \n" +
				"see http://code.google.com/p/openmeetings/wiki/BackupPanel for Details \n" +
				"###############################################");
		
		Element root = document.addElement("root");
		
		Element ldapconfigs = root.addElement("ldapconfigs");

		for (Iterator<LdapConfig> it = ldapConfigList.iterator();it.hasNext();) {
			
			LdapConfig ldapC = it.next();

			Element ldapconfig = ldapconfigs.addElement("ldapconfig");
			
			ldapconfig.addElement("name").addCDATA(formatString(""+ldapC.getName()));
			ldapconfig.addElement("configFileName").addCDATA(formatString(""+ldapC.getConfigFileName()));
			ldapconfig.addElement("addDomainToUserName").addCDATA(formatString(""+ldapC.getAddDomainToUserName()));
			ldapconfig.addElement("domain").addCDATA(formatString(""+ldapC.getDomain()));
			ldapconfig.addElement("isActive").addCDATA(formatString(""+ldapC.getIsActive()));

		}

		return document;
	}

	public Document createDocument(List<Users> uList) throws Exception {
		Document document = DocumentHelper.createDocument();
		document.setXMLEncoding("UTF-8");
		document.addComment(
				"###############################################\n" +
				"This File is auto-generated by the Backup Tool \n" +
				"you should use the BackupPanel to modify or change this file \n" +
				"see http://code.google.com/p/openmeetings/wiki/BackupPanel for Details \n" +
				"###############################################");
		
		Element root = document.addElement("root");
		
		Element users = root.addElement("users");

		for (Iterator<Users> it = uList.iterator();it.hasNext();) {
			Users u = it.next();

			Element user = users.addElement("user");

			user.addElement("user_id").addCDATA(formatString(""+u.getUser_id()));
			user.addElement("deleted").addCDATA(formatString(""+u.getDeleted()));
			user.addElement("age").addCDATA(formatString(""+CalendarPatterns.getDateByMiliSeconds(u.getAge())));
			if (u.getAvailible() != null) {
				user.addElement("availible").addCDATA(formatString(""+u.getAvailible().toString()));
			} else {
				user.addElement("availible").addCDATA("0");
			}
			user.addElement("deleted").addCDATA(formatString(""+u.getDeleted()));
			user.addElement("firstname").addCDATA(formatString(""+u.getFirstname()));
			user.addElement("lastname").addCDATA(formatString(""+u.getLastname()));
			user.addElement("login").addCDATA(formatString(""+u.getLogin()));
			user.addElement("pass").addCDATA(formatString(""+u.getPassword()));
			
			if (u.getActivatehash()!= null) {
				user.addElement("activatehash").addCDATA(formatString(""+u.getActivatehash()));
			} else {
				user.addElement("activatehash").addCDATA(formatString(""));
			}
			if (u.getExternalUserType() != null) {
				user.addElement("externalUserType").addCDATA(formatString(""+u.getExternalUserType()));
			} else {
				user.addElement("externalUserType").addCDATA(formatString(""));
			}
			
			if (u.getExternalUserId() != null) {
				user.addElement("externalUserId").addCDATA(formatString(""+u.getExternalUserId()));
			} else {
				user.addElement("externalUserId").addCDATA(formatString(""));
			}
			
			if (u.getResethash() != null) {
				user.addElement("resethash").addCDATA(formatString(""+u.getResethash()));
			} else {
				user.addElement("resethash").addCDATA(formatString(""));
			}
			
			if (u.getUserOffers() != null) {
				user.addElement("userOffers").addCDATA(formatString(""+u.getUserOffers()));
			} else {
				user.addElement("userOffers").addCDATA(formatString(""));
			}
			
			if (u.getUserSearchs() != null) {
				user.addElement("userSearchs").addCDATA(formatString(""+u.getUserSearchs()));
			} else {
				user.addElement("userSearchs").addCDATA(formatString(""));
			}
			
			if (u.getForceTimeZoneCheck() != null) {
				user.addElement("forceTimeZoneCheck").addCDATA(formatString(""+u.getForceTimeZoneCheck()));
			} else {
				user.addElement("ForceTimeZoneCheck").addCDATA(formatString(""));
			}
			
			if (u.getLasttrans() != null) {
				user.addElement("lasttrans").addCDATA(formatString(""+u.getLasttrans()));
			} else {
				user.addElement("lasttrans").addCDATA(formatString(""));
			}

			if (u.getShowContactData() != null) {
				user.addElement("showContactData").addCDATA(formatString(""+u.getShowContactData()));
			} else {
				user.addElement("showContactData").addCDATA(formatString(""));
			}

			if (u.getShowContactDataToContacts() != null) {
				user.addElement("showContactDataToContacts").addCDATA(formatString(""+u.getShowContactDataToContacts()));
			} else {
				user.addElement("showContactDataToContacts").addCDATA(formatString(""));
			}			
			
			String pictureuri = u.getPictureuri();
			if (pictureuri != null) user.addElement("pictureuri").addCDATA(pictureuri);
			else user.addElement("pictureuri").addCDATA(formatString(""));
			
			if ( u.getLanguage_id() != null ) user.addElement("language_id").addCDATA(formatString(u.getLanguage_id().toString()));
			else user.addElement("language_id").addCDATA(formatString(""));
				
			if (u.getStatus() != null) {
				user.addElement("status").addCDATA(formatString(""+u.getStatus().toString()));
			} else {
				user.addElement("status").addCDATA("0");
			}
			user.addElement("regdate").addCDATA(formatString(""+CalendarPatterns.getDateWithTimeByMiliSeconds(u.getRegdate())));
			
			if (u.getTitle_id() != null) {
				user.addElement("title_id").addCDATA(formatString(""+u.getTitle_id().toString()));
				user.addElement("level_id").addCDATA(formatString(""+u.getLevel_id().toString()));
			} else {
				user.addElement("title_id").addCDATA("1");
				user.addElement("level_id").addCDATA("1");
			}
			
			if (u.getOmTimeZone() != null) {
				user.addElement("omTimeZone").addCDATA(formatString(""+u.getOmTimeZone().getJname()));
			} else {
				user.addElement("omTimeZone").addCDATA(formatString(""));
			}
			
			if (u.getAdresses() != null) {
				user.addElement("additionalname").addCDATA(formatString(""+u.getAdresses().getAdditionalname()));
				user.addElement("comment").addCDATA(formatString(""+u.getAdresses().getComment()));
				//A User can not have a deleted Adress, you cannot delete the Adress of an User
				//String deleted = u.getAdresses().getDeleted()
				//Phone Number not done yet
				user.addElement("fax").addCDATA(formatString(""+u.getAdresses().getFax()));
				if (u.getAdresses().getStates() != null) {
					user.addElement("state_id").addCDATA(formatString(""+u.getAdresses().getStates().getState_id().toString()));
				} else {
					user.addElement("state_id").addCDATA("1");
				}
				user.addElement("street").addCDATA(formatString(""+u.getAdresses().getStreet()));
				user.addElement("town").addCDATA(formatString(""+u.getAdresses().getTown()));
				user.addElement("zip").addCDATA(formatString(""+u.getAdresses().getZip()));
				
				// Email and Phone
				user.addElement("mail").addCDATA(formatString(""+u.getAdresses().getEmail()));
				user.addElement("phone").addCDATA(formatString(""+u.getAdresses().getPhone()));
			} else {
				user.addElement("additionalname").addCDATA(formatString(""));
				user.addElement("comment").addCDATA(formatString(""));
				//A User can not have a deleted address, you cannot delete the address of an User without deleting the user
				//only SOAP users might have a null
				//String deleted = u.getAdresses().getDeleted()
				//Phone Number not done yet
				user.addElement("fax").addCDATA(formatString(""));
				user.addElement("state_id").addCDATA("1");
				user.addElement("street").addCDATA(formatString(""));
				user.addElement("town").addCDATA(formatString(""));
				user.addElement("zip").addCDATA(formatString(""));
				user.addElement("mail").addCDATA(formatString(""));
				user.addElement("phone").addCDATA(formatString(""));
				
			}
			
			if (u.getUserSipData() != null) {
				user.addElement("sip_username").addCDATA(formatString(""+u.getUserSipData().getUsername()));
				user.addElement("sip_userpass").addCDATA(formatString(""+u.getUserSipData().getUserpass()));
				user.addElement("sip_authid").addCDATA(formatString(""+u.getUserSipData().getAuthId()));
			}
			
			
			
			Element user_organisations = user.addElement("organisations");
			//List<String> organisations = new LinkedList();
			for (Iterator<Organisation_Users> iterObj = u.getOrganisation_users().iterator();iterObj.hasNext(); ) {
				
				Element user_organisation = user_organisations.addElement("user_organisation");
				
				Organisation_Users orgUsers = iterObj.next();
				if (orgUsers.getOrganisation() != null) {
					user_organisation.addElement("organisation_id").addCDATA(formatString(""+orgUsers.getOrganisation().getOrganisation_id().toString()));
				} else {
					user_organisation.addElement("organisation_id").addCDATA("0");
				}
				
				user_organisation.addElement("deleted").addCDATA(formatString(""+orgUsers.getDeleted()));
				user_organisation.addElement("user_id").addCDATA(formatString(""+orgUsers.getUser_id()));
				user_organisation.addElement("isModerator").addCDATA(formatString(""+orgUsers.getIsModerator()));
				user_organisation.addElement("comment").addCDATA(formatString(""+orgUsers.getComment()));
				
			}
			
			//Not need at the moment
			//Element user_groups = user.addElement("groups");

		}

		return document;
	}	
	public void serializetoXML(OutputStream out, String aEncodingScheme, Document doc)
			throws Exception {
		OutputFormat outformat = OutputFormat.createPrettyPrint();
		outformat.setXHTML(true);
		outformat.setEncoding(aEncodingScheme);
		XMLWriter writer = new XMLWriter(out, outformat);
		writer.write(doc);
		writer.flush();
		writer.close();
		out.close();
	}


}
