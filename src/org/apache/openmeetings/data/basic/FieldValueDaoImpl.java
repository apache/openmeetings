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
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.openmeetings.data.OmDAO;
import org.apache.openmeetings.data.basic.dao.ConfigurationDaoImpl;
import org.apache.openmeetings.persistence.beans.lang.Fieldlanguagesvalues;
import org.apache.openmeetings.persistence.beans.lang.Fieldvalues;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class FieldValueDaoImpl implements OmDAO<Fieldvalues> {
	@PersistenceContext
	private EntityManager em;
	@Autowired
	private ConfigurationDaoImpl configurationDaoImpl;
	@Autowired
	private FieldLanguagesValuesDaoImpl flvDaoImpl;

	public Fieldvalues get(long id) {
		return get(configurationDaoImpl.getConfValue("default_lang_id",
				Long.class, "1"), id);
	}

	public Fieldvalues get(Long language_id, long id) {
		TypedQuery<Fieldvalues> q = em.createNamedQuery("getFieldByIdAndLanguage", Fieldvalues.class);
		q.setParameter("id", id);
		q.setParameter("lang", language_id);
		List<Fieldvalues> l = q.getResultList();
		return l == null || l.isEmpty() ? null : l.get(0);
	}

	public List<Fieldvalues> get(int start, int count) {
		return get(configurationDaoImpl.getConfValue("default_lang_id",
				Long.class, "1"), start, count);
	}

	public List<Fieldvalues> get(Long language_id, int start, int count) {
		TypedQuery<Fieldvalues> q = em.createNamedQuery("getFieldByLanguage", Fieldvalues.class);
		//q.setParameter("lang", language_id); //FIXME commented for now
		q.setFirstResult(start);
		q.setMaxResults(count);
		List<Fieldvalues> result = q.getResultList();
		for (Fieldvalues fv : result) {
			//FIXME ineffective !!!!!!!!!!!!!!!!!!!!
			Fieldlanguagesvalues flv = flvDaoImpl.get(fv.getFieldvalues_id(), language_id);
			if (flv == null) {
				flv = new Fieldlanguagesvalues();
				flv.setLanguage_id(language_id);
				flv.setFieldvalues(fv);
			}
			fv.setFieldlanguagesvalue(flv);
		}
		return result;
	}

	public long count() {
		TypedQuery<Long> q = em.createNamedQuery("getFieldCount", Long.class);
		return q.getSingleResult();
	}

	public void update(Fieldvalues entity, long userId) {
		entity.setDeleted(false);
		if (entity.getFieldvalues_id() == null) {
			entity.setFieldvalues_id(count() + 1);
			entity.setStarttime(new Date());
			em.persist(entity);
		} else {
			entity = em.merge(entity);
		}
	}

	public void delete(Fieldvalues entity, long userId) {
		// TODO Auto-generated method stub
		
	}

}
