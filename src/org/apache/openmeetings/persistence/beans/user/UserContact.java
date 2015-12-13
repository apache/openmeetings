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
package org.apache.openmeetings.persistence.beans.user;

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
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Entity
@NamedQueries({
	@NamedQuery(name = "deleteUserContact", query = "delete from UserContact u where u.userContactId = :userContactDeleteId"),
	@NamedQuery(name = "deleteAllUserContacts", query = "delete from UserContact u where u.owner.user_id = :ownerId"),
	@NamedQuery(name = "checkUserContacts", query = "select count(c.userContactId) from UserContact c " +
			"where c.contact.user_id = :user_id AND c.owner.user_id = :ownerId "),
	@NamedQuery(name = "getContactsByHash", query = "select c from UserContacts c " +
			"where c.hash like :hash "),
	@NamedQuery(name = "getContactsByUserAndStatus", query = "select c from UserContact c " +
			"where c.owner.user_id = :ownerId " +
			"AND c.pending = :pending " +
			"AND c.contact.deleted <> true"),
	@NamedQuery(name = "getUserContactByShareCalendar", query = "select c from UserContact c "
			+ "where c.contact.user_id = :userId "
			+ "AND c.owner.user_id = :contactId "
			+ "AND c.shareCalendar = :shareCalendar "
			+ "AND c.contact.deleted <> true"),
	@NamedQuery(name = "getContactsByShareCalendar", query = "select c from UserContact c " +
			"where c.contact.user_id = :contactId " +
			"AND c.shareCalendar = :shareCalendar " +
			"AND c.contact.deleted <> true"),
	@NamedQuery(name = "getContactRequestsByUserAndStatus", query = "select c from UserContact c " +
			"where c.contact.user_id = :user_id " +
			"AND c.pending = :pending " +
			"AND c.contact.deleted <> true"),
	@NamedQuery(name = "getUserContactsById", query = "select c from UserContact c " +
			"where c.userContactId = :userContactId"),
	@NamedQuery(name = "getUserContacts", query = "select c from UserContact c")
})
@Table(name = "user_contact")
@Root(name="usercontact")
public class UserContact implements Serializable {
	private static final long serialVersionUID = 2391405538978996206L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	@Element(data=true)
	private long userContactId;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="user_id")
	@Element(data=true, required=false)
	private User contact;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="owner_id")
	@ForeignKey(enabled = true)
	@Element(data=true, required=false)
	private User owner;
	
	@Column(name="pending")
	@Element(data=true)
	private Boolean pending;
	
	@Column(name="hash")
	@Element(data=true, required = false)
	private String hash;
	
	@Column(name="inserted")
	private Date inserted;
	
	@Column(name="updated")
	private Date updated;
	
	@Column(name="share_calendar")
	@Element(data=true, required=false)
	private Boolean shareCalendar;
	
	public long getUserContactId() {
		return userContactId;
	}
	public void setUserContactId(long userContactId) {
		this.userContactId = userContactId;
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
	
	public Boolean getPending() {
		return pending;
	}
	public void setPending(Boolean pending) {
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
	
	public String getHash() {
		return hash;
	}
	public void setHash(String hash) {
		this.hash = hash;
	}
	
	public Boolean getShareCalendar() {
		return shareCalendar;
	}
	public void setShareCalendar(Boolean shareCalendar) {
		this.shareCalendar = shareCalendar;
	}
	
}
