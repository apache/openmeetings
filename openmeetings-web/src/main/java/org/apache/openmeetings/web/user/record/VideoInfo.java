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

import org.apache.openmeetings.core.converter.InterviewConverter;
import org.apache.openmeetings.core.converter.RecordingConverter;
import org.apache.openmeetings.db.dao.record.RecordingChunkDao;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.entity.file.BaseFileItem;
import org.apache.openmeetings.db.entity.record.Recording;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class VideoInfo extends Panel {
	private static final long serialVersionUID = 1L;
	private final Form<Void> form = new Form<>("form");
	private final IModel<Recording> rm = new CompoundPropertyModel<>(new Recording());
	private final IModel<String> roomName = Model.of((String)null);
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
		if (_r instanceof Recording) {
			Recording r = (Recording)_r;
			rm.setObject(r);
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
		}
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
				, new Label("roomName", roomName));

		update(null, null);
	}
}
