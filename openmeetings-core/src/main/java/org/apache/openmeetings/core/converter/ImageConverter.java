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

import static org.apache.commons.io.FileUtils.copyFile;
import static org.apache.openmeetings.util.OmFileHelper.EXTENSION_JPG;
import static org.apache.openmeetings.util.OmFileHelper.JPG_MIME_TYPE;
import static org.apache.openmeetings.util.OmFileHelper.getUploadProfilesUserDir;
import static org.apache.openmeetings.util.OmFileHelper.profileFileName;
import static org.apache.openmeetings.util.OmFileHelper.profileImagePrefix;
import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;
import static org.apache.openmeetings.util.process.ConverterProcessResult.ZERO;
import static org.apache.tika.metadata.HttpHeaders.CONTENT_TYPE;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.file.BaseFileItem;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.util.OmFileHelper;
import org.apache.openmeetings.util.StoredFile;
import org.apache.openmeetings.util.process.ConverterProcessResult;
import org.apache.openmeetings.util.process.ConverterProcessResultList;
import org.apache.openmeetings.util.process.ProcessHelper;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TIFF;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.image.ImageParser;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.xml.sax.helpers.DefaultHandler;

public class ImageConverter extends BaseConverter {
	private static final Logger log = Red5LoggerFactory.getLogger(ImageConverter.class, webAppRootKey);

	@Autowired
	private UserDao userDao;

	public ConverterProcessResultList convertImage(BaseFileItem f, StoredFile sf) throws IOException {
		ConverterProcessResultList returnMap = new ConverterProcessResultList();

		File jpg = f.getFile(EXTENSION_JPG);
		if (!sf.isJpg()) {
			File img = f.getFile(sf.getExt());

			log.debug("##### convertImage destinationFile: " + jpg);
			returnMap.addItem("processJPG", convertSingleJpg(img, jpg));
		} else if (!jpg.exists()){
			copyFile(f.getFile(sf.getExt()), jpg);
		}
		returnMap.addItem("get JPG dimensions", initSize(f, jpg, JPG_MIME_TYPE));
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
		returnMap.addItem("processThumb2", generateThumb(profileImagePrefix, destinationFile, 126));

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

	private static ConverterProcessResult initSize(BaseFileItem f, File img, String mime) {
		ConverterProcessResult res = new ConverterProcessResult();
		res.setProcess("get image dimensions :: " + f.getId());
		final Parser parser = new ImageParser();
		try (InputStream is = new FileInputStream(img)) {
			Metadata metadata = new Metadata();
			metadata.set(CONTENT_TYPE, mime);
			parser.parse(is, new DefaultHandler(), metadata, new ParseContext());
			f.setWidth(Integer.valueOf(metadata.get(TIFF.IMAGE_WIDTH)));
			f.setHeight(Integer.valueOf(metadata.get(TIFF.IMAGE_LENGTH)));
			res.setExitCode(ZERO);
		} catch (Exception e) {
			log.error("Error while getting dimensions", e);
			res.setError("Error while getting dimensions");
			res.setException(e.getMessage());
			res.setExitCode(-1);
		}
		return res;
	}

	/**
	 * -density 150 -resize 800
	 * @throws IOException
	 *
	 */
	private ConverterProcessResult convertSingleJpg(File in, File out) throws IOException {
		String[] argv = new String[] { getPathToConvert(), in.getCanonicalPath(), out.getCanonicalPath() };

		return ProcessHelper.executeScript("convertSingleJpg", argv);
	}

	public ConverterProcessResult resize(File in, File out, Integer width, Integer height) throws IOException {
		String[] argv = new String[] { getPathToConvert()
				, "-resize", (width == null ? "" : width) + (height == null ? "" : "x" + height)
				, in.getCanonicalPath(), out.getCanonicalPath()
				};
		return ProcessHelper.executeScript("GenerateImage::resize", argv);
	}

	public ConverterProcessResult decodePDF(String inputfile, String outputfile) {
		log.debug("decodePDF");
		String[] argv = new String[] { getPathToConvert(), inputfile, outputfile };

		return ProcessHelper.executeScript("generateBatchThumbByWidth", argv);
	}

	public ConverterProcessResult generateBatchThumb(File in, File outDir, Integer thumbSize, String pre) throws IOException {
		log.debug("generateBatchThumbByWidth");
		String[] argv = new String[] {
			getPathToConvert()
			, "-thumbnail" // FIXME
			, "" + thumbSize
			, in.getCanonicalPath()
			, new File(outDir, "_" + pre + "_page-%04d.jpg").getCanonicalPath()
			};

		return ProcessHelper.executeScript("generateBatchThumbByWidth", argv);
	}

	public ConverterProcessResult generateThumb(String pre, File f, Integer thumbSize) throws IOException {
		log.debug("generateThumb");
		// Init variables
		String name = f.getName();
		File parent = f.getParentFile();

		String[] argv = new String[] {
			getPathToConvert()
			, "-thumbnail"
			, thumbSize + "x" + thumbSize
			, f.getCanonicalPath()
			, new File(parent, pre + name).getCanonicalPath()
			};

		return ProcessHelper.executeScript("generateBatchThumbByWidth", argv);
	}
}
