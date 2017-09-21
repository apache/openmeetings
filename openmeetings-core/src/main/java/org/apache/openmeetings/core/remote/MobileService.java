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
package org.apache.openmeetings.core.remote;

import static org.apache.openmeetings.db.util.LocaleHelper.getCountryName;
import static org.apache.openmeetings.util.OmException.UNKNOWN;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DEFAULT_GROUP_ID;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_EMAIL_VERIFICATION;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_MYROOMS_ENABLED;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_REGISTER_FRONTEND;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_REGISTER_OAUTH;
import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;
import static org.apache.openmeetings.util.Version.getVersion;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.openmeetings.core.remote.ScopeApplicationAdapter.MessageSender;
import org.apache.openmeetings.core.util.IClientUtil;
import org.apache.openmeetings.core.util.WebSocketHelper;
import org.apache.openmeetings.db.dao.basic.ChatDao;
import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dao.label.LabelDao;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.dao.server.ISessionManager;
import org.apache.openmeetings.db.dao.server.SessiondataDao;
import org.apache.openmeetings.db.dao.user.IUserManager;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.basic.ChatMessage;
import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.room.StreamClient;
import org.apache.openmeetings.db.entity.server.Sessiondata;
import org.apache.openmeetings.db.entity.user.Group;
import org.apache.openmeetings.db.entity.user.GroupUser;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.util.LocaleHelper;
import org.apache.openmeetings.util.OmException;
import org.apache.wicket.util.string.Strings;
import org.red5.logging.Red5LoggerFactory;
import org.red5.server.api.IConnection;
import org.red5.server.api.Red5;
import org.red5.server.api.service.IServiceCapableConnection;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("mobile.service")
public class MobileService {
	private static final Logger log = Red5LoggerFactory.getLogger(MainService.class, webAppRootKey);
	@Autowired
	private ConfigurationDao cfgDao;
	@Autowired
	private UserDao userDao;
	@Autowired
	private IUserManager userManager;
	@Autowired
	private SessiondataDao sessionDao;
	@Autowired
	private ISessionManager sessionManager;
	@Autowired
	private RoomDao roomDao;
	@Autowired
	private ChatDao chatDao;
	@Autowired
	private ScopeApplicationAdapter scopeAdapter;

	private static void add(Map<String, Object> m, String key, Object v) {
		m.put(key, v == null ? "" : v);
	}

	public String refreshSession(String sid) {
		sessionDao.check(sid);
		return "ok";
	}

	/**
	 * @return - List of all available Languages
	 */
	public List<Language> getLanguages() {
		List<Language> result = new ArrayList<>();
		for (Map.Entry<Long, Locale> e : LabelDao.languages.entrySet()) {
			result.add(new Language(e.getKey(), e.getValue().toLanguageTag(), e.getValue().getDisplayName(Locale.ENGLISH)));
		}
		return result;
	}

	public long switchMicMuted(String publicSID, boolean mute) {
		return scopeAdapter.switchMicMuted(publicSID, mute);
	}

	public Map<String, Object> checkServer() {
		Map<String, Object> result = new HashMap<>();
		result.put("allowSelfRegister",  cfgDao.getBool(CONFIG_REGISTER_FRONTEND, false));
		result.put("allowOauthRegister",  cfgDao.getBool(CONFIG_REGISTER_OAUTH, false));
		return result;
	}

	public Map<String, String> getStates() {
		Map<String, String> result = new HashMap<>();
		for (String code : Locale.getISOCountries()) {
			result.put(code, getCountryName(code));
		}
		return result;
	}

	public String[] getTimezones() {
		return TimeZone.getAvailableIDs();
	}

	public Map<String, Object> loginGoogle(Map<String, String> umap) {
		Map<String, Object> result = getResult();
		try {
			if (cfgDao.getBool(CONFIG_REGISTER_OAUTH, false)) {
				User u = userManager.loginOAuth(umap, 2); //TODO hardcoded
				result = login(u, result);
			}
		} catch (Exception e) {
			log.error("[loginGoogle]", e);
		}
		return result;
	}

