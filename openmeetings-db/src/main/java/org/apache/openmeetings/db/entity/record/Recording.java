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

import static org.apache.openmeetings.util.OmFileHelper.EXTENSION_AVI;
import static org.apache.openmeetings.util.OmFileHelper.EXTENSION_FLV;
import static org.apache.openmeetings.util.OmFileHelper.EXTENSION_OGG;
import static org.apache.openmeetings.util.OmFileHelper.EXTENSION_MP4;
import static org.apache.openmeetings.util.OmFileHelper.getRecording;
import static org.apache.openmeetings.util.OmFileHelper.recordingFileName;

import java.io.File;
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
	@NamedQuery(name = "getRecordingById", query = "SELECT f FROM Recording f WHERE f.id = :id") 
	, @NamedQuery(name = "getRecordingByHash", query = "SELECT f FROM Recording f WHERE f.hash = :hash") 
	, @NamedQuery(name = "getRecordingsByExternalUser", query = "SELECT c FROM Recording c, User u "
			+ "WHERE c.insertedBy = u.id AND u.externalId = :externalId  AND u.externalType = :externalType "
			+ "AND c.deleted = false") 
	, @NamedQuery(name = "getRecordingsPublic", query = "SELECT f FROM Recording f WHERE f.deleted = false AND f.ownerId IS NULL "
			+ "AND f.groupId IS NULL AND (f.parentId IS NULL OR f.parentId = 0) "
			+ "ORDER BY f.type DESC, f.inserted")
	, @NamedQuery(name = "getRecordingsByGroup", query = "SELECT f FROM Recording f WHERE f.deleted = false AND f.ownerId IS NULL "
			+ "AND f.groupId = :groupId AND (f.parentId IS NULL OR f.parentId = 0) "
			+ "ORDER BY f.type DESC, f.inserted")
	, @NamedQuery(name = "getRecordingsByOwner", query = "SELECT f FROM Recording f WHERE f.deleted = false AND f.ownerId = :ownerId "
			+ "AND (f.parentId IS NULL OR f.parentId = 0) "
			+ "ORDER BY f.type DESC, f.inserted")
	, @NamedQuery(name = "resetRecordingProcessingStatus", query = "UPDATE Recording f SET f.status = :error WHERE f.status IN (:recording, :converting)")
	, @NamedQuery(name = "getRecordingsAll", query = "SELECT c FROM Recording c LEFT JOIN FETCH c.metaData ORDER BY c.id")
	, @NamedQuery(name = "getRecordingsByExternalTypeAndOwner", query = "SELECT c FROM Recording c, Room r WHERE c.roomId = r.id "
			+ "AND r.externalType LIKE :externalType AND c.insertedBy LIKE :insertedBy AND c.deleted = false")
	, @NamedQuery(name = "getRecordingsByRoom", query = "SELECT c FROM Recording c WHERE c.deleted = false AND c.roomId = :roomId "
			+ "ORDER BY c.type ASC, c.inserted")
	, @NamedQuery(name = "getRecordingsByParent", query = "SELECT f FROM Recording f WHERE f.deleted = false AND f.parentId = :parentId "
			+ "ORDER BY f.type ASC, f.inserted")
	, @NamedQuery(name = "getRecordingsByExternalType", query = "SELECT rec FROM Recording rec, Room r, User u "
			+ "WHERE rec.deleted = false AND rec.roomId = r.id AND rec.insertedBy = u.id "
			+ "AND (r.externalType = :externalType OR u.externalType = :externalType)")
})
@Table(name = "recording")
@Root(name = "flvrecording")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Recording extends FileItem {
	private static final long serialVersionUID = 1L;
	
	@XmlType(namespace="org.apache.openmeetings.record")
	public enum Status {
		NONE
		, RECORDING
		, CONVERTING
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

	@Column(name = "comment")
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

	@Column(name = "group_id")
	@Element(data = true, required = false)
	private Long groupId;

	@Column(name = "is_interview")
	@Element(data = true, required = false)
	private boolean interview;

	@Column(name = "progress_post_processing")
	@Element(data = true, required = false)
	private Integer progressPostProcessing;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "recording_id")
	@ElementList(name = "flvrecordingmetadatas", required = false)
	private List<RecordingMetaData> metaData;

	@Column(name = "status")
	@Enumerated(value = EnumType.STRING)
	@Element(data = true, required = false)
	private Status status = Status.NONE;
	
	// Not Mapped
	@Transient
	private List<RecordingLog> log;

	@Override
	public Long getId() {
		return id;
	}

	@Override
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

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public List<RecordingMetaData> getMetaData() {
		return metaData;
	}

	public void setMetaData(List<RecordingMetaData> metaData) {
		this.metaData = metaData;
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

	public List<RecordingLog> getLog() {
		return log;
	}

	public void setLog(List<RecordingLog> log) {
		this.log = log;
	}

	public boolean isInterview() {
		return interview;
	}

	public void setInterview(boolean interview) {
		this.interview = interview;
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

	@Override
	public File internalGetFile(String ext) {
		File f = null;
		if (getId() != null && !isDeleted()) {
			if (ext == null || EXTENSION_MP4.equals(ext)) {
				f = getRecording(String.format("%s.%s.%s", recordingFileName, EXTENSION_FLV, EXTENSION_AVI));
			} else if (EXTENSION_FLV.equals(ext)) {
				f = getRecording(String.format("%s.%s", recordingFileName, EXTENSION_FLV));
			} else if (EXTENSION_AVI.equals(ext)) {
				f = getRecording(String.format("%s.%s", recordingFileName, EXTENSION_AVI));
			} else if (EXTENSION_OGG.equals(ext)) {
				f = getRecording(String.format("%s.%s.%s", recordingFileName, EXTENSION_FLV, EXTENSION_AVI));
			}
		}
		return f;
	}
}
