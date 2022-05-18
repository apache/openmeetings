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

import org.apache.openmeetings.db.util.ws.RoomMessage;
import org.apache.openmeetings.db.util.ws.TextRoomMessage;

import java.io.Serializable;
import java.util.Date;

public class Activity implements Serializable {
	private static final long serialVersionUID = 1L;
	public enum Type {
		ROOM_ENTER
		, ROOM_EXIT
		, REQ_RIGHT_MODERATOR(true)
		, REQ_RIGHT_PRESENTER(true)
		, REQ_RIGHT_WB(true)
		, REQ_RIGHT_SHARE(true)
		, REQ_RIGHT_REMOTE(true)
		, REQ_RIGHT_A(true)
		, REQ_RIGHT_AV(true)
		, REQ_RIGHT_MUTE_OTHERS(true)
		, REQ_RIGHT_HAVE_QUESTION(true);

		private final boolean action;

		Type() {
			this(false);
		}

		Type(boolean action) {
			this.action = action;
		}

		public boolean isAction() {
			return action;
		}
	}
	private final String id;
	private final String uid;
	private final Long sender;
	private final String name;
	private final Date created;
	private final Type type;

	public Activity(RoomMessage m, Type type) {
		this(m.getUid(), null, m.getUserId(), m.getName(), type);
	}

	public Activity(TextRoomMessage m, Type type) {
		this(m.getUid(), m.getText(), m.getUserId(), m.getName(), type);
	}

	public Activity(String id, String uid, Long sender, String name, Type type) {
		this.id = id;
		this.uid = uid;
		this.sender = sender;
		this.name = name;
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

	public String getName() {
		return name;
	}

	public Type getType() {
		return type;
	}

	public Date getCreated() {
		return created;
	}
}
