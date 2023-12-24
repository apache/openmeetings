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
package org.apache.openmeetings.web.user.rooms;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.web.app.ClientManager;
import org.apache.openmeetings.web.common.UserPanel;
import org.apache.openmeetings.util.OmFileHelper;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.ByteArrayResource;

import org.apache.wicket.util.io.IOUtils;

import jakarta.inject.Inject;

public class RoomsPanel extends UserPanel {
	private static final long serialVersionUID = 1L;
	private final WebMarkupContainer clientsContainer = new WebMarkupContainer("clientsContainer");
	private final WebMarkupContainer details = new WebMarkupContainer("details");
	private final ListView<Client> clients;
	private final Label roomIdLbl = new Label("roomId", Model.of((Long)null));
	private final Label roomNameLbl = new Label("roomName", Model.of((String)null));
	private final Label roomCommentLbl = new Label("roomComment", Model.of((String)null));
	private List<Client> clientsInRoom = null;
	private final List<Room> rooms;
	private Long roomId = 0L;

	@Inject
	private UserDao userDao;
	@Inject
	private ClientManager cm;
	@Inject
	private RoomDao roomDao;

	public RoomsPanel(String id, List<Room> rooms) {
		super(id);
		this.rooms = rooms;
		clients = new ListView<>("clients", clientsInRoom){
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(final ListItem<Client> item) {
				Client client = item.getModelObject();
				final Long userId = client.getUserId();
				item.add(new Image("clientImage", new ByteArrayResource("image/png") {
					private static final long serialVersionUID = 1L;

					@Override
					protected ResourceResponse newResourceResponse(Attributes attributes) {
						ResourceResponse rr = super.newResourceResponse(attributes);
						rr.disableCaching();
						return rr;
					}

					@Override
					protected byte[] getData(Attributes attributes) {
						String uri = null;
						if (userId != null) {
							uri = userDao.get(userId > 0 ? userId : -userId).getPictureUri();
						}
						File img = OmFileHelper.getUserProfilePicture(userId, uri);
						try (InputStream is = new FileInputStream(img)) {
							return IOUtils.toByteArray(is);
						} catch (Exception e) {
							//no-op
						}
						return null;
					}
				}));
				item.add(new Label("clientLogin", client.getUser().getLogin()));
				item.add(new Label("from", client.getConnectedSince()));
			}
		};
	}

	@Override
	protected void onInitialize() {
		add(new RoomListPanel("list", rooms, getString("lbl.enter")) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onContainerClick(AjaxRequestTarget target, Room r) {
				roomId = r.getId();
				updateRoomDetails(target);
			}

			@Override
			public void onRefreshClick(AjaxRequestTarget target, Room r) {
				super.onRefreshClick(target, r);
				roomId = r.getId();
				updateRoomDetails(target);
			}
		});

		// Users in this Room
		add(details.setOutputMarkupId(true).setVisible(!rooms.isEmpty()));
		details.add(roomIdLbl, roomNameLbl, roomCommentLbl);
		details.add(clientsContainer.add(clients.setOutputMarkupId(true)).setOutputMarkupId(true));

		super.onInitialize();
	}

	void updateRoomDetails(AjaxRequestTarget target) {
		clients.setDefaultModelObject(cm.streamByRoom(roomId).toList());
		Room room = roomDao.get(roomId);
		roomIdLbl.setDefaultModelObject(room.getId());
		roomNameLbl.setDefaultModelObject(room.getName());
		roomCommentLbl.setDefaultModelObject(room.getComment());
		target.add(clientsContainer, details);
	}
}
