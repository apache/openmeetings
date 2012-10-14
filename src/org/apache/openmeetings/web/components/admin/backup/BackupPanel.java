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
package org.apache.openmeetings.web.components.admin.backup;

import org.apache.openmeetings.data.basic.dao.ConfigurationDaoImpl;
import org.apache.openmeetings.utils.ImportHelper;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.components.admin.AdminPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.form.upload.UploadProgressBar;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.lang.Bytes;

/**
 * Panel component to manage Backup Import/Export
 * 
 * @author swagner
 * 
 */
public class BackupPanel extends AdminPanel {

	private static final long serialVersionUID = -1L;
	
	// Create feedback panels
	final FeedbackPanel uploadFeedback;

	/**
	 * Form to handle upload files
	 * 
	 * @author swagner
	 * 
	 */
	private class BackupForm extends Form<Void> {

		private static final long serialVersionUID = 1L;

		FileUploadField fileUploadField;

		public BackupForm(String id) {
			super(id);

			// set this form to multipart mode (allways needed for uploads!)
			setMultiPart(true);

			// Add one file input field
			fileUploadField = new FileUploadField("fileInput");
			add(fileUploadField);

			CheckBox includeFilesInBackup = new CheckBox(
					"includeFilesInBackup", Model.of(true));
			add(includeFilesInBackup);

			// Set maximum size controlled by configuration
			setMaxSize(Bytes.bytes(ImportHelper.getMaxUploadSize(Application
					.getBean(ConfigurationDaoImpl.class))));
			
			// add an download button
			add(new AjaxButton("ajax-backup-download-button", this) {
				private static final long serialVersionUID = 839803820502260006L;

				@Override
				protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
					// repaint the feedback panel so that it is hidden
					target.add(uploadFeedback);

				}

				@Override
				protected void onError(AjaxRequestTarget target, Form<?> form) {
					// repaint the feedback panel so errors are shown
					target.add(uploadFeedback);
				}
			});

			// add an upload button
			add(new AjaxButton("ajax-backup-upload-button", this) {
				private static final long serialVersionUID = 839803820502260006L;

				@Override
				protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
					// repaint the feedback panel so that it is hidden
					target.add(uploadFeedback);

				}

				@Override
				protected void onError(AjaxRequestTarget target, Form<?> form) {
					// repaint the feedback panel so errors are shown
					target.add(uploadFeedback);
				}
			});
		}

	}

	public BackupPanel(String id) {
		super(id);

		// Create feedback panels
		uploadFeedback = new FeedbackPanel("uploadFeedback");
		// Set Id so that it can be replaced dynamically
		uploadFeedback.setOutputMarkupId(true);
		add(uploadFeedback);

		BackupForm backupForm = new BackupForm("backupUpload");

		backupForm.add(new UploadProgressBar("progress", backupForm,
				backupForm.fileUploadField));

		add(backupForm);
	}
}
