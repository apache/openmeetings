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
package org.apache.openmeetings.core.sip;

import static javax.sip.message.Request.INVITE;
import static javax.sip.message.Request.REGISTER;
import static javax.sip.message.Response.OK;
import static javax.sip.message.Response.RINGING;
import static javax.sip.message.Response.TRYING;
import static javax.sip.message.Response.UNAUTHORIZED;
import static org.apache.openmeetings.util.OmFileHelper.SIP_USER_ID;
import static org.apache.openmeetings.util.OpenmeetingsVariables.isSipEnabled;

import java.text.ParseException;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.annotation.PostConstruct;
import javax.sip.ClientTransaction;
import javax.sip.DialogTerminatedEvent;
import javax.sip.IOExceptionEvent;
import javax.sip.ListeningPoint;
import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;
import javax.sip.ServerTransaction;
import javax.sip.SipException;
import javax.sip.SipFactory;
import javax.sip.SipProvider;
import javax.sip.TimeoutEvent;
import javax.sip.TransactionTerminatedEvent;
import javax.sip.address.Address;
import javax.sip.address.AddressFactory;
import javax.sip.header.ContactHeader;
import javax.sip.header.HeaderFactory;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;

import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.manager.ISipManager;
import org.apache.openmeetings.util.OmFileHelper;
import org.apache.wicket.util.string.Strings;
import org.asteriskjava.manager.DefaultManagerConnection;
import org.asteriskjava.manager.ManagerConnection;
import org.asteriskjava.manager.ManagerConnectionFactory;
import org.asteriskjava.manager.ResponseEvents;
import org.asteriskjava.manager.action.ConfbridgeListAction;
import org.asteriskjava.manager.action.DbDelAction;
import org.asteriskjava.manager.action.DbDelTreeAction;
import org.asteriskjava.manager.action.DbGetAction;
import org.asteriskjava.manager.action.DbPutAction;
import org.asteriskjava.manager.action.EventGeneratingAction;
import org.asteriskjava.manager.action.ManagerAction;
import org.asteriskjava.manager.action.OriginateAction;
import org.asteriskjava.manager.response.ManagerError;
import org.asteriskjava.manager.response.ManagerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import gov.nist.javax.sip.DialogTimeoutEvent;
import gov.nist.javax.sip.SipListenerExt;
import gov.nist.javax.sip.SipStackExt;
import gov.nist.javax.sip.address.SipUri;
import gov.nist.javax.sip.clientauthutils.AuthenticationHelper;
import gov.nist.javax.sip.clientauthutils.UserCredentials;
import gov.nist.javax.sip.stack.NioMessageProcessorFactory;

@Service
public class SipManager implements ISipManager, SipListenerExt {
	private static final Logger log = LoggerFactory.getLogger(SipManager.class);
	public static final String ASTERISK_OM_FAMILY = "openmeetings";
	public static final String ASTERISK_OM_KEY = "rooms";
	public static final String SIP_FIRST_NAME = "SIP Transport";
	public static final String SIP_USER_NAME = "--SIP--";
	private static final String SIP_TRANSPORT = "ws";
	private static final <T> Consumer<T> noop() {
		return t -> {};
	}

	@Value("${sip.hostname}")
	private String sipHostname;
	@Value("${sip.manager.port}")
	private int managerPort;
	@Value("${sip.manager.user}")
	private String managerUser;
	@Value("${sip.manager.password}")
	private String managerPass;
	@Value("${sip.manager.timeout}")
	private long managerTimeout;

	@Value("${sip.ws.local.port}")
	private int localWsPort = 6666;
	@Value("${sip.ws.local.host}")
	private String localWsHost;
	@Value("${sip.ws.remote.port}")
	private int wsPort;
	@Value("${sip.ws.remote.user}")
	private String omSipUser;
	@Value("${sip.ws.remote.password}")
	private String omSipPasswd;

	private final AtomicLong cseq = new AtomicLong();
	private final Random rnd = new Random();

	private String tag;
	private String branch;

	private SipProvider sipProvider;
	private SipStackExt sipStack;
	private MessageFactory messageFactory;
	private HeaderFactory headerFactory;
	private AddressFactory addressFactory;
	private ContactHeader contactHeader;
	private ManagerConnectionFactory factory;
	private String sipUserPicture;

