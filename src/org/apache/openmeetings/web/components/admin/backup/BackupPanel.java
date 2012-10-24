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

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.data.basic.dao.ConfigurationDaoImpl;
import org.apache.openmeetings.servlet.outputhandler.BackupExport;
import org.apache.openmeetings.servlet.outputhandler.BackupImportController;
import org.apache.openmeetings.utils.ImportHelper;
import org.apache.openmeetings.utils.OmFileHelper;
import org.apache.openmeetings.utils.math.CalendarPatterns;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.components.admin.AdminPanel;
import org.apache.openmeetings.web.util.AjaxDownload;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.form.upload.UploadProgressBar;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.request.resource.ContentDisposition;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.util.resource.FileResourceStream;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

/**
 * Panel component to manage Backup Import/Export
 * 
 * @author swagner
 * 
 */
public class BackupPanel extends AdminPanel {

	private static final Logger log = Red5LoggerFactory.getLogger(
			BackupPanel.class, OpenmeetingsVariables.webAppRootKey);

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
		CheckBox includeFilesInBackup;

		public BackupForm(String id) {
			super(id);

			// set this form to multipart mode (allways needed for uploads!)
			setMultiPart(true);

			// Add one file input field
			fileUploadField = new FileUploadField("fileInput");
			add(fileUploadField);

			includeFilesInBackup = new CheckBox("includeFilesInBackup",
					Model.of(true));
			add(includeFilesInBackup);

			// Set maximum size controlled by configuration
			setMaxSize(Bytes.bytes(ImportHelper.getMaxUploadSize(Application
					.getBean(ConfigurationDaoImpl.class))));

			// Add a component to download a file without page refresh
			final AjaxDownload download = new AjaxDownload();
			add(download);
			// add an download button
			add(new AjaxButton("download", this) {
				private static final long serialVersionUID = 839803820502260006L;

				@Override
				protected void onSubmit(AjaxRequestTarget target, Form<?> form) {

					File working_dir = OmFileHelper.getUploadBackupDir();

					String dateString = "backup_"
							+ CalendarPatterns.getTimeForStreamId(new Date());

					File backup_dir = new File(working_dir, dateString);
					File backupFile = new File(backup_dir, dateString + ".zip");

					try {
						Application.getBean(BackupExport.class).performExport(
								backupFile,
								backup_dir,
								includeFilesInBackup.getConvertedInput()
										.booleanValue());

						/*download.setFileName(backupFile.getName());
						download.setResourceStream(new FileResourceStream(backupFile));
						download.initiate(target);*/
						ResourceStreamRequestHandler handler
							= new ResourceStreamRequestHandler(new FileResourceStream(backupFile), backupFile.getName());
						handler.setContentDisposition(ContentDisposition.ATTACHMENT);
				
						getRequestCycle().scheduleRequestHandlerAfterCurrent(handler);
					} catch (Exception e) {
						log.error("Exception on panel backup download ", e);
						uploadFeedback.error(e);
					}

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
			add(new AjaxButton("upload", this) {
				private static final long serialVersionUID = 839803820502260006L;

				@Override
				protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
					FileUpload upload = fileUploadField.getFileUpload();
					try {
						if (upload == null || upload.getInputStream() == null) {
							uploadFeedback.error("File is empty");
							return;
						}
						Application.getBean(BackupImportController.class)
								.performImport(upload.getInputStream());
					} catch (IOException e) {
						log.error("IOException on panel backup upload ", e);
						uploadFeedback.error(e);
					} catch (Exception e) {
						log.error("Exception on panel backup upload ", e);
						uploadFeedback.error(e);
					}

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
