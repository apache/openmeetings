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
package org.apache.openmeetings.data.flvrecord.listener.async;

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;
import static org.red5.server.net.rtmp.event.VideoData.FrameType.KEYFRAME;

import java.util.Date;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.openmeetings.db.dao.record.FlvRecordingMetaDataDao;
import org.apache.openmeetings.db.entity.record.FlvRecordingMetaData;
import org.red5.io.ITag;
import org.red5.io.flv.impl.Tag;
import org.red5.logging.Red5LoggerFactory;
import org.red5.server.api.scope.IScope;
import org.slf4j.Logger;

public class StreamVideoWriter extends BaseStreamWriter {
	private static final Logger log = Red5LoggerFactory.getLogger(StreamVideoWriter.class, webAppRootKey);
	private Date startedSessionScreenTimeDate = null;
	private final FlvRecordingMetaDataDao metaDataDao;

	public StreamVideoWriter(String streamName, IScope scope, Long metaDataId, boolean isScreenData,
			boolean isInterview, FlvRecordingMetaDataDao metaDataDao) {

		super(streamName, scope, metaDataId, isScreenData);

		this.metaDataDao = metaDataDao;

		FlvRecordingMetaData metaData = metaDataDao.get(metaDataId);
		metaData.setStreamReaderThreadComplete(false);
		metaDataDao.update(metaData);
	}

	@Override
	public void packetReceived(CachedEvent streampacket) {
		try {
			int timeStamp = streampacket.getTimestamp();
			log.trace("incoming timeStamp :: " + timeStamp);
			if (startTimeStamp == -1 && KEYFRAME != streampacket.getFrameType()) {
				//skip until keyframe
				log.trace("no KEYFRAME, skipping ::" + streampacket.getFrameType());
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
				// Calculate the delta between the initial start and the first packet data
				initialDelta = startedSessionScreenTimeDate.getTime() - startedSessionTimeDate.getTime();

				// This is important for the Interview Post Processing to get the time between starting the stream and
				// the actual Access to the webcam by the Flash Security Dialog
				metaDataDao.updateFlvRecordingMetaDataInitialGap(metaDataId, initialDelta);
			}

			if (startTimeStamp == -1) {
				// That will be not bigger then long value
				startTimeStamp = timeStamp;
			}

			timeStamp -= startTimeStamp;

			log.trace("timeStamp :: " + timeStamp);
			ITag tag = new Tag();
			tag.setDataType(streampacket.getDataType());

			tag.setBodySize(data.limit());
			tag.setTimestamp(timeStamp);
			tag.setBody(data);

			writer.writeTag(tag);
		} catch (Exception e) {
			log.error("[packetReceived]", e);
		}
	}

	@Override
	public void closeStream() {
		try {
			writer.close();

			// Add Delta in the beginning, this Delta is the Gap between the device chosen and when the User hits the
			// button in the Flash Security Warning
			FlvRecordingMetaData metaData = metaDataDao.get(metaDataId);

			metaData.setRecordStart(new Date(metaData.getRecordStart().getTime() + initialDelta));

			metaData.setStreamReaderThreadComplete(true);

			metaDataDao.update(metaData);
		} catch (Exception err) {
			log.error("[closeStream]", err);
		}
	}
}
