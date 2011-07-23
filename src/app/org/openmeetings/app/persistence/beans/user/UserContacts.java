package org.openmeetings.app.persistence.beans.user;

import java.io.Serializable;
import java.util.Date;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

@Entity
@Table(name = "user_contacts")
public class UserContacts implements Serializable {
	
	private static final long serialVersionUID = 2391405538978996206L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	
	@Column(name="user_contact_id")
	private long userContactId;
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="user_id")
	private Users contact;
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="owner_id")
	private Users owner;
	@Column(name="pending")
	private Boolean pending;
	@Column(name="hash")
	private String hash;
	@Column(name="inserted")
	private Date inserted;
	@Column(name="updated")
	private Date updated;
	@Column(name="share_calendar")
	private Boolean shareCalendar;
	
	public long getUserContactId() {
		return userContactId;
	}
	public void setUserContactId(long userContactId) {
		this.userContactId = userContactId;
	}
	
	public Users getContact() {
		return contact;
	}
	public void setContact(Users contact) {
		this.contact = contact;
	}
	
	public Users getOwner() {
		return owner;
	}
	public void setOwner(Users owner) {
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
