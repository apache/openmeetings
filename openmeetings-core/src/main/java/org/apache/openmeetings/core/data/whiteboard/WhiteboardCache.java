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
package org.apache.openmeetings.core.data.whiteboard;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.openmeetings.db.dto.room.Whiteboard;
import org.apache.openmeetings.db.dto.room.Whiteboards;

/**
 * Memory based cache, configured as singleton in spring configuration
 *
 * @author sebawagner
 *
 */
public class WhiteboardCache {
	private Map<Long, Whiteboards> cache = new ConcurrentHashMap<>();

	private volatile AtomicLong whiteboardId = new AtomicLong(0);

	public long getNewWhiteboardId(Long roomId, String name) {
		long wbId = whiteboardId.getAndIncrement();
		set(roomId, new Whiteboard(name), wbId);
		return wbId;
	}

	/*
	 * Room items a Whiteboard
	 */
	public Whiteboards get(Long roomId) {
		if (roomId == null) {
			return null;
		}
		if (cache.containsKey(roomId)) {
			return cache.get(roomId);
		} else {
			Whiteboards whiteboards = new Whiteboards();
			whiteboards.setRoomId(roomId);
			set(roomId, whiteboards);
			return whiteboards;
		}
	}

	public Whiteboard get(Long roomId, Long whiteBoardId) {
		Whiteboards whiteboards = get(roomId);
		Whiteboard wb = whiteboards.getWhiteboards().get(whiteBoardId);
		if (wb == null) {
			wb = new Whiteboard();
			set(roomId, wb, whiteBoardId);
		}
		return wb;
	}

	/*
	 * Whiteboard Object List
	 *
	 */
	public void set(Long roomId, Whiteboards whiteboards) {
		cache.put(roomId, whiteboards);
	}

	public void set(Long roomId, Whiteboard wb, long whiteBoardId) {
		Whiteboards whiteboards = get(roomId);
		wb.setWhiteBoardId(whiteBoardId);
		whiteboards.getWhiteboards().put(whiteBoardId, wb);
		cache.put(roomId, whiteboards);
	}
}
