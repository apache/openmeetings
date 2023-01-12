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

import org.apache.openmeetings.db.dao.record.RecordingDao;
import org.apache.openmeetings.web.common.InvitationForm;
import org.apache.wicket.ajax.AjaxRequestTarget;

import jakarta.inject.Inject;


public class RecordingInvitationForm extends InvitationForm {
	private static final long serialVersionUID = 1L;
	private Long recordingId;

	@Inject
	private RecordingDao recDao;

	public RecordingInvitationForm(String id, String dropDownParentId) {
		super(id, dropDownParentId);
	}

	@Override
	protected void onInitialize() {
		add(recipients);
		super.onInitialize();
	}

	@Override
	public void updateModel(AjaxRequestTarget target) {
		super.updateModel(target);
		getModelObject().setRecording(recDao.get(recordingId));
	}

	public void setRecordingId(Long recordingId) {
		this.recordingId = recordingId;
	}
}
