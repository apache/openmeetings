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

import java.util.ArrayList;
import java.util.List;

import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.web.common.UserBasePanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.ui.widget.tabs.TabbedPanel;

public class SettingsPanel extends UserBasePanel {
	private static final long serialVersionUID = 1L;
	public static final int PROFILE_TAB_ID = 0;
	public static final int MESSAGES_TAB_ID = 1;
	public static final int EDIT_PROFILE_TAB_ID = 2;
	public static final int SEARCH_TAB_ID = 3;
	public static final int INVITATIONS_TAB_ID = 4;
	public static final int DASHBOARD_TAB_ID = 5;
	public final int active;
	private UserProfilePanel profilePanel;
	private MessagesContactsPanel messagesPanel;
	@SpringBean
	private UserDao userDao;

	public SettingsPanel(String id, int active) {
		super(id);
		this.active = active;
	}

	@Override
	protected void onInitialize() {
		List<ITab> tabs = new ArrayList<>();
		tabs.add(new AbstractTab(new ResourceModel("1170")) {
			private static final long serialVersionUID = 1L;

			@Override
			public WebMarkupContainer getPanel(String panelId) {
				if (profilePanel == null) {
					profilePanel = new UserProfilePanel(panelId, getUserId());
				}
				return profilePanel;
			}
		});
		tabs.add(new AbstractTab(new ResourceModel("1188")) {
			private static final long serialVersionUID = 1L;

			@Override
			public WebMarkupContainer getPanel(String panelId) {
				if (messagesPanel == null) {
					messagesPanel = new MessagesContactsPanel(panelId);
				}
				return messagesPanel;
			}
		});
		tabs.add(new AbstractTab(new ResourceModel("1171")) {
			private static final long serialVersionUID = 1L;

			@Override
			public WebMarkupContainer getPanel(String panelId) {
				return new EditProfilePanel(panelId);
			}
		});
		tabs.add(new AbstractTab(new ResourceModel("1172")) {
			private static final long serialVersionUID = 1L;

			@Override
			public WebMarkupContainer getPanel(String panelId) {
				return new UserSearchPanel(panelId);
			}
		});
		tabs.add(new AbstractTab(new ResourceModel("profile.invitations")) {
			private static final long serialVersionUID = 1L;

			@Override
			public WebMarkupContainer getPanel(String panelId) {
				return new InvitationsPanel(panelId);
			}
		});
		tabs.add(new AbstractTab(new ResourceModel("1548")) {
			private static final long serialVersionUID = 1L;

			@Override
			public WebMarkupContainer getPanel(String panelId) {
				return new WidgetsPanel(panelId);
			}
		});
		add(new TabbedPanel("tabs", tabs, new Options("active", active)) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isActivatingEventEnabled() {
				return true;
			}

			@Override
			public void onActivating(AjaxRequestTarget target, int index, ITab tab) {
				if (index == 0) {
					profilePanel.update(target);
				} else if (index == 1) {
					messagesPanel.updateTable(target);
				}
			}
		}.setActiveTab(active));

		super.onInitialize();
	}
}
