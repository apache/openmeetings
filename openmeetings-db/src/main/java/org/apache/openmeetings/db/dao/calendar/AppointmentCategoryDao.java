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
package org.apache.openmeetings.db.dao.calendar;

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.calendar.AppointmentCategory;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class AppointmentCategoryDao {
	private static final Logger log = Red5LoggerFactory.getLogger(AppointmentCategoryDao.class, webAppRootKey);

	@PersistenceContext
	private EntityManager em;
	@Autowired
	private UserDao usersDao;

	public AppointmentCategory get(Long id) {
		try {
			log.debug("getAppointmentCategoryById: " + id);

			String hql = "select app from AppointmentCategory app WHERE app.deleted = false AND app.id = :id";

			TypedQuery<AppointmentCategory> query = em.createQuery(hql, AppointmentCategory.class);
			query.setParameter("id", id);

			AppointmentCategory appointCategory = null;
			try {
				appointCategory = query.getSingleResult();
			} catch (NoResultException ex) {
			}

			return appointCategory;
		} catch (Exception ex2) {
			log.error("[get]: " + ex2);
		}
		return null;
	}

	public Long updateAppointmentCategory(Long categoryId, String name) {
		try {

			AppointmentCategory ac = get(categoryId);

			ac.setName(name);
			ac.setUpdated(new Date());

			if (ac.getId() == null) {
				em.persist(ac);
			} else {
				if (!em.contains(ac)) {
					em.merge(ac);
				}
			}

			return categoryId;
		} catch (Exception ex2) {
			log.error("[updateAppointmentCategory]: ", ex2);
		}
		return null;
	}

	public Long addAppointmentCategory(Long userId, String name, String comment) {
		try {

			AppointmentCategory ac = new AppointmentCategory();

			ac.setName(name);
			ac.setInserted(new Date());
			ac.setDeleted(false);
			ac.setUser(usersDao.get(userId));
			ac.setComment(comment);

			ac = em.merge(ac);

			return ac.getId();
		} catch (Exception ex2) {
			log.error("[addAppointmentCategory]: ", ex2);
		}
		return null;
	}

	public Long deleteAppointmentCategory(Long categoryId) {
		try {

			AppointmentCategory ac = get(categoryId);

			log.debug("ac: " + ac);

			if (ac == null) {
				log.debug("Already deleted / Could not find: " + categoryId);
				return categoryId;
			}
			ac.setUpdated(new Date());
			ac.setDeleted(true);
			if (ac.getId() == null) {
				em.persist(ac);
			} else {
				if (!em.contains(ac)) {
					em.merge(ac);
				}
			}
			return categoryId;
		} catch (Exception ex2) {
			log.error("[deleteAppointmentCategory]: " + ex2);
		}
		return null;
	}

	public List<AppointmentCategory> getAppointmentCategoryList() {
		try {

			String hql = "select a from AppointmentCategory a WHERE a.deleted = false ";

			TypedQuery<AppointmentCategory> query = em.createQuery(hql, AppointmentCategory.class);

			List<AppointmentCategory> listAppointmentCategory = query.getResultList();

			return listAppointmentCategory;
		} catch (Exception ex2) {
			log.error("[AppointmentCategory]: " + ex2);
		}
		return null;
	}
}
