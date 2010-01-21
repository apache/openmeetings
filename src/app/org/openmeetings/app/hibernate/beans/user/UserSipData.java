package org.openmeetings.app.hibernate.beans.user;

import java.util.Date;

/**
 * 
 * @hibernate.class table="user_sip_data"
 * lazy="false"
 *
 */
public class UserSipData {
	
	private long userSipDataId;
	private String username;
	private String userpass;
	private String authId;
	private Date inserted;
	private Date updated;
	
    /**
     * 
     * @hibernate.id
     *  column="user_sip_data_id"
     *  generator-class="increment"
     */
	public long getUserSipDataId() {
		return userSipDataId;
	}
	public void setUserSipDataId(long userSipDataId) {
		this.userSipDataId = userSipDataId;
	}
	
    /**
     * @hibernate.property
     *  column="username"
     *  type="string"
     */  
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
    /**
     * @hibernate.property
     *  column="userpass"
     *  type="string"
     */ 	
	public String getUserpass() {
		return userpass;
	}
	public void setUserpass(String userpass) {
		this.userpass = userpass;
	}
	
    /**
     * @hibernate.property
     *  column="authId"
     *  type="string"
     */ 	
	public String getAuthId() {
		return authId;
	}
	public void setAuthId(String authId) {
		this.authId = authId;
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
	

}