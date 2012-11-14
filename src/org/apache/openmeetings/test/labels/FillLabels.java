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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.openmeetings.persistence.beans.lang.Fieldlanguagesvalues;
import org.apache.openmeetings.persistence.beans.lang.Fieldvalues;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.junit.Test;

/**
 * Syncs all labels from a given master language file to all language files You
 * can also specify some labels that are overwritten from the master file to all
 * other files
 * 
 * @author sebawagner
 * 
 */
public class FillLabels {

	private final String basePath = "./WebContent/languages/";

	private final String masterLangFile = "english.xml";

	private final String[] excludeFiles = { masterLangFile, "errorvalues.xml",
			"countries.xml", "timezones.xml", "languages.xml" };

	/**
	 * those labels will be overwritten from the master to all language files,
	 * other label-id's will be only filled up if missing at the end of the
	 * language file
	 */
	private final int[] replaceIds = {};

	private final List<Fieldlanguagesvalues> masterLabels = new ArrayList<Fieldlanguagesvalues>();

	@Test
	public void test() {

		try {

			// Read master file
			masterLabels.addAll(parseToLabelsArray(masterLangFile));

			File languagesFilesFolder = new File(basePath);

			for (File file : languagesFilesFolder
					.listFiles(new FilenameFilter() {
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

				List<Fieldlanguagesvalues> labelsArray = parseToLabelsArray(file
						.getName());

				for (int i = 0; i < masterLabels.size(); i++) {
					boolean isReplaced = false;
					if (i + 1 < labelsArray.size()) {
						for (int replaceId : replaceIds) {
							if (replaceId == labelsArray.get(i)
									.getFieldvalues_id().intValue()) {
								labelsArray.set(i, masterLabels.get(i));
								isReplaced = true;
							}
						}
					}
					if (!isReplaced) {
						if (i + 1 < labelsArray.size()) {
							if (!labelsArray
									.get(i)
									.getFieldvalues_id()
									.equals(masterLabels.get(i)
											.getFieldvalues_id())) {
								throw new Exception(
										"Master and fill language has another index in labelid: "
												+ masterLabels.get(i)
														.getFieldvalues_id()
												+ " Check file: "
												+ file.getName() + " "
												+ labelsArray.get(i).getValue());
							}
						} else {
							labelsArray.add(masterLabels.get(i));
						}
					}
				}

				OutputStream out = new FileOutputStream(basePath
						+ file.getName());

				this.serializetoXML(out, "UTF-8", createDocument(labelsArray));

			}

		} catch (Exception er) {
			er.printStackTrace();
		}

	}

	/**
	 * parses a given language file to an array of Labels
	 * 
	 * @return
	 * @throws FileNotFoundException
	 * @throws DocumentException
	 */
	private List<Fieldlanguagesvalues> parseToLabelsArray(String fileName)
			throws FileNotFoundException, DocumentException {

		List<Fieldlanguagesvalues> labelsArray = new ArrayList<Fieldlanguagesvalues>();

		InputStream is = new FileInputStream(basePath + fileName);
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
			Fieldlanguagesvalues fValue = new Fieldlanguagesvalues();
			fValue.setFieldvalues_id(fieldvalues_id);
			fValue.setValue(value);
			Fieldvalues fLabel = new Fieldvalues();
			fLabel.setName(fieldName);
			fValue.setFieldvalues(fLabel);
			labelsArray.add(fValue);
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
	private Document createDocument(List<Fieldlanguagesvalues> flvList)
			throws Exception {

		Document document = DocumentHelper.createDocument();
		document.setXMLEncoding("UTF-8");
		document.addComment(""
				+ "\n"
				+ "  Licensed to the Apache Software Foundation (ASF) under one\n"
				+ "  or more contributor license agreements.  See the NOTICE file\n"
				+ "  distributed with this work for additional information\n"
				+ "  regarding copyright ownership.  The ASF licenses this file\n"
				+ "  to you under the Apache License, Version 2.0 (the\n"
				+ "  \"License\"); you may not use this file except in compliance\n"
				+ "  with the License.  You may obtain a copy of the License at\n"
				+ "  \n"
				+ "      http://www.apache.org/licenses/LICENSE-2.0\n"
				+ "    	  \n"
				+ "  Unless required by applicable law or agreed to in writing,\n"
				+ "  software distributed under the License is distributed on an\n"
				+ "  \"AS IS\" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY\n"
				+ "  KIND, either express or implied.  See the License for the\n"
				+ "  specific language governing permissions and limitations\n"
				+ "  under the License.\n"
				+ "  \n"
				+ "\n"
				+ "\n"
				+ "###############################################\n"
				+ "This File is auto-generated by the LanguageEditor \n"
				+ "to add new Languages or modify/customize it use the LanguageEditor \n"
				+ "see http://incubator.apache.org/openmeetings/LanguageEditor.html for Details \n"
				+ "###############################################");

		Element root = document.addElement("language");
		root.add(new Namespace("xsi",
				"http://www.w3.org/2001/XMLSchema-instance"));
		root.add(new Namespace("noNamespaceSchemaLocation", "language.xsd"));

		for (Fieldlanguagesvalues flv : flvList) {
			Element eTemp = root.addElement("string")
					.addAttribute("id", flv.getFieldvalues_id().toString())
					.addAttribute("name", flv.getFieldvalues().getName());
			Element value = eTemp.addElement("value");
			value.addText(flv.getValue());
		}

		return document;
	}

	/**
	 * write XML
	 * 
	 * @param out
	 * @param aEncodingScheme
	 * @param doc
	 * @throws Exception
	 */
	private void serializetoXML(OutputStream out, String aEncodingScheme,
			Document doc) throws Exception {
		OutputFormat outformat = OutputFormat.createPrettyPrint();
		outformat.setEncoding(aEncodingScheme);
		XMLWriter writer = new XMLWriter(out, outformat);
		writer.write(doc);
		writer.flush();
	}

}
