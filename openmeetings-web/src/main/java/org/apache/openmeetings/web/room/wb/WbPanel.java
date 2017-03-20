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
package org.apache.openmeetings.web.room.wb;

import static org.apache.openmeetings.web.app.Application.getBean;

import org.apache.openmeetings.core.data.whiteboard.WhiteboardCache;
import org.apache.openmeetings.db.entity.room.Room.Right;
import org.apache.openmeetings.web.room.RoomPanel;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.PriorityHeaderItem;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.ResourceReference;

public class WbPanel extends Panel {
	private static final long serialVersionUID = 1L;
	private final WbTabbedPanel tabs;
	private boolean readOnly = true;
	private final RoomPanel rp;

	public WbPanel(String id, RoomPanel rp) {
		super(id);
		this.rp = rp;
		setOutputMarkupId(true);

		getBean(WhiteboardCache.class).get(rp.getRoom().getId()).getWhiteboards();//TODO
		add(tabs = new WbTabbedPanel("tabs", this));
		tabs.setOutputMarkupId(true);// FIXME TODO add Sortable for tabs add(new Sortable<T>)
		tabs.addWb("Whiteboard 1");
	}

	private static ResourceReference newResourceReference() {
		return new JavaScriptResourceReference(WbPanel.class, "wb.js");
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(new PriorityHeaderItem(JavaScriptHeaderItem.forReference(newResourceReference())));
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public WbPanel setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
		return this;
	}

	public WbPanel update(IPartialPageRequestHandler handler) {
		readOnly = !rp.getClient().hasRight(Right.whiteBoard);
		if (handler != null) {
			tabs.reload(handler);
			handler.appendJavaScript("setRoomSizes();");
		}
		return this;
	}
}
