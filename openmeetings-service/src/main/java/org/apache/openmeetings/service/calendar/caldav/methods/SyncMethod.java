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

import java.io.IOException;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavMethods;
import org.apache.jackrabbit.webdav.DavServletResponse;
import org.apache.jackrabbit.webdav.MultiStatus;
import org.apache.jackrabbit.webdav.client.methods.BaseDavRequest;
import org.apache.jackrabbit.webdav.client.methods.XmlEntity;
import org.apache.jackrabbit.webdav.header.DepthHeader;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

/**
 * Class to work with WebDAV-Sync Method defined in RFC 6578.
 *
 * @see SyncReportInfo for Request Report to be given as argument
 */
public class SyncMethod extends BaseDavRequest {
	private static final Logger log = LoggerFactory.getLogger(SyncMethod.class);

	private MultiStatus multiStatus = null;
	private String synctoken = null;
	private boolean processedResponse = false;

	public SyncMethod(URI uri, SyncReportInfo reportInfo) throws IOException {
		super(uri);
		setEntity(XmlEntity.create(reportInfo));

		if (reportInfo.getDepth() >= 0) {
			parseDepth(reportInfo.getDepth());
		}

		log.info("Using the WEBDAV-SYNC method for syncing.");
	}

	public SyncMethod(String uri, SyncReportInfo reportInfo) throws IOException {
		this(URI.create(uri), reportInfo);
	}

	/**
	 * Used to add request header for Depth.
	 *
	 * @param depth
	 *            Depth of the Request
	 */
	private void parseDepth(int depth) {
		DepthHeader dh = new DepthHeader(depth);
		setHeader(dh.getHeaderName(), dh.getHeaderValue());
	}

	/**
	 * Set the Depth Header of the Sync Report.
	 *
	 * @param depth
	 *            Depth of the Request
	 */
	public void setDepth(int depth) {
		parseDepth(depth);
	}

	/**
	 * Implements the Report Method.
	 */
	@Override
	public String getMethod() {
		return DavMethods.METHOD_REPORT;
	}

	/**
	 * @see BaseDavRequest#succeeded(HttpResponse)
	 * @return Return true only when when Response is Multistatus.
	 */
	@Override
	public boolean succeeded(HttpResponse response) {
		return response.getStatusLine().getStatusCode() == DavServletResponse.SC_MULTI_STATUS;
	}

	public String getResponseSynctoken(HttpResponse response) {
		if (!processedResponse) {
			processResponseBody(response);
		}
		return synctoken;
	}

	/**
	 * Adapted from DavMethodBase to handle MultiStatus responses.
	 *
	 * @param response {@link HttpResponse} to be converted to {@link MultiStatus}
	 * @return MultiStatus response
	 * @throws DavException if the response body could not be parsed
	 */
	@Override
	public MultiStatus getResponseBodyAsMultiStatus(HttpResponse response) throws DavException {
		if (!processedResponse) {
			processResponseBody(response);
		}

		if (multiStatus != null) {
			return multiStatus;
		} else {
			DavException dx = getResponseException(response);
			if (dx != null) {
				throw dx;
			} else {
				throw new DavException(response.getStatusLine().getStatusCode(), getMethod() + " resulted with unexpected status: " + response.getStatusLine());
			}
		}
	}

	/**
	 * Process the sync-token, from the response.
	 */
	protected void processResponseBody(HttpResponse response) {
		if (!processedResponse && succeeded(response)) {
			try {
				Document document = getResponseBodyAsDocument(response.getEntity());
				if (document != null) {
					synctoken = DomUtil.getChildText(document.getDocumentElement(), SyncReportInfo.XML_SYNC_TOKEN, DavConstants.NAMESPACE);
					log.info("Sync-Token for REPORT: {}", synctoken);
					multiStatus = MultiStatus.createFromXml(document.getDocumentElement());
				}
			} catch (IOException e) {
				log.error("Error while parsing sync-token.", e);
			}

			processedResponse = true;
		}
	}

	@Override
	public void reset() {
		super.reset();
		processedResponse = false;
	}
}
