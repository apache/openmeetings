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
package org.apache.openmeetings.persistence.beans.user;

import java.io.Serializable;
import java.util.Date;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Entity
@Table(name = "private_messages_folder")
@Root(name="privatemessagefolder")
public class PrivateMessageFolder implements Serializable {
	private static final long serialVersionUID = 3689814412815025816L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	@Element(data=true)
	private long privateMessageFolderId;
	
	@Column(name="folder_name")
	@Element(data=true)
	private String folderName;
	
	@Column(name="user_id")
	@Element(data=true)
	private Long userId;
	
	@Column(name="inserted")
	private Date inserted;
	
	@Column(name="updated")
	private Date updated;
	
	public long getPrivateMessageFolderId() {
		return privateMessageFolderId;
	}
	public void setPrivateMessageFolderId(long privateMessageFolderId) {
		this.privateMessageFolderId = privateMessageFolderId;
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
