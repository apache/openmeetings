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

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.openmeetings.app.dto.NaviDTO;
import org.openmeetings.web.app.WebSession;
import org.openmeetings.web.pages.admin.UsersPage;

public class MenuPage extends UserPage {
	private static final long serialVersionUID = 6421960759218157999L;
	
	public MenuPage() {
		add(new ListView<NaviDTO>("mainItem", WebSession.getNavMenu()) {
			private static final long serialVersionUID = 2173926553418745231L;

			@Override
			protected void populateItem(ListItem<NaviDTO> item) {
				NaviDTO gl = item.getModelObject();
				item.add(new Label("label", gl.getLabel()).setRenderBodyOnly(true));
				
				item.add(new ListView<NaviDTO>("childItem", gl.getItems()) {
					private static final long serialVersionUID = 3609635268338379087L;

					@Override
					protected void populateItem(ListItem<NaviDTO> item) {
						NaviDTO m = item.getModelObject();
						Link<Void> link = new BookmarkablePageLink<Void>("link", MenuPage.class);
						switch(m.getAction()) {
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
								link = new BookmarkablePageLink<Void>("link", UsersPage.class);
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
						item.add(link);
						link.add(new Label("name", m.getLabel()).setRenderBodyOnly(true));
						link.add(new Label("description", m.getTooltip()).setRenderBodyOnly(true));
					}
				}.setReuseItems(true));
			}
		}.setReuseItems(true));
	}
}
