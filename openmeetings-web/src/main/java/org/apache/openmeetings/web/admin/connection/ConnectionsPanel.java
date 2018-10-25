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

import static org.apache.openmeetings.util.OpenmeetingsVariables.ATTR_CLASS;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.openmeetings.core.remote.KurentoHandler;
import org.apache.openmeetings.db.dao.user.IUserManager;
import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.web.admin.AdminBasePanel;
import org.apache.openmeetings.web.admin.SearchableDataView;
import org.apache.openmeetings.web.app.ClientManager;
import org.apache.openmeetings.web.common.ConfirmableAjaxBorder;
import org.apache.openmeetings.web.common.PagedEntityListPanel;
import org.apache.openmeetings.web.data.SearchableDataProvider;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.googlecode.wicket.jquery.ui.form.button.ButtonBehavior;

public class ConnectionsPanel extends AdminBasePanel {
	private static final long serialVersionUID = 1L;
	@SpringBean
	private ClientManager cm;
	@SpringBean
	private KurentoHandler scm;
	@SpringBean
	private IUserManager userManager;

	public ConnectionsPanel(String id) {
		super(id);

		SearchableDataProvider<Client> sdp = new SearchableDataProvider<Client>(null) {
			private static final long serialVersionUID = 1L;

			private List<Client> list() {
				List<Client> l = new ArrayList<>();
				l.addAll(cm.list());
				return l;
			}

			@Override
			public Iterator<? extends Client> iterator(long first, long count) {
				List<Client> l = list();
				return l.subList((int)Math.max(0, first), (int)Math.min(first + count, l.size())).iterator();
			}

			@Override
			public long size() {
				return list().size();
			}
		};
		final WebMarkupContainer container = new WebMarkupContainer("container");
		final WebMarkupContainer details = new WebMarkupContainer("details");
		SearchableDataView<Client> dataView = new SearchableDataView<Client>("clientList", sdp) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(final Item<Client> item) {
				Client c = item.getModelObject();
				final ConfirmableAjaxBorder confirm = new ConfirmableAjaxBorder("kick", getString("603"), getString("605")) {
					private static final long serialVersionUID = 1L;

					@Override
					protected void onSubmit(AjaxRequestTarget target) {
						cm.invalidate(c.getUserId(), c.getSessionId());
						target.add(container, details.setVisible(false));
					}
				};
				confirm.setOutputMarkupId(true).add(new ButtonBehavior(String.format("#%s", confirm.getMarkupId())));
				item.add(new Label("type", "html5"));
				item.add(new Label("login", c.getUser().getLogin()));
				item.add(new Label("since", c.getConnectedSince()));
				item.add(new Label("scope", c.getRoom() == null ? "html5" : "" + c.getRoom().getId()));
				item.add(new Label("server", c.getServerId()));
				item.add(confirm);
				item.add(new AjaxEventBehavior(EVT_CLICK) {
					private static final long serialVersionUID = 1L;

					@Override
					protected void onEvent(AjaxRequestTarget target) {
						Field[] ff = item.getModelObject().getClass().getDeclaredFields();
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
				item.add(AttributeModifier.append(ATTR_CLASS, ROW_CLASS));
			}
		};
		add(container.add(dataView).setOutputMarkupId(true), details.setVisible(false).setOutputMarkupPlaceholderTag(true));
		add(new PagedEntityListPanel("navigator", dataView) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				target.add(container);
			}
		});
	}
}
