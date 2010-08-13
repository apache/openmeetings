package org.openmeetings.app.hibernate.beans.basic;

import java.util.Date;

import org.openmeetings.app.hibernate.beans.user.Users;

/**
 * 
 * @hibernate.class table="ldapconfig"
 *
 */
public class LdapConfig {
	
	public long ldapConfigId;
	public String name;
	public String configFileName;
	public Boolean addDomainToUserName;
	public String domain;
	public Boolean isActive;
	public Date inserted;
	public Date updated;
	public Users insertedby;
	public Users updatedby;
	private String deleted;
	
    /**
     * 
     * @hibernate.id
     *  column="ldapconfig_id"
     *  generator-class="increment"
     */ 	
	public long getLdapConfigId() {
		return ldapConfigId;
	}
	public void setLdapConfigId(long ldapConfigId) {
		this.ldapConfigId = ldapConfigId;
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
     *  column="config_file_name"
     *  type="string"
     */ 	
	public String getConfigFileName() {
		return configFileName;
	}
	public void setConfigFileName(String configFileName) {
		this.configFileName = configFileName;
	}
    
    /**
     * @hibernate.property
     *  column="add_domain_to_user_name"
     *  type="boolean"
     */ 	
	public Boolean getAddDomainToUserName() {
		return addDomainToUserName;
	}
	public void setAddDomainToUserName(Boolean addDomainToUserName) {
		this.addDomainToUserName = addDomainToUserName;
	}
    
    /**
     * @hibernate.property
     *  column="domain"
     *  type="string"
     */ 	
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
    
    /**
     * @hibernate.property
     *  column="is_active"
     *  type="boolean"
     */ 	
	public Boolean getIsActive() {
		return isActive;
	}
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}
	
	/**
     * @hibernate.property
     *  column="inserted"
     *  type="java.util.Date"
     */ 	
	public Date getInserted() {
		return inserted;
	}
	public void setInserted(Date inserted) {
		this.inserted = inserted;
	}
	
	/**
     * @hibernate.property
     *  column="updated"
     *  type="java.util.Date"
     */	
	public Date getUpdated() {
		return updated;
	}
	public void setUpdated(Date updated) {
		this.updated = updated;
	}
	
    /**
	 * @hibernate.many-to-one
	 * column = "insertedby_id"
	 * class = "org.openmeetings.app.hibernate.beans.user.Users"
	 * insert="true"
	 * update="true"
	 * outer-join="true"
	 * lazy="false"
     */		
	public Users getInsertedby() {
		return insertedby;
	}
	public void setInsertedby(Users insertedby) {
		this.insertedby = insertedby;
	}
	
    /**
	 * @hibernate.many-to-one
	 * column = "updatedby_id"
	 * class = "org.openmeetings.app.hibernate.beans.user.Users"
	 * insert="true"
	 * update="true"
	 * outer-join="true"
	 * lazy="false"
     */		
	public Users getUpdatedby() {
		return updatedby;
	}
	public void setUpdatedby(Users updatedby) {
		this.updatedby = updatedby;
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
