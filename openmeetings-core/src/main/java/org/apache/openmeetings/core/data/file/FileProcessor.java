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

import static org.apache.openmeetings.util.OmFileHelper.FLV_EXTENSION;
import static org.apache.openmeetings.util.OmFileHelper.getUploadFilesDir;
import static org.apache.openmeetings.util.OmFileHelper.getUploadTempFilesDir;
import static org.apache.openmeetings.util.OmFileHelper.thumbImagePrefix;
import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

import java.io.File;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import org.apache.commons.transaction.util.FileHelper;
import org.apache.openmeetings.core.converter.FlvExplorerConverter;
import org.apache.openmeetings.core.converter.GenerateImage;
import org.apache.openmeetings.core.converter.GenerateThumbs;
import org.apache.openmeetings.core.documents.GeneratePDF;
import org.apache.openmeetings.db.dao.file.FileExplorerItemDao;
import org.apache.openmeetings.db.entity.file.FileExplorerItem;
import org.apache.openmeetings.db.entity.file.FileItem.Type;
import org.apache.openmeetings.util.StoredFile;
import org.apache.openmeetings.util.crypt.MD5;
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
		
		int dotidx = f.getName().lastIndexOf('.');

		// Generate a random string to prevent any problems with
		// foreign characters and duplicates
		String newName = MD5.do_checksum("FILE_" + new Date().getTime());

		String extDot = f.getName().substring(dotidx, f.getName().length()).toLowerCase();
		String ext = extDot.substring(1);
		log.debug("file extension: " + ext);
		StoredFile storedFile = new StoredFile(newName, ext); 

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

		File completeName = new File(isAsIs ? getUploadFilesDir() : getUploadTempFilesDir(), newName + extDot);
		log.debug("writing file to: " + completeName);
		FileHelper.copy(is, completeName);
		is.close();

		String hash = newName + extDot;
		if (isImage) {
			hash = newName + ".jpg";
			f.setType(Type.Image);
		} else if (isVideo) {
			hash = newName + FLV_EXTENSION;
			f.setType(Type.Video);
		} else if (isChart) {
			f.setType(Type.PollChart);
		} else if (isPdf || canBeConverted) {
			hash = newName;
			f.setType(Type.Presentation);
		}
		f.setHash(hash);

		f = fileDao.update(f);
		log.debug("fileId: " + f.getId());
		
		log.debug("canBeConverted: " + canBeConverted);
		if (canBeConverted) {
			// convert to pdf, thumbs, swf and xml-description
			returnError = generatePDF.convertPDF(newName, "files", true, completeName);
		} else if (isPdf) {
			// convert to thumbs, swf and xml-description
			returnError = generatePDF.convertPDF(newName, "files", false, completeName);
		} else if (isChart) {
			log.debug("uploaded chart file");
		} else if (isImage && !isAsIs) {
			// convert it to JPG
			log.debug("##### convert it to JPG: ");
			returnError = generateImage.convertImage(newName, extDot, "files", newName, false);
		} else if (isAsIs) {
			ConverterProcessResult processThumb = generateThumbs.generateThumb(thumbImagePrefix, completeName, 50);
			returnError.addItem("processThumb", processThumb);
		} else if (isVideo) {
			List<ConverterProcessResult> returnList = flvExplorerConverter.startConversion(f.getId(), completeName.getCanonicalPath());
			
			int i = 0;
			for (ConverterProcessResult returnMap : returnList) {
				returnError.addItem("processFLV " + i, returnMap);
			}
		}
		
		// has to happen at the end, otherwise it will be overwritten
		//cause the variable is new initialized
		returnError.setCompleteName(completeName.getName());
		returnError.setFileExplorerItemId(f.getId());
		
		return returnError;
	}
}
