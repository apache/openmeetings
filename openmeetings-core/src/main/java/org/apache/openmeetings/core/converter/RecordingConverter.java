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

import static org.apache.openmeetings.util.OmFileHelper.FLV_EXTENSION;
import static org.apache.openmeetings.util.OmFileHelper.getStreamsHibernateDir;
import static org.apache.openmeetings.util.OmFileHelper.recordingFileName;
import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.openmeetings.db.dao.record.RecordingDao;
import org.apache.openmeetings.db.dao.record.RecordingLogDao;
import org.apache.openmeetings.db.dao.record.RecordingMetaDataDao;
import org.apache.openmeetings.db.entity.record.Recording;
import org.apache.openmeetings.db.entity.record.RecordingMetaData;
import org.apache.openmeetings.db.entity.record.RecordingMetaData.Status;
import org.apache.openmeetings.util.process.ConverterProcessResult;
import org.apache.openmeetings.util.process.ProcessHelper;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class RecordingConverter extends BaseConverter implements IRecordingConverter {
	private static final Logger log = Red5LoggerFactory.getLogger(RecordingConverter.class, webAppRootKey);

	// Spring loaded Beans
	@Autowired
	private RecordingDao recordingDao;
	@Autowired
	private RecordingMetaDataDao metaDataDao;
	@Autowired
	private RecordingLogDao logDao;

	private String FFMPEG_MAP_PARAM = ":";

	@Override
	public void startConversion(Long recordingId) {
		Recording recording = recordingDao.get(recordingId);
		if (recording == null) {
			log.warn("Conversion is NOT started. Recording with ID {} is not found", recordingId);
			return;
		}
		try {
			if (isUseOldStyleFfmpegMap()) {
				FFMPEG_MAP_PARAM = ".";
			}

			String finalNamePrefix = recordingFileName + recordingId;
			log.debug("recording " + recording.getId());

			List<ConverterProcessResult> returnLog = new ArrayList<ConverterProcessResult>();
			List<String> listOfFullWaveFiles = new ArrayList<String>();
			File streamFolder = getStreamFolder(recording);
			
			RecordingMetaData screenMetaData = metaDataDao.getScreenMetaDataByRecording(recording.getId());

			if (screenMetaData == null) {
				throw new Exception("screenMetaData is Null recordingId " + recording.getId());
			}

			if (screenMetaData.getStreamStatus() == Status.NONE) {
				throw new Exception("Stream has not been started, error in recording");
			}
			recording.setStatus(Recording.Status.CONVERTING);
			recording = recordingDao.update(recording);

			screenMetaData = waitForTheStream(screenMetaData.getId());

			stripAudioFirstPass(recording, returnLog, listOfFullWaveFiles, streamFolder);

			// Merge Wave to Full Length
			String hashFileFullName = screenMetaData.getStreamName() + "_FINAL_WAVE.wav";
			String outputFullWav = new File(streamFolder, hashFileFullName).getCanonicalPath();

			if (listOfFullWaveFiles.size() == 1) {
				outputFullWav = listOfFullWaveFiles.get(0);
			} else if (listOfFullWaveFiles.size() > 0) {
				String[] argv_full_sox = mergeAudioToWaves(listOfFullWaveFiles, outputFullWav);

				returnLog.add(ProcessHelper.executeScript("mergeAudioToWaves", argv_full_sox));
			} else {
				// create default Audio to merge it. strip to content length
				String outputWav = new File(getStreamsHibernateDir(), "one_second.wav").getCanonicalPath();

				// Calculate delta at beginning
				double deltaPadding = diffSeconds(recording.getRecordEnd(), recording.getRecordStart());

				String[] argv_full_sox = new String[] { getPathToSoX(), outputWav, outputFullWav, "pad", "0", "" + deltaPadding };

				returnLog.add(ProcessHelper.executeScript("generateSampleAudio", argv_full_sox));
			}
			screenMetaData.setFullWavAudioData(hashFileFullName);
			metaDataDao.update(screenMetaData);

			// Merge Audio with Video / Calculate resulting FLV

			String inputScreenFullFlv = new File(streamFolder, screenMetaData.getStreamName() + FLV_EXTENSION).getCanonicalPath();

			File outputFullFlv = new File(getStreamsHibernateDir(), finalNamePrefix + FLV_EXTENSION);

			// ffmpeg -vcodec flv -qscale 9.5 -r 25 -ar 22050 -ab 32k -s 320x240
			// -i 65318fb5c54b1bc1b1bca077b493a914_28_12_2009_23_38_17_FINAL_WAVE.wav
			// -i 65318fb5c54b1bc1b1bca077b493a914_28_12_2009_23_38_17.flv
			// final1.flv

			int flvWidth = recording.getWidth();
			int flvHeight = recording.getHeight();

			log.debug("flvWidth -1- " + flvWidth);
			log.debug("flvHeight -1- " + flvHeight);

			flvWidth = Double.valueOf((Math.floor(flvWidth / 16)) * 16).intValue();
			flvHeight = Double.valueOf((Math.floor(flvHeight / 16)) * 16).intValue();

			log.debug("flvWidth -2- " + flvWidth);
			log.debug("flvHeight -2- " + flvHeight);

			recording.setFlvWidth(flvWidth);
			recording.setFlvHeight(flvHeight);

			String[] argv_fullFLV = new String[] { getPathToFFMPEG(), "-y",//
					"-itsoffset", formatMillis(diff(screenMetaData.getRecordStart(), recording.getRecordStart())),
					"-i", inputScreenFullFlv, "-i", outputFullWav, "-ar", "22050", //
					"-acodec", "libmp3lame", //
					"-ab", "32k", //
					"-s", flvWidth + "x" + flvHeight, //
					"-vcodec", "flashsv", //
					"-map", "0" + FFMPEG_MAP_PARAM + "0", //
					"-map", "1" + FFMPEG_MAP_PARAM + "0", //
					outputFullFlv.getCanonicalPath() };

			returnLog.add(ProcessHelper.executeScript("generateFullFLV", argv_fullFLV));

			recording.setHash(outputFullFlv.getName());

			// Extract first Image for preview purpose
			// ffmpeg -i movie.flv -vcodec mjpeg -vframes 1 -an -f rawvideo -s
			// 320x240 movie.jpg

			File outPutJpeg = new File(getStreamsHibernateDir(), finalNamePrefix + ".jpg");

			recording.setPreviewImage(outPutJpeg.getName());

			String[] argv_previewFLV = new String[] { //
					getPathToFFMPEG(), "-y",//
					"-i", outputFullFlv.getCanonicalPath(), //
					"-vcodec", "mjpeg", //
					"-vframes", "1", "-an", //
					"-f", "rawvideo", //
					"-s", flvWidth + "x" + flvHeight, //
					outPutJpeg.getCanonicalPath() };

			returnLog.add(ProcessHelper.executeScript("previewFullFLV", argv_previewFLV));

			File alternateDownload = new File(getStreamsHibernateDir(), finalNamePrefix + ".avi");

			String[] argv_alternateDownload = new String[] { getPathToFFMPEG(), "-y", "-i", outputFullFlv.getCanonicalPath(), "-vcodec",
					"copy", alternateDownload.getCanonicalPath() };

			returnLog.add(ProcessHelper.executeScript("alternateDownload", argv_alternateDownload));

			recording.setAlternateDownload(alternateDownload.getName());

			updateDuration(recording);
			convertToMp4(recording, returnLog);
			recording.setStatus(Recording.Status.PROCESSED);

			logDao.deleteByRecordingId(recording.getId());
			for (ConverterProcessResult returnMap : returnLog) {
				logDao.add("generateFFMPEG", recording, returnMap);
			}

			// Delete Wave Files
			for (String fileName : listOfFullWaveFiles) {
				File audio = new File(fileName);
				if (audio.exists()) {
					audio.delete();
				}
			}

		} catch (Exception err) {
			log.error("[startConversion]", err);
			recording.setStatus(Recording.Status.ERROR);
		}
		recordingDao.update(recording);
	}
}
