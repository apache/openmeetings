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

import static org.apache.openmeetings.util.OmFileHelper.EXTENSION_AVI;
import static org.apache.openmeetings.util.OmFileHelper.EXTENSION_FLV;
import static org.apache.openmeetings.util.OmFileHelper.EXTENSION_JPG;
import static org.apache.openmeetings.util.OmFileHelper.getRecordingMetaData;
import static org.apache.openmeetings.util.OmFileHelper.getStreamsHibernateDir;
import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.openmeetings.db.dao.file.FileItemLogDao;
import org.apache.openmeetings.db.dao.record.RecordingDao;
import org.apache.openmeetings.db.dao.record.RecordingMetaDataDao;
import org.apache.openmeetings.db.entity.record.Recording;
import org.apache.openmeetings.db.entity.record.RecordingMetaData;
import org.apache.openmeetings.util.OmFileHelper;
import org.apache.openmeetings.util.process.ConverterProcessResult;
import org.apache.openmeetings.util.process.ProcessHelper;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class InterviewConverter extends BaseConverter implements IRecordingConverter {
	private static final Logger log = Red5LoggerFactory.getLogger(InterviewConverter.class, webAppRootKey);
	private static class ReConverterParams {
		private int leftSideLoud = 1;
		private int rightSideLoud = 1;
		@SuppressWarnings("unused")
		private Integer leftSideTime = 0;
		@SuppressWarnings("unused")
		private Integer rightSideTime = 0;
	}

	// Spring loaded Beans
	@Autowired
	private RecordingDao recordingDao;
	@Autowired
	private RecordingMetaDataDao metaDataDao;
	@Autowired
	private FileItemLogDao logDao;

	private String[] mergeAudioToWaves(List<File> waveFiles, File wav,
			List<RecordingMetaData> metaDataList, ReConverterParams rcv) throws IOException {
		String[] argv_full_sox = new String[waveFiles.size() + 5];
		argv_full_sox[0] = this.getPathToSoX();
		argv_full_sox[1] = "-m";

		int counter = 2;
		for (File _wav : waveFiles) {
			for (RecordingMetaData metaData : metaDataList) {
				String hashFileFullNameStored = metaData.getFullWavAudioData();

				if (hashFileFullNameStored.equals(_wav.getName())) {
					if (metaData.getInteriewPodId() == 1) {
						argv_full_sox[counter] = "-v " + rcv.leftSideLoud;
						counter++;
					}
					if (metaData.getInteriewPodId() == 2) {
						argv_full_sox[counter] = "-v " + rcv.rightSideLoud;
						counter++;
					}
				}
			}
			argv_full_sox[counter] = _wav.getCanonicalPath();
			counter++;
		}

		argv_full_sox[counter] = wav.getCanonicalPath();

		return argv_full_sox;
	}

	@Override
	public void startConversion(Long recordingId) {
		startConversion(recordingId, false, new ReConverterParams());
	}

	public void startConversion(Long id, boolean reconversion, ReConverterParams rcv) {
		Recording r = null;
		try {
			r = recordingDao.get(id);
			log.debug("recording " + r.getId());
			r.setStatus(Recording.Status.CONVERTING);
			r = recordingDao.update(r);

			List<ConverterProcessResult> returnLog = new ArrayList<>();
			List<File> waveFiles = new ArrayList<>();
			File streamFolder = getStreamFolder(r);
			List<RecordingMetaData> metaDataList = metaDataDao.getAudioMetaDataByRecording(r.getId());
	
			stripAudioFirstPass(r, returnLog, waveFiles, streamFolder, metaDataList);
		
			// Merge Wave to Full Length
			File streamFolderGeneral = getStreamsHibernateDir();

			File wav = new File(streamFolder, "INTERVIEW_" + r.getId() + "_FINAL_WAVE.wav");
			deleteFileIfExists(wav);

			if (waveFiles.size() == 1) {
				wav = waveFiles.get(0);
			} else if (waveFiles.size() > 0) {
				String[] argv_full_sox;
				if (reconversion) {
					argv_full_sox = mergeAudioToWaves(waveFiles, wav, metaDataList, rcv);
				} else {
					argv_full_sox = mergeAudioToWaves(waveFiles, wav);
				}

				returnLog.add(ProcessHelper.executeScript("mergeAudioToWaves", argv_full_sox));
			} else {
				// create default Audio to merge it.
				// strip to content length
				File outputWav = new File(streamFolderGeneral, "one_second.wav");

				// Calculate delta at beginning
				double deltaPadding = diffSeconds(r.getRecordEnd(), r.getRecordStart());

				String[] argv_full_sox = new String[] { getPathToSoX(), outputWav.getCanonicalPath(), wav.getCanonicalPath(), "pad", "0", "" + deltaPadding };

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
			for (RecordingMetaData meta : metaDataList) {
				File flv = getRecordingMetaData(r.getRoomId(), meta.getStreamName());

				Integer pod = meta.getInteriewPodId();
				if (flv.exists() && pod != null && pod > 0 && pod < 3) {
					String path = flv.getCanonicalPath();
					/*
					 * CHECK FILE:
					 * ffmpeg -i rec_316_stream_567_2013_08_28_11_51_45.flv -v error -f null file.null
					 */ 
					String[] args = new String[] {getPathToFFMPEG(), "-y"
							, "-i", path
							, "-an" // only input files with video will be treated as video sources
							, "-v", "error"
							, "-f", "null"
							, "file.null"};
					ConverterProcessResult res = ProcessHelper.executeScript("checkFlvPod_" + pod , args);
					returnLog.add(res);
					if (res.isOk()) {
						//TODO need to remove smallest gap
						long diff = diff(meta.getRecordStart(), meta.getRecording().getRecordStart());
						if (diff != 0L) {
							// stub to add
							// ffmpeg -y -loop 1 -i /home/solomax/work/openmeetings/branches/3.0.x/dist/red5/webapps/openmeetings/streams/hibernate/default_interview_image.jpg -filter_complex '[0:0]scale=320:260' -c:v libx264 -t 00:00:29.059 -pix_fmt yuv420p out.flv
							File podFB = new File(streamFolder, meta.getStreamName() + "_pod_" + pod + "_blank.flv");
							String podPB = podFB.getCanonicalPath();
							String[] argsPodB = new String[] { getPathToFFMPEG(), "-y" //
									, "-loop", "1", "-i", defaultInterviewImageFile.getCanonicalPath() //
									, "-filter_complex", String.format("[0:0]scale=%1$d:%2$d", flvWidth, flvHeight) //
									, "-c:v", "libx264" //
									, "-t", formatMillis(diff) //
									, "-pix_fmt", "yuv420p" //
									, podPB };
							returnLog.add(ProcessHelper.executeScript("blankFlvPod_" + pod , argsPodB));
							
							//ffmpeg -y -i out.flv -i rec_15_stream_4_2014_07_15_20_41_03.flv -filter_complex '[0:0]setsar=1/1[sarfix];[1:0]scale=320:260,setsar=1/1[scale];[sarfix] [scale] concat=n=2:v=1:a=0 [v]' -map '[v]'  output1.flv
							File podF = new File(streamFolder, OmFileHelper.getName(meta.getStreamName() + "_pod_" + pod, EXTENSION_FLV));
							String podP = podF.getCanonicalPath();
							String[] argsPod = new String[] { getPathToFFMPEG(), "-y"//
									, "-i", podPB //
									, "-i", path //
									, "-filter_complex", String.format("[0:0]setsar=1/1[sarfix];[1:0]scale=%1$d:%2$d,setsar=1/1[scale];[sarfix] [scale] concat=n=2:v=1:a=0 [v]", flvWidth, flvHeight) //
									, "-map", "[v]" //
									, podP };
							returnLog.add(ProcessHelper.executeScript("shiftedFlvPod_" + pod , argsPod));
	
							pods[pod - 1] = podP;
						} else {
							pods[pod - 1] = path;
						}
					}
					found = true;
				}
			}
			if (!found) {
				ConverterProcessResult res = new ConverterProcessResult();
				res.setProcess("CheckFlvFilesExists");
				res.setError("No valid pods found");
				returnLog.add(res);
				return;
			}
			boolean shortest = false;
			List<String> args = new ArrayList<String>();
			args.add(getPathToFFMPEG());
			args.add("-y"); 
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
			args.add("-i"); args.add(wav.getCanonicalPath());
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
			File flv = r.getFile(EXTENSION_FLV);
			args.add(flv.getCanonicalPath());
			// TODO additional flag to 'quiet' output should be added
			returnLog.add(ProcessHelper.executeScript("generateFullBySequenceFLV", args.toArray(new String[]{})));

			r.setFlvWidth(2 * flvWidth);
			r.setFlvHeight(flvHeight);

			// Extract first Image for preview purpose
			// ffmpeg -i movie.flv -vcodec mjpeg -vframes 1 -an -f rawvideo -s
			// 320x240 movie.jpg

			File jpg = r.getFile(EXTENSION_JPG);
			deleteFileIfExists(jpg);

			String[] argv_previewFLV = new String[] { //
					getPathToFFMPEG(), "-y", //
					"-i", flv.getCanonicalPath(), //
					"-vcodec", "mjpeg", //
					"-vframes", "100", "-an", //
					"-f", "rawvideo", //
					"-s", (2 * flvWidth) + "x" + flvHeight, //
					jpg.getCanonicalPath() };

			returnLog.add(ProcessHelper.executeScript("generateFullFLV", argv_previewFLV));

			File avi = r.getFile(EXTENSION_AVI);
			deleteFileIfExists(avi);

			String[] argv_alternateDownload = new String[] { getPathToFFMPEG(), "-y", "-i", flv.getCanonicalPath(), avi.getCanonicalPath() };

			returnLog.add(ProcessHelper.executeScript("alternateDownload", argv_alternateDownload));

			updateDuration(r);
			convertToMp4(r, returnLog);
			r.setStatus(Recording.Status.PROCESSED);

			logDao.delete(r);
			for (ConverterProcessResult returnMap : returnLog) {
				logDao.add("generateFFMPEG", r, returnMap);
			}

			// Delete Wave Files
			for (File audio : waveFiles) {
				if (audio.exists()) {
					audio.delete();
				}
			}
		} catch (Exception err) {
			log.error("[startConversion]", err);
			r.setStatus(Recording.Status.ERROR);
		}
		recordingDao.update(r);
	}
}
