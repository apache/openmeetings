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
package org.apache.openmeetings.cli;

import static org.apache.openmeetings.util.OpenmeetingsVariables.ATTR_VALUE;

import java.io.File;

import javax.xml.XMLConstants;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.StringUtils;
import org.apache.openmeetings.db.util.XmlHelper;
import org.apache.openmeetings.util.ConnectionProperties;
import org.apache.openmeetings.util.ConnectionProperties.DbType;
import org.apache.openmeetings.util.OmFileHelper;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class ConnectionPropertiesPatcher {
	public static final String DEFAULT_DB_NAME = "openmeetings";
	protected static final String URL_PREFIX = "Url";
	protected static final String DRIVER_PREFIX = "DriverClassName";
	protected static final String USER_PREFIX = "Username";
	protected static final String PASS_PREFIX = "Password";
	protected ConnectionProperties props;

	public static ConnectionPropertiesPatcher getPatcher(ConnectionProperties props) {
		ConnectionPropertiesPatcher patcher;
		switch (props.getDbType()) {
			case DB2:
				patcher = new Db2Patcher();
				break;
			case MSSQL:
				patcher = new MssqlPatcher();
				break;
			case MYSQL:
				patcher = new MysqlPatcher();
				break;
			case ORACLE:
				patcher = new OraclePatcher();
				break;
			case POSTGRESQL:
				patcher = new PostgresPatcher();
				break;
			case H2:
			default:
				patcher = new H2Patcher();
				break;
		}
		patcher.props = props;
		return patcher;
	}

	public static ConnectionProperties getConnectionProperties(File conf) throws Exception {
		ConnectionProperties props = new ConnectionProperties();
		Document doc = getDocument(conf);
		Attr attr = getConnectionProperties(doc);
		String[] tokens = attr.getValue().split(",");
		loadProperties(tokens, props);

		return props;
	}

	private static Document getDocument(File xml) throws Exception {
		return XmlHelper.createBuilder().parse(xml);
	}

	private static Attr getConnectionProperties(Document doc) throws Exception {
		XPath xPath = XPathFactory.newInstance().newXPath();
		XPathExpression expr = xPath.compile("/persistence/persistence-unit/properties/property[@name='openjpa.ConnectionProperties']");

		Element element = (Element)expr.evaluate(doc, XPathConstants.NODE);
		return element.getAttributeNode(ATTR_VALUE);
	}

	public static void patch(ConnectionProperties props) throws Exception {
		ConnectionPropertiesPatcher patcher = getPatcher(props);
		Document doc = getDocument(OmFileHelper.getPersistence(props.getDbType()));
		Attr attr = getConnectionProperties(doc);
		String[] tokens = attr.getValue().split(",");
		patcher.patchAttribute(tokens);
		attr.setValue(StringUtils.join(tokens, ","));

		TransformerFactory factory = TransformerFactory.newInstance();
		factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
		factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
		Transformer transformer = factory.newTransformer();
		DOMSource source = new DOMSource(doc);
		transformer.transform(source, new StreamResult(OmFileHelper.getPersistence().getCanonicalPath())); //this constructor is used to avoid transforming path to URI
	}

	public static ConnectionProperties patch(DbType dbType, String host, String port, String db, String user, String pass) throws Exception {
		ConnectionProperties props = getConnectionProperties(OmFileHelper.getPersistence(dbType));
		props.setLogin(user);
		props.setPassword(pass);
		ConnectionPropertiesPatcher patcher = getPatcher(props);
		props.setURL(patcher.getUrl(props.getURL(), host, port, db));
		patch(props);
		return props;
	}

	public static void updateUrl(ConnectionProperties props, String host, String port, String db) {
		ConnectionPropertiesPatcher patcher = getPatcher(props);
		props.setURL(patcher.getUrl(props.getURL(), host, port, db));
	}

	protected void patchAttribute(String[] tokens) {
		for (int i = 0; i < tokens.length; ++i) {
			patchProp(tokens, i, USER_PREFIX, props.getLogin() == null ? "" : props.getLogin());
			patchProp(tokens, i, PASS_PREFIX, props.getPassword() == null ? "" : props.getPassword());
			patchProp(tokens, i, URL_PREFIX, props.getURL());
		}
	}

	protected static void patchProp(String[] tokens, int idx, String name, String value) {
		String prop = tokens[idx].trim();
		if (prop.startsWith(name)) {
			tokens[idx] = String.format("%s=%s", name, value);
		}
	}

	private static void loadProperties(String[] tokens, ConnectionProperties connectionProperties) {
		String prop;
		for (int i = 0; i < tokens.length; ++i) {
			prop = getPropFromPersistence(tokens, i, DRIVER_PREFIX);
			if (prop != null) {
				connectionProperties.setDriver(prop);
			}

			prop = getPropFromPersistence(tokens, i, USER_PREFIX);
			if (prop != null) {
				connectionProperties.setLogin(prop);
			}

			prop = getPropFromPersistence(tokens, i, PASS_PREFIX);
			if (prop != null) {
				connectionProperties.setPassword(prop);
			}

			prop = getPropFromPersistence(tokens, i, URL_PREFIX);
			if (prop != null) {
				try {
					//will try to "guess" dbType
					String[] parts = prop.split(":");
					connectionProperties.setDbType(DbType.of(parts[1]));
				} catch (Exception e) {
					//ignore
				}
				connectionProperties.setURL(prop);
			}
		}
	}

	protected static String getPropFromPersistence(String[] tokens, int idx, String name){
		String prop = tokens[idx].trim();
		if (prop.startsWith(name)) {
			//From "Username=root" getting only "root"
			return prop.substring(prop.indexOf('=') + 1);
		}
		return null;
	}

	protected abstract String getUrl(String url, String host, String port, String db);
}
