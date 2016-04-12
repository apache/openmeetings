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

import static org.apache.openmeetings.util.OmFileHelper.FLV_EXTENSION;
import static org.apache.openmeetings.util.OmFileHelper.recordingFileName;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.transaction.util.FileHelper;
import org.apache.openmeetings.db.dao.file.FileExplorerItemDao;
import org.apache.openmeetings.db.dao.record.RecordingDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.file.FileExplorerItem;
import org.apache.openmeetings.db.entity.record.Recording;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.util.OmFileHelper;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class CleanupHelper {
	private static final Logger log = Red5LoggerFactory.getLogger(CleanupHelper.class);
	private static File hibernateDir = OmFileHelper.getStreamsHibernateDir();
	
	public static CleanupUnit getTempUnit() {
		return new CleanupUnit(OmFileHelper.getUploadTempDir());
	}

	public static CleanupEntityUnit getProfileUnit(final UserDao udao) {
		File parent = OmFileHelper.getUploadProfilesDir();
		List<File> invalid = new ArrayList<>();
		List<File> deleted = new ArrayList<>();
		int missing = 0;
		for (File profile : list(parent, null)) {
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
		return new CleanupEntityUnit(parent, invalid, deleted, missing);
	}

	public static CleanupUnit getImportUnit() {
		return new CleanupUnit(OmFileHelper.getUploadImportDir());
	}

	public static CleanupUnit getBackupUnit() {
		return new CleanupUnit(OmFileHelper.getUploadBackupDir());
	}

	public static CleanupEntityUnit getFileUnit(final FileExplorerItemDao fileDao) {
		File parent = OmFileHelper.getUploadFilesDir();
		List<File> invalid = new ArrayList<>();
		List<File> deleted = new ArrayList<>();
		int missing = 0;
		for (File f : list(parent, null)) {
			FileExplorerItem item = fileDao.getByHash(f.getName()); // TODO probable extension should be stripped
			if (item == null) {
				invalid.add(f);
			} else if (item.isDeleted()) {
				deleted.add(f);
			}
		}
		//TODO WML_DIR should also be checked
		for (FileExplorerItem item : fileDao.get()) {
			if (!item.isDeleted() && item.getHash() != null && !new File(parent, item.getHash()).exists()) {
				missing++;
			}
		}
		return new CleanupEntityUnit(parent, invalid, deleted, missing);
	}

	public static CleanupEntityUnit getRecUnit(final RecordingDao recordDao) {
		File parent = OmFileHelper.getStreamsDir();
		List<File> invalid = new ArrayList<>();
		List<File> deleted = new ArrayList<>();
		int missing = 0;
		for (File f : list(hibernateDir, new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.startsWith(recordingFileName) && name.endsWith(FLV_EXTENSION);
			}
		})) {
			if (!f.isFile()) {
				log.warn("Recording found is not a file: " + f);
				continue;
			}
			Long id = Long.valueOf(f.getName().substring(recordingFileName.length(), f.getName().length() - FLV_EXTENSION.length()));
			Recording item = recordDao.get(id);
			if (item == null) {
				add(invalid, id);
			} else if (item.isDeleted()) {
				add(deleted, id);
			}
		}
		for (Recording item : recordDao.get()) {
			if (!item.isDeleted() && item.getHash() != null && list(item.getId()).length == 0) {
				missing++;
			}
		}
		return new CleanupEntityUnit(parent, invalid, deleted, missing) {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void cleanup() throws IOException {
				String hiberPath = hibernateDir.getCanonicalPath();
				for (File f : list(getParent(), null)) {
					if (!f.getCanonicalPath().equals(hiberPath)) {
						FileHelper.removeRec(f);
					}
				}
				super.cleanup();
			}
		};
	}

	private static File[] list(File f, FilenameFilter ff) {
		File[] l = ff == null ? f.listFiles() : f.listFiles(ff);
		return l == null ? new File[0] : l;
	}
	
	private static File[] list(final Long id) {
		return list(hibernateDir, new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.startsWith(recordingFileName + id);
			}
		});
	}
	
	private static void add(List<File> list, final Long id) {
		for (File f : list(id)) {
			list.add(f);
		}
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
