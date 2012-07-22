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
package org.openmeetings.app.persistence.beans.basic;

import java.io.Serializable;
import java.util.Date;

import org.openmeetings.app.persistence.beans.user.Users;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "ldapconfig")
public class LdapConfig implements Serializable {
	
	private static final long serialVersionUID = 2839158519803993035L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	
	@Column(name="ldapconfig_id")
	public long ldapConfigId;
	@Column(name="name")
	public String name;
	@Column(name="config_file_name")
	public String configFileName;
	@Column(name="add_domain_to_user_name")
	public Boolean addDomainToUserName;
	@Column(name="domain")
	public String domain;
	@Column(name="is_active")
	public Boolean isActive;
	@Column(name="inserted")
	public Date inserted;
	@Column(name="updated")
	public Date updated;
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="insertedby_id", updatable=true, insertable=true)
	public Users insertedby;
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="updatedby_id", updatable=true, insertable=true)
	public Users updatedby;
	@Column(name="deleted")
	private boolean deleted;
	
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
	
	public Users getInsertedby() {
		return insertedby;
	}
	public void setInsertedby(Users insertedby) {
		this.insertedby = insertedby;
	}
	
	public Users getUpdatedby() {
		return updatedby;
	}
	public void setUpdatedby(Users updatedby) {
		this.updatedby = updatedby;
	}
	
	public boolean getDeleted() {
		return deleted;
	}
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
	
}
