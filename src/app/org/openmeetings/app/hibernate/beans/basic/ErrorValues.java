package org.openmeetings.app.hibernate.beans.basic;

import java.util.Date;

import org.openmeetings.app.hibernate.beans.lang.Fieldvalues;

/**
 * 
 * @hibernate.class table="errorvalues"
 *
 */
public class ErrorValues {
	
	private Long errorvalues_id;
	private ErrorType errorType;
	private Fieldvalues fieldvalues;
	private Date starttime;
	private Date updatetime;
	private String deleted;
	
	private Long errortype_id;
	private Long fieldvalues_id;
		
    /**
     * 
     * @hibernate.id
     *  column="errorvalues_id"
     *  generator-class="increment"
     */ 
	public Long getErrorvalues_id() {
		return errorvalues_id;
	}
	public void setErrorvalues_id(Long errorvalues_id) {
		this.errorvalues_id = errorvalues_id;
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
	 * @hibernate.many-to-one
	 * column = "fieldvalues_id"
	 * class = "org.openmeetings.app.hibernate.beans.lang.Fieldvalues"
	 * insert="false"
	 * update="false"
	 * outer-join="true"
	 * lazy="false"
     */	
	public Fieldvalues getFieldvalues() {
		return fieldvalues;
	}
	public void setFieldvalues(Fieldvalues fieldvalues) {
		this.fieldvalues = fieldvalues;
	}
	
    /**
	 * @hibernate.many-to-one
	 * column = "errortype_id"
	 * class = "org.openmeetings.app.hibernate.beans.basic.ErrorType"
	 * insert="false"
	 * update="false"
	 * outer-join="true"
	 * lazy="false"
     */	
	public ErrorType getErrorType() {
		return errorType;
	}
	public void setErrorType(ErrorType errorType) {
		this.errorType = errorType;
	}
	
	
	/**
     * @hibernate.property
     *  column="fieldvalues_id"
     *  type="long"
     */  
	public Long getFieldvalues_id() {
		return fieldvalues_id;
	}
	public void setFieldvalues_id(Long fieldvalues_id) {
		this.fieldvalues_id = fieldvalues_id;
	}
	
	/**
     * @hibernate.property
     *  column="errortype_id"
     *  type="long"
     */  	
	public Long getErrortype_id() {
		return errortype_id;
	}
	public void setErrortype_id(Long errortype_id) {
		this.errortype_id = errortype_id;
	}

	
}
