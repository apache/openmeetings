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
package org.apache.openmeetings.backup;

import java.io.InputStream;
import java.util.Iterator;

import org.apache.openmeetings.db.dao.label.FieldLanguageDao;
import org.apache.openmeetings.db.dao.label.FieldLanguagesValuesDao;
import org.apache.openmeetings.db.dao.label.FieldValueDao;
import org.apache.openmeetings.db.entity.label.Fieldlanguagesvalues;
import org.apache.openmeetings.db.entity.label.Fieldvalues;
import org.apache.openmeetings.util.OpenmeetingsVariables;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class LanguageImport {
	private static final Logger log = Red5LoggerFactory.getLogger(
			LanguageImport.class, OpenmeetingsVariables.webAppRootKey);

	@Autowired
	private FieldValueDao fieldValueDao;
	@Autowired
	private FieldLanguagesValuesDao fieldLangValueDao;
	@Autowired
	private FieldLanguageDao fieldLanguageDaoImpl;

	public Long addLanguageByDocument(Long language_id, InputStream is, long userId)
			throws Exception {

		// return null if no language availible
		if (fieldLanguageDaoImpl.getFieldLanguageById(language_id) == null) {
			return null;
		}

		SAXReader reader = new SAXReader();
		Document document = reader.read(is);

		Element root = document.getRootElement();

		for (@SuppressWarnings("unchecked")
		Iterator<Element> i = root.elementIterator(); i.hasNext();) {
			Element itemObject = i.next();
			Long fieldvalues_id = Long.valueOf(
					itemObject.attribute("id").getText()).longValue();
			String fieldName = itemObject.attribute("name").getText();
			String value = itemObject.element("value").getText();
			log.info("CHECK " + language_id + "," + fieldvalues_id + ","
					+ fieldName + "," + value);
			addFieldValueById(language_id, fieldvalues_id, fieldName, value, userId);
		}

		return null;
	}

	private void addFieldValueById(Long language_id, Long fieldvalues_id,
			String fieldName, String value, long userId) throws Exception {

		Fieldvalues fv = fieldValueDao.get(fieldvalues_id);

		if (fv == null) {
			fv = new Fieldvalues();
			fv.setFieldvalues_id(fieldvalues_id);
			fv.setName(fieldName);
			fv = fieldValueDao.update(fv, userId);
		}

		Fieldlanguagesvalues flv = fieldLangValueDao.get(fieldvalues_id, language_id);
		if (flv == null) {
			flv = new Fieldlanguagesvalues();
			flv.setFieldvalues(fv);
			flv.setLanguage_id(language_id);
			flv.setValue(value);
		} else {
			flv.setValue(value);
		}
		fieldLangValueDao.update(flv, userId);
	}

}
