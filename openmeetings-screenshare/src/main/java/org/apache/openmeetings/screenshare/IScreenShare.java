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

import java.util.Map;

import org.red5.client.net.rtmp.ClientExceptionHandler;
import org.red5.client.net.rtmp.INetStreamEventHandler;
import org.red5.server.api.service.IPendingServiceCallback;
import org.red5.server.messaging.IMessage;
import org.red5.server.net.rtmp.RTMPConnection;

public interface IScreenShare extends ClientExceptionHandler {
	RTMPConnection getConnection();
	void invoke(String method, Object[] params, IPendingServiceCallback callback);
	Map<String, Object> makeDefaultConnectionParams(String server, int port, String application);
	void connect(String server, int port, Map<String, Object> connectionParams, IPendingServiceCallback connectCallback, Object[] connectCallArguments);
	void setServiceProvider(Object serviceProvider);
	void disconnect();
	void createStream(IPendingServiceCallback callback);
	void publish(Number streamId, String name, String mode, INetStreamEventHandler handler);
	void unpublish(Number streamId);
	void publishStreamData(Number streamId, IMessage message);
}
