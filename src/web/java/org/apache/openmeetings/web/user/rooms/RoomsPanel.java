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
import java.util.List;

import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.dao.server.ISessionManager;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.room.Client;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.util.OmFileHelper;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.common.UserPanel;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.ByteArrayResource;
import org.apache.wicket.util.io.IOUtils;

public class RoomsPanel extends UserPanel {
	private static final long serialVersionUID = -892281210307880052L;
	final WebMarkupContainer clientsContainer;
	final ListView<Client> clients;
	Label roomComment;
	List<Client> clientsInRoom = null;
	long roomId = 0;

	public RoomsPanel(String id, List<Room> rooms) {
		super(id);
		add(new ListView<Room>("list", rooms) {
			private static final long serialVersionUID = 9189085478336224890L;

			@Override
			protected void populateItem(ListItem<Room> item) {
				final Room r = item.getModelObject();
				WebMarkupContainer roomContainer;
				item.add((roomContainer = new WebMarkupContainer("roomContainer")).add(new AjaxEventBehavior("onclick"){
					private static final long serialVersionUID = 1L;
					
					@Override
					protected void onEvent(AjaxRequestTarget target) {
						roomId = r.getRooms_id();
						updateRoomDetails(target);
					}
				}));
				roomContainer.add(new Label("roomName", r.getName()));
				final IModel<Integer> curUsersModel = new Model<Integer>(Application.getBean(ISessionManager.class).getClientListByRoom(r.getRooms_id()).size()); 
				final Label curUsers = new Label("curUsers", curUsersModel);
				roomContainer.add(curUsers.setOutputMarkupId(true));
				roomContainer.add(new Label("totalUsers", r.getNumberOfPartizipants()));
				item.add(new WebMarkupContainer("enter").add(new RoomEnterBehavior(r.getRooms_id())));
				roomContainer.add(new AjaxLink<Void>("refresh") {
					private static final long serialVersionUID = -3426813755917489787L;

					@Override
					public void onClick(AjaxRequestTarget target) {
						roomId = r.getRooms_id();
						curUsersModel.setObject(Application.getBean(ISessionManager.class).getClientListByRoom(r.getRooms_id()).size());
						target.add(curUsers);
						updateRoomDetails(target);
					}
				});
			}
		});
		
		// Users in this Room
		roomComment = new Label("roomComment", Model.of(""));
		add(roomComment.setOutputMarkupId(true));
		clientsContainer = new WebMarkupContainer("clientsContainer");
		clients = new ListView<Client>("clients", clientsInRoom){
			private static final long serialVersionUID = 8542589945574690054L;

			@Override
			protected void populateItem(final ListItem<Client> item) {
				Client client = item.getModelObject();
				final Long userId = client.getUser_id();
				item.add(new Image("clientImage", new ByteArrayResource("image/jpeg") {
					private static final long serialVersionUID = 6039580072791941591L;

					@Override
					protected ResourceResponse newResourceResponse(Attributes attributes) {
						ResourceResponse rr = super.newResourceResponse(attributes);
						rr.disableCaching();
						return rr;
					}
					
					@Override
					protected byte[] getData(Attributes attributes) {
						String uri = Application.getBean(UserDao.class).get(userId).getPictureuri();
						File img = OmFileHelper.getUserProfilePicture(userId, uri);
						try {
							return IOUtils.toByteArray(new FileInputStream(img));
						} catch (Exception e) {
						}
						return null;
					}
				}));
				item.add(new Label("clientLogin", "" + client.getUsername()));
			}
		};
		add(clientsContainer.add(clients.setOutputMarkupId(true)).setOutputMarkupId(true));
	}

	void updateRoomDetails(AjaxRequestTarget target) {
		final List<Client> clientsInRoom = Application.getBean(ISessionManager.class).getClientListByRoom(roomId);
		clients.setDefaultModelObject(clientsInRoom);
		Room room = Application.getBean(RoomDao.class).get(roomId); 
		roomComment.setDefaultModel(Model.of(room.getComment()));
		target.add(clientsContainer, roomComment);
	}
}
