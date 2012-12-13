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
package org.apache.openmeetings.data.flvrecord.converter;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.data.flvrecord.FlvRecordingDao;
import org.apache.openmeetings.data.flvrecord.FlvRecordingLogDao;
import org.apache.openmeetings.data.flvrecord.FlvRecordingMetaDataDao;
import org.apache.openmeetings.documents.beans.ConverterProcessResult;
import org.apache.openmeetings.persistence.beans.flvrecord.FlvRecording;
import org.apache.openmeetings.persistence.beans.flvrecord.FlvRecordingMetaData;
import org.apache.openmeetings.utils.ProcessHelper;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class FlvRecorderConverter extends BaseConverter {

	private static final Logger log = Red5LoggerFactory
			.getLogger(FlvRecorderConverter.class, OpenmeetingsVariables.webAppRootKey);

	// Spring loaded Beans
	@Autowired
	private FlvRecordingDao flvRecordingDaoImpl = null;
	@Autowired
	private FlvRecordingMetaDataDao flvRecordingMetaDataDaoImpl = null;
	@Autowired
	private FlvRecordingLogDao flvRecordingLogDaoImpl;

	private String FFMPEG_MAP_PARAM = ":";

	public void startConversion(Long flvRecordingId) {
		try {
			if (isUseOldStyleFfmpegMap()) {
				FFMPEG_MAP_PARAM = ".";
			}

			FlvRecording flvRecording = this.flvRecordingDaoImpl
					.getFlvRecordingById(flvRecordingId);
			log.debug("flvRecording " + flvRecording.getFlvRecordingId());

			// Strip Audio out of all Audio-FLVs
			this.stripAudioFromFLVs(flvRecording);

			// Add empty pieces at the beginning and end of the wav

		} catch (Exception err) {
			log.error("[startConversion]", err);
		}
	}

	public void stripAudioFromFLVs(FlvRecording flvRecording) {
		List<ConverterProcessResult> returnLog = new ArrayList<ConverterProcessResult>();
		List<String> listOfFullWaveFiles = new LinkedList<String>();
		File streamFolder = getStreamFolder(flvRecording);
		try {
			stripAudioFirstPass(flvRecording, returnLog, listOfFullWaveFiles, streamFolder);

			// Merge Wave to Full Length
			String streamFolderGeneralName = getStreamFolder().getCanonicalPath() + File.separator; //FIXME

			FlvRecordingMetaData flvRecordingMetaDataOfScreen = this.flvRecordingMetaDataDaoImpl
					.getFlvRecordingMetaDataScreenFlvByRecording(flvRecording
							.getFlvRecordingId());
			
			if (flvRecordingMetaDataOfScreen == null) {
				throw new Exception("flvRecordingMetaDataOfScreen is Null FlvRecordingId "+flvRecording
							.getFlvRecordingId());
			}
			
			if (flvRecordingMetaDataOfScreen.getStreamReaderThreadComplete() == null) {
				throw new Exception("StreamReaderThreadComplete Bit is NULL, error in recording");
			}
			
			if (!flvRecordingMetaDataOfScreen.getStreamReaderThreadComplete()) {
				
				log.debug("### meta ScreenStream not yet written to disk" + flvRecordingMetaDataOfScreen.getFlvRecordingMetaDataId());
				boolean doStop = true;
				while(doStop) {
					
					log.debug("### Stream not yet written Thread Sleep - " + flvRecordingMetaDataOfScreen.getFlvRecordingMetaDataId());
					
					Thread.sleep(100L);
					
					flvRecordingMetaDataOfScreen = flvRecordingMetaDataDaoImpl.getFlvRecordingMetaDataById(flvRecordingMetaDataOfScreen.getFlvRecordingMetaDataId());
					
					if (flvRecordingMetaDataOfScreen.getStreamReaderThreadComplete()) {
						log.debug("### Screen Stream now written Thread continue - " );
						doStop = false;
					}
				}
			}
			
			
			String hashFileFullName = flvRecordingMetaDataOfScreen
					.getStreamName() + "_FINAL_WAVE.wav";
			String outputFullWav = new File(streamFolder, hashFileFullName).getCanonicalPath();

			if (listOfFullWaveFiles.size() == 1) {

				outputFullWav = listOfFullWaveFiles.get(0);

				flvRecordingMetaDataOfScreen
						.setFullWavAudioData(hashFileFullName);

			} else if (listOfFullWaveFiles.size() > 0) {

				String[] argv_full_sox = mergeAudioToWaves(listOfFullWaveFiles, outputFullWav);

				log.debug("START mergeAudioToWaves ################# ");
				log.debug(argv_full_sox.toString());
				String iString = "";
				for (int i = 0; i < argv_full_sox.length; i++) {
					iString += argv_full_sox[i] + " ";
				}
				log.debug(iString);
				log.debug("END mergeAudioToWaves ################# ");

				flvRecordingMetaDataOfScreen
						.setFullWavAudioData(hashFileFullName);

				this.flvRecordingMetaDataDaoImpl
						.updateFlvRecordingMetaData(flvRecordingMetaDataOfScreen);

				returnLog.add(ProcessHelper.executeScript("mergeWave",
						argv_full_sox));
			} else {

				// create default Audio to merge it.
				// strip to content length
				String outputWav = streamFolderGeneralName + "one_second.wav";

				// Calculate delta at beginning
				Long deltaTimeMilliSeconds = flvRecording.getRecordEnd()
						.getTime() - flvRecording.getRecordStart().getTime();
				Float deltaPadding = (Float.parseFloat(deltaTimeMilliSeconds
						.toString()) / 1000) - 1;

				String[] argv_full_sox = new String[] { this.getPathToSoX(),
						outputWav, outputFullWav, "pad", "0",
						deltaPadding.toString() };

				log.debug("START generateSampleAudio ################# ");
				String tString = "";
				for (int i = 0; i < argv_full_sox.length; i++) {
					tString += argv_full_sox[i] + " ";
				}
				log.debug(tString);
				log.debug("END generateSampleAudio ################# ");

				flvRecordingMetaDataOfScreen
						.setFullWavAudioData(hashFileFullName);

				this.flvRecordingMetaDataDaoImpl
						.updateFlvRecordingMetaData(flvRecordingMetaDataOfScreen);

				returnLog.add(ProcessHelper.executeScript("mergeWave",
						argv_full_sox));

			}

			// Merge Audio with Video / Calculate resulting FLV

			String inputScreenFullFlv = new File(streamFolder, flvRecordingMetaDataOfScreen.getStreamName() + ".flv").getCanonicalPath();

			String hashFileFullNameFlv = "flvRecording_"
					+ flvRecording.getFlvRecordingId() + ".flv";
			String outputFullFlv = streamFolderGeneralName
					+ hashFileFullNameFlv;

			// ffmpeg -vcodec flv -qscale 9.5 -r 25 -ar 22050 -ab 32k -s 320x240
			// -i
			// 65318fb5c54b1bc1b1bca077b493a914_28_12_2009_23_38_17_FINAL_WAVE.wav
			// -i 65318fb5c54b1bc1b1bca077b493a914_28_12_2009_23_38_17.flv
			// final1.flv

			int flvWidth = flvRecording.getWidth();
			int flvHeight = flvRecording.getHeight();

			log.debug("flvWidth -1- " + flvWidth);
			log.debug("flvHeight -1- " + flvHeight);

			flvWidth = Double.valueOf((Math.floor(flvWidth / 16)) * 16)
					.intValue();
			flvHeight = Double.valueOf((Math.floor(flvHeight / 16)) * 16)
					.intValue();

			log.debug("flvWidth -2- " + flvWidth);
			log.debug("flvHeight -2- " + flvHeight);

			flvRecording.setFlvWidth(flvWidth);
			flvRecording.setFlvHeight(flvHeight);

			String[] argv_fullFLV = new String[] {
					this.getPathToFFMPEG(), //
					"-i", inputScreenFullFlv, "-i", outputFullWav, "-ar",
					"22050", //
					"-acodec", "libmp3lame", //
					"-ab", "32k", //
					"-s", flvWidth + "x" + flvHeight, //
					"-vcodec", "flashsv", //
					"-map", "0" + FFMPEG_MAP_PARAM + "0", //
					"-map", "1" + FFMPEG_MAP_PARAM + "0", //
					outputFullFlv };

			log.debug("START generateFullFLV ################# ");
			String tString = "";
			for (int i = 0; i < argv_fullFLV.length; i++) {
				tString += argv_fullFLV[i] + " ";
				// log.debug(" i " + i + " argv-i " + argv_fullFLV[i]);
			}
			log.debug(tString);
			log.debug("END generateFullFLV ################# ");

			returnLog.add(ProcessHelper.executeScript("generateFullFLV",
					argv_fullFLV));

			flvRecording.setFileHash(hashFileFullNameFlv);

			// Extract first Image for preview purpose
			// ffmpeg -i movie.flv -vcodec mjpeg -vframes 1 -an -f rawvideo -s
			// 320x240 movie.jpg

			String hashFileFullNameJPEG = "flvRecording_"
					+ flvRecording.getFlvRecordingId() + ".jpg";
			String outPutJpeg = streamFolderGeneralName + hashFileFullNameJPEG;

			flvRecording.setPreviewImage(hashFileFullNameJPEG);

			String[] argv_previewFLV = new String[] { //
					this.getPathToFFMPEG(), //
					"-i", outputFullFlv, //
					"-vcodec", "mjpeg", //
					"-vframes", "1", "-an", //
					"-f", "rawvideo", //
					"-s", flvWidth + "x" + flvHeight, //
					outPutJpeg };

			log.debug("START previewFullFLV ################# ");
			log.debug(argv_previewFLV.toString());
			String kString = "";
			for (int i = 0; i < argv_previewFLV.length; i++) {
				kString += argv_previewFLV[i] + " ";
			}
			log.debug(kString);
			log.debug("END previewFullFLV ################# ");

			returnLog.add(ProcessHelper.executeScript("generateFullFLV",
					argv_previewFLV));

			String alternateDownloadName = "flvRecording_"
					+ flvRecording.getFlvRecordingId() + ".avi";
			String alternateDownloadFullName = streamFolderGeneralName
					+ alternateDownloadName;

			String[] argv_alternateDownload = new String[] {
					this.getPathToFFMPEG(), "-i", outputFullFlv,
					"-vcodec", "copy",
					alternateDownloadFullName };

			log.debug("START alternateDownLoad ################# ");
			log.debug(argv_previewFLV.toString());
			String sString = "";
			for (int i = 0; i < argv_alternateDownload.length; i++) {
				sString += argv_alternateDownload[i] + " ";
			}
			log.debug(sString);
			log.debug("END alternateDownLoad ################# ");

			returnLog.add(ProcessHelper.executeScript("alternateDownload",
					argv_alternateDownload));

			flvRecording.setAlternateDownload(alternateDownloadName);

			this.flvRecordingDaoImpl.updateFlvRecording(flvRecording);

			for (ConverterProcessResult returnMap : returnLog) {
				this.flvRecordingLogDaoImpl.addFLVRecordingLog(
						"generateFFMPEG", flvRecording, returnMap);
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
