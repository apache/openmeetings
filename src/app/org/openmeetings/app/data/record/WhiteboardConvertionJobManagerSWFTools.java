package org.openmeetings.app.data.record;

import java.io.File;
import java.io.FileWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;
import java.util.Map;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.slf4j.Logger;
import org.red5.logging.Red5LoggerFactory;
import org.openmeetings.app.data.record.dao.RecordingConversionJobDaoImpl;
import org.openmeetings.app.data.record.dao.RecordingDaoImpl;
import org.openmeetings.app.data.record.dao.RoomRecordingDaoImpl;
import org.openmeetings.app.data.record.dao.WhiteBoardEventDaoImpl;
import org.openmeetings.app.documents.GenerateImage;
import org.openmeetings.app.documents.GenerateSWF;
import org.openmeetings.app.persistence.beans.recording.Recording;
import org.openmeetings.app.persistence.beans.recording.RecordingConversionJob;
import org.openmeetings.app.persistence.beans.recording.RoomRecording;
import org.openmeetings.app.persistence.beans.recording.WhiteBoardEvent;
import org.openmeetings.app.remote.StreamService;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.openmeetings.utils.math.CalendarPatterns;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.XppDriver;

public class WhiteboardConvertionJobManagerSWFTools {
	
	//This is the amount of time the Conversion Job will create an SVN
	//So 200 Milliseconds == 5 Images per second
	private static Long numberOfMilliseconds = 200L;
	
	//This is the number of Images of SVG Files per Folder
	//Its rather small as ImageMagick seems to have problems in 
	//processing bigger numbers
	private static Long maxNumberOfBatchFolderSize = 10L;
	
	//This is the bpp (Bits per Pixel) BUT in this case its meant
	//for each channel ... RGB each one 8Bit
	//by default ImageMagick uses 16 buts it seems like SWFTools only can 
	//take 8Bit
	private static int depth = 8;
	
	//This is the number of SVGs that will be created in one
	//quartz scheduler run
	private static int svgBatchProcessByQuery = 100;
	
	private static boolean isRunning = false;
	
	//in JUnit tests you have to change that to true
	//see also Methods on bottom to change the Default Out dir in JUnit
	private static boolean isDebug = false;

	private static final Logger log = Red5LoggerFactory.getLogger(WhiteboardConvertionJobManagerSWFTools.class, ScopeApplicationAdapter.webAppRootKey);

	private WhiteboardConvertionJobManagerSWFTools() {
	}

	private static WhiteboardConvertionJobManagerSWFTools instance = null;

	public static synchronized WhiteboardConvertionJobManagerSWFTools getInstance() {
		if (instance == null) {
			instance = new WhiteboardConvertionJobManagerSWFTools();
		}

		return instance;
	}
	
	public synchronized void initJobs() {
		try {
			if (!isRunning) {
				isRunning = true;
				
				List<Recording> recordingsNonConversionStarted = RecordingDaoImpl.getInstance().getRecordingWhiteboardToConvert();
	
				for (Recording recording : recordingsNonConversionStarted) {
					recording.setWhiteBoardConverted(true);
					RecordingDaoImpl.getInstance().updateRecording(recording);
				}
				//log.debug("initJobs: "+recordingsNonConversionStarted.size());
				
				for (Recording recording : recordingsNonConversionStarted) {
					
					RecordingConversionJob recordingConversionJob = new RecordingConversionJob();
					recordingConversionJob.setRecording(recording);
					recordingConversionJob.setStarted(new Date());
					recordingConversionJob.setEndTimeInMilliSeconds(0L);
					recordingConversionJob.setImageNumber(0L);
					recordingConversionJob.setCurrentWhiteBoardAsXml("");
					
					RecordingConversionJobDaoImpl.getInstance().addRecordingConversionJob(recordingConversionJob);
					
				}
				
				//Do SVG Conversion for next 100 SVG Images, that should fill one Folder
				for (int i=0;i<svgBatchProcessByQuery;i++) {
					this.processJobs();
				}
				
				//Do SVG to PNG Batch Conversion
				this.processConvertionJobs();
				
				//Do PNG to SWF Conversion
				this.processConvertionSWFJobs();
				
				isRunning = false;
			} else {
				
				log.debug("CANNOT PROCESS WAIT FOR FREE SLOT");
				
			}
		} catch (Exception err) {
			log.error("[initJobs]",err);
		}
	}

