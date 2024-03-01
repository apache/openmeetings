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
import static org.apache.openmeetings.util.process.ProcessResult.ZERO;
import static org.apache.tika.metadata.HttpHeaders.CONTENT_TYPE;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.function.DoubleConsumer;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;
import org.xml.sax.helpers.DefaultHandler;

import jakarta.inject.Inject;

@Component
public class ImageConverter extends BaseConverter {
	private static final Logger log = LoggerFactory.getLogger(ImageConverter.class);
	private static final String PAGE_TMPLT = DOC_PAGE_PREFIX + "-%04d." + EXTENSION_PNG;

	@Inject
	private UserDao userDao;

	public ProcessResultList convertImage(BaseFileItem f, StoredFile sf, Optional<DoubleConsumer> progress) throws IOException {
		return convertImage(f, sf, new ProcessResultList(), progress);
	}

	public ProcessResultList convertImage(BaseFileItem f, StoredFile sf, ProcessResultList logs, Optional<DoubleConsumer> progress) throws IOException {
		File png = f.getFile(EXTENSION_PNG);
		if (!sf.isPng()) {
			File img = f.getFile(sf.getExt());

			log.debug("##### convertImage destinationFile: {}", png);
			logs.add(convertSinglePng(img, png));
		} else if (!png.exists()){
			copyFile(f.getFile(sf.getExt()), png);
		}
		progress.ifPresent(theProgress -> theProgress.accept(HALF_STEP));
		logs.add(initSize(f, png, PNG_MIME_TYPE));
		progress.ifPresent(theProgress -> theProgress.accept(HALF_STEP));
		return logs;
	}

	public ProcessResultList convertImageUserProfile(File file, Long userId) throws Exception {
		ProcessResultList returnMap = new ProcessResultList();

		// User Profile Update
		try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(
				getUploadProfilesUserDir(userId).toPath()
				, fi -> fi.toString().endsWith(EXTENSION_PNG)))
		{
			dirStream.forEach(path -> FileUtils.deleteQuietly(path.toFile()));
		}

		File destinationFile = OmFileHelper.getNewFile(getUploadProfilesUserDir(userId), PROFILE_FILE_NAME, EXTENSION_PNG);
		returnMap.add(resize(file, destinationFile, 250, 250, true));

		// Delete old one
		Files.deleteIfExists(file.toPath());

		String img = destinationFile.getName();
		User us = userDao.get(userId);
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
	 * @throws IOException - if any Io exception occurs while processing
	 *
	 */
	private ProcessResult convertSinglePng(File in, File out) throws IOException {
		List<String> argv = List.of(getPathToConvert(), in.getCanonicalPath(), out.getCanonicalPath());

		return ProcessHelper.exec("convertSinglePng", argv);
	}

	public ProcessResult resize(File in, File out, Integer width, Integer height, boolean max) throws IOException {
		List<String> argv = List.of(getPathToConvert()
				, "-resize", (width == null ? "" : width) + (height == null ? "" : "x" + height) + (max ? ">" : "")
				, in.getCanonicalPath(), out.getCanonicalPath());
		return ProcessHelper.exec("resize", argv);
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
	public ProcessResultList convertDocument(FileItem f, File pdf, ProcessResultList logs, Optional<DoubleConsumer> progress) throws IOException {
		log.debug("convertDocument");
		List<String> argv = List.of(
				getPathToConvert()
				, "-density", getDpi()
				, "-define", "pdf:use-cropbox=true"
				, pdf.getCanonicalPath()
				, "+profile", "'*'"
				, "-quality", getQuality()
				, new File(pdf.getParentFile(), PAGE_TMPLT).getCanonicalPath());
		ProcessResult res = ProcessHelper.exec("convert PDF to images", argv);
		logs.add(res);
		progress.ifPresent(theProgress -> theProgress.accept(1. / 4));
		if (res.isOk()) {
			File[] pages = pdf.getParentFile().listFiles(fi -> fi.isFile() && fi.getName().startsWith(DOC_PAGE_PREFIX) && fi.getName().endsWith(EXTENSION_PNG));
			if (pages == null || pages.length == 0) {
				f.setCount(0);
			} else {
				f.setCount(pages.length);
				logs.add(initSize(f, pages[0], PNG_MIME_TYPE));
			}
		}
		progress.ifPresent(theProgress -> theProgress.accept(1. / 4));
		return logs;
	}
}
