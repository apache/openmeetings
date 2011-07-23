package org.openmeetings.app.data.basic;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.red5.logging.Red5LoggerFactory;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import org.openmeetings.app.persistence.beans.basic.ErrorType;
import org.openmeetings.app.persistence.beans.basic.ErrorValues;
import org.openmeetings.app.persistence.utils.HibernateUtil;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;

public class ErrorManagement {
	private static final Logger log = Red5LoggerFactory.getLogger(ErrorManagement.class, ScopeApplicationAdapter.webAppRootKey);

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
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			eType = session.merge(eType);
			Long newerrortype_id = eType.getErrortype_id(); 
			tx.commit();
			HibernateUtil.closeSession(idf);
			return newerrortype_id;
		} catch (Exception ex2) {
			log.error("[addErrorType]: " + ex2);
		}
		return null;
	}
	
	public List<ErrorType> getErrorTypes() {
		try {
			String hql = "select c from ErrorType as c " +
					"WHERE c.deleted <> :deleted ";			
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			Query query = session.createQuery(hql);
			query.setParameter("deleted", "true");
			List<ErrorType> ll = query.getResultList();
			tx.commit();
			HibernateUtil.closeSession(idf);
			return ll;
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
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			eValue = session.merge(eValue);
			Long newerrorvalues_id = eValue.getErrorvalues_id(); 
			tx.commit();
			HibernateUtil.closeSession(idf);
			return newerrorvalues_id;
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
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			eValue = session.merge(eValue);
			Long newerrorvalues_id = eValue.getErrorvalues_id(); 
			tx.commit();
			HibernateUtil.closeSession(idf);
			return newerrorvalues_id;
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
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			eValue = session.merge(eValue);
			Long newerrorvalues_id = eValue.getErrorvalues_id(); 
			tx.commit();
			HibernateUtil.closeSession(idf);
			return newerrorvalues_id;
		} catch (Exception ex2) {
			log.error("[addErrorType]: " + ex2);
		}
		return null;
	}	
	
	public ErrorValues getErrorValuesById(Long errorvalues_id) {
		try {
			String hql = "select c from ErrorValues as c " +
					" where c.errorvalues_id = :errorvalues_id " +
					" AND c.deleted <> :deleted";
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			session.flush();
			Query query = session.createQuery(hql);
			query.setParameter("errorvalues_id", errorvalues_id);
			query.setParameter("deleted", "true");
			ErrorValues e = null;
			try{
				e = (ErrorValues) query.getSingleResult();
	        } catch (NoResultException ex) {
	        }
			tx.commit();
			HibernateUtil.closeSession(idf);
			return e;
		} catch (Exception ex2) {
			log.error("[getErrorValuesById]",ex2);
		}
		return null;
	}		
}
