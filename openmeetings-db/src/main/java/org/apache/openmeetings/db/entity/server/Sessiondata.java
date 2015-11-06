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
package org.apache.openmeetings.db.entity.server;

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
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@NamedQueries({
	@NamedQuery(name = "getSessionById", query = "select c from Sessiondata as c "
					+ "where c.session_id LIKE :sessionId"),
	@NamedQuery(name = "getSessionToDelete", query = "Select c from Sessiondata c "
					+ "WHERE c.refresh_time < :refreshed AND c.storePermanent = false")
})
@Table(name = "sessiondata")
@XmlRootElement
public class Sessiondata implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	private Long id;
	
	@Column(name="user_id")
	private Long user_id;
	
	@Column(name="session_id")
	private String session_id;
	
	@Column(name="created")
	private Date starttermin_time;
	
	@Column(name="refreshed")
	private Date refresh_time;
	
	@Lob
	@Column(name="xml")
	private String sessionXml;
	
	@Column(name="permanent")
	private boolean storePermanent;
	
	@Column(name="language_id")
	private Long language_id;
	
	@Column(name="organization_id")
	private Long organization_id;
	
	public Sessiondata() {
	}
    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    
    public Date getRefreshed() {
        return refresh_time;
    }
    public void setRefreshed(Date refreshed) {
        this.refresh_time = refreshed;
    }
    
    public String getSessionId() {
        return session_id;
    }
    public void setSessionId(String sessionId) {
        this.session_id = sessionId;
    }
    
    public Date getCreated() {
        return starttermin_time;
    }
    public void setCreated(Date created) {
        this.starttermin_time = created;
    }
    
    public Long getUserId() {
        return user_id;
    }
    public void setUserId(Long user_id) {
        this.user_id = user_id;
    }

	public String getXml() {
		return sessionXml;
	}
	public void setXml(String xml) {
		this.sessionXml = xml;
	}

	public boolean isPermanent() {
		return storePermanent;
	}
	public void setPermanent(boolean permanent) {
		this.storePermanent = permanent;
	}

	public Long getLanguageId() {
		return language_id;
	}
	public void setLanguageId(Long languageId) {
		this.language_id = languageId;
	}

	public Long getOrganizationId() {
		return organization_id;
	}
	public void setOrganizationId(Long organizationId) {
		this.organization_id = organizationId;
	}
	
}
