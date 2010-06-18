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

import org.openmeetings.app.data.user.dao.UsersDaoImpl;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.openmeetings.app.data.basic.Sessionmanagement;
import org.openmeetings.app.data.calendar.daos.AppointmentDaoImpl;
import org.openmeetings.app.data.calendar.daos.MeetingMemberDaoImpl;
import org.openmeetings.app.data.conference.Roommanagement;
import org.openmeetings.app.data.user.Organisationmanagement;
import org.openmeetings.app.data.user.Usermanagement;
import org.openmeetings.app.hibernate.beans.calendar.Appointment;
import org.openmeetings.app.hibernate.beans.calendar.MeetingMember;
import org.openmeetings.app.hibernate.beans.domain.Organisation;
import org.openmeetings.app.hibernate.beans.domain.Organisation_Users;
import org.openmeetings.app.hibernate.beans.rooms.Rooms;
import org.openmeetings.app.hibernate.beans.rooms.Rooms_Organisation;
import org.openmeetings.app.hibernate.beans.user.Users;
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

			// if (user_level!=null && user_level > 0) {
			if (true) {
				
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
						Document doc = this.createDocument(uList);
						
						String userListXML = backup_dir + "users.xml";

						FileOutputStream fos = new FileOutputStream(userListXML);
						
						this.serializetoXML(fos, "UTF-8", doc);
						
					}
					
					
					/* #####################
					 * Backup Room
					 */
					List<Rooms> roomList = Roommanagement.getInstance().getAllRooms();
					
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
			
			appointment.addElement("appointmentId").setText(""+a.getAppointmentId());
			appointment.addElement("appointmentName").setText(""+a.getAppointmentName());
			appointment.addElement("appointmentLocation").setText(""+a.getAppointmentLocation());
			appointment.addElement("appointmentDescription").setText(""+a.getAppointmentDescription());
			appointment.addElement("appointmentStarttime").setText(CalendarPatterns.getDateWithTimeByMiliSeconds(a.getAppointmentStarttime()));
			appointment.addElement("appointmentEndtime").setText(CalendarPatterns.getDateWithTimeByMiliSeconds(a.getAppointmentEndtime()));
			if (a.getAppointmentCategory() != null) {
				appointment.addElement("categoryId").setText(""+a.getAppointmentCategory().getCategoryId());
			} else {
				appointment.addElement("categoryId").setText(""+0);
			}
			if (a.getUserId() != null) {
				appointment.addElement("users_id").setText(""+a.getUserId().getUser_id());
			} else {
				appointment.addElement("users_id").setText(""+0);
			}
			appointment.addElement("deleted").setText(""+a.getDeleted());
			appointment.addElement("comment").setText(""+a.getComment());
			if (a.getRemind() != null) {
				appointment.addElement("typId").setText(""+a.getRemind().getTypId());
			} else {
				appointment.addElement("typId").setText(""+0);
			}
			appointment.addElement("isDaily").setText(""+a.getIsDaily());
			appointment.addElement("isWeekly").setText(""+a.getIsWeekly());
			appointment.addElement("isMonthly").setText(""+a.getIsMonthly());
			appointment.addElement("isYearly").setText(""+a.getIsYearly());
			if (a.getRoom() != null) {
				appointment.addElement("room_id").setText(""+a.getRoom().getRooms_id());
			} else {
				appointment.addElement("room_id").setText(""+0);
			}
			appointment.addElement("icalId").setText(""+a.getIcalId());
			appointment.addElement("language_id").setText(""+a.getLanguage_id());
			appointment.addElement("isPasswordProtected").setText(""+a.getIsPasswordProtected());
			appointment.addElement("password").setText(""+a.getPassword());
			
			//Separated XML File
			
