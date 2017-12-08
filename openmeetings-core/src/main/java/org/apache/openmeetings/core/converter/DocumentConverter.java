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
import static org.apache.openmeetings.util.OmFileHelper.EXTENSION_PDF;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_PATH_OFFICE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getWebAppRootKey;

import java.io.File;
import java.util.function.Consumer;

import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.entity.file.FileItem;
import org.apache.openmeetings.util.StoredFile;
import org.apache.openmeetings.util.process.ProcessResult;
import org.apache.openmeetings.util.process.ProcessResultList;
import org.apache.wicket.util.string.Strings;
import org.artofsolving.jodconverter.OfficeDocumentConverter;
import org.artofsolving.jodconverter.office.DefaultOfficeManagerConfiguration;
import org.artofsolving.jodconverter.office.OfficeManager;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DocumentConverter {
	private static final Logger log = Red5LoggerFactory.getLogger(DocumentConverter.class, getWebAppRootKey());

	@Autowired
	protected ConfigurationDao cfgDao;
	@Autowired
	private ImageConverter imageConverter;

	public ProcessResultList convertPDF(FileItem f, StoredFile sf) throws Exception {
		return convertPDF(f, sf, new ProcessResultList());
	}

	public ProcessResultList convertPDF(FileItem f, StoredFile sf, ProcessResultList logs) throws Exception {
		boolean fullProcessing = !sf.isPdf();
		File original = f.getFile(sf.getExt());
		File pdf = f.getFile(EXTENSION_PDF);
		log.debug("fullProcessing: " + fullProcessing);
		if (fullProcessing) {
			log.debug("-- running JOD --");
			logs.add(doJodConvert(original, pdf));
		} else if (!EXTENSION_PDF.equals(sf.getExt())) {
			copyFile(original, pdf);
		}

		log.debug("-- generate page images --");
		return imageConverter.convertDocument(f, pdf, logs);
	}

	public static void createOfficeManager(String officePath, Consumer<OfficeManager> consumer) {
		OfficeManager manager = null;
		try {
			DefaultOfficeManagerConfiguration configuration = new DefaultOfficeManagerConfiguration();
			if (!Strings.isEmpty(officePath)) {
				configuration.setOfficeHome(officePath);
			}
			manager = configuration.buildOfficeManager();
			manager.start();
			if (consumer != null) {
				consumer.accept(manager);
			}
		} finally {
			if (manager != null) {
				manager.stop();
			}
		}
	}

	/**
	 * Generates PDF using JOD Library (external library)
	 *
	 * @param in - file to convert
	 * @param out - file to write result
	 * @return - result of the conversion as {@link ProcessResult}
	 */
	public ProcessResult doJodConvert(File in, File out) {
		try {
			createOfficeManager(cfgDao.getString(CONFIG_PATH_OFFICE, null)
					, man -> {
						OfficeDocumentConverter converter = new OfficeDocumentConverter(man);
						converter.convert(in, out);
					});
		} catch (Exception ex) {
			log.error("doJodConvert", ex);
			return new ProcessResult("doJodConvert", ex.getMessage(), ex);
		}
		return new ProcessResult("doJodConvert", "Document converted successfully", null)
				.setExitCode(0);
	}
}
