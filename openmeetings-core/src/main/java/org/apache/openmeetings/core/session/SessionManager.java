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
package org.apache.openmeetings.core.session;

import static org.apache.openmeetings.core.remote.ScopeApplicationAdapter.getApp;
import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.openmeetings.IApplication;
import org.apache.openmeetings.db.dao.server.ISessionManager;
import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.db.entity.basic.IClient;
import org.apache.openmeetings.db.entity.room.StreamClient;
import org.red5.logging.Red5LoggerFactory;
import org.red5.server.Server;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

/**
 * Handle {@link StreamClient} objects.
 *
 * Use a kind of decorator pattern to inject the {@link Server} into every call.
 *
 * @author sebawagner
 *
 */
@Component
public class SessionManager implements ISessionManager {
	protected static final Logger log = Red5LoggerFactory.getLogger(SessionManager.class, webAppRootKey);

	private static Map<String, StreamClient> getClients() {
		return getApp().getStreamClients();
	}

	@Override
	public StreamClient add(StreamClient c) {
		if (c == null) {
			return null;
		}
		IApplication iapp = getApp();
		c.setServerId(iapp.getServerId());
		c.setConnectedSince(new Date());
		c.setRoomEnter(new Date());
		iapp.update(c);
		return c;
	}

	@Override
	public Collection<StreamClient> list() {
		return getClients().values();
	}

	@Override
	public StreamClient get(String uid) {
		if (uid == null) {
			return null;
		}
		return getClients().get(uid);
	}

	@Override
	public void update(IClient rcm) {
		getApp().update(rcm);
	}

	@Override
	public boolean remove(String uid) {
		if (uid == null) {
			return false;
		}
		StreamClient c = getClients().remove(uid);
		return c != null;
	}

	@Override
	public List<StreamClient> listByRoom(Long roomId) {
		return list().stream()
				.filter(c -> roomId.equals(c.getRoomId()) && Client.Type.sharing != c.getType())
				.collect(Collectors.toList());
	}

	@Override
	public long getRecordingCount(Long roomId) {
		if (roomId == null) {
			return 0;
		}
		return list().stream()
				.filter(c -> roomId.equals(c.getRoomId()) && c.isRecordingStarted())
				.collect(Collectors.toList()).size();
	}

	@Override
	public long getPublishingCount(Long roomId) {
		if (roomId == null) {
			return 0;
		}
		return list().stream()
				.filter(c -> roomId.equals(c.getRoomId()) && c.isPublishStarted())
				.collect(Collectors.toList()).size();
	}

	@Override
	public long getSharingCount(Long roomId) {
		if (roomId == null) {
			return 0;
		}
		return list().stream()
				.filter(c -> roomId.equals(c.getRoomId()) && c.isSharingStarted())
				.collect(Collectors.toList()).size();
	}

	@Override
	public long getBroadcastingCount(Long roomId) {
		if (roomId == null) {
			return 0;
		}
		return list().stream()
				.filter(c -> roomId.equals(c.getRoomId()) && c.isBroadcasting() && c.getBroadcastId() != null)
				.collect(Collectors.toList()).size();
	}

	@Override
	public Set<Long> getActiveRoomIds() {
		return getApp().getActiveRoomIds();
	}

	@Override
	public Set<Long> getActiveRoomIds(String serverId) {
		Set<Long> ids = new HashSet<>();
		if (serverId != null) {
			for (Map.Entry<String, StreamClient> e : getClients().entrySet()) {
				if (serverId.equals(e.getValue().getServerId())) {
					ids.add(e.getValue().getRoomId());
				}
			}
		}
		return ids;
	}
}
