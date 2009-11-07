package org.openmeetings.app.hibernate.beans.basic;

import java.util.Date;

/**
 * 
 * @hibernate.class table="sessiondata"
 *
 */
public class Sessiondata {
	private Long id;
	private Long user_id;
	private String session_id;
	private Date starttermin_time;
	private Date refresh_time;
	private String sessionXml;
	private Boolean storePermanent;
	private Long language_id;
	private Long organization_id;
	public Sessiondata() {
		super();
		// TODO Auto-generated constructor stub
	}
    
    /**
     * 
     * @hibernate.id
     *  column="id"
     *  generator-class="increment"
     */     
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    
    /**
     * @hibernate.property
     *  column="refresh_time"
     *  type="java.util.Date"
     */     
    public Date getRefresh_time() {
        return refresh_time;
    }
    public void setRefresh_time(Date refresh_time) {
        this.refresh_time = refresh_time;
    }
    
    /**
     * @hibernate.property
     *  column="session_id"
     *  type="string"
     */     
    public String getSession_id() {
        return session_id;
    }
    public void setSession_id(String session_id) {
        this.session_id = session_id;
    }
    
    /**
     * @hibernate.property
     *  column="starttermin_time"
     *  type="java.util.Date"
     */     
    public Date getStarttermin_time() {
        return starttermin_time;
    }
    public void setStarttermin_time(Date starttermin_time) {
        this.starttermin_time = starttermin_time;
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

    /**
     * @hibernate.property
     *  column="sessionXml"
     *  type="text"
     */ 
	public String getSessionXml() {
		return sessionXml;
	}
	public void setSessionXml(String sessionXml) {
		this.sessionXml = sessionXml;
	}

	/**
     * @hibernate.property
     *  column="storePermanent"
     *  type="boolean"
     */ 
	public Boolean getStorePermanent() {
		return storePermanent;
	}
	public void setStorePermanent(Boolean storePermanent) {
		this.storePermanent = storePermanent;
	}

	/**
     * @hibernate.property
     *  column="language_id"
     *  type="long"
     */ 
	public Long getLanguage_id() {
		return language_id;
	}
	public void setLanguage_id(Long language_id) {
		this.language_id = language_id;
	}

	/**
     * @hibernate.property
     *  column="organization_id"
     *  type="long"
     */
	public Long getOrganization_id() {
		return organization_id;
	}
	public void setOrganization_id(Long organization_id) {
		this.organization_id = organization_id;
	}
	
}
