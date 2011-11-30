package org.openmeetings.app.data.flvrecord.converter;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import org.openmeetings.app.data.basic.Configurationmanagement;
import org.openmeetings.app.data.flvrecord.FlvRecordingMetaDataDaoImpl;
import org.openmeetings.app.data.flvrecord.FlvRecordingMetaDeltaDaoImpl;
import org.openmeetings.app.documents.GenerateSWF;
import org.openmeetings.app.persistence.beans.flvrecord.FlvRecording;
import org.openmeetings.app.persistence.beans.flvrecord.FlvRecordingMetaData;
import org.openmeetings.app.persistence.beans.flvrecord.FlvRecordingMetaDelta;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseConverter {
	private static final Logger log = Red5LoggerFactory.getLogger(
			BaseConverter.class, ScopeApplicationAdapter.webAppRootKey);

	@Autowired
	private Configurationmanagement configurationmanagement;
	@Autowired
	private FlvRecordingMetaDataDaoImpl flvRecordingMetaDataDaoImpl;
	@Autowired
	private FlvRecordingMetaDeltaDaoImpl flvRecordingMetaDeltaDaoImpl;

	protected String getPathToFFMPEG() {
		String pathToFFMPEG = configurationmanagement.getConfKey(3,
				"ffmpeg_path").getConf_value();
		if (!pathToFFMPEG.equals("") && !pathToFFMPEG.endsWith(File.separator)) {
			pathToFFMPEG += File.separator;
		}
		pathToFFMPEG += "ffmpeg";
		return pathToFFMPEG;
	}

	protected String getPathToSoX() {
		String pathToSoX = configurationmanagement.getConfKey(3, "sox_path")
				.getConf_value();
		if (!pathToSoX.equals("") && !pathToSoX.endsWith(File.separator)) {
			pathToSoX += File.separator;
		}
		pathToSoX += "sox";
		return pathToSoX;
	}

	protected String getPathToImageMagick() {
		String pathToImageMagick = this.configurationmanagement.getConfKey(3,
				"imagemagick_path").getConf_value();
		if (!pathToImageMagick.equals("")
				&& !pathToImageMagick.endsWith(File.separator)) {
			pathToImageMagick += File.separator;
		}
		pathToImageMagick += "convert" + GenerateSWF.execExt;
		return pathToImageMagick;
	}

	protected boolean isUseOldStyleFfmpegMap() {
		return "1".equals(configurationmanagement.getConfValue(
				"use.old.style.ffmpeg.map.option", String.class, "0"));
	}
	
	protected String getStreamFolderName() {
		return getStreamFolderName("hibernate");
	}

	protected String getStreamFolderName(FlvRecording flvRecording) {
		return getStreamFolderName("" + flvRecording.getRoom_id());
	}
	
	protected String getStreamFolderName(String name) {
		String streamFolderName = ScopeApplicationAdapter.webAppPath
				+ File.separatorChar + "streams" + File.separatorChar
				+ name + File.separatorChar;

		log.debug("###################################################");
		log.debug("### streamFolderName - " + streamFolderName);
		
		File sf = new File(streamFolderName);
		if (!sf.exists()) {
			log.debug("### streamFolderName is NOT exists");
			if (!sf.mkdir()) {
				log.error("### streamFolderName: Failed to create folder");
			}
		}
		return streamFolderName;
	}
	
	protected void deleteFileIfExists(String name) {
		File f = new File(name);

		if (f.exists()) {
			f.delete();
		}
	}
	
	protected String[] mergeAudioToWaves(List<String> listOfFullWaveFiles, String outputFullWav) {
		String[] argv_full_sox = new String[listOfFullWaveFiles.size() + 3];
		argv_full_sox[0] = getPathToSoX();
		argv_full_sox[1] = "-m";

		int i = 0;
		for (;i < listOfFullWaveFiles.size(); ++i) {
			argv_full_sox[2 + i] = listOfFullWaveFiles.get(i);
		}
		argv_full_sox[i + 3] = outputFullWav;
		
		return argv_full_sox;
	}
	
	protected void stripAudioFirstPass(FlvRecording flvRecording, List<HashMap<String, String>> returnLog, List<String> listOfFullWaveFiles, String streamFolderName) {
		try {
			List<FlvRecordingMetaData> metaDataList = flvRecordingMetaDataDaoImpl
					.getFlvRecordingMetaDataAudioFlvsByRecording(flvRecording
							.getFlvRecordingId());
	
			// Init variables
			log.debug("### meta Data Number - " + metaDataList.size());
			log.debug("###################################################");
	
			for (FlvRecordingMetaData flvRecordingMetaData : metaDataList) {
	
				String inputFlv = streamFolderName
						+ flvRecordingMetaData.getStreamName() + ".flv";
	
				String hashFileName = flvRecordingMetaData.getStreamName()
						+ "_WAVE.wav";
				String outputWav = streamFolderName + hashFileName;
	
				flvRecordingMetaData.setWavAudioData(hashFileName);
	
				File inputFlvFile = new File(inputFlv);
	
				if (inputFlvFile.exists()) {
	
					String[] argv = new String[] { this.getPathToFFMPEG(),
							"-async", "1", "-i", inputFlv, outputWav };
	
					log.debug("START stripAudioFromFLVs ################# ");
					for (int i = 0; i < argv.length; i++) {
						log.debug(" i " + i + " argv-i " + argv[i]);
					}
					log.debug("END stripAudioFromFLVs ################# ");
	
					returnLog.add(GenerateSWF.executeScript("generateFFMPEG",
							argv));
	
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
					flvRecordingMetaData.setAudioIsValid(false);
				}
	
				if (flvRecordingMetaData.getAudioIsValid()) {
	
					// Strip Wave to Full Length
					String outputGapFullWav = outputWav;
	
					// Fix Gaps in Audio
					List<FlvRecordingMetaDelta> flvRecordingMetaDeltas = flvRecordingMetaDeltaDaoImpl
							.getFlvRecordingMetaDeltaByMetaId(flvRecordingMetaData
									.getFlvRecordingMetaDataId());
	
					int counter = 0;
	
					for (FlvRecordingMetaDelta flvRecordingMetaDelta : flvRecordingMetaDeltas) {
	
						String inputFile = outputGapFullWav;
	
						// Strip Wave to Full Length
						String hashFileGapsFullName = flvRecordingMetaData
								.getStreamName()
								+ "_GAP_FULL_WAVE_"
								+ counter
								+ ".wav";
						outputGapFullWav = streamFolderName
								+ hashFileGapsFullName;
	
						flvRecordingMetaDelta
								.setWaveOutPutName(hashFileGapsFullName);
	
						String[] argv_sox = null;
	
						if (flvRecordingMetaDelta.getIsStartPadding() != null
								&& flvRecordingMetaDelta.getIsStartPadding()) {
	
							double gapSeconds = Double.valueOf(
									flvRecordingMetaDelta.getDeltaTime()
											.toString()).doubleValue() / 1000;
	
							Double.valueOf(
									flvRecordingMetaDelta.getDeltaTime()
											.toString()).doubleValue();
	
							if (gapSeconds > 0) {
								// Add the item at the beginning
								argv_sox = new String[] { this.getPathToSoX(),
										inputFile, outputGapFullWav, "pad",
										String.valueOf(gapSeconds).toString(),
										"0" };
							}
	
						} else if (flvRecordingMetaDelta.getIsEndPadding() != null
								&& flvRecordingMetaDelta.getIsEndPadding()) {
	
							double gapSeconds = Double.valueOf(
									flvRecordingMetaDelta.getDeltaTime()
											.toString()).doubleValue() / 1000;
	
							if (gapSeconds > 0) {
								// Add the item at the end
								argv_sox = new String[] { this.getPathToSoX(),
										inputFile, outputGapFullWav, "pad",
										"0",
										String.valueOf(gapSeconds).toString() };
							}
						}
	
						if (argv_sox != null) {
							log.debug("START addGapAudioToWaves ################# ");
							log.debug("START addGapAudioToWaves ################# Delta-ID :: "
									+ flvRecordingMetaDelta
											.getFlvRecordingMetaDeltaId());
							String commandHelper = " ";
							for (int i = 0; i < argv_sox.length; i++) {
								commandHelper += " " + argv_sox[i];
							}
							log.debug(" commandHelper " + commandHelper);
							log.debug("END addGapAudioToWaves ################# ");
	
							returnLog.add(GenerateSWF.executeScript("fillGap",
									argv_sox));
	
							this.flvRecordingMetaDeltaDaoImpl
									.updateFlvRecordingMetaDelta(flvRecordingMetaDelta);
							counter++;
						} else {
							outputGapFullWav = inputFile;
						}
	
					}
	
					// Strip Wave to Full Length
					String hashFileFullName = flvRecordingMetaData
							.getStreamName() + "_FULL_WAVE.wav";
					String outputFullWav = streamFolderName + hashFileFullName;
	
					// Calculate delta at beginning
					Long deltaTimeStartMilliSeconds = flvRecordingMetaData
							.getRecordStart().getTime()
							- flvRecording.getRecordStart().getTime();
	
					Float startPadding = Float
							.parseFloat(deltaTimeStartMilliSeconds.toString()) / 1000;
	
					// Calculate delta at ending
					Long deltaTimeEndMilliSeconds = flvRecording.getRecordEnd()
							.getTime()
							- flvRecordingMetaData.getRecordEnd().getTime();
	
					Float endPadding = Float
							.parseFloat(deltaTimeEndMilliSeconds.toString()) / 1000;
	
					String[] argv_sox = new String[] { this.getPathToSoX(),
							outputGapFullWav, outputFullWav, "pad",
							startPadding.toString(), endPadding.toString() };
	
					log.debug("START addAudioToWaves ################# ");
					String padString = "";
					for (int i = 0; i < argv_sox.length; i++) {
						padString += " " + argv_sox[i];
					}
					log.debug("padString :: " + padString);
					log.debug("END addAudioToWaves ################# ");
	
					returnLog.add(GenerateSWF.executeScript(
							"addStartEndToAudio", argv_sox));
	
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
					flvRecordingMetaData.setFullWavAudioData(hashFileFullName);
	
					// Finally add it to the row!
					listOfFullWaveFiles.add(outputFullWav);
	
				}
	
				flvRecordingMetaDataDaoImpl
						.updateFlvRecordingMetaData(flvRecordingMetaData);
	
			}
		} catch (Exception err) {
			log.error("[stripAudioFromFLVs]", err);
		}
	}
}
