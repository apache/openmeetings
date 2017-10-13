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
package org.apache.openmeetings.core.data.record.listener;

import static org.apache.openmeetings.util.OpenmeetingsVariables.getWebAppRootKey;

import java.util.Date;

import org.apache.openmeetings.core.data.record.listener.async.BaseStreamWriter;
import org.apache.openmeetings.core.data.record.listener.async.CachedEvent;
import org.apache.openmeetings.core.data.record.listener.async.StreamAudioWriter;
import org.apache.openmeetings.core.data.record.listener.async.StreamVideoWriter;
import org.red5.logging.Red5LoggerFactory;
import org.red5.server.api.scope.IScope;
import org.red5.server.api.stream.IBroadcastStream;
import org.red5.server.api.stream.IStreamListener;
import org.red5.server.api.stream.IStreamPacket;
import org.red5.server.net.rtmp.event.VideoData;
import org.slf4j.Logger;

public class StreamListener implements IStreamListener {
	private static final Logger log = Red5LoggerFactory.getLogger(StreamListener.class, getWebAppRootKey());

	private final BaseStreamWriter streamWriter;

	public StreamListener(boolean isAudio, String streamName, IScope scope, Long metaDataId,
			boolean isScreenData, boolean isInterview) {
		streamWriter = isAudio
			? new StreamAudioWriter(streamName, scope, metaDataId, isScreenData, isInterview)
			: new StreamVideoWriter(streamName, scope, metaDataId, isScreenData);
	}

	@Override
	public void packetReceived(IBroadcastStream broadcastStream, IStreamPacket streampacket) {
		try {
			CachedEvent cachedEvent = new CachedEvent();
			cachedEvent.setData(streampacket.getData().duplicate());
			cachedEvent.setDataType(streampacket.getDataType());
			cachedEvent.setTimestamp(streampacket.getTimestamp());
			cachedEvent.setCurrentTime(new Date());
			if (streampacket instanceof VideoData) {
				cachedEvent.setFrameType(((VideoData) streampacket).getFrameType());
			}

			if (log.isTraceEnabled()) {
				log.trace("##REC:: Packet recieved. type: {} frame type: {}", cachedEvent.getDataType(), cachedEvent.getFrameType());
			}
			streamWriter.append(cachedEvent);
		} catch (Exception e) {
			log.error("##REC:: [packetReceived]", e);
		}
	}

	public void closeStream() {
		streamWriter.stop();
	}
}
