package org.openmeetings.app.data.flvrecord.converter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.openmeetings.app.data.basic.Configurationmanagement;
import org.openmeetings.app.data.file.dao.FileExplorerItemDaoImpl;
import org.openmeetings.app.data.flvrecord.FlvRecordingLogDaoImpl;
import org.openmeetings.app.documents.GenerateSWF;
import org.openmeetings.app.hibernate.beans.files.FileExplorerItem;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class FlvExplorerConverter {

	private static final Logger log = Red5LoggerFactory.getLogger(FlvExplorerConverter.class);
	
	//Spring loaded Beans
	private Configurationmanagement configurationmanagement;
	private FileExplorerItemDaoImpl fileExplorerItemDaoImpl;
	private FlvRecordingLogDaoImpl flvRecordingLogDaoImpl;

	public Configurationmanagement getConfigurationmanagement() {
		return configurationmanagement;
	}
	public void setConfigurationmanagement(
			Configurationmanagement configurationmanagement) {
		this.configurationmanagement = configurationmanagement;
	}
	
	public FileExplorerItemDaoImpl getFileExplorerItemDaoImpl() {
		return fileExplorerItemDaoImpl;
	}
	public void setFileExplorerItemDaoImpl(
			FileExplorerItemDaoImpl fileExplorerItemDaoImpl) {
		this.fileExplorerItemDaoImpl = fileExplorerItemDaoImpl;
	}
	
	public FlvRecordingLogDaoImpl getFlvRecordingLogDaoImpl() {
		return flvRecordingLogDaoImpl;
	}
	public void setFlvRecordingLogDaoImpl(
			FlvRecordingLogDaoImpl flvRecordingLogDaoImpl) {
		this.flvRecordingLogDaoImpl = flvRecordingLogDaoImpl;
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
	
	public void startConversion(Long fileExplorerItemId, String moviePath) {
		try {

			FileExplorerItem fileExplorerItem = this.fileExplorerItemDaoImpl.getFileExplorerItemsById(fileExplorerItemId);
			
			log.debug("fileExplorerItem " + fileExplorerItem.getFileExplorerItemId());

			// Strip Audio out of all Audio-FLVs
			this.convertToFLV(fileExplorerItem, moviePath);

			// Add empty pieces at the beginning and end of the wav

		} catch (Exception err) {
			log.error("[startConversion]", err);
		}
		
	}
	
	private void convertToFLV(FileExplorerItem fileExplorerItem, String moviePath) {
		try {
			
			List<HashMap<String, Object>> returnLog = new LinkedList<HashMap<String, Object>>();
			
			String streamFolderName = ScopeApplicationAdapter.webAppPath
										+ File.separatorChar + "streams" + File.separatorChar
										+ "hibernate" + File.separatorChar;
			
			String outputFullFlv = streamFolderName + "UPLOADFLV_" + fileExplorerItem.getFileExplorerItemId() + ".flv";
			
			int flvWidth = 300;
			int flvHeight = 240;
			
			fileExplorerItem.setFlvWidth(flvWidth);
			fileExplorerItem.setFlvHeight(flvHeight);
			fileExplorerItem.setIsVideo(true);

			String[] argv_fullFLV = null;

			argv_fullFLV = new String[] { this.getPathToFFMPEG(), "-i",
					moviePath, "-ar", "22050",
					"-acodec", "libmp3lame", "-ab", "32k", "-s",
					flvWidth + "x" + flvHeight, "-vcodec", "flv",
					outputFullFlv };

			log.debug("START generateFullFLV ################# ");
			String tString = "";
			for (int i = 0; i < argv_fullFLV.length; i++) {
				tString += argv_fullFLV[i] + " ";
				//log.debug(" i " + i + " argv-i " + argv_fullFLV[i]);
			}
			log.debug(tString);
			log.debug("END generateFullFLV ################# ");

			returnLog.add(GenerateSWF.executeScript("uploadFLV ID :: "+fileExplorerItem.getFileExplorerItemId(),
					argv_fullFLV));

			
			String hashFileFullNameJPEG = "UPLOADFLV_" + fileExplorerItem.getFileExplorerItemId() + ".jpg";
			String outPutJpeg = streamFolderName + hashFileFullNameJPEG;
	
			fileExplorerItem.setPreviewImage(hashFileFullNameJPEG);
	
			String[] argv_previewFLV = new String[] { this.getPathToFFMPEG(),
					"-i", outputFullFlv, "-vcodec", "mjpeg", "-vframes", "1",
					"-an", "-f", "rawvideo", "-s", flvWidth + "x" + flvHeight,
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
	
			returnLog.add(GenerateSWF.executeScript("previewUpload ID :: "+fileExplorerItem.getFileExplorerItemId(),
					argv_previewFLV));
			
			this.fileExplorerItemDaoImpl.updateFileOrFolder(fileExplorerItem);

			for (HashMap<String, Object> returnMap : returnLog) {
				this.flvRecordingLogDaoImpl.addFLVRecordingLog(
						"generateFFMPEG", null, returnMap);
			}
			
		} catch (Exception err) {
			log.error("[convertToFLV]", err);
		}
		
	}
	
}
