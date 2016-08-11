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
package org.apache.openmeetings.core.data.file;

import static org.apache.openmeetings.util.OmFileHelper.MP4_EXTENSION;
import static org.apache.openmeetings.util.OmFileHelper.JPG_EXTENSION;
import static org.apache.openmeetings.util.OmFileHelper.thumbImagePrefix;
import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.openmeetings.db.dao.file.FileExplorerItemDao;
import org.apache.openmeetings.db.entity.file.FileExplorerItem;
import org.apache.openmeetings.db.entity.file.FileItem.Type;
import org.apache.openmeetings.util.OmFileHelper;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class FileUtils {
	private static final Logger log = Red5LoggerFactory.getLogger(FileProcessor.class, webAppRootKey);

	@Autowired
	private FileExplorerItemDao fileDao;

	public long getSize(List<FileExplorerItem> list) {
		long size = 0;
		for (FileExplorerItem f : list) {
			log.debug("FileExplorerItem fList " + f.getName());
			size += getSize(f);
		}
		return size;
	}
	
	public long getSize(FileExplorerItem file) {
		try {
			long fileSize = 0;

			File base = OmFileHelper.getUploadFilesDir();
			if (Type.Image == file.getType()) {
				String fname = String.format("%s%s", file.getHash(), JPG_EXTENSION);
				File tFile = new File(base, fname);
				if (tFile.exists()) {
					fileSize += tFile.length();
				}

				File thumbFile = new File(base, thumbImagePrefix + fname);
				if (thumbFile.exists()) {
					fileSize += thumbFile.length();
				}
			}

			if (Type.Presentation == file.getType()) {
				File tFolder = new File(base, file.getHash());

				if (tFolder.exists()) {
					fileSize += OmFileHelper.getSize(tFolder);
				}
			}

			if (Type.Video == file.getType()) {
				//FIXME TODO refactor
				File video = new File(OmFileHelper.getStreamsHibernateDir(), "UPLOADFLV_" + file.getId() + MP4_EXTENSION);
				if (video.exists()) {
					fileSize += video.length();
				}
				File thumb = new File(OmFileHelper.getStreamsHibernateDir(), "UPLOADFLV_" + file.getId() + JPG_EXTENSION);
				if (thumb.exists()) {
					fileSize += thumb.length();
				}
			}

			log.debug("calling [1] fileDao.update()");
			for (FileExplorerItem child : fileDao.getByParent(file.getId())) {
				fileSize += getSize(child);
			}

			return fileSize;
		} catch (Exception err) {
			log.error("[getSizeOfDirectoryAndSubs] ", err);
		}
		return 0;
	}

	public String formatDate(Date date) {
		SimpleDateFormat formatter;
		String pattern = "dd/MM/yy H:mm:ss";
		Locale locale = new Locale("en", "US");
		formatter = new SimpleDateFormat(pattern, locale);
		return formatter.format(date);
	}
}
