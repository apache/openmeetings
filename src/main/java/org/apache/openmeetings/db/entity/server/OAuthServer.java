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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.apache.openmeetings.db.entity.IDataProviderEntity;

@Entity
@Table(name = "oauth_servers")
@NamedQueries({
	@NamedQuery(name = "getEnabledOAuthServers", query = "select s from OAuthServer as s where s.enabled = true and s.deleted = false"),
	@NamedQuery(name = "getOAuthServerById", query = "select s from OAuthServer as s where s.id = :id"),
	@NamedQuery(name = "getAllOAuthServers", query = "select s from OAuthServer as s where s.deleted = false"),
	@NamedQuery(name = "countOAuthServers", query = "select count(s) from OAuthServer s WHERE s.deleted = false")
})
public class OAuthServer implements Serializable, IDataProviderEntity {
	
	private static final long serialVersionUID = -9034438721147720175L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	@Column(name = "name")
	private String name;
	@Column(name = "icon_url")
	private String iconUrl;
	@Column(name = "enabled")
	private Boolean enabled;
	@Column(name = "client_id")
	private String clientId;
	@Column(name = "client_secret")
	private String clientSecret;
	@Column(name = "request_key_url")
	private String requestKeyUrl;
	@Column(name = "request_token_url")
	private String requestTokenUrl;
	@Column(name = "request_token_attributes")
	private String requestTokenAttributes;
	@Column(name = "request_method")
	private RequestMethod requestTokenMethod;
	@Column(name = "request_info_url")
	private String requestInfoUrl;
	@Column(name = "login_param_name")
	private String loginParamName;
	@Column(name = "email_param_name")
	private String emailParamName;
	@Column(name = "firstname_param_name")
	private String firstnameParamName;
	@Column(name = "lastname_param_name")
	private String lastnameParamName;
	@Column(name = "deleted")
	private boolean deleted;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	public Boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	public String getRequestKeyUrl() {
		return requestKeyUrl;
	}

	public void setRequestKeyUrl(String requestKeyUrl) {
		this.requestKeyUrl = requestKeyUrl;
	}

	public String getRequestTokenUrl() {
		return requestTokenUrl;
	}

	public void setRequestTokenUrl(String requestTokenUrl) {
		this.requestTokenUrl = requestTokenUrl;
	}

	public String getRequestTokenAttributes() {
		return requestTokenAttributes;
	}

	public void setRequestTokenAttributes(String requestTokenAttributes) {
		this.requestTokenAttributes = requestTokenAttributes;
	}

	public RequestMethod getRequestTokenMethod() {
		return requestTokenMethod;
	}

	public void setRequestTokenMethod(RequestMethod requestTokenMethod) {
		this.requestTokenMethod = requestTokenMethod;
	}

	public String getRequestInfoUrl() {
		return requestInfoUrl;
	}

	public void setRequestInfoUrl(String requestInfoUrl) {
		this.requestInfoUrl = requestInfoUrl;
	}

	public String getLoginParamName() {
		return loginParamName;
	}

	public void setLoginParamName(String loginParamName) {
		this.loginParamName = loginParamName;
	}

	public String getEmailParamName() {
		return emailParamName;
	}

	public void setEmailParamName(String emailParamName) {
		this.emailParamName = emailParamName;
	}

	public String getFirstnameParamName() {
		return firstnameParamName;
	}

	public void setFirstnameParamName(String firstnameParamName) {
		this.firstnameParamName = firstnameParamName;
	}

	public String getLastnameParamName() {
		return lastnameParamName;
	}

	public void setLastnameParamName(String lastnameParamName) {
		this.lastnameParamName = lastnameParamName;
	}

	public boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	@Override
	public String toString() {
		return "OAuthServer [id=" + id + ", name=" + name + ", iconUrl="
				+ iconUrl + ", enabled=" + enabled + ", clientId=" + clientId
				+ ", clientSecret=" + clientSecret + ", requestKeyUrl="
				+ requestKeyUrl + ", requestTokenUrl=" + requestTokenUrl
				+ ", requestTokenAttributes=" + requestTokenAttributes
				+ ", requestTokenMethod=" + requestTokenMethod
				+ ", requestInfoUrl=" + requestInfoUrl + ", loginParamName="
				+ loginParamName + ", emailParamName=" + emailParamName
				+ ", firstnameParamName=" + firstnameParamName
				+ ", lastnameParamName=" + lastnameParamName + ", deleted="
				+ deleted + "]";
	}

	public enum RequestMethod {
		POST("post"),
		GET("get");
		
		private String name;

		private RequestMethod(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}
	}
	
}
