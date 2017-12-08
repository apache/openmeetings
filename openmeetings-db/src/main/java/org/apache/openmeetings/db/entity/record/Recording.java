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

import static org.apache.openmeetings.util.OmFileHelper.EXTENSION_MP4;
import static org.apache.openmeetings.util.OmFileHelper.RECORDING_FILE_NAME;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.openmeetings.db.entity.file.BaseFileItem;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

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
@NamedQueries({
	@NamedQuery(name = "getRecordingsByExternalUser", query = "SELECT c FROM Recording c, User u "
			+ "WHERE c.insertedBy = u.id AND u.externalId = :externalId  AND u.externalType = :externalType "
			+ "AND c.deleted = false")
	, @NamedQuery(name = "getRecordingsPublic", query = "SELECT f FROM Recording f WHERE f.deleted = false AND f.ownerId IS NULL "
			+ "AND f.groupId IS NULL AND (f.parentId IS NULL OR f.parentId = 0) "
			+ "ORDER BY f.type ASC, f.inserted")
	, @NamedQuery(name = "getRecordingsByGroup", query = "SELECT f FROM Recording f WHERE f.deleted = false AND f.ownerId IS NULL "
			+ "AND f.groupId = :groupId AND (f.parentId IS NULL OR f.parentId = 0) "
			+ "ORDER BY f.type ASC, f.inserted")
	, @NamedQuery(name = "getRecordingsByOwner", query = "SELECT f FROM Recording f WHERE f.deleted = false AND f.ownerId = :ownerId "
			+ "AND (f.parentId IS NULL OR f.parentId = 0) "
			+ "ORDER BY f.type ASC, f.inserted")
	, @NamedQuery(name = "resetRecordingProcessingStatus", query = "UPDATE Recording f SET f.status = :error WHERE f.status IN (:recording, :converting)")
	, @NamedQuery(name = "getRecordingsAll", query = "SELECT c FROM Recording c LEFT JOIN FETCH c.metaData ORDER BY c.id")
	, @NamedQuery(name = "getRecordingsByRoom", query = "SELECT c FROM Recording c WHERE c.deleted = false AND c.roomId = :roomId "
			+ "ORDER BY c.type ASC, c.inserted")
	, @NamedQuery(name = "getRecordingsByParent", query = "SELECT f FROM Recording f WHERE f.deleted = false AND f.parentId = :parentId "
			+ "ORDER BY f.type ASC, f.inserted")
	, @NamedQuery(name = "getRecordingsByExternalType", query = "SELECT rec FROM Recording rec, Room r, User u "
			+ "WHERE rec.deleted = false AND rec.roomId = r.id AND rec.insertedBy = u.id "
			+ "AND (r.externalType = :externalType OR u.externalType = :externalType)")
	, @NamedQuery(name = "getExpiringRecordings", query = "SELECT DISTINCT rec FROM Recording rec "
			+ "WHERE rec.deleted = false AND rec.notified = :notified AND rec.inserted < :date "
			+ "  AND (rec.groupId = :groupId "
			+ "    OR rec.ownerId IN (SELECT gu.user.id FROM GroupUser gu WHERE gu.group.id = :groupId)"
			+ "    OR rec.roomId IN (SELECT rg.room.id FROM RoomGroup rg WHERE rg.group.id = :groupId)"
			+ "  ) order by rec.inserted ASC")
})
@Root(name = "flvrecording")
@XmlRootElement
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
	@Element(data = true, required = false)
	private String comment;

	@Column(name = "record_start")
	@Element(data = true, required = false)
	private Date recordStart;

	@Column(name = "record_end")
	@Element(data = true, required = false)
	private Date recordEnd;

	@Column(name = "duration")
	@Element(data = true, required = false)
	private String duration;

	@Column(name = "recorder_stream_id")
	@Element(data = true, required = false)
	private String recorderStreamId;

	@Column(name = "is_interview")
	@Element(data = true, required = false)
	private Boolean interview = false;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "recording_id")
	@ElementList(name = "flvrecordingmetadatas", required = false)
	private List<RecordingMetaData> metaData;

	@Column(name = "status")
	@Enumerated(value = EnumType.STRING)
	@Element(data = true, required = false)
	private Status status = Status.NONE;

	@Column(name = "notified")
	@Element(data = true, required = false)
	private Boolean notified = false;

	@Override
	@Element(data = true, name = "flvRecordingId")
	public Long getId() {
		return super.getId();
	}

	@Override
	@Element(data = true, name = "flvRecordingId")
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

	public String getRecorderStreamId() {
		return recorderStreamId;
	}

	public void setRecorderStreamId(String recorderStreamId) {
		this.recorderStreamId = recorderStreamId;
	}

	public List<RecordingMetaData> getMetaData() {
		return metaData;
	}

	public void setMetaData(List<RecordingMetaData> metaData) {
		this.metaData = metaData;
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
		return Boolean.TRUE.equals(notified);
	}

	public void setNotified(boolean notified) {
		this.notified = notified;
	}

	@Override
	public String getFileName(String ext) {
		return String.format("%s%s.%s", RECORDING_FILE_NAME, getId(), ext == null ? EXTENSION_MP4 : ext);
	}
}
