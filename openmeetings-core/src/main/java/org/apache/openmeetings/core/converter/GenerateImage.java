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
package org.apache.openmeetings.core.converter;

import static org.apache.openmeetings.util.OmFileHelper.JPG_EXTENSION;
import static org.apache.openmeetings.util.OmFileHelper.getUploadProfilesUserDir;
import static org.apache.openmeetings.util.OmFileHelper.profileFileName;
import static org.apache.openmeetings.util.OmFileHelper.profileImagePrefix;
import static org.apache.openmeetings.util.OmFileHelper.thumbImagePrefix;
import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import org.apache.commons.transaction.util.FileHelper;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.util.OmFileHelper;
import org.apache.openmeetings.util.process.ConverterProcessResult;
import org.apache.openmeetings.util.process.ConverterProcessResultList;
import org.apache.openmeetings.util.process.ProcessHelper;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class GenerateImage extends BaseConverter {
	private static final Logger log = Red5LoggerFactory.getLogger(GenerateImage.class, webAppRootKey);

	@Autowired
	private UserDao userDao;
	@Autowired
	private GenerateThumbs generateThumbs;

	public ConverterProcessResultList convertImage(String fileName, String fileExt, String roomName) throws IOException {
		ConverterProcessResultList returnMap = new ConverterProcessResultList();

		File fileFullPath = new File(OmFileHelper.getUploadTempRoomDir(roomName), fileName + fileExt);

		File destinationFile = OmFileHelper.getNewFile(OmFileHelper.getUploadRoomDir(roomName), fileName, ".jpg");

		log.debug("##### convertImage destinationFile: " + destinationFile);

		ConverterProcessResult processJPG = convertSingleJpg(fileFullPath.getCanonicalPath(), destinationFile);
		ConverterProcessResult processThumb = generateThumbs.generateThumb(thumbImagePrefix, destinationFile, 50);

		returnMap.addItem("processJPG", processJPG);
		returnMap.addItem("processThumb", processThumb);

		// Delete old one
		fileFullPath.delete();

		return returnMap;
	}

	public ConverterProcessResultList convertImageUserProfile(File file, Long userId, boolean skipConvertion) throws Exception {
		ConverterProcessResultList returnMap = new ConverterProcessResultList();
		
		// User Profile Update
		File[] files = getUploadProfilesUserDir(userId).listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.getName().endsWith(JPG_EXTENSION);
			}
		});
		if (files != null) {
			for (File f : files) {
				FileHelper.removeRec(f);
			}
		}
		
		File destinationFile = OmFileHelper.getNewFile(getUploadProfilesUserDir(userId), profileFileName, JPG_EXTENSION);
		if (!skipConvertion) {
			returnMap.addItem("processJPG", convertSingleJpg(file.getCanonicalPath(), destinationFile));
		} else {
			FileHelper.copy(file, destinationFile);
		}
		returnMap.addItem("processThumb2", generateThumbs.generateThumb(profileImagePrefix, destinationFile, 126));

		if (!skipConvertion) {
			// Delete old one
			file.delete();
		}

		String pictureuri = destinationFile.getName();
		User us = userDao.get(userId);
		us.setUpdated(new java.util.Date());
		us.setPictureuri(pictureuri);
		userDao.update(us, userId);

		//FIXME: After uploading a new picture all other clients should refresh
		//scopeApplicationAdapter.updateUserSessionObject(userId, pictureuri);

		return returnMap;
	}

	/**
	 * -density 150 -resize 800
	 * @throws IOException 
	 * 
	 */
	private ConverterProcessResult convertSingleJpg(String inputFile, File outputfile) throws IOException {
		String[] argv = new String[] { getPathToImageMagick(), inputFile, outputfile.getCanonicalPath() };

		return ProcessHelper.executeScript("generateBatchThumbByWidth", argv);
	}

	public ConverterProcessResult convertImageByTypeAndSize(String inputFile,
			String outputfile, int width, int height) {
		String[] argv = new String[] { getPathToImageMagick(), "-size",
				width + "x" + height, inputFile, outputfile };
		return ProcessHelper.executeScript("convertImageByTypeAndSizeAndDepth", argv);
	}

	public ConverterProcessResult convertImageByTypeAndSizeAndDepth(
			String inputFile, String outputfile, int width, int height,
			int depth) {
		String[] argv = new String[] { getPathToImageMagick(), "-size",
				width + "x" + height, "-depth", Integer.toString(depth),
				inputFile, outputfile };
		return ProcessHelper.executeScript("convertImageByTypeAndSizeAndDepth", argv);
	}
}
