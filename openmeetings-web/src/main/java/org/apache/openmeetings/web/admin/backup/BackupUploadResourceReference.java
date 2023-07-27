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
package org.apache.openmeetings.web.admin.backup;

import static org.apache.openmeetings.util.OpenmeetingsVariables.getApplicationName;
import static org.apache.openmeetings.web.util.ThreadHelper.startRunnable;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.fileupload2.core.FileItem;
import org.apache.openmeetings.backup.BackupImport;
import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.web.common.upload.UploadResourceReference;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.protocol.http.servlet.MultipartServletWebRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.inject.Inject;

public class BackupUploadResourceReference extends UploadResourceReference {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(BackupUploadResourceReference.class);

	@Inject
	private BackupImport backupImport;

	public BackupUploadResourceReference() {
		super(BackupUploadResourceReference.class, "backup-file-upload");
		Injector.get().inject(this);
	}

	@Override
	protected boolean isUploadAllowed(Client c) {
		return Optional.ofNullable(c)
				.map(Client::getUser)
				.map(User::getRights)
				.map(rights -> rights.contains(User.Right.ADMIN) || rights.contains(User.Right.ADMIN_BACKUP))
				.orElse(false);
	}

	@Override
	protected void processFiles(Client c, List<FileItem> fileItems, String uuid, MultipartServletWebRequest multiPartRequest) {
		startRunnable(() -> {
			final AtomicInteger lastProgress = new AtomicInteger(0);
			final AtomicInteger progress = new AtomicInteger(0);
			if (fileItems.isEmpty()) {
				sendError(c, uuid, "File is empty");
				return;
			}
			Timer timer = new Timer();
			try {
				FileItem fileItem = fileItems.get(0);
				if (fileItem.getInputStream() == null) {
					sendError(c, uuid, "File is empty");
					return;
				}

				timer.schedule(new TimerTask() {
					@Override
					public void run() {
						sendProgress(c, uuid, lastProgress, progress.get());
					}
				}, 0, 1000);
				backupImport.performImport(fileItem.getInputStream(), progress);
			} catch (Exception e) {
				log.error("Exception on panel backup download ", e);
				sendError(c, uuid, e.getMessage() == null ? "Unexpected error" : e.getMessage());
			} finally {
				fileItems.forEach(fi -> {
					try {
						fi.delete();
					} catch (IOException e) {
						log.error("IOException while deleting FileItem ", e);
					}
				});
				timer.cancel();
			}
			sendProgress(c, uuid, lastProgress, 100);
		}, getApplicationName() + " - Restore");
	}
}