	public synchronized void processJobs() {
		try {
			
			List<RecordingConversionJob> listOfConversionJobs = RecordingConversionJobDaoImpl.getInstance().getRecordingConversionJobs();
			
			//log.debug("processJobs: "+listOfConversionJobs.size());
			
			for (RecordingConversionJob recordingConversionJob : listOfConversionJobs) {
				
				//log.debug("TIME INITIAL : "+recordingConversionJob.getEndTimeInMilliSeconds());
				
				if (recordingConversionJob.getEndTimeInMilliSeconds().equals(0L)) {
					
					log.debug("DRAW INITIAL IMAGE");
					
					XStream xStream_temp = new XStream(new XppDriver());
					xStream_temp.setMode(XStream.NO_REFERENCES);
					
					String roomRecordingInXML = recordingConversionJob.getRecording().getRoomRecording().getInitwhiteboardvarsInXml();
					
					Map initWhiteBoardObjects = (Map) xStream_temp.fromXML(roomRecordingInXML);
					
					this.generateFileAsSVG(initWhiteBoardObjects, roomRecordingInXML, recordingConversionJob);
			       
				} else {
					
					log.debug("DRAW NEXT IMAGE");
					
					if (recordingConversionJob.getRecording().getDuration() >= recordingConversionJob.getEndTimeInMilliSeconds()) {
					
						List<WhiteBoardEvent> whiteBoardEventList = WhiteBoardEventDaoImpl.getInstance().
								getWhiteboardEventsInRange(
										recordingConversionJob.getEndTimeInMilliSeconds(), 
										recordingConversionJob.getEndTimeInMilliSeconds()+(numberOfMilliseconds-1), 
										recordingConversionJob.getRecording().getRoomRecording().getRoomrecordingId());
						
						
						log.debug("whiteBoardEventList SIZE "+whiteBoardEventList.size());
						
						XStream xStream_temp = new XStream(new XppDriver());
						xStream_temp.setMode(XStream.NO_REFERENCES);
						
						String roomRecordingInXML = recordingConversionJob.getCurrentWhiteBoardAsXml();
						Map whiteBoardObjects = (Map) xStream_temp.fromXML(roomRecordingInXML);
							
						//Do simulate Whiteboard Events in Temp Object
						
						for (WhiteBoardEvent whiteBoardEvent : whiteBoardEventList) {
							
							log.debug("whiteBoardEvent: "+whiteBoardEvent.getStarttime());
							
							XStream xStream_temp_action = new XStream(new XppDriver());
							xStream_temp_action.setMode(XStream.NO_REFERENCES);
							
							Map actionObj = (Map) xStream_temp_action.fromXML(whiteBoardEvent.getAction());
							
							log.debug("whiteBoardEvent: "+actionObj);
							
							Date dateOfEvent = (Date) actionObj.get(1);
							String action = actionObj.get(2).toString();	
							Map actionObject = (Map) actionObj.get(3);
							
							log.debug("action: "+action);
							
							if (action.equals("draw") || action.equals("redo")){
								
								//log.debug(actionObject);
								//log.debug(actionObject.size()-1);
								//log.debug(actionObject.get(actionObject.size()-1));
								
								String objectOID = actionObject.get(actionObject.size()-1).toString();
								log.debug("objectOID: "+objectOID);
								whiteBoardObjects.put(objectOID, actionObject);
							} else if (action.equals("clear")) {
								whiteBoardObjects = new HashMap<String,Map>();
							} else if (action.equals("delete") || action.equals("undo")) {
								String objectOID = actionObject.get(actionObject.size()-1).toString();
								log.debug("removal of objectOID: "+objectOID);
								whiteBoardObjects.remove(objectOID);
							} else if (action.equals("size") || action.equals("editProp") 
									|| action.equals("editText")) {
								String objectOID = actionObject.get(actionObject.size()-1).toString();
								//Map roomItem = (Map) whiteBoardObjects.get(objectOID);
								whiteBoardObjects.put(objectOID, actionObject);
								
							} else {
								log.warn("Unkown Type: "+action+" actionObject: "+actionObject);
							}
							
						}
						
						XStream xStream_temp_store = new XStream(new XppDriver());
						xStream_temp_store.setMode(XStream.NO_REFERENCES);
						String roomRecordingInXMLToSave = xStream_temp_store.toXML(whiteBoardObjects);
						
						this.generateFileAsSVG(whiteBoardObjects, roomRecordingInXMLToSave, recordingConversionJob);
					
					} else {
						
						log.debug("THIS FILE IS PROCESSED UPDATE: "+recordingConversionJob.getRecordingConversionJobId());
						
						//FIXME: this should happen only one time per conversion Job
						
						recordingConversionJob.setEnded(new Date());
						recordingConversionJob.setStartedPngConverted(new Date());
						recordingConversionJob.setBatchProcessCounter(0L);
						RecordingConversionJobDaoImpl.getInstance().updateRecordingConversionJobs(recordingConversionJob);
						
					}
				}
				
			}
			
		} catch (Exception err) {
			log.error("[processJobs]",err);
		}
		
	}
	
