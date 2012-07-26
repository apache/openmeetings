/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License") +  you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.openmeetings.app.data.basic;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.openmeetings.app.OpenmeetingsVariables;
import org.openmeetings.app.persistence.beans.basic.ErrorType;
import org.openmeetings.app.persistence.beans.basic.ErrorValue;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class ErrorManagement {
	private static final Logger log = Red5LoggerFactory.getLogger(
			ErrorManagement.class, OpenmeetingsVariables.webAppRootKey);

	@PersistenceContext
	private EntityManager em;

	public Long addErrorType(Long errortype_id, Long fieldvalues_id) {
		try {
			ErrorType eType = new ErrorType();
			eType.setId(errortype_id);
			eType.setStarttime(new Date());
			eType.setDeleted(false);
			eType.setFieldvalues_id(fieldvalues_id);
			eType = em.merge(eType);
			Long newerrortype_id = eType.getId();
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
			TypedQuery<ErrorType> query = em.createQuery(hql, ErrorType.class);
			query.setParameter("deleted", true);
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
					+ "WHERE c.deleted <> :deleted AND c.id = :errortype_id";
			TypedQuery<ErrorType> query = em.createQuery(hql, ErrorType.class);
			query.setParameter("deleted", true);
			query.setParameter("errortype_id", errortype_id);
			return query.getSingleResult();
		} catch (Exception ex2) {
			log.error("[getErrorType(" + errortype_id + ")]", ex2);
		}
		return null;
	}

	public Long addErrorValues(Long errorvalues_id, Long errortype_id,
			Long fieldvalues_id) {
		try {
			ErrorValue eValue = new ErrorValue();
			eValue.setId(errorvalues_id);
			eValue.setType(getErrorType(errortype_id));
			eValue.setDeleted(false);
			eValue.setStarttime(new Date());
			eValue.setFieldvalues_id(fieldvalues_id);
			eValue = em.merge(eValue);
			return eValue.getId();
		} catch (Exception ex2) {
			log.error("[addErrorValues]: ", ex2);
		}
		return null;
	}

	public Long getErrorValueById(Long errortype_id, Long fieldvalues_id) {
		try {
			ErrorValue eValue = new ErrorValue();
			eValue.setType(getErrorType(errortype_id));
			eValue.setStarttime(new Date());
			eValue.setFieldvalues_id(fieldvalues_id);
			eValue = em.merge(eValue);
			Long newerrorvalues_id = eValue.getId();
			return newerrorvalues_id;
		} catch (Exception ex2) {
			log.error("[getErrorValueById]: ", ex2);
		}
		return null;
	}

	public Long updateErrorValues(Long errortype_id, Long fieldvalues_id) {
		try {
			ErrorValue eValue = new ErrorValue();
			eValue.setType(getErrorType(errortype_id));
			eValue.setStarttime(new Date());
			eValue.setFieldvalues_id(fieldvalues_id);
			eValue = em.merge(eValue);
			Long newerrorvalues_id = eValue.getId();
			return newerrorvalues_id;
		} catch (Exception ex2) {
			log.error("[addErrorType]: ", ex2);
		}
		return null;
	}

	public ErrorValue getErrorValuesById(Long errorvalues_id) {
		try {
			String hql = "select c from ErrorValue as c "
					+ " where c.id = :errorvalues_id "
					+ " AND c.deleted <> :deleted";
			TypedQuery<ErrorValue> query = em.createQuery(hql, ErrorValue.class);
			query.setParameter("errorvalues_id", errorvalues_id);
			query.setParameter("deleted", true);
			ErrorValue e = null;
			try {
				e = query.getSingleResult();
			} catch (NoResultException ex) {
			}
			return e;
		} catch (Exception ex2) {
			log.error("[getErrorValuesById]", ex2);
		}
		return null;
	}
}
