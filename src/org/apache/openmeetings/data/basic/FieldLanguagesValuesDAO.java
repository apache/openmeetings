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
package org.apache.openmeetings.data.basic;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.openmeetings.data.OmDAO;
import org.apache.openmeetings.persistence.beans.lang.Fieldlanguagesvalues;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class FieldLanguagesValuesDAO implements OmDAO<Fieldlanguagesvalues> {
	@PersistenceContext
	private EntityManager em;
	@Autowired
	private Configurationmanagement cfgManagement;

	public List<Fieldlanguagesvalues> get(int first, int count) {
		return get(cfgManagement.getConfValue("default_lang_id", Long.class, "1"), first, count);
	}
	
	public List<Fieldlanguagesvalues> get(Long language_id, int first, int count) {
		// all Fieldlanguagesvalues in current Language
		TypedQuery<Fieldlanguagesvalues> q = em.createNamedQuery("allFieldLanguageValues", Fieldlanguagesvalues.class);
		q.setParameter("language_id", language_id);
		q.setFirstResult(first);
		q.setMaxResults(count);
		return q.getResultList();
	}

	public Fieldlanguagesvalues get(long id) {
		TypedQuery<Fieldlanguagesvalues> q = em.createNamedQuery("getFieldLanguagesValuesById", Fieldlanguagesvalues.class);
		q.setParameter("id", id);
		Fieldlanguagesvalues flv = null;
		try {
			flv = q.getSingleResult();
		} catch (NoResultException e) {
			
		}
		return flv;
	}

	public long count() {
		TypedQuery<Long> q = em.createNamedQuery("getFieldCount", Long.class);
		return q.getSingleResult();
	}

	public void update(Fieldlanguagesvalues entity, long userId) {
		entity.setUpdatetime(new Date());
		if (entity.getFieldlanguagesvalues_id() == null) {
			em.persist(entity);
		} else {
			entity = em.merge(entity);
		}
	}

	public void delete(Fieldlanguagesvalues entity, long userId) {
		entity.setDeleted(true);
		entity.setUpdatetime(new Date());
		entity = em.merge(entity);
	}
}
