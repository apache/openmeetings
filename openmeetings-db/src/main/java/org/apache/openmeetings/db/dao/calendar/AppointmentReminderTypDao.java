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
import org.apache.openmeetings.db.entity.calendar.AppointmentReminderType;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class AppointmentReminderTypDao {
	private static final Logger log = Red5LoggerFactory.getLogger(AppointmentReminderTypDao.class, webAppRootKey);

	@PersistenceContext
	private EntityManager em;
	@Autowired
	private UserDao usersDao;

	public AppointmentReminderType get(Long typId) {
		try {
			log.debug("AppointmentReminderTypeById: " + typId);

			TypedQuery<AppointmentReminderType> query = em.createNamedQuery("getAppointmentReminderTypeById", AppointmentReminderType.class);
			query.setParameter("typId", typId);

			try {
				return query.getSingleResult();
			} catch (NoResultException ex) {
			}
		} catch (Exception ex2) {
			log.error("[get]: " + ex2);
		}
		return null;
	}

	public Long update(Long typId, String name) {
		try {
			AppointmentReminderType ac = get(typId);

			ac.setName(name);
			ac.setUpdatetime(new Date());

			if (ac.getId() == null) {
				em.persist(ac);
			} else {
				if (!em.contains(ac)) {
					em.merge(ac);
				}
			}

			return typId;
		} catch (Exception ex2) {
			log.error("[update]: ", ex2);
		}
		return null;
	}

	public Long add(Long user_id, String name, long fieldvalues_id) {
		try {

			AppointmentReminderType ac = new AppointmentReminderType();

			ac.setName(name);
			ac.setStarttime(new Date());
			ac.setDeleted(false);
			ac.setUser(usersDao.get(user_id));
			ac.setFieldvalues_id(fieldvalues_id);

			ac = em.merge(ac);
			Long category_id = ac.getId();

			return category_id;
		} catch (Exception ex2) {
			log.error("[add]: ", ex2);
		}
		return null;
	}

	public Long delete(Long typId) {
		try {

			AppointmentReminderType ac = get(typId);

			log.debug("ac: " + ac);

			if (ac == null) {
				log.debug("Already deleted / Could not find: " + typId);
				return typId;
			}
			ac.setUpdatetime(new Date());
			ac.setDeleted(true);

			if (ac.getId() == null) {
				em.persist(ac);
			} else {
				if (!em.contains(ac)) {
					em.merge(ac);
				}
			}

			return typId;
		} catch (Exception ex2) {
			log.error("[delete]: " + ex2);
		}
		return null;
	}

	public List<AppointmentReminderType> getList(long language_id) {
		log.debug("getList");
		try {
			TypedQuery<AppointmentReminderType> query = em.createNamedQuery("getAppointmentReminderTypes", AppointmentReminderType.class);

			List<AppointmentReminderType> listAppointmentReminderTyp = query.getResultList();

			return listAppointmentReminderTyp;
		} catch (Exception ex2) {
			log.error("[getList]: " + ex2);
		}
		return null;
	}
}
