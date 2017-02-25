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

import static org.apache.wicket.RuntimeConfigurationType.DEVELOPMENT;

import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.common.BasePanel;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.PriorityHeaderItem;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.mapper.parameter.PageParametersEncoder;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.util.string.StringValue;
import org.apache.wicket.util.string.Strings;
import org.json.JSONArray;
import org.json.JSONObject;

public class SwfPanel extends BasePanel {
	private static final long serialVersionUID = 1L;
	public static final String SWF = "swf";
	public static final String SWF_TYPE_NETWORK = "network";
	public static final String SWF_TYPE_SETTINGS = "settings";

	public SwfPanel(String id) {
		this(id, new PageParameters());
	}

	public String getInitFunction() {
		return getInitFunction(new PageParameters());
	}

	public String getInitFunction(PageParameters pp) {
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
			} else if (SWF_TYPE_SETTINGS.equals(type.toString())) {
				lbls = getStringLabels("448", "449", "450", "451", "758", "447", "52", "53", "1429", "1430"
						, "775", "452", "767", "764", "765", "918", "54", "761", "762", "144", "203", "642"
						, "save.success");
			}
			initStr = String.format("var labels = %s; initSwf(%s);", lbls
					, new JSONObject().put("src", swf + new PageParametersEncoder().encodePageParameters(pp)).toString());
		}
		return initStr;
	}

	private String getFlashFile(StringValue type) {
		String fmt;
		if (SWF_TYPE_SETTINGS.equals(type.toString())) {
			fmt = "main%s.swf11.swf";
		} else if (SWF_TYPE_NETWORK.equals(type.toString())) {
			fmt = "networktesting%s.swf10.swf";
		} else {
			return "";
		}
		return String.format(fmt, DEVELOPMENT == getApplication().getConfigurationType() ? "debug" : "");
	}

	public static String getStringLabels(String... ids) {
		JSONArray arr = new JSONArray();
		for (String id : ids) {
			arr.put(new JSONObject().put("id", id).put("value", Application.getString(id)));
		}
		return arr.toString();
	}

	public SwfPanel(String id, PageParameters pp) {
		super(id);
		add(new Label("init", getInitFunction(pp)).setEscapeModelStrings(false));
	}

	private static ResourceReference newResourceReference() {
		return new JavaScriptResourceReference(SwfPanel.class, "swf-functions.js");
	}
	
	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(new PriorityHeaderItem(JavaScriptHeaderItem.forReference(newResourceReference())));
		response.render(new PriorityHeaderItem(JavaScriptHeaderItem.forUrl("js/openmeetings_functions.js")));
	}
}
