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

import org.apache.openmeetings.db.dto.user.ExternalUserDTO;

import com.github.openjson.JSONObject;

/**
 * This Class is marshaled as JSON-Object and stored as a String in the DB to
 * make it more easy to extend it
 *
 * @author sebastianwagner
 *
 */
public class RemoteSessionObject {
	private String username;
	private String firstname;
	private String lastname;
	private String pictureUrl;
	private String email;
	private String externalId;
	private String externalType;

	public RemoteSessionObject() {
		// def constructor
	}

	/**
	 * @param user - {@link ExternalUserDTO} object
	 *
	 *            06.09.2009 17:05:48 sebastianwagner
	 */
	public RemoteSessionObject(ExternalUserDTO user) {
		this.username = user.getLogin();
		this.firstname = user.getFirstname();
		this.lastname = user.getLastname();
		this.pictureUrl = user.getProfilePictureUrl();
		this.email = user.getEmail();
		this.externalId = user.getExternalId();
		this.externalType = user.getExternalType();
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getPictureUrl() {
		return pictureUrl;
	}

	public void setPictureUrl(String pictureUrl) {
		this.pictureUrl = pictureUrl;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public String getExternalType() {
		return externalType;
	}

	public void setExternalType(String externalType) {
		this.externalType = externalType;
	}

	public static RemoteSessionObject fromString(String s) {
		JSONObject o = new JSONObject(s);
		RemoteSessionObject ro = new RemoteSessionObject();
		ro.username = o.optString("username");
		ro.firstname = o.optString("firstname");
		ro.lastname = o.optString("lastname");
		ro.pictureUrl = o.optString("pictureUrl");
		ro.email = o.optString("email");
		ro.externalId = o.getString("externalId");
		ro.externalType = o.getString("externalType");
		return ro;
	}

	@Override
	public String toString() {
		return new JSONObject(this).toString();
	}
}
