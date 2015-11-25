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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.openmeetings.db.entity.file.FileExplorerItem;
import org.apache.openmeetings.db.entity.file.FileItem.Type;

/**
 * This Object will represent a File on the File-System
 * 
 * @author sebastianwagner
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class FileExplorerItemDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long id;
	private String name;
	private String hash;
	private Long parentId;
	private Long roomId;
	private Long size;
	private String externalId;
	private String externalType;
	private Type type;
	private Integer flvWidth;
	private Integer flvHeight;

	public FileExplorerItemDTO() {}

	public FileExplorerItemDTO(FileExplorerItem f) {
		id = f.getId();
		name = f.getName();
		hash = f.getHash();
		parentId = f.getParentId();
		roomId = f.getRoomId();
		size = f.getSize();
		externalId = f.getExternalId();
		externalType = f.getExternalType();
		type = f.getType();
		flvWidth = f.getFlvWidth();
		flvHeight = f.getFlvHeight();
	}
	
	public FileExplorerItem get() {
		FileExplorerItem f = new FileExplorerItem();
		f.setId(id);
		f.setName(name);
		f.setHash(hash);
		f.setParentId(parentId);
		f.setRoomId(roomId);
		f.setSize(size);
		f.setExternalId(externalId);
		f.setExternalType(externalType);
		f.setType(type);
		f.setFlvWidth(flvWidth);
		f.setFlvHeight(flvHeight);
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

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
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

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
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

	public static List<FileExplorerItemDTO> list(List<FileExplorerItem> l) {
		List<FileExplorerItemDTO> list = new ArrayList<>();
		if (l != null) {
			for (FileExplorerItem f : l) {
				list.add(new FileExplorerItemDTO(f));
			}
		}
		return list;
	}
}
