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

import org.apache.commons.transaction.util.FileHelper;
import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.data.flvrecord.FlvRecordingDao;
import org.apache.openmeetings.data.flvrecord.FlvRecordingLogDao;
import org.apache.openmeetings.data.flvrecord.FlvRecordingMetaDataDao;
import org.apache.openmeetings.documents.GenerateThumbs;
import org.apache.openmeetings.documents.beans.ConverterProcessResult;
import org.apache.openmeetings.persistence.beans.flvrecord.FlvRecording;
import org.apache.openmeetings.persistence.beans.flvrecord.FlvRecordingMetaData;
import org.apache.openmeetings.utils.ProcessHelper;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class FlvInterviewConverter extends BaseConverter {
	private int leftSideLoud = 1;
	private int rightSideLoud = 1;
	private Integer leftSideTime = 0;
	private Integer rightSideTime = 0;

	private static final Logger log = Red5LoggerFactory.getLogger(
			FlvInterviewConverter.class,
			OpenmeetingsVariables.webAppRootKey);

	// Spring loaded Beans
	@Autowired
	private FlvRecordingDao flvRecordingDaoImpl = null;
	@Autowired
	private FlvRecordingMetaDataDao flvRecordingMetaDataDaoImpl = null;
	@Autowired
	private FlvRecordingLogDao flvRecordingLogDaoImpl;
	@Autowired
	private GenerateThumbs generateThumbs;

	public void startReConversion(Long flvRecordingId, Integer leftSideLoud,
			Integer rightSideLoud, Integer leftSideTime, Integer rightSideTime) {

		log.debug("++++++++++++ leftSideLoud :: " + leftSideLoud);
		log.debug("++++++++++++ rightSideLoud :: " + rightSideLoud);

		this.leftSideLoud += leftSideLoud;
		this.rightSideLoud += rightSideLoud;

		this.leftSideTime = leftSideTime;
		this.rightSideTime = rightSideTime;

		log.debug("++++++++++++ this.leftSideLoud :: " + this.leftSideLoud);
		log.debug("++++++++++++ this.rightSideLoud :: " + this.rightSideLoud);
		log.debug("++++++++++++ this.leftSideTime :: " + this.leftSideTime);
		log.debug("++++++++++++ this.rightSideTime :: " + this.rightSideTime);
		startConversion(flvRecordingId, true);
	}

	public void startConversion(Long flvRecordingId) {
		startConversion(flvRecordingId, false);
	}
	
	public void startConversion(Long flvRecordingId, boolean reconversion) {
		try {

			FlvRecording flvRecording = this.flvRecordingDaoImpl
					.getFlvRecordingById(flvRecordingId);
			log.debug("flvRecording " + flvRecording.getFlvRecordingId());

			// Strip Audio out of all Audio-FLVs
			stripAudioFromFLVs(flvRecording, reconversion);

			// Add empty pieces at the beginning and end of the wav

		} catch (Exception err) {
			log.error("[startConversion]", err);
		}
	}
	
	private String[] mergeAudioToWaves(List<String> listOfFullWaveFiles, String outputFullWav, List<FlvRecordingMetaData> metaDataList) {
		String[] argv_full_sox = new String[listOfFullWaveFiles.size() + 5];
		argv_full_sox[0] = this.getPathToSoX();
		argv_full_sox[1] = "-m";

		int counter = 2;
		for (int i = 0; i < listOfFullWaveFiles.size(); i++) {
			for (FlvRecordingMetaData flvRecordingMetaData : metaDataList) {
				String hashFileFullNameStored = flvRecordingMetaData
						.getFullWavAudioData();

				String fullFilePath = listOfFullWaveFiles.get(i);
				String fileNameOnly = new File(fullFilePath).getName();

				if (hashFileFullNameStored.equals(fileNameOnly)) {
					if (flvRecordingMetaData.getInteriewPodId() == 1) {
						argv_full_sox[counter] = "-v "
								+ this.leftSideLoud;
						counter++;
					}
					if (flvRecordingMetaData.getInteriewPodId() == 2) {
						argv_full_sox[counter] = "-v "
								+ this.rightSideLoud;
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
	
	public void stripAudioFromFLVs(FlvRecording flvRecording, boolean reconversion) {
		List<ConverterProcessResult> returnLog = new ArrayList<ConverterProcessResult>();
		List<String> listOfFullWaveFiles = new LinkedList<String>();
		File streamFolder = getStreamFolder(flvRecording);
		List<FlvRecordingMetaData> metaDataList = flvRecordingMetaDataDaoImpl
				.getFlvRecordingMetaDataAudioFlvsByRecording(flvRecording
						.getFlvRecordingId());

		stripAudioFirstPass(flvRecording, returnLog, listOfFullWaveFiles,
				streamFolder, metaDataList);
		try {
			// Merge Wave to Full Length
			File streamFolderGeneral = getStreamFolder();

			String hashFileFullName = "INTERVIEW_"
					+ flvRecording.getFlvRecordingId() + "_FINAL_WAVE.wav";
			String outputFullWav = streamFolder.getAbsolutePath() + File.separatorChar + hashFileFullName;
			deleteFileIfExists(outputFullWav);

			if (listOfFullWaveFiles.size() == 1) {

				outputFullWav = listOfFullWaveFiles.get(0);

			} else if (listOfFullWaveFiles.size() > 0) {
				String[] argv_full_sox;
				if (reconversion) {
					argv_full_sox = mergeAudioToWaves(listOfFullWaveFiles, outputFullWav, metaDataList);
				} else {
					argv_full_sox = mergeAudioToWaves(listOfFullWaveFiles, outputFullWav);
				}

				log.debug("START mergeAudioToWaves ################# ");
				log.debug(argv_full_sox.toString());
				String iString = "";
				for (int i = 0; i < argv_full_sox.length; i++) {
					iString += argv_full_sox[i] + " ";
				}
				log.debug(iString);
				log.debug("END mergeAudioToWaves ################# ");

				returnLog.add(ProcessHelper.executeScript("mergeWave",
						argv_full_sox));
			} else {

				// create default Audio to merge it.
				// strip to content length
				File outputWav = new File(streamFolderGeneral, "one_second.wav");

				// Calculate delta at beginning
				Long deltaTimeMilliSeconds = flvRecording.getRecordEnd()
						.getTime() - flvRecording.getRecordStart().getTime();
				Float deltaPadding = (Float.parseFloat(deltaTimeMilliSeconds
						.toString()) / 1000) - 1;

				String[] argv_full_sox = new String[] { this.getPathToSoX(),
						outputWav.getCanonicalPath(), outputFullWav, "pad", "0",
						deltaPadding.toString() };

				log.debug("START generateSampleAudio ################# ");
				String tString = "";
				for (int i = 0; i < argv_full_sox.length; i++) {
					tString += argv_full_sox[i] + " ";
				}
				log.debug(tString);
				log.debug("END generateSampleAudio ################# ");

				returnLog.add(ProcessHelper.executeScript("mergeWave",
						argv_full_sox));

			}

			// Merge Audio with Video / Calculate resulting FLV

			// Start extracting image sequence
			int frameRate = 25;

			for (FlvRecordingMetaData flvRecordingMetaData : metaDataList) {

				// FLV to 24 FPS Sequence AVI
				String inputFlv = new File(streamFolder, flvRecordingMetaData.getStreamName() + ".flv").getCanonicalPath();

				File inputFlvFile = new File(inputFlv);

				if (inputFlvFile.exists()) {
					// TO Image Sequence

					// Image Folder
					File imageSequenceFolder = new File(streamFolder
						, "" + flvRecordingMetaData.getFlvRecordingMetaDataId());
					imageSequenceFolder.mkdir();
					
					String outputImages = new File(imageSequenceFolder, "image%d.png").getCanonicalPath();

					String[] argv_imageSeq = new String[] {
							this.getPathToFFMPEG(), "-i", inputFlv, "-r",
							"" + frameRate, "-s", "320x240", outputImages };

					log.debug("START generateImageSequence ################# ");
					String iString = "";
					for (int i = 0; i < argv_imageSeq.length; i++) {
						iString += argv_imageSeq[i] + " ";
					}
					log.debug(iString);
					log.debug("END generateImageSequence ################# ");

					returnLog.add(ProcessHelper.executeScript(
							"generateImageSequence", argv_imageSeq));

				}

			}

			// Default Image for empty interview video pods
			File defaultInterviewImageFile = new File(streamFolderGeneral, "default_interview_image.png");

			if (!defaultInterviewImageFile.exists()) {
				throw new Exception("defaultInterviewImageFile does not exist!");
			}
			String defaultInterviewImage = defaultInterviewImageFile.getCanonicalPath();

			// Create Folder for the output Image Sequence
			String outputImageMergedData = streamFolder.getAbsolutePath() + File.separatorChar 
					+ "INTERVIEW_"	+ flvRecording.getFlvRecordingId() 
					+ File.separatorChar;

			// Merged Image Folder
			File outputImageMergedDateFolder = new File(outputImageMergedData);
			outputImageMergedDateFolder.mkdir();

			// Generate the Single Image by sequencing
			boolean jobRunning = true;
			long currentTimeInMilliSeconds = 0;

			long completeLengthInSeconds = flvRecording.getRecordEnd()
					.getTime() - flvRecording.getRecordStart().getTime();

			log.debug("completeLengthInSeconds :: " + completeLengthInSeconds);

			int sequenceCounter = 0;

			while (jobRunning) {

				// Process one Second of Movie
				String[] interviewPod1Images = new String[frameRate];
				String[] interviewPod2Images = new String[frameRate];
				int[] outputFrameNumbers = new int[frameRate];

				for (FlvRecordingMetaData flvRecordingMetaData : metaDataList) {

					long deltaStartRecording = flvRecordingMetaData
							.getRecordStart().getTime()
							- flvRecording.getRecordStart().getTime();

					if (flvRecording.getRecordStart().getTime()
							+ currentTimeInMilliSeconds >= flvRecordingMetaData
							.getRecordStart().getTime()
							&& flvRecording.getRecordStart().getTime()
									+ currentTimeInMilliSeconds <= flvRecordingMetaData
									.getRecordEnd().getTime()) {

						// Calculate which images should be in here

						// Calculate the relative starting point
						long thisImageSequenceStartingPoint = currentTimeInMilliSeconds
								- deltaStartRecording;

						// Calculate the first and following frameRate FPS
						// Number
						int secondToStart = Long.valueOf(
								thisImageSequenceStartingPoint / 1000)
								.intValue();

						int firstFrame = secondToStart * frameRate;

						for (int i = 0; i < frameRate; i++) {

							int currentImageNumber = firstFrame + i;
							currentImageNumber -= (frameRate / 2); 
							// Remove the first half seconds and fill
							// it up with black screens

							// Remove the first period of Images, this is where
							// the user has started
							// to share his Video but does not have agreed in
							// the Flash Security Warning Dialogue
							Integer initialGapSeconds = flvRecordingMetaData
									.getInitialGapSeconds();
							if (initialGapSeconds != null) {
								int initialMissingImages = Double.valueOf(
										Math.floor((initialGapSeconds / 1000)
												* frameRate)).intValue();
								currentImageNumber -= initialMissingImages;
							}

							String imageName = "image" + currentImageNumber + ".png";

							File outputMetaImageFullDataFile = new File(streamFolder
								, "" + flvRecordingMetaData.getFlvRecordingMetaDataId()
									+ File.separatorChar + imageName);
							
							String outputMetaImageFullData = 
								!outputMetaImageFullDataFile.exists() ? defaultInterviewImage
								: outputMetaImageFullDataFile.getCanonicalPath();

							if (flvRecordingMetaData.getInteriewPodId() == 1) {
								interviewPod1Images[i] = outputMetaImageFullData;
							} else if (flvRecordingMetaData.getInteriewPodId() == 2) {
								interviewPod2Images[i] = outputMetaImageFullData;
							}

						}

					}

				}

				// Update Sequence Count
				for (int i = 0; i < frameRate; i++) {
					outputFrameNumbers[i] = sequenceCounter;
					sequenceCounter++;
				}

				// Now we should have found the needed Images to calculate, in
				// case not we add an empty black screen
				for (int i = 0; i < frameRate; i++) {
					String addZeros = "";

					String outputImageName = outputImageMergedData + "image"
							+ addZeros + outputFrameNumbers[i] + ".png";

					if (interviewPod1Images[i] == null) {
						interviewPod1Images[i] = defaultInterviewImage;
					}
					if (interviewPod2Images[i] == null) {
						interviewPod2Images[i] = defaultInterviewImage;
					}

					if (System.getProperty("os.name").toUpperCase()
							.indexOf("WINDOWS") == -1) {
						String[] argv_imageMagick = new String[] {
								this.getPathToImageMagick(), "+append",
								interviewPod1Images[i], interviewPod2Images[i],
								outputImageName };
						returnLog.add(ProcessHelper.executeScript(
								"generateImageSequence", argv_imageMagick));
					} else {
						returnLog.add(processImageWindows(
								interviewPod1Images[i], interviewPod2Images[i],
								outputImageName));
					}
				}

				currentTimeInMilliSeconds += 1000;

				double cLength = 100 * ((double) currentTimeInMilliSeconds)
						/ completeLengthInSeconds;

				int progress = Double.valueOf(cLength).intValue();

				log.debug("completeLengthInSeconds|currentTimeInMilliSeconds "
						+ completeLengthInSeconds + "|"
						+ currentTimeInMilliSeconds + "|" + progress + "|"
						+ cLength);

				flvRecordingDaoImpl.updateFlvRecordingProgress(
						flvRecording.getFlvRecordingId(), progress);

				if (currentTimeInMilliSeconds >= completeLengthInSeconds) {

					jobRunning = false;

				}

			}

			// Generate Movie by sequence of Images

			String imagescomplete = outputImageMergedData + "image%d.png";

			String[] argv_generatedMoview = null;

			String inputScreenFullFlv = streamFolder.getAbsolutePath() + File.separatorChar
					+ "COMPLETE_INTERVIEW_" + flvRecording.getFlvRecordingId()
					+ ".flv";
			deleteFileIfExists(inputScreenFullFlv);

			argv_generatedMoview = new String[] { this.getPathToFFMPEG(), "-i",
					imagescomplete, "-r", "" + frameRate, "-vcodec", "flv",
					"-qmax", "1", "-qmin", "1", inputScreenFullFlv };

			log.debug("START generateFullBySequenceFLV ################# ");
			String tString2 = "";
			for (int i = 0; i < argv_generatedMoview.length; i++) {
				tString2 += argv_generatedMoview[i] + " ";
			}
			log.debug(tString2);
			log.debug("END generateFullBySequenceFLV ################# ");

			returnLog.add(ProcessHelper.executeScript(
					"generateFullBySequenceFLV", argv_generatedMoview));

			String hashFileFullNameFlv = "flvRecording_"
					+ flvRecording.getFlvRecordingId() + ".flv";
			String outputFullFlv = new File(streamFolderGeneral, hashFileFullNameFlv).getCanonicalPath();
			deleteFileIfExists(outputFullFlv);

			// ffmpeg -vcodec flv -qscale 9.5 -r 25 -ar 22050 -ab 32k -s 320x240
			// -i
			// 65318fb5c54b1bc1b1bca077b493a914_28_12_2009_23_38_17_FINAL_WAVE.wav
			// -i 65318fb5c54b1bc1b1bca077b493a914_28_12_2009_23_38_17.flv
			// final1.flv

			int flvWidth = 640;
			int flvHeight = 240;

			flvRecording.setFlvWidth(flvWidth);
			flvRecording.setFlvHeight(flvHeight);

			String[] argv_fullFLV = new String[] {
					this.getPathToFFMPEG(), //
					"-i", inputScreenFullFlv, "-i", outputFullWav,
					"-ar",
					"22050", //
					"-ab",
					"32k", //
					"-s",
					flvWidth + "x" + flvHeight, //
					"-vcodec",
					"flv", //
					"-r", "" + frameRate, "-qmax", "1", "-qmin", "1",
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
			String outPutJpeg = new File(streamFolderGeneral, hashFileFullNameJPEG).getCanonicalPath();
			deleteFileIfExists(outPutJpeg);

			flvRecording.setPreviewImage(hashFileFullNameJPEG);

			String[] argv_previewFLV = new String[] { //
			this.getPathToFFMPEG(), //
					"-i", outputFullFlv, //
					"-vcodec", "mjpeg", //
					"-vframes", "100", "-an", //
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
			String alternateDownloadFullName = new File(streamFolderGeneral, alternateDownloadName).getCanonicalPath();
			deleteFileIfExists(alternateDownloadFullName);

			String[] argv_alternateDownload = new String[] {
					this.getPathToFFMPEG(), "-i", outputFullFlv,
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

			flvRecordingDaoImpl.updateFlvRecording(flvRecording);

			flvRecordingLogDaoImpl
					.deleteFLVRecordingLogByRecordingId(flvRecording
							.getFlvRecordingId());

			for (ConverterProcessResult returnMap : returnLog) {
				flvRecordingLogDaoImpl.addFLVRecordingLog("generateFFMPEG",
						flvRecording, returnMap);
			}

			// Delete Wave Files
			for (String fileName : listOfFullWaveFiles) {
				File audio = new File(fileName);
				if (audio.exists()) {
					audio.delete();
				}
			}

			// Delete all Image temp dirs
			for (FlvRecordingMetaData flvRecordingMetaData : metaDataList) {
				FileHelper.removeRec(new File(streamFolder, "" + flvRecordingMetaData.getFlvRecordingMetaDataId()));
			}

			FileHelper.removeRec(new File(outputImageMergedData));

		} catch (Exception err) {
			log.error("[stripAudioFromFLVs]", err);
		}
	}

	public ConverterProcessResult thumbProcessImageWindows(String file1,
			String file2, String file3) {

		// Init variables
		String[] cmd = { this.getPathToImageMagick(), file1, file2, "+append",
				file3 };

		return generateThumbs.processImageWindows(cmd);
	}

	public ConverterProcessResult processImageWindows(String file1,
			String file2, String file3) {
		return ProcessHelper.executeScriptWindows("processImageWindows"
			, new String[]{getPathToImageMagick(), file1, file2, "+append", file3});
	}
}
