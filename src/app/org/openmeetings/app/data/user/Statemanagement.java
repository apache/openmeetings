package org.openmeetings.app.data.user;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import org.openmeetings.app.hibernate.beans.adresses.States;
import org.openmeetings.app.hibernate.utils.HibernateUtil;

/**
 * 
 * @author swagner
 *
 */
public class Statemanagement {
	private static final Logger log = Logger.getLogger(Statemanagement.class);

	private static Statemanagement instance = null;

	public static synchronized Statemanagement getInstance() {
		if (instance == null) {
			instance = new Statemanagement();
		}
		return instance;
	}

	/**
	 * adds a new State to the states table
	 * @param statename
	 * @return the id of the new state or null if an error occurred
	 */
	public Long addState(String statename) {
		try {
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();

			States st = new States();
			st.setName(statename);
			st.setStarttime(new Date());
			st.setDeleted("false");

			Long id = (Long) session.save(st);

			tx.commit();
			HibernateUtil.closeSession(idf);

			log.debug("added id " + id);

			return id;
		} catch (HibernateException ex) {
			log.error("addState",ex);
		} catch (Exception ex2) {
			log.error("addState",ex2);
		}
		return null;
	}

	/**
	 * selects a state by its id
	 * @param state_id
	 * @return the state-object or null
	 */
	public States getStateById(long state_id) {
		try {
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Query query = session
					.createQuery("select c from States as c where c.state_id = :state_id AND deleted != :deleted");
			query.setLong("state_id", state_id);
			query.setString("deleted", "true");
			List ll = query.list();
			tx.commit();
			HibernateUtil.closeSession(idf);
			if (ll.size() > 0) {
				return (States) ll.get(0);
			}
		} catch (HibernateException ex) {
			log.error("getStateById",ex);
		} catch (Exception ex2) {
			log.error("getStateById",ex2);
		}
		return null;
	}

	/**
	 * Get all state-Object
	 * @return List of State Objects or null
	 */
	public List<States> getStates() {
		try {
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Query query = session
					.createQuery("select c from States as c where deleted != :deleted");
			query.setString("deleted", "true");
			List<States> ll = query.list();
			tx.commit();
			HibernateUtil.closeSession(idf);
			return ll;
		} catch (HibernateException ex) {
			log.error("getStates",ex);
		} catch (Exception ex2) {
			log.error("getStates",ex2);
		}
		return null;
	}

}
