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
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Entity
@NamedQueries({
	@NamedQuery(name="selectMaxUsersByOrganisationId",
		query="SELECT COUNT(c.organisation_users_id) FROM Organisation_Users c WHERE c.deleted = false AND c.organisation.organisation_id = :organisation_id")
	, @NamedQuery(name="getOrganisation_UserByUserAndOrganisation",
		query="SELECT ou FROM Users u, IN(u.organisation_users) ou WHERE u.deleted = false AND u.user_id = :user_id AND ou.organisation.organisation_id = :organisation_id")
	, @NamedQuery(name="deleteUsersFromOrganisation",
	query="DELETE FROM Organisation_Users c WHERE c.organisation.organisation_id = :organisation_id")
})
@Table(name = "organisation_users")
@Root(name="user_organisation")
public class Organisation_Users implements Serializable {
	private static final long serialVersionUID = 7206870465903375817L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long organisation_users_id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "organisation_id", insertable = true, updatable = true)
	@Element(name="organisation_id", required=false)
	private Organisation organisation;

	@Column(name = "user_id")
	private Long user_id;

	@Column(name = "starttime")
	private Date starttime;
	
	@Column(name = "updatetime")
	private Date updatetime;
	
	@Column(name = "deleted")
	@Element(data=true)
	private boolean deleted;
	
	@Column(name = "is_moderator")
	@Element(data=true, required=false)
	private Boolean isModerator;
	
	public Organisation getOrganisation() {
		return organisation;
	}

	public void setOrganisation(Organisation organisation) {
		this.organisation = organisation;
	}

	public Long getOrganisation_users_id() {
		return organisation_users_id;
	}

	public void setOrganisation_users_id(Long organisation_users_id) {
		this.organisation_users_id = organisation_users_id;
	}

	public Long getUser_id() {
		return user_id;
	}

	public void setUser_id(Long user_id) {
		this.user_id = user_id;
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

	public Boolean getIsModerator() {
		return isModerator;
	}

	public void setIsModerator(Boolean isModerator) {
		this.isModerator = isModerator;
	}
}
