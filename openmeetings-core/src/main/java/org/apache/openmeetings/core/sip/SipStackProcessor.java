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

import static javax.sip.message.Request.BYE;
import static javax.sip.message.Request.INVITE;
import static javax.sip.message.Request.REGISTER;
import static javax.sip.message.Response.OK;
import static javax.sip.message.Response.RINGING;
import static javax.sip.message.Response.TRYING;
import static javax.sip.message.Response.UNAUTHORIZED;
import static org.apache.openmeetings.core.sip.SipManager.getSipNumber;

import java.text.ParseException;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

import javax.sip.ClientTransaction;
import javax.sip.Dialog;
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
import javax.sip.header.CSeqHeader;
import javax.sip.header.ContactHeader;
import javax.sip.header.HeaderFactory;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;

import org.apache.openmeetings.db.entity.room.Room;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.nist.javax.sip.DialogTimeoutEvent;
import gov.nist.javax.sip.SipListenerExt;
import gov.nist.javax.sip.SipStackExt;
import gov.nist.javax.sip.SipStackImpl;
import gov.nist.javax.sip.address.SipUri;
import gov.nist.javax.sip.clientauthutils.AuthenticationHelper;
import gov.nist.javax.sip.clientauthutils.UserCredentials;
import gov.nist.javax.sip.stack.NioMessageProcessorFactory;

public class SipStackProcessor implements SipListenerExt {
	private static final Logger log = LoggerFactory.getLogger(SipStackProcessor.class);
	private static final String SIP_TRANSPORT = "ws";
	private static final <T> Consumer<T> noop() {
		return t -> {};
	}

	private final SipManager manager;
	private final ISipCallbacks callbacks;
	private final String sipHost;
	private final int wsPort;
	private final String omSipUser;
	private final String omSipPasswd;
	private final String localWsHost;
	private final int localWsPort;
	private final String tag;

	private final SipProvider sipProvider;
	private final SipStackExt sipStack;
	private final MessageFactory messageFactory;
	private final HeaderFactory headerFactory;
	private final AddressFactory addressFactory;
	private final ContactHeader contactHeader;
	private final AtomicLong cseq = new AtomicLong();
	private final Random rnd = new Random();
	private Dialog dialog;

	SipStackProcessor(SipManager manager, String name, int localWsPort, ISipCallbacks callbacks) throws Exception {
		this.manager = manager;
		this.callbacks = callbacks;
		this.sipHost = manager.getSipHostname();
		this.wsPort = manager.getWsPort();
		this.omSipUser = manager.getOmSipUser();
		this.omSipPasswd = manager.getOmSipPasswd();
		this.localWsHost = manager.getLocalWsHost();
		this.localWsPort = localWsPort;

		final SipFactory sipFactory = SipFactory.getInstance();

		final Properties properties = new Properties();
		properties.setProperty("javax.sip.STACK_NAME", name);
		if (log.isTraceEnabled()) {
			properties.setProperty("gov.nist.javax.sip.TRACE_LEVEL", "32");
		}
		properties.setProperty("gov.nist.javax.sip.LOG_MESSAGE_CONTENT", "true");
		properties.setProperty("gov.nist.javax.sip.MESSAGE_PROCESSOR_FACTORY", NioMessageProcessorFactory.class.getName());
		sipStack = new SipStackImpl(properties);
		tag = getRnd(10);

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
			if (Request.OPTIONS.equals(method) || Request.BYE.equals(method)) {
				ServerTransaction transaction = evt.getServerTransaction();
				if (transaction == null) {
					transaction = sipProvider.getNewServerTransaction(rq);
				}
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
				if (REGISTER.equals(prevReq.getMethod())) {
					callbacks.onRegisterOk();
				} else if (INVITE.equals(prevReq.getMethod())) {
					dialog = evt.getDialog();
					callbacks.onInviteOk(new String((byte[])resp.getContent()), answer -> ack(evt, answer));
				} else if (BYE.equals(prevReq.getMethod())) {
					doDestroy();
				}
				break;
			case TRYING, RINGING:
				break;
			default:
				log.debug("No handler for response: \n\n{}", resp);
		}
	}

	private void ack(ResponseEvent evt, String answer) {
		try {
			Response resp = evt.getResponse();
			Dialog dlg = evt.getDialog();
			CSeqHeader cseqHead = (CSeqHeader)resp.getHeader(CSeqHeader.NAME);
			Request ack = dlg.createAck(cseqHead.getSeqNumber());
			addSdp(ack, answer);
			dlg.sendAck(ack);
		} catch (Exception e) {
			log.error("ack {}", evt, e);
		}
	}

	private void sayBye() {
		try {
			Request req = dialog.createRequest(BYE);
			ClientTransaction ct = sipProvider.getNewClientTransaction(req);
			dialog.sendRequest(ct);
		} catch (SipException e) {
			log.error("bye ", e);
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
		return createAddr(user, sipHost, noop());
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

	private void sendRequest(String method, String to, Consumer<SipUri> uriCons, Consumer<Request> reqCons) {
		try {
			SipUri uri = new SipUri();
			uri.setHost(sipHost);
			uri.setPort(wsPort);
			uri.setTransportParam(SIP_TRANSPORT);
			uri.setMethodParam("GET");
			uri.setHeader("Host", sipHost);
			uri.setHeader("Location", "/ws");
			uriCons.accept(uri);

			Request request = messageFactory.createRequest(
					uri
					, method
					, sipProvider.getNewCallId()
					, headerFactory.createCSeqHeader(cseq.incrementAndGet(), method)
					, headerFactory.createFromHeader(createAddr(omSipUser), tag)
					, headerFactory.createToHeader(createAddr(to), null)
					, List.of(headerFactory.createViaHeader(localWsHost, localWsPort, SIP_TRANSPORT, null))
					, headerFactory.createMaxForwardsHeader(70));
			request.addHeader(contactHeader);
			request.addHeader(headerFactory.createExpiresHeader(600));

			reqCons.accept(request);

			ClientTransaction trans = sipProvider.getNewClientTransaction(request);
			trans.sendRequest();
		} catch (Exception e) {
			log.error("Error while sending request", e);
		}
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

	public void register() {
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

	private void addSdp(Request req, String sdp) throws Exception {
		if (sdp != null) {
			req.addHeader(headerFactory.createContentLengthHeader(sdp.length()));
			req.setContent(sdp, headerFactory.createContentTypeHeader("application", "sdp"));
		}
	}

	public void invite(Room r, String sdp) {
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
					try {
						addAllow(req);
						addSdp(req, sdp);
					} catch (Exception e) {
						log.error("fail patch invite request", e);
					}
				});
	}

	public void destroy() {
		if (dialog != null) {
			sayBye();
		} else {
			doDestroy();
		}
	}

	private void doDestroy() {
		sipStack.stop();
		manager.freePort(localWsPort);
	}
}
