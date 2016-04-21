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

public abstract class RoomResourceReference extends FileItemResourceReference<FileExplorerItem> {
	private static final long serialVersionUID = 1L;

	public RoomResourceReference(String name) {
		super(name);
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
			//TODO ADDITIONALLY CHECK Rights !! and room !!
			return getBean(FileExplorerItemDao.class).get(id);
		}
		return null;
	}

	protected File getFile(FileExplorerItem r, String ext) {
		File d = new File(OmFileHelper.getUploadFilesDir(), r.getHash());
		return new File(d, String.format("%s.%s", r.getHash(), ext));
	}
}
