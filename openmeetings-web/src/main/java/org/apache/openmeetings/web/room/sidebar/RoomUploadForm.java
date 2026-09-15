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

import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.db.entity.file.BaseFileItem;
import org.apache.openmeetings.web.common.upload.UploadForm;
import org.apache.openmeetings.web.room.RoomPanel;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.PriorityHeaderItem;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

public class RoomUploadForm extends UploadForm {
	private static final long serialVersionUID = 1L;

	public RoomUploadForm(String id) {
		super(id, "" + RequestCycle.get().urlFor(new RoomFileUploadResourceReference(), new PageParameters()));
	}

	@Override
	public void show(IPartialPageRequestHandler handler) {
		final RoomPanel rp = findParent(RoomPanel.class);
		final BaseFileItem last = rp.getSidebar().getFilesPanel().getLastSelected();
		Client c = rp.getClient();
		c.getCustomProps().clear();
		if (last.getId() == null) {
			c.getCustomProps().put(PARAM_LAST_SELECTED_ROOM, last.getRoomId());
			c.getCustomProps().put(PARAM_LAST_SELECTED_OWNER, last.getOwnerId());
			c.getCustomProps().put(PARAM_LAST_SELECTED_GROUP, last.getGroupId());
		} else {
			c.getCustomProps().put(PARAM_LAST_SELECTED_ID, last.getId());
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