	@PostConstruct
	public void init() throws Exception {
		if (!Strings.isEmpty(sipHostname)) {
			factory = new ManagerConnectionFactory(
					sipHostname
					, managerPort
					, managerUser
					, managerPass);
			final SipFactory sipFactory = SipFactory.getInstance();
			sipFactory.setPathName("gov.nist");

			final Properties properties = new Properties();
			properties.setProperty("javax.sip.STACK_NAME", "stack");
			//properties.setProperty("gov.nist.javax.sip.TRACE_LEVEL", "32");
			properties.setProperty("gov.nist.javax.sip.LOG_MESSAGE_CONTENT", "true");
			properties.setProperty("gov.nist.javax.sip.MESSAGE_PROCESSOR_FACTORY", NioMessageProcessorFactory.class.getName());
			sipStack = (SipStackExt) sipFactory.createSipStack(properties);
			tag = getRnd(10);
			branch = getRnd(14);

			messageFactory = sipFactory.createMessageFactory();
			headerFactory = sipFactory.createHeaderFactory();
			addressFactory = sipFactory.createAddressFactory();
			final ListeningPoint listeningPoint = sipStack.createListeningPoint(
					localWsHost
					, localWsPort
					, SIP_TRANSPORT);
			sipProvider = sipStack.createSipProvider(listeningPoint);
			sipProvider.addSipListener(this);
			Address contact = createAddr(omSipUser, localWsHost, uri -> {
				try {
					uri.setPort(localWsPort);
					uri.setTransportParam(SIP_TRANSPORT);
				} catch (ParseException e) {
					log.error("fail to create contact address", e);
				}
			});
			contactHeader = headerFactory.createContactHeader(contact);
		}
	}

	private ManagerConnection getConnection() {
		DefaultManagerConnection con = (DefaultManagerConnection)factory.createManagerConnection();
		con.setDefaultEventTimeout(managerTimeout);
		con.setDefaultResponseTimeout(managerTimeout);
		con.setSocketReadTimeout((int)managerTimeout);
		con.setSocketTimeout((int)managerTimeout);
		return con;
	}

	private ManagerResponse exec(ManagerAction action) {
		if (factory == null) {
			log.warn("There is no Asterisk configured");
			return null;
		}
		ManagerConnection con = getConnection();
		try {
			con.login();
			ManagerResponse r = con.sendAction(action);
			if (r != null) {
				log.debug(r.toString());
			}
			return (r instanceof ManagerError) ? null : r;
		} catch (Exception e) {
			log.error("Error while executing ManagerAction: {}", action, e);
		} finally {
			try {
				con.logoff();
			} catch (Exception e) {
				// no-op
			}
		}
		return null;
	}

	private ResponseEvents execEvent(EventGeneratingAction action) {
		if (factory == null) {
			log.warn("There is no Asterisk configured");
			return null;
		}
		ManagerConnection con = getConnection();
		try {
			con.login("on");
			ResponseEvents r = con.sendEventGeneratingAction(action);
			if (r != null) {
				log.debug(r.getResponse().toString());
			}
			return (r == null || r.getResponse() instanceof ManagerError) ? null : r;
		} catch (Exception e) {
			log.error("Error while executing EventGeneratingAction: {}", action, e);
		} finally {
			try {
				con.logoff();
			} catch (Exception e) {
				// no-op
			}
		}
		return null;
	}

	private static String getKey(String confno) {
		return ASTERISK_OM_KEY + "/" + confno;
	}

	private static String getSipNumber(Room r) {
		return (r != null && r.getConfno() != null) ? r.getConfno() : null;
	}

	public String get(String confno) {
		String pin = null;
		DbGetAction da = new DbGetAction(ASTERISK_OM_FAMILY, getKey(confno));
		ManagerResponse r = exec(da);
		if (r != null) {
			pin = r.getResponse();
		}
		return pin;
	}

	@Override
	public void update(String confno, String pin) {
		delete(confno);
		DbPutAction da = new DbPutAction(ASTERISK_OM_FAMILY, getKey(confno), pin);
		exec(da);
	}

	public void delete() {
		DbDelTreeAction da = new DbDelTreeAction(ASTERISK_OM_FAMILY, ASTERISK_OM_KEY);
		exec(da);
	}

