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

import static org.apache.openmeetings.db.dao.room.SipDao.SIP_FIRST_NAME;
import static org.apache.openmeetings.util.OmFileHelper.SIP_USER_ID;

import java.util.Date;
import java.util.UUID;

import org.apache.openmeetings.db.entity.basic.IClient;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.wicket.protocol.ws.api.message.IWebSocketPushMessage;

public class RoomMessage implements IWebSocketPushMessage {
	private static final long serialVersionUID = 1L;
	public enum Type {
		roomEnter
		, roomExit
		, roomClosed
		, pollCreated
		, pollUpdated
		, recordingStarted
		, recordingStoped
		, sharingStarted
		, sharingStoped
		, rightUpdated
		, activityRemove
		, requestRightModerator
		, requestRightPresenter
		, requestRightWb
		, requestRightShare
		, requestRightRemote
		, requestRightA
		, requestRightAv
		, requestRightExclusive
		, haveQuestion
		, kick
		, newStream
		, closeStream
		, mute
		, exclusive
		, quickPollUpdated
	}
	private final Date timestamp;
	private final String uid;
	private final Long roomId;
	private final Long userId;
	private final String name;
	private final Type type;

	public RoomMessage(Long roomId, IClient c, Type type) {
		this(roomId, c.getUserId(), c.getFirstname(), c.getLastname(), type);
	}

	public RoomMessage(Long roomId, User u, Type type) {
		this(roomId, u.getId(), u.getFirstname(), u.getLastname(), type);
	}

	private RoomMessage(Long roomId, Long userId, String firstName, String lastName, Type type) {
		this.timestamp = new Date();
		this.roomId = roomId;
		if (SIP_USER_ID.equals(userId)) {
			this.name = SIP_FIRST_NAME;
		} else {
			name = String.format("%s %s", firstName, lastName);
		}
		this.userId = userId;
		this.type = type;
		this.uid = UUID.randomUUID().toString();
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
}
