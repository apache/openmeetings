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
package org.apache.openmeetings.web.room.sidebar.icon;

import static org.apache.openmeetings.web.room.sidebar.RoomSidebar.FUNC_CHANGE_RIGHT;

import org.apache.openmeetings.db.entity.room.Room.Right;
import org.apache.openmeetings.web.app.Client;
import org.apache.openmeetings.web.room.RoomPanel;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.html.WebMarkupContainer;

public abstract class RoomRightIcon extends WebMarkupContainer {
	private static final long serialVersionUID = 1L;
	private static final String CLS_GRANTED = "granted ";
	private static final String ICON_CLASS = "ui-icon ";
	protected static final String ALIGN_LEFT = "align-left ";
	protected static final String ALIGN_RIGHT = "align-right ";
	protected static final String CLS_CLICKABLE = "clickable ";
	protected final RoomPanel room;
	protected final Right right;
	protected final boolean self;
	protected final Client client;
	protected String mainCssClass;
	
	public RoomRightIcon(String id, Client client, Right right, RoomPanel room) {
		super(id);
		this.room = room;
		this.client = client;
		this.right = right;
		self = room.getClient().getUid().equals(client.getUid());
		setOutputMarkupId(true);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		update(null);
	}
	
	protected abstract String getTitle();
	
	protected String getAlign() {
		return ALIGN_LEFT;
	}
	
	protected boolean isClickable() {
		return (self && !hasRight()) || (!self && room.getClient().hasRight(Right.moderator));
	}
	
	protected boolean hasRight() {
		return client.hasRight(right);
	}

	protected String getScript() {
		return String.format("%s('%s', '%s');", FUNC_CHANGE_RIGHT, right.name(), client.getUid());
	}
	
	public void update(IPartialPageRequestHandler handler) {
		StringBuilder cls = new StringBuilder(ICON_CLASS);
		cls.append(getAlign()).append(mainCssClass);
		if (hasRight()) {
			cls.append(CLS_GRANTED);
		}
		if (isClickable()) {
			//request/remove
			cls.append(CLS_CLICKABLE);
			add(AttributeAppender.replace("onclick", getScript()));
		}
		add(AttributeAppender.replace("title", getTitle()));
		add(AttributeAppender.replace("class", cls));
		
		if (handler != null) {
			handler.add(this);
		}
	}
}
