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
package org.apache.openmeetings.screenshare;

import org.red5.client.net.rtmp.INetStreamEventHandler;
import org.red5.client.net.rtmp.RTMPClient;
import org.red5.server.api.service.IPendingServiceCall;
import org.red5.server.api.service.IPendingServiceCallback;
import org.red5.server.net.rtmp.RTMPConnection;
import org.red5.server.net.rtmp.event.Notify;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class RTMPClientPublish extends RTMPClient implements IPendingServiceCallback, INetStreamEventHandler, IScreenShare {
	private static final Logger logger = LoggerFactory.getLogger(RTMPClientPublish.class);
	private final CaptureScreen publishScreen;
	private String id;
	private Core core;

	RTMPClientPublish(Core core, String host, String app, String id) {
		this.id = id;
		this.core = core;
		publishScreen = new CaptureScreen(core, this, host, app, 1935);
	}

	public void setCore(Core core) {
		this.core = core;
	}

	public void connect() {
		super.connect(publishScreen.getHost(), 1935, publishScreen.getApp(), this);
	}

	@Override
	public void handleException(Throwable throwable) {
		logger.error("ERROR", throwable);
	}

	@Override
	public void connectionOpened(RTMPConnection conn) {
		super.connectionOpened(conn);
		createStream(this);
	}

	@Override
	public void connectionClosed(RTMPConnection conn) {
		super.connectionClosed(conn);
		connectionClosed();
	}

	private void connectionClosed() {
		publishScreen.setStartPublish(false);
		publishScreen.release();
		core.publishingStop();
	}

	@Override
	public void resultReceived(IPendingServiceCall call) {
		String method = call == null ? null : call.getServiceMethodName();
		logger.trace("call ### get Method Name " + method);
		if ("createStream".equals(method)) {
			if (call.getResult() != null) {
				publishScreen.setStreamId((Integer)call.getResult());
				publish(publishScreen.getStreamId(), id, "live", this);
				publishScreen.setStartPublish(true);
				publishScreen.start();
			} else {
				connectionClosed();
			}
		}
	}

	@Override
	public void onStreamEvent(Notify notify) {
		//no-op
	}
}