package org.openmeetings.app.data.flvrecord.converter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.openmeetings.app.data.basic.Configurationmanagement;
import org.openmeetings.app.data.flvrecord.FlvRecordingDaoImpl;
import org.openmeetings.app.data.flvrecord.FlvRecordingLogDaoImpl;
import org.openmeetings.app.data.flvrecord.FlvRecordingMetaDataDaoImpl;
import org.openmeetings.app.data.flvrecord.FlvRecordingMetaDeltaDaoImpl;
import org.openmeetings.app.documents.GenerateSWF;
import org.openmeetings.app.documents.GenerateThumbs;
import org.openmeetings.app.persistence.beans.flvrecord.FlvRecording;
import org.openmeetings.app.persistence.beans.flvrecord.FlvRecordingMetaData;
import org.openmeetings.app.persistence.beans.flvrecord.FlvRecordingMetaDelta;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class FlvInterviewReConverter {
	private int initCutSeconds = 0;
	private int leftSideLoud = 1;
	private int rightSideLoud = 1;
	private Integer leftSideTime = 0;
	private Integer rightSideTime = 0;	

	private static final Logger log = Red5LoggerFactory.getLogger(FlvInterviewReConverter.class);

	// Spring loaded Beans
	@Autowired
	private FlvRecordingDaoImpl flvRecordingDaoImpl = null;
	@Autowired
	private FlvRecordingMetaDataDaoImpl flvRecordingMetaDataDaoImpl = null;
	@Autowired
	private Configurationmanagement configurationmanagement;
	@Autowired
	private FlvRecordingLogDaoImpl flvRecordingLogDaoImpl;
	@Autowired
	private FlvRecordingMetaDeltaDaoImpl flvRecordingMetaDeltaDaoImpl;
	@Autowired
	private GenerateThumbs generateThumbs;
	
	private String FFMPEG_MAP_PARAM = ":";
	
	public void startConversion(Long flvRecordingId, Integer leftSideLoud, Integer rightSideLoud, Integer leftSideTime, Integer rightSideTime) {
		try {
			
			String use_old_style_ffmpeg_map = configurationmanagement
					.getConfValue("use.old.style.ffmpeg.map.option",
							String.class, "0");
			if (use_old_style_ffmpeg_map.equals("1")) {
				FFMPEG_MAP_PARAM = ".";
			}
			
			log.debug("++++++++++++ leftSideLoud :: "+leftSideLoud);
			log.debug("++++++++++++ rightSideLoud :: "+rightSideLoud);
			
			this.leftSideLoud += leftSideLoud;
			this.rightSideLoud += rightSideLoud;
			
			this.leftSideTime = leftSideTime;
			this.rightSideTime = rightSideTime;
			
			log.debug("++++++++++++ this.leftSideLoud :: "+this.leftSideLoud);
			log.debug("++++++++++++ this.rightSideLoud :: "+this.rightSideLoud);

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


	private String getPathToFFMPEG() {
		String pathToFFMPEG = this.configurationmanagement.getConfKey(3,
				"ffmpeg_path").getConf_value();
		if (!pathToFFMPEG.equals("") && !pathToFFMPEG.endsWith(File.separator)) {
			pathToFFMPEG += File.separator;
		}
		pathToFFMPEG += "ffmpeg";
		return pathToFFMPEG;
	}

	private String getPathToSoX() {
		String pathToSoX = this.configurationmanagement.getConfKey(3,
				"sox_path").getConf_value();
		if (!pathToSoX.equals("") && !pathToSoX.endsWith(File.separator)) {
			pathToSoX += File.separator;
		}
		pathToSoX += "sox";
		return pathToSoX;
	}
	
	private String getPathToImageMagick() {
		String pathToImageMagick = this.configurationmanagement.getConfKey(3,
				"imagemagick_path").getConf_value();
		if (!pathToImageMagick.equals("") && !pathToImageMagick.endsWith(File.separator)) {
			pathToImageMagick += File.separator;
		}
		pathToImageMagick += "convert"+GenerateSWF.execExt;
		return pathToImageMagick;
	}

	public void stripAudioFromFLVs(FlvRecording flvRecording) {
		List<HashMap<String, Object>> returnLog = new LinkedList<HashMap<String, Object>>();
		try {

			List<FlvRecordingMetaData> metaDataList = flvRecordingMetaDataDaoImpl
					.getFlvRecordingMetaDataAudioFlvsByRecording(
							flvRecording.getFlvRecordingId());

			// Init variables
			String streamFolderName = ScopeApplicationAdapter.webAppPath
					+ File.separatorChar + "streams" + File.separatorChar
					+ flvRecording.getRoom_id() + File.separatorChar;

			log.debug("###################################################");
			log.debug("### streamFolderName - " + streamFolderName);
			log.debug("### meta Data Number - " + metaDataList.size());
			log.debug("###################################################");

			List<String> listOfFullWaveFiles = new LinkedList<String>();

			for (FlvRecordingMetaData flvRecordingMetaData : metaDataList) {

				String inputFlv = streamFolderName
						+ flvRecordingMetaData.getStreamName() + ".flv";

				String hashFileName = flvRecordingMetaData.getStreamName()
						+ "_WAVE.wav";
				String outputWav = streamFolderName + hashFileName;
				
				File outputWavFile = new File(outputWav);
				
				if (outputWavFile.exists()) {
					outputWavFile.delete();
				}

				flvRecordingMetaData.setWavAudioData(hashFileName);
				
				File inputFlvFile = new File(inputFlv);
				
				if (inputFlvFile.exists()) {

					String[] argv = new String[] { this.getPathToFFMPEG(),
							 "-async", "1",
							"-i", inputFlv, outputWav };
	
					log.debug("START stripAudioFromFLVs ################# ");
					for (int i = 0; i < argv.length; i++) {
						log.debug(" i " + i + " argv-i " + argv[i]);
					}
					log.debug("END stripAudioFromFLVs ################# ");
	
					returnLog.add(GenerateSWF.executeScript("generateFFMPEG", argv));
	
					// check if the resulting Audio is valid
					File output_wav = new File(outputWav);
	
					if (!output_wav.exists()) {
						flvRecordingMetaData.setAudioIsValid(false);
					} else {
						if (output_wav.length() == 0) {
							flvRecordingMetaData.setAudioIsValid(false);
						} else {
							flvRecordingMetaData.setAudioIsValid(true);
						}
					}
				
				} else {
					log.debug("inputFlvFile.exists() Does not exist "+inputFlv);
					
					flvRecordingMetaData.setAudioIsValid(false);
				}
				
				if (flvRecordingMetaData.getAudioIsValid()) {
					
					// Strip Wave to Full Length
					String outputGapFullWav = outputWav;
					
					//Fix Gaps in Audio
					List<FlvRecordingMetaDelta> flvRecordingMetaDeltas = this.flvRecordingMetaDeltaDaoImpl.getFlvRecordingMetaDeltaByMetaId(flvRecordingMetaData.getFlvRecordingMetaDataId());
					
					int counter = 0;
					
					//double startGap = 0;
					
					for (FlvRecordingMetaDelta flvRecordingMetaDelta : flvRecordingMetaDeltas) {
						
						String inputFile = outputGapFullWav;
						
						// Strip Wave to Full Length
						String hashFileGapsFullName = flvRecordingMetaData.getStreamName()
											+ "_GAP_FULL_WAVE_"+counter+".wav";
						outputGapFullWav = streamFolderName + hashFileGapsFullName;
						
						File outputGapFullWavFile = new File(outputGapFullWav);
						
						if (outputGapFullWavFile.exists()) {
							outputGapFullWavFile.delete();
						}
						
						flvRecordingMetaDelta.setWaveOutPutName(hashFileGapsFullName);
						
						String[] argv_sox = null;
						
						
						if (flvRecordingMetaDelta.getIsStartPadding() != null && flvRecordingMetaDelta.getIsStartPadding()) {
							
							double gapSeconds = Double.valueOf(flvRecordingMetaDelta.getDeltaTime().toString()).doubleValue()/1000;
							
							Double.valueOf(flvRecordingMetaDelta.getDeltaTime().toString()).doubleValue();
							
							if (gapSeconds > 0) {
								//Add the item at the beginning
								argv_sox = new String[] { this.getPathToSoX(),
										inputFile, outputGapFullWav, "pad",
										String.valueOf(gapSeconds).toString(),"0" };
							}
							
						} else if (flvRecordingMetaDelta.getIsEndPadding() != null && flvRecordingMetaDelta.getIsEndPadding()) {
							
							double gapSeconds = Double.valueOf(flvRecordingMetaDelta.getDeltaTime().toString()).doubleValue()/1000;
							
							if (gapSeconds > 0) {
								//Add the item at the end
								argv_sox = new String[] { this.getPathToSoX(),
										inputFile, outputGapFullWav, "pad",
										"0",String.valueOf(gapSeconds).toString() };
							}
						}
						
						if (argv_sox != null) {
							log.debug("START addGapAudioToWaves ################# ");
							log.debug("START addGapAudioToWaves ################# Delta-ID :: "+flvRecordingMetaDelta.getFlvRecordingMetaDeltaId());
							String commandHelper = " ";
							for (int i = 0; i < argv_sox.length; i++) {
								commandHelper += " "+argv_sox[i];
								//log.debug(" i " + i + " argv-i " + argv_sox[i]);
							}
							log.debug(" commandHelper " + commandHelper);
							log.debug("END addGapAudioToWaves ################# ");
	
							returnLog.add(GenerateSWF.executeScript("fillGap",argv_sox));
							
							this.flvRecordingMetaDeltaDaoImpl.updateFlvRecordingMetaDelta(flvRecordingMetaDelta);
							counter++;
						} else {
							outputGapFullWav = inputFile;
						}
						
					}
					
					// Strip Wave to Full Length
					String hashFileFullName = flvRecordingMetaData.getStreamName()
												+ "_FULL_WAVE.wav";
					String outputFullWav = streamFolderName + hashFileFullName;

					File outputFullWavFile = new File(outputFullWav);
					
					if (outputFullWavFile.exists()) {
						outputFullWavFile.delete();
					}
					
					// Calculate delta at beginning
					Long deltaTimeStartMilliSeconds = flvRecordingMetaData.getRecordStart().getTime()
															- flvRecording.getRecordStart().getTime();
					
					Float startPadding = Float.parseFloat(deltaTimeStartMilliSeconds.toString()) / 1000;

					// Calculate delta at ending
					Long deltaTimeEndMilliSeconds = flvRecording.getRecordEnd().getTime()
															- flvRecordingMetaData.getRecordEnd().getTime();
					
					Float endPadding = Float.parseFloat(deltaTimeEndMilliSeconds.toString()) / 1000;

					String[] argv_sox = new String[] { this.getPathToSoX(),
							outputGapFullWav, outputFullWav, "pad",
							startPadding.toString(), endPadding.toString() };

					log.debug("START addAudioToWaves ################# ");
					String padString = "";
					for (int i = 0; i < argv_sox.length; i++) {
						padString += " "+argv_sox[i];
						//log.debug(" i " + i + " argv-i " + argv_sox[i]);
					}
					log.debug("padString :: "+padString);
					log.debug("END addAudioToWaves ################# ");

					returnLog.add(GenerateSWF.executeScript("addStartEndToAudio",argv_sox));
					
					//Fix for Audio Length - Invalid Audio Length in Recorded Files
					//Audio must match 100% the Video
					log.debug("############################################");
					log.debug("Trim Audio to Full Length -- Start");
					File aFile = new File(outputFullWav);
					
					if (!aFile.exists()) {
						throw new Exception("Audio File does not exist , could not extract the Audio correctly");
					}
					
					if (flvRecordingMetaData.getInteriewPodId() == 1) {
						//Left
						
						if (this.leftSideTime != 0) {
							String temporaryFullWave = outputFullWav;
							
							String hashFileFullNameCut = flvRecordingMetaData.getStreamName()
															+ "_FULL_WAVE_ADDED_LEFT.wav";
							String outputFullWavCut = streamFolderName + hashFileFullNameCut;
							
							String cutSecond = ""+((new Float(Math.abs(this.leftSideTime)))/1000);
							
							String[] argv_cut_sox = null;
							if (this.leftSideTime > 0) {
								argv_cut_sox = new String[] { this.getPathToSoX(),
														temporaryFullWave, outputFullWavCut, "pad",
														cutSecond,"0" };
							} else {
								argv_cut_sox = new String[] { this.getPathToSoX(),
										temporaryFullWave, outputFullWavCut, "pad",
										"0",cutSecond };
							}
							
							log.debug("START addPadCutStringToWaves ################# ");
							String padCutString = "";
							for (int i = 0; i < argv_cut_sox.length; i++) {
								padCutString += " "+argv_cut_sox[i];
								//log.debug(" i " + i + " argv-i " + argv_sox[i]);
							}
							log.debug("padCutString :LEFT: "+padCutString);
							log.debug("END addPadCutStringToWaves ################# ");
	
							returnLog.add(GenerateSWF.executeScript("addPadCutStringToAudio",argv_cut_sox));
							
							outputFullWav = outputFullWavCut;
							hashFileFullName = hashFileFullNameCut;
							
							//Cut away the piece at the end
							
							File outputFullWavCuttedFile = new File(outputFullWav);
							
							AudioInputStream aInputStream = AudioSystem.getAudioInputStream(outputFullWavCuttedFile);
							AudioFormat aFormat = aInputStream.getFormat(); 
							long frameLength = aInputStream.getFrameLength();
							float frameRate = aFormat.getFrameRate();
							
							double audioLength = Math.round(frameLength / frameRate); 
							
							String newLength = ""+(new Float(audioLength)-((new Float(Math.abs(this.leftSideTime)))/1000));
							
							log.debug("newLength :newLength: "+newLength);
							
							String temporaryFullWaveAdd = outputFullWav;
							
							String hashFileFullNameAdd = flvRecordingMetaData.getStreamName()
											+ "_FULL_WAVE_CUT_LEFT.wav";
							String outputFullWavAdd = streamFolderName + hashFileFullNameAdd;
						
							String[] argv_add_sox = null;
								
							if (this.leftSideTime > 0) {	
								argv_add_sox = new String[] { this.getPathToSoX(),
													temporaryFullWaveAdd, outputFullWavAdd, "trim",
													"0",newLength };
							} else {
								argv_add_sox = new String[] { this.getPathToSoX(),
										temporaryFullWaveAdd, outputFullWavAdd, "trim",
										cutSecond,""+audioLength };
							}
							
							log.debug("START addPadAddStringToWaves ################# ");
							String padAddString = "";
							for (int i = 0; i < argv_add_sox.length; i++) {
								padAddString += " "+argv_add_sox[i];
							    log.debug(" i " + i + " argv-i " + argv_add_sox[i]);
							}
							log.debug("padAddString :LEFT: "+padAddString);
							log.debug("END addPadAddStringToWaves ################# ");
							
							returnLog.add(GenerateSWF.executeScript("addPadAddStringToAudio",argv_add_sox));
							
							outputFullWav = outputFullWavAdd;
							hashFileFullName = hashFileFullNameAdd;
							
						}
						
					}	
					
					if (flvRecordingMetaData.getInteriewPodId() == 2) {
						//Right
						
						if (this.rightSideTime != 0) {
							String temporaryFullWave = outputFullWav;
							
							String hashFileFullNameCut = flvRecordingMetaData.getStreamName()
															+ "_FULL_WAVE_ADDED_RIGHT.wav";
							String outputFullWavCut = streamFolderName + hashFileFullNameCut;
							
							String cutSecond = ""+((new Float(Math.abs(this.rightSideTime)))/1000);
							
							String[] argv_cut_sox = null;
							if (this.rightSideTime > 0) {
								argv_cut_sox = new String[] { this.getPathToSoX(),
														temporaryFullWave, outputFullWavCut, "pad",
														cutSecond,"0" };
							} else {
								argv_cut_sox = new String[] { this.getPathToSoX(),
										temporaryFullWave, outputFullWavCut, "pad",
										"0",cutSecond };
							}
							
							log.debug("START addPadCutStringToWaves ################# ");
							String padCutString = "";
							for (int i = 0; i < argv_cut_sox.length; i++) {
								padCutString += " "+argv_cut_sox[i];
								//log.debug(" i " + i + " argv-i " + argv_sox[i]);
							}
							log.debug("padCutString :RIGHT: "+padCutString);
							log.debug("END addPadCutStringToWaves ################# ");
	
							returnLog.add(GenerateSWF.executeScript("addPadCutStringToAudio",argv_cut_sox));
						
							outputFullWav = outputFullWavCut;
							hashFileFullName = hashFileFullNameCut;
							
							//Cut away the piece at the end
							
							File outputFullWavCuttedFile = new File(outputFullWav);
							
							AudioInputStream aInputStream = AudioSystem.getAudioInputStream(outputFullWavCuttedFile);
							AudioFormat aFormat = aInputStream.getFormat(); 
							long frameLength = aInputStream.getFrameLength();
							float frameRate = aFormat.getFrameRate();
							
							double audioLength = Math.round(frameLength / frameRate); 
							
							String newLength = ""+(new Float(audioLength)-((new Float(Math.abs(this.leftSideTime)))/1000));
							
							log.debug("newLength :newLength: "+newLength);
							
							String temporaryFullWaveAdd = outputFullWav;
							
							String hashFileFullNameAdd = flvRecordingMetaData.getStreamName()
											+ "_FULL_WAVE_CUT_RIGHT.wav";
							String outputFullWavAdd = streamFolderName + hashFileFullNameAdd;
							
							String[] argv_add_sox = null;
							
							if (this.rightSideTime > 0) {	
								argv_add_sox = new String[] { this.getPathToSoX(),
													temporaryFullWaveAdd, outputFullWavAdd, "trim",
													"0",newLength };
							} else {
								argv_add_sox = new String[] { this.getPathToSoX(),
										temporaryFullWaveAdd, outputFullWavAdd, "trim",
										cutSecond,""+audioLength };
							}
							
							log.debug("START addPadAddStringToWaves ################# ");
							String padAddString = "";
							for (int i = 0; i < argv_add_sox.length; i++) {
								padAddString += " "+argv_add_sox[i];
							    log.debug(" i " + i + " argv-i " + argv_add_sox[i]);
							}
							log.debug("padAddString :LEFT: "+padAddString);
							log.debug("END addPadAddStringToWaves ################# ");
							
							returnLog.add(GenerateSWF.executeScript("addPadAddStringToAudio",argv_add_sox));
							
							outputFullWav = outputFullWavAdd;
							hashFileFullName = hashFileFullNameAdd;
						}
						
					}
					
					flvRecordingMetaData.setFullWavAudioData(hashFileFullName);
					
					//Finally add it to the row!
					listOfFullWaveFiles.add(outputFullWav);
					
				}

				flvRecordingMetaDataDaoImpl.updateFlvRecordingMetaData(flvRecordingMetaData);

			}

			// Merge Wave to Full Length
			String streamFolderGeneralName = ScopeApplicationAdapter.webAppPath
					+ File.separatorChar + "streams" + File.separatorChar
					+ "hibernate" + File.separatorChar;

			String hashFileFullName = "INTERVIEW_"+flvRecording.getFlvRecordingId()
					+ "_FINAL_WAVE.wav";
			String outputFullWav = streamFolderName + hashFileFullName;

			File outputFullWavFile = new File(outputFullWav);
			
			if (outputFullWavFile.exists()) {
				outputFullWavFile.delete();
			}
			
			if (listOfFullWaveFiles.size() == 1) {

				outputFullWav = listOfFullWaveFiles.get(0);
				
			} else if (listOfFullWaveFiles.size() > 0) {

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
								argv_full_sox[counter] = "-v "+this.leftSideLoud;
								counter++;
							}
							
							if (flvRecordingMetaData.getInteriewPodId() == 2) {
								argv_full_sox[counter] = "-v "+this.rightSideLoud;
								counter++;
							}
							
						}
						
					}
					
					argv_full_sox[counter] = listOfFullWaveFiles.get(i);
					counter++;
				}
				
				argv_full_sox[counter] = outputFullWav;

				log.debug("START mergeAudioToWaves ################# ");
				log.debug(argv_full_sox.toString());
				String iString = "";
				for (int i = 0; i < argv_full_sox.length; i++) {
					iString += argv_full_sox[i] + " ";
					//log.debug(" i " + i + " argv-i " + argv_full_sox[i]);
				}
				log.debug(iString);
				log.debug("END mergeAudioToWaves ################# ");

				returnLog.add(GenerateSWF.executeScript("mergeWave",
						argv_full_sox));
			} else {

				// create default Audio to merge it.
				// strip to content length
				String outputWav = streamFolderGeneralName + "one_second.wav";

				// Calculate delta at beginning
				Long deltaTimeMilliSeconds = flvRecording.getRecordEnd()
						.getTime()
						- flvRecording.getRecordStart().getTime();
				Float deltaPadding = (Float.parseFloat(deltaTimeMilliSeconds
						.toString()) / 1000) - 1;

				String[] argv_full_sox = new String[] { this.getPathToSoX(),
						outputWav, outputFullWav, "pad", "0",
						deltaPadding.toString() };

				log.debug("START generateSampleAudio ################# ");
				String tString = "";
				for (int i = 0; i < argv_full_sox.length; i++) {
					tString += argv_full_sox[i] + " ";
					//log.debug(" i " + i + " argv-i " + argv_full_sox[i]);
				}
				log.debug(tString);
				log.debug("END generateSampleAudio ################# ");

				returnLog.add(GenerateSWF.executeScript("mergeWave",
						argv_full_sox));

			}

			// Merge Audio with Video / Calculate resulting FLV

//			String inputScreenFullFlv = streamFolderName
//					+ flvRecordingMetaDataOfScreen.getStreamName() + ".flv";

			//Start extracting image sequence
			int frameRate = 25;
			
			for (FlvRecordingMetaData flvRecordingMetaData : metaDataList) {
				
				//FLV to 24 FPS Sequence AVI
				String inputFlv = streamFolderName
							+ "AV_" + flvRecordingMetaData.getStreamName() + ".flv";
				
				File inputFlvFile = new File(inputFlv);
				
				if (inputFlvFile.exists()) {
					
					//TO 24FPS AVI
					
//					String outputAVI = streamFolderName
//										+ "AV_" + flvRecordingMetaData.getStreamName() + ".avi";
//					
//					String[] argv_24AVI = new String[] { this.getPathToFFMPEG(), "-i",
//							inputFlv, "-an", "-vcodec", "copy", outputAVI };
//
//					log.debug("START generateImageSequenceAVI ################# ");
//					String tString = "";
//					for (int i = 0; i < argv_24AVI.length; i++) {
//						tString += argv_24AVI[i] + " ";
//						//log.debug(" i " + i + " argv-i " + argv_fullFLV[i]);
//					}
//					log.debug(tString);
//					log.debug("END generateImageSequenceAVI ################# ");
//
//					returnLog.add(GenerateSWF.executeScript("generateImageSequenceAVI", 
//							argv_24AVI));

					//TO Image Sequence
					
					String outputMetaImageData = streamFolderName + flvRecordingMetaData.getFlvRecordingMetaDataId() 
														+ File.separatorChar;
					
					//Image Folder
					File imageSequenceFolder = new File(outputMetaImageData);
					imageSequenceFolder.mkdir();
					
					String outputImages = outputMetaImageData + "image%d.png";
					
					String[] argv_imageSeq = new String[] { this.getPathToFFMPEG(), "-i",
									inputFlv, "-r", ""+frameRate, "-s", "320x240",
									outputImages  };

					log.debug("START generateImageSequence ################# ");
					String iString = "";
					for (int i = 0; i < argv_imageSeq.length; i++) {
						iString += argv_imageSeq[i] + " ";
						//log.debug(" i " + i + " argv-i " + argv_fullFLV[i]);
					}
					log.debug(iString);
					log.debug("END generateImageSequence ################# ");

					returnLog.add(GenerateSWF.executeScript("generateImageSequence",
							argv_imageSeq));
					
				}
				
			}
			
			//Default Image for empty interview video pods
			String defaultInterviewImage = streamFolderGeneralName + "default_interview_image.png";
			File defaultInterviewImageFile = new File (defaultInterviewImage);
			
			if (!defaultInterviewImageFile.exists()) {
				throw new Exception("defaultInterviewImageFile does not exist!");
			}
			
			//Create Folder for the output Image Sequence
			String outputImageMergedData = streamFolderName + "INTERVIEW_" 
												+ flvRecording.getFlvRecordingId() + File.separatorChar;
			
			//Merged Image Folder
			File outputImageMergedDateFolder = new File(outputImageMergedData);
			outputImageMergedDateFolder.mkdir();
			
			//Generate the Single Image by sequencing
			boolean jobRunning = true;
			long currentTimeInMilliSeconds = 0;
			
			long completeLengthInSeconds = flvRecording.getRecordEnd().getTime() - flvRecording.getRecordStart().getTime(); 
			
			log.debug("completeLengthInSeconds :: "+completeLengthInSeconds);
			
			int sequenceCounter = 0;
			
			while (jobRunning) {
				
				//Process one Second of Movie
				String[] interviewPod1Images = new String[frameRate];
				String[] interviewPod2Images = new String[frameRate];
				int[] outputFrameNumbers = new int[frameRate];
				
				for (FlvRecordingMetaData flvRecordingMetaData : metaDataList) {
					
					long deltaStartRecording = flvRecordingMetaData.getRecordStart().getTime() - flvRecording.getRecordStart().getTime();
				
					if (flvRecording.getRecordStart().getTime() + currentTimeInMilliSeconds >= flvRecordingMetaData.getRecordStart().getTime()
							&&
							flvRecording.getRecordStart().getTime() + currentTimeInMilliSeconds <= flvRecordingMetaData.getRecordEnd().getTime()) {
						
						//Calculate which images should be in here
						
						//Calculate the relative starting point
						long thisImageSequenceStartingPoint = currentTimeInMilliSeconds - deltaStartRecording;
						
//						log.debug("-- thisImageSequenceStartingPoint "+thisImageSequenceStartingPoint );
//						log.debug("-- currentTimeInMilliSeconds "+currentTimeInMilliSeconds );
//						log.debug("-- deltaStartRecording "+deltaStartRecording );
						
						//Calculate the first and following frameRate FPS Number
						int secondToStart = Long.valueOf(thisImageSequenceStartingPoint/1000).intValue();
						
						int firstFrame = secondToStart*frameRate;
						
						//log.debug("-- secondToStart "+secondToStart + " firstFrame " + firstFrame);
						
						for (int i=0;i<frameRate;i++) {
							
							int currentImageNumber = firstFrame + i;
							currentImageNumber -= (frameRate/2); //Remove the first half seconds and fill it up with black screens
							
							//Remove the first period of Images, this is where the user has started 
							//to share his Video but does not have agreed in the Flash Security Warning Dialogue
							Integer initialGapSeconds = flvRecordingMetaData.getInitialGapSeconds();
							if (initialGapSeconds != null) {
								int initialMissingImages = Double.valueOf(Math.floor((initialGapSeconds/1000) * frameRate)).intValue();
								currentImageNumber -= initialMissingImages;
							}
							
							String imageName = "image"+currentImageNumber+".png";
							
							//log.debug("imageName :: "+imageName+" AT: "+sequenceCounter);
							
							String outputMetaImageFullData = streamFolderName + flvRecordingMetaData.getFlvRecordingMetaDataId() 
																+ File.separatorChar + imageName;
							
							//log.debug("outputMetaImageFullData :: "+outputMetaImageFullData);
							
							File outputMetaImageFullDataFile = new File(outputMetaImageFullData);
							
							if (!outputMetaImageFullDataFile.exists()) {
								outputMetaImageFullData = defaultInterviewImage;
							}
							
							if (flvRecordingMetaData.getInteriewPodId() == 1) {
								interviewPod1Images[i] = outputMetaImageFullData;
							} else if (flvRecordingMetaData.getInteriewPodId() == 2){
								interviewPod2Images[i] = outputMetaImageFullData;
							}
							
						}
						
					}
					
				}
				
				//Update Sequence Count
				for (int i=0;i<frameRate;i++) {
					outputFrameNumbers[i] = sequenceCounter;
					sequenceCounter++;
				}
				
				
				//Now we should have found the needed Images to calculate, in case not we add an empty black screen
				for (int i=0;i<frameRate;i++) {
					
					String outputImageName = outputImageMergedData + "image" + outputFrameNumbers[i] + ".png";
					
					if (interviewPod1Images[i] == null) {
						interviewPod1Images[i] = defaultInterviewImage;
					}
					if (interviewPod2Images[i] == null) {
						interviewPod2Images[i] = defaultInterviewImage;
					}
					
					if (System.getProperty("os.name").toUpperCase().indexOf("WINDOWS") == -1) {
						String[] argv_imageMagick = new String[] { this.getPathToImageMagick(), "+append",
								interviewPod1Images[i], interviewPod2Images[i], outputImageName  };
		
	//					log.debug("START generateImageSequence ################# ");
	//					String iString = "";
	//					for (int k = 0; k < argv_imageMagick.length; k++) {
	//						iString += argv_imageMagick[k] + " ";
	//						//log.debug(" i " + i + " argv-i " + argv_fullFLV[i]);
	//					}
	//					log.debug(iString);
	//					log.debug("END generateImageSequence ################# ");
						
						//log.debug("returnLog "+returnLog);
						//log.debug("returnLog "+argv_imageMagick);
						
						//returnLog.add();
						GenerateSWF.executeScript("generateImageSequence",
								argv_imageMagick);
						
					} else {
						
						//returnLog.add();
						this.processImageWindows(interviewPod1Images[i], interviewPod2Images[i], outputImageName);
						
					}
				}
				
				currentTimeInMilliSeconds+=1000;
				
				double cLength = 100 * ((double)currentTimeInMilliSeconds) / ((double)completeLengthInSeconds);
				
				int progress = Double.valueOf(cLength).intValue();
				
				log.debug("completeLengthInSeconds|currentTimeInMilliSeconds " + 
						completeLengthInSeconds + "|" + currentTimeInMilliSeconds + "|" + progress + "|" + cLength);
				
				flvRecordingDaoImpl.updateFlvRecordingProgress(flvRecording.getFlvRecordingId(), progress);
				
				if (currentTimeInMilliSeconds >= completeLengthInSeconds) {
				
					jobRunning = false;
				
				}
				
				
			}
			
			//Generate Movie by sequence of Images
			
			String imagescomplete = outputImageMergedData + "image%d.png";
			
			String[] argv_generatedMoview = null;
			
			String inputScreenFullFlv = streamFolderName + "COMPLETE_INTERVIEW_" 
											+ flvRecording.getFlvRecordingId() + ".flv";

			File inputScreenFullFlvFile = new File(inputScreenFullFlv);
			
			if (inputScreenFullFlvFile.exists()) {
				inputScreenFullFlvFile.delete();
			}
			
			argv_generatedMoview = new String[] { this.getPathToFFMPEG(), 
					"-i", imagescomplete, 
					"-r", ""+frameRate, 
					"-vcodec", "flv", 
					"-qmax", "1", "-qmin", "1", 
					inputScreenFullFlv };

			log.debug("START generateFullBySequenceFLV ################# ");
			String tString2 = "";
			for (int i = 0; i < argv_generatedMoview.length; i++) {
				tString2 += argv_generatedMoview[i] + " ";
				//log.debug(" i " + i + " argv-i " + argv_fullFLV[i]);
			}
			log.debug(tString2);
			log.debug("END generateFullBySequenceFLV ################# ");

			returnLog.add(GenerateSWF.executeScript("generateFullBySequenceFLV",
					argv_generatedMoview));

			
			File outputFolder = new File(streamFolderGeneralName);
			if (!outputFolder.exists()) {
				outputFolder.mkdir();
			}

			String hashFileFullNameFlv = "flvRecording_"
					+ flvRecording.getFlvRecordingId() + ".flv";
			String outputFullFlv = streamFolderGeneralName
					+ hashFileFullNameFlv;

			File outputFullFlvFile = new File(outputFullFlv);
			
			if (outputFullFlvFile.exists()) {
				outputFullFlvFile.delete();
			}
			
			// ffmpeg -vcodec flv -qscale 9.5 -r 25 -ar 22050 -ab 32k -s 320x240
			// -i
			// 65318fb5c54b1bc1b1bca077b493a914_28_12_2009_23_38_17_FINAL_WAVE.wav
			// -i 65318fb5c54b1bc1b1bca077b493a914_28_12_2009_23_38_17.flv
			// final1.flv

			int flvWidth = 640;
			int flvHeight = 240;

			flvRecording.setFlvWidth(flvWidth);
			flvRecording.setFlvHeight(flvHeight);

			String[] argv_fullFLV = null;

			argv_fullFLV = new String[] { this.getPathToFFMPEG(), 
					"-r", ""+frameRate, 
					"-vcodec", "flv", 
					"-ar", "22050",
					"-ab", "32k", 
					"-s", flvWidth + "x" + flvHeight,
					"-qmax", "1", "-qmin", "1", 
					"-i", inputScreenFullFlv, 
					"-i", outputFullWav, 
					outputFullFlv };

			log.debug("START generateFullFLV ################# ");
			String tString = "";
			for (int i = 0; i < argv_fullFLV.length; i++) {
				tString += argv_fullFLV[i] + " ";
				//log.debug(" i " + i + " argv-i " + argv_fullFLV[i]);
			}
			log.debug(tString);
			log.debug("END generateFullFLV ################# ");

			returnLog.add(GenerateSWF.executeScript("generateFullFLV",
					argv_fullFLV));

			flvRecording.setFileHash(hashFileFullNameFlv);

			// Extract first Image for preview purpose
			// ffmpeg -i movie.flv -vcodec mjpeg -vframes 1 -an -f rawvideo -s
			// 320x240 movie.jpg

			String hashFileFullNameJPEG = "flvRecording_"
					+ flvRecording.getFlvRecordingId() + ".jpg";
			String outPutJpeg = streamFolderGeneralName + hashFileFullNameJPEG;

			File outPutJpegFile = new File(outPutJpeg);
			
			if (outPutJpegFile.exists()) {
				outPutJpegFile.delete();
			}
			
			flvRecording.setPreviewImage(hashFileFullNameJPEG);

			String[] argv_previewFLV = new String[] { this.getPathToFFMPEG(),
					"-i", outputFullFlv, "-vcodec", "mjpeg",
					"-vframes","100",
					"-an", "-f", "rawvideo", 
					"-s", flvWidth + "x" + flvHeight,
					outPutJpeg };

			log.debug("START previewFullFLV ################# ");
			log.debug(argv_previewFLV.toString());
			String kString = "";
			for (int i = 0; i < argv_previewFLV.length; i++) {
				kString += argv_previewFLV[i] + " ";
				//log.debug(" i " + i + " argv-i " + argv_previewFLV[i]);
			}
			log.debug(kString);
			log.debug("END previewFullFLV ################# ");

			returnLog.add(GenerateSWF.executeScript("generateFullFLV",
					argv_previewFLV));

			
			String alternateDownloadName = "flvRecording_"
						+ flvRecording.getFlvRecordingId() + ".avi";
			String alternateDownloadFullName = streamFolderGeneralName
					+ alternateDownloadName;
			
			File alternateDownloadFullNameFile = new File(alternateDownloadFullName);
			
			if (alternateDownloadFullNameFile.exists()) {
				alternateDownloadFullNameFile.delete();
			}
			
			String[] argv_alternateDownload = new String[] { this.getPathToFFMPEG(),
					"-i", outputFullFlv, alternateDownloadFullName };

			log.debug("START alternateDownLoad ################# ");
			log.debug(argv_previewFLV.toString());
			String sString = "";
			for (int i = 0; i < argv_alternateDownload.length; i++) {
				sString += argv_alternateDownload[i] + " ";
				//log.debug(" i " + i + " argv-i " + argv_previewFLV[i]);
			}
			log.debug(sString);
			log.debug("END alternateDownLoad ################# ");

			returnLog.add(GenerateSWF.executeScript("alternateDownload",
					argv_alternateDownload));			
			
			flvRecording.setAlternateDownload(alternateDownloadName);
			
			this.flvRecordingDaoImpl.updateFlvRecording(flvRecording);
			
			this.flvRecordingLogDaoImpl.deleteFLVRecordingLogByRecordingId(flvRecording.getFlvRecordingId());

			for (HashMap<String, Object> returnMap : returnLog) {
				this.flvRecordingLogDaoImpl.addFLVRecordingLog(
						"generateFFMPEG", flvRecording, returnMap);
			}
			
			//Delete Wave Files
			for (String fileName : listOfFullWaveFiles) {
				File audio = new File(fileName);
				if (audio.exists()) {
					audio.delete();
				}
			}
			
			//Delete all Image temp dirs
			for (FlvRecordingMetaData flvRecordingMetaData : metaDataList) {
				String outputMetaImageFullData = streamFolderName 
								+ flvRecordingMetaData.getFlvRecordingMetaDataId() 
								+ File.separatorChar ;
					
				this.deleteDirectory(new File(outputMetaImageFullData));
			}
			
			this.deleteDirectory(new File(outputImageMergedData));

		} catch (Exception err) {
			log.error("[stripAudioFromFLVs]", err);
		}
	}
	
	public boolean deleteDirectory(File path) throws Exception {
	    if( path.exists() ) {
	      File[] files = path.listFiles();
	      for(int i=0; i<files.length; i++) {
	         if(files[i].isDirectory()) {
	           deleteDirectory(files[i]);
	         }
	         else {
	           files[i].delete();
	         }
	      }
	    }
	    return( path.delete() );
	  }

	public HashMap<String,Object> processImageWindows(String file1, String file2, String file3) {
                
        //Init variables
        String[] cmd = {this.getPathToImageMagick(),
                        file1,file2,"+append",file3};
           
        return generateThumbs.processImageWindows(cmd);
        
        //GenerateSWF.executeScript("mergeWave",cmd);
        
	}
	
	public HashMap<String,Object> processImageWindowsinFile(String file1, String file2, String file3) {
        HashMap<String,Object> returnMap = new HashMap<String,Object>();
        returnMap.put("process", "processImageWindows");
        try {
                
                //Init variables
                String[] cmd;
                String executable_fileName = "";        
                String pathToIMagick = this.getPathToImageMagick();
                
                Date tnow = new Date();
                String runtimeFile = "interviewMerge"+tnow.getTime()+".bat";
                
                //String runtimeFile = "interviewMerge.bat";
                executable_fileName = ScopeApplicationAdapter.batchFileFir
                			 + runtimeFile;
                
                cmd = new String[1];
                //cmd[0] = "cmd.exe";
                //cmd[1] = "/C";
                //cmd[2] = "start";
                cmd[0] = executable_fileName;
                
                //log.debug("executable_fileName: "+executable_fileName);
                
                //Create the Content of the Converter Script (.bat or .sh File)
                String fileContent = pathToIMagick +
                                " " + file1 +
                                " " + file2 +
                                " " + "+append" +
                                " " + file3 +
                                ScopeApplicationAdapter.lineSeperator + "exit";
                        
                File previous = new File(executable_fileName);
                if (previous.exists()) {
                	previous.delete();
        		}
                
                //execute the Script
                FileOutputStream fos = new FileOutputStream(executable_fileName);
                fos.write(fileContent.getBytes());
                fos.close();
                
                File now = new File(executable_fileName);
                now.setExecutable(true);
                
                Runtime rt = Runtime.getRuntime();                      
                returnMap.put("command", cmd.toString());
                Process proc = rt.exec(cmd);
                
                InputStream stderr = proc.getErrorStream();
                InputStreamReader isr = new InputStreamReader(stderr);
                BufferedReader br = new BufferedReader(isr);
                String line = null;
                String error = "";
                while ((line = br.readLine()) != null){
                        error += line;
                        //log.debug("line: "+line);
                }
                returnMap.put("error", error);
                int exitVal = proc.waitFor();
                //log.debug("exitVal: "+exitVal);
                returnMap.put("exitValue", exitVal);

                if (now.exists()) {
                	now.delete();
                }
                
                return returnMap;
        } catch (Throwable t) {
                t.printStackTrace();
                returnMap.put("error", t.getMessage());
                returnMap.put("exitValue", -1);
                return returnMap;
        }
}
	

}
