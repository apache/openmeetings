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
package org.apache.openmeetings.db.util.ws;

import static java.util.UUID.randomUUID;
import static org.apache.openmeetings.db.manager.ISipManager.SIP_FIRST_NAME;
import static org.apache.openmeetings.util.OmFileHelper.SIP_USER_ID;

import java.util.Date;

import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.wicket.protocol.ws.api.message.IWebSocketPushMessage;

public class RoomMessage implements IWebSocketPushMessage {
	private static final long serialVersionUID = 1L;
	public enum Type {
		ROOM_ENTER
		, ROOM_EXIT
		, ROOM_CLOSED
		, POLL_CREATED
		, POLL_UPDATED
		, RECORDING_TOGGLED
		, SHARING_TOGGLED
		, RIGHT_UPDATED
		, ACTIVITY_REMOVE
		, REQUEST_RIGHT_MODERATOR
		, REQUEST_RIGHT_PRESENTER
		, REQUEST_RIGHT_WB
		, REQUEST_RIGHT_SHARE
		, REQUEST_RIGHT_REMOTE
		, REQUEST_RIGHT_A
		, REQUEST_RIGHT_AV
		, REQUEST_RIGHT_MUTE_OTHERS
		, HAVE_QUESTION
		, KICK
		, MUTE
		, MUTE_OTHERS
		, QUICK_POLL_UPDATED
		, KURENTO_STATUS
		, WB_RELOAD
		, MODERATOR_IN_ROOM
		, WB_PUT_FILE
		, FILE_TREE_UPDATE
	}
	private final Date timestamp;
	private final String uid;
	private final Long roomId;
	private final Long userId;
	private final String name;
	private final Type type;

	public RoomMessage(Long roomId, Client c, Type type) {
		this(roomId, c.getUser(), type);
	}

	public RoomMessage(Long roomId, User u, Type type) {
		this(roomId, u.getId(), u.getDisplayName(), type);
	}

	private RoomMessage(Long roomId, Long userId, String displayName, Type type) {
		this.timestamp = new Date();
		this.roomId = roomId;
		if (SIP_USER_ID.equals(userId)) {
			this.name = SIP_FIRST_NAME;
		} else {
			name = displayName;
		}
		this.userId = userId;
		this.type = type;
		this.uid = randomUUID().toString();
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public Long getRoomId() {
		return roomId;
	}

	public Long getUserId() {
		return userId;
	}

	public String getName() {
		return name;
	}

	public Type getType() {
		return type;
	}

	public String getUid() {
		return uid;
	}

	@Override
	public String toString() {
		return new StringBuilder().append("RoomMessage [roomId=").append(roomId)
				.append(", userId=").append(userId)
				.append(", type=").append(type).append("]")
				.toString();
	}
}
