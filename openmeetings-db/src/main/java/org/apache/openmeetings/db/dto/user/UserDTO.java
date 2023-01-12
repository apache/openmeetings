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

import static org.apache.openmeetings.db.util.DtoHelper.optEnum;
import static org.apache.openmeetings.db.util.DtoHelper.optEnumList;
import static org.apache.openmeetings.db.util.DtoHelper.optLong;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.xml.bind.annotation.XmlRootElement;

import org.apache.openmeetings.db.entity.user.Address;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.entity.user.User.Right;
import org.apache.openmeetings.db.entity.user.User.Type;

import com.github.openjson.JSONObject;

@XmlRootElement
public class UserDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long id;
	private String login;
	private String password;
	private String firstname;
	private String lastname;
	private Set<Right> rights = new HashSet<>();
	private Long languageId;
	private Address address = new Address();
	private String timeZoneId;
	private String externalId;
	private String externalType;
	private String pictureUri;
	private Type type = Type.USER;

	public UserDTO() {
		//def constructor
	}

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
		externalType = u.externalType();
		pictureUri = u.getPictureUri();
	}

	public static List<UserDTO> list(List<User> l) {
		List<UserDTO> uList = new ArrayList<>();
		if (l != null) {
			for (User u : l) {
				uList.add(new UserDTO(u));
			}
		}
		return uList;
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

	public String getPictureUri() {
		return pictureUri;
	}

	public void setPictureUri(String pictureUri) {
		this.pictureUri = pictureUri;
	}

	public static UserDTO fromString(String s) {
		return get(new JSONObject(s));
	}

	public static UserDTO get(JSONObject o) {
		if (o == null) {
			return null;
		}
		UserDTO u = new UserDTO();
		u.id = optLong(o, "id");
		u.login = o.optString("login");
		u.password = o.optString("password");
		u.firstname = o.optString("firstname");
		u.lastname = o.optString("lastname");
		u.rights.addAll(optEnumList(Right.class, o.optJSONArray("rights")));
		u.languageId = o.optLong("languageId");
		JSONObject a = o.optJSONObject("address");
		if (a != null) {
			u.address.setId(optLong(a, "id"));
			u.address.setCountry(a.optString("country"));
			u.address.setEmail(a.optString("email"));
		}
		u.timeZoneId = o.optString("timeZoneId");
		u.externalId = o.optString("externalId", null);
		u.externalType = o.optString("externalType", null);
		u.type = optEnum(Type.class, o, "type");
		u.pictureUri = o.optString("pictureUri", null);
		return u;
	}

	@Override
	public String toString() {
		return new JSONObject(this).toString();
	}
}
