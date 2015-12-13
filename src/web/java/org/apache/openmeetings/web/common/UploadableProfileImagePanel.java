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

import static org.apache.openmeetings.web.app.Application.getBean;

import java.util.List;

import org.apache.openmeetings.converter.GenerateImage;
import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.util.StoredFile;
import org.apache.openmeetings.web.util.BootstrapFileUploadBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.extensions.ajax.markup.html.form.upload.UploadProgressBar;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.lang.Bytes;

public class UploadableProfileImagePanel extends ProfileImagePanel {
	private static final long serialVersionUID = 1L;
	private FileUploadField fileUploadField;
	
	public UploadableProfileImagePanel(String id, final long userId) {
		super(id, userId);
		final Form<Void> form = new Form<Void>("form");
		form.setMultiPart(true);
		form.setMaxSize(Bytes.bytes(getBean(ConfigurationDao.class).getMaxUploadSize()));
		// Model is necessary here to avoid writing image to the User object
		form.add(fileUploadField = new FileUploadField("image", new IModel<List<FileUpload>>() {
			private static final long serialVersionUID = 1L;

			//FIXME this need to be eliminated
			public void detach() {
			}
			
			public void setObject(List<FileUpload> object) {
			}
			
			public List<FileUpload> getObject() {
				return null;
			}
		}));
		form.add(new UploadProgressBar("progress", form, fileUploadField));
		fileUploadField.add(new AjaxFormSubmitBehavior(form, "onchange") {
			private static final long serialVersionUID = 2160216679027859231L;

			@Override
			protected void onSubmit(AjaxRequestTarget target) {
				FileUpload fu = fileUploadField.getFileUpload();
				if (fu != null) {
					StoredFile sf = new StoredFile(fu.getClientFileName());
					if (sf.isImage()) {
						boolean asIs = sf.isAsIs();
						try {
							//FIXME need to work with InputStream !!!
							getBean(GenerateImage.class)
								.convertImageUserProfile(fu.writeToTempFile(), userId, asIs);
						} catch (Exception e) {
							// TODO display error
							e.printStackTrace();
						}
					} else {
						//TODO display error
					}
				}
				target.add(profile, form);
			}
		});
		add(form.setOutputMarkupId(true));
		add(BootstrapFileUploadBehavior.INSTANCE);
	}
}
