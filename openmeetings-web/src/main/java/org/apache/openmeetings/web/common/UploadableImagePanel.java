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

import static org.apache.openmeetings.util.OpenmeetingsVariables.getMaxUploadSize;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getWebAppRootKey;

import java.io.File;

import org.apache.openmeetings.util.StoredFile;
import org.apache.openmeetings.web.util.upload.BootstrapFileUploadBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.extensions.ajax.markup.html.form.upload.UploadProgressBar;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.util.lang.Bytes;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public abstract class UploadableImagePanel extends ImagePanel {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Red5LoggerFactory.getLogger(UploadableImagePanel.class, getWebAppRootKey());
	private final FileUploadField fileUploadField = new FileUploadField("image", new ListModel<FileUpload>());

	public UploadableImagePanel(String id) {
		super(id);
	}

	protected abstract void processImage(StoredFile sf, File f) throws Exception;

	@Override
	protected void onInitialize() {
		super.onInitialize();
		final Form<Void> form = new Form<>("form");
		form.setMultiPart(true);
		form.setMaxSize(Bytes.bytes(getMaxUploadSize()));
		// Model is necessary here to avoid writing image to the User object
		form.add(fileUploadField);
		form.add(new UploadProgressBar("progress", form, fileUploadField));
		fileUploadField.add(new AjaxFormSubmitBehavior(form, "change") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target) {
				FileUpload fu = fileUploadField.getFileUpload();
				if (fu != null) {
					File temp = null;
					try {
						temp = fu.writeToTempFile();
						StoredFile sf = new StoredFile(fu.getClientFileName(), temp);
						if (sf.isImage()) {
							processImage(sf, temp);
						} else {
							//TODO display error
						}
					} catch (Exception e) {
						// TODO display error
						log.error("Error", e);
					} finally {
						if (temp != null && temp.exists()) {
							log.debug("Temp file was deleted ? {}", temp.delete());
						}
						fu.closeStreams();
						fu.delete();
					}
				}
				update();
				target.add(profile, form);
			}
		});
		add(form.setOutputMarkupId(true));
		add(BootstrapFileUploadBehavior.INSTANCE);
	}
}
