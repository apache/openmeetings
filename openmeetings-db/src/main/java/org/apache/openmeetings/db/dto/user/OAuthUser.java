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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.openmeetings.db.entity.server.OAuthServer;
import org.apache.wicket.util.string.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.openjson.JSONArray;
import com.github.openjson.JSONObject;

public class OAuthUser implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(OAuthUser.class);
	public static final String PARAM_LOGIN = "login";
	public static final String PARAM_EMAIL = "address.email";
	public static final String PARAM_FNAME = "firstname";
	public static final String PARAM_LNAME = "lastname";
	private final Map<String, String> userData;

	/**
	 * OAuth constructor
	 *
	 * @param jsonStr - json data from server as string
	 * @param server - {@link OAuthServer} to get mapping
	 */
	public OAuthUser(String jsonStr, OAuthServer server) {
		// get attributes names
		// we expect login mapping always in place
		JSONObject json = getJSON(jsonStr, server.getMapping().get(PARAM_LOGIN));
		Map<String, String> data = new HashMap<>();
		for (Map.Entry<String, String> entry : server.getMapping().entrySet()) {
			data.put(entry.getKey(), json.optString(entry.getValue()));
		}
		String login = data.get(PARAM_LOGIN);
		String email = data.get(PARAM_EMAIL);
		if (Strings.isEmpty(email)) {
			try {
				data.put(PARAM_EMAIL, String.format("%s@%s", login, new URL(server.getIconUrl()).getHost()));
			} catch (MalformedURLException e) {
				log.error("Failed to get user email from JSON: {}", json);
			}
		}
		userData = Collections.unmodifiableMap(data);
	}

	/**
	 * constructor for mobile service
	 *
	 * @param umap - google data
	 */
	public OAuthUser(Map<String, String> umap) {
		Map<String, String> data = new HashMap<>();
		data.put(PARAM_LOGIN, umap.get(PARAM_LOGIN));
		data.put(PARAM_EMAIL, umap.get("email"));
		data.put(PARAM_FNAME, umap.get(PARAM_FNAME));
		data.put(PARAM_LNAME, umap.get(PARAM_LNAME));
		userData = Collections.unmodifiableMap(data);
	}

	public Map<String, String> getUserData() {
		return userData;
	}

	public String getLogin() {
		return userData.get(PARAM_LOGIN);
	}

	public String getEmail() {
		return userData.get(PARAM_EMAIL);
	}

	private static JSONObject getJSON(String str, String prop) {
		JSONObject res = getJSON(new JSONObject(str), prop);
		return res == null ? new JSONObject() : res;
	}

	private static JSONObject getJSON(JSONObject json, String prop) {
		if (json.has(prop)) {
			return json;
		}
		// will only check 1 additional level
		for (String key : json.keySet()) {
			Object o = json.get(key);
			if (o instanceof JSONArray ja) {
				//Assuming here array consist of objects
				for (int i = 0; i < ja.length(); ++i) {
					JSONObject jao = ja.getJSONObject(i);
					JSONObject res = getJSON(jao, prop);
					if (res != null) {
						return res;
					}
				}
			} else if (o instanceof JSONObject jo) {
				JSONObject res = getJSON(jo, prop);
				if (res != null) {
					return res;
				}
			}
		}
		return null;
	}
}
