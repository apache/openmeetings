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
package org.apache.openmeetings.core.data.record.listener.async;

import static org.apache.openmeetings.util.OpenmeetingsVariables.getWebAppRootKey;
import static org.red5.server.net.rtmp.event.VideoData.FrameType.KEYFRAME;

import java.util.Date;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.openmeetings.db.entity.record.RecordingMetaData;
import org.red5.logging.Red5LoggerFactory;
import org.red5.server.api.scope.IScope;
import org.slf4j.Logger;

public class StreamVideoWriter extends BaseStreamWriter {
	private static final Logger log = Red5LoggerFactory.getLogger(StreamVideoWriter.class, getWebAppRootKey());
	private Date startedSessionScreenTimeDate = null;

	public StreamVideoWriter(String streamName, IScope scope, Long metaDataId, boolean isScreenData) {
		super(streamName, scope, metaDataId, isScreenData);
	}

	@Override
	public void packetReceived(CachedEvent streampacket) {
		try {
			int timeStamp = streampacket.getTimestamp();
			log.trace("incoming timeStamp :: " + timeStamp);
			if (startTimeStamp == -1 && KEYFRAME != streampacket.getFrameType()) {
				//skip until keyframe
				log.trace("no KEYFRAME, skipping :: {}", streampacket.getFrameType());
				return;
			}
			if (timeStamp <= 0) {
				log.warn("Negative TimeStamp");
				return;
			}
			IoBuffer data = streampacket.getData().asReadOnlyBuffer();
			if (data.limit() == 0) {
				log.trace("Data.limit() == 0");
				return;
			}
			Date virtualTime = streampacket.getCurrentTime();

			if (startedSessionScreenTimeDate == null) {
				startedSessionScreenTimeDate = virtualTime;

				RecordingMetaData metaData = metaDataDao.get(metaDataId);
				metaData.setRecordStart(virtualTime);
				metaDataDao.update(metaData);
			}

			if (startTimeStamp == -1) {
				// That will be not bigger then long value
				startTimeStamp = timeStamp;
			}
			timeStamp -= startTimeStamp;

			write(timeStamp, streampacket.getDataType(), data);
		} catch (Exception e) {
			log.error("[packetReceived]", e);
		}
	}

	@Override
	protected void internalCloseStream() {
		//should be empty for video writer
	}
}
