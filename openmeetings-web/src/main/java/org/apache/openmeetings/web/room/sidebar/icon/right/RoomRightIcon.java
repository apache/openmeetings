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

import static org.apache.openmeetings.web.room.sidebar.RoomSidebar.FUNC_TOGGLE_RIGHT;

import org.apache.openmeetings.db.entity.room.Room.Right;
import org.apache.openmeetings.web.room.sidebar.icon.ClientIcon;

public abstract class RoomRightIcon extends ClientIcon {
	private static final long serialVersionUID = 1L;
	private static final String CLS_GRANTED = "granted ";
	protected final Right right;

	public RoomRightIcon(String id, String uid, Right right) {
		super(id, uid);
		this.right = right;
	}

	@Override
	protected boolean isClickable() {
		return (isSelf() && !hasRight()) || !isSelf() && roomHasRight(Right.moderator);
	}

	protected boolean hasRight() {
		return hasRight(right);
	}

	@Override
	protected String getScript() {
		return String.format("%s('%s', '%s');", FUNC_TOGGLE_RIGHT, right.name(), uid);
	}

	protected boolean visible() {
		return !hasRight(Right.superModerator) && (
				(isSelf() && !hasRight() && getRoom().isAllowUserQuestions())
				|| (!isSelf() && roomHasRight(Right.moderator))
			);
	}

	@Override
	public void internalUpdate() {
		setVisible(visible());
		if (hasRight()) {
			cssClass.append(CLS_GRANTED);
		}
	}
}
