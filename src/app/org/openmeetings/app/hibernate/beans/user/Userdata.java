package org.openmeetings.app.hibernate.beans.user;

import java.util.Date;

/**
 * 
 * @hibernate.class table="userdata"
 * lazy="false"
 *
 */
public class Userdata {
	
    private Long data_id;
    private Long user_id;    
    private String data_key;
    private String data;
    private Date starttime;
    private Date updatetime;
    private String comment;
    private String deleted;

	public Userdata() {
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
     * @hibernate.property
     *  column="data"
     *  type="string"
     */ 
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}

    /**
     * @hibernate.property
     *  column="data_key"
     *  type="string"
     */ 
	public String getData_key() {
		return data_key;
	}
	public void setData_key(String data_key) {
		this.data_key = data_key;
	}
    
    /**
     * 
     * @hibernate.id
     *  column="data_id"
     *  generator-class="increment"
     */
	public Long getData_id() {
		return data_id;
	}
	public void setData_id(Long data_id) {
		this.data_id = data_id;
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



}
