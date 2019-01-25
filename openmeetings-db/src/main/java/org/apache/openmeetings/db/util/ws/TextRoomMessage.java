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

import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.db.entity.user.User;

public class TextRoomMessage extends RoomMessage {
	private static final long serialVersionUID = 1L;
	private final String text;

	public TextRoomMessage(Long roomId, Client client, Type type, String text) {
		super(roomId, client, type);
		this.text = text;
	}

	public TextRoomMessage(Long roomId, User u, Type type, String text) {
		super(roomId, u, type);
		this.text = text;
	}

	public String getText() {
		return text;
	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append("TextRoomMessage [text=").append(text)
				.append(", getRoomId()=").append(getRoomId())
				.append(", getUserId()=").append(getUserId())
				.append(", getType()=").append(getType()).append("]")
				.toString();
	}
}
