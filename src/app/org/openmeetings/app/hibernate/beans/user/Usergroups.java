package org.openmeetings.app.hibernate.beans.user;

import java.util.Date;

/**
 * 
 * @hibernate.class table="usergroups"
 * lazy="false"
 *
 */
public class Usergroups {
	
	private Long usergroup_id;
	private Long level_id;
	private String name;
	private Date starttime;
	private Date updatetime;
	private Long user_id;
	private String description;
	private String comment;
	private String deleted;

	public Usergroups() {
		super();
		// TODO Auto-generated constructor stub
	}
    
    
    /**
     * @hibernate.property
     *  column="level_id"
     *  type="long"
     */ 
	public Long getLevel_id() {
		return level_id;
	}
	public void setLevel_id(Long level_id) {
		this.level_id = level_id;
	}

    /**
     * @hibernate.property
     *  column="name"
     *  type="string"
     */ 
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
     * 
     * @hibernate.id
     *  column="usergroup_id"
     *  generator-class="increment"
     */  
	public Long getUsergroup_id() {
		return usergroup_id;
	}
	public void setUsergroup_id(Long usergroup_id) {
		this.usergroup_id = usergroup_id;
	}		
	
    /**
     * @hibernate.property
     *  column="comment"
     *  type="string"
     */ 
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	
    /**
     * @hibernate.property
     *  column="description"
     *  type="string"
     */ 
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
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
	
}
