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

import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DASHBOARD_SHOW_MYROOMS_KEY;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DASHBOARD_SHOW_RSS_KEY;
import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.Application.getDashboardContext;
import static org.apache.openmeetings.web.app.WebSession.getDashboard;

import java.util.Iterator;
import java.util.List;

import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.web.user.dashboard.MyRoomsWidget;
import org.apache.openmeetings.web.user.dashboard.RssWidget;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.wicketstuff.dashboard.Dashboard;
import org.wicketstuff.dashboard.Widget;
import org.wicketstuff.dashboard.WidgetDescriptor;
import org.wicketstuff.dashboard.web.DashboardContext;

public class WidgetsPanel extends Panel {
	private static final long serialVersionUID = 1L;

	private static Widget isDisplayed(WidgetDescriptor wd) {
		for (Widget w : getDashboard().getWidgets()) {
			if (w.getClass().getName().equals(wd.getWidgetClassName())) {
				return w;
			}
		}
		return null;
	}
	public WidgetsPanel(String id) {
		super(id);
		
		ConfigurationDao cfgDao = getBean(ConfigurationDao.class);
		boolean confShowMyRooms = 1 == cfgDao.getConfValue(CONFIG_DASHBOARD_SHOW_MYROOMS_KEY, Integer.class, "0");
		boolean confShowRss = 1 == cfgDao.getConfValue(CONFIG_DASHBOARD_SHOW_RSS_KEY, Integer.class, "0");
		List<WidgetDescriptor> widgets = getDashboardContext().getWidgetRegistry().getWidgetDescriptors();
		for (Iterator<WidgetDescriptor> i = widgets.iterator(); i.hasNext();) {
			WidgetDescriptor wd = i.next();
			if (!confShowMyRooms && MyRoomsWidget.class.getCanonicalName().equals(wd.getWidgetClassName())) {
				i.remove();
				continue;
			}
			if (!confShowRss && RssWidget.class.getCanonicalName().equals(wd.getWidgetClassName())) {
				i.remove();
				continue;
			}
		}
		add(new ListView<WidgetDescriptor>("widgets", widgets) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<WidgetDescriptor> item) {
				final WidgetDescriptor wd = item.getModelObject();
				item.add(new Label("name", wd.getName()));
				item.add(new Label("description", wd.getDescription()));
				item.add(new AjaxCheckBox("display", Model.of(isDisplayed(wd) != null)) {
					private static final long serialVersionUID = 1L;

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
						dashboardContext.getDashboardPersister().save(d);
					}
				});
			}
		});
	}
}
