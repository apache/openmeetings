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

import static org.apache.openmeetings.web.common.BasePanel.EVT_CLICK;

import org.apache.openmeetings.db.dao.file.FileItemLogDao;
import org.apache.openmeetings.db.entity.file.BaseFileItem;
import org.apache.openmeetings.db.entity.record.Recording;
import org.apache.openmeetings.db.entity.record.Recording.Status;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;

import jakarta.inject.Inject;

public class FileItemPanel extends FolderPanel {
	private static final long serialVersionUID = 1L;
	private final WebMarkupContainer errors = new WebMarkupContainer("errors");

	@Inject
	private FileItemLogDao fileLogDao;

	public FileItemPanel(String id, final IModel<BaseFileItem> model, final FileTreePanel fileTreePanel) {
		super(id, model, fileTreePanel);
		BaseFileItem f = model.getObject();
		long errorCount = fileLogDao.countErrors(f);
		boolean visible = errorCount != 0;
		if (BaseFileItem.Type.RECORDING == f.getType()) {
			Recording r = (Recording)f;
			visible |= (Status.RECORDING != r.getStatus() && Status.CONVERTING != r.getStatus() && !f.exists());
		} else {
			visible |= !f.exists();
		}
		errors.add(new AjaxEventBehavior(EVT_CLICK) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				fileTreePanel.errorsDialog.setDefaultModel(model);
				fileTreePanel.errorsDialog.show(target);
			}
		}).setVisible(visible);
		add(errors);
	}
}
