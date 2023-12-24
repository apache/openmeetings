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
package org.apache.openmeetings.web.common;

import static org.apache.openmeetings.util.OmFileHelper.EXTENSION_PNG;
import static org.apache.openmeetings.util.OmFileHelper.PROFILE_FILE_NAME;
import static org.apache.openmeetings.util.OmFileHelper.getUploadProfilesUserDir;
import static org.apache.openmeetings.web.util.ProfileImageResourceReference.getUrl;

import java.io.File;
import java.nio.file.Files;

import org.apache.openmeetings.core.converter.ImageConverter;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.util.OmFileHelper;
import org.apache.openmeetings.util.StoredFile;

import jakarta.inject.Inject;

public class UploadableProfileImagePanel extends UploadableImagePanel {
	private static final long serialVersionUID = 1L;
	private Long userId;

	@Inject
	private ImageConverter converter;
	@Inject
	private UserDao userDao;

	public UploadableProfileImagePanel(String id, final Long userId) {
		super(id, false);
		this.userId = userId;
	}

	@Override
	protected void onConfigure() {
		super.onConfigure();
		setVisible(userDao.get(userId) != null);
	}

	@Override
	protected void processImage(StoredFile sf, File f) throws Exception {
		converter.convertImageUserProfile(f, userId);
	}

	@Override
	protected void deleteImage() throws Exception {
		File f = new File(getUploadProfilesUserDir(userId), OmFileHelper.getName(PROFILE_FILE_NAME, EXTENSION_PNG));
		Files.deleteIfExists(f.toPath());
	}

	@Override
	protected String getImageUrl() {
		User u = userDao.get(userId);
		return u == null ? "" : getUrl(getRequestCycle(), u);
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
}
