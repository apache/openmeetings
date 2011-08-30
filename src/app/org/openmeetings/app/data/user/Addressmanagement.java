package org.openmeetings.app.data.user;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.openmeetings.app.persistence.beans.adresses.Adresses;
import org.openmeetings.app.persistence.beans.adresses.States;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class Addressmanagement {

	private static final Logger log = Red5LoggerFactory.getLogger(
			Addressmanagement.class, ScopeApplicationAdapter.webAppRootKey);

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private Statemanagement statemanagement;

	/**
	 * adds a new record to the adress table
	 * 
	 * @param street
	 * @param zip
	 * @param town
	 * @param states_id
	 * @param additionalname
	 * @param comment
	 * @param fax
	 * @param phone
	 * @param email
	 * @return id of generated Adress-Object or NULL
	 */
	public Long saveAddress(String street, String zip, String town,
			long states_id, String additionalname, String comment, String fax,
			String phone, String email) {
		try {
			States st = statemanagement.getStateById(states_id);

			Adresses adr = new Adresses();
			adr.setAdditionalname(additionalname);
			adr.setComment(comment);
			adr.setStarttime(new Date());
			adr.setFax(fax);
			adr.setStreet(street);
			adr.setTown(town);
			adr.setZip(zip);
			adr.setStates(st);
			adr.setPhone(phone);
			adr.setEmail(email);

			adr = em.merge(adr);
			Long id = adr.getAdresses_id();

			return id;
		} catch (Exception ex2) {
			log.error("saveAddress", ex2);
		}
		return null;
	}

	public Long saveAddressObj(Adresses adr) {
		try {

			adr = em.merge(adr);
			Long id = adr.getAdresses_id();
			log.debug("added id " + id);

			return id;
		} catch (Exception ex2) {
			log.error("saveAddress", ex2);
		}
		return null;
	}

	/**
	 * gets an adress by its id
	 * 
	 * @param adresses_id
	 * @return Adress-Object or NULL
	 */
	public Adresses getAdressbyId(long adresses_id) {
		try {
			String hql = "select c from Adresses as c where c.adresses_id = :adresses_id";
			Query query = em.createQuery(hql);
			query.setParameter("adresses_id", new Long(adresses_id));
			Adresses addr = null;
			try {
				addr = (Adresses) query.getSingleResult();
			} catch (NoResultException ex) {
			}
			return addr;
		} catch (Exception ex2) {
			log.error("getAdressbyId", ex2);
		}
		return null;
	}

	/**
	 * @author o.becherer, swagner
	 * @param email
	 * @return
	 */
	public Adresses retrieveAddressByEmail(String email) throws Exception {
		log.debug("retrieveAddressByEmail : " + email);

		String hql = "select c from Adresses as c "
				+ "where c.email LIKE :email";
		// "and c.deleted <> :deleted";
		Query query = em.createQuery(hql);
		query.setParameter("email", email);
		// query.setParameter("deleted", "true");

		List<Adresses> addr = query.getResultList();

		log.debug("retrieveAddressByEmail " + addr.size());

		if (addr.size() > 0) {
			return addr.get(0);
		}

		return null;
	}

	/**
	 * updates an Adress-Record by its given Id
	 * 
	 * @param adresses_id
	 * @param street
	 * @param zip
	 * @param town
	 * @param states_id
	 * @param additionalname
	 * @param comment
	 * @param fax
	 * @return the updated Adress-Object or null
	 */
	public Adresses updateAdress(long adresses_id, String street, String zip,
			String town, long states_id, String additionalname, String comment,
			String fax, String email, String phone) {
		try {
			States st = statemanagement.getStateById(states_id);

			Adresses adr = this.getAdressbyId(adresses_id);

			adr.setAdditionalname(additionalname);
			adr.setComment(comment);
			adr.setUpdatetime(new Date());
			adr.setFax(fax);
			adr.setStreet(street);
			adr.setTown(town);
			adr.setZip(zip);
			adr.setStates(st);
			adr.setPhone(phone);
			adr.setEmail(email);

			if (adr.getAdresses_id() == null) {
				em.persist(adr);
			} else {
				if (!em.contains(adr)) {
					em.merge(adr);
				}
			}

			return adr;
		} catch (Exception ex2) {
			log.error("updateAdress", ex2);
		}
		return null;
	}

	/**
	 * 
	 * @param addr
	 * @return
	 */
	public Adresses updateAdress(Adresses addr) {
		log.debug("updateAddress");

		try {

			if (addr.getAdresses_id() == null) {
				em.persist(addr);
			} else {
				if (!em.contains(addr)) {
					em.merge(addr);
				}
			}

			return addr;
		} catch (Exception ex2) {
			log.error("updateAdress", ex2);
		}
		return null;
	}

}
