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
package org.apache.openmeetings.persistence.beans.flvrecord;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
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

import org.apache.openmeetings.persistence.beans.room.Room;
import org.apache.openmeetings.persistence.beans.user.User;
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
	@NamedQuery(name = "getRecordingByHash", query = "SELECT f FROM FlvRecording f "
		+ "WHERE f.fileHash = :fileHash") 
})
@Table(name = "flvrecording")
@Root(name = "flvrecording")
public class FlvRecording implements Serializable {

	private static final long serialVersionUID = -2234874663310617072L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	@Element(data = true)
	private long flvRecordingId;

	@Column(name = "filename")
	@Element(data = true, required = false)
	private String fileName;

	@Column(name = "alternate_download")
	@Element(data = true, required = false)
	private String alternateDownload;

	@Column(name = "filehash")
	@Element(data = true, required = false)
	private String fileHash;

	@Column(name = "comment_field")
	@Element(data = true, required = false)
	private String comment;

	@Column(name = "parent_fileexploreritem_id")
	@Element(data = true, required = false)
	private Long parentFileExplorerItemId;

	@Column(name = "room_id")
	@Element(data = true, required = false)
	private Long room_id;

	@Column(name = "owner_id")
	@Element(data = true, required = false)
	private Long ownerId;// OwnerID => only set if its directly root in Owner
							// Directory, other Folders and Files
							// maybe are also in a Home directory but just
							// because their parent is

	@Column(name = "is_folder")
	@Element(data = true, required = false)
	private Boolean isFolder;

	@Column(name = "is_image")
	@Element(data = true, required = false)
	private Boolean isImage;

	@Column(name = "is_presentation")
	@Element(data = true, required = false)
	private Boolean isPresentation;

	@Column(name = "is_recording")
	@Element(data = true, required = false)
	private Boolean isRecording;

	@Column(name = "record_start")
	@Element(data = true, required = false)
	private Date recordStart;

	@Column(name = "record_end")
	@Element(data = true, required = false)
	private Date recordEnd;

	@Column(name = "inserted_by")
	@Element(data = true, required = false)
	private Long insertedBy;

	@Column(name = "inserted")
	@Element(data = true, required = false)
	private Date inserted;

	@Column(name = "updated")
	private Date updated;

	@Column(name = "deleted")
	@Element(data = true, required = false)
	private boolean deleted;

	@Column(name = "width")
	@Element(data = true, required = false)
	private Integer width;

	@Column(name = "height")
	@Element(data = true, required = false)
	private Integer height;

	@Column(name = "flv_width")
	@Element(data = true, required = false)
	private Integer flvWidth;

	@Column(name = "flv_height")
	@Element(data = true, required = false)
	private Integer flvHeight;

	@Column(name = "preview_image")
	@Element(data = true, required = false)
	private String previewImage;

	@Column(name = "filesize")
	@Element(data = true, required = false)
	private Long fileSize;

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

	// Not Mapped
	@Transient
	private User creator;

	@Transient
	private Room room;

	@Transient
	private List<FlvRecordingLog> flvRecordingLog;

	public long getFlvRecordingId() {
		return flvRecordingId;
	}

	public void setFlvRecordingId(long flvRecordingId) {
		this.flvRecordingId = flvRecordingId;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getFileHash() {
		return fileHash;
	}

	public void setFileHash(String fileHash) {
		this.fileHash = fileHash;
	}

	public Long getParentFileExplorerItemId() {
		return parentFileExplorerItemId;
	}

	public void setParentFileExplorerItemId(Long parentFileExplorerItemId) {
		this.parentFileExplorerItemId = parentFileExplorerItemId;
	}

	public Long getRoom_id() {
		return room_id;
	}

	public void setRoom_id(Long room_id) {
		this.room_id = room_id;
	}

	public Long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}

	public Boolean getIsFolder() {
		return isFolder;
	}

	public void setIsFolder(Boolean isFolder) {
		this.isFolder = isFolder;
	}

	public Boolean getIsImage() {
		return isImage;
	}

	public void setIsImage(Boolean isImage) {
		this.isImage = isImage;
	}

	public Boolean getIsPresentation() {
		return isPresentation;
	}

	public void setIsPresentation(Boolean isPresentation) {
		this.isPresentation = isPresentation;
	}

	public Boolean getIsRecording() {
		return isRecording;
	}

	public void setIsRecording(Boolean isRecording) {
		this.isRecording = isRecording;
	}

	public Long getInsertedBy() {
		return insertedBy;
	}

	public void setInsertedBy(Long insertedBy) {
		this.insertedBy = insertedBy;
	}

	public Date getInserted() {
		return inserted;
	}

	public void setInserted(Date inserted) {
		this.inserted = inserted;
	}

	public boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	public Long getFileSize() {
		return fileSize;
	}

	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
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

	public Integer getFlvWidth() {
		return flvWidth;
	}

	public void setFlvWidth(Integer flvWidth) {
		this.flvWidth = flvWidth;
	}

	public Integer getFlvHeight() {
		return flvHeight;
	}

	public void setFlvHeight(Integer flvHeight) {
		this.flvHeight = flvHeight;
	}

	public String getPreviewImage() {
		return previewImage;
	}

	public void setPreviewImage(String previewImage) {
		this.previewImage = previewImage;
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

}
