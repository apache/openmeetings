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
package org.apache.openmeetings.web.user.record;

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;
import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.WebSession.getExternalType;
import static org.apache.openmeetings.web.app.WebSession.getRecordingId;
import static org.apache.openmeetings.web.app.WebSession.getUserId;
import static org.red5.logging.Red5LoggerFactory.getLogger;

import org.apache.openmeetings.db.dao.record.RecordingDao;
import org.apache.openmeetings.db.dao.user.GroupUserDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.file.FileItem.Type;
import org.apache.openmeetings.db.entity.record.Recording;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.util.FileItemResourceReference;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.IResource.Attributes;
import org.apache.wicket.util.string.StringValue;
import org.slf4j.Logger;

public abstract class RecordingResourceReference extends FileItemResourceReference<Recording> {
	private static final long serialVersionUID = 1L;
	private static final Logger log = getLogger(RecordingResourceReference.class, webAppRootKey);

	public RecordingResourceReference(String name) {
		super(name);
	}

	@Override
	public String getMimeType(Recording r) {
		return getMimeType();
	}
	
	public abstract String getMimeType();
	
	@Override
	protected Recording getFileItem(Attributes attributes) {
		PageParameters params = attributes.getParameters();
		StringValue _id = params.get("id");
		Long id = null;
		try {
			id = _id.toOptionalLong();
		} catch (Exception e) {
			//no-op expected
		}
		WebSession ws = WebSession.get();
		if (id == null && ws.signIn(_id.toString(), true)) {
			id = getRecordingId();
		}
		if (id != null && ws.isSignedIn()) {
			return getRecording(id);
		}
		return null;
	}
	
	private static Recording getRecording(Long id) {
		log.debug("Recording with id {} is requested", id);
		Recording r = getBean(RecordingDao.class).get(id);
		if (r == null || r.getType() == Type.Folder || r.isDeleted()) {
			return null;
		}
		if (id.equals(getRecordingId())) {
			return r;
		}
		//TODO should we check parentId here
		if (r.getOwnerId() == null && r.getGroupId() == null) {
			//public
			return r;
		}
		if (r.getOwnerId() != null && getUserId().equals(r.getOwnerId())) {
			//own
			return r;
		}
		if (r.getGroupId() != null && getBean(GroupUserDao.class).isUserInGroup(r.getGroupId(), getUserId())) {
			return r;
		}
		//external group check was added for plugin recording download
		String extType = getExternalType();
		if (extType != null) {
			User creator = getBean(UserDao.class).get(r.getInsertedBy());
			if (extType.equals(creator.getExternalType())) {
				return r;
			}
		}
		return null;
	}
}
