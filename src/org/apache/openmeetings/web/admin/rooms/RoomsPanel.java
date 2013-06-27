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
package org.apache.openmeetings.web.admin.rooms;

import org.apache.openmeetings.data.conference.dao.RoomDao;
import org.apache.openmeetings.persistence.beans.room.Room;
import org.apache.openmeetings.web.admin.AddUsersForm;
import org.apache.openmeetings.web.admin.AdminPanel;
import org.apache.openmeetings.web.admin.SearchableDataView;
import org.apache.openmeetings.web.common.PagedEntityListPanel;
import org.apache.openmeetings.web.data.DataViewContainer;
import org.apache.openmeetings.web.data.OrderByBorder;
import org.apache.openmeetings.web.data.SearchableDataProvider;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;

public class RoomsPanel extends AdminPanel {

	private static final long serialVersionUID = -1L;
	private AddUsersForm addModeratorsForm;
	private RoomForm form;
	
	@Override
	public void onMenuPanelLoad(AjaxRequestTarget target) {
		target.appendJavaScript("omRoomPanelInit();");
	}

	@SuppressWarnings("unchecked")
	public RoomsPanel(String id) {
		super(id);
		SearchableDataView<Room> dataView = new SearchableDataView<Room>("roomList", new SearchableDataProvider<Room>(RoomDao.class)) {
			private static final long serialVersionUID = 8715559628755439596L;

			@Override
			protected void populateItem(final Item<Room> item) {
				final Room room = item.getModelObject();
				item.add(new Label("rooms_id", "" + room.getRooms_id()));
				item.add(new Label("name", "" + room.getName()));
				item.add(new Label("ispublic", ""+room.getIspublic()));
				item.add(new AjaxEventBehavior("onclick") {
					private static final long serialVersionUID = -8069413566800571061L;

					protected void onEvent(AjaxRequestTarget target) {
						form.hideNewRecord();
						form.setModelObject(room);
						form.updateView(target);
						target.add(form);
						target.appendJavaScript("omRoomPanelInit();");
					}
				});
				item.add(AttributeModifier.replace("class", (item.getIndex() % 2 == 1) ? "even" : "odd"));
			}
		};
		
		final WebMarkupContainer listContainer = new WebMarkupContainer("listContainer");
		add(listContainer.add(dataView).setOutputMarkupId(true));
		DataViewContainer<Room> container = new DataViewContainer<Room>(listContainer, dataView);
		container.setLinks(new OrderByBorder<Room>("orderById", "rooms_id", container)
				, new OrderByBorder<Room>("orderByName", "name", container)
				, new OrderByBorder<Room>("orderByPublic", "ispublic", container));
		add(container.orderLinks);
		add(new PagedEntityListPanel("navigator", dataView) {
			private static final long serialVersionUID = -1L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				target.add(listContainer);
			}
		});

		final WebMarkupContainer addModerator = new WebMarkupContainer("addModerator");
		addModerator.add(new AjaxEventBehavior("onclick") {
			private static final long serialVersionUID = 1818116963707864134L;

			protected void onEvent(AjaxRequestTarget target) {
        		addModeratorsForm.clear();
        		target.add(addModeratorsForm);
        		target.appendJavaScript("addModerators();");
        	}
        });
		
		form = new RoomForm("form", listContainer, new Room()){
			private static final long serialVersionUID = 3186201157375166657L;

			@Override
			protected void onModelChanged() {
				super.onModelChanged();
				boolean roomEmpty = (getModelObject() == null || getModelObject().getRooms_id() == null);
				if (roomEmpty) {
					addModerator.add(AttributeModifier.replace("class", "formNewButton disabled"));
				} else {
					addModerator.add(AttributeModifier.replace("class", "formNewButton"));
				}
				addModerator.setEnabled(!roomEmpty);
			}
			
			/*@Override
			public void updateView(AjaxRequestTarget target) {
				super.updateView(target);
				target.add(addModerator);
			}*/
		};
		
        add(form.add(addModerator.setOutputMarkupId(true)));
        addModeratorsForm = new AddUsersForm("addModerators", form);
		add(addModeratorsForm);
	}
}
