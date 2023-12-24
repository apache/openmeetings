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

import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.util.OmFileHelper;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.ByteArrayResource;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceReference;

import org.apache.wicket.util.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletResponse;

import static org.apache.openmeetings.util.OmFileHelper.PNG_MIME_TYPE;
import static org.apache.openmeetings.util.OmFileHelper.SIP_USER_ID;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;

public class ProfileImageResourceReference extends ResourceReference {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(ProfileImageResourceReference.class);
	@Inject
	private UserDao userDao;

	public ProfileImageResourceReference() {
		super(ProfileImageResourceReference.class, "profile");
		Injector.get().inject(this);
	}

	public String getUrl(RequestCycle rc, Long userId) {
		return getUrl(rc, userDao.get(userId));
	}

	public static String getUrl(RequestCycle rc, User u) {
		String uri = u.getPictureUri();
		if (isRelative(uri)) {
			File img = OmFileHelper.getUserProfilePicture(u.getId(), uri);
			uri = rc.urlFor(new ProfileImageResourceReference()
					, new PageParameters().add("id", u.getId()).add("anticache", img.lastModified())).toString();
		}
		return uri;
	}

	private static boolean isRelative(String uri) {
		boolean relative = true;
		try {
			relative = !URI.create(uri).isAbsolute();
		} catch (Exception e) {
			//no-op
		}
		return relative;
	}

	@Override
	public IResource getResource() {
		return new ByteArrayResource(PNG_MIME_TYPE) {
			private static final long serialVersionUID = 1L;
			private Long userId = null;
			private String uri = null;

			@Override
			protected ResourceResponse newResourceResponse(Attributes attributes) {
				ResourceResponse rr;
				if (WebSession.get().isSignedIn()) {
					PageParameters params = attributes.getParameters();
					try {
						userId = params.get("id").toOptionalLong();
						uri = SIP_USER_ID.equals(userId) ? null : userDao.get(userId).getPictureUri();
					} catch (Exception e) {
						// no-op, junk filter
					}
					rr = super.newResourceResponse(attributes);
					rr.disableCaching();
				} else {
					log.debug("Not authorized");
					rr = new ResourceResponse();
					rr.setError(HttpServletResponse.SC_FORBIDDEN);
				}
				return rr;
			}

			@Override
			protected byte[] getData(Attributes attributes) {
				if (isRelative(uri)) {
					File img = OmFileHelper.getUserProfilePicture(userId, uri);
					try (InputStream is = new FileInputStream(img)) {
						return IOUtils.toByteArray(is);
					} catch (Exception e) {
						log.error("failed to get bytes from image", e);
					}
				}
				return null;
			}
		};
	}
}
