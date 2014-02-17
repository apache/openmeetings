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
package org.apache.openmeetings.web.user.record;

import static org.apache.openmeetings.util.OmFileHelper.MP4_EXTENSION;
import static org.apache.openmeetings.util.OmFileHelper.isRecordingExists;
import static org.apache.openmeetings.web.app.Application.getBean;

import org.apache.openmeetings.db.dao.record.FlvRecordingLogDao;
import org.apache.openmeetings.db.entity.record.FlvRecording;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;

public class RecordingItemPanel extends RecordingPanel {
	private static final long serialVersionUID = 1L;

	public RecordingItemPanel(String id, final IModel<FlvRecording> model, final RecordingErrorsDialog errorsDialog) {
		super(id, model);
		long errorCount = getBean(FlvRecordingLogDao.class).countErrors(model.getObject().getFlvRecordingId());
		boolean visible = errorCount != 0 || !isRecordingExists(model.getObject().getFileHash() + MP4_EXTENSION);
		item.add(new WebMarkupContainer("errors").add(new AjaxEventBehavior("click") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				errorsDialog.setDefaultModel(model);
				errorsDialog.open(target);
			}
		}).setVisible(visible));
	}
}
