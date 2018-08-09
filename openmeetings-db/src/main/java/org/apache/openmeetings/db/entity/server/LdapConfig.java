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
package org.apache.openmeetings.db.entity.server;

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
import org.apache.openmeetings.db.entity.HistoricalEntity;
import org.apache.openmeetings.db.entity.user.User;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Entity
@NamedQueries({
	@NamedQuery(name="getNondeletedLdapConfigs", query="SELECT u FROM LdapConfig u WHERE u.deleted = false")
	, @NamedQuery(name="getLdapConfigById", query="SELECT c FROM LdapConfig c WHERE c.id = :id AND c.deleted = false")
	, @NamedQuery(name="countNondeletedLdapConfigs", query="SELECT COUNT(c.id) FROM LdapConfig c WHERE c.deleted = false")
	, @NamedQuery(name="getActiveLdapConfigs", query="SELECT c FROM LdapConfig c WHERE c.deleted = false AND c.active = :isActive ORDER BY c.id")
})
@Table(name = "ldapconfig")
@Root(name="ldapconfig")
public class LdapConfig extends HistoricalEntity {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "name")
	@Element(data = true, required = false)
	private String name;

	@Column(name = "config_file_name")
	@Element(data = true, required = false)
	private String configFileName;

	@Column(name = "add_domain_to_user_name", nullable = false)
	@Element(data = true, required = false)
	private boolean addDomainToUserName;

	@Column(name = "domain")
	@Element(data = true, required = false)
	private String domain;

	@Column(name = "is_active", nullable = false)
	@Element(data = true, name = "isActive", required = false)
	private boolean active;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "insertedby_id", updatable = true, insertable = true)
	@ForeignKey(enabled = true)
	private User insertedby;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "updatedby_id", updatable = true, insertable = true)
	@ForeignKey(enabled = true)
	private User updatedby;

	@Lob
	@Column(name = "comment", length = 2048)
	@Element(data = true, required = false)
	private String comment;

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

	public String getConfigFileName() {
		return configFileName;
	}

	public void setConfigFileName(String configFileName) {
		this.configFileName = configFileName;
	}

	public boolean getAddDomainToUserName() {
		return addDomainToUserName;
	}

	public void setAddDomainToUserName(boolean addDomainToUserName) {
		this.addDomainToUserName = addDomainToUserName;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public User getInsertedby() {
		return insertedby;
	}

	public void setInsertedby(User insertedby) {
		this.insertedby = insertedby;
	}

	public User getUpdatedby() {
		return updatedby;
	}

	public void setUpdatedby(User updatedby) {
		this.updatedby = updatedby;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
}
