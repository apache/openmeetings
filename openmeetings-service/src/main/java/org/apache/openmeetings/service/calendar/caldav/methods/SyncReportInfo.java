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
package org.apache.openmeetings.service.calendar.caldav.methods;

import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.property.DavPropertyNameSet;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.apache.jackrabbit.webdav.xml.Namespace;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Used to represent a Sync Report, defined in RFC 6578. Taken from RFC 6578 are
 * the following XML definitions
 *
 * <code>
 * &lt;!ELEMENT sync-collection (sync-token, sync-level, limit?, prop)&gt;
 *
 *
 * &lt;!ELEMENT sync-token #PCDATA&gt;
 * &lt;!-- Text MUST be a valid URI --&gt;
 *
 *
 * &lt;!ELEMENT sync-level CDATA&gt;
 * &lt;!-- Text MUST be either "1" or "infinite" --&gt;
 * </code>
 *
 * @see SyncMethod
 */
public class SyncReportInfo implements XmlSerializable {
	public static final String XML_SYNC_COLLECTION = "sync-collection";
	public static final String XML_SYNC_TOKEN = "sync-token";
	public static final String XML_SYNC_LEVEL = "sync-level";
	public static final String XML_LIMIT = "limit";
	public static final String XML_NRESULTS = "nresults";
	public static final Namespace NAMESPACE = DavConstants.NAMESPACE;

	public static final int SYNC_LEVEL_1 = 1;
	public static final int SYNC_LEVEL_INF = Integer.MAX_VALUE;

	private String syncToken = null;
	private int syncLevel = SYNC_LEVEL_1;
	private DavPropertyNameSet properties = new DavPropertyNameSet();
	private int depth = -1;
	private int limit = Integer.MIN_VALUE;

	public SyncReportInfo() {
		//default constructor
	}

	public SyncReportInfo(String syncToken, DavPropertyNameSet properties, int syncLevel) {
		this.syncToken = syncToken;
		this.properties.addAll(properties);
		this.syncLevel = syncLevel;
	}

	public SyncReportInfo(String syncToken, DavPropertyNameSet properties, int syncLevel, int depth) {
		this(syncToken, properties, syncLevel);
		this.depth = depth;
	}

	public SyncReportInfo(String syncToken, DavPropertyNameSet properties, int syncLevel, int limit, int depth) {
		this(syncToken, properties, syncLevel, depth);
		this.limit = limit;
	}

	// Getters+setters
	public void setSyncToken(String syncToken) {
		this.syncToken = syncToken;
	}

	public String getSyncToken() {
		return syncToken;
	}

	public void addProperty(String name, Namespace namespace) {
		this.addProperty(DavPropertyName.create(name, namespace));
	}

	public void addProperty(DavPropertyName name) {
		properties.add(name);
	}

	public void addProperties(DavPropertyNameSet set) {
		properties.addAll(set);
	}

	public DavPropertyNameSet getProperties() {
		return properties;
	}

	public void setProperties(DavPropertyNameSet properties) {
		this.properties = properties;
	}

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	public void setSyncLevel(int syncLevel) {
		this.syncLevel = syncLevel;
	}

	public int getSyncLevel() {
		return syncLevel;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public int getLimit() {
		return limit;
	}

	/**
	 * @see XmlSerializable#toXml(Document)
	 * @param document - document to create report info from
	 * @return report info as {@link Element}
	 */
	@Override
	public Element toXml(Document document) {
		Element syncCollection = DomUtil.createElement(document, XML_SYNC_COLLECTION, NAMESPACE);

		DomUtil.addChildElement(syncCollection, XML_SYNC_TOKEN, NAMESPACE, syncToken);

		if (limit > 0) {
			Element xlimit = DomUtil.addChildElement(syncCollection, XML_LIMIT, NAMESPACE);
			DomUtil.addChildElement(xlimit, XML_NRESULTS, NAMESPACE, Integer.toString(this.limit));
		}

		DomUtil.addChildElement(syncCollection, XML_SYNC_LEVEL, NAMESPACE,
				(syncLevel == SYNC_LEVEL_INF) ? "infinity" : "1");

		if (properties != null && !properties.isEmpty()) {
			syncCollection.appendChild(properties.toXml(document));
		}

		return syncCollection;
	}
}
