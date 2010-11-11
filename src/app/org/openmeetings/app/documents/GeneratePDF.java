package org.openmeetings.app.documents;

import java.io.File;
import java.util.HashMap;

import org.apache.commons.transaction.util.FileHelper;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class GeneratePDF {
	
	private static final Logger log = Red5LoggerFactory.getLogger(GeneratePDF.class, ScopeApplicationAdapter.webAppRootKey);
	
	private static GeneratePDF instance;

	private GeneratePDF() {}

	public static synchronized GeneratePDF getInstance() {
		if (instance == null) {
			instance = new GeneratePDF();
		}
		return instance;
	}
	
	public HashMap<String, HashMap<String, Object>> convertPDF(
			String current_dir, String fileName, String fileExt,
			String roomName, boolean fullProcessing, String completeName)
			throws Exception {

		HashMap<String, HashMap<String, Object>> returnError = new HashMap<String, HashMap<String, Object>>();
		
		HashMap<String,Object> processPDF = new HashMap<String,Object>();
		processPDF.put("process","processPDF");
		
		String working_imgdir = current_dir + "upload" + File.separatorChar + roomName
				+ File.separatorChar;
		String working_pptdir = current_dir + "uploadtemp" + File.separatorChar
				+ roomName + File.separatorChar;

		String fileFullPath = working_pptdir + fileName + fileExt;
		String destinationFolder = working_imgdir + fileName;

		File f = new File(destinationFolder + File.separatorChar);
		if (f.exists()) {
			int recursiveNumber = 0;
			String tempd = destinationFolder + "_" + recursiveNumber;
			while (f.exists()) {
				recursiveNumber++;
				tempd = destinationFolder + "_" + recursiveNumber;
				f = new File(tempd);

			}
			destinationFolder = tempd;
		}

		boolean b = f.mkdir();
		if (!b) {
			processPDF.put("error", "convertPDF + ERROR: Folder could not create " + f.getAbsolutePath());
			processPDF.put("exitValue",-1);
		} else {
			processPDF.put("exitValue",0);
		}
		returnError.put("processPDF", processPDF);
		
		String outputfolder = destinationFolder + File.separatorChar;
		destinationFolder = destinationFolder + File.separatorChar;

		log.debug("fullProcessing: "+fullProcessing);
		if (fullProcessing) {
			HashMap<String,Object> processOpenOffice = this.doJodConvert(current_dir, fileFullPath, destinationFolder,fileName);
			returnError.put("processOpenOffice", processOpenOffice);
			HashMap<String,Object> processThumb = GenerateThumbs.getInstance().generateBatchThumb(current_dir, destinationFolder + fileName + ".pdf", destinationFolder, 80, "thumb");
			returnError.put("processThumb", processThumb);		
			HashMap<String,Object> processSWF = GenerateSWF.getInstance().generateSwf(current_dir, destinationFolder, destinationFolder, fileName);
			returnError.put("processSWF", processSWF);	
		} else {
			
			log.debug("-- generateBatchThumb --");
			
			HashMap<String,Object> processThumb = GenerateThumbs.getInstance().generateBatchThumb(current_dir, fileFullPath, destinationFolder, 80, "thumb");
			returnError.put("processThumb", processThumb);
			
			
			HashMap<String,Object> processSWF = GenerateSWF.getInstance().generateSwf(current_dir, (new File(fileFullPath)).getParentFile().getAbsolutePath()+File.separatorChar , destinationFolder, fileName);
			returnError.put("processSWF", processSWF);				
		}
				
		//now it should be completed so copy that file to the expected location
		File fileToBeMoved = new File(completeName + fileExt);
		File fileWhereToMove = new File(outputfolder+ fileName + fileExt);
		fileWhereToMove.createNewFile();
		FileHelper.moveRec(fileToBeMoved, fileWhereToMove);
		
		if (fullProcessing) {
			HashMap<String,Object> processXML = CreateLibraryPresentation.getInstance().generateXMLDocument(outputfolder, fileName + fileExt, fileName + ".pdf", fileName + ".swf");
			returnError.put("processXML", processXML);	
		} else {
			HashMap<String,Object> processXML = CreateLibraryPresentation.getInstance().generateXMLDocument(outputfolder, fileName + fileExt, null, fileName + ".swf");
			returnError.put("processXML", processXML);
		}
		
		return returnError;
	}	
	
	/**
	 * Generates PDF and thumbs (and swf).
	 */
	public HashMap<String, Object> doJodConvert(String current_dir,
			String fileFullPath, String destinationFolder, String outputfile) {
		// Path to all JARs of JOD
		String jodClassPathFolder = current_dir + "jod" + File.separatorChar;

		// Create the Content of the Converter Script (.bat or .sh File)
		String[] argv = new String[] {
				"java",
				"-cp",
				"\"" + jodClassPathFolder + "commons-cli-1.2.jar\" -cp \"" 
						+ jodClassPathFolder + "commons-io-1.4.jar\" -cp \""
						+ jodClassPathFolder + "jodconverter-2.2.2.jar\" -cp \""
						+ jodClassPathFolder + "jodconverter-cli-2.2.2.jar\" -cp \"" 
						+ jodClassPathFolder + "juh-3.0.1.jar\" -cp \""
						+ jodClassPathFolder + "jurt-3.0.1.jar\" -cp \""
						+ jodClassPathFolder + "ridl-3.0.1.jar\" -cp \""
						+ jodClassPathFolder + "slf4j-api-1.5.6.jar\" -cp \""
						+ jodClassPathFolder + "slf4j-jdk14-1.5.6.jar\" -cp \"" 
						+ jodClassPathFolder + "unoil-3.0.1.jar\" -cp \"" 
						+ jodClassPathFolder + "xstream-1.3.1.jar\"",
				"-jar", jodClassPathFolder + "jodconverter-cli-2.2.2.jar",
                fileFullPath, destinationFolder + outputfile + ".pdf" };

		return GenerateSWF.executeScript("doJodConvert", argv);

	}	
	
}
