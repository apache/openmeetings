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

import static java.util.UUID.randomUUID;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getMaxUploadSize;
import static org.apache.openmeetings.web.app.WebSession.getUserId;
import static org.apache.openmeetings.web.util.ThreadHelper.startRunnable;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.DoubleConsumer;

import javax.ws.rs.core.MediaType;

import org.apache.commons.fileupload.FileItem;
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
import org.apache.openmeetings.web.app.ClientManager;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.protocol.http.servlet.MultipartServletWebRequest;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.request.resource.AbstractResource;
import org.apache.wicket.request.resource.AbstractResource.ResourceResponse;
import org.apache.wicket.request.resource.AbstractResource.WriteCallback;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.IResource.Attributes;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.lang.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.openjson.JSONObject;

public class RoomFileUploadResourceReference extends ResourceReference {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(RoomFileUploadResourceReference.class);
	private static final String PARAM_FILE_NAME = "room-upload-file";
	private static final String PARAM_TO_WB_NAME = "room-upload-to-wb";
	private static final String PARAM_CLEAN_NAME = "room-upload-clean";
	private static final String PARAM_SID_NAME = "room-upload-sid";
	private static final String PARAM_LAST_SELECTED_NAME = "room-upload-last-selected";
	private enum Status {
		SUCCESS
		, PROGRESS
		, ERROR
	}

	@SpringBean
	private ClientManager cm;
	@SpringBean
	private FileProcessor processor;
	@SpringBean
	private FileItemLogDao fileLogDao;
	@SpringBean
	private FileItemDao fileDao;

	public RoomFileUploadResourceReference() {
		super(RoomFileUploadResourceReference.class, "room-file-upload");
		Injector.get().inject(this);
	}

	@Override
	public IResource getResource() {
		return new AbstractResource() {
			private static final long serialVersionUID = 1L;

			@Override
			protected ResourceResponse newResourceResponse(Attributes attributes) {
				final ResourceResponse response = new ResourceResponse();
				final ServletWebRequest webRequest = (ServletWebRequest) attributes.getRequest();
				try {
					MultipartServletWebRequest multiPartRequest = webRequest.newMultipartWebRequest(Bytes.bytes(getMaxUploadSize()), "ignored");
					multiPartRequest.parseFileParts();

					String sid = multiPartRequest.getPostParameters().getParameterValue(PARAM_SID_NAME).toString();
					Client c = cm.getBySid(sid);
					final long langId = getLangId(c);
					if (isUploadAllowed(c)) {
						Map<String, List<FileItem>> files = multiPartRequest.getFiles();
						final List<FileItem> fileItems = files.get(PARAM_FILE_NAME);

						final boolean toWb = multiPartRequest.getPostParameters().getParameterValue(PARAM_TO_WB_NAME).toBoolean(false);
						final boolean clean = multiPartRequest.getPostParameters().getParameterValue(PARAM_CLEAN_NAME).toBoolean(false);
						final long lastSelected = multiPartRequest.getPostParameters().getParameterValue(PARAM_LAST_SELECTED_NAME).toLong(-1L);
						final String uuid = randomUUID().toString();
						startRunnable(() -> convertAll(c, fileItems, uuid, toWb, clean, lastSelected));

						prepareResponse(response, Status.SUCCESS, uuid, Application.getString("54", langId));
					} else {
						prepareResponse(response, Status.ERROR, null, Application.getString("access.denied.header", langId));
					}
				} catch (Exception e) {
					log.error("An error occurred while uploading a file", e);
					prepareResponse(response, Status.ERROR, null, e.getMessage());
				}
				return response;
			}
		};
	}

	private static long getLangId(Client c) {
		return c == null || c.getUser() == null ? 1L : c.getUser().getLanguageId();
	}

	private static void prepareResponse(ResourceResponse response, Status status, String uuid, String msg) {
		response.setContentType(MediaType.APPLICATION_JSON);
		response.setWriteCallback(new WriteCallback() {
			@Override
			public void writeData(Attributes attributes) throws IOException {
				attributes.getResponse().write(new JSONObject()
						.put("status", status.name())
						.put("message", msg)
						.put("uuid", uuid)
						.toString());
			}
		});
	}

	private static boolean isUploadAllowed(Client c) {
		if (c == null || c.getRoom() == null || Room.Type.INTERVIEW == c.getRoom().getType()) {
			return false;
		}
		Room r = c.getRoom();
		return !r.isHidden(RoomElement.FILES) && c.hasRight(Right.PRESENTER);
	}

	private void convertAll(Client c, List<FileItem> files, String uuid, boolean toWb, boolean clean, long lastSelected) {
		final BaseFileItem parent = fileDao.get(lastSelected);
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
					f.setOwnerId(getUserId());
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
				curItem.delete();
			}
			currentSize += size;
			sendProgress(c, uuid, progress, (int)(100 * currentSize / totalSize));
		}
		sendProgress(c, uuid, progress, 100);
	}

	private JSONObject getBaseMessage(String uuid) {
		return new JSONObject()
				.put("uuid", uuid)
				.put("type", "room-upload");
	}
	private void sendError(Client c, String uuid, String msg) {
		WebSocketHelper.sendClient(c, getBaseMessage(uuid)
				.put("status", Status.ERROR.name())
				.put("message", msg));
	}

	private void sendProgress(Client c, String uuid, AtomicInteger progress, int cur) {
		if (cur > progress.get()) {
			progress.set(cur);
			WebSocketHelper.sendClient(c, getBaseMessage(uuid)
					.put("status", Status.PROGRESS.name())
					.put("progress", cur));
		}
	}
}
