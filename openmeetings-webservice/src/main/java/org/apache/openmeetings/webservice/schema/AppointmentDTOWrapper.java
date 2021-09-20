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

import org.apache.openmeetings.db.dto.calendar.AppointmentDTO;

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
		+ "    \"appointmentDTO\": {\n"
		+ "        \"connectedEvent\": false,\n"
		+ "        \"deleted\": false,\n"
		+ "        \"description\": \"Comments\",\n"
		+ "        \"end\": \"2021-09-23T12:31:00+1200\",\n"
		+ "        \"icalId\": \"7507ab5d-81ec-458a-bdb1-48d102978e49\",\n"
		+ "        \"id\": 1,\n"
		+ "        \"inserted\": \"2021-09-19T11:31:37+1200\",\n"
		+ "        \"meetingMembers\": [\n"
		+ "            {\n"
		+ "                \"id\": 1,\n"
		+ "                \"user\": {\n"
		+ "                    \"address\": {\n"
		+ "                        \"country\": \"NZ\",\n"
		+ "                        \"deleted\": false,\n"
		+ "                        \"email\": \"seba.wagner@gmail.com\",\n"
		+ "                        \"id\": 1,\n"
		+ "                        \"inserted\": {\n"
		+ "                            \"date\": 29,\n"
		+ "                            \"day\": 0,\n"
		+ "                            \"hours\": 10,\n"
		+ "                            \"minutes\": 59,\n"
		+ "                            \"month\": 7,\n"
		+ "                            \"ownerField\": 1,\n"
		+ "                            \"seconds\": 49,\n"
		+ "                            \"time\": 1630191589000,\n"
		+ "                            \"timezoneOffset\": -720,\n"
		+ "                            \"year\": 121\n"
		+ "                        }\n"
		+ "                    },\n"
		+ "                    \"firstname\": \"firstname\",\n"
		+ "                    \"id\": 1,\n"
		+ "                    \"languageId\": 1,\n"
		+ "                    \"lastname\": \"lastname\",\n"
		+ "                    \"login\": \"admin\",\n"
		+ "                    \"rights\": [\n"
		+ "                        \"ROOM\",\n"
		+ "                        \"SOAP\",\n"
		+ "                        \"DASHBOARD\",\n"
		+ "                        \"ADMIN\",\n"
		+ "                        \"LOGIN\"\n"
		+ "                    ],\n"
		+ "                    \"timeZoneId\": \"Europe/Berlin\",\n"
		+ "                    \"type\": \"USER\"\n"
		+ "                }\n"
		+ "            }\n"
		+ "        ],\n"
		+ "        \"owner\": {\n"
		+ "            \"address\": {\n"
		+ "                \"country\": \"NZ\",\n"
		+ "                \"deleted\": false,\n"
		+ "                \"email\": \"seba.wagner@gmail.com\",\n"
		+ "                \"id\": 1,\n"
		+ "                \"inserted\": {\n"
		+ "                    \"date\": 29,\n"
		+ "                    \"day\": 0,\n"
		+ "                    \"hours\": 10,\n"
		+ "                    \"minutes\": 59,\n"
		+ "                    \"month\": 7,\n"
		+ "                    \"ownerField\": 1,\n"
		+ "                    \"seconds\": 49,\n"
		+ "                    \"time\": 1630191589000,\n"
		+ "                    \"timezoneOffset\": -720,\n"
		+ "                    \"year\": 121\n"
		+ "                }\n"
		+ "            },\n"
		+ "            \"firstname\": \"firstname\",\n"
		+ "            \"id\": 1,\n"
		+ "            \"languageId\": 1,\n"
		+ "            \"lastname\": \"lastname\",\n"
		+ "            \"login\": \"admin\",\n"
		+ "            \"rights\": [\n"
		+ "                \"ROOM\",\n"
		+ "                \"SOAP\",\n"
		+ "                \"DASHBOARD\",\n"
		+ "                \"ADMIN\",\n"
		+ "                \"LOGIN\"\n"
		+ "            ],\n"
		+ "            \"timeZoneId\": \"Europe/Berlin\",\n"
		+ "            \"type\": \"USER\"\n"
		+ "        },\n"
		+ "        \"passwordProtected\": false,\n"
		+ "        \"reminder\": \"ICAL\",\n"
		+ "        \"reminderEmailSend\": false,\n"
		+ "        \"room\": {\n"
		+ "            \"allowRecording\": true,\n"
		+ "            \"allowUserQuestions\": false,\n"
		+ "            \"appointment\": true,\n"
		+ "            \"audioOnly\": false,\n"
		+ "            \"capacity\": 50,\n"
		+ "            \"closed\": false,\n"
		+ "            \"demo\": false,\n"
		+ "            \"files\": [],\n"
		+ "            \"hiddenElements\": [\n"
		+ "                \"MICROPHONE_STATUS\"\n"
		+ "            ],\n"
		+ "            \"id\": 11,\n"
		+ "            \"moderated\": false,\n"
		+ "            \"name\": \"New Event\",\n"
		+ "            \"public\": false,\n"
		+ "            \"type\": \"CONFERENCE\",\n"
		+ "            \"waitModerator\": false,\n"
		+ "            \"waitRecording\": false\n"
		+ "        },\n"
		+ "        \"start\": \"2021-09-23T11:31:00+1200\",\n"
		+ "        \"title\": \"New Event\"\n"
		+ "    }\n"
		+ "}")
public class AppointmentDTOWrapper {
	private AppointmentDTO appointmentDTO;

	public AppointmentDTO getAppointmentDTO() {
		return appointmentDTO;
	}

	public void setAppointmentDTO(AppointmentDTO appointmentDTO) {
		this.appointmentDTO = appointmentDTO;
	}
}
