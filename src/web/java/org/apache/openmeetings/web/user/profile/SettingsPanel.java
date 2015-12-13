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

import static org.apache.openmeetings.web.app.WebSession.getUserId;

import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.common.UserPanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.PriorityHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.Model;

import com.googlecode.wicket.jquery.core.JQueryBehavior;
import com.googlecode.wicket.jquery.core.Options;

public class SettingsPanel extends UserPanel {
	private static final long serialVersionUID = -830146821166434373L;
	public static final int PROFILE_TAB_ID = 0;
	public static final int MESSAGES_TAB_ID = 1;
	public static final int EDIT_PROFILE_TAB_ID = 2;
	public static final int SEARCH_TAB_ID = 3;
	public static final int DASHBOARD_TAB_ID = 4;

	private String getTabId(int idx) {
		return "tab" + idx;
	}
	
	private void addTab(RepeatingView tabs, String name, String id) {
		WebMarkupContainer tab = new WebMarkupContainer(tabs.newChildId());
		tab.add(new WebMarkupContainer("link")
			.add(new Label("name", Model.of(name)))
			.add(new AttributeModifier("href", "#" + id)));
		tabs.add(tab);
	}
	
	private void addPanel(RepeatingView panels, String id, Panel p) {
		WebMarkupContainer tab = new WebMarkupContainer(panels.newChildId());
		tab.add(p.setMarkupId(id)).setRenderBodyOnly(true);
		panels.add(tab);
	}
	
	public SettingsPanel(String id, int active) {
		super(id);
		RepeatingView tabs = new RepeatingView("tabs");
		addTab(tabs, WebSession.getString(1170), getTabId(PROFILE_TAB_ID));
		addTab(tabs, WebSession.getString(1188), getTabId(MESSAGES_TAB_ID));
		addTab(tabs, WebSession.getString(1171), getTabId(EDIT_PROFILE_TAB_ID));
		addTab(tabs, WebSession.getString(1172), getTabId(SEARCH_TAB_ID));
		addTab(tabs, WebSession.getString(1548), getTabId(DASHBOARD_TAB_ID));
		
		RepeatingView panels = new RepeatingView("panels");
		addPanel(panels, getTabId(PROFILE_TAB_ID), new UserProfilePanel("tab", getUserId()));
		addPanel(panels, getTabId(MESSAGES_TAB_ID), new MessagesContactsPanel("tab"));
		addPanel(panels, getTabId(EDIT_PROFILE_TAB_ID), new ProfilePanel("tab"));
		addPanel(panels, getTabId(SEARCH_TAB_ID), new UserSearchPanel("tab"));
		addPanel(panels, getTabId(DASHBOARD_TAB_ID), new WidgetsPanel("tab"));
		
		add(new JQueryBehavior("#tabs", "tabs", new Options("active", active)) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void renderScript(JavaScriptHeaderItem script, IHeaderResponse response) {
				response.render(new PriorityHeaderItem(script));
			}
		});
		add(tabs, panels);
	}
}
