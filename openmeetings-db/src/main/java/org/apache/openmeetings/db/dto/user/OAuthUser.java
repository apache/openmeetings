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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import org.apache.openmeetings.db.entity.server.OAuthServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.openjson.JSONArray;
import com.github.openjson.JSONException;
import com.github.openjson.JSONObject;

public class OAuthUser implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(OAuthUser.class);
	private final String uid;
	private String email;
	private String firstName;
	private String lastName;
	private String picture;
	private String locale;

	/**
	 * OAuth constructor
	 *
	 * @param jsonStr - json data from server as string
	 * @param server - {@link OAuthServer} to get mapping
	 */
	public OAuthUser(String jsonStr, OAuthServer server) {
		// get attributes names
		String pEmail = server.getEmailParamName();
		String pFirstname = server.getFirstnameParamName();
		String pLastname = server.getLastnameParamName();
		String pLogin = server.getLoginParamName();
		JSONObject json = getJSON(jsonStr, pLogin);
		String login = json.getString(pLogin);

		this.uid = login;
		try {
			this.email = json.has(pEmail)
					? json.getString(pEmail)
					: String.format("%s@%s", login, new URL(server.getIconUrl()).getHost());
		} catch (JSONException | MalformedURLException e) {
			this.email = null;
			// no-op, bad user
			log.error("Failed to get user from JSON: {}", json);
		}
		if (json.has(pFirstname)) {
			this.firstName = json.getString(pFirstname);
		}
		if (json.has(pLastname)) {
			this.lastName = json.getString(pLastname);
		}
	}

	/**
	 * constructor for mobile service
	 *
	 * @param umap - google data
	 */
	public OAuthUser(Map<String, String> umap) {
		this.uid = umap.get("login");
		this.email = umap.get("email");
		this.firstName = umap.get("firstname");
		this.lastName = umap.get("lastname");
	}

	public String getUid() {
		return uid;
	}

	public String getEmail() {
		return email;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getPicture() {
		return picture;
	}

	public String getLocale() {
		return locale;
	}

	private static JSONObject getJSON(String str, String prop) {
		JSONObject json = new JSONObject(str);
		if (json.has(prop)) {
			return json;
		}
		// will only check 1 additional level
		for (String key : json.keySet()) {
			Object o = json.get(key);
			if (o instanceof JSONArray) {
				JSONArray ja = (JSONArray)o;
				//Assuming here array consist of objects
				for (int i = 0; i < ja.length(); ++i) {
					JSONObject jao = ja.getJSONObject(i);
					if (jao.has(prop)) {
						return jao;
					}
				}
			}
		}
		return new JSONObject();
	}
}
