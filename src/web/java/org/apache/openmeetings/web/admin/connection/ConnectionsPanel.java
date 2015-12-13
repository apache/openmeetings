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
package org.apache.openmeetings.web.admin.connection;

import static java.lang.Boolean.TRUE;
import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.WebSession.getSid;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.openmeetings.db.dao.server.ISessionManager;
import org.apache.openmeetings.db.dao.user.IUserService;
import org.apache.openmeetings.db.entity.room.Client;
import org.apache.openmeetings.web.admin.AdminPanel;
import org.apache.openmeetings.web.admin.SearchableDataView;
import org.apache.openmeetings.web.common.ConfirmableAjaxLink;
import org.apache.openmeetings.web.common.PagedEntityListPanel;
import org.apache.openmeetings.web.data.SearchableDataProvider;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RepeatingView;

public class ConnectionsPanel extends AdminPanel {
	private static final long serialVersionUID = -5074847509312630580L;

	public ConnectionsPanel(String id) {
		super(id);
	
		SearchableDataProvider<Client> sdp = new SearchableDataProvider<Client>(null) {
			private static final long serialVersionUID = 1L;

			//FIXME add search
			
			@Override
			public Iterator<? extends Client> iterator(long first, long count) {
				//FIXME add grouping by public SID
				List<Client> l = new ArrayList<Client>(getBean(ISessionManager.class).getClients());
				return l.subList((int)Math.max(0, first), (int)Math.min(first + count, l.size())).iterator();
			}
			
			@Override
			public long size() {
				return getBean(ISessionManager.class).getClients().size();
			}
		};
		final WebMarkupContainer container = new WebMarkupContainer("container");
		final WebMarkupContainer details = new WebMarkupContainer("details");
		SearchableDataView<Client> dataView = new SearchableDataView<Client>("clientList", sdp) {
			private static final long serialVersionUID = 2863400959190625386L;

			@Override
			protected void populateItem(final Item<Client> item) {
				Client c = item.getModelObject();
				item.add(new Label("id", c.getStreamid()));
				item.add(new Label("login", c.getUsername()));
				item.add(new Label("since", c.getConnectedSince()));
				item.add(new Label("scope", c.getScope()));
				item.add(new Label("server", c.getServer() == null ? "no cluster" : c.getServer().getAddress())); //FIXME localization
				item.add(new ConfirmableAjaxLink("kick", 605) {
					private static final long serialVersionUID = 1860369399943234763L;

					@Override
					public void onClick(AjaxRequestTarget target) {
						Client c = item.getModelObject();
						getBean(IUserService.class).kickUserByStreamId(getSid(), c.getStreamid()
								, c.getServer() == null ? 0 : c.getServer().getId());
						target.add(container, details.setVisible(false));
					}
				}.setEnabled(!TRUE.equals(c.getIsScreenClient()) && !TRUE.equals(c.getIsAVClient())));
				item.add(new AjaxEventBehavior("onclick") {
					private static final long serialVersionUID = 9180559138402389613L;

					@Override
					protected void onEvent(AjaxRequestTarget target) {
						Field[] ff = Client.class.getDeclaredFields();
						RepeatingView lines = new RepeatingView("line");
						Client c = item.getModelObject();
						for (Field f : ff) {
							int mod = f.getModifiers();
							if (Modifier.isStatic(mod) || Modifier.isTransient(mod)) {
								continue;
							}
							WebMarkupContainer line = new WebMarkupContainer(lines.newChildId());
							line.add(new Label("name", f.getName()));
							String val = "";
							try {
								f.setAccessible(true);
								val = "" + f.get(c);
							} catch (Exception e) {
								//noop
							}
							line.add(new Label("value", val));
							lines.add(line);
						}
						details.addOrReplace(lines);
						target.add(details.setVisible(true));
					}
				});
				item.add(AttributeModifier.append("class", "clickable " + ((item.getIndex() % 2 == 1) ? "even" : "odd")));
			}
		};
		add(container.add(dataView).setOutputMarkupId(true), details.setVisible(false).setOutputMarkupPlaceholderTag(true));
		
		add(new PagedEntityListPanel("navigator", dataView) {
			private static final long serialVersionUID = 5097048616003411362L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				target.add(container);
			}
		});
	}

	@Override
	public void onMenuPanelLoad(AjaxRequestTarget target) {
	}
}
