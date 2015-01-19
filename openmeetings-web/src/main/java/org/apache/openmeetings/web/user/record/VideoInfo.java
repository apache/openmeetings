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

import static org.apache.openmeetings.util.OmFileHelper.getRecording;
import static org.apache.openmeetings.util.OmFileHelper.getRecordingMetaData;
import static org.apache.openmeetings.util.OmFileHelper.isRecordingExists;
import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.WebSession.getUserId;

import java.util.List;

import org.apache.openmeetings.core.converter.FlvInterviewConverter;
import org.apache.openmeetings.core.converter.FlvRecorderConverter;
import org.apache.openmeetings.core.converter.IRecordingConverter;
import org.apache.openmeetings.db.dao.record.FlvRecordingMetaDataDao;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.entity.record.FlvRecording;
import org.apache.openmeetings.db.entity.record.FlvRecording.Status;
import org.apache.openmeetings.db.entity.record.FlvRecordingMetaData;
import org.apache.openmeetings.db.entity.room.Room;
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

public class VideoInfo extends Panel {
	private static final long serialVersionUID = 1L;
	private final Form<Void> form = new Form<Void>("form");
	private final AjaxButton downloadAvi = new AjaxButton("dAVI") {
		private static final long serialVersionUID = 1L;

		@Override
		protected String getIcon() {
			return JQueryIcon.ARROWTHICKSTOP_1_S;
		};
		
		@Override
		protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
			download.setFileName(rm.getObject().getAlternateDownload());
			download.setResourceStream(new FileResourceStream(getRecording(rm.getObject().getAlternateDownload())));
			download.initiate(target);
		}
	};
	private final AjaxButton downloadFlv = new AjaxButton("dFLV") {
		private static final long serialVersionUID = 1L;

		@Override
		protected String getIcon() {
			return JQueryIcon.ARROWTHICKSTOP_1_S;
		};
		
		@Override
		protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
			download.setFileName(rm.getObject().getFileHash());
			download.setResourceStream(new FileResourceStream(getRecording(rm.getObject().getFileHash())));
			download.initiate(target);
		}
	};
	private final AjaxButton reConvert = new AjaxButton("re-convert") {
		private static final long serialVersionUID = 1L;

		@Override
		protected String getIcon() {
			return JQueryIcon.REFRESH;
		};
		
		@Override
		protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
			final IRecordingConverter converter = getBean(isInterview ? FlvInterviewConverter.class : FlvRecorderConverter.class);
			new Thread() {
				public void run() {
					converter.startConversion(rm.getObject().getId());
				}
			}.start();
		}
	};
	private final AjaxDownload download = new AjaxDownload();
	private final IModel<FlvRecording> rm = new CompoundPropertyModel<FlvRecording>(new FlvRecording());
	private final IModel<String> roomName = Model.of((String)null);
	private boolean isInterview = false;

	public VideoInfo(String id) {
		this(id, null);
	}
	
	public VideoInfo(String id, FlvRecording r) {
		super(id);
		add(form.setOutputMarkupId(true));
		setDefaultModel(rm);
		
		form.add(new Label("fileName"), new Label("duration"), new Label("recordEnd"), new Label("roomName", roomName),
				downloadFlv.setEnabled(false), downloadAvi.setEnabled(false), reConvert.setEnabled(false));
		add(download);
		update(null, r);
	}
	
	public VideoInfo update(AjaxRequestTarget target, FlvRecording _r) {
		FlvRecording r = _r == null ? new FlvRecording() : _r;
		rm.setObject(r);
		try {
			String name = null;
			if (r.getRoomId() != null) {
				Room room = getBean(RoomDao.class).get(r.getRoomId());
				if (room != null) {
					name = room.getName();
					isInterview = room.getRoomtype().getId() == 4;
				}
			}
			roomName.setObject(name);
		} catch (Exception e) {
			//no-op
		}
		
		boolean reConvEnabled = false;
		if (r.getOwnerId() != null && getUserId() == r.getOwnerId() && r.getStatus() != Status.PROCESSING) {
			List<FlvRecordingMetaData> metas = getBean(FlvRecordingMetaDataDao.class).getByRecording(r.getId());
			reconvLabel:
			if (!metas.isEmpty()) {
				for (FlvRecordingMetaData meta : metas) {
					if (!getRecordingMetaData(r.getRoomId(), meta.getStreamName()).exists()) {
						break reconvLabel;
					}
				}
				reConvEnabled = true;
			}
		}
		reConvert.setEnabled(reConvEnabled);
		downloadAvi.setEnabled(isRecordingExists(r.getAlternateDownload()));
		downloadFlv.setEnabled(isRecordingExists(r.getFileHash()));
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
}
