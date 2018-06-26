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
package org.apache.openmeetings.web.room;

import static org.apache.openmeetings.util.OpenmeetingsVariables.FLASH_PORT;
import static org.apache.openmeetings.util.OpenmeetingsVariables.FLASH_SECURE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.FLASH_SSL_PORT;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getWebAppRootKey;
import static org.apache.openmeetings.web.app.Application.NAME_ATTR_KEY;
import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.room.SwfPanel.FLASH_JS_REFERENCE;
import static org.apache.wicket.RuntimeConfigurationType.DEVELOPMENT;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.util.OmFileHelper;
import org.apache.openmeetings.util.OpenmeetingsVariables;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.app.StreamClientManager;
import org.apache.openmeetings.web.util.ExtendedClientProperties;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.PriorityHeaderItem;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

import com.github.openjson.JSONObject;
import com.hazelcast.core.Member;

public class VideoSettings extends Panel {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Red5LoggerFactory.getLogger(VideoSettings.class, getWebAppRootKey());
	private static final ResourceReference SETTINGS_JS_REFERENCE = new JavaScriptResourceReference(VideoSettings.class, "settings.js");
	public static final String URL = "url";
	public static final String FALLBACK = "fallback";

	public VideoSettings(String id) {
		super(id);
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(new PriorityHeaderItem(JavaScriptHeaderItem.forReference(FLASH_JS_REFERENCE)));
		response.render(new PriorityHeaderItem(JavaScriptHeaderItem.forReference(SETTINGS_JS_REFERENCE)));
	}

	private static String getUri(String protocol, String host, Object port, String app) {
		return String.format("%s://%s:%s/%s", protocol, host, port, app);
	}

	public static JSONObject getInitJson(ExtendedClientProperties cp, Long roomId, String sid) {
		String scope = roomId == null ? OmFileHelper.HIBERNATE : "" + roomId;
		JSONObject gs = OpenmeetingsVariables.getRoomSettings();
		JSONObject s = new JSONObject(gs.toString())
				.put("sid", sid)
				.put("debug", DEVELOPMENT == Application.get().getConfigurationType())
				.put("wmode", "opaque");
		try {
			URL url = new URL(cp.getCodebase());
			String path = url.getPath();
			path = path.substring(1, path.indexOf('/', 2) + 1) + scope;
			String host = getHost(roomId, url.getHost());
			int port = url.getPort() > -1 ? url.getPort() : url.getDefaultPort();
			if (gs.getBoolean(FLASH_SECURE)) {
				s.put(URL, getUri("rtmps", host, gs.getString(FLASH_SSL_PORT), path));
				s.put(FALLBACK, getUri("rtmps", host, port, path));
			} else {
				s.put(URL, getUri("rtmp", host, gs.getString(FLASH_PORT), path));
				s.put(FALLBACK, getUri("rtmpt", host, port, path));
			}
		} catch (Exception e) {
			log.error("Error while constructing video settings parameters", e);
		}
		return s;
	}

	private static String getHost(Long roomId, String _host) {
		if (roomId == null) {
			return _host;
		}
		long minimum = -1;
		Member result = null;
		Map<Member, Set<Long>> activeRoomsMap = new HashMap<>();
		List<Member> servers = Application.get().getServers();
		if (servers.size() > 1) {
			for (Member m : servers) {
				String serverId = m.getStringAttribute(NAME_ATTR_KEY);
				Set<Long> roomIds = getBean(StreamClientManager.class).getActiveRoomIds(serverId);
				if (roomIds.contains(roomId)) {
					// if the room is already opened on a server, redirect the user to that one,
					log.debug("Room is already opened on a server {}", m.getAddress());
					return m.getAddress().getHost();
				}
				activeRoomsMap.put(m, roomIds);
			}
			for (Map.Entry<Member, Set<Long>> entry : activeRoomsMap.entrySet()) {
				Set<Long> roomIds = entry.getValue();
				long capacity = getBean(RoomDao.class).getRoomsCapacityByIds(roomIds);
				if (minimum < 0 || capacity < minimum) {
					minimum = capacity;
					result = entry.getKey();
				}
				log.debug("Checking server: {} Number of rooms {} RoomIds: {} max(Sum): {}", entry.getKey(), roomIds.size(), roomIds, capacity);
			}
		}
		return result == null ? _host : result.getAddress().getHost();
	}
}
