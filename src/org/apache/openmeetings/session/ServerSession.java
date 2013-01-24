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

import org.apache.openmeetings.persistence.beans.basic.Server;
import org.apache.openmeetings.persistence.beans.rooms.Client;



/**
 * 
 * Session object including the server info
 * 
 * So in this object you can store meta information that the client does not
 * need to know. This is handy because the {@link Client} object otherwise gets too
 * big.
 * 
 * For example the {@link Server} can be referenced here.
 * 
 * @author sebawagner
 * 
 */
public class ServerSession {

	/**
	 * if null, the connection is handled on the master, otherwise the
	 * connection is handled via a slave of the cluster
	 */
	private Long server;

	private Client roomClient;

	public ServerSession(Long server, Client roomClient) {
		super();
		this.server = server;
		this.roomClient = roomClient;
	}

	/**
	 * @see ServerSession#server
	 */
	public Long getServer() {
		return server;
	}

	public void setServer(Long server) {
		this.server = server;
	}

	public Client getRoomClient() {
		return roomClient;
	}

	public void setRoomClient(Client roomClient) {
		this.roomClient = roomClient;
	}

}
