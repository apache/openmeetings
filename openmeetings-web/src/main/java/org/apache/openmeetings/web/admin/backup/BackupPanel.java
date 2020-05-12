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

import static java.time.Duration.ZERO;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getMaxUploadSize;

import java.io.File;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.openmeetings.backup.BackupExport;
import org.apache.openmeetings.backup.BackupImport;
import org.apache.openmeetings.backup.ProgressHolder;
import org.apache.openmeetings.util.CalendarPatterns;
import org.apache.openmeetings.util.OmFileHelper;
import org.apache.openmeetings.web.admin.AdminBasePanel;
import org.apache.openmeetings.web.util.ThreadHelper;
import org.apache.openmeetings.web.util.upload.BootstrapFileUploadBehavior;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.extensions.ajax.AjaxDownloadBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.resource.FileSystemResource;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.lang.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import de.agilecoders.wicket.core.markup.html.bootstrap.components.progress.UpdatableProgressBar;
import de.agilecoders.wicket.core.markup.html.bootstrap.utilities.BackgroundColorBehavior;
/**
 * Panel component to manage Backup Import/Export
 *
 * @author swagner
 *
 */
public class BackupPanel extends AdminBasePanel {
	private static final Logger log = LoggerFactory.getLogger(BackupPanel.class);
	private static final long serialVersionUID = 1L;

	private final NotificationPanel feedback = new NotificationPanel("feedback");
	@SpringBean
	private BackupExport backupExport;
	@SpringBean
	private BackupImport backupImport;

	/**
	 * Form to handle upload files
	 *
	 * @author swagner
	 *
	 */
	private class BackupForm extends Form<Void> {
		private static final long serialVersionUID = 1L;
		private final Model<Boolean> includeFilesInBackup = Model.of(true);
		private final FileUploadField fileUploadField = new FileUploadField("fileInput", new IModel<List<FileUpload>>() {
			private static final long serialVersionUID = 1L;

			@Override
			public void setObject(List<FileUpload> object) {
				//no-op
			}

			@Override
			public List<FileUpload> getObject() {
				return new ArrayList<>();
			}
		}) {
			private static final long serialVersionUID = 1L;

			@Override
			protected boolean forceCloseStreamsOnDetach() {
				return false;
			}
		};
		private UpdatableProgressBar progressBar;
		private File backupFile;
		private Throwable th = null;
		private boolean modeDownload = false;
		private final ProgressHolder progressHolder = new ProgressHolder();
		private BootstrapAjaxButton download;
		private final WebMarkupContainer upload = new WebMarkupContainer("upload");

		public BackupForm(String id) {
			super(id);
			// set this form to multipart mode (allways needed for uploads!)
			setMultiPart(true);
		}

		@Override
		protected void onInitialize() {
			// set max upload size in form as info text
			Long maxBytes = getMaxUploadSize();
			double megaBytes = maxBytes.doubleValue() / 1024 / 1024;
			DecimalFormat formatter = new DecimalFormat("#,###.00");
			add(new Label("MaxUploadSize", formatter.format(megaBytes)));

			add(new CheckBox("includeFilesInBackup", includeFilesInBackup).setOutputMarkupId(true));

			// Set maximum size controlled by configuration
			setMaxSize(Bytes.bytes(maxBytes));

			// Add a component to download a file without page refresh
			final AjaxDownloadBehavior downloader = new AjaxDownloadBehavior(new IResource() {
				private static final long serialVersionUID = 1L;

				@Override
				public void respond(Attributes attributes) {
					new FileSystemResource(backupFile.toPath()) {
						private static final long serialVersionUID = 1L;

						@Override
						protected ResourceResponse createResourceResponse(Attributes attr, Path path) {
							ResourceResponse response = super.createResourceResponse(attr, path);
							response.setCacheDuration(ZERO);
							return response;
						}
					}.respond(attributes);
				}
			});
			add(downloader);
			// add an download button
			add(download = new BootstrapAjaxButton("download", new ResourceModel("1066"), this, Buttons.Type.Outline_Primary) {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onSubmit(AjaxRequestTarget target) {
					modeDownload = true;
					String dateString = "backup_" + CalendarPatterns.getTimeForStreamId(new Date());
					backupFile = new File(OmFileHelper.getUploadBackupDir(), dateString + ".zip");
					startWithProgress(() -> {
						try {
							backupExport.performExport(backupFile, includeFilesInBackup.getObject(), progressHolder);
						} catch (Exception e) {
							log.error("Exception on panel backup download ", e);
							th = e;
						}
					}, dateString, target);
				}

				@Override
				protected void onError(AjaxRequestTarget target) {
					// repaint the feedback panel so errors are shown
					target.add(feedback);
				}
			});
			add(progressBar = new UpdatableProgressBar("progress", new Model<>(0), BackgroundColorBehavior.Color.Info, true) {
				private static final long serialVersionUID = 1L;

				@Override
				protected IModel<Integer> newValue() {
					return Model.of(progressHolder.getProgress());
				}

				@Override
				protected void onPostProcessTarget(IPartialPageRequestHandler target) {
					if (th != null) {
						stop(target);
						feedback.error(th.getMessage());
						onComplete(target);
					}
					super.onPostProcessTarget(target);
				}

				@Override
				protected void onComplete(IPartialPageRequestHandler target) {
					progressBar.setVisible(false);
					target.add(feedback);
					updateButtons(target, true);
					if (modeDownload) {
						downloader.initiate(target);
					}
					super.onComplete(target);
				}
			});
			progressBar.updateInterval(Duration.ofSeconds(1)).stop(null).striped(false).setVisible(false).setOutputMarkupPlaceholderTag(true);
			upload.add(fileUploadField.add(new AjaxFormSubmitBehavior(this, "change") {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onSubmit(AjaxRequestTarget target) {
					FileUpload upload = fileUploadField.getFileUpload();
					modeDownload = false;
					try {
						startWithProgress(() -> {
							try {
								if (upload == null || upload.getInputStream() == null) {
									feedback.error("File is empty");
									target.add(feedback);
									return;
								}
								backupImport.performImport(upload.getInputStream(), progressHolder);
								feedback.success(getString("387") + " - " + getString("54"));
							} catch (Exception e) {
								log.error("Exception on panel backup download ", e);
								th = e;
							} finally {
								if (upload != null) {
									upload.closeStreams();
									upload.delete();
								}
							}
						}, "Restore", target);
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
			add(upload.setOutputMarkupId(true), new Label("cmdLineDesc", getString("1505")).setEscapeModelStrings(false).setRenderBodyOnly(true));
			super.onInitialize();
		}

		private void updateButtons(IPartialPageRequestHandler target, boolean enabled) {
			download.setEnabled(enabled);
			upload.add(enabled ? AttributeModifier.remove("disabled") : AttributeModifier.append("disabled", "disabled"));
			fileUploadField.setEnabled(enabled);
			target.add(download, upload);
		}

		private void startWithProgress(Runnable r, String label, AjaxRequestTarget target) {
			th = null;
			progressHolder.setProgress(0);

			ThreadHelper.startRunnable(r, "Openmeetings - " + label);

			// repaint the feedback panel so that it is hidden
			updateButtons(target, false);
			target.add(feedback, progressBar.restart(target).setModelObject(0).setVisible(true));
		}

		@Override
		protected void onDetach() {
			includeFilesInBackup.detach();
			super.onDetach();
		}
	}

	public BackupPanel(String id) {
		super(id);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		add(feedback.setOutputMarkupId(true));

		add(new BackupForm("backupUpload"));
		add(BootstrapFileUploadBehavior.INSTANCE);
	}
}
