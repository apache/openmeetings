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
package org.apache.openmeetings;

import java.util.Locale;
import java.util.Set;
import java.util.function.Supplier;

import jakarta.annotation.Nonnull;
import jakarta.servlet.ServletContext;

import org.apache.openmeetings.db.entity.room.Invitation;
import org.apache.openmeetings.util.ws.IClusterWsMessage;
import org.apache.wicket.request.IExceptionMapper;
import org.apache.wicket.request.IRequestMapper;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public interface IApplication {
	<T> T getBean(Class<T> clazz);
	ServletContext getServletContext();
	IRequestMapper getRootRequestMapper();
	Supplier<IExceptionMapper> getExceptionMapperProvider();
	String getOmString(String key);
	String getOmString(String key, long languageId);
	String getOmString(String key, final Locale loc, String... params);
	String getOmContactsLink();
	String getOmInvitationLink(Invitation i, String baseUrl);
	String urlForActivatePage(PageParameters pp);

	String getServerId();

	//JPA
	void updateJpaAddresses();

	//WS
	void publishWsTopic(@Nonnull IClusterWsMessage msg);
	Set<String> getWsUrls();

	void updateTheme();
}
