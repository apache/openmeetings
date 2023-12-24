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

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

import org.apache.openmeetings.db.dto.user.OAuthUser;
import org.apache.openmeetings.db.util.XmlHelper;
import org.apache.wicket.util.string.Strings;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class OauthMapAdapter extends XmlAdapter<Object, Map<String, String>> {

	@Override
	public Object marshal(Map<String, String> v) throws Exception {
		Document document = XmlHelper.createBuilder().newDocument();
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
		return root;
	}

	private static void putValue(Map<String, String> map, String key, String value) {
		if (!Strings.isEmpty(value)) {
			map.put(key, value);
		}
	}

	private static Map<String, String> getMap(String key, String value) {
		Map<String, String> map = new HashMap<>();
		putValue(map, key, value);
		return map;
	}

	private static Map<String, String> getMap(NodeList entries) {
		Map<String, String> map = new HashMap<>();
		for (int i = 0; i < entries.getLength(); ++i) {
			Node entry = entries.item(i);
			NodeList children = entry.getChildNodes();
			if ("entry".equals(entry.getLocalName()) && children.getLength() > 1) {
				Node key = null;
				Node value = null;
				for (int j = 0; j < children.getLength(); ++j) {
					Node n = children.item(j);
					if (n.getNodeType() == Node.TEXT_NODE) {
						continue;
					}
					if (key == null) {
						key = n;
						continue;
					}
					value = n;
					break;
				}
				if (key != null && value != null) {
					putValue(map, key.getTextContent(), value.getTextContent());
				}
			}
		}
		return map;
	}

	@Override
	public Map<String, String> unmarshal(Object v) throws Exception {
		Element el = (Element)v;
		if ("loginParamName".equals(el.getLocalName())) {
			return getMap(OAuthUser.PARAM_LOGIN, el.getTextContent());
		}
		if ("emailParamName".equals(el.getLocalName())) {
			return getMap(OAuthUser.PARAM_EMAIL, el.getTextContent());
		}
		if ("firstnameParamName".equals(el.getLocalName())) {
			return getMap(OAuthUser.PARAM_FNAME, el.getTextContent());
		}
		if ("lastnameParamName".equals(el.getLocalName())) {
			return getMap(OAuthUser.PARAM_LNAME, el.getTextContent());
		}
		if ("mapping".equals(el.getLocalName())) {
			return getMap(el.getChildNodes());
		}
		return Map.of();
	}
}
