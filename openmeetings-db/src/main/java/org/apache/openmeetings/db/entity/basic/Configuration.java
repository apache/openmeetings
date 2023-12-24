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

import static java.lang.Boolean.TRUE;
import static org.apache.openmeetings.db.bind.Constants.CFG_NODE;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.openjpa.persistence.jdbc.ForeignKey;
import org.apache.openmeetings.db.bind.adapter.ConfigTypeAdapter;
import org.apache.openmeetings.db.bind.adapter.LongAdapter;
import org.apache.openmeetings.db.bind.adapter.UserAdapter;
import org.apache.openmeetings.db.entity.HistoricalEntity;
import org.apache.openmeetings.db.entity.user.User;

@Entity
@NamedQuery(name = "forceGetConfigurationByKey", query = "SELECT c FROM Configuration c WHERE c.key LIKE :key")
@NamedQuery(name = "getConfigurationsByKeys", query = "SELECT c FROM Configuration c "
		+ "WHERE c.key IN :keys and c.deleted = false")
@NamedQuery(name = "getNondeletedConfiguration", query = "SELECT c FROM Configuration c  "
		+ "LEFT JOIN FETCH c.user WHERE c.deleted = false ORDER BY c.id ASC")
@NamedQuery(name = "getConfigurationById", query = "SELECT c FROM Configuration c "
		+ "LEFT JOIN FETCH c.user WHERE c.id = :id and c.deleted = false")
@NamedQuery(name = "countConfigurations", query = "SELECT COUNT(c) FROM Configuration c WHERE c.deleted = false")
@Table(name = "configuration", indexes = {
		@Index(name = "key_idx", columnList = "om_key", unique = true)
})
@XmlRootElement(name = CFG_NODE)
@XmlAccessorType(XmlAccessType.FIELD)
public class Configuration extends HistoricalEntity {
	private static final long serialVersionUID = 1L;

	public enum Type {
		STRING
		, NUMBER
		, BOOL
		, HOTKEY
		, PATH
	}
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	@XmlElement(name = "id", required = false)
	@XmlJavaTypeAdapter(LongAdapter.class)
	private Long id;

	@Column(name = "type")
	@Enumerated(EnumType.STRING)
	@XmlElement(name = "type", required = false)
	@XmlJavaTypeAdapter(ConfigTypeAdapter.class)
	private Type type = Type.STRING;

	@Column(name = "om_key") // unique via index
	@XmlElement(name = "key", required = false)
	private String key;

	@Column(name = "value")
	@XmlElement(name = "value", required = false)
	private String value;

	@Lob
	@Column(name = "comment", length = 2048)
	@XmlElement(name = "comment", required = false)
	private String comment;

	@Column(name = "fromVersion")
	@XmlElement(name = "fromVersion", required = false)
	private String fromVersion;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	@ForeignKey(enabled = true)
	@XmlElement(name = "user_id", required = false)
	@XmlJavaTypeAdapter(UserAdapter.class)
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

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	/* proxy methods to perform conversions */
	public String getValueS() {
		return value;
	}

	public void setValueS(String value) {
		this.value = value;
	}

	public Long getValueN() {
		return value == null ? null : Long.valueOf(value);
	}

	public void setValueN(Long value) {
		this.value = value == null ? null : value.toString();
	}

	public boolean getValueB() {
		return value != null && TRUE.equals(Boolean.valueOf(value));
	}

	public void setValueB(boolean value) {
		this.value = String.valueOf(value);
	}

	public String getFromVersion() {
		return fromVersion;
	}

	public void setFromVersion(String fromVersion) {
		this.fromVersion = fromVersion;
	}

	@Override
	public String toString() {
		return "Configuration [id=" + id + ", type=" + type + ", key=" + key + ", value=" + value + "]";
	}
}
