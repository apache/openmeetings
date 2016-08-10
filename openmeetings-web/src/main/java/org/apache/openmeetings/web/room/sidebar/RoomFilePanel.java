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
package org.apache.openmeetings.web.room.sidebar;

import org.apache.openmeetings.db.entity.file.FileItem;
import org.apache.openmeetings.web.common.tree.FileTreePanel;
import org.apache.openmeetings.web.room.RoomPanel;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;

public class RoomFilePanel extends FileTreePanel {
	private static final long serialVersionUID = 1L;
	private final RoomPanel room;

	public RoomFilePanel(String id, RoomPanel room) {
		super(id, room.getRoom().getId());
		this.room = room;
	}
	
	@Override
	public void updateSizes() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void update(AjaxRequestTarget target, FileItem f) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected String getContainment() {
		return "";
	}
	
	@Override
	protected Component getUpload(String id) {
		Component u = super.getUpload(id);
		u.setVisible(true);
		u.add(new AjaxEventBehavior("click") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				room.getSidebar().showUpload(target);
			}
		});
		return u;
	}
}
