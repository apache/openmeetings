package org.openmeetings.app.persistence.beans.user;

import java.io.Serializable;
import java.util.Date;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "user_sip_data")
public class UserSipData implements Serializable {
	
	private static final long serialVersionUID = 3438146320304686554L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	
	@Column(name="user_sip_data_id")
	private long userSipDataId;
	@Column(name="username")
	private String username;
	@Column(name="userpass")
	private String userpass;
	@Column(name="authId")
	private String authId;
	@Column(name="inserted")
	private Date inserted;
	@Column(name="updated")
	private Date updated;
	
	public long getUserSipDataId() {
		return userSipDataId;
	}
	public void setUserSipDataId(long userSipDataId) {
		this.userSipDataId = userSipDataId;
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getUserpass() {
		return userpass;
	}
	public void setUserpass(String userpass) {
		this.userpass = userpass;
	}
	
	public String getAuthId() {
		return authId;
	}
	public void setAuthId(String authId) {
		this.authId = authId;
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
	

}