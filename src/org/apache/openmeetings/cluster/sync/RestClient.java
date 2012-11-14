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
package org.apache.openmeetings.cluster.sync;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.conference.room.RoomClient;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class RestClient {

	private static final Logger log = Red5LoggerFactory.getLogger(
			RestClient.class, OpenmeetingsVariables.webAppRootKey);

	private boolean loginSuccess = false;
	private String sessionId;

	private final String host;
	private final int port;
	private final String protocol;
	private final String webapp;
	private final String user;
	private final String pass;

	private String getUserServiceEndPoint() {
		return protocol + "://" + host + ":" + port + "/" + webapp
				+ "/services/UserService";
	}

	private String getServerServiceEndPoint() {
		return protocol + "://" + host + ":" + port + "/" + webapp
				+ "/services/ServerService";
	}

	public static void main(String... strings) {
		RestClient rClient = new RestClient("127.0.0.1", 5080, "http",
				"openmeetings", "swagner", "qweqwe");
		try {
			rClient.loginUser();
			rClient.ping();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public RestClient(String host, int port, String protocol, String webapp,
			String user, String pass) {
		super();
		this.host = host;
		this.port = port;
		this.protocol = protocol;
		this.webapp = webapp;
		this.user = user;
		this.pass = pass;
	}

	/**
	 * Login the user via REST
	 * 
	 * @throws Exception
	 */
	public void loginUser() throws Exception {
		Options options = new Options();
		options.setTo(new EndpointReference(getUserServiceEndPoint()));

		options.setProperty(Constants.Configuration.ENABLE_REST,
				Constants.VALUE_TRUE);

		ServiceClient sender = new ServiceClient();
		sender.engageModule(new QName(Constants.MODULE_ADDRESSING)
				.getLocalPart());
		sender.setOptions(options);
		OMElement getSessionResult = sender
				.sendReceive(getPayloadMethodGetSession());
		sessionId = getSessionIdFromResult(getSessionResult);
		log.debug("sessionId:: " + sessionId);

		OMElement loginUserResult = sender
				.sendReceive(getPayloadMethodLoginUser());

		log.debug("loginUserResult:: " + loginUserResult);

		loginSuccess = loginSuccessFromResult(loginUserResult);

		log.debug("loginSuccess:: " + loginSuccess);

	}

	/**
	 * verifies if the user is logged in, if yes, it will try to load the
	 * current list of sessions from the slave
	 * 
	 * @throws Exception
	 */
	public void ping() throws Exception {
		if (!loginSuccess) {
			loginUser();
		} else {
			Options options = new Options();
			options.setTo(new EndpointReference(getServerServiceEndPoint()));

			options.setProperty(Constants.Configuration.ENABLE_REST,
					Constants.VALUE_TRUE);

			ServiceClient sender = new ServiceClient();
			sender.engageModule(new QName(Constants.MODULE_ADDRESSING)
					.getLocalPart());
			sender.setOptions(options);

			OMElement pingResult = sender
					.sendReceive(getPayloadMethodPingTemp());

			log.debug("pingResult:: " + pingResult);

			pingFromResult(pingResult);
		}
	}

	/**
	 * Create the REST request to get a new session Id
	 * 
	 * @return
	 */
	private OMElement getPayloadMethodGetSession() {
		OMFactory fac = OMAbstractFactory.getOMFactory();
		OMNamespace omNs = fac.createOMNamespace(
				"http://services.axis.openmeetings.apache.org", "pre");
		OMElement method = fac.createOMElement("getSession", omNs);
		return method;
	}

	/**
	 * Parse the session Id from the REST request
	 * 
	 * @param result
	 * @return
	 * @throws Exception
	 */
	private String getSessionIdFromResult(OMElement result) throws Exception {
		QName sessionElements = new QName(null, "session_id");

		@SuppressWarnings("unchecked")
		Iterator<OMElement> elements = result.getFirstElement()
				.getChildrenWithName(sessionElements);
		if (elements.hasNext()) {
			OMElement sessionElement = elements.next();
			return sessionElement.getText();
		} else {
			throw new Exception("Could not find session id");
		}
	}

	/**
	 * create the payload to login to another openmeetings instance via REST
	 * 
	 * @return
	 */
	private OMElement getPayloadMethodLoginUser() {
		OMFactory fac = OMAbstractFactory.getOMFactory();
		OMNamespace omNs = fac.createOMNamespace(
				"http://services.axis.openmeetings.apache.org", "pre");
		OMElement method = fac.createOMElement("loginUser", omNs);

		OMElement sid = fac.createOMElement("SID", omNs);
		sid.addChild(fac.createOMText(sid, sessionId));
		method.addChild(sid);

		OMElement username = fac.createOMElement("username", omNs);
		username.addChild(fac.createOMText(username, user));
		method.addChild(username);

		OMElement userpass = fac.createOMElement("userpass", omNs);
		userpass.addChild(fac.createOMText(userpass, pass));
		method.addChild(userpass);

		return method;
	}

	/**
	 * check the result of the REST request if the login was successful
	 * 
	 * @param result
	 * @return
	 * @throws Exception
	 */
	private boolean loginSuccessFromResult(OMElement result) throws Exception {

		QName loginResult = new QName(
				"http://services.axis.openmeetings.apache.org", "return");

		@SuppressWarnings("unchecked")
		Iterator<OMElement> elements = result.getChildrenWithName(loginResult);
		if (elements.hasNext()) {
			OMElement resultElement = elements.next();
			if (resultElement.getText().equals("1")) {
				return true;
			} else {
				throw new Exception("Could not login user at, error code is: "
								+ resultElement.getText());
			}
		} else {
			throw new Exception("Could not parse login result");
		}

	}

	/**
	 * Create the REST request for the ping method to load the users
	 * 
	 * @return
	 */
	private OMElement getPayloadMethodPingTemp() {
		OMFactory fac = OMAbstractFactory.getOMFactory();
		OMNamespace omNs = fac.createOMNamespace(
				"http://services.axis.openmeetings.apache.org", "pre");
		OMElement method = fac.createOMElement("ping", omNs);

		OMElement sid = fac.createOMElement("SID", omNs);
		sid.addChild(fac.createOMText(sid, sessionId));
		method.addChild(sid);

		return method;
	}

	/**
	 * Parses the result of the rest request and returns a list of
	 * {@link RoomClient}s
	 * 
	 * @param result
	 *            the result of the REST request
	 * @return list of {@link RoomClient}s
	 * @throws Exception
	 */
	private List<RoomClient> pingFromResult(OMElement result) throws Exception {

		QName pingResult = new QName(
				"http://services.axis.openmeetings.apache.org", "return");
		String nameSpaceForSlaveDto = "http://room.conference.openmeetings.apache.org/xsd";

		@SuppressWarnings("unchecked")
		Iterator<OMElement> elements = result.getChildrenWithName(pingResult);
		List<RoomClient> clients = new ArrayList<RoomClient>();
		while (elements.hasNext()) {
			OMElement resultElement = elements.next();
			clients.add(new RoomClient( //
				resultElement.getFirstChildWithName(new QName(nameSpaceForSlaveDto, "streamid")).getText(), //
				resultElement.getFirstChildWithName(new QName(nameSpaceForSlaveDto, "publicSID")).getText(), //
				Long.valueOf(resultElement.getFirstChildWithName(new QName(nameSpaceForSlaveDto, "roomId")).getText()).longValue(), //
				Long.valueOf(resultElement.getFirstChildWithName(new QName(nameSpaceForSlaveDto, "userId")).getText()).longValue(), //
				resultElement.getFirstChildWithName(new QName(nameSpaceForSlaveDto, "firstName")).getText(), //
				resultElement.getFirstChildWithName(new QName(nameSpaceForSlaveDto, "lastName")).getText()) //
			);
		}
		return clients;
	}

}
