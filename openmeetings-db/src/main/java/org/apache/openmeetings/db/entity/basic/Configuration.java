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
		@NamedQuery(name = "forceGetConfigurationByKey", query = "SELECT c FROM Configuration c WHERE c.key LIKE :key"),
		@NamedQuery(name = "getConfigurationsByKeys", query = "SELECT c FROM Configuration c "
				+ "WHERE c.key IN :keys and c.deleted = false"),
		@NamedQuery(name = "getNondeletedConfiguration", query = "SELECT c FROM Configuration c  "
				+ "LEFT JOIN FETCH c.user WHERE c.deleted = false ORDER BY c.id ASC"),
		@NamedQuery(name = "getConfigurationById", query = "SELECT c FROM Configuration c "
				+ "LEFT JOIN FETCH c.user WHERE c.id = :id and c.deleted = false"),
		@NamedQuery(name = "countConfigurations", query = "SELECT COUNT(c) FROM Configuration c WHERE c.deleted = false")
})
@Table(name = "configuration")
@Root(name = "config")
public class Configuration extends HistoricalEntity {
	private static final long serialVersionUID = 1L;

	public enum Type {
		string
		, number
		, bool
	}
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	@Element(name = "id", data = true, required = false)
	private Long id;

	@Column(name = "type")
	@Element(name = "type", data = true, required = false)
	private Type type = Type.string;

	@Column(name = "om_key", unique = true)
	@Element(name = "key", data = true, required = false)
	private String key;

	@Column(name = "value")
	@Element(name = "value", data = true, required = false)
	private String value;

	@Lob
	@Column(name = "comment", length = 2048)
	@Element(data = true, required = false)
	private String comment;

	@Column(name = "fromVersion")
	@Element(data = true, required = false)
	private String fromVersion;

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
		return value == null ? false : TRUE.equals(Boolean.valueOf(value));
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
