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
package org.apache.openmeetings.service.scheduler;

import static org.apache.commons.text.StringEscapeUtils.escapeXml11;
import static org.apache.openmeetings.core.rss.LoadAtomRssFeed.getFeedConnection;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.apache.openmeetings.db.util.XmlHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.openjson.JSONArray;
import com.github.openjson.JSONObject;

public class AtomReader {
	private static final Logger log = LoggerFactory.getLogger(AtomReader.class);
	private static final String ATTR_CONTENT = "content";
	private static final String ATTR_PUBLISHED = "published";
	private static final int MAX_ITEM_COUNT = 5;
	private static final Map<String, Spec> specs = new HashMap<>();
	private static final XMLInputFactory inputFactory = XmlHelper.createInputFactory();
	static {
		add("item")
			.add(new Field("title"))
			.add(new Field("link"))
			.add(new Field("description", ATTR_CONTENT, true))
			.add(new Field("pubDate", ATTR_PUBLISHED))
			.add(new Field("author"));
		add("entry")
			.add(new Field("title"))
			.add(new Field("link", "link", "href", false))
			.add(new Field(ATTR_CONTENT, ATTR_CONTENT, true))
			.add(new Field("updated", ATTR_PUBLISHED))
			.add(new Field(ATTR_PUBLISHED))
			.add(new Field("author"));
	}

	private AtomReader() {
		// denied
	}

	private static Spec add(String name) {
		Spec s = new Spec(name);
		specs.put(name, s);
		return s;
	}

	public static void load(String url, JSONArray feed) {
		HttpURLConnection con = null;
		try {
			con = getFeedConnection(url);
			try (InputStream is = con.getInputStream()) {
				XMLEventReader reader = inputFactory.createXMLEventReader(is);
				int i = 0;
				JSONObject obj = null;
				StringBuilder val = new StringBuilder(); // for NPE safety
				Spec spec = new Spec(""); // for NPE safety
				Field f = null;
				while (reader.hasNext()) {
					XMLEvent evt = reader.nextEvent();
					if (obj == null && evt.isStartElement()) {
						StartElement start = (StartElement)evt;
						String name = start.getName().getLocalPart();
						if (specs.containsKey(name)) {
							spec = specs.get(name);
							obj = new JSONObject();
							i++;
						}
					} else if (obj != null) {
						if (evt.isStartElement()) {
							StartElement start = (StartElement)evt;
							String name = start.getName().getLocalPart();
							if (spec.contains(name)) {
								f = spec.get(name);
								val = new StringBuilder();
								if (f.getAttr() != null) {
									Attribute a = start.getAttributeByName(new QName(f.getAttr()));
									if (a != null) {
										val.append(a.getValue());
									}
								}
							}
						} else if (f != null && evt.isCharacters()) {
							val.append(escapeXml11(((Characters)evt).getData()));
						} else if (f != null && evt.isEndElement() && f.getName().equals(((EndElement)evt).getName().getLocalPart())) {
							if (!obj.has(f.getAlias())) {
								obj.put(f.getAlias(), val.toString());
							}
							f = null;
						} else if (evt.isEndElement() && spec.getName().equals(((EndElement)evt).getName().getLocalPart())) {
							feed.put(obj);
							obj = null;
						}
					}
					if (i > MAX_ITEM_COUNT) {
						break;
					}
				}
			}
		} catch (IOException|XMLStreamException e) {
			log.error("Unexpected error while getting RSS", e);
		} finally {
			if (con != null) {
				con.disconnect();
			}
		}
	}

	public static class Spec {
		private final String name;
		private final Map<String, Field> fields = new LinkedHashMap<>();

		public Spec(final String name) {
			this.name = name;
		}

		public Spec add(Field f) {
			fields.put(f.getName(), f);
			return this;
		}

		public String getName() {
			return name;
		}

		public boolean contains(String f) {
			return fields.containsKey(f);
		}

		public Field get(String f) {
			return fields.get(f);
		}
	}

	public static class Field {
		private final String name;
		private final String alias;
		private final String attr;
		private final boolean xml;

		public Field(String name) {
			this(name, name);
		}

		public Field(String name, String alias) {
			this(name, alias, false);
		}

		public Field(String name, String alias, boolean xml) {
			this(name, alias, null, xml);
		}

		public Field(String name, String alias, String attr, boolean xml) {
			this.name= name;
			this.alias = alias;
			this.attr = attr;
			this.xml = xml;
		}

		public String getName() {
			return name;
		}

		public String getAlias() {
			return alias;
		}

		public String getAttr() {
			return attr;
		}

		public boolean isXml() {
			return xml;
		}
	}
}
