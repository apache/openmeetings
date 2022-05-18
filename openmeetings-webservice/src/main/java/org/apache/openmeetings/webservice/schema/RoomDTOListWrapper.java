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

import org.apache.openmeetings.db.dto.room.RoomDTO;

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
		  "roomDTO": [
		    {
		      "id": 2,
		      "name": "Public Conference Room [install.room.public.conference]",
		      "comment": "",
		      "type": "CONFERENCE",
		      "capacity": 32,
		      "appointment": false,
		      "isPublic": true,
		      "demo": false,
		      "closed": false,
		      "moderated": false,
		      "waitModerator": false,
		      "allowUserQuestions": true,
		      "allowRecording": true,
		      "waitRecording": false,
		      "audioOnly": false,
		      "hiddenElements": "MICROPHONE_STATUS"
		    },
		    {
		      "id": 3,
		      "name": "Public Video Only Room [install.room.public.video.only]",
		      "comment": "",
		      "type": "CONFERENCE",
		      "capacity": 32,
		      "appointment": false,
		      "isPublic": true,
		      "demo": false,
		      "closed": false,
		      "moderated": false,
		      "waitModerator": false,
		      "allowUserQuestions": true,
		      "allowRecording": true,
		      "waitRecording": false,
		      "audioOnly": false,
		      "hiddenElements": [
		        "WHITEBOARD",
		        "MICROPHONE_STATUS"
		      ]
		    },
		    {
		      "id": 4,
		      "name": "Public Video And Whiteboard Room [install.room.public.video.wb]",
		      "comment": "",
		      "type": "CONFERENCE",
		      "capacity": 32,
		      "appointment": false,
		      "isPublic": true,
		      "demo": false,
		      "closed": false,
		      "moderated": false,
		      "waitModerator": false,
		      "allowUserQuestions": true,
		      "allowRecording": true,
		      "waitRecording": false,
		      "audioOnly": false,
		      "hiddenElements": "MICROPHONE_STATUS"
		    },
		    {
		      "id": 7,
		      "name": "Conference room with microphone option set [install.room.conference.micro]",
		      "comment": "",
		      "type": "CONFERENCE",
		      "capacity": 32,
		      "appointment": false,
		      "isPublic": true,
		      "demo": false,
		      "closed": false,
		      "moderated": false,
		      "waitModerator": false,
		      "allowUserQuestions": true,
		      "allowRecording": true,
		      "waitRecording": false,
		      "audioOnly": false
		    }
		  ]
		}""")
public class RoomDTOListWrapper {
	private List<RoomDTO> roomDTO;

	public List<RoomDTO> getRoomDTO() {
		return roomDTO;
	}

	public void setRoomDTO(List<RoomDTO> roomDTO) {
		this.roomDTO = roomDTO;
	}
}
