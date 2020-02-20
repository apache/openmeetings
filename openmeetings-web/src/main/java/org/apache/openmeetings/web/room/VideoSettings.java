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

import static org.apache.openmeetings.util.OpenmeetingsVariables.getRoomSettings;

import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.PriorityHeaderItem;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.ResourceReference;

import com.github.openjson.JSONObject;

public class VideoSettings extends Panel {
	private static final long serialVersionUID = 1L;
	private static final ResourceReference SETTINGS_JS_REFERENCE = new JavaScriptResourceReference(VideoSettings.class, "settings.js");
	public static final String URL = "url";
	public static final String FALLBACK = "fallback";

	public VideoSettings(String id) {
		super(id);
		add(new EmptyPanel("ws-panel"));
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(new PriorityHeaderItem(JavaScriptHeaderItem.forReference(SETTINGS_JS_REFERENCE)));
	}

	public static JSONObject getInitJson(String sid) {
		return new JSONObject(getRoomSettings().toString())
				.put("interview", false)
				.put("audioOnly", false)
				.put("sid", sid);
	}
}
