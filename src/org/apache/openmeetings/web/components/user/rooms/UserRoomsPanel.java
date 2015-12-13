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
package org.apache.openmeetings.web.components.user.rooms;

import java.util.ArrayList;
import java.util.List;

import org.apache.openmeetings.data.conference.dao.RoomDao;
import org.apache.openmeetings.persistence.beans.room.Room;
import org.apache.openmeetings.session.SessionManager;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.components.MenuPanel.MenuParams;
import org.apache.openmeetings.web.components.UserPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

public class UserRoomsPanel extends UserPanel {
	private static final long serialVersionUID = -3246259803623925341L;

	public UserRoomsPanel(String id, MenuParams param) {
		super(id);

		long title, desc;
		List<Room> rooms;
		switch (param) {
			case myTabButton:
				title = 781L;
				desc = 782L;
				//rooms = Application.getBean(RoomDao.class).getPublicRooms();
				//FIXME 2 !!!! fake rooms appointmentLogic.getTodaysAppointmentsForUser(users_id);
				rooms = new ArrayList<Room>();
				break;
			case privateTabButton:
				title = 779L;
				desc = 780L;
				//FIXME getRoomsOrganisationByOrganisationId
				rooms = new ArrayList<Room>();
				break;
			case publicTabButton:
			default:
				title = 777L;
				desc = 778L;
				rooms = Application.getBean(RoomDao.class).getPublicRooms();
				break;
		}
		add(new Label("title", WebSession.getString(title)));
		add(new Label("desc", WebSession.getString(desc)));
		add(new ListView<Room>("list", rooms) {
			private static final long serialVersionUID = 9189085478336224890L;

			@Override
			protected void populateItem(ListItem<Room> item) {
				final Room r = item.getModelObject();
				item.add(new Label("roomName", r.getName()));
				final IModel<Integer> curUsersModel = new Model<Integer>(Application.getBean(SessionManager.class).getClientListByRoom(r.getRooms_id()).size()); 
				final Label curUsers = new Label("curUsers", curUsersModel);
				item.add(curUsers.setOutputMarkupId(true));
				item.add(new Label("totalUsers", r.getNumberOfPartizipants()));
				item.add(new AjaxLink<Void>("refresh") {
					private static final long serialVersionUID = -3426813755917489787L;

					@Override
					public void onClick(AjaxRequestTarget target) {
						curUsersModel.setObject(Application.getBean(SessionManager.class).getClientListByRoom(r.getRooms_id()).size());
						target.add(curUsers);
					}
				});
			}
		});
	}
}
