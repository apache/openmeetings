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

import static org.apache.openmeetings.web.pages.BasePage.ALIGN_LEFT;
import static org.apache.openmeetings.web.pages.BasePage.ALIGN_RIGHT;

import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.db.entity.basic.Client.Activity;
import org.apache.openmeetings.web.pages.BasePage;

public class RefreshIcon extends ClientIcon {
	private static final long serialVersionUID = 1L;

	public RefreshIcon(String id, String uid) {
		super(id, uid);
		mainCssClass = "restart ui-icon-refresh ";
	}

	@Override
	protected String getTitle() {
		return getString("lbl.refresh");
	}

	@Override
	protected String getAlign() {
		return ((BasePage)getPage()).isRtl() ? ALIGN_LEFT : ALIGN_RIGHT;
	}

	@Override
	protected boolean isClickable() {
		final Client c = getClient();
		return c != null && (c.hasActivity(Activity.broadcastA) || c.hasActivity(Activity.broadcastV));
	}

	@Override
	protected String getScript() {
		return String.format("VideoManager.refresh('%s');", uid);
	}

	@Override
	public void internalUpdate() {
		super.internalUpdate();
		setVisible(isClickable());
	}
}
