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

import static org.apache.openmeetings.util.OpenmeetingsVariables.FLASH_NATIVE_SSL;
import static org.apache.openmeetings.util.OpenmeetingsVariables.FLASH_PORT;
import static org.apache.openmeetings.util.OpenmeetingsVariables.FLASH_SECURE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.FLASH_SSL_PORT;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getWebAppRootKey;

import java.net.URL;

import org.apache.openmeetings.util.OpenmeetingsVariables;
import org.apache.openmeetings.web.common.BasePanel;
import org.apache.openmeetings.web.common.OmAjaxClientInfoBehavior;
import org.apache.openmeetings.web.util.ExtendedClientProperties;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.PriorityHeaderItem;
import org.apache.wicket.protocol.http.request.WebClientInfo;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.util.string.StringValue;
import org.apache.wicket.util.string.Strings;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

import com.github.openjson.JSONArray;
import com.github.openjson.JSONObject;

public class SwfPanel extends BasePanel {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Red5LoggerFactory.getLogger(SwfPanel.class, getWebAppRootKey());
	public static final ResourceReference FLASH_JS_REFERENCE = new JavaScriptResourceReference(SwfPanel.class, "swf-functions.js");
	public static final String SWF = "swf";
	public static final String SWF_TYPE_NETWORK = "network";
	public static final String SWF_TYPE_SETTINGS = "settings";
	private final PageParameters pp;

	public SwfPanel(String id) {
		this(id, new PageParameters());
	}

	public SwfPanel(String id, PageParameters pp) {
		super(id);
		this.pp = pp;
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		add(new OmAjaxClientInfoBehavior() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onClientInfo(AjaxRequestTarget target, WebClientInfo info) {
				super.onClientInfo(target, info);
				ExtendedClientProperties cp = (ExtendedClientProperties)info.getProperties();
				PageParameters spp = new PageParameters(pp);
				target.appendJavaScript(getInitFunction(spp, cp));
			}
		});
	}

	private static ResourceReference newResourceReference() {
		return new JavaScriptResourceReference(SwfPanel.class, "swf-functions.js");
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(new PriorityHeaderItem(JavaScriptHeaderItem.forReference(newResourceReference())));
	}

	public String getInitFunction(PageParameters pp, ExtendedClientProperties cp) {
		String initStr = null;
		StringValue type = pp.get(SWF);
		String swf = getFlashFile(type);
		if (!Strings.isEmpty(swf)) {
			String lbls = null;
			if (SWF_TYPE_NETWORK.equals(type.toString())) {
				lbls = getStringLabels(
						"network.test.ms", "network.test.mb", "network.test.sec"
						, "network.test.click.play", "network.test.copy.log"
						, "network.test.report", "network.test.report.start", "network.test.report.error"
						, "network.test.report.con.err"
						, "network.test.ping", "network.test.ping.avg", "network.test.ping.rcv"
						, "network.test.ping.lost", "network.test.ping.load"
						, "network.test.port", "network.test.port.avail", "network.test.port.stopped"
						, "network.test.jitter", "network.test.jitter.avg", "network.test.jitter.min"
						, "network.test.jitter.max"
						, "network.test.dwn", "network.test.dwn.bytes", "network.test.dwn.time"
						, "network.test.dwn.speed"
						, "network.test.upl", "network.test.upl.bytes", "network.test.upl.time"
						, "network.test.upl.speed"
						);
			}
			JSONObject s = new JSONObject();
			try {
				URL url = new URL(cp.getCodebase());
				String path = url.getPath();
				path = path.substring(1, path.indexOf('/', 2) + 1);
				JSONObject gs = OpenmeetingsVariables.getRoomSettings();
				s.put("flashProtocol", gs.getBoolean(FLASH_SECURE) ? "rtmps" : "rtmp")
						.put("flashPort", gs.getBoolean(FLASH_SECURE) ? gs.getString(FLASH_SSL_PORT) : gs.getString(FLASH_PORT))
						.put("proxy", gs.getBoolean(FLASH_NATIVE_SSL) ? "best" : "none")
						.put("httpProtocol", url.getProtocol())
						.put("httpPort", url.getPort() > -1 ? url.getPort() : url.getDefaultPort())
						.put("host", url.getHost())
						.put("path", path)
						.put("width", "100%")
						.put("height", "100%")
						.put("wmode", "opaque");
			} catch (Exception e) {
				log.error("Error while constructing video settings parameters", e);
			}
			initStr = String.format("labels = %s; initSwf(%s, '%s', '%s', %s);"
					, lbls, "$('.hash-panel-main')", swf, "nettest", s.toString());
		}
		return initStr;
	}

	private static String getFlashFile(StringValue type) {
		return SWF_TYPE_NETWORK.equals(type.toString()) ? "networktest.swf" : "";
	}

	public String getStringLabels(String... ids) {
		JSONArray arr = new JSONArray();
		for (String id : ids) {
			arr.put(new JSONObject().put("id", id).put("value", getString(id)));
		}
		return arr.toString();
	}
}
