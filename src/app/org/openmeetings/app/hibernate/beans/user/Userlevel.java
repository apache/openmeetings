package org.openmeetings.app.hibernate.beans.user;

import java.util.Date;

/**
 * 
 * @hibernate.class table="userlevel"
 * lazy="false"
 *
 */
public class Userlevel {
	
	private Long level_id;
	private String description;
	private Integer statuscode;
	private Date starttime;
	private Date updatetime;
	private String deleted;
	
	public Userlevel() {
		super();
		// TODO Auto-generated constructor stub
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
     * 
     * @hibernate.id
     *  column="level_id"
     *  generator-class="increment"
     */  
	public Long getLevel_id() {
		return level_id;
	}
	public void setLevel_id(Long level_id) {
		this.level_id = level_id;
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
     *  column="statuscode"
     *  type="int"
     */  
	public Integer getStatuscode() {
		return statuscode;
	}
	public void setStatuscode(Integer statuscode) {
		this.statuscode = statuscode;
	}
	
}
