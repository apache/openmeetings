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
package org.openmeetings.app.data.flvrecord.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;

import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class FlvInterviewConverterTask {
	private static final Logger log = Red5LoggerFactory.getLogger(FlvInterviewConverterTask.class, ScopeApplicationAdapter.webAppRootKey);

	@Autowired
	@Qualifier("openmeetings.TaskExecutor")
	private TaskExecutor taskExecutor;
	@Autowired
	private FlvInterviewConverter flvInterviewConverter;

	public void startConversionThread(final Long flvRecordingId) {
		try {
			
			log.debug("[-1-]"+this.taskExecutor);
			
			this.taskExecutor.execute(new Runnable() {
				public void run() {
						flvInterviewConverter.startConversion( flvRecordingId );
	               }
	          });
			
		} catch (Exception err) {
			log.error("[startConversionThread]",err);
		}
	}

}
