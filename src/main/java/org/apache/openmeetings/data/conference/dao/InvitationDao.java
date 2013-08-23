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
package org.apache.openmeetings.data.conference.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.persistence.beans.invitation.Invitations;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class InvitationDao {
	
	private static final Logger log = Red5LoggerFactory.getLogger(
			InvitationDao.class, OpenmeetingsVariables.webAppRootKey);
	
	@PersistenceContext
	private EntityManager em;
	
	public Invitations updateInvitation(Invitations invitation) throws Exception {
		invitation.setUpdatetime(new Date());
		if (invitation.getInvitations_id() == null) {
			em.persist(invitation);
			return invitation;
		} else {
			if (!em.contains(invitation)) {
				return em.merge(invitation);
			}
		}
		return null;
	}
	
	public Invitations getInvitationbyId(Long invId) {
		try {
			
			TypedQuery<Invitations> query = em.createNamedQuery("getInvitationbyId", Invitations.class);
			query.setParameter("deleted", true);
			query.setParameter("invid", invId);
			
			try {
				return query.getSingleResult();
			} catch (NoResultException ex) {
			}

		} catch (Exception e) {
			log.error("getInvitationbyId : ", e);
		}
		return null;
	}
	
	public Invitations getInvitationByHashCode(String hashCode, boolean hidePass) {
		try {
			
			TypedQuery<Invitations> query = em.createNamedQuery("getInvitationByHashCode", Invitations.class);
			query.setParameter("hashCode", hashCode);
			query.setParameter("deleted", false);
			
			try {
				return query.getSingleResult();
			} catch (NoResultException ex) {
			}
			
		} catch (Exception e) {
			log.error("getInvitationbyAppointementId : ", e);
		}
		return null;
	}
	
	public void updateInvitationByAppointment(Long appointmentId,
			Date appointmentstart, Date appointmentend) {
		try {

			Date gmtTimeStartShifted = new Date(appointmentstart.getTime()
					- (5 * 60 * 1000));

			TypedQuery<Invitations> query = em.createNamedQuery("getInvitationByAppointment", Invitations.class);
			query.setParameter("appointmentId", appointmentId);

			List<Invitations> listInvitations = query.getResultList();

			for (Invitations inv : listInvitations) {
				inv.setValidFrom(gmtTimeStartShifted);
				inv.setValidTo(appointmentend);
				if (inv.getInvitations_id() == null) {
					em.persist(inv);
				} else {
					if (!em.contains(inv)) {
						em.merge(inv);
					}
				}
			}

		} catch (Exception err) {
			log.error("updateInvitationByAppointment : ", err);
		}
	}
}
