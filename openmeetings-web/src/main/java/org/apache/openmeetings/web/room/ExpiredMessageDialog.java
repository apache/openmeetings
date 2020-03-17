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
package org.apache.openmeetings.web.room;

import org.apache.openmeetings.web.common.OmModalCloseButton;
import org.apache.openmeetings.web.room.menu.RoomMenuPanel;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.model.ResourceModel;

public class ExpiredMessageDialog extends IconTextModal {
	private static final long serialVersionUID = 1L;
	private final RoomMenuPanel menu;

	public ExpiredMessageDialog(String id, String message, RoomMenuPanel menu) {
		super(id);
		withLabel(message);
		this.menu = menu;
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		withErrorIcon();
		header(new ResourceModel("204"));
		setBackdrop(Backdrop.TRUE);
		show(true);
		setUseCloseHandler(true);
		addButton(OmModalCloseButton.of("54"));
	}

	@Override
	protected void onClose(IPartialPageRequestHandler target) {
		menu.exit(target);
	}
}
