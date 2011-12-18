package org.openmeetings.app.data.user;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.openmeetings.app.data.basic.Fieldmanagment;
import org.openmeetings.app.persistence.beans.user.Salutations;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author swagner
 * 
 */
@Transactional
public class Salutationmanagement {

	private static final Logger log = Red5LoggerFactory.getLogger(
			Salutationmanagement.class, ScopeApplicationAdapter.webAppRootKey);

	@Autowired
	private Fieldmanagment fieldmanagment;

	@PersistenceContext
	private EntityManager em;

	/**
	 * Adds a new Salutation to the table Titles
	 * 
	 * @param titelname
	 */
	public Long addUserSalutation(String titelname, long fieldvalues_id) {
		try {
			Salutations ti = new Salutations();
			ti.setName(titelname);
			ti.setDeleted("false");
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
	 * get a list of all availible Salutations
	 * 
	 * @param user_level
	 * @return
	 */
	public List<Salutations> getUserSalutations(long language_id) {
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Salutations> cq = cb.createQuery(Salutations.class);
			Root<Salutations> from = cq.from(Salutations.class);
			CriteriaQuery<Salutations> select = cq.select(from);
			TypedQuery<Salutations> q = em.createQuery(select);
			List<Salutations> ll = q.getResultList();
			for (Salutations ti : ll) {
				ti.setLabel(fieldmanagment.getFieldByIdAndLanguage(
						ti.getFieldvalues_id(), language_id));
			}

			return ll;
		} catch (Exception ex2) {
			log.error("[addUserSalutation]", ex2);
		}
		return null;
	}

}
