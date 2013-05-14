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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.openmeetings.data.user.UserManager;
import org.apache.openmeetings.persistence.beans.user.User;
import org.apache.openmeetings.web.common.UserPanel;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigator;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

public class UserSearchPanel extends UserPanel {
	private static final long serialVersionUID = -3604291982438458598L;
	private final static List<Integer> itemsPerPage = Arrays.asList(10, 25, 50, 75, 100, 200, 500, 1000, 2500, 5000);
	private String text;
	private String search;
	private String offer;
	private String orderBy = "u.firstname";
	private boolean asc = true;
	private boolean searched = false;
	private int items = 1000;

	public UserSearchPanel(String id) {
		super(id);
		
		final WebMarkupContainer container = new WebMarkupContainer("container");
		add(new Form<Void>("form") {
			private static final long serialVersionUID = 3171252669473438834L;
			{
				add(new TextField<String>("text", new PropertyModel<String>(UserSearchPanel.this, "text")));
				add(new TextField<String>("offer", new PropertyModel<String>(UserSearchPanel.this, "offer")));
				add(new TextField<String>("search", new PropertyModel<String>(UserSearchPanel.this, "search")));
				add(new AjaxFormSubmitBehavior(this, "onsubmit") {
					private static final long serialVersionUID = -5767210068168188960L;

					@Override
					protected void onSubmit(AjaxRequestTarget target) {
						searched = true;
						target.add(container);
					}
				});
			}
		});
		IDataProvider<User> dp = new IDataProvider<User>() {
			private static final long serialVersionUID = -1757645522251197822L;

			public void detach() {
			}

			public Iterator<? extends User> iterator(long first, long count) {
				return searched ? getBean(UserManager.class).searchUserProfile(text, offer, search, orderBy, (int)first, (int)count, asc).iterator()
						: new ArrayList<User>().iterator();
			}

			public long size() {
				return searched ? getBean(UserManager.class).searchCountUserProfile(text, offer, search) : 0;
			}

			public IModel<User> model(User object) {
				return new CompoundPropertyModel<User>(object);
			}
			
		};
		final UserInfoDialog d = new UserInfoDialog("infoDialog");
		final DataView<User> dw = new DataView<User>("users", dp) {
			private static final long serialVersionUID = -3314136686941736574L;

			@Override
			protected void populateItem(Item<User> item) {
				User u = item.getModelObject();
				final long userId = u.getUser_id();
				item.add(new Label("name", getName(u)));
				item.add(new Label("tz", getTz(u)));
				item.add(new Label("offer", u.getUserOffers()));
				item.add(new Label("search", u.getUserSearchs()));
				//TODO add actions
				item.add(new WebMarkupContainer("view").add(new AjaxEventBehavior("onclick") {
					private static final long serialVersionUID = 7223188816617664993L;

					@Override
					protected void onEvent(AjaxRequestTarget target) {
						d.getContainer().replace(new UserProfilePanel("body", userId));
						target.add(d.getContainer());
						d.open(target);
					}
				}));
				//item.add(new TooltipBehavior(new Options("content", "TODO:: Picture will be displayed"))); //FIXME 
			}
		};
		DropDownChoice<Integer> itemsPP = new DropDownChoice<Integer>("items", new PropertyModel<Integer>(this, "items"), itemsPerPage);
		itemsPP.add(new OnChangeAjaxBehavior() {
			private static final long serialVersionUID = -3787928869045330486L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				dw.setItemsPerPage(items);
				target.add(container);
			}
		});
		add(d, container.add(dw, itemsPP
				, new AjaxPagingNavigator("navigator", dw)).setOutputMarkupId(true));
	}
	
	private String getName(User u) {
		return "" + u.getFirstname() + " " + u.getLastname() + " [" + u.getLogin() + "]"; //FIXME salutation
	}

	private String getTz(User u) {
		return u.getOmTimeZone().getJname() + "(" + u.getOmTimeZone().getIcal() + ")";
	}
}
