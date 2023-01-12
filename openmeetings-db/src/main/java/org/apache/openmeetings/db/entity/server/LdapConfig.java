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

import static org.apache.openmeetings.db.bind.Constants.LDAP_NODE;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.openjpa.persistence.jdbc.ForeignKey;
import org.apache.openmeetings.db.bind.adapter.BooleanAdapter;
import org.apache.openmeetings.db.entity.HistoricalEntity;
import org.apache.openmeetings.db.entity.user.User;

@Entity
@NamedQuery(name="getNondeletedLdapConfigs", query="SELECT u FROM LdapConfig u WHERE u.deleted = false")
@NamedQuery(name="getLdapConfigById", query="SELECT c FROM LdapConfig c WHERE c.id = :id AND c.deleted = false")
@NamedQuery(name="countNondeletedLdapConfigs", query="SELECT COUNT(c.id) FROM LdapConfig c WHERE c.deleted = false")
@NamedQuery(name="getActiveLdapConfigs", query="SELECT c FROM LdapConfig c WHERE c.deleted = false AND c.active = :isActive ORDER BY c.id")
@Table(name = "ldapconfig")
@XmlRootElement(name = LDAP_NODE)
@XmlAccessorType(XmlAccessType.FIELD)
public class LdapConfig extends HistoricalEntity {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	@XmlElement(name = "id", required = false)
	private Long id;

	@Column(name = "name")
	@XmlElement(name = "name", required = false)
	private String name;

	@Column(name = "config_file_name")
	@XmlElement(name = "configFileName", required = false)
	private String configFileName;

	@Column(name = "add_domain_to_user_name", nullable = false)
	@XmlElement(name = "addDomainToUserName", required = false)
	@XmlJavaTypeAdapter(value = BooleanAdapter.class, type = boolean.class)
	private boolean addDomainToUserName;

	@Column(name = "domain")
	@XmlElement(name = "domain", required = false)
	private String domain;

	@Column(name = "is_active", nullable = false)
	@XmlElement(name = "isActive", required = false)
	@XmlJavaTypeAdapter(value = BooleanAdapter.class, type = boolean.class)
	private boolean active;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "insertedby_id", updatable = true, insertable = true)
	@ForeignKey(enabled = true)
	@XmlTransient
	private User insertedby;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "updatedby_id", updatable = true, insertable = true)
	@ForeignKey(enabled = true)
	@XmlTransient
	private User updatedby;

	@Lob
	@Column(name = "comment", length = 2048)
	@XmlElement(name = "comment", required = false)
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
