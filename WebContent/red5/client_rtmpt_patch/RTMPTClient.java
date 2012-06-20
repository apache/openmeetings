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

import java.util.Map;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.red5.client.net.rtmp.BaseRTMPClientHandler;
import org.red5.io.object.Deserializer;
import org.red5.io.object.Serializer;
import org.red5.server.net.protocol.ProtocolState;
import org.red5.server.net.rtmp.RTMPConnection;
import org.red5.server.net.rtmp.RTMPMinaConnection;
import org.red5.server.net.rtmp.codec.RTMP;
import org.red5.server.net.rtmp.codec.RTMPProtocolDecoder;
import org.red5.server.net.rtmp.codec.RTMPProtocolEncoder;
import org.red5.server.net.rtmp.message.Constants;
import org.red5.server.net.rtmpt.codec.RTMPTCodecFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RTMPT client object
 * 
 * @author Anton Lebedevich
 */
public class RTMPTClient extends BaseRTMPClientHandler {

	private static final Logger log = LoggerFactory.getLogger(RTMPTClient.class);

	// guarded by this
	private RTMPTClientConnector connector;
	
	private RTMPTCodecFactory codecFactory;
	
	public RTMPTClient() {
		codecFactory = new RTMPTCodecFactory();
		codecFactory.setDeserializer(new Deserializer());
		codecFactory.setSerializer(new Serializer());
		codecFactory.init();
	}

	public Map<String, Object> makeDefaultConnectionParams(String server, int port, String application) {
		Map<String, Object> params = super.makeDefaultConnectionParams(server, port, application);
		if (!params.containsKey("tcUrl")) {
			params.put("tcUrl", "rtmpt://" + server + ':' + port + '/' + application);
		}
		return params;
	}

	protected synchronized void startConnector(String server, int port) {
		connector = new RTMPTClientConnector(server, port, this);
		log.debug("Created connector {}", connector);
		connector.start();
	}

	/** {@inheritDoc} */
	@Override
	public void messageReceived(Object in, IoSession session) throws Exception {
		RTMPConnection conn = (RTMPTClientConnection) session.getAttribute(RTMPConnection.RTMP_CONNECTION_KEY);
		RTMP state = (RTMP) session.getAttribute(ProtocolState.SESSION_KEY);
		if (in instanceof IoBuffer) {
			rawBufferRecieved(conn, state, (IoBuffer) in);
		} else {
			super.messageReceived(in, session);
		}
	}

	/**
	 * Handle raw buffer receipt
	 * 
	 * @param conn
	 *            RTMP connection
	 * @param state
	 *            Protocol state
	 * @param in
	 *            IoBuffer with input raw data
	 */
	private void rawBufferRecieved(RTMPConnection conn, ProtocolState state, IoBuffer _in) {
		log.debug("Handshake 3d phase - size: {}", _in.remaining());
		IoBuffer out = IoBuffer.allocate(Constants.HANDSHAKE_SIZE);
		IoBuffer in = _in;
		if (!in.isAutoExpand()) {
			in = IoBuffer.allocate(in.position() + Constants.HANDSHAKE_SIZE);
			in.setAutoExpand(true);
			in.put(in);
		}
		in.skip(1);
		in.limit(in.position() + Constants.HANDSHAKE_SIZE);
		out.put(in);
		out.flip();
		conn.writeRaw(out);
		connectionOpened(conn, conn.getState());
	}

	public synchronized void disconnect() {
		if (connector != null) {
			connector.setStopRequested(true);
			connector.interrupt();
		}
		super.disconnect();
	}

	public RTMPProtocolDecoder getDecoder() {
		return codecFactory.getRTMPDecoder();
	}

	public RTMPProtocolEncoder getEncoder() {
		return codecFactory.getRTMPEncoder();
	}
	
	
}
