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

import static org.apache.openmeetings.db.util.DaoHelper.setLimits;
import static org.apache.openmeetings.util.OpenmeetingsVariables.PARAM_STATUS;

import java.util.Calendar;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.apache.openmeetings.db.dao.IDataProviderDao;
import org.apache.openmeetings.db.entity.basic.MailMessage;
import org.apache.openmeetings.db.entity.basic.MailMessage.Status;
import org.apache.openmeetings.db.util.DaoHelper;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class MailMessageDao implements IDataProviderDao<MailMessage> {
	private static final List<String> searchFields = List.of("recipients", "subject", "body", "lastError");
	@PersistenceContext
	private EntityManager em;

	@Override
	public MailMessage get(Long id) {
		return em.createNamedQuery("getMailMessageById", MailMessage.class).setParameter("id", id).getSingleResult();
	}

	@Override
	public List<MailMessage> get(long start, long count) {
		return setLimits(em.createNamedQuery("getMailMessages", MailMessage.class)
				, start, count).getResultList();
	}

	public List<MailMessage> get(long start, long count, Status status) {
		return setLimits(em.createNamedQuery("getMailMessagesByStatus", MailMessage.class).setParameter(PARAM_STATUS, status)
				, start, count).getResultList();
	}

	@Override
	public List<MailMessage> get(String search, long start, long count, SortParam<String> sort) {
		return DaoHelper.get(em, MailMessage.class, false, search, searchFields, false, null, sort, start, count);
	}

	@Override
	public long count() {
		return em.createNamedQuery("countMailMessages", Long.class).getSingleResult();
	}

	@Override
	public long count(String search) {
		return DaoHelper.count(em, MailMessage.class, search, searchFields, false, null);
	}

	public void resetSendingStatus(Calendar date) {
		em.createNamedQuery("resetMailStatusByDate")
			.setParameter("noneStatus", Status.NONE)
			.setParameter("sendingStatus", Status.SENDING)
			.setParameter("date", date)
			.executeUpdate();
	}

	public void resetSendingStatus(Long id) {
		em.createNamedQuery("resetMailStatusById")
			.setParameter("noneStatus", Status.NONE)
			.setParameter("id", id)
			.executeUpdate();
	}

	@Override
	public MailMessage update(MailMessage m, Long userId) {
		if (m.getId() == null) {
			em.persist(m);
		} else {
			m = em.merge(m);
		}
		return m;
	}

	@Override
	public void delete(MailMessage m, Long userId) {
		if (m != null) {
			delete(m.getId());
		}
	}

	/**
	 * @param id - entity id
	 */
	public void delete(Long id) {
		if (id != null) {
			em.remove(get(id));
		}
	}
}
