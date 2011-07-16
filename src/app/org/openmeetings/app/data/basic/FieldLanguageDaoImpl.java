package org.openmeetings.app.data.basic;

import java.util.List;
import java.util.Date;

import org.slf4j.Logger;
import org.red5.logging.Red5LoggerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import org.openmeetings.app.hibernate.utils.HibernateUtil;

import org.openmeetings.app.hibernate.beans.lang.FieldLanguage;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;

/**
 * 
 * @author sebastianwagner
 *
 */
public class FieldLanguageDaoImpl {

	private static final Logger log = Red5LoggerFactory.getLogger(FieldLanguageDaoImpl.class, ScopeApplicationAdapter.webAppRootKey);

	private FieldLanguageDaoImpl() {
	}

	private static FieldLanguageDaoImpl instance = null;
 
	public static synchronized FieldLanguageDaoImpl getInstance() {
		if (instance == null) {
			instance = new FieldLanguageDaoImpl();
		}
		return instance;
	}
	

	public Long addLanguage(String langName, Boolean langRtl) {
		try {
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();

			FieldLanguage fl = new FieldLanguage();
			fl.setStarttime(new Date());
			fl.setDeleted("false");
			fl.setName(langName);
			fl.setRtl(langRtl);

			fl = session.merge(fl);
			session.flush();
			Long languages_id = fl.getLanguage_id();

			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return languages_id;
		} catch (Exception ex2) {
			log.error("[addLanguage]: ",ex2);
		}
		return null;
	}

 
	public void emptyFieldLanguage() {
		try {
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			
//			 TODO delete hql query doesn't work, must be repared
			session.createQuery("delete from FieldLanguage");
			tx.commit();
			HibernateUtil.closeSession(idf);
		} catch (Exception ex2) {
			log.error("[getConfKey]: ",ex2);
		}
	}
	
	public Long updateFieldLanguage(Long language_id, String langName, String deleted) {
		try {
			FieldLanguage fl = this.getFieldLanguageById(language_id);
			fl.setUpdatetime(new Date());
			if (langName.length()>0) fl.setName(langName);
			fl.setDeleted(deleted);
			this.updateLanguage(fl);
			return language_id;
		} catch (Exception ex2) {
			log.error("[updateLanguage]: ",ex2);
		}
		return new Long(-1);
	}

	
	private void updateLanguage(FieldLanguage fl) throws Exception {
		Object idf = HibernateUtil.createSession();
		EntityManager session = HibernateUtil.getSession();
		EntityTransaction tx = session.getTransaction();
		tx.begin();
		if (fl.getLanguage_id() == null) {
			session.persist(fl);
		    } else {
		    	if (!session.contains(fl)) {
		    		session.merge(fl);
		    }
		}
		tx.commit();
		HibernateUtil.closeSession(idf);
	}	


	public FieldLanguage getFieldLanguageById(Long language_id) {
		try {
			String hql = "select c from FieldLanguage as c " +
					"WHERE c.deleted <> :deleted " +
					"AND c.language_id = :language_id";
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			Query query = session.createQuery(hql);
			query.setParameter("deleted", "true");
			query.setParameter("language_id", language_id);
			FieldLanguage fl = null;
			try {
				fl = (FieldLanguage) query.getSingleResult();
	        } catch (NoResultException ex) {
	        }
			tx.commit();
			HibernateUtil.closeSession(idf);
			return fl;
		} catch (Exception ex2) {
			log.error("[getLanguageById]: ",ex2);
		}
		return null;
	}
	
	public List<FieldLanguage> getLanguages() {
		try {
			String hql = "select c from FieldLanguage as c " +
					"WHERE c.deleted <> :deleted ";
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			Query query = session.createQuery(hql);
			query.setParameter("deleted", "true");
			List<FieldLanguage> ll = query.getResultList();
			tx.commit();
			HibernateUtil.closeSession(idf);
			return ll;
		} catch (Exception ex2) {
			log.error("[getLanguages]: ",ex2);
		}
		return null;
	}

}
