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

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class WhiteboardObject {
	private static final Logger log = Red5LoggerFactory.getLogger(WhiteboardObject.class, webAppRootKey);
	private long whiteBoardId;
	private Integer x = 0;
	private Integer y = 0;
	private Integer zoom = 100;
	private Boolean fullFit = true;
	private Map<String, List<Object>> roomItems = new ConcurrentHashMap<>();
	private Date created = new Date();
	private int zIndex = 1;

	public WhiteboardObject() {}
	
	public long getWhiteBoardId() {
		return whiteBoardId;
	}

	public void setWhiteBoardId(long whiteBoardId) {
		this.whiteBoardId = whiteBoardId;
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
	public Map<String, List<Object>> getRoomItems() {
		return roomItems;
	}

	public void add(String oid, List<Object> actionObject) {
		Object type = actionObject.size() > 0 ? actionObject.get(0) : "";
		if (actionObject.size() > 8 && ("swf".equals(type) || "image".equals(type) || "flv".equals(type))) {
			Integer zInd = (Integer)actionObject.get(actionObject.size() - 8);
			if (zInd == null || zInd == 0 || zInd < zIndex) {
				actionObject.set(actionObject.size() - 8, zIndex++);
			}
		}
		roomItems.put(oid, actionObject);
	}

	public List<Object> get(String oid) {
		return roomItems.get(oid);
	}

	public Set<Entry<String, List<Object>>> entrySet() {
		return roomItems.entrySet();
	}

	public void remove(Object oid) {
		roomItems.remove(oid);
	}

	public void remove(List<Object> actionObject) {
		String oid = actionObject.get(actionObject.size() - 1).toString();
		String type = actionObject.get(0).toString();
		log.debug("Removal of object: oid = {}, type = {} ", oid, type);

		/* I believe this is redundant
		// Re-Index all items in its zIndex
		if (type.equals("ellipse")
				|| type.equals("drawarrow")
				|| type.equals("line")
				|| type.equals("paint")
				|| type.equals("rectangle")
				|| type.equals("uline")
				|| type.equals("image")
				|| type.equals("letter")
				|| type.equals("clipart")
				|| type.equals("swf")
				|| type.equals("mindmapnode")
				|| type.equals("flv")) {

			Integer zIndex = Integer.valueOf(actionObject.get(actionObject.size() - 8).toString());

			log.debug("1|zIndex " + zIndex);
			log.debug("2|zIndex " + actionObject.get(actionObject.size() - 8).toString());
			log.debug("3|zIndex " + actionObject.get(actionObject.size() - 8));

			int l = 0;
			for (Object o : actionObject) {
				log.debug("4|zIndex " + l + " -- " + o);
				l++;
			}

			for (Entry<String, List<Object>> e : roomItems.entrySet()) {
				List<Object> actionObjectStored = e.getValue();

				Integer zIndexStored = Integer.valueOf(actionObjectStored.get(actionObjectStored.size() - 8).toString());

				log.debug("zIndexStored|zIndex " + zIndexStored + "|" + zIndex);

				if (zIndexStored >= zIndex) {
					zIndexStored -= 1;
					log.debug("new-zIndex " + zIndexStored);
				}
				actionObjectStored.set(actionObjectStored.size() - 8, zIndexStored);
			}
		}
		*/
		roomItems.remove(oid);
	}
}
