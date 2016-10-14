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

import org.apache.openmeetings.web.app.Client;
import org.apache.openmeetings.web.room.RoomPanel;
import org.apache.openmeetings.web.room.sidebar.icon.right.AudioRightIcon;
import org.apache.openmeetings.web.room.sidebar.icon.right.ExclusiveRightIcon;
import org.apache.openmeetings.web.room.sidebar.icon.right.ModeratorRightIcon;
import org.apache.openmeetings.web.room.sidebar.icon.right.MuteRightIcon;
import org.apache.openmeetings.web.room.sidebar.icon.right.RemoteControlRightIcon;
import org.apache.openmeetings.web.room.sidebar.icon.right.ScreenShareRightIcon;
import org.apache.openmeetings.web.room.sidebar.icon.right.VideoRightIcon;
import org.apache.openmeetings.web.room.sidebar.icon.right.WhiteboardRightIcon;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.html.panel.Panel;

public class ClientIconsPanel extends Panel {
	private static final long serialVersionUID = 1L;
	private final ModeratorRightIcon rightModer;
	private final WhiteboardRightIcon rightWb;
	private final ScreenShareRightIcon rightScreen;
	private final RemoteControlRightIcon rightRemote;
	private final AudioRightIcon rightAudio;
	private final VideoRightIcon rightVideo;
	private final MuteRightIcon rightMute;
	private final ExclusiveRightIcon rightExclsv;

	public ClientIconsPanel(String id, Client client, RoomPanel room) {
		super(id);
		setOutputMarkupId(true);
		setOutputMarkupPlaceholderTag(true);
		add(rightModer = new ModeratorRightIcon("right-moder", client, room));
		add(rightWb = new WhiteboardRightIcon("right-wb", client, room));
		add(rightScreen = new ScreenShareRightIcon("right-screen-share", client, room));
		add(rightRemote = new RemoteControlRightIcon("right-remote-control", client, room));
		add(rightAudio = new AudioRightIcon("right-audio", client, room));
		add(rightVideo = new VideoRightIcon("right-video", client, room));
		add(rightMute = new MuteRightIcon("right-mute", client, room));
		add(rightExclsv = new ExclusiveRightIcon("right-exclsv", client, room));
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		update(null);
	}
	
	public void update(IPartialPageRequestHandler handler) {
		rightModer.update(handler);
		rightWb.update(handler);
		rightScreen.update(handler);
		rightRemote.update(handler);
		rightAudio.update(handler);
		rightVideo.update(handler);
		rightMute.update(handler);
		rightExclsv.update(handler);
	}
}
