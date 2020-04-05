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

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Collects all results of conversion jobs
 *
 * @author sebawagner
 *
 */
public class ProcessResultList {
	private List<ProcessResult> jobs = new ArrayList<>();

	public void add(ProcessResult res) {
		jobs.add(res);
	}

	public ProcessResult getLast() {
		return jobs.isEmpty() ? null : jobs.get(jobs.size() - 1);
	}

	/**
	 * checks if process was successful
	 *
	 * @return true if there was an job with exitValue "-1"
	 */
	public boolean hasError() {
		for (ProcessResult res : jobs) {
			if (!res.isOk()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * converts all job results into a human readable format
	 *
	 * @return - string representation of the job
	 */
	public String getLogMessage() {
		StringBuilder logMessage = new StringBuilder();
		for (ProcessResult res : jobs) {
			logMessage.append("key: ");
			logMessage.append(res.getProcess());
			logMessage.append("\r\n");
			logMessage.append(res.buildLogMessage());
		}
		return logMessage.toString();
	}

	public int size() {
		return jobs.size();
	}

	public List<ProcessResult> getJobs() {
		return jobs;
	}
}
