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
import static org.apache.openmeetings.core.converter.BaseConverter.HALF_STEP;
import static org.apache.openmeetings.util.OmFileHelper.EXTENSION_PDF;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_PATH_OFFICE;

import java.io.File;
import java.util.Optional;
import java.util.function.DoubleConsumer;
import java.util.function.Function;

import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.entity.file.FileItem;
import org.apache.openmeetings.util.StoredFile;
import org.apache.openmeetings.util.process.ProcessResult;
import org.apache.openmeetings.util.process.ProcessResultList;
import org.apache.wicket.util.string.Strings;
import org.jodconverter.core.job.ConversionJob;
import org.jodconverter.core.office.OfficeException;
import org.jodconverter.core.office.OfficeManager;
import org.jodconverter.local.LocalConverter;
import org.jodconverter.local.office.LocalOfficeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

import jakarta.inject.Inject;

@Component
public class DocumentConverter {
	private static final Logger log = LoggerFactory.getLogger(DocumentConverter.class);
	private static final String JOD_JOD_NAME = "doJodConvert";

	@Inject
	protected ConfigurationDao cfgDao;
	@Inject
	private ImageConverter imageConverter;

	public ProcessResultList convertPDF(FileItem f, StoredFile sf) throws Exception {
		return convertPDF(f, sf, new ProcessResultList(), Optional.empty());
	}

	public ProcessResultList convertPDF(FileItem f, StoredFile sf, ProcessResultList logs, Optional<DoubleConsumer> progress) throws Exception {
		boolean fullProcessing = !sf.isPdf();
		File original = f.getFile(sf.getExt());
		File pdf = f.getFile(EXTENSION_PDF);
		log.debug("fullProcessing: {}", fullProcessing);
		if (fullProcessing) {
			log.debug("-- running JOD --");
			logs.add(doJodConvert(original, pdf));
		} else if (!EXTENSION_PDF.equals(sf.getExt())) {
			copyFile(original, pdf);
		}
		progress.ifPresent(theProgress -> theProgress.accept(HALF_STEP));

		log.debug("-- generate page images --");
		return imageConverter.convertDocument(f, pdf, logs, progress);
	}

	public static void createOfficeManager(String officePath, Function<OfficeManager, ConversionJob> job) throws OfficeException {
		OfficeManager manager = null;
		try {
			LocalOfficeManager.Builder builder = LocalOfficeManager.builder();
			if (!Strings.isEmpty(officePath)) {
				builder.officeHome(officePath);
			}
			manager = builder.build();
			manager.start();
			if (job != null) {
				job.apply(manager).execute();
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
					, man -> LocalConverter.make(man).convert(in).to(out));
		} catch (Exception ex) {
			log.error(JOD_JOD_NAME, ex);
			return new ProcessResult(JOD_JOD_NAME, ex.getMessage(), ex);
		}
		return new ProcessResult(JOD_JOD_NAME, "Document converted successfully", null)
				.setExitCode(0);
	}
}
