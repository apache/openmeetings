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

import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DEFAULT_GROUP_ID;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_FRONTEND_REGISTER_KEY;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_OAUTH_REGISTER_KEY;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_SOAP_REGISTER_KEY;
import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import org.apache.openmeetings.core.remote.red5.ScopeApplicationAdapter;
import org.apache.openmeetings.core.remote.util.SessionVariablesUtil;
import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dao.label.LabelDao;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.dao.server.ISessionManager;
import org.apache.openmeetings.db.dao.server.SessiondataDao;
import org.apache.openmeetings.db.dao.user.IUserManager;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.room.Client;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.server.Sessiondata;
import org.apache.openmeetings.db.entity.user.Group;
import org.apache.openmeetings.db.entity.user.GroupUser;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.util.OmException;
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
	private LabelDao labelDao;
	@Autowired
	private ScopeApplicationAdapter scopeAdapter;

	private static void add(Map<String, Object> m, String key, Object v) {
		m.put(key, v == null ? "" : v);
	}
	
	public Map<String, Object> checkServer() {
		Map<String, Object> result = new Hashtable<>();
		result.put("allowSelfRegister",  "1".equals(cfgDao.getConfValue(CONFIG_FRONTEND_REGISTER_KEY, String.class, "0")));
		result.put("allowSoapRegister",  "1".equals(cfgDao.getConfValue(CONFIG_SOAP_REGISTER_KEY, String.class, "0")));
		result.put("allowOauthRegister",  "1".equals(cfgDao.getConfValue(CONFIG_OAUTH_REGISTER_KEY, String.class, "0")));
		return result;
	}
	
	public Map<String, String> getStates() {
		Map<String, String> result = new Hashtable<>();
		for (String code : Locale.getISOCountries()) {
			result.put(code, new Locale.Builder().setRegion(code).build().getDisplayCountry());
		}
		return result;
	}
	
	public String[] getTimezones() {
		return TimeZone.getAvailableIDs();
	}
	
	public Map<String, Object> loginGoogle(Map<String, String> umap) {
		Map<String, Object> result = getResult();
		try {
			if ("1".equals(cfgDao.getConfValue(CONFIG_OAUTH_REGISTER_KEY, String.class, "0"))) {
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
			if ("1".equals(cfgDao.getConfValue(CONFIG_FRONTEND_REGISTER_KEY, String.class, "0"))) {
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
						&& 1 == cfgDao.getConfValue("sendEmailWithVerficationCode", Integer.class, "0");
				Long userId = userManager.registerUserInit(UserDao.getDefaultRights(), login, password, lastname
						, firstname, email, null /* age/birthday */, "" /* street */
						, "" /* additionalname */, "" /* fax */, "" /* zip */, country
						, "" /* town */, langId, true /* sendWelcomeMessage */
						, Arrays.asList(cfgDao.getConfValue(CONFIG_DEFAULT_GROUP_ID, Long.class, null)),
						"" /* phone */, false, sendConfirmation, TimeZone.getTimeZone(tzId),
						false /* forceTimeZoneCheck */, "" /* userOffers */, "" /* userSearchs */, false /* showContactData */,
						true /* showContactDataToContacts */, hash);
				if (userId == null) {
					//do nothing
				} else if (userId > 0) {
					User u = userDao.get(userId);
					if (sendConfirmation) {
						add(result, "status", -666L);
					} else {
						result = login(u, result);
					}
				} else {
					add(result, "status", userId);
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
			result.put("status", e.getCode());
		} catch (Exception e) {
			log.error("[loginUser]", e);
		}
		return result;
	}
	
	private static Map<String, Object> getResult() {
		Map<String, Object> result = new Hashtable<>();
		result.put("status", -1);
		return result;
	}
	
	private Map<String, Object> login(User u, Map<String, Object> result) {
		if (u != null) {
			Sessiondata sd = sessionDao.create(u.getId(), u.getLanguageId());
			IConnection conn = Red5.getConnectionLocal();
			String streamId = conn.getClient().getId();
			Client c = sessionManager.getClientByStreamId(streamId, null);
			if (c == null) {
				c = sessionManager.addClientListItem(streamId, conn.getScope().getName(), conn.getRemotePort(),
					conn.getRemoteAddress(), "", null);
			}
			if (c == null) {
				// Failed to create client
				result.put("status", -1);
			} else {
				SessionVariablesUtil.initClient(conn.getClient(), c.getPublicSID());
				c.setUserId(u.getId());
				c.setFirstname(u.getFirstname());
				c.setLastname(u.getLastname());
				c.setMobile(true);
				sessionManager.updateClientByStreamId(streamId, c, false, null);

				add(result, "sid", sd.getSessionId());
				add(result, "publicSid", c.getPublicSID());
				add(result, "status", 0);
				add(result, "userId", u.getId());
				add(result, "firstname", u.getFirstname());
				add(result, "lastname", u.getLastname());
				add(result, "login", u.getLogin());
				add(result, "email", u.getAddress() == null ? "" : u.getAddress().getEmail());
				add(result, "language", u.getLanguageId()); //TODO rights
			}
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
				if (!Strings.isEmpty(c.getAvsettings()) && !c.isScreenClient()) {
					Map<String, Object> map = new Hashtable<String, Object>();
					add(map, "streamId", c.getStreamid());
					add(map, "broadCastId", c.getBroadCastID());
					add(map, "userId", c.getUserId());
					add(map, "firstname", c.getFirstname());
					add(map, "lastname", c.getLastname());
					add(map, "publicSid", c.getPublicSID());
					add(map, "login", c.getUsername());
					add(map, "email", c.getEmail());
					add(map, "avsettings", c.getAvsettings());
					add(map, "interviewPodId", c.getInterviewPodId());
					add(map, "vWidth", c.getVWidth());
					add(map, "vHeight", c.getVHeight());
					result.add(map);
				}
			}
		}
		return result;
	}

	private void addRoom(String type, String org, boolean first, List<Map<String, Object>> result, Room r) {
		Map<String, Object> room = new Hashtable<String, Object>();
		room.put("id", r.getId());
		room.put("name", r.getName());
		room.put("type", type);
		room.put("roomTypeId", r.getType().getId());
		if (org != null) {
			room.put("org", org);
		}
		room.put("first", first);
		room.put("users", sessionManager.getClientListByRoom(r.getId()).size());
		room.put("total", r.getNumberOfPartizipants());
		room.put("audioOnly", r.isAudioOnly());
		result.add(room);
	}
	
	public List<Map<String, Object>> getRooms() {
		List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();
		// FIXME duplicated code
		IConnection current = Red5.getConnectionLocal();
		Client c = sessionManager.getClientByStreamId(current.getClient().getId(), null);
		User u = userDao.get(c.getUserId());
		//my rooms
		List<Room> myl = new ArrayList<Room>();
		myl.add(roomDao.getUserRoom(u.getId(), Room.Type.conference, labelDao.getString(1306L, u.getLanguageId())));
		myl.add(roomDao.getUserRoom(u.getId(), Room.Type.restricted, labelDao.getString(1307L, u.getLanguageId())));
		myl.addAll(roomDao.getAppointedRoomsByUser(u.getId()));
		for (Room r : myl) {
			addRoom("my", null, false, result, r);
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
	
	public Map<String, Object> roomConnect(String SID, Long userId) {
		Map<String, Object> result = new Hashtable<String, Object>();
		User u = userDao.get(userId);
		Client c = scopeAdapter.setUsernameReconnect(SID, userId, u.getLogin(), u.getFirstname(), u.getLastname(), u.getPictureuri());
		 //TODO check if we need anything here
		long broadcastId = scopeAdapter.getBroadCastId();
		c.setSipTransport(true);
		c.setRoomId(Long.parseLong(c.getScope()));
		c.setRoomEnter(new Date());
		c.setBroadCastID(broadcastId);
		c.setMobile(true);
		c.setIsBroadcasting(true);
		sessionManager.updateClientByStreamId(c.getStreamid(), c, false, null);
		result.put("broadcastId", broadcastId);
		result.put("publicSid", c.getPublicSID());

		scopeAdapter.sendMessageToCurrentScope("addNewUser", c, false, false);
		return result;
	}

	public Map<String, Object> updateAvMode(String avMode, String width, String height, Integer interviewPodId) {
		IConnection current = Red5.getConnectionLocal();
		Client c = sessionManager.getClientByStreamId(current.getClient().getId(), null);
		c.setAvsettings(avMode);
		c.setVWidth(Integer.parseInt(width));
		c.setVHeight(Integer.parseInt(height));
		if (interviewPodId > 0) {
			c.setInterviewPodId(interviewPodId);
		}
		sessionManager.updateClientByStreamId(c.getStreamid(), c, false, null);
		HashMap<String, Object> hsm = new HashMap<String, Object>();
		hsm.put("client", c);
		hsm.put("message", new String[]{"avsettings", "0", avMode});
		Map<String, Object> result = new Hashtable<String, Object>();
		if (!"n".equals(avMode)) {
			result.put("broadcastId", scopeAdapter.getBroadCastId());
		}

		scopeAdapter.sendMessageToCurrentScope("sendVarsToMessageWithClient", hsm, true, false);
		return result;
	}
}
