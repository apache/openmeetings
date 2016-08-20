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

import java.io.File;
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

	@Column(name = "name")
	@Element(name = "fileName", data = true, required = false)
	private String name;

	@Column(name = "hash")
	@Element(name = "fileHash", data = true, required = false)
	private String hash;

	@Column(name = "parent_item_id")
	@Element(data = true, name = "parentFileExplorerItemId", required = false)
	private Long parentId;

	@Column(name = "room_id")
	@Element(data = true, required = false, name = "room_id")
	private Long roomId;

	// OwnerID => only set if its directly root in Owner Directory, other Folders and Files
	// maybe are also in a Home directory but just because their parent is
	@Column(name = "owner_id")
	@Element(data = true, required = false)
	private Long ownerId;

	@Column(name = "inserted_by")
	@Element(data = true, required = false)
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
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

	public File getFile() {
		return getFile(null);
	}

	protected abstract File internalGetFile(String ext);
	
	public final File getFile(String ext) {
		return internalGetFile(ext);
	}

	public final boolean exists() {
		return exists(null);
	}

	public final boolean exists(String ext) {
		if (getId() != null && !isDeleted()) {
			File f = getFile(ext);
			return f != null && f.exists() && f.isFile();
		}
		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((hash == null) ? 0 : hash.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((ownerId == null) ? 0 : ownerId.hashCode());
		result = prime * result + ((parentId == null) ? 0 : parentId.hashCode());
		result = prime * result + ((roomId == null) ? 0 : roomId.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FileItem other = (FileItem) obj;
		if (hash == null) {
			if (other.hash != null)
				return false;
		} else if (!hash.equals(other.hash))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (ownerId == null) {
			if (other.ownerId != null)
				return false;
		} else if (!ownerId.equals(other.ownerId))
			return false;
		if (parentId == null) {
			if (other.parentId != null)
				return false;
		} else if (!parentId.equals(other.parentId))
			return false;
		if (roomId == null) {
			if (other.roomId != null)
				return false;
		} else if (!roomId.equals(other.roomId))
			return false;
		if (type != other.type)
			return false;
		return true;
	}
}
