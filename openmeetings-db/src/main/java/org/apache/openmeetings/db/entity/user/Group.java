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

import static org.apache.openmeetings.db.bind.Constants.GROUP_NODE;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.openmeetings.db.bind.adapter.BooleanAdapter;
import org.apache.openmeetings.db.bind.adapter.IntAdapter;
import org.apache.openmeetings.db.bind.adapter.LongAdapter;
import org.apache.openmeetings.db.entity.HistoricalEntity;

@Entity
@NamedQuery(name = "getGroupById", query = "SELECT g FROM Group AS g WHERE g.id = :id AND g.deleted = false")
@NamedQuery(name = "getGroupByName", query = "SELECT g FROM Group AS g WHERE g.name = :name AND g.deleted = false")
@NamedQuery(name = "getExtGroupByName", query = "SELECT g FROM Group AS g WHERE g.name = :name AND g.deleted = false AND g.external = true")
@NamedQuery(name = "getAnyGroupById", query = "SELECT g FROM Group AS g WHERE g.id = :groupId")
@NamedQuery(name = "getGroupsByIds", query = "SELECT g FROM Group AS g WHERE g.id IN :ids")
@NamedQuery(name = "getNondeletedGroups", query = "SELECT g FROM Group g WHERE g.deleted = false ORDER BY g.id")
@NamedQuery(name = "countGroups", query = "SELECT COUNT(g) FROM Group AS g WHERE g.deleted = false")
@NamedQuery(name = "getLimitedGroups", query = "SELECT g FROM Group AS g WHERE g.deleted = false AND g.limited = true")
@NamedQuery(name = "getGroupsForUserNotifications", query = "SELECT g FROM Group AS g WHERE g.deleted = false AND g.notifyInterval > 0")
@Table(name = "om_group", indexes = {
		@Index(name = "group_name_idx", columnList = "name")
})
@XmlRootElement(name = GROUP_NODE)
@XmlAccessorType(XmlAccessType.FIELD)
public class Group extends HistoricalEntity {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	@XmlElement(name = "organisation_id", required = false)
	@XmlJavaTypeAdapter(LongAdapter.class)
	private Long id;

	@Column(name = "name")
	@XmlElement(name = "name", required = false)
	private String name;

	@Column(name = "insertedby")
	@XmlTransient
	private Long insertedby;

	@Column(name = "updatedby")
	@XmlTransient
	private Long updatedby;

	@Column(name = "tag")
	@XmlElement(name = "tag", required = false)
	private String tag;

	@Column(name = "limited", nullable = false)
	@XmlElement(name = "limited", required = false)
	@XmlJavaTypeAdapter(value = BooleanAdapter.class, type = boolean.class)
	private boolean limited;

	@Column(name = "restricted", nullable = false)
	@XmlElement(name = "restricted", required = false)
	@XmlJavaTypeAdapter(value = BooleanAdapter.class, type = boolean.class)
	private boolean restricted;

	@Column(name = "max_files_size", nullable = false)
	@XmlElement(name = "maxFilesSize", required = false)
	@XmlJavaTypeAdapter(value = IntAdapter.class, type = int.class)
	private int maxFilesSize;

	@Column(name = "max_rec_size", nullable = false)
	@XmlElement(name = "maxRecordingsSize", required = false)
	@XmlJavaTypeAdapter(value = IntAdapter.class, type = int.class)
	private int maxRecordingsSize;

	@Column(name = "max_rooms", nullable = false)
	@XmlElement(name = "maxRooms", required = false)
	@XmlJavaTypeAdapter(value = IntAdapter.class, type = int.class)
	private int maxRooms;

	@Column(name = "recording_ttl", nullable = false)
	@XmlElement(name = "recordingTtl", required = false)
	@XmlJavaTypeAdapter(value = IntAdapter.class, type = int.class)
	private int recordingTtl;

	@Column(name = "reminder_days", nullable = false)
	@XmlElement(name = "reminderDays", required = false)
	@XmlJavaTypeAdapter(value = IntAdapter.class, type = int.class)
	private int reminderDays;

	@Column(name = "external", nullable = false)
	@XmlElement(name = "external", required = false)
	@XmlJavaTypeAdapter(value = BooleanAdapter.class, type = boolean.class)
	private boolean external;

	@Column(name = "notify_user_int", nullable = false)
	@XmlElement(name = "notifyNewUsersInterval", required = false)
	@XmlJavaTypeAdapter(value = IntAdapter.class, type = int.class)
	private int notifyInterval;

	public Long getInsertedby() {
		return insertedby;
	}

	public void setInsertedby(Long insertedby) {
		this.insertedby = insertedby;
	}

	public String getName() {
		return name;
	}

	public Group setName(String name) {
		this.name = name;
		return this;
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

	public boolean isExternal() {
		return external;
	}

	public Group setExternal(boolean external) {
		this.external = external;
		return this;
	}

	public int getNotifyInterval() {
		return notifyInterval;
	}

	public void setNotifyInterval(int notifyInterval) {
		this.notifyInterval = notifyInterval;
	}

	@Override
	public String toString() {
		return "Group [id=" + id + ", name=" + name + ", deleted=" + isDeleted() + "]";
	}
}
