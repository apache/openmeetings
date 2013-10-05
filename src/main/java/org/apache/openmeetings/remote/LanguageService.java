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

import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DEFAUT_LANG_KEY;

import java.util.List;
import java.util.Map;

import org.apache.openmeetings.data.basic.FieldManager;
import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dao.label.FieldLanguageDao;
import org.apache.openmeetings.db.entity.label.FieldLanguage;
import org.apache.openmeetings.db.entity.label.Fieldlanguagesvalues;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * @author sebastianwagner
 * 
 */
public class LanguageService {
	@Autowired
	private ConfigurationDao configurationDao;
	@Autowired
	private FieldManager fieldManager;
	@Autowired
	private FieldLanguageDao fieldLanguageDaoImpl;

	/**
	 * @return - List of all available Languages
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
		return configurationDao.getConfValue(CONFIG_DEFAUT_LANG_KEY, Integer.class, "1");
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
}
