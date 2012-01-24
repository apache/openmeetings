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
package org.openlaszlo.schema;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.openlaszlo.generator.elements.ClassAttribute;
import org.openlaszlo.generator.elements.ClassElement;
import org.w3c.dom.Document;

import com.thaiopensource.relaxng.translate.Driver;

public class GenerateXsd {
	
	public static void main(String... args) {
		new GenerateXsd();
	}
	
	public GenerateXsd() {
		
		//Driver driver = new Driver();
		
		String[] args = { "test/lzx.rnc", "test/lzx_by_rnc.xsd" };
		
		Driver.main(args);
		
	}
	
	private DocumentBuilder documentBuilder;
	private TransformerFactory transformerFactory;
	private Transformer transformer;

	public void xsdPrint(String fileName) {
		try {

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			documentBuilder = dbf.newDocumentBuilder();
			transformerFactory = TransformerFactory.newInstance();
			transformer = transformerFactory.newTransformer();
			Document schema = documentBuilder.newDocument();

			schema.appendChild(schema.createElement("schema"));

			ClassElement canvas = new ClassElement();
			canvas.getAllClassAttributes().add(
					new ClassAttribute("width", false, "", null, ""));

			this.addElement(schema, "canvas", canvas);
			
			
			
			StringWriter sw = new StringWriter();
			
			transformer.transform(new DOMSource(schema), new StreamResult(sw));
			
			System.out.println(sw);
			
			File f = new File(fileName+".xml");
			if (f.exists()) {
				f.delete();
			}
			f.createNewFile();
			OutputStream ou = new FileOutputStream(f);
			ou.write(sw.toString().getBytes());
			ou.close();
			String[] args = { fileName+".xml", fileName+".xsd" };
			
			Driver.main(args);
			
		} catch (Exception err) {
			err.printStackTrace();
		}

	}

	private void addElement(Document document, String name,
			ClassElement classElement) {

		org.w3c.dom.Element e = document.createElement(name);

		for (ClassAttribute attr : classElement.getAllClassAttributes()) {

			e.setAttribute(attr.getName(), "string");

		}

		document.getDocumentElement().appendChild(e);

	}

}
