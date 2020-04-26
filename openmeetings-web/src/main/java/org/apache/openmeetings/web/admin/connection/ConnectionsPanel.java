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

import org.apache.openmeetings.core.remote.KRoom;
import org.apache.openmeetings.core.remote.KurentoHandler;
import org.apache.openmeetings.core.remote.StreamProcessor;
import org.apache.openmeetings.db.dao.user.IUserManager;
import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.web.admin.AdminBasePanel;
import org.apache.openmeetings.web.admin.SearchableDataView;
import org.apache.openmeetings.web.admin.connection.dto.ConnectionListItem;
import org.apache.openmeetings.web.admin.connection.dto.ConnectionListKStreamItem;
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
	private KurentoHandler scm;
	@SpringBean
	private StreamProcessor streamProcessor;
	@SpringBean
	private IUserManager userManager;
	
	/**
	 * This needs to combine two lists as we currently hold a reference to the KStream in two places:
	 * <ul>
	 * <li>{@link StreamProcessor#getStreams()}}</li>
	 * <li>{@link KRoom#getParticipants()}</li>
	 * </ul>
	 * Both are singletons and hold a reference to a stream list and can get out of sync or leak.
	 * 
	 * TODO: Investigate if we can have 1 source of truth.
	 *
	 * @return list of KStreams registered
	 */
	public Collection<ConnectionListKStreamItem> getAllStreams() {
		Collection<ConnectionListKStreamItem> allStreams = new ArrayList<>();
		
		allStreams.addAll(
				streamProcessor.getStreams().stream()
					.map(stream -> new ConnectionListKStreamItem(
								streamProcessor.getClass().getSimpleName(),
								stream.getSid(),
								stream.getUid(),
								(stream.getRoom() == null) ? null : stream.getRoom().getRoomId(),
								stream.getConnectedSince(),
								stream.getStreamType(),
								stream.getProfile().toString(),
								(stream.getRecorder() == null) ? null : stream.getRecorder().toString(),
								stream.getChunkId(),
								stream.getType()
							))
					.collect(Collectors.toList())
				);
		
		log.info("Retrieve all Streams, StreamProcessor has {} of streams", allStreams.size());
		
		// Add any streams from the KRoom that are not in the StreamProcessor 
		scm.getRooms().forEach(
			room -> {
				log.info("Retrieve room {}, participants {}", room, room.getParticipants().size());
				room.getParticipants().forEach(
					participant -> {
						if (!streamProcessor.getStreams().contains(participant)) {
							log.warn("Stream was in KRoom but not in StreamProcessor, stream {}", participant);
							allStreams.add(new ConnectionListKStreamItem(
									scm.getClass().getSimpleName(),
									participant.getSid(),
									participant.getUid(),
									(participant.getRoom() == null) ? null : participant.getRoom().getRoomId(),
									participant.getConnectedSince(),
									participant.getStreamType(),
									participant.getProfile().toString(),
									(participant.getRecorder() == null) ? null : participant.getRecorder().toString(),
									participant.getChunkId(),
									participant.getType()
								));
						}
					}
				);
			}
		);
		
		return allStreams;
	}
	
	/**
	 * Combine lists for Client and KStream
	 * 
	 * @return
	 */
	protected List<ConnectionListItem> getConnections() {
		
		List<ConnectionListItem> connections = new ArrayList<>();
		List<Client> clients = cm.list();
		Collection<ConnectionListKStreamItem> streams = getAllStreams();
		
		connections.addAll(
				clients.stream()
					.map(client -> new ConnectionListItem(client, null))
					.collect(Collectors.toList())
				);
		connections.addAll(
				streams.stream()
					.map(stream -> new ConnectionListItem(null, stream))
					.collect(Collectors.toList())
				);
		return connections;
	}

	public ConnectionsPanel(String id) {
		super(id);

		SearchableDataProvider<ConnectionListItem> sdp = new SearchableDataProvider<>(null) {
			private static final long serialVersionUID = 1L;

			@Override
			public Iterator<? extends ConnectionListItem> iterator(long first, long count) {
				List<ConnectionListItem> l = getConnections();
				return l.subList((int)Math.max(0, first), (int)Math.min(first + count, l.size())).iterator();
			}

			@Override
			public long size() {
				return getConnections().size();
			}
		};
		final WebMarkupContainer container = new WebMarkupContainer("container");
		final WebMarkupContainer details = new WebMarkupContainer("details");
		SearchableDataView<ConnectionListItem> dataView = new SearchableDataView<>("clientList", sdp) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(final Item<ConnectionListItem> item) {
				ConnectionListItem connection = item.getModelObject();
				
				if (connection.getStream() != null) {
					ConnectionListKStreamItem kStream = connection.getStream();
					item.add(new Label("type", kStream.getType()));
					item.add(new Label("login", kStream.getUid()));
					item.add(new Label("since", getDateFormat().format(kStream.getConnectedSince())));
					item.add(new Label("scope", kStream.getStreamType()));
					item.add(new Label("server", kStream.getSource()));
					item.add(new Label("kick", ""));
				}
				if (connection.getClient() != null) {
					Client c = connection.getClient();
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
						
						RepeatingView lines = new RepeatingView("line");
						ConnectionListItem connection = item.getModelObject();
						
						Field[] ff;
						Object c;
						
						if (connection.getStream() != null) {
							ff = connection.getStream().getClass().getDeclaredFields();
							c = connection.getStream();
						} else if (connection.getClient() != null) {
							ff = connection.getClient().getClass().getDeclaredFields();
							c = connection.getClient();
						} else {
							log.warn("Should be either Client or ConnectionListItem, modelObject {}", item.getModelObject());
							return;
						}
						
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
