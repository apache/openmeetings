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

import static org.apache.openmeetings.web.app.WebSession.getUserId;
import static org.apache.openmeetings.util.OmFileHelper.getHumanSize;
import static org.apache.openmeetings.util.OmFileHelper.getRecordingChunk;

import java.util.List;

import org.apache.openmeetings.core.converter.IRecordingConverter;
import org.apache.openmeetings.core.converter.InterviewConverter;
import org.apache.openmeetings.core.converter.RecordingConverter;
import org.apache.openmeetings.db.dao.record.RecordingChunkDao;
import org.apache.openmeetings.db.dao.record.RecordingDao;
import org.apache.openmeetings.db.dto.record.RecordingContainerData;
import org.apache.openmeetings.db.entity.file.BaseFileItem;
import org.apache.openmeetings.db.entity.record.Recording;
import org.apache.openmeetings.db.entity.record.Recording.Status;
import org.apache.openmeetings.web.common.InvitationDialog;
import org.apache.openmeetings.web.common.NameDialog;
import org.apache.openmeetings.web.common.UserBasePanel;
import org.apache.openmeetings.web.common.tree.FileTreePanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;


import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import jakarta.inject.Inject;

public class RecordingsPanel extends UserBasePanel {
	private static final long serialVersionUID = 1L;
	private static final String INVITE_DIALOG_ID = "recordingInviteDialog";
	private final VideoPlayer video = new VideoPlayer("video");
	private final VideoInfo info = new VideoInfo("info");
	private FileTreePanel fileTree;
	private InvitationDialog invite;
	private RecordingInvitationForm rif = new RecordingInvitationForm("form", INVITE_DIALOG_ID);

	@Inject
	private RecordingDao recDao;
	@Inject
	private InterviewConverter interviewConverter;
	@Inject
	private RecordingConverter recordingConverter;
	@Inject
	private RecordingChunkDao chunkDao;

	public RecordingsPanel(String id) {
		super(id);
	}

	@Override
	protected void onInitialize() {
		final NameDialog addFolder = new NameDialog("addFolder", getString("712")) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target) {
				fileTree.createFolder(target, getModelObject());
				super.onSubmit(target);
			}
		};
		add(fileTree = new FileTreePanel("tree", null, addFolder) {
			private static final long serialVersionUID = 1L;

			@Override
			public void updateSizes() {
				RecordingContainerData sizeData = recDao.getContainerData(getUserId());
				if (sizeData != null) {
					homeSize.setObject(getHumanSize(sizeData.getUserHomeSize()));
					publicSize.setObject(getHumanSize(sizeData.getPublicFileSize()));
				}
			}

			@Override
			protected void update(AjaxRequestTarget target, BaseFileItem f) {
				video.update(target, f);
				info.update(target, f);
			}

			@Override
			protected List<AbstractLink> newOtherButtons(String markupId) {
				return List.of(new BootstrapAjaxLink<>(markupId, Model.of(""), Buttons.Type.Outline_Warning, new ResourceModel("1600")) {
					private static final long serialVersionUID = 1L;
					private boolean isInterview = false;

					@Override
					protected void onConfigure() {
						super.onConfigure();
						boolean enabled = false;
						isInterview = false;
						if (getSelected().size() == 1 && BaseFileItem.Type.RECORDING == getLastSelected().getType()) {
							Recording r = (Recording)getLastSelected();
							isInterview = r.isInterview();

							if (r.getRoomId() != null && r.getOwnerId() != null && r.getOwnerId().equals(getUserId()) && r.getStatus() != Status.RECORDING && r.getStatus() != Status.CONVERTING) {
								// will enable re-conversion if at least some of the chunks are OK
								enabled = chunkDao.getByRecording(r.getId())
										.stream()
										.anyMatch(chunk -> getRecordingChunk(r.getRoomId(), chunk.getStreamName()).exists());
							}
						}
						setEnabled(enabled);
					}

					@Override
					public void onClick(AjaxRequestTarget target) {
						final IRecordingConverter converter = isInterview ? interviewConverter : recordingConverter;
						new Thread(() -> converter.startConversion((Recording)getLastSelected())).start();
					}
				}, new BootstrapAjaxLink<>(markupId, Model.of(""), Buttons.Type.Outline_Success, new ResourceModel("button.label.share")) {
					private static final long serialVersionUID = 1L;

					@Override
					protected void onConfigure() {
						super.onConfigure();
						boolean enabled = false;
						if (getSelected().size() == 1 && BaseFileItem.Type.RECORDING == getLastSelected().getType()) {
							Recording r = (Recording)getLastSelected();
							if (!r.isReadOnly() && r.exists()) {
								enabled = true;
							}
						}
						setEnabled(enabled);
					}

					@Override
					public void onClick(AjaxRequestTarget target) {
						rif.setRecordingId(getLastSelected().getId());
						invite.updateModel(target);
						invite.show(target);
					}
				});
			}
		});
		add(video, info, addFolder);
		invite = new InvitationDialog(INVITE_DIALOG_ID, rif);
		add(invite);
		rif.setDialog(invite);

		super.onInitialize();
	}
}
