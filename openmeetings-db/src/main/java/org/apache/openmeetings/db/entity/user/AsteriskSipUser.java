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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlType;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Entity
@Table(name = "sipusers")
@Root(name="asterisksipuser")
public class AsteriskSipUser implements Serializable {
	private static final long serialVersionUID = 1L;

	@XmlType(namespace = "org.apache.openmeetings.user.asterisk")
	public enum Type {
		friend, user, peer
	}

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Element(data = true)
	private long id;

	@Column(name = "type", nullable = false, length = 6)
	@Enumerated(EnumType.STRING)
	@Element(data = true, required = false)
	private Type type = Type.friend;

	@Column(name = "name", nullable = false, length = 128)
	@Element(data = true, required = false)
	private String name = ""; // Varchar 128

	@Column(name = "secret", length = 128)
	@Element(data = true, required = false)
	private String secret; // Varchar 128

	@Column(name = "context", length = 128)
	@Element(data = true, required = false)
	private String context; // Varchar 128

	@Column(name = "host", nullable = false, length = 128)
	@Element(data = true, required = false)
	private String host = "dynamic"; // Varchar 128

	@Column(name = "ipaddr", nullable = false, length = 128)
	@Element(data = true, required = false)
	private String ipaddr = ""; // Varchar 128

	@Column(name = "port", nullable = false, length = 8)
	@Element(data = true, required = false)
	private Integer port = 0; // mediumint(8)

	@Column(name = "regseconds", nullable = false)
	@Element(data = true, required = false)
	private Long regseconds = 0L; // Bigint

	@Column(name = "defaultuser", nullable = true, length = 128)
	@Element(data = true, required = false)
	private String defaultuser; // Varchar 128

	@Column(name = "fullcontact", length = 512)
	@Element(data = true, required = false)
	private String fullcontact;

	@Column(name = "regserver", nullable = true, length = 128)
	@Element(data = true, required = false)
	private String regserver; // Varchar 128

	@Column(name = "useragent", nullable = true, length = 128)
	@Element(data = true, required = false)
	private String useragent; // Varchar 128

	@Column(name = "lastms")
	@Element(data = true, required = false)
	private Integer lastms; // Integer

	@Column(name = "md5secret")
	@Element(data = true, required = false)
	private String md5secret;

	@Column(name = "nat", nullable = false)
	@Element(data = true, required = false)
	private String nat = "force_rport,comedia";

	@Column(name = "callbackextension", nullable = true, length = 250)
	@Element(data = true, required = false)
	private String callbackextension;

	@Column(name = "allow", nullable = false, length = 100)
	@Element(data = true, required = false)
	private String allow = "ulaw;alaw;h264";

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
}
