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

import java.lang.reflect.Constructor;
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
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.conference.room.RoomClient;
import org.apache.openmeetings.conference.room.SlaveClientDto;
import org.apache.openmeetings.documents.beans.UploadCompleteMessage;
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
		//send a ping to the user
		PING, 
		//kick the user from the server
		KICK_USER,
		//send a sync message to a client on that server
		SYNC_MESSAGE
	}

	/**
	 * The observerInstance will be notified whenever a ping was completed
	 */
	private IRestClientObserver observerInstance;
	
	private Server server;
	private final String host;
	private final int port;
	private final String protocol;
	private final String webapp;
	private final String user;
	private final String pass;

	private boolean loginSuccess = false;
	private String sessionId;
	
	private boolean pingRunning = false;

	private String publicSID;

	private UploadCompleteMessage uploadCompleteMessage;

	/**
	 * there are two publicSIDs, one for the kickUser REST call and one for the syncMessage call
	 * theoretically they could be performed at the same time but to different users, so we don't want
	 * to use the same variable for both
	 */
	private String publicSIDSync;
	
	private static String nameSpaceForSlaveDto = "http://room.conference.openmeetings.apache.org/xsd";
	
	/**
	 * returns true as long as the RestClient performs a ping and parses the result
	 * 
	 * @return
	 */
	public boolean getPingRunning() {
		return pingRunning;
	}

	private static String NAMESPACE_PREFIX = "http://services.axis.openmeetings.apache.org";

	private String getUserServiceEndPoint() {
		return protocol + "://" + host + ":" + port + "/" + webapp
				+ "/services/UserService";
	}

	private String getServerServiceEndPoint() {
		return protocol + "://" + host + ":" + port + "/" + webapp
				+ "/services/ServerService";
	}
	
	private String getRoomServiceEndPoint() {
		return protocol + "://" + host + ":" + port + "/" + webapp
				+ "/services/RoomService";
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
	public RestClient(IRestClientObserver observerInstance, Server server) {
		this.observerInstance = observerInstance;
		this.server = server;
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
			rClient.loginUser(Action.PING);
			rClient.ping();
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

		if (action == Action.PING) {
			ping();
		} else if (action == Action.KICK_USER) {
			kickUserInternl();
		} else if (action == Action.SYNC_MESSAGE) {
			syncMessageInternl();
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
	 * set s the publicSID the message object and sends it to the slave by calling a REST service
	 * 
	 * @param publicSID
	 * @param uploadCompleteMessage
	 */
	public void syncMessage(String publicSID, UploadCompleteMessage uploadCompleteMessage) {
		this.publicSIDSync = publicSID;
		this.uploadCompleteMessage = uploadCompleteMessage;
		syncMessageInternl();
	}
	
	private void syncMessageInternl() {
		try {
			
			if (!loginSuccess) {
				loginUser(Action.SYNC_MESSAGE);
			}
			
			ServiceClient sender = createServiceClient(getRoomServiceEndPoint());
			OMElement syncMessageResult = sender
					.sendReceive(getPayloadMethodSyncMessage());
			Boolean result = syncMessageResultFromResult(syncMessageResult);
			
			if (!result) {
				throw new Exception("Could not sync message to slave host");
			}
			
		} catch (Exception err) {
			log.error("[syncMessage failed]", err);
		}
	}
	
	private Boolean syncMessageResultFromResult(OMElement result) throws Exception {
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
	
	private OMElement getPayloadMethodSyncMessage() {
		
		OMFactory fac = OMAbstractFactory.getOMFactory();
		OMNamespace omNs = fac.createOMNamespace(NAMESPACE_PREFIX, "pre");
		OMElement method = fac.createOMElement("syncUploadCompleteMessage", omNs);

		method.addChild(createOMElement(fac, omNs, "SID", sessionId));
		method.addChild(createOMElement(fac, omNs, "publicSID", publicSIDSync));
		method.addChild(createOMElement(fac, omNs, "userId", ""+ uploadCompleteMessage.getUserId()));
		method.addChild(createOMElement(fac, omNs, "message", uploadCompleteMessage.getMessage()));
		method.addChild(createOMElement(fac, omNs, "action", uploadCompleteMessage.getAction()));
		method.addChild(createOMElement(fac, omNs, "error", uploadCompleteMessage.getError()));
		method.addChild(createOMElement(fac, omNs, "hasError", ""+uploadCompleteMessage.isHasError()));
		method.addChild(createOMElement(fac, omNs, "fileName", uploadCompleteMessage.getFileName()));
		
		method.addChild(createOMElement(fac, omNs, "fileSystemName", uploadCompleteMessage.getFileSystemName()));
		method.addChild(createOMElement(fac, omNs, "isPresentation", ""+uploadCompleteMessage.getIsPresentation()));
		method.addChild(createOMElement(fac, omNs, "isImage", ""+uploadCompleteMessage.getIsImage()));
		method.addChild(createOMElement(fac, omNs, "isVideo", ""+uploadCompleteMessage.getIsVideo()));
		method.addChild(createOMElement(fac, omNs, "fileHash", uploadCompleteMessage.getFileHash()));
		
		return method;
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
	 * verifies if the user is logged in, if yes, it will try to load the
	 * current list of sessions from the slave
	 * 
	 * @throws Exception
	 */
	public void ping() {
		try {
			//flag this ping flow as active, so that the scheduler does not run multiple ping's 
			//on the same instance, at the same time, cause a ping could take longer then the 
			//scheduler interval, for example because of the server load
			pingRunning = true;
			
			if (!loginSuccess) {
				loginUser(Action.PING);
			} else {
				 
				ServiceClient sender = createServiceClient(getServerServiceEndPoint());
				OMElement pingResult = sender
						.sendReceive(getPayloadMethodPingTemp());

				List<SlaveClientDto> slaveClients = pingFromResult(pingResult);

				if (this.observerInstance != null) {
					this.observerInstance.pingComplete(server, slaveClients);
				}
				
				//flag this flow as complete
				pingRunning = false;

			}
			// Catches all errors to make sure the observer is notified that the
			// ping was performed (even when performed badly)
		} catch (Exception ex) {
			
			//Clear the list of clients if there are any for this server
			if (this.observerInstance != null) {
				this.observerInstance.pingComplete(server, new ArrayList<SlaveClientDto>(0));
			}
			
			//flag this flow as complete
			pingRunning = false;
			log.error("[ping failed]", ex);
		}
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
	 * create the payload to login to another openmeetings instance via REST
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
	 * Create the REST request for the ping method to load the users
	 * 
	 * @return
	 */
	private OMElement getPayloadMethodPingTemp() throws Exception {
		OMFactory fac = OMAbstractFactory.getOMFactory();
		OMNamespace omNs = fac.createOMNamespace(NAMESPACE_PREFIX, "pre");
		OMElement method = fac.createOMElement("ping", omNs);
		method.addChild(createOMElement(fac, omNs, "SID", sessionId));
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
	private List<SlaveClientDto> pingFromResult(OMElement result) throws Exception {

		QName pingResult = new QName(NAMESPACE_PREFIX, "return");

		@SuppressWarnings("unchecked")
		Iterator<OMElement> elements = result.getChildrenWithName(pingResult);
		List<SlaveClientDto> clients = new ArrayList<SlaveClientDto>();
		while (elements.hasNext()) {
			OMElement resultElement = elements.next();
			SlaveClientDto slaveDto = new SlaveClientDto( //
					getElementTextByName(resultElement, "streamid", String.class), //
					getElementTextByName(resultElement, "publicSID", String.class), //
					getElementTextByName(resultElement, "roomId", Long.class), //
					getElementTextByName(resultElement, "userId", Long.class), //
					getElementTextByName(resultElement, "firstName", String.class), //
					getElementTextByName(resultElement, "lastName", String.class), //
					getElementTextByName(resultElement, "AVClient", Boolean.class), //
					getElementTextByName(resultElement, "scope", String.class), //
					getElementTextByName(resultElement, "username", String.class), //
					getElementTextByName(resultElement, "connectedSince", String.class)
				); //
			log.debug(slaveDto.toString());
			clients.add(slaveDto);
		}
		return clients;
	}
	
	/**
	 * Get and cast the element's text (if there is any)
	 * 
	 * @param resultElement
	 * @param elementName
	 * @param typeObject
	 * @return
	 */
	private <T> T getElementTextByName(OMElement resultElement, String elementName, Class<T> typeObject) {
		try {
			OMElement userIdElement = resultElement
					.getFirstChildWithName(new QName(nameSpaceForSlaveDto, elementName));
			if (userIdElement != null && userIdElement.getText() != null
					&& userIdElement.getText().length() > 0) {
				
				String defaultValue = userIdElement.getText();
				
				// Either this can be directly assigned or try to find a constructor
				// that handles it
				if (typeObject.isAssignableFrom(defaultValue.getClass())) {
					return typeObject.cast(defaultValue);
				}
				Constructor<T> c = typeObject.getConstructor(defaultValue
						.getClass());
				return c.newInstance(defaultValue);
				
			}
		} catch (Exception err) {
			//Catch any class cast exception, but log only
			log.error("[getElementTextByName]", err);
		}
		return null;
	}
	
	
}
