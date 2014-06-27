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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Entity
@NamedQueries({
	@NamedQuery(name = "getAllFiles", query = "SELECT c FROM FileExplorerItem c ORDER BY c.id")
	, @NamedQuery(name = "getFileById", query = "SELECT c FROM FileExplorerItem c WHERE c.id = :id")
	, @NamedQuery(name = "getFileByHash", query = "SELECT c FROM FileExplorerItem c WHERE c.fileHash = :fileHash")
	, @NamedQuery(name = "getFilesByRoomAndOwner", query = "SELECT c FROM FileExplorerItem c WHERE c.deleted = false "
			+ " AND c.roomId = :roomId AND c.ownerId = :ownerId ORDER BY c.type ASC, c.fileName ")
	, @NamedQuery(name = "getFilesByRoom", query = "SELECT c FROM FileExplorerItem c WHERE c.deleted = false AND c.roomId = :roomId " +
			"AND c.ownerId IS NULL AND c.parentItemId = :parentItemId ORDER BY c.type ASC, c.fileName ")
	, @NamedQuery(name = "getFilesByOwner", query = "SELECT c FROM FileExplorerItem c WHERE c.deleted = false AND c.ownerId = :ownerId "
			+ "AND c.parentItemId = :parentItemId ORDER BY c.type ASC, c.fileName ")
	, @NamedQuery(name = "getFilesByParent", query = "SELECT c FROM FileExplorerItem c WHERE c.deleted = false "
			+ "AND c.parentItemId = :parentItemId ORDER BY c.type ASC, c.fileName ")
	, @NamedQuery(name = "getFileExternal", query = "SELECT c FROM FileExplorerItem c WHERE c.externalFileId = :externalFileId "
			+ "AND c.externalType LIKE :externalType")
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
	private Long fileSize;

	@Column(name = "wml_file_path")
	@Element(data = true, required = false)
	private String wmlFilePath;

	@Column(name = "external_file_id")
	private Long externalFileId;

	@Column(name = "external_type")
	private String externalType;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getFileSize() {
		return fileSize;
	}

	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
	}

	public String getWmlFilePath() {
		return wmlFilePath;
	}

	public void setWmlFilePath(String wmlFilePath) {
		this.wmlFilePath = wmlFilePath;
	}

	public Long getExternalFileId() {
		return externalFileId;
	}

	public void setExternalFileId(Long externalFileId) {
		this.externalFileId = externalFileId;
	}

	public String getExternalType() {
		return externalType;
	}

	public void setExternalType(String externalType) {
		this.externalType = externalType;
	}

}
