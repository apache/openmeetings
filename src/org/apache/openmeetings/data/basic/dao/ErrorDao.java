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
package org.apache.openmeetings.data.basic.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.persistence.beans.basic.ErrorType;
import org.apache.openmeetings.persistence.beans.basic.ErrorValue;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class ErrorDao {
	private static final Logger log = Red5LoggerFactory.getLogger(
			ErrorDao.class, OpenmeetingsVariables.webAppRootKey);

	@PersistenceContext
	private EntityManager em;

	public Long addErrorType(Long errortype_id, Long fieldvalues_id) {
		try {
			ErrorType eType = new ErrorType();
			eType.setErrortype_id(errortype_id);
			eType.setStarttime(new Date());
			eType.setDeleted(false);
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
			TypedQuery<ErrorType> query = em.createNamedQuery("getErrorTypes", ErrorType.class);
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
			TypedQuery<ErrorType> query = em.createNamedQuery("getErrorType", ErrorType.class);
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
			eValue.setErrorvalues_id(errorvalues_id);
			eValue.setErrortype_id(errortype_id);
			eValue.setDeleted(false);
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
			ErrorValue eValue = new ErrorValue();
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
			ErrorValue eValue = new ErrorValue();
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

	public ErrorValue getErrorValuesById(Long errorvalues_id) {
		try {
			TypedQuery<ErrorValue> query = em.createNamedQuery("getErrorValuesById", ErrorValue.class);
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
