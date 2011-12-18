package org.openmeetings.app.documents;

import java.util.Iterator;
import java.util.LinkedList;

import org.slf4j.Logger;
import org.red5.logging.Red5LoggerFactory;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import org.openmeetings.app.data.basic.files.*;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;

public class LoadLibraryPresentationToObject {
	
	private static final Logger log = Red5LoggerFactory.getLogger(LoadLibraryPresentationToObject.class, ScopeApplicationAdapter.webAppRootKey);
	
	private static LoadLibraryPresentationToObject instance;

	private LoadLibraryPresentationToObject() {}

	public static synchronized LoadLibraryPresentationToObject getInstance() {
		if (instance == null) {
			instance = new LoadLibraryPresentationToObject();
		}
		return instance;
	}	
	
	public PresentationObject parseLibraryFileToObject(String filePath){
		try {
			PresentationObject lMap = new PresentationObject();
			
	        SAXReader reader = new SAXReader();
	        Document document = reader.read(filePath);
	        
	        Element root = document.getRootElement();
	        Integer k = 0;
	        
	        for (@SuppressWarnings("unchecked")
			Iterator<Element> i = root.elementIterator(); i.hasNext(); ) {
	            Element item = i.next();
	            log.error(item.getName());
	            
	            String nodeVal = item.getName();
	            
				if (nodeVal.equals("originalDocument")){
					lMap.setOriginalDocument(this.createListObjectLibraryByFileDocument(item));
				} else if (nodeVal.equals("pdfDocument")){
					lMap.setPdfDocument(this.createListObjectLibraryByFileDocument(item));
				} else if (nodeVal.equals("swfDocument")) {
					lMap.setSwfDocument(this.createListObjectLibraryByFileDocument(item));
				} else if (nodeVal.equals("thumbs")) {
					lMap.setThumbs(this.createListObjectLibraryByFileDocumentThumbs(item));
				}
	            
	            k++;

	        }
			
			return lMap;
		} catch (Exception err) {
			log.error("parseLibraryFileToObject",err);
			return null;
		}
	}
	
	public FilesObject createListObjectLibraryByFileDocument(Element fileElement){
		try {
			
			log.info("createListObjectLibraryByFileDocument"+fileElement);
			FilesObject fileObject = new FilesObject();
			fileObject.setFileName(fileElement.getText());
			fileObject.setLastModified(fileElement.attribute("lastmod").getText());
			fileObject.setFileBytes(fileElement.attribute("size").getText());
			return fileObject;
		} catch (Exception err) {
			log.error("createListObjectLibraryByFileDocument",err);
		}
		return null;
	}		
	
	public LinkedList<FilesObject> createListObjectLibraryByFileDocumentThumbs(Element fileElement){
		try {

			LinkedList<FilesObject> thumbMap = new LinkedList<FilesObject>();
			
			for (@SuppressWarnings("unchecked")
			Iterator<Element> i = fileElement.elementIterator(); i.hasNext(); ) {
				Element thumbElement = i.next();
				log.info("createListObjectLibraryByFileDocumentThumbs"+thumbElement);
				FilesObject singleThumb = new FilesObject();
				singleThumb.setFileName(thumbElement.getName());
				singleThumb.setFileNamePure(thumbElement.getText());
				singleThumb.setLastModified(thumbElement.attribute("lastmod").getText());
				singleThumb.setFileBytes(thumbElement.attribute("size").getText());
				thumbMap.add(singleThumb);
			}
			
			return thumbMap;
			
		} catch (Exception err) {
			log.error("createListObjectLibraryByFileDocumentThumbs",err);
		}
		return null;
	}	
	
}
