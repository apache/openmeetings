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

import static org.apache.openmeetings.persistence.beans.basic.Configuration.DEFAUT_LANG_KEY;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.openmeetings.data.IDataProviderDao;
import org.apache.openmeetings.data.basic.dao.ConfigurationDao;
import org.apache.openmeetings.persistence.beans.lang.Fieldlanguagesvalues;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * CRUD operations for {@link Fieldlanguagesvalues}
 * 
 * @author solomax, swagner
 * 
 */
@Transactional
public class FieldLanguagesValuesDao implements IDataProviderDao<Fieldlanguagesvalues> {
	@PersistenceContext
	private EntityManager em;
	@Autowired
	private ConfigurationDao configurationDao;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.openmeetings.data.OmDAO#get(int, int)
	 */
	public List<Fieldlanguagesvalues> get(int first, int count) {
		return get(configurationDao.getConfValue(DEFAUT_LANG_KEY, Long.class, "1"), first, count);
	}

	public List<Fieldlanguagesvalues> get(String search, int start, int count, String sort) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public List<Fieldlanguagesvalues> get(Long language_id, String search, int start, int count, String sort) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * Advanced query to set the language id
	 * 
	 * @param language_id
	 * @param first
	 * @param count
	 * @return
	 */
	public List<Fieldlanguagesvalues> get(Long language_id, int first, int count) {
		// all Fieldlanguagesvalues in current Language
		TypedQuery<Fieldlanguagesvalues> q = em.createNamedQuery(
				"allFieldLanguageValues", Fieldlanguagesvalues.class);
		q.setParameter("language_id", language_id);
		q.setFirstResult(first);
		q.setMaxResults(count);
		return q.getResultList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.openmeetings.data.OmDAO#get(long)
	 */
	public Fieldlanguagesvalues get(long id) {
		TypedQuery<Fieldlanguagesvalues> q = em.createNamedQuery(
				"getFieldLanguagesValuesById", Fieldlanguagesvalues.class);
		q.setParameter("id", id);
		Fieldlanguagesvalues flv = null;
		try {
			flv = q.getSingleResult();
		} catch (NoResultException e) {

		}
		return flv;
	}

	public Fieldlanguagesvalues get(long fieldValuesId, long langId) {
		TypedQuery<Fieldlanguagesvalues> q = em.createNamedQuery(
				"getFieldLanguagesValuesByValueAndLang", Fieldlanguagesvalues.class);
		q.setParameter("fieldValuesId", fieldValuesId);
		q.setParameter("lang", langId);
		Fieldlanguagesvalues flv = null;
		try {
			flv = q.getSingleResult();
		} catch (NoResultException e) {
		}
		return flv;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.openmeetings.data.OmDAO#count()
	 */
	public long count() {
		TypedQuery<Long> q = em.createNamedQuery("getFieldCount", Long.class);
		return q.getSingleResult();
	}

	public long count(String search) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.openmeetings.data.OmDAO#update(org.apache.openmeetings.persistence
	 * .beans.OmEntity, long)
	 */
	public Fieldlanguagesvalues update(Fieldlanguagesvalues entity, Long userId) {
		entity.setUpdatetime(new Date());
		if (entity.getFieldlanguagesvalues_id() == null) {
			em.persist(entity);
		} else {
			entity = em.merge(entity);
		}
		return entity;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.openmeetings.data.OmDAO#delete(org.apache.openmeetings.persistence
	 * .beans.OmEntity, long)
	 */
	public void delete(Fieldlanguagesvalues entity, Long userId) {
		entity.setDeleted(true);
		entity.setUpdatetime(new Date());
		entity = em.merge(entity);
	}
}
