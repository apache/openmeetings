package org.openmeetings.app.data.user;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.red5.logging.Red5LoggerFactory;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.openmeetings.app.hibernate.beans.user.Salutations;
import org.openmeetings.app.hibernate.utils.HibernateUtil;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;

import org.openmeetings.app.data.basic.Fieldmanagment;

/**
 * 
 * @author swagner
 *
 */
public class Salutationmanagement {

	private static final Logger log = Red5LoggerFactory.getLogger(Salutationmanagement.class, ScopeApplicationAdapter.webAppRootKey);

	private static Salutationmanagement instance = null;

	private Salutationmanagement() {
	}

	public static synchronized Salutationmanagement getInstance() {
		if (instance == null) {
			instance = new Salutationmanagement();
		}
		return instance;
	}

	/**
	 * Adds a new Salutation to the table Titles
	 * @param titelname
	 */
	public Long addUserSalutation(String titelname, long fieldvalues_id) {
		try {
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			Salutations ti = new Salutations();
			ti.setName(titelname);
			ti.setDeleted("false");
			ti.setFieldvalues_id(fieldvalues_id);
			ti.setStarttime(new Date());
			ti = session.merge(ti);
			Long salutations_id = ti.getSalutations_id();
			tx.commit();
			HibernateUtil.closeSession(idf);
			return salutations_id;
		} catch (Exception ex2) {
			log.error("[addUserSalutation]" ,ex2);
		}
		return null;
	}
	
	/**
	 * get a list of all availible Salutations
	 * @param user_level
	 * @return
	 */
	public List<Salutations> getUserSalutations(long language_id){
		try {
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			CriteriaBuilder cb = session.getCriteriaBuilder();
			CriteriaQuery<Salutations> cq = cb.createQuery(Salutations.class);
			Root<Salutations> from = cq.from(Salutations.class);
			CriteriaQuery<Salutations> select = cq.select(from);
			TypedQuery<Salutations> q = session.createQuery(select);
			List<Salutations> ll = q.getResultList();
			tx.commit();
			HibernateUtil.closeSession(idf);
			for (Iterator it4 = ll.iterator(); it4.hasNext();) {
				Salutations ti = (Salutations) it4.next();
				ti.setLabel(Fieldmanagment.getInstance().getFieldByIdAndLanguage(ti.getFieldvalues_id(),language_id));
			}
			
			
			return ll;
		} catch (Exception ex2) {
			log.error("[addUserSalutation]" ,ex2);
		}
		return null;
	}

}
