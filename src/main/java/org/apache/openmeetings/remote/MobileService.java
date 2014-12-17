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
package org.apache.openmeetings.remote;

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.openmeetings.db.dao.label.FieldLanguagesValuesDao;
import org.apache.openmeetings.db.dao.room.IRoomManager;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.dao.server.ISessionManager;
import org.apache.openmeetings.db.dao.server.SessiondataDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.room.Client;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.server.Sessiondata;
import org.apache.openmeetings.db.entity.user.Organisation;
import org.apache.openmeetings.db.entity.user.Organisation_Users;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.remote.red5.ScopeApplicationAdapter;
import org.apache.openmeetings.remote.util.SessionVariablesUtil;
import org.apache.wicket.util.string.Strings;
import org.red5.logging.Red5LoggerFactory;
import org.red5.server.api.IConnection;
import org.red5.server.api.Red5;
import org.red5.server.api.service.IServiceCapableConnection;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class MobileService {
	private static final Logger log = Red5LoggerFactory.getLogger(MainService.class, webAppRootKey);
	@Autowired
	private UserDao userDao;
	@Autowired
	private SessiondataDao sessionDao;
	@Autowired
	private ISessionManager sessionManager;
	@Autowired
	private RoomDao roomDao;
	@Autowired
	private IRoomManager roomManager;
	@Autowired
	private FieldLanguagesValuesDao labelDao;
	@Autowired
	private ScopeApplicationAdapter scopeAdapter;

	public Map<String, Object> loginUser(String login, String password) {
		Map<String, Object> result = new Hashtable<String, Object>();
		try {
			result.put("status", -1);
			User u = userDao.login(login, password);
			if (u != null) {
				Sessiondata sd = sessionDao.startsession();
				Boolean bool = sessionDao.updateUser(sd.getSession_id(), u.getUser_id(), false, u.getLanguage_id());
				if (bool == null) {
					// Exception
				} else if (!bool) {
					// invalid Session-Object
					result.put("status", -35);
				} else {
					IConnection conn = Red5.getConnectionLocal();
					String streamId = conn.getClient().getId();
					Client c = sessionManager.getClientByStreamId(streamId, null);
					if (c == null) {
						c = sessionManager.addClientListItem(streamId, conn.getScope().getName(), conn.getRemotePort(),
							conn.getRemoteAddress(), "", false, null);
					}
					
					SessionVariablesUtil.initClient(conn.getClient(), false, c.getPublicSID());
					c.setUser_id(u.getUser_id());
					c.setFirstname(u.getFirstname());
					c.setLastname(u.getLastname());
					//c.set
					sessionManager.updateClientByStreamId(streamId, c, false, null);

					result.put("sid", sd.getSession_id());
					result.put("publicSid", c.getPublicSID());
					result.put("status", 0);
					result.put("userId", u.getUser_id());
					result.put("firstname", u.getFirstname());
					result.put("lastname", u.getLastname());
					result.put("login", u.getLogin());
					result.put("email", u.getAdresses() == null ? "" : u.getAdresses().getEmail());
					result.put("language", u.getLanguage_id()); //TODO rights
				}
			}
		} catch (Exception e) {
			log.error("[loginUser]", e);
		}
		return result;
	}
	
	public List<Map<String, Object>> getVideoStreams() {
		List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();
		// Notify all clients of the same scope (room)
		IConnection current = Red5.getConnectionLocal();
		for (IConnection conn : current.getScope().getClientConnections()) {
			if (conn != null && conn instanceof IServiceCapableConnection) {
				Client c = sessionManager.getClientByStreamId(conn.getClient().getId(), null);
				if (c.getIsAVClient() && !Strings.isEmpty(c.getAvsettings())) {
					Map<String, Object> map = new Hashtable<String, Object>();
					map.put("streamId", c.getStreamid());
					map.put("broadCastId", c.getBroadCastID());
					map.put("userId", c.getUser_id() == null ? "" : c.getUser_id());
					map.put("firstname", c.getFirstname());
					map.put("lastname", c.getLastname());
					map.put("publicSid", c.getPublicSID());
					map.put("login", c.getUsername());
					map.put("email", c.getEmail() == null ? "" : c.getEmail());
					map.put("avsettings", c.getAvsettings());
					result.add(map);
				}
			}
		}
		return result;
	}

	private void addRoom(String type, String org, boolean first, List<Map<String, Object>> result, Room r) {
		Map<String, Object> room = new Hashtable<String, Object>();
		room.put("id", r.getRooms_id());
		room.put("name", r.getName());
		room.put("type", type);
		if (org != null) {
			room.put("org", org);
		}
		room.put("first", first);
		room.put("users", sessionManager.getClientListByRoom(r.getRooms_id()).size());
		room.put("total", r.getNumberOfPartizipants());
		room.put("audioOnly", Boolean.TRUE.equals(r.getIsAudioOnly()));
		result.add(room);
	}
	
	public List<Map<String, Object>> getRooms() {
		List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();
		// FIXME duplicated code
		IConnection current = Red5.getConnectionLocal();
		Client c = sessionManager.getClientByStreamId(current.getClient().getId(), null);
		User u = userDao.get(c.getUser_id());
		//my rooms
		List<Room> myl = new ArrayList<Room>();
		myl.add(roomManager.getRoomByOwnerAndTypeId(u.getUser_id(), 1L, labelDao.getString(1306L, u.getLanguage_id())));
		myl.add(roomManager.getRoomByOwnerAndTypeId(u.getUser_id(), 3L, labelDao.getString(1307L, u.getLanguage_id())));
		myl.addAll(roomDao.getAppointedRoomsByUser(u.getUser_id()));
		for (Room r : myl) {
			addRoom("my", null, false, result, r);
		}
		
		//private rooms
		for (Organisation_Users ou : u.getOrganisation_users()) {
			Organisation org = ou.getOrganisation();
			boolean first = true;
			for (Room r : roomDao.getOrganisationRooms(org.getOrganisation_id())) {
				addRoom("private", org.getName(), first, result, r);
				first = false;
			}
		}
		
		//public rooms
		for (Room r : roomDao.getPublicRooms()) {
			addRoom("public", null, false, result, r);
		}
		return result;
	}
	
	public Map<String, Object> roomConnect(String SID, Long userId) {
		Map<String, Object> result = new Hashtable<String, Object>();
		User u = userDao.get(userId);
		Client c = scopeAdapter.setUsernameReconnect(SID, userId, u.getLogin(), u.getFirstname(), u.getLastname(), u.getPictureuri());
		 //TODO check if we need anything here
		long broadcastId = scopeAdapter.getBroadCastId();
		c.setSipTransport(true);
		c.setRoom_id(Long.parseLong(c.getScope()));
		c.setRoomEnter(new Date());
		c.setBroadCastID(broadcastId);
		c.setIsBroadcasting(true);
		sessionManager.updateClientByStreamId(c.getStreamid(), c, false, null);
		result.put("broadcastId", broadcastId);

		scopeAdapter.syncMessageToCurrentScope("addNewUser", c, false, false);
		return result;
	}

	public Map<String, Object> updateAvMode(String avMode, String width, String height) {
		IConnection current = Red5.getConnectionLocal();
		Client c = sessionManager.getClientByStreamId(current.getClient().getId(), null);
		c.setAvsettings(avMode);
		c.setVWidth(Integer.parseInt(width));
		c.setVHeight(Integer.parseInt(height));
		sessionManager.updateClientByStreamId(c.getStreamid(), c, false, null);
		HashMap<String, Object> hsm = new HashMap<String, Object>();
		hsm.put("client", c);
		hsm.put("message", new String[]{"avsettings", "0", avMode});
		Map<String, Object> result = new Hashtable<String, Object>();
		if (!"n".equals(avMode)) {
			result.put("broadcastId", scopeAdapter.getBroadCastId());
		}

		scopeAdapter.syncMessageToCurrentScope("sendVarsToMessageWithClient", hsm, true, false);
		return result;
	}
}
