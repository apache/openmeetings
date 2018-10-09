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

import static org.apache.openmeetings.core.remote.ScopeApplicationAdapter.getApp;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getWebAppRootKey;
import static org.red5.io.IoConstants.TYPE_AUDIO;
import static org.red5.server.net.rtmp.event.VideoData.FrameType.KEYFRAME;

import java.util.Date;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.openmeetings.db.dao.record.RecordingMetaDeltaDao;
import org.apache.openmeetings.db.entity.record.RecordingMetaData;
import org.apache.openmeetings.db.entity.record.RecordingMetaDelta;
import org.red5.logging.Red5LoggerFactory;
import org.red5.server.api.scope.IScope;
import org.slf4j.Logger;

public class StreamAudioWriter extends BaseStreamWriter {
	private static final Logger log = Red5LoggerFactory.getLogger(StreamAudioWriter.class, getWebAppRootKey());

	private int duration = 0;

	private Integer lastTimeStamp = -1;
	private Date lastcurrentTime = null;

	private int lastStreamPacketTimeStamp = -1;

	private long byteCount = 0;

	// Autowire is not possible
	protected final RecordingMetaDeltaDao metaDeltaDao;

	private boolean isInterview = false;

	public StreamAudioWriter(String streamName, IScope scope, Long metaDataId, boolean isScreenData, boolean isInterview) {
		super(streamName, scope, metaDataId, isScreenData);

		this.metaDeltaDao = getApp().getOmBean(RecordingMetaDeltaDao.class);
		this.isInterview = isInterview;
	}

	@Override
	public void packetReceived(CachedEvent streampacket) {
		try {
			// We only care about audio at this moment
			if (isInterview || TYPE_AUDIO == streampacket.getDataType()) {
				if (streampacket.getTimestamp() <= 0) {
					log.warn("##REC:: Negative TimeStamp");
					return;
				}
				// we should not skip audio data in case it is Audio only interview
				if ((isInterview || isScreenData) && startTimeStamp == -1 && KEYFRAME != streampacket.getFrameType()) {
					//skip until keyframe
					log.trace("##REC:: no KEYFRAME, skipping");
					return;
				}
				IoBuffer data = streampacket.getData().asReadOnlyBuffer();
				if (data.limit() == 0) {
					log.trace("##REC:: data.limit() == 0 ");
					return;
				}

				byteCount += data.limit();

				lastcurrentTime = streampacket.getCurrentTime();
				int timeStamp = streampacket.getTimestamp();
				Date virtualTime = streampacket.getCurrentTime();

				if (startTimeStamp == -1) {
					// Calculate the delta between the initial start and the first audio-packet data

					initialDelta = virtualTime.getTime() - startedSessionTimeDate.getTime();

					RecordingMetaDelta metaDelta = new RecordingMetaDelta();

					metaDelta.setDeltaTime(initialDelta);
					metaDelta.setMetaDataId(metaDataId);
					metaDelta.setTimeStamp(0);
					metaDelta.setDebugStatus("INIT AUDIO");
					metaDelta.setStartPadding(true);
					metaDelta.setEndPadding(false);
					metaDelta.setDataLengthPacket(data.limit());
					metaDelta.setReceivedAudioDataLength(byteCount);
					metaDelta.setStartTime(startedSessionTimeDate);
					metaDelta.setPacketTimeStamp(streampacket.getTimestamp());

					Long deltaTimeStamp = virtualTime.getTime() - startedSessionTimeDate.getTime();

					metaDelta.setDuration(0);

					Long missingTime = deltaTimeStamp - 0;

					metaDelta.setMissingTime(missingTime);

					metaDelta.setCurrentTime(virtualTime);
					metaDelta.setDeltaTimeStamp(deltaTimeStamp);
					metaDelta.setStartTimeStamp(startTimeStamp);

					metaDeltaDao.add(metaDelta);

					// That will be not bigger then long value
					startTimeStamp = streampacket.getTimestamp();

					RecordingMetaData metaData = metaDataDao.get(metaDataId);
					metaData.setRecordStart(virtualTime);
					metaDataDao.update(metaData);
				}

				lastStreamPacketTimeStamp = streampacket.getTimestamp();

				timeStamp -= startTimeStamp;

				// Offset at the beginning is calculated above
				long deltaTime = lastTimeStamp == -1 ? 0 : timeStamp - lastTimeStamp;

				Long preLastTimeStamp = Long.parseLong(lastTimeStamp.toString());

				lastTimeStamp = timeStamp;

				if (deltaTime > 75) {
					RecordingMetaDelta metaDelta = new RecordingMetaDelta();

					metaDelta.setDeltaTime(deltaTime);
					metaDelta.setMetaDataId(metaDataId);
					metaDelta.setTimeStamp(timeStamp);
					metaDelta.setDebugStatus("RUN AUDIO");
					metaDelta.setStartPadding(false);
					metaDelta.setLastTimeStamp(preLastTimeStamp);
					metaDelta.setEndPadding(false);
					metaDelta.setDataLengthPacket(data.limit());
					metaDelta.setReceivedAudioDataLength(byteCount);
					metaDelta.setStartTime(startedSessionTimeDate);
					metaDelta.setPacketTimeStamp(streampacket.getTimestamp());

					Date curDate = new Date();
					Long deltaTimeStamp = curDate.getTime() - startedSessionTimeDate.getTime();

					duration = Math.max(duration, timeStamp + writer.getOffset());
					metaDelta.setDuration(duration);

					Long missingTime = deltaTimeStamp - timeStamp;

					metaDelta.setMissingTime(missingTime);

					metaDelta.setCurrentTime(curDate);
					metaDelta.setDeltaTimeStamp(deltaTimeStamp);
					metaDelta.setStartTimeStamp(startTimeStamp);

					metaDeltaDao.add(metaDelta);
				}

				log.trace("##REC:: timeStamp :: {}", timeStamp);
				write(timeStamp, streampacket.getDataType(), data);
			}
		} catch (Exception e) {
			log.error("##REC:: [packetReceived]", e);
		}
	}

