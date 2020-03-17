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
package org.apache.openmeetings.db.util;

import static javax.xml.stream.XMLInputFactory.IS_NAMESPACE_AWARE;
import static javax.xml.stream.XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES;
import static javax.xml.stream.XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES;
import static javax.xml.stream.XMLInputFactory.IS_VALIDATING;
import static javax.xml.stream.XMLInputFactory.SUPPORT_DTD;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLInputFactory;

import org.dom4j.io.SAXReader;
import org.xml.sax.SAXException;

public class XmlHelper {
	private static final String NO_DOCTYPE = "http://apache.org/xml/features/disallow-doctype-decl";
	private XmlHelper() {
		//no access
	}

	public static DocumentBuilder createBuilder() throws ParserConfigurationException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		dbFactory.setFeature(NO_DOCTYPE, true);
		dbFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
		dbFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
		dbFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		dbFactory.setXIncludeAware(false);
		dbFactory.setExpandEntityReferences(false);
		return dbFactory.newDocumentBuilder();
	}

	public static XMLInputFactory createInputFactory() {
		XMLInputFactory factory = XMLInputFactory.newInstance();
		factory.setProperty(IS_REPLACING_ENTITY_REFERENCES, false);
		factory.setProperty(IS_VALIDATING, false);
		factory.setProperty(IS_NAMESPACE_AWARE, false);
		factory.setProperty(IS_SUPPORTING_EXTERNAL_ENTITIES, false);
		factory.setProperty(SUPPORT_DTD, false);
		return factory;
	}

	public static SAXReader createSaxReader() throws SAXException {
		SAXReader reader = new SAXReader();
		reader.setFeature(NO_DOCTYPE, true);
		return reader;
	}
}
