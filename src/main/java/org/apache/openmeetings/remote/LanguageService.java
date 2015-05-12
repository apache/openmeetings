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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dao.label.LabelDao;
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
	private LabelDao labelDao;

	/**
	 * @return - List of all available Languages
	 */
	public List<Language> getLanguages() {
		List<Language> result = new ArrayList<Language>();
		for (Map.Entry<Long, Locale> e : LabelDao.languages.entrySet()) {
			result.add(new Language(e.getKey(), e.getValue().toLanguageTag(), e.getValue().getDisplayName(Locale.ENGLISH)));
		}
		return result;
	}

	public Integer getDefaultLanguage() {
		return configurationDao.getConfValue(CONFIG_DEFAUT_LANG_KEY, Integer.class, "1");
	}

	/**
	 * get all fields of a given Language_id by params
	 * 
	 * @param language_id
	 * @param start
	 * @param count
	 * @return - all fields of a given Language_id in the range given
	 */
	public List<Map<String, Object>> getLanguageByIdAndMax(int language_id, int start, int count) {
		return labelDao.getStrings((long)language_id, start, count);
	}

	public static class Language implements Serializable {
		private static final long serialVersionUID = 1L;
		public long language_id;
		public String code;
		public String name;
		
		public Language() {}
		
		public Language(long language_id, String code, String name) {
			this.language_id = language_id;
			this.code = code;
			this.name = name;
		}
	}
}
