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

import static org.apache.openmeetings.util.OmFileHelper.EXTENSION_PNG;
import static org.apache.openmeetings.util.OmFileHelper.PNG_MIME_TYPE;

import java.io.File;

import org.apache.openmeetings.db.entity.file.BaseFileItem;
import org.apache.openmeetings.db.entity.file.FileItem;
import org.apache.wicket.request.resource.IResource.Attributes;

public class RoomPreviewResourceReference extends RoomResourceReference {
	private static final long serialVersionUID = 1L;
	private static final String DEFAULT_NAME = "wb-room-file-preview";

	public RoomPreviewResourceReference() {
		super(DEFAULT_NAME);
	}

	public RoomPreviewResourceReference(String name) {
		super(name);
	}

	@Override
	protected String getMimeType(FileItem r) {
		return PNG_MIME_TYPE;
	}

	@Override
	protected FileItem getFileItem(Attributes attr) {
		FileItem f = super.getFileItem(attr);
		if (f != null && BaseFileItem.Type.VIDEO == f.getType()) {
			return f;
		}
		return null;
	}

	@Override
	protected File getFile(FileItem f, Attributes attr) {
		return getFile(f, EXTENSION_PNG);
	}

	@Override
	protected String getFileName(FileItem f) {
		return f.getFileName(EXTENSION_PNG);
	}
}
