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
package org.apache.openmeetings.web.room.menu;

import java.io.Serializable;
import java.util.List;

import org.apache.openmeetings.db.dao.room.ExtraMenuDao;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.entity.room.ExtraMenu;
import org.apache.openmeetings.db.entity.room.RoomGroup;
import org.apache.openmeetings.db.entity.user.Group;
import org.apache.openmeetings.web.common.menu.OmMenuItem;
import org.apache.openmeetings.web.room.RoomPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.injection.Injector;

import jakarta.inject.Inject;


public class ExtrasSubMenu implements Serializable {
	private static final long serialVersionUID = 1L;
	private final RoomPanel room;
	private OmMenuItem extraMenu;

	@Inject
	private RoomDao roomDao;
	@Inject
	private ExtraMenuDao menuDao;

	public ExtrasSubMenu(final RoomPanel room) {
		this.room = room;
	}

	public void init() {
		Injector.get().inject(this);
		extraMenu = new OmMenuItem(room.getString("menu.extras"), null, false);
		List<Long> groups = roomDao.get(room.getRoom().getId()).getGroups().stream()
				.map(RoomGroup::getGroup)
				.map(Group::getId)
				.toList();
		for (ExtraMenu em : menuDao.getByGroups(groups)) {
			extraMenu.add(new OmMenuItem(em.getName(), em.getDescription()) {
				private static final long serialVersionUID = 1L;

				@Override
				public void onClick(AjaxRequestTarget target) {
					target.appendJavaScript(String.format("window.open('%s', '_blank');", em.getLink()));
				}
			});
		}
		extraMenu.setVisible(extraMenu.hasItems());
	}

	OmMenuItem getMenu() {
		return extraMenu;
	}
}
