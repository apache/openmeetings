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
package org.apache.openmeetings.web.util;

import static org.apache.openmeetings.web.user.dashboard.MyRoomsWidget.WIDGET_ID_MY_ROOMS;
import static org.apache.openmeetings.web.user.dashboard.RssWidget.WIDGET_ID_RSS;
import static org.apache.openmeetings.web.user.dashboard.admin.AdminWidget.WIDGET_ID_ADMIN;

import org.wicketstuff.dashboard.DefaultDashboard;
import org.wicketstuff.dashboard.Widget;

public class UserDashboard extends DefaultDashboard {
	private static final long serialVersionUID = 1L;
	private boolean widgetRssDeleted = false;
	private boolean widgetMyRoomsDeleted = false;
	private boolean widgetAdminDeleted = false;
	
	public UserDashboard(String id, String title) {
		super(id, title);
	}

	public boolean isWidgetRssDeleted() {
		return widgetRssDeleted;
	}

	public void setWidgetRssDeleted(boolean widgetRssDeleted) {
		this.widgetRssDeleted = widgetRssDeleted;
	}

	public boolean isWidgetMyRoomsDeleted() {
		return widgetMyRoomsDeleted;
	}

	public void setWidgetMyRoomsDeleted(boolean widgetMyRoomsDeleted) {
		this.widgetMyRoomsDeleted = widgetMyRoomsDeleted;
	}

	public boolean isWidgetAdminDeleted() {
		return widgetAdminDeleted;
	}

	public void setWidgetAdminDeleted(boolean widgetAdminDeleted) {
		this.widgetAdminDeleted = widgetAdminDeleted;
	}
	
	@Override
	public void deleteWidget(String widgetId) {
		switch (widgetId) {
			case WIDGET_ID_RSS:
				widgetRssDeleted = true;
				break;
			case WIDGET_ID_MY_ROOMS:
				widgetMyRoomsDeleted = true;
				break;
			case WIDGET_ID_ADMIN:
				widgetAdminDeleted = true;
				break;
			default:
				break;
		}
		super.deleteWidget(widgetId);
	}
	
	@Override
	public void addWidget(Widget widget) {
		switch (widget.getId()) {
			case WIDGET_ID_RSS:
				widgetRssDeleted = false;
				break;
			case WIDGET_ID_MY_ROOMS:
				widgetMyRoomsDeleted = false;
				break;
			case WIDGET_ID_ADMIN:
				widgetAdminDeleted = false;
				break;
			default:
				break;
		}
		super.addWidget(widget);
	}
}
