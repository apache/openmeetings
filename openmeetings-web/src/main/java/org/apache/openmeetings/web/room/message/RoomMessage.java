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
package org.apache.openmeetings.web.room.message;

import java.io.Serializable;
import java.util.Date;

import org.apache.openmeetings.web.app.WebSession;
import org.apache.wicket.protocol.ws.api.message.IWebSocketPushMessage;

public class RoomMessage implements IWebSocketPushMessage, Serializable {
	private static final long serialVersionUID = 1L;
	public enum Type {
		roomEnter
		, roomExit
		, pollCreated
		, pollClosed
		, pollDeleted
		, voted
		, rightUpdated
	}
	private final Date timestamp;
	private final Long sentUserId;
	private final Long roomId;
	private final Long userId;
	private final Type type;

	public RoomMessage(Long roomId, Long userId, Type type) {
		this.timestamp = new Date();
		this.sentUserId = WebSession.getUserId();
		this.roomId = roomId;
		this.userId = userId;
		this.type = type;
	}
	
	public Date getTimestamp() {
		return timestamp;
	}

	public Long getSentUserId() {
		return sentUserId;
	}

	public Long getRoomId() {
		return roomId;
	}

	public Long getUserId() {
		return userId;
	}

	public Type getType() {
		return type;
	}
}
