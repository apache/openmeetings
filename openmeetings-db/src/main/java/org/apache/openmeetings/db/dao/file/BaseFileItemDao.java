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

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.openmeetings.db.entity.file.BaseFileItem;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class BaseFileItemDao {
	private static final Logger log = Red5LoggerFactory.getLogger(BaseFileItemDao.class, webAppRootKey);
	@PersistenceContext
	protected EntityManager em;

	public BaseFileItem get(String hash) {
		log.debug("getByHash() started");
		BaseFileItem f = null;
		TypedQuery<BaseFileItem> query = em.createNamedQuery("getFileByHash", BaseFileItem.class);
		query.setParameter("hash", hash);

		try {
			f = query.getSingleResult();
		} catch (NoResultException ex) {
			//no-op
		}
		return f;
	}

	public BaseFileItem get(Long id) {
		BaseFileItem f = null;
		if (id != null && id > 0) {
			TypedQuery<BaseFileItem> query = em.createNamedQuery("getFileById", BaseFileItem.class)
					.setParameter("id", id);

			try {
				f = query.getSingleResult();
			} catch (NoResultException ex) {
			}
		} else {
			log.info("[get] " + "Info: No id given");
		}
		return f;
	}

	public void delete(BaseFileItem f) {
		if (f == null || f.getId() == null) {
			return;
		}
		f.setDeleted(true);
		f.setUpdated(new Date());

		_update(f);
	}

	public BaseFileItem _update(BaseFileItem f) {
		if (f.getId() == null) {
			f.setInserted(new Date());
			em.persist(f);
		} else {
			f.setUpdated(new Date());
			f = em.merge(f);
		}
		return f;
	}
}
