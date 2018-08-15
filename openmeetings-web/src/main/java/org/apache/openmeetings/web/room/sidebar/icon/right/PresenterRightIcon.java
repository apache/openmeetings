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
package org.apache.openmeetings.web.room.sidebar.icon.right;

import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.room.Room.Right;
import org.apache.openmeetings.db.entity.room.Room.RoomElement;

public class PresenterRightIcon extends RoomRightIcon {
	private static final long serialVersionUID = 1L;

	public PresenterRightIcon(String id, String uid) {
		super(id, uid, Right.presenter);
		mainCssClass = "right presenter bumper ";
	}

	@Override
	protected String getTitle() {
		return getString(String.format("ulist.right.presenter.%s", (isSelf() ? "request" : (hasRight() ? "revoke" : "grant"))));
	}

	@Override
	protected boolean visible() {
		Room r = getRoom();
		return Room.Type.interview != r.getType() && !r.isHidden(RoomElement.Whiteboard) && super.visible();
	}
}
