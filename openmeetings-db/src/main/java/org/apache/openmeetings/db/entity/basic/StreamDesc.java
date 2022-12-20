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
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentHashMap.KeySetView;

import org.apache.openmeetings.db.entity.basic.Client.Activity;
import org.apache.openmeetings.db.entity.basic.Client.StreamType;

import com.github.openjson.JSONArray;
import com.github.openjson.JSONObject;

public abstract class StreamDesc implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
	protected final /*serializable*/ KeySetView<Activity, Boolean> activities = ConcurrentHashMap.newKeySet();
	private final Client client;
	private final String uid;
	private final StreamType type;
	private int width;
	private int height;

	protected StreamDesc(StreamDesc sd) {
		this.client = sd.client;
		this.uid = sd.uid;
		this.type = sd.type;
		this.width = sd.width;
		this.height = sd.height;
		this.activities.addAll(sd.activities);
	}

	protected StreamDesc(final Client client, StreamType type) {
		this.client = client;
		this.uid = randomUUID().toString();
		this.type = type;
	}

	protected abstract boolean allowed(Activity a);

	public boolean has(Activity a) {
		return activities.contains(a);
	}

	public void add(Activity a) {
		if (allowed(a)) {
			activities.add(a);
		}
	}

	public StreamDesc remove(Activity a) {
		activities.remove(a);
		return this;
	}

	public List<Activity> getActivities() {
		return List.copyOf(activities);
	}

	public Client getClient() {
		return client;
	}

	public String getSid() {
		return client.getSid();
	}

	public String getUid() {
		return uid;
	}

	public StreamType getType() {
		return type;
	}

	public int getWidth() {
		return width;
	}

	public StreamDesc setWidth(int width) {
		this.width = width;
		return this;
	}

	public int getHeight() {
		return height;
	}

	public StreamDesc setHeight(int height) {
		this.height = height;
		return this;
	}

	@Override
	public String toString() {
		return String.format("Stream[uid=%s,type=%s,activities=%s]", uid, type, activities);
	}

	public JSONObject toJson() {
		return toJson(false);
	}

	public JSONObject toJson(boolean self) {
		JSONObject o = new JSONObject()
				.put("uid", uid)
				.put("type", type.name())
				.put("width", width)
				.put("height", height)
				.put("activities", new JSONArray(activities))
				.put("cuid", client.getUid());
		return client.addUserJson(client.addCamMic(self, o));
	}
}
