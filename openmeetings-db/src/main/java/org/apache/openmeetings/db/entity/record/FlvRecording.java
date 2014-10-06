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

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.openmeetings.db.entity.file.FileItem;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.user.User;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

/**
 * An item in the file explorer in the recording section. Can be either:<br/>
 * <ul>
 * <li>a conference recording</li>
 * <li>a interview recording</li>
 * <li>a folder</li>
 * </ul>
 * 
 * Recorded files are situated in: webapps/openmeetings/streams/hibernate.<br/>
 * The raw recorded files are situated in:
 * webapps/openmeetings/streams/$ROOM_ID.<br/>
 * 
 * @author sebawagner
 * 
 */
@Entity
@NamedQueries({ 
	@NamedQuery(name = "getRecordingById", query = "SELECT f FROM FlvRecording f WHERE f.id = :id") 
	, @NamedQuery(name = "getRecordingByHash", query = "SELECT f FROM FlvRecording f WHERE f.fileHash = :fileHash") 
	, @NamedQuery(name = "getRecordingsByExternalUser", query = "SELECT NEW org.apache.openmeetings.db.dto.file.RecordingObject(c) "
			+ "FROM FlvRecording c, User u "
			+ "WHERE c.insertedBy = u.id AND u.externalUserId = :externalUserId  AND u.externalUserType = :externalUserType "
			+ "AND c.deleted = false") 
	, @NamedQuery(name = "getRecordingsPublic", query = "SELECT f FROM FlvRecording f WHERE f.deleted = false AND f.ownerId IS NULL "
			+ "AND f.organization_id IS NULL AND (f.parentItemId IS NULL OR f.parentItemId = 0) "
			+ "ORDER BY f.type DESC, f.fileName")
	, @NamedQuery(name = "getRecordingsByOrganization", query = "SELECT f FROM FlvRecording f WHERE f.deleted = false AND f.ownerId IS NULL "
			+ "AND f.organization_id = :organization_id AND (f.parentItemId IS NULL OR f.parentItemId = 0) "
			+ "ORDER BY f.type DESC, f.fileName")
	, @NamedQuery(name = "getRecordingsByOwner", query = "SELECT f FROM FlvRecording f WHERE f.deleted = false AND f.ownerId = :ownerId "
			+ "AND (f.parentItemId IS NULL OR f.parentItemId = 0) "
			+ "ORDER BY f.type DESC, f.fileName ")
	, @NamedQuery(name = "resetRecordingProcessingStatus", query = "UPDATE FlvRecording f SET f.status = :error WHERE f.status = :processing")
	, @NamedQuery(name = "getRecordingsAll", query = "SELECT c FROM FlvRecording c LEFT JOIN FETCH c.flvRecordingMetaData ORDER BY c.id")
	, @NamedQuery(name = "getRecordingsByExternalRoomTypeAndOwner", query = "SELECT c FROM FlvRecording c, Room r WHERE c.roomId = r.id "
			+ "AND r.externalRoomType LIKE :externalRoomType AND c.insertedBy LIKE :insertedBy AND c.deleted = false")
	, @NamedQuery(name = "getRecordingsByExternalRoomType", query = "SELECT c FROM FlvRecording c, Room r WHERE c.roomId = r.id "
			+ "AND r.externalRoomType LIKE :externalRoomType AND c.deleted = false")
	, @NamedQuery(name = "getRecordingsByRoom", query = "SELECT c FROM FlvRecording c WHERE c.deleted = false AND c.roomId = :roomId "
			+ "ORDER BY c.type ASC, c.fileName")
	, @NamedQuery(name = "getRecordingsByParent", query = "SELECT c FROM FlvRecording c WHERE c.deleted = false AND c.parentItemId = :parentItemId "
			+ "ORDER BY c.type ASC, c.fileName") 
	, @NamedQuery(name = "getRecordingsByExternalType", query = "SELECT rec FROM FlvRecording rec, Room r, User u "
			+ "WHERE rec.deleted = false AND rec.room_id = r.rooms_id AND rec.insertedBy = u.user_id "
					+ "AND (r.externalRoomType = :externalType OR u.externalUserType = :externalType)")
})
@Table(name = "flvrecording")
@Root(name = "flvrecording")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class FlvRecording extends FileItem {
	private static final long serialVersionUID = 1L;
	
	@XmlType(namespace="org.apache.openmeetings.record")
	public enum Status {
		NONE
		, PROCESSING
		, PROCESSED
		, ERROR
	}
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	@Element(data = true, name = "flvRecordingId")
	private Long id;

	@Column(name = "alternate_download")
	@Element(data = true, required = false)
	private String alternateDownload;

	@Column(name = "comment_field")
	@Element(data = true, required = false)
	private String comment;

	@Column(name = "record_start")
	@Element(data = true, required = false)
	private Date recordStart;

	@Column(name = "record_end")
	@Element(data = true, required = false)
	private Date recordEnd;

	@Column(name = "width")
	@Element(data = true, required = false)
	private Integer width;

	@Column(name = "height")
	@Element(data = true, required = false)
	private Integer height;

	@Column(name = "duration")
	@Element(data = true, required = false)
	private String duration;

	@Column(name = "recorder_stream_id")
	@Element(data = true, required = false)
	private String recorderStreamId;

	@Column(name = "organization_id")
	@Element(data = true, required = false)
	private Long organization_id;

	@Column(name = "is_interview")
	@Element(data = true, required = false)
	private Boolean isInterview;

	@Column(name = "progress_post_processing")
	@Element(data = true, required = false)
	private Integer progressPostProcessing;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "flvrecording_id")
	@ElementList(name = "flvrecordingmetadatas", required = false)
	private List<FlvRecordingMetaData> flvRecordingMetaData;

	@Column(name = "status")
	@Enumerated(value = EnumType.STRING)
	@Element(data = true, required = false)
	private Status status = Status.NONE;
	
	// Not Mapped
	@Transient
	private User creator;

	@Transient
	private Room room;

	@Transient
	private List<FlvRecordingLog> flvRecordingLog;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public Long getOrganization_id() {
		return organization_id;
	}

	public void setOrganization_id(Long organization_id) {
		this.organization_id = organization_id;
	}

	public List<FlvRecordingMetaData> getFlvRecordingMetaData() {
		return flvRecordingMetaData;
	}

	public void setFlvRecordingMetaData(
			List<FlvRecordingMetaData> flvRecordingMetaData) {
		this.flvRecordingMetaData = flvRecordingMetaData;
	}

	public User getCreator() {
		return creator;
	}

	public void setCreator(User creator) {
		this.creator = creator;
	}

	public Room getRoom() {
		return room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}

	public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	public String getAlternateDownload() {
		return alternateDownload;
	}

	public void setAlternateDownload(String alternateDownload) {
		this.alternateDownload = alternateDownload;
	}

	public List<FlvRecordingLog> getFlvRecordingLog() {
		return flvRecordingLog;
	}

	public void setFlvRecordingLog(List<FlvRecordingLog> flvRecordingLog) {
		this.flvRecordingLog = flvRecordingLog;
	}

	public Boolean getIsInterview() {
		return isInterview;
	}

	public void setIsInterview(Boolean isInterview) {
		this.isInterview = isInterview;
	}

	public Integer getProgressPostProcessing() {
		return progressPostProcessing;
	}

	public void setProgressPostProcessing(Integer progressPostProcessing) {
		this.progressPostProcessing = progressPostProcessing;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}
}