	public Map<String, Object> registerUser(Map<String, String> umap) {
		Map<String, Object> result = getResult();
		try {
			if (cfgDao.getBool(CONFIG_REGISTER_FRONTEND, false)) {
				String login = umap.get("login");
				String email = umap.get("email");
				String lastname = umap.get("lastname");
				String firstname = umap.get("firstname");
				if (firstname == null) {
					firstname = "";
				}
				if (lastname == null) {
					lastname = "";
				}
				String password = umap.get("password");
				String tzId = umap.get("tzId");
				String country = umap.get("stateId");
				Long langId = Long.valueOf(umap.get("langId"));

				//FIXME TODO unify with Register dialog
				String hash = UUID.randomUUID().toString();

				String baseURL = cfgDao.getBaseUrl();
				boolean sendConfirmation = !Strings.isEmpty(baseURL)
						&& cfgDao.getBool(CONFIG_EMAIL_VERIFICATION, false);
				Object user = userManager.registerUserInit(UserDao.getDefaultRights(), login, password, lastname
						, firstname, email, null /* age/birthday */, "" /* street */
						, "" /* additionalname */, "" /* fax */, "" /* zip */, country
						, "" /* town */, langId, true /* sendWelcomeMessage */
						, Arrays.asList(cfgDao.getLong(CONFIG_DEFAULT_GROUP_ID, null)),
						"" /* phone */, false, sendConfirmation, TimeZone.getTimeZone(tzId),
						false /* forceTimeZoneCheck */, "" /* userOffers */, "" /* userSearchs */, false /* showContactData */,
						true /* showContactDataToContacts */, hash);
				if (user == null) {
					//do nothing
				} else if (user instanceof User) {
					User u = (User)user;
					if (sendConfirmation) {
						add(result, "status", -666L);
					} else {
						result = login(u, result);
					}
				} else {
					add(result, "status", user);
				}
			}
		} catch (Exception e) {
			log.error("[registerUser]", e);
		}
		return result;
	}

	public Map<String, Object> loginUser(String login, String password) {
		Map<String, Object> result = getResult();
		try {
			User u = userDao.login(login, password);
			result = login(u, result);
		} catch (OmException e) {
			result.put("status", e.getKey());
		} catch (Exception e) {
			log.error("[loginUser]", e);
		}
		return result;
	}

	private static Map<String, Object> getResult() {
		Map<String, Object> result = new HashMap<>();
		result.put("status", UNKNOWN.getKey());
		return result;
	}

	public StreamClient create(User u, Sessiondata sd) {
		StreamClient c = new StreamClient();
		c.setType(StreamClient.Type.mobile);
		c.setSid(sd.getSessionId());
		c.setUid(UUID.randomUUID().toString());
		return create(c, u);
	}

	public StreamClient create(StreamClient c, User u) {
		c.setUserId(u.getId());
		c.setFirstname(u.getFirstname());
		c.setLastname(u.getLastname());
		if (c.getUserId() != null) {
			c.setLogin(u.getLogin());
			c.setFirstname(u.getFirstname());
			c.setLastname(u.getLastname());
			c.setEmail(u.getAddress() == null ? null : u.getAddress().getEmail());
		}
		c.setBroadcastId(UUID.randomUUID().toString());
		return c;
	}

	private Map<String, Object> login(User u, Map<String, Object> result) {
		if (u != null) {
			IConnection conn = Red5.getConnectionLocal();
			Sessiondata sd = sessionDao.create(u.getId(), u.getLanguageId());
			StreamClient c = create(u, sd);
			c.setScope(conn.getScope().getName());
			sessionManager.add(c);
			IClientUtil.init(conn.getClient(), c.getUid(), false);

			add(result, "sid", sd.getSessionId());
			add(result, "publicSid", c.getUid());
			add(result, "status", 0);
			add(result, "userId", u.getId());
			add(result, "firstname", u.getFirstname());
			add(result, "lastname", u.getLastname());
			add(result, "login", u.getLogin());
			add(result, "email", u.getAddress() == null ? "" : u.getAddress().getEmail());
			add(result, "language", u.getLanguageId());
			add(result, "version", getVersion());
		}
		return result;
	}

