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
package org.apache.openmeetings.web.room;

import static org.apache.openmeetings.core.remote.red5.ScopeApplicationAdapter.FLASH_FPS;
import static org.apache.openmeetings.core.remote.red5.ScopeApplicationAdapter.FLASH_NATIVE_SSL;
import static org.apache.openmeetings.core.remote.red5.ScopeApplicationAdapter.FLASH_PORT;
import static org.apache.openmeetings.core.remote.red5.ScopeApplicationAdapter.FLASH_SECURE;
import static org.apache.openmeetings.core.remote.red5.ScopeApplicationAdapter.FLASH_SSL_PORT;
import static org.apache.openmeetings.core.remote.red5.ScopeApplicationAdapter.FLASH_VIDEO_CODEC;
import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;
import static org.apache.openmeetings.web.app.Application.getBean;

import java.net.URL;

import org.apache.directory.api.util.Strings;
import org.apache.openmeetings.core.remote.red5.ScopeApplicationAdapter;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.util.ExtendedClientProperties;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.PriorityHeaderItem;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

import com.github.openjson.JSONObject;

public class VideoSettings extends Panel {
	private final static long serialVersionUID = 1L;
	private final static Logger log = Red5LoggerFactory.getLogger(VideoSettings.class, webAppRootKey);
	private final static ResourceReference SETTINGS_JS_REFERENCE = new JavaScriptResourceReference(VideoSettings.class, "settings.js");
	private final static String URL = "url";
	private final static String FALLBACK = "fallback";

	public VideoSettings(String id) {
		super(id);
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(new PriorityHeaderItem(JavaScriptHeaderItem.forReference(SETTINGS_JS_REFERENCE)));
	}

	private static String getUri(String protocol, String host, Object port, String app) {
		return String.format("%s://%s:%s/%s", protocol, host, port, app);
	}

	public static StringBuilder getInitScript(ExtendedClientProperties cp, String scope, String uid) {
		JSONObject gs = getBean(ScopeApplicationAdapter.class).getFlashSettings();
		JSONObject s = new JSONObject()
				.put(FLASH_VIDEO_CODEC, gs.get(FLASH_VIDEO_CODEC))
				.put(FLASH_FPS, gs.get(FLASH_FPS))
				.put("SID", WebSession.getSid())
				.put("wmode", cp.isBrowserInternetExplorer() && cp.getBrowserVersionMajor() == 11 ? "opaque" : "direct");
		if (!Strings.isEmpty(uid)) {
			s.put("uid", uid);
		}
		try {
			URL url = new URL(cp.getCodebase());
			String path = url.getPath();
			path = path.substring(1, path.indexOf('/', 2) + 1) + scope;
			if (gs.getBoolean(FLASH_SECURE)) {
				s.put(FLASH_NATIVE_SSL, gs.get(FLASH_NATIVE_SSL));
				s.put(URL, getUri("rtmps", url.getHost(), gs.getString(FLASH_SSL_PORT), path));
				s.put(FALLBACK, getUri("rtmps", url.getHost(), url.getPort(), path));
			} else {
				s.put(URL, getUri("rtmp", url.getHost(), gs.getString(FLASH_PORT), path));
				s.put(FALLBACK, getUri("rtmpt", url.getHost(), url.getPort(), path));
			}
		} catch (Exception e) {
			log.error("Error while constructing video settings parameters", e);
		}
		return new StringBuilder("VideoSettings.init(").append(s.toString()).append(");");
	}
}
