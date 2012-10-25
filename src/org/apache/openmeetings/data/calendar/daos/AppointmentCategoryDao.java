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
package org.apache.openmeetings.data.calendar.daos;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.data.basic.dao.ConfigurationDao;
import org.apache.openmeetings.data.user.dao.UsersDao;
import org.apache.openmeetings.persistence.beans.calendar.AppointmentCategory;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class AppointmentCategoryDao {

	private static final Logger log = Red5LoggerFactory.getLogger(
			ConfigurationDao.class,
			OpenmeetingsVariables.webAppRootKey);

	@PersistenceContext
	private EntityManager em;
	@Autowired
	private UsersDao usersDao;

	public AppointmentCategory getAppointmentCategoryById(Long categoryId) {
		try {
			log.debug("getAppointmentCategoryById: " + categoryId);

			String hql = "select app from AppointmentCategory app "
					+ "WHERE app.deleted <> :deleted "
					+ "AND app.categoryId = :categoryId";

			TypedQuery<AppointmentCategory> query = em.createQuery(hql, AppointmentCategory.class);
			query.setParameter("deleted", true);
			query.setParameter("categoryId", categoryId);

			AppointmentCategory appointCategory = null;
			try {
				appointCategory = query.getSingleResult();
			} catch (NoResultException ex) {
			}

			return appointCategory;
		} catch (Exception ex2) {
			log.error("[getAppointmentCategoryById]: " + ex2);
		}
		return null;
	}

	public Long updateAppointmentCategory(Long categoryId, String name) {
		try {

			AppointmentCategory ac = this
					.getAppointmentCategoryById(categoryId);

			ac.setName(name);
			ac.setUpdatetime(new Date());

			if (ac.getCategoryId() == null) {
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

	public Long addAppointmentCategory(Long user_id, String name, String comment) {
		try {

			AppointmentCategory ac = new AppointmentCategory();

			ac.setName(name);
			ac.setStarttime(new Date());
			ac.setDeleted(false);
			ac.setUser(usersDao.get(user_id));
			ac.setComment(comment);

			ac = em.merge(ac);
			Long category_id = ac.getCategoryId();

			return category_id;
		} catch (Exception ex2) {
			log.error("[addAppointmentCategory]: ", ex2);
		}
		return null;
	}

	public Long deleteAppointmentCategory(Long categoryId) {
		try {

			AppointmentCategory ac = this
					.getAppointmentCategoryById(categoryId);

			log.debug("ac: " + ac);

			if (ac == null) {
				log.debug("Already deleted / Could not find: " + categoryId);
				return categoryId;
			}
			ac.setUpdatetime(new Date());
			ac.setDeleted(true);
			if (ac.getCategoryId() == null) {
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

			String hql = "select a from AppointmentCategory a "
					+ "WHERE a.deleted <> :deleted ";

			TypedQuery<AppointmentCategory> query = em.createQuery(hql, AppointmentCategory.class);
			query.setParameter("deleted", true);

			List<AppointmentCategory> listAppointmentCategory = query
					.getResultList();

			return listAppointmentCategory;
		} catch (Exception ex2) {
			log.error("[AppointmentCategory]: " + ex2);
		}
		return null;
	}
}
