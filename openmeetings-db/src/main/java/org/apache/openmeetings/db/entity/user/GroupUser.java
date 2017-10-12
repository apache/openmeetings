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
import org.apache.openmeetings.db.entity.HistoricalEntity;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Entity
@NamedQueries({
	@NamedQuery(name="deleteGroupUsersByGroup", query="DELETE FROM GroupUser gu WHERE gu.group.id = :id")
	, @NamedQuery(name="countGroupUsers", query="SELECT COUNT(c) FROM GroupUser c WHERE c.group.id = :id")
	, @NamedQuery(name="getGroupUsersById", query="SELECT c FROM GroupUser c WHERE c.id = :id")
	, @NamedQuery(name="getGroupUsersByGroupId", query="SELECT c FROM GroupUser c WHERE c.group.id = :id")
	, @NamedQuery(name="isUserInGroup", query="SELECT c FROM GroupUser c WHERE c.group.id = :groupId AND c.user.id = :userId")
})
@Table(name = "group_user")
@Root(name="user_organisation")
public class GroupUser extends HistoricalEntity {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "group_id", insertable = true, updatable = true)
	@ForeignKey(enabled = true)
	@Element(name="organisation_id", required=false)
	private Group group;

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "user_id", insertable = true, updatable = true)
	@ForeignKey(enabled = true)
	private User user;

	@Column(name = "is_moderator", nullable = false)
	@Element(data = true, required = false)
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
