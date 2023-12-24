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
import static org.apache.openmeetings.util.OmFileHelper.SIP_USER_ID;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import jakarta.annotation.Nonnull;

import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.IDataProviderEntity;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.room.Room.Right;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.wicket.util.string.Strings;

import com.github.openjson.JSONArray;
import com.github.openjson.JSONObject;

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
	private final Set<Right> rights = ConcurrentHashMap.newKeySet();
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
		return user == null ? null : user.getId();
	}

	public boolean sameUserId(Long userId) {
		return getUserId() != null && getUserId().equals(userId);
	}

	public String getPictureUri() {
		return pictureUri;
	}

	@Override
	@Nonnull
	public String getUid() {
		return uid;
	}

	public String getSid() {
		return sid;
	}

	public boolean isSip() {
		return SIP_USER_ID.equals(getUserId());
	}

	public void clear() {
		rights.clear();
		streams.clear();
	}

	public boolean hasRight(Right right) {
		if (Right.SUPER_MODERATOR == right) {
			return rights.contains(right);
		}
		return rights.contains(Right.SUPER_MODERATOR) || rights.contains(Right.MODERATOR) || rights.contains(right);
	}

	public Client allow(Right... inRights) {
		allow(List.of(inRights));
		return this;
	}

	public void allow(Iterable<Right> inRights) {
		for (Right right : inRights) {
			if (!hasRight(right)) {
				rights.add(right);
			}
		}
	}

	public void deny(Right... inRights) {
		for (Right right : inRights) {
			rights.remove(right);
		}
	}

	public boolean isBroadcasting() {
		return getCamStreams()
				.anyMatch(WebcamStreamDesc::isBroadcasting);
	}

	public List<Activity> getActivities() {
		return getCamStreams()
				.flatMap(sd -> sd.getActivities().stream())
				.toList();
	}

	public boolean has(Activity activity) {
		return getCamStreams()
				.flatMap(sd -> sd.getActivities().stream())
				.anyMatch(a -> activity == a);
	}

	public boolean isAllowed(Activity a) {
		boolean r = false;
		if (room == null) {
			return r;
		}
		switch (a) {
			case AUDIO:
				r = hasRight(Right.AUDIO);
				break;
			case VIDEO:
				r = !room.isAudioOnly() && hasRight(Right.VIDEO);
				break;
			case AUDIO_VIDEO:
				r = !room.isAudioOnly() && hasRight(Right.AUDIO) && hasRight(Right.VIDEO);
				break;
			default:
				break;
		}
		return r;
	}

	public StreamDesc addStream(StreamType stype, Activity toggle) {
		StreamDesc sd = switch(stype) {
			case SCREEN -> new ScreenStreamDesc(this, toggle);
			case WEBCAM -> new WebcamStreamDesc(this, toggle);
		};
		streams.put(sd.getUid(), sd);
		return sd;
	}

	public StreamDesc removeStream(String inUid) {
		return streams.remove(inUid);
	}

	public List<StreamDesc> getStreams() {
		return List.copyOf(streams.values());
	}

	public StreamDesc getStream(String inUid) {
		return streams.get(inUid);
	}

	public Optional<ScreenStreamDesc> getScreenStream() {
		return streams.values().stream()
				.filter(sd -> StreamType.SCREEN == sd.getType())
				.map(ScreenStreamDesc.class::cast)
				.findFirst();
	}

	public Stream<WebcamStreamDesc> getCamStreams() {
		return streams.values().stream()
				.filter(sd -> StreamType.WEBCAM == sd.getType())
				.map(WebcamStreamDesc.class::cast);
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

	public Long getRoomId() {
		return room == null ? null : room.getId();
	}

	public Client setRoom(Room room) {
		this.room = room;
		return this;
	}

	public boolean isCamEnabled() {
		return (room == null || !room.isAudioOnly()) && cam > -1;
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
		return room != null && room.isInterview() ? 320 : width;
	}

	public Client setWidth(int width) {
		this.width = width;
		return this;
	}

	public int getHeight() {
		return room != null && room.isInterview() ? 260 : height;
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

	// package private for StremDesc
	JSONObject addUserJson(JSONObject o) {
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
		final int level;
		if (hasRight(Right.MODERATOR)) {
			level = 5;
		} else if(hasRight(Right.WHITEBOARD)) {
			level = 3;
		} else {
			level = 1;
		}
		return o.put("user", u)
				.put("level", level);
	}

	// package private for StremDesc
	JSONObject addCamMic(boolean self, JSONObject json) {
		if (self) {
			json.put("cam", cam).put("mic", mic);
		}
		return json;
	}

	public JSONObject toJson(boolean self) {
		JSONArray streamArr = new JSONArray();
		for (Entry<String, StreamDesc> e : streams.entrySet()) {
			streamArr.put(e.getValue().toJson(self));
		}
		JSONObject json = new JSONObject()
				.put("cuid", uid)
				.put("uid", uid)
				.put("rights", new JSONArray(rights))
				.put("activities", new JSONArray(getActivities()))
				.put("streams", streamArr)
				.put("width", getWidth())
				.put("height", getHeight())
				.put("self", self);
		return addUserJson(addCamMic(self, json));
	}

	public void merge(Client c) {
		if (c == this) {
			return;
		}
		user = c.user;
		room = c.room;
		synchronized (rights) {
			Set<Right> rr = new HashSet<>(c.rights);
			rights.clear();
			rights.addAll(rr);
		}
		synchronized (streams) {
			streams.clear();
			c.streams.values().stream()
				.map(sd ->
					switch (sd.getType()) {
						case SCREEN -> new ScreenStreamDesc((ScreenStreamDesc)sd);
						case WEBCAM -> new WebcamStreamDesc((WebcamStreamDesc)sd);
					}
				).forEach(sd -> streams.put(sd.getUid(), sd));
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
		return "Client [uid=" + uid + ", sessionId=" + sessionId + ", pageId=" + pageId + ", userId=" + getUserId() + ", room=" + getRoomId()
				+ ", rights=" + rights + ", activities=" + getActivities() + ", connectedSince=" + connectedSince + "]";
	}
}
