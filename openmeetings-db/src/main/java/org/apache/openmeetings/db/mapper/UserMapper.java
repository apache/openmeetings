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
package org.apache.openmeetings.db.mapper;

import org.apache.openmeetings.db.dao.user.GroupDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.dto.user.GroupDTO;
import org.apache.openmeetings.db.dto.user.UserDTO;
import org.apache.openmeetings.db.entity.user.Group;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.entity.user.User.Type;
import org.apache.wicket.util.string.Strings;

import org.springframework.stereotype.Component;

import jakarta.inject.Inject;

@Component
public class UserMapper {
	@Inject
	private UserDao userDao;
	@Inject
	private GroupDao groupDao;

	public User get(UserDTO dto) {
		User u = dto.getId() == null ? new User() : userDao.get(dto.getId());
		u.setLogin(dto.getLogin());
		u.setFirstname(dto.getFirstname());
		u.setLastname(dto.getLastname());
		u.setRights(dto.getRights());
		u.setLanguageId(dto.getLanguageId());
		u.setAddress(dto.getAddress());
		u.setTimeZoneId(dto.getTimeZoneId());
		String externalId = dto.getExternalId();
		String externalType = dto.getExternalType();
		Type type = dto.getType();
		if (Type.EXTERNAL == type || (!Strings.isEmpty(externalId) && !Strings.isEmpty(externalType))) {
			type = Type.EXTERNAL;
			if (u.getGroupUsers().stream().filter(gu -> gu.getGroup().isExternal() && gu.getGroup().getName().equals(externalType)).count() == 0) {
				u.addGroup(groupDao.getExternal(externalType));
			}
			u.setExternalId(externalId);
		}
		u.setType(type == null ? Type.USER : type);
		u.setPictureUri(dto.getPictureUri());
		return u;
	}

	public Group get(GroupDTO dto) {
		Group g = dto.getId() == null ? new Group() : groupDao.get(dto.getId());
		g.setName(dto.getName());
		g.setTag(dto.getTag());
		return g;
	}
}
