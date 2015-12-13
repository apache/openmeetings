/*
 * RED5 Open Source Flash Server - http://code.google.com/p/red5/
 * 
 * Copyright 2006-2012 by respective authors (see below). All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.red5.client.net.rtmpt;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.DummySession;
import org.apache.mina.core.session.IoSession;
import org.red5.client.net.rtmp.RTMPClientConnManager;
import org.red5.server.net.protocol.ProtocolState;
import org.red5.server.net.rtmp.RTMPConnection;
import org.red5.server.net.rtmp.codec.RTMP;
import org.red5.server.net.rtmp.message.Constants;
import org.red5.server.util.HttpConnectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Client connector for RTMPT
 * 
 * @author Anton Lebedevich (mabrek@gmail.com)
 * @author Paul Gregoire (mondain@gmail.com)
 */
class RTMPTClientConnector extends Thread {
	
	private static final Logger log = LoggerFactory.getLogger(RTMPTClientConnector.class);

	private static final String CONTENT_TYPE = "application/x-fcs";

	private static final ByteArrayEntity ZERO_REQUEST_ENTITY = new ByteArrayEntity(new byte[] { 0 });

	/**
	 * Size to split messages queue by, borrowed from
	 * RTMPTServlet.RESPONSE_TARGET_SIZE
	 */
	private static final int SEND_TARGET_SIZE = 32768;

	private final DefaultHttpClient httpClient = HttpConnectionUtil.getClient();

	private final HttpHost targetHost;
	
	private final RTMPTClient client;

	private final RTMPClientConnManager connManager;

	private int clientId;

	private long messageCount = 1;

	private volatile boolean stopRequested = false;

	public RTMPTClientConnector(String server, int port, RTMPTClient client) {
		targetHost = new HttpHost(server, port, "http");
		httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
		this.client = client;
		this.connManager = RTMPClientConnManager.getInstance();
	}

	public void run() {
		HttpPost post = null;	
		try {
			RTMPTClientConnection connection = openConnection();
			while (!connection.isClosing() && !stopRequested) {
				IoBuffer toSend = connection.getPendingMessages(SEND_TARGET_SIZE);			
				int limit = toSend != null ? toSend.limit() : 0;
				if (limit > 0) {
					post = makePost("send");
					post.setEntity(new InputStreamEntity(toSend.asInputStream(), limit));
					post.addHeader("Content-Type", CONTENT_TYPE);
				} else {
					post = makePost("idle");
					post.setEntity(ZERO_REQUEST_ENTITY);
				}
				// execute
				HttpResponse response = httpClient.execute(targetHost, post);
				// check for error
				checkResponseCode(response);
				// handle data
				byte[] received = EntityUtils.toByteArray(response.getEntity());
				IoBuffer data = IoBuffer.wrap(received);
				if (data.limit() > 0) {
					data.skip(1); // XXX: polling interval lies in this byte
				}
				List<?> messages = connection.decode(data);
				if (messages == null || messages.isEmpty()) {
					try {
						// XXX handle polling delay
						Thread.sleep(250);
					} catch (InterruptedException e) {
						if (stopRequested) {
							post.abort();
							break;
						}
					}
					continue;
				}
				IoSession session = new DummySession();
				session.setAttribute(RTMPConnection.RTMP_CONNECTION_KEY, connection);
				session.setAttribute(ProtocolState.SESSION_KEY, connection.getState());
				for (Object message : messages) {
					try {
						client.messageReceived(message, session);
					} catch (Exception e) {
						log.error("Could not process message.", e);
					}
				}
			}
			finalizeConnection();
			client.connectionClosed(connection, connection.getState());
		} catch (Throwable e) {
			log.debug("RTMPT handling exception", e);
			client.handleException(e);
			if (post != null) {
				post.abort();
			}
		}
	}

	private RTMPTClientConnection openConnection() throws IOException {
		RTMPTClientConnection connection = null;
		HttpPost openPost = new HttpPost("/open/1");
		setCommonHeaders(openPost);
		openPost.setEntity(ZERO_REQUEST_ENTITY);
		// execute
		HttpResponse response = httpClient.execute(targetHost, openPost);
		checkResponseCode(response);
		// get the response entity
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			String responseStr = EntityUtils.toString(entity);
			clientId = Integer.parseInt(responseStr.substring(0, responseStr.length() - 1));
			log.debug("Got client id {}", clientId);
			// create a new connection
			connection = (RTMPTClientConnection) connManager.createConnection(RTMPTClientConnection.class);
			// client state
			RTMP state = new RTMP();
			connection.setState(state);
			connection.setHandler(client);
			connection.setDecoder(client.getDecoder());
			connection.setEncoder(client.getEncoder());
			log.debug("Handshake 1st phase");
			IoBuffer handshake = IoBuffer.allocate(Constants.HANDSHAKE_SIZE + 1);
			handshake.put((byte) 0x03);
			handshake.fill((byte) 0x01, Constants.HANDSHAKE_SIZE);
			handshake.flip();
			connection.writeRaw(handshake);
		}
		return connection;			
	}

	private void finalizeConnection() throws IOException {
		log.debug("Sending close post");
		HttpPost closePost = new HttpPost(makeUrl("close"));
		closePost.setEntity(ZERO_REQUEST_ENTITY);
		HttpResponse response = httpClient.execute(targetHost, closePost);
		EntityUtils.consume(response.getEntity());
	}

	private HttpPost makePost(String command) {
		HttpPost post = new HttpPost(makeUrl(command));
		setCommonHeaders(post);
		return post;
	}

	private String makeUrl(String command) {
		// use message count from connection
		return String.format("/%s/%s/%s", command, clientId, messageCount++);
	}

	private void setCommonHeaders(HttpPost post) {
		post.addHeader("Connection", "Keep-Alive");
		post.addHeader("Cache-Control", "no-cache");
	}

	private void checkResponseCode(HttpResponse response) throws ParseException, IOException {
		int code = response.getStatusLine().getStatusCode();
		if (code != HttpStatus.SC_OK) {
			throw new RuntimeException("Bad HTTP status returned, line: " + response.getStatusLine() + "; body: " + EntityUtils.toString(response.getEntity()));
		}
	}

	public void setStopRequested(boolean stopRequested) {
		this.stopRequested = stopRequested;
	}
}
