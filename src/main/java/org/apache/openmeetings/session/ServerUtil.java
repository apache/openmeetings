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
package org.apache.openmeetings.session;

import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.data.basic.dao.ServerDao;
import org.apache.openmeetings.persistence.beans.basic.Server;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Configures the current reference to the {@link Server} for this Tomcat/Web-Container.
 * 
 * It is a Spring Bean and configured as Singleton. There is only one instance of this 
 * bean in the entire Web-Application.  
 * 
 * @author sebawagner
 *
 */
public class ServerUtil {
	protected static final Logger log = Red5LoggerFactory.getLogger(
			ServerUtil.class, OpenmeetingsVariables.webAppRootKey);

	@Autowired
	private ServerDao serverDao;
	
	/**
	 * Injected via Spring configuration
	 * 
	 * for cluster configuration, if only using one server (no cluster), serverId is null,
	 * this is the current serverId of this Tomcat instance (null means no cluster is configured)
	 */
	private String serverId = null;
	
	/**
	 * a reference of the current server in that Tomcat instance
	 */
	private Server currentServer;
	
	public String getServerId() {
		return serverId;
	}
	
	public void setServerId(String serverId) {
		this.serverId = serverId;
	}
	
	/**
	 * 
	 * @return the current server
	 */
	public Server getCurrentServer() {
		if (serverId == null) {
			return null;
		}
		if (currentServer != null && serverId.equals(currentServer.getId().toString())) {
			return currentServer;
		}
		currentServer = serverDao.get(Long.parseLong(serverId));
		if (currentServer == null) {
			log.warn("You have configured a serverId that does not exist in your list of servers, serverId: "+serverId);
		}
		return currentServer;
	}
}
