package org.openmeetings.app.hibernate.beans.logs;

import java.util.Date;

/**
 * 
 * @hibernate.class table="conferencelog"
 *
 */
public class ConferenceLog {
	
	
	private long conferenceLogId;
	private ConferenceLogType conferenceLogType;
	private Date inserted;
	private long insertedby;
	
	//NULL means its a Guest/Invited User
	private Long userId;
	private Long externalUserId;
	private String externalUserType;
	private String streamid;
	private Long room_id;
	private String userip;
	private String scopeName;
	private String email;
	private String firstname;
	private String lastname;
	
	/**
     * 
     * @hibernate.id
     *  column="conferencelog_id"
     *  generator-class="increment"
     */ 
	public long getConferenceLogId() {
		return conferenceLogId;
	}
	public void setConferenceLogId(long conferenceLogId) {
		this.conferenceLogId = conferenceLogId;
	}
	
	/**
	 * @hibernate.many-to-one
	 * column = "conferencelogtype_id"
	 * class = "org.openmeetings.app.hibernate.beans.logs.ConferenceLogType"
	 * insert="true"
	 * update="true"
	 * outer-join="true"
	 * lazy="false"
     */
	public ConferenceLogType getConferenceLogType() {
		return conferenceLogType;
	}
	public void setConferenceLogType(ConferenceLogType conferenceLogType) {
		this.conferenceLogType = conferenceLogType;
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
     *  column="insertedby"
     *  type="long"
     */
	public long getInsertedby() {
		return insertedby;
	}
	public void setInsertedby(long insertedby) {
		this.insertedby = insertedby;
	}
	
	/**
     * @hibernate.property
     *  column="user_id"
     *  type="long"
     */
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	/**
     * @hibernate.property
     *  column="streamid"
     *  type="string"
     */
	public String getStreamid() {
		return streamid;
	}
	public void setStreamid(String streamid) {
		this.streamid = streamid;
	}
	
	/**
     * @hibernate.property
     *  column="room_id"
     *  type="long"
     */
	public Long getRoom_id() {
		return room_id;
	}
	public void setRoom_id(Long room_id) {
		this.room_id = room_id;
	}
	
	/**
     * @hibernate.property
     *  column="userip"
     *  type="string"
     */
	public String getUserip() {
		return userip;
	}
	public void setUserip(String userip) {
		this.userip = userip;
	}
	
	/**
     * @hibernate.property
     *  column="scopename"
     *  type="string"
     */
	public String getScopeName() {
		return scopeName;
	}
	public void setScopeName(String scopeName) {
		this.scopeName = scopeName;
	}
	
	/**
     * @hibernate.property
     *  column="external_user_id"
     *  type="long"
     */
	public Long getExternalUserId() {
		return externalUserId;
	}
	public void setExternalUserId(Long externalUserId) {
		this.externalUserId = externalUserId;
	}
	
	/**
     * @hibernate.property
     *  column="external_user_type"
     *  type="string"
     */
	public String getExternalUserType() {
		return externalUserType;
	}
	public void setExternalUserType(String externalUserType) {
		this.externalUserType = externalUserType;
	}
	
	/**
     * @hibernate.property
     *  column="email"
     *  type="string"
     */
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	/**
     * @hibernate.property
     *  column="firstname"
     *  type="string"
     */
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	
	/**
     * @hibernate.property
     *  column="lastname"
     *  type="string"
     */
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	
}
