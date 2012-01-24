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
package org.openlaszlo.generator.elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.xml.sax.Attributes;

public class ElementList {

	// Using natural order ?!
	private final Map<String, Element> elementList = new HashMap<String, Element>();

	public void addElement(String name, Attributes attributes, String memoryLastElement) {
		
		if (memoryLastElement.length() > 0) {
			Element parentElement = elementList.get(memoryLastElement);
			if (parentElement == null) {
				new Exception("parentElement missing "+memoryLastElement);
				return;
			}
			if (parentElement.getChildelements() == null) {
				new Exception("getChildelements missing ");
			}
			parentElement.getChildelements().add(name);
		}

		Element element = elementList.get(name);
		
		if (element == null) {
			element = new Element();
		}
		
		for (int i = 0; i < attributes.getLength(); i++) {
			element.getAttributes().add(attributes.getQName(i));
		}

		elementList.put(name, element);

	}
	
	public final String[] TEXT_OPTION_ENABLED = { "handler", "method", "text" };
	
	private boolean checkAllowSingleTextNode(String key) {
		for (String textOption : TEXT_OPTION_ENABLED) {
			if (textOption.equals(key)) {
				return true;
			}
		}
		return false;
	}

	public void filePrint(boolean debug) {
		try {
			File f = new File("project.dtd");
			if (f.exists()){
				f.delete();
			}
			f.createNewFile();
			
			OutputStream ou = new FileOutputStream(f);
			
			for (Entry<String, Element> entry : elementList
					.entrySet()) {
				
				String key = entry.getKey();
				Element element = entry.getValue();
				
				StringBuilder sBuilder = new StringBuilder();

				if (element.getChildelements().size()>0) {
					sBuilder.append("<!ELEMENT " + key + " ( ");
					int i = 0;
					for (String child : element.getChildelements()) {
						if (i!=0) {
							sBuilder.append(" |");
						}
						sBuilder.append(" "+child+"");
						i++;
					}
					sBuilder.append(" )* >\n");
				} else {
					
					if (checkAllowSingleTextNode(key)) {
						sBuilder.append("<!ELEMENT " + key + " ( #PCDATA ) > \n");
					} else {
						sBuilder.append("<!ELEMENT " + key + " EMPTY > \n");
					}
					
				}
				
				if (element.getAttributes().size() > 0) {
					sBuilder.append("<!ATTLIST " + key + " \n");
		
					for (String attribute : element.getAttributes()) {
						sBuilder.append("    " + attribute + " CDATA  #IMPLIED \n");
					}
					sBuilder.append(">\n");
				}
				
				if (debug) {
					System.out.print(sBuilder);
				}
				
				ou.write(sBuilder.toString().getBytes());

			}
			ou.close();
		} catch (Exception err) {
			err.printStackTrace();
		}
	}

}
