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
package org.apache.openmeetings.web.room.activities;

import java.io.Serializable;
import java.util.Date;

import org.apache.openmeetings.util.message.RoomMessage;
import org.apache.openmeetings.util.message.TextRoomMessage;

public class Activity implements Serializable {
	private static final long serialVersionUID = 1L;
	public enum Type {
		roomEnter
		, roomExit
		, reqRightModerator
		, reqRightWb
		, reqRightShare
		, reqRightRemote
		, reqRightA
		, reqRightAv
		, reqRightMute
		, reqRightExclusive
		, haveQuestion
	}
	private final String id;
	private final String uid;
	private final Long sender;
	private final Date created;
	private final Type type;

	public Activity(RoomMessage m, Type type) {
		this(m.getUid(), null, m.getUserId(), type);
	}

	public Activity(TextRoomMessage m, Type type) {
		this(m.getUid(), m.getText(), m.getUserId(), type);
	}

	public Activity(String id, String uid, Long sender, Type type) {
		this.id = id;
		this.uid = uid;
		this.sender = sender;
		this.type = type;
		this.created = new Date();
	}

	public String getId() {
		return id;
	}

	public String getUid() {
		return uid;
	}

	public Long getSender() {
		return sender;
	}

	public Type getType() {
		return type;
	}

	public Date getCreated() {
		return created;
	}
}
