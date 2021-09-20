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
package org.apache.openmeetings.web.room.sidebar;

import static org.apache.openmeetings.util.OpenmeetingsVariables.getMaxUploadSize;
import static org.apache.openmeetings.web.room.sidebar.RoomFileUploadResourceReference.PARAM_LAST_SELECTED_GROUP;
import static org.apache.openmeetings.web.room.sidebar.RoomFileUploadResourceReference.PARAM_LAST_SELECTED_ID;
import static org.apache.openmeetings.web.room.sidebar.RoomFileUploadResourceReference.PARAM_LAST_SELECTED_OWNER;
import static org.apache.openmeetings.web.room.sidebar.RoomFileUploadResourceReference.PARAM_LAST_SELECTED_ROOM;

import org.apache.openmeetings.db.entity.file.BaseFileItem;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.common.OmModalCloseButton;
import org.apache.openmeetings.web.room.RoomPanel;
import org.apache.openmeetings.web.util.upload.BootstrapFileUploadBehavior;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.PriorityHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.util.lang.Bytes;

import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;

public class UploadDialog extends Modal<String> {
	private static final long serialVersionUID = 1L;
	private final WebMarkupContainer form = new WebMarkupContainer("form");
	private final RoomFilePanel roomFiles;
	private final RoomPanel room;
	private final WebMarkupContainer lastSelectedId = new WebMarkupContainer("lastSelectedId");
	private final WebMarkupContainer lastSelectedRoom = new WebMarkupContainer("lastSelectedRoom"); // required for "fake" root
	private final WebMarkupContainer lastSelectedOwner = new WebMarkupContainer("lastSelectedOwner"); // required for "fake" root
	private final WebMarkupContainer lastSelectedGroup = new WebMarkupContainer("lastSelectedGroup"); // required for "fake" root

	public UploadDialog(String id, RoomPanel room, RoomFilePanel roomFiles) {
		super(id);
		this.roomFiles = roomFiles;
		this.room = room;
	}

	@Override
	protected void onInitialize() {
		header(new ResourceModel("upload.dlg.choose.title"));
		setCloseOnEscapeKey(false);
		setBackdrop(Backdrop.STATIC);

		add(form.add(AttributeModifier.append("data-max-size", getMaxUploadSize()))
				.add(AttributeModifier.append("data-max-size-lbl", Bytes.bytes(getMaxUploadSize()).toString(WebSession.get().getLocale())))
				.add(AttributeModifier.append("data-upload-lbl", getString("593")))
				.add(AttributeModifier.append("data-max-upload-lbl", getString("1491")))
				.add(AttributeModifier.append("action", "" + urlFor(new RoomFileUploadResourceReference(), new PageParameters())))
				.setOutputMarkupId(true)
				.setOutputMarkupPlaceholderTag(true));
		form.add(new WebMarkupContainer("sid").add(AttributeModifier.append("value", room.getClient().getSid())).setMarkupId("room-upload-sid").setOutputMarkupId(true));
		form.add(lastSelectedId.setMarkupId(PARAM_LAST_SELECTED_ID).setOutputMarkupId(true));
		form.add(lastSelectedRoom.setMarkupId(PARAM_LAST_SELECTED_ROOM).setOutputMarkupId(true));
		form.add(lastSelectedOwner.setMarkupId(PARAM_LAST_SELECTED_OWNER).setOutputMarkupId(true));
		form.add(lastSelectedGroup.setMarkupId(PARAM_LAST_SELECTED_GROUP).setOutputMarkupId(true));
		add(BootstrapFileUploadBehavior.INSTANCE);
		addButton(OmModalCloseButton.of("85"));

		super.onInitialize();
	}

	@Override
	public Modal<String> show(IPartialPageRequestHandler handler) {
		BaseFileItem last = roomFiles.getLastSelected();
		if (last.getId() == null) {
			lastSelectedRoom.add(AttributeModifier.replace("value", last.getRoomId()));
			lastSelectedOwner.add(AttributeModifier.replace("value", last.getOwnerId()));
			lastSelectedGroup.add(AttributeModifier.replace("value", last.getGroupId()));
		} else {
			lastSelectedId.add(AttributeModifier.replace("value", last.getId()));
		}
		handler.add(form.setVisible(true));
		handler.appendJavaScript("Upload.bindUpload();");
		return super.show(handler);
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(new PriorityHeaderItem(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(UploadDialog.class, "upload.js"))));
	}
}
