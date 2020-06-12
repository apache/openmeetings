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

import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DEFAULT_LANDING_ZONE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getBaseUrl;

import java.io.Serializable;

import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.web.admin.backup.BackupPanel;
import org.apache.openmeetings.web.admin.configurations.ConfigsPanel;
import org.apache.openmeetings.web.admin.connection.ConnectionsPanel;
import org.apache.openmeetings.web.admin.email.EmailPanel;
import org.apache.openmeetings.web.admin.groups.GroupsPanel;
import org.apache.openmeetings.web.admin.labels.LangPanel;
import org.apache.openmeetings.web.admin.ldaps.LdapsPanel;
import org.apache.openmeetings.web.admin.oauth.OAuthPanel;
import org.apache.openmeetings.web.admin.rooms.RoomsPanel;
import org.apache.openmeetings.web.admin.users.UsersPanel;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.app.ClientManager;
import org.apache.openmeetings.web.common.BasePanel;
import org.apache.openmeetings.web.room.RoomPanel;
import org.apache.openmeetings.web.user.calendar.CalendarPanel;
import org.apache.openmeetings.web.user.dashboard.OmDashboardPanel;
import org.apache.openmeetings.web.user.profile.EditProfilePanel;
import org.apache.openmeetings.web.user.profile.InvitationsPanel;
import org.apache.openmeetings.web.user.profile.MessagesContactsPanel;
import org.apache.openmeetings.web.user.profile.UserSearchPanel;
import org.apache.openmeetings.web.user.profile.WidgetsPanel;
import org.apache.openmeetings.web.user.record.RecordingsPanel;
import org.apache.openmeetings.web.user.rooms.RoomsSelectorPanel;
import org.apache.wicket.request.flow.RedirectToUrlException;

public class OmUrlFragment implements Serializable {
	private static final long serialVersionUID = 1L;
	private AreaKeys area = AreaKeys.user;
	private String type = "";
	public static final String CHILD_ID = "child";
	public static final String TYPE_CALENDAR = "calendar";
	public static final String TYPE_DASHBOARD = "dashboard";
	public static final String TYPE_MESSAGES = "messages";
	public static final String TYPE_EDIT = "edit";
	public static final String TYPE_RECORDINGS = "record";
	public static final String TYPE_MY = "my";
	public static final String TYPE_GROUP = "group";
	public static final String TYPE_PUBLIC = "public";
	public static final String TYPE_USER = "user";
	public static final String TYPE_CONNECTION = "connection";
	public static final String TYPE_ROOM = "room";
	public static final String TYPE_CONFIG = "config";
	public static final String TYPE_LANG = "lang";
	public static final String TYPE_LDAP = "ldap";
	public static final String TYPE_BACKUP = "backup";
	public static final String TYPE_OAUTH2 = "oauth2";
	public static final String TYPE_EMAIL = "email";
	public static final String TYPE_SEARCH = "search";
	public static final String TYPE_INVITATION = "invitation";
	public static final String TYPE_WIDGET = "widget";
	public static final OmUrlFragment DASHBOARD = new OmUrlFragment(AreaKeys.user, TYPE_DASHBOARD);
	public static final OmUrlFragment PROFILE_EDIT = new OmUrlFragment(AreaKeys.profile, TYPE_EDIT);
	public static final OmUrlFragment PROFILE_MESSAGES = new OmUrlFragment(AreaKeys.profile, TYPE_MESSAGES);
	public static final OmUrlFragment CALENDAR = new OmUrlFragment(AreaKeys.user, TYPE_CALENDAR);
	public static final OmUrlFragment ROOMS_PUBLIC = new OmUrlFragment(AreaKeys.rooms, TYPE_PUBLIC);
	public static final OmUrlFragment ROOMS_GROUP = new OmUrlFragment(AreaKeys.rooms, TYPE_GROUP);
	public static final OmUrlFragment ROOMS_MY = new OmUrlFragment(AreaKeys.rooms, TYPE_MY);

	public enum AreaKeys {
		user
		, admin
		, profile
		, room
		, rooms
	}

	public enum MenuActions {
		DASHBOARD_START
		, DASHBOARD_CALENDAR
		, RECORD
		, ROOMS_PUBLIC
		, ROOMS_GROUP
		, ROOMS_MY
		, ADMIN_USER
		, ADMIN_CONNECTION
		, ADMIN_GROUP
		, ADMIN_ROOM
		, ADMIN_CONFIG
		, ADMIN_LABEL
		, ADMIN_LDAP
		, ADMIN_BACKUP
		, ADMIN_OAUTH
		, ADMIN_EMAIL
		, PROFILE_MESSAGE
		, PROFILE_EDIT
		, PROFILE_SEARCH
		, PROFILE_INVITATION
		, PROFILE_WIDGET
	}

	public OmUrlFragment(AreaKeys area, String type) {
		this.setArea(area);
		this.setType(type);
	}

