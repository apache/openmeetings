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

import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.apache.openmeetings.db.entity.HistoricalEntity;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Entity
@Table(name = "oauth_server")
@NamedQueries({
	@NamedQuery(name = "getEnabledOAuthServers", query = "select s from OAuthServer as s where s.enabled = true and s.deleted = false"),
	@NamedQuery(name = "getOAuthServerById", query = "select s from OAuthServer as s where s.id = :id"),
	@NamedQuery(name = "getAllOAuthServers", query = "SELECT s FROM OAuthServer s WHERE s.deleted = false ORDER BY s.id"),
	@NamedQuery(name = "countOAuthServers", query = "select count(s) from OAuthServer s WHERE s.deleted = false") })
@Root
public class OAuthServer extends HistoricalEntity {
	private static final long serialVersionUID = 1L;

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

	@Column(name = "enabled", nullable = false)
	@Element(data = true)
	private boolean enabled;

	@Column(name = "client_id")
	@Element(data = true)
	private String clientId;

	@Column(name = "client_secret")
	@Element(data = true)
	private String clientSecret;

	@Column(name = "key_url")
	@Element(data = true)
	private String requestKeyUrl;

	@Column(name = "token_url")
	@Element(data = true)
	private String requestTokenUrl;

	@Column(name = "token_attributes")
	@Element(data = true)
	private String requestTokenAttributes;

	@Column(name = "token_method")
	@Element(data = true)
	@Enumerated(EnumType.STRING)
	private RequestTokenMethod requestTokenMethod = RequestTokenMethod.POST;

	@Column(name = "info_url")
	@Element(data = true)
	private String requestInfoUrl;

	@Column(name = "info_method")
	@Element(data = true, required = false)
	@Enumerated(EnumType.STRING)
	private RequestInfoMethod requestInfoMethod = RequestInfoMethod.GET;

	@ElementCollection(fetch = FetchType.EAGER)
	@Element(data = true, required = false)
	@MapKeyColumn(name = "name")
	@Column(name = "value")
	@CollectionTable(name = "oauth_mapping", joinColumns = @JoinColumn(name = "oauth_id"))
	private Map<String, String> mapping = new LinkedHashMap<>();

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public OAuthServer setName(String name) {
		this.name = name;
		return this;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public OAuthServer setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
		return this;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public OAuthServer setEnabled(boolean enabled) {
		this.enabled = enabled;
		return this;
	}

	public String getClientId() {
		return clientId;
	}

	public OAuthServer setClientId(String clientId) {
		this.clientId = clientId;
		return this;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public OAuthServer setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
		return this;
	}

	public String getRequestKeyUrl() {
		return requestKeyUrl;
	}

	public OAuthServer setRequestKeyUrl(String requestKeyUrl) {
		this.requestKeyUrl = requestKeyUrl;
		return this;
	}

	public String getRequestTokenUrl() {
		return requestTokenUrl;
	}

	public OAuthServer setRequestTokenUrl(String requestTokenUrl) {
		this.requestTokenUrl = requestTokenUrl;
		return this;
	}

	public String getRequestTokenAttributes() {
		return requestTokenAttributes;
	}

	public OAuthServer setRequestTokenAttributes(String requestTokenAttributes) {
		this.requestTokenAttributes = requestTokenAttributes;
		return this;
	}

	public RequestTokenMethod getRequestTokenMethod() {
		return requestTokenMethod;
	}

	public OAuthServer setRequestTokenMethod(RequestTokenMethod method) {
		this.requestTokenMethod = method;
		return this;
	}

	public String getRequestInfoUrl() {
		return requestInfoUrl;
	}

	public OAuthServer setRequestInfoUrl(String requestInfoUrl) {
		this.requestInfoUrl = requestInfoUrl;
		return this;
	}

	public RequestInfoMethod getRequestInfoMethod() {
		return requestInfoMethod;
	}

	public OAuthServer setRequestInfoMethod(RequestInfoMethod method) {
		this.requestInfoMethod = method;
		return this;
	}

	public Map<String, String> getMapping() {
		return mapping;
	}

	public OAuthServer addMapping(String name, String value) {
		mapping.put(name, value);
		return this;
	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append("OAuthServer [id=").append(id)
				.append(", name=").append(name)
				.append(", iconUrl=").append(iconUrl)
				.append(", enabled=").append(enabled)
				.append(", clientId=").append(clientId)
				.append(", clientSecret=").append(clientSecret)
				.append(", requestKeyUrl=").append(requestKeyUrl)
				.append(", requestTokenUrl=").append(requestTokenUrl)
				.append(", requestTokenAttributes=").append(requestTokenAttributes)
				.append(", requestTokenMethod=").append(requestTokenMethod)
				.append(", requestInfoUrl=").append(requestInfoUrl)
				.append(", mapping=").append(mapping)
				.append(", isDeleted()=").append(isDeleted())
				.append("]").toString();
	}

	public enum RequestTokenMethod {
		POST, GET;
	}

	public enum RequestInfoMethod {
		POST, GET, HEADER;
	}
}
