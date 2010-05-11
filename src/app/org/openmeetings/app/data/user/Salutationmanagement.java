package org.openmeetings.app.data.user;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.red5.logging.Red5LoggerFactory;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.Criteria;
import org.openmeetings.app.hibernate.beans.user.Salutations;
import org.openmeetings.app.hibernate.utils.HibernateUtil;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;

import org.openmeetings.app.data.basic.AuthLevelmanagement;
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
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Salutations ti = new Salutations();
			ti.setName(titelname);
			ti.setDeleted("false");
			ti.setFieldvalues_id(fieldvalues_id);
			ti.setStarttime(new Date());
			Long salutations_id = (Long)session.save(ti);
			tx.commit();
			HibernateUtil.closeSession(idf);
			return salutations_id;
		} catch (HibernateException ex) {
			log.error("[addUserSalutation]" ,ex);
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
	public List getUserSalutations(long language_id){
		try {
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Criteria crit = session.createCriteria(Salutations.class, ScopeApplicationAdapter.webAppRootKey);
			List ll = crit.list();
			tx.commit();
			HibernateUtil.closeSession(idf);
			for (Iterator it4 = ll.iterator(); it4.hasNext();) {
				Salutations ti = (Salutations) it4.next();
				ti.setLabel(Fieldmanagment.getInstance().getFieldByIdAndLanguage(ti.getFieldvalues_id(),language_id));
			}
			
			
			return ll;
		} catch (HibernateException ex) {
			log.error("[addUserSalutation]" ,ex);
		} catch (Exception ex2) {
			log.error("[addUserSalutation]" ,ex2);
		}
		return null;
	}

}
