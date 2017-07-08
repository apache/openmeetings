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
package org.apache.openmeetings.webservice;

import static org.apache.openmeetings.util.OpenmeetingsVariables.wicketApplicationName;

import java.util.Set;

import org.apache.openmeetings.IApplication;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.dao.server.SessiondataDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.server.Sessiondata;
import org.apache.openmeetings.db.entity.user.User.Right;
import org.apache.wicket.Application;

public abstract class BaseWebService {
	static <T> T getBean(Class<T> clazz) {
		IApplication iapp = (IApplication)Application.get(wicketApplicationName);
		return iapp._getOmBean(clazz);
	}

	static SessiondataDao getSessionDao() {
		return getBean(SessiondataDao.class);
	}

	static Sessiondata check(String sid) {
		return getSessionDao().check(sid);
	}

	static Set<Right> getRights(String sid) {
		Sessiondata sd = check(sid);
		return getRights(sd.getUserId());
	}

	static UserDao getUserDao() {
		return getBean(UserDao.class);
	}

	static RoomDao getRoomDao() {
		return getBean(RoomDao.class);
	}

	static Set<Right> getRights(Long id) {
		return getUserDao().getRights(id);
	}
}
