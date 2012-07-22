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
package org.openmeetings.app.persistence.beans.domain;

import java.io.Serializable;
import java.util.Date;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@NamedQueries({
	@NamedQuery(name="getOrganisationById",
		query="SELECT c FROM Organisation AS c WHERE c.organisation_id = :organisation_id AND c.deleted <> :deleted")
	, @NamedQuery(name="getAnyOrganisationById",
		query="SELECT c FROM Organisation AS c WHERE c.organisation_id = :organisation_id")
	, @NamedQuery(name="getOrganisationsByUserId",
		query="SELECT ou.organisation FROM Users u, IN(u.organisation_users) ou WHERE u.deleted = false AND u.user_id = :user_id")
})
@Table(name = "organisation")
public class Organisation implements Serializable {
	private static final long serialVersionUID = 99123580264065654L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="organisation_id")
	private Long organisation_id;
	
	@Column(name="name")
	private String name;
	
	@Column(name="insertedby")
	private Long insertedby;
	
	@Column(name="updatedby")
	private Long updatedby;
	
	@Column(name="starttime")
	private Date starttime;
	
	@Column(name="updatetime")
	private Date updatetime;
	
	@Column(name="deleted")
	private boolean deleted;
	

	public Long getInsertedby() {
		return insertedby;
	}
	public void setInsertedby(Long insertedby) {
		this.insertedby = insertedby;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public Long getOrganisation_id() {
		return organisation_id;
	}
	public void setOrganisation_id(Long organisation_id) {
		this.organisation_id = organisation_id;
	}

	public Long getUpdatedby() {
		return updatedby;
	}
	public void setUpdatedby(Long updatedby) {
		this.updatedby = updatedby;
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
}
