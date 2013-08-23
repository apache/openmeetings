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
package org.apache.openmeetings.data.user.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.persistence.beans.user.State;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

/**
 * CRUD operations for {@link State}
 * 
 * @author sebawagner
 * 
 */
@Transactional
public class StateDao {
	private static final Logger log = Red5LoggerFactory.getLogger(
			StateDao.class, OpenmeetingsVariables.webAppRootKey);

	@PersistenceContext
	private EntityManager em;

	/**
	 * adds a new State to the states table with no short name and code
	 * 
	 * @param name
	 * @return the id of the new state or null if an error occurred
	 */
	public Long addState(String name) {
		return addState(name, "", 0);
	}
	
	/**
	 * adds a new State to the states table
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
			Long id = st.getState_id();

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
	 * @param state_id
	 * @return the state-object or null
	 */
	public State getStateById(long state_id) {
		try {
			TypedQuery<State> query = em
					.createNamedQuery("getStateById", State.class);
			query.setParameter("state_id", state_id);
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
	 * selects a state by its name
	 * 
	 * @param state_id
	 * @return the state-object or null
	 */
	public State getStateByName(String name) {
		try {
			if (name == null) {
				return null;
			}
			TypedQuery<State> query = em
					.createNamedQuery("getStateByName", State.class);
			query.setParameter("name", name.toLowerCase());
			query.setParameter("deleted", true);
			List<State> ll = query.getResultList();
			if (ll.size() > 0) {
				return ll.get(0);
			}
		} catch (Exception ex2) {
			log.error("getStateByName", ex2);
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
					.createNamedQuery("getStates", State.class);
			query.setParameter("deleted", true);
			List<State> ll = query.getResultList();
			return ll;
		} catch (Exception ex2) {
			log.error("getStates", ex2);
		}
		return null;
	}

}
