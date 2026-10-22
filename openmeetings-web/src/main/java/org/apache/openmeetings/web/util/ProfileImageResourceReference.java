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

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.HashSet;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;

public class ProfileImageResourceReference extends ResourceReference {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(ProfileImageResourceReference.class);
	private static final Set<String> ALLOWED_SCHEMAS = new HashSet<>();
	private static final URI EMPTY_URI = URI.create("");
	@Inject
	private UserDao userDao;

	static {
		Properties props = new Properties();
		try (final InputStream is = ProfileImageResourceReference.class.getResourceAsStream("/openmeetings.properties")) {
			props.load(is);
		} catch (Exception e) {
			log.error("Failed to load openmeetings.properties {}", e.getMessage());
		}
		String[] schemas = props.getProperty("valid.avatar.schemas", "https").split(",");
		for (String schema : schemas) {
			ALLOWED_SCHEMAS.add(schema);
		}
	}

	public ProfileImageResourceReference() {
		super(ProfileImageResourceReference.class, "profile");
		Injector.get().inject(this);
	}

	public String getUrl(RequestCycle rc, Long userId) {
		return getUrl(rc, userDao.get(userId));
	}

	public static PageParameters getImgParams(User u) {
		return new PageParameters()
				.add("id", u.getId())
				.add("anticache", Optional.ofNullable(u.getUpdated())
						.orElse(u.getInserted()).getTime());
	}

	public static String getUrl(RequestCycle rc, User u) {
		URI uri = sanitize(u);
		return !uri.isAbsolute()
			? rc.urlFor(new ProfileImageResourceReference(), getImgParams(u)).toString()
			: uri.toString();
	}

	//package private for testing
	static URI sanitize(User u) {
		URI uri = EMPTY_URI;
		try {
			uri = u == null ? EMPTY_URI : URI.create(u.getPictureUri());
			if (!uri.isAbsolute()) {
				return uri;
			}
			if (!ALLOWED_SCHEMAS.contains(uri.getScheme())) {
				return EMPTY_URI;
			}
			//let's check if we can construct URL out of this
			URL url = uri.toURL();
			uri = url.toURI().normalize();
		} catch (Exception e) {
			uri = EMPTY_URI;
		}
		return uri;
	}

	@Override
	public IResource getResource() {
		return new ByteArrayResource(PNG_MIME_TYPE) {
			private static final long serialVersionUID = 1L;
			private Long userId = null;
			private URI uri = null;

			@Override
			protected ResourceResponse newResourceResponse(Attributes attributes) {
				ResourceResponse rr;
				if (WebSession.get().isSignedIn()) {
					PageParameters params = attributes.getParameters();
					try {
						userId = params.get("id").toOptionalLong();
						uri = sanitize(userDao.get(userId));
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
				if (!uri.isAbsolute()) {
					File img = OmFileHelper.getUserProfilePicture(userId, uri.toString());
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