	public synchronized void processConvertionJobs() {
		try {
			
			List<RecordingConversionJob> listOfConversionJobs = RecordingConversionJobDaoImpl.getInstance().getRecordingConversionBatchConversionJobs();
			
			//log.debug("processBatchJobs SIZE: "+listOfConversionJobs.size());
			
			for (RecordingConversionJob recordingConversionJob : listOfConversionJobs) {
				
				Double maxFolderDoub = Math.floor(recordingConversionJob.getImageNumber() / maxNumberOfBatchFolderSize);
				
				int maxFolder = maxFolderDoub.intValue();
				
				if (maxFolder >= recordingConversionJob.getBatchProcessCounter()) {
					
					Map<String,String> outputFileNames = null;
					if (isDebug) {
						outputFileNames = this.generateBatchFileDebug(recordingConversionJob.getRecordingConversionJobId(), 
											recordingConversionJob.getBatchProcessCounter());
					} else {
						outputFileNames = this.generateBatchFile(recordingConversionJob.getRecordingConversionJobId(), 
								recordingConversionJob.getBatchProcessCounter());
					}
					
					
					
					GenerateImage.getInstance().convertImageByTypeAndSizeAndDepth(
							outputFileNames.get("input"), 
							outputFileNames.get("output"), 
							660, 580, depth);
					
					//Add Count For next Round
					RecordingConversionJob recordingConversionJobUpdate = RecordingConversionJobDaoImpl.getInstance().
						getRecordingConversionJobsByRecordingConversionJobsId(recordingConversionJob.getRecordingConversionJobId());
				
					recordingConversionJobUpdate.setBatchProcessCounter(recordingConversionJob.getBatchProcessCounter()+1);
					RecordingConversionJobDaoImpl.getInstance().updateRecordingConversionJobs(recordingConversionJobUpdate);
					
				} else {
					
					log.debug("Batch Processing Done");
					RecordingConversionJob recordingConversionJobUpdate = RecordingConversionJobDaoImpl.getInstance().
						getRecordingConversionJobsByRecordingConversionJobsId(recordingConversionJob.getRecordingConversionJobId());
					
					recordingConversionJobUpdate.setEndPngConverted(new Date());
					recordingConversionJobUpdate.setStartedSWFConverted(new Date());
					RecordingConversionJobDaoImpl.getInstance().updateRecordingConversionJobs(recordingConversionJobUpdate);
					
				}
				
			}
			
		} catch (Exception err) {
			log.error("[processConvertionJobs]",err);
		}
	}
	
