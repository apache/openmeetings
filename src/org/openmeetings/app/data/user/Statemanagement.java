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
package org.openmeetings.app.data.user;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.openmeetings.app.OpenmeetingsVariables;
import org.openmeetings.app.persistence.beans.address.State;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author swagner
 * 
 */
@Transactional
public class Statemanagement {
	private static final Logger log = Red5LoggerFactory.getLogger(
			Statemanagement.class, OpenmeetingsVariables.webAppRootKey);

	@PersistenceContext
	private EntityManager em;

	/**
	 * adds a new State to the state table with no short name and code
	 * 
	 * @param name
	 * @return the id of the new state or null if an error occurred
	 */
	public Long addState(String name) {
		return addState(name, "", 0);
	}
	
	/**
	 * adds a new State to the state table
	 * 
	 * @param name the name of the country
	 * @param shortName the short name of the country
	 * @param code the code of the country
	 * @return the id of the new state or null if an error occurred
	 */
	public Long addState(String name, String shortName, int code) {
		try {

			State st = new State();
			st.setName(name);
			st.setShortName(shortName);
			st.setCode(code);
			st.setStarttime(new Date());
			st.setDeleted(false);

			st = em.merge(st);
			Long id = st.getId();

			log.debug("added id " + id);

			return id;
		} catch (Exception ex2) {
			log.error("addState", ex2);
		}
		return null;
	}

	/**
	 * selects a state by its id
	 * 
	 * @param id
	 * @return the state-object or null
	 */
	public State getStateById(long id) {
		try {
			TypedQuery<State> query = em
					.createQuery("select c from State as c where c.id = :id AND c.deleted <> :deleted", State.class);
			query.setParameter("id", id);
			query.setParameter("deleted", true);
			List<State> ll = query.getResultList();
			if (ll.size() > 0) {
				return ll.get(0);
			}
		} catch (Exception ex2) {
			log.error("getStateById", ex2);
		}
		return null;
	}

	/**
	 * Get all state-Object
	 * 
	 * @return List of State Objects or null
	 */
	public List<State> getStates() {
		try {
			TypedQuery<State> query = em
					.createQuery("select c from State as c where c.deleted <> :deleted", State.class);
			query.setParameter("deleted", true);
			List<State> ll = query.getResultList();
			return ll;
		} catch (Exception ex2) {
			log.error("getStates", ex2);
		}
		return null;
	}

}
