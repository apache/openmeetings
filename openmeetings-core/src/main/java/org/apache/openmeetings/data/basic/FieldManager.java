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
package org.apache.openmeetings.data.basic;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dao.label.FieldLanguageDao;
import org.apache.openmeetings.db.dao.label.FieldLanguagesValuesDao;
import org.apache.openmeetings.db.entity.label.FieldLanguage;
import org.apache.openmeetings.db.entity.label.Fieldlanguagesvalues;
import org.apache.openmeetings.db.entity.label.Fieldvalues;
import org.apache.openmeetings.util.OpenmeetingsVariables;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author sebastianwagner
 * 
 */
@Transactional
public class FieldManager {

	private static final Logger log = Red5LoggerFactory.getLogger(
			FieldManager.class, OpenmeetingsVariables.webAppRootKey);

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private FieldLanguageDao fieldLanguageDaoImpl;

	@Autowired
	private FieldLanguagesValuesDao fieldLanguagesValuesDAO;

	@Autowired
	private ConfigurationDao configurationDaoImpl;
	
	public String getString(Long fieldvalues_id, Long language_id) {
		String result = null;
		Fieldlanguagesvalues flv = getFieldByIdAndLanguage(fieldvalues_id, language_id);
		if (flv != null) {
			result = performReplace(flv).getValue();
		}
		return result;
	}
	
	public Fieldlanguagesvalues getFieldByIdAndLanguage(Long fieldvalues_id,
			Long language_id) {
		try {

			String hql = "select f from Fieldlanguagesvalues as f "
					+ "WHERE f.language_id = :language_id "
					+ "AND f.fieldvalues_id = :fieldvalues_id";

			Fieldlanguagesvalues flv = null;

			TypedQuery<Fieldlanguagesvalues> query = em.createQuery(hql, Fieldlanguagesvalues.class);

			query.setParameter("fieldvalues_id", fieldvalues_id);
			query.setParameter("language_id", language_id);
			List<Fieldlanguagesvalues> fList = query.getResultList();

			if (fList.size() > 0) {
				flv = fList.get(0); //replace should not be performed here to enable string editing via admin
			}

			return flv;
		} catch (Exception ex2) {
			log.error("[getFieldByIdAndLanguage]: ", ex2);
		}
		return null;
	}

	public List<Fieldlanguagesvalues> getAllFieldsByLanguage(Long language_id) {
		try {

			TypedQuery<Fieldlanguagesvalues> query = em
					.createQuery("select f from Fieldlanguagesvalues f WHERE f.language_id = :language_id ", Fieldlanguagesvalues.class);
			query.setParameter("language_id", language_id);
			List<Fieldlanguagesvalues> returnList = performReplace(query.getResultList());

			return returnList;
		} catch (Exception ex2) {
			log.error("[getConfKey]: " + ex2);
		}
		return null;
	}

	public List<Map<String, Object>> getLabelsByLanguage(Long language_id,
			int start, int max) {
		try {

			String sql = "select f.fieldvalues.fieldvalues_id, f.value from Fieldlanguagesvalues as f "
					+ "WHERE f.language_id = :language_id ";

			TypedQuery<Object> query = em.createQuery(sql, Object.class);
			query.setParameter("language_id", language_id);
			query.setFirstResult(start);
			query.setMaxResults(max);

			String appName = configurationDaoImpl.getAppName();
			List<Map<String, Object>> returnList = new LinkedList<Map<String, Object>>();
			for (Object fl : query.getResultList()) {
				Map<String, Object> map = new HashMap<String, Object>();
				Object[] row = (Object[])fl;
				map.put("id", row[0]);
				map.put("value", performReplace((String)row[1], appName));
				returnList.add(map);
			}
			FieldLanguage fieldLanguage = fieldLanguageDaoImpl
					.getFieldLanguageById(language_id);

			log.debug("Getting Labels for Language " + fieldLanguage.getName());

			// Check for Right To Left Languages
			if (fieldLanguage.getRtl()) {
				log.debug("Language requieres RTL!");

				List<Map<String, Object>> returnRtlList = new LinkedList<Map<String, Object>>();

				for (Iterator<Map<String, Object>> iter = returnList.iterator(); iter
						.hasNext();) {
					Map<String, Object> remote = iter.next();

					Map<String, Object> toAdd = new HashMap<String, Object>();
					toAdd.put("id", remote.get("id"));

					String value = remote.get("value").toString();

					String[] splitted = value.split(" ");
					String reverseOrder = "";
					for (int i = splitted.length - 1; i >= 0; i--) {
						reverseOrder += splitted[i];
						if (splitted.length != 1) {
							reverseOrder += " ";
						}
					}
					log.debug("reverseOrder " + reverseOrder);
					toAdd.put("value", value);

					returnRtlList.add(toAdd);
				}

				return returnRtlList;
			} else {
				log.debug("Language doesnt requiere RTL!");

				return returnList;

			}

		} catch (Exception ex2) {
			log.error("[getLabelsByLanguage]: ", ex2);
			ex2.printStackTrace();
		}
		return null;
	}

