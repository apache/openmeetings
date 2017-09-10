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

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.openmeetings.util.NullStringer;

import com.github.openjson.JSONArray;
import com.github.openjson.JSONObject;

public class Whiteboard implements Serializable {
	private static final long serialVersionUID = 1L;
	public enum ZoomMode {
		fullFit
		, pageWidth
		, zoom
	}
	public static final String ITEMS_KEY = "roomItems";
	private static final int DEFAULT_WIDTH = 1920;
	private static final int DEFAULT_HEIGHT = 1080;
	private long id;
	private double zoom = 1.;
	private ZoomMode zoomMode = ZoomMode.fullFit;
	private int width = DEFAULT_WIDTH;
	private int height = DEFAULT_HEIGHT;
	private Map<String, String> roomItems = Collections.synchronizedMap(new LinkedHashMap<>());
	private Date created = new Date();
	private int slide = 0;
	private String name;

	public Whiteboard() {}

	public Whiteboard(String name) {
		this.name = name;
		this.created = new Date();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public double getZoom() {
		return zoom;
	}

	public void setZoom(double zoom) {
		this.zoom = zoom;
	}

	public ZoomMode getZoomMode() {
		return zoomMode;
	}

	public void setZoomMode(ZoomMode zoomMode) {
		this.zoomMode = zoomMode;
	}

	public void clear() {
		roomItems.clear();
		width = DEFAULT_WIDTH;
		height = DEFAULT_HEIGHT;
	}

	public Whiteboard put(String uid, JSONObject obj) {
		roomItems.put(uid, obj.toString(new NullStringer()));
		return this;
	}

	public JSONObject get(String uid) {
		return new JSONObject(roomItems.get(uid));
	}

	public boolean contains(String uid) {
		return roomItems.containsKey(uid);
	}

	public JSONArray clearSlide(int slide) {
		JSONArray arr = new JSONArray();
		roomItems.entrySet().removeIf(e -> {
				JSONObject o = new JSONObject(e.getValue());
				boolean match = !"Presentation".equals(o.optString("fileType")) && o.optInt("slide", -1) == slide;
				if (match) {
					arr.put(e);
				}
				return match;
			});
		return arr;
	}

	public List<JSONObject> list() {
		List<JSONObject> items = new LinkedList<>();
		for (Entry<String, String> e : roomItems.entrySet()) {
			items.add(new JSONObject(e.getValue()));
		}
		return items;
	}

	public JSONObject remove(Object oid) {
		return new JSONObject(roomItems.remove(oid));
	}

	public boolean isEmpty() {
		return roomItems.isEmpty();
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

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public JSONObject toJson() {
		//deep-copy
		JSONObject json = new JSONObject(new JSONObject(this).toString(new NullStringer()));
		json.remove("id"); //filtering
		if (!json.has(ITEMS_KEY)) {
			json.put(ITEMS_KEY, new JSONObject());
		}
		JSONObject items = json.getJSONObject(ITEMS_KEY);
		for (String uid : items.keySet()) {
			JSONObject o = items.getJSONObject(uid);
			o.remove("_src");
			o.remove("src"); //filtering
		}
		return json;
	}
}
