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
package org.apache.openmeetings.web.common;

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;
import static org.apache.openmeetings.web.app.Application.getBean;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.util.OmFileHelper;
import org.apache.openmeetings.web.app.Application;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.TransparentWebMarkupContainer;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.request.resource.ByteArrayResource;
import org.apache.wicket.util.io.IOUtils;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class ProfileImagePanel extends BasePanel {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Red5LoggerFactory.getLogger(ProfileImagePanel.class, webAppRootKey);
	protected final WebMarkupContainer profile;
	
	public ProfileImagePanel(String id, final long userId) {
		super(id);
		
		profile = new TransparentWebMarkupContainer("profile");
		String uri = getBean(UserDao.class).get(userId).getPictureuri();
		boolean absolute = false;
		try {
			absolute = URI.create(uri).isAbsolute();
		} catch (Exception e) {
			//no-op
		}
		if (absolute) {
			profile.add(new Image("img", Application.getString(5L)).add(AttributeModifier.replace("src", uri)));
		} else {
			profile.add(new Image("img", new ByteArrayResource("image/jpeg") {
				private static final long serialVersionUID = 1L;

				@Override
				protected ResourceResponse newResourceResponse(Attributes attributes) {
					ResourceResponse rr = super.newResourceResponse(attributes);
					rr.disableCaching();
					return rr;
				}
				
				@Override
				protected byte[] getData(Attributes attributes) {
					String uri = getBean(UserDao.class).get(userId).getPictureuri();
					File img = OmFileHelper.getUserProfilePicture(userId, uri);
					InputStream is = null;
					try {
						is = new FileInputStream(img);
						return IOUtils.toByteArray(is);
					} catch (Exception e) {
						log.error("failed to get bytes from image", e);
					} finally {
						if (is != null) {
							try {
								is.close();
							} catch (IOException e) {}
						}
					}
					return null;
				}
			}));
		}
		add(profile.setOutputMarkupId(true));
	}
}
