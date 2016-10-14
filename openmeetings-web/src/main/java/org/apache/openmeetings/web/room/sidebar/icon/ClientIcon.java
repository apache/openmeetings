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

import org.apache.openmeetings.web.app.Client;
import org.apache.openmeetings.web.room.RoomPanel;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.html.WebMarkupContainer;

public abstract class ClientIcon extends WebMarkupContainer {
	private static final long serialVersionUID = 1L;
	protected static final String ICON_CLASS = "ui-icon ";
	protected static final String ALIGN_LEFT = "align-left ";
	protected static final String ALIGN_RIGHT = "align-right ";
	protected static final String CLS_CLICKABLE = "clickable ";
	protected final RoomPanel room;
	protected final boolean self;
	protected final Client client;
	protected String mainCssClass;
	protected final StringBuilder cssClass = new StringBuilder(ICON_CLASS);

	public ClientIcon(String id, Client client, RoomPanel room) {
		super(id);
		this.room = room;
		this.client = client;
		self = room.getClient().getUid().equals(client.getUid());
		setOutputMarkupId(true);
		setOutputMarkupPlaceholderTag(true);
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

	protected abstract boolean isClickable();

	protected abstract String getScript();

	protected void internalUpdate() {
	}

	public final void update(IPartialPageRequestHandler handler) {
		cssClass.setLength(0);
		StringBuilder cls = new StringBuilder(ICON_CLASS);
		cls.append(getAlign()).append(mainCssClass);
		if (isClickable()) {
			//request/remove
			cls.append(CLS_CLICKABLE);
			add(AttributeAppender.replace("onclick", getScript()));
		}
		internalUpdate();
		add(AttributeAppender.replace("title", getTitle()));
		add(AttributeAppender.replace("class", cls.append(cssClass)));
		
		if (handler != null) {
			handler.add(this);
		}
	}
}
