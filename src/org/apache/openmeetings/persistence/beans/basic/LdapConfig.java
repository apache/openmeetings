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
	@NamedQuery(name="getNondeletedLdapConfigs", query="SELECT u FROM LdapConfig u WHERE u.deleted = false")
})
@Table(name = "ldapconfig")
@Root(name="ldapconfig")
public class LdapConfig implements Serializable, IDataProviderEntity {
	private static final long serialVersionUID = 2839158519803993035L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	public long ldapConfigId = 0L;
	
	@Column(name="name")
	@Element(data=true)
	public String name;
	
	@Column(name="config_file_name")
	@Element(data=true)
	public String configFileName;
	
	@Column(name="add_domain_to_user_name")
	@Element(data=true)
	public Boolean addDomainToUserName;
	
	@Column(name="domain")
	@Element(data=true)
	public String domain;
	
	@Column(name="is_active")
	@Element(data=true)
	public Boolean isActive;
	
	@Column(name="inserted")
	public Date inserted;
	
	@Column(name="updated")
	public Date updated;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="insertedby_id", updatable=true, insertable=true)
	@ForeignKey(enabled = true)
	public User insertedby;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="updatedby_id", updatable=true, insertable=true)
	@ForeignKey(enabled = true)
	public User updatedby;
	
	@Column(name="deleted")
	private boolean deleted;
	
	@Lob
	@Column(name = "comment_field", length = 2048)
	@Element(data = true, required = false)
	private String comment;

	public long getLdapConfigId() {
		return ldapConfigId;
	}
	public void setLdapConfigId(long ldapConfigId) {
		this.ldapConfigId = ldapConfigId;
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
    
	public Boolean getAddDomainToUserName() {
		return addDomainToUserName;
	}
	public void setAddDomainToUserName(Boolean addDomainToUserName) {
		this.addDomainToUserName = addDomainToUserName;
	}
    
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
    
	public Boolean getIsActive() {
		return isActive;
	}
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
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
	
	public boolean getDeleted() {
		return deleted;
	}
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
	
}
