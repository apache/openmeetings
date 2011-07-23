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
@Table(name = "usergroups")
public class Usergroups implements Serializable {
	
	private static final long serialVersionUID = 6488627715951400445L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	
	@Column(name="usergroup_id")
	private Long usergroup_id;
	@Column(name="level_id")
	private Long level_id;
	@Column(name="name")
	private String name;
	@Column(name = "starttime")
	private Date starttime;
	@Column(name = "updatetime")
	private Date updatetime;
	@Column(name="user_id")
	private Long user_id;
	@Column(name="description")
	private String description;
	@Column(name = "comment_field")
	private String comment;
	@Column(name = "deleted")
	private String deleted;

	public Usergroups() {
		super();
		// TODO Auto-generated constructor stub
	}
    
    
	public Long getLevel_id() {
		return level_id;
	}
	public void setLevel_id(Long level_id) {
		this.level_id = level_id;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	
	public Long getUsergroup_id() {
		return usergroup_id;
	}
	public void setUsergroup_id(Long usergroup_id) {
		this.usergroup_id = usergroup_id;
	}		
	
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public Long getUser_id() {
		return user_id;
	}
	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}
	
}
