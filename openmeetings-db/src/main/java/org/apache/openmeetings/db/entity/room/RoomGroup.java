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

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.apache.openjpa.persistence.jdbc.ForeignKey;
import org.apache.openmeetings.db.entity.IDataProviderEntity;
import org.apache.openmeetings.db.entity.user.Group;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Entity
@NamedQueries({
	@NamedQuery(name = "getAllRoomGroups", query = "select ro from RoomGroup ro ORDER BY ro.id"),
	@NamedQuery(name = "getRoomGroupByGroupIdAndRoomType", query = "select c from RoomGroup as c "
			+ "where c.room.type = :type AND c.group.id = :groupId "
			+ "AND c.deleted = false"),
	@NamedQuery(name = "getRoomGroupByGroupId", query = "SELECT c FROM RoomGroup c "
			+ "LEFT JOIN FETCH c.room "
			+ "WHERE c.group.id = :groupId "
			+ "AND c.deleted = false AND c.room.deleted = false AND c.room.appointment = false "
			+ "AND c.group.deleted = false "
			+ "ORDER BY c.room.name ASC"),
	@NamedQuery(name = "selectMaxFromRoomsByGroup", query = "select c from RoomGroup as c "
			+ "where c.group.id = :groupId "
			+ "AND c.deleted = false"),
	@NamedQuery(name = "getRoomGroupByGroupIdAndRoomId", query = "select c from RoomGroup as c "
			+ "where c.room.id = :roomId "
			+ "AND c.group.id = :groupId "
			+ "AND c.deleted = false"),
	@NamedQuery(name = "getRoomGroupByRoomsId", query = "select c from RoomGroup as c "
			+ "where c.room.id = :roomId "
			+ "AND c.deleted = false")
})
@Table(name = "room_group")
@Root(name="room_organisation")
public class RoomGroup implements IDataProviderEntity {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	@Element(data = true, name = "rooms_organisation_id")
	private Long id;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="room_id", nullable=true)
	@ForeignKey(enabled = true)
	@Element(name="rooms_id", data=true, required=false)
	private Room room;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="group_id", nullable=true)
	@ForeignKey(enabled = true)
	@Element(name="organisation_id", data=true, required=false)
	private Group group;
	
	@Column(name = "inserted")
	private Date inserted;
	
	@Column(name = "updated")
	private Date updated;
	
	@Column(name = "deleted")
	@Element(data=true)
	private boolean deleted;

	public RoomGroup(Group org, Room room) {
		this.group = org;
		this.room = room;
	}

	public RoomGroup() {
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

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
    
	public Date getInserted() {
		return inserted;
	}
	public void setInserted(Date inserted) {
		this.inserted = inserted;
	}
    
	public Date getUpdated() {
		return updated;
	}
	public void setUpdated(Date updated) {
		this.updated = updated;
	}	
	
	public boolean isDeleted() {
		return deleted;
	}
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

}
