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
package org.apache.openmeetings.db.dao.user;

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.openmeetings.db.dao.label.FieldLanguagesValuesDao;
import org.apache.openmeetings.db.entity.user.Salutation;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * CRUD operations for {@link Salutation}
 * 
 * @author swagner
 * 
 */
@Transactional
public class SalutationDao {
	private static final Logger log = Red5LoggerFactory.getLogger(SalutationDao.class, webAppRootKey);

	@Autowired
	private FieldLanguagesValuesDao fieldLangValDao;

	@PersistenceContext
	private EntityManager em;

	/**
	 * Adds a new Salutation to the table Titles
	 * 
	 * @param titelname
	 * @param fieldvalues_id
	 * @return
	 */
	public Long addUserSalutation(String titelname, long fieldvalues_id) {
		try {
			Salutation ti = new Salutation();
			ti.setName(titelname);
			ti.setDeleted(false);
			ti.setFieldvalues_id(fieldvalues_id);
			ti.setStarttime(new Date());
			ti = em.merge(ti);
			Long salutations_id = ti.getSalutations_id();
			return salutations_id;
		} catch (Exception ex2) {
			log.error("[addUserSalutation]", ex2);
		}
		return null;
	}

	/**
	 * get a Salutation by given id
	 * 
	 * @param id
	 * @param language_id
	 * @return
	 */
	public Salutation get(long id, long language_id) {
		List<Salutation> ll = em.createNamedQuery("getSalutationById", Salutation.class)
				.setParameter("id", id).getResultList();
		for (Salutation ti : ll) {
			ti.setLabel(fieldLangValDao.get(ti.getFieldvalues_id(), language_id));
		}
		return ll.get(0);
	}
	
	/**
	 * get a list of all availible Salutations
	 * 
	 * @param language_id
	 * @return
	 */
	public List<Salutation> getUserSalutations(long language_id) {
		List<Salutation> ll = em.createNamedQuery("getSalutations", Salutation.class).getResultList();
		for (Salutation ti : ll) {
			ti.setLabel(fieldLangValDao.get(ti.getFieldvalues_id(), language_id));
		}
		return ll;
	}
}
