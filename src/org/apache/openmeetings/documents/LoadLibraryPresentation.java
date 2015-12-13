/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License") +  you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.openmeetings.documents;

import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;

import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.data.file.dto.LibraryPresenationThumbs;
import org.apache.openmeetings.data.file.dto.LibraryPresentation;
import org.apache.openmeetings.data.file.dto.LibraryPresentationFile;
import org.apache.openmeetings.data.file.dto.LibraryPresentationThumb;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class LoadLibraryPresentation {
	private static final Logger log = Red5LoggerFactory.getLogger(LoadLibraryPresentation.class, OpenmeetingsVariables.webAppRootKey);
	
	public static LibraryPresentation parseLibraryFileToObject(File file){
		try {
			LibraryPresentation lPresentation = new LibraryPresentation();
			
	        SAXReader reader = new SAXReader();
	        Document document = reader.read( new FileInputStream(file) );
	        
	        Element root = document.getRootElement();
	        
	        for ( @SuppressWarnings("unchecked")
			Iterator<Element> i = root.elementIterator(); i.hasNext(); ) {
	        	
	            Element item = i.next();
	            
	            log.debug(item.getName());
	            
	            String nodeVal = item.getName();
	            
	            //LinkedHashMap<String,Object> subMap = new LinkedHashMap<String,Object>();
	            
	            //subMap.put("name", nodeVal);

				if (nodeVal.equals("originalDocument")){
					
					lPresentation.setOriginalDocument(createListObjectLibraryByFileDocument(item));
					
				} else if (nodeVal.equals("pdfDocument")){
					
					lPresentation.setPdfDocument(createListObjectLibraryByFileDocument(item));
					
				} else if (nodeVal.equals("swfDocument")){
					
					lPresentation.setSwfDocument(createListObjectLibraryByFileDocument(item));
					
				} else if (nodeVal.equals("thumbs")) {
					
					lPresentation.setThumbs(createListObjectLibraryByFileDocumentThumbs(item));
					
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
	
	public static LibraryPresentationFile createListObjectLibraryByFileDocument(Element fileElement){
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
	
	public static LibraryPresenationThumbs createListObjectLibraryByFileDocumentThumbs(Element fileElement){
		try {
			
			LibraryPresenationThumbs thumbMap = new LibraryPresenationThumbs();
			thumbMap.setName(fileElement.getName());
			
			Integer k = 0;
			for ( @SuppressWarnings("unchecked")
			Iterator<Element> i = fileElement.elementIterator(); i.hasNext(); i.next()) {
				k++;
			}
			
			thumbMap.setThumbs(new LibraryPresentationThumb[k]);
			
			
			k = 0;
			for ( @SuppressWarnings("unchecked")
			Iterator<Element> i = fileElement.elementIterator(); i.hasNext(); ) {
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
