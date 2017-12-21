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

import static org.apache.openmeetings.util.OpenmeetingsVariables.getMaxUploadSize;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getWebAppRootKey;
import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.wicket.util.time.Duration.NONE;

import java.io.File;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.Date;

import org.apache.openmeetings.backup.BackupExport;
import org.apache.openmeetings.backup.BackupImport;
import org.apache.openmeetings.backup.ProgressHolder;
import org.apache.openmeetings.util.CalendarPatterns;
import org.apache.openmeetings.util.OmFileHelper;
import org.apache.openmeetings.web.admin.AdminBasePanel;
import org.apache.openmeetings.web.util.upload.BootstrapFileUploadBehavior;
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.extensions.ajax.AjaxDownloadBehavior;
import org.apache.wicket.extensions.ajax.markup.html.form.upload.UploadProgressBar;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.resource.FileSystemResource;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.util.time.Duration;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.ui.form.button.AjaxButton;
import com.googlecode.wicket.jquery.ui.widget.progressbar.ProgressBar;
import com.googlecode.wicket.kendo.ui.panel.KendoFeedbackPanel;
/**
 * Panel component to manage Backup Import/Export
 *
 * @author swagner
 *
 */
public class BackupPanel extends AdminBasePanel {
	private static final Logger log = Red5LoggerFactory.getLogger(BackupPanel.class, getWebAppRootKey());
	private static final long serialVersionUID = 1L;

	private final KendoFeedbackPanel feedback = new KendoFeedbackPanel("feedback", new Options("button", true));

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
			Long maxBytes = getMaxUploadSize();
			double megaBytes = maxBytes.doubleValue() / 1024 / 1024;
			DecimalFormat formatter = new DecimalFormat("#,###.00");
			add(new Label("MaxUploadSize", formatter.format(megaBytes)));

			// Add one file input field
			fileUploadField = new FileUploadField("fileInput");

			add(new CheckBox("includeFilesInBackup", includeFilesInBackup));

			// Set maximum size controlled by configuration
			setMaxSize(Bytes.bytes(maxBytes));

			// Add a component to download a file without page refresh
			final AjaxDownloadBehavior download = new AjaxDownloadBehavior(new IResource() {
				private static final long serialVersionUID = 1L;

				@Override
				public void respond(Attributes attributes) {
					new FileSystemResource(backupFile.toPath()) {
						private static final long serialVersionUID = 1L;

						@Override
						protected ResourceResponse createResourceResponse(Attributes attr, Path path) {
							ResourceResponse response = super.createResourceResponse(attr, path);
							response.setCacheDuration(NONE);
							return response;
						}
					}.respond(attributes);
				}
			});
			add(download);
			// add an download button
			add(new AjaxButton("download", this) {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onSubmit(AjaxRequestTarget target) {
					String dateString = "backup_" + CalendarPatterns.getTimeForStreamId(new Date());
					backupFile = new File(OmFileHelper.getUploadBackupDir(), dateString + ".zip");
					th = null;
					started = true;
					progressHolder = new ProgressHolder();

					timer.restart(target);
					new Thread(new BackupProcess(getBean(BackupExport.class), includeFilesInBackup.getObject())
						, "Openmeetings - " + dateString).start();

					// repaint the feedback panel so that it is hidden
					target.add(feedback, progressBar.setVisible(true));
				}

				@Override
				protected void onError(AjaxRequestTarget target) {
					// repaint the feedback panel so errors are shown
					target.add(feedback);
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
						progressBar.setVisible(false);
						feedback.error(th.getMessage());
						target.add(feedback);
					} else {
						progressBar.setModelObject(progressHolder.getProgress());
						progressBar.refresh(target);
					}
				}
			});
			add((progressBar = new ProgressBar("dprogress", new Model<>(0)) {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onComplete(AjaxRequestTarget target) {
					timer.stop(target);
					target.add(progressBar.setVisible(false));

					download.initiate(target);
				}
			}).setVisible(false).setOutputMarkupPlaceholderTag(true));
			add(fileUploadField.add(new AjaxFormSubmitBehavior(this, "change") {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onSubmit(AjaxRequestTarget target) {
					FileUpload upload = fileUploadField.getFileUpload();
					try {
						if (upload == null || upload.getInputStream() == null) {
							feedback.error("File is empty");
							target.add(feedback);
							return;
						}
						getBean(BackupImport.class).performImport(upload.getInputStream());
					} catch (Exception e) {
						log.error("Exception on panel backup upload ", e);
						feedback.error(e);
					}
					// repaint the feedback panel so that it is hidden
					target.add(feedback);
				}

				@Override
				protected void onError(AjaxRequestTarget target) {
					// repaint the feedback panel so errors are shown
					target.add(feedback);
				}
			}));
		}

		@Override
		protected void onInitialize() {
			add(new Label("cmdLineDesc", getString("1505")).setEscapeModelStrings(false));
			super.onInitialize();
		}

		@Override
		protected void onDetach() {
			includeFilesInBackup.detach();
			super.onDetach();
		}

		private class BackupProcess implements Runnable {
			private BackupExport backup;
			private boolean includeFiles;

			public BackupProcess(BackupExport backup, boolean includeFiles) {
				this.backup = backup;
				this.includeFiles = includeFiles;
				th = null;
			}

			@Override
			public void run() {
				try {
					backup.performExport(backupFile, includeFiles, progressHolder);
				} catch (Exception e) {
					log.error("Exception on panel backup download ", e);
					th = e;
				}
			}
		}
	}

	public BackupPanel(String id) {
		super(id);

		add(feedback);

		BackupForm backupForm = new BackupForm("backupUpload");

		backupForm.add(new UploadProgressBar("progress", backupForm, backupForm.fileUploadField));

		add(backupForm);
		add(BootstrapFileUploadBehavior.INSTANCE);
	}
}
