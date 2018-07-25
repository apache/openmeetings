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
import static org.apache.openmeetings.util.OmFileHelper.FILE_NAME_FMT;
import static org.apache.openmeetings.util.OmFileHelper.getStreamsHibernateDir;
import static org.apache.openmeetings.util.OmFileHelper.getUploadFilesDir;
import static org.apache.openmeetings.util.OmFileHelper.getUploadWmlDir;

import java.io.File;
import java.io.FileFilter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlType;

import org.apache.openmeetings.db.entity.HistoricalEntity;
import org.apache.openmeetings.util.OmFileHelper;
import org.simpleframework.xml.Element;

@Entity
@NamedQueries({
	@NamedQuery(name = "getFileById", query = "SELECT f FROM BaseFileItem f WHERE f.deleted = false AND f.id = :id")
	, @NamedQuery(name = "getAnyFileById", query = "SELECT f FROM BaseFileItem f WHERE f.id = :id")
	, @NamedQuery(name = "getFileByHash", query = "SELECT f FROM BaseFileItem f WHERE f.deleted = false AND f.hash = :hash")
	, @NamedQuery(name = "getAllFileItemsForRoom", query = "SELECT f FROM BaseFileItem f"
			+ " WHERE f.deleted = false AND f.type <> :folder"
			+ " AND (f.roomId IS NULL OR f.roomId = :roomId)"
			+ " AND (f.groupId IS NULL OR f.groupId IN :groups)"
			+ " AND f.ownerId IS NULL" // not loading personal files
			+ " AND f.name LIKE :name"
			+ " ORDER BY f.name")
	, @NamedQuery(name = "getFileItemsByIds", query = "SELECT f FROM BaseFileItem f"
			+ " WHERE f.deleted = false AND f.id IN :ids")
})
@Table(name = "om_file")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class BaseFileItem extends HistoricalEntity {
	private static final long serialVersionUID = 1L;

	@XmlType(namespace = "org.apache.openmeetings.file")
	public enum Type {
		// Folder need to be alphabetically first, for correct sorting
		Folder, Image, PollChart, Presentation, Recording, Video, WmlFile
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

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

	@Column(name = "width")
	@Element(data = true, required = false)
	private Integer width;

	@Column(name = "height")
	@Element(data = true, required = false)
	private Integer height;

	@Column(name = "type")
	@Element(data = true, required = false)
	@Enumerated(EnumType.STRING)
	private Type type;

	@Column(name = "group_id")
	@Element(data = true, required = false)
	private Long groupId;

	@Column(name = "page_count", nullable = false)
	@Element(data = true, required = false)
	private int count = 1;

	// Not Mapped
	@Transient
	private List<FileItemLog> log;

	@Transient
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
		return ext == null ? name : String.format(FILE_NAME_FMT, name, ext);
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

	public final File getFile(String ext) {
		File f = null;
		if (!isDeleted() && getHash() != null) {
			File d = new File(getUploadFilesDir(), getHash());
			switch (getType()) {
				case WmlFile:
					f = new File(getUploadWmlDir(), String.format(FILE_NAME_FMT, getHash(), ext == null ? EXTENSION_WML : ext));
					break;
				case Image:
					if (ext == null) {
						f = new File(d, String.format(FILE_NAME_FMT, getHash(), EXTENSION_PNG));
						if (!f.exists()) {
							f = new File(d, String.format(FILE_NAME_FMT, getHash(), EXTENSION_JPG)); // backward compatibility
						}
					} else {
						f = new File(d, String.format(FILE_NAME_FMT, getHash(), ext));
					}
					break;
				case Recording:
					f = new File(getStreamsHibernateDir(), String.format(FILE_NAME_FMT, getHash(), ext == null ? EXTENSION_MP4 : ext));
					break;
				case Video:
					f = new File(d, String.format(FILE_NAME_FMT, getHash(), ext == null ? EXTENSION_MP4 : ext));
					break;
				case Presentation:
					int slide;
					if (ext == null) {
						slide = 0;
					} else {
						slide = toInt(ext, -1);
					}
					if (slide > -1) {
						f = new File(d, String.format("%1$s-%2$04d.%3$s", DOC_PAGE_PREFIX, slide, EXTENSION_PNG));
					} else {
						f = new File(d, String.format(FILE_NAME_FMT, getHash(), ext == null ? EXTENSION_PDF : ext));
					}
					break;
				case PollChart:
				case Folder:
				default:
			}
		}
		return f;
	}

	public final File getOriginal() {
		File f = getFile(null);
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
		if (type != other.type) {
			return false;
		}
		return true;
	}

	private class OriginalFilter implements FileFilter {
		Set<String> exclusions = new HashSet<>();

		OriginalFilter() {
			exclusions.add(EXTENSION_PNG);
			exclusions.add("swf");
			if (Type.Presentation == getType()) {
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
