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
package org.apache.openmeetings.cli;

import java.io.File;

import org.apache.commons.transaction.util.FileHelper;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.util.OmFileHelper;

public class CleanupHelper {
	public static CleanupUnit getTempUnit() {
		return new CleanupUnit(OmFileHelper.getUploadTempDir()) {
			@Override
			public void cleanup() {
				for (File f : getParent().listFiles()) {
					FileHelper.removeRec(f);
				}
			}
		};
	}

	public static CleanupEntityUnit getProfileUnit(final UserDao udao) {
		return new CleanupEntityUnit(OmFileHelper.getUploadProfilesDir()) {
			@Override
			public void cleanup() {
				for (File i : invalid) {
					FileHelper.removeRec(i);
				}
				for (File i : deleted) {
					FileHelper.removeRec(i);
				}
			}
			
			@Override
			public void fill() {
				for (File profile : getParent().listFiles()) {
					long userId = getUserIdByProfile(profile.getName());
					User u = udao.get(userId);
					if (profile.isFile() || userId < 0 || u == null) {
						invalid.add(profile);
					} else if (u.isDeleted()) {
						deleted.add(profile);
					}
				}
				for (User u : udao.getAllBackupUsers()) {
					if (!u.isDeleted() && u.getPictureuri() != null && !new File(OmFileHelper.getUploadProfilesUserDir(u.getId()), u.getPictureuri()).exists()) {
						missing++;
					}
				}
			}
		};
	}

	private static long getUserIdByProfile(String name) {
		long result = -1;
		if (name.startsWith(OmFileHelper.profilesPrefix)) {
			try {
				result = Long.parseLong(name.substring(OmFileHelper.profilesPrefix.length()));
			} catch (Exception e) {
				//noop
			}
		}
		return result;
	}
}