	@Override
	protected void internalCloseStream() {
		try {
			// We do not add any End Padding or count the gaps for the
			// Screen Data, cause there is no!

			Date virtualTime = lastcurrentTime;
			log.debug("##REC:: virtualTime: {}", virtualTime);
			log.debug("##REC:: startedSessionTimeDate: {}", startedSessionTimeDate);

			long deltaRecordingTime = virtualTime == null ? 0 : virtualTime.getTime() - startedSessionTimeDate.getTime();

			log.debug("##REC:: lastTimeStamp :closeStream: {}", lastTimeStamp);
			log.debug("##REC:: lastStreamPacketTimeStamp :closeStream: {}", lastStreamPacketTimeStamp);
			log.debug("##REC:: deltaRecordingTime :closeStream: {}", deltaRecordingTime);

			long deltaTimePaddingEnd = deltaRecordingTime - lastTimeStamp - initialDelta;

			log.debug("##REC:: deltaTimePaddingEnd :: {}", deltaTimePaddingEnd);

			RecordingMetaDelta metaDelta = new RecordingMetaDelta();

			metaDelta.setDeltaTime(deltaTimePaddingEnd);
			metaDelta.setMetaDataId(metaDataId);
			metaDelta.setTimeStamp(lastTimeStamp);
			metaDelta.setDebugStatus("END AUDIO");
			metaDelta.setStartPadding(false);
			metaDelta.setEndPadding(true);
			metaDelta.setDataLengthPacket(null);
			metaDelta.setReceivedAudioDataLength(byteCount);
			metaDelta.setStartTime(startedSessionTimeDate);
			metaDelta.setCurrentTime(new Date());

			metaDeltaDao.add(metaDelta);
		} catch (Exception err) {
			log.error("##REC:: [internalCloseStream]", err);
		}
	}
}
