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
package org.apache.openmeetings.converter;

import static org.apache.openmeetings.util.OmFileHelper.getStreamsHibernateDir;
import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.openmeetings.db.dao.record.FlvRecordingDao;
import org.apache.openmeetings.db.dao.record.FlvRecordingLogDao;
import org.apache.openmeetings.db.dao.record.FlvRecordingMetaDataDao;
import org.apache.openmeetings.db.entity.record.FlvRecording;
import org.apache.openmeetings.db.entity.record.FlvRecordingMetaData;
import org.apache.openmeetings.db.entity.record.FlvRecordingMetaData.Status;
import org.apache.openmeetings.util.process.ConverterProcessResult;
import org.apache.openmeetings.util.process.ProcessHelper;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class FlvRecorderConverter extends BaseConverter {

	private static final Logger log = Red5LoggerFactory.getLogger(FlvRecorderConverter.class, webAppRootKey);

	// Spring loaded Beans
	@Autowired
	private FlvRecordingDao recordingDao;
	@Autowired
	private FlvRecordingMetaDataDao metaDataDao;
	@Autowired
	private FlvRecordingLogDao logDao;

	private String FFMPEG_MAP_PARAM = ":";

	public void startConversion(Long flvRecordingId) {
		try {
			if (isUseOldStyleFfmpegMap()) {
				FFMPEG_MAP_PARAM = ".";
			}

			FlvRecording flvRecording = recordingDao.get(flvRecordingId);
			log.debug("flvRecording " + flvRecording.getFlvRecordingId());

			// Strip Audio out of all Audio-FLVs
			stripAudioFromFLVs(flvRecording);

			// Add empty pieces at the beginning and end of the wav

		} catch (Exception err) {
			log.error("[startConversion]", err);
		}
	}

	private String getDifference(Date from, Date to) {
		long millis = from.getTime() - to.getTime();
		long hours = TimeUnit.MILLISECONDS.toHours(millis);
		millis -= TimeUnit.HOURS.toMillis(hours);
		long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
		millis -= TimeUnit.MINUTES.toMillis(minutes);
		long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
		millis -= TimeUnit.SECONDS.toMillis(seconds);
		return String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds, millis);
	}
	
	public void stripAudioFromFLVs(FlvRecording flvRecording) {
		List<ConverterProcessResult> returnLog = new ArrayList<ConverterProcessResult>();
		List<String> listOfFullWaveFiles = new LinkedList<String>();
		File streamFolder = getStreamFolder(flvRecording);
		try {
			FlvRecordingMetaData screenMetaData = metaDataDao.getScreenMetaDataByRecording(flvRecording.getFlvRecordingId());

			if (screenMetaData == null) {
				throw new Exception("screenMetaData is Null FlvRecordingId " + flvRecording.getFlvRecordingId());
			}

			if (screenMetaData.getStreamStatus() == Status.NONE) {
				throw new Exception("Stream has not been started, error in recording");
			}

			screenMetaData = waitForTheStream(screenMetaData.getFlvRecordingMetaDataId());

			stripAudioFirstPass(flvRecording, returnLog, listOfFullWaveFiles, streamFolder);

			// Merge Wave to Full Length
			String streamFolderGeneralName = getStreamsHibernateDir().getCanonicalPath() + File.separator; // FIXME

			String hashFileFullName = screenMetaData.getStreamName() + "_FINAL_WAVE.wav";
			String outputFullWav = new File(streamFolder, hashFileFullName).getCanonicalPath();

			if (listOfFullWaveFiles.size() == 1) {
				outputFullWav = listOfFullWaveFiles.get(0);
			} else if (listOfFullWaveFiles.size() > 0) {
				String[] argv_full_sox = mergeAudioToWaves(listOfFullWaveFiles, outputFullWav);

				returnLog.add(ProcessHelper.executeScript("mergeAudioToWaves", argv_full_sox));
			} else {
				// create default Audio to merge it. strip to content length
				String outputWav = streamFolderGeneralName + "one_second.wav";

				// Calculate delta at beginning
				Long deltaTimeMilliSeconds = flvRecording.getRecordEnd().getTime() - flvRecording.getRecordStart().getTime();
				Float deltaPadding = (Float.parseFloat(deltaTimeMilliSeconds.toString()) / 1000) - 1;

				String[] argv_full_sox = new String[] { getPathToSoX(), outputWav, outputFullWav, "pad", "0", deltaPadding.toString() };

				returnLog.add(ProcessHelper.executeScript("generateSampleAudio", argv_full_sox));
			}
			screenMetaData.setFullWavAudioData(hashFileFullName);
			metaDataDao.update(screenMetaData);

			// Merge Audio with Video / Calculate resulting FLV

			String inputScreenFullFlv = new File(streamFolder, screenMetaData.getStreamName() + ".flv")
					.getCanonicalPath();

			String hashFileFullNameFlv = "flvRecording_" + flvRecording.getFlvRecordingId() + ".flv";
			String outputFullFlv = streamFolderGeneralName + hashFileFullNameFlv;

			// ffmpeg -vcodec flv -qscale 9.5 -r 25 -ar 22050 -ab 32k -s 320x240
			// -i 65318fb5c54b1bc1b1bca077b493a914_28_12_2009_23_38_17_FINAL_WAVE.wav
			// -i 65318fb5c54b1bc1b1bca077b493a914_28_12_2009_23_38_17.flv
			// final1.flv

			int flvWidth = flvRecording.getWidth();
			int flvHeight = flvRecording.getHeight();

			log.debug("flvWidth -1- " + flvWidth);
			log.debug("flvHeight -1- " + flvHeight);

			flvWidth = Double.valueOf((Math.floor(flvWidth / 16)) * 16).intValue();
			flvHeight = Double.valueOf((Math.floor(flvHeight / 16)) * 16).intValue();

			log.debug("flvWidth -2- " + flvWidth);
			log.debug("flvHeight -2- " + flvHeight);

			flvRecording.setFlvWidth(flvWidth);
			flvRecording.setFlvHeight(flvHeight);

			String[] argv_fullFLV = new String[] { getPathToFFMPEG(), //
					"-itsoffset", getDifference(screenMetaData.getRecordStart(), screenMetaData.getFlvRecording().getRecordStart()),
					"-i", inputScreenFullFlv, "-i", outputFullWav, "-ar", "22050", //
					"-acodec", "libmp3lame", //
					"-ab", "32k", //
					"-s", flvWidth + "x" + flvHeight, //
					"-vcodec", "flashsv", //
					"-map", "0" + FFMPEG_MAP_PARAM + "0", //
					"-map", "1" + FFMPEG_MAP_PARAM + "0", //
					outputFullFlv };

			returnLog.add(ProcessHelper.executeScript("generateFullFLV", argv_fullFLV));

			flvRecording.setFileHash(hashFileFullNameFlv);

			// Extract first Image for preview purpose
			// ffmpeg -i movie.flv -vcodec mjpeg -vframes 1 -an -f rawvideo -s
			// 320x240 movie.jpg

			String hashFileFullNameJPEG = "flvRecording_" + flvRecording.getFlvRecordingId() + ".jpg";
			String outPutJpeg = streamFolderGeneralName + hashFileFullNameJPEG;

			flvRecording.setPreviewImage(hashFileFullNameJPEG);

			String[] argv_previewFLV = new String[] { //
					getPathToFFMPEG(), //
					"-i", outputFullFlv, //
					"-vcodec", "mjpeg", //
					"-vframes", "1", "-an", //
					"-f", "rawvideo", //
					"-s", flvWidth + "x" + flvHeight, //
					outPutJpeg };

			returnLog.add(ProcessHelper.executeScript("previewFullFLV", argv_previewFLV));

			String alternateDownloadName = "flvRecording_" + flvRecording.getFlvRecordingId() + ".avi";
			String alternateDownloadFullName = streamFolderGeneralName + alternateDownloadName;

			String[] argv_alternateDownload = new String[] { getPathToFFMPEG(), "-i", outputFullFlv, "-vcodec",
					"copy", alternateDownloadFullName };

			returnLog.add(ProcessHelper.executeScript("alternateDownload", argv_alternateDownload));

			flvRecording.setAlternateDownload(alternateDownloadName);

			recordingDao.update(flvRecording);
			convertToMp4(flvRecording, returnLog);

			for (ConverterProcessResult returnMap : returnLog) {
				logDao.addFLVRecordingLog("generateFFMPEG", flvRecording, returnMap);
			}

			// Delete Wave Files
			for (String fileName : listOfFullWaveFiles) {
				File audio = new File(fileName);
				if (audio.exists()) {
					audio.delete();
				}
			}

		} catch (Exception err) {
			log.error("[stripAudioFromFLVs]", err);
		}
	}

}
