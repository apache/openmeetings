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
package org.apache.openmeetings.web.room.sidebar;

import static org.apache.openmeetings.util.OpenmeetingsVariables.getMaxUploadSize;
import static org.apache.openmeetings.web.app.WebSession.getUserId;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.DoubleConsumer;

import org.apache.openmeetings.core.data.file.FileProcessor;
import org.apache.openmeetings.db.dao.file.FileItemLogDao;
import org.apache.openmeetings.db.entity.file.BaseFileItem;
import org.apache.openmeetings.db.entity.file.FileItem;
import org.apache.openmeetings.util.process.ProcessResult;
import org.apache.openmeetings.util.process.ProcessResultList;
import org.apache.openmeetings.web.common.OmModalCloseButton;
import org.apache.openmeetings.web.room.RoomPanel;
import org.apache.openmeetings.web.util.ThreadHelper;
import org.apache.openmeetings.web.util.upload.BootstrapFileUploadBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.PriorityHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.util.string.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import de.agilecoders.wicket.core.markup.html.bootstrap.components.progress.UpdatableProgressBar;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import de.agilecoders.wicket.core.markup.html.bootstrap.utilities.BackgroundColorBehavior;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.spinner.SpinnerAjaxButton;

public class UploadDialog extends Modal<String> {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(UploadDialog.class);
	private final NotificationPanel feedback = new NotificationPanel("feedback");
	private final Form<String> form = new Form<>("form") {
		private static final long serialVersionUID = 1L;

		@Override
		protected boolean handleMultiPart() {
			try {
				return super.handleMultiPart();
			} catch (Exception e) {
				log.warn("Multipart processing failed {}", e.getMessage());
			}
			return true;
		}
	};
	private SpinnerAjaxButton upload;
	private final FileUploadField uploadField = new FileUploadField("file", new IModel<List<FileUpload>>() {
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
	private final HiddenField<String> fileName = new HiddenField<>("name", Model.of(""));
	private final CheckBox toWb = new CheckBox("to-wb", Model.of(false));
	private final WebMarkupContainer cleanBlock = new WebMarkupContainer("clean-block");
	private final CheckBox cleanWb = new CheckBox("clean-wb", Model.of(false));
	private final RoomFilePanel roomFiles;
	private final RoomPanel room;

	@SpringBean
	private FileProcessor processor;
	@SpringBean
	private FileItemLogDao fileLogDao;

	private final UpdatableProgressBar progressBar = new UpdatableProgressBar("progress", new Model<>(0), BackgroundColorBehavior.Color.Info, true) {
		private static final long serialVersionUID = 1L;

		@Override
		protected IModel<Integer> newValue() {
			return Model.of(progress);
		}

		@Override
		protected void onComplete(IPartialPageRequestHandler target) {
			progressBar.setVisible(false);
			room.getSidebar().updateFiles(target);
			if (form.hasError()) {
				target.add(form.setVisible(true));
				target.add(feedback);
			} else {
				close(target);
			}
			target.add(progressBar);
		}
	};
	private int progress = 0;

	public UploadDialog(String id, RoomPanel room, RoomFilePanel roomFiles) {
		super(id);
		this.roomFiles = roomFiles;
		this.room = room;
	}

	@Override
	protected void onInitialize() {
		header(new ResourceModel("upload.dlg.choose.title"));
		setCloseOnEscapeKey(false);
		setBackdrop(Backdrop.STATIC);

		add(form.setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true));
		toWb.add(new OnChangeAjaxBehavior() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				target.add(cleanBlock.setVisible(toWb.getModelObject()));
			}
		});
		form.add(feedback.setOutputMarkupId(true), toWb.setOutputMarkupId(true)
				, cleanBlock.add(cleanWb.setOutputMarkupId(true)).setVisible(false).setOutputMarkupPlaceholderTag(true));

