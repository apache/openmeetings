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
package org.apache.openmeetings.db.dto.room;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Whiteboards {
	private Long roomId;
	private final String uid = UUID.randomUUID().toString();
	private Map<Long, Whiteboard> whiteboards = new ConcurrentHashMap<>();

	public Whiteboards() {}

	public Long getRoomId() {
		return roomId;
	}

	public void setRoomId(Long roomId) {
		this.roomId = roomId;
	}

	public Map<Long, Whiteboard> getWhiteboards() {
		return whiteboards;
	}

	public void setWhiteboards(Map<Long, Whiteboard> whiteboards) {
		this.whiteboards = whiteboards;
	}

	public String getUid() {
		return uid;
	}
}
