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
package org.apache.openmeetings.persistence.beans.basic;

import java.io.Serializable;
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
import org.apache.openmeetings.persistence.beans.IDataProviderEntity;
import org.apache.openmeetings.persistence.beans.user.User;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Entity
@NamedQueries({
		@NamedQuery(name = "getConfigurationByKey", query = "SELECT c FROM Configuration c " 
				+ "WHERE c.conf_key LIKE :conf_key and c.deleted = false"),
		@NamedQuery(name = "getConfigurationsByKeys", query = "SELECT c FROM Configuration c "
				+ "WHERE c.conf_key IN :conf_keys and c.deleted = false"),
		@NamedQuery(name = "getNondeletedConfiguration", query = "SELECT c FROM Configuration c  "
				+ "LEFT JOIN FETCH c.user WHERE c.deleted = false"),
		@NamedQuery(name = "getConfigurationById", query = "SELECT c FROM Configuration c "
				+ "LEFT JOIN FETCH c.user "
				+ "WHERE c.configuration_id = :configuration_id and c.deleted = false")
})
@Table(name = "configuration")
@Root(name = "config")
public class Configuration implements Serializable, IDataProviderEntity {
	private static final long serialVersionUID = -6129473946508963339L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	@Element(name = "id", data = true)
	private Long configuration_id;

	@Column(name = "conf_key")
	@Element(name = "key", data = true)
	private String conf_key;

	@Column(name = "conf_value")
	@Element(name = "value", data = true, required = false)
	private String conf_value;

	@Column(name = "starttime")
	@Element(name = "created", data = true, required = false)
	private Date starttime;

	@Column(name = "updatetime")
	@Element(name = "updated", data = true, required = false)
	private Date updatetime;

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

	public String getConf_key() {
		return conf_key;
	}

	public void setConf_key(String conf_key) {
		this.conf_key = conf_key;
	}

	public String getConf_value() {
		return conf_value;
	}

	public void setConf_value(String conf_value) {
		this.conf_value = conf_value;
	}

	public Long getConfiguration_id() {
		return configuration_id;
	}

	public void setConfiguration_id(Long configuration_id) {
		this.configuration_id = configuration_id;
	}

	public Date getStarttime() {
		return starttime;
	}

	public void setStarttime(Date starttime) {
		this.starttime = starttime;
	}

	public Date getUpdatetime() {
		return updatetime;
	}

	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}

	public boolean getDeleted() {
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
