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
package org.apache.openmeetings.db.entity.user;

import static org.apache.openmeetings.db.bind.Constants.MSG_FOLDER_NODE;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.openmeetings.db.bind.adapter.DateAdapter;
import org.apache.openmeetings.db.bind.adapter.LongAdapter;
import org.apache.openmeetings.db.entity.IDataProviderEntity;

@Entity
@Table(name = "private_message_folder")
@XmlRootElement(name = MSG_FOLDER_NODE)
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQuery(name = "getMsgFolders", query = "SELECT f FROM PrivateMessageFolder f ORDER BY f.id")
@NamedQuery(name = "getMsgFolderById", query = "SELECT f FROM PrivateMessageFolder f WHERE f.id = :id")
@NamedQuery(name = "getMsgFolderByUser", query = "SELECT f FROM PrivateMessageFolder f WHERE f.userId = :userId")
public class PrivateMessageFolder implements IDataProviderEntity {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	@XmlElement(name = "privateMessageFolderId")
	@XmlJavaTypeAdapter(LongAdapter.class)
	private Long id;

	@Column(name = "folder_name")
	@XmlElement(name = "folderName")
	private String folderName;

	@Column(name = "user_id")
	@XmlElement(name = "userId")
	@XmlJavaTypeAdapter(LongAdapter.class)
	private Long userId;

	@Column(name = "inserted")
	@XmlElement(name = "inserted")
	@XmlJavaTypeAdapter(DateAdapter.class)
	private Date inserted;

	@Column(name = "updated")
	@XmlTransient
	private Date updated;

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public String getFolderName() {
		return folderName;
	}

	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
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
}
