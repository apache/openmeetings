package org.openmeetings.app.hibernate.beans.basic;

import java.io.Serializable;
import java.util.Date;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name = "sessiondata")
public class Sessiondata implements Serializable {
	private static final long serialVersionUID = 1928177917452866750L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	
	@Column(name="id")
	private Long id;
	@Column(name="user_id")
	private Long user_id;
	@Column(name="session_id")
	private String session_id;
	@Column(name="starttermin_time")
	private Date starttermin_time;
	@Column(name="refresh_time")
	private Date refresh_time;
	@Lob
	@Column(name="sessionXml")
	private String sessionXml;
	@Column(name="storePermanent")
	private Boolean storePermanent;
	@Column(name="language_id")
	private Long language_id;
	@Column(name="organization_id")
	private Long organization_id;
	public Sessiondata() {
		super();
		// TODO Auto-generated constructor stub
	}
    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    
    public Date getRefresh_time() {
        return refresh_time;
    }
    public void setRefresh_time(Date refresh_time) {
        this.refresh_time = refresh_time;
    }
    
    public String getSession_id() {
        return session_id;
    }
    public void setSession_id(String session_id) {
        this.session_id = session_id;
    }
    
    public Date getStarttermin_time() {
        return starttermin_time;
    }
    public void setStarttermin_time(Date starttermin_time) {
        this.starttermin_time = starttermin_time;
    }
    
    public Long getUser_id() {
        return user_id;
    }
    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

	public String getSessionXml() {
		return sessionXml;
	}
	public void setSessionXml(String sessionXml) {
		this.sessionXml = sessionXml;
	}

	public Boolean getStorePermanent() {
		return storePermanent;
	}
	public void setStorePermanent(Boolean storePermanent) {
		this.storePermanent = storePermanent;
	}

	public Long getLanguage_id() {
		return language_id;
	}
	public void setLanguage_id(Long language_id) {
		this.language_id = language_id;
	}

	public Long getOrganization_id() {
		return organization_id;
	}
	public void setOrganization_id(Long organization_id) {
		this.organization_id = organization_id;
	}
	
}
