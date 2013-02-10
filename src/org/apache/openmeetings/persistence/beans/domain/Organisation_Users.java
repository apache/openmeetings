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
package org.apache.openmeetings.persistence.beans.domain;

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

import org.apache.openjpa.persistence.jdbc.ForeignKey;
import org.apache.openmeetings.persistence.beans.IDataProviderEntity;
import org.apache.openmeetings.persistence.beans.user.User;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Entity
@NamedQueries({
	@NamedQuery(name="getOrganisation_UserByUserAndOrganisation",
		query="SELECT ou FROM User u, IN(u.organisation_users) ou WHERE u.deleted = false AND u.user_id = :user_id AND ou.organisation.organisation_id = :organisation_id")
	, @NamedQuery(name="deleteUsersFromOrganisation", query="DELETE FROM Organisation_Users c WHERE c.organisation.organisation_id = :id")
	, @NamedQuery(name="countOrganisationUsers", query="SELECT COUNT(c) FROM Organisation_Users c WHERE c.organisation.organisation_id = :id")
	, @NamedQuery(name="getOrganisationUsersById", query="SELECT c FROM Organisation_Users c WHERE c.organisation_users_id = :id")
	, @NamedQuery(name="getOrganisationUsersByOrgId", query="SELECT c FROM Organisation_Users c WHERE c.organisation.organisation_id = :id")
})
@Table(name = "organisation_users")
@Root(name="user_organisation")
public class Organisation_Users implements Serializable, IDataProviderEntity {
	private static final long serialVersionUID = 7206870465903375817L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long organisation_users_id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "organisation_id", insertable = true, updatable = true)
	@ForeignKey(enabled = true)
	@Element(name="organisation_id", required=false)
	private Organisation organisation;

	// FIXME: Does not get updated if a new relation is stored in the user
	@Column(name = "user_id")
	private Long user_id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id", insertable = true, updatable = true)
	@ForeignKey(enabled = true)
	private User user;
	
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
	
	public Organisation_Users() {
	}
	
	public Organisation_Users(Organisation organisation) {
		this.organisation = organisation;
	}
	
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((organisation_users_id == null) ? 0 : organisation_users_id
						.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (obj instanceof Organisation_Users) {
			Organisation_Users other = (Organisation_Users) obj;
			if (organisation_users_id == null) {
				if (other.organisation_users_id != null) {
					return false;
				}
			} else if (!organisation_users_id.equals(other.organisation_users_id)) {
				return false;
			}
		} else {
			return false;
		}
		return true;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
