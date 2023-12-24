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

import static org.apache.openmeetings.db.util.DaoHelper.only;
import static org.apache.openmeetings.db.util.DaoHelper.UNSUPPORTED;
import static org.apache.openmeetings.db.util.DaoHelper.setLimits;

import java.util.Date;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.apache.openmeetings.db.dao.IDataProviderDao;
import org.apache.openmeetings.db.entity.user.PrivateMessageFolder;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class PrivateMessageFolderDao implements IDataProviderDao<PrivateMessageFolder> {
	private static final Logger log = LoggerFactory.getLogger(PrivateMessageFolderDao.class);
	@PersistenceContext
	private EntityManager em;

	public Long addPrivateMessageFolder(String folderName, Long userId) {
		try {
			PrivateMessageFolder privateMessageFolder = new PrivateMessageFolder();
			privateMessageFolder.setFolderName(folderName);
			privateMessageFolder.setUserId(userId);
			privateMessageFolder.setInserted(new Date());

			privateMessageFolder = em.merge(privateMessageFolder);
			return privateMessageFolder.getId();
		} catch (Exception e) {
			log.error("[addPrivateMessageFolder]",e);
		}
		return null;
	}

	public Long addPrivateMessageFolderObj(PrivateMessageFolder folder) {
		folder.setInserted(new Date());

		folder = em.merge(folder);
		return folder.getId();
	}

	@Override
	public PrivateMessageFolder get(Long id) {
		return only(em.createNamedQuery("getMsgFolderById", PrivateMessageFolder.class)
				.setParameter("id", id)
				.getResultList());
	}

	public List<PrivateMessageFolder> getByUser(Long userId) {
		return em.createNamedQuery("getMsgFolderByUser", PrivateMessageFolder.class)
				.setParameter("userId", userId)
				.getResultList();
	}

	@Override
	public List<PrivateMessageFolder> get(long start, long count) {
		return setLimits(
				em.createNamedQuery("getMsgFolders", PrivateMessageFolder.class)
				, start, count)
				.getResultList();
	}

	@Override
	public PrivateMessageFolder update(PrivateMessageFolder folder, Long userId) {
		if (folder.getId() == null) {
			em.persist(folder);
		} else {
			folder = em.merge(folder);
		}
		return folder;
	}

	@Override
	public void delete(PrivateMessageFolder folder, Long userId) {
		folder = em.find(PrivateMessageFolder.class, folder.getId());
		em.remove(folder);
	}

	@Override
	public List<PrivateMessageFolder> get(String search, long start, long count, SortParam<String> order) {
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
}
