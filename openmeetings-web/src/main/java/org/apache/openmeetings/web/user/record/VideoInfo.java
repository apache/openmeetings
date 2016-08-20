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

import static org.apache.openmeetings.util.OmFileHelper.EXTENSION_AVI;
import static org.apache.openmeetings.util.OmFileHelper.EXTENSION_FLV;
import static org.apache.openmeetings.util.OmFileHelper.EXTENSION_MP4;
import static org.apache.openmeetings.util.OmFileHelper.getRecordingMetaData;
import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.WebSession.getUserId;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.openmeetings.core.converter.IRecordingConverter;
import org.apache.openmeetings.core.converter.InterviewConverter;
import org.apache.openmeetings.core.converter.RecordingConverter;
import org.apache.openmeetings.db.dao.record.RecordingMetaDataDao;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.entity.record.Recording;
import org.apache.openmeetings.db.entity.record.Recording.Status;
import org.apache.openmeetings.db.entity.record.RecordingMetaData;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.web.common.InvitationDialog;
import org.apache.openmeetings.web.util.AjaxDownload;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.resource.FileResourceStream;

import com.googlecode.wicket.jquery.ui.JQueryIcon;
import com.googlecode.wicket.jquery.ui.form.button.AjaxButton;
import com.googlecode.wicket.jquery.ui.form.button.AjaxSplitButton;
import com.googlecode.wicket.jquery.ui.widget.menu.IMenuItem;
import com.googlecode.wicket.jquery.ui.widget.menu.MenuItem;

public class VideoInfo extends Panel {
	private static final long serialVersionUID = 1L;
	private final Form<Void> form = new Form<Void>("form");
	private final AjaxSplitButton downloadBtn = new AjaxSplitButton("downloadBtn", new ArrayList<IMenuItem>());
	private final AjaxButton reConvert = new AjaxButton("re-convert") {
		private static final long serialVersionUID = 1L;

		@Override
		protected String getIcon() {
			return JQueryIcon.REFRESH;
		};
		
		@Override
		protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
			final IRecordingConverter converter = getBean(isInterview ? InterviewConverter.class : RecordingConverter.class);
			new Thread() {
				@Override
				public void run() {
					converter.startConversion(rm.getObject().getId());
				}
			}.start();
		}
	};
	private final AjaxDownload download = new AjaxDownload();
	private final IModel<Recording> rm = new CompoundPropertyModel<Recording>(new Recording());
	private final IModel<String> roomName = Model.of((String)null);
	private boolean isInterview = false;
	private final InvitationDialog invite;
	RecordingInvitationForm rif = new RecordingInvitationForm("form");
	private final AjaxButton share = new AjaxButton("share") {
		private static final long serialVersionUID = 1L;

		@Override
		protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
			rif.setRecordingId(rm.getObject().getId());
			invite.open(target);
		}
	};

	public VideoInfo(String id) {
		this(id, null);
	}
	
	public VideoInfo(String id, Recording r) {
		super(id);
		add(form.setOutputMarkupId(true));
		setDefaultModel(rm);
		
		form.add(new Label("name"), new Label("duration"), new Label("recordEnd"), new Label("roomName", roomName),
				downloadBtn.setEnabled(false), reConvert.setEnabled(false), share.setEnabled(false));
		add(download);
		add(invite = new InvitationDialog("invitation", rif));
		rif.setDialog(invite);

		update(null, r);
	}
	
	public VideoInfo update(AjaxRequestTarget target, Recording _r) {
		Recording r = _r == null ? new Recording() : _r;
		rm.setObject(r);
		try {
			String name = null;
			if (r.getRoomId() != null) {
				Room room = getBean(RoomDao.class).get(r.getRoomId());
				if (room != null) {
					name = room.getName();
					isInterview = Room.Type.interview == room.getType();
				}
			}
			roomName.setObject(name);
		} catch (Exception e) {
			//no-op
		}
		
		boolean reConvEnabled = false;
		if (r.getOwnerId() != null && r.getOwnerId().equals(getUserId()) && r.getStatus() != Status.RECORDING && r.getStatus() != Status.CONVERTING) {
			List<RecordingMetaData> metas = getBean(RecordingMetaDataDao.class).getByRecording(r.getId());
			reconvLabel:
			if (!metas.isEmpty()) {
				for (RecordingMetaData meta : metas) {
					if (r.getRoomId() == null || !getRecordingMetaData(r.getRoomId(), meta.getStreamName()).exists()) {
						break reconvLabel;
					}
				}
				reConvEnabled = true;
			}
		}
		reConvert.setEnabled(reConvEnabled);
		boolean exists = r.exists() || r.exists(EXTENSION_AVI);
		downloadBtn.setEnabled(exists);
		share.setEnabled(exists);
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
		downloadBtn.setDefaultModelObject(newDownloadMenuList());
	}
	
	private List<IMenuItem> newDownloadMenuList() {
		List<IMenuItem> list = new ArrayList<>();

		//mp4
		list.add(new MenuItem(EXTENSION_MP4, JQueryIcon.ARROWTHICKSTOP_1_S) {
			private static final long serialVersionUID = 1L;
			
			@Override
			public boolean isEnabled() {
				Recording r = rm.getObject();
				return r != null && r.exists(EXTENSION_MP4);
			}
			
			@Override
			public void onClick(AjaxRequestTarget target) {
				File f = rm.getObject().getFile(EXTENSION_MP4);
				download.setFileName(f.getName());
				download.setResourceStream(new FileResourceStream(f));
				download.initiate(target);
			}
		});
		//avi
		list.add(new MenuItem(getString("884"), JQueryIcon.ARROWTHICKSTOP_1_S) {
			private static final long serialVersionUID = 1L;
			
			@Override
			public boolean isEnabled() {
				Recording r = rm.getObject();
				return r != null && r.exists(EXTENSION_AVI);
			}
			
			@Override
			public void onClick(AjaxRequestTarget target) {
				File f = rm.getObject().getFile(EXTENSION_AVI);
				download.setFileName(f.getName());
				download.setResourceStream(new FileResourceStream(f));
				download.initiate(target);
			}
		});
		//flv
		list.add(new MenuItem(getString("883"), JQueryIcon.ARROWTHICKSTOP_1_S) {
			private static final long serialVersionUID = 1L;
			
			@Override
			public boolean isEnabled() {
				Recording r = VideoInfo.this.rm.getObject();
				return r != null && r.exists(EXTENSION_FLV);
			}
			
			@Override
			public void onClick(AjaxRequestTarget target) {
				File f = rm.getObject().getFile(EXTENSION_FLV);
				download.setFileName(f.getName());
				download.setResourceStream(new FileResourceStream(f));
				download.initiate(target);
			}
		});
		return list;
	}
}
