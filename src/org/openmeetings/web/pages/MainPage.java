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
package org.openmeetings.web.pages;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.openmeetings.app.data.basic.Navimanagement;
import org.openmeetings.app.persistence.beans.basic.Naviglobal;
import org.openmeetings.app.persistence.beans.basic.Navimain;
import org.openmeetings.web.app.Application;
import org.openmeetings.web.app.WebSession;
import org.openmeetings.web.components.admin.UsersPanel;

@AuthorizeInstantiation("USER")
public class MainPage extends BasePage {
	private static final long serialVersionUID = 6421960759218157999L;
	public enum MenuActions {
		dashboardModuleStartScreen
		, dashboardModuleCalendar
		, recordModule
		, conferenceModuleRoomList
		, eventModuleRoomList
		, moderatorModuleUser
		, moderatorModuleRoom
		, adminModuleUser
		, adminModuleConnections
		, adminModuleOrg
		, adminModuleRoom
		, adminModuleConfiguration
		, adminModuleLanguages
		, adminModuleLDAP
		, adminModuleBackup
		, adminModuleServers
	}
	
	public MainPage() {
		final MarkupContainer contents = new WebMarkupContainer("contents");
		contents.add(new WebMarkupContainer("child")).setOutputMarkupId(true);
		add(contents);
		
		final Navimanagement man = Application.getBean(Navimanagement.class);
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
						item.add(new AjaxLink<Void>("link") {
							private static final long serialVersionUID = 5632618935550133709L;
							{
								add(new Label("name", name).setRenderBodyOnly(true));
								add(new Label("description", desc).setRenderBodyOnly(true));
							}
							public void onClick(AjaxRequestTarget target) {
								switch(action) {
									case dashboardModuleStartScreen:
										break;
									case dashboardModuleCalendar:
										break;
									case recordModule:
										break;
									case conferenceModuleRoomList:
										//requires params
										break;
									case eventModuleRoomList:
										break;
									case moderatorModuleUser:
										break;
									case moderatorModuleRoom:
										break;
									case adminModuleUser:
										target.add(contents.replace(new UsersPanel("child")));
										//link = new BookmarkablePageLink<Void>("link", UsersPanel.class);
										break;
									case adminModuleConnections:
										break;
									case adminModuleOrg:
										break;
									case adminModuleRoom:
										break;
									case adminModuleConfiguration:
										break;
									case adminModuleLanguages:
										break;
									case adminModuleLDAP:
										break;
									case adminModuleBackup:
										break;
									case adminModuleServers:
										break;
								}
							};
						});
					}
				}.setReuseItems(true));
			}
		}.setReuseItems(true));
	}
}
