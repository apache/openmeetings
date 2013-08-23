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

/**
 * This Class is marshaled as an XML-Object and stored as a String in the DB to make
 * it more easy to extend it
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
	
	private String externalUserId;
	private String externalUserType;
	
	public RemoteSessionObject(String username, String firstname, String lastname,
			String pictureUrl, String email) {
		this(username, firstname, lastname, pictureUrl, email, "0", "");
	}
	
	
	/**
	 * @param username
	 * @param firstname
	 * @param lastname
	 * @param pictureUrl
	 * @param email
	 * @param externalUserId
	 * @param externalUserType
	 *
	 * 06.09.2009 17:05:48
	 * sebastianwagner
	 * 
	 * 
	 */
	public RemoteSessionObject(String username, String firstname,
			String lastname, String pictureUrl, String email,
			String externalUserId, String externalUserType) {
		super();
		this.username = username;
		this.firstname = firstname;
		this.lastname = lastname;
		this.pictureUrl = pictureUrl;
		this.email = email;
		this.externalUserId = externalUserId;
		this.externalUserType = externalUserType;
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

	public String getExternalUserId() {
		return externalUserId;
	}
	public void setExternalUserId(String externalUserId) {
		this.externalUserId = externalUserId;
	}

	public String getExternalUserType() {
		return externalUserType;
	}
	public void setExternalUserType(String externalUserType) {
		this.externalUserType = externalUserType;
	}
	
}
