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
package org.apache.openmeetings.db.entity.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
import org.apache.openmeetings.db.bind.adapter.GroupAdapter;
import org.apache.openmeetings.db.entity.HistoricalEntity;

@Entity
@NamedQuery(name = "deleteGroupUsersByGroup", query = "DELETE FROM GroupUser gu WHERE gu.group.id = :id")
@NamedQuery(name = "countGroupUsers", query = "SELECT COUNT(gu) FROM GroupUser gu WHERE gu.group.id = :id")
@NamedQuery(name = "getGroupUsersById", query = "SELECT gu FROM GroupUser gu WHERE gu.id = :id")
@NamedQuery(name = "getGroupUsersByGroupId", query = "SELECT gu FROM GroupUser gu WHERE gu.group.id = :id")
@NamedQuery(name = "isUserInGroup", query = "SELECT gu FROM GroupUser gu WHERE gu.group.id = :groupId AND gu.user.id = :userId")
@NamedQuery(name = "getGroupUserCountAddedAfter", query = "SELECT COUNT(gu) FROM GroupUser gu WHERE gu.deleted = false AND gu.group.id = :id AND gu.inserted > :inserted")
@NamedQuery(name = "getGroupModerators", query = "SELECT gu.user FROM GroupUser gu WHERE gu.deleted = false AND gu.moderator = true AND  gu.group.id = :id")
@Table(name = "group_user")
@XmlRootElement(name = "user_organisation")
@XmlAccessorType(XmlAccessType.FIELD)
public class GroupUser extends HistoricalEntity {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	@XmlTransient
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "group_id", insertable = true, updatable = true)
	@ForeignKey(enabled = true)
	@XmlElement(name = "organisation_id", required = false)
	@XmlJavaTypeAdapter(GroupAdapter.class)
	private Group group;

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "user_id", insertable = true, updatable = true)
	@ForeignKey(enabled = true)
	@XmlTransient
	private User user;

	@Column(name = "is_moderator", nullable = false)
	@XmlElement(name = "moderator", required = false)
	@XmlJavaTypeAdapter(value = BooleanAdapter.class, type = boolean.class)
	private boolean moderator;

	public GroupUser() {
		//def constructor
	}

	public GroupUser(Group group, User u) {
		this.group = group;
		this.user = u;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public boolean isModerator() {
		return moderator;
	}

	public void setModerator(boolean moderator) {
		this.moderator = moderator;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public String toString() {
		return "GroupUser [id=" + id + ", moderator=" + moderator + ", group=" + group + ", user=" + user + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((group.getId() == null) ? 0 : group.getId().hashCode());
		result = prime * result + ((user.getId() == null) ? 0 : user.getId().hashCode());
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
		if (getClass() != obj.getClass()) {
			return false;
		}
		GroupUser other = (GroupUser) obj;
		if (group.getId() == null) {
			if (other.group.getId() != null) {
				return false;
			}
		} else if (!group.getId().equals(other.group.getId())) {
			return false;
		}
		if (user.getId() == null) {
			if (other.user.getId() != null) {
				return false;
			}
		} else if (!user.getId().equals(other.user.getId())) {
			return false;
		}
		return true;
	}
}
