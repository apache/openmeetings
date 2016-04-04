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

import static org.apache.openmeetings.web.app.Application.getBean;

import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.web.admin.AdminPanel;
import org.apache.openmeetings.web.admin.SearchableDataView;
import org.apache.openmeetings.web.common.PagedEntityListPanel;
import org.apache.openmeetings.web.data.DataViewContainer;
import org.apache.openmeetings.web.data.OmOrderByBorder;
import org.apache.openmeetings.web.data.SearchableDataProvider;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

public class RoomsPanel extends AdminPanel {
	private static final long serialVersionUID = -1L;
	private final static JavaScriptResourceReference ROOM_FUNCTIONS = new JavaScriptResourceReference(RoomsPanel.class, "room.js");
	final WebMarkupContainer listContainer = new WebMarkupContainer("listContainer");
	private RoomForm form;
	
	@Override
	public void onMenuPanelLoad(IPartialPageRequestHandler handler) {
		super.onMenuPanelLoad(handler);
		handler.appendJavaScript("omRoomPanelInit();");
	}

	public RoomsPanel(String id) {
		super(id);
		SearchableDataView<Room> dataView = new SearchableDataView<Room>("roomList", new SearchableDataProvider<Room>(RoomDao.class)) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(final Item<Room> item) {
				Room room = item.getModelObject();
				final Long roomId = room.getId();
				item.add(new Label("id"));
				item.add(new Label("name"));
				item.add(new Label("ispublic"));
				item.add(new AjaxEventBehavior("click") {
					private static final long serialVersionUID = 1L;

					@Override
					protected void onEvent(AjaxRequestTarget target) {
						form.hideNewRecord();
						form.setModelObject(getBean(RoomDao.class).get(roomId));
						form.updateView(target);
						target.add(form, listContainer);
						target.appendJavaScript("omRoomPanelInit();");
					}
				});
				item.add(AttributeModifier.replace("class", getRowClass(room.getId(), form.getModelObject().getId())));
			}
		};
		
		add(listContainer.add(dataView).setOutputMarkupId(true));
		PagedEntityListPanel navigator = new PagedEntityListPanel("navigator", dataView) {
			private static final long serialVersionUID = -1L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				target.add(listContainer);
			}
		};
		DataViewContainer<Room> container = new DataViewContainer<Room>(listContainer, dataView, navigator);
		container.addLink(new OmOrderByBorder<Room>("orderById", "id", container))
			.addLink(new OmOrderByBorder<Room>("orderByName", "name", container))
			.addLink(new OmOrderByBorder<Room>("orderByPublic", "ispublic", container));
		add(container.getLinks());
		add(navigator);

		add(form = new RoomForm("form", listContainer, new Room()));
	}
	
	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(JavaScriptHeaderItem.forReference(ROOM_FUNCTIONS));
	}
}
