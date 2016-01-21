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

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.openmeetings.db.dao.record.RecordingDao;
import org.apache.openmeetings.db.dao.user.GroupUserDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.record.Recording;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.IResource.Attributes;
import org.apache.wicket.resource.FileSystemResource;
import org.apache.wicket.resource.FileSystemResourceReference;
import org.apache.wicket.util.string.StringValue;
import org.slf4j.Logger;

public abstract class RecordingResourceReference extends FileSystemResourceReference {
	private static final long serialVersionUID = 1L;
	private static final Logger log = getLogger(RecordingResourceReference.class, webAppRootKey);

	public RecordingResourceReference(String name) {
		super(name);
	}

	@Override
	public IResource getResource() {
		return new FileSystemResource() {
			private static final long serialVersionUID = 1L;
			private File file;
			
			@Override
			protected String getMimeType() throws IOException {
				return RecordingResourceReference.this.getMimeType();
			}
			
			@Override
			protected ResourceResponse newResourceResponse(Attributes attributes) {
				Recording r = getRecording(attributes);
				if (r != null) {
					file = getFile(r);
					return createResourceResponse(file.toPath());
				} else {
					log.debug("No recording was found");
					ResourceResponse rr = new ResourceResponse();
					rr.setError(HttpServletResponse.SC_NOT_FOUND);
					return rr;
				}
			}
		};
	}
	
	abstract String getMimeType();
	abstract String getFileName(Recording r);
	abstract File getFile(Recording r);
	
	private Recording getRecording(Long id) {
		Recording r = getBean(RecordingDao.class).get(id);
		// TODO should we process public?
		// || r.getOwnerId() == 0 || r.getParentFileExplorerItemId() == null || r.getParentFileExplorerItemId() == 0
		if (r == null) {
			return r;
		}
		if (r.getOwnerId() == null || getUserId() == r.getOwnerId()) {
			return r;
		}
		if (r.getGroupId() == null || getBean(GroupUserDao.class).isUserInGroup(r.getGroupId(), getUserId())) {
			return r;
		}
		//TODO external group check was added for plugin recording download
		String extType = getExternalType();
		if (extType != null) {
			User creator = getBean(UserDao.class).get(r.getInsertedBy());
			if (extType.equals(creator.getExternalType())) {
				return r;
			}
		}
		return null;
	}
	
	private Recording getRecording(Attributes attributes) {
		PageParameters params = attributes.getParameters();
		StringValue _id = params.get("id");
		Long id = null;
		try {
			id = _id.toOptionalLong();
		} catch (NumberFormatException e) {
			//no-op expected
		}
		WebSession ws = WebSession.get();
		if (id != null && ws.isSignedIn()) {
			return getRecording(id);
		} else {
			ws.invalidate();
			if (ws.signIn(_id.toString(), true)) {
				return getRecording(getRecordingId());
			}
		}
		return null;
	}
}