		form.setMultiPart(true);
		form.setMaxSize(Bytes.bytes(getMaxUploadSize()));
		// Model is necessary here to avoid writing image to the User object
		form.add(uploadField);
		final Form<String> nameForm = new Form<>("name-form");
		fileName.add(new AjaxFormSubmitBehavior(nameForm, "change") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target) {
				if (!Strings.isEmpty(getComponent().getDefaultModelObjectAsString())) {
					target.add(upload.setEnabled(true));
				}
			}
		}).setOutputMarkupId(true);

		add(nameForm.add(fileName.setOutputMarkupId(true)));
		add(BootstrapFileUploadBehavior.INSTANCE);
		addButton(upload = new SpinnerAjaxButton("button", new ResourceModel("593"), form, Buttons.Type.Outline_Primary) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onError(AjaxRequestTarget target) {
				target.add(feedback);
			}

			@Override
			protected void onSubmit(AjaxRequestTarget target) {
				List<FileUpload> ful = uploadField.getFileUploads();
				if (ful != null) {

					progress = 0;
					progressBar.restart(target);
					target.add(
							progressBar.setModelObject(progress).setVisible(true)
							, form.setVisible(false)
							, upload.setEnabled(false));

					ThreadHelper.startRunnable(UploadDialog.this::convertAll);
				}
			}
		});
		upload.setEnabled(false);
		addButton(OmModalCloseButton.of("85"));

		progressBar.updateInterval(Duration.ofSeconds(1)).stop(null).striped(false);
		add(progressBar.setOutputMarkupPlaceholderTag(true).setVisible(false));
		super.onInitialize();
	}

	@Override
	public Modal<String> show(IPartialPageRequestHandler handler) {
		handler.add(upload.setEnabled(true));
		uploadField.setModelObject(new ArrayList<>());
		handler.add(form.setVisible(true), fileName);
		handler.appendJavaScript(String.format("bindUpload('%s', '%s');", form.getMarkupId(), fileName.getMarkupId()));
		return super.show(handler);
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(new PriorityHeaderItem(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(UploadDialog.class, "upload.js"))));
	}

	private void convertAll() {
		List<FileUpload> ful = uploadField.getFileUploads();
		final BaseFileItem parent = roomFiles.getLastSelected();
		boolean clean = cleanWb.getModelObject();
		final long totalSize = ful.stream().mapToLong(FileUpload::getSize).sum();
		long currentSize = 0;
		for (FileUpload fu : ful) {
			long size = fu.getSize();
			try {
				FileItem f = new FileItem();
				f.setSize(size);
				f.setName(fu.getClientFileName());
				if (parent == null || !(parent instanceof FileItem)) {
					f.setOwnerId(getUserId());
				} else {
					f.setRoomId(parent.getRoomId());
					f.setOwnerId(parent.getOwnerId());
					f.setGroupId(parent.getGroupId());
					if (parent.getId() != null) {
						f.setParentId(BaseFileItem.Type.FOLDER == parent.getType() ? parent.getId() : parent.getParentId());
					}
				}
				f.setInsertedBy(getUserId());

				ProcessResultList logs = processor.processFile(f, fu.getInputStream()
						, Optional.<DoubleConsumer>of(part -> progress += (int)(100 * part * size / totalSize)));
				for (ProcessResult res : logs.getJobs()) {
					fileLogDao.add(res.getProcess(), f, res);
				}
				if (logs.hasError()) {
					form.error(getString("convert.errors.file"));
				} else {
					if (toWb.getModelObject()) {
						room.getWb().sendFileToWb(f, clean);
						clean = false;
					}
				}
			} catch (Exception e) {
				log.error("Unexpected error while processing uploaded file", e);
				form.error(e.getMessage() == null ? "Unexpected error" : e.getMessage());
			} finally {
				fu.closeStreams();
				fu.delete();
			}
			currentSize += size;
			progress = (int)(100 * currentSize / totalSize);
		}
		progress = 100;
	}
}
