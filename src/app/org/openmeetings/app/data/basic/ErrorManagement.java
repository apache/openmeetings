package org.openmeetings.app.data.basic;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.openmeetings.app.hibernate.beans.basic.ErrorType;
import org.openmeetings.app.hibernate.beans.basic.ErrorValues;
import org.openmeetings.app.hibernate.utils.HibernateUtil;

public class ErrorManagement {
	private static final Logger log = Logger.getLogger(ErrorManagement.class);

	private ErrorManagement() {
	}

	private static ErrorManagement instance = null;

	public static synchronized ErrorManagement getInstance() {
		if (instance == null) {
			instance = new ErrorManagement();
		}
		return instance;
	}
	 
	public Long addErrorType(Long errortype_id, Long fieldvalues_id) {
		try {
			ErrorType eType = new ErrorType();
			eType.setErrortype_id(errortype_id);
			eType.setStarttime(new Date());
			eType.setDeleted("false");
			eType.setFieldvalues_id(fieldvalues_id);
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Long newerrortype_id = (Long) session.save(eType);
			tx.commit();
			HibernateUtil.closeSession(idf);
			return newerrortype_id;
		} catch (HibernateException ex) {
			log.error("[addErrorType]: " + ex);
		} catch (Exception ex2) {
			log.error("[addErrorType]: " + ex2);
		}
		return null;
	}
	
	public List<ErrorType> getErrorTypes() {
		try {
			String hql = "select c from ErrorType as c " +
					"WHERE c.deleted != :deleted ";			
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Query query = session.createQuery(hql);
			query.setString("deleted", "true");
			List<ErrorType> ll = query.list();
			tx.commit();
			HibernateUtil.closeSession(idf);
			return ll;
		} catch (HibernateException ex) {
			log.error("[getErrorTypes]: " + ex);
		} catch (Exception ex2) {
			log.error("[getErrorTypes]: " + ex2);
		}
		return null;
	}
	
	public Long addErrorValues(Long errorvalues_id, Long errortype_id, Long fieldvalues_id) {
		try {
			ErrorValues eValue = new ErrorValues();
			eValue.setErrorvalues_id(errorvalues_id);
			eValue.setErrortype_id(errortype_id);
			eValue.setDeleted("false");
			eValue.setStarttime(new Date());
			eValue.setFieldvalues_id(fieldvalues_id);
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Long newerrorvalues_id = (Long) session.save(eValue);
			tx.commit();
			HibernateUtil.closeSession(idf);
			return newerrorvalues_id;
		} catch (HibernateException ex) {
			log.error("[addErrorType]: " + ex);
		} catch (Exception ex2) {
			log.error("[addErrorType]: " + ex2);
		}
		return null;
	}	
	
	public Long getErrorValueById(Long errortype_id, Long fieldvalues_id) {
		try {
			ErrorValues eValue = new ErrorValues();
			eValue.setErrortype_id(errortype_id);
			eValue.setStarttime(new Date());
			eValue.setFieldvalues_id(fieldvalues_id);
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Long newerrorvalues_id = (Long) session.save(eValue);
			tx.commit();
			HibernateUtil.closeSession(idf);
			return newerrorvalues_id;
		} catch (HibernateException ex) {
			log.error("[addErrorType]: " + ex);
		} catch (Exception ex2) {
			log.error("[addErrorType]: " + ex2);
		}
		return null;
	}	
	
	public Long updateErrorValues(Long errortype_id, Long fieldvalues_id) {
		try {
			ErrorValues eValue = new ErrorValues();
			eValue.setErrortype_id(errortype_id);
			eValue.setStarttime(new Date());
			eValue.setFieldvalues_id(fieldvalues_id);
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Long newerrorvalues_id = (Long) session.save(eValue);
			tx.commit();
			HibernateUtil.closeSession(idf);
			return newerrorvalues_id;
		} catch (HibernateException ex) {
			log.error("[addErrorType]: " + ex);
		} catch (Exception ex2) {
			log.error("[addErrorType]: " + ex2);
		}
		return null;
	}	
	
	public ErrorValues getErrorValuesById(Long errorvalues_id) {
		try {
			String hql = "select c from ErrorValues as c " +
					" where c.errorvalues_id = :errorvalues_id " +
					" AND c.deleted != :deleted";
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Query query = session.createQuery(hql);
			query.setLong("errorvalues_id", errorvalues_id);
			query.setString("deleted", "true");
			ErrorValues e = (ErrorValues) query.uniqueResult();
			tx.commit();
			HibernateUtil.closeSession(idf);
			return e;
		} catch (HibernateException ex) {
			log.error("[getErrorValuesById]",ex);
		} catch (Exception ex2) {
			log.error("[getErrorValuesById]",ex2);
		}
		return null;
	}		
}
