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

import static org.apache.openmeetings.web.app.WebSession.getUserId;
import static org.apache.openmeetings.web.common.BasePanel.EVT_CLICK;
import static org.apache.openmeetings.util.OmFileHelper.getHumanSize;

import org.apache.openmeetings.db.dao.file.FileItemDao;
import org.apache.openmeetings.db.dao.record.RecordingDao;
import org.apache.openmeetings.db.dto.record.RecordingContainerData;
import org.apache.openmeetings.db.entity.file.BaseFileItem;
import org.apache.openmeetings.web.common.NameDialog;
import org.apache.openmeetings.web.common.tree.FileTreePanel;
import org.apache.openmeetings.web.room.RoomPanel;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;

import jakarta.inject.Inject;


public class RoomFilePanel extends FileTreePanel {
	private static final long serialVersionUID = 1L;
	private final RoomPanel room;

	@Inject
	private FileItemDao fileDao;
	@Inject
	private RecordingDao recDao;

	public RoomFilePanel(String id, RoomPanel room, NameDialog addFolder) {
		super(id, room.getRoom().getId(), addFolder);
		this.room = room;
	}

	@Override
	public void updateSizes() {
		RecordingContainerData sizeData = recDao.getContainerData(getUserId());
		long userSize = fileDao.getOwnSize(getUserId());
		long roomSize = fileDao.getRoomSize(room.getRoom().getId());
		if (sizeData != null) {
			userSize += sizeData.getUserHomeSize();
			roomSize += sizeData.getPublicFileSize();
		}
		homeSize.setObject(getHumanSize(userSize));
		publicSize.setObject(getHumanSize(roomSize));
	}

	@Override
	protected void update(AjaxRequestTarget target, BaseFileItem f) {
		//no-op
	}

	@Override
	protected String getContainment() {
		return "";
	}

	@Override
	protected Component getUpload() {
		return super.getUpload()
				.setVisible(true)
				.add(AjaxEventBehavior.onEvent(EVT_CLICK, target -> room.getSidebar().showUpload(target)));
	}
}
