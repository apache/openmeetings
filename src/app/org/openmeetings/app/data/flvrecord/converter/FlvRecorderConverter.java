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
			this.stripAudioFromFLVs(flvRecording, flvRecordingId);
			
		} catch (Exception err) {
			log.error("[startConversion]",err);
		}
	}
	
	private String getPathToFFMPEG() {
		String pathToFFMPEG = this.configurationmanagement
				.getConfKey(3, "imagemagick_path").getConf_value();
		if (!pathToFFMPEG.equals("")
				&& !pathToFFMPEG.endsWith(File.separator)) {
			pathToFFMPEG += File.separator;
		}
		pathToFFMPEG += "ffmpeg";
		return pathToFFMPEG;
	}
	
	public void stripAudioFromFLVs(FlvRecording flvRecording, Long flvRecordingId) {
		List<HashMap<String, Object>> returnLog = new LinkedList<HashMap<String, Object>>();
		try {
			
			List<FlvRecordingMetaData> metaDataList = this.getFlvRecordingMetaDataDaoImpl().getFlvRecordingMetaDataAudioFLVsByRecording(flvRecordingId);
			
			// Init variables
			String streamFolderName = ScopeApplicationAdapter.webAppPath + File.separatorChar + 
					"streams" + File.separatorChar + flvRecording.getRoom_id() + File.separatorChar;
			
			log.debug("###################################################");
			log.debug("### streamFolderName - "+streamFolderName);
			log.debug("### meta Data Number - "+metaDataList.size());
			log.debug("###################################################");
			
			for (FlvRecordingMetaData flvRecordingMetaData : metaDataList) {
				
				String inputFlv = streamFolderName + flvRecordingMetaData.getStreamName() + ".flv";
				
				String hashFileName = MD5.do_checksum("WAVE"+new java.util.Date().getTime())+".wav";
				String outputWav = streamFolderName + hashFileName;

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
				
			}
			
			for (HashMap<String, Object> returnMap : returnLog) {
				this.flvRecordingLogDaoImpl.addFLVRecordingLog("ffmpeg", flvRecording, returnMap);
			}
			
		} catch (Exception err) {
			log.error("[stripAudioFromFLVs]",err);
		}
	}

}
