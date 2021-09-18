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
package org.apache.openmeetings.webservice.schema;

import java.util.List;

import org.apache.openmeetings.db.dto.record.RecordingDTO;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 *
 * @author Sebastian.wagner
 *
 * Provide the correct schema response including the wrapping root element + add example response.
 *
 * See https://issues.apache.org/jira/browse/OPENMEETINGS-2667
 *
 */
@Schema(example = "{\n"
		+ "    \"recordingDTO\": [\n"
		+ "        {\n"
		+ "            \"id\": 1,\n"
		+ "            \"name\": \"Recording [file.name.recording]19/09/21, 12:43 am\",\n"
		+ "            \"hash\": \"1ee00bf1-6cb6-44d2-bb91-cfbe96e2ae19\",\n"
		+ "            \"roomId\": 7,\n"
		+ "            \"status\": \"PROCESSED\",\n"
		+ "            \"interview\": false,\n"
		+ "            \"start\": \"2021-09-19T10:43:39+12:00\",\n"
		+ "            \"end\": \"2021-09-19T10:43:56+12:00\",\n"
		+ "            \"width\": 1792,\n"
		+ "            \"height\": 1120,\n"
		+ "            \"ownerId\": 1\n"
		+ "        }\n"
		+ "    ]\n"
		+ "}")
public class RecordingDTOListWrapper {
	private List<RecordingDTO> recordingDTO;

	public List<RecordingDTO> getRecordingDTO() {
		return recordingDTO;
	}

	public void setRecordingDTO(List<RecordingDTO> recordingDTO) {
		this.recordingDTO = recordingDTO;
	}
}
