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
package org.apache.openmeetings.db.dto.file;

import java.util.Date;

import org.apache.openmeetings.db.entity.file.FileItem.Type;
import org.apache.openmeetings.db.entity.record.FlvRecording;

public class RecordingObject {
	private Long id;
	
	private String fileName;
	private String alternateDownload;
	private String fileHash;
	private String comment;
	
	private Long parentItemId;
	private Long room_id;
	private Long ownerId;//OwnerID => only set if its directly root in Owner Directory, other Folders and Files
	//maybe are also in a Home directory but just because their parent is
	
	private Type type;
	
	private Date recordStart;
	private Date recordEnd;
	
	private Long insertedBy;
	private Date inserted;
	private Date updated;
	private boolean deleted;
	
	private Integer width;
	private Integer height;
	
	private Integer flvWidth;
	private Integer flvHeight;
	private String previewImage;
	
	private String duration;
	
	private String recorderStreamId;
	private Long organization_id;
	
	private Boolean isInterview;
	private Integer progressPostProcessing;
	
	public RecordingObject() {}
	
	public RecordingObject(FlvRecording customObject) {
		this.id = customObject.getId();
		
		this.fileName = customObject.getFileName();
		this.alternateDownload = customObject.getAlternateDownload();
		this.fileHash = customObject.getFileHash();
		this.comment = customObject.getComment();
		
		this.parentItemId = customObject.getParentItemId();
		this.room_id = customObject.getRoomId();
		this.ownerId = customObject.getOwnerId();//OwnerID => only set if its directly root in Owner Directory, other Folders and Files
		//maybe are also in a Home directory but just because their parent is
		
		this.type = customObject.getType();
		
		this.recordStart = customObject.getRecordStart();
		this.recordEnd = customObject.getRecordEnd();
		
		this.insertedBy = customObject.getInsertedBy();
		this.inserted = customObject.getInserted();
		this.updated = customObject.getUpdated();
		this.deleted = customObject.isDeleted();
		
		this.width = customObject.getWidth();
		this.height = customObject.getHeight();
		
		this.flvWidth = customObject.getFlvWidth();
		this.flvHeight = customObject.getFlvHeight();
		this.previewImage = customObject.getPreviewImage();
		
		this.duration = customObject.getDuration();
		
		this.recorderStreamId = customObject.getRecorderStreamId();
		this.organization_id = customObject.getOrganization_id();
		
		this.isInterview = customObject.getIsInterview();
		this.progressPostProcessing = customObject.getProgressPostProcessing();
	}

	public long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getAlternateDownload() {
		return alternateDownload;
	}

	public void setAlternateDownload(String alternateDownload) {
		this.alternateDownload = alternateDownload;
	}

	public String getFileHash() {
		return fileHash;
	}

	public void setFileHash(String fileHash) {
		this.fileHash = fileHash;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Long getParentItemId() {
		return parentItemId;
	}

	public void setParentItemId(Long parentItemId) {
		this.parentItemId = parentItemId;
	}

	public Long getRoom_id() {
		return room_id;
	}

	public void setRoom_id(Long roomId) {
		room_id = roomId;
	}

	public Long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
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

	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	public boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
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

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
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

	public void setOrganization_id(Long organizationId) {
		organization_id = organizationId;
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
