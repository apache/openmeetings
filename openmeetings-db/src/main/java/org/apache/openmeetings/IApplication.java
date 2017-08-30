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

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import javax.servlet.ServletContext;

import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dto.room.Whiteboards;
import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.db.entity.basic.IClient;
import org.apache.openmeetings.db.entity.room.Invitation;
import org.apache.openmeetings.db.entity.room.StreamClient;
import org.apache.openmeetings.util.ws.IClusterWsMessage;
import org.apache.wicket.request.IExceptionMapper;
import org.apache.wicket.request.IRequestMapper;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public interface IApplication {
	<T> T getOmBean(Class<T> clazz);
	<T> T _getOmBean(Class<T> clazz);
	ServletContext getServletContext();
	IRequestMapper getRootRequestMapper();
	Supplier<IExceptionMapper> getExceptionMapperProvider();
	String getOmString(String key);
	String getOmString(String key, long languageId);
	String getOmString(String key, final Locale loc, String... params);
	String getOmContactsLink();
	String getOmInvitationLink(Invitation i);
	String urlForActivatePage(PageParameters pp);
	void invalidateClient(Long userId, String sessionId);
	void exit(String uid);
	void setXFrameOptions(String xFrameOptions);
	void setContentSecurityPolicy(String contentSecurityPolicy);

	IClient update(IClient c);

	// web client
	Client getOmClient(String uid);
	Client getOmClientBySid(String sid);
	Client getOmOnlineClient(String uid);
	List<Client> getOmRoomClients(Long roomId);
	List<Client> getOmClients(Long userId);

	// stream client
	StreamClient updateClient(StreamClient rcl, boolean forceSize);
	String getServerId();
	Map<String, StreamClient> getStreamClients();
	Set<Long> getActiveRoomIds();

	//JPA
	void updateJpaAddresses(ConfigurationDao dao);

	//WS
	void publishWsTopic(IClusterWsMessage msg);

	Map<Long, Whiteboards> getWhiteboards();
}
