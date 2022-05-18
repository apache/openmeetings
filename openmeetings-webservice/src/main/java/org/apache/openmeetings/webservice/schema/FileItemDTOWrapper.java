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

import org.apache.openmeetings.db.dto.file.FileItemDTO;

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
@Schema(example = """
		{
		  "fileItemDTO": {
		    "id": 2,
		    "name": "OpenMeetings-NodeJS-Use-Case_preview.jpg",
		    "hash": "f802b727-5bfc-4273-8c90-939ea15bf388",
		    "ownerId": 1,
		    "size": 391449,
		    "type": "IMAGE",
		    "width": 2193,
		    "height": 1823
		  }
		}""")
public class FileItemDTOWrapper {
	private FileItemDTO fileItemDTO;

	public FileItemDTO getFileItemDTO() {
		return fileItemDTO;
	}

	public void setFileItemDTO(FileItemDTO fileItemDTO) {
		this.fileItemDTO = fileItemDTO;
	}
}
