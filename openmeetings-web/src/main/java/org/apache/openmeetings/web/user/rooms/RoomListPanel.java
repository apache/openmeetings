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

import java.util.List;

import org.apache.openmeetings.db.dao.server.ISessionManager;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.common.UserPanel;
import org.apache.openmeetings.web.pages.MainPage;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;

import com.googlecode.wicket.jquery.ui.form.button.Button;

public class RoomListPanel extends UserPanel {
	private static final long serialVersionUID = 1L;
	private final ListView<Room> list;

	public RoomListPanel(String id, List<Room> rooms, final String label) {
		super(id);
		setOutputMarkupId(true);
		add(list = new ListView<Room>("list", rooms) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<Room> item) {
				final Room r = item.getModelObject();
				WebMarkupContainer roomContainer;
				item.add((roomContainer = new WebMarkupContainer("roomContainer")).add(new AjaxEventBehavior("click"){
					private static final long serialVersionUID = 1L;
					
					@Override
					protected void onEvent(AjaxRequestTarget target) {
						onContainerClick(target, r);
					}
				}));
				roomContainer.add(new Label("roomName", r.getName()));
				final Label curUsers = new Label("curUsers", new Model<Integer>(Application.getBean(ISessionManager.class).getClientListByRoom(r.getId()).size()));
				roomContainer.add(curUsers.setOutputMarkupId(true));
				roomContainer.add(new Label("totalUsers", r.getNumberOfPartizipants()));
				item.add(new Button("btn").add(new Label("label", label)).add(new RoomEnterBehavior(r.getId()) {
					private static final long serialVersionUID = 1L;

					@Override
					protected void onEvent(AjaxRequestTarget target) {
						onRoomEnter(target, roomId);
					}
				}));
				roomContainer.add(new AjaxLink<Void>("refresh") {
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick(AjaxRequestTarget target) {
						target.add(curUsers.setDefaultModelObject(Application.getBean(ISessionManager.class).getClientListByRoom(r.getId()).size()));
						onRefreshClick(target, r);
					}
				});
			}
		});
	}
	
	public void update(IPartialPageRequestHandler handler, List<Room> rooms) {
		list.setList(rooms);
		handler.add(this);
	}

	/**
	 * this method need to be overriden to perform custom actions on room container click
	 */
	public void onContainerClick(AjaxRequestTarget target, Room r) {
	}

	/**
	 * this method need to be overriden to perform custom actions on room refresh click
	 */
	public void onRefreshClick(AjaxRequestTarget target, Room r) {
	}

	/**
	 * this method need to be overriden to perform custom actions on room enter click
	 */
	public void onRoomEnter(AjaxRequestTarget target, Long roomId) {
		RoomEnterBehavior.roomEnter((MainPage)getPage(), target, roomId);
	}
}
