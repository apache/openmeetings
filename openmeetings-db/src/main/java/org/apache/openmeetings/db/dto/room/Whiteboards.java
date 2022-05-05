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

import static java.util.UUID.randomUUID;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class Whiteboards implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long roomId;
	private final String uid = randomUUID().toString();
	private Map<Long, Whiteboard> boards = new ConcurrentHashMap<>();
	private AtomicLong whiteboardId = new AtomicLong(0);
	private AtomicLong activeWb = new AtomicLong(0);

	public Whiteboards() {
		//def constructor
	}

	public Whiteboards(Long roomId) {
		this.roomId = roomId;
	}

	public Long getRoomId() {
		return roomId;
	}

	public void setRoomId(Long roomId) {
		this.roomId = roomId;
	}

	public Whiteboards add(Whiteboard wb) {
		wb.setId(whiteboardId.getAndIncrement());
		boards.put(wb.getId(), wb);
		return this;
	}

	public Whiteboard get(Long id) {
		return boards.get(id);
	}

	public int count() {
		return boards.size();
	}

	public Map<Long, Whiteboard> getWhiteboards() {
		return boards;
	}

	public void setWhiteboards(Map<Long, Whiteboard> whiteboards) {
		this.boards = whiteboards;
	}

	public void update(Whiteboard wb) {
		boards.put(wb.getId(), wb);
	}

	public String getUid() {
		return uid;
	}

	public long getActiveWb() {
		return activeWb.get();
	}

	public void setActiveWb(long wbId) {
		activeWb.set(wbId);
	}
}
