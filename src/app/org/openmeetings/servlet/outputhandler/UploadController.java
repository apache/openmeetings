package org.openmeetings.servlet.outputhandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.openmeetings.app.OpenmeetingsVariables;
import org.openmeetings.app.data.file.FileProcessor;
import org.openmeetings.app.data.file.dao.FileExplorerItemDaoImpl;
import org.openmeetings.app.data.user.dao.UsersDaoImpl;
import org.openmeetings.app.documents.GenerateImage;
import org.openmeetings.app.documents.GeneratePDF;
import org.openmeetings.app.documents.GenerateThumbs;
import org.openmeetings.app.persistence.beans.user.Users;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.openmeetings.utils.StoredFile;
import org.openmeetings.utils.stringhandlers.StringComparer;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class UploadController extends AbstractUploadController {
	private static final Logger log = Red5LoggerFactory.getLogger(
			UploadController.class, OpenmeetingsVariables.webAppRootKey);
	
	@Autowired
	private UsersDaoImpl usersDao;
	@Autowired
	private ScopeApplicationAdapter scopeApplicationAdapter;
	@Autowired
	private GeneratePDF generatePDF;
	@Autowired
	private GenerateThumbs generateThumbs;
	@Autowired
	private GenerateImage generateImage;
	@Autowired
	private FileProcessor fileProcessor;
	@Autowired
	private FileExplorerItemDaoImpl fileExplorerItemDao;

	private String filesString[] = null;
	
    @RequestMapping(value = "/file.upload", method = RequestMethod.POST)
    public void handleFileUpload(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException {
    	HashMap<UploadParams, Object> params = validate(request, true);
    	try {
	    	LinkedHashMap<String, Object> hs = prepareMessage(params);
			String room_idAsString = request.getParameter("room_id");
			if (room_idAsString == null) {
				throw new ServletException("Missing Room ID");
			}
	
			Long room_id_to_Store = Long.parseLong(room_idAsString);
	
			String isOwnerAsString = request.getParameter("isOwner");
			if (isOwnerAsString == null) {
				throw new ServletException("Missing isOwnerAsString");
			}
			boolean isOwner = false;
			if (isOwnerAsString.equals("1")) {
				isOwner = true;
			}
	
			String parentFolderIdAsString = request
					.getParameter("parentFolderId");
			if (parentFolderIdAsString == null) {
				throw new ServletException("Missing parentFolderId ID");
			}
			Long parentFolderId = Long.parseLong(parentFolderIdAsString);
	
			String current_dir = context.getRealPath("/");
	
			MultipartFile multipartFile = getParam(params, UploadParams.pFile, MultipartFile.class);
			InputStream is = multipartFile.getInputStream();
			String fileSystemName = multipartFile.getOriginalFilename();
			log.debug("fileSystemName: " + fileSystemName);
	
			HashMap<String, HashMap<String, String>> returnError = fileProcessor
					.processFile(getParam(params, UploadParams.pUserId, Long.class), room_id_to_Store, isOwner, is,
							parentFolderId, fileSystemName, current_dir, hs, 0L, ""); // externalFilesId,
																						// externalType
	
			HashMap<String, String> returnAttributes = returnError
					.get("returnAttributes");
	
			// Flash cannot read the response of an upload
			// httpServletResponse.getWriter().print(returnError);
			hs.put("message", "library");
			hs.put("action", "newFile");
			hs.put("fileExplorerItem",
					fileExplorerItemDao.getFileExplorerItemsById(
							Long.parseLong(returnAttributes.get(
									"fileExplorerItemId").toString())));
			hs.put("error", returnError);
			hs.put("fileName", returnAttributes.get("completeName"));
			sendMessage(params, hs);
		} catch (ServletException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception during upload: ", e);
			throw new ServletException(e);
    	}
    }

    @RequestMapping(value = "/upload.upload", method = RequestMethod.POST)
    public void handleFormUpload(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		try {
	    	HashMap<UploadParams, Object> params = validate(request, true);
	    	LinkedHashMap<String, Object> hs = prepareMessage(params);
			String room_id = request.getParameter("room_id");
			if (room_id == null) {
				room_id = "default";
			}
			String roomName = StringUtils.deleteWhitespace(room_id);
	
			String moduleName = request.getParameter("moduleName");
			if (moduleName == null) {
				moduleName = "nomodule";
			}
			if (moduleName.equals("nomodule")) {
				log.debug("module name missed");
				return;
			}
			boolean userProfile = moduleName.equals("userprofile");
	
			MultipartFile multipartFile = getParam(params, UploadParams.pFile, MultipartFile.class);
			InputStream is = multipartFile.getInputStream();
			String fileSystemName = multipartFile.getOriginalFilename();
			fileSystemName = StringUtils.deleteWhitespace(fileSystemName);
	
			// Flash cannot read the response of an upload
			// httpServletResponse.getWriter().print(returnError);
			uploadFile(request, userProfile, getParam(params, UploadParams.pUserId, Long.class), roomName, is, fileSystemName, hs);
			sendMessage(params, hs);
		} catch (ServletException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception during upload: ", e);
			throw new ServletException(e);
		}
    }

    private LinkedHashMap<String, Object> prepareMessage(HashMap<UploadParams, Object> params) {
		LinkedHashMap<String, Object> hs = new LinkedHashMap<String, Object>();
		hs.put("user", usersDao.getUser(getParam(params, UploadParams.pUserId, Long.class)));
		return hs;
    }
    
    private void sendMessage(HashMap<UploadParams, Object> params, LinkedHashMap<String, Object> hs) {
		scopeApplicationAdapter.sendMessageWithClientByPublicSID(hs,
				getParam(params, UploadParams.pPublicSID, String.class));
    }
    
	private void uploadFile(HttpServletRequest request, boolean userProfile, Long userId, String roomName,
			InputStream is, String fileSystemName, Map<String, Object> hs)
			throws Exception {
		HashMap<String, HashMap<String, String>> returnError = new HashMap<String, HashMap<String, String>>();

		// Get the current user directory
		String currentDir = context.getRealPath("/");
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
			completeName += ScopeApplicationAdapter.profilesPrefix + userId
					+ File.separatorChar;
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
							+ ScopeApplicationAdapter.profilesPrefix + userId
							: roomName) + File.separatorChar;
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
			returnError = generatePDF.convertPDF(currentDir, newFileName,
					newFileExtDot, roomName, true, completeName);
		} else if (isPdf) {
			
			boolean isEncrypted = true; 
			
			log.debug("isEncrypted :: " + isEncrypted);

			if (isEncrypted) {
				// Do convert pdf to other pdf first
				String inputfile = completeName + newFileExtDot;

				completeName = completeName + "_N_E";
				newFileName = newFileName + "_N_E";

				String outputfile = completeName + newFileExtDot;

				generateThumbs.decodePDF(inputfile, outputfile);

				File f_old = new File(inputfile);
				if (f_old.exists()) {
					f_old.delete();
				}

			}

			// convert to thumbs, swf and xml-description
			returnError = generatePDF.convertPDF(currentDir, newFileName,
					newFileExtDot, roomName, false, completeName);

			// returnError.put("decodePDF", returnError2);

		} else if (isImage && !isAsIs) {

			log.debug("##### isImage! userProfilePic: " + userProfile);

			if (userProfile) {
				// User Profile Update
				this.deleteUserProfileFiles(currentDir, userId);
				// convert it to JPG
				returnError = generateImage.convertImageUserProfile(
						currentDir, newFileName, newFileExtDot, userId,
						newFileName, false);
			} else {
				// convert it to JPG
				log.debug("##### convert it to JPG: " + userProfile);
				returnError = generateImage.convertImage(currentDir,
						newFileName, newFileExtDot, roomName, newFileName,
						false);
			}
		} else if (isAsIs) {
			if (userProfile) {
				// User Profile Update
				this.deleteUserProfileFiles(currentDir, userId);
				// is UserProfile Picture
				HashMap<String, String> processThumb1 = generateThumbs
						.generateThumb("_chat_", currentDir, completeName, 40);
				HashMap<String, String> processThumb2 = generateThumbs
						.generateThumb("_profile_", currentDir, completeName,
								126);
				HashMap<String, String> processThumb3 = generateThumbs
						.generateThumb("_big_", currentDir, completeName, 240);
				returnError.put("processThumb1", processThumb1);
				returnError.put("processThumb2", processThumb2);
				returnError.put("processThumb3", processThumb3);

				File fileNameToStore = new File(completeName + ".jpg");
				String pictureuri = fileNameToStore.getName();
				Users us = usersDao.getUser(userId);
				us.setUpdatetime(new java.util.Date());
				us.setPictureuri(pictureuri);
				usersDao.updateUser(us);

				//FIXME: After updating the picture url all other users should refresh
			} else {
				HashMap<String, String> processThumb = generateThumbs
						.generateThumb("_thumb_", currentDir, completeName, 50);
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
				+ "profiles" + File.separatorChar
				+ ScopeApplicationAdapter.profilesPrefix + users_id
				+ File.separatorChar;
		File f = new File(working_imgdir);
		if (f.exists() && f.isDirectory()) {
			this.filesString = f.list();
		}
	}

	private void deleteUserProfileFiles(String current_dir, Long users_id)
			throws Exception {

		String working_imgdir = current_dir + "upload" + File.separatorChar
				+ "profiles" + File.separatorChar
				+ ScopeApplicationAdapter.profilesPrefix + users_id
				+ File.separatorChar;

		for (int i = 0; i < this.filesString.length; i++) {
			String fileName = filesString[i];
			File subf = new File(working_imgdir + fileName);
			subf.delete();
		}
	}
}
