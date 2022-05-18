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
package org.apache.openmeetings.core.converter;

import static java.util.UUID.randomUUID;
import static org.apache.openmeetings.util.CalendarHelper.formatMillis;
import static org.apache.openmeetings.util.OmFileHelper.getRecordingChunk;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.openmeetings.db.entity.record.Recording;
import org.apache.openmeetings.db.entity.record.RecordingChunk;
import org.apache.openmeetings.db.entity.record.RecordingChunk.Status;
import org.apache.openmeetings.util.process.ProcessResultList;
import org.apache.wicket.util.string.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class RecordingConverter extends BaseConverter implements IRecordingConverter {
	private static final Logger log = LoggerFactory.getLogger(RecordingConverter.class);

	@Override
	public void startConversion(Recording r) {
		if (r == null) {
			log.warn("Conversion is NOT started. Recording passed is NULL");
			return;
		}
		ProcessResultList logs = new ProcessResultList();
		List<File> waveFiles = new ArrayList<>();
		try {
			log.debug("recording {}", r.getId());

			File streamFolder = getStreamFolder(r);

			RecordingChunk screenChunk = chunkDao.getScreenByRecording(r.getId());

			if (screenChunk == null) {
				throw new ConversionException("screenMetaData is Null recordingId " + r.getId());
			}

			if (screenChunk.getStreamStatus() == Status.NONE) {
				printChunkInfo(screenChunk, "StartConversion");
				throw new ConversionException("Stream has not been started, error in recording");
			}
			if (Strings.isEmpty(r.getHash())) {
				r.setHash(randomUUID().toString());
			}
			r.setStatus(Recording.Status.CONVERTING);
			r = recordingDao.update(r);

			screenChunk = waitForTheStream(screenChunk.getId());

			// Merge Wave to Full Length
			File wav = new File(streamFolder, screenChunk.getStreamName() + "_FINAL_WAVE.wav");
			createWav(r, logs, streamFolder, waveFiles, wav, null);

			chunkDao.update(screenChunk);

			// Merge Audio with Video / Calculate resulting FLV

			String inputScreenFullFlv = getRecordingChunk(r.getRoomId(), screenChunk.getStreamName()).getCanonicalPath();

			// ffmpeg -vcodec flv -qscale 9.5 -r 25 -ar 22050 -ab 32k -s 320x240
			// -i 65318fb5c54b1bc1b1bca077b493a914_28_12_2009_23_38_17_FINAL_WAVE.wav
			// -i 65318fb5c54b1bc1b1bca077b493a914_28_12_2009_23_38_17.flv
			// final1.flv

			String mp4path = convertToMp4(r, List.of(
					"-itsoffset", formatMillis(diff(screenChunk.getStart(), r.getRecordStart())),
					"-i", inputScreenFullFlv, "-i", wav.getCanonicalPath()
					), false, logs);
			Dimension dim = getDimension(logs.getLast().getError(), null); // will return 100x100 for non-video to be able to play
			if (dim != null) {
				r.setWidth(dim.width());
				r.setHeight(dim.height());
			}

			finalizeRec(r, mp4path, logs);
		} catch (Exception err) {
			log.error("[startConversion]", err);
			r.setStatus(Recording.Status.ERROR);
		}
		postProcess(r, logs);
		postProcess(waveFiles);
		recordingDao.update(r);
	}
}
