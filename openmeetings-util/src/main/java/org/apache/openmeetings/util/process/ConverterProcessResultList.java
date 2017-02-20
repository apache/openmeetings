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
package org.apache.openmeetings.util.process;

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;
import static org.red5.logging.Red5LoggerFactory.getLogger;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;

/**
 *
 * Collects all results of conversion jobs
 *
 * @author sebawagner
 *
 */
public class ConverterProcessResultList {
	private static final Logger log = getLogger(ConverterProcessResultList.class, webAppRootKey);

	private Map<String, ConverterProcessResult> jobs = new LinkedHashMap<>();

	private Long fileItemId;
	private String completeName;

	public Long getFileItemId() {
		return fileItemId;
	}

	public void setFileItemId(Long fileItemId) {
		this.fileItemId = fileItemId;
	}

	public String getCompleteName() {
		return completeName;
	}

	public void setCompleteName(String completeName) {
		this.completeName = completeName;
	}

	public ConverterProcessResult addItem(String name, ConverterProcessResult processResult) {
		if (jobs.containsKey(name)) {
			log.error("Duplicate key in jobslist:: " + name);
			return null;
		}
		return jobs.put(name, processResult);
	}

	/**
	 * returns true if there was an job with exitValue "-1"
	 *
	 * @return
	 */
	public boolean hasError() {
		for (Entry<String, ConverterProcessResult> entry : jobs.entrySet()) {
			if (!entry.getValue().isOk()) {
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
		for (Entry<String, ConverterProcessResult> entry : jobs.entrySet()) {
			logMessage.append("key: ");
			logMessage.append(entry.getKey());
			logMessage.append("\r\n");
			logMessage.append(entry.getValue().buildLogMessage());
		}
		return logMessage.toString();
	}

	public int size() {
		return jobs.size();
	}

	public Map<String, ConverterProcessResult> getJobs() {
		return jobs;
	}
}
