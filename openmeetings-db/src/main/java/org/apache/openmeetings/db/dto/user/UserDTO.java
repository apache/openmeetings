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
import java.util.Set;

import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.user.Address;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.entity.user.User.Right;
import org.apache.openmeetings.db.entity.user.User.Type;

public class UserDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long id;
	private String login;
	private String password;
	private String firstname;
	private String lastname;
	private Set<Right> rights;
	private Long languageId;
	private Address address;
	private String timeZoneId;
	private String externalId;
	private String externalType;
	private Type type = Type.user;

	public UserDTO() {}

	public UserDTO(User u) {
		id = u.getId();
		firstname = u.getFirstname();
		lastname = u.getLastname();
		rights = u.getRights();
		login = u.getLogin();
		languageId = u.getLanguageId();
		address = u.getAddress();
		timeZoneId = u.getTimeZoneId();
		type = u.getType();
		externalId = u.getExternalId();
		externalType = u.getExternalType();
	}
	
	public User get(UserDao userDao) {
		User u = id == null ? new User() : userDao.get(id);
		u.setFirstname(firstname);
		u.setLastname(lastname);
		u.setRights(rights);
		u.setLanguageId(languageId);
		u.setAddress(address);
		u.setTimeZoneId(timeZoneId);
		u.setExternalId(externalId);
		u.setExternalType(externalType);
		u.setType(type);
		return u;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public Set<Right> getRights() {
		return rights;
	}

	public void setRights(Set<Right> rights) {
		this.rights = rights;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public Long getLanguageId() {
		return languageId;
	}

	public void setLanguageId(Long languageId) {
		this.languageId = languageId;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public String getTimeZoneId() {
		return timeZoneId;
	}

	public void setTimeZoneId(String timeZoneId) {
		this.timeZoneId = timeZoneId;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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
}
