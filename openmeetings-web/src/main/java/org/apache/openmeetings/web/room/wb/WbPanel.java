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
import static org.apache.wicket.AttributeModifier.append;

import java.util.Arrays;

import org.apache.openmeetings.core.data.whiteboard.WhiteboardCache;
import org.apache.openmeetings.db.entity.room.Room.Right;
import org.apache.openmeetings.util.OmFileHelper;
import org.apache.openmeetings.web.room.RoomPanel;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.ResourceReference;

public class WbPanel extends Panel {
	private static final long serialVersionUID = 1L;
	private final static ResourceReference WB_JS_REFERENCE = new JavaScriptResourceReference(WbPanel.class, "wb.js");
	private final static ResourceReference FABRIC_JS_REFERENCE = new JavaScriptResourceReference(WbPanel.class, "fabric.js");
	private boolean readOnly = true;
	private final RoomPanel rp;

	public WbPanel(String id, RoomPanel rp) {
		super(id);
		this.rp = rp;
		setOutputMarkupId(true);

		getBean(WhiteboardCache.class).get(rp.getRoom().getId()).getWhiteboards();//TODO
		add(new ListView<String>("clipart", Arrays.asList(OmFileHelper.getPublicClipartsDir().list())) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<String> item) {
				String cls = String.format("clipart-%s", item.getIndex());
				item.add(append("class", cls), append("data-mode", cls)
						, new AttributeAppender("data-image", item.getModelObject()).setSeparator(""));
			}
		});
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(JavaScriptHeaderItem.forReference(FABRIC_JS_REFERENCE));
		response.render(JavaScriptHeaderItem.forReference(WB_JS_REFERENCE));
		response.render(OnDomReadyHeaderItem.forScript("WbArea.init(); WbArea.add(1, 'Whiteboard 1');"));
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
			handler.appendJavaScript("setRoomSizes();");
		}
		return this;
	}
}
