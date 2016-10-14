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

import static org.apache.openmeetings.core.documents.CreateLibraryPresentation.generateXMLDocument;
import static org.apache.openmeetings.util.OmFileHelper.EXTENSION_PDF;
import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

import java.io.File;

import org.apache.openmeetings.core.converter.GenerateSWF;
import org.apache.openmeetings.core.converter.GenerateThumbs;
import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.entity.file.FileExplorerItem;
import org.apache.openmeetings.util.process.ConverterProcessResult;
import org.apache.openmeetings.util.process.ConverterProcessResultList;
import org.apache.wicket.util.string.Strings;
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

	public ConverterProcessResultList convertPDF(FileExplorerItem f, String ext) throws Exception {
		ConverterProcessResultList errors = new ConverterProcessResultList();

		boolean fullProcessing = !EXTENSION_PDF.equals(ext);
		File original = f.getFile(ext);
		File pdf = f.getFile(EXTENSION_PDF);
		log.debug("fullProcessing: " + fullProcessing);
		if (fullProcessing) {
			log.debug("-- running JOD --");
			errors.addItem("processOpenOffice", doJodConvert(original, pdf));
		}
		
		log.debug("-- generateBatchThumb --");
		errors.addItem("processThumb", generateThumbs.generateBatchThumb(pdf, pdf.getParentFile(), 80, "thumb"));
		File swf = f.getFile();
		errors.addItem("processSWF", generateSWF.generateSwf(pdf, swf));

		errors.addItem("processXML", generateXMLDocument(original, fullProcessing ? pdf : null, swf));
		return errors;
	}

	/**
	 * Generates PDF using JOD Library (external library)
	 */
	public ConverterProcessResult doJodConvert(File in, File out) {
		try {
			String officePath = configurationDao.getConfValue("office.path", String.class, null);
			DefaultOfficeManagerConfiguration configuration = new DefaultOfficeManagerConfiguration();
			if (!Strings.isEmpty(officePath)) {
				configuration.setOfficeHome(officePath);
			}
			OfficeManager officeManager = configuration.buildOfficeManager();
			officeManager.start();
			OfficeDocumentConverter converter = new OfficeDocumentConverter(officeManager);
			try {
				converter.convert(in, out);
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
		result.setExitCode(0);
		return result;
	}
}
