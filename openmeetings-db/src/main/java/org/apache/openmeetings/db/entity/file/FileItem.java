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
package org.apache.openmeetings.db.entity.file;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;
import javax.xml.bind.annotation.XmlType;

import org.apache.openmeetings.db.entity.IDataProviderEntity;
import org.simpleframework.xml.Element;

@MappedSuperclass
public abstract class FileItem implements IDataProviderEntity {
	private static final long serialVersionUID = 1L;

	@XmlType(namespace = "org.apache.openmeetings.file")
	public enum Type {
		// Folder need to be alphabetically first, for correct sorting
		Folder, Image, PollChart, Presentation, Recording, Video, WmlFile
	}

	@Column(name = "filename")
	@Element(data = true, required = false)
	private String fileName;

	@Column(name = "filehash")
	@Element(data = true, required = false)
	private String fileHash;

	@Column(name = "parent_fileexploreritem_id")
	@Element(data = true, name = "parentFileExplorerItemId")
	private Long parentItemId;

	@Column(name = "room_id")
	@Element(data = true, required = false)
	private Long roomId;

	// OwnerID => only set if its directly root in Owner Directory, other Folders and Files
	// maybe are also in a Home directory but just because their parent is
	@Column(name = "owner_id")
	@Element(data = true, required = false)
	private Long ownerId;

	@Column(name = "inserted_by")
	@Element(data = true)
	private Long insertedBy;

	@Column(name = "inserted")
	@Element(data = true)
	private Date inserted;

	@Column(name = "updated")
	@Element(data = true, required = false)
	private Date updated;

	@Column(name = "deleted")
	@Element(data = true)
	private boolean deleted;

	@Column(name = "flv_width")
	@Element(data = true, required = false)
	private Integer flvWidth;

	@Column(name = "flv_height")
	@Element(data = true, required = false)
	private Integer flvHeight;

	@Column(name = "preview_image")
	@Element(data = true, required = false)
	private String previewImage;

	@Column(name = "type")
	@Element(data = true, required = false)
	@Enumerated(EnumType.STRING)
	private Type type;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileHash() {
		return fileHash;
	}

	public void setFileHash(String fileHash) {
		this.fileHash = fileHash;
	}

	public Long getParentItemId() {
		return parentItemId;
	}

	public void setParentItemId(Long parentItemId) {
		this.parentItemId = parentItemId;
	}

	public Long getRoomId() {
		return roomId;
	}

	public void setRoomId(Long roomId) {
		this.roomId = roomId;
	}

	public Long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
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

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
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

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}
}
