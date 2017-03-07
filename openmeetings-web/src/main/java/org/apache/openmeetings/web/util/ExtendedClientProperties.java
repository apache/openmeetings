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
import static org.apache.openmeetings.web.app.Application.NOTINIT_MAPPING;
import static org.apache.openmeetings.web.app.Application.SIGNIN_MAPPING;

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

	private static StringBuilder cleanUrl(StringBuilder sb, String _url) {
		for (String tail : new String[]{HASH_MAPPING, SIGNIN_MAPPING, NOTINIT_MAPPING}) {
			if (_url.endsWith(tail)) {
				sb.setLength(_url.length() - tail.length());
				break;
			}
		}
		return sb;
	}

	@Override
	public void read(IRequestParameters parameters) {
		super.read(parameters);
		String _url = parameters.getParameterValue("codebase").toString("N/A");
		StringBuilder sb = new StringBuilder(_url);
		cleanUrl(sb, _url);
		if (sb.charAt(sb.length() - 1) != '/') {
			sb.append('/');
		}
		baseUrl = sb.toString();
		codebase = sb.append("screenshare").toString();
		settings = parameters.getParameterValue("settings").toString("{}");
	}
}
