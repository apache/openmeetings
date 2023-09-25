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

import static org.apache.openmeetings.db.util.AuthLevelUtil.hasAdminLevel;
import static org.apache.openmeetings.web.app.WebSession.getRights;
import static org.apache.openmeetings.web.app.WebSession.getUserId;
import static org.apache.openmeetings.util.OmFileHelper.PNG_MIME_TYPE;
import static org.apache.openmeetings.util.OmFileHelper.getGroupLogo;

import java.io.File;
import java.io.IOException;

import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.dao.user.GroupUserDao;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.room.RoomGroup;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.resource.FileSystemResource;
import org.apache.wicket.resource.FileSystemResourceReference;

import org.apache.wicket.util.string.StringValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GroupLogoResourceReference extends FileSystemResourceReference {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(GroupLogoResourceReference.class);

	@Inject
	private GroupUserDao groupUserDao;
	@Inject
	private RoomDao roomDao;

	public GroupLogoResourceReference() {
		super(GroupLogoResourceReference.class, "grouplogo");
		Injector.get().inject(this);
	}

	@Override
	public IResource getResource() {
		return new FileSystemResource() {
			private static final long serialVersionUID = 1L;

			@Override
			protected String getMimeType() throws IOException {
				return PNG_MIME_TYPE;
			}

			@Override
			protected ResourceResponse newResourceResponse(Attributes attrs) {
				Long id = null;
				boolean allowed = false;
				WebSession ws = WebSession.get();
				if (ws.isSignedIn()) {
					PageParameters params = attrs.getParameters();
					StringValue inId = params.get("id");
					try {
						id = inId.toOptionalLong();
					} catch (Exception e) {
						//no-op expected
					}
					allowed = id == null || hasAdminLevel(getRights()) || null != groupUserDao.getByGroupAndUser(id, getUserId());
					if (!allowed && ws.getInvitation() != null) {
						Room r = ws.getInvitation().getRoom() == null ? null : roomDao.get(ws.getInvitation().getRoom().getId());
						if (r != null && r.getGroups() != null) {
							for (RoomGroup rg : r.getGroups()) {
								if (rg.getGroup().getId().equals(id)) {
									allowed = true;
									break;
								}
							}
						}
					}
				}
				if (allowed) {
					return createResourceResponse(attrs, getGroupLogo(id, true).toPath());
				} else {
					log.debug("Not authorized");
					ResourceResponse rr = new ResourceResponse();
					rr.setError(HttpServletResponse.SC_FORBIDDEN);
					return rr;
				}
			}
		};
	}

	public static String getUrl(RequestCycle rc, Long groupId) {
		PageParameters pp = new PageParameters();
		if (groupId != null) {
			pp.add("id", groupId);
		}
		File img = getGroupLogo(groupId, true);
		return rc.urlFor(new GroupLogoResourceReference(), pp.add("anticache", img.lastModified())).toString();
	}

}
