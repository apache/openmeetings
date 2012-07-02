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
package org.openmeetings.app.documents;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.transaction.util.FileHelper;
import org.openmeetings.app.OpenmeetingsVariables;
import org.openmeetings.app.data.basic.Configurationmanagement;
import org.openmeetings.utils.OmFileHelper;
import org.openmeetings.utils.ProcessHelper;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class GeneratePDF {

	private static final Logger log = Red5LoggerFactory.getLogger(
			GeneratePDF.class, OpenmeetingsVariables.webAppRootKey);

	@Autowired
	private GenerateThumbs generateThumbs;
	@Autowired
	private GenerateSWF generateSWF;
	@Autowired
	private Configurationmanagement cfgManagement;

	public HashMap<String, HashMap<String, String>> convertPDF(String fileName,
			String roomName, boolean fullProcessing, File inFile)
			throws Exception {

		String inFileName = inFile.getName();
		HashMap<String, HashMap<String, String>> returnError = new HashMap<String, HashMap<String, String>>();

		File fileFullPath = new File(OmFileHelper.getUploadTempRoomDir(roomName), inFileName);
		File destinationFolder = OmFileHelper.getNewDir(OmFileHelper.getUploadRoomDir(roomName), fileName);

		log.debug("fullProcessing: " + fullProcessing);
		if (fullProcessing) {
			HashMap<String, String> processOpenOffice = doJodConvert(
					fileFullPath, destinationFolder, fileName);
			returnError.put("processOpenOffice", processOpenOffice);
			HashMap<String, String> processThumb = generateThumbs
					.generateBatchThumb(new File(destinationFolder, fileName + ".pdf"), destinationFolder, 80, "thumb");
			returnError.put("processThumb", processThumb);
			HashMap<String, String> processSWF = generateSWF
					.generateSwf(destinationFolder, destinationFolder, fileName);
			returnError.put("processSWF", processSWF);
		} else {

			log.debug("-- generateBatchThumb --");

			HashMap<String, String> processThumb = generateThumbs
					.generateBatchThumb(fileFullPath, destinationFolder, 80, "thumb");
			returnError.put("processThumb", processThumb);

			HashMap<String, String> processSWF = generateSWF.generateSwf(
					fileFullPath.getParentFile(), destinationFolder, fileName);
			returnError.put("processSWF", processSWF);
		}

		// now it should be completed so copy that file to the expected location
		File fileWhereToMove = new File(destinationFolder, inFileName);
		fileWhereToMove.createNewFile();
		FileHelper.moveRec(inFile, fileWhereToMove);

		if (fullProcessing) {
			HashMap<String, String> processXML = CreateLibraryPresentation
					.generateXMLDocument(destinationFolder,
							inFileName, fileName + ".pdf",
							fileName + ".swf");
			returnError.put("processXML", processXML);
		} else {
			HashMap<String, String> processXML = CreateLibraryPresentation
					.generateXMLDocument(destinationFolder,
							inFileName, null, fileName + ".swf");
			returnError.put("processXML", processXML);
		}

		return returnError;
	}

	/**
	 * Generates PDF using JOD Library (external library)
	 */
	public HashMap<String, String> doJodConvert(File fileFullPath, File destinationFolder, String outputfile) {
		try {

			String jodPath = cfgManagement.getConfValue("jod.path", String.class, "./jod");
			String officePath = cfgManagement.getConfValue("office.path", String.class, "");

			File jodFolder = new File(jodPath);
			if (!jodFolder.exists() || !jodFolder.isDirectory()) {
				throw new Exception("Path to JOD Library folder does not exist");
			}

			ArrayList<String> argv = new ArrayList<String>();
			argv.add("java");

			if (officePath.trim().length() > 0) {
				argv.add("-Doffice.home=" + officePath);
			}
			String jodConverterJar = "";

			for (String jar : jodFolder.list(new FilenameFilter() {
				public boolean accept(File file1, String name) {
					return name.endsWith(".jar");
				}
			})) {
				argv.add("-cp");
				if (jar.startsWith("jodconverter")) {
					jodConverterJar = jar;
				}
				argv.add(new File(jodFolder, jar).getCanonicalPath());
			}
			if (jodConverterJar.length() == 0) {
				throw new Exception(
						"Could not find jodConverter JAR file in JOD folder");
			}

			argv.add("-jar");
			argv.add(new File(jodFolder, jodConverterJar).getCanonicalPath());
			argv.add(fileFullPath.getCanonicalPath());
			argv.add(new File(destinationFolder, outputfile + ".pdf").getCanonicalPath());

			return ProcessHelper.executeScript("doJodConvert",
					argv.toArray(new String[argv.size()]));

		} catch (Exception ex) {
			log.error("doJodConvert", ex);
			return buildErrorMessage("doJodConvert", ex.getMessage(), ex);
		}
	}

	private HashMap<String, String> buildErrorMessage(String process,
			String error, Exception ex) {
		HashMap<String, String> returnMap = new HashMap<String, String>();
		returnMap.put("process", process);
		returnMap.put("exception", ex.toString());
		returnMap.put("error", error);
		returnMap.put("exitValue", "-1");
		return returnMap;
	}

}
