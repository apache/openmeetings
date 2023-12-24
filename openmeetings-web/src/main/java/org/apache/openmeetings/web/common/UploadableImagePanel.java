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
package org.apache.openmeetings.web.common;

import static org.apache.openmeetings.web.common.confirmation.ConfirmationHelper.newOkCancelConfirm;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getMaxUploadSize;

import java.io.File;
import java.nio.file.Files;
import java.util.Optional;

import org.apache.openmeetings.util.StoredFile;
import org.apache.openmeetings.web.util.upload.BootstrapFileUploadBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.extensions.ajax.markup.html.form.upload.UploadProgressBar;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.util.lang.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesome6IconType;

public abstract class UploadableImagePanel extends ImagePanel {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(UploadableImagePanel.class);
	private static final String ERROR = "Error";
	private final FileUploadField fileUploadField = new FileUploadField("image", new ListModel<>());
	private final Form<Void> form = new Form<>("form");
	private final boolean delayed;
	private HiddenField<Boolean> deleted = new HiddenField<>("imgDeleted", Model.of(false));

	protected UploadableImagePanel(String id, boolean delayed) {
		super(id);
		this.delayed = delayed;
	}

	protected abstract void processImage(StoredFile sf, File f) throws Exception;

	protected abstract void deleteImage() throws Exception;

	@Override
	protected void onInitialize() {
		super.onInitialize();
		form.setMultiPart(true);
		form.setMaxSize(Bytes.bytes(getMaxUploadSize()));
		// Model is necessary here to avoid writing image to the User object
		form.add(fileUploadField, deleted);
		form.add(new UploadProgressBar("progress", form, fileUploadField));
		form.addOrReplace(getImage());
		if (delayed) {
			add(new WebMarkupContainer("remove"));
		} else {
			BootstrapAjaxLink<String> remove = new BootstrapAjaxLink<>("remove", Buttons.Type.Outline_Secondary) {
				private static final long serialVersionUID = 1L;

				@Override
				public void onClick(AjaxRequestTarget target) {
					try {
						deleteImage();
					} catch (Exception e) {
						log.error(ERROR, e);
					}
					update(Optional.of(target));
				}
			};
			add(remove
					.setIconType(FontAwesome6IconType.xmark_s)
					.add(newOkCancelConfirm(this, getString("833"))));
			fileUploadField.add(new AjaxFormSubmitBehavior(form, "change") {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onSubmit(AjaxRequestTarget target) {
					process(Optional.of(target));
				}
			});
		}
		add(form.setOutputMarkupId(true));
		add(BootstrapFileUploadBehavior.getInstance());
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		if (delayed) {
			response.render(OnDomReadyHeaderItem.forScript("""
					(function(){
						const form = $('#%s')
							, fileinput = form.find('.fileinput')
							, deleted = form.find('#imgDeleted');
						fileinput.off('change.bs.fileinput').on('change.bs.fileinput', function() {
							deleted.val(false);
						});
						form.siblings('.remove').off()
							.click(function() {
								fileinput.fileinput('clear');
								deleted.val(true);
							});
					})();
					""".formatted(form.getMarkupId())));
		}
	}

	@Override
	public void update() {
		deleted.setModelObject(false);
		profile.addOrReplace(new WebMarkupContainer("img").setVisible(false));
		form.addOrReplace(getImage());
	}

	private void update(Optional<AjaxRequestTarget> target) {
		update();
		target.ifPresent(t -> t.add(profile, form));
	}

	private void processImage(FileUpload fu) {
		if (fu != null) {
			try {
				File temp = null;
				try {
					temp = fu.writeToTempFile();
					StoredFile sf = new StoredFile(fu.getClientFileName(), temp);
					if (sf.isImage()) {
						processImage(sf, temp);
					}
				} finally {
					if (temp != null) {
						log.debug("Temp file was deleted ? {}", Files.deleteIfExists(temp.toPath()));
					}
					fu.closeStreams();
					fu.delete();
				}
			} catch (Exception e) {
				log.error(ERROR, e);
			}
		}
	}

	public void process(Optional<AjaxRequestTarget> target) {
		if (delayed && Boolean.TRUE.equals(deleted.getModelObject())) {
			try {
				deleteImage();
			} catch (Exception e) {
				log.error(ERROR, e);
			}
		} else {
			FileUpload fu = fileUploadField.getFileUpload();
			processImage(fu);
		}
		update(target);
	}
}
