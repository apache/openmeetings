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

import static org.apache.openmeetings.util.OmFileHelper.EXTENSION_JPG;
import static org.apache.openmeetings.util.OmFileHelper.getUploadProfilesUserDir;
import static org.apache.openmeetings.util.OmFileHelper.profileFileName;
import static org.apache.openmeetings.util.OmFileHelper.profileImagePrefix;
import static org.apache.openmeetings.util.OmFileHelper.thumbImagePrefix;
import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.file.FileItem;
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

	public ConverterProcessResultList convertImage(FileItem f, String ext) throws IOException {
		ConverterProcessResultList returnMap = new ConverterProcessResultList();

		File img = f.getFile(ext);
		File jpg = f.getFile(EXTENSION_JPG);

		log.debug("##### convertImage destinationFile: " + jpg);

		returnMap.addItem("processJPG", convertSingleJpg(img, jpg));
		returnMap.addItem("processThumb", generateThumbs.generateThumb(thumbImagePrefix, jpg, 50));

		return returnMap;
	}

	public ConverterProcessResultList convertImageUserProfile(File file, Long userId, boolean skipConvertion) throws Exception {
		ConverterProcessResultList returnMap = new ConverterProcessResultList();
		
		// User Profile Update
		File[] files = getUploadProfilesUserDir(userId).listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.getName().endsWith(EXTENSION_JPG);
			}
		});
		if (files != null) {
			for (File f : files) {
				FileUtils.deleteQuietly(f);
			}
		}
		
		File destinationFile = OmFileHelper.getNewFile(getUploadProfilesUserDir(userId), profileFileName, EXTENSION_JPG);
		if (!skipConvertion) {
			returnMap.addItem("processJPG", convertSingleJpg(file, destinationFile));
		} else {
			FileUtils.copyFile(file, destinationFile);
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
	private ConverterProcessResult convertSingleJpg(File in, File out) throws IOException {
		String[] argv = new String[] { getPathToImageMagick(), in.getCanonicalPath(), out.getCanonicalPath() };

		return ProcessHelper.executeScript("convertSingleJpg", argv);
	}

	public ConverterProcessResult resize(File in, File out, Integer width, Integer height) throws IOException {
		String[] argv = new String[] { getPathToImageMagick()
				, "-size", (width == null ? "" : width) + (height == null ? "" : "x" + height)
				, in.getCanonicalPath(), out.getCanonicalPath()
				};
		return ProcessHelper.executeScript("GenerateImage::resize", argv);
	}
}
