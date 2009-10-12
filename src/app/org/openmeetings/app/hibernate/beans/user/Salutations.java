package org.openmeetings.app.hibernate.beans.user;

import java.util.Date;

import org.openmeetings.app.hibernate.beans.lang.Fieldlanguagesvalues;

/**
 * 
 * @hibernate.class table="salutations"
 * lazy="false"
 *
 */
public class Salutations {
	
	private Long salutations_id;
	private String name;
	private Date starttime;
	private Date updatetime;
	private String deleted;
	private Long fieldvalues_id;
	private Fieldlanguagesvalues label;
	
	public Salutations() {
		super();
		// TODO Auto-generated constructor stub
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
     *  column="salutations_id"
     *  generator-class="increment"
     */  
	public Long getSalutations_id() {
		return salutations_id;
	}
	public void setSalutations_id(Long salutations_id) {
		this.salutations_id = salutations_id;
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
     *  column="fieldvalues_id"
     *  type="long"
     */
	public Long getFieldvalues_id() {
		return fieldvalues_id;
	}
	public void setFieldvalues_id(Long fieldvalues_id) {
		this.fieldvalues_id = fieldvalues_id;
	}

	public Fieldlanguagesvalues getLabel() {
		return label;
	}

	public void setLabel(Fieldlanguagesvalues label) {
		this.label = label;
	}	
	
}
