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

import org.apache.openmeetings.web.room.sidebar.icon.SettingsIcon;
import org.apache.openmeetings.web.room.sidebar.icon.activity.CamActivityIcon;
import org.apache.openmeetings.web.room.sidebar.icon.activity.MicActivityIcon;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;

public class SelfIconsPanel extends ClientIconsPanel {
	private static final long serialVersionUID = 1L;
	private final SettingsIcon settings;
	private final CamActivityIcon cam;
	private final MicActivityIcon mic;
	private final boolean header;

	public SelfIconsPanel(String id, String uid, boolean header) {
		super(id, uid);
		this.header = header;
		add(settings = new SettingsIcon("settings", uid)
			, cam = new CamActivityIcon("cam", uid)
			, mic = new MicActivityIcon("mic", uid));
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		update(null);
		if (header) {
			//This is required to avoid initial displaying of all icons
			setVisible(false);
		}
	}

	@Override
	public ClientIconsPanel update(IPartialPageRequestHandler handler) {
		setVisible(true);
		super.update(handler);
		settings.update(handler);
		cam.update(handler);
		mic.update(handler);
		return this;
	}
}
