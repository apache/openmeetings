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
package org.apache.openmeetings.screen.webstart;

import org.red5.client.net.rtmps.RTMPSClient;
import org.red5.server.net.ICommand;
import org.red5.server.net.rtmp.Channel;
import org.red5.server.net.rtmp.RTMPConnection;
import org.red5.server.net.rtmp.message.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RTMPSScreenShare extends RTMPSClient implements IScreenShare {
	private static final Logger logger = LoggerFactory.getLogger(RTMPSScreenShare.class);

	private final CoreScreenShare core;

	public RTMPSScreenShare(CoreScreenShare core) {
		protocol = "rtmps"; //FIXME TODO need to be removed after fix in red5
		this.core = core;
	};

	// ------------------------------------------------------------------------
	//
	// Override
	//
	// ------------------------------------------------------------------------
	@Override
	public void connectionOpened(RTMPConnection conn) {
		logger.debug("connection opened");
		super.connectionOpened(conn);
		core.setDeadlockGuard(conn);
	}

	@Override
	public void connectionClosed(RTMPConnection conn) {
		logger.debug("connection closed");
		super.connectionClosed(conn);
		if (core.isAudioNotify()) {
			AudioTone.play();
		}
		core.stopStream();
	}

	@Override
	protected void onCommand(RTMPConnection conn, Channel channel, Header source, ICommand command) {
		super.onCommand(conn, channel, source, command);
		core.onCommand(conn, channel, source, command);
	}

	@Override
	public void handleException(Throwable throwable) {
		logger.error("{}", new Object[] { throwable.getCause() });
		System.out.println(throwable.getCause());
	}
}
