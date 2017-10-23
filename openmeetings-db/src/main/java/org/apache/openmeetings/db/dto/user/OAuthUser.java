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

import static org.apache.openmeetings.util.OpenmeetingsVariables.getWebAppRootKey;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import org.apache.openmeetings.db.entity.server.OAuthServer;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

import com.github.openjson.JSONArray;
import com.github.openjson.JSONException;
import com.github.openjson.JSONObject;

public class OAuthUser implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Red5LoggerFactory.getLogger(OAuthUser.class, getWebAppRootKey());
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
		String email = server.getEmailParamName();
		String firstname = server.getFirstnameParamName();
		String lastname = server.getLastnameParamName();
		JSONObject json = getJSON(jsonStr, server.getLoginParamName());
		String login = json.getString(server.getLoginParamName());

		this.uid = login;
		try {
			this.email = json.has(email)
					? json.getString(email)
					: String.format("%s@%s", login, new URL(server.getIconUrl()).getHost());
		} catch (JSONException | MalformedURLException e) {
			this.email = null;
			// no-op, bad user
			log.error("Failed to get user from JSON: {}", json);
		}
		if (json.has(firstname)) {
			this.firstName = json.getString(firstname);
		}
		if (json.has(lastname)) {
			this.lastName = json.getString(lastname);
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
			if (o instanceof JSONObject) {
				JSONObject jo = (JSONObject)o;
				if (jo.has(prop)) {
					return jo;
				}
			} else if (o instanceof JSONArray) {
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
