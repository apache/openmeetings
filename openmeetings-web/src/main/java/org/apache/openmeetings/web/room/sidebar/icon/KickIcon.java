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
package org.apache.openmeetings.web.room.sidebar.icon;

import static org.apache.openmeetings.web.room.sidebar.RoomSidebar.FUNC_ACTION;

import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.db.entity.room.Room.Right;
import org.apache.openmeetings.web.room.RoomPanel.Action;

public class KickIcon extends ClientIcon {
	private static final long serialVersionUID = 1L;

	public KickIcon(String id, String uid) {
		super(id, uid);
		mainCssClass = "kick ui-icon-closethick ";
	}

	@Override
	protected String getTitle() {
		return getString("603");
	}

	@Override
	protected boolean isClickable() {
		return !isSelf() && roomHasRight(Right.moderator) && !hasRight(Right.superModerator);
	}

	@Override
	protected String getScript() {
		Client c = getClient();
		return c == null ? "" : String.format("%s('%s', '%s');", FUNC_ACTION, Action.kick.name(), c.getUid());
	}

	@Override
	public void internalUpdate() {
		super.internalUpdate();
		setVisible(isClickable());
	}
}
