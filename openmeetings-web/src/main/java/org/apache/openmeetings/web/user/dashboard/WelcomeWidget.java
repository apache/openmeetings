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
package org.apache.openmeetings.web.user.dashboard;

import org.apache.openmeetings.web.app.WebSession;
import org.apache.wicket.model.Model;

import ro.fortsoft.wicket.dashboard.AbstractWidget;
import ro.fortsoft.wicket.dashboard.Widget;
import ro.fortsoft.wicket.dashboard.WidgetLocation;
import ro.fortsoft.wicket.dashboard.web.WidgetView;

public class WelcomeWidget extends AbstractWidget {
	private static final long serialVersionUID = 1L;

	public WelcomeWidget() {
		super();
		location = new WidgetLocation(0, 0);
		init();
	}
	
	@Override
	public void init() {
		super.init();
		title = WebSession.getString(1546L);
	}
	
	public WidgetView createView(String viewId) {
		return new WelcomeWidgetView(viewId, new Model<Widget>(this));
	}
}
