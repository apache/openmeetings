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

import org.apache.openmeetings.db.dto.file.FileExplorerObject;

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
		+ "    \"fileExplorerObject\": {\n"
		+ "        \"userHome\": {\n"
		+ "            \"id\": 2,\n"
		+ "            \"name\": \"OpenMeetings-NodeJS-Use-Case_preview.jpg\",\n"
		+ "            \"hash\": \"f802b727-5bfc-4273-8c90-939ea15bf388\",\n"
		+ "            \"ownerId\": 1,\n"
		+ "            \"size\": 391449,\n"
		+ "            \"type\": \"IMAGE\",\n"
		+ "            \"width\": 2193,\n"
		+ "            \"height\": 1823\n"
		+ "        },\n"
		+ "        \"roomHome\": [\n"
		+ "            {\n"
		+ "                \"id\": 4,\n"
		+ "                \"name\": \"Folder1\",\n"
		+ "                \"hash\": \"fad982f0-2468-4b10-9b6c-be3648f55143\",\n"
		+ "                \"roomId\": 7,\n"
		+ "                \"type\": \"FOLDER\"\n"
		+ "            },\n"
		+ "            {\n"
		+ "                \"id\": 3,\n"
		+ "                \"name\": \"browsers-768x768.png\",\n"
		+ "                \"hash\": \"91902c95-9944-435e-a54d-a2b64158758b\",\n"
		+ "                \"roomId\": 7,\n"
		+ "                \"size\": 325415,\n"
		+ "                \"type\": \"IMAGE\",\n"
		+ "                \"width\": 768,\n"
		+ "                \"height\": 768\n"
		+ "            }\n"
		+ "        ],\n"
		+ "        \"userHomeSize\": 1444970,\n"
		+ "        \"roomHomeSize\": 334311\n"
		+ "    }\n"
		+ "}")
public class FileExplorerObjectWrapper {
	private FileExplorerObject fileExplorerObject;

	public FileExplorerObject getFileExplorerObject() {
		return fileExplorerObject;
	}

	public void setFileExplorerObject(FileExplorerObject fileExplorerObject) {
		this.fileExplorerObject = fileExplorerObject;
	}
}
