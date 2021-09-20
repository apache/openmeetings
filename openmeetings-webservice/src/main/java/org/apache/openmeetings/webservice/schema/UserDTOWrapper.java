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

import org.apache.openmeetings.db.dto.user.UserDTO;

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
		+ "  \"userDTO\": {\n"
		+ "    \"address\": {\n"
		+ "      \"created\": 1630191589000,\n"
		+ "      \"deleted\": false,\n"
		+ "      \"country\": \"NZ\",\n"
		+ "      \"mail\": \"seba.wagner@gmail.com\"\n"
		+ "    },\n"
		+ "    \"firstname\": \"firstname\",\n"
		+ "    \"id\": 1,\n"
		+ "    \"languageId\": 1,\n"
		+ "    \"lastname\": \"lastname\",\n"
		+ "    \"login\": \"admin\",\n"
		+ "    \"rights\": [\n"
		+ "      \"ROOM\",\n"
		+ "      \"SOAP\",\n"
		+ "      \"DASHBOARD\",\n"
		+ "      \"ADMIN\",\n"
		+ "      \"LOGIN\"\n"
		+ "    ],\n"
		+ "    \"timeZoneId\": \"Europe/Berlin\",\n"
		+ "    \"type\": \"USER\"\n"
		+ "  }\n"
		+ "}")
public class UserDTOWrapper {
	private UserDTO userDTO;

	public UserDTO getUserDTO() {
		return userDTO;
	}

	public void setUserDTO(UserDTO userDTO) {
		this.userDTO = userDTO;
	}
}
