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

import org.apache.openmeetings.web.common.OmModalCloseButton;
import org.apache.openmeetings.web.util.upload.BootstrapFileUploadBehavior;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.model.ResourceModel;

import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;

public class UploadDialog extends Modal<String> {
	private static final long serialVersionUID = 1L;
	private RoomUploadForm wsUpload;
	private final RoomFilePanel roomFiles;

	public UploadDialog(String id, RoomFilePanel roomFiles) {
		super(id);
		this.roomFiles = roomFiles;
	}

	@Override
	protected void onInitialize() {
		header(new ResourceModel("upload.dlg.choose.title"));
		setCloseOnEscapeKey(false);
		setBackdrop(Backdrop.STATIC);

		add(wsUpload = new RoomUploadForm("wsupload", roomFiles));
		add(BootstrapFileUploadBehavior.INSTANCE);
		addButton(OmModalCloseButton.of("85"));

		super.onInitialize();
	}

	@Override
	public Modal<String> show(IPartialPageRequestHandler handler) {
		wsUpload.show(handler);
		return super.show(handler);
	}
}
