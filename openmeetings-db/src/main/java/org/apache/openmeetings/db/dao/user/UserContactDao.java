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

import static org.apache.openmeetings.db.util.DaoHelper.setLimits;
import static org.apache.openmeetings.util.OpenmeetingsVariables.PARAM_USER_ID;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getWebAppRootKey;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.openmeetings.db.entity.user.UserContact;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class UserContactDao {
	private static final Logger log = Red5LoggerFactory.getLogger(UserContactDao.class, getWebAppRootKey());
	@PersistenceContext
	private EntityManager em;
	@Autowired
	private UserDao userDao;

	public UserContact add(Long userId, Long ownerId, boolean pending) {
		try {
			UserContact userContact = new UserContact();
			userContact.setInserted(new Date());
			userContact.setOwner(userDao.get(ownerId));
			userContact.setContact(userDao.get(userId));
			userContact.setPending(pending);

			userContact = update(userContact);

			return userContact;
		} catch (Exception e) {
			log.error("[addUserContact]",e);
		}
		return null;
	}

	/**
	 * @param id - id of item to delete
	 */
	public void delete(Long id) {
		em.createNamedQuery("deleteUserContact").setParameter("id", id).executeUpdate();
	}

	/**
	 * @param ownerId - id of owner
	 * @return rowcount of update
	 */
	public Integer deleteAllUserContacts(Long ownerId) {
		return em.createNamedQuery("deleteAllUserContacts").setParameter("ownerId",ownerId).executeUpdate();
	}

	public UserContact get(Long userId, Long ownerId) {
		List<UserContact> ll = em.createNamedQuery("getContactByUserOwner", UserContact.class)
				.setParameter(PARAM_USER_ID, userId)
				.setParameter("ownerId", ownerId)
				.getResultList();
		log.info("number of contacts:: " + (ll == null ? null : ll.size()));
		return ll != null && ll.size() == 1 ? ll.get(0) : null;
	}

	public boolean isContact(Long userId, Long ownerId) {
		UserContact c = get(userId, ownerId);
		return c == null ? false : !c.isPending();
	}

	public List<UserContact> get(long ownerId, long first, long count) {
		return setLimits(
				em.createNamedQuery("getContactsByUser", UserContact.class).setParameter(PARAM_USER_ID, ownerId)
				, first, count).getResultList();
	}

	public long count(long ownerId) {
		TypedQuery<Long> q = em.createNamedQuery("countContactsByUser", Long.class);
		q.setParameter(PARAM_USER_ID, ownerId);
		return q.getSingleResult();
	}

	public List<UserContact> getContactsByUserAndStatus(Long ownerId, boolean pending) {
		return em.createNamedQuery("getContactsByUserAndStatus", UserContact.class)
				.setParameter("ownerId", ownerId)
				.setParameter("pending", pending)
				.getResultList();
	}

	public List<UserContact> getContactRequestsByUserAndStatus(Long userId, boolean pending) {
		return em.createNamedQuery("getContactRequestsByUserAndStatus", UserContact.class)
				.setParameter(PARAM_USER_ID, userId)
				.setParameter("pending", pending)
				.getResultList();
	}

	public UserContact get(Long id) {
		List<UserContact> list = em.createNamedQuery("getUserContactsById", UserContact.class)
				.setParameter("id", id).getResultList();
		return list.size() == 1 ? list.get(0) : null;
	}

	public List<UserContact> get() {
		return em.createNamedQuery("getUserContacts", UserContact.class).getResultList();
	}

	public Long updateContactStatus(Long id, boolean pending) {
		try {
			UserContact uc = get(id);
			uc.setPending(pending);
			update(uc);
			return id;
		} catch (Exception e) {
			log.error("[updateContactStatus]",e);
		}
		return null;
	}

	public UserContact update(UserContact c) {
		if (c.getId() == null) {
			c.setInserted(new Date());
			em.persist(c);
		} else {
			c.setUpdated(new Date());
			c = em.merge(c);
		}
		return c;
	}
}
