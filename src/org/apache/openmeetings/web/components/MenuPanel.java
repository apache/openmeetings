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

import org.apache.openmeetings.data.basic.Navimanagement;
import org.apache.openmeetings.persistence.beans.basic.Naviglobal;
import org.apache.openmeetings.persistence.beans.basic.Navimain;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.components.admin.labels.LangPanel;
import org.apache.openmeetings.web.components.admin.configurations.ConfigsPanel;
import org.apache.openmeetings.web.components.admin.groups.GroupsPanel;
import org.apache.openmeetings.web.components.admin.ldaps.LdapsPanel;
import org.apache.openmeetings.web.components.admin.rooms.RoomsPanel;
import org.apache.openmeetings.web.components.admin.servers.ServersPanel;
import org.apache.openmeetings.web.components.admin.user.UsersPanel;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.core.util.string.JavaScriptUtils;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

public class MenuPanel extends BasePanel {
	private static final long serialVersionUID = 6626039612808753514L;

	private enum MenuActions {
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

	public MenuPanel(String id, final MarkupContainer contents) {
		super(id);
		setMarkupId(id);
		
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
								String hash = "#";
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
										hash = "#admin/users";
										break;
									case adminModuleConnections:
										break;
									case adminModuleOrg:
										target.add(contents.replace(new GroupsPanel("child")));
										hash = "#admin/groups";
										break;
									case adminModuleRoom:
										target.add(contents.replace(new RoomsPanel("child")));
										hash = "#admin/rooms";
										break;
									case adminModuleConfiguration:
										target.add(contents.replace(new ConfigsPanel("child")));
										hash = "#admin/configs";
										break;
									case adminModuleLanguages:
										target.add(contents.replace(new LangPanel("child")));
										hash = "#admin/lang";
										break;
									case adminModuleLDAP:
										target.add(contents.replace(new LdapsPanel("child")));
										hash = "#admin/ldaps";
										break;
									case adminModuleBackup:
										break;
									case adminModuleServers:
										target.add(contents.replace(new ServersPanel("child")));
										hash = "#admin/servers";
										break;
								}
								target.appendJavaScript("window.location.hash = '" + JavaScriptUtils.escapeQuotes(hash) + "';");
							};
						});
					}
				}.setReuseItems(true));
			}
		}.setReuseItems(true));
	}

}
