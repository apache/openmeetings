package org.openmeetings.app.data.flvrecord.converter;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.openmeetings.app.data.basic.Configurationmanagement;
import org.openmeetings.app.data.flvrecord.FlvRecordingDaoImpl;
import org.openmeetings.app.data.flvrecord.FlvRecordingLogDaoImpl;
import org.openmeetings.app.data.flvrecord.FlvRecordingMetaDataDaoImpl;
import org.openmeetings.app.documents.GenerateImage;
import org.openmeetings.app.documents.GenerateSWF;
import org.openmeetings.app.hibernate.beans.flvrecord.FlvRecording;
import org.openmeetings.app.hibernate.beans.flvrecord.FlvRecordingMetaData;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.openmeetings.utils.crypt.MD5;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class FlvRecorderConverter {

	private static final Logger log = Red5LoggerFactory.getLogger(FlvRecorderConverter.class);
	
	//Spring loaded Beans
	private FlvRecordingDaoImpl flvRecordingDaoImpl = null;
	private FlvRecordingMetaDataDaoImpl flvRecordingMetaDataDaoImpl = null;
	private Configurationmanagement configurationmanagement;
	private FlvRecordingLogDaoImpl flvRecordingLogDaoImpl;

	public FlvRecordingDaoImpl getFlvRecordingDaoImpl() {
		return flvRecordingDaoImpl;
	}
	public void setFlvRecordingDaoImpl(FlvRecordingDaoImpl flvRecordingDaoImpl) {
		this.flvRecordingDaoImpl = flvRecordingDaoImpl;
	}

	public FlvRecordingMetaDataDaoImpl getFlvRecordingMetaDataDaoImpl() {
		return flvRecordingMetaDataDaoImpl;
	}
	public void setFlvRecordingMetaDataDaoImpl(
			FlvRecordingMetaDataDaoImpl flvRecordingMetaDataDaoImpl) {
		this.flvRecordingMetaDataDaoImpl = flvRecordingMetaDataDaoImpl;
	}
	
	public Configurationmanagement getConfigurationmanagement() {
		return configurationmanagement;
	}
	public void setConfigurationmanagement(
			Configurationmanagement configurationmanagement) {
		this.configurationmanagement = configurationmanagement;
	}
	
	public FlvRecordingLogDaoImpl getFlvRecordingLogDaoImpl() {
		return flvRecordingLogDaoImpl;
	}
	public void setFlvRecordingLogDaoImpl(
			FlvRecordingLogDaoImpl flvRecordingLogDaoImpl) {
		this.flvRecordingLogDaoImpl = flvRecordingLogDaoImpl;
	}
	
	public void startConversion(Long flvRecordingId) {
		try {
			
			FlvRecording flvRecording = this.flvRecordingDaoImpl.getFlvRecordingById(flvRecordingId);
			log.debug("flvRecording "+flvRecording.getFlvRecordingId());
			
			//Strip Audio out of all Audio-FLVs
			this.stripAudioFromFLVs(flvRecording);
			
			//Add empty pieces at the beginning and end of the wav
			
			
		} catch (Exception err) {
			log.error("[startConversion]",err);
		}
	}
	
	private String getPathToFFMPEG() {
		String pathToFFMPEG = this.configurationmanagement
				.getConfKey(3, "ffmpeg_path").getConf_value();
		if (!pathToFFMPEG.equals("")
				&& !pathToFFMPEG.endsWith(File.separator)) {
			pathToFFMPEG += File.separator;
		}
		pathToFFMPEG += "ffmpeg";
		return pathToFFMPEG;
	}
	
	private String getPathToSoX() {
		String pathToSoX = this.configurationmanagement
				.getConfKey(3, "sox_path").getConf_value();
		if (!pathToSoX.equals("")
				&& !pathToSoX.endsWith(File.separator)) {
			pathToSoX += File.separator;
		}
		pathToSoX += "sox";
		return pathToSoX;
	}
	
	public void stripAudioFromFLVs(FlvRecording flvRecording) {
		List<HashMap<String, Object>> returnLog = new LinkedList<HashMap<String, Object>>();
		try {
			
			List<FlvRecordingMetaData> metaDataList = this.getFlvRecordingMetaDataDaoImpl().getFlvRecordingMetaDataAudioFlvsByRecording(flvRecording.getFlvRecordingId());
			
			// Init variables
			String streamFolderName = ScopeApplicationAdapter.webAppPath + File.separatorChar + 
										"streams" + File.separatorChar + flvRecording.getRoom_id() + File.separatorChar;
			
			log.debug("###################################################");
			log.debug("### streamFolderName - "+streamFolderName);
			log.debug("### meta Data Number - "+metaDataList.size());
			log.debug("###################################################");
			
			List<String> listOfFullWaveFiles = new LinkedList<String>();
			
			for (FlvRecordingMetaData flvRecordingMetaData : metaDataList) {
				
				String inputFlv = streamFolderName + flvRecordingMetaData.getStreamName() + ".flv";
				
				String hashFileName = flvRecordingMetaData.getStreamName()+"_WAVE.wav";
				String outputWav = streamFolderName + hashFileName;
				
				flvRecordingMetaData.setWavAudioData(hashFileName);

				String[] argv = new String[] {
						this.getPathToFFMPEG(), 
						"-i",
						inputFlv,
						outputWav };

				log.debug("START stripAudioFromFLVs ################# ");
				for (int i=0;i<argv.length;i++) {
					log.debug(" i "+i+" argv-i "+argv[i]);
				}
				log.debug("END stripAudioFromFLVs ################# ");
				
				returnLog.add( GenerateSWF.executeScript("generateFFMPEG", argv) );
				
				//Strip Wave to Full Length
				String hashFileFullName = flvRecordingMetaData.getStreamName()+"_FULL_WAVE.wav";
				String outputFullWav = streamFolderName + hashFileFullName;
				
				listOfFullWaveFiles.add(outputFullWav);
				
				flvRecordingMetaData.setFullWavAudioData(hashFileFullName);
				
				//Calculate delta at beginning
				Long deltaTimeStartMilliSeconds = flvRecordingMetaData.getRecordStart().getTime() - flvRecording.getRecordStart().getTime();
				Float startPadding = Float.parseFloat(deltaTimeStartMilliSeconds.toString())/1000;
				
				//Calculate delta at ending
				Long deltaTimeEndMilliSeconds = flvRecording.getRecordEnd().getTime() - flvRecordingMetaData.getRecordEnd().getTime();
				Float endPadding = Float.parseFloat(deltaTimeEndMilliSeconds.toString())/1000;
				
				String[] argv_sox = new String[] {
						this.getPathToSoX(), 
						outputWav,
						outputFullWav,
						"pad",
						startPadding.toString(),
						endPadding.toString()};
				
				log.debug("START addAudioToWaves ################# ");
				for (int i=0;i<argv_sox.length;i++) {
					log.debug(" i "+i+" argv-i "+argv_sox[i]);
				}
				log.debug("END addAudioToWaves ################# ");
				
				returnLog.add( GenerateSWF.executeScript("generateWave", argv_sox) );
				
				this.getFlvRecordingMetaDataDaoImpl().updateFlvRecordingMetaData(flvRecordingMetaData);
				
			}
			
			//Merge Wave to Full Length
			
			FlvRecordingMetaData flvRecordingMetaDataOfScreen = this.flvRecordingMetaDataDaoImpl.getFlvRecordingMetaDataScreenFlvByRecording(flvRecording.getFlvRecordingId());
			String hashFileFullName = flvRecordingMetaDataOfScreen.getStreamName()+"_FINAL_WAVE.wav";
			String outputFullWav = streamFolderName + hashFileFullName;
			
			String[] argv_full_sox = new String[listOfFullWaveFiles.size()+3];
			argv_full_sox[0] = this.getPathToSoX();
			argv_full_sox[1] = "-m";
			
			int counter = 0;
			for (int i=0;i<listOfFullWaveFiles.size();i++) {
				argv_full_sox[2+i] = listOfFullWaveFiles.get(i); 
				counter = i;
			}
			argv_full_sox[counter+3] = outputFullWav;
			
			log.debug("START mergeAudioToWaves ################# ");
			for (int i=0;i<argv_full_sox.length;i++) {
				log.debug(" i "+i+" argv-i "+argv_full_sox[i]);
			}
			log.debug("END mergeAudioToWaves ################# ");
			
			flvRecordingMetaDataOfScreen.setFullWavAudioData(hashFileFullName);
			
			this.flvRecordingMetaDataDaoImpl.updateFlvRecordingMetaData(flvRecordingMetaDataOfScreen);
			
			returnLog.add( GenerateSWF.executeScript("mergeWave", argv_full_sox) );
			
			//Merge Audio with Video
			
			String inputScreenFullFlv = streamFolderName + flvRecordingMetaDataOfScreen.getStreamName() + ".flv";
			
			String streamFolderGeneralName = ScopeApplicationAdapter.webAppPath + File.separatorChar + 
												"streams" + File.separatorChar + "hibernate" + File.separatorChar;
			
			File outputFolder = new File(streamFolderGeneralName);
			if (!outputFolder.exists()) {
				outputFolder.mkdir();
			}
			
			String hashFileFullNameFlv = "flvRecording_"+flvRecording.getFlvRecordingId()+".flv";
			String outputFullFlv = streamFolderGeneralName + hashFileFullNameFlv;
			
			//ffmpeg -vcodec flv -qscale 9.5 -r 25 -ar 22050 -ab 32k -s 320x240 -i 65318fb5c54b1bc1b1bca077b493a914_28_12_2009_23_38_17_FINAL_WAVE.wav -i 65318fb5c54b1bc1b1bca077b493a914_28_12_2009_23_38_17.flv final1.flv
			
			int flvWidth = 640;
			int flvHeight = 480;
			
			if (flvRecording.getWidth() >= 1280 || flvRecording.getWidth() <= 1600) {
				Double scaleFactor = 2D;
				
				log.debug("scaleFactor :: "+scaleFactor);
				
				flvWidth = Double.valueOf(Math.round (flvRecording.getWidth() / scaleFactor)).intValue();
				flvHeight = Double.valueOf(Math.round (flvRecording.getHeight() / scaleFactor)).intValue();
				
			} else if (flvRecording.getWidth() > 640) {
				Double scaleFactor = (Math.floor(flvRecording.getWidth() / 640))+1;
				
				log.debug("scaleFactor :: "+scaleFactor);
				
				flvWidth = Double.valueOf(Math.round (flvRecording.getWidth() / scaleFactor)).intValue();
				flvHeight = Double.valueOf(Math.round (flvRecording.getHeight() / scaleFactor)).intValue();
				
			}
			
			flvRecording.setFlvWidth(flvWidth);
			flvRecording.setFlvHeight(flvHeight);
			
			String[] argv_fullFLV = new String[] {
					this.getPathToFFMPEG(), 
					"-i",
					inputScreenFullFlv,
					"-i",
					outputFullWav,
					"-ar",
					"22050",
					"-acodec",
					"libmp3lame",
					"-ab",
					"32k",
					"-s",
					flvWidth+"x"+flvHeight,
					"-vcodec",
					"flashsv",
					"-map",
					"0.0",
					"-map",
					"1.0",
					outputFullFlv};

			log.debug("START generateFullFLV ################# ");
			for (int i=0;i<argv_fullFLV.length;i++) {
				log.debug(" i "+i+" argv-i "+argv_fullFLV[i]);
			}
			log.debug("END generateFullFLV ################# ");
			
			returnLog.add( GenerateSWF.executeScript("generateFullFLV", argv_fullFLV) );
			
			flvRecording.setFileHash(hashFileFullNameFlv);
			
			
			//Extract first Image for preview purpose
			//ffmpeg -i movie.flv -vcodec mjpeg -vframes 1 -an -f rawvideo -s 320x240 movie.jpg
			
			String hashFileFullNameJPEG = "flvRecording_"+flvRecording.getFlvRecordingId()+".jpg";
			String outPutJpeg = streamFolderGeneralName + hashFileFullNameJPEG;

			flvRecording.setPreviewImage(hashFileFullNameJPEG);
			
			String[] argv_previewFLV = new String[] {
					this.getPathToFFMPEG(), 
					"-i",
					outputFullFlv,
					"-vcodec",
					"mjpeg",
					"-vframes",
					"1",
					"-an",
					"-f",
					"rawvideo",
					"-s",
					flvWidth+"x"+flvHeight,
					outPutJpeg};

			log.debug("START previewFullFLV ################# ");
			for (int i=0;i<argv_previewFLV.length;i++) {
				log.debug(" i "+i+" argv-i "+argv_previewFLV[i]);
			}
			log.debug("END previewFullFLV ################# ");
			
			returnLog.add( GenerateSWF.executeScript("generateFullFLV", argv_previewFLV) );
			
			
			this.flvRecordingDaoImpl.updateFlvRecording(flvRecording);
			
			for (HashMap<String, Object> returnMap : returnLog) {
				this.flvRecordingLogDaoImpl.addFLVRecordingLog("generateFFMPEG", flvRecording, returnMap);
			}
			
		} catch (Exception err) {
			log.error("[stripAudioFromFLVs]",err);
		}
	}
	
	
	
	
}
