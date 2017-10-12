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
package org.apache.openmeetings.db.entity.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.apache.openmeetings.db.entity.HistoricalEntity;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Entity
@NamedQueries({
	@NamedQuery(name="getGroupById", query="SELECT g FROM Group AS g WHERE g.id = :id AND g.deleted = false")
	, @NamedQuery(name="getGroupByName", query="SELECT g FROM Group AS g WHERE g.name = :name AND g.deleted = false")
	, @NamedQuery(name="getAnyGroupById", query="SELECT g FROM Group AS g WHERE g.id = :groupId")
	, @NamedQuery(name="getGroupsByIds", query="SELECT g FROM Group AS g WHERE g.id IN :ids")
	, @NamedQuery(name="getNondeletedGroups", query="SELECT g FROM Group g WHERE g.deleted = false ORDER BY g.id")
	, @NamedQuery(name="countGroups", query="SELECT COUNT(g) FROM Group AS g WHERE g.deleted = false")
	, @NamedQuery(name="getLimitedGroups", query="SELECT g FROM Group AS g WHERE g.deleted = false AND g.limited = true")
})
@Table(name = "om_group")
@Root(name = "organisation")
public class Group extends HistoricalEntity {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	@Element(data = true, name = "organisation_id")
	private Long id;

	@Column(name = "name")
	@Element(data = true, required = false)
	private String name;

	@Column(name = "insertedby")
	private Long insertedby;

	@Column(name = "updatedby")
	private Long updatedby;

	@Column(name = "tag")
	@Element(data = true, required = false)
	private String tag;

	@Column(name = "limited", nullable = false)
	@Element(data = true, required = false)
	private boolean limited;

	@Column(name = "restricted", nullable = false)
	@Element(data = true, required = false)
	private boolean restricted;

	@Column(name = "max_files_size", nullable = false)
	@Element(data = true, required = false)
	private int maxFilesSize;

	@Column(name = "max_rec_size", nullable = false)
	@Element(data = true, required = false)
	private int maxRecordingsSize;

	@Column(name = "max_rooms", nullable = false)
	@Element(data = true, required = false)
	private int maxRooms;

	@Column(name = "recording_ttl", nullable = false)
	@Element(data = true, required = false)
	private int recordingTtl;

	@Column(name = "reminder_days", nullable = false)
	@Element(data = true, required = false)
	private int reminderDays;

	public Long getInsertedby() {
		return insertedby;
	}

	public void setInsertedby(Long insertedby) {
		this.insertedby = insertedby;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public Long getUpdatedby() {
		return updatedby;
	}

	public void setUpdatedby(Long updatedby) {
		this.updatedby = updatedby;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public boolean isLimited() {
		return limited;
	}

	public void setLimited(boolean limited) {
		this.limited = limited;
	}

	public boolean isRestricted() {
		return restricted;
	}

	public void setRestricted(boolean restricted) {
		this.restricted = restricted;
	}

	public int getMaxFilesSize() {
		return maxFilesSize;
	}

	public void setMaxFilesSize(int maxFilesSize) {
		this.maxFilesSize = maxFilesSize;
	}

	public int getMaxRecordingsSize() {
		return maxRecordingsSize;
	}

	public void setMaxRecordingsSize(int maxRecordingsSize) {
		this.maxRecordingsSize = maxRecordingsSize;
	}

	public int getMaxRooms() {
		return maxRooms;
	}

	public void setMaxRooms(int maxRooms) {
		this.maxRooms = maxRooms;
	}

	public int getRecordingTtl() {
		return recordingTtl;
	}

	public void setRecordingTtl(int recordingTtl) {
		this.recordingTtl = recordingTtl;
	}

	public int getReminderDays() {
		return reminderDays;
	}

	public void setReminderDays(int reminderDays) {
		this.reminderDays = reminderDays;
	}

	@Override
	public String toString() {
		return "Group [id=" + id + ", name=" + name + ", deleted=" + isDeleted() + "]";
	}
}