	public List<Map<String, Object>> getVideoStreams() {
		List<Map<String, Object>> result = new ArrayList<>();
		// Notify all clients of the same scope (room)
		IConnection current = Red5.getConnectionLocal();
		for (IConnection conn : current.getScope().getClientConnections()) {
			if (conn != null && conn instanceof IServiceCapableConnection) {
				StreamClient c = sessionManager.get(IClientUtil.getId(conn.getClient()));
				if (!Strings.isEmpty(c.getAvsettings()) && Client.Type.sharing != c.getType()) {
					//TODO duplicates !!!!!!!!!!!!!!
					Map<String, Object> map = new HashMap<>();
					add(map, "streamId", c.getId());
					add(map, "broadCastId", c.getBroadcastId());
					add(map, "userId", c.getUserId());
					add(map, "firstname", c.getFirstname());
					add(map, "lastname", c.getLastname());
					add(map, "publicSid", c.getUid());
					add(map, "login", c.getLogin());
					add(map, "email", c.getEmail());
					add(map, "avsettings", c.getAvsettings());
					add(map, "interviewPodId", c.getInterviewPodId());
					add(map, "vWidth", c.getWidth());
					add(map, "vHeight", c.getHeight());
					result.add(map);
				}
			}
		}
		return result;
	}

	private void addRoom(String type, String org, boolean first, List<Map<String, Object>> result, Room r) {
		Map<String, Object> room = new HashMap<>();
		room.put("id", r.getId());
		room.put("name", r.getName());
		room.put("type", type);
		room.put("roomTypeId", r.getType().getId());
		if (org != null) {
			room.put("org", org);
		}
		room.put("first", first);
		room.put("users", sessionManager.listByRoom(r.getId()).size());
		room.put("total", r.getCapacity());
		room.put("audioOnly", r.isAudioOnly());
		result.add(room);
	}

