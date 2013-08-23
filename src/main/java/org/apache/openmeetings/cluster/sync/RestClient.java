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

import java.util.Iterator;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.persistence.beans.basic.Server;
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

	private static final Logger log = Red5LoggerFactory.getLogger(
			RestClient.class, OpenmeetingsVariables.webAppRootKey);
	
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

//	private static String nameSpaceForSlaveDto = "http://room.conference.openmeetings.apache.org/xsd";
	
	private static String NAMESPACE_PREFIX = "http://services.axis.openmeetings.apache.org";

	private String getUserServiceEndPoint() {
		return protocol + "://" + host + ":" + port + "/" + webapp
				+ "/services/UserService";
	}
//
//	private String getRoomServiceEndPoint() {
//		return protocol + "://" + host + ":" + port + "/" + webapp
//				+ "/services/RoomService";
//	}

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
		//this.server = server;
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
		RestClient rClient = new RestClient("127.0.0.1", 5080, "http",
				"openmeetings", "swagner", "qweqwe");
		try {
			rClient.loginUser(Action.KICK_USER);
		} catch (Exception e) {
			e.printStackTrace();
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
	private RestClient(String host, int port, String protocol, String webapp,
			String user, String pass) {
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

		if (!host.equals(server2.getAddress())) {
			return true;
		}
		if (port != server2.getPort()) {
			return true;
		}
		if (!user.equals(server2.getUser())) {
			return true;
		}
		if (!pass.equals(server2.getPass())) {
			return true;
		}
		if (!webapp.equals(server2.getWebapp())) {
			return true;
		}
		if (!protocol.equals(server2.getProtocol())) {
			return true;
		}

		return false;
	}

	/**
	 * Login the user via REST
	 * 
	 * @throws Exception
	 */
	public void loginUser(Action action) throws Exception {

		ServiceClient sender = createServiceClient(getUserServiceEndPoint());
		
		OMElement getSessionResult = sender
				.sendReceive(getPayloadMethodGetSession());
		sessionId = getSessionIdFromResult(getSessionResult);

		OMElement loginUserResult = sender
				.sendReceive(getPayloadMethodLoginUser());

		loginSuccess = loginSuccessFromResult(loginUserResult);

		switch (action) {
			case KICK_USER:
				kickUserInternl();
				break;
			default:
				throw new Exception("No action defined");
		}

	}
	
	private ServiceClient createServiceClient(String serviceEndPoint) throws Exception {
		ServiceClient sender = new ServiceClient();
		sender.engageModule(new QName(Constants.MODULE_ADDRESSING)
				.getLocalPart());
		Options options = new Options();
		options.setTo(new EndpointReference(serviceEndPoint));
		options.setProperty(Constants.Configuration.ENABLE_REST,
				Constants.VALUE_TRUE);
		int timeOutInMilliSeconds = 2000;
		// setting timeout to 2 second should be sufficient, if the server is
		// not available within the 3 second interval you got a problem anyway
		options.setTimeOutInMilliSeconds(timeOutInMilliSeconds);
		options.setProperty(HTTPConstants.SO_TIMEOUT, timeOutInMilliSeconds);
		options.setProperty(HTTPConstants.CONNECTION_TIMEOUT, timeOutInMilliSeconds);
		sender.setOptions(options);
		
		return sender;
	}
	
	private OMElement createOMElement(OMFactory fac, OMNamespace omNs, String name, String value) {
		OMElement omElement = fac.createOMElement(name, omNs);
		omElement.addChild(fac.createOMText(omElement, value));
		return omElement;
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

			ServiceClient sender = createServiceClient(getUserServiceEndPoint());
			
			OMElement kickUserByPublicSIDResult = sender
					.sendReceive(getPayloadMethodKickUserByPublicSID());
			Boolean result = kickUserByPublicSIDFromResult(kickUserByPublicSIDResult);
			
			if (!result) {
				throw new Exception("Could not delete user from slave host");
			}

		} catch (Exception err) {
			log.error("[kickUser failed]", err);
		}
	}

	private Boolean kickUserByPublicSIDFromResult(OMElement result) throws Exception {
		QName kickUserResult = new QName(NAMESPACE_PREFIX, "return");

		@SuppressWarnings("unchecked")
		Iterator<OMElement> elements = result.getChildrenWithName(kickUserResult);
		if (elements.hasNext()) {
			OMElement resultElement = elements.next();
			if (resultElement.getText().equals("true")) {
				return true;
			} else {
				throw new Exception("Could not delete user from slave host, returns: "
						+ resultElement.getText());
			}
		} else {
			throw new Exception("Could not parse kickUserByPublicSID result");
		}
	}

	private OMElement getPayloadMethodKickUserByPublicSID() throws Exception {
		OMFactory fac = OMAbstractFactory.getOMFactory();
		OMNamespace omNs = fac.createOMNamespace(NAMESPACE_PREFIX, "pre");
		OMElement method = fac.createOMElement("kickUserByPublicSID", omNs);
		method.addChild(createOMElement(fac, omNs, "SID", sessionId));
		method.addChild(createOMElement(fac, omNs, "publicSID", publicSID));
		return method;
	}

	/**
	 * Create the REST request to get a new session Id
	 * 
	 * @return
	 */
	private OMElement getPayloadMethodGetSession() throws Exception {
		OMFactory fac = OMAbstractFactory.getOMFactory();
		OMNamespace omNs = fac.createOMNamespace(NAMESPACE_PREFIX, "pre");
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
	 * create the payload to login to another OpenMeetings instance via REST
	 * 
	 * @return
	 */
	private OMElement getPayloadMethodLoginUser() throws Exception {
		OMFactory fac = OMAbstractFactory.getOMFactory();
		OMNamespace omNs = fac.createOMNamespace(NAMESPACE_PREFIX, "pre");
		OMElement method = fac.createOMElement("loginUser", omNs);
		method.addChild(createOMElement(fac, omNs, "SID", sessionId));
		method.addChild(createOMElement(fac, omNs, "username", user));
		method.addChild(createOMElement(fac, omNs, "userpass", pass));
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

		QName loginResult = new QName(NAMESPACE_PREFIX, "return");

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
	 * Get and cast the element's text (if there is any)
	 * 
	 * @param resultElement
	 * @param elementName
	 * @param typeObject
	 * @return
	 */
//	private <T> T getElementTextByName(OMElement resultElement, String elementName, Class<T> typeObject) {
//		try {
//			OMElement userIdElement = resultElement
//					.getFirstChildWithName(new QName(nameSpaceForSlaveDto, elementName));
//			if (userIdElement != null && userIdElement.getText() != null
//					&& userIdElement.getText().length() > 0) {
//				
//				String defaultValue = userIdElement.getText();
//				
//				// Either this can be directly assigned or try to find a constructor
//				// that handles it
//				if (typeObject.isAssignableFrom(defaultValue.getClass())) {
//					return typeObject.cast(defaultValue);
//				}
//				Constructor<T> c = typeObject.getConstructor(defaultValue
//						.getClass());
//				return c.newInstance(defaultValue);
//				
//			}
//		} catch (Exception err) {
//			//Catch any class cast exception, but log only
//			log.error("[getElementTextByName]", err);
//		}
//		return null;
//	}
	
	
}
