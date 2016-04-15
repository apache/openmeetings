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

import org.apache.openmeetings.cli.CleanupEntityUnit;
import org.apache.openmeetings.cli.CleanupUnit;
import org.apache.openmeetings.db.dao.file.FileExplorerItemDao;
import org.apache.openmeetings.db.dao.record.RecordingDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;

import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.ui.form.button.ConfirmAjaxButton;
import com.googlecode.wicket.jquery.ui.widget.dialog.AbstractDialog;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;
import com.googlecode.wicket.kendo.ui.panel.KendoFeedbackPanel;

public class AdminCleanupInfoDialog extends AbstractDialog<String> {
	private static final long serialVersionUID = 1L;
	private final CleanupUnitPanel temp;
	private final Label uploadSize;
	private final CleanupEntityUnitPanel profile;
	private final CleanupUnitPanel imp;
	private final CleanupUnitPanel backup;
	private final CleanupEntityUnitPanel files;
	private final Label streamsSize;
	private final CleanupEntityUnitPanel fin;
	private final WebMarkupContainer container = new WebMarkupContainer("container");
	private final KendoFeedbackPanel feedback = new KendoFeedbackPanel("feedback", new Options("button", true));

	public AdminCleanupInfoDialog(String id) {
		super(id, "");
		temp = new CleanupUnitPanel("temp", "dashboard.widget.admin.cleanup.temp", new CleanupUnit());
		uploadSize = new Label("upload-size", "");
		profile = new CleanupEntityUnitPanel("profile", "dashboard.widget.admin.cleanup.profiles", new CleanupEntityUnit());
		imp = new CleanupUnitPanel("import", "dashboard.widget.admin.cleanup.import", new CleanupUnit());
		backup = new CleanupUnitPanel("backup", "dashboard.widget.admin.cleanup.backup", new CleanupUnit());
		files = new CleanupEntityUnitPanel("files", "dashboard.widget.admin.cleanup.files", new CleanupEntityUnit());
		streamsSize = new Label("streams-size", "");
		fin = new CleanupEntityUnitPanel("final", "dashboard.widget.admin.cleanup.final", new CleanupEntityUnit());

		add(feedback.setOutputMarkupId(true));
		add(container.add(temp, uploadSize, profile, imp, backup, files, streamsSize, fin).setOutputMarkupId(true));
		add(new Form<Void>("form") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onInitialize() {
				super.onInitialize();
				add(new ConfirmAjaxButton("cleanup", getString("dashboard.widget.admin.cleanup.cleanup")
						, getString("dashboard.widget.admin.cleanup.cleanup")
						, getString("dashboard.widget.admin.cleanup.warn"))
				{
					private static final long serialVersionUID = 1L;

					@Override
					protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
						cleanup(target);
					}

					@Override
					protected void onError(AjaxRequestTarget target, Form<?> form) {
						target.add(feedback);
					}			
				});
			}
		});
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		setTitle(Model.of(getString("dashboard.widget.admin.cleanup.title")));
	}
	
	@Override
	public void onClose(IPartialPageRequestHandler handler, DialogButton button) {
	}
	
	private void update(AjaxRequestTarget target) {
		temp.setDefaultModelObject(getTempUnit());
		uploadSize.setDefaultModelObject(getHumanSize(getUploadDir()));
		profile.setDefaultModelObject(getProfileUnit(getBean(UserDao.class)));
		imp.setDefaultModelObject(getImportUnit());
		backup.setDefaultModelObject(getBackupUnit());
		files.setDefaultModelObject(getFileUnit(getBean(FileExplorerItemDao.class)));
		streamsSize.setDefaultModelObject(getHumanSize(getStreamsDir()));
		fin.setDefaultModelObject(getRecUnit(getBean(RecordingDao.class)));
		target.add(container);
	}
	
	public void show(AjaxRequestTarget target) {
		update(target);
		open(target);
	}

	public void cleanup(AjaxRequestTarget target) {
		try {
			((CleanupUnit)temp.getDefaultModelObject()).cleanup();
			((CleanupEntityUnit)profile.getDefaultModelObject()).cleanup();
			((CleanupUnit)imp.getDefaultModelObject()).cleanup();;
			((CleanupUnit)backup.getDefaultModelObject()).cleanup();
			((CleanupEntityUnit)files.getDefaultModelObject()).cleanup();;
			((CleanupEntityUnit)fin.getDefaultModelObject()).cleanup();
			update(target);
		} catch (Exception e) {
			error(getString("dashboard.widget.admin.cleanup.error"));
		}
	}
}
