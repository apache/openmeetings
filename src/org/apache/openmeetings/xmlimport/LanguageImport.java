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
package org.apache.openmeetings.xmlimport;

import java.io.InputStream;
import java.util.Iterator;

import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.data.basic.FieldLanguageDao;
import org.apache.openmeetings.data.basic.FieldManager;
import org.apache.openmeetings.persistence.beans.lang.Fieldlanguagesvalues;
import org.apache.openmeetings.persistence.beans.lang.Fieldvalues;
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
	private FieldManager fieldManager;
	@Autowired
	private FieldLanguageDao fieldLanguageDaoImpl;

	public Long addLanguageByDocument(Long language_id, InputStream is)
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
			log.error("CHECK " + language_id + "," + fieldvalues_id + ","
					+ fieldName + "," + value);
			this.addFieldValueById(language_id, fieldvalues_id, fieldName,
					value);
		}

		return null;
	}

	private void addFieldValueById(Long language_id, Long fieldvalues_id,
			String fieldName, String value) throws Exception {

		Fieldvalues fv = fieldManager.getFieldvaluesById(fieldvalues_id);

		if (fv == null) {
			fv = fieldManager.addFieldById(fieldName, fieldvalues_id);
		}

		Fieldlanguagesvalues flv = fieldManager.getFieldByIdAndLanguage(
				fieldvalues_id, language_id);

		if (flv == null) {
			fieldManager.addFieldValueByFieldAndLanguage(fv,
					language_id, value);
		} else {
			flv.setValue(value);
			flv.setUpdatetime(new java.util.Date());
			fieldManager.updateFieldValueByFieldAndLanguage(flv);
		}
	}

}
