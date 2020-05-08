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

import org.apache.openmeetings.web.common.BasePanel;
import org.apache.openmeetings.web.common.OmAjaxClientInfoBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.protocol.http.request.WebClientInfo;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.ResourceReference;

import com.github.openjson.JSONObject;

public class NetTestPanel extends BasePanel {
	private static final long serialVersionUID = 1L;
	private static final ResourceReference NETTEST_JS_REFERENCE = new JavaScriptResourceReference(VideoSettings.class, "nettest.js");

	public NetTestPanel(String id) {
		super(id);
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(JavaScriptHeaderItem.forReference(NETTEST_JS_REFERENCE));
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		add(new OmAjaxClientInfoBehavior() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onClientInfo(AjaxRequestTarget target, WebClientInfo info) {
				super.onClientInfo(target, info);
				target.appendJavaScript(String.format("NetTest.init(%s);", getStringLabels()));
			}
		});
	}

	private JSONObject getStringLabels() {
		JSONObject o = new JSONObject();
		for (String id : new String[] {"network.test.ms", "network.test.mb", "network.test.sec"
				, "network.test.click.play", "network.test.copy.log", "network.test.report.start"
				, "network.test.ping", "network.test.ping.avg", "network.test.ping.rcv"
				, "network.test.ping.lost", "network.test.ping.load"
				, "network.test.port", "network.test.port.avail", "network.test.port.stopped"
				, "network.test.jitter", "network.test.jitter.avg", "network.test.jitter.min"
				, "network.test.jitter.max", "network.test.jitter.avgAbbr"
				, "network.test.dwn", "network.test.dwn.bytes", "network.test.dwn.time"
				, "network.test.dwn.speed"
				, "network.test.upl", "network.test.upl.bytes", "network.test.upl.time"
				, "network.test.upl.speed"})
		{
			o.put(id.substring("network.test.".length()), getString(id));
		}
		return o;
	}
}
