package org.openmeetings.app.hibernate.beans.user;

import java.io.Serializable;
import java.util.Date;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 
 * @hibernate.class table="users_usergroups"
 * lazy="false"
 *
 */
@Entity
@Table(name = "users_usergroups")
public class Users_Usergroups implements Serializable {
	
	private static final long serialVersionUID = 1006320698442560046L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	
	@Column(name="users_usergroups_id")
	private Long users_usergroups_id;
	@Column(name = "comment_field")
	private String comment;
	@Column(name = "starttime")
	private Date starttime;
	@Column(name = "updatetime")
	private Date updatetime;
	@Column(name="user_id")
	private Long user_id;
	@Column(name="usergroup_id")
	private Long usergroup_id;
	@Column(name = "deleted")
	private String deleted;
	
	public Users_Usergroups() {
		super();
		// TODO Auto-generated constructor stub
	}
    
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
    
	public Long getUsers_usergroups_id() {
		return users_usergroups_id;
	}
	public void setUsers_usergroups_id(Long users_usergroups_id) {
		this.users_usergroups_id = users_usergroups_id;
	}
    
    
	public Date getStarttime() {
		return starttime;
	}
	public void setStarttime(Date starttime) {
		this.starttime = starttime;
	}
    
	public Date getUpdatetime() {
		return updatetime;
	}
	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}
	
	public String getDeleted() {
		return deleted;
	}
	public void setDeleted(String deleted) {
		this.deleted = deleted;
	}
    
	public Long getUser_id() {
		return user_id;
	}
	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}
    
	public Long getUsergroup_id() {
		return usergroup_id;
	}
	public void setUsergroup_id(Long usergroup_id) {
		this.usergroup_id = usergroup_id;
	}
	
}
