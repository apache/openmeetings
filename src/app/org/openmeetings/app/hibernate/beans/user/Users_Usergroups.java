package org.openmeetings.app.hibernate.beans.user;

import java.util.Date;

/**
 * 
 * @hibernate.class table="users_usergroups"
 * lazy="false"
 *
 */
public class Users_Usergroups {
	
	private Long users_usergroups_id;
	private String comment;
	private Date starttime;
	private Date updatetime;
	private Long user_id;
	private Long usergroup_id;
	private String deleted;

	//Todo: Auto many-to-one
	private Usergroups usergroups;
	
	public Users_Usergroups() {
		super();
		// TODO Auto-generated constructor stub
	}
    
    /**
     * @hibernate.property
*  column="comment_field"
     *  type="string"
     */ 
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
    
    /**
     * 
     * @hibernate.id
     *  column="users_usergroups_id"
     *  generator-class="increment"
     */   
	public Long getUsers_usergroups_id() {
		return users_usergroups_id;
	}
	public void setUsers_usergroups_id(Long users_usergroups_id) {
		this.users_usergroups_id = users_usergroups_id;
	}
    
    
    /**
     * @hibernate.property
     *  column="starttime"
     *  type="java.util.Date"
     */  	
	public Date getStarttime() {
		return starttime;
	}
	public void setStarttime(Date starttime) {
		this.starttime = starttime;
	}
    
    /**
     * @hibernate.property
     *  column="updatetime"
     *  type="java.util.Date"
     */  	
	public Date getUpdatetime() {
		return updatetime;
	}
	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}
	
    /**
     * @hibernate.property
     *  column="deleted"
     *  type="string"
     */	
	public String getDeleted() {
		return deleted;
	}
	public void setDeleted(String deleted) {
		this.deleted = deleted;
	}
    
    /**
     * @hibernate.property
     *  column="user_id"
     *  type="long"
     */
	public Long getUser_id() {
		return user_id;
	}
	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}
    
    /**
     * @hibernate.property
     *  column="usergroup_id"
     *  type="long"
     */
	public Long getUsergroup_id() {
		return usergroup_id;
	}
	public void setUsergroup_id(Long usergroup_id) {
		this.usergroup_id = usergroup_id;
	}

	public Usergroups getUsergroups() {
		return usergroups;
	}
	public void setUsergroups(Usergroups usergroups) {
		this.usergroups = usergroups;
	}
	
}
