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
package org.apache.openmeetings.webservice.cluster;

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;
import static org.apache.openmeetings.webservice.Constants.TNS;
import static org.apache.openmeetings.webservice.Constants.USER_SERVICE_NAME;
import static org.apache.openmeetings.webservice.Constants.USER_SERVICE_PORT_NAME;

import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import org.apache.openmeetings.db.dto.basic.ServiceResult;
import org.apache.openmeetings.db.entity.server.Server;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

/**
 * 
 * Performs call to the WebService Gateway to load the server load from the
 * slave's of the cluster
 * 
 * @author sebawagner
 * 
 */
public class RestClient {
	private static final Logger log = Red5LoggerFactory.getLogger(RestClient.class, webAppRootKey);
	private final static QName USER_SERVICE_QNAME = new QName(TNS, USER_SERVICE_NAME);
	private final static QName USER_SERVICE_PORT_QNAME = new QName(TNS, USER_SERVICE_PORT_NAME);
	
	private enum Action {
		//kick the user from the server
		KICK_USER,
	}

	private final String host;
	private final int port;
	private final String protocol;
	private final String webapp;
	private final String user;
	private final String pass;

	private boolean loginSuccess = false;
	private String sessionId;
	
	private String publicSID;

	private String getUserServiceWsdl() {
		return String.format("%s://%s:%s/%s/services/UserService?wsdl", protocol, host, port, webapp);
	}

	/**
	 * The observerInstance will be notified whenever a ping was completed
	 * 
	 * @param observerInstance
	 * @param host
	 * @param port
	 * @param protocol
	 * @param webapp
	 * @param user
	 * @param pass
	 */
	public RestClient(Server server) {
		this.host = server.getAddress();
		this.port = server.getPort();
		this.protocol = server.getProtocol();
		this.webapp = server.getWebapp();
		this.user = server.getUser();
		this.pass = server.getPass();
	}
	
	/**
	 * Main method to perform tests
	 * 
	 * @param strings
	 */
	public static void main(String... strings) {
		RestClient rClient = new RestClient("127.0.0.1", 5080, "http", "openmeetings", "admin", "12345");
		try {
			rClient.loginUser(Action.KICK_USER);
		} catch (Exception e) {
			log.error("Error", e);
		}
	}

	/**
	 * for simple testing this method provides a version of the constructor
	 * without need for a server entity.<br/>
	 * <br/>
	 * There is no spring or JPA enhanced class in use here. This Object is
	 * stored in session/memory
	 * 
	 * @param host
	 * @param port
	 * @param protocol
	 * @param webapp
	 * @param user
	 * @param pass
	 */
	private RestClient(String host, int port, String protocol, String webapp, String user, String pass) {
		this.host = host;
		this.port = port;
		this.protocol = protocol;
		this.webapp = webapp;
		this.user = user;
		this.pass = pass;
	}

	/**
	 * compare if the details here and the one stored are still the same
	 * 
	 * @param server2
	 * @return
	 */
	public boolean hasServerDetailsChanged(Server server2) {
		return !host.equals(server2.getAddress()) 
				|| port != server2.getPort() 
				|| !user.equals(server2.getUser())
				|| !pass.equals(server2.getPass())
				|| !webapp.equals(server2.getWebapp())
				|| !protocol.equals(server2.getProtocol());
	}
	
	private UserService getUserClient() throws Exception {
		URL wsdlURL = new URL(getUserServiceWsdl());
		Service service = Service.create(wsdlURL, USER_SERVICE_QNAME);
		return service.getPort(USER_SERVICE_PORT_QNAME, UserService.class);
	}
	
	/**
	 * Login the user via REST
	 * 
	 * @throws Exception
	 */
	public void loginUser(Action action) throws Exception {
		UserService client = getUserClient();
		
		ServiceResult result = client.login(user, pass);

		loginSuccess = result.getCode() > 0;

		switch (action) {
			case KICK_USER:
				kickUserInternl();
				break;
			default:
				throw new Exception("No action defined");
		}
	}
	
	/**
	 * sets the publicSID and removes a user from a slave host by calling a REST service
	 * 
	 * @param publicSID
	 */
	public void kickUser(String publicSID) {
		this.publicSID = publicSID;
		kickUserInternl();
	}
	
	private void kickUserInternl() {
		try {
			if (!loginSuccess) {
				loginUser(Action.KICK_USER);
			}

			UserService client = getUserClient();
			ServiceResult result = client.kick(sessionId, publicSID);

			if (result.getCode() == 0) {
				throw new Exception("Could not delete user from slave host");
			}

		} catch (Exception err) {
			log.error("[kickUser failed]", err);
		}
	}
}