	@Override
	public void delete(String confno) {
		DbDelAction da = new DbDelAction(ASTERISK_OM_FAMILY, getKey(confno));
		exec(da);
	}

	public Integer countUsers(String confno) {
		if (confno == null) {
			return null;
		}
		ConfbridgeListAction da = new ConfbridgeListAction(confno);
		ResponseEvents r = execEvent(da);
		if (r != null) {
			log.debug("SipManager::countUsers size == {}", r.getEvents().size());
			// "- 1" here means: ListComplete event
			return r.getEvents().size() - 1;
		}
		return 0;
	}

	/**
	 * Perform call to specified phone number and join to conference
	 *
	 * @param number
	 *            number to call
	 * @param r
	 *            room to be connected to the call
	 */
	public void joinToConfCall(String number, Room r) {
		String sipNumber = getSipNumber(r);
		if (sipNumber == null) {
			log.warn("Failed to get SIP number for room: {}", r);
			return;
		}

		OriginateAction oa = new OriginateAction();
		oa.setChannel(String.format("Local/%s@rooms-originate", sipNumber));
		oa.setContext("rooms-out");
		oa.setExten(number);
		oa.setPriority(1);
		oa.setTimeout(managerTimeout);

		exec(oa);
	}

	public void setUserPicture(Function<User, String> pictureCreator) {
		User u = new User();
		u.setId(SIP_USER_ID);
		sipUserPicture = pictureCreator.apply(u);
	}

	public User getSipUser(Room r) {
		if (factory == null || !isSipEnabled() || !r.isSipEnabled()) {
			return null;
		}
		User u = new User();
		u.setId(OmFileHelper.SIP_USER_ID);
		u.setFirstname(SIP_FIRST_NAME);
		u.setLogin(SIP_USER_NAME);
		u.setPictureUri(sipUserPicture);
		return u;
	}

	@Override
	public void processDialogTerminated(DialogTerminatedEvent evt) {
		log.error("processDialogTerminated: \n{}", evt);
	}

	@Override
	public void processIOException(IOExceptionEvent evt) {
		log.error("processIOException: \n{}", evt);
	}

	@Override
	public void processTimeout(TimeoutEvent evt) {
		log.error("processTimeout: \n{}", evt);
	}

	@Override
	public void processTransactionTerminated(TransactionTerminatedEvent evt) {
		log.error("processTransactionTerminated: \n{}", evt);
	}

	@Override
	public void processDialogTimeout(DialogTimeoutEvent timeoutEvent) {
		log.error("processDialogTimeout: \n{}", timeoutEvent);
	}

	@Override
	public void processRequest(RequestEvent evt) {
		log.debug("processRequest: \n\n{}", evt.getRequest());
		Request rq = evt.getRequest();
		String method = rq.getMethod();
		try {
			if (Request.OPTIONS.equals(method)) {
				ServerTransaction transaction = sipProvider.getNewServerTransaction(rq);
				Response resp = messageFactory.createResponse(200, rq);
				resp.addHeader(contactHeader);
				transaction.sendResponse(resp);
			}
		} catch (Exception e) {
			log.error("processRequest", e);
		}
	}

	@Override
	public void processResponse(ResponseEvent evt) {
		Response resp = evt.getResponse();
		ClientTransaction curTrans = evt.getClientTransaction();
		Request prevReq = curTrans.getRequest();
		log.warn("Response code: {} on {}", resp.getStatusCode(), prevReq.getMethod());
		switch (resp.getStatusCode()) {
			case UNAUTHORIZED:
				processUnauth(evt);
				break;
			case OK:
				break;
			case TRYING:
			case RINGING:
				break;
			// FIXME TODO other codes: 404
			default:
				log.debug("No handler for response: \n\n{}", resp);
		}
	}

	private String getRnd(int count) {
		return rnd.ints('0', 'z' + 1)
				.filter(ch -> (ch >= '0' && ch <= '9') || (ch >= 'a' && ch <= 'z'))
				.limit(count)
				.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
				.toString();
	}

	private Address createAddr(String user) {
		return createAddr(user, sipHostname, noop());
	}

