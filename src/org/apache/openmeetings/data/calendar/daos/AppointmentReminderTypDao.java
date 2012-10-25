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
import org.apache.openmeetings.data.user.dao.UsersDao;
import org.apache.openmeetings.persistence.beans.calendar.AppointmentReminderTyps;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class AppointmentReminderTypDao {

	private static final Logger log = Red5LoggerFactory.getLogger(
			AppointmentReminderTypDao.class,
			OpenmeetingsVariables.webAppRootKey);

	@PersistenceContext
	private EntityManager em;
	@Autowired
	private UsersDao usersDao;

	public AppointmentReminderTyps getAppointmentReminderTypById(Long typId) {
		try {
			log.debug("AppointmentReminderTypById: " + typId);

			String hql = "select app from AppointmentReminderTyps app "
					+ "WHERE app.deleted <> :deleted "
					+ "AND app.typId = :typId";

			TypedQuery<AppointmentReminderTyps> query = em.createQuery(hql, AppointmentReminderTyps.class);
			query.setParameter("deleted", true);
			query.setParameter("typId", typId);

			AppointmentReminderTyps appointmentReminderTyps = null;
			try {
				appointmentReminderTyps = query.getSingleResult();
			} catch (NoResultException ex) {
			}

			return appointmentReminderTyps;
		} catch (Exception ex2) {
			log.error("[getAppointmentReminderTypsById]: " + ex2);
		}
		return null;
	}

	public Long updateAppointmentReminderTyps(Long typId, String name) {
		try {

			AppointmentReminderTyps ac = this
					.getAppointmentReminderTypById(typId);

			ac.setName(name);
			ac.setUpdatetime(new Date());

			if (ac.getTypId() == null) {
				em.persist(ac);
			} else {
				if (!em.contains(ac)) {
					em.merge(ac);
				}
			}

			return typId;
		} catch (Exception ex2) {
			log.error("[updateAppointmentReminderTyps]: ", ex2);
		}
		return null;
	}

	public Long addAppointmentReminderTyps(Long user_id, String name) {
		try {

			AppointmentReminderTyps ac = new AppointmentReminderTyps();

			ac.setName(name);
			ac.setStarttime(new Date());
			ac.setDeleted(false);
			ac.setUser(usersDao.get(user_id));

			ac = em.merge(ac);
			Long category_id = ac.getTypId();

			return category_id;
		} catch (Exception ex2) {
			log.error("[addAppointmentReminderTyps]: ", ex2);
		}
		return null;
	}

	public Long deleteAppointmentReminderTyp(Long typId) {
		try {

			AppointmentReminderTyps ac = this
					.getAppointmentReminderTypById(typId);

			log.debug("ac: " + ac);

			if (ac == null) {
				log.debug("Already deleted / Could not find: " + typId);
				return typId;
			}
			ac.setUpdatetime(new Date());
			ac.setDeleted(true);

			if (ac.getTypId() == null) {
				em.persist(ac);
			} else {
				if (!em.contains(ac)) {
					em.merge(ac);
				}
			}

			return typId;
		} catch (Exception ex2) {
			log.error("[deleteAppointmentReminderTyp]: " + ex2);
		}
		return null;
	}

	public List<AppointmentReminderTyps> getAppointmentReminderTypList() {
		log.debug("getAppointmenetReminderTypList");

		try {

			String hql = "select a from AppointmentReminderTyps a "
					+ "WHERE a.deleted <> :deleted ";

			TypedQuery<AppointmentReminderTyps> query = em.createQuery(hql, AppointmentReminderTyps.class);
			query.setParameter("deleted", true);

			List<AppointmentReminderTyps> listAppointmentReminderTyp = query
					.getResultList();

			return listAppointmentReminderTyp;
		} catch (Exception ex2) {
			log.error("[getAppointmentReminderTypList]: " + ex2);
		}
		return null;
	}
}
