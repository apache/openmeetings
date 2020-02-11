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

import static java.time.Duration.ZERO;
import static org.apache.openmeetings.util.OmFileHelper.EXTENSION_MP4;
import static org.apache.openmeetings.util.OmFileHelper.getRecordingChunk;
import static org.apache.openmeetings.web.app.WebSession.getUserId;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.openmeetings.core.converter.IRecordingConverter;
import org.apache.openmeetings.core.converter.InterviewConverter;
import org.apache.openmeetings.core.converter.RecordingConverter;
import org.apache.openmeetings.db.dao.record.RecordingChunkDao;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.entity.file.BaseFileItem;
import org.apache.openmeetings.db.entity.record.Recording;
import org.apache.openmeetings.db.entity.record.Recording.Status;
import org.apache.openmeetings.db.entity.record.RecordingChunk;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.web.common.InvitationDialog;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.AjaxDownloadBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.resource.FileSystemResource;
import org.apache.wicket.spring.injection.annot.SpringBean;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.dropdown.SplitButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.IconType;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesome5IconType;

public class VideoInfo extends Panel {
	private static final long serialVersionUID = 1L;
	private final Form<Void> form = new Form<>("form");
	private final SplitButton downloadBtn = new SplitButton("downloadBtn", Model.of("")) {
		private static final long serialVersionUID = 1L;

		private AbstractLink createLink(String markupId, IModel<String> model, String ext) {
			return new BootstrapAjaxLink<>(markupId, model, Buttons.Type.Outline_Primary, model) {
				private static final long serialVersionUID = 1L;

				@Override
				public boolean isEnabled() {
					BaseFileItem r = rm.getObject();
					return r != null && r.exists(EXTENSION_MP4) && !r.isReadOnly();
				}

				@Override
				public void onClick(AjaxRequestTarget target) {
					download.initiate(target);
				}
			}.setIconType(FontAwesome5IconType.download_s);
		}

		@Override
		protected List<AbstractLink> newSubMenuButtons(String buttonMarkupId) {
			return List.of(createLink(buttonMarkupId, Model.of(EXTENSION_MP4), EXTENSION_MP4));
		}

		@Override
		protected AbstractLink newBaseButton(String markupId, IModel<String> labelModel, IModel<IconType> iconTypeModel) {
			return createLink(markupId, Model.of(EXTENSION_MP4), EXTENSION_MP4);
		}
	};
	private final BootstrapAjaxButton reConvert = new BootstrapAjaxButton("re-convert", new ResourceModel("1600"), form, Buttons.Type.Outline_Warning) {
		private static final long serialVersionUID = 1L;

		@Override
		protected void onSubmit(AjaxRequestTarget target) {
			final IRecordingConverter converter = isInterview ? interviewConverter : recordingConverter;
			new Thread() {
				@Override
				public void run() {
					converter.startConversion(rm.getObject());
				}
			}.start();
		}
	};
	private final AjaxDownloadBehavior download = new AjaxDownloadBehavior(new IResource() {
		private static final long serialVersionUID = 1L;

		@Override
		public void respond(Attributes attributes) {
			File f = rm.getObject().getFile(EXTENSION_MP4);
			new FileSystemResource(f.toPath()) {
				private static final long serialVersionUID = 1L;

				@Override
				protected ResourceResponse createResourceResponse(Attributes attr, Path path) {
					ResourceResponse response = super.createResourceResponse(attr, path);
					response.setCacheDuration(ZERO);
					return response;
				}
			}.respond(attributes);
		}
	});
	private final IModel<Recording> rm = new CompoundPropertyModel<>(new Recording());
	private final IModel<String> roomName = Model.of((String)null);
	private boolean isInterview = false;
	private InvitationDialog invite;
	RecordingInvitationForm rif = new RecordingInvitationForm("form");
	private final BootstrapAjaxButton share = new BootstrapAjaxButton("share", new ResourceModel("button.label.share"), form, Buttons.Type.Outline_Success) {
		private static final long serialVersionUID = 1L;

		@Override
		protected void onSubmit(AjaxRequestTarget target) {
			rif.setRecordingId(rm.getObject().getId());
			invite.updateModel(target);
			invite.show(target);
		}
	};
	@SpringBean
	private InterviewConverter interviewConverter;
	@SpringBean
	private RecordingConverter recordingConverter;
	@SpringBean
	private RoomDao roomDao;
	@SpringBean
	private RecordingChunkDao chunkDao;

	public VideoInfo(String id) {
		super(id);
		setDefaultModel(rm);
	}

	public VideoInfo update(AjaxRequestTarget target, BaseFileItem _r) {
		boolean reConvEnabled = false;
		boolean exists = false;
		if (_r instanceof Recording) {
			Recording r = (Recording)_r;
			isInterview = r.isInterview();
			rm.setObject(r);
			exists = r.exists();
			try {
				String name = null;
				if (r.getRoomId() != null) {
					Room room = roomDao.get(r.getRoomId());
					if (room != null) {
						name = room.getName();
					}
				}
				roomName.setObject(name);
			} catch (Exception e) {
				//no-op
			}

			if (r.getOwnerId() != null && r.getOwnerId().equals(getUserId()) && r.getStatus() != Status.RECORDING && r.getStatus() != Status.CONVERTING) {
				List<RecordingChunk> chunks = chunkDao.getByRecording(r.getId())
						.stream()
						.filter(chunk -> r.getRoomId() == null || !getRecordingChunk(r.getRoomId(), chunk.getStreamName()).exists())
						.collect(Collectors.toList());
				reConvEnabled = !chunks.isEmpty();
			}
		}
		reConvert.setEnabled(reConvEnabled);
		downloadBtn.setEnabled(exists && !_r.isReadOnly());
		share.setEnabled(exists && !_r.isReadOnly());
		if (target != null) {
			target.add(form);
		}

		return this;
	}

	@Override
	protected void onDetach() {
		rm.detach();
		roomName.detach();
		super.onDetach();
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		add(form.setOutputMarkupId(true));
		form.add(new Label("name")
				, new Label("duration")
				, new Label("recordEnd")
				, new Label("roomName", roomName)
				, downloadBtn.setEnabled(false)
				, reConvert.setIconType(FontAwesome5IconType.sync_alt_s).setEnabled(false)
				, share.setIconType(FontAwesome5IconType.share_alt_s).setEnabled(false));
		add(download);
		add(invite = new InvitationDialog("invitation", rif));
		rif.setDialog(invite);

		update(null, null);
	}

	public VideoInfo setShowShare(boolean visible) {
		reConvert.setVisible(visible);
		share.setVisible(visible);
		return this;
	}
}
