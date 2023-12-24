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
package org.apache.openmeetings.web.common.upload;

import static java.util.UUID.randomUUID;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getMaxUploadSize;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import jakarta.inject.Inject;
import jakarta.ws.rs.core.MediaType;

import org.apache.commons.fileupload2.core.FileItem;
import org.apache.openmeetings.core.util.WebSocketHelper;
import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.app.ClientManager;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.protocol.http.servlet.MultipartServletWebRequest;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.request.resource.AbstractResource;
import org.apache.wicket.request.resource.AbstractResource.ResourceResponse;
import org.apache.wicket.request.resource.AbstractResource.WriteCallback;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.IResource.Attributes;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.util.lang.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.openjson.JSONObject;

public abstract class UploadResourceReference extends ResourceReference {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(UploadResourceReference.class);
	private static final String PARAM_FILE_NAME = "omws-upload-file";
	private static final String PARAM_SID_NAME = "omws-upload-sid";
	private static final String ATTR_STATUS = "status";
	private enum Status {
		SUCCESS
		, PROGRESS
		, ERROR
	}

	@Inject
	private ClientManager cm;

	protected UploadResourceReference(Class<?> scope, String name) {
		super(scope, name);
		Injector.get().inject(this);
	}

	@Override
	public IResource getResource() {
		return new AbstractResource() {
			private static final long serialVersionUID = 1L;

			@Override
			protected ResourceResponse newResourceResponse(Attributes attributes) {
				final ResourceResponse response = new ResourceResponse();
				final ServletWebRequest webRequest = (ServletWebRequest) attributes.getRequest();
				try {
					MultipartServletWebRequest multiPartRequest = webRequest.newMultipartWebRequest(Bytes.bytes(getMaxUploadSize()), "ignored");
					multiPartRequest.parseFileParts();

					String sid = multiPartRequest.getPostParameters().getParameterValue(PARAM_SID_NAME).toString();
					Client c = cm.getBySid(sid);
					final long langId = getLangId(c);
					if (isUploadAllowed(c)) {
						Map<String, List<FileItem>> files = multiPartRequest.getFiles();
						final List<FileItem> fileItems = files.get(PARAM_FILE_NAME);

						final String uuid = randomUUID().toString();
						processFiles(c, fileItems, uuid, multiPartRequest);

						prepareResponse(response, Status.SUCCESS, uuid, Application.getString("54", langId));
					} else {
						prepareResponse(response, Status.ERROR, null, Application.getString("access.denied.header", langId));
					}
				} catch (Exception e) {
					log.error("An error occurred while uploading a file", e);
					prepareResponse(response, Status.ERROR, null, e.getMessage());
				}
				return response;
			}
		};
	}

	protected abstract boolean isUploadAllowed(Client c);
	protected abstract void processFiles(Client c, List<FileItem> fileItems, String uuid , MultipartServletWebRequest multiPartRequest);

	protected static long getLangId(Client c) {
		return c == null || c.getUser() == null ? 1L : c.getUser().getLanguageId();
	}

	private static void prepareResponse(ResourceResponse response, Status status, String uuid, String msg) {
		response.setContentType(MediaType.APPLICATION_JSON);
		response.setWriteCallback(new WriteCallback() {
			@Override
			public void writeData(Attributes attributes) throws IOException {
				attributes.getResponse().write(new JSONObject()
						.put(ATTR_STATUS, status.name())
						.put("message", msg)
						.put("uuid", uuid)
						.toString());
			}
		});
	}

	protected JSONObject getBaseMessage(String uuid) {
		return new JSONObject()
				.put("uuid", uuid)
				.put("type", "omws-upload");
	}

	protected void sendError(Client c, String uuid, String msg) {
		WebSocketHelper.sendClient(c, getBaseMessage(uuid)
				.put(ATTR_STATUS, Status.ERROR.name())
				.put("message", msg));
	}

	protected void sendProgress(Client c, String uuid, AtomicInteger progress, int cur) {
		if (cur > progress.get()) {
			progress.set(cur);
			WebSocketHelper.sendClient(c, getBaseMessage(uuid)
					.put(ATTR_STATUS, Status.PROGRESS.name())
					.put("progress", cur));
		}
	}
}
