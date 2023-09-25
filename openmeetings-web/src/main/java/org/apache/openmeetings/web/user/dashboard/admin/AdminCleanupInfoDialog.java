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
import static org.apache.openmeetings.web.common.confirmation.ConfirmationHelper.newOkCancelDangerConfirm;
import static org.apache.openmeetings.util.OmFileHelper.getHumanSize;
import static org.apache.openmeetings.util.OmFileHelper.getStreamsDir;
import static org.apache.openmeetings.util.OmFileHelper.getUploadDir;

import org.apache.openmeetings.cli.CleanupEntityUnit;
import org.apache.openmeetings.cli.CleanupUnit;
import org.apache.openmeetings.db.dao.file.FileItemDao;
import org.apache.openmeetings.db.dao.record.RecordingDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.web.common.OmModalCloseButton;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.ResourceModel;


import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.spinner.SpinnerAjaxButton;
import jakarta.inject.Inject;

public class AdminCleanupInfoDialog extends Modal<String> {
	private static final long serialVersionUID = 1L;
	private Label uploadSize;
	private CleanupEntityUnitPanel profile;
	private CleanupUnitPanel imp;
	private CleanupUnitPanel backup;
	private CleanupEntityUnitPanel files;
	private Label streamsSize;
	private CleanupEntityUnitPanel fin;
	private final WebMarkupContainer container = new WebMarkupContainer("container");
	private final NotificationPanel feedback = new NotificationPanel("feedback");

	@Inject
	private UserDao userDao;
	@Inject
	private FileItemDao fileDao;
	@Inject
	private RecordingDao recDao;

	public AdminCleanupInfoDialog(String id) {
		super(id);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		header(new ResourceModel("dashboard.widget.admin.cleanup.title"));
		setCloseOnEscapeKey(true);
		addButton(OmModalCloseButton.of("54"));

		uploadSize = new Label("upload-size", "");
		profile = new CleanupEntityUnitPanel("profile", "dashboard.widget.admin.cleanup.profiles", new CleanupEntityUnit());
		imp = new CleanupUnitPanel("import", "dashboard.widget.admin.cleanup.import", new CleanupUnit());
		backup = new CleanupUnitPanel("backup", "dashboard.widget.admin.cleanup.backup", new CleanupUnit());
		files = new CleanupEntityUnitPanel("files", "dashboard.widget.admin.cleanup.files", new CleanupEntityUnit());
		streamsSize = new Label("streams-size", "");
		fin = new CleanupEntityUnitPanel("final", "dashboard.widget.admin.cleanup.final", new CleanupEntityUnit());

		add(feedback.setOutputMarkupId(true));
		add(container.add(uploadSize, profile, imp, backup, files, streamsSize, fin).setOutputMarkupId(true));
		add(new Form<Void>("form") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onInitialize() {
				super.onInitialize();
				SpinnerAjaxButton cleanup = new SpinnerAjaxButton("cleanup", new ResourceModel("dashboard.widget.admin.cleanup.cleanup"), this, Buttons.Type.Outline_Danger) {
					private static final long serialVersionUID = 1L;

					@Override
					protected void onSubmit(AjaxRequestTarget target) {
						cleanup(target);
					}

					@Override
					protected void onError(AjaxRequestTarget target) {
						target.add(feedback);
					}
				};
				add(cleanup.add(newOkCancelDangerConfirm(this, getString("dashboard.widget.admin.cleanup.warn"))));
			}
		});
	}

	private void update(IPartialPageRequestHandler target) {
		uploadSize.setDefaultModelObject(getHumanSize(getUploadDir()));
		profile.setDefaultModelObject(getProfileUnit(userDao));
		imp.setDefaultModelObject(getImportUnit());
		backup.setDefaultModelObject(getBackupUnit());
		files.setDefaultModelObject(getFileUnit(fileDao));
		streamsSize.setDefaultModelObject(getHumanSize(getStreamsDir()));
		fin.setDefaultModelObject(getRecUnit(recDao));
		target.add(container);
	}

	@Override
	public Modal<String> show(final IPartialPageRequestHandler target) {
		update(target);
		return super.show(target);
	}

	public void cleanup(AjaxRequestTarget target) {
		try {
			((CleanupEntityUnit)profile.getDefaultModelObject()).cleanup();
			((CleanupUnit)imp.getDefaultModelObject()).cleanup();
			((CleanupUnit)backup.getDefaultModelObject()).cleanup();
			((CleanupEntityUnit)files.getDefaultModelObject()).cleanup();
			((CleanupEntityUnit)fin.getDefaultModelObject()).cleanup();
			update(target);
		} catch (Exception e) {
			error(getString("dashboard.widget.admin.cleanup.error"));
		}
	}
}
