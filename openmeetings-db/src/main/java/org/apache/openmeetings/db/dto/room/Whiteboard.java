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
package org.apache.openmeetings.db.dto.room;

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

import com.github.openjson.JSONObject;

public class Whiteboard {
	private static final Logger log = Red5LoggerFactory.getLogger(Whiteboard.class, webAppRootKey);
	private long id;
	private Integer x = 0;
	private Integer y = 0;
	private Integer zoom = 100;
	private Boolean fullFit = true;
	private Map<String, JSONObject> roomItems = Collections.synchronizedMap(new LinkedHashMap<>());
	private Date created = new Date();
	private int slide = 0;
	private int zIndex = 1;
	private String name;

	public Whiteboard() {}

	public Whiteboard(String name) {
		this.name = name;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Integer getX() {
		return x;
	}

	public void setX(Integer x) {
		this.x = x;
	}

	public Integer getY() {
		return y;
	}

	public void setY(Integer y) {
		this.y = y;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Integer getZoom() {
		return zoom;
	}

	public void setZoom(Integer zoom) {
		this.zoom = zoom;
	}

	public Boolean getFullFit() {
		return fullFit;
	}

	public void setFullFit(Boolean fullFit) {
		this.fullFit = fullFit;
	}

	public void clear() {
		roomItems.clear();
		zIndex = 1;
	}

	//getter is required, otherwise roomItems are not available in red5
	public Map<String, JSONObject> getRoomItems() {
		return roomItems;
	}

	public void put(String uid, JSONObject obj) {
		roomItems.put(uid, obj);
	}

	public JSONObject get(String uid) {
		return roomItems.get(uid);
	}

	public Set<Entry<String, JSONObject>> entrySet() {
		return roomItems.entrySet();
	}

	public void remove(Object oid) {
		roomItems.remove(oid);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSlide() {
		return slide;
	}

	public void setSlide(int slide) {
		this.slide = slide;
	}

	public JSONObject toJson() {
		//deep-copy
		JSONObject json = new JSONObject(new JSONObject(this).toString());
		json.remove("id"); //filtering
		JSONObject items = json.getJSONObject("roomItems");
		for (String uid : items.keySet()) {
			items.getJSONObject(uid).remove("_src"); //filtering
		}
		return json;
	}
}
