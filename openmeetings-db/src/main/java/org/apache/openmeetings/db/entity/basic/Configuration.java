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
package org.apache.openmeetings.db.entity.basic;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.apache.openjpa.persistence.jdbc.ForeignKey;
import org.apache.openmeetings.db.entity.IDataProviderEntity;
import org.apache.openmeetings.db.entity.user.User;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Entity
@NamedQueries({
		@NamedQuery(name = "forceGetConfigurationByKey", query = "SELECT c FROM Configuration c " 
				+ "WHERE c.key LIKE :key"),
		@NamedQuery(name = "getConfigurationsByKeys", query = "SELECT c FROM Configuration c "
				+ "WHERE c.key IN :keys and c.deleted = false"),
		@NamedQuery(name = "getNondeletedConfiguration", query = "SELECT c FROM Configuration c  "
				+ "LEFT JOIN FETCH c.user WHERE c.deleted = false"),
		@NamedQuery(name = "getConfigurationById", query = "SELECT c FROM Configuration c "
				+ "LEFT JOIN FETCH c.user WHERE c.id = :id and c.deleted = false"),
		@NamedQuery(name = "countConfigurations", query = "SELECT COUNT(c) FROM Configuration c WHERE c.deleted = false")
})
@Table(name = "configuration")
@Root(name = "config")
public class Configuration implements IDataProviderEntity {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	@Element(name = "id", data = true)
	private Long id;
	
	@Column(name = "key", unique = true)
	@Element(name = "key", data = true, required = false)
	private String key;

	@Column(name = "value")
	@Element(name = "value", data = true, required = false)
	private String value;

	@Column(name = "inserted")
	@Element(name = "created", data = true, required = false)
	private Date inserted;

	@Column(name = "updated")
	@Element(name = "updated", data = true, required = false)
	private Date updated;

	@Lob
	@Column(name = "comment_field", length = 2048)
	@Element(data = true, required = false)
	private String comment;

	@Column(name = "deleted")
	@Element(data = true)
	private boolean deleted;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	@ForeignKey(enabled = true)
	@Element(name = "user_id", data = true, required = false)
	private User user;

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
