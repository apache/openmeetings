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
package org.apache.openmeetings.db.entity.basic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.room.Room.Right;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.wicket.util.collections.ConcurrentHashSet;
import org.apache.wicket.util.string.Strings;

import com.github.openjson.JSONArray;
import com.github.openjson.JSONObject;

/**
 * @author solomax
 *
 */
public class Client implements IClient, IWsClient {
	private static final long serialVersionUID = 1L;

	public enum Activity {
		broadcastA //sends Audio to the room
		, broadcastV //sends Video to the room
		, broadcastAV //sends Audio+Video to the room
		, share
		, record
		, publish //sends A/V to external server
	}
	public enum StreamType {
		webCam //sends Audio/Video to the room
		, screen //send screen sharing
	}
	private final String sessionId;
	private final int pageId;
	private User user;
	private Room room;
	private final String uid;
	private final String sid;
	private String remoteAddress;
	private final Set<Right> rights = new ConcurrentHashSet<>();
	private final Set<Activity> activities = new ConcurrentHashSet<>();
	private final Map<String, StreamDesc> streams = new ConcurrentHashMap<>();
	private final Date connectedSince;
	private int cam = -1;
	private int mic = -1;
	private int width = 0;
	private int height = 0;
	private String serverId = null;
	private Long recordingId;

	public Client(String sessionId, int pageId, Long userId, UserDao dao) {
		this.sessionId = sessionId;
		this.pageId = pageId;
		this.user = dao.get(userId);
		this.connectedSince = new Date();
		uid = UUID.randomUUID().toString();
		sid = UUID.randomUUID().toString();
	}

	@Override
	public String getSessionId() {
		return sessionId;
	}

	@Override
	public int getPageId() {
		return pageId;
	}

	public User getUser() {
		return user;
	}

	public Client updateUser(UserDao dao) {
		user = dao.get(user.getId());
		return this;
	}

	@Override
	public Long getUserId() {
		return user.getId();
	}

	@Override
	public String getLogin() {
		return user.getLogin();
	}

	@Override
	public String getFirstname() {
		return user.getFirstname();
	}

	@Override
	public String getLastname() {
		return user.getLastname();
	}

	@Override
	public String getUid() {
		return uid;
	}

	@Override
	public String getSid() {
		return sid;
	}

	public void clear() {
		activities.clear();
		rights.clear();
		streams.clear();
	}

	public boolean hasRight(Right right) {
		if (Right.superModerator == right) {
			return rights.contains(right);
		}
		return rights.contains(Right.superModerator) || rights.contains(Right.moderator) || rights.contains(right);
	}

	public Client allow(Right... _rights) {
		allow(Arrays.asList(_rights));
		return this;
	}

	public void allow(Iterable<Right> _rights) {
		for (Right right : _rights) {
			if (!hasRight(right)) {
				rights.add(right);
			}
		}
	}

	public void deny(Right... _rights) {
		for (Right right : _rights) {
			rights.remove(right);
		}
	}

	public void clearActivities() {
		activities.clear();
	}

	public boolean hasAnyActivity(Activity... aa) {
		boolean res = false;
		if (aa != null) {
			for (Activity a : aa) {
				res |= activities.contains(a);
			}
		}
		return res;
	}

	public boolean hasActivity(Activity a) {
		return activities.contains(a);
	}

	public Client toggle(Activity a) {
		if (hasActivity(a)) {
			remove(a);
		} else {
			set(a);
		}
		return this;
	}

	public Client set(Activity a) {
		activities.add(a);
		switch (a) {
			case broadcastV:
			case broadcastA:
				if (hasActivity(Activity.broadcastA) && hasActivity(Activity.broadcastV)) {
					activities.add(Activity.broadcastAV);
				}
				break;
			case broadcastAV:
				activities.add(Activity.broadcastA);
				activities.add(Activity.broadcastV);
				break;
			default:
		}
		return this;
	}

	public Client remove(Activity a) {
		activities.remove(a);
		switch (a) {
			case broadcastV:
			case broadcastA:
				activities.remove(Activity.broadcastAV);
				break;
			case broadcastAV:
				activities.remove(Activity.broadcastA);
				activities.remove(Activity.broadcastV);
				break;
			default:
		}
		return this;
	}

	public StreamDesc addStream(StreamType stype) {
		StreamDesc sd = new StreamDesc(stype);
		streams.put(sd.getUid(), sd);
		return sd;
	}

	public Client removeStream(String _uid) {
		streams.remove(_uid);
		return this;
	}

	public List<StreamDesc> getStreams() {
		return new ArrayList<>(streams.values());
	}

	public StreamDesc getStream(String _uid) {
		return streams.get(_uid);
	}

	public Date getConnectedSince() {
		return connectedSince;
	}

	@Override
	public Long getId() {
		return null;
	}

	@Override
	public void setId(Long id) {
		//no-op
	}

	public Room getRoom() {
		return room;
	}

	public Client setRoom(Room room) {
		this.room = room;
		return this;
	}

	public boolean isCamEnabled() {
		return cam > -1;
	}

	public int getCam() {
		return cam;
	}

	public Client setCam(int cam) {
		this.cam = cam;
		return this;
	}

	public boolean isMicEnabled() {
		return mic > -1;
	}

	public int getMic() {
		return mic;
	}

	public Client setMic(int mic) {
		this.mic = mic;
		return this;
	}

	@Override
	public int getWidth() {
		return width;
	}

