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
package org.apache.openmeetings.db.dao.label;

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.openmeetings.db.entity.label.FieldLanguage;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author sebastianwagner
 * 
 */
@Transactional
public class FieldLanguageDao {
	private static final Logger log = Red5LoggerFactory.getLogger(FieldLanguageDao.class, webAppRootKey);

	@PersistenceContext
	private EntityManager em;

	public long getNextAvailableId() {
		TypedQuery<Long> q = em.createNamedQuery("getNextLanguageId", Long.class);
		return q.getSingleResult() + 1L;
	}
	
	public FieldLanguage addLanguage(int langId, String langName, Boolean langRtl, String code) {
		try {
			FieldLanguage fl = new FieldLanguage();
			fl.setId((long)langId);
			fl.setStarttime(new Date());
			fl.setDeleted(false);
			fl.setName(langName);
			fl.setRtl(langRtl);
			fl.setCode(code);

			fl = em.merge(fl);

			//Eagerly FETCH values list
			TypedQuery<FieldLanguage> q = em.createNamedQuery("getFullLanguageById", FieldLanguage.class);
			q.setParameter("id", langId);
			List<FieldLanguage> results = q.getResultList();
			
			return results != null && !results.isEmpty() ? results.get(0) : null;
		} catch (Exception ex2) {
			log.error("[addLanguage]: ", ex2);
		}
		return null;
	}

	public void emptyFieldLanguage() {
		try {
			TypedQuery<FieldLanguage> q = em.createNamedQuery("deleteAllLanguages", FieldLanguage.class);
			q.executeUpdate();
		} catch (Exception ex2) {
			log.error("[emptyFieldLanguage]: ", ex2);
		}
	}

	public void delete(FieldLanguage fl) {
		fl.setUpdatetime(new Date());
		fl.setDeleted(true);
		em.merge(fl);
	}

	public void update(FieldLanguage fl) {
		if (fl.getId() == null) {
			em.persist(fl);
		} else {
			em.merge(fl);
		}
	}

	public FieldLanguage get(Long id) {
		TypedQuery<FieldLanguage> query = em.createNamedQuery("getLanguageById", FieldLanguage.class);
		query.setParameter("id", id);
		FieldLanguage fl = null;
		try {
			fl = query.getSingleResult();
		} catch (NoResultException ex) {
		}
		return fl;
	}

	public List<FieldLanguage> get() {
		TypedQuery<FieldLanguage> query = em.createNamedQuery("getLanguages", FieldLanguage.class);
		return query.getResultList();
	}
}
