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

import static org.apache.openmeetings.db.util.TimezoneUtil.getTimeZone;
import static org.apache.openmeetings.web.app.WebSession.getUserId;
import static org.apache.openmeetings.util.OpenmeetingsVariables.ATTR_CLASS;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.openmeetings.db.dao.user.UserContactDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.web.app.ClientManager;
import org.apache.openmeetings.web.common.PagingNavigatorPanel;
import org.apache.openmeetings.web.common.UserBasePanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;


import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import jakarta.inject.Inject;

public class UserSearchPanel extends UserBasePanel {
	private static final long serialVersionUID = 1L;
	private static final String ATTR_USER_ID = "data-user-id";
	private static final List<Integer> itemsPerPage = List.of(10, 25, 50, 75, 100, 200, 500, 1000, 2500, 5000);
	private static final SortParam<String> orderBy = new SortParam<>("firstname", true);
	private final TextField<String> text = new TextField<>("text", Model.of(""));
	private final TextField<String> search = new TextField<>("search", Model.of(""));
	private final TextField<String> offer = new TextField<>("offer", Model.of(""));
	private boolean searched = false;
	private final WebMarkupContainer container = new WebMarkupContainer("container");

	@Inject
	private UserDao userDao;
	@Inject
	private UserContactDao contactDao;
	@Inject
	private ClientManager cm;

	public UserSearchPanel(String id) {
		super(id);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		container.add(new Form<Void>("form") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onInitialize() {
				super.onInitialize();
				add(text, offer, search);
				add(new BootstrapAjaxButton("submit", new ResourceModel("714"), Buttons.Type.Outline_Primary) {
					private static final long serialVersionUID = 1L;

					@Override
					protected void onSubmit(AjaxRequestTarget target) {
						searched = true;
						refresh(target);
					}
				});
			}
		});
		IDataProvider<User> dp = new IDataProvider<>() {
			private static final long serialVersionUID = 1L;

			@Override
			public Iterator<? extends User> iterator(long first, long count) {
				return searched ? userDao.searchUserProfile(getUserId(), text.getModelObject(), offer.getModelObject(), search.getModelObject(), orderBy, first, count).iterator()
						: Collections.emptyIterator();
			}

			@Override
			public long size() {
				return searched ? userDao.searchCountUserProfile(getUserId(), text.getModelObject(), offer.getModelObject(), search.getModelObject()) : 0;
			}

			@Override
			public IModel<User> model(User object) {
				return new CompoundPropertyModel<>(object);
			}

		};
		final DataView<User> dw = new DataView<>("users", dp) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(Item<User> item) {
				User u = item.getModelObject();
				final long userId = u.getId();
				item.add(new WebMarkupContainer("status").add(AttributeModifier.append(ATTR_CLASS, cm.isOnline(userId) ? "online" : "offline")));
				item.add(new Label("name", u.getDisplayName()));
				item.add(new Label("tz", getTimeZone(u).getID()));
				item.add(new Label("offer", u.getUserOffers()));
				item.add(new Label("search", u.getUserSearchs()));
				item.add(new WebMarkupContainer("view").add(AttributeModifier.append(ATTR_USER_ID, userId)));
				item.add(new WebMarkupContainer("add").setVisible(userId != getUserId() && !contactDao.isContact(userId, getUserId()))
						.add(AttributeModifier.append(ATTR_USER_ID, userId)));
				item.add(new WebMarkupContainer("message").setVisible(userId != getUserId()).add(AttributeModifier.append(ATTR_USER_ID, userId)));
				item.add(new WebMarkupContainer("invite").setVisible(userId != getUserId()).add(AttributeModifier.append(ATTR_USER_ID, userId)));
			}
		};

		add(container.add(dw, new PagingNavigatorPanel("navigator", dw, itemsPerPage, 100) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				refresh(target);
			}
		}).setOutputMarkupId(true));
	}

	private void refresh(IPartialPageRequestHandler handler) {
		handler.add(container);
		handler.appendJavaScript("$('#searchUsersTable .contact-add.om-icon.clickable').off().click(function() {addContact($(this).data('user-id'));});");
		handler.appendJavaScript("$('#searchUsersTable .new-msg.om-icon.clickable').off().click(function() {privateMessage($(this).data('user-id'));});");
		handler.appendJavaScript("$('#searchUsersTable .profile.om-icon.clickable').off().click(function() {showUserInfo($(this).data('user-id'));});");
		handler.appendJavaScript("$('#searchUsersTable .invite.om-icon.clickable').off().click(function() {inviteUser($(this).data('user-id'));});");
	}

	@Override
	public void onNewMessageClose(IPartialPageRequestHandler handler) {
		refresh(handler);
	}
}
