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
package org.openmeetings.app.data.flvrecord.listener.async;

import java.io.IOException;
import java.util.Date;

import org.apache.mina.core.buffer.IoBuffer;
import org.openmeetings.app.OpenmeetingsVariables;
import org.openmeetings.app.data.flvrecord.FlvRecordingMetaDataDaoImpl;
import org.openmeetings.app.persistence.beans.flvrecord.FlvRecordingMetaData;
import org.red5.io.ITag;
import org.red5.io.flv.impl.Tag;
import org.red5.logging.Red5LoggerFactory;
import org.red5.server.api.scope.IScope;
import org.slf4j.Logger;

public class StreamVideoWriter extends BaseStreamWriter {
	
	private int startTimeStamp = -1;

	private Date startedSessionScreenTimeDate = null;

	private long initialDelta = 0;
	
	private final FlvRecordingMetaDataDaoImpl flvRecordingMetaDataDao;

	private static final Logger log = Red5LoggerFactory.getLogger(
			StreamVideoWriter.class, OpenmeetingsVariables.webAppRootKey);
	
	public StreamVideoWriter(String streamName, IScope scope,
			Long flvRecordingMetaDataId, boolean isScreenData,
			boolean isInterview,
			FlvRecordingMetaDataDaoImpl flvRecordingMetaDataDao) {
		
		super(streamName, scope, flvRecordingMetaDataId, isScreenData);
		
		this.flvRecordingMetaDataDao = flvRecordingMetaDataDao;
		
		FlvRecordingMetaData flvRecordingMetaData = flvRecordingMetaDataDao.
				getFlvRecordingMetaDataById(flvRecordingMetaDataId);
		flvRecordingMetaData.setStreamReaderThreadComplete(false);
		flvRecordingMetaDataDao.updateFlvRecordingMetaData(flvRecordingMetaData);
	}
	
	@Override
	public void packetReceived(CachedEvent streampacket) {
		try {

			// We only are concerned about video at this moment
			// if (streampacket.getDataType() == 9) {
			
			int timeStamp = streampacket.getTimestamp();
			Date virtualTime = streampacket.getCurrentTime();

			if (this.startedSessionScreenTimeDate == null) {

				this.startedSessionScreenTimeDate = virtualTime;

				// Calculate the delta between the initial start and the first
				// packet data

				this.initialDelta = this.startedSessionScreenTimeDate.getTime()
						- this.startedSessionTimeDate.getTime();

				// This is important for the Interview Post Processing to get
				// the time between starting the stream and the actual Access to
				// the
				// webcam by the Flash Security Dialog
				flvRecordingMetaDataDao.updateFlvRecordingMetaDataInitialGap(
						flvRecordingMetaDataId, this.initialDelta);

			}

			if (streampacket.getTimestamp() <= 0) {
				log.warn("Negative TimeStamp");
				return;
			}

			IoBuffer data = streampacket.getData().asReadOnlyBuffer();

			if (data.limit() == 0) {
				return;
			}

			if (startTimeStamp == -1) {
				// That will be not bigger then long value
				startTimeStamp = streampacket.getTimestamp();
			}

			timeStamp -= startTimeStamp;

			ITag tag = new Tag();
			tag.setDataType(streampacket.getDataType());

			// log.debug("data.limit() :: "+data.limit());
			tag.setBodySize(data.limit());
			tag.setTimestamp(timeStamp);
			tag.setBody(data);

//			if (this.isInterview) {
//				if (timeStamp <= 500) {
//					// We will cut the first 0.5 seconds
//					// The First seconds seem to break the Recording Video often
//					return;
//				}
//			}
			
			writer.writeTag(tag);

		} catch (IOException e) {
			log.error("[packetReceived]", e);
		} catch (Exception e) {
			log.error("[packetReceived]", e);
		}
	}

	@Override
	public void closeStream() {
		try {
			
			writer.close();

			// Add Delta in the beginning, this Delta is the Gap between the
			// device chosen and when the User hits the button in the Flash
			// Security Warning
			FlvRecordingMetaData flvRecordingMetaData = flvRecordingMetaDataDao
					.getFlvRecordingMetaDataById(this.flvRecordingMetaDataId);

			flvRecordingMetaData.setRecordStart(new Date(
					flvRecordingMetaData.getRecordStart().getTime()
							+ this.initialDelta));
			
			flvRecordingMetaData.setStreamReaderThreadComplete(true);
			
			flvRecordingMetaDataDao
					.updateFlvRecordingMetaData(flvRecordingMetaData);
			
			

		} catch (Exception err) {
			log.error("[closeStream]", err);
		}
	}

}
