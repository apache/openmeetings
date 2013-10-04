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

import static org.apache.openmeetings.OpenmeetingsVariables.CONFIG_DEFAUT_LANG_KEY;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.openmeetings.db.dao.IDataProviderDao;
import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.entity.label.Fieldlanguagesvalues;
import org.apache.openmeetings.db.entity.label.Fieldvalues;
import org.apache.openmeetings.util.DaoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class FieldValueDao implements IDataProviderDao<Fieldvalues> {
	public final static String[] searchFields = {"value", "fieldvalues.name"};
	
	@PersistenceContext
	private EntityManager em;
	@Autowired
	private ConfigurationDao configurationDao;
	@Autowired
	private FieldLanguagesValuesDao flvDaoImpl;

	private Long getDefaultLanguage() {
		return configurationDao.getConfValue(CONFIG_DEFAUT_LANG_KEY, Long.class, "1");
	}
	
	public Fieldvalues get(long id) {
		return get(getDefaultLanguage(), id);
	}

	public Fieldvalues get(Long language_id, long id) {
		TypedQuery<Fieldvalues> q = em.createNamedQuery("getFieldByIdAndLanguage", Fieldvalues.class);
		q.setParameter("id", id);
		q.setParameter("lang", language_id);
		List<Fieldvalues> l = q.getResultList();
		return l == null || l.isEmpty() ? null : l.get(0);
	}

	public List<Fieldvalues> get(int start, int count) {
		return get(getDefaultLanguage(), start, count);
	}

	public List<Fieldvalues> get(String search, int start, int count, String sort) {
		return get(getDefaultLanguage(), search, start, count, sort);
	}
	
	public List<Fieldvalues> get(Long language_id, String search, int start, int count, String sort) {
		String sql = DaoHelper.getSearchQuery(
				"Fieldlanguagesvalues"
				, "flv"
				, search
				, true
				, false
				, "flv.fieldvalues.deleted = false AND flv.language_id = :lang"
				, sort
				, searchFields);
		TypedQuery<Fieldlanguagesvalues> q = em.createQuery(sql, Fieldlanguagesvalues.class);
		q.setParameter("lang", language_id);
		q.setFirstResult(start);
		q.setMaxResults(count);
		
		//now lets create the list of Fieldvalues
		List<Fieldlanguagesvalues> flvList = q.getResultList();
		List<Fieldvalues> r = new ArrayList<Fieldvalues>(flvList.size());
		for (Fieldlanguagesvalues flv : flvList) {
			Fieldvalues fv = flv.getFieldvalues();
			fv.setFieldlanguagesvalue(flv);
			r.add(fv);
		}
		return r;
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

	public long count(String search) {
		return count(getDefaultLanguage(), search);
	}
	
	public long count(Long language_id, String search) {
		String sql = DaoHelper.getSearchQuery(
				"Fieldlanguagesvalues"
				, "flv"
				, search
				, true
				, true
				, "flv.fieldvalues.deleted = false AND flv.language_id = :lang"
				, null
				, searchFields);
		TypedQuery<Long> q = em.createQuery(sql, Long.class);
		q.setParameter("lang", language_id);
		return q.getSingleResult();
	}
	
	public Fieldvalues update(Fieldvalues entity, Long userId) {
		entity.setDeleted(false);
		if (entity.getFieldvalues_id() == null) {
			entity.setFieldvalues_id(count() + 1);
			entity.setStarttime(new Date());
			em.persist(entity);
		} else {
			entity = em.merge(entity);
		}
		return entity;
	}

	public void delete(Fieldvalues entity, Long userId) {
		// TODO Auto-generated method stub
		
	}

}
