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
package org.apache.openmeetings.db.entity.room;

import static org.apache.openmeetings.db.bind.Constants.ROOM_GRP_NODE;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.openjpa.persistence.jdbc.ForeignKey;
import org.apache.openmeetings.db.bind.adapter.GroupAdapter;
import org.apache.openmeetings.db.bind.adapter.LongAdapter;
import org.apache.openmeetings.db.bind.adapter.RoomAdapter;
import org.apache.openmeetings.db.entity.IDataProviderEntity;
import org.apache.openmeetings.db.entity.user.Group;

@Entity
@NamedQuery(name = "getAllRoomGroups", query = "SELECT rg FROM RoomGroup rg ORDER BY rg.id")
@Table(name = "room_group")
@XmlRootElement(name = ROOM_GRP_NODE)
@XmlAccessorType(XmlAccessType.FIELD)
public class RoomGroup implements IDataProviderEntity {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	@XmlElement(name = "rooms_organisation_id", required = false)
	@XmlJavaTypeAdapter(LongAdapter.class)
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "room_id", nullable = true)
	@ForeignKey(enabled = true)
	@XmlElement(name = "rooms_id", required = false)
	@XmlJavaTypeAdapter(RoomAdapter.class)
	private Room room;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "group_id", nullable = true)
	@ForeignKey(enabled = true)
	@XmlElement(name = "organisation_id", required = false)
	@XmlJavaTypeAdapter(GroupAdapter.class)
	private Group group;

	public RoomGroup() {
		//def constructor
	}

	public RoomGroup(Group org, Room room) {
		this.group = org;
		this.room = room;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public Room getRoom() {
		return room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}
}
