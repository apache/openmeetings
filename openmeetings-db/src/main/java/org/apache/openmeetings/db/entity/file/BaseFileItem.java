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

import static org.apache.commons.lang3.math.NumberUtils.toInt;
import static org.apache.openmeetings.util.OmFileHelper.DOC_PAGE_PREFIX;
import static org.apache.openmeetings.util.OmFileHelper.EXTENSION_JPG;
import static org.apache.openmeetings.util.OmFileHelper.EXTENSION_MP4;
import static org.apache.openmeetings.util.OmFileHelper.EXTENSION_PDF;
import static org.apache.openmeetings.util.OmFileHelper.EXTENSION_PNG;
import static org.apache.openmeetings.util.OmFileHelper.EXTENSION_WML;
import static org.apache.openmeetings.util.OmFileHelper.getFileSafe;
import static org.apache.openmeetings.util.OmFileHelper.getStreamsHibernateDir;
import static org.apache.openmeetings.util.OmFileHelper.getUploadFilesDir;
import static org.apache.openmeetings.util.OmFileHelper.getUploadWmlDir;

import java.io.File;
import java.io.FileFilter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.openmeetings.db.bind.adapter.FileTypeAdapter;
import org.apache.openmeetings.db.bind.adapter.IntAdapter;
import org.apache.openmeetings.db.bind.adapter.LongAdapter;
import org.apache.openmeetings.db.entity.HistoricalEntity;
import org.apache.openmeetings.util.OmFileHelper;

@Entity
@NamedQuery(name = "getFileById", query = "SELECT f FROM BaseFileItem f WHERE f.deleted = false AND f.id = :id")
@NamedQuery(name = "getAnyFileById", query = "SELECT f FROM BaseFileItem f WHERE f.id = :id")
@NamedQuery(name = "getFileByHash", query = "SELECT f FROM BaseFileItem f WHERE f.deleted = false AND f.hash = :hash")
@NamedQuery(name = "getAnyFileByHash", query = "SELECT f FROM BaseFileItem f WHERE f.hash = :hash")
@NamedQuery(name = "getAllFileItemsForRoom", query = "SELECT f FROM BaseFileItem f"
		+ " WHERE f.deleted = false AND f.type <> :folder"
		+ " AND (f.roomId IS NULL OR f.roomId = :roomId)"
		+ " AND (f.groupId IS NULL OR f.groupId IN :groups)"
		+ " AND f.ownerId IS NULL" // not loading personal files
		+ " AND f.name LIKE :name"
		+ " ORDER BY f.name")
@NamedQuery(name = "getFileItemsByIds", query = "SELECT f FROM BaseFileItem f"
		+ " WHERE f.deleted = false AND f.id IN :ids")
