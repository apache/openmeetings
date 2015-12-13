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

import static org.apache.openmeetings.util.OmFileHelper.MP4_EXTENSION;
import static org.apache.openmeetings.util.OmFileHelper.OGG_EXTENSION;
import static org.apache.openmeetings.util.OmFileHelper.getRecording;
import static org.apache.openmeetings.util.OmFileHelper.getStreamsSubDir;
import static org.apache.openmeetings.util.OmFileHelper.isRecordingExists;
import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dao.record.FlvRecordingMetaDataDao;
import org.apache.openmeetings.db.dao.record.FlvRecordingMetaDeltaDao;
import org.apache.openmeetings.db.entity.record.FlvRecording;
import org.apache.openmeetings.db.entity.record.FlvRecordingMetaData;
import org.apache.openmeetings.db.entity.record.FlvRecordingMetaData.Status;
import org.apache.openmeetings.db.entity.record.FlvRecordingMetaDelta;
import org.apache.openmeetings.util.process.ConverterProcessResult;
import org.apache.openmeetings.util.process.ProcessHelper;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseConverter {
	private static final Logger log = Red5LoggerFactory.getLogger(BaseConverter.class, webAppRootKey);

	@Autowired
	private ConfigurationDao configurationDao;
	@Autowired
	private FlvRecordingMetaDataDao metaDataDao;
	@Autowired
	private FlvRecordingMetaDeltaDao metaDeltaDao;

	private String getPath(String key, String app) {
		String path = configurationDao.getConfValue(key, String.class, "");
		if (!"".equals(path) && !path.endsWith(File.separator)) {
			path += File.separator;
		}
		path += app;
		return path;
	}
	
	public String getPathToFFMPEG() {
		return getPath("ffmpeg_path", "ffmpeg");
	}

	protected String getPathToSoX() {
		return getPath("sox_path", "sox");
	}

	protected String getPathToImageMagick() {
		return getPath("imagemagick_path", "convert") + GenerateSWF.execExt;
	}

	protected boolean isUseOldStyleFfmpegMap() {
		return "1".equals(configurationDao.getConfValue("use.old.style.ffmpeg.map.option", String.class, "0"));
	}
	
	protected File getStreamFolder(FlvRecording flvRecording) {
		return getStreamsSubDir(flvRecording.getRoom_id());
	}
	
	protected void deleteFileIfExists(String name) {
		File f = new File(name);

		if (f.exists()) {
			f.delete();
		}
	}
	
	protected String[] mergeAudioToWaves(List<String> listOfFullWaveFiles, String outputFullWav) {
		List<String> argv = new ArrayList<String>();
		
		argv.add(getPathToSoX());
		argv.add("-m");
		for (String arg : listOfFullWaveFiles) {
			argv.add(arg);
		}
		argv.add(outputFullWav);
		
		return argv.toArray(new String[0]);
	}
	
	protected void stripAudioFirstPass(FlvRecording flvRecording, List<ConverterProcessResult> returnLog,
			List<String> listOfFullWaveFiles, File streamFolder)
	{
		stripAudioFirstPass(flvRecording, returnLog, listOfFullWaveFiles, streamFolder
				, metaDataDao.getAudioMetaDataByRecording(flvRecording.getFlvRecordingId()));
	}
	
	private String[] addSoxPad(List<ConverterProcessResult> returnLog, String job, double length, double position, String inFile, String outFile) {
		//FIXME need to check this
		if (length < 0 || position < 0) {
			log.debug("::addSoxPad " + job + " Invalid parameters: "
					+ " length = " + length + "; position = " + position + "; inFile = " + inFile);
		}
		length = length < 0 ? 0 : length;
		position = position < 0 ? 0 : position;

		String[] argv = new String[] { getPathToSoX(), inFile, outFile, "pad", "" + length, "" + position };

		returnLog.add(ProcessHelper.executeScript(job, argv));
		return argv;
	}
	
	protected FlvRecordingMetaData waitForTheStream(long metaId) throws InterruptedException {
		FlvRecordingMetaData metaData = metaDataDao.get(metaId);
		if (metaData.getStreamStatus() != Status.STOPPED) {
			log.debug("### meta Stream not yet written to disk " + metaId);
			boolean doStop = true;
			while(doStop) {
				log.trace("### Stream not yet written Thread Sleep - " + metaId);
				
				metaData = metaDataDao.get(metaId);
				
				if (metaData.getStreamStatus() == Status.STOPPED) {
					log.debug("### Stream now written Thread continue - " );
					doStop = false;
				}
				
				Thread.sleep(100L);
			}
		}
		return metaData;
	}
	
	protected void stripAudioFirstPass(FlvRecording flvRecording,
			List<ConverterProcessResult> returnLog,
			List<String> listOfFullWaveFiles, File streamFolder,
			List<FlvRecordingMetaData> metaDataList) {
		try {
			// Init variables
			log.debug("### meta Data Number - " + metaDataList.size());
			log.debug("###################################################");
	
			for (FlvRecordingMetaData metaData : metaDataList) {
				long metaId = metaData.getFlvRecordingMetaDataId();
				log.debug("### processing metadata: " + metaId);
				if (metaData.getStreamStatus() == Status.NONE) {
					log.debug("Stream has not been started, error in recording " + metaId);
					continue;
				}
				
				metaData = waitForTheStream(metaId);
	
				File inputFlvFile = new File(streamFolder, metaData.getStreamName() + ".flv");
	
				String hashFileName = metaData.getStreamName() + "_WAVE.wav";
				String outputWav = new File(streamFolder, hashFileName).getCanonicalPath(); //FIXME
	
				metaData.setWavAudioData(hashFileName);
	
				
				log.debug("FLV File Name: {} Length: {} ", inputFlvFile.getName(), inputFlvFile.length());
	
				metaData.setAudioIsValid(false);
				if (inputFlvFile.exists()) {
					String[] argv = new String[] {getPathToFFMPEG(), "-async", "1", "-i", inputFlvFile.getCanonicalPath(), outputWav};
	
					returnLog.add(ProcessHelper.executeScript("stripAudioFromFLVs", argv));
	
					// check if the resulting Audio is valid
					File output_wav = new File(outputWav);
	
					if (output_wav.exists() && output_wav.length() != 0) {
						metaData.setAudioIsValid(true);
					}
				}
	
				if (metaData.getAudioIsValid()) {
					// Strip Wave to Full Length
					String outputGapFullWav = outputWav;
	
					// Fix Start/End in Audio
					List<FlvRecordingMetaDelta> flvRecordingMetaDeltas = metaDeltaDao.getFlvRecordingMetaDeltaByMetaId(metaId);
	
					int counter = 0;
	
					for (FlvRecordingMetaDelta metaDelta : flvRecordingMetaDeltas) {
						String inputFile = outputGapFullWav;
	
						// Strip Wave to Full Length
						String hashFileGapsFullName = metaData.getStreamName() + "_GAP_FULL_WAVE_" + counter + ".wav";
						outputGapFullWav = new File(streamFolder, hashFileGapsFullName).getCanonicalPath();
	
						metaDelta.setWaveOutPutName(hashFileGapsFullName);
	
						String[] argv_sox = null;
	
						if (metaDelta.getDeltaTime() != null) {
							if (metaDelta.getIsStartPadding() != null && metaDelta.getIsStartPadding()) {
								double gapSeconds = ((double)metaDelta.getDeltaTime()) / 1000;
								argv_sox = addSoxPad(returnLog, "fillGap", gapSeconds, 0, inputFile, outputGapFullWav);
							} else if (metaDelta.getIsEndPadding() != null && metaDelta.getIsEndPadding()) {
								double gapSeconds = ((double)metaDelta.getDeltaTime()) / 1000;
								argv_sox = addSoxPad(returnLog, "fillGap", 0, gapSeconds, inputFile, outputGapFullWav);
							}
						}
	
						if (argv_sox != null) {
							log.debug("START fillGap ################# Delta-ID :: " + metaDelta.getFlvRecordingMetaDeltaId());
	
							metaDeltaDao.updateFlvRecordingMetaDelta(metaDelta);
							counter++;
						} else {
							outputGapFullWav = inputFile;
						}
					}
	
					// Strip Wave to Full Length
					String hashFileFullName = metaData.getStreamName() + "_FULL_WAVE.wav";
					String outputFullWav = new File(streamFolder, hashFileFullName).getCanonicalPath();
	
					// Calculate delta at beginning
					long deltaTimeStartMilliSeconds = metaData.getRecordStart().getTime() - flvRecording.getRecordStart().getTime();
	
					double length = ((double)deltaTimeStartMilliSeconds) / 1000;
	
					// Calculate delta at ending
					long deltaTimeEndMilliSeconds = flvRecording.getRecordEnd().getTime() - metaData.getRecordEnd().getTime();
	
					double endPadding = ((double)deltaTimeEndMilliSeconds) / 1000;
	
					addSoxPad(returnLog, "addStartEndToAudio", length, endPadding, outputGapFullWav, outputFullWav);
	
					// Fix for Audio Length - Invalid Audio Length in Recorded Files
					// Audio must match 100% the Video
					log.debug("############################################");
					log.debug("Trim Audio to Full Length -- Start");
					File aFile = new File(outputFullWav);
	
					if (!aFile.exists()) {
						throw new Exception("Audio File does not exist , could not extract the Audio correctly");
					}
					metaData.setFullWavAudioData(hashFileFullName);
	
					// Finally add it to the row!
					listOfFullWaveFiles.add(outputFullWav);
				}
	
				metaDataDao.update(metaData);
			}
		} catch (Exception err) {
			log.error("[stripAudioFirstPass]", err);
		}
	}
	
	public void convertToMp4(FlvRecording r, List<ConverterProcessResult> returnLog) throws IOException {
		//TODO add faststart, move filepaths to helpers
		if (!isRecordingExists(r.getFileHash())) {
			return;
		}
		File file = getRecording(r.getFileHash());
		String path = file.getCanonicalPath();
		String mp4path = path + MP4_EXTENSION;
		String[] argv = new String[] {
				getPathToFFMPEG(), //
				"-i", path,
				"-c:v", "libx264",
				"-crf", "24",
				"-pix_fmt", "yuv420p",
				"-preset", "medium",
				"-profile:v", "baseline",
				"-c:a", "libfaac",
				"-c:a", "libfdk_aac", "-b:a", "32k", //FIXME add quality constants 
				"-s", r.getFlvWidth() + "x" + r.getFlvHeight(), //
				mp4path
				};
		returnLog.add(ProcessHelper.executeScript("generate MP4", argv));
		
		argv = new String[] {
				getPathToFFMPEG(), //
				"-i", mp4path,
				"-vcodec", "libtheora",
				"-acodec", "libvorbis",
				path + OGG_EXTENSION
				};

		returnLog.add(ProcessHelper.executeScript("generate OGG", argv));
	}
}
