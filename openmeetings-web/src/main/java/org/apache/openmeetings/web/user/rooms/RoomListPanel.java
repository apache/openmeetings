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

import static org.apache.openmeetings.util.OpenmeetingsVariables.ATTR_TITLE;
import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.common.BasePanel.EVT_CLICK;

import java.util.List;

import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.web.app.ClientManager;
import org.apache.openmeetings.web.pages.MainPage;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import com.googlecode.wicket.jquery.core.JQueryBehavior;
import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.ui.JQueryIcon;
import com.googlecode.wicket.jquery.ui.form.button.Button;
import com.googlecode.wicket.jquery.ui.markup.html.link.AjaxLink;
import com.googlecode.wicket.jquery.ui.widget.tooltip.TooltipBehavior;

public class RoomListPanel extends Panel {
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
				item.add((roomContainer = new WebMarkupContainer("roomContainer")).add(new AjaxEventBehavior(EVT_CLICK){
					private static final long serialVersionUID = 1L;

					@Override
					protected void onEvent(AjaxRequestTarget target) {
						onContainerClick(target, r);
					}
				}));
				roomContainer.add(new Label("roomName", r.getName()));
				final WebMarkupContainer info = new WebMarkupContainer("info");
				roomContainer.add(info.setOutputMarkupId(true)
						.add(AttributeModifier.append(ATTR_TITLE, getString(String.format("room.type.%s.desc", r.getType().name())))));
				final Label curUsers = new Label("curUsers", new Model<>(getBean(ClientManager.class).listByRoom(r.getId()).size()));
				roomContainer.add(curUsers.setOutputMarkupId(true));
				roomContainer.add(new Label("totalUsers", r.getCapacity()));
				item.add(new Button("btn").add(new Label("label", label)).add(new RoomEnterBehavior(r.getId()) {
					private static final long serialVersionUID = 1L;

					@Override
					protected void onEvent(AjaxRequestTarget target) {
						onRoomEnter(target, roomId);
					}
				}));
				roomContainer.add(new AjaxLink<String>("refresh") {
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick(AjaxRequestTarget target) {
						target.add(curUsers.setDefaultModelObject(getBean(ClientManager.class).listByRoom(r.getId()).size()));
						onRefreshClick(target, r);
					}

					@Override
					public void onConfigure(JQueryBehavior behavior) {
						behavior.setOption("icon", Options.asString(JQueryIcon.REFRESH));
						behavior.setOption("showLabel", false);
					}
				});
			}
		});
		add(new TooltipBehavior(".info-text"));
	}

	public void update(IPartialPageRequestHandler handler, List<Room> rooms) {
		list.setList(rooms);
		handler.add(this);
	}

	/**
	 * this method need to be overriden to perform custom actions on room container click
	 * @param target - the {@link AjaxRequestTarget}
	 * @param r - current {@link Room}
	 */
	public void onContainerClick(AjaxRequestTarget target, Room r) {
		//no-op
	}

	/**
	 * this method need to be overriden to perform custom actions on room refresh click
	 * @param target - the {@link AjaxRequestTarget}
	 * @param r - current {@link Room}
	 */
	public void onRefreshClick(AjaxRequestTarget target, Room r) {
		//no-op
	}

	/**
	 * this method need to be overriden to perform custom actions on room enter click
	 * @param target - the {@link AjaxRequestTarget}
	 * @param roomId - id of the room being entered
	 */
	public void onRoomEnter(AjaxRequestTarget target, Long roomId) {
		RoomEnterBehavior.roomEnter((MainPage)getPage(), target, roomId);
	}
}