@Table(name = "om_file", indexes = {
		@Index(name = "file_hash_idx", columnList = "hash", unique = true)
})
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class BaseFileItem extends HistoricalEntity {
	private static final long serialVersionUID = 1L;

	@XmlType(namespace = "org.apache.openmeetings.file")
	public enum Type {
		// Folder need to be alphabetically first, for correct sorting
		FOLDER
		, IMAGE
		, POLL_CHART
		, PRESENTATION
		, RECORDING
		, VIDEO
		, WML_FILE
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	@XmlTransient
	private Long id;

	@Column(name = "name")
	@XmlElement(name = "fileName", required = false)
	private String name;

	@Column(name = "hash")
	@XmlElement(name = "fileHash", required = false)
	private String hash;

	@Column(name = "parent_item_id")
	@XmlElement(name = "parentFileExplorerItemId", required = false)
	@XmlJavaTypeAdapter(LongAdapter.class)
	private Long parentId;

	@Column(name = "room_id")
	@XmlElement(name = "room_id", required = false)
	@XmlJavaTypeAdapter(LongAdapter.class)
	private Long roomId;

	// OwnerID => only set if its directly root in Owner Directory, other Folders and Files
	// maybe are also in a Home directory but just because their parent is
	@Column(name = "owner_id")
	@XmlElement(name = "ownerId", required = false)
	@XmlJavaTypeAdapter(LongAdapter.class)
	private Long ownerId;

	@Column(name = "inserted_by")
	@XmlElement(name = "insertedBy", required = false)
	@XmlJavaTypeAdapter(LongAdapter.class)
	private Long insertedBy;

	@Column(name = "width")
	@XmlElement(name = "width", required = false)
	@XmlJavaTypeAdapter(IntAdapter.class)
	private Integer width;

	@Column(name = "height")
	@XmlElement(name = "height", required = false)
	@XmlJavaTypeAdapter(IntAdapter.class)
	private Integer height;

	@Column(name = "type")
	@Enumerated(EnumType.STRING)
	@XmlElement(name = "type", required = false)
	@XmlJavaTypeAdapter(FileTypeAdapter.class)
	private Type type;

	@Column(name = "group_id")
	@XmlElement(name = "groupId", required = false)
	@XmlJavaTypeAdapter(LongAdapter.class)
	private Long groupId;

	@Column(name = "page_count", nullable = false)
	@XmlElement(name = "count", required = false)
	@XmlJavaTypeAdapter(value = IntAdapter.class, type = int.class)
	private int count = 1;

	@Column(name = "external_type")
	@XmlElement(name = "externalType", required = false)
	private String externalType;

	// Not Mapped
	@Transient
	@XmlTransient
	private List<FileItemLog> log;

	@Transient
	@XmlTransient
	private boolean readOnly;

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

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

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public List<FileItemLog> getLog() {
		return log;
	}

	public void setLog(List<FileItemLog> log) {
		this.log = log;
	}

	public String getFileName(String ext) {
		return ext == null ? name : OmFileHelper.getName(name, ext);
	}

	public File getFile() {
		return getFile(null);
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public String getExternalType() {
		return externalType;
	}

	public void setExternalType(String externalType) {
		this.externalType = externalType;
	}

	public final File getFile(String ext) {
		File f = null;
		if (!isDeleted() && getHash() != null) {
			File d = new File(getUploadFilesDir(), getHash());
			switch (getType()) {
				case WML_FILE:
					f = getFileSafe(getUploadWmlDir(), getHash(), ext, EXTENSION_WML);
					break;
				case IMAGE:
					if (ext == null) {
						f = getFileSafe(d, getHash(), EXTENSION_PNG);
						if (!f.exists()) {
							f = getFileSafe(d, getHash(), EXTENSION_JPG); // backward compatibility
						}
					} else {
						f = getFileSafe(d, getHash(), ext);
					}
					break;
				case RECORDING:
					f = getFileSafe(getStreamsHibernateDir(), getHash(), ext, EXTENSION_MP4);
					break;
				case VIDEO:
					f = getFileSafe(d, getHash(), ext, EXTENSION_MP4);
					break;
				case PRESENTATION:
					int slide = ext == null ? 0 : toInt(ext, -1);
					if (slide > -1) {
						f = new File(d, String.format("%1$s-%2$04d.%3$s", DOC_PAGE_PREFIX, slide, EXTENSION_PNG));
					} else {
						f = getFileSafe(d, getHash(), ext, EXTENSION_PDF);
					}
					break;
				case POLL_CHART, FOLDER:
				default:
			}
		}
		return f;
	}

	public final File getOriginal() {
		File f = getFile(Type.PRESENTATION == type ? EXTENSION_PDF : null);
		if (f != null) {
			File p = f.getParentFile();
			if (p != null && p.exists()) {
				File[] ff = p.listFiles(new OriginalFilter());
				if (ff != null && ff.length > 0) {
					f = ff[0];
				}
			}
		}
		return f;
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
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		BaseFileItem other = (BaseFileItem) obj;
		if (hash == null) {
			if (other.hash != null) {
				return false;
			}
		} else if (!hash.equals(other.hash)) {
			return false;
		}
		return type == other.type;
	}

	private class OriginalFilter implements FileFilter {
		Set<String> exclusions = new HashSet<>();

		OriginalFilter() {
			exclusions.add(EXTENSION_PNG);
			exclusions.add("swf");
			if (Type.PRESENTATION == getType()) {
				exclusions.add(EXTENSION_PDF);
			}
		}

		@Override
		public boolean accept(File f) {
			String n = f.getName();
			String ext = OmFileHelper.getFileExt(n);
			return n.startsWith(getHash()) && !exclusions.contains(ext);
		}
	}

	@Override
	public String toString() {
		return "FileItem [id=" + id + ", name=" + name + ", room=" + roomId + ", type=" + type.name() + "]";
	}
}
