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
package org.apache.openmeetings.core.data.file;

import static java.util.UUID.randomUUID;
import static org.apache.commons.io.FileUtils.copyFile;
import static org.apache.commons.io.FileUtils.copyInputStreamToFile;
import static org.apache.openmeetings.util.OmFileHelper.getFileExt;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Optional;
import java.util.function.DoubleConsumer;

import org.apache.openmeetings.core.converter.DocumentConverter;
import org.apache.openmeetings.core.converter.ImageConverter;
import org.apache.openmeetings.core.converter.VideoConverter;
import org.apache.openmeetings.db.dao.file.FileItemDao;
import org.apache.openmeetings.db.entity.file.BaseFileItem.Type;
import org.apache.openmeetings.db.entity.file.FileItem;
import org.apache.openmeetings.util.StoredFile;
import org.apache.openmeetings.util.process.ProcessResult;
import org.apache.openmeetings.util.process.ProcessResultList;
import org.apache.tika.exception.UnsupportedFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

import jakarta.inject.Inject;

@Component
public class FileProcessor {
	private static final Logger log = LoggerFactory.getLogger(FileProcessor.class);

	//Spring loaded Beans
	@Inject
	private VideoConverter videoConverter;
	@Inject
	private FileItemDao fileDao;
	@Inject
	private ImageConverter imageConverter;
	@Inject
	private DocumentConverter docConverter;

	public ProcessResultList processFile(FileItem f, InputStream is, Optional<DoubleConsumer> progress) throws Exception {
		ProcessResultList logs = new ProcessResultList();
		// Generate a random string to prevent any problems with
		// foreign characters and duplicates
		String hash = randomUUID().toString();

		File temp = null;
		try {
			temp = File.createTempFile("upload_" + hash, ".tmp");
			copyInputStreamToFile(is, temp);

			String ext = getFileExt(f.getName());
			log.debug("file extension: {}", ext);
			//this method moves stream, so temp file MUST be created first
			StoredFile sf = new StoredFile(hash, ext, temp);

			log.debug("isAsIs: {}", sf.isAsIs());

			if (sf.isImage()) {
				f.setType(Type.IMAGE);
			} else if (sf.isVideo()) {
				f.setType(Type.VIDEO);
			} else if (sf.isChart()) {
				f.setType(Type.POLL_CHART);
			} else if (sf.isPdf() || sf.isOffice()) {
				f.setType(Type.PRESENTATION);
			} else {
				throw new UnsupportedFormatException("The file type cannot be converted :: " + f.getName());
			}
			f.setHash(hash);

			processFile(f, sf, temp, logs, progress);
		} catch (Exception e) {
			log.debug("Error while processing the file", e);
			throw e;
		} finally {
			if (temp != null && temp.exists() && temp.isFile()) {
				log.debug("Clean up was successful ? {}", Files.deleteIfExists(temp.toPath()));
			}
		}
		return logs;
	}

	private void processFile(FileItem f, StoredFile sf, File temp, ProcessResultList logs, Optional<DoubleConsumer> progress) throws Exception {
		try {
			File file = f.getFile(sf.getExt());
			log.debug("writing file to: {}", file);
			if (!file.getParentFile().exists() && !file.getParentFile().mkdirs()) {
				logs.add(new ProcessResult("Unable to create parent for file: " + file.getCanonicalPath()));
				return;
			}
			switch(f.getType()) {
				case PRESENTATION:
					log.debug("Office document: {}", file);
					copyFile(temp, file);
					// convert to pdf, thumbs, swf and xml-description
					docConverter.convertPDF(f, sf, logs, progress);
					break;
				case POLL_CHART:
					log.debug("uploaded chart file"); // NOT implemented yet
					break;
				case IMAGE:
					// convert it to PNG
					log.debug("##### convert it to PNG: ");
					copyFile(temp, file);
					imageConverter.convertImage(f, sf, progress);
					break;
				case VIDEO:
					copyFile(temp, file);
					videoConverter.convertVideo(f, sf, logs, progress);
					break;
				default:
					break;
			}
		} finally {
			f = fileDao.update(f);
			log.debug("fileId: {}", f.getId());
		}
	}
}
