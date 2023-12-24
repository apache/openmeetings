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
import static org.apache.openmeetings.web.util.ThreadHelper.startRunnable;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.DoubleConsumer;

import org.apache.commons.fileupload2.core.FileItem;
import org.apache.openmeetings.core.data.file.FileProcessor;
import org.apache.openmeetings.core.util.WebSocketHelper;
import org.apache.openmeetings.db.dao.file.FileItemDao;
import org.apache.openmeetings.db.dao.file.FileItemLogDao;
import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.db.entity.file.BaseFileItem;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.room.Room.Right;
import org.apache.openmeetings.db.entity.room.Room.RoomElement;
import org.apache.openmeetings.db.util.ws.RoomMessage;
import org.apache.openmeetings.db.util.ws.TextRoomMessage;
import org.apache.openmeetings.util.process.ProcessResult;
import org.apache.openmeetings.util.process.ProcessResultList;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.common.upload.UploadResourceReference;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.protocol.http.servlet.MultipartServletWebRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.openjson.JSONObject;

import jakarta.inject.Inject;

public class RoomFileUploadResourceReference extends UploadResourceReference {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(RoomFileUploadResourceReference.class);
	private static final String PARAM_TO_WB_NAME = "room-upload-to-wb";
	private static final String PARAM_CLEAN_NAME = "room-upload-clean";
	public static final String PARAM_LAST_SELECTED_ID = "room-upload-last-selected-id";
	public static final String PARAM_LAST_SELECTED_ROOM = "room-upload-last-selected-room";
	public static final String PARAM_LAST_SELECTED_OWNER = "room-upload-last-selected-owner";
	public static final String PARAM_LAST_SELECTED_GROUP = "room-upload-last-selected-group";

	@Inject
	private FileProcessor processor;
	@Inject
	private FileItemLogDao fileLogDao;
	@Inject
	private FileItemDao fileDao;

	public RoomFileUploadResourceReference() {
		super(RoomFileUploadResourceReference.class, "room-file-upload");
		Injector.get().inject(this);
	}

	@Override
	protected boolean isUploadAllowed(Client c) {
		return Optional.ofNullable(c)
				.map(Client::getRoom)
				.map(room -> Room.Type.INTERVIEW != room.getType() && !room.isHidden(RoomElement.FILES) && c.hasRight(Right.PRESENTER))
				.orElse(false);
	}

	@Override
	protected void processFiles(Client c, List<FileItem> fileItems, String uuid, MultipartServletWebRequest multiPartRequest) {
		final boolean toWb = multiPartRequest.getPostParameters().getParameterValue(PARAM_TO_WB_NAME).toBoolean(false);
		final boolean clean = multiPartRequest.getPostParameters().getParameterValue(PARAM_CLEAN_NAME).toBoolean(false);
		final long lastSelectedId = multiPartRequest.getPostParameters().getParameterValue(PARAM_LAST_SELECTED_ID).toLong(-1L);
		final long lastSelectedRoom = multiPartRequest.getPostParameters().getParameterValue(PARAM_LAST_SELECTED_ROOM).toLong(-1L);
		final long lastSelectedOwner = multiPartRequest.getPostParameters().getParameterValue(PARAM_LAST_SELECTED_OWNER).toLong(-1L);
		final long lastSelectedGroup = multiPartRequest.getPostParameters().getParameterValue(PARAM_LAST_SELECTED_GROUP).toLong(-1L);

		startRunnable(() -> convertAll(c, fileItems, uuid, toWb, clean, lastSelectedId, lastSelectedRoom, lastSelectedOwner, lastSelectedGroup));
	}

	private void convertAll(Client c, List<FileItem> files, String uuid, boolean toWb, boolean clean, long lastSelectedId, long lastSelectedRoom, long lastSelectedOwner, long lastSelectedGroup) {
		final BaseFileItem parent = fileDao.get(lastSelectedId);
		final long langId = getLangId(c);
		final long totalSize = files.stream().mapToLong(FileItem::getSize).sum();
		final AtomicInteger progress = new AtomicInteger(0);
		long currentSize = 0;
		for (FileItem curItem : files) {
			long size = curItem.getSize();
			try {
				org.apache.openmeetings.db.entity.file.FileItem f = new org.apache.openmeetings.db.entity.file.FileItem();
				f.setSize(size);
				f.setName(curItem.getName());
				if (parent == null || BaseFileItem.Type.RECORDING == parent.getType()) {
					if (lastSelectedGroup > -1) {
						f.setGroupId(lastSelectedGroup);
					} else if (lastSelectedRoom > -1) {
						f.setRoomId(lastSelectedRoom);
					} else {
						f.setOwnerId(lastSelectedOwner > -1 ? lastSelectedOwner : getUserId());
					}
				} else {
					f.setRoomId(parent.getRoomId());
					f.setOwnerId(parent.getOwnerId());
					f.setGroupId(parent.getGroupId());
					if (parent.getId() != null) {
						f.setParentId(BaseFileItem.Type.FOLDER == parent.getType() ? parent.getId() : parent.getParentId());
					}
				}
				f.setInsertedBy(getUserId());

				ProcessResultList logs = processor.processFile(f, curItem.getInputStream()
						, Optional.<DoubleConsumer>of(part -> sendProgress(c, uuid, progress, progress.get() + (int)(100 * part * size / totalSize))));
				for (ProcessResult res : logs.getJobs()) {
					fileLogDao.add(res.getProcess(), f, res);
				}
				if (logs.hasError()) {
					sendError(c, uuid, Application.getString("convert.errors.file", langId));
				} else {
					WebSocketHelper.sendClient(c, new TextRoomMessage(c.getRoomId(), c, RoomMessage.Type.FILE_TREE_UPDATE
							, new JSONObject() // not necessary for now
									.put("fileId", f.getId())
									.toString()));
					if (toWb) {
						WebSocketHelper.sendClient(c, new TextRoomMessage(c.getRoomId(), c, RoomMessage.Type.WB_PUT_FILE
								, new JSONObject()
										.put("fileId", f.getId())
										.put("clean", clean)
										.toString()));
						clean = false;
					}
				}
			} catch (Exception e) {
				log.error("Unexpected error while processing uploaded file", e);
				sendError(c, uuid, e.getMessage() == null ? "Unexpected error" : e.getMessage());
			} finally {
				try {
					curItem.delete();
				} catch (IOException e) {
					log.error("IOException while deleting FileItem ", e);
				}
			}
			currentSize += size;
			sendProgress(c, uuid, progress, (int)(100 * currentSize / totalSize));
		}
		sendProgress(c, uuid, progress, 100);
	}
}
