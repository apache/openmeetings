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

import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.AbstractItem;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.openmeetings.app.data.basic.Navimanagement;
import org.openmeetings.app.persistence.beans.basic.Naviglobal;
import org.openmeetings.app.persistence.beans.basic.Navimain;
import org.openmeetings.web.app.Application;
import org.openmeetings.web.app.WebSession;
import org.openmeetings.web.pages.admin.UsersPage;

public class MenuPage extends UserPage {
	private static final long serialVersionUID = 6421960759218157999L;
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
	
	public MenuPage() {
		//FIXME all this need to be refactored
		List<Naviglobal> menu = Application.getBean(Navimanagement.class)
			.getMainMenu(WebSession.getUserLevel(), WebSession.getUserId(), WebSession.getLanguage());

		RepeatingView repeater = new RepeatingView("mainItem");
		add(repeater);
		for (Naviglobal global : menu) {
			AbstractItem item = new AbstractItem(repeater.newChildId());
			repeater.add(item);
			item.add(new Label("label", global.getLabel().getValue()).setRenderBodyOnly(true));

			RepeatingView subRepeater = new RepeatingView("childItem");
			item.add(subRepeater);
			for (Navimain subMenu : global.getMainnavi()) {
				AbstractItem subItem = new AbstractItem(subRepeater.newChildId());
				subRepeater.add(subItem);
				

				Link<Void> link = new BookmarkablePageLink<Void>("link", MenuPage.class);
				switch(MenuActions.valueOf(subMenu.getAction())) {
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
				subItem.add(link);
				link.add(new Label("name", subMenu.getLabel().getValue()).setRenderBodyOnly(true));
				link.add(new Label("description", subMenu.getTooltip().getValue()).setRenderBodyOnly(true));
			}
		}
	}
}
