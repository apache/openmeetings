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
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.openmeetings.db.dao.label.LabelDao;
import org.apache.openmeetings.db.dto.room.Whiteboard;
import org.apache.openmeetings.db.dto.room.Whiteboards;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Memory based cache, configured as singleton in spring configuration
 *
 * @author sebawagner
 *
 */
public class WhiteboardCache {
	private Map<Long, Whiteboards> cache = new ConcurrentHashMap<>();

	@Autowired
	private LabelDao labelDao;

	private String getDefaultName(Long langId, int num) {
		StringBuilder sb = new StringBuilder(labelDao.getString("615", langId));
		if (num > 0) {
			sb.append(" ").append(num);
		}
		return sb.toString();
	}

	public Set<Entry<Long, Whiteboard>> list(Long roomId, Long langId) {
		Whiteboards wbs = get(roomId);
		if (wbs.getWhiteboards().isEmpty()) {
			add(wbs, langId);
		}
		return wbs.getWhiteboards().entrySet();
	}

	public Whiteboard add(Long roomId, Long langId) {
		Whiteboards wbs = get(roomId);
		return add(wbs, langId);
	}

	public Whiteboard add(Whiteboards wbs, Long langId) {
		Whiteboard wb = new Whiteboard(getDefaultName(langId, wbs.count()));
		wbs.add(wb);
		return wb;
	}

	public Whiteboards get(Long roomId) {
		if (roomId == null) {
			return null;
		}
		Whiteboards wbs = cache.get(roomId);
		if (wbs == null) {
			wbs = new Whiteboards(roomId);
			cache.put(roomId, wbs);
		}
		return wbs;
	}

	public void clear(Long roomId, Long wbId) {
		Whiteboard wb = get(roomId).get(wbId);
		if (wb != null) {
			wb.clear();
		}
	}

	public Whiteboard remove(Long roomId, Long wbId) {
		Whiteboards wbs = get(roomId);
		return wbs.getWhiteboards().remove(wbId);
	}
}
