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

import static org.apache.openmeetings.util.OmFileHelper.EXTENSION_JPG;
import static org.apache.openmeetings.util.OmFileHelper.EXTENSION_SWF;
import static org.apache.openmeetings.util.OmFileHelper.JPG_MIME_TYPE;
import static org.apache.openmeetings.util.OmFileHelper.MP4_MIME_TYPE;
import static org.apache.openmeetings.util.OmFileHelper.getOmHome;
import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.Application.getOnlineClient;

import java.io.File;
import java.util.Map.Entry;

import org.apache.directory.api.util.Strings;
import org.apache.openmeetings.core.data.whiteboard.WhiteboardCache;
import org.apache.openmeetings.db.dao.file.FileExplorerItemDao;
import org.apache.openmeetings.db.dto.room.Whiteboard;
import org.apache.openmeetings.db.dto.room.Whiteboards;
import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.db.entity.file.FileExplorerItem;
import org.apache.openmeetings.db.entity.file.FileItem;
import org.apache.openmeetings.db.entity.file.FileItem.Type;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.util.FileItemResourceReference;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.IResource.Attributes;
import org.apache.wicket.util.string.StringValue;

public class RoomResourceReference extends FileItemResourceReference<FileExplorerItem> {
	private static final long serialVersionUID = 1L;
	public static final String DEFAULT_NAME = "wb-room-file";
	private boolean preview = false;

	public RoomResourceReference() {
		super(DEFAULT_NAME);
	}

	public RoomResourceReference(String name) {
		super(name);
	}

	@Override
	protected String getMimeType(FileExplorerItem r) {
		String mime = null;
		switch (r.getType()) {
			case WmlFile:
				mime = "application/xml";
				break;
			case Image:
				mime = JPG_MIME_TYPE;
				break;
			case Presentation:
				mime = "application/x-shockwave-flash";
				break;
			case Video:
				mime = preview ? JPG_MIME_TYPE : MP4_MIME_TYPE;
				break;
			default:
				throw new RuntimeException("Not supported");
		}
		return mime;
	}

	@Override
	protected FileExplorerItem getFileItem(Attributes attributes) {
		PageParameters params = attributes.getParameters();
		StringValue _id = params.get("id");
		StringValue _preview = params.get("preview");
		preview = _preview.toBoolean(false);
		String uid = params.get("uid").toString();
		Long id = null;
		try {
			id = _id.toOptionalLong();
		} catch (NumberFormatException e) {
			//no-op expected
		}
		WebSession ws = WebSession.get();
		Client c = getOnlineClient(uid);
		if (id == null || !ws.isSignedIn() || c == null) {
			return null;
		}
		FileExplorerItem f = getBean(FileExplorerItemDao.class).get(id);
		String ruid = params.get("ruid").toString();
		Whiteboards wbs = getBean(WhiteboardCache.class).get(c.getRoomId());
		if (!Strings.isEmpty(ruid) && ruid.equals(wbs.getUid())) {
			for (Entry<Long, Whiteboard> e : wbs.getWhiteboards().entrySet()) {
				if (e.getValue().getRoomItems().containsKey(f.getHash())) {
					return f; // item IS on WB
				}
			}
		}
		return null;
	}

	protected File getFile(FileExplorerItem f, String ext) {
		File file = f.getFile(ext);
		if (file == null || !file.exists()) {
			file = new File(new File(getOmHome(), "default"), String.format("deleted.%s"
					, FileItem.Type.Image == f.getType() ? EXTENSION_JPG : EXTENSION_SWF));
		}
		return file;
	}

	@Override
	protected File getFile(FileExplorerItem f) {
		return getFile(f, Type.Video == f.getType() && preview ? EXTENSION_JPG : null);
	}

	@Override
	protected String getFileName(FileExplorerItem f) {
		return f.getFileName(preview ? EXTENSION_JPG : null);
	}
}
