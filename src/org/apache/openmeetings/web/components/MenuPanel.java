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
package org.apache.openmeetings.web.components;

import org.apache.openmeetings.data.basic.NaviBuilder;
import org.apache.openmeetings.persistence.beans.basic.Naviglobal;
import org.apache.openmeetings.persistence.beans.basic.Navimain;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.pages.MainPage;
import org.apache.openmeetings.web.util.UrlFragment;
import org.apache.openmeetings.web.util.UrlFragment.MenuActions;
import org.apache.openmeetings.web.util.UrlFragment.MenuParams;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

/**
 * Loads the menu items into the main area
 * 
 * @author sebawagner
 *
 */
public class MenuPanel extends BasePanel {
	private static final long serialVersionUID = 6626039612808753514L;

	public MenuPanel(String id) {
		super(id);
		setMarkupId(id);
		
		final NaviBuilder man = Application.getBean(NaviBuilder.class);
		add(new ListView<Naviglobal>("mainItem", man.getMainMenu(WebSession.getUserLevel(), WebSession.getUserId(), WebSession.getLanguage())) {
			private static final long serialVersionUID = 2173926553418745231L;

			@Override
			protected void populateItem(ListItem<Naviglobal> item) {
				Naviglobal gl = item.getModelObject();
				item.add(new Label("label", gl.getLabel().getValue()).setRenderBodyOnly(true));
				
				item.add(new ListView<Navimain>("childItem", gl.getMainnavi()) {
					private static final long serialVersionUID = 3609635268338379087L;

					@Override
					protected void populateItem(ListItem<Navimain> item) {
						Navimain m = item.getModelObject();
						final String name = m.getLabel().getValue();
						final String desc = m.getTooltip().getValue();
						final MenuActions action = MenuActions.valueOf(m.getAction());
						final MenuParams params = m.getParams() != null ? MenuParams.valueOf(m.getParams()) : MenuParams.publicTabButton;
						item.add(new AjaxLink<Void>("link") {
							private static final long serialVersionUID = 5632618935550133709L;
							{
								add(new Label("name", name));
								add(new Label("description", desc));
							}
							
							public void onClick(AjaxRequestTarget target) {
								((MainPage)getPage()).updateContents(new UrlFragment(action, params), target);
							}
						});
					}
				}.setReuseItems(true));
			}
		}.setReuseItems(true));
	}
}
