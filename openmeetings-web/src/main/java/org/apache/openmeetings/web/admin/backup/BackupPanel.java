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

import java.io.File;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.openmeetings.backup.BackupExport;
import org.apache.openmeetings.util.CalendarPatterns;
import org.apache.openmeetings.util.OmFileHelper;
import org.apache.openmeetings.web.admin.AdminBasePanel;
import org.apache.openmeetings.web.common.BasePanel;
import org.apache.openmeetings.web.common.upload.UploadForm;
import org.apache.openmeetings.web.util.ThreadHelper;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.extensions.ajax.AjaxDownloadBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.resource.FileSystemResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import de.agilecoders.wicket.core.markup.html.bootstrap.components.progress.UpdatableProgressBar;
import de.agilecoders.wicket.core.markup.html.bootstrap.utilities.BackgroundColorBehavior;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesome6IconType;
import jakarta.inject.Inject;
/**
 * Panel component to manage Backup Import/Export
 *
 * @author swagner
 *
 */
@AuthorizeInstantiation({"ADMIN", "ADMIN_BACKUP"})
public class BackupPanel extends AdminBasePanel {
	private static final Logger log = LoggerFactory.getLogger(BackupPanel.class);
	private static final long serialVersionUID = 1L;

	private final NotificationPanel feedback = new NotificationPanel("feedback");
	private UploadForm upload;

	@Inject
	private BackupExport backupExport;

	public BackupPanel(String id) {
		super(id);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		upload = new UploadForm("upload", "" + RequestCycle.get().urlFor(new BackupUploadResourceReference(), new PageParameters())) {
			private static final long serialVersionUID = 1L;

			@Override
			protected String uploadLocation() {
				return ".backup-upload .card-footer";
			}

			@Override
			protected boolean allowMultiple() {
				return false;
			}

			@Override
			protected boolean showDescBlock() {
				return false;
			}

			@Override
			protected String buttonLabelKey() {
				return "admin.backup.import.lbl";
			}

			@Override
			protected String processingLabelKey() {
				return "admin.backup.import.lbl";
			}
		};
		add(feedback.setOutputMarkupId(true)
				, new BackupForm("backupUpload")
				, upload
				, new Label("cmdLineDesc", new ResourceModel("admin.backup.cmd.line.desc"))
					.setEscapeModelStrings(false)
					.setRenderBodyOnly(true));
	}

	@Override
	public BasePanel onMenuPanelLoad(IPartialPageRequestHandler handler) {
		upload.show(handler);
		return super.onMenuPanelLoad(handler);
	}

	private class BackupForm extends Form<Void> {
		private static final long serialVersionUID = 1L;
		private final Model<Boolean> includeFilesInBackup = Model.of(true);
		private UpdatableProgressBar progressBar;
		private File backupFile;
		private Throwable th = null;
		private final AtomicInteger progress = new AtomicInteger();
		private BootstrapAjaxButton download;

		public BackupForm(String id) {
			super(id);
			// set this form to multipart mode (allways needed for uploads!)
			setMultiPart(true);
		}

		@Override
		protected void onInitialize() {
			add(new CheckBox("includeFilesInBackup", includeFilesInBackup).setOutputMarkupId(true));

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
			download = new BootstrapAjaxButton("download", new ResourceModel("admin.backup.lbl"), this, Buttons.Type.Outline_Primary) {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onSubmit(AjaxRequestTarget target) {
					String dateString = "backup_" + CalendarPatterns.getTimeForStreamId(new Date());
					backupFile = new File(OmFileHelper.getUploadBackupDir(), dateString + ".zip");
					startWithProgress(() -> {
						try {
							backupExport.performExport(backupFile, includeFilesInBackup.getObject(), progress);
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
			};
			download.setIconType(FontAwesome6IconType.file_arrow_down_s);
			progressBar = new UpdatableProgressBar("progress", new Model<>(0), BackgroundColorBehavior.Color.Info, true) {
				private static final long serialVersionUID = 1L;

				@Override
				protected IModel<Integer> newValue() {
					return Model.of(progress.get());
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
					downloader.initiate(target);
					super.onComplete(target);
				}
			};
			progressBar.updateInterval(Duration.ofSeconds(1)).stop(null).striped(false).setVisible(false).setOutputMarkupPlaceholderTag(true);
			add(downloader);
			add(progressBar
					, download
					, new Label("backupSteps", new ResourceModel("admin.backup.steps"))
						.setEscapeModelStrings(false)
						.setRenderBodyOnly(true));
			super.onInitialize();
		}

		private void updateButtons(IPartialPageRequestHandler target, boolean enabled) {
			target.add(download.setEnabled(enabled));
		}

		private void startWithProgress(Runnable r, String label, AjaxRequestTarget target) {
			th = null;
			progress.set(0);

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
}
