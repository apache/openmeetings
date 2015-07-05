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
package org.apache.openmeetings.web.util;

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;
import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.WebSession.getExternalType;
import static org.apache.openmeetings.web.app.WebSession.getRecordingId;
import static org.apache.openmeetings.web.app.WebSession.getUserId;
import static org.red5.logging.Red5LoggerFactory.getLogger;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.openmeetings.db.dao.record.FlvRecordingDao;
import org.apache.openmeetings.db.dao.user.OrganisationUserDao;
import org.apache.openmeetings.db.entity.record.FlvRecording;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.AbstractResource;
import org.apache.wicket.request.resource.AbstractResource.ResourceResponse;
import org.apache.wicket.request.resource.ContentDisposition;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.IResource.Attributes;
import org.apache.wicket.request.resource.PartWriterCallback;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.util.io.IOUtils;
import org.apache.wicket.util.resource.FileResourceStream;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;
import org.apache.wicket.util.string.StringValue;
import org.apache.wicket.util.time.Time;
import org.slf4j.Logger;

public abstract class RecordingResourceReference extends ResourceReference {
	private static final long serialVersionUID = 1L;
	private static final Logger log = getLogger(RecordingResourceReference.class, webAppRootKey);

	public RecordingResourceReference(Class<? extends RecordingResourceReference> clazz) {
		super(clazz, "recordings");
	}

	@Override
	public IResource getResource() {
		return new AbstractResource() {
			private static final long serialVersionUID = 1L;
			private File file;
			
			@Override
			protected ResourceResponse newResourceResponse(Attributes attributes) {
				ResourceResponse rr = new ResourceResponse();
				FlvRecording r = getRecording(attributes);
				if (r != null) {
					file = getFile(r);
					IResourceStream rs = file == null ? null : new FileResourceStream(file);
					
					if (rs != null) {
						rr.setFileName(getFileName(r));
						rr.setContentType(RecordingResourceReference.this.getContentType());
						rr.setContentDisposition(ContentDisposition.INLINE);
						rr.setLastModified(Time.millis(file.lastModified()));
						rr.setAcceptRange(ContentRangeType.BYTES);
						
						try {
							// read resource data to get the content length
							InputStream inputStream = rs.getInputStream();
	
							byte[] bytes = null;
							// send Content-Length header
							bytes = IOUtils.toByteArray(inputStream);
							rr.setContentLength(bytes.length);
	
							// get content range information
							RequestCycle cycle = RequestCycle.get();
							Long startbyte = cycle.getMetaData(CONTENT_RANGE_STARTBYTE);
							Long endbyte = cycle.getMetaData(CONTENT_RANGE_ENDBYTE);
	
							// send response body with resource data
							PartWriterCallback partWriterCallback = new PartWriterCallback(bytes != null
								? new ByteArrayInputStream(bytes) : inputStream, rr.getContentLength(), startbyte, endbyte);
	
							// If read buffered is set to false ensure the part writer callback is going to
							// close the input stream
							rr.setWriteCallback(partWriterCallback.setClose(false));
						} catch (IOException e) {
							log.debug(e.getMessage(), e);
							return sendResourceError(rr, file, 500, "Unable to read resource stream");
						} catch (ResourceStreamNotFoundException e) {
							log.debug(e.getMessage(), e);
							return sendResourceError(rr, file, 500, "Unable to open resource stream");
						} finally {
							try {
								IOUtils.close(rs);
							} catch (IOException e) {
								log.warn("Unable to close the resource stream", e);
							}
						}
					}
						
				} else {
					rr.setError(HttpServletResponse.SC_NOT_FOUND);
				}
				return rr;
			}
		};
	}
	
	private ResourceResponse sendResourceError(ResourceResponse resourceResponse, File file, int errorCode, String errorMessage) {
		String msg = String.format("resource [file = %s]: %s (status=%d)", file == null ? null : file.getAbsolutePath(), errorMessage, errorCode);

		log.warn(msg);

		resourceResponse.setError(errorCode, errorMessage);
		return resourceResponse;
	}

	abstract String getContentType();
	abstract String getFileName(FlvRecording r);
	abstract File getFile(FlvRecording r);
	
	private Long getLong(StringValue id) {
		Long result = null;
		try {
			result = id.toLongObject();
		} catch(Exception e) {
			//no-op
		}
		return result;
	}
	
	private FlvRecording getRecording(Long id) {
		FlvRecording r = getBean(FlvRecordingDao.class).get(id);
		// TODO should we process public?
		// || r.getOwnerId() == 0 || r.getParentFileExplorerItemId() == null || r.getParentFileExplorerItemId() == 0
		if (r == null) {
			return r;
		}
		if (r.getOwnerId() == null || getUserId() == r.getOwnerId()) {
			return r;
		}
		if (r.getOrganizationId() == null || getBean(OrganisationUserDao.class).isUserInOrganization(r.getOrganizationId(), getUserId())) {
			return r;
		}
		//TODO external group check was added for plugin recording download
		String extType = getExternalType();
		if (extType != null && extType.equals(r.getCreator().getExternalType())) {
			return r;
		}
		return null;
	}
	
	private FlvRecording getRecording(Attributes attributes) {
		PageParameters params = attributes.getParameters();
		StringValue idStr = params.get("id");
		Long id = getLong(idStr);
		WebSession ws = WebSession.get();
		if (id != null && ws.isSignedIn()) {
			return getRecording(id);
		} else {
			ws.invalidate();
			if (ws.signIn(idStr.toString())) {
				return getRecording(getRecordingId());
			}
		}
		return null;
	}
}
