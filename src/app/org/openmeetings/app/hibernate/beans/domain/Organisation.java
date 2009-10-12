package org.openmeetings.app.hibernate.beans.domain;

import java.util.Date;

/**
 * 
 * @hibernate.class table="organisation"
 * lazy="false"
 *
 */
public class Organisation {

	private Long organisation_id;
	private String name;
	private Long insertedby;
	private Long updatedby;
	private Date starttime;
	private Date updatetime;
	private String deleted;
	
	
	public Organisation() {
		super();
		// TODO Auto-generated constructor stub
	}

    /**
     * @hibernate.property
     *  column="insertedby"
     *  type="long"
     */
	public Long getInsertedby() {
		return insertedby;
	}
	public void setInsertedby(Long insertedby) {
		this.insertedby = insertedby;
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
     * 
     * @hibernate.id
     *  column="organisation_id"
     *  generator-class="increment"
     */ 
	public Long getOrganisation_id() {
		return organisation_id;
	}
	public void setOrganisation_id(Long organisation_id) {
		this.organisation_id = organisation_id;
	}

    /**
     * @hibernate.property
     *  column="updatedby"
     *  type="long"
     */
	public Long getUpdatedby() {
		return updatedby;
	}
	public void setUpdatedby(Long updatedby) {
		this.updatedby = updatedby;
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
}
