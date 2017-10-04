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
package org.apache.openmeetings.core.data.record.converter;

import static org.apache.openmeetings.util.OpenmeetingsVariables.getWebAppRootKey;

import org.apache.openmeetings.core.converter.RecordingConverter;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

@Component
public class RecordingConverterTask {
	private static final Logger log = Red5LoggerFactory.getLogger(RecordingConverterTask.class, getWebAppRootKey());

	@Autowired
	private TaskExecutor taskExecutor;
	@Autowired
	private RecordingConverter recordingConverter;

	public void startConversionThread(final Long recordingId) {
		try {
			log.debug("[-1-]" + taskExecutor);

			taskExecutor.execute(() -> recordingConverter.startConversion(recordingId));
		} catch (Exception err) {
			log.error("[startConversionThread]", err);
		}
	}
}
