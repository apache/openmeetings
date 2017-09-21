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
package org.apache.openmeetings.db.dao.room;

import static org.apache.openmeetings.util.CalendarHelper.getZoneId;
import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.openmeetings.db.entity.room.Invitation;
import org.apache.openmeetings.util.CalendarHelper;
import org.apache.wicket.util.string.Strings;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class InvitationDao {
	private static final Logger log = Red5LoggerFactory.getLogger(InvitationDao.class, webAppRootKey);

	@PersistenceContext
	private EntityManager em;

	public Invitation update(Invitation invitation) {
		if (invitation.getId() == null) {
			invitation.setInserted(new Date());
			em.persist(invitation);
		} else {
			invitation.setUpdated(new Date());
			invitation = em.merge(invitation);
		}
		return invitation;
	}

	public Invitation get(Long invId) {
		try {
			TypedQuery<Invitation> query = em.createNamedQuery("getInvitationbyId", Invitation.class);
			query.setParameter("id", invId);
			try {
				return query.getSingleResult();
			} catch (NoResultException ex) {
			}
		} catch (Exception e) {
			log.error("get : ", e);
		}
		return null;
	}

	public Invitation getByHash(String hash, boolean hidePass, boolean markUsed) {
		List<Invitation> list = em.createNamedQuery("getInvitationByHashCode", Invitation.class)
				.setParameter("hashCode", hash).getResultList();
		Invitation i = list != null && list.size() == 1 ? list.get(0) : null;
		if (i != null) {
			switch (i.getValid()) {
				case OneTime:
					// one-time invitation
					i.setAllowEntry(!i.isUsed());
					if (markUsed) {
						i.setUsed(true);
						update(i);
						em.flush(); // flash is required to eliminate 'detach' effect
					}
					break;
				case Period:
					String tzId = i.getInvitee().getTimeZoneId();
					if (Strings.isEmpty(tzId) && i.getAppointment() != null) {
						log.warn("User with NO timezone found: {}", i.getInvitee().getId());
						tzId = i.getAppointment().getOwner().getTimeZoneId();
					}
					if (Strings.isEmpty(tzId)) {
						log.warn("Unable to obtain valid timezone, setting SYSTEM TZ");
						tzId = TimeZone.getDefault().getID();
					}
					LocalDateTime now = ZonedDateTime.now(getZoneId(tzId)).toLocalDateTime();
					LocalDateTime from = CalendarHelper.getDateTime(i.getValidFrom(), tzId);
					LocalDateTime to = CalendarHelper.getDateTime(i.getValidTo(), tzId);
					i.setAllowEntry(now.isAfter(from) && now.isBefore(to));
					break;
				case Endless:
				default:
					i.setAllowEntry(true);
					break;
			}
			em.detach(i); // required to disable password update
			if (hidePass) {
				i.setPassword(null);
			}
		}
		return i;
	}
}
