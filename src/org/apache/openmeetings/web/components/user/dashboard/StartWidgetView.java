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
package org.apache.openmeetings.web.components.user.dashboard;

import static org.apache.openmeetings.web.util.UrlFragment.CALENDAR;

import org.apache.openmeetings.web.pages.MainPage;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.model.Model;

import ro.fortsoft.wicket.dashboard.Widget;
import ro.fortsoft.wicket.dashboard.web.WidgetView;

public class StartWidgetView extends WidgetView {
	private static final long serialVersionUID = -3886388347737468022L;

	public StartWidgetView(String id, Model<Widget> model) {
		super(id, model);
		add(new Button("calendar").add(new AjaxEventBehavior("onclick") {
			private static final long serialVersionUID = -1736827409409436737L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				((MainPage)getPage()).updateContents(CALENDAR, target);
			}
		}));
	}
}
