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

import org.apache.openmeetings.web.app.Application;
import org.apache.wicket.model.Model;
import org.wicketstuff.dashboard.AbstractWidget;
import org.wicketstuff.dashboard.Widget;
import org.wicketstuff.dashboard.WidgetLocation;
import org.wicketstuff.dashboard.web.WidgetView;

public class StartWidget extends AbstractWidget {
	private static final long serialVersionUID = 1L;

	public StartWidget() {
		super();
		location = new WidgetLocation(1, 0);
		init();
	}
	
	@Override
	public void init() {
		super.init();
		title = Application.getString(774L);
		id = "StartWidget";
	}
	
	@Override
	public WidgetView createView(String viewId) {
		return new StartWidgetView(viewId, new Model<Widget>(this));
	}
}
