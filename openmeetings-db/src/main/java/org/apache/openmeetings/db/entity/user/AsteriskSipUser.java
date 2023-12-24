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
package org.apache.openmeetings.db.entity.user;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.openmeetings.db.bind.adapter.IntAdapter;
import org.apache.openmeetings.db.bind.adapter.LongAdapter;

@Entity
@Table(name = "sipusers")
@XmlRootElement(name = "asterisksipuser")
@XmlAccessorType(XmlAccessType.FIELD)
public class AsteriskSipUser implements Serializable {
	private static final long serialVersionUID = 1L;

	@XmlType(namespace = "org.apache.openmeetings.user.asterisk")
	public enum Type {
		friend, user, peer
	}

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@XmlElement(name = "id", required = false)
	@XmlJavaTypeAdapter(value = LongAdapter.class, type = long.class)
	private long id;

	@Column(name = "type", nullable = false, length = 6)
	@Enumerated(EnumType.STRING)
	@XmlElement(name = "type", required = false)
	private Type type = Type.friend;

	@Column(name = "name", nullable = false, length = 128)
	@XmlElement(name = "name", required = false)
	private String name = ""; // Varchar 128

	@Column(name = "secret", length = 128)
	@XmlElement(name = "secret", required = false)
	private String secret; // Varchar 128

	@Column(name = "context", length = 128)
	@XmlElement(name = "context", required = false)
	private String context; // Varchar 128

	@Column(name = "host", nullable = false, length = 128)
	@XmlElement(name = "host", required = false)
	private String host = "dynamic"; // Varchar 128

	@Column(name = "ipaddr", nullable = false, length = 128)
	@XmlElement(name = "ipaddr", required = false)
	private String ipaddr = ""; // Varchar 128

	@Column(name = "port", nullable = false, length = 8)
	@XmlElement(name = "port", required = false)
	@XmlJavaTypeAdapter(IntAdapter.class)
	private Integer port = 0; // mediumint(8)

	@Column(name = "regseconds", nullable = false)
	@XmlElement(name = "regseconds", required = false)
	@XmlJavaTypeAdapter(LongAdapter.class)
	private Long regseconds = 0L; // Bigint

	@Column(name = "defaultuser", nullable = true, length = 128)
	@XmlElement(name = "defaultuser", required = false)
	private String defaultuser; // Varchar 128

	@Column(name = "fullcontact", length = 512)
	@XmlElement(name = "fullcontact", required = false)
	private String fullcontact;

	@Column(name = "regserver", nullable = true, length = 128)
	@XmlElement(name = "regserver", required = false)
	private String regserver; // Varchar 128

	@Column(name = "useragent", nullable = true, length = 128)
	@XmlElement(name = "useragent", required = false)
	private String useragent; // Varchar 128

	@Column(name = "lastms")
	@XmlElement(name = "lastms", required = false)
	@XmlJavaTypeAdapter(IntAdapter.class)
	private Integer lastms; // Integer

	@Column(name = "md5secret")
	@XmlElement(name = "md5secret", required = false)
	private String md5secret;

	@Column(name = "nat", nullable = false)
	@XmlElement(name = "nat", required = false)
	private String nat = "force_rport,comedia";

	@Column(name = "callbackextension", nullable = true, length = 250)
	@XmlElement(name = "callbackextension", required = false)
	private String callbackextension;

	@Column(name = "transport", nullable = false, length = 100)
	@XmlElement(name = "transport", required = false)
	private String transport = "wss,ws,udp";

	@Column(name = "allow", nullable = false, length = 100)
	@XmlElement(name = "allow", required = false)
	private String allow = "!all,ulaw,opus,vp8";

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getIpaddr() {
		return ipaddr;
	}

	public void setIpaddr(String ipaddr) {
		this.ipaddr = ipaddr;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public Long getRegseconds() {
		return regseconds;
	}

	public void setRegseconds(Long regseconds) {
		this.regseconds = regseconds;
	}

	public String getDefaultuser() {
		return defaultuser;
	}

	public void setDefaultuser(String defaultuser) {
		this.defaultuser = defaultuser;
	}

	public String getFullcontact() {
		return fullcontact;
	}

	public void setFullcontact(String fullcontact) {
		this.fullcontact = fullcontact;
	}

	public String getRegserver() {
		return regserver;
	}

	public void setRegserver(String regserver) {
		this.regserver = regserver;
	}

	public String getUseragent() {
		return useragent;
	}

	public void setUseragent(String useragent) {
		this.useragent = useragent;
	}

	public Integer getLastms() {
		return lastms;
	}

	public void setLastms(Integer lastms) {
		this.lastms = lastms;
	}

	public String getMd5secret() {
		return md5secret;
	}

	public void setMd5secret(String md5secret) {
		this.md5secret = md5secret;
	}

	public String getNat() {
		return nat;
	}

	public void setNat(String nat) {
		this.nat = nat;
	}

	public String getCallbackextension() {
		return callbackextension;
	}

	public void setCallbackextension(String callbackextension) {
		this.callbackextension = callbackextension;
	}

	public String getAllow() {
		return allow;
	}

	public void setAllow(String allow) {
		this.allow = allow;
	}

	public String getTransport() {
		return transport;
	}

	public void setTransport(String transport) {
		this.transport = transport;
	}
}
