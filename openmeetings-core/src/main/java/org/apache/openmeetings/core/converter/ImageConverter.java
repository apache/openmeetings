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
import static org.apache.openmeetings.util.OmFileHelper.DOC_PAGE_PREFIX;
import static org.apache.openmeetings.util.OmFileHelper.EXTENSION_PNG;
import static org.apache.openmeetings.util.OmFileHelper.PNG_MIME_TYPE;
import static org.apache.openmeetings.util.OmFileHelper.PROFILE_FILE_NAME;
import static org.apache.openmeetings.util.OmFileHelper.getUploadProfilesUserDir;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DOCUMENT_DPI;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DOCUMENT_QUALITY;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getWebAppRootKey;
import static org.apache.openmeetings.util.process.ProcessResult.ZERO;
import static org.apache.tika.metadata.HttpHeaders.CONTENT_TYPE;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.file.BaseFileItem;
import org.apache.openmeetings.db.entity.file.FileItem;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.util.OmFileHelper;
import org.apache.openmeetings.util.StoredFile;
import org.apache.openmeetings.util.process.ProcessHelper;
import org.apache.openmeetings.util.process.ProcessResult;
import org.apache.openmeetings.util.process.ProcessResultList;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TIFF;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.image.ImageParser;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xml.sax.helpers.DefaultHandler;

@Component
public class ImageConverter extends BaseConverter {
	private static final Logger log = Red5LoggerFactory.getLogger(ImageConverter.class, getWebAppRootKey());
	private static final String PAGE_TMPLT = DOC_PAGE_PREFIX + "-%04d." + EXTENSION_PNG;

	@Autowired
	private UserDao userDao;

	public ProcessResultList convertImage(BaseFileItem f, StoredFile sf) throws IOException {
		return convertImage(f, sf, new ProcessResultList());
	}

	public ProcessResultList convertImage(BaseFileItem f, StoredFile sf, ProcessResultList logs) throws IOException {
		File png = f.getFile(EXTENSION_PNG);
		if (!sf.isPng()) {
			File img = f.getFile(sf.getExt());

			log.debug("##### convertImage destinationFile: " + png);
			logs.add(convertSinglePng(img, png));
		} else if (!png.exists()){
			copyFile(f.getFile(sf.getExt()), png);
		}
		logs.add(initSize(f, png, PNG_MIME_TYPE));
		return logs;
	}

	public ProcessResultList convertImageUserProfile(File file, Long userId, boolean skipConvertion) throws Exception {
		ProcessResultList returnMap = new ProcessResultList();

		// User Profile Update
		File[] files = getUploadProfilesUserDir(userId).listFiles(fi -> fi.getName().endsWith(EXTENSION_PNG));
		if (files != null) {
			for (File f : files) {
				FileUtils.deleteQuietly(f);
			}
		}

		File destinationFile = OmFileHelper.getNewFile(getUploadProfilesUserDir(userId), PROFILE_FILE_NAME, EXTENSION_PNG);
		if (!skipConvertion) {
			returnMap.add(convertSinglePng(file, destinationFile));
		} else {
			FileUtils.copyFile(file, destinationFile);
		}

		if (!skipConvertion) {
			// Delete old one
			file.delete();
		}

		String img = destinationFile.getName();
		User us = userDao.get(userId);
		us.setUpdated(new Date());
		us.setPictureUri(img);
		userDao.update(us, userId);

		return returnMap;
	}

	private String getDpi() {
		return cfgDao.getString(CONFIG_DOCUMENT_DPI, "150");
	}

	private String getQuality() {
		return cfgDao.getString(CONFIG_DOCUMENT_QUALITY, "90");
	}

	private static ProcessResult initSize(BaseFileItem f, File img, String mime) {
		ProcessResult res = new ProcessResult();
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
	 * @param in - input file
	 * @param out - output file
	 * @return - conversion result
	 * @throws IOException
	 *
	 */
	private ProcessResult convertSinglePng(File in, File out) throws IOException {
		String[] argv = new String[] { getPathToConvert(), in.getCanonicalPath(), out.getCanonicalPath() };

		return ProcessHelper.executeScript("convertSinglePng", argv);
	}

	public ProcessResult resize(File in, File out, Integer width, Integer height) throws IOException {
		String[] argv = new String[] { getPathToConvert()
				, "-resize", (width == null ? "" : width) + (height == null ? "" : "x" + height)
				, in.getCanonicalPath(), out.getCanonicalPath()
				};
		return ProcessHelper.executeScript("resize", argv);
	}

	/**
	 * Converts PDF document to the series of images
	 *
	 * @param f - {@link FileItem} object to write number of pages and size
	 * @param pdf - input PDF document
	 * @param logs - logs of the conversion
	 * @return - result of conversion
	 * @throws IOException in case IO exception occurred
	 */
	public ProcessResultList convertDocument(FileItem f, File pdf, ProcessResultList logs) throws IOException {
		log.debug("convertDocument");
		String[] argv = new String[] {
			getPathToConvert()
			, "-density", getDpi()
			, pdf.getCanonicalPath()
			, "-quality", getQuality()
			, new File(pdf.getParentFile(), PAGE_TMPLT).getCanonicalPath()
			};
		ProcessResult res = ProcessHelper.executeScript("convert PDF to images", argv);
		logs.add(res);
		if (res.isOk()) {
			File[] pages = pdf.getParentFile().listFiles(fi -> fi.isFile() && fi.getName().startsWith(DOC_PAGE_PREFIX) && fi.getName().endsWith(EXTENSION_PNG));
			if (pages == null || pages.length == 0) {
				f.setCount(0);
			} else {
				f.setCount(pages.length);
				logs.add(initSize(f, pages[0], PNG_MIME_TYPE));
			}
		}
		return logs;
	}
}
