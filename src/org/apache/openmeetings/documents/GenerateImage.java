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
package org.apache.openmeetings.documents;

import java.io.File;
import java.io.IOException;

import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.data.flvrecord.converter.BaseConverter;
import org.apache.openmeetings.data.user.dao.UsersDao;
import org.apache.openmeetings.documents.beans.ConverterProcessResult;
import org.apache.openmeetings.documents.beans.ConverterProcessResultList;
import org.apache.openmeetings.persistence.beans.user.User;
import org.apache.openmeetings.utils.OmFileHelper;
import org.apache.openmeetings.utils.ProcessHelper;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class GenerateImage extends BaseConverter {

	private static final Logger log = Red5LoggerFactory.getLogger(
			GenerateImage.class, OpenmeetingsVariables.webAppRootKey);

	@Autowired
	private UsersDao usersDao;
	@Autowired
	private GenerateThumbs generateThumbs;

	public ConverterProcessResultList convertImage(String fileName, String fileExt,
			String roomName, String fileNameShort, boolean fullProcessing)
			throws Exception {

		ConverterProcessResultList returnMap = new ConverterProcessResultList();

		File fileFullPath = new File(OmFileHelper.getUploadTempRoomDir(roomName), fileName + fileExt);

		File destinationFile = OmFileHelper.getNewFile(OmFileHelper.getUploadRoomDir(roomName), fileName, ".jpg");

		log.debug("##### convertImage destinationFile: " + destinationFile);

		ConverterProcessResult processJPG = this.convertSingleJpg(
				fileFullPath.getCanonicalPath(), destinationFile);
		ConverterProcessResult processThumb = generateThumbs.generateThumb(
				"_thumb_", destinationFile, 50);

		returnMap.addItem("processJPG", processJPG);
		returnMap.addItem("processThumb", processThumb);

		// Delete old one
		fileFullPath.delete();

		return returnMap;
	}

	public ConverterProcessResultList convertImageUserProfile(String fileName, String fileExt, Long users_id,
			String fileNameShort, boolean fullProcessing) throws Exception {

		ConverterProcessResultList returnMap = new ConverterProcessResultList();

		File working_pptdir = OmFileHelper.getUploadTempProfilesUserDir(users_id);

		String fileFullPath = new File(working_pptdir, fileName + fileExt).getCanonicalPath();

		File destinationFile = OmFileHelper.getNewFile(OmFileHelper.getUploadProfilesUserDir(users_id), fileName, ".jpg");
		ConverterProcessResult processJPG = this.convertSingleJpg(
				fileFullPath, destinationFile);

		ConverterProcessResult processThumb1 = generateThumbs.generateThumb(
				"_chat_", destinationFile, 40);
		ConverterProcessResult processThumb2 = generateThumbs.generateThumb(
				"_profile_", destinationFile, 126);
		ConverterProcessResult processThumb3 = generateThumbs.generateThumb(
				"_big_", destinationFile, 240);

		returnMap.addItem("processJPG", processJPG);
		returnMap.addItem("processThumb1", processThumb1);
		returnMap.addItem("processThumb2", processThumb2);
		returnMap.addItem("processThumb3", processThumb3);

		// Delete old one
		File fToDelete = new File(fileFullPath);
		fToDelete.delete();

		String pictureuri = destinationFile.getName();
		User us = usersDao.get(users_id);
		us.setUpdatetime(new java.util.Date());
		us.setPictureuri(pictureuri);
		usersDao.update(us, users_id);

		//FIXME: After uploading a new picture all other clients should refresh
		//scopeApplicationAdapter.updateUserSessionObject(users_id, pictureuri);

		return returnMap;
	}

	/**
	 * -density 150 -resize 800
	 * @throws IOException 
	 * 
	 */
	private ConverterProcessResult convertSingleJpg(String inputFile, File outputfile) throws IOException {
		String[] argv = new String[] { getPathToImageMagick(), inputFile, outputfile.getCanonicalPath() };

		// return GenerateSWF.executeScript("convertSingleJpg", argv);

		if (System.getProperty("os.name").toUpperCase().indexOf("WINDOWS") == -1) {
			return ProcessHelper.executeScript("generateBatchThumbByWidth", argv);
		} else {
			return generateThumbs.processImageWindows(argv);
		}

	}

	public ConverterProcessResult convertImageByTypeAndSize(String inputFile,
			String outputfile, int width, int height) {
		String[] argv = new String[] { getPathToImageMagick(), "-size",
				width + "x" + height, inputFile, outputfile };
		return ProcessHelper.executeScript("convertImageByTypeAndSizeAndDepth",
				argv);
	}

	public ConverterProcessResult convertImageByTypeAndSizeAndDepth(
			String inputFile, String outputfile, int width, int height,
			int depth) {
		String[] argv = new String[] { getPathToImageMagick(), "-size",
				width + "x" + height, "-depth", Integer.toString(depth),
				inputFile, outputfile };
		return ProcessHelper.executeScript("convertImageByTypeAndSizeAndDepth",
				argv);
	}

}
