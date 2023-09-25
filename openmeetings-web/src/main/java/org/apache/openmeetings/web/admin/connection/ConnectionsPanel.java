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

import static org.apache.openmeetings.web.app.WebSession.getDateFormat;
import static org.apache.openmeetings.web.common.confirmation.ConfirmationHelper.newOkCancelConfirm;
import static org.apache.openmeetings.util.OpenmeetingsVariables.ATTR_CLASS;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import org.apache.openmeetings.db.entity.IDataProviderEntity;
import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.web.admin.AdminBasePanel;
import org.apache.openmeetings.web.admin.SearchableDataView;
import org.apache.openmeetings.web.app.ClientManager;
import org.apache.openmeetings.web.common.PagedEntityListPanel;
import org.apache.openmeetings.web.data.SearchableDataProvider;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.ResourceModel;
import org.apache.openmeetings.mediaserver.StreamProcessor;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import jakarta.inject.Inject;

@AuthorizeInstantiation({"ADMIN", "ADMIN_CONNECTIONS"})
public class ConnectionsPanel extends AdminBasePanel {
	private static final long serialVersionUID = 1L;

	@Inject
	private ClientManager cm;
	@Inject
	private StreamProcessor streamProcessor;

	public ConnectionsPanel(String id) {
		super(id);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();

		SearchableDataProvider<IDataProviderEntity> sdp = new SearchableDataProvider<>(null) {
			private static final long serialVersionUID = 1L;

			private List<IDataProviderEntity> getConnections() {
				return Stream.concat(cm.stream()
						, streamProcessor.getStreams()
						.stream()
						.map(KStreamDto::new)
					).toList();
			}

			@Override
			public Iterator<? extends IDataProviderEntity> iterator(long first, long count) {
				List<IDataProviderEntity> l = getConnections();
				return l.subList((int)Math.max(0, first), (int)Math.min(first + count, l.size())).iterator();
			}

			@Override
			public long size() {
				return getConnections().size();
			}
		};
		final WebMarkupContainer container = new WebMarkupContainer("container");
		final WebMarkupContainer details = new WebMarkupContainer("details");
		SearchableDataView<IDataProviderEntity> dataView = new SearchableDataView<>("clientList", sdp) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(final Item<IDataProviderEntity> item) {
				if (item.getModelObject() instanceof KStreamDto kStream) {
					item.add(new Label("type", kStream.getType() + " " + kStream.getStreamType()));
					item.add(new Label("login", kStream.getUid()));
					item.add(new Label("since", getDateFormat().format(kStream.getConnectedSince())));
					item.add(new Label("scope", kStream.getRoomId()));
					item.add(new Label("server", ""));
					item.add(new Label("kick", ""));
				}
				if (item.getModelObject() instanceof Client c) {
					item.add(new Label("type", "html5"));
					item.add(new Label("login", c.getUser().getLogin()));
					item.add(new Label("since", getDateFormat().format(c.getConnectedSince())));
					item.add(new Label("scope", c.getRoom() == null ? "html5" : "" + c.getRoom().getId()));
					item.add(new Label("server", c.getServerId()));
					item.add(new BootstrapAjaxLink<String>("kick", null, Buttons.Type.Outline_Danger, new ResourceModel("603")) {
						private static final long serialVersionUID = 1L;
						{
							setSize(Buttons.Size.Small);
						}

						@Override
						public void onClick(AjaxRequestTarget target) {
							cm.invalidate(c.getUserId(), c.getSessionId());
							target.add(container, details.setVisible(false));
						}
					}.add(newOkCancelConfirm(this, getString("605"))));
				}

				item.add(AjaxEventBehavior.onEvent(EVT_CLICK, target -> {
					Field[] ff = (item.getModelObject() instanceof KStreamDto ? KStreamDto.class : Client.class).getDeclaredFields();
					RepeatingView lines = new RepeatingView("line");
					Object c = item.getModelObject();

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
				}));

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
