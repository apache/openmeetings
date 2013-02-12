/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License") +  you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.openmeetings.persistence.beans.basic;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@NamedQueries({
	@NamedQuery(name = "getSessionById", query = "select c from Sessiondata as c "
					+ "where c.session_id LIKE :session_id"),
	@NamedQuery(name = "getSessionToDelete", query = "Select c from Sessiondata c "
					+ "WHERE c.refresh_time < :refresh_time " + "AND ( "
					+ "c.storePermanent IS NULL " + "OR "
					+ "c.storePermanent = false " + ")")
})
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
