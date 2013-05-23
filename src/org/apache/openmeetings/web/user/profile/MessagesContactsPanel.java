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

import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.WebSession.getUserId;

import org.apache.openmeetings.data.user.dao.PrivateMessageFolderDao;
import org.apache.openmeetings.data.user.dao.PrivateMessagesDao;
import org.apache.openmeetings.persistence.beans.user.PrivateMessageFolder;
import org.apache.openmeetings.web.common.UserPanel;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

public class MessagesContactsPanel extends UserPanel {
	private static final long serialVersionUID = 8098087441571734957L;
	private final WebMarkupContainer container = new WebMarkupContainer("container");
	private final IModel<Long> unreadModel = Model.of(0L);

	public MessagesContactsPanel(String id) {
		super(id);
		
		add(new WebMarkupContainer("new").add(new AjaxEventBehavior("click") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
			}
		}));
		add(new WebMarkupContainer("inbox").add(new AjaxEventBehavior("click") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
			}
		}));
		add(new WebMarkupContainer("sent").add(new AjaxEventBehavior("click") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
			}
		}));
		add(new WebMarkupContainer("trash").add(new AjaxEventBehavior("click") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
			}
		}));
		add(new WebMarkupContainer("newdir").add(new AjaxEventBehavior("click") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
			}
		}));
		add(new ListView<PrivateMessageFolder>("folder", getBean(PrivateMessageFolderDao.class).get(0, Integer.MAX_VALUE)) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<PrivateMessageFolder> item) {
				item.add(new Label("name", item.getModelObject().getFolderName()));
				item.add(new WebMarkupContainer("delete"));
			}
		});
		unreadModel.setObject(getBean(PrivateMessagesDao.class).count(getUserId(), null, false, false));
		container.add(new Label("unread", unreadModel));
		
		add(container.setOutputMarkupId(true));
	}
	
}
