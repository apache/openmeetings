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
package org.apache.openmeetings.data.flvrecord.listener;

import java.util.Date;

import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.data.flvrecord.FlvRecordingMetaDataDao;
import org.apache.openmeetings.data.flvrecord.listener.async.CachedEvent;
import org.apache.openmeetings.data.flvrecord.listener.async.StreamVideoWriter;
import org.red5.logging.Red5LoggerFactory;
import org.red5.server.api.scope.IScope;
import org.red5.server.api.stream.IBroadcastStream;
import org.red5.server.api.stream.IStreamPacket;
import org.slf4j.Logger;

public class StreamVideoListener extends BaseStreamListener {
	
	private static final Logger log = Red5LoggerFactory.getLogger(
			StreamVideoListener.class, OpenmeetingsVariables.webAppRootKey);

	private final StreamVideoWriter streamVideoWriter;

	public StreamVideoListener(String streamName, IScope scope,
			Long flvRecordingMetaDataId, boolean isScreenData,
			boolean isInterview,
			FlvRecordingMetaDataDao flvRecordingMetaDataDao) {
		streamVideoWriter = new StreamVideoWriter(streamName, scope, flvRecordingMetaDataId, isScreenData,
				isInterview, flvRecordingMetaDataDao);
	}

	public void packetReceived(IBroadcastStream broadcastStream,
			IStreamPacket streampacket) {
		try {

			CachedEvent cachedEvent = new CachedEvent();
			cachedEvent.setData(streampacket.getData().duplicate());
			cachedEvent.setDataType(streampacket.getDataType());
			cachedEvent.setTimestamp(streampacket.getTimestamp());
			cachedEvent.setCurrentTime(new Date());

			streamVideoWriter.append(cachedEvent);

		} catch (Exception e) {
			log.error("[packetReceived]", e);
		}
	}

	@Override
	public void closeStream() {
		streamVideoWriter.stop();
	}
	
}
