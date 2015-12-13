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
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Entity
@Table(name = "oauth_servers")
@NamedQueries({
		@NamedQuery(name = "getEnabledOAuthServers", query = "select s from OAuthServer as s where s.enabled = true and s.deleted = false"),
		@NamedQuery(name = "getOAuthServerById", query = "select s from OAuthServer as s where s.id = :id"),
		@NamedQuery(name = "getAllOAuthServers", query = "SELECT s FROM OAuthServer s WHERE s.deleted = false ORDER BY s.id"),
		@NamedQuery(name = "countOAuthServers", query = "select count(s) from OAuthServer s WHERE s.deleted = false") })
@Root
public class OAuthServer implements Serializable, IDataProviderEntity {

	private static final long serialVersionUID = -9034438721147720175L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	@Element(data = true)
	private Long id;
	
	@Column(name = "name")
	@Element(data = true)
	private String name;
	
	@Column(name = "icon_url")
	@Element(data = true)
	private String iconUrl;
	
	@Column(name = "enabled")
	@Element(data = true)
	private boolean enabled;
	
	@Column(name = "client_id")
	@Element(data = true)
	private String clientId;
	
	@Column(name = "client_secret")
	@Element(data = true)
	private String clientSecret;
	
	@Column(name = "request_key_url")
	@Element(data = true)
	private String requestKeyUrl;
	
	@Column(name = "request_token_url")
	@Element(data = true)
	private String requestTokenUrl;
	
	@Column(name = "request_token_attributes")
	@Element(data = true)
	private String requestTokenAttributes;
	
	@Column(name = "request_method")
	@Element(data = true)
	private RequestMethod requestTokenMethod;
	
	@Column(name = "request_info_url")
	@Element(data = true)
	private String requestInfoUrl;
	
	@Column(name = "login_param_name")
	@Element(data = true)
	private String loginParamName;
	
	@Column(name = "email_param_name")
	@Element(data = true, required = false)
	private String emailParamName;
	
	@Column(name = "firstname_param_name")
	@Element(data = true, required = false)
	private String firstnameParamName;
	
	@Column(name = "lastname_param_name")
	@Element(data = true, required = false)
	private String lastnameParamName;
	
	@Column(name = "deleted")
	@Element(data = true)
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

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
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

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	@Override
	public String toString() {
		return "OAuthServer [id=" + id + ", name=" + name + ", iconUrl=" + iconUrl + ", enabled=" + enabled
				+ ", clientId=" + clientId + ", clientSecret=" + clientSecret + ", requestKeyUrl=" + requestKeyUrl
				+ ", requestTokenUrl=" + requestTokenUrl + ", requestTokenAttributes=" + requestTokenAttributes
				+ ", requestTokenMethod=" + requestTokenMethod + ", requestInfoUrl=" + requestInfoUrl
				+ ", loginParamName=" + loginParamName + ", emailParamName=" + emailParamName + ", firstnameParamName="
				+ firstnameParamName + ", lastnameParamName=" + lastnameParamName + ", deleted=" + deleted + "]";
	}

	public enum RequestMethod {
		POST("post"), GET("get");

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