	public OmUrlFragment(MenuActions action) {
		switch(action) {
			case DASHBOARD_START:
				setArea(AreaKeys.user);
				setType(TYPE_DASHBOARD);
				break;
			case DASHBOARD_CALENDAR:
				setArea(AreaKeys.user);
				setType(TYPE_CALENDAR);
				break;
			case RECORD:
				setArea(AreaKeys.user);
				setType(TYPE_RECORDINGS);
				break;
			case ROOMS_PUBLIC:
				setArea(AreaKeys.rooms);
				setType(TYPE_PUBLIC);
				break;
			case ROOMS_GROUP:
				setArea(AreaKeys.rooms);
				setType(TYPE_GROUP);
				break;
			case ROOMS_MY:
				setArea(AreaKeys.rooms);
				setType(TYPE_MY);
				break;
			case ADMIN_USER:
				setArea(AreaKeys.admin);
				setType(TYPE_USER);
				break;
			case ADMIN_CONNECTION:
				setArea(AreaKeys.admin);
				setType(TYPE_CONNECTION);
				break;
			case ADMIN_GROUP:
				setArea(AreaKeys.admin);
				setType(TYPE_GROUP);
				break;
			case ADMIN_ROOM:
				setArea(AreaKeys.admin);
				setType(TYPE_ROOM);
				break;
			case ADMIN_CONFIG:
				setArea(AreaKeys.admin);
				setType(TYPE_CONFIG);
				break;
			case ADMIN_LABEL:
				setArea(AreaKeys.admin);
				setType(TYPE_LANG);
				break;
			case ADMIN_LDAP:
				setArea(AreaKeys.admin);
				setType(TYPE_LDAP);
				break;
			case ADMIN_BACKUP:
				setArea(AreaKeys.admin);
				setType(TYPE_BACKUP);
				break;
			case ADMIN_OAUTH:
				setArea(AreaKeys.admin);
				setType(TYPE_OAUTH2);
				break;
			case ADMIN_EMAIL:
				setArea(AreaKeys.admin);
				setType(TYPE_EMAIL);
				break;
			case PROFILE_MESSAGE:
				setArea(AreaKeys.profile);
				setType(TYPE_MESSAGES);
				break;
			case PROFILE_EDIT:
				setArea(AreaKeys.profile);
				setType(TYPE_EDIT);
				break;
			case PROFILE_SEARCH:
				setArea(AreaKeys.profile);
				setType(TYPE_SEARCH);
				break;
			case PROFILE_INVITATION:
				setArea(AreaKeys.profile);
				setType(TYPE_INVITATION);
				break;
			case PROFILE_WIDGET:
				setArea(AreaKeys.profile);
				setType(TYPE_WIDGET);
				break;
		}
	}

	public static OmUrlFragment get() {
		String[] arr = Application.get().getBean(ConfigurationDao.class).getString(CONFIG_DEFAULT_LANDING_ZONE, "").split("/");
		if (arr != null && arr.length == 2) {
			try {
				return new OmUrlFragment(AreaKeys.valueOf(arr[0]), arr[1]);
			} catch (Exception e) {
				// no-op
			}
		}
		return DASHBOARD;
	}

	public AreaKeys getArea() {
		return area;
	}

	public void setArea(AreaKeys area) {
		this.area = area;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public static BasePanel getPanel(AreaKeys area, String type) {
		BasePanel basePanel = null;
		switch(area) {
			case admin:
				if (TYPE_USER.equals(type)) {
					basePanel = new UsersPanel(CHILD_ID);
				} else if (TYPE_CONNECTION.equals(type)) {
					basePanel = new ConnectionsPanel(CHILD_ID);
				} else if (TYPE_GROUP.equals(type)) {
					basePanel = new GroupsPanel(CHILD_ID);
				} else if (TYPE_ROOM.equals(type)) {
					basePanel = new RoomsPanel(CHILD_ID);
				} else if (TYPE_CONFIG.equals(type)) {
					basePanel = new ConfigsPanel(CHILD_ID);
				} else if (TYPE_LANG.equals(type)) {
					basePanel = new LangPanel(CHILD_ID);
				} else if (TYPE_LDAP.equals(type)) {
					basePanel = new LdapsPanel(CHILD_ID);
				} else if (TYPE_BACKUP.equals(type)) {
					basePanel = new BackupPanel(CHILD_ID);
				} else if (TYPE_OAUTH2.equals(type)) {
					basePanel = new OAuthPanel(CHILD_ID);
				} else if (TYPE_EMAIL.equals(type)) {
					basePanel = new EmailPanel(CHILD_ID);
				}
				break;
			case profile:
				if (TYPE_MESSAGES.equals(type)) {
					basePanel = new MessagesContactsPanel(CHILD_ID);
				} else if (TYPE_EDIT.equals(type)) {
					basePanel = new EditProfilePanel(CHILD_ID);
				} else if (TYPE_SEARCH.equals(type)) {
					basePanel = new UserSearchPanel(CHILD_ID);
				} else if (TYPE_INVITATION.equals(type)) {
					basePanel = new InvitationsPanel(CHILD_ID);
				} else if (TYPE_WIDGET.equals(type)) {
					basePanel = new WidgetsPanel(CHILD_ID);
				}
				break;
			case room: {
				Room r = null;
				try {
					Long roomId = Long.valueOf(type);
					r = Application.get().getBean(RoomDao.class).get(roomId);
				} catch(NumberFormatException ne) {
					r = Application.get().getBean(RoomDao.class).get(type);
				}
				if (r != null) {
					moveToServer(r);
					basePanel = new RoomPanel(CHILD_ID, r);
				} else {
					basePanel = new OmDashboardPanel(CHILD_ID);
				}
			}
				break;
			case rooms:
				basePanel = new RoomsSelectorPanel(CHILD_ID, type);
				break;
			case user:
				if (TYPE_CALENDAR.equals(type)) {
					basePanel = new CalendarPanel(CHILD_ID);
				} else if (TYPE_RECORDINGS.equals(type)) {
					basePanel = new RecordingsPanel(CHILD_ID);
				} else {
					basePanel = new OmDashboardPanel(CHILD_ID);
				}
				break;
			default:
				break;
		}
		return basePanel;
	}

	public String getLink() {
		return getBaseUrl() + "#" + getArea().name() + "/" + getType();
	}

	private static void moveToServer(Room r) {
		String url = Application.get().getBean(ClientManager.class).getServerUrl(r, null);
		if (url != null) {
			throw new RedirectToUrlException(url);
		}
	}
}
