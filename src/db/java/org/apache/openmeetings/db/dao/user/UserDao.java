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
package org.apache.openmeetings.db.dao.user;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.apache.openmeetings.db.entity.user.User;
import org.springframework.beans.factory.annotation.Autowired;

public class UserDao {
	@Autowired
	private AbstractUserDao dao;

	public User get(long id) {
		return dao.get(id);
	}

	public List<User> get(int start, int count) {
		return dao.get(start, count);
	}

	public List<User> get(String search, long currentUserId) {
		return dao.get(search, false, currentUserId);
	}
	
	public List<User> get(String search, int start, int count, String order, long currentUserId) {
		return dao.get(search, start, count, order, false, currentUserId);
	}

	public long count() {
		return dao.count();
	}

	public long count(String search, long currentUserId) {
		return dao.count(search, false, currentUserId);
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
	
	public User getContact(String email, long ownerId) {
		return dao.getContact(email, ownerId);
	}
	
	public User getContact(String email, String firstName, String lastName, long ownerId) {
		return dao.getContact(email, firstName, lastName, ownerId);
	}
	
	public User getUserByActivationHash(String hash) {
		return dao.getUserByActivationHash(hash);
	}

	public List<User> searchUserProfile(long userId, String text, String offers, String search, String orderBy, int start, int max, boolean asc) {
		return dao.searchUserProfile(userId, text, offers, search, orderBy, start, max, asc);
	}

	public Long searchCountUserProfile(long userId, String text, String offers, String search) {
		return dao.searchCountUserProfile(userId, text, offers, search);
	}
	
	public User getExternalUser(String extId, String extType) {
		return dao.getExternalUser(extId, extType);
	}
}
