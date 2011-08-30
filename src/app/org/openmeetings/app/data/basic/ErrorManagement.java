package org.openmeetings.app.data.basic;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.openmeetings.app.persistence.beans.basic.ErrorType;
import org.openmeetings.app.persistence.beans.basic.ErrorValues;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class ErrorManagement {
	private static final Logger log = Red5LoggerFactory.getLogger(
			ErrorManagement.class, ScopeApplicationAdapter.webAppRootKey);

	@PersistenceContext
	private EntityManager em;

	public Long addErrorType(Long errortype_id, Long fieldvalues_id) {
		try {
			ErrorType eType = new ErrorType();
			eType.setErrortype_id(errortype_id);
			eType.setStarttime(new Date());
			eType.setDeleted("false");
			eType.setFieldvalues_id(fieldvalues_id);
			eType = em.merge(eType);
			Long newerrortype_id = eType.getErrortype_id();
			return newerrortype_id;
		} catch (Exception ex2) {
			log.error("[addErrorType]: ", ex2);
		}
		return null;
	}

	public List<ErrorType> getErrorTypes() {
		try {
			String hql = "select c from ErrorType as c "
					+ "WHERE c.deleted <> :deleted ";
			Query query = em.createQuery(hql);
			query.setParameter("deleted", "true");
			@SuppressWarnings("unchecked")
			List<ErrorType> ll = query.getResultList();
			return ll;
		} catch (Exception ex2) {
			log.error("[getErrorTypes]: ", ex2);
		}
		return null;
	}

	public ErrorType getErrorType(Long errortype_id) {
		try {
			String hql = "select c from ErrorType as c "
					+ "WHERE c.deleted <> :deleted AND c.errortype_id = :errortype_id";
			Query query = em.createQuery(hql);
			query.setParameter("deleted", "true");
			query.setParameter("errortype_id", errortype_id);
			return (ErrorType) query.getSingleResult();
		} catch (Exception ex2) {
			log.error("[getErrorType(" + errortype_id + ")]", ex2);
		}
		return null;
	}

	public Long addErrorValues(Long errorvalues_id, Long errortype_id,
			Long fieldvalues_id) {
		try {
			ErrorValues eValue = new ErrorValues();
			eValue.setErrorvalues_id(errorvalues_id);
			eValue.setErrortype_id(errortype_id);
			eValue.setDeleted("false");
			eValue.setStarttime(new Date());
			eValue.setFieldvalues_id(fieldvalues_id);
			eValue = em.merge(eValue);
			return eValue.getErrorvalues_id();
		} catch (Exception ex2) {
			log.error("[addErrorValues]: ", ex2);
		}
		return null;
	}

	public Long getErrorValueById(Long errortype_id, Long fieldvalues_id) {
		try {
			ErrorValues eValue = new ErrorValues();
			eValue.setErrortype_id(errortype_id);
			eValue.setStarttime(new Date());
			eValue.setFieldvalues_id(fieldvalues_id);
			eValue = em.merge(eValue);
			Long newerrorvalues_id = eValue.getErrorvalues_id();
			return newerrorvalues_id;
		} catch (Exception ex2) {
			log.error("[getErrorValueById]: ", ex2);
		}
		return null;
	}

	public Long updateErrorValues(Long errortype_id, Long fieldvalues_id) {
		try {
			ErrorValues eValue = new ErrorValues();
			eValue.setErrortype_id(errortype_id);
			eValue.setStarttime(new Date());
			eValue.setFieldvalues_id(fieldvalues_id);
			eValue = em.merge(eValue);
			Long newerrorvalues_id = eValue.getErrorvalues_id();
			return newerrorvalues_id;
		} catch (Exception ex2) {
			log.error("[addErrorType]: ", ex2);
		}
		return null;
	}

	public ErrorValues getErrorValuesById(Long errorvalues_id) {
		try {
			String hql = "select c from ErrorValues as c "
					+ " where c.errorvalues_id = :errorvalues_id "
					+ " AND c.deleted <> :deleted";
			Query query = em.createQuery(hql);
			query.setParameter("errorvalues_id", errorvalues_id);
			query.setParameter("deleted", "true");
			ErrorValues e = null;
			try {
				e = (ErrorValues) query.getSingleResult();
			} catch (NoResultException ex) {
			}
			return e;
		} catch (Exception ex2) {
			log.error("[getErrorValuesById]", ex2);
		}
		return null;
	}
}
