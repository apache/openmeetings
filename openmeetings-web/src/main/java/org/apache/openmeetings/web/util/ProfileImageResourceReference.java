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

import static org.apache.openmeetings.util.OmFileHelper.PNG_MIME_TYPE;
import static org.apache.openmeetings.util.OmFileHelper.SIP_USER_ID;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getWebAppRootKey;
import static org.apache.openmeetings.web.app.Application.getBean;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;

import javax.servlet.http.HttpServletResponse;

import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.util.OmFileHelper;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.ByteArrayResource;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.util.io.IOUtils;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class ProfileImageResourceReference extends ResourceReference {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Red5LoggerFactory.getLogger(ProfileImageResourceReference.class, getWebAppRootKey());

	public ProfileImageResourceReference() {
		super(ProfileImageResourceReference.class, "profile");
	}

	public static String getUrl(RequestCycle rc, Long userId) {
		return getUrl(rc, getBean(UserDao.class).get(userId));
	}

	public static String getUrl(RequestCycle rc, User u) {
		String uri = u.getPictureUri();
		if (!isAbsolute(uri)) {
			File img = OmFileHelper.getUserProfilePicture(u.getId(), uri);
			uri = rc.urlFor(new ProfileImageResourceReference()
					, new PageParameters().add("id", u.getId()).add("anticache", img.lastModified())).toString();
		}
		return uri;
	}

	private static boolean isAbsolute(String uri) {
		boolean absolute = false;
		try {
			absolute = URI.create(uri).isAbsolute();
		} catch (Exception e) {
			//no-op
		}
		return absolute;
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
						uri = SIP_USER_ID.equals(userId) ? null : getBean(UserDao.class).get(userId).getPictureUri();
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
				if (!isAbsolute(uri)) {
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
