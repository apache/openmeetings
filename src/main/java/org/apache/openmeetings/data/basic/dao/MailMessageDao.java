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
package org.apache.openmeetings.data.basic.dao;

import java.util.Calendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.openmeetings.data.IDataProviderDao;
import org.apache.openmeetings.persistence.beans.basic.MailMessage;
import org.apache.openmeetings.persistence.beans.basic.MailMessage.Status;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class MailMessageDao  implements IDataProviderDao<MailMessage> {
	@PersistenceContext
	private EntityManager em;
	
	public MailMessage get(long id) {
		return em.createNamedQuery("getMailMessageById", MailMessage.class).setParameter("id", id).getSingleResult();
	}

	public List<MailMessage> get(int start, int count) {
		return em.createNamedQuery("getMailMessages", MailMessage.class).setParameter("status", Status.NONE)
				.setFirstResult(start).setMaxResults(count).getResultList();
	}

	public List<MailMessage> get(String search, int start, int count, String order) {
		return get(start, count);
	}

	public long count() {
		return em.createNamedQuery("countMailMessages", Long.class).setParameter("status", Status.NONE)
				.getSingleResult();
	}

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

	public void delete(MailMessage m, Long userId) {
		em.remove(m);
	}
}
