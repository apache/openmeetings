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

import org.apache.openmeetings.web.room.sidebar.icon.right.AudioRightIcon;
import org.apache.openmeetings.web.room.sidebar.icon.right.ExclusiveRightIcon;
import org.apache.openmeetings.web.room.sidebar.icon.right.ModeratorRightIcon;
import org.apache.openmeetings.web.room.sidebar.icon.right.PresenterRightIcon;
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
	private final PresenterRightIcon rightPresenter;
	private final ScreenShareRightIcon rightScreen;
	private final RemoteControlRightIcon rightRemote;
	private final AudioRightIcon rightAudio;
	private final VideoRightIcon rightVideo;
	private final ExclusiveRightIcon rightExclsv;

	public ClientIconsPanel(String id, String uid) {
		super(id);
		setOutputMarkupId(true);
		setOutputMarkupPlaceholderTag(true);
		add(rightModer = new ModeratorRightIcon("right-moder", uid));
		add(rightPresenter = new PresenterRightIcon("right-presenter", uid));
		add(rightWb = new WhiteboardRightIcon("right-wb", uid));
		add(rightScreen = new ScreenShareRightIcon("right-screen-share", uid));
		add(rightRemote = new RemoteControlRightIcon("right-remote-control", uid));
		add(rightAudio = new AudioRightIcon("right-audio", uid));
		add(rightVideo = new VideoRightIcon("right-video", uid));
		add(rightExclsv = new ExclusiveRightIcon("right-exclsv", uid));
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		update(null);
	}

	public ClientIconsPanel update(IPartialPageRequestHandler handler) {
		rightModer.update(handler);
		rightPresenter.update(handler);
		rightWb.update(handler);
		rightScreen.update(handler);
		rightRemote.update(handler);
		rightAudio.update(handler);
		rightVideo.update(handler);
		rightExclsv.update(handler);
		return this;
	}
}