	public synchronized void processConvertionSWFJobs() {
		try {
			
			List<RecordingConversionJob> listOfSWFConversionJobs = RecordingConversionJobDaoImpl.getInstance().getRecordingConversionSWFConversionJobs();
			
			log.debug("processSWFJobs SIZE: "+listOfSWFConversionJobs.size());
			
			for (RecordingConversionJob recordingConversionJob : listOfSWFConversionJobs) {
				
				//Get All Images
				
				Double maxFolderDoub = Math.floor(recordingConversionJob.getImageNumber() / maxNumberOfBatchFolderSize);
				long maxFolder = maxFolderDoub.longValue();
				
				List<String> images = new LinkedList<String>();
				
				for (long i=0;i<maxFolder;i++) {
					
					String folderName = this.getBatchFileFolder(recordingConversionJob.getRecordingConversionJobId(), i);	
					
					for (int k=0;k<maxNumberOfBatchFolderSize;k++) {
						
						//three times to simulate a FPS of 30 while we only generate
						//an image every 200 milliseconds (would be fps of 10)
						images.add(folderName+k+".png");
						images.add(folderName+k+".png");
						images.add(folderName+k+".png");
						
					}
					
				}
				
				//restImages will be always smaller then maxNumberOfBatchFolderSize
				long restImages = recordingConversionJob.getImageNumber() - (maxFolder*maxNumberOfBatchFolderSize);
				
				log.debug("restImages: "+restImages);
				
				for (int k=0;k<restImages;k++) {
					
					String folderName = this.getBatchFileFolder(recordingConversionJob.getRecordingConversionJobId(), maxFolder);	
					
					//three times to simulate a FPS of 30 while we only generate
					//an image every 200 milliseconds (would be fps of 10)
					images.add(folderName+k+".png");
					images.add(folderName+k+".png");
					images.add(folderName+k+".png");
					
				}
				
				log.debug("images: "+images); 
				log.debug("images: "+images.size()); 
				
				String output = this.getSWFFileForResult(recordingConversionJob.getRecordingConversionJobId());
				
				String outputFolder = this.getSWFFolderForResult(recordingConversionJob.getRecordingConversionJobId());
				
				//Generate that SWF only by 1000 Files as png2swf.c has a MAX_INPUT_FILE of 1024
				
				//This workaround is just to workaround the 1024 File LIMIT of SWFTools!!!
				
				List<String> outputfiles = new LinkedList<String>();
				
				int maxnumber = Double.valueOf(Math.floor(images.size() / 1000)).intValue();
				
				log.debug("maxnumber: "+maxnumber);
				
				if (maxnumber > 0) {
					for (int i=0;i<=maxnumber;i++) {
						
						log.debug("Generate SWF for Items: "+i+" TO : "+(i*1000));
						String fileName = outputFolder + File.separatorChar + "whiteboard"+i+".swf";
						outputfiles.add(fileName);
						
						List<String> imageForConvert = new LinkedList<String>();
						for (int k=0;k<1000;k++) {
							int index = k+(i*1000);
							log.debug("index Number "+index+" size: "+images.size());
							if (index < images.size()) {
								imageForConvert.add(images.get(index));
							} else {
								break;
							}
						}
						
						GenerateSWF.getInstance().generateSwfByImages(imageForConvert, fileName, 30);
					}
					
					//Combine Resulting SWFs to one SWF
					
					//FIXME: IT SEEMS LIKE SWFCOMBINE DOES PRODUCE INVALID Files!!
					GenerateSWF.getInstance().generateSWFByCombine(outputfiles, output, 30);
					
				} else {
					
					//Directly write to SWF
					GenerateSWF.getInstance().generateSwfByImages(images, output, 30);
					
					
				}
				RecordingConversionJob recordingConversionJobUpdate = RecordingConversionJobDaoImpl.getInstance().
					getRecordingConversionJobsByRecordingConversionJobsId(recordingConversionJob.getRecordingConversionJobId());
		
				recordingConversionJobUpdate.setEndSWFConverted(new Date());
				RecordingConversionJobDaoImpl.getInstance().updateRecordingConversionJobs(recordingConversionJobUpdate);
				
			}
			
		} catch (Exception err) {
			log.error("[processConvertionSWFJobs]",err);
		}
	}	
	
