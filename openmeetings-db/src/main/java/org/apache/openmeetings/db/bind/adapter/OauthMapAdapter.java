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
package org.apache.openmeetings.db.bind.adapter;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.openmeetings.db.dto.user.OAuthUser;
import org.apache.wicket.util.string.Strings;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class OauthMapAdapter extends XmlAdapter<Object, Map<String, String>> {

	@Override
	public Object marshal(Map<String, String> v) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document document = db.newDocument();
		Element root = document.createElement("mapping");
		document.appendChild(root);

		for (Entry<String, String> e : v.entrySet()) {
			Element entry = document.createElement("entry");
			Element key = document.createElement("key");
			key.setTextContent(e.getKey());
			entry.appendChild(key);
			Element value = document.createElement("value");
			value.setTextContent(e.getValue());
			entry.appendChild(value);
			root.appendChild(entry);
		}
		return document;
	}

	private static void putValue(Map<String, String> map, String key, String value) {
		if (!Strings.isEmpty(value)) {
			map.put(key, value);
		}
	}

	@Override
	public Map<String, String> unmarshal(Object v) throws Exception {
		Map<String, String> map = new HashMap<>();
		Element el = (Element)v;
		if ("loginParamName".equals(el.getLocalName())) {
			putValue(map, OAuthUser.PARAM_LOGIN, el.getTextContent());
		} else if ("emailParamName".equals(el.getLocalName())) {
			putValue(map, OAuthUser.PARAM_EMAIL, el.getTextContent());
		} else if ("firstnameParamName".equals(el.getLocalName())) {
			putValue(map, OAuthUser.PARAM_FNAME, el.getTextContent());
		} else if ("lastnameParamName".equals(el.getLocalName())) {
			putValue(map, OAuthUser.PARAM_LNAME, el.getTextContent());
		} else if ("mapping".equals(el.getLocalName())) {
			NodeList entries = el.getChildNodes();
			for (int i = 0; i < entries.getLength(); ++i) {
				Node entry = entries.item(i);
				NodeList children = entry.getChildNodes();
				if ("entry".equals(entry.getLocalName()) && children.getLength() == 2) {
					putValue(map, children.item(0).getTextContent(), children.item(1).getTextContent());
				}
			}
		}
		return map;
	}
}
