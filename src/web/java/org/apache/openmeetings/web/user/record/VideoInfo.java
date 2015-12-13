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
import static org.apache.openmeetings.util.OmFileHelper.isRecordingExists;
import static org.apache.openmeetings.web.app.Application.getBean;

import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.entity.record.FlvRecording;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.web.util.AjaxDownload;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.resource.FileResourceStream;

public class VideoInfo extends Panel {
	private static final long serialVersionUID = 1L;
	private final WebMarkupContainer container = new WebMarkupContainer("container");
	private final Button dAVI = new Button("dAVI");
	private final Button dFLV = new Button("dFLV");
	private final AjaxDownload download = new AjaxDownload();
	private final IModel<FlvRecording> rm = new CompoundPropertyModel<FlvRecording>(new FlvRecording());
	private final IModel<String> roomName = Model.of((String)null);

	public VideoInfo(String id) {
		this(id, null);
	}
	
	public VideoInfo(String id, FlvRecording r) {
		super(id);
		add(container.setOutputMarkupId(true));
		setDefaultModel(rm);
		
		container.add(new Label("fileName"), new Label("fileSize"), new Label("recordEnd"), new Label("room_id", roomName),
				dFLV.setEnabled(false), dAVI.setEnabled(false));
		dAVI.add(new AjaxEventBehavior("click"){
			private static final long serialVersionUID = 1L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				download.setFileName(rm.getObject().getAlternateDownload());
				download.setResourceStream(new FileResourceStream(getRecording(rm.getObject().getAlternateDownload())));
				download.initiate(target);
			}
		});
		dFLV.add(new AjaxEventBehavior("click"){
			private static final long serialVersionUID = 1L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				download.setFileName(rm.getObject().getFileHash());
				download.setResourceStream(new FileResourceStream(getRecording(rm.getObject().getFileHash())));
				download.initiate(target);
			}
		});
		add(download);
		update(null, r);
	}
	
	public VideoInfo update(AjaxRequestTarget target, FlvRecording r) {
		rm.setObject(r == null ? new FlvRecording() : r);
		try {
			Room room = getBean(RoomDao.class).get(r.getRoom_id());
			roomName.setObject(room.getName());
		} catch (Exception e) {
			//no-op
		}
		
		dAVI.setEnabled(isRecordingExists(rm.getObject().getAlternateDownload()));
		dFLV.setEnabled(isRecordingExists(rm.getObject().getFileHash()));
		if (target != null) {
			target.add(container);
		}
		
		return this;
	}
}
