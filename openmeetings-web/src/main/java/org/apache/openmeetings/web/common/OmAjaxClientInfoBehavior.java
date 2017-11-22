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
package org.apache.openmeetings.web.common;

import java.util.List;

import org.apache.openmeetings.web.app.WebSession;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxClientInfoBehavior;
import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.PriorityHeaderItem;
import org.apache.wicket.markup.html.pages.BrowserInfoForm;
import org.apache.wicket.protocol.http.request.WebClientInfo;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

public class OmAjaxClientInfoBehavior extends AjaxClientInfoBehavior {
	private static final long serialVersionUID = 1L;
	private static final JavaScriptResourceReference MAIN_JS = new JavaScriptResourceReference(MainPanel.class, "main.js") {
		private static final long serialVersionUID = 1L;

		@Override
		public List<HeaderItem> getDependencies() {
			List<HeaderItem> list = super.getDependencies();
			list.add(JavaScriptHeaderItem.forReference(BrowserInfoForm.JS));
			return list;
		}
	};

	@Override
	public void renderHead(Component component, IHeaderResponse response) {
		super.renderHead(component, response);
		response.render(new PriorityHeaderItem(JavaScriptHeaderItem.forReference(MAIN_JS)));
	}

	@Override
	protected WebClientInfo newWebClientInfo(RequestCycle requestCycle) {
		return new WebClientInfo(requestCycle, WebSession.get().getExtendedProperties());
	}
}
