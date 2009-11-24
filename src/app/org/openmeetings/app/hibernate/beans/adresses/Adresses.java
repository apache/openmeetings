package org.openmeetings.app.hibernate.beans.adresses;

import java.util.Date;
import java.util.Set;

/**
 * 
 * @hibernate.class table="adresses"
 * lazy="false"
 *
 */

public class Adresses {
    
    private Long adresses_id;
    private String additionalname;
    private String comment;
    private String fax;
    private Date starttime;
    private States states;
    private String street;  
    private String town;     
    private Date updatetime;
    private String zip;
    private String deleted;
    
    private String email;
    private String phone;
    
    
    public Adresses() {
        // TODO Auto-generated constructor stub
    }

    /**
     * @hibernate.property
     *  column="additionalname"
     *  type="string"
     */ 
    public String getAdditionalname() {
        return additionalname;
    }
    public void setAdditionalname(String additionalname) {
        this.additionalname = additionalname;
    }
    
    /**
     * 
     * @hibernate.id
     *  column="adresses_id"
     *  generator-class="increment"
     */      
    public Long getAdresses_id() {
        return adresses_id;
    }
    public void setAdresses_id(Long adresses_id) {
        this.adresses_id = adresses_id;
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
     *  column="fax"
     *  type="string"
     */       
    public String getFax() {
        return fax;
    }
    public void setFax(String fax) {
        this.fax = fax;
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
     * @hibernate.many-to-one
     *  cascade="none"
     *  column="state_id"
     *  lazy="false"
     *  class="org.openmeetings.app.hibernate.beans.adresses.States"
     *  not-null="false"
     *  outer-join="true"
     */   
    public States getStates() {
        return states;
    }
    public void setStates(States states) {
        this.states = states;
    }
    
    /**
     * @hibernate.property
     *  column="street"
     *  type="string"
     */       
    public String getStreet() {
        return street;
    }
    public void setStreet(String street) {
        this.street = street;
    }
    
    /**
     * @hibernate.property
     *  column="town"
     *  type="string"
     */        
    public String getTown() {
        return town;
    }
    public void setTown(String town) {
        this.town = town;
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
     *  column="zip"
     *  type="string"
     */        
    public String getZip() {
        return zip;
    }
    public void setZip(String zip) {
        this.zip = zip;
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
     *  column="email"
     *  type="string"
     */	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	/**
     * @hibernate.property
     *  column="phone"
     *  type="string"
     */	
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
	
    


}
