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
package org.apache.openmeetings.documents.beans;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.axis.services.FileImportError;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

/**
 * 
 * Collects all results of conversion jobs
 * 
 * @author sebawagner
 * 
 */
public class ConverterProcessResultList {

	private static final Logger log = Red5LoggerFactory.getLogger(
			ConverterProcessResultList.class,
			OpenmeetingsVariables.webAppRootKey);

	private Map<String, ConverterProcessResult> jobslist = new HashMap<String, ConverterProcessResult>();

	private Long fileExplorerItemId;
	private String completeName;

	public Long getFileExplorerItemId() {
		return fileExplorerItemId;
	}

	public void setFileExplorerItemId(Long fileExplorerItemId) {
		this.fileExplorerItemId = fileExplorerItemId;
	}

	public String getCompleteName() {
		return completeName;
	}

	public void setCompleteName(String completeName) {
		this.completeName = completeName;
	}

	public ConverterProcessResult addItem(String name,
			ConverterProcessResult processResult) {
		if (jobslist.containsKey(name)) {
			log.error("Duplicate key in jobslist");
			return null;
		}
		return jobslist.put(name, processResult);
	}

	/**
	 * returns true if there was an job with exitValue "-1"
	 * 
	 * @return
	 */
	public boolean hasError() {
		for (Entry<String, ConverterProcessResult> entry : jobslist.entrySet()) {
			if (entry.getValue().getExitValue().equals("-1")) {
				return true;
			}
		}
		return false;
	}

	/**
	 * converts all job results into a human readable format
	 * 
	 * @return
	 */
	public String getLogMessage() {
		StringBuilder logMessage = new StringBuilder();
		for (Entry<String, ConverterProcessResult> entry : jobslist.entrySet()) {
			logMessage.append("key: ");
			logMessage.append(entry.getKey());
			logMessage.append("\r\n");
			logMessage.append(entry.getValue().buildLogMessage());
		}
		return logMessage.toString();
	}

	/**
	 * Axis need Objects or array of objects, Map won't work
	 * 
	 * @return
	 */
	public FileImportError[] convertToFileImportErrors() {
		FileImportError[] fileImportErrors = new FileImportError[jobslist
				.size()];

		int i = 0;
		// Axis need Objects or array of objects, Map won't work
		for (Iterator<String> iter = jobslist.keySet().iterator(); iter
				.hasNext();) {

			ConverterProcessResult returnAttribute = jobslist.get(iter
					.next());

			fileImportErrors[i] = new FileImportError();
			fileImportErrors[i]
					.setCommand((returnAttribute.getCommand() != null) ? returnAttribute
							.getCommand().toString() : "");
			fileImportErrors[i]
					.setError((returnAttribute.getError() != null) ? returnAttribute
							.getError().toString() : "");
			fileImportErrors[i]
					.setExitValue((returnAttribute.getExitValue() != null) ? Integer
							.valueOf(returnAttribute.getExitValue().toString())
							.intValue() : 0);
			fileImportErrors[i]
					.setProcess((returnAttribute.getProcess() != null) ? returnAttribute
							.getProcess().toString() : "");

			i++;
		}
		
		return fileImportErrors;
	}

	public int size() {
		return jobslist.size();
	}

}