//			Element meetingMembers = appointment.addElement("meetingMembers");
//			//List<String> organisations = new LinkedList();
//			for (Iterator<MeetingMember> iterObj = a.getMeetingMember().iterator();iterObj.hasNext(); ) {
//				MeetingMember m = iterObj.next();
//				Element meetingMember = meetingMembers.addElement("meetingMember");
//				meetingMember.addElement("meetingMemberId").setText(""+m.getMeetingMemberId());
//				if (m.getUserid() != null) {
//					meetingMember.addElement("userid").setText(""+m.getUserid().getUser_id());
//				} else {
//					meetingMember.addElement("userid").setText("null");
//				}
//				meetingMember.addElement("firstname").setText(""+m.getFirstname());
//				meetingMember.addElement("lastname").setText(""+m.getLastname());
//				meetingMember.addElement("memberStatus").setText(""+m.getMemberStatus());
//				meetingMember.addElement("appointmentStatus").setText(""+m.getAppointmentStatus());
//				meetingMember.addElement("email").setText(""+m.getEmail());
//				meetingMember.addElement("deleted").setText(""+m.getDeleted());
//				meetingMember.addElement("comment").setText(""+m.getComment());
//				meetingMember.addElement("invitor").setText(""+m.getInvitor());
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
			
			room.addElement("name").setText(""+r.getName());
			room.addElement("rooms_id").setText(""+r.getRooms_id());
			room.addElement("deleted").setText(""+r.getDeleted());
			room.addElement("comment").setText(""+r.getComment());
			room.addElement("numberOfPartizipants").setText(""+r.getNumberOfPartizipants());
			room.addElement("appointment").setText(""+r.getAppointment());
			room.addElement("externalRoomId").setText(""+r.getExternalRoomId());
			room.addElement("externalRoomType").setText(""+r.getExternalRoomType());
			if (r.getRoomtype() != null) {
				room.addElement("roomtypeId").setText(""+r.getRoomtype().getRoomtypes_id());
			} else {
				room.addElement("roomtypeId").setText(""+0);
			}
			room.addElement("isDemoRoom").setText(""+r.getIsDemoRoom());
			room.addElement("demoTime").setText(""+r.getDemoTime());
			room.addElement("isModeratedRoom").setText(""+r.getIsModeratedRoom());
			room.addElement("allowUserQuestions").setText(""+r.getAllowUserQuestions());
			room.addElement("sipNumber").setText(""+r.getSipNumber());
			room.addElement("conferencePin").setText(""+r.getConferencePin());
			
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
			
			organisation.addElement("name").setText(""+org.getName());
			organisation.addElement("organisation_id").setText(""+org.getOrganisation_id());
			organisation.addElement("deleted").setText(""+org.getDeleted());
			
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
			
			room_organisation.addElement("rooms_organisation_id").setText(""+roomOrg.getRooms_organisation_id());
			room_organisation.addElement("organisation_id").setText(""+roomOrg.getOrganisation().getOrganisation_id());
			room_organisation.addElement("rooms_id").setText(""+roomOrg.getRoom().getRooms_id());
			room_organisation.addElement("deleted").setText(""+roomOrg.getDeleted());
			
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
			
			meetingmember.addElement("meetingMemberId").setText(""+meetMember.getMeetingMemberId());
			if (meetMember.getUserid() != null) {
				meetingmember.addElement("userid").setText(""+meetMember.getUserid().getUser_id());
			} else {
				meetingmember.addElement("userid").setText("0");
			}
			if (meetMember.getAppointment() != null) {
				meetingmember.addElement("appointment").setText(""+meetMember.getAppointment().getAppointmentId());
			} else {
				meetingmember.addElement("appointment").setText("0");
			}
			meetingmember.addElement("firstname").setText(""+meetMember.getFirstname());
			meetingmember.addElement("lastname").setText(""+meetMember.getLastname());
			meetingmember.addElement("memberStatus").setText(""+meetMember.getMemberStatus());
			meetingmember.addElement("appointmentStatus").setText(""+meetMember.getAppointmentStatus());
			meetingmember.addElement("email").setText(""+meetMember.getEmail());
			meetingmember.addElement("deleted").setText(""+meetMember.getDeleted());
			meetingmember.addElement("comment").setText(""+meetMember.getComment());
			meetingmember.addElement("invitor").setText(""+meetMember.getInvitor());
			
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

			user.addElement("user_id").setText(""+u.getUser_id());
			user.addElement("deleted").setText(""+u.getDeleted());
			user.addElement("age").setText(""+CalendarPatterns.getDateByMiliSeconds(u.getAge()));
			user.addElement("availible").setText(""+u.getAvailible().toString());
			user.addElement("deleted").setText(""+u.getDeleted());
			user.addElement("firstname").setText(""+u.getFirstname());
			user.addElement("lastname").setText(""+u.getLastname());
			user.addElement("login").setText(""+u.getLogin());
			user.addElement("pass").setText(""+u.getPassword());
			
			String pictureuri = u.getPictureuri();
			if (pictureuri != null) user.addElement("pictureuri").setText(pictureuri);
			else user.addElement("pictureuri").setText("");
			
			if ( u.getLanguage_id() != null ) user.addElement("language_id").setText(u.getLanguage_id().toString());
			else user.addElement("language_id").setText("");
				
			user.addElement("status").setText(""+u.getStatus().toString());
			user.addElement("regdate").setText(""+CalendarPatterns.getDateWithTimeByMiliSeconds(u.getRegdate()));
			user.addElement("title_id").setText(""+u.getTitle_id().toString());
			user.addElement("level_id").setText(""+u.getLevel_id().toString());
			
			user.addElement("additionalname").setText(u.getAdresses().getAdditionalname());
			user.addElement("comment").setText(u.getAdresses().getComment());
			//A User can not have a deleted Adress, you cannot delete the Adress of an User
			//String deleted = u.getAdresses().getDeleted()
			//Phone Number not done yet
			user.addElement("fax").setText(""+u.getAdresses().getFax());
			user.addElement("state_id").setText(""+u.getAdresses().getStates().getState_id().toString());
			user.addElement("street").setText(""+u.getAdresses().getStreet());
			user.addElement("town").setText(""+u.getAdresses().getTown());
			user.addElement("zip").setText(""+u.getAdresses().getZip());
			
			// Email and Phone
			user.addElement("mail").setText(""+u.getAdresses().getEmail());
			user.addElement("phone").setText(""+u.getAdresses().getPhone());
			
			
			Element user_organisations = user.addElement("organisations");
			//List<String> organisations = new LinkedList();
			for (Iterator<Organisation_Users> iterObj = u.getOrganisation_users().iterator();iterObj.hasNext(); ) {
				Organisation_Users orgUsers = iterObj.next();
				if (orgUsers.getOrganisation() != null) {
					user_organisations.addElement("organisation_id").addText(""+orgUsers.getOrganisation().getOrganisation_id().toString());
				} else {
					user_organisations.addElement("organisation_id").addText("0");
				}
				
				user_organisations.addElement("deleted").addText(""+orgUsers.getDeleted());
				user_organisations.addElement("user_id").addText(""+orgUsers.getUser_id());
				user_organisations.addElement("isModerator").addText(""+orgUsers.getIsModerator());
				user_organisations.addElement("comment").addText(""+orgUsers.getComment());
				
			}
			
			//Not need at the moment
			//Element user_groups = user.addElement("groups");

		}

		return document;
	}	
	public void serializetoXML(OutputStream out, String aEncodingScheme, Document doc)
			throws Exception {
		OutputFormat outformat = OutputFormat.createPrettyPrint();
		outformat.setEncoding(aEncodingScheme);
		XMLWriter writer = new XMLWriter(out, outformat);
		writer.write(doc);
		writer.flush();
	}


}
