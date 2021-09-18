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
@Schema(example = "{\n"
		+ "    \"roomDTO\": [\n"
		+ "        {\n"
		+ "            \"id\": 2,\n"
		+ "            \"name\": \"Public Conference Room [install.room.public.conference]\",\n"
		+ "            \"comment\": \"\",\n"
		+ "            \"type\": \"CONFERENCE\",\n"
		+ "            \"capacity\": 32,\n"
		+ "            \"appointment\": false,\n"
		+ "            \"isPublic\": true,\n"
		+ "            \"demo\": false,\n"
		+ "            \"closed\": false,\n"
		+ "            \"moderated\": false,\n"
		+ "            \"waitModerator\": false,\n"
		+ "            \"allowUserQuestions\": true,\n"
		+ "            \"allowRecording\": true,\n"
		+ "            \"waitRecording\": false,\n"
		+ "            \"audioOnly\": false,\n"
		+ "            \"hiddenElements\": \"MICROPHONE_STATUS\"\n"
		+ "        },\n"
		+ "        {\n"
		+ "            \"id\": 3,\n"
		+ "            \"name\": \"Public Video Only Room [install.room.public.video.only]\",\n"
		+ "            \"comment\": \"\",\n"
		+ "            \"type\": \"CONFERENCE\",\n"
		+ "            \"capacity\": 32,\n"
		+ "            \"appointment\": false,\n"
		+ "            \"isPublic\": true,\n"
		+ "            \"demo\": false,\n"
		+ "            \"closed\": false,\n"
		+ "            \"moderated\": false,\n"
		+ "            \"waitModerator\": false,\n"
		+ "            \"allowUserQuestions\": true,\n"
		+ "            \"allowRecording\": true,\n"
		+ "            \"waitRecording\": false,\n"
		+ "            \"audioOnly\": false,\n"
		+ "            \"hiddenElements\": [\n"
		+ "                \"WHITEBOARD\",\n"
		+ "                \"MICROPHONE_STATUS\"\n"
		+ "            ]\n"
		+ "        },\n"
		+ "        {\n"
		+ "            \"id\": 4,\n"
		+ "            \"name\": \"Public Video And Whiteboard Room [install.room.public.video.wb]\",\n"
		+ "            \"comment\": \"\",\n"
		+ "            \"type\": \"CONFERENCE\",\n"
		+ "            \"capacity\": 32,\n"
		+ "            \"appointment\": false,\n"
		+ "            \"isPublic\": true,\n"
		+ "            \"demo\": false,\n"
		+ "            \"closed\": false,\n"
		+ "            \"moderated\": false,\n"
		+ "            \"waitModerator\": false,\n"
		+ "            \"allowUserQuestions\": true,\n"
		+ "            \"allowRecording\": true,\n"
		+ "            \"waitRecording\": false,\n"
		+ "            \"audioOnly\": false,\n"
		+ "            \"hiddenElements\": \"MICROPHONE_STATUS\"\n"
		+ "        },\n"
		+ "        {\n"
		+ "            \"id\": 7,\n"
		+ "            \"name\": \"Conference room with microphone option set [install.room.conference.micro]\",\n"
		+ "            \"comment\": \"\",\n"
		+ "            \"type\": \"CONFERENCE\",\n"
		+ "            \"capacity\": 32,\n"
		+ "            \"appointment\": false,\n"
		+ "            \"isPublic\": true,\n"
		+ "            \"demo\": false,\n"
		+ "            \"closed\": false,\n"
		+ "            \"moderated\": false,\n"
		+ "            \"waitModerator\": false,\n"
		+ "            \"allowUserQuestions\": true,\n"
		+ "            \"allowRecording\": true,\n"
		+ "            \"waitRecording\": false,\n"
		+ "            \"audioOnly\": false\n"
		+ "        }\n"
		+ "    ]\n"
		+ "}")
public class RoomDTOListWrapper {
	private List<RoomDTO> roomDTO;

	public List<RoomDTO> getRoomDTO() {
		return roomDTO;
	}

	public void setRoomDTO(List<RoomDTO> roomDTO) {
		this.roomDTO = roomDTO;
	}
}
