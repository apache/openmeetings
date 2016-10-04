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

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

import java.io.IOException;

import org.apache.commons.httpclient.HttpConnection;
import org.apache.commons.httpclient.HttpState;
import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavMethods;
import org.apache.jackrabbit.webdav.DavServletResponse;
import org.apache.jackrabbit.webdav.MultiStatus;
import org.apache.jackrabbit.webdav.client.methods.DavMethodBase;
import org.apache.jackrabbit.webdav.client.methods.ReportMethod;
import org.apache.jackrabbit.webdav.header.DepthHeader;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.w3c.dom.Document;

/**
 * Class to work with WebDAV-Sync Method defined in RFC 6578.
 * 
 * @see SyncReportInfo for Request Report to be given as argument
 */
public class SyncMethod extends DavMethodBase {
	private static final Logger log = Red5LoggerFactory.getLogger(ReportMethod.class, webAppRootKey);

	private MultiStatus multiStatus = null;
	private String synctoken = null;

	public SyncMethod(String uri, SyncReportInfo reportInfo) throws IOException {
		super(uri);
		setRequestBody(reportInfo);

		if (reportInfo.getDepth() >= 0) {
			parseDepth(reportInfo.getDepth());
		}

		log.info("Using the WEBDAV-SYNC method for syncing.");
	}

	/**
	 * Used to add request header for Depth.
	 * 
	 * @param depth
	 *            Depth of the Request
	 */
	private void parseDepth(int depth) {
		addRequestHeader(new DepthHeader(depth));
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
	public String getName() {
		return DavMethods.METHOD_REPORT;
	}

	/**
	 * @see DavMethodBase#isSuccess
	 * @return Return true only when when Response is Multistatus.
	 */
	@Override
	protected boolean isSuccess(int statusCode) {
		return statusCode == DavServletResponse.SC_MULTI_STATUS;
	}

	public String getResponseSynctoken() {
		checkUsed();
		return synctoken;
	}

	/**
	 * Adapted from DavMethodBase to handle MultiStatus responses.
	 * 
	 * @return MultiStatus response
	 * @throws IOException
	 * @throws DavException
	 */
	@Override
	public MultiStatus getResponseBodyAsMultiStatus() throws IOException, DavException {
		checkUsed();
		if (multiStatus != null) {
			return multiStatus;
		} else {
			DavException dx = getResponseException();
			if (dx != null) {
				throw dx;
			} else {
				throw new DavException(getStatusCode(), getName() + " resulted with unexpected status: " + getStatusLine());
			}
		}
	}

	/**
	 * Overridden to process the sync-token. Adapted from DavMethodBase. TODO:
	 * Fix this override.
	 * 
	 * @see DavMethodBase#processResponseBody(HttpState, HttpConnection)
	 */
	@Override
	protected void processResponseBody(HttpState httpState, HttpConnection httpConnection) {
		if (getStatusCode() == DavServletResponse.SC_MULTI_STATUS) {
			try {
				Document document = getResponseBodyAsDocument();
				if (document != null) {
					synctoken = DomUtil.getChildText(document.getDocumentElement(), SyncReportInfo.XML_SYNC_TOKEN, DavConstants.NAMESPACE);
					log.info("Sync-Token for REPORT: " + synctoken);

					multiStatus = MultiStatus.createFromXml(document.getDocumentElement());
					processMultiStatusBody(multiStatus, httpState, httpConnection);
				}
			} catch (IOException e) {
				log.error("Error while parsing sync-token.", e);
				setSuccess(false);
			}
		}
	}
}
