package org.openmeetings.app.data.calendar.daos;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.openmeetings.app.data.basic.Configurationmanagement;
import org.openmeetings.app.data.user.dao.UsersDaoImpl;
import org.openmeetings.app.persistence.beans.calendar.AppointmentCategory;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class AppointmentCategoryDaoImpl {

	private static final Logger log = Red5LoggerFactory.getLogger(
			Configurationmanagement.class,
			ScopeApplicationAdapter.webAppRootKey);

	@PersistenceContext
	private EntityManager em;
	@Autowired
	private UsersDaoImpl usersDao;

	public AppointmentCategory getAppointmentCategoryById(Long categoryId) {
		try {
			log.debug("getAppointmentCategoryById: " + categoryId);

			String hql = "select app from AppointmentCategory app "
					+ "WHERE app.deleted <> :deleted "
					+ "AND app.categoryId = :categoryId";

			Query query = em.createQuery(hql);
			query.setParameter("deleted", "true");
			query.setParameter("categoryId", categoryId);

			AppointmentCategory appointCategory = null;
			try {
				appointCategory = (AppointmentCategory) query.getSingleResult();
			} catch (NoResultException ex) {
			}

			return appointCategory;
		} catch (Exception ex2) {
			log.error("[getAppointmentCategoryById]: " + ex2);
		}
		return null;
	}

	public Long updateAppointmentCategory(Long categoryId, String name) {
		try {

			AppointmentCategory ac = this
					.getAppointmentCategoryById(categoryId);

			ac.setName(name);
			ac.setUpdatetime(new Date());

			if (ac.getCategoryId() == null) {
				em.persist(ac);
			} else {
				if (!em.contains(ac)) {
					em.merge(ac);
				}
			}

			return categoryId;
		} catch (Exception ex2) {
			log.error("[updateAppointmentCategory]: ", ex2);
		}
		return null;
	}

	public Long addAppointmentCategory(Long user_id, String name, String comment) {
		try {

			AppointmentCategory ac = new AppointmentCategory();

			ac.setName(name);
			ac.setStarttime(new Date());
			ac.setDeleted("false");
			ac.setUser(usersDao.getUser(user_id));
			ac.setComment(comment);

			ac = em.merge(ac);
			Long category_id = ac.getCategoryId();

			return category_id;
		} catch (Exception ex2) {
			log.error("[addAppointmentCategory]: ", ex2);
		}
		return null;
	}

	public Long deleteAppointmentCategory(Long categoryId) {
		try {

			AppointmentCategory ac = this
					.getAppointmentCategoryById(categoryId);

			log.debug("ac: " + ac);

			if (ac == null) {
				log.debug("Already deleted / Could not find: " + categoryId);
				return categoryId;
			}
			ac.setUpdatetime(new Date());
			ac.setDeleted("true");
			if (ac.getCategoryId() == null) {
				em.persist(ac);
			} else {
				if (!em.contains(ac)) {
					em.merge(ac);
				}
			}
			return categoryId;
		} catch (Exception ex2) {
			log.error("[deleteAppointmentCategory]: " + ex2);
		}
		return null;
	}

	public List<AppointmentCategory> getAppointmentCategoryList() {
		try {

			String hql = "select a from AppointmentCategory a "
					+ "WHERE a.deleted <> :deleted ";

			Query query = em.createQuery(hql);
			query.setParameter("deleted", "true");

			@SuppressWarnings("unchecked")
			List<AppointmentCategory> listAppointmentCategory = query
					.getResultList();

			return listAppointmentCategory;
		} catch (Exception ex2) {
			log.error("[AppointmentCategory]: " + ex2);
		}
		return null;
	}
}
