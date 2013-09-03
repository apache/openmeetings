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
package org.apache.openmeetings.data.user.dao;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.apache.openmeetings.data.IDataProviderDao;
import org.apache.openmeetings.persistence.beans.user.User;
import org.springframework.beans.factory.annotation.Autowired;

public class UserDao implements IDataProviderDao<User> {
	
	@Autowired
	private AbstractUserDao dao;

	public User get(long id) {
		return dao.get(id);
	}

	public List<User> get(int start, int count) {
		return dao.get(start, count);
	}

	public List<User> get(String search) {
		return dao.get(search, false);
	}
	
	public List<User> get(String search, int start, int count, String order) {
		return dao.get(search, start, count, order, false);
	}

	public long count() {
		return dao.count();
	}

	public long count(String search) {
		return dao.count(search, false);
	}

	public User update(User entity, Long userId) {
		return dao.update(entity, userId);
	}
	
	public User update(User user, String password, long updatedBy) throws NoSuchAlgorithmException {
		return dao.update(user, password, updatedBy);
	}

	public void delete(User entity, Long userId) {
		dao.delete(entity, userId);
	}

}
