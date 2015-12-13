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
import java.util.List;

import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.data.basic.dao.ConfigurationDao;
import org.apache.openmeetings.data.flvrecord.FlvRecordingMetaDataDao;
import org.apache.openmeetings.data.flvrecord.FlvRecordingMetaDeltaDao;
import org.apache.openmeetings.documents.GenerateSWF;
import org.apache.openmeetings.documents.beans.ConverterProcessResult;
import org.apache.openmeetings.persistence.beans.flvrecord.FlvRecording;
import org.apache.openmeetings.persistence.beans.flvrecord.FlvRecordingMetaData;
import org.apache.openmeetings.persistence.beans.flvrecord.FlvRecordingMetaDelta;
import org.apache.openmeetings.utils.OmFileHelper;
import org.apache.openmeetings.utils.ProcessHelper;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseConverter {
	private static final Logger log = Red5LoggerFactory.getLogger(
			BaseConverter.class, OpenmeetingsVariables.webAppRootKey);

	@Autowired
	private ConfigurationDao configurationDao;
	@Autowired
	private FlvRecordingMetaDataDao flvRecordingMetaDataDaoImpl;
	@Autowired
	private FlvRecordingMetaDeltaDao flvRecordingMetaDeltaDaoImpl;

	private String getPath(String key, String app) {
		String path = configurationDao.getConfValue(key, String.class, "");
		if (!path.equals("") && !path.endsWith(File.separator)) {
			path += File.separator;
		}
		path += app;
		return path;
	}
	
	protected String getPathToFFMPEG() {
		return getPath("ffmpeg_path", "ffmpeg");
	}

	protected String getPathToSoX() {
		return getPath("sox_path", "sox");
	}

	protected String getPathToImageMagick() {
		return getPath("imagemagick_path", "convert") + GenerateSWF.execExt;
	}

	protected boolean isUseOldStyleFfmpegMap() {
		return "1".equals(configurationDao.getConfValue(
				"use.old.style.ffmpeg.map.option", String.class, "0"));
	}
	
	protected File getStreamFolder() {
		return OmFileHelper.getStreamsHibernateDir();
	}

	protected File getStreamFolder(FlvRecording flvRecording) {
		return OmFileHelper.getStreamsSubDir(flvRecording.getRoom_id());
	}
	
	protected void deleteFileIfExists(String name) {
		File f = new File(name);

		if (f.exists()) {
			f.delete();
		}
	}
	
	protected String[] mergeAudioToWaves(List<String> listOfFullWaveFiles, String outputFullWav) throws Exception {
		String[] argv_full_sox = new String[listOfFullWaveFiles.size() + 3];
		
		log.debug(" listOfFullWaveFiles "+listOfFullWaveFiles.size()+" argv_full_sox LENGTH "+argv_full_sox.length);
		
		argv_full_sox[0] = getPathToSoX();
		argv_full_sox[1] = "-m";

		int i = 0;
		for (;i < listOfFullWaveFiles.size(); i++) {
			log.debug(" i "+i+" = "+listOfFullWaveFiles.get(i));
			argv_full_sox[2 + i] = listOfFullWaveFiles.get(i);
		}
		log.debug(" i + 2 "+(i+2)+" "+outputFullWav);
		
		argv_full_sox[i + 2] = outputFullWav;
		
		return argv_full_sox;
	}
	
	protected void stripAudioFirstPass(FlvRecording flvRecording,
			List<ConverterProcessResult> returnLog,
			List<String> listOfFullWaveFiles, File streamFolder) throws Exception {
		List<FlvRecordingMetaData> metaDataList = flvRecordingMetaDataDaoImpl
				.getFlvRecordingMetaDataAudioFlvsByRecording(flvRecording
						.getFlvRecordingId());
		stripAudioFirstPass(flvRecording, returnLog, listOfFullWaveFiles, streamFolder, metaDataList);
	}
	
	private String[] addSoxPad(List<ConverterProcessResult> returnLog, String job, double length, double position, String inFile, String outFile) {
		if (length >= 0 && position >= 0 && (length > 0 || position > 0)) {
			String[] argv_sox = new String[] { getPathToSoX(),
					inFile, outFile, "pad",
					"" + length, "" + position };
	
			if (log.isDebugEnabled()) {
				log.debug("START " + job + " ################# ");
				String padString = "";
				for (int i = 0; i < argv_sox.length; i++) {
					padString += " " + argv_sox[i];
				}
				log.debug("padString :: " + padString);
				log.debug("END " + job + " ################# ");
			}
			returnLog.add(ProcessHelper.executeScript(job, argv_sox));
			return argv_sox;
		} else {
			log.debug("::addSoxPad " + job + " Invalid parameters: "
				+ " length = " + length + "; position = " + position + "; inFile = " + inFile);
		}
		return null;
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
				
				if (metaData.getStreamReaderThreadComplete() == null) {
					throw new IllegalStateException("StreamReaderThreadComplete Bit is NULL, error in recording");
				}
				
				if (!metaData.getStreamReaderThreadComplete()) {
					
					log.debug("### meta Stream not yet written to disk" + metaData.getFlvRecordingMetaDataId());
					boolean doStop = true;
					while(doStop) {
						
						log.debug("### Stream not yet written Thread Sleep - " + metaData.getFlvRecordingMetaDataId());
						
						metaData = flvRecordingMetaDataDaoImpl.getFlvRecordingMetaDataById(metaData.getFlvRecordingMetaDataId());
						
						if (metaData.getStreamReaderThreadComplete()) {
							log.debug("### Stream now written Thread continue - " );
							doStop = false;
						}
						
						Thread.sleep(100L);
					}
				}
	
				File inputFlvFile = new File(streamFolder, metaData.getStreamName() + ".flv");
	
				String hashFileName = metaData.getStreamName() + "_WAVE.wav";
				String outputWav = new File(streamFolder, hashFileName).getCanonicalPath(); //FIXME
	
				metaData.setWavAudioData(hashFileName);
	
				
				log.debug("FLV File Name: {} Length: {} ", inputFlvFile.getName(), inputFlvFile.length());
	
				metaData.setAudioIsValid(false);
				if (inputFlvFile.exists()) {
	
					String[] argv = new String[] { this.getPathToFFMPEG(),
							"-async", "1", "-i", inputFlvFile.getCanonicalPath(), outputWav };
	
					log.debug("START stripAudioFromFLVs ################# ");
					for (int i = 0; i < argv.length; i++) {
						log.debug(" i " + i + " argv-i " + argv[i]);
					}
					log.debug("END stripAudioFromFLVs ################# ");
	
					returnLog.add(ProcessHelper.executeScript("generateFFMPEG",
							argv));
	
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
					List<FlvRecordingMetaDelta> flvRecordingMetaDeltas = flvRecordingMetaDeltaDaoImpl
							.getFlvRecordingMetaDeltaByMetaId(metaData
									.getFlvRecordingMetaDataId());
	
					int counter = 0;
	
					for (FlvRecordingMetaDelta metaDelta : flvRecordingMetaDeltas) {
	
						String inputFile = outputGapFullWav;
	
						// Strip Wave to Full Length
						String hashFileGapsFullName = metaData
								.getStreamName()
								+ "_GAP_FULL_WAVE_"
								+ counter
								+ ".wav";
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
							log.debug("START fillGap ################# Delta-ID :: "
									+ metaDelta.getFlvRecordingMetaDeltaId());
	
							flvRecordingMetaDeltaDaoImpl.updateFlvRecordingMetaDelta(metaDelta);
							counter++;
						} else {
							outputGapFullWav = inputFile;
						}
					}
	
					// Strip Wave to Full Length
					String hashFileFullName = metaData.getStreamName() + "_FULL_WAVE.wav";
					String outputFullWav = new File(streamFolder, hashFileFullName).getCanonicalPath();
	
					// Calculate delta at beginning
					long deltaTimeStartMilliSeconds = metaData.getRecordStart().getTime()
							- flvRecording.getRecordStart().getTime();
	
					double length = ((double)deltaTimeStartMilliSeconds) / 1000;
	
					// Calculate delta at ending
					long deltaTimeEndMilliSeconds = flvRecording.getRecordEnd().getTime()
							- metaData.getRecordEnd().getTime();
	
					double endPadding = ((double)deltaTimeEndMilliSeconds) / 1000;
	
					addSoxPad(returnLog, "addStartEndToAudio", length, endPadding, outputGapFullWav, outputFullWav);
	
					// Fix for Audio Length - Invalid Audio Length in Recorded
					// Files
					// Audio must match 100% the Video
					log.debug("############################################");
					log.debug("Trim Audio to Full Length -- Start");
					File aFile = new File(outputFullWav);
	
					if (!aFile.exists()) {
						throw new Exception(
								"Audio File does not exist , could not extract the Audio correctly");
					}
					metaData.setFullWavAudioData(hashFileFullName);
	
					// Finally add it to the row!
					listOfFullWaveFiles.add(outputFullWav);
	
				}
	
				flvRecordingMetaDataDaoImpl.updateFlvRecordingMetaData(metaData);
			}
		} catch (Exception err) {
			log.error("[stripAudioFromFLVs]", err);
		}
	}
}
