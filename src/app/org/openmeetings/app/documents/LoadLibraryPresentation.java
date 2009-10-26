package org.openmeetings.app.documents;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.io.FileInputStream;

import org.slf4j.Logger;
import org.red5.logging.Red5LoggerFactory;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class LoadLibraryPresentation {
	
	private static final Logger log = Red5LoggerFactory.getLogger(LoadLibraryPresentation.class, "openmeetings");
	
	private static LoadLibraryPresentation instance;

	private LoadLibraryPresentation() {}

	public static synchronized LoadLibraryPresentation getInstance() {
		if (instance == null) {
			instance = new LoadLibraryPresentation();
		}
		return instance;
	}	
	
	public LinkedHashMap<String,LinkedHashMap> parseLibraryFileToObject(String filePath){
		try {
			LinkedHashMap<String,LinkedHashMap> lMap = new LinkedHashMap<String,LinkedHashMap>();
			
	        SAXReader reader = new SAXReader();
	        Document document = reader.read( new FileInputStream(filePath) );
	        
	        Element root = document.getRootElement();
	        Integer k = 0;
	        
	        for ( Iterator i = root.elementIterator(); i.hasNext(); ) {
	            Element item = (Element) i.next();
	            log.debug(item.getName());
	            
	            String nodeVal = item.getName();
	            
	            LinkedHashMap<String,Object> subMap = new LinkedHashMap<String,Object>();
	            subMap.put("name", nodeVal);

				if (nodeVal.equals("originalDocument") || nodeVal.equals("pdfDocument")
						|| nodeVal.equals("swfDocument")){
					this.createListObjectLibraryByFileDocument(item, subMap);
				} else if (nodeVal.equals("thumbs")) {
					this.createListObjectLibraryByFileDocumentThumbs(item, subMap);
				}
	            
				log.debug("parseLibraryFileToObject"+subMap);
	            lMap.put(nodeVal, subMap);
	            
	            k++;

	        }
			
			return lMap;
		} catch (Exception err) {
			log.error("parseLibraryFileToObject",err);
			return null;
		}
	}
	
	public void createListObjectLibraryByFileDocument(Element fileElement, LinkedHashMap<String,Object> subMap){
		try {
			
			log.info("createListObjectLibraryByFileDocument"+fileElement);

			subMap.put("filename", fileElement.getText());
			subMap.put("lastmod", fileElement.attribute("lastmod").getText());
			subMap.put("size", Long.valueOf(fileElement.attribute("size").getText()).longValue());
			
		} catch (Exception err) {
			log.error("createListObjectLibraryByFileDocument",err);
		}
	}		
	
	public void createListObjectLibraryByFileDocumentThumbs(Element fileElement, LinkedHashMap<String,Object> subMap){
		try {

			LinkedHashMap<Integer,LinkedHashMap> thumbMap = new LinkedHashMap<Integer,LinkedHashMap>();
			Integer k = 0;
			
			for ( Iterator i = fileElement.elementIterator(); i.hasNext(); ) {
				Element thumbElement = (Element) i.next();
				log.info("createListObjectLibraryByFileDocumentThumbs"+thumbElement);
				LinkedHashMap<String,Object> singleThumb = new LinkedHashMap<String,Object>();
				singleThumb.put("name", thumbElement.getName());
				singleThumb.put("filename", thumbElement.getText());
				singleThumb.put("lastmod", thumbElement.attribute("lastmod").getText());
				singleThumb.put("size", Long.valueOf(thumbElement.attribute("size").getText()).longValue());
				thumbMap.put(k, singleThumb);
				k++;
			}
			subMap.put("thumbs", thumbMap);
			
		} catch (Exception err) {
			log.error("createListObjectLibraryByFileDocumentThumbs",err);
		}
	}	
	
}
