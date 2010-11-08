package org.openmeetings.servlet.outputhandler;

import http.utils.multipartrequest.ServletMultipartRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.openmeetings.app.data.basic.Sessionmanagement;
import org.openmeetings.app.data.user.Usermanagement;
import org.openmeetings.app.data.user.dao.UsersDaoImpl;
import org.openmeetings.app.documents.GenerateImage;
import org.openmeetings.app.documents.GeneratePDF;
import org.openmeetings.app.documents.GenerateThumbs;
import org.openmeetings.app.hibernate.beans.user.Users;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.openmeetings.utils.stringhandlers.StringComparer;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

import com.itextpdf.text.pdf.PdfReader;

public class UploadHandler extends HttpServlet {

	private static final Logger log = Red5LoggerFactory.getLogger(UploadHandler.class, ScopeApplicationAdapter.webAppRootKey);
	 
	private String filesString[] = null;

	protected HashMap<String, String> fileExtensions = new HashMap<String, String>();

	protected HashMap<String, String> pdfExtensions = new HashMap<String, String>();

	protected HashMap<String, String> imageExtensions = new HashMap<String, String>();

	protected HashMap<String, String> jpgExtensions = new HashMap<String, String>();

	public UploadHandler() {
		fileExtensions.put("ext1", ".ppt");
		fileExtensions.put("ext2", ".odp");
		fileExtensions.put("ext3", ".odt");
		fileExtensions.put("ext4", ".sxw");
		fileExtensions.put("ext5", ".wpd");
		fileExtensions.put("ext6", ".doc");
		fileExtensions.put("ext7", ".rtf");
		fileExtensions.put("ext8", ".txt");
		fileExtensions.put("ext9", ".ods");
		fileExtensions.put("ext10", ".sxc");
		fileExtensions.put("ext11", ".xls");
		fileExtensions.put("ext12", ".sxi");
		fileExtensions.put("ext13", ".pptx");
		fileExtensions.put("ext14", ".docx");
		fileExtensions.put("ext15", ".xlsx");
		

		pdfExtensions.put("ext1", ".pdf");
		pdfExtensions.put("ext2", ".ps"); //PostScript

		jpgExtensions.put("ext1", ".jpg");


		imageExtensions.put("ext1", ".png");
		imageExtensions.put("ext2", ".gif");
		imageExtensions.put("ext3", ".svg");
		imageExtensions.put("ext4", ".dpx"); //DPX
		imageExtensions.put("ext5", ".exr"); //EXR
		imageExtensions.put("ext6", ".pcd"); //PhotoCD
		imageExtensions.put("ext7", ".pcds"); //PhotoCD
		imageExtensions.put("ext8", ".psd"); //Adobe Photoshop bitmap file
		imageExtensions.put("ext9", ".tiff"); //Tagged Image File Format
		imageExtensions.put("ext10", ".ttf"); //TrueType font file
		imageExtensions.put("ext11", ".xcf"); //GIMP imag
		imageExtensions.put("ext12", ".wpg"); //Word Perfect Graphics File
		imageExtensions.put("ext13", ".txt"); //Raw text file
		imageExtensions.put("ext14", ".bmp");
		imageExtensions.put("ext15", ".ico"); //Microsoft Icon File
		imageExtensions.put("ext16", ".tga"); //Truevision Targa image
		imageExtensions.put("ext17", ".jpeg");
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void service(HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) throws ServletException,
			IOException {
		boolean userProfilePic = false;
		try {
      
			if (httpServletRequest.getContentLength() > 0) {
				
				log.debug("uploading........ ");
				
				HashMap<String, HashMap<String, Object>> returnError = new HashMap<String, HashMap<String, Object>>();

				String sid = httpServletRequest.getParameter("sid");
				if (sid == null) {
					sid = "default";
				}

				log.debug("uploading........ sid: "+sid);
				
				Long users_id = Sessionmanagement.getInstance().checkSession(sid);
				Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);
				
				
				String publicSID = httpServletRequest.getParameter("publicSID");
				if (publicSID == null) {
					//Always ask for Public SID
					return;
				}

				if (user_level > 0) {
					
					log.debug("uploading........ user_level: "+user_level);
					
					String room_id = httpServletRequest.getParameter("room_id");
					if (room_id == null) {
						room_id = "default";
					}

					String moduleName = httpServletRequest.getParameter("moduleName");
					if (moduleName == null) {
						moduleName = "nomodule";
					}
					
					//System.out.println("MODUL-NAME: "+moduleName);
					//make a complete name out of domain(organisation) + roomname
					String roomName = room_id;
					//trim whitespaces cause it is a directory name
					roomName = StringUtils.deleteWhitespace(roomName);

					//Get the current User-Directory

					String current_dir = getServletContext().getRealPath("/");
					String working_dir = current_dir + "upload"
							+ File.separatorChar + roomName
							+ File.separatorChar;
					String working_dirppt = "";
					if (moduleName.equals("userprofile")){
						//System.out.println("IS USER PROFILE");
						working_dirppt = current_dir + "uploadtemp" 
							+ File.separatorChar + "profiles" + File.separatorChar;
						File f2 = new File(working_dirppt);
						if (!f2.exists()) {
							f2.mkdir();
						}	
						working_dirppt += "profile_" + users_id + File.separatorChar;
					} else {
						//System.out.println("IS SYSTEM PROFILE");
						//Add the Folder for the Room if it does not exist yet
						File localFolder = new File(working_dir);
						if (!localFolder.exists()) {
							localFolder.mkdir();
						}

						working_dirppt = current_dir + "uploadtemp" 
						+ File.separatorChar + roomName
						+ File.separatorChar;
					}

					// add Temp Folder Structure
					File localFolderppt = new File(working_dirppt);
					if (!localFolderppt.exists()) {
						localFolderppt.mkdir();
					}

					log.debug("#### UploadHandler working_dir: "+ working_dir);

					if (!moduleName.equals("nomodule")) {
						//Check variable to see if this file is a presentation

						ServletMultipartRequest upload = new ServletMultipartRequest(
								httpServletRequest, 104857600, "utf-8"); // max 100 mb
						InputStream is = upload.getFileContents("Filedata");

						//trim whitespace
						String fileSystemName = upload.getFileSystemName("Filedata");
						
						StringUtils.deleteWhitespace(fileSystemName);

						int dotidx=fileSystemName.lastIndexOf('.');
						String newFileSystemName = StringComparer.getInstance()
								.compareForRealPaths(
										fileSystemName.substring(0,
												dotidx));
						String newFileSystemExtName = fileSystemName.substring(
								dotidx,
								fileSystemName.length()).toLowerCase();

						// trim long names cause cannot output that
						final int MAX_FILE_NAME_LENGTH = 30;
						if (newFileSystemName.length() >= MAX_FILE_NAME_LENGTH) {
							newFileSystemName = newFileSystemName.substring(0,
									MAX_FILE_NAME_LENGTH);
						}

						//check if this is a a file that can be converted by openoffice-service
						boolean canBeConverted = checkForConvertion(newFileSystemExtName);
						boolean isPDF = checkForPDF(newFileSystemExtName);
						boolean isImage = checkForImage(newFileSystemExtName);
						boolean isJpg = checkForJpg(newFileSystemExtName);

						String completeName = "";
						
						//add outputfolders for profiles
						if (moduleName.equals("userprofile")) {
							//User Profile Update
							this.deleteUserProfileFilesStoreTemp(current_dir, users_id);
							
							completeName = current_dir + "upload"
								+ File.separatorChar + "profiles" + File.separatorChar;
							File f = new File(completeName);
							if (!f.exists()) {
								boolean c = f.mkdir();
								if (!c) {
									log.error("cannot write to directory");
									//System.out.println("cannot write to directory");
								}
							}
							completeName += "profile_"+users_id + File.separatorChar;
							File f2 = new File(completeName);
							if (!f2.exists()) {
								boolean c = f2.mkdir();
								if (!c) {
									log.error("cannot write to directory");
									//System.out.println("cannot write to directory");
								}
							}
							userProfilePic = true;
						}						
						//if it is a presenation it will be copied to another place
						if (canBeConverted || isPDF || isImage) {
							//check if this is a room file or UserProfile
							if (moduleName.equals("userprofile")) {
								userProfilePic = true;
							}
							completeName = working_dirppt + newFileSystemName;
						} else if (isJpg) {
							//check if this is a room file or UserProfile
							if (moduleName.equals("userprofile")) {
								userProfilePic = true;
								completeName += newFileSystemName;
							} else {
								completeName = working_dir + newFileSystemName;
							}
						} else {
							return;
						}

						File f = new File(completeName + newFileSystemExtName);
						if (f.exists()) {
							int recursiveNumber = 0;
							String tempd = completeName + "_" + recursiveNumber;
							while (f.exists()) {
								recursiveNumber++;
								tempd = completeName + "_" + recursiveNumber;
								f = new File(tempd + newFileSystemExtName);

							}
							completeName = tempd;
						}
						
						log.debug("##### WRITE FILE TO: "+completeName + newFileSystemExtName);

						FileOutputStream fos = new FileOutputStream(completeName + newFileSystemExtName);
						byte[] buffer = new byte[1024];
						int len = 0;

						while (len != (-1)) {
							len = is.read(buffer, 0, 1024);
							if (len != (-1))
								fos.write(buffer, 0, len);
						}

						fos.close();
						is.close();

						log.debug("canBeConverted: "+canBeConverted);
						if (canBeConverted) {
							//convert to pdf, thumbs, swf and xml-description
							returnError = GeneratePDF.getInstance().convertPDF(current_dir, newFileSystemName , newFileSystemExtName, roomName, true, completeName);
						} else if (isPDF) {
							
							boolean isEncrypted = false;
							try {
								//Check if PDF is encrpyted
								PdfReader pdfReader = new PdfReader(completeName);
								
								log.debug("pdfReader.isEncrypted() :: "+pdfReader.isEncrypted());
								
								if (pdfReader.isEncrypted()) {
									isEncrypted = true;
								}
								
							} catch (Exception err) {
								isEncrypted = true;
							}
							
							log.debug("isEncrypted :: "+isEncrypted);
							
							HashMap<String, Object> returnError2 = new HashMap<String, Object>();
							
							if (isEncrypted) {
								//Do convert pdf to other pdf first
								String inputfile = completeName + newFileSystemExtName;
								
								completeName = completeName + "_NOT_ENCRYPTED";
								newFileSystemName = newFileSystemName + "_NOT_ENCRYPTED";
								
								String outputfile = completeName + newFileSystemExtName;
								
								returnError2 = GenerateThumbs.getInstance().decodePDF(inputfile, outputfile);
								
								File f_old = new File(inputfile);
								if (f_old.exists()) {
									f_old.delete();
								}
								
							}
							
							//convert to thumbs, swf and xml-description
							returnError = GeneratePDF.getInstance().convertPDF(current_dir, newFileSystemName , newFileSystemExtName, roomName, false, completeName);						
						
							returnError.put("decodePDF", returnError2);
							
						} else if (isImage) {
							
							log.debug("##### isImage! userProfilePic: "+userProfilePic);
							
							if (userProfilePic) {
								//User Profile Update
								this.deleteUserProfileFiles(current_dir, users_id);
								//convert it to JPG
					 			returnError = GenerateImage.getInstance().convertImageUserProfile(current_dir, newFileSystemName, 
					 								newFileSystemExtName, users_id, newFileSystemName, false);
							} else {
								//convert it to JPG
								log.debug("##### convert it to JPG: "+userProfilePic);
					 			returnError = GenerateImage.getInstance().convertImage(current_dir, newFileSystemName, 
					 								newFileSystemExtName, roomName,newFileSystemName, false);
							}
						} else if (isJpg) {
							if (userProfilePic) {
								//User Profile Update
								this.deleteUserProfileFiles(current_dir, users_id);
								//is UserProfile Picture
								HashMap<String,Object> processThumb1 = GenerateThumbs.getInstance().generateThumb("_chat_", current_dir, completeName, 40);
								HashMap<String,Object> processThumb2 = GenerateThumbs.getInstance().generateThumb("_profile_", current_dir, completeName, 126);
								HashMap<String,Object> processThumb3 = GenerateThumbs.getInstance().generateThumb("_big_", current_dir, completeName, 240);
								returnError.put("processThumb1", processThumb1);
								returnError.put("processThumb2", processThumb2);
								returnError.put("processThumb3", processThumb3);
								
								File fileNameToStore = new File(completeName+".jpg");
								String pictureuri = fileNameToStore.getName();
								Users us = UsersDaoImpl.getInstance().getUser(users_id);
								us.setUpdatetime(new java.util.Date());
								us.setPictureuri(pictureuri);
								UsersDaoImpl.getInstance().updateUser(us);
								
								ScopeApplicationAdapter.getInstance().updateUserSessionObject(users_id,pictureuri);
							} else {
								HashMap<String,Object> processThumb = GenerateThumbs.getInstance().generateThumb("_thumb_", current_dir, completeName, 50);
								returnError.put("processThumb", processThumb);
							}
						}
						
						//Flash cannot read the response of an upload
						//httpServletResponse.getWriter().print(returnError);
						LinkedHashMap<String,Object> hs = new LinkedHashMap<String,Object>();
						hs.put("user", UsersDaoImpl.getInstance().getUser(users_id));
						hs.put("message", "library");
						hs.put("action", "newFile");
						hs.put("error", returnError);
						hs.put("fileName", completeName);	
						
						log.debug("moduleName.equals(userprofile) ? "+moduleName);
						
						//if (!moduleName.equals("userprofile")) {
							log.debug("moduleName.equals(userprofile) ! ");
							
						ScopeApplicationAdapter.getInstance().sendMessageWithClientByPublicSID(hs,publicSID);
						//}
						
					}
				}
			}
		} catch (Exception e) {
			System.out.println("ee " + e);
			e.printStackTrace();
			throw new ServletException(e);
		}
		return;
	}
	
