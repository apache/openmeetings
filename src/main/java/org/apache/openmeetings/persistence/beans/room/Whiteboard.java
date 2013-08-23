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
package org.apache.openmeetings.persistence.beans.room;

import java.util.Calendar;
import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.openjpa.persistence.jdbc.ElementJoinColumn;
import org.apache.openjpa.persistence.jdbc.ForeignKey;

@Entity
@Table(name = "whiteboard")
public class Whiteboard {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;

	@Column(name = "room_id")
	private Integer roomId = 0;

	@Column(name = "x")
	private Integer x = 0;

	@Column(name = "y")
	private Integer y = 0;

	@Column(name = "zoom")
	private Integer zoom = 100;

	@Column(name = "full_fit")
	private Boolean fullFit;

	@Column(name = "created")
	private Calendar created;

	@OneToMany
	@ElementJoinColumn(name = "whiteboarditem_id", referencedColumnName = "id")
	@ForeignKey(enabled = true)
	private Collection<WhiteboardItem> roomItems;

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

	public Calendar getCreated() {
		return created;
	}

	public void setCreated(Calendar created) {
		this.created = created;
	}

	public Integer getRoomId() {
		return roomId;
	}

	public void setRoomId(Integer roomId) {
		this.roomId = roomId;
	}

	public Collection<WhiteboardItem> getRoomItems() {
		return roomItems;
	}

	public void setRoomItems(Collection<WhiteboardItem> roomItems) {
		this.roomItems = roomItems;
	}

}
