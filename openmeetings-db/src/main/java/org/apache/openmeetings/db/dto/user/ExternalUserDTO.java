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
package org.apache.openmeetings.db.dto.user;

import java.io.Serializable;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

import com.github.openjson.JSONObject;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ExternalUserDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	private String login;
	private String firstname;
	private String lastname;
	private String profilePictureUrl;
	private String email;
	private String externalId;
	private String externalType;

	public ExternalUserDTO() {
		//def constructor
	}

	public String getLogin() {
		return login;
	}

	public ExternalUserDTO setLogin(String login) {
		this.login = login;
		return this;
	}

	public String getFirstname() {
		return firstname;
	}

	public ExternalUserDTO setFirstname(String firstname) {
		this.firstname = firstname;
		return this;
	}

	public String getLastname() {
		return lastname;
	}

	public ExternalUserDTO setLastname(String lastname) {
		this.lastname = lastname;
		return this;
	}

	public String getProfilePictureUrl() {
		return profilePictureUrl;
	}

	public ExternalUserDTO setProfilePictureUrl(String profilePictureUrl) {
		this.profilePictureUrl = profilePictureUrl;
		return this;
	}

	public String getEmail() {
		return email;
	}

	public ExternalUserDTO setEmail(String email) {
		this.email = email;
		return this;
	}

	public String getExternalId() {
		return externalId;
	}

	public ExternalUserDTO setExternalId(String externalId) {
		this.externalId = externalId;
		return this;
	}

	public String getExternalType() {
		return externalType;
	}

	public ExternalUserDTO setExternalType(String externalType) {
		this.externalType = externalType;
		return this;
	}

	public static ExternalUserDTO fromString(String s) {
		JSONObject o = new JSONObject(s);
		ExternalUserDTO u = new ExternalUserDTO();
		u.email = o.optString("email", null);
		u.externalId = o.optString("externalId", null);
		u.externalType = o.optString("externalType", null);
		u.firstname = o.optString("firstname", null);
		u.lastname = o.optString("lastname", null);
		u.login = o.optString("login", null);
		u.profilePictureUrl = o.optString("profilePictureUrl", null);
		return u;
	}

	@Override
	public String toString() {
		return new JSONObject(this).toString();
	}
}
