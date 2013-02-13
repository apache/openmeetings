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
package org.apache.openmeetings.rss;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.data.basic.AuthLevelUtil;
import org.apache.openmeetings.data.basic.dao.ConfigurationDao;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class LoadAtomRssFeed {

	private static final Logger log = Red5LoggerFactory.getLogger(
			LoadAtomRssFeed.class, OpenmeetingsVariables.webAppRootKey);
	@Autowired
	private ConfigurationDao configurationDao;
	@Autowired
	private AuthLevelUtil authLevelmanagement;

	public LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, Object>>>> getRssFeeds(
			Long user_level) {
		try {
			if (authLevelmanagement.checkUserLevel(user_level)) {
				LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, Object>>>> returnMap = new LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, Object>>>>();

				String url1 = configurationDao.getConfValue("rss_feed1", String.class, "");
				returnMap.put("feed1", this.parseRssFeed(url1));

				String url2 = configurationDao.getConfValue("rss_feed2", String.class, "");
				returnMap.put("feed2", this.parseRssFeed(url2));

				return returnMap;
			} else {
				log.error("[getRssFeeds] authorization required");
			}

		} catch (Exception ex) {
			log.error("[getRssFeeds]", ex);
		}
		return null;
	}

	public LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, Object>>> parseRssFeed(
			String urlEndPoint) {
		try {
			LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, Object>>> lMap = new LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, Object>>>();

			URL url = new URL(urlEndPoint);

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; .NET CLR 1.1.4322)");
			conn.setRequestProperty("Referer",
					"http://openmeetings.apache.org/");
			conn.connect();

			SAXReader reader = new SAXReader();
			Document document = reader.read(conn.getInputStream());

			Element root = document.getRootElement();
			int l = 0;

			for (@SuppressWarnings("unchecked")
			Iterator<Element> i = root.elementIterator(); i.hasNext();) {
				Element item = i.next();
				LinkedHashMap<String, LinkedHashMap<String, Object>> items = new LinkedHashMap<String, LinkedHashMap<String, Object>>();
				boolean isSubElement = false;

				for (@SuppressWarnings("unchecked")
				Iterator<Element> it2 = item.elementIterator(); it2.hasNext();) {
					Element subItem = it2.next();

					LinkedHashMap<String, Object> itemObj = new LinkedHashMap<String, Object>();

					itemObj.put("name", subItem.getName());
					itemObj.put("text", subItem.getText());

					LinkedHashMap<String, String> attributes = new LinkedHashMap<String, String>();

					for (@SuppressWarnings("unchecked")
					Iterator<Attribute> attr = subItem.attributeIterator(); attr
							.hasNext();) {
						Attribute at = attr.next();
						attributes.put(at.getName(), at.getText());
					}
					itemObj.put("attributes", attributes);

					// log.error(subItem.getName()+ ": " +subItem.getText());
					items.put(subItem.getName(), itemObj);
					isSubElement = true;
				}

				if (isSubElement) {
					l++;
					lMap.put("item" + l, items);
				}

			}

			return lMap;

		} catch (Exception err) {
			log.error("[parseRssFeed]", err);
		}
		return null;

	}

}
