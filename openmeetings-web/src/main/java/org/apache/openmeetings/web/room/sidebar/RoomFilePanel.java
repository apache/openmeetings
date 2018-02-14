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

import static org.apache.openmeetings.util.OmFileHelper.getHumanSize;
import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.WebSession.getUserId;
import static org.apache.openmeetings.web.common.BasePanel.EVT_CLICK;

import org.apache.openmeetings.db.dao.file.FileItemDao;
import org.apache.openmeetings.db.dao.record.RecordingDao;
import org.apache.openmeetings.db.dto.record.RecordingContainerData;
import org.apache.openmeetings.db.entity.file.BaseFileItem;
import org.apache.openmeetings.web.common.ConfirmableAjaxBorder.ConfirmableBorderDialog;
import org.apache.openmeetings.web.common.NameDialog;
import org.apache.openmeetings.web.common.tree.FileTreePanel;
import org.apache.openmeetings.web.room.RoomPanel;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;

public class RoomFilePanel extends FileTreePanel {
	private static final long serialVersionUID = 1L;
	private final RoomPanel room;

	public RoomFilePanel(String id, RoomPanel room, NameDialog addFolder, ConfirmableBorderDialog trashConfirm) {
		super(id, room.getRoom().getId(), addFolder, trashConfirm);
		this.room = room;
	}

	@Override
	public void updateSizes() {
		FileItemDao dao = getBean(FileItemDao.class);
		RecordingContainerData sizeData = getBean(RecordingDao.class).getContainerData(getUserId());
		long userSize = dao.getOwnSize(getUserId());
		long roomSize = dao.getRoomSize(room.getRoom().getId());
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
				.add(new AjaxEventBehavior(EVT_CLICK) {
					private static final long serialVersionUID = 1L;

					@Override
					protected void onEvent(AjaxRequestTarget target) {
						room.getSidebar().showUpload(target);
					}
				});
	}

	@Override
	public void select(BaseFileItem fi, AjaxRequestTarget target, boolean shift, boolean ctrl) {
		super.select(fi, target, shift, ctrl);
		if (target != null) {
			target.appendJavaScript("Room.setSize();");
		}
	}
}
