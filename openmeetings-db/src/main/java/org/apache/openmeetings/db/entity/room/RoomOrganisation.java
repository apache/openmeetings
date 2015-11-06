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
import org.apache.openmeetings.db.entity.user.Organisation;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Entity
@NamedQueries({
	@NamedQuery(name = "getAllRoomsOrganisations", query = "select ro from RoomOrganisation ro ORDER BY ro.rooms_organisation_id"),
	@NamedQuery(name = "getRoomGroupByGroupIdAndRoomId", query = "select c from RoomOrganisation as c "
			+ "where c.room.rooms_id = :roomId "
			+ "AND c.organisation.organisation_id = :groupId "
			+ "AND c.deleted = false"),
})
@Table(name = "rooms_organisation")
@Root(name="room_organisation")
public class RoomOrganisation implements IDataProviderEntity {
	private static final long serialVersionUID = 4153935045968138984L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	@Element(data=true)
	private Long rooms_organisation_id;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="rooms_id", nullable=true)
	@ForeignKey(enabled = true)
	@Element(name="rooms_id", data=true, required=false)
	private Room room;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="organisation_id", nullable=true)
	@ForeignKey(enabled = true)
	@Element(name="organisation_id", data=true, required=false)
	private Organisation organisation;
	
	@Column(name = "starttime")
	private Date starttime;
	
	@Column(name = "updatetime")
	private Date updatetime;
	
	@Column(name = "deleted")
	@Element(data=true)
	private boolean deleted;

	public RoomOrganisation(Organisation org, Room room) {
		this.organisation = org;
		this.room = room;
	}

	public RoomOrganisation() {
	}

	public Organisation getOrganisation() {
		return organisation;
	}
	public void setOrganisation(Organisation organisation) {
		this.organisation = organisation;
	}
	
	public Room getRoom() {
		return room;
	}
	public void setRoom(Room room) {
		this.room = room;
	}

	public Long getId() {
		return rooms_organisation_id;
	}
	public void setId(Long id) {
		this.rooms_organisation_id = id;
	}
    
	public Date getInserted() {
		return starttime;
	}
	public void setInserted(Date inserted) {
		this.starttime = inserted;
	}
    
	public Date getUpdated() {
		return updatetime;
	}
	public void setUpdated(Date updated) {
		this.updatetime = updated;
	}	
	
	public boolean isDeleted() {
		return deleted;
	}
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

}
