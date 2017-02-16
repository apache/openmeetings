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

import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.db.entity.basic.Client.Activity;
import org.apache.openmeetings.web.room.RoomPanel;

public class RefreshIcon extends ClientIcon {
	private static final long serialVersionUID = 1L;

	public RefreshIcon(String id, Client client, RoomPanel room) {
		super(id, client, room);
		mainCssClass = "restart ";
	}

	@Override
	protected String getTitle() {
		return getString("610");
	}

	@Override
	protected String getAlign() {
		return ALIGN_RIGHT;
	}

	@Override
	protected boolean isClickable() {
		return client.hasActivity(Activity.broadcastA) || client.hasActivity(Activity.broadcastV);
	}

	@Override
	protected String getScript() {
		return String.format("document.getElementById('lzapp').refreshAv('%s');", client.getUid());
	}

	@Override
	public void internalUpdate() {
		super.internalUpdate();
		setVisible(isClickable());
	}
}
