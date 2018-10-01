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

import static org.apache.openmeetings.db.dto.room.Whiteboard.ATTR_FILE_ID;
import static org.apache.openmeetings.db.dto.room.Whiteboard.ATTR_SLIDE;
import static org.apache.openmeetings.util.OmFileHelper.EXTENSION_PNG;
import static org.apache.openmeetings.util.OmFileHelper.MP4_MIME_TYPE;
import static org.apache.openmeetings.util.OmFileHelper.PNG_MIME_TYPE;
import static org.apache.openmeetings.util.OmFileHelper.getImagesDir;
import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.WebSession.getUserId;

import java.io.File;
import java.util.Map.Entry;

import org.apache.openmeetings.db.dao.file.FileItemDao;
import org.apache.openmeetings.db.dao.user.GroupUserDao;
import org.apache.openmeetings.db.dto.room.Whiteboard;
import org.apache.openmeetings.db.dto.room.Whiteboards;
import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.db.entity.file.FileItem;
import org.apache.openmeetings.web.app.ClientManager;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.app.WhiteboardManager;
import org.apache.openmeetings.web.util.FileItemResourceReference;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.IResource.Attributes;
import org.apache.wicket.util.string.StringValue;
import org.apache.wicket.util.string.Strings;

import com.github.openjson.JSONObject;

public class RoomResourceReference extends FileItemResourceReference<FileItem> {
	private static final long serialVersionUID = 1L;
	private static final String DEFAULT_NAME = "wb-room-file";

	public RoomResourceReference() {
		super(DEFAULT_NAME);
	}

	public RoomResourceReference(String name) {
		super(name);
	}

	@Override
	protected String getMimeType(FileItem r) {
		String mime;
		switch (r.getType()) {
			case WmlFile:
				mime = "application/xml";
				break;
			case Image:
				mime = PNG_MIME_TYPE;
				break;
			case Presentation:
				mime = PNG_MIME_TYPE;
				break;
			case Video:
				mime = MP4_MIME_TYPE;
				break;
			default:
				throw new RuntimeException("Not supported");
		}
		return r.isDeleted() ? PNG_MIME_TYPE : mime;
	}

	@Override
	protected FileItem getFileItem(Attributes attr) {
		PageParameters params = attr.getParameters();
		StringValue _id = params.get("id");
		String uid = params.get("uid").toString();
		Long id = null;
		try {
			id = _id.toOptionalLong();
		} catch (NumberFormatException e) {
			//no-op expected
		}
		WebSession ws = WebSession.get();
		Client c = getBean(ClientManager.class).get(uid);
		if (id == null || !ws.isSignedIn() || c == null) {
			return null;
		}
		FileItem f = (FileItem)getBean(FileItemDao.class).getAny(id);
		if (f == null) {
			return null;
		}
		String ruid = params.get("ruid").toString();
		String wuid = params.get("wuid").toString();
		if (c.getRoom() != null) {
			Whiteboards wbs = getBean(WhiteboardManager.class).get(c.getRoom().getId());
			if (!Strings.isEmpty(wuid) && !Strings.isEmpty(ruid) && ruid.equals(wbs.getUid())) {
				for (Entry<Long, Whiteboard> e : wbs.getWhiteboards().entrySet()) {
					JSONObject file = e.getValue().get(wuid);
					if (file != null && f.getId().equals(file.optLong(ATTR_FILE_ID))) {
						return f; // item IS on WB
					}
				}
			}
		}
		if (f.getGroupId() != null && getBean(GroupUserDao.class).isUserInGroup(f.getGroupId(), getUserId())) {
			return f;
		}
		return null;
	}

	protected File getFile(FileItem f, String ext) {
		File file = f.getFile(ext);
		if (file == null || !file.exists()) {
			file = new File(getImagesDir(), String.format("deleted.%s", EXTENSION_PNG));
		}
		return file;
	}

	@Override
	protected File getFile(FileItem f, Attributes attr) {
		String ext = f.getType() == FileItem.Type.Presentation
				? attr.getParameters().get(ATTR_SLIDE).toString() : null;
		return getFile(f, ext);
	}

	@Override
	protected String getFileName(FileItem f) {
		return f.getFileName(null);
	}
}
