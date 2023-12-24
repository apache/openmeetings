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
package org.apache.openmeetings.db.entity.record;

import static org.apache.openmeetings.db.bind.Constants.RECORDING_NODE;
import static org.apache.openmeetings.db.dao.user.UserDao.FETCH_GROUP_BACKUP;
import static org.apache.openmeetings.util.OmFileHelper.EXTENSION_MP4;
import static org.apache.openmeetings.util.OmFileHelper.RECORDING_FILE_NAME;

import java.util.Date;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.openjpa.persistence.FetchAttribute;
import org.apache.openjpa.persistence.FetchGroup;
import org.apache.openjpa.persistence.FetchGroups;
import org.apache.openmeetings.db.bind.adapter.BooleanAdapter;
import org.apache.openmeetings.db.bind.adapter.DateAdapter;
import org.apache.openmeetings.db.bind.adapter.LongAdapter;
import org.apache.openmeetings.db.bind.adapter.RecordingStatusAdapter;
import org.apache.openmeetings.db.entity.file.BaseFileItem;

/**
 * An item in the file explorer in the recording section. Can be either:
 * <ul>
 * <li>a conference recording</li>
 * <li>a interview recording</li>
 * <li>a folder</li>
 * </ul>
 *
 * Recorded files are situated in: webapps/openmeetings/streams/hibernate.
 * The raw recorded files are situated in:
 * webapps/openmeetings/streams/$ROOM_ID.
 *
 * @author sebawagner
 *
 */
@Entity
@FetchGroups({
	@FetchGroup(name = FETCH_GROUP_BACKUP, attributes = { @FetchAttribute(name = "chunks") })
})
@NamedQuery(name = "getRecordingsByExternalUser", query = "SELECT r FROM Recording r "
		+ "WHERE r.insertedBy = (SELECT gu.user.id FROM GroupUser gu WHERE "
		+ "gu.group.deleted = false AND gu.group.external = true AND gu.group.name = :externalType "
		+ "AND gu.user.deleted = false AND gu.user.type = :type AND gu.user.externalId = :externalId) "
		+ "AND r.deleted = false")
@NamedQuery(name = "getRecordingsPublic", query = "SELECT r FROM Recording r WHERE r.deleted = false AND r.ownerId IS NULL "
		+ "AND r.groupId IS NULL AND (r.parentId IS NULL OR r.parentId = 0) "
		+ "ORDER BY r.type ASC, r.inserted")
@NamedQuery(name = "getRecordingsByGroup", query = "SELECT r FROM Recording r WHERE r.deleted = false AND r.ownerId IS NULL "
		+ "AND r.groupId = :groupId AND (r.parentId IS NULL OR r.parentId = 0) "
		+ "ORDER BY r.type ASC, r.inserted")
@NamedQuery(name = "getRecordingsByOwner", query = "SELECT r FROM Recording r WHERE r.deleted = false AND r.ownerId = :ownerId "
		+ "AND (r.parentId IS NULL OR r.parentId = 0) "
		+ "ORDER BY r.type ASC, r.inserted")
@NamedQuery(name = "resetRecordingProcessingStatus", query = "UPDATE Recording r SET r.status = :error WHERE r.status IN (:recording, :converting)")
@NamedQuery(name = "getRecordingsAll", query = "SELECT r FROM Recording r ORDER BY r.id")
@NamedQuery(name = "getRecordingsByRoom", query = "SELECT r FROM Recording r WHERE r.deleted = false AND r.roomId = :roomId "
		+ "ORDER BY r.type ASC, r.inserted")
@NamedQuery(name = "getRecordingsByParent", query = "SELECT r FROM Recording r WHERE r.deleted = false AND r.parentId = :parentId "
		+ "ORDER BY r.type ASC, r.inserted")
@NamedQuery(name = "getRecordingsByExternalType", query = "SELECT r FROM Recording r "
		+ "WHERE r.deleted = false AND r.externalType = :externalType")
@NamedQuery(name = "getExpiringRecordings", query = "SELECT DISTINCT r FROM Recording r "
		+ "WHERE r.deleted = false AND r.notified = :notified AND r.inserted < :date "
		+ "  AND (r.groupId = :groupId "
		+ "    OR r.ownerId IN (SELECT gu.user.id FROM GroupUser gu WHERE gu.group.id = :groupId)"
		+ "    OR r.roomId IN (SELECT rg.room.id FROM RoomGroup rg WHERE rg.group.id = :groupId)"
		+ "  ) order by r.inserted ASC")
@XmlRootElement(name = RECORDING_NODE)
@XmlAccessorType(XmlAccessType.FIELD)
public class Recording extends BaseFileItem {
	private static final long serialVersionUID = 1L;

	@XmlType(namespace="org.apache.openmeetings.record")
	public enum Status {
		NONE
		, RECORDING
		, CONVERTING
		, PROCESSED
		, ERROR
	}

	@Column(name = "comment")
	@XmlElement(name = "comment", required = false)
	private String comment;

	@Column(name = "record_start")
	@XmlElement(name = "recordStart", required = false)
	@XmlJavaTypeAdapter(DateAdapter.class)
	private Date recordStart;

	@Column(name = "record_end")
	@XmlElement(name = "recordEnd", required = false)
	@XmlJavaTypeAdapter(DateAdapter.class)
	private Date recordEnd;

	@Column(name = "duration")
	@XmlElement(name = "duration", required = false)
	private String duration;

	@Column(name = "is_interview")
	@XmlElement(name = "interview", required = false)
	@XmlJavaTypeAdapter(value = BooleanAdapter.class, type = boolean.class)
	private boolean interview = false;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "recording_id")
	@XmlElementWrapper(name = "flvrecordingmetadatas", required = false)
	@XmlElement(name = "flvrecordingmetadata", required = false)
	private List<RecordingChunk> chunks;

	@Column(name = "status")
	@Enumerated(value = EnumType.STRING)
	@XmlElement(name = "status", required = false)
	@XmlJavaTypeAdapter(RecordingStatusAdapter.class)
	private Status status = Status.NONE;

	@Column(name = "notified")
	@XmlElement(name = "notified", required = false)
	@XmlJavaTypeAdapter(value = BooleanAdapter.class, type = boolean.class)
	private boolean notified = false;

	/**
	 * Method to get ID
	 *
	 * required to be overridden for valid export
	 */
	@Override
	public Long getId() {
		return super.getId();
	}

	@Override
	@XmlElement(name = "flvRecordingId")
	@XmlJavaTypeAdapter(LongAdapter.class)
	public void setId(Long id) {
		super.setId(id);
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public Date getRecordStart() {
		return recordStart;
	}

	public void setRecordStart(Date recordStart) {
		this.recordStart = recordStart;
	}

	public Date getRecordEnd() {
		return recordEnd;
	}

	public void setRecordEnd(Date recordEnd) {
		this.recordEnd = recordEnd;
	}

	public List<RecordingChunk> getChunks() {
		return chunks;
	}

	public void setChunks(List<RecordingChunk> chunks) {
		this.chunks = chunks;
	}

	public boolean isInterview() {
		return Boolean.TRUE.equals(interview);
	}

	public void setInterview(boolean interview) {
		this.interview = interview;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public boolean isNotified() {
		return notified;
	}

	public void setNotified(boolean notified) {
		this.notified = notified;
	}

	@Override
	public String getFileName(String ext) {
		return String.format("%s%s.%s", RECORDING_FILE_NAME, getId(), ext == null ? EXTENSION_MP4 : ext);
	}
}
