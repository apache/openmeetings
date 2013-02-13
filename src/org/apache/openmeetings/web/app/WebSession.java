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
package org.apache.openmeetings.web.app;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import org.apache.openmeetings.data.basic.AuthLevelUtil;
import org.apache.openmeetings.data.basic.FieldLanguageDao;
import org.apache.openmeetings.data.basic.FieldManager;
import org.apache.openmeetings.data.basic.SessiondataDao;
import org.apache.openmeetings.data.basic.dao.ConfigurationDao;
import org.apache.openmeetings.data.user.UserManager;
import org.apache.openmeetings.data.user.dao.UsersDao;
import org.apache.openmeetings.persistence.beans.basic.Sessiondata;
import org.apache.openmeetings.persistence.beans.lang.FieldLanguage;
import org.apache.openmeetings.persistence.beans.user.User;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.request.Request;

public class WebSession extends AbstractAuthenticatedWebSession {
	private static final long serialVersionUID = 1123393236459095315L;
	private long userId = -1;
	private long userLevel = -1;
	private String SID = null;
	private String area = null;
	private TimeZone tz;
	private SimpleDateFormat ISO8601FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
	
	public WebSession(Request request) {
		super(request);
	}

	@Override
	public void invalidate() {
		super.invalidate();
		userId = -1;
		userLevel = -1;
		SID = null;
	}
	
	@Override
	public Roles getRoles() {
		Roles r = null;
		if (isSignedIn()) {
			userLevel = Application.getBean(UserManager.class).getUserLevelByID(userId);
			AuthLevelUtil authLevel = Application.getBean(AuthLevelUtil.class);
			r = new Roles(Roles.USER);
			if (authLevel.checkUserLevel(userLevel)) {
				r.add(Roles.USER);
			}
			if (authLevel.checkAdminLevel(userLevel)) {
				r.add(Roles.ADMIN);
			}
		}
		return r;
	}

	@Override
	public boolean isSignedIn() {
		return (userId > -1);
	}

	public boolean signIn(String login, String password) {
		Sessiondata sessData = Application.getBean(SessiondataDao.class).startsession();
		SID = sessData.getSession_id();
		Object u = Application.getBean(UserManager.class).loginUser(SID, login, password,
				null, null, false);
		
		if (u instanceof User) {
			User user = (User)u;
			userId = user.getUser_id();
			tz = TimeZone.getTimeZone(user.getOmTimeZone().getIcal());
			ISO8601FORMAT.setTimeZone(tz);
			if (null == getId()) {
				bind();
			}
			return true;
		}
		return false;
	}
	
	public static WebSession get() {
		return (WebSession)AbstractAuthenticatedWebSession.get();
	}
	
	public static String getString(long id) {
		FieldManager fieldManager = Application.getBean(FieldManager.class);
		return fieldManager.getString(id, getLanguage());
	}
	
	public static long getLanguage() {
		WebSession session = get();
		if (session.isSignedIn()) {
			return Application.getBean(UsersDao.class).get(session.userId).getLanguage_id();
		} else {
			return Application.getBean(ConfigurationDao.class).getConfValue("default_lang_id", Long.class, "1");
		}
	}
	
	public static FieldLanguage getLanguageObj() {
		return Application.getBean(FieldLanguageDao.class).getFieldLanguageById(getLanguage());
	}
	
	public static String getSid() {
		return get().SID;
	}

	public static long getUserId() {
		return get().userId;
	}

	public static Calendar getCalendar() {
		return Calendar.getInstance(get().tz);
	}

	public static DateFormat getDateFormat() {
		return get().ISO8601FORMAT;
	}

	public static long getUserLevel() {
		return get().userLevel;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}
}
