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
package org.apache.openmeetings.web.app;

import static org.apache.openmeetings.util.OpenmeetingsVariables.getDefaultLang;
import static org.apache.openmeetings.web.app.Application.getHazelcast;

import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.openmeetings.db.dao.label.LabelDao;
import org.apache.openmeetings.db.dto.room.Whiteboard;
import org.apache.openmeetings.db.dto.room.Whiteboards;
import org.apache.openmeetings.db.manager.IWhiteboardManager;
import org.springframework.stereotype.Component;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

/**
 * Hazelcast based Whiteboard manager
 *
 * @author sebawagner
 *
 */
@Component
public class WhiteboardManager implements IWhiteboardManager {
	private static final String WBS_KEY = "WBS_KEY";
	private HazelcastInstance hazelcast;

	@PostConstruct
	private void init() {
		this.hazelcast = getHazelcast();
	}

	private IMap<Long, Whiteboards> getCache() {
		return hazelcast.getMap(WBS_KEY);
	}

	public boolean tryLock(Long roomId) {
		return getCache().tryLock(roomId);
	}

	public void unlock(Long roomId) {
		getCache().unlock(roomId);
	}

	private static String getDefaultName(Long langId, int num) {
		StringBuilder sb = new StringBuilder(LabelDao.getString("615", langId));
		if (num > 0) {
			sb.append(" ").append(num);
		}
		return sb.toString();
	}

	public boolean contains(Long roomId) {
		return getCache().containsKey(roomId);
	}

	@Override
	public Whiteboards get(Long roomId) {
		return get(roomId, null);
	}

	public Whiteboards get(Long roomId, Long langId) {
		if (roomId == null) {
			return null;
		}
		Whiteboards wbs = getCache().get(roomId);
		if (wbs == null) {
			wbs = new Whiteboards(roomId);
			Whiteboard wb = add(wbs, langId);
			wbs.setActiveWb(wb.getId());
			update(wbs);
		}
		return wbs;
	}

	public Set<Entry<Long, Whiteboard>> list(long roomId) {
		Whiteboards wbs = get(roomId);
		return wbs.getWhiteboards().entrySet();
	}

	public Whiteboard add(long roomId, Long langId) {
		Whiteboards wbs = get(roomId);
		Whiteboard wb = add(wbs, langId);
		update(wbs);
		return wb;
	}

	private static Whiteboard add(Whiteboards wbs, Long langId) {
		Whiteboard wb = new Whiteboard(getDefaultName(langId == null ? getDefaultLang() : langId, wbs.count()));
		wbs.add(wb);
		return wb;
	}

	public Whiteboard clear(long roomId, Long wbId) {
		Whiteboards wbs = get(roomId);
		Whiteboard wb = wbs.get(wbId);
		if (wb != null) {
			wb.clear();
			update(wbs);
		}
		return wb;
	}

	public Whiteboard remove(long roomId, Long wbId) {
		Whiteboards wbs = get(roomId);
		Whiteboard wb = wbs.getWhiteboards().remove(wbId);
		update(wbs);
		return wb;
	}

	public void activate(long roomId, Long wbId) {
		Whiteboards wbs = get(roomId);
		wbs.setActiveWb(wbId);
		update(wbs);
	}

	public void update(long roomId, Whiteboard wb) {
		Whiteboards wbs = get(roomId);
		wbs.update(wb);
		update(wbs);
	}

	private void update(Whiteboards wbs) {
		getCache().put(wbs.getRoomId(), wbs);
	}
}
