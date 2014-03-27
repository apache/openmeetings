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
import java.util.LinkedList;
import java.util.List;

import org.apache.openmeetings.db.dao.record.FlvRecordingDao;
import org.apache.openmeetings.db.dao.record.FlvRecordingLogDao;
import org.apache.openmeetings.db.dao.record.FlvRecordingMetaDataDao;
import org.apache.openmeetings.db.entity.record.FlvRecording;
import org.apache.openmeetings.db.entity.record.FlvRecordingMetaData;
import org.apache.openmeetings.util.process.ConverterProcessResult;
import org.apache.openmeetings.util.process.ProcessHelper;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class FlvInterviewConverter extends BaseConverter {
	private static final Logger log = Red5LoggerFactory.getLogger(FlvInterviewConverter.class, webAppRootKey);
	private class ReConverterParams {
		private int leftSideLoud = 1;
		private int rightSideLoud = 1;
		private Integer leftSideTime = 0;
		private Integer rightSideTime = 0;
	}

	// Spring loaded Beans
	@Autowired
	private FlvRecordingDao recordingDao;
	@Autowired
	private FlvRecordingMetaDataDao metaDataDao;
	@Autowired
	private FlvRecordingLogDao logDao;
	@Autowired
	private GenerateThumbs generateThumbs;

	public void startReConversion(Long flvRecordingId, Integer leftSideLoud, Integer rightSideLoud,
			Integer leftSideTime, Integer rightSideTime) {

		log.debug("++++++++++++ leftSideLoud :: " + leftSideLoud);
		log.debug("++++++++++++ rightSideLoud :: " + rightSideLoud);

		ReConverterParams rcv = new ReConverterParams();
		rcv.leftSideLoud += leftSideLoud;
		rcv.rightSideLoud += rightSideLoud;

		rcv.leftSideTime = leftSideTime;
		rcv.rightSideTime = rightSideTime;

		log.debug("++++++++++++ this.leftSideLoud :: " + rcv.leftSideLoud);
		log.debug("++++++++++++ this.rightSideLoud :: " + rcv.rightSideLoud);
		log.debug("++++++++++++ this.leftSideTime :: " + rcv.leftSideTime);
		log.debug("++++++++++++ this.rightSideTime :: " + rcv.rightSideTime);
		startConversion(flvRecordingId, true, rcv);
	}

	private String[] mergeAudioToWaves(List<String> listOfFullWaveFiles, String outputFullWav,
			List<FlvRecordingMetaData> metaDataList, ReConverterParams rcv) {
		String[] argv_full_sox = new String[listOfFullWaveFiles.size() + 5];
		argv_full_sox[0] = this.getPathToSoX();
		argv_full_sox[1] = "-m";

		int counter = 2;
		for (int i = 0; i < listOfFullWaveFiles.size(); i++) {
			for (FlvRecordingMetaData flvRecordingMetaData : metaDataList) {
				String hashFileFullNameStored = flvRecordingMetaData.getFullWavAudioData();

				String fullFilePath = listOfFullWaveFiles.get(i);
				String fileNameOnly = new File(fullFilePath).getName();

				if (hashFileFullNameStored.equals(fileNameOnly)) {
					if (flvRecordingMetaData.getInteriewPodId() == 1) {
						argv_full_sox[counter] = "-v " + rcv.leftSideLoud;
						counter++;
					}
					if (flvRecordingMetaData.getInteriewPodId() == 2) {
						argv_full_sox[counter] = "-v " + rcv.rightSideLoud;
						counter++;
					}
				}
			}
			argv_full_sox[counter] = listOfFullWaveFiles.get(i);
			counter++;
		}

		argv_full_sox[counter] = outputFullWav;

		return argv_full_sox;
	}

	public void startConversion(Long flvRecordingId) {
		startConversion(flvRecordingId, false, new ReConverterParams());
	}

	public void startConversion(Long flvRecordingId, boolean reconversion, ReConverterParams rcv) {
		FlvRecording flvRecording = null;
		try {
			flvRecording = recordingDao.get(flvRecordingId);
			log.debug("flvRecording " + flvRecording.getFlvRecordingId());

			List<ConverterProcessResult> returnLog = new ArrayList<ConverterProcessResult>();
			List<String> listOfFullWaveFiles = new LinkedList<String>();
			File streamFolder = getStreamFolder(flvRecording);
			List<FlvRecordingMetaData> metaDataList = metaDataDao.getAudioMetaDataByRecording(flvRecording.getFlvRecordingId());
	
			stripAudioFirstPass(flvRecording, returnLog, listOfFullWaveFiles, streamFolder, metaDataList);
		
			// Merge Wave to Full Length
			File streamFolderGeneral = getStreamsHibernateDir();

			String hashFileFullName = "INTERVIEW_" + flvRecording.getFlvRecordingId() + "_FINAL_WAVE.wav";
			String outputFullWav = streamFolder.getAbsolutePath() + File.separatorChar + hashFileFullName;
			deleteFileIfExists(outputFullWav);

			if (listOfFullWaveFiles.size() == 1) {
				outputFullWav = listOfFullWaveFiles.get(0);
			} else if (listOfFullWaveFiles.size() > 0) {
				String[] argv_full_sox;
				if (reconversion) {
					argv_full_sox = mergeAudioToWaves(listOfFullWaveFiles, outputFullWav, metaDataList, rcv);
				} else {
					argv_full_sox = mergeAudioToWaves(listOfFullWaveFiles, outputFullWav);
				}

				returnLog.add(ProcessHelper.executeScript("mergeAudioToWaves", argv_full_sox));
			} else {
				// create default Audio to merge it.
				// strip to content length
				File outputWav = new File(streamFolderGeneral, "one_second.wav");

				// Calculate delta at beginning
				Long deltaTimeMilliSeconds = flvRecording.getRecordEnd().getTime() - flvRecording.getRecordStart().getTime();
				Float deltaPadding = (Float.parseFloat(deltaTimeMilliSeconds.toString()) / 1000) - 1;

				String[] argv_full_sox = new String[] { getPathToSoX(), outputWav.getCanonicalPath(), outputFullWav, "pad", "0", deltaPadding.toString() };

				returnLog.add(ProcessHelper.executeScript("generateSampleAudio", argv_full_sox));
			}
			// Default Image for empty interview video pods
			final File defaultInterviewImageFile = new File(streamFolderGeneral, "default_interview_image.png");

			if (!defaultInterviewImageFile.exists()) {
				throw new Exception("defaultInterviewImageFile does not exist!");
			}

			final int flvWidth = 320;
			final int flvHeight = 260;
			final int frameRate = 25;
			// Merge Audio with Video / Calculate resulting FLV

			String[] pods = new String[2];
			boolean found = false;
			for (FlvRecordingMetaData meta : metaDataList) {
				File flv = new File(streamFolder, meta.getStreamName() + ".flv");

				Integer pod = meta.getInteriewPodId();
				if (flv.exists() && pod != null && pod > 0 && pod < 3) {
					String path = flv.getCanonicalPath();
					/*
					 * CHECK FILE:
					 * ffmpeg -i rec_316_stream_567_2013_08_28_11_51_45.flv -v error -f null file.null
					 */ 
					String[] args = new String[] {getPathToFFMPEG()
							, "-i", path
							, "-an" // only input files with video will be treated as video sources
							, "-v", "error"
							, "-f", "null"
							, "file.null"};
					ConverterProcessResult r = ProcessHelper.executeScript("checkFlvPod_" + pod , args);
					returnLog.add(r);
					if ("".equals(r.getError())) {
						pods[pod - 1] = path;
					}
					found = true;
				}
			}
			if (!found) {
				ConverterProcessResult r = new ConverterProcessResult();
				r.setProcess("CheckFlvFilesExists");
				r.setError("No valid pods found");
				returnLog.add(r);
				return;
			}
			boolean shortest = false;
			List<String> args = new ArrayList<String>();
			args.add(getPathToFFMPEG());
			for (int i = 0; i < 2; ++i) {
				/*
				 * INSERT BLANK INSTEAD OF BAD PAD:
				 * ffmpeg -loop 1 -i default_interview_image.jpg -i rec_316_stream_569_2013_08_28_11_51_45.flv -filter_complex '[0:v]scale=320:260,pad=2*320:260[left];[1:v]scale=320:260[right];[left][right]overlay=main_w/2:0' -shortest -y out4.flv
				 *
				 * JUST MERGE:
				 * ffmpeg -i rec_316_stream_569_2013_08_28_11_51_45.flv -i rec_316_stream_569_2013_08_28_11_51_45.flv -filter_complex '[0:v]scale=320:260,pad=2*320:260[left];[1:v]scale=320:260[right];[left][right]overlay=main_w/2:0' -y out4.flv
				 */
				if (pods[i] == null) {
					shortest = true;
					args.add("-loop"); args.add("1");
					args.add("-i"); args.add(defaultInterviewImageFile.getCanonicalPath());
				} else {
					args.add("-i"); args.add(pods[i]);
				}
			}
			args.add("-i"); args.add(outputFullWav);
			args.add("-ar"); args.add("22050");
			args.add("-ab"); args.add("32k");
			args.add("-filter_complex");
			args.add(String.format("[0:v]scale=%1$d:%2$d,pad=2*%1$d:%2$d[left];[1:v]scale=%1$d:%2$d[right];[left][right]overlay=main_w/2:0%3$s"
					, flvWidth, flvHeight, shortest ? ":shortest=1" : ""));
			if (shortest) {
				args.add("-shortest");
			}
			args.add("-map"); args.add("0:0");
			args.add("-map"); args.add("1:0");
			args.add("-map"); args.add("2:0");
			args.add("-r"); args.add("" + frameRate);
			args.add("-qmax"); args.add("1");
			args.add("-qmin"); args.add("1");
			args.add("-y");
			String hashFileFullNameFlv = "flvRecording_" + flvRecording.getFlvRecordingId() + ".flv";
			String outputFullFlv = new File(streamFolderGeneral, hashFileFullNameFlv).getCanonicalPath();
			args.add(outputFullFlv);
			// TODO additional flag to 'quiet' output should be added
			returnLog.add(ProcessHelper.executeScript("generateFullBySequenceFLV", args.toArray(new String[]{})));

			flvRecording.setFlvWidth(2 * flvWidth);
			flvRecording.setFlvHeight(flvHeight);

			flvRecording.setFileHash(hashFileFullNameFlv);

			// Extract first Image for preview purpose
			// ffmpeg -i movie.flv -vcodec mjpeg -vframes 1 -an -f rawvideo -s
			// 320x240 movie.jpg

			String hashFileFullNameJPEG = "flvRecording_" + flvRecording.getFlvRecordingId() + ".jpg";
			String outPutJpeg = new File(streamFolderGeneral, hashFileFullNameJPEG).getCanonicalPath();
			deleteFileIfExists(outPutJpeg);

			flvRecording.setPreviewImage(hashFileFullNameJPEG);

			String[] argv_previewFLV = new String[] { //
					getPathToFFMPEG(), //
					"-i", outputFullFlv, //
					"-vcodec", "mjpeg", //
					"-vframes", "100", "-an", //
					"-f", "rawvideo", //
					"-s", (2 * flvWidth) + "x" + flvHeight, //
					outPutJpeg };

			returnLog.add(ProcessHelper.executeScript("generateFullFLV", argv_previewFLV));

			String alternateDownloadName = "flvRecording_" + flvRecording.getFlvRecordingId() + ".avi";
			String alternateDownloadFullName = new File(streamFolderGeneral, alternateDownloadName).getCanonicalPath();
			deleteFileIfExists(alternateDownloadFullName);

			String[] argv_alternateDownload = new String[] { getPathToFFMPEG(), "-i", outputFullFlv, alternateDownloadFullName };

			returnLog.add(ProcessHelper.executeScript("alternateDownload", argv_alternateDownload));

			flvRecording.setAlternateDownload(alternateDownloadName);

			updateDuration(flvRecording);
			convertToMp4(flvRecording, returnLog);
			flvRecording.setStatus(FlvRecording.Status.PROCESSED);

			logDao.deleteByRecordingId(flvRecording.getFlvRecordingId());

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
			log.error("[startConversion]", err);
			flvRecording.setStatus(FlvRecording.Status.ERROR);
		}
		recordingDao.update(flvRecording);
	}

	public ConverterProcessResult processImageWindows(String file1, String file2, String file3) {
		return ProcessHelper.executeScriptWindows("processImageWindows", new String[] { getPathToImageMagick(), file1,
				file2, "+append", file3 });
	}
}
