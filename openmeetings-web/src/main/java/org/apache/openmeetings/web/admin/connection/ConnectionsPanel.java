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
import static org.apache.openmeetings.web.app.WebSession.getDateFormat;
import static org.apache.openmeetings.web.common.confirmation.ConfirmableAjaxBorder.newOkCancelConfirm;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.openmeetings.core.remote.KurentoHandler;
import org.apache.openmeetings.core.remote.StreamProcessor;
import org.apache.openmeetings.db.dao.user.IUserManager;
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
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;

public class ConnectionsPanel extends AdminBasePanel {

	private static final Logger log = LoggerFactory.getLogger(ConnectionsPanel.class);

	private static final long serialVersionUID = 1L;
	@SpringBean
	private ClientManager cm;
	@SpringBean
	private KurentoHandler kHandler;
	@SpringBean
	private StreamProcessor streamProcessor;
	@SpringBean
	private IUserManager userManager;

	public ConnectionsPanel(String id) {
		super(id);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();

		SearchableDataProvider<IDataProviderEntity> sdp = new SearchableDataProvider<>(null) {
			private static final long serialVersionUID = 1L;

			private List<IDataProviderEntity> getConnections() {
				List<IDataProviderEntity> l = new ArrayList<>();
				l.addAll(cm.list());
				Collection<KStreamDto> streams = streamProcessor.getStreams()
						.stream()
						.map(kStream -> new KStreamDto("processor", kStream))
						.collect(Collectors.toList());
				l.addAll(streams);
				log.info("Retrieve all Streams, StreamProcessor has {} of streams", streams.size());

				return l;
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
				if (item.getModelObject() instanceof KStreamDto) {
					KStreamDto kStream = (KStreamDto)item.getModelObject();
					item.add(new Label("type", kStream.getType() + " " + kStream.getStreamType()));
					item.add(new Label("login", kStream.getUid()));
					item.add(new Label("since", getDateFormat().format(kStream.getConnectedSince())));
					item.add(new Label("scope", kStream.getRoomId()));
					item.add(new Label("server", ""));
					item.add(new Label("kick", ""));
				}
				if (item.getModelObject() instanceof Client) {
					Client c = (Client)item.getModelObject();
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

				item.add(new AjaxEventBehavior(EVT_CLICK) {
					private static final long serialVersionUID = 1L;

					@Override
					protected void onEvent(AjaxRequestTarget target) {
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
