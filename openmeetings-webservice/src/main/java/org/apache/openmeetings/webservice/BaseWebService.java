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

import static org.apache.openmeetings.util.OpenmeetingsVariables.getWebAppRootKey;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getWicketApplicationName;
import static org.apache.openmeetings.webservice.error.ServiceException.NO_PERMISSION;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import org.apache.openmeetings.IApplication;
import org.apache.openmeetings.db.dao.file.FileItemDao;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.dao.server.SessiondataDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.server.Sessiondata;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.entity.user.User.Right;
import org.apache.openmeetings.db.util.AuthLevelUtil;
import org.apache.openmeetings.webservice.error.ServiceException;
import org.apache.wicket.Application;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public abstract class BaseWebService {
	private static final Logger log = Red5LoggerFactory.getLogger(BaseWebService.class, getWebAppRootKey());

	static IApplication getApp() {
		return (IApplication)Application.get(getWicketApplicationName());
	}

	static <T> T getBean(Class<T> clazz) {
		T b = null;
		try {
			b = getApp()._getOmBean(clazz);
		} catch (Exception e) {
			throw new ServiceException(e.getMessage());
		}
		if (b == null) {
			throw new ServiceException("");
		}
		return b;
	}

	static SessiondataDao getSessionDao() {
		return getBean(SessiondataDao.class);
	}

	// this one is fail safe
	static Sessiondata check(String sid) {
		try {
			return getSessionDao().check(sid);
		} catch (ServiceException e) {
			log.debug("Exception while checking sid", e);
		}
		return new Sessiondata();
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

	static FileItemDao getFileDao() {
		return getBean(FileItemDao.class);
	}

	// this one is fail safe
	static Set<Right> getRights(Long id) {
		try {
			return getUserDao().getRights(id);
		} catch (ServiceException e) {
			log.debug("Exception while getting rights", e);
		}
		return new HashSet<>();
	}

	static <T> T performCall(String sid, User.Right level, Function<Sessiondata, T> action) {
		return performCall(sid, sd -> AuthLevelUtil.check(getRights(sd.getUserId()), level), action);
	}

	static <T> T performCall(String sid, Predicate<Sessiondata> allowed, Function<Sessiondata, T> action) {
		try {
			Sessiondata sd = check(sid);
			if (allowed.test(sd)) {
				return action.apply(sd);
			} else {
				throw NO_PERMISSION;
			}
		} catch (ServiceException err) {
			throw err;
		} catch (Exception err) {
			log.error("[performCall]", err);
			throw new ServiceException(err.getMessage());
		}
	}
}
