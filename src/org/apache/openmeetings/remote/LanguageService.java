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
package org.apache.openmeetings.remote;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.data.basic.AuthLevelUtil;
import org.apache.openmeetings.data.basic.FieldLanguageDao;
import org.apache.openmeetings.data.basic.FieldManager;
import org.apache.openmeetings.data.basic.SessiondataDao;
import org.apache.openmeetings.data.basic.dao.ConfigurationDao;
import org.apache.openmeetings.data.beans.basic.SearchResult;
import org.apache.openmeetings.data.user.UserManager;
import org.apache.openmeetings.persistence.beans.lang.FieldLanguage;
import org.apache.openmeetings.persistence.beans.lang.Fieldlanguagesvalues;
import org.apache.openmeetings.persistence.beans.lang.Fieldvalues;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * @author sebastianwagner
 * 
 */
public class LanguageService {

	private static final Logger log = Red5LoggerFactory.getLogger(
			LanguageService.class, OpenmeetingsVariables.webAppRootKey);
	@Autowired
	private SessiondataDao sessiondataDao;
	@Autowired
	private ConfigurationDao configurationDao;
	@Autowired
	private UserManager userManager;
	@Autowired
	private FieldManager fieldManager;
	@Autowired
	private FieldLanguageDao fieldLanguageDaoImpl;
	@Autowired
	private AuthLevelUtil authLevelUtil;

	/**
	 * get a List of all availaible Languages
	 * 
	 * @return - List of all availaible Languages
	 */
	public List<FieldLanguage> getLanguages() {
		return fieldLanguageDaoImpl.getLanguages();
	}

	/**
	 * get all fields of a given Language_id
	 * 
	 * @param language_id
	 * @deprecated
	 * @return - all fields of a given Language_id
	 */
	@Deprecated
	public List<Fieldlanguagesvalues> getLanguageById(Long language_id) {
		return fieldManager.getAllFieldsByLanguage(language_id);
	}

	public Integer getDefaultLanguage() {
		return configurationDao.getConfValue("default_lang_id", Integer.class, "1");
	}

	/**
	 * get all fields of a given Language_id by params
	 * 
	 * @param language_id
	 * @return - all fields of a given Language_id in the range given
	 */
	public List<Map<String, Object>> getLanguageByIdAndMax(Long language_id,
			int start, int max) {
		return fieldManager.getLabelsByLanguage(language_id, start, max);
	}

	public Fieldvalues getFieldvalueById(String SID, Long fieldvalues_id,
			Long language_id) {
		Long users_id = sessiondataDao.checkSession(SID);
		Long user_level = userManager.getUserLevelByID(users_id);
		if (authLevelUtil.checkAdminLevel(user_level)) {
			return fieldManager.getFieldvaluesById(fieldvalues_id,
					language_id);
		}
		return null;
	}

	public Long addLanguage(String SID, String langName, String code) {
		Long users_id = sessiondataDao.checkSession(SID);
		Long user_level = userManager.getUserLevelByID(users_id);
		if (authLevelUtil.checkAdminLevel(user_level)) {
			if (langName.length() == 0)
				return new Long(-30);
			FieldLanguage lang = fieldLanguageDaoImpl.addLanguage(0, langName, false, code);
			return lang != null ? lang.getLanguage_id() : -1;
		}
		return null;
	}

	public Long updateLanguage(String SID, Long language_id, String langName, String code) {
		Long users_id = sessiondataDao.checkSession(SID);
		Long user_level = userManager.getUserLevelByID(users_id);
		if (authLevelUtil.checkAdminLevel(user_level)) {
			if (langName.length() == 0)
				return new Long(-30);
			return fieldLanguageDaoImpl.updateFieldLanguage(language_id,
					langName, code, false);
		}
		return null;
	}

	public Long deleteLanguage(String SID, Long language_id) {
		Long users_id = sessiondataDao.checkSession(SID);
		Long user_level = userManager.getUserLevelByID(users_id);
		if (authLevelUtil.checkAdminLevel(user_level)) {
			return fieldLanguageDaoImpl.updateFieldLanguage(language_id, "",
					"", true);
		}
		return null;
	}

	public Long deleteFieldlanguagesvaluesById(String SID,
			Long fieldlanguagesvalues_id) {
		Long users_id = sessiondataDao.checkSession(SID);
		Long user_level = userManager.getUserLevelByID(users_id);
		if (authLevelUtil.checkAdminLevel(user_level)) {
			return fieldManager
					.deleteFieldlanguagesvaluesById(fieldlanguagesvalues_id);
		}
		return null;
	}

	/**
	 * 
	 * @param SID
	 * @param start
	 * @param max
	 * @param orderby
	 * @param asc
	 * @param language_id
	 * @return - the list of field values being searched in case of success, null otherwise
	 */
	public SearchResult<Fieldvalues> getFieldsByLanguage(String SID, int start, int max,
			String orderby, boolean asc, Long language_id, String search) {
		Long users_id = sessiondataDao.checkSession(SID);
		Long user_level = userManager.getUserLevelByID(users_id);
		if (authLevelUtil.checkAdminLevel(user_level)) {
			return fieldManager.getFieldsByLanguage(start, max, orderby, asc,
					language_id, search);
		}
		return null;
	}

	/**
	 * 
	 * @param SID
	 * @param values
	 * @return - id of the label added or updated in case of success, error code otherwise
	 */
	public Long saveOrUpdateLabel(String SID,
			LinkedHashMap<Object, Object> values) {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);
			Long fieldvalues_id = Long.valueOf(
					values.get("fieldvalues_id").toString()).longValue();
			String name = values.get("name").toString();
			Long fieldlanguagesvalues_id = Long.valueOf(
					values.get("fieldlanguagesvalues_id").toString())
					.longValue();
			Long language_id = Long.valueOf(
					values.get("language_id").toString()).longValue();
			String value = values.get("value").toString();
			if (authLevelUtil.checkAdminLevel(user_level)) {
				if (fieldvalues_id > 0 && fieldlanguagesvalues_id > 0) {
					log.error("UPDATE LABEL");
					return fieldManager.updateLabel(fieldvalues_id, name,
							fieldlanguagesvalues_id, value);
				} else if (fieldvalues_id > 0 && fieldlanguagesvalues_id == 0) {
					log.error("INSERT NEW LABEL");
					return fieldManager.addAndUpdateLabel(fieldvalues_id,
							name, value, language_id);
				} else {
					log.error("INSERT NEW FIELD AND LABEL");
					return fieldManager.addFieldAndLabel(name, value,
							language_id);
				}
			}
			return new Long(-26);
		} catch (Exception e) {
			log.error("[saveOrUpdateLabel]", e);
		}
		return new Long(-1);
	}

}