	public List<Fieldlanguagesvalues> getAllFieldsByLanguage(Long language_id,
			int start, int max) {
		try {

			String sql = "select f from Fieldlanguagesvalues f WHERE f.language_id = :language_id "
					+ "AND f.fieldvalues_id >= :start AND f.fieldvalues_id <  :max";

			TypedQuery<Fieldlanguagesvalues> query = em.createQuery(sql, Fieldlanguagesvalues.class);
			query.setParameter("language_id", language_id);
			query.setParameter("start", start);
			query.setParameter("max", start + max);

			List<Fieldlanguagesvalues> returnList = performReplace(query.getResultList());
			FieldLanguage fieldLanguage = fieldLanguageDaoImpl
					.getFieldLanguageById(language_id);

			log.debug("GEtting all fields by language : "
					+ fieldLanguage.getName());

			// Check for Right To Left Languages
			if (fieldLanguage.getRtl()) {
				log.debug("language : " + fieldLanguage.getName()
						+ " requieres RTL");

				List<Fieldlanguagesvalues> returnRtlList = new LinkedList<Fieldlanguagesvalues>();

				for (Iterator<Fieldlanguagesvalues> iter = returnList
						.iterator(); iter.hasNext();) {
					Fieldlanguagesvalues remote = iter.next();
					Fieldlanguagesvalues toAdd = new Fieldlanguagesvalues();
					toAdd.setFieldlanguagesvalues_id(remote
							.getFieldlanguagesvalues_id());
					toAdd.setFieldvalues_id(remote.getFieldvalues_id());
					toAdd.setLanguage_id(remote.getLanguage_id());

					String[] splitted = remote.getValue().split(" ");
					String reverseOrder = "";
					for (int i = splitted.length - 1; i >= 0; i--) {
						reverseOrder += splitted[i];
						if (splitted.length != 1) {
							reverseOrder += " ";
						}
					}
					toAdd.setValue(reverseOrder);

					returnRtlList.add(toAdd);
				}

				return returnRtlList;
			} else {
				log.debug("language : " + fieldLanguage.getName()
						+ " requieres NO RTL");

				return returnList;

			}

		} catch (Exception ex2) {
			log.error("[getConfKey]: ", ex2);
		}
		return null;
	}

	public Long addFieldValueByFieldAndLanguage(Fieldvalues fv,
			Long language_id, String fieldvalue) {
		try {

			Fieldlanguagesvalues flv = new Fieldlanguagesvalues();
			flv.setStarttime(new Date());
			flv.setValue(fieldvalue);
			flv.setLanguage_id(language_id);
			flv.setFieldvalues(fv);
			flv.setDeleted(false);

			flv = em.merge(flv);
			Long fieldlanguagesvaluesId = flv.getFieldlanguagesvalues_id();

			return fieldlanguagesvaluesId;
		} catch (Exception ex2) {
			log.error("[getConfKey]: ", ex2);
		}
		return null;
	}

	public void updateFieldValueByFieldAndLanguage(Fieldlanguagesvalues flv) {
		try {

			if (flv.getFieldlanguagesvalues_id() == null) {
				em.persist(flv);
			} else {
				if (!em.contains(flv)) {
					em.merge(flv);
				}
			}

		} catch (Exception ex2) {
			log.error("[updateFieldValueByFieldAndLanguage]: ", ex2);
		}

	}

	public Fieldvalues addFieldById(String fieldName, Long fieldvalues_id) {
		try {
			Fieldvalues fl = new Fieldvalues();
			fl.setFieldvalues_id(fieldvalues_id);
			fl.setStarttime(new Date());
			fl.setName(fieldName);
			fl.setDeleted(false);

			return em.merge(fl);
		} catch (Exception ex2) {
			log.error("[getConfKey]: ", ex2);
		}
		return null;
	}

	public long getNextFieldvaluesId() {
		return fieldLanguagesValuesDAO.count() + 1;
	}
	
	public Fieldvalues getFieldvaluesById(Long fieldvalues_id) {
		String hql = "select f from Fieldvalues f WHERE f.fieldvalues_id = :fieldvalues_id ";
		TypedQuery<Fieldvalues> query = em.createQuery(hql, Fieldvalues.class);
		query.setParameter("fieldvalues_id", fieldvalues_id);
		Fieldvalues fv = null;
		try {
			fv = query.getSingleResult();
		} catch (NoResultException ex) {
		}
		return fv;
	}

	private Fieldlanguagesvalues performReplace(Fieldlanguagesvalues f) {
		String appName = configurationDaoImpl.getAppName();
		return performReplace(f, appName);
	}
	
	private String performReplace(String val, String appName) {
		return val == null ? val : val.replaceAll("\\$APP_NAME", appName);
	}
	
	private Fieldlanguagesvalues performReplace(Fieldlanguagesvalues f, String appName) {
		Fieldlanguagesvalues r = new Fieldlanguagesvalues();
		r.setDeleted(f.getDeleted());
		r.setFieldlanguagesvalues_id(f.getFieldlanguagesvalues_id());
		r.setFieldvalues_id(f.getFieldvalues_id());
		r.setLanguage_id(f.getLanguage_id());
		r.setStarttime(f.getStarttime());
		r.setUpdatetime(f.getUpdatetime());
		r.setValue(performReplace(f.getValue(), appName));
		return r;
	}
	
	private <T extends Collection<Fieldlanguagesvalues>> T performReplace(T flv) {
		String appName = configurationDaoImpl.getAppName();
		
		for (Fieldlanguagesvalues f : flv) {
			performReplace(f, appName);
		}
		return flv;
	}
	
}
