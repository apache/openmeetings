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

import static org.apache.openmeetings.db.bind.Constants.OAUTH_NODE;

import java.util.LinkedHashMap;
import java.util.Map;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.openmeetings.db.bind.adapter.BooleanAdapter;
import org.apache.openmeetings.db.bind.adapter.LongAdapter;
import org.apache.openmeetings.db.bind.adapter.OauthMapAdapter;
import org.apache.openmeetings.db.entity.HistoricalEntity;

@Entity
@Table(name = "oauth_server")
@NamedQuery(name = "getEnabledOAuthServers", query = "select s from OAuthServer as s where s.enabled = true and s.deleted = false")
@NamedQuery(name = "getOAuthServerById", query = "select s from OAuthServer as s where s.id = :id")
@NamedQuery(name = "getAllOAuthServers", query = "SELECT s FROM OAuthServer s WHERE s.deleted = false ORDER BY s.id")
@NamedQuery(name = "countOAuthServers", query = "select count(s) from OAuthServer s WHERE s.deleted = false")
@XmlRootElement(name = OAUTH_NODE)
@XmlAccessorType(XmlAccessType.FIELD)
public class OAuthServer extends HistoricalEntity {
	private static final long serialVersionUID = 1L;

	public enum RequestTokenMethod {
		POST, GET
	}

	public enum RequestInfoMethod {
		POST, GET, HEADER
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	@XmlElement(name = "id", required = false)
	@XmlJavaTypeAdapter(LongAdapter.class)
	private Long id;

	@Column(name = "name")
	@XmlElement(name = "name", required = false)
	private String name;

	@Column(name = "icon_url")
	@XmlElement(name = "iconUrl", required = false)
	private String iconUrl;

	@Column(name = "enabled", nullable = false)
	@XmlElement(name = "enabled", required = false)
	@XmlJavaTypeAdapter(value = BooleanAdapter.class, type = boolean.class)
	private boolean enabled;

	@Column(name = "client_id")
	@XmlElement(name = "clientId", required = false)
	private String clientId;

	@Column(name = "client_secret")
	@XmlElement(name = "clientSecret", required = false)
	private String clientSecret;

	@Column(name = "key_url")
	@XmlElement(name = "requestKeyUrl", required = false)
	private String requestKeyUrl;

	@Column(name = "token_url")
	@XmlElement(name = "requestTokenUrl", required = false)
	private String requestTokenUrl;

	@Column(name = "token_attributes")
	@XmlElement(name = "requestTokenAttributes", required = false)
	private String requestTokenAttributes;

	@Column(name = "token_method")
	@Enumerated(EnumType.STRING)
	@XmlElement(name = "requestTokenMethod", required = false)
	private RequestTokenMethod requestTokenMethod = RequestTokenMethod.POST;

	@Column(name = "info_url")
	@XmlElement(name = "requestInfoUrl", required = false)
	private String requestInfoUrl;

	@Column(name = "info_method")
	@Enumerated(EnumType.STRING)
	@XmlElement(name = "requestInfoMethod", required = false)
	private RequestInfoMethod requestInfoMethod = RequestInfoMethod.GET;

	@ElementCollection(fetch = FetchType.EAGER)
	@MapKeyColumn(name = "name")
	@Column(name = "value")
	@CollectionTable(name = "oauth_mapping", joinColumns = @JoinColumn(name = "oauth_id"))
	@XmlTransient
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

	public OAuthServer addMapping(String pname, String pvalue) {
		mapping.put(pname, pvalue);
		return this;
	}

	@XmlAnyElement
	@XmlJavaTypeAdapter(OauthMapAdapter.class)
	public OAuthServer setMapping(Map<String, String> map) {
		mapping.putAll(map);
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
}
