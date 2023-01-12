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

import static org.apache.openmeetings.webservice.error.ServiceException.NO_PERMISSION;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import org.apache.openmeetings.db.dao.file.FileItemDao;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.dao.server.SessiondataDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.server.Sessiondata;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.entity.user.User.Right;
import org.apache.openmeetings.db.util.AuthLevelUtil;
import org.apache.openmeetings.webservice.error.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.inject.Inject;


public abstract class BaseWebService {
	private static final Logger log = LoggerFactory.getLogger(BaseWebService.class);

	@Inject
	protected SessiondataDao sessionDao;
	@Inject
	protected UserDao userDao;
	@Inject
	protected RoomDao roomDao;
	@Inject
	protected FileItemDao fileDao;

	// this one is fail safe
	Sessiondata check(String sid) {
		try {
			return sessionDao.check(sid);
		} catch (Exception e) {
			log.debug("Exception while checking sid", e);
		}
		return new Sessiondata();
	}

	Set<Right> getRights(String sid) {
		Sessiondata sd = check(sid);
		return getRights(sd.getUserId());
	}

	// this one is fail safe
	Set<Right> getRights(Long id) {
		try {
			return userDao.getRights(id);
		} catch (Exception e) {
			log.debug("Exception while getting rights", e);
		}
		return new HashSet<>();
	}

	<T> T performCall(String sid, User.Right level, Function<Sessiondata, T> action) throws ServiceException {
		return performCall(sid, sd -> AuthLevelUtil.check(getRights(sd.getUserId()), level), action);
	}

	<T> T performCall(String sid, Predicate<Sessiondata> allowed, Function<Sessiondata, T> action)
			throws ServiceException
	{
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
