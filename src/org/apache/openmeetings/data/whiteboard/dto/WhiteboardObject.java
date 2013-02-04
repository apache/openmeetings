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
package org.apache.openmeetings.data.whiteboard.dto;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WhiteboardObject {

	private Long whiteBoardId;
	private Integer x = 0;
	private Integer y = 0;
	private Integer zoom = 100;
	private Boolean fullFit = true;
	@SuppressWarnings("rawtypes")
	private Map<String, List> roomItems = new HashMap<String, List>();
	private Date created = new Date();

	public Long getWhiteBoardId() {
		return whiteBoardId;
	}

	public void setWhiteBoardId(Long whiteBoardId) {
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

	@SuppressWarnings("rawtypes")
	public Map<String, List> getRoomItems() {
		return roomItems;
	}

	@SuppressWarnings("rawtypes")
	public void setRoomItems(Map<String, List> roomItems) {
		this.roomItems = roomItems;
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

}
