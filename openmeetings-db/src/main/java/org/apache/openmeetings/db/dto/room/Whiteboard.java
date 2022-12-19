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

import static org.apache.openmeetings.util.OpenmeetingsVariables.PARAM_SRC;
import static org.apache.openmeetings.util.OpenmeetingsVariables.PARAM_SRC_UND;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.openmeetings.db.entity.file.BaseFileItem;
import org.apache.openmeetings.util.NullStringer;
import org.apache.wicket.util.string.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.openjson.JSONArray;
import com.github.openjson.JSONObject;

public class Whiteboard implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(Whiteboard.class);
	public static final String ATTR_TYPE = "type";
	public static final String ATTR_OMTYPE = "omType";
	public static final String ATTR_FILE_ID = "fileId";
	public static final String ATTR_FILE_TYPE = "fileType";
	public static final String ATTR_SLIDE = "slide";
	public static final String ATTR_WIDTH = "width";
	public static final String ATTR_HEIGHT = "height";
	public static final String ATTR_ZOOM = "zoom";
	public enum ZoomMode {
		FULL_FIT
		, PAGE_WIDTH
		, ZOOM
	}
	public static final String ITEMS_KEY = "roomItems";
	private static final int DEFAULT_WIDTH = 1920;
	private static final int DEFAULT_HEIGHT = 1080;
	private long id;
	private double zoom = 1.;
	private ZoomMode zoomMode = ZoomMode.PAGE_WIDTH;
	private int width = DEFAULT_WIDTH;
	private int height = DEFAULT_HEIGHT;
	private Map<String, String> roomItems = Collections.synchronizedMap(new LinkedHashMap<>());
	private Date created = new Date();
	private int slide = 0;
	private String name;

	public Whiteboard() {
		//def constructor
	}

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
		String obj = roomItems.get(uid);
		return obj == null ? null : new JSONObject(obj);
	}

	public boolean contains(String uid) {
		return roomItems.containsKey(uid);
	}

	public JSONArray clearSlide(int slide) {
		JSONArray arr = new JSONArray();
		roomItems.entrySet().removeIf(e -> {
				JSONObject o = new JSONObject(e.getValue());
				boolean match = !BaseFileItem.Type.PRESENTATION.name().equals(o.optString(ATTR_FILE_TYPE)) && o.optInt(ATTR_SLIDE, -1) == slide;
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
		final String obj = roomItems.remove(oid);
		return Strings.isEmpty(obj) ? null : new JSONObject(obj);
	}

	public boolean isEmpty() {
		return roomItems.isEmpty();
	}

	public String getName() {
		return name;
	}

	public Whiteboard setName(String name) {
		this.name = name;
		return this;
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

	public JSONObject getAddJson() {
		return new JSONObject().put("wbId", getId())
				.put("name", getName())
				.put(ATTR_WIDTH, getWidth())
				.put(ATTR_HEIGHT, getHeight())
				.put(ATTR_ZOOM, getZoom())
				.put("zoomMode", getZoomMode().name());
	}

	public JSONObject toJson() {
		//deep-copy
		JSONObject json = new JSONObject(new JSONObject(this).toString(new NullStringer()));
		json.remove("id"); //filtering
		json.remove("empty"); //filtering
		JSONObject items = new JSONObject();
		for (Entry<String, String> e : roomItems.entrySet()) {
			JSONObject o = new JSONObject(e.getValue());
			//filtering
			if ("Clipart".equals(o.opt(ATTR_OMTYPE))) {
				if (o.has(PARAM_SRC_UND)) {
					o.put(PARAM_SRC, o.get(PARAM_SRC_UND));
				}
			} else {
				o.remove(PARAM_SRC);
			}
			o.remove(PARAM_SRC_UND);
			items.put(e.getKey(), o);
		}
		json.put(ITEMS_KEY, items);
		return json;
	}

	public String save(Path path) {
		try (BufferedWriter writer = Files.newBufferedWriter(path)) {
			writer.write(toJson().toString(new NullStringer(2)));
		} catch (IOException e) {
			log.error("Unexpected error while saving WB", e);
			return e.getMessage();
		}
		return null;
	}
}
