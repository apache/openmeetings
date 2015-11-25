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
	private Long parentId;
	private Long roomId;
	private Long size;
	private String externalId;
	private String externalType;
	private Type type;

	public FileExplorerItemDTO() {}

	public FileExplorerItemDTO(FileExplorerItem f) {
		id = f.getId();
		name = f.getName();
		parentId = f.getParentId();
		roomId = f.getRoomId();
		size = f.getSize();
		externalId = f.getExternalId();
		externalType = f.getExternalType();
		type = f.getType();
	}
	
	public FileExplorerItem get() {
		FileExplorerItem f = new FileExplorerItem();
		f.setId(id);
		f.setName(name);
		f.setParentId(parentId);
		f.setRoomId(roomId);
		f.setSize(size);
		f.setExternalId(externalId);
		f.setExternalType(externalType);
		f.setType(type);
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

	public void setName(String fileName) {
		this.name = fileName;
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
