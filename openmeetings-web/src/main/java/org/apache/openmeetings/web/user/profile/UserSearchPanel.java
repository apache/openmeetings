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
import static org.apache.openmeetings.util.OpenmeetingsVariables.ATTR_CLASS;
import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.WebSession.getUserId;
import static org.apache.openmeetings.web.util.CallbackFunctionHelper.addOnClick;

import java.util.ArrayList;
import java.util.Arrays;
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
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.ui.form.button.AjaxButton;
import com.googlecode.wicket.jquery.ui.plugins.fixedheadertable.FixedHeaderTableBehavior;

public class UserSearchPanel extends UserBasePanel {
	private static final long serialVersionUID = 1L;
	private static final List<Integer> itemsPerPage = Arrays.asList(10, 25, 50, 75, 100, 200, 500, 1000, 2500, 5000);
	private String text;
	private String search;
	private String offer;
	private String orderBy = "u.firstname";
	private boolean asc = true;
	private boolean searched = false;
	private final WebMarkupContainer container = new WebMarkupContainer("container");
	private final FixedHeaderTableBehavior fixedHeader = new FixedHeaderTableBehavior("#searchUsersTable", new Options("height", 400));

	public UserSearchPanel(String id) {
		super(id);

		add(new Form<Void>("form") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onInitialize() {
				super.onInitialize();
				add(new TextField<>("text", new PropertyModel<String>(UserSearchPanel.this, "text")));
				add(new TextField<>("offer", new PropertyModel<String>(UserSearchPanel.this, "offer")));
				add(new TextField<>("search", new PropertyModel<String>(UserSearchPanel.this, "search")));
				add(new AjaxButton("submit") {
					private static final long serialVersionUID = 1L;

					@Override
					protected void onSubmit(AjaxRequestTarget target) {
						searched = true;
						refresh(target);
					}
				});
			}
		});
		IDataProvider<User> dp = new IDataProvider<User>() {
			private static final long serialVersionUID = 1L;

			@Override
			public Iterator<? extends User> iterator(long first, long count) {
				return searched ? getBean(UserDao.class).searchUserProfile(getUserId(), text, offer, search, orderBy, first, count, asc).iterator()
						: new ArrayList<User>().iterator();
			}

			@Override
			public long size() {
				return searched ? getBean(UserDao.class).searchCountUserProfile(getUserId(), text, offer, search) : 0;
			}

			@Override
			public IModel<User> model(User object) {
				return new CompoundPropertyModel<>(object);
			}

		};
		final DataView<User> dw = new DataView<User>("users", dp) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(Item<User> item) {
				final UserContactDao contactsDao = getBean(UserContactDao.class);
				User u = item.getModelObject();
				final long userId = u.getId();
				item.add(new WebMarkupContainer("status").add(AttributeModifier.append(ATTR_CLASS, getBean(ClientManager.class).isOnline(userId) ? "online" : "offline")));
				item.add(new Label("name", getName(u)));
				item.add(new Label("tz", getTimeZone(u).getID()));
				item.add(new Label("offer", u.getUserOffers()));
				item.add(new Label("search", u.getUserSearchs()));
				item.add(new WebMarkupContainer("view").add(addOnClick(String.format("showUserInfo(%s);", userId))));
				item.add(new WebMarkupContainer("add").setVisible(userId != getUserId() && !contactsDao.isContact(userId, getUserId()))
						.add(addOnClick(String.format("addContact(%s);", userId))));
				item.add(new WebMarkupContainer("message").setVisible(userId != getUserId()).add(addOnClick(String.format("privateMessage(%s);", userId))));
				item.add(new WebMarkupContainer("invite").setVisible(userId != getUserId()).add(addOnClick(String.format("inviteUser(%s);", userId))));
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
		handler.add(container.add(fixedHeader));
	}

	private static String getName(User u) {
		return "" + u.getFirstname() + " " + u.getLastname() + " [" + u.getLogin() + "]";
	}

	@Override
	public void onNewMessageClose(IPartialPageRequestHandler handler) {
		refresh(handler);
	}
}
