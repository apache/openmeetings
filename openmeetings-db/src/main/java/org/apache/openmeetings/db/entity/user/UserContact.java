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

import static org.apache.openmeetings.db.bind.Constants.CONTACT_NODE;

import java.io.Serializable;
import java.util.Date;

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
import org.apache.openmeetings.db.bind.adapter.LongAdapter;
import org.apache.openmeetings.db.bind.adapter.UserAdapter;

@Entity
@NamedQuery(name = "deleteUserContact", query = "delete from UserContact u where u.id = :id")
@NamedQuery(name = "deleteAllUserContacts", query = "delete from UserContact u where u.owner.id = :ownerId")
@NamedQuery(name = "getContactByUserOwner", query = "SELECT c FROM UserContact c WHERE c.contact.id = :userId AND c.owner.id = :ownerId AND c.contact.deleted = false")
@NamedQuery(name = "getContactsByUserAndStatus", query = "select c from UserContact c "
		+ "where c.owner.id = :ownerId AND c.pending = :pending AND c.contact.deleted = false")
@NamedQuery(name = "getContactRequestsByUserAndStatus", query = "select c from UserContact c "
		+ "where c.contact.id = :userId AND c.pending = :pending AND c.contact.deleted = false")
@NamedQuery(name = "getContactsByUser", query = "SELECT c FROM UserContact c "
		+ "WHERE c.contact.id = :userId AND c.contact.deleted = false ORDER BY c.pending DESC")
@NamedQuery(name = "countContactsByUser", query = "select COUNT(c) from UserContact c "
		+ "where c.contact.id = :userId AND c.contact.deleted = false")
@NamedQuery(name = "getUserContactsById", query = "SELECT c FROM UserContact c WHERE c.id = :id")
@NamedQuery(name = "getUserContacts", query = "SELECT c FROM UserContact c ORDER BY c.id")
@Table(name = "user_contact")
@XmlRootElement(name = CONTACT_NODE)
@XmlAccessorType(XmlAccessType.FIELD)
public class UserContact implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	@XmlElement(name = "userContactId")
	@XmlJavaTypeAdapter(LongAdapter.class)
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id")
	@XmlElement(name = "contact", required = false)
	@XmlJavaTypeAdapter(UserAdapter.class)
	private User contact;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "owner_id")
	@ForeignKey(enabled = true)
	@XmlElement(name = "owner", required = false)
	@XmlJavaTypeAdapter(UserAdapter.class)
	private User owner;

	@Column(name = "pending", nullable = false)
	@XmlElement(name = "pending")
	@XmlJavaTypeAdapter(value = BooleanAdapter.class, type = boolean.class)
	private boolean pending;

	@Column(name = "inserted")
	@XmlTransient
	private Date inserted;

	@Column(name = "updated")
	@XmlTransient
	private Date updated;

	@Column(name = "share_calendar", nullable = false)
	@XmlElement(name = "pending", required = false)
	@XmlJavaTypeAdapter(value = BooleanAdapter.class, type = boolean.class)
	private boolean shareCalendar;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getContact() {
		return contact;
	}

	public void setContact(User contact) {
		this.contact = contact;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public boolean isPending() {
		return pending;
	}

	public void setPending(boolean pending) {
		this.pending = pending;
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

	public boolean getShareCalendar() {
		return shareCalendar;
	}

	public void setShareCalendar(boolean shareCalendar) {
		this.shareCalendar = shareCalendar;
	}
}
