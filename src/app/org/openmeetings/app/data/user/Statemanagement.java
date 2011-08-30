package org.openmeetings.app.data.user;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.openmeetings.app.persistence.beans.adresses.States;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
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
			Statemanagement.class, ScopeApplicationAdapter.webAppRootKey);

	@PersistenceContext
	private EntityManager em;

	/**
	 * adds a new State to the states table
	 * 
	 * @param statename
	 * @return the id of the new state or null if an error occurred
	 */
	public Long addState(String statename) {
		try {

			States st = new States();
			st.setName(statename);
			st.setStarttime(new Date());
			st.setDeleted("false");

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
	public States getStateById(long state_id) {
		try {
			Query query = em
					.createQuery("select c from States as c where c.state_id = :state_id AND c.deleted <> :deleted");
			query.setParameter("state_id", state_id);
			query.setParameter("deleted", "true");
			List ll = query.getResultList();
			if (ll.size() > 0) {
				return (States) ll.get(0);
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
	public List<States> getStates() {
		try {
			Query query = em
					.createQuery("select c from States as c where c.deleted <> :deleted");
			query.setParameter("deleted", "true");
			List<States> ll = query.getResultList();
			return ll;
		} catch (Exception ex2) {
			log.error("getStates", ex2);
		}
		return null;
	}

}
