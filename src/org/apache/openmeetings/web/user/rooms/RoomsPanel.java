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

import org.apache.openmeetings.persistence.beans.room.Room;
import org.apache.openmeetings.session.SessionManager;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.common.UserPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

public class RoomsPanel extends UserPanel {
	private static final long serialVersionUID = -892281210307880052L;

	public RoomsPanel(String id, List<Room> rooms) {
		super(id);
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
