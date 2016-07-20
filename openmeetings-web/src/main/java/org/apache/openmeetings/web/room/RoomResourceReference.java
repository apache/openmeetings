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

import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.Application.getOnlineClient;

import java.io.File;

import org.apache.openmeetings.db.dao.file.FileExplorerItemDao;
import org.apache.openmeetings.db.entity.file.FileExplorerItem;
import org.apache.openmeetings.util.OmFileHelper;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.util.FileItemResourceReference;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.IResource.Attributes;
import org.apache.wicket.util.string.StringValue;
import org.apache.wicket.util.string.Strings;

public class RoomResourceReference extends FileItemResourceReference<FileExplorerItem> {
	private static final long serialVersionUID = 1L;
	public static final String DEFAULT_NAME = "wb-room-file";

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
			case Image:
				mime = "image/jpeg";
				break;
			case Presentation:
				mime = "application/x-shockwave-flash";
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
		String uid = params.get("uid").toString();
		Long id = null;
		try {
			id = _id.toOptionalLong();
		} catch (NumberFormatException e) {
			//no-op expected
		}
		WebSession ws = WebSession.get();
		if (id != null && ws.isSignedIn() && !Strings.isEmpty(uid) && getOnlineClient(uid) != null) {
			//FIXME TODO ADDITIONALLY CHECK Rights !! and room !!
			return getBean(FileExplorerItemDao.class).get(id);
		}
		return null;
	}

	protected File getFile(FileExplorerItem r, String ext) {
		File result = null;
		switch (r.getType()) {
			case Image: {
				result = new File(OmFileHelper.getUploadFilesDir(), String.format("%s.%s", r.getHash(), ext == null ? "jpg" : ext));
			}
				break;
			case Presentation: {
				File d = new File(OmFileHelper.getUploadFilesDir(), r.getHash());
				result = new File(d, String.format("%s.%s", r.getHash(), ext == null ? "swf" : ext));
			}
				break;
			default:
				throw new RuntimeException("Not supported");
		}
		return result;
	}

	@Override
	protected File getFile(FileExplorerItem r) {
		return getFile(r, null);
	}
	
	@Override
	protected String getFileName(FileExplorerItem r) {
		// TODO Auto-generated method stub
		return null;
	}
}
