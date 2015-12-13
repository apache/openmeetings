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
package org.apache.openmeetings.test.labels;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.openmeetings.persistence.beans.lang.Fieldlanguagesvalues;
import org.apache.openmeetings.persistence.beans.lang.Fieldvalues;
import org.apache.openmeetings.servlet.outputhandler.LangExport;
import org.apache.openmeetings.utils.OmFileHelper;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.junit.Test;

/**
 * Syncs all labels from a given master language file to all language files You
 * can also specify some labels that are overwritten from the master file to all
 * other files.<br/>
 * <br/>
 * It will read first the {@link #masterLangFile} and then all other XML files and fill
 * it. Practically I have put "english.xml" in the {@link #masterLangFile}, so you can
 * add new labels to the english.xml, run the test, refresh Eclipse and all
 * other lang files should have the new label(s) too.<br/>
 * <br/>
 * It is also possible to replace existing other labels, just see the array
 * variable: {@link #replaceIds}. All Ids, in that array will be synced from the
 * {@link #masterLangFile} to all other languages.<br/>
 * <br/>
 * The Junit test also does some tests, for example it fails if it detects
 * duplicate labelid's.<br/>
 * 
 * @author sebawagner
 * 
 */
public class FillLabels {

	private final String basePath = "WebContent/languages/";

	private final String masterLangFile = "english.xml";

	private final String[] excludeFiles = { "errorvalues.xml",
			"countries.xml", "timezones.xml", "languages.xml" };

	/**
	 * those labels will be overwritten from the master to all language files,
	 * other label-id's will be only filled up if missing at the end of the
	 * language file
	 */
	private final long[] replaceIds = {  }; // 1518L

	private Map<Long, Fieldlanguagesvalues> masterLabels;

	@Test
	public void test() throws Exception {
		OmFileHelper.setOmHome(System.getProperty("om.home", "."));
		// Read master file
		File base = new File(OmFileHelper.getOmHome(), basePath);
		masterLabels = parseToLabelsArray(new File(base, masterLangFile));

		File langFolder = new File(OmFileHelper.getOmHome(), basePath);

		for (File file : langFolder.listFiles(new FilenameFilter() {
					public boolean accept(File file, String string1) {
						if (!string1.endsWith("xml")) {
							return false;
						}
						for (String excludeFileName : excludeFiles) {
							if (string1.equals(excludeFileName)) {
								return false;
							}
						}
						return true;
					}
				})) {

			Map<Long, Fieldlanguagesvalues> labelsArray = parseToLabelsArray(file);

			for (Entry<Long, Fieldlanguagesvalues> entryMaster : masterLabels.entrySet()) {
				for (long replaceId : replaceIds) {
					if (replaceId == entryMaster.getKey()) {
						labelsArray.put(entryMaster.getKey(), entryMaster.getValue());
					}
				}
				if (!labelsArray.containsKey(entryMaster.getKey())) {
					labelsArray.put(entryMaster.getKey(), entryMaster.getValue());
				}
			}
			LangExport.serializetoXML(new FileOutputStream(file), "UTF-8", createDocument(labelsArray));
		}
	}

	/**
	 * parses a given language file to an array of Labels
	 * 
	 * @return
	 * @throws FileNotFoundException
	 * @throws DocumentException
	 */
	private Map<Long, Fieldlanguagesvalues> parseToLabelsArray(File file)
			throws FileNotFoundException, DocumentException {

		Map<Long, Fieldlanguagesvalues> labelsArray = new LinkedHashMap<Long, Fieldlanguagesvalues>();
		SAXReader reader = new SAXReader();
		Document document = reader.read(file);

		Element root = document.getRootElement();

		for (@SuppressWarnings("unchecked")
		Iterator<Element> i = root.elementIterator(); i.hasNext();) {
			Element itemObject = i.next();
			Long fieldvalues_id = Long.valueOf(
					itemObject.attribute("id").getText()).longValue();
			String fieldName = itemObject.attribute("name").getText();
			String value = itemObject.element("value").getText();
			Fieldlanguagesvalues fValue = new Fieldlanguagesvalues();
			fValue.setFieldvalues_id(fieldvalues_id);
			fValue.setValue(value);
			Fieldvalues fLabel = new Fieldvalues();
			fLabel.setName(fieldName);
			fValue.setFieldvalues(fLabel);
			
			labelsArray.put(fieldvalues_id, fValue);
		}

		return labelsArray;
	}

	/**
	 * Create the document
	 * 
	 * @param flvList
	 * @return
	 * @throws Exception
	 */
	private Document createDocument(Map<Long, Fieldlanguagesvalues> labelsArray)
			throws Exception {
		Document document = LangExport.createDocument();
		Element root = LangExport.createRoot(document);

		for (Entry<Long, Fieldlanguagesvalues> entryLabel : labelsArray.entrySet()) {
			Element eTemp = root.addElement("string")
					.addAttribute("id", entryLabel.getValue().getFieldvalues_id().toString())
					.addAttribute("name", entryLabel.getValue().getFieldvalues().getName());
			Element value = eTemp.addElement("value");
			value.addText(entryLabel.getValue().getValue());
		}

		return document;
	}
}
