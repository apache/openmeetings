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
package org.openmeetings.cli;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class ConnectionPropertiesPatcher {
	protected static final String URL_PREFIX = "Url=";
	protected ConnectionProperties connectionProperties;
	
	public enum PatcherType {
		db2
		, derby
		, mysql
		, oracle
		, postgres
	}
	
	static ConnectionPropertiesPatcher getPatcher(String _dbType) {
		PatcherType dbType = PatcherType.valueOf(_dbType);
		ConnectionPropertiesPatcher patcher = null;
		switch (dbType) {
			case db2:
				patcher = new Db2Patcher();
				break;
			case mysql:
				patcher = new MysqlPatcher();
				break;
			case oracle:
				patcher = new OraclePatcher();
				break;
			case postgres:
				patcher = new PostgresPatcher();
				break;
			case derby:
			default:
				patcher = new DerbyPatcher();
				break;
		}
		return patcher;
	}
	
	static ConnectionProperties getConnectionProperties(File conf) throws Exception {
		ConnectionProperties connectionProperties = new ConnectionProperties();
		Document doc = getDocument(conf);
		Attr attr = getConnectionProperties(doc);
		String[] tokens = attr.getValue().split(",");
		processBasicProperties(tokens, null, null, connectionProperties);
		
		return connectionProperties;
	}
	
	private static Document getDocument(File xml) throws Exception {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		//dbFactory.setNamespaceAware(true);
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		return dBuilder.parse(xml);
	}
	
	private static Attr getConnectionProperties(Document doc) throws Exception {
		XPath xPath = XPathFactory.newInstance().newXPath();
		XPathExpression expr = xPath.compile("/persistence/persistence-unit/properties/property[@name='openjpa.ConnectionProperties']");

		Element element = (Element)expr.evaluate(doc, XPathConstants.NODE);
		return element.getAttributeNode("value");
	}
	
	public void patch(File srcXml, File destXml, String host, String port, String db, String user, String pass, ConnectionProperties connectionProperties) throws Exception {
		this.connectionProperties = connectionProperties;
		Document doc = getDocument(srcXml);
		
		Attr val = getConnectionProperties(doc);
		val = patchAttribute(val, host, port, db, user, pass);
		
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		transformer.transform(source, new StreamResult(destXml));
	}
	
	protected Attr patchAttribute(Attr attr, String host, String port, String db, String user, String pass) {
		String[] tokens = attr.getValue().split(",");
		processBasicProperties(tokens, user, pass, connectionProperties);
		patchDb(tokens, host, port, db);
		attr.setValue(StringUtils.join(tokens, ","));
		return attr;
	}

	protected static void patchProp(String[] tokens, int idx, String name, String value) {
		String prop = tokens[idx].trim();
		if (prop.startsWith(name)) {
			prop = name + "=" + StringEscapeUtils.escapeXml(value);
			tokens[idx] = prop;
		}
	}
	
	private static void processBasicProperties(String[] tokens, String user,
			String pass, ConnectionProperties connectionProperties) {
		String prop;
		for (int i = 0; i < tokens.length; ++i) {
			prop = getPropFromPersistence(tokens, i, "DriverClassName");
			if (prop != null) {
				connectionProperties.setDriver(prop);
			}
			
			if (user != null) {
				patchProp(tokens, i, "Username", user);
				connectionProperties.setLogin(user);
			} else {
				prop = getPropFromPersistence(tokens, i, "Username");
				if (prop != null) {
					connectionProperties.setLogin(prop);
				}
			}
			
			if (pass != null) {
				patchProp(tokens, i, "Password", pass);
				connectionProperties.setPassword(pass);
			} else {
				prop = getPropFromPersistence(tokens, i, "Password");
				if (prop != null) {
					connectionProperties.setPassword(prop);
				}
			}
			prop = getPropFromPersistence(tokens, i, "Url");
			if (prop != null) {
				connectionProperties.setURL(prop);
			}
		}
	}
	
	protected static String getPropFromPersistence(String[] tokens, int idx, String name){
		String prop = tokens[idx].trim();
		if (prop.startsWith(name)) {
			//From "Username=root" getting only "root"
			return prop.substring(prop.indexOf("=") + 1);
		}
		return null;
	}
	
	private void patchDb(String[] tokens, String host, String _port, String _db) {
		for (int i = 0; i < tokens.length; ++i) {
			String prop = tokens[i].trim();
			if (prop.startsWith(URL_PREFIX)) {
				String url = getUrl(prop.substring(URL_PREFIX.length()), host, _port, _db);
				connectionProperties.setURL(url);
				tokens[i] = URL_PREFIX + url;
				break;
			}
		}
	}
	
	protected abstract String getUrl(String url, String host, String port, String db);
}
