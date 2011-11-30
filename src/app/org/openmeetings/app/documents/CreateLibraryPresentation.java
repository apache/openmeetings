package org.openmeetings.app.documents;

import java.util.HashMap;
import java.io.File;
import java.io.FilenameFilter;
import java.io.FileOutputStream;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.XMLWriter;
import java.util.Arrays;

public class CreateLibraryPresentation {
	
	public static final String libraryFileName = "library.xml";
	
	private static CreateLibraryPresentation instance;

	private CreateLibraryPresentation() {}

	public static synchronized CreateLibraryPresentation getInstance() {
		if (instance == null) {
			instance = new CreateLibraryPresentation();
		}
		return instance;
	}
	
	
	public HashMap<String,String> generateXMLDocument(String targetDirectory, String originalDocument, 
			String pdfDocument, String swfDocument){
		HashMap<String,String> returnMap = new HashMap<String,String>();
		returnMap.put("process", "generateXMLDocument");		
		try {
			
	        Document document = DocumentHelper.createDocument();
	        Element root = document.addElement( "presentation" );

	        File file = new File(targetDirectory+originalDocument);
	        root.addElement( "originalDocument" )
				.addAttribute("lastmod", (new Long(file.lastModified())).toString())
				.addAttribute("size", (new Long(file.length())).toString())	        
	            .addText( originalDocument );
	        
	        if (pdfDocument!=null){
	        	File pdfDocumentFile = new File(targetDirectory+pdfDocument);
		        root.addElement( "pdfDocument" )
					.addAttribute("lastmod", (new Long(pdfDocumentFile.lastModified())).toString())
					.addAttribute("size", (new Long(pdfDocumentFile.length())).toString())	   		        
		            .addText( pdfDocument );
	        }
	        
	        if (swfDocument!=null){
	        	File swfDocumentFile = new File(targetDirectory+originalDocument);
		        root.addElement( "swfDocument" )
					.addAttribute("lastmod", (new Long(swfDocumentFile.lastModified())).toString())
					.addAttribute("size", (new Long(swfDocumentFile.length())).toString())	  		        
	            	.addText( swfDocument );	  
	        }
	        
	        Element thumbs = root.addElement( "thumbs" );
	        
	        File dir = new File(targetDirectory);
	        
			//Secoond get all Files of this Folder
			FilenameFilter ff = new FilenameFilter() {
			     public boolean accept(File b, String name) {
			    	  String absPath = b.getAbsolutePath()+File.separatorChar+name;
			    	  File f = new File (absPath);
			          return f.isFile();
			     }
			};	
			
			String[] allfiles = dir.list(ff);			
			if(allfiles!=null){
				Arrays.sort(allfiles);
				for(int i=0; i<allfiles.length; i++){
					File thumbfile = new File(targetDirectory+allfiles[i]);
					if (allfiles[i].startsWith("_thumb_")){
						thumbs.addElement( "thumb" )
							.addAttribute("lastmod", (new Long(thumbfile.lastModified())).toString())
							.addAttribute("size", (new Long(thumbfile.length())).toString())
			            	.addText( allfiles[i] );
					}
				}
			}
	        
	        // lets write to a file
	        XMLWriter writer = new XMLWriter(
	            new FileOutputStream( targetDirectory+CreateLibraryPresentation.libraryFileName )
	        );
	        writer.write( document );
	        writer.close();
			
	        returnMap.put("exitValue", "0");
	        
			return returnMap;
		} catch (Exception err) {
			err.printStackTrace();
			returnMap.put("error", err.getMessage());
			returnMap.put("exitValue", "-1");
			return returnMap;
		}
	}
	
	
}
