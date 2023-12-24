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

import static org.apache.openmeetings.db.dto.room.Whiteboard.ATTR_SLIDE;
import static org.apache.openmeetings.db.util.ApplicationHelper.ensureApplication;
import static org.apache.openmeetings.web.room.wb.WbWebSocketHelper.sendWbAll;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getDefaultLang;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.openmeetings.core.util.WebSocketHelper;
import org.apache.openmeetings.db.dao.label.LabelDao;
import org.apache.openmeetings.db.dto.room.Whiteboard;
import org.apache.openmeetings.db.dto.room.Whiteboards;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.room.RoomFile;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.manager.IWhiteboardManager;
import org.apache.openmeetings.db.util.ws.RoomMessage;
import org.apache.openmeetings.web.room.wb.WbAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

import com.github.openjson.JSONArray;
import com.github.openjson.JSONObject;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.map.IMap;
import com.hazelcast.map.listener.EntryAddedListener;
import com.hazelcast.map.listener.EntryRemovedListener;
import com.hazelcast.map.listener.EntryUpdatedListener;

import jakarta.inject.Inject;

/**
 * Hazelcast based Whiteboard manager
 *
 * @author sebawagner
 *
 */
@Component
public class WhiteboardManager implements IWhiteboardManager {
	private static final Logger log = LoggerFactory.getLogger(WhiteboardManager.class);
	private final Map<Long, Whiteboards> onlineWbs = new ConcurrentHashMap<>();
	private static final String WBS_KEY = "WBS_KEY";

	@Inject
	private Application app;

	private IMap<Long, Whiteboards> map() {
		return app.hazelcast.getMap(WBS_KEY);
	}

	void init() {
		map().addEntryListener(new WbListener(), true);
		map().entrySet().forEach(e -> onlineWbs.put(e.getKey(), e.getValue()));
	}

	private static String getDefaultName(Long langId, int num) {
		StringBuilder sb = new StringBuilder(LabelDao.getString("615", langId));
		if (num > 0) {
			sb.append(" ").append(num);
		}
		return sb.toString();
	}

	private boolean contains(Long roomId) {
		return onlineWbs.containsKey(roomId);
	}

	@Override
	public void reset(Long roomId, Long userId) {
		if (roomId == null) {
			return;
		}
		try {
			if (contains(roomId) && map().tryLock(roomId, 1, TimeUnit.SECONDS)) {
				try {
					onlineWbs.remove(roomId);
					map().delete(roomId);
				} finally {
					map().unlock(roomId);
				}
			}
			new Thread(() -> {
				ensureApplication();
				User u = new User();
				u.setId(userId);
				WebSocketHelper.sendRoom(new RoomMessage(roomId, u, RoomMessage.Type.WB_RELOAD));
			}).start();
		} catch (InterruptedException e) {
			log.warn("Unexpected exception while map clean-up", e);
			Thread.currentThread().interrupt();
		}
	}

	@Override
	public Whiteboards get(Long roomId) {
		return get(roomId, null);
	}

	private Whiteboards getOrCreate(Long roomId, Consumer<Whiteboards> consumer) {
		if (roomId == null) {
			return null;
		}
		Whiteboards wbs = onlineWbs.get(roomId);
		if (wbs == null) {
			wbs = new Whiteboards(roomId);
			if (consumer != null) {
				consumer.accept(wbs);
			}
		}
		return wbs;
	}

	public void initFiles(Room r, Long langId, TriConsumer<Whiteboards, Whiteboard, List<RoomFile>> creator) {
		if (!contains(r.getId())
				&& r.getFiles() != null
				&& !r.getFiles().isEmpty()
				&& map().tryLock(r.getId()))
		{
			try {
				getOrCreate(r.getId(), wbs -> {
					r.getFiles().stream()
						.sorted((rf1, rf2) -> (int)(rf1.getWbIdx() - rf2.getWbIdx()))
						.collect(Collectors.groupingBy(RoomFile::getWbIdx))
						.forEach((wbIdx, fileList) -> {
							log.trace("WBS init, processing idx {}", wbIdx);
							Whiteboard wb = add(wbs, langId);
							wbs.setActiveWb(wb.getId());
							creator.accept(wbs, wb, fileList);
						});
					update(wbs);
				});
			} finally {
				map().unlock(r.getId());
			}
		}
	}

	public Whiteboards get(Long roomId, Long langId) {
		Whiteboards wbs = getOrCreate(roomId, inWbs -> {
			if (inWbs.getWhiteboards().isEmpty()) {
				Whiteboard wb = add(inWbs, langId);
				inWbs.setActiveWb(wb.getId());
				update(inWbs);
			}
		});
		if (wbs == null) {
			return null;
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

	@Override
	public void clearAll(Long roomId, long wbId, boolean redo, BiConsumer<Whiteboard, Boolean> addUndo) {
		Whiteboard wb = get(roomId).get(wbId);
		if (wb == null) {
			return;
		}
		if (addUndo != null) {
			addUndo.accept(wb, redo);
		}
		wb = clear(roomId, wbId);
		sendWbAll(roomId, WbAction.CLEAR_ALL, new JSONObject().put("wbId", wbId));
		sendWbAll(roomId, WbAction.SET_SIZE, wb.getAddJson());
	}

	@Override
	public void cleanSlide(Long roomId, long wbId, int slide, BiConsumer<Whiteboard, JSONArray> consumer) {
		Whiteboard wb = get(roomId).get(wbId);
		JSONArray arr = wb.clearSlide(slide);
		if (arr.length() != 0) {
			update(roomId, wb);
			if (consumer != null) {
				consumer.accept(wb, arr);
			}
			sendWbAll(roomId, WbAction.CLEAR_SLIDE, new JSONObject()
					.put("wbId", wbId)
					.put(ATTR_SLIDE, slide));
		}
	}

	public Whiteboard remove(long roomId, long wbId, long prevWbId) {
		Whiteboards wbs = get(roomId);
		Whiteboard wb = wbs.getWhiteboards().remove(wbId);
		if (prevWbId > -1) {
			wbs.setActiveWb(prevWbId);
		}
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
		onlineWbs.put(wbs.getRoomId(), wbs);
		new Thread(() -> map().put(wbs.getRoomId(), wbs)).start();
	}

	public class WbListener implements
			EntryAddedListener<Long, Whiteboards>
			, EntryUpdatedListener<Long, Whiteboards>
			, EntryRemovedListener<Long, Whiteboards>
	{
		@Override
		public void entryAdded(EntryEvent<Long, Whiteboards> event) {
			log.trace("WbListener::Add");
			onlineWbs.put(event.getKey(), event.getValue());
		}

		@Override
		public void entryUpdated(EntryEvent<Long, Whiteboards> event) {
			log.trace("WbListener::Update");
			onlineWbs.put(event.getKey(), event.getValue());
		}

		@Override
		public void entryRemoved(EntryEvent<Long, Whiteboards> event) {
			log.trace("WbListener::Remove");
			onlineWbs.remove(event.getKey());
		}
	}

	@FunctionalInterface
	public static interface TriConsumer<T, U, S> {
		void accept(T t, U u, S s);
	}
}
