package org.openmeetings.servlet.outputhandler;

import http.utils.multipartrequest.ServletMultipartRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

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
import org.openmeetings.utils.StoredFile;
import org.openmeetings.utils.stringhandlers.StringComparer;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

import com.itextpdf.text.pdf.PdfReader;

public class UploadHandler extends HttpServlet {

	private static final long serialVersionUID = 8955335681521483484L;

	private static final Logger log = Red5LoggerFactory.getLogger(
			UploadHandler.class, ScopeApplicationAdapter.webAppRootKey);

	private String filesString[] = null;

	public UploadHandler() {
	}

	protected void service(HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) throws ServletException,
			IOException {
		log.debug("starting upload");
		try {
			int contentLength = httpServletRequest.getContentLength();
			if (contentLength <= 0) {
				log.debug("ContentLength = " + contentLength + ", aborted");
				return;
			}
			log.debug("uploading " + contentLength + " bytes");

			String sid = httpServletRequest.getParameter("sid");
			if (sid == null) {
				throw new ServletException("Missing SID");
			}
			log.debug("sid: " + sid);

			Long userId = Sessionmanagement.getInstance().checkSession(sid);
			Long userLevel = Usermanagement.getInstance().getUserLevelByID(
					userId);
			log.debug("userId = " + userId + ", userLevel = " + userLevel);

			if (userLevel <= 0) {
				log.debug("insufficient user level " + userLevel);
				return;
			}

			String publicSID = httpServletRequest.getParameter("publicSID");
			if (publicSID == null) {
				// Always ask for Public SID
				throw new ServletException("Missing publicSID");
			}

			LinkedHashMap<String, Object> hs = new LinkedHashMap<String, Object>();
			hs.put("user", UsersDaoImpl.getInstance().getUser(userId));
			
			fileService(httpServletRequest, sid, userId, hs);
			ScopeApplicationAdapter.getInstance()
					.sendMessageWithClientByPublicSID(hs, publicSID);
		} catch (Exception e) {
			System.out.println("Exception during upload: " + e);
			e.printStackTrace();
			throw new ServletException(e);
		}
	}

	protected void fileService(HttpServletRequest httpServletRequest,
			String sid, Long userId, Map<String, Object> hs) throws ServletException,
			Exception {

		String room_id = httpServletRequest.getParameter("room_id");
		if (room_id == null) {
			room_id = "default";
		}
		String roomName = StringUtils.deleteWhitespace(room_id);

		String moduleName = httpServletRequest.getParameter("moduleName");
		if (moduleName == null) {
			moduleName = "nomodule";
		}
		if (moduleName.equals("nomodule")) {
			log.debug("module name missed");
			return;
		}
		boolean userProfile = moduleName.equals("userprofile");

		ServletMultipartRequest upload = new ServletMultipartRequest(
				httpServletRequest, 104857600, "utf-8"); // max 100 mb
		InputStream is = upload.getFileContents("Filedata");

		// trim whitespace
		String fileSystemName = upload.getFileSystemName("Filedata");
		fileSystemName = StringUtils.deleteWhitespace(fileSystemName);

		// Flash cannot read the response of an upload
		// httpServletResponse.getWriter().print(returnError);
		uploadFile(userProfile, userId, roomName, is, fileSystemName, hs);
	}

