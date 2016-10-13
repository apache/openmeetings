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
package org.apache.openmeetings.core.documents;

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

import java.io.File;

import org.apache.commons.transaction.util.FileHelper;
import org.apache.openmeetings.core.converter.GenerateSWF;
import org.apache.openmeetings.core.converter.GenerateThumbs;
import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.util.OmFileHelper;
import org.apache.openmeetings.util.process.ConverterProcessResult;
import org.apache.openmeetings.util.process.ConverterProcessResultList;
import org.artofsolving.jodconverter.OfficeDocumentConverter;
import org.artofsolving.jodconverter.office.DefaultOfficeManagerConfiguration;
import org.artofsolving.jodconverter.office.OfficeException;
import org.artofsolving.jodconverter.office.OfficeManager;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class GeneratePDF {
	private static final Logger log = Red5LoggerFactory.getLogger(GeneratePDF.class, webAppRootKey);

	@Autowired
	private GenerateThumbs generateThumbs;
	@Autowired
	private GenerateSWF generateSWF;
	@Autowired
	private ConfigurationDao configurationDao;

	public ConverterProcessResultList convertPDF(String fileName,
			String roomName, boolean fullProcessing, File inFile)
			throws Exception {

		String inFileName = inFile.getName();
		ConverterProcessResultList returnError = new ConverterProcessResultList();

		File fileFullPath = new File(OmFileHelper.getUploadTempRoomDir(roomName), inFileName);
		File destinationFolder = OmFileHelper.getNewDir(OmFileHelper.getUploadRoomDir(roomName), fileName);

		log.debug("fullProcessing: " + fullProcessing);
		if (fullProcessing) {
			ConverterProcessResult processOpenOffice = doJodConvert(
					fileFullPath, destinationFolder, fileName);
			returnError.addItem("processOpenOffice", processOpenOffice);
			ConverterProcessResult processThumb = generateThumbs
					.generateBatchThumb(new File(destinationFolder, fileName + ".pdf"), destinationFolder, 80, "thumb");
			returnError.addItem("processThumb", processThumb);
			ConverterProcessResult processSWF = generateSWF
					.generateSwf(destinationFolder, destinationFolder, fileName);
			returnError.addItem("processSWF", processSWF);
		} else {

			log.debug("-- generateBatchThumb --");

			ConverterProcessResult processThumb = generateThumbs
					.generateBatchThumb(fileFullPath, destinationFolder, 80, "thumb");
			returnError.addItem("processThumb", processThumb);

			ConverterProcessResult processSWF = generateSWF.generateSwf(
					fileFullPath.getParentFile(), destinationFolder, fileName);
			returnError.addItem("processSWF", processSWF);
		}

		// now it should be completed so copy that file to the expected location
		File fileWhereToMove = new File(destinationFolder, inFileName);
		fileWhereToMove.createNewFile();
		FileHelper.moveRec(inFile, fileWhereToMove);

		if (fullProcessing) {
			ConverterProcessResult processXML = CreateLibraryPresentation
					.generateXMLDocument(destinationFolder,
							inFileName, fileName + ".pdf",
							fileName + ".swf");
			returnError.addItem("processXML", processXML);
		} else {
			ConverterProcessResult processXML = CreateLibraryPresentation
					.generateXMLDocument(destinationFolder,
							inFileName, null, fileName + ".swf");
			returnError.addItem("processXML", processXML);
		}

		return returnError;
	}

	/**
	 * Generates PDF using JOD Library
	 */
	public ConverterProcessResult doJodConvert(File fileFullPath, File destinationFolder, String outputfile) {
		try {
			String officePath = configurationDao.getConfValue("office.path", String.class, "");
			DefaultOfficeManagerConfiguration configuration = new DefaultOfficeManagerConfiguration();
			configuration.setOfficeHome(officePath);
			OfficeManager officeManager = configuration.buildOfficeManager();
			officeManager.start();
			OfficeDocumentConverter converter = new OfficeDocumentConverter(officeManager);
			try {
				converter.convert(fileFullPath, new File(destinationFolder, outputfile + ".pdf"));
			} catch (OfficeException ex) {
				log.error("doJodConvert", ex);
				return new ConverterProcessResult("doJodConvert", ex.getMessage(), ex);
			} finally {
					officeManager.stop();
			}
		} catch (Exception ex) {
			log.error("doJodConvert", ex);
			return new ConverterProcessResult("doJodConvert", ex.getMessage(), ex);
		}
		ConverterProcessResult result = new ConverterProcessResult("doJodConvert", "Document converted successfully", null);
		result.setExitValue("0");
		return result;
	}
}