	public Client setWidth(int width) {
		this.width = width;
		return this;
	}

	@Override
	public int getHeight() {
		return height;
	}

	public Client setHeight(int height) {
		this.height = height;
		return this;
	}

	@Override
	public String getRemoteAddress() {
		return remoteAddress;
	}

	public Client setRemoteAddress(String remoteAddress) {
		this.remoteAddress = remoteAddress;
		return this;
	}

	@Override
	public String getServerId() {
		return serverId;
	}

	public void setServerId(String serverId) {
		this.serverId = serverId;
	}

	@Override
	public void setRecordingStarted(boolean recordingStarted) {
		if (recordingStarted) {
			activities.add(Activity.record);
		} else {
			activities.remove(Activity.record);
		}
	}

	@Override
	public Long getRecordingId() {
		return recordingId;
	}

	@Override
	public void setRecordingId(Long recordingId) {
		this.recordingId = recordingId;
	}

	@Override
	public Long getRoomId() {
		return room == null ? null : room.getId();
	}

	@Override
	public Room.Type getRoomType() {
		return room == null ? null : room.getType();
	}

	public JSONObject toJson(boolean self) {
		JSONObject u = new JSONObject();
		if (user != null) {
			JSONObject a = new JSONObject();
			u.put("id", user.getId())
				.put("firstName", user.getFirstname())
				.put("lastName", user.getLastname())
				.put("address", a);
			if (user.getAddress() != null) {
				if (Strings.isEmpty(user.getFirstname()) && Strings.isEmpty(user.getLastname())) {
					a.put("email", user.getAddress().getEmail());
				}
				a.put("country", user.getAddress().getCountry());
			}
		}
		JSONArray streamArr = new JSONArray();
		for (Entry<String, StreamDesc> e : streams.entrySet()) {
			streamArr.put(e.getValue().toJson());
		}
		JSONObject json = new JSONObject()
				.put("user", u)
				.put("cuid", uid)
				.put("uid", uid)
				.put("rights", new JSONArray(rights))
				.put("sactivities", new JSONArray(activities))
				.put("streams", streamArr)
				.put("width", width)
				.put("height", height)
				.put("self", self);
		if (self) {
			json.put("cam", cam).put("mic", mic);
		}
		return json;
	}

	public void merge(Client c) {
		user = c.user;
		room = c.room;
		Set<Right> rr = new HashSet<>(c.rights);
		synchronized (rights) {
			rights.clear();
			rights.addAll(rr);
		}
		Set<Activity> aa = new HashSet<>(c.activities);
		synchronized (activities) {
			activities.clear();
			activities.addAll(aa);
		}
		Map<String, StreamDesc> ss = new HashMap<>(c.streams);
		synchronized (streams) {
			streams.clear();
			streams.putAll(ss);
		}
		cam = c.cam;
		mic = c.mic;
		width = c.width;
		height = c.height;
		recordingId = c.recordingId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uid == null) ? 0 : uid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Client)) {
			return false;
		}
		Client other = (Client) obj;
		if (uid == null) {
			if (other.uid != null) {
				return false;
			}
		} else if (!uid.equals(other.uid)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Client [uid=" + uid + ", sessionId=" + sessionId + ", pageId=" + pageId + ", userId=" + user.getId() + ", room=" + (room == null ? null : room.getId())
				+ ", rights=" + rights + ", sactivities=" + activities + ", connectedSince=" + connectedSince + "]";
	}

	public class StreamDesc implements Serializable {
		private static final long serialVersionUID = 1L;
		private final Set<Activity> sactivities = new ConcurrentHashSet<>();
		private final String uuid;
		private final StreamType type;
		private int swidth;
		private int sheight;

		public StreamDesc(StreamType type) {
			this.uuid = UUID.randomUUID().toString();
			this.type = type;
			setActivities();
			if (StreamType.webCam == type) {
				this.swidth = width;
				this.sheight = height;
			}
		}

		public String getSid() {
			return sid;
		}

		public String getUid() {
			return uuid;
		}

		public StreamType getType() {
			return type;
		}

		public int getWidth() {
			return swidth;
		}

		public StreamDesc setWidth(int width) {
			this.swidth = width;
			return this;
		}

		public int getHeight() {
			return sheight;
		}

		public StreamDesc setHeight(int height) {
			this.sheight = height;
			return this;
		}

		public StreamDesc setActivities() {
			sactivities.clear();
			if (StreamType.webCam == type) {
				if (Client.this.hasActivity(Activity.broadcastA)) {
					sactivities.add(Activity.broadcastA);
				}
				if (Client.this.hasActivity(Activity.broadcastV)) {
					sactivities.add(Activity.broadcastV);
				}
			}
			if (StreamType.screen == type) {
				sactivities.add(Activity.broadcastV);
			}
			return this;
		}

		public boolean hasActivity(Activity a) {
			return sactivities.contains(a);
		}

		public Client getClient() {
			return Client.this;
		}

		private JSONArray getActivities() {
			return new JSONArray(new ArrayList<>(sactivities));
		}

		public JSONObject toJson() {
			return new JSONObject()
					.put("uid", uuid)
					.put("type", type.name())
					.put("width", swidth)
					.put("height", sheight)
					.put("activities", getActivities())
					.put("cuid", uid)
					.put("user", new JSONObject()
							.put("id", user.getId())
							.put("firstName", user.getFirstname())
							.put("lastName", user.getLastname())
							);
		}
	}
}
