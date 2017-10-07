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

import org.red5.client.net.rtmps.RTMPSClient;
import org.red5.server.net.ICommand;
import org.red5.server.net.rtmp.Channel;
import org.red5.server.net.rtmp.RTMPConnection;
import org.red5.server.net.rtmp.message.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RTMPSScreenShare extends RTMPSClient implements IScreenShare {
	private static final Logger log = LoggerFactory.getLogger(RTMPSScreenShare.class);

	private final Core core;

	public RTMPSScreenShare(Core core) {
		this.core = core;
	};

	@Override
	public void connectionOpened(RTMPConnection conn) {
		log.debug("connection opened");
		super.connectionOpened(conn);
		core.setDeadlockGuard(conn);
	}

	@Override
	public void connectionClosed(RTMPConnection conn) {
		log.debug("connection closed");
		super.connectionClosed(conn);
		if (core.isAudioNotify()) {
			AudioTone.play();
		}
		core.stopStream(null);
	}

	@Override
	protected void onCommand(RTMPConnection conn, Channel channel, Header source, ICommand command) {
		super.onCommand(conn, channel, source, command);
		core.onCommand(command);
	}

	@Override
	public void handleException(Throwable throwable) {
		Throwable cause = throwable.getCause();
		log.error("{}", new Object[] { cause });
		core.handleException(cause);
	}
}
