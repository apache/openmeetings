package org.openmeetings.app.hibernate.beans.calendar;

import java.util.Date;

import org.openmeetings.app.hibernate.beans.user.Users;

/**
 * 
 * @hibernate.class table="appointmentremindertyps"
 * lazy="false"
 *
 */

public class AppointmentReminderTyps {
	
	private Long typId;
	private String name;
	private Users user;
	
	private Date starttime;
	private Date updatetime;
	private String deleted;
	private String comment;
	
	/**
     * 
     * @hibernate.id
     *  column="typ_id"
     *  generator-class="increment"
     */  
	public Long getTypId() {
		return typId;
	}
	public void setTypId(Long typId) {
		this.typId = typId;
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
     * @hibernate.many-to-one
     *  cascade="none"
     *  column="user_id"
     *  lazy="false"
     *  class="org.openmeetings.app.hibernate.beans.user.Users"
     *  not-null="false"
     *  outer-join="true"
     */ 
	public Users getUser() {
		return user;
	}
	public void setUser(Users user) {
		this.user = user;
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
*  column="comment_field"
     *  type="string"
     */    
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	

}
