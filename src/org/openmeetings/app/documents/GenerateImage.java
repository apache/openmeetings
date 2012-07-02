/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License") +  you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.openmeetings.app.documents;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.openmeetings.app.OpenmeetingsVariables;
import org.openmeetings.app.data.basic.Configurationmanagement;
import org.openmeetings.app.data.user.dao.UsersDaoImpl;
import org.openmeetings.app.persistence.beans.user.Users;
import org.openmeetings.utils.OmFileHelper;
import org.openmeetings.utils.ProcessHelper;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class GenerateImage {

	private static final Logger log = Red5LoggerFactory.getLogger(
			GenerateImage.class, OpenmeetingsVariables.webAppRootKey);

	@Autowired
	private Configurationmanagement cfgManagement;
	@Autowired
	private UsersDaoImpl usersDao;
	@Autowired
	private GenerateThumbs generateThumbs;

	String getPathToImageMagic() {
		String pathToImageMagic = cfgManagement.getConfKey(3,
				"imagemagick_path").getConf_value();
		if (!pathToImageMagic.equals("")
				&& !pathToImageMagic.endsWith(File.separator)) {
			pathToImageMagic += File.separator;
		}
		pathToImageMagic += "convert" + GenerateSWF.execExt;
		return pathToImageMagic;
	}

	public HashMap<String, HashMap<String, String>> convertImage(String fileName, String fileExt,
			String roomName, String fileNameShort, boolean fullProcessing)
			throws Exception {

		HashMap<String, HashMap<String, String>> returnMap = new HashMap<String, HashMap<String, String>>();

		File fileFullPath = new File(OmFileHelper.getUploadTempRoomDir(roomName), fileName + fileExt);

		File destinationFile = OmFileHelper.getNewFile(OmFileHelper.getUploadRoomDir(roomName), fileName, ".jpg");

		log.debug("##### convertImage destinationFile: " + destinationFile);

		HashMap<String, String> processJPG = this.convertSingleJpg(
				fileFullPath.getCanonicalPath(), destinationFile);
		HashMap<String, String> processThumb = generateThumbs.generateThumb(
				"_thumb_", destinationFile, 50);

		returnMap.put("processJPG", processJPG);
		returnMap.put("processThumb", processThumb);

		// Delete old one
		fileFullPath.delete();

		return returnMap;
	}

	public HashMap<String, HashMap<String, String>> convertImageUserProfile(String fileName, String fileExt, Long users_id,
			String fileNameShort, boolean fullProcessing) throws Exception {

		HashMap<String, HashMap<String, String>> returnMap = new HashMap<String, HashMap<String, String>>();

		File working_pptdir = OmFileHelper.getUploadTempProfilesUserDir(users_id);

		String fileFullPath = new File(working_pptdir, fileName + fileExt).getCanonicalPath();

		File destinationFile = OmFileHelper.getNewFile(OmFileHelper.getUploadProfilesUserDir(users_id), fileName, ".jpg");
		HashMap<String, String> processJPG = this.convertSingleJpg(
				fileFullPath, destinationFile);

		HashMap<String, String> processThumb1 = generateThumbs.generateThumb(
				"_chat_", destinationFile, 40);
		HashMap<String, String> processThumb2 = generateThumbs.generateThumb(
				"_profile_", destinationFile, 126);
		HashMap<String, String> processThumb3 = generateThumbs.generateThumb(
				"_big_", destinationFile, 240);

		returnMap.put("processJPG", processJPG);
		returnMap.put("processThumb1", processThumb1);
		returnMap.put("processThumb2", processThumb2);
		returnMap.put("processThumb3", processThumb3);

		// Delete old one
		File fToDelete = new File(fileFullPath);
		fToDelete.delete();

		String pictureuri = destinationFile.getName();
		Users us = usersDao.getUser(users_id);
		us.setUpdatetime(new java.util.Date());
		us.setPictureuri(pictureuri);
		usersDao.updateUser(us);

		//FIXME: After uploading a new picture all other clients should refresh
		//scopeApplicationAdapter.updateUserSessionObject(users_id, pictureuri);

		return returnMap;
	}

	/**
	 * -density 150 -resize 800
	 * @throws IOException 
	 * 
	 */
	private HashMap<String, String> convertSingleJpg(String inputFile, File outputfile) throws IOException {
		String[] argv = new String[] { getPathToImageMagic(), inputFile, outputfile.getCanonicalPath() };

		// return GenerateSWF.executeScript("convertSingleJpg", argv);

		if (System.getProperty("os.name").toUpperCase().indexOf("WINDOWS") == -1) {
			return ProcessHelper.executeScript("generateBatchThumbByWidth", argv);
		} else {
			return generateThumbs.processImageWindows(argv);
		}

	}

	public HashMap<String, String> convertImageByTypeAndSize(String inputFile,
			String outputfile, int width, int height) {
		String[] argv = new String[] { getPathToImageMagic(), "-size",
				width + "x" + height, inputFile, outputfile };
		return ProcessHelper.executeScript("convertImageByTypeAndSizeAndDepth",
				argv);
	}

	public HashMap<String, String> convertImageByTypeAndSizeAndDepth(
			String inputFile, String outputfile, int width, int height,
			int depth) {
		String[] argv = new String[] { getPathToImageMagic(), "-size",
				width + "x" + height, "-depth", Integer.toString(depth),
				inputFile, outputfile };
		return ProcessHelper.executeScript("convertImageByTypeAndSizeAndDepth",
				argv);
	}

}
