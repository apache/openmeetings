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

import static org.apache.openmeetings.util.OmFileHelper.EXTENSION_JPG;
import static org.apache.openmeetings.util.OmFileHelper.EXTENSION_MP4;
import static org.apache.openmeetings.util.OmFileHelper.EXTENSION_SWF;
import static org.apache.openmeetings.util.OmFileHelper.EXTENSION_WML;
import static org.apache.openmeetings.util.OmFileHelper.WB_VIDEO_FILE_PREFIX;

import java.io.File;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.apache.openmeetings.util.OmFileHelper;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Entity
@NamedQueries({
	@NamedQuery(name = "getAllFiles", query = "SELECT c FROM FileExplorerItem c ORDER BY c.id")
	, @NamedQuery(name = "getFileById", query = "SELECT c FROM FileExplorerItem c WHERE c.id = :id")
	, @NamedQuery(name = "getFileByHash", query = "SELECT c FROM FileExplorerItem c WHERE c.hash = :hash")
	, @NamedQuery(name = "getFilesByRoomAndOwner", query = "SELECT c FROM FileExplorerItem c WHERE c.deleted = false "
			+ " AND c.roomId = :roomId AND c.ownerId = :ownerId ORDER BY c.type ASC, c.name ")
	, @NamedQuery(name = "getFilesByRoom", query = "SELECT c FROM FileExplorerItem c WHERE c.deleted = false AND c.roomId = :roomId " +
			"AND c.ownerId IS NULL AND c.parentId IS NULL ORDER BY c.type ASC, c.name ")
	, @NamedQuery(name = "getFilesByOwner", query = "SELECT c FROM FileExplorerItem c WHERE c.deleted = false AND c.ownerId = :ownerId "
			+ "AND c.parentId IS NULL ORDER BY c.type ASC, c.name ")
	, @NamedQuery(name = "getFilesByParent", query = "SELECT c FROM FileExplorerItem c WHERE c.deleted = false "
			+ "AND c.parentId = :parentId ORDER BY c.type ASC, c.name ")
	, @NamedQuery(name = "getFileExternal", query = "SELECT c FROM FileExplorerItem c WHERE c.externalId = :externalId AND c.externalType LIKE :externalType")
})
@Table(name = "fileexploreritem")
@Root
public class FileExplorerItem extends FileItem {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	@Element(data = true, name = "fileExplorerItemId")
	private Long id;

	@Column(name = "filesize")
	@Element(data = true, required = false)
	private Long size;

	@Column(name = "external_id")
	private String externalId;

	@Column(name = "external_type")
	private String externalType;

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long fileSize) {
		this.size = fileSize;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public String getExternalType() {
		return externalType;
	}

	public void setExternalType(String externalType) {
		this.externalType = externalType;
	}

	@Override
	protected File internalGetFile(String ext) {
		File f = null;
		switch (getType()) {
			case WmlFile:
				f = new File(OmFileHelper.getUploadWmlDir(), String.format("%s.%s", getHash(), ext == null ? EXTENSION_WML : ext));
				break;
			case Image:
				f = new File(OmFileHelper.getUploadFilesDir(), String.format("%s.%s", getHash(), ext == null ? EXTENSION_JPG : ext));
				break;
			case Video:
				f = new File(OmFileHelper.getStreamsHibernateDir(), String.format("%s%s.%s", WB_VIDEO_FILE_PREFIX, getId(), ext == null ? EXTENSION_MP4 : ext));
				break;
			case Presentation: {
					File d = new File(OmFileHelper.getUploadFilesDir(), getHash());
					f = new File(d, String.format("%s.%s", getHash(), ext == null ? EXTENSION_SWF : ext));
				}
				break;
			case PollChart:
			case Folder:
			default:
		}
		return f;
	}
}
