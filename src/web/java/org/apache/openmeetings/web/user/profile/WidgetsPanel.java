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
package org.apache.openmeetings.web.user.profile;

import static org.apache.openmeetings.web.app.Application.getDashboardContext;
import static org.apache.openmeetings.web.app.WebSession.getDashboard;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import ro.fortsoft.wicket.dashboard.Dashboard;
import ro.fortsoft.wicket.dashboard.Widget;
import ro.fortsoft.wicket.dashboard.WidgetDescriptor;
import ro.fortsoft.wicket.dashboard.web.DashboardContext;

public class WidgetsPanel extends Panel {
	private static final long serialVersionUID = -3959536582694664524L;

	private Widget isDisplayed(WidgetDescriptor wd) {
		//FIXME the only way to compare is By TITLE
		for (Widget w : getDashboard().getWidgets()) {
			if (w.getTitle().equals(wd.getName())) {
				return w;
			}
		}
		return null;
	}
	public WidgetsPanel(String id) {
		super(id);
		
		add(new ListView<WidgetDescriptor>("widgets"
				, getDashboardContext().getWidgetRegistry().getWidgetDescriptors()) {
					private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<WidgetDescriptor> item) {
				final WidgetDescriptor wd = item.getModelObject();
				item.add(new Label("name", wd.getName()));
				item.add(new Label("description", wd.getDescription()));
				item.add(new AjaxCheckBox("display", Model.of(isDisplayed(wd) != null)) {
					private static final long serialVersionUID = -7079665921153653164L;

					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						Widget w = isDisplayed(wd);
						boolean b = getModelObject();
						DashboardContext dashboardContext = getDashboardContext();
						Dashboard d = getDashboard();
						if (w != null && !b) {
							d.deleteWidget(w.getId());
						}
						if (w == null && b) {
							d.addWidget(dashboardContext.getWidgetFactory().createWidget(wd));
						}
						dashboardContext.getDashboardPersiter().save(d);
					}
				});
			}
		});
	}
}
