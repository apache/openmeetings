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

import org.apache.openmeetings.db.dao.IDataProviderDao;
import org.apache.openmeetings.db.entity.user.User;
import org.springframework.beans.factory.annotation.Autowired;

public class AdminUserDao implements IDataProviderDao<User> {
	
	@Autowired
	private AbstractUserDao dao;

	public final static String[] searchFields = AbstractUserDao.searchFields;

	
	public User get(long id) {
		return dao.get(id);
	}

	public List<User> get(int start, int count) {
		return dao.get(start, count);
	}

	public List<User> get(String search, int start, int count, String order) {
		return dao.get(search, start, count, order, true);
	}

	public List<User> get(String search) {
		return dao.get(search, true);
	}

	public long count() {
		return dao.count();
	}

	public long count(String search) {
		return dao.count(search, true);
	}

	public User update(User entity, Long userId) {
		return dao.update(entity, userId);
	}

	public void delete(User entity, Long userId) {
		dao.delete(entity, userId);
	}

	public Long deleteUserID(long userId) {
		return dao.deleteUserID(userId);
	}
	
	public User getNewUserInstance(User currentUser) {
		return dao.getNewUserInstance(currentUser);
	}
	
	public User update(User user, String password, long updatedBy) throws NoSuchAlgorithmException {
		return dao.update(user, password, updatedBy);
	}

	public List<User> getAllUsers() {
		return dao.getAllUsers();
	}

	public List<User> getAllUsersDeleted() {
		return dao.getAllUsersDeleted();
	}
	
	public boolean verifyPassword(Long userId, String password) {
		return dao.verifyPassword(userId, password);
	}

	public boolean checkUserLogin(String login, Long id) {
		return dao.checkUserLogin(login, id);
	}
	
	public boolean checkUserEMail(String email, Long id) {
		return dao.checkUserEMail(email, id);
	}
	
	public User getUserByName(String login) {
		return dao.getUserByName(login);
	}

	public User getUserByEmail(String email) {
		return dao.getUserByEmail(email);
	}
	
	public Object getUserByHash(String hash) {
		return dao.getUserByHash(hash);
	}
	
	public User getContact(String email, User owner) {
		return dao.getContact(email, owner);
	}
	
	public User getContact(String email, long ownerId) {
		return dao.getContact(email, ownerId);
	}
	
	public User getContact(String email, String firstName, String lastName, long ownerId) {
		return dao.getContact(email, firstName, lastName, ownerId);
	}
	
	public User getContact(String email, String firstName, String lastName, Long langId, String tzId, long ownerId) {
		return dao.getContact(email, firstName, lastName, langId, tzId, dao.get(ownerId));
	}
}
