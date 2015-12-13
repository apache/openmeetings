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
package org.apache.openmeetings.web.user.rooms;

import org.apache.openmeetings.web.pages.MainPage;
import org.apache.openmeetings.web.util.OmUrlFragment;
import org.apache.openmeetings.web.util.OmUrlFragment.AreaKeys;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;

public class RoomEnterBehavior extends AjaxEventBehavior {
	private static final long serialVersionUID = -5195740583653865055L;
	private final long roomid;
	
	public RoomEnterBehavior(long roomid) {
		super("click");
		this.roomid = roomid;
	}

	public static OmUrlFragment getRoomUrlFragment(long roomId) {
		return new OmUrlFragment(AreaKeys.room, "" + roomId);
	}
	
	public static void roomEnter(MainPage page, AjaxRequestTarget target, long roomId) {
		page.updateContents(getRoomUrlFragment(roomId), target);
	}
	
	@Override
	protected void onEvent(AjaxRequestTarget target) {
		roomEnter((MainPage)getComponent().getPage(), target, roomid);
	}
}
