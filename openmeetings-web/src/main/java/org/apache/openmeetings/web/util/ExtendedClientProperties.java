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

import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.util.OpenmeetingsVariables;
import org.apache.wicket.protocol.http.ClientProperties;
import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.util.string.Strings;

import com.github.openjson.JSONObject;

public class ExtendedClientProperties extends ClientProperties {
	private static final long serialVersionUID = 1L;
	public static final String CAM = "cam";
	public static final String MIC = "mic";
	public static final String WIDTH = "width";
	public static final String HEIGHT = "height";
	private String baseUrl;
	private String codebase;
	private String settings;

	public String getCodebase() {
		return codebase;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public ExtendedClientProperties setSettings(JSONObject s) {
		settings = s.toString();
		return this;
	}

	public JSONObject getSettings() {
		try {
			return Strings.isEmpty(settings) ? new JSONObject() : new JSONObject(settings);
		} catch (Exception e) {
			//can throw, no op
		}
		return new JSONObject();
	}

	private static StringBuilder cleanUrl(String inUrl) {
		StringBuilder sb = new StringBuilder();
		String url = inUrl;
		int semi = url.indexOf(';');
		if (semi > -1) {
			url = url.substring(0, semi);
		}
		for (String tail : new String[]{HASH_MAPPING, SIGNIN_MAPPING, NOTINIT_MAPPING}) {
			if (url.endsWith(tail)) {
				url = url.substring(0, url.length() - tail.length());
				break;
			}
		}
		return sb.append(url);
	}

	@Override
	public void read(IRequestParameters parameters) {
		super.read(parameters);
		String url = parameters.getParameterValue("codebase").toString(OpenmeetingsVariables.getBaseUrl());
		StringBuilder sb = cleanUrl(url);
		if (sb.charAt(sb.length() - 1) != '/') {
			sb.append('/');
		}
		baseUrl = sb.toString();
		codebase = sb.append("screenshare").toString();
		settings = parameters.getParameterValue("settings").toString("{}");
	}

	public Client update(Client c) {
		JSONObject s = getSettings().optJSONObject("video");
		if (s == null) {
			s = new JSONObject();
		}
		return c.setRemoteAddress(getRemoteAddress())
				.setCam(s.optInt(CAM, -1))
				.setMic(s.optInt(MIC, -1))
				.setWidth(s.optInt(WIDTH, 0))
				.setHeight(s.optInt(HEIGHT, 0));
	}
}
