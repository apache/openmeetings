package org.openmeetings.app.documents;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.io.FileInputStream;

import org.slf4j.Logger;
import org.openmeetings.app.data.file.dto.LibraryPresenationThumbs;
import org.openmeetings.app.data.file.dto.LibraryPresentation;
import org.openmeetings.app.data.file.dto.LibraryPresentationFile;
import org.openmeetings.app.data.file.dto.LibraryPresentationThumb;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.Red5LoggerFactory;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class LoadLibraryPresentation {
	
	private static final Logger log = Red5LoggerFactory.getLogger(LoadLibraryPresentation.class, ScopeApplicationAdapter.webAppRootKey);
	
	private static LoadLibraryPresentation instance;

	private LoadLibraryPresentation() {}

	public static synchronized LoadLibraryPresentation getInstance() {
		if (instance == null) {
			instance = new LoadLibraryPresentation();
		}
		return instance;
	}	
	
	public LibraryPresentation parseLibraryFileToObject(String filePath){
		try {
			LibraryPresentation lPresentation = new LibraryPresentation();
			
	        SAXReader reader = new SAXReader();
	        Document document = reader.read( new FileInputStream(filePath) );
	        
	        Element root = document.getRootElement();
	        
	        for ( Iterator<Element> i = root.elementIterator(); i.hasNext(); ) {
	        	
	            Element item = i.next();
	            
	            log.debug(item.getName());
	            
	            String nodeVal = item.getName();
	            
	            //LinkedHashMap<String,Object> subMap = new LinkedHashMap<String,Object>();
	            
	            //subMap.put("name", nodeVal);

				if (nodeVal.equals("originalDocument")){
					
					lPresentation.setOriginalDocument(this.createListObjectLibraryByFileDocument(item));
					
				} else if (nodeVal.equals("pdfDocument")){
					
					lPresentation.setPdfDocument(this.createListObjectLibraryByFileDocument(item));
					
				} else if (nodeVal.equals("swfDocument")){
					
					lPresentation.setSwfDocument(this.createListObjectLibraryByFileDocument(item));
					
				} else if (nodeVal.equals("thumbs")) {
					
					lPresentation.setThumbs(this.createListObjectLibraryByFileDocumentThumbs(item));
					
				} else {
					throw new Exception("Unkown Library type: "+nodeVal);
				}
	            
	        }
	        
	        log.debug("Returning presentation file object");
			
			return lPresentation;
		} catch (Exception err) {
			log.error("parseLibraryFileToObject",err);
			return null;
		}
	}
	
	public LibraryPresentationFile createListObjectLibraryByFileDocument(Element fileElement){
		try {
			
			log.info("createListObjectLibraryByFileDocument"+fileElement);
			
			LibraryPresentationFile libraryPresentationFile = new LibraryPresentationFile();
			libraryPresentationFile.setName(fileElement.getName());
			libraryPresentationFile.setFilename(fileElement.getText());
			libraryPresentationFile.setLastmod(fileElement.attribute("lastmod").getText());
			libraryPresentationFile.setSize(Long.valueOf(fileElement.attribute("size").getText()).longValue());
			
			return libraryPresentationFile;
			
		} catch (Exception err) {
			log.error("createListObjectLibraryByFileDocument",err);
		}
		return null;
	}		
	
	public LibraryPresenationThumbs createListObjectLibraryByFileDocumentThumbs(Element fileElement){
		try {
			
			LibraryPresenationThumbs thumbMap = new LibraryPresenationThumbs();
			thumbMap.setName(fileElement.getName());
			
			Integer k = 0;
			for ( Iterator<Element> i = fileElement.elementIterator(); i.hasNext(); ) {
				Element thumbElement = i.next();
				k++;
			}
			
			thumbMap.setThumbs(new LibraryPresentationThumb[k]);
			
			
			k = 0;
			for ( Iterator<Element> i = fileElement.elementIterator(); i.hasNext(); ) {
				Element thumbElement = i.next();
				//log.info("createListObjectLibraryByFileDocumentThumbs"+thumbElement);
				LibraryPresentationThumb singleThumb = new LibraryPresentationThumb();
				singleThumb.setName(thumbElement.getName());
				singleThumb.setFilename(thumbElement.getText());
				singleThumb.setLastmod(thumbElement.attribute("lastmod").getText());
				singleThumb.setSize(Long.valueOf(thumbElement.attribute("size").getText()).longValue());
				thumbMap.getThumbs()[k] = singleThumb;
				k++;
			}
			
			
			return thumbMap;
			
		} catch (Exception err) {
			log.error("createListObjectLibraryByFileDocumentThumbs",err);
		}
		return null;
	}	
	
}