	private void generateFileAsSVG(Map whiteBoardObjects, String roomRecordingInXML, RecordingConversionJob recordingConversionJob) throws Exception {
		
		// Get a DOMImplementation.
        DOMImplementation domImpl =
            GenericDOMImplementation.getDOMImplementation();

        // Create an instance of org.w3c.dom.Document.
        //String svgNS = "http://www.w3.org/2000/svg";
        String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;

        Document document = domImpl.createDocument(svgNS, "svg", null);
        
        // Get the root element (the 'svg' element).
        Element svgRoot = document.getDocumentElement();

        
        // Set the width and height attributes on the root 'svg' element.
        svgRoot.setAttributeNS(null, "width", ""+660);
        svgRoot.setAttributeNS(null, "height", ""+620);
        

        // Create an instance of the SVG Generator.
        SVGGraphics2D svgGenerator = new SVGGraphics2D(document);
        
        //FIXME
        //svgGenerator = WhiteboardMapToSVG.getInstance().convertMapToSVG(svgGenerator, whiteBoardObjects);
		
        // Finally, stream out SVG to the standard output using
        // UTF-8 encoding.
        boolean useCSS = true; // we want to use CSS style attributes
        //Writer out = new OutputStreamWriter(System.out, "UTF-8");
        
       //log.debug(out.toString());
        
//       String firstImageName = this.generateFileName(recordingConversionJob.getRecordingConversionJobId(), fileNumber);
//       log.debug("Write File To: "+firstImageName);
//       
//       FileWriter fileWriter = new FileWriter(firstImageName);
//       
//       svgGenerator.stream(fileWriter, useCSS);
       
//       StringWriter stringWriter = new StringWriter();
//       
//       svgGenerator.stream(stringWriter, useCSS);
//       
//       log.debug("stringWriter"+stringWriter.toString());

        String firstImageName = "";
        
        if (isDebug) {
        	firstImageName = this.generateSVGFileDebug(
        				recordingConversionJob.getRecordingConversionJobId(), 
        				recordingConversionJob.getImageNumber());
        } else {
        	firstImageName = this.generateSVGFileName(
    				recordingConversionJob.getRecordingConversionJobId(), 
    				recordingConversionJob.getImageNumber());
        }

        log.debug("Write File To: " + firstImageName);

		FileWriter fileWriter = new FileWriter(firstImageName);
		svgGenerator.stream(fileWriter, useCSS);
		
		RecordingConversionJob recordingConversionJobToStore = RecordingConversionJobDaoImpl.getInstance().getRecordingConversionJobsByRecordingConversionJobsId(
				recordingConversionJob.getRecordingConversionJobId());

		recordingConversionJobToStore.setEndTimeInMilliSeconds(recordingConversionJob.getEndTimeInMilliSeconds() + numberOfMilliseconds);
		recordingConversionJobToStore.setCurrentWhiteBoardAsXml(roomRecordingInXML);
		recordingConversionJobToStore.setImageNumber(recordingConversionJob.getImageNumber()+1);
		
		log.debug("updateRecordingConversionJobs: generateFileAsSVG");
		RecordingConversionJobDaoImpl.getInstance().updateRecordingConversionJobs(recordingConversionJobToStore);
       
	}
	
	private String generateSVGFileDebug(Long conversionJobId, Long imageNumber) throws Exception {
       String recordingRootDir  = "/Users/swagner/Documents/work/red5_distros/red5_r3200_snapshot/webapps/openmeetings/test/";
       
       String recordingFileDir = recordingRootDir + File.separatorChar + conversionJobId;
       File recordingFileDirFolder = new File(recordingFileDir);
       if (!recordingFileDirFolder.exists()) {
    	   recordingFileDirFolder.mkdir();
       }
       Double numberOfFolder = Math.floor(imageNumber / maxNumberOfBatchFolderSize);
       String folderDir = ""+numberOfFolder.intValue();
       
       String batchFileSVGDir = recordingFileDir + File.separatorChar + folderDir;
       File recordingBatchFileDirFolder = new File(batchFileSVGDir);
       if (!recordingBatchFileDirFolder.exists()) {
    	   recordingBatchFileDirFolder.mkdir();
       } 
       
       return batchFileSVGDir + File.separatorChar + imageNumber + ".svg";
	}
	
	/**
	 * This won't work in Debug(=>means JUnit!) Modus as the batch File-Dir is relative to the Server-Path
	 * 
	 * @param conversionJobId
	 * @param folderNumber
	 * @return
	 * @throws Exception
	 */
	private Map<String, String> generateBatchFileDebug(Long conversionJobId,
			Long folderNumber) throws Exception {
		String recordingRootDir = "/Users/swagner/Documents/work/red5_distros/red5_r3200_snapshot/webapps/openmeetings/test/";

		// The Folders must already exist here otherwise no Image could exist
		// here, so no need to
		// check for existance
		String recordingFileDir = recordingRootDir + File.separatorChar
				+ conversionJobId;
		String batchFileSVGDir = recordingFileDir + File.separatorChar
				+ folderNumber;

		String batchFilePNGDir = batchFileSVGDir + File.separatorChar + "PNG";
		File recordingBatchFilePNGDirFolder = new File(batchFilePNGDir);
		if (!recordingBatchFilePNGDirFolder.exists()) {
			recordingBatchFilePNGDirFolder.mkdir();
		}

		Map<String, String> returnMap = new HashMap<String, String>();

		returnMap.put("input", batchFileSVGDir + File.separatorChar + "*.svg");
		returnMap
				.put("output", batchFilePNGDir + File.separatorChar + "%d.png");

		return returnMap;
	}
	