	private void deleteUserProfileFilesStoreTemp(String current_dir, Long users_id) throws Exception{
		
		String working_imgdir = current_dir + "upload" + File.separatorChar + "profiles" + File.separatorChar + "profile_"+users_id + File.separatorChar;
		File f = new File(working_imgdir);
		if (f.exists() && f.isDirectory()) {
			this.filesString = f.list();
		}
	}
	
	private void deleteUserProfileFiles(String current_dir, Long users_id) throws Exception{
		
		String working_imgdir = current_dir + "upload" + File.separatorChar + "profiles" + File.separatorChar + "profile_"+users_id + File.separatorChar;

		for (int i=0;i<this.filesString.length;i++) {
			String fileName = filesString[i];
			File subf = new File(working_imgdir + fileName);
			subf.delete();
		}
	}
	
	private boolean checkForConvertion(String fileExtension) throws Exception {
		Iterator<String> extensionIt = fileExtensions.keySet().iterator();
		while (extensionIt.hasNext()) {
			String fileExt = fileExtensions.get(extensionIt.next());
			if (fileExtension.equals(fileExt)) {
				return true;
			}
		}
		return false;
	}

	private boolean checkForPDF(String fileExtension) throws Exception {
		Iterator<String> extensionIt = pdfExtensions.keySet().iterator();
		while (extensionIt.hasNext()) {
			String fileExt = pdfExtensions.get(extensionIt.next());
			if (fileExtension.equals(fileExt)) {
				return true;
			}
		}
		return false;
	}

	private boolean checkForJpg(String fileExtension) throws Exception {
		Iterator<String> extensionIt = jpgExtensions.keySet().iterator();
		while (extensionIt.hasNext()) {
			String fileExt = jpgExtensions.get(extensionIt.next());
			if (fileExtension.equals(fileExt)) {
				return true;
			}
		}
		return false;
	}

	private boolean checkForImage(String fileExtension) throws Exception {
		Iterator<String> extensionIt = imageExtensions.keySet().iterator();
		while (extensionIt.hasNext()) {
			String fileExt = imageExtensions.get(extensionIt.next());
			if (fileExtension.equals(fileExt)) {
				return true;
			}
		}
		return false;
	}



}
