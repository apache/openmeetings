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

import static org.apache.openmeetings.db.dto.room.Whiteboard.ATTR_SLIDE;
import static org.apache.openmeetings.web.app.WebSession.getUserId;
import static org.apache.openmeetings.util.OmFileHelper.EXTENSION_PNG;
import static org.apache.openmeetings.util.OmFileHelper.MP4_MIME_TYPE;
import static org.apache.openmeetings.util.OmFileHelper.PNG_MIME_TYPE;
import static org.apache.openmeetings.util.OmFileHelper.getImagesDir;

import java.io.File;

import org.apache.openmeetings.db.dao.file.FileItemDao;
import org.apache.openmeetings.db.dao.user.GroupUserDao;
import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.db.entity.file.BaseFileItem;
import org.apache.openmeetings.db.entity.file.FileItem;
import org.apache.openmeetings.web.app.ClientManager;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.util.FileItemResourceReference;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.IResource.Attributes;

import org.apache.wicket.util.string.StringValue;

import jakarta.inject.Inject;

public class RoomResourceReference extends FileItemResourceReference<FileItem> {
	private static final long serialVersionUID = 1L;
	private static final String DEFAULT_NAME = "wb-room-file";

	@Inject
	private ClientManager cm;
	@Inject
	private FileItemDao fileDao;
	@Inject
	private GroupUserDao groupUserDao;

	public RoomResourceReference() {
		this(DEFAULT_NAME);
	}

	public RoomResourceReference(String name) {
		super(name);
		Injector.get().inject(this);
	}

	@Override
	protected String getMimeType(FileItem r) {
		String mime;
		switch (r.getType()) {
			case WML_FILE:
				mime = "application/json";
				break;
			case IMAGE:
				mime = PNG_MIME_TYPE;
				break;
			case PRESENTATION:
				mime = PNG_MIME_TYPE;
				break;
			case VIDEO:
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
		StringValue idStr = params.get("id");
		String uid = params.get("uid").toString();
		Long id = null;
		try {
			id = idStr.toOptionalLong();
		} catch (NumberFormatException e) {
			//no-op expected
		}
		WebSession ws = WebSession.get();
		Client c = cm.get(uid);
		if (id == null || !ws.isSignedIn() || c == null) {
			return null;
		}
		FileItem f = (FileItem)fileDao.getAny(id);
		if (f == null) {
			return null;
		}
		String ruid = params.get("ruid").toString();
		String wuid = params.get("wuid").toString();
		if (isAtWb(c, ruid, wuid, f.getId())) {
			return f; // item IS on WB
		}
		if (f.getGroupId() != null && groupUserDao.isUserInGroup(f.getGroupId(), getUserId())) {
			return f;
		}
		return null;
	}

	protected File getFile(FileItem f, String ext) {
		File file = f.getFile(ext);
		if (file == null || !file.exists()) {
			file = new File(getImagesDir(), "deleted." + EXTENSION_PNG);
		}
		return file;
	}

	@Override
	protected File getFile(FileItem f, Attributes attr) {
		String ext = f.getType() == BaseFileItem.Type.PRESENTATION
				? attr.getParameters().get(ATTR_SLIDE).toString() : null;
		return getFile(f, ext);
	}

	@Override
	protected String getFileName(FileItem f) {
		return f.getFileName(null);
	}
}