	private void uploadFile(boolean userProfile, Long userId, String roomName,
			InputStream is, String fileSystemName, Map<String, Object> hs)
			throws Exception {
		HashMap<String, HashMap<String, Object>> returnError = new HashMap<String, HashMap<String, Object>>();

		// Get the current user directory
		String currentDir = getServletContext().getRealPath("/");
		String workingDir = currentDir + "upload" + File.separatorChar
				+ roomName + File.separatorChar;
		log.debug("workingDir: " + workingDir);

		File localFolder = new File(workingDir);
		if (!localFolder.exists()) {
			localFolder.mkdirs();
		}

        // Check variable to see if this file is a presentation
        int dotidx = fileSystemName.lastIndexOf('.');
        String newFileName = StringComparer.getInstance().compareForRealPaths(
                fileSystemName.substring(0, dotidx));
        String newFileExtDot = fileSystemName.substring(dotidx,
                fileSystemName.length()).toLowerCase();
        String newFileExt = newFileExtDot.substring(1);

		// trim long names cause cannot output that
		final int MAX_FILE_NAME_LENGTH = 30;
		if (newFileName.length() >= MAX_FILE_NAME_LENGTH) {
			newFileName = newFileName.substring(0, MAX_FILE_NAME_LENGTH);
		}
        StoredFile storedFile = new StoredFile(newFileName, newFileExt);

		// check if this is a a file that can be converted by
		// openoffice-service
		boolean canBeConverted = storedFile.isConvertable();
		boolean isPdf = storedFile.isPdf();
		boolean isImage = storedFile.isImage();
		boolean isAsIs = storedFile.isAsIs();

		String completeName = "";

		// add outputfolders for profiles
		if (userProfile) {
			// User Profile Update
			this.deleteUserProfileFilesStoreTemp(currentDir, userId);

			completeName = currentDir + "upload" + File.separatorChar
					+ "profiles" + File.separatorChar;
			File f = new File(completeName);
			if (!f.exists()) {
				boolean c = f.mkdir();
				if (!c) {
					log.error("cannot write to directory");
					// System.out.println("cannot write to directory");
				}
			}
			completeName += "profile_" + userId + File.separatorChar;
			File f2 = new File(completeName);
			if (!f2.exists()) {
				boolean c = f2.mkdir();
				if (!c) {
					log.error("cannot write to directory");
					// System.out.println("cannot write to directory");
				}
			}
		}
		// if it is a presenation it will be copied to another
		// place
		if (isAsIs) {
			// check if this is a room file or UserProfile
			if (userProfile) {
				completeName += newFileName;
			} else {
				completeName = workingDir + newFileName;
			}
		} else if (canBeConverted || isPdf || isImage) {
			// check if this is a room file or UserProfile
			// add Temp folder structure
			String workingDirPpt = currentDir
					+ "uploadtemp"
					+ File.separatorChar
					+ ((userProfile) ? "profiles" + File.separatorChar
							+ "profile_" + userId : roomName)
					+ File.separatorChar;
			localFolder = new File(workingDirPpt);
			if (!localFolder.exists()) {
				localFolder.mkdirs();
			}
			completeName = workingDirPpt + newFileName;
		} else {
			return;
		}

		File f = new File(completeName + newFileExtDot);
		if (f.exists()) {
			int recursiveNumber = 0;
			String tempd = completeName + "_" + recursiveNumber;
			while (f.exists()) {
				recursiveNumber++;
				tempd = completeName + "_" + recursiveNumber;
				f = new File(tempd + newFileExtDot);

			}
			completeName = tempd;
		}

		log.debug("write file to : " + completeName + newFileExtDot);

		FileOutputStream fos = new FileOutputStream(completeName
				+ newFileExtDot);
		byte[] buffer = new byte[1024];
		int len = 0;

		while (len != (-1)) {
			len = is.read(buffer, 0, 1024);
			if (len != (-1))
				fos.write(buffer, 0, len);
		}

		fos.close();
		is.close();

		log.debug("canBeConverted: " + canBeConverted);
		if (canBeConverted) {
			// convert to pdf, thumbs, swf and xml-description
			returnError = GeneratePDF.getInstance().convertPDF(currentDir,
					newFileName, newFileExtDot, roomName, true, completeName);
		} else if (isPdf) {
			boolean isEncrypted = false;
			try {
				// Check if PDF is encrpyted
				PdfReader pdfReader = new PdfReader(completeName
						+ newFileExtDot);

				log.debug("pdfReader.isEncrypted() :: "
						+ pdfReader.isEncrypted());

				log.debug("isMetadataEncrypted : "
						+ pdfReader.isMetadataEncrypted());
				log.debug("is128Key : " + pdfReader.is128Key());
				log.debug("isEncrypted : " + pdfReader.isEncrypted());

				if (pdfReader.isEncrypted()) {
					isEncrypted = true;
				}

			} catch (Exception err) {
				log.error("isEncrypted ", err);
				isEncrypted = true;
			}

			log.debug("isEncrypted :: " + isEncrypted);

			HashMap<String, Object> returnError2 = new HashMap<String, Object>();

			if (isEncrypted) {
				// Do convert pdf to other pdf first
				String inputfile = completeName + newFileExtDot;

				completeName = completeName + "_N_E";
				newFileName = newFileName + "_N_E";

				String outputfile = completeName + newFileExtDot;

				returnError2 = GenerateThumbs.getInstance().decodePDF(
						inputfile, outputfile);

				File f_old = new File(inputfile);
				if (f_old.exists()) {
					f_old.delete();
				}

			}

			// convert to thumbs, swf and xml-description
			returnError = GeneratePDF.getInstance().convertPDF(currentDir,
					newFileName, newFileExtDot, roomName, false, completeName);

			// returnError.put("decodePDF", returnError2);

		} else if (isImage && !isAsIs) {

			log.debug("##### isImage! userProfilePic: " + userProfile);

			if (userProfile) {
				// User Profile Update
				this.deleteUserProfileFiles(currentDir, userId);
				// convert it to JPG
				returnError = GenerateImage.getInstance()
						.convertImageUserProfile(currentDir, newFileName,
								newFileExtDot, userId, newFileName, false);
			} else {
				// convert it to JPG
				log.debug("##### convert it to JPG: " + userProfile);
				returnError = GenerateImage.getInstance().convertImage(
						currentDir, newFileName, newFileExtDot, roomName,
						newFileName, false);
			}
		} else if (isAsIs) {
			if (userProfile) {
				// User Profile Update
				this.deleteUserProfileFiles(currentDir, userId);
				// is UserProfile Picture
				HashMap<String, Object> processThumb1 = GenerateThumbs
						.getInstance().generateThumb("_chat_", currentDir,
								completeName, 40);
				HashMap<String, Object> processThumb2 = GenerateThumbs
						.getInstance().generateThumb("_profile_", currentDir,
								completeName, 126);
				HashMap<String, Object> processThumb3 = GenerateThumbs
						.getInstance().generateThumb("_big_", currentDir,
								completeName, 240);
				returnError.put("processThumb1", processThumb1);
				returnError.put("processThumb2", processThumb2);
				returnError.put("processThumb3", processThumb3);

				File fileNameToStore = new File(completeName + ".jpg");
				String pictureuri = fileNameToStore.getName();
				Users us = UsersDaoImpl.getInstance().getUser(userId);
				us.setUpdatetime(new java.util.Date());
				us.setPictureuri(pictureuri);
				UsersDaoImpl.getInstance().updateUser(us);

				ScopeApplicationAdapter.getInstance().updateUserSessionObject(
						userId, pictureuri);
			} else {
				HashMap<String, Object> processThumb = GenerateThumbs
						.getInstance().generateThumb("_thumb_", currentDir,
								completeName, 50);
				returnError.put("processThumb", processThumb);
			}
		}

		hs.put("message", "library");
		hs.put("action", "newFile");
		hs.put("error", returnError);
		hs.put("fileName", completeName);
	}

	private void deleteUserProfileFilesStoreTemp(String current_dir,
			Long users_id) throws Exception {

		String working_imgdir = current_dir + "upload" + File.separatorChar
				+ "profiles" + File.separatorChar + "profile_" + users_id
				+ File.separatorChar;
		File f = new File(working_imgdir);
		if (f.exists() && f.isDirectory()) {
			this.filesString = f.list();
		}
	}

	private void deleteUserProfileFiles(String current_dir, Long users_id)
			throws Exception {

		String working_imgdir = current_dir + "upload" + File.separatorChar
				+ "profiles" + File.separatorChar + "profile_" + users_id
				+ File.separatorChar;

		for (int i = 0; i < this.filesString.length; i++) {
			String fileName = filesString[i];
			File subf = new File(working_imgdir + fileName);
			subf.delete();
		}
	}

}
