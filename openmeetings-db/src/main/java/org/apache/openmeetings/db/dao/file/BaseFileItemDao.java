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
package org.apache.openmeetings.db.dao.file;

import static org.apache.openmeetings.db.util.DaoHelper.UNSUPPORTED;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.openmeetings.db.dao.IDataProviderDao;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.dao.user.GroupDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.file.BaseFileItem;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.user.Group;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.wicket.util.string.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class BaseFileItemDao implements IDataProviderDao<BaseFileItem> {
	private static final Logger log = LoggerFactory.getLogger(BaseFileItemDao.class);
	@PersistenceContext
	protected EntityManager em;
	@Autowired
	private RoomDao roomDao;
	@Autowired
	private GroupDao groupDao;
	@Autowired
	private UserDao userDao;

	public BaseFileItem get(String hash) {
		log.debug("getByHash() started");
		List<BaseFileItem> list = em.createNamedQuery("getFileByHash", BaseFileItem.class)
				.setParameter("hash", hash).getResultList();
		return list.size() == 1 ? list.get(0) : null;
	}

	@Override
	public BaseFileItem get(Long id) {
		return getBase(id);
	}

	public BaseFileItem getBase(Long id) {
		if (id == null || id.longValue() <= 0) {
			return null;
		}
		List<BaseFileItem> list = em.createNamedQuery("getFileById", BaseFileItem.class)
					.setParameter("id", id).getResultList();
		return list.size() == 1 ? list.get(0) : null;
	}

	public BaseFileItem getAny(Long id) {
		List<BaseFileItem> list = em.createNamedQuery("getAnyFileById", BaseFileItem.class)
					.setParameter("id", id).getResultList();
		return list.size() == 1 ? list.get(0) : null;
	}

	public void delete(BaseFileItem f) {
		if (f == null || f.getId() == null) {
			return;
		}
		f.setDeleted(true);

		updateBase(f);
	}

	public BaseFileItem updateBase(BaseFileItem f) {
		f.setExternalType(null);
		BaseFileItem parent = get(f.getParentId());
		if (parent != null) {
			f.setExternalType(parent.getExternalType());
		}
		if (Strings.isEmpty(f.getExternalType())) {
			Room r = roomDao.get(f.getRoomId());
			if (r != null) {
				f.setExternalType(r.externalType());
			}
		}
		if (Strings.isEmpty(f.getExternalType())) {
			Group g = groupDao.get(f.getGroupId());
			if (g != null && g.isExternal()) {
				f.setExternalType(g.getName());
			}
		}
		if (Strings.isEmpty(f.getExternalType())) {
			User u = userDao.get(f.getOwnerId());
			if (u != null) {
				f.setExternalType(u.externalType());
			}
		}
		if (Strings.isEmpty(f.getExternalType())) {
			User u = userDao.get(f.getInsertedBy());
			if (u != null) {
				f.setExternalType(u.externalType());
			}
		}
		if (f.getId() == null) {
			em.persist(f);
		} else {
			f = em.merge(f);
		}
		return f;
	}

	@Override
	public List<BaseFileItem> get(long start, long count) {
		throw UNSUPPORTED;
	}

	@Override
	public List<BaseFileItem> get(String search, long start, long count, String order) {
		throw UNSUPPORTED;
	}

	@Override
	public long count() {
		throw UNSUPPORTED;
	}

	@Override
	public long count(String search) {
		throw UNSUPPORTED;
	}

	@Override
	public BaseFileItem update(BaseFileItem entity, Long userId) {
		throw UNSUPPORTED;
	}

	@Override
	public void delete(BaseFileItem entity, Long userId) {
		throw UNSUPPORTED;
	}
}