	private Address createAddr(String user, String host, Consumer<SipUri> cons) {
		try {
			SipUri uri = new SipUri();
			uri.setHost(host);
			uri.setUser(user);
			cons.accept(uri);
			return addressFactory.createAddress(user, uri);
		} catch (ParseException e) {
			log.error("fail to create address", e);
		}
		return null;
	}

	private void sendRequest(String method, String to, Consumer<SipUri> uriCons, Consumer<Request> reqCons) throws Exception {
		SipUri uri = new SipUri();
		uri.setHost(sipHostname);
		uri.setPort(wsPort);
		uri.setTransportParam(SIP_TRANSPORT);
		uri.setMethodParam("GET");
		uri.setHeader("Host", sipHostname);
		uri.setHeader("Location", "/ws");
		uriCons.accept(uri);

		Request request = messageFactory.createRequest(
				uri
				, method
				, sipProvider.getNewCallId()
				, headerFactory.createCSeqHeader(cseq.incrementAndGet(), method)
				, headerFactory.createFromHeader(createAddr(omSipUser), tag)
				, headerFactory.createToHeader(createAddr(to), null)
				, List.of(headerFactory.createViaHeader(localWsHost, localWsPort, SIP_TRANSPORT, branch))
				, headerFactory.createMaxForwardsHeader(70));
		request.addHeader(contactHeader);
		request.addHeader(headerFactory.createExpiresHeader(600));

		reqCons.accept(request);

		log.debug("sendRequest: \n\n{}", request);

		ClientTransaction trans = sipProvider.getNewClientTransaction(request);
		trans.sendRequest();
	}

	private void processUnauth(ResponseEvent evt) {
		Response resp = evt.getResponse();
		ClientTransaction curTrans = evt.getClientTransaction();
		AuthenticationHelper helper = sipStack.getAuthenticationHelper((trans, s) -> new UserCredentials() {
			@Override
			public String getUserName() {
				return omSipUser;
			}

			@Override
			public String getPassword() {
				return omSipPasswd;
			}

			@Override
			public String getSipDomain() {
				return "asterisk";
			}
		}, headerFactory);
		try {
			ClientTransaction trans = helper.handleChallenge(resp, curTrans, sipProvider, 5);
			trans.sendRequest();
		} catch (SipException e) {
			log.error("Error while sending AUTH", e);
		}
	}

	private void addAllow(Request req) throws ParseException {
		req.addHeader(headerFactory.createAllowHeader("ACK,CANCEL,INVITE,MESSAGE,BYE,OPTIONS,INFO,NOTIFY,REFER"));
	}

	private void register() throws Exception {
		sendRequest(
				REGISTER
				, omSipUser
				, noop()
				, req -> {
					try {
						addAllow(req);
					} catch (ParseException e) {
						log.error("fail to create allow header", e);
					}
				});
	}

	private void invite(Room r, String sdp) throws Exception {
		final String sipNumber = getSipNumber(r);
		if (sipNumber == null) {
			log.warn("Failed to get SIP number for room: {}", r);
			return;
		}
		sendRequest(
				INVITE
				, sipNumber
				, uri -> uri.setUser(sipNumber)
				, req -> {
					/*
					 * ContentLengthHeader contentLength =
					 * this.headerFactory.createContentLengthHeader(300);
					 * ContentTypeHeader contentType =
					 * this.headerFactory.createContentTypeHeader("application",
					 * "sdp");
					 *
					 * String sdpData = "v=0\n" +
					 * "o=user1 392867480 292042336 IN IP4 xx.xx.xx.xx\n" + "s=-\n"
					 * + "c=IN IP4 xx.xx.xx.xx\n" + "t=0 0\n" +
					 * "m=audio 8000 RTP/AVP 0 8 101\n" + "a=rtpmap:0 PCMU/8000\n" +
					 * "a=rtpmap:8 PCMA/8000\n" +
					 * "a=rtpmap:101 telephone-event/8000\n" + "a=sendrecv"; byte[]
					 * contents = sdpData.getBytes(); this.contactHeader =
					 * this.headerFactory.createContactHeader(contactAddress);
					 */
					try {
						addAllow(req);
						req.addHeader(headerFactory.createContentLengthHeader(sdp.length()));
						req.setContent(sdp, headerFactory.createContentTypeHeader("application", "sdp"));
					} catch (Exception e) {
						log.error("fail to create allow header", e);
					}
				});
	}
}
