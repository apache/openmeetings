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

import static org.apache.openmeetings.util.OmFileHelper.getFileExt;
import static org.apache.openmeetings.util.OmFileHelper.thumbImagePrefix;
import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import org.apache.commons.transaction.util.FileHelper;
import org.apache.openmeetings.core.converter.FlvExplorerConverter;
import org.apache.openmeetings.core.converter.GenerateImage;
import org.apache.openmeetings.core.converter.GenerateThumbs;
import org.apache.openmeetings.core.documents.GeneratePDF;
import org.apache.openmeetings.db.dao.file.FileExplorerItemDao;
import org.apache.openmeetings.db.entity.file.FileExplorerItem;
import org.apache.openmeetings.db.entity.file.FileItem.Type;
import org.apache.openmeetings.util.StoredFile;
import org.apache.openmeetings.util.process.ConverterProcessResult;
import org.apache.openmeetings.util.process.ConverterProcessResultList;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class FileProcessor {
	private static final Logger log = Red5LoggerFactory.getLogger(FileProcessor.class, webAppRootKey);

	//Spring loaded Beans
	@Autowired
	private FlvExplorerConverter flvExplorerConverter;
	@Autowired
	private FileExplorerItemDao fileDao;
	@Autowired
	private GenerateImage generateImage;
	@Autowired
	private GenerateThumbs generateThumbs;
	@Autowired
	private GeneratePDF generatePDF;

	//FIXME TODO this method need to be refactored to throw exceptions
	public ConverterProcessResultList processFile(Long userId, FileExplorerItem f, InputStream is) throws Exception {
		ConverterProcessResultList returnError = new ConverterProcessResultList();
		
		// Generate a random string to prevent any problems with
		// foreign characters and duplicates
		String hash = UUID.randomUUID().toString();

		String ext = getFileExt(f.getName());
		log.debug("file extension: " + ext);
		StoredFile storedFile = new StoredFile(hash, ext); 

		// Check variable to see if this file is a presentation
		// check if this is a a file that can be converted by
		// openoffice-service
		boolean canBeConverted = storedFile.isConvertable();
		boolean isPdf = storedFile.isPdf();
		boolean isImage = storedFile.isImage();
		boolean isChart = storedFile.isChart();
		boolean isAsIs = storedFile.isAsIs();
		boolean isVideo = storedFile.isVideo();

		log.debug("isAsIs: " + isAsIs);

		// add outputfolders for profiles
		// if it is a presenation it will be copied to another place
		if (!(canBeConverted || isPdf || isImage || isVideo || isAsIs)) {
			returnError.addItem("wrongType", new ConverterProcessResult("The file type cannot be converted"));
			return returnError;
		}
		if (isImage) {
			f.setType(Type.Image);
		} else if (isVideo) {
			f.setType(Type.Video);
		} else if (isChart) {
			f.setType(Type.PollChart);
		} else if (isPdf || canBeConverted) {
			f.setType(Type.Presentation);
		}
		f.setHash(hash);

		f = fileDao.update(f);
		log.debug("fileId: " + f.getId());

		File file = f.getFile(ext);
		log.debug("writing file to: " + file);
		if (!file.getParentFile().exists() && !file.getParentFile().mkdirs()) {
			returnError.addItem("No parent", new ConverterProcessResult("Unable to create parent for file: " + file.getCanonicalPath()));
			return returnError;
		}
		FileHelper.copy(is, file);
		is.close();

		
		log.debug("canBeConverted: " + canBeConverted);
		if (canBeConverted || isPdf) {
			// convert to pdf, thumbs, swf and xml-description
			returnError = generatePDF.convertPDF(f, ext);
		} else if (isChart) {
			log.debug("uploaded chart file");
		} else if (isImage && !isAsIs) {
			// convert it to JPG
			log.debug("##### convert it to JPG: ");
			returnError = generateImage.convertImage(f, ext);
		} else if (isAsIs) {
			ConverterProcessResult processThumb = generateThumbs.generateThumb(thumbImagePrefix, file, 50);
			returnError.addItem("processThumb", processThumb);
		} else if (isVideo) {
			List<ConverterProcessResult> returnList = flvExplorerConverter.convertToMP4(f, ext);
			
			int i = 0;
			for (ConverterProcessResult returnMap : returnList) {
				returnError.addItem("processFLV " + i, returnMap);
			}
		}
		
		// has to happen at the end, otherwise it will be overwritten
		//cause the variable is new initialized
		returnError.setCompleteName(file.getName());
		returnError.setFileItemId(f.getId());
		
		return returnError;
	}
}
