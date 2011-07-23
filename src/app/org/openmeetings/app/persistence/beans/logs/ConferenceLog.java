package org.openmeetings.app.persistence.beans.logs;

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
import javax.persistence.Table;



@Entity
@Table(name = "conferencelog")
public class ConferenceLog implements Serializable {
	
	
	private static final long serialVersionUID = 147341496943518159L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	
	@Column(name="conferencelog_id")
	private long conferenceLogId;
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="conferencelogtype_id", updatable=true, insertable=true)
	private ConferenceLogType conferenceLogType;
	@Column(name="inserted")
	private Date inserted;
	@Column(name="insertedby")
	private long insertedby;
	
	//NULL means its a Guest/Invited User
	@Column(name="user_id")
	private Long userId;
	@Column(name="external_user_id")
	private Long externalUserId;
	@Column(name="external_user_type")
	private String externalUserType;
	@Column(name="streamid")
	private String streamid;
	@Column(name="room_id")
	private Long room_id;
	@Column(name="userip")
	private String userip;
	@Column(name="scopename")
	private String scopeName;
	@Column(name="email")
	private String email;
	@Column(name="firstname")
	private String firstname;
	@Column(name="lastname")
	private String lastname;
	
	public long getConferenceLogId() {
		return conferenceLogId;
	}
	public void setConferenceLogId(long conferenceLogId) {
		this.conferenceLogId = conferenceLogId;
	}
	
	public ConferenceLogType getConferenceLogType() {
		return conferenceLogType;
	}
	public void setConferenceLogType(ConferenceLogType conferenceLogType) {
		this.conferenceLogType = conferenceLogType;
	}
	
	public Date getInserted() {
		return inserted;
	}
	public void setInserted(Date inserted) {
		this.inserted = inserted;
	}

	public long getInsertedby() {
		return insertedby;
	}
	public void setInsertedby(long insertedby) {
		this.insertedby = insertedby;
	}
	
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	public String getStreamid() {
		return streamid;
	}
	public void setStreamid(String streamid) {
		this.streamid = streamid;
	}
	
	public Long getRoom_id() {
		return room_id;
	}
	public void setRoom_id(Long room_id) {
		this.room_id = room_id;
	}
	
	public String getUserip() {
		return userip;
	}
	public void setUserip(String userip) {
		this.userip = userip;
	}
	
	public String getScopeName() {
		return scopeName;
	}
	public void setScopeName(String scopeName) {
		this.scopeName = scopeName;
	}
	
	public Long getExternalUserId() {
		return externalUserId;
	}
	public void setExternalUserId(Long externalUserId) {
		this.externalUserId = externalUserId;
	}
	
	public String getExternalUserType() {
		return externalUserType;
	}
	public void setExternalUserType(String externalUserType) {
		this.externalUserType = externalUserType;
	}
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	
}
