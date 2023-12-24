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

import static org.apache.openmeetings.web.common.BasePanel.EVT_CLICK;
import static org.apache.openmeetings.util.OpenmeetingsVariables.ATTR_TITLE;

import java.io.Serializable;
import java.util.List;

import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.web.app.ClientManager;
import org.apache.openmeetings.web.pages.MainPage;
import org.apache.openmeetings.web.util.OmTooltipBehavior;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;


import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesome6IconType;
import jakarta.inject.Inject;

public class RoomListPanel extends Panel {
	private static final long serialVersionUID = 1L;
	private final String label;
	private final ListView<Room> list = new ListView<>("list") {
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
			final Label curUsers = new Label("curUsers", new Model<>(cm.streamByRoom(r.getId()).count()));
			roomContainer.add(curUsers.setOutputMarkupId(true));
			roomContainer.add(new Label("totalUsers", r.getCapacity()));
			item.add(new WebMarkupContainer("btn").add(new Label("label", label)).add(new RoomEnterBehavior(r.getId()) {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onEvent(AjaxRequestTarget target) {
					onRoomEnter(target, roomId);
				}
			}));
			roomContainer.add(new BootstrapAjaxLink<String>("refresh", null, Buttons.Type.Outline_Info, new ResourceModel("lbl.refresh")) {
				private static final long serialVersionUID = 1L;

				{
					setIconType(FontAwesome6IconType.rotate_s);
				}

				@Override
				protected <L extends Serializable> Component newLabel(String markupId, IModel<L> model) {
					return super.newLabel(markupId, model).setRenderBodyOnly(false).add(new CssClassNameAppender("sr-only"));
				}

				@Override
				public void onClick(AjaxRequestTarget target) {
					target.add(curUsers.setDefaultModelObject(cm.streamByRoom(r.getId()).count()));
					onRefreshClick(target, r);
				}
			}.add(AttributeModifier.append(ATTR_TITLE, new ResourceModel("lbl.refresh"))));
		}
	};

	@Inject
	private ClientManager cm;

	public RoomListPanel(String id, List<Room> rooms, final String label) {
		super(id);
		setOutputMarkupId(true);
		this.label = label;
		list.setList(rooms);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		add(list);
		add(new OmTooltipBehavior());
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
