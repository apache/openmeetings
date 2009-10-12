package org.openmeetings.app.data.basic;

import java.util.List;
import java.util.Date;

import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.openmeetings.app.hibernate.utils.HibernateUtil;

import org.openmeetings.app.hibernate.beans.lang.FieldLanguage;

/**
 * 
 * @author sebastianwagner
 *
 */
public class FieldLanguageDaoImpl {

	private static final Logger log = Logger.getLogger(FieldLanguageDaoImpl.class);

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
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();

			FieldLanguage fl = new FieldLanguage();
			fl.setStarttime(new Date());
			fl.setDeleted("false");
			fl.setName(langName);
			fl.setRtl(langRtl);

			Long languages_id = (Long)session.save(fl);

			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return languages_id;
		} catch (HibernateException ex) {
			log.error("[addLanguage]: ",ex);
		} catch (Exception ex2) {
			log.error("[addLanguage]: ",ex2);
		}
		return null;
	}

 
	public void emptyFieldLanguage() {
		try {
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			
//			 TODO delete hql query doesn't work, must be repared
			session.createQuery("delete from FieldLanguage");
			tx.commit();
			HibernateUtil.closeSession(idf);
		} catch (HibernateException ex) {
			log.error("[getConfKey]: ",ex);
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
		} catch (HibernateException ex) {
			log.error("[updateLanguage]: ",ex);
		} catch (Exception ex2) {
			log.error("[updateLanguage]: ",ex2);
		}
		return new Long(-1);
	}

	
	private void updateLanguage(FieldLanguage fl) throws Exception {
		Object idf = HibernateUtil.createSession();
		Session session = HibernateUtil.getSession();
		Transaction tx = session.beginTransaction();
		session.update(fl);
		tx.commit();
		HibernateUtil.closeSession(idf);
	}	


	public FieldLanguage getFieldLanguageById(Long language_id) {
		try {
			String hql = "select c from FieldLanguage as c " +
					"WHERE c.deleted != :deleted " +
					"AND c.language_id = :language_id";
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Query query = session.createQuery(hql);
			query.setString("deleted", "true");
			query.setLong("language_id", language_id);
			FieldLanguage fl = (FieldLanguage) query.uniqueResult();
			tx.commit();
			HibernateUtil.closeSession(idf);
			return fl;
		} catch (HibernateException ex) {
			log.error("[getLanguageById]: ",ex);
		} catch (Exception ex2) {
			log.error("[getLanguageById]: ",ex2);
		}
		return null;
	}
	
	public List<FieldLanguage> getLanguages() {
		try {
			String hql = "select c from FieldLanguage as c " +
					"WHERE c.deleted != :deleted ";
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Query query = session.createQuery(hql);
			query.setString("deleted", "true");
			List<FieldLanguage> ll = query.list();
			tx.commit();
			HibernateUtil.closeSession(idf);
			return ll;
		} catch (HibernateException ex) {
			log.error("[getLanguages]: ",ex);
		} catch (Exception ex2) {
			log.error("[getLanguages]: ",ex2);
		}
		return null;
	}

}