	private Map<String, String> generateBatchFile(Long conversionJobId,
			Long folderNumber) throws Exception {
		
		String recordingRootDir = ScopeApplicationAdapter.webAppPath + File.separatorChar
				+ "upload" + File.separatorChar
				+ StreamService.folderForRecordings;

		// The Folders must already exist here otherwise no Image could exist
		// here, so no need to
		// check for existance
		String recordingFileDir = recordingRootDir + File.separatorChar + conversionJobId;
		String batchFileSVGDir = recordingFileDir + File.separatorChar + folderNumber;

		String batchFilePNGDir = batchFileSVGDir + File.separatorChar + "PNG";
		File recordingBatchFilePNGDirFolder = new File(batchFilePNGDir);
		if (!recordingBatchFilePNGDirFolder.exists()) {
			recordingBatchFilePNGDirFolder.mkdir();
		}

		Map<String, String> returnMap = new HashMap<String, String>();

		returnMap.put("input", batchFileSVGDir + File.separatorChar + "*.svg");
		returnMap.put("output", batchFilePNGDir + File.separatorChar + "%d.png");

		return returnMap;
	}
	
	private String generateSVGFileName(Long conversionJobId, Long imageNumber) throws Exception {
		
		String recordingRootDir = ScopeApplicationAdapter.webAppPath + File.separatorChar
				+ "upload" + File.separatorChar
				+ StreamService.folderForRecordings;
		
		File recordingRootDirFolder = new File(recordingRootDir);
		if (!recordingRootDirFolder.exists()) {
			recordingRootDirFolder.mkdir();
		}
		
		String recordingFileDir = recordingRootDir + File.separatorChar + conversionJobId;
		File recordingFileDirFolder = new File(recordingFileDir);
		if (!recordingFileDirFolder.exists()) {
			recordingFileDirFolder.mkdir();
		}
		Double numberOfFolder = Math.floor(imageNumber / maxNumberOfBatchFolderSize);
		String folderDir = "" + numberOfFolder.intValue();

		String batchFileSVGDir = recordingFileDir + File.separatorChar + folderDir;
		File recordingBatchFileDirFolder = new File(batchFileSVGDir);
		if (!recordingBatchFileDirFolder.exists()) {
			recordingBatchFileDirFolder.mkdir();
		}

		return batchFileSVGDir + File.separatorChar + imageNumber + ".svg";
	}
	
	private String getBatchFileFolder(Long conversionJobId,
			Long folderNumber) throws Exception {
		
		String recordingRootDir = ScopeApplicationAdapter.webAppPath + File.separatorChar
				+ "upload" + File.separatorChar
				+ StreamService.folderForRecordings;

		// The Folders must already exist here otherwise no Image could exist
		// here, so no need to
		// check for existance
		String recordingFileDir = recordingRootDir + File.separatorChar + conversionJobId;
		String batchFileSVGDir = recordingFileDir + File.separatorChar + folderNumber;
		String batchFilePNGDir = batchFileSVGDir + File.separatorChar + "PNG" + File.separatorChar;

		File f = new File(batchFilePNGDir);
		return f.getAbsolutePath() + File.separatorChar;
	}
	
	private String getSWFFolderForResult(Long conversionJobId) throws Exception {
		
		String recordingRootDir = ScopeApplicationAdapter.webAppPath + File.separatorChar
				+ "upload" + File.separatorChar
				+ StreamService.folderForRecordings;

		// The Folders must already exist here otherwise no Image could exist
		// here, so no need to
		// check for existance
		String recordingFileDir = recordingRootDir + File.separatorChar + conversionJobId;
		String recordingFileDirAbsolute = (new File(recordingFileDir)).getAbsolutePath();
		
		return recordingFileDirAbsolute + File.separatorChar;
	}
	
	private String getSWFFileForResult(Long conversionJobId) throws Exception {
		
		String recordingRootDir = ScopeApplicationAdapter.webAppPath + File.separatorChar
				+ "upload" + File.separatorChar
				+ StreamService.folderForRecordings;

		// The Folders must already exist here otherwise no Image could exist
		// here, so no need to
		// check for existance
		String recordingFileDir = recordingRootDir + File.separatorChar + conversionJobId;
		String recordingFileDirAbsolute = (new File(recordingFileDir)).getAbsolutePath();
		
		String swfFile = recordingFileDirAbsolute + File.separatorChar + "whiteboard.swf";
		return swfFile;
	}
	
}
