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
import static org.apache.openmeetings.util.OpenmeetingsVariables.getWebAppRootKey;
import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.WebSession.getUserId;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.openmeetings.core.data.file.FileProcessor;
import org.apache.openmeetings.db.dao.file.FileItemLogDao;
import org.apache.openmeetings.db.entity.file.BaseFileItem;
import org.apache.openmeetings.db.entity.file.FileItem;
import org.apache.openmeetings.util.process.ProcessResult;
import org.apache.openmeetings.util.process.ProcessResultList;
import org.apache.openmeetings.web.room.RoomPanel;
import org.apache.openmeetings.web.util.upload.BootstrapFileUploadBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.extensions.ajax.markup.html.form.upload.UploadProgressBar;
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
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.util.string.Strings;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.ui.widget.dialog.AbstractFormDialog;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;
import com.googlecode.wicket.kendo.ui.panel.KendoFeedbackPanel;

public class UploadDialog extends AbstractFormDialog<String> {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Red5LoggerFactory.getLogger(UploadDialog.class, getWebAppRootKey());
	private final KendoFeedbackPanel feedback = new KendoFeedbackPanel("feedback", new Options("button", true));
	private final Form<String> form;
	private DialogButton upload;
	private DialogButton cancel;
	private final FileUploadField uploadField;
	private final HiddenField<String> fileName;
	private final CheckBox toWb = new CheckBox("to-wb", Model.of(false));
	private final WebMarkupContainer cleanBlock = new WebMarkupContainer("clean-block");
	private final CheckBox cleanWb = new CheckBox("clean-wb", Model.of(false));
	private final RoomFilePanel roomFiles;
	private final RoomPanel room;

	public UploadDialog(String id, RoomPanel room, RoomFilePanel roomFiles) {
		super(id, "");
		this.roomFiles = roomFiles;
		this.room = room;
		add(form = new Form<>("form"));
		toWb.add(new OnChangeAjaxBehavior() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				target.add(cleanBlock.setVisible(toWb.getModelObject()));
			}
		});
		form.add(feedback.setOutputMarkupId(true), toWb.setOutputMarkupId(true)
				, cleanBlock.add(cleanWb.setOutputMarkupId(true)).setVisible(false).setOutputMarkupPlaceholderTag(true))
			.setOutputMarkupId(true);

		form.setMultiPart(true);
		form.setMaxSize(Bytes.bytes(getMaxUploadSize()));
		// Model is necessary here to avoid writing image to the User object
		form.add(uploadField = new FileUploadField("file", new IModel<List<FileUpload>>() {
			private static final long serialVersionUID = 1L;

			@Override
			public void setObject(List<FileUpload> object) {
				//no-op
			}

			@Override
			public List<FileUpload> getObject() {
				return new ArrayList<>();
			}
		}));
		Form<String> nameForm = new Form<>("name-form");
		fileName = new HiddenField<>("name", Model.of(""));
		fileName.add(new AjaxFormSubmitBehavior(nameForm, "change") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target) {
				if (!Strings.isEmpty(getComponent().getDefaultModelObjectAsString())) {
					upload.setEnabled(true, target);
				}
			}
		}).setOutputMarkupId(true);
		form.add(new UploadProgressBar("progress", form, uploadField));
		add(nameForm.add(fileName.setOutputMarkupId(true)));
		add(BootstrapFileUploadBehavior.INSTANCE);
	}

	@Override
	protected void onInitialize() {
		getTitle().setObject(getString("304"));
		upload = new DialogButton("upload", getString("593"), false) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isIndicating() {
				return true;
			}
		};
		cancel = new DialogButton("close", getString("85"));
		super.onInitialize();
	}

	@Override
	public void onClick(AjaxRequestTarget target, DialogButton button) {
		if (button == null || button.match("close")) {
			super.onClick(target, button);
		}
	}

	@Override
	protected List<DialogButton> getButtons() {
		return Arrays.asList(upload, cancel);
	}

	@Override
	public DialogButton getSubmitButton() {
		return upload;
	}

	@Override
	public Form<?> getForm() {
		return form;
	}

	@Override
	protected void onOpen(IPartialPageRequestHandler handler) {
		super.onOpen(handler);
		upload.setEnabled(true, handler);
		uploadField.setModelObject(new ArrayList<>());
		handler.add(form, fileName);
		handler.appendJavaScript(String.format("bindUpload('%s', '%s');", form.getMarkupId(), fileName.getMarkupId()));
	}

	@Override
	protected void onError(AjaxRequestTarget target, DialogButton btn) {
		target.add(feedback);
	}

	@Override
	protected void onSubmit(AjaxRequestTarget target, DialogButton btn) {
		List<FileUpload> ful = uploadField.getFileUploads();
		if (ful != null) {
			boolean clean = cleanWb.getModelObject();
			for (FileUpload fu : ful) {
				FileItem f = new FileItem();
				f.setSize(fu.getSize());
				f.setName(fu.getClientFileName());
				f.setExternalType(room.getRoom().getExternalType());
				BaseFileItem parent = roomFiles.getLastSelected();
				if (parent == null || !(parent instanceof FileItem)) {
					f.setOwnerId(getUserId());
				} else {
					f.setRoomId(parent.getRoomId());
					f.setOwnerId(parent.getOwnerId());
					f.setGroupId(parent.getGroupId());
					if (parent.getId() != null) {
						f.setParentId(BaseFileItem.Type.Folder == parent.getType() ? parent.getId() : parent.getParentId());
					}
				}
				f.setInsertedBy(getUserId());

				try {
					ProcessResultList logs = getBean(FileProcessor.class).processFile(f, fu.getInputStream());
					for (ProcessResult res : logs.getJobs()) {
						getBean(FileItemLogDao.class).add(res.getProcess(), f, res);
					}
					room.getSidebar().updateFiles(target);
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
			}
			if (form.hasError()) {
				onError(target, null);
			} else {
				close(target, null);
			}
		}
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(new PriorityHeaderItem(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(UploadDialog.class, "upload.js"))));
	}
}
