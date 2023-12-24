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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.apache.openmeetings.db.entity.file.BaseFileItem.Type;
import org.apache.openmeetings.db.entity.file.FileItem;

/**
 * This Object will represent a File on the File-System
 *
 * @author sebastianwagner
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class FileItemDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long id;
	private String name;
	private String hash;
	private Long parentId;
	private Long roomId;
	private Long groupId;
	private Long ownerId;
	private Long size;
	private String externalId;
	private String externalType;
	private Type type;
	private Integer width;
	private Integer height;

	public FileItemDTO() {
		//def constructor
	}

	public FileItemDTO(FileItem f) {
		id = f.getId();
		name = f.getName();
		hash = f.getHash();
		parentId = f.getParentId();
		roomId = f.getRoomId();
		groupId = f.getGroupId();
		ownerId = f.getOwnerId();
		size = f.getSize();
		externalId = f.getExternalId();
		externalType = f.getExternalType();
		type = f.getType();
		width = f.getWidth();
		height = f.getHeight();
	}

	public FileItem get() {
		FileItem f = new FileItem();
		f.setId(id);
		f.setName(name);
		f.setHash(hash);
		f.setParentId(parentId != null && parentId > 0 ? parentId : null);
		f.setRoomId(roomId != null && roomId > 0 ? roomId : null);
		f.setRoomId(groupId != null && groupId > 0 ? groupId : null);
		f.setOwnerId(ownerId != null && ownerId > 0 ? ownerId : null);
		f.setSize(size);
		f.setExternalId(externalId);
		f.setExternalType(externalType);
		f.setType(type);
		f.setWidth(width);
		f.setHeight(height);
		return f;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public FileItemDTO setName(String name) {
		this.name = name;
		return this;
	}

	public String getHash() {
		return hash;
	}

	public FileItemDTO setHash(String hash) {
		this.hash = hash;
		return this;
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

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public Long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public String getExternalId() {
		return externalId;
	}

	public FileItemDTO setExternalId(String externalId) {
		this.externalId = externalId;
		return this;
	}

	public String getExternalType() {
		return externalType;
	}

	public FileItemDTO setExternalType(String externalType) {
		this.externalType = externalType;
		return this;
	}

	public Type getType() {
		return type;
	}

	public FileItemDTO setType(Type type) {
		this.type = type;
		return this;
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

	public static List<FileItemDTO> list(List<FileItem> l) {
		List<FileItemDTO> list = new ArrayList<>();
		if (l != null) {
			for (FileItem f : l) {
				list.add(new FileItemDTO(f));
			}
		}
		return list;
	}
}
