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

import static org.apache.openmeetings.web.app.WebSession.getDateFormat;

import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.entity.file.BaseFileItem;
import org.apache.openmeetings.db.entity.record.Recording;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;

import jakarta.inject.Inject;


public class VideoInfo extends Panel {
	private static final long serialVersionUID = 1L;
	private final Form<Void> form = new Form<>("form");
	private final Label dateLbl = new Label("recordEnd", Model.of(""));
	private final Label roomNameLbl = new Label("roomName", Model.of(""));

	@Inject
	private RoomDao roomDao;

	public VideoInfo(String id) {
		super(id);
		setDefaultModel(new CompoundPropertyModel<>(new Recording()));
	}

	public VideoInfo update(AjaxRequestTarget target, BaseFileItem file) {
		if (file instanceof Recording r) {
			setDefaultModelObject(file);
			try {
				String name = null;
				if (r.getRoomId() != null) {
					Room room = roomDao.get(r.getRoomId());
					if (room != null) {
						name = room.getName();
					}
				}
				dateLbl.setDefaultModelObject(getDateFormat().format(r.getRecordEnd()));
				roomNameLbl.setDefaultModelObject(name);
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
	protected void onInitialize() {
		super.onInitialize();
		add(form.setOutputMarkupId(true));
		form.add(new Label("name")
				, new Label("duration")
				, dateLbl
				, roomNameLbl);

		update(null, null);
	}
}
