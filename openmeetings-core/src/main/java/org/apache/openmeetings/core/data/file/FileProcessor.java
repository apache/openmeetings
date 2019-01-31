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

import static org.apache.commons.io.FileUtils.copyFile;
import static org.apache.commons.io.FileUtils.copyInputStreamToFile;
import static org.apache.openmeetings.util.OmFileHelper.getFileExt;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getWebAppRootKey;

import java.io.File;
import java.io.InputStream;
import java.util.UUID;

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
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FileProcessor {
	private static final Logger log = Red5LoggerFactory.getLogger(FileProcessor.class, getWebAppRootKey());

	//Spring loaded Beans
	@Autowired
	private VideoConverter videoConverter;
	@Autowired
	private FileItemDao fileDao;
	@Autowired
	private ImageConverter imageConverter;
	@Autowired
	private DocumentConverter docConverter;

	public ProcessResultList processFile(FileItem f, InputStream is) throws Exception {
		ProcessResultList logs = new ProcessResultList();
		// Generate a random string to prevent any problems with
		// foreign characters and duplicates
		String hash = UUID.randomUUID().toString();

		File temp = null;
		try {
			temp = File.createTempFile(String.format("upload_%s", hash), ".tmp");
			copyInputStreamToFile(is, temp);

			String ext = getFileExt(f.getName());
			log.debug("file extension: {}", ext);
			//this method moves stream, so temp file MUST be created first
			StoredFile sf = new StoredFile(hash, ext, temp);

			log.debug("isAsIs: {}", sf.isAsIs());

			if (sf.isImage()) {
				f.setType(Type.Image);
			} else if (sf.isVideo()) {
				f.setType(Type.Video);
			} else if (sf.isChart()) {
				f.setType(Type.PollChart);
			} else if (sf.isPdf() || sf.isOffice()) {
				f.setType(Type.Presentation);
			} else {
				throw new UnsupportedFormatException("The file type cannot be converted :: " + f.getName());
			}
			f.setHash(hash);

			processFile(f, sf, temp, logs);
		} catch (Exception e) {
			log.debug("Error while processing the file", e);
			throw e;
		} finally {
			if (temp != null && temp.exists() && temp.isFile()) {
				log.debug("Clean up was successful ? {}", temp.delete());
			}
		}
		return logs;
	}

	private void processFile(FileItem f, StoredFile sf, File temp, ProcessResultList logs) throws Exception {
		try {
			File file = f.getFile(sf.getExt());
			log.debug("writing file to: {}", file);
			if (!file.getParentFile().exists() && !file.getParentFile().mkdirs()) {
				logs.add(new ProcessResult("Unable to create parent for file: " + file.getCanonicalPath()));
				return;
			}
			switch(f.getType()) {
				case Presentation:
					log.debug("Office document: {}", file);
					copyFile(temp, file);
					// convert to pdf, thumbs, swf and xml-description
					docConverter.convertPDF(f, sf, logs);
					break;
				case PollChart:
					log.debug("uploaded chart file"); // NOT implemented yet
					break;
				case Image:
					// convert it to PNG
					log.debug("##### convert it to PNG: ");
					copyFile(temp, file);
					imageConverter.convertImage(f, sf);
					break;
				case Video:
					copyFile(temp, file);
					videoConverter.convertVideo(f, sf, logs);
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
