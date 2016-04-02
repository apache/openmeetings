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
package org.apache.openmeetings.db.dao.basic;

import java.util.Calendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.openmeetings.db.dao.IDataProviderDao;
import org.apache.openmeetings.db.entity.basic.MailMessage;
import org.apache.openmeetings.db.entity.basic.MailMessage.Status;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class MailMessageDao  implements IDataProviderDao<MailMessage> {
	@PersistenceContext
	private EntityManager em;
	
	@Override
	public MailMessage get(Long id) {
		return em.createNamedQuery("getMailMessageById", MailMessage.class).setParameter("id", id).getSingleResult();
	}

	@Override
	public List<MailMessage> get(int start, int count) {
		return em.createNamedQuery("getMailMessages", MailMessage.class)
				.setFirstResult(start).setMaxResults(count).getResultList();
	}

	public List<MailMessage> get(int start, int count, Status status) {
		return em.createNamedQuery("getMailMessagesByStatus", MailMessage.class).setParameter("status", status)
				.setFirstResult(start).setMaxResults(count).getResultList();
	}
	
	@Override
	public List<MailMessage> get(String search, int start, int count, String order) {
		return get(start, count);
	}

	@Override
	public long count() {
		return em.createNamedQuery("countMailMessages", Long.class).getSingleResult();
	}

	@Override
	public long count(String search) {
		return count();
	}

	public void resetSendingStatus(Calendar date) {
		em.createQuery("UPDATE MailMessage m SET m.status = :noneStatus WHERE m.status = :sendingStatus " +
				"	AND m.updated < :date")
			.setParameter("noneStatus", Status.NONE)
			.setParameter("sendingStatus", Status.SENDING)
			.setParameter("date", date)
			.executeUpdate();
	}
	
	@Override
	public MailMessage update(MailMessage m, Long userId) {
		if (m.getId() == null) {
			m.setInserted(Calendar.getInstance());
			em.persist(m);
		} else {
			m.setUpdated(Calendar.getInstance());
			m =	em.merge(m);
		}
		return m;
	}

	@Override
	public void delete(MailMessage m, Long userId) {
		em.remove(m);
	}
}
