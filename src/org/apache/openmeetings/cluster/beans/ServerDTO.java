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
package org.apache.openmeetings.cluster.beans;

import org.apache.openmeetings.persistence.beans.basic.Server;

/**
 * 
 * Bean send to the client about the server he is going to use for the conference 
 * session
 * 
 * @author sebawagner
 *
 */
public class ServerDTO {

	private Long id;
	private String address;
	private int port;
	private String protocol;
	private String webapp;

	public ServerDTO(Server server) {
		if (server == null) {
			return;
		}
		id = server.getId();
		address = server.getAddress();
		port = server.getPort();
		protocol = server.getProtocol();
		webapp = server.getWebapp();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getWebapp() {
		return webapp;
	}

	public void setWebapp(String webapp) {
		this.webapp = webapp;
	}
	
	@Override
	public String toString() {
		return "id "+id+" address "+address+" port "+port+" protocol "+protocol;
	}

}
