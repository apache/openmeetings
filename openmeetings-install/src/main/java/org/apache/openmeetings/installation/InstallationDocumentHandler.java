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
package org.apache.openmeetings.installation;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.apache.openmeetings.util.OmFileHelper;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

public class InstallationDocumentHandler {
	public static void createDocument(int stepNo) throws Exception {
		Document document = DocumentHelper.createDocument();

		Element root = document.addElement("install");
		Element step = root.addElement("step");

		step.addElement("stepnumber").addText("" + stepNo);
		step.addElement("stepname").addText("Step " + stepNo);

		try (OutputStream os = new FileOutputStream(OmFileHelper.getInstallFile())) {
			XMLWriter writer = new XMLWriter(new OutputStreamWriter(os, UTF_8));
			writer.write(document);
			writer.close();
		}
	}

	public static int getCurrentStepNumber() throws Exception {
		SAXReader reader = new SAXReader();
		Document document = reader.read(OmFileHelper.getInstallFile());

		Node node = document.selectSingleNode("//install/step/stepnumber");

		return Integer.valueOf(node.getText()).intValue();
	}
}
