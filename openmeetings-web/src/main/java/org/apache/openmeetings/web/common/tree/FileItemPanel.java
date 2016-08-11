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
package org.apache.openmeetings.web.common.tree;

import static org.apache.openmeetings.web.app.Application.getBean;

import org.apache.openmeetings.db.dao.record.RecordingLogDao;
import org.apache.openmeetings.db.entity.file.FileItem;
import org.apache.openmeetings.db.entity.record.Recording;
import org.apache.openmeetings.db.entity.record.Recording.Status;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;

public class FileItemPanel extends FolderPanel {
	private static final long serialVersionUID = 1L;
	private final WebMarkupContainer errors = new WebMarkupContainer("errors");

	public FileItemPanel(String id, final IModel<? extends FileItem> model, final FileTreePanel fileTreePanel) {
		super(id, model, fileTreePanel);
		if (model.getObject() instanceof Recording) {
			Recording r = (Recording)model.getObject();
			long errorCount = getBean(RecordingLogDao.class).countErrors(r.getId());
			boolean visible = errorCount != 0 || (Status.RECORDING != r.getStatus() && Status.CONVERTING != r.getStatus() && !r.exists());
			errors.add(new AjaxEventBehavior("click") {
				private static final long serialVersionUID = 1L;
	
				@Override
				protected void onEvent(AjaxRequestTarget target) {
					fileTreePanel.errorsDialog.setDefaultModel(model);
					fileTreePanel.errorsDialog.open(target);
				}
			}).setVisible(visible);
		} else {
			errors.setVisible(false);
		}
		drag.add(errors);
	}
}
