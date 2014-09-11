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

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;
import static org.apache.openmeetings.web.app.Application.getBean;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Date;

import org.apache.openmeetings.backup.BackupExport;
import org.apache.openmeetings.backup.BackupImport;
import org.apache.openmeetings.backup.ProgressHolder;
import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.util.CalendarPatterns;
import org.apache.openmeetings.util.OmFileHelper;
import org.apache.openmeetings.web.admin.AdminPanel;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.util.AjaxDownload;
import org.apache.openmeetings.web.util.BootstrapFileUploadBehavior;
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.form.upload.UploadProgressBar;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.util.resource.FileResourceStream;
import org.apache.wicket.util.time.Duration;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

import com.googlecode.wicket.jquery.ui.widget.progressbar.ProgressBar;

/**
 * Panel component to manage Backup Import/Export
 * 
 * @author swagner
 * 
 */
public class BackupPanel extends AdminPanel {
	private static final Logger log = Red5LoggerFactory.getLogger(BackupPanel.class, webAppRootKey);

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
		private final FileUploadField fileUploadField;
		private final Model<Boolean> includeFilesInBackup = Model.of(true);
		private final AbstractAjaxTimerBehavior timer;
		private final ProgressBar progressBar;
		private File backupFile;
		private Throwable th = null;
		private boolean started = false;
		private ProgressHolder progressHolder;

		public BackupForm(String id) {
			super(id);

			// set this form to multipart mode (allways needed for uploads!)
			setMultiPart(true);

			// set max upload size in form as info text
			Long maxBytes = getBean(ConfigurationDao.class).getMaxUploadSize();
			double megaBytes = maxBytes.doubleValue() / 1024 / 1024;
			DecimalFormat formatter = new DecimalFormat("#,###.00");
			add(new Label("MaxUploadSize", formatter.format(megaBytes)));

			// Add one file input field
			fileUploadField = new FileUploadField("fileInput");

			add(new CheckBox("includeFilesInBackup", includeFilesInBackup));

			// Set maximum size controlled by configuration
			setMaxSize(Bytes.bytes(maxBytes));

			// Add a component to download a file without page refresh
			final AjaxDownload download = new AjaxDownload();
			add(download);
			// add an download button
			add(new AjaxButton("download", this) {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
					File workingDir = OmFileHelper.getUploadBackupDir();
					String dateString = "backup_" + CalendarPatterns.getTimeForStreamId(new Date());
					File backupDir = new File(workingDir, dateString);
					backupFile = new File(backupDir, dateString + ".zip");
					th = null;
					started = true;
					progressHolder = new ProgressHolder();

					timer.restart(target);
					new Thread(new BackupProcess(getBean(BackupExport.class), backupDir, includeFilesInBackup.getObject(), progressHolder)
						, "Openmeetings - " + dateString).start();

					// repaint the feedback panel so that it is hidden
					target.add(uploadFeedback, progressBar.setVisible(true));
				}

				@Override
				protected void onError(AjaxRequestTarget target, Form<?> form) {
					// repaint the feedback panel so errors are shown
					target.add(uploadFeedback);
				}
			});
			add(timer = new AbstractAjaxTimerBehavior(Duration.ONE_SECOND) {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onTimer(AjaxRequestTarget target) {
					if (!started) {
						timer.stop(target);
						return;
					}
					if (th != null) {
						timer.stop(target);
						//TODO change text, localize
						progressBar.setVisible(false);
						uploadFeedback.error(th);
						target.add(uploadFeedback);
					} else {
						progressBar.setModelObject(progressHolder.getProgress());
						progressBar.refresh(target);
						//TODO add current step result as info
					}
				}
			});
			add((progressBar = new ProgressBar("dprogress", new Model<Integer>(0)) {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onComplete(AjaxRequestTarget target) {
					timer.stop(target);
					target.add(progressBar.setVisible(false));
					
					download.setFileName(backupFile.getName());
					download.setResourceStream(new FileResourceStream(backupFile));
					download.initiate(target);
				}
			}).setVisible(false).setOutputMarkupPlaceholderTag(true));
			add(fileUploadField.add(new AjaxFormSubmitBehavior(this, "onchange") {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onSubmit(AjaxRequestTarget target) {
					FileUpload upload = fileUploadField.getFileUpload();
					try {
						if (upload == null || upload.getInputStream() == null) {
							uploadFeedback.error("File is empty");
							target.add(uploadFeedback);
							return;
						}
						getBean(BackupImport.class).performImport(upload.getInputStream());
					} catch (Exception e) {
						log.error("Exception on panel backup upload ", e);
						uploadFeedback.error(e);
					}
					// repaint the feedback panel so that it is hidden
					target.add(uploadFeedback);
				}

				@Override
				protected void onError(AjaxRequestTarget target) {
					// repaint the feedback panel so errors are shown
					target.add(uploadFeedback);
				}
			}));
			add(new Label("cmdLineDesc", WebSession.getString(1505)).setEscapeModelStrings(false));
		}

		private class BackupProcess implements Runnable {
			private BackupExport backup;
			private File backupDir;
			private boolean includeFiles;
			private ProgressHolder progressHolder;
			
			public BackupProcess(BackupExport backup, File backupDir, boolean includeFiles, ProgressHolder progressHolder) {
				this.backup = backup;
				this.backupDir = backupDir;
				this.includeFiles = includeFiles;
				this.progressHolder = progressHolder;
				th = null;
			}
			
			public void run() {
				try {
					backup.performExport(backupFile, backupDir, includeFiles, progressHolder);
				} catch (Exception e) {
					log.error("Exception on panel backup download ", e);
					th = e;
				}
			}
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

		backupForm.add(new UploadProgressBar("progress", backupForm, backupForm.fileUploadField));

		add(backupForm);
		add(BootstrapFileUploadBehavior.INSTANCE);
	}
}
