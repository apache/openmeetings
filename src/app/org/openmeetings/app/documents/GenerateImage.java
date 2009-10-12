package org.openmeetings.app.documents;

import java.io.File;
import java.util.HashMap;

import org.openmeetings.app.data.basic.Configurationmanagement;
import org.openmeetings.app.data.user.dao.UsersDaoImpl;
import org.openmeetings.app.hibernate.beans.user.Users;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.slf4j.LoggerFactory;
import org.apache.log4j.Logger;

public class GenerateImage {

	private static final Logger log = Logger.getLogger(
			GenerateImage.class);

	private static GenerateImage instance;

	private GenerateImage() {
	}

	public static synchronized GenerateImage getInstance() {
		if (instance == null) {
			instance = new GenerateImage();
		}
		return instance;
	}

	static String getPathToImageMagic() {
		String pathToImageMagic = Configurationmanagement.getInstance()
				.getConfKey(3, "imagemagick_path").getConf_value();
		if (!pathToImageMagic.equals("")
				&& !pathToImageMagic.endsWith(File.separator)) {
			pathToImageMagic += File.separator;
		}
		pathToImageMagic += "convert" + GenerateSWF.execExt;
		return pathToImageMagic;
	}
	
	public HashMap<String, HashMap<String, Object>> convertImage(
			String current_dir, String fileName, String fileExt,
			String roomName, String fileNameShort, boolean fullProcessing)
			throws Exception {

		HashMap<String, HashMap<String, Object>> returnMap = new HashMap<String, HashMap<String, Object>>();

		String working_imgdir = current_dir + "upload" + File.separatorChar
				+ roomName + File.separatorChar;
		String working_pptdir = current_dir + "uploadtemp" + File.separatorChar
				+ roomName + File.separatorChar;

		String fileFullPath = working_pptdir + fileName + fileExt;

		File f = new File(working_imgdir + fileName + fileExt);
		if (f.exists()) {
			int recursiveNumber = 0;
			String tempd = fileName + "_" + recursiveNumber;
			while (f.exists()) {
				recursiveNumber++;
				tempd = fileName + "_" + recursiveNumber;
				f = new File(working_imgdir + tempd + fileExt);
			}
			fileName = tempd;
		}

		String destinationFile = working_imgdir + fileName;

		log.debug("##### convertImage destinationFile: " + destinationFile);

		HashMap<String, Object> processJPG = this.convertSingleJpg(
				fileFullPath, destinationFile);
		HashMap<String, Object> processThumb = GenerateThumbs.getInstance()
				.generateThumb("_thumb_", current_dir, destinationFile, 50);

		returnMap.put("processJPG", processJPG);
		returnMap.put("processThumb", processThumb);

		// Delete old one
		File fToDelete = new File(fileFullPath);
		fToDelete.delete();

		return returnMap;
	}

	public HashMap<String, HashMap<String, Object>> convertImageUserProfile(
			String current_dir, String fileName, String fileExt, Long users_id,
			String fileNameShort, boolean fullProcessing) throws Exception {

		HashMap<String, HashMap<String, Object>> returnMap = new HashMap<String, HashMap<String, Object>>();

		String working_imgdir = current_dir + "upload" + File.separatorChar
				+ "profiles" + File.separatorChar + "profile_" + users_id
				+ File.separatorChar;
		String working_pptdir = current_dir + "uploadtemp" + File.separatorChar
				+ "profiles" + File.separatorChar + "profile_" + users_id
				+ File.separatorChar;

		String fileFullPath = working_pptdir + fileName + fileExt;

		File f = new File(working_imgdir + fileName + fileExt);
		if (f.exists()) {
			int recursiveNumber = 0;
			String tempd = fileName + "_" + recursiveNumber;
			while (f.exists()) {
				recursiveNumber++;
				tempd = fileName + "_" + recursiveNumber;
				f = new File(working_imgdir + tempd + fileExt);
			}
			fileName = tempd;
		}

		String destinationFile = working_imgdir + fileName;
		HashMap<String, Object> processJPG = this.convertSingleJpg(
				fileFullPath, destinationFile);

		HashMap<String, Object> processThumb1 = GenerateThumbs.getInstance()
				.generateThumb("_chat_", current_dir, destinationFile, 40);
		HashMap<String, Object> processThumb2 = GenerateThumbs.getInstance()
				.generateThumb("_profile_", current_dir, destinationFile, 126);
		HashMap<String, Object> processThumb3 = GenerateThumbs.getInstance()
				.generateThumb("_big_", current_dir, destinationFile, 240);

		returnMap.put("processJPG", processJPG);
		returnMap.put("processThumb1", processThumb1);
		returnMap.put("processThumb2", processThumb2);
		returnMap.put("processThumb3", processThumb3);

		// Delete old one
		File fToDelete = new File(fileFullPath);
		fToDelete.delete();

		File fileNameToStore = new File(destinationFile + ".jpg");
		String pictureuri = fileNameToStore.getName();
		Users us = UsersDaoImpl.getInstance().getUser(users_id);
		us.setUpdatetime(new java.util.Date());
		us.setPictureuri(pictureuri);
		UsersDaoImpl.getInstance().updateUser(us);

		ScopeApplicationAdapter.getInstance().updateUserSessionObject(users_id,
				pictureuri);

		return returnMap;
	}

	/**
	 * -density 150 -resize 800
	 * 
	 */
	private HashMap<String, Object> convertSingleJpg(String inputFile,
			String outputfile) {
		String[] argv = new String[] { getPathToImageMagic(),
				inputFile, outputfile + ".jpg" };
		return GenerateSWF.executeScript("convertSingleJpg", argv);
	}

	public HashMap<String, Object> convertImageByTypeAndSize(String inputFile,
			String outputfile, int width, int height) {
		String[] argv = new String[] { getPathToImageMagic(), "-size",
				width + "x" + height, inputFile, outputfile };
		return GenerateSWF.executeScript("convertImageByTypeAndSizeAndDepth",
				argv);
	}

	public HashMap<String, Object> convertImageByTypeAndSizeAndDepth(
			String inputFile, String outputfile, int width, int height,
			int depth) {
		String[] argv = new String[] { getPathToImageMagic(), "-size",
				width + "x" + height, "-depth", Integer.toString(depth),
				inputFile, outputfile };
		return GenerateSWF.executeScript("convertImageByTypeAndSizeAndDepth",
				argv);
	}

}
