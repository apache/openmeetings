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
@Schema(example = """
		{
		  "appointmentDTO": [
		    {
		      "connectedEvent": false,
		      "deleted": false,
		      "description": "Comments",
		      "end": "2021-09-23T12:31:00+1200",
		      "icalId": "7507ab5d-81ec-458a-bdb1-48d102978e49",
		      "id": 1,
		      "inserted": "2021-09-19T11:31:37+1200",
		      "meetingMembers": [
		        {
		          "id": 1,
		          "user": {
		            "address": {
		              "country": "NZ",
		              "deleted": false,
		              "email": "seba.wagner@gmail.com",
		              "id": 1,
		              "inserted": {
		                "date": 29,
		                "day": 0,
		                "hours": 10,
		                "minutes": 59,
		                "month": 7,
		                "ownerField": 1,
		                "seconds": 49,
		                "time": 1630191589000,
		                "timezoneOffset": -720,
		                "year": 121
		              }
		            },
		            "firstname": "firstname",
		            "id": 1,
		            "languageId": 1,
		            "lastname": "lastname",
		            "login": "admin",
		            "rights": [
		              "ROOM",
		              "SOAP",
		              "DASHBOARD",
		              "ADMIN",
		              "LOGIN"
		            ],
		            "timeZoneId": "Europe/Berlin",
		            "type": "USER"
		          }
		        }
		      ],
		      "owner": {
		        "address": {
		          "country": "NZ",
		          "deleted": false,
		          "email": "seba.wagner@gmail.com",
		          "id": 1,
		          "inserted": {
		            "date": 29,
		            "day": 0,
		            "hours": 10,
		            "minutes": 59,
		            "month": 7,
		            "ownerField": 1,
		            "seconds": 49,
		            "time": 1630191589000,
		            "timezoneOffset": -720,
		            "year": 121
		          }
		        },
		        "firstname": "firstname",
		        "id": 1,
		        "languageId": 1,
		        "lastname": "lastname",
		        "login": "admin",
		        "rights": [
		          "ROOM",
		          "SOAP",
		          "DASHBOARD",
		          "ADMIN",
		          "LOGIN"
		        ],
		        "timeZoneId": "Europe/Berlin",
		        "type": "USER"
		      },
		      "passwordProtected": false,
		      "reminder": "ICAL",
		      "reminderEmailSend": false,
		      "room": {
		        "allowRecording": true,
		        "allowUserQuestions": false,
		        "appointment": true,
		        "audioOnly": false,
		        "capacity": 50,
		        "closed": false,
		        "demo": false,
		        "files": [],
		        "hiddenElements": [
		          "MICROPHONE_STATUS"
		        ],
		        "id": 11,
		        "moderated": false,
		        "name": "New Event",
		        "public": false,
		        "type": "CONFERENCE",
		        "waitModerator": false,
		        "waitRecording": false
		      },
		      "start": "2021-09-23T11:31:00+1200",
		      "title": "New Event"
		    }
		  ]
		}
		""")
public class AppointmentDTOListWrapper {
	private List<AppointmentDTO> appointmentDTO;

	public List<AppointmentDTO> getAppointmentDTO() {
		return appointmentDTO;
	}

	public void setAppointmentDTO(List<AppointmentDTO> appointmentDTO) {
		this.appointmentDTO = appointmentDTO;
	}
}
