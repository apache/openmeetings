package org.openmeetings.app.hibernate.beans.user;

import java.util.Date;

/**
 * 
 * @hibernate.class table="user_contacts"
 * lazy="false"
 *
 */
public class UserContacts {
	
	private long userContactId;
	private Users contact;
	private Users owner;
	private Boolean pending;
	private String hash;
	private Date inserted;
	private Date updated;
	private Boolean shareCalendar;
	
	/**
     * 
     * @hibernate.id
     *  column="user_contact_id"
     *  generator-class="increment"
     */
	public long getUserContactId() {
		return userContactId;
	}
	public void setUserContactId(long userContactId) {
		this.userContactId = userContactId;
	}
	
    /**
     * @hibernate.many-to-one
     *  cascade="none"
     *  column="user_id"
     *  lazy="false"
     *  class="org.openmeetings.app.hibernate.beans.user.Users"
     *  not-null="false"
     *  outer-join="true"
     */ 	
	public Users getContact() {
		return contact;
	}
	public void setContact(Users contact) {
		this.contact = contact;
	}
	
	/**
     * @hibernate.many-to-one
     *  cascade="none"
     *  column="owner_id"
     *  lazy="false"
     *  class="org.openmeetings.app.hibernate.beans.user.Users"
     *  not-null="false"
     *  outer-join="true"
     */ 
	public Users getOwner() {
		return owner;
	}
	public void setOwner(Users owner) {
		this.owner = owner;
	}
	
	/**
     * @hibernate.property
     *  column="pending"
     *  type="boolean"
     */
	public Boolean getPending() {
		return pending;
	}
	public void setPending(Boolean pending) {
		this.pending = pending;
	}
	
	/**
     * @hibernate.property
     *  column="inserted"
     *  type="java.util.Date"
     */ 
	public Date getInserted() {
		return inserted;
	}
	public void setInserted(Date inserted) {
		this.inserted = inserted;
	}
	
	/**
     * @hibernate.property
     *  column="updated"
     *  type="java.util.Date"
     */
	public Date getUpdated() {
		return updated;
	}
	public void setUpdated(Date updated) {
		this.updated = updated;
	}
	
	/**
     * @hibernate.property
     *  column="hash"
     *  type="string"
     */	
	public String getHash() {
		return hash;
	}
	public void setHash(String hash) {
		this.hash = hash;
	}
	
	/**
     * @hibernate.property
     *  column="share_calendar"
     *  type="boolean"
     */
	public Boolean getShareCalendar() {
		return shareCalendar;
	}
	public void setShareCalendar(Boolean shareCalendar) {
		this.shareCalendar = shareCalendar;
	}
	
}
