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
package org.apache.openmeetings.web.components.user.profile;

import org.apache.openmeetings.web.components.UserPanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.Model;

import com.googlecode.wicket.jquery.core.JQueryBehavior;

public class SettingsPanel extends UserPanel {
	private static final long serialVersionUID = -830146821166434373L;
	public static final String DASHBOARD_TAB_ID = "tab1";

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
	
	public SettingsPanel(String id) {
		super(id);
		RepeatingView tabs = new RepeatingView("tabs");
		addTab(tabs, "[Widgets]", DASHBOARD_TAB_ID); //FIXME localize
		
		RepeatingView panels = new RepeatingView("panels");
		addPanel(panels, DASHBOARD_TAB_ID, new WidgetsPanel("tab"));
		
		add(new JQueryBehavior("#tabs", "tabs"));
		add(tabs, panels);
	}
}
