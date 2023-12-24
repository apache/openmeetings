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

import static org.apache.openmeetings.web.app.WebSession.getRecordingId;
import static org.apache.openmeetings.web.app.WebSession.getUserId;

import org.apache.openmeetings.db.dao.record.RecordingDao;
import org.apache.openmeetings.db.dao.user.GroupUserDao;
import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.db.entity.file.BaseFileItem.Type;
import org.apache.openmeetings.db.entity.record.Recording;
import org.apache.openmeetings.web.app.ClientManager;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.util.FileItemResourceReference;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.IResource.Attributes;

import org.apache.wicket.util.string.StringValue;

import jakarta.inject.Inject;

public abstract class RecordingResourceReference extends FileItemResourceReference<Recording> {
	private static final long serialVersionUID = 1L;

	@Inject
	private RecordingDao recDao;
	@Inject
	private ClientManager cm;
	@Inject
	private GroupUserDao groupUserDao;

	protected RecordingResourceReference(String name) {
		super(name);
		Injector.get().inject(this);
	}

	@Override
	public String getMimeType(Recording r) {
		return getMimeType();
	}

	public abstract String getMimeType();

	@Override
	protected Recording getFileItem(Attributes attributes) {
		PageParameters params = attributes.getParameters();
		StringValue inId = params.get("id");
		String ruid = params.get("ruid").toString();
		String uid = params.get("uid").toString();
		String wbItemId = params.get("wuid").toString();
		Long id = null;
		try {
			id = inId.toOptionalLong();
		} catch (Exception e) {
			//no-op expected
		}
		WebSession ws = WebSession.get();
		if (id == null && ws.signIn(inId.toString(), true)) {
			id = getRecordingId();
		}
		if (id != null && ws.isSignedIn()) {
			return getRecording(id, ruid, uid, wbItemId);
		}
		return null;
	}

	private Recording getRecording(Long id, String ruid, String uid, String wbItemId) {
		log.debug("Recording with id {} is requested", id);
		Recording r = recDao.get(id);
		if (r == null || r.getType() == Type.FOLDER || r.isDeleted()) {
			return null;
		}
		if (id.equals(getRecordingId())) {
			return r;
		}
		Client c = cm.get(uid);
		if (isAtWb(c, ruid, wbItemId, r.getId())) {
			return r; // item IS on WB
		}
		if (r.getOwnerId() == null && r.getGroupId() == null) {
			//public
			return r;
		}
		if (r.getOwnerId() != null && getUserId().equals(r.getOwnerId())) {
			//own
			return r;
		}
		if (r.getGroupId() != null && groupUserDao.isUserInGroup(r.getGroupId(), getUserId())) {
			return r;
		}
		return null;
	}
}
