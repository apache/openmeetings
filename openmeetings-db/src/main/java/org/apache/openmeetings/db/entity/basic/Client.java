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

import static java.util.UUID.randomUUID;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.IDataProviderEntity;
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
public class Client implements IDataProviderEntity, IWsClient {
	private static final long serialVersionUID = 1L;

	public enum Activity {
		AUDIO //sends Audio to the room
		, VIDEO //sends Video to the room
		, AUDIO_VIDEO //sends Audio+Video to the room
		, SCREEN //screen is shared
		, RECORD //record in non-interview room
	}
	public enum StreamType {
		WEBCAM //sends Audio/Video to the room
		, SCREEN //send screen sharing
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
	private final String pictureUri;

	public Client(String sessionId, int pageId, User u, String pictureUri) {
		this.sessionId = sessionId;
		this.pageId = pageId;
		this.user = u;
		this.connectedSince = new Date();
		this.pictureUri = pictureUri;
		uid = randomUUID().toString();
		sid = randomUUID().toString();
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

	public Long getUserId() {
		return user.getId();
	}

	public String getPictureUri() {
		return pictureUri;
	}

	@Override
	public String getUid() {
		return uid;
	}

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
			case VIDEO:
			case AUDIO:
				if (hasActivity(Activity.AUDIO) && hasActivity(Activity.VIDEO)) {
					activities.add(Activity.AUDIO_VIDEO);
				}
				break;
			case AUDIO_VIDEO:
				activities.add(Activity.AUDIO);
				activities.add(Activity.VIDEO);
				break;
			default:
		}
		return this;
	}

	public Client remove(Activity a) {
		activities.remove(a);
		switch (a) {
			case VIDEO:
			case AUDIO:
				activities.remove(Activity.AUDIO_VIDEO);
				break;
			case AUDIO_VIDEO:
				activities.remove(Activity.AUDIO);
				activities.remove(Activity.VIDEO);
				break;
			default:
		}
		return this;
	}

	public StreamDesc addStream(StreamType stype, Activity...activities) {
		StreamDesc sd = new StreamDesc(stype, activities);
		streams.put(sd.getUid(), sd);
		return sd;
	}

	public StreamDesc removeStream(String _uid) {
		return streams.remove(_uid);
	}

	public List<StreamDesc> getStreams() {
		return new ArrayList<>(streams.values());
	}

	public StreamDesc getStream(String _uid) {
		return streams.get(_uid);
	}

	public Optional<StreamDesc> getScreenStream() {
		return streams.values().stream()
				.filter(sd -> StreamType.SCREEN == sd.getType())
				.findFirst();
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

	public int getWidth() {
		return width;
	}

	public Client setWidth(int width) {
		this.width = width;
		return this;
	}

	public int getHeight() {
		return height;
	}

	public Client setHeight(int height) {
		this.height = height;
		return this;
	}

	public String getRemoteAddress() {
		return remoteAddress;
	}

	public Client setRemoteAddress(String remoteAddress) {
		this.remoteAddress = remoteAddress;
		return this;
	}

	public String getServerId() {
		return serverId;
	}

	public void setServerId(String serverId) {
		this.serverId = serverId;
	}

	public Long getRoomId() {
		return room == null ? null : room.getId();
	}

	private JSONObject addUserJson(JSONObject o) {
		JSONObject u = new JSONObject();
		if (user != null) {
			JSONObject a = new JSONObject();
			u.put("id", user.getId())
				.put("firstName", user.getFirstname())
				.put("lastName", user.getLastname())
				.put("displayName", user.getDisplayName())
				.put("address", a)
				.put("pictureUri", pictureUri);
			if (user.getAddress() != null) {
				if (Strings.isEmpty(user.getFirstname()) && Strings.isEmpty(user.getLastname())) {
					a.put("email", user.getAddress().getEmail());
				}
				a.put("country", user.getAddress().getCountry());
			}
		}
		return o.put("user", u);
	}

	public JSONObject toJson(boolean self) {
		JSONArray streamArr = new JSONArray();
		for (Entry<String, StreamDesc> e : streams.entrySet()) {
			streamArr.put(e.getValue().toJson());
		}
		JSONObject json = new JSONObject()
				.put("cuid", uid)
				.put("uid", uid)
				.put("rights", new JSONArray(rights))
				.put("activities", new JSONArray(activities))
				.put("streams", streamArr)
				.put("width", width)
				.put("height", height)
				.put("self", self);
		if (self) {
			json.put("cam", cam).put("mic", mic);
		}
		return addUserJson(json);
	}

	public void merge(Client c) {
		user = c.user;
		room = c.room;
		synchronized (rights) {
			Set<Right> rr = new HashSet<>(c.rights);
			rights.clear();
			rights.addAll(rr);
		}
		synchronized (activities) {
			Set<Activity> aa = new HashSet<>(c.activities);
			activities.clear();
			activities.addAll(aa);
		}
		synchronized (streams) {
			Map<String, StreamDesc> ss = new HashMap<>(c.streams);
			streams.clear();
			for (Entry<String, StreamDesc> e : ss.entrySet()) {
				streams.put(e.getKey(), new StreamDesc(e.getValue()));
			}
		}
		cam = c.cam;
		mic = c.mic;
		width = c.width;
		height = c.height;
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

		public StreamDesc(StreamDesc sd) {
			this.uuid = sd.uuid;
			this.type = sd.type;
			this.swidth = sd.swidth;
			this.sheight = sd.sheight;
			sactivities.addAll(sd.sactivities);
		}

		public StreamDesc(StreamType type, Activity...activities) {
			this.uuid = randomUUID().toString();
			this.type = type;
			if (activities == null || activities.length == 0) {
				setActivities();
			} else {
				sactivities.addAll(Arrays.asList(activities));
			}
			if (StreamType.WEBCAM == type) {
				boolean interview = room != null && Room.Type.interview == room.getType();
				this.swidth = interview ? 320 : width;
				this.sheight = interview ? 260 : height;
			}
			if (StreamType.SCREEN == type) {
				this.swidth = 800;
				this.sheight = 600;
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
			if (StreamType.WEBCAM == type) {
				if (Client.this.hasActivity(Activity.AUDIO)) {
					sactivities.add(Activity.AUDIO);
				}
				if (Client.this.hasActivity(Activity.VIDEO)) {
					sactivities.add(Activity.VIDEO);
				}
			}
			if (StreamType.SCREEN == type) {
				sactivities.add(Activity.SCREEN);
			}
			return this;
		}

		public boolean hasActivity(Activity a) {
			return sactivities.contains(a);
		}

		public void addActivity(Activity a) {
			sactivities.add(a);
		}

		public StreamDesc removeActivity(Activity a) {
			sactivities.remove(a);
			return this;
		}

		public Client getClient() {
			return Client.this;
		}

		private JSONArray getActivities() {
			return new JSONArray(new ArrayList<>(sactivities));
		}

		public JSONObject toJson() {
			return addUserJson(new JSONObject()
					.put("uid", uuid)
					.put("type", type.name())
					.put("width", swidth)
					.put("height", sheight)
					.put("activities", getActivities())
					.put("cuid", uid));
		}

		@Override
		public String toString() {
			return String.format("Stream[uid=%s,type=%s,activities=%s]", uid, type, sactivities);
		}
	}
}
