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

import static org.apache.openmeetings.web.room.sidebar.RoomFileUploadResourceReference.PARAM_LAST_SELECTED_GROUP;
import static org.apache.openmeetings.web.room.sidebar.RoomFileUploadResourceReference.PARAM_LAST_SELECTED_ID;
import static org.apache.openmeetings.web.room.sidebar.RoomFileUploadResourceReference.PARAM_LAST_SELECTED_OWNER;
import static org.apache.openmeetings.web.room.sidebar.RoomFileUploadResourceReference.PARAM_LAST_SELECTED_ROOM;
import static org.apache.openmeetings.util.OpenmeetingsVariables.ATTR_VALUE;

import org.apache.openmeetings.db.entity.file.BaseFileItem;
import org.apache.openmeetings.web.common.upload.UploadForm;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.PriorityHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

public class RoomUploadForm extends UploadForm {
	private static final long serialVersionUID = 1L;
	private final WebMarkupContainer lastSelectedId = new WebMarkupContainer("lastSelectedId");
	private final WebMarkupContainer lastSelectedRoom = new WebMarkupContainer("lastSelectedRoom"); // required for "fake" root
	private final WebMarkupContainer lastSelectedOwner = new WebMarkupContainer("lastSelectedOwner"); // required for "fake" root
	private final WebMarkupContainer lastSelectedGroup = new WebMarkupContainer("lastSelectedGroup"); // required for "fake" root
	private final RoomFilePanel roomFiles;

	public RoomUploadForm(String id, RoomFilePanel roomFiles) {
		super(id, "" + RequestCycle.get().urlFor(new RoomFileUploadResourceReference(), new PageParameters()));
		this.roomFiles = roomFiles;
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		form.add(lastSelectedId.setMarkupId(PARAM_LAST_SELECTED_ID).setOutputMarkupId(true));
		form.add(lastSelectedRoom.setMarkupId(PARAM_LAST_SELECTED_ROOM).setOutputMarkupId(true));
		form.add(lastSelectedOwner.setMarkupId(PARAM_LAST_SELECTED_OWNER).setOutputMarkupId(true));
		form.add(lastSelectedGroup.setMarkupId(PARAM_LAST_SELECTED_GROUP).setOutputMarkupId(true));
	}

	@Override
	public void show(IPartialPageRequestHandler handler) {
		BaseFileItem last = roomFiles.getLastSelected();
		if (last.getId() == null) {
			lastSelectedRoom.add(AttributeModifier.replace(ATTR_VALUE, last.getRoomId()));
			lastSelectedOwner.add(AttributeModifier.replace(ATTR_VALUE, last.getOwnerId()));
			lastSelectedGroup.add(AttributeModifier.replace(ATTR_VALUE, last.getGroupId()));
		} else {
			lastSelectedId.add(AttributeModifier.replace(ATTR_VALUE, last.getId()));
		}
		super.show(handler);
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(new PriorityHeaderItem(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(RoomUploadForm.class, "room-upload.js"))));
	}

	@Override
	protected String uploadLocation() {
		return "." + UploadDialog.DIALOG_CLASS + " .modal-content .modal-footer";
	}

	@Override
	protected String extraBindFunc() {
		return "roomUploadExtaBindFunc";
	}

	@Override
	protected String onCompleteFunc() {
		return "roomUploadOnComplete";
	}
}
