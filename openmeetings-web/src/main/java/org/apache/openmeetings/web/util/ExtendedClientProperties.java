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
package org.apache.openmeetings.web.util;

import static org.apache.openmeetings.web.app.Application.HASH_MAPPING;

import org.apache.wicket.protocol.http.ClientProperties;
import org.apache.wicket.request.IRequestParameters;
import org.json.JSONObject;

public class ExtendedClientProperties extends ClientProperties {
	private static final long serialVersionUID = 1L;
	private String baseUrl;
	private String codebase;
	private String settings;

	public String getCodebase() {
		return codebase;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setSettings(JSONObject s) {
		settings = s.toString();
	}

	public JSONObject getSettings() {
		try {
			return settings == null ? new JSONObject() : new JSONObject(settings.toString());
		} catch (Exception e) {
			//can throw, no op
		}
		return new JSONObject();
	}

	@Override
	public void read(IRequestParameters parameters) {
		super.read(parameters);
		String _url = parameters.getParameterValue("codebase").toString("N/A");
		StringBuilder sb = new StringBuilder(_url);
		if (_url.endsWith(HASH_MAPPING)) {
			sb.setLength(_url.length() - HASH_MAPPING.length());
		}
		if (sb.charAt(sb.length() - 1) != '/') {
			sb.append('/');
		}
		baseUrl = sb.toString();
		codebase = sb.append("screenshare").toString();
		settings = parameters.getParameterValue("settings").toString("{}");
	}
}