	public List<Map<String, Object>> getRooms() {
		List<Map<String, Object>> result = new ArrayList<>();
		// FIXME duplicated code
		IConnection current = Red5.getConnectionLocal();
		StreamClient c = sessionManager.get(IClientUtil.getId(current.getClient()));
		User u = userDao.get(c.getUserId());
		if (cfgDao.getBool(CONFIG_MYROOMS_ENABLED, true)) {
			//my rooms
			List<Room> myl = new ArrayList<>();
			myl.add(roomDao.getUserRoom(u.getId(), Room.Type.conference, LabelDao.getString("my.room.conference", u.getLanguageId())));
			myl.add(roomDao.getUserRoom(u.getId(), Room.Type.presentation, LabelDao.getString("my.room.presentation", u.getLanguageId())));
			myl.addAll(roomDao.getAppointedRoomsByUser(u.getId()));
			for (Room r : myl) {
				addRoom("my", null, false, result, r);
			}
		}

		//private rooms
		for (GroupUser ou : u.getGroupUsers()) {
			Group org = ou.getGroup();
			boolean first = true;
			for (Room r : roomDao.getGroupRooms(org.getId())) {
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

	/**
	 * designed to do nothing remain for compatibility
	 *
	 * @param SID - sid
	 * @param userId - redundant userId
	 */
	public Map<String, Object> roomConnect(String SID, Long userId) {
		// publicSid is changed on mobile room connect
		IConnection current = Red5.getConnectionLocal();
		StreamClient c = sessionManager.get(IClientUtil.getId(current.getClient()));
		Map<String, Object> result = new HashMap<>();
		result.put("publicSid", c.getUid());
		result.put("broadCastId", c.getBroadcastId());
		return result;
	}

	public Map<String, Object> updateAvMode(String avMode, String width, String height, Integer interviewPodId) {
		IConnection current = Red5.getConnectionLocal();
		StreamClient c = sessionManager.get(IClientUtil.getId(current.getClient()));
		c.setAvsettings(avMode);
		if (!"n".equals(avMode)) {
			c.setBroadcastId(UUID.randomUUID().toString());
			c.setBroadcasting(true);
		}
		c.setWidth(Double.valueOf(width).intValue());
		c.setHeight(Double.valueOf(height).intValue());
		if (interviewPodId > 0) {
			c.setInterviewPodId(interviewPodId);
		}
		sessionManager.update(c);
		Map<String, Object> hsm = new HashMap<>();
		hsm.put("client", c);
		hsm.put("message", new String[]{"avsettings", "0", avMode});
		Map<String, Object> result = new HashMap<>();
		if (!"n".equals(avMode)) {
			result.put("broadcastId", c.getBroadcastId());
		}

		scopeAdapter.sendMessageToCurrentScope("sendVarsToMessageWithClient", hsm, true, false);
		return result;
	}

	public void sendChatMessage(String msg) {
		IConnection current = Red5.getConnectionLocal();
		StreamClient c = sessionManager.get(IClientUtil.getId(current.getClient()));

		ChatMessage m = new ChatMessage();
		m.setMessage(msg);
		m.setSent(new Date());
		User u = userDao.get(c.getUserId());
		m.setFromUser(u);
		Room r = roomDao.get(c.getRoomId());
		m.setToRoom(r);
		m.setNeedModeration(r.isChatModerated() && !isModerator(c));
		chatDao.update(m);
		FastDateFormat fmt = getFmt(u);
		sendChatMessage(c, m, fmt);
		WebSocketHelper.sendRoom(m, WebSocketHelper.getMessage(u.getId(), Arrays.asList(m), fmt, null));
	}

	public void sendChatMessage(String uid, ChatMessage m, FastDateFormat fmt) {
		sendChatMessage(sessionManager.get(uid), m, fmt);
	}

	public void sendChatMessage(StreamClient c, ChatMessage m, FastDateFormat fmt) {
		if (c == null) {
			return;
		}
		Map<String, Object> hsm = new HashMap<>();
		hsm.put("client", c);
		hsm.put("message", Arrays.asList("chat", encodeChatMessage(m, fmt)));

		final Long roomId = c.getRoomId();
		//Sync to all users of current scope
		new MessageSender(scopeAdapter.getChildScope("" + roomId), "sendVarsToMessageWithClient", hsm, scopeAdapter) {
			@Override
			public boolean filter(IConnection conn) {
				StreamClient rcl = sessionManager.get(IClientUtil.getId(conn.getClient()));
				return Client.Type.sharing == rcl.getType()
						|| rcl.getRoomId() == null || !rcl.getRoomId().equals(roomId);
			}
		}.start();
	}

	private static boolean isModerator(StreamClient c) {
		return c.isMod() || c.isSuperMod();
	}

	private static FastDateFormat getFmt(User u) {
		return FastDateFormat.getDateTimeInstance(
				FastDateFormat.SHORT
				, FastDateFormat.SHORT
				, TimeZone.getTimeZone(u.getTimeZoneId())
				, LocaleHelper.getLocale(u));
	}

	private static Map<String, Object> encodeChatMessage(ChatMessage m, FastDateFormat fmt) {
		Map<String, Object> mm = new HashMap<>();
		mm.put("from", String.format("%s %s", m.getFromUser().getFirstname(), m.getFromUser().getLastname()));
		mm.put("time", fmt.format(m.getSent()));
		mm.put("msg", m.getMessage());
		return mm;
	}

	public List<Map<String, Object>> getRoomChatHistory() {
		List<Map<String,Object>> myChatList = new ArrayList<>();
		try {
			IConnection current = Red5.getConnectionLocal();
			StreamClient c = sessionManager.get(IClientUtil.getId(current.getClient()));
			Long roomId = c.getRoomId();

			log.debug("GET CHATROOM: " + roomId);

			Room r = roomDao.get(roomId);
			User u = userDao.get(c.getUserId());
			FastDateFormat fmt = getFmt(u);
			for (ChatMessage m : chatDao.getRoom(roomId, 0, 30, !r.isChatModerated() || isModerator(c))) {
				myChatList.add(encodeChatMessage(m, fmt));
			}

		} catch (Exception err) {
			log.error("[getRoomChatHistory] ",err);
		}
		return myChatList;
	}

	public static class Language implements Serializable {
		private static final long serialVersionUID = 1L;
		public long language_id;
		public String code;
		public String name;

		public Language() {}

		public Language(long language_id, String code, String name) {
			this.language_id = language_id;
			this.code = code;
			this.name = name;
		}
	}
}
