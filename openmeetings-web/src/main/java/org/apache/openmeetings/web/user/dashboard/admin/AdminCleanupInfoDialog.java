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
package org.apache.openmeetings.web.user.dashboard.admin;

import static org.apache.openmeetings.cli.CleanupHelper.getBackupUnit;
import static org.apache.openmeetings.cli.CleanupHelper.getFileUnit;
import static org.apache.openmeetings.cli.CleanupHelper.getImportUnit;
import static org.apache.openmeetings.cli.CleanupHelper.getProfileUnit;
import static org.apache.openmeetings.cli.CleanupHelper.getRecUnit;
import static org.apache.openmeetings.cli.CleanupHelper.getTempUnit;
import static org.apache.openmeetings.util.OmFileHelper.getHumanSize;
import static org.apache.openmeetings.util.OmFileHelper.getStreamsDir;
import static org.apache.openmeetings.util.OmFileHelper.getUploadDir;
import static org.apache.openmeetings.web.app.Application.getBean;

import org.apache.openmeetings.db.dao.file.FileExplorerItemDao;
import org.apache.openmeetings.db.dao.record.RecordingDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;

import com.googlecode.wicket.jquery.ui.widget.dialog.AbstractDialog;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;

public class AdminCleanupInfoDialog extends AbstractDialog<String> {
	private static final long serialVersionUID = 1L;

	public AdminCleanupInfoDialog(String id) {
		super(id, "");
		add(new CleanupUnitPanel("temp", "dashboard.widget.admin.cleanup.temp", getTempUnit()));
		add(new Label("upload-size", getHumanSize(getUploadDir())));
		add(new CleanupEntityUnitPanel("profile", "dashboard.widget.admin.cleanup.profiles", getProfileUnit(getBean(UserDao.class))));
		add(new CleanupUnitPanel("import", "dashboard.widget.admin.cleanup.import", getImportUnit()));
		add(new CleanupUnitPanel("backup", "dashboard.widget.admin.cleanup.backup", getBackupUnit()));
		add(new CleanupEntityUnitPanel("files", "dashboard.widget.admin.cleanup.files", getFileUnit(getBean(FileExplorerItemDao.class))));
		add(new Label("streams-size", getHumanSize(getStreamsDir())));
		add(new CleanupEntityUnitPanel("final", "dashboard.widget.admin.cleanup.final", getRecUnit(getBean(RecordingDao.class))));
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		setTitle(Model.of(getString("dashboard.widget.admin.cleanup.title")));
	}
	
	@Override
	public void onClose(IPartialPageRequestHandler handler, DialogButton button) {
		// TODO Auto-generated method stub

	}
}
