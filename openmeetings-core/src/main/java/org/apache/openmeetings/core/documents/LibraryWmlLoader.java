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
package org.apache.openmeetings.core.documents;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.apache.openmeetings.util.OmFileHelper;
import org.apache.openmeetings.util.OpenmeetingsVariables;
import org.dom4j.Element;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.XppDriver;

public class LibraryWmlLoader {
	
	private static final Logger log = Red5LoggerFactory.getLogger(LibraryWmlLoader.class, OpenmeetingsVariables.webAppRootKey);
	
	private static final String fileExt = ".wml";
	
	@SuppressWarnings({ "rawtypes" })
	public ArrayList loadWmlFile(String fileName){
		BufferedReader reader = null;
		try {
			String name = fileName;
			if (!name.endsWith(fileExt)) {
				name += fileExt;
			}
			File filepathComplete = new File(OmFileHelper.getUploadWmlDir(), name);
			log.debug("filepathComplete: "+filepathComplete);
			
			XStream xStream = new XStream(new XppDriver());
			xStream.setMode(XStream.NO_REFERENCES);
			
			reader = new BufferedReader(new FileReader(filepathComplete));
		    ArrayList lMapList = (ArrayList) xStream.fromXML(reader);
			
			return lMapList;
		} catch (Exception err){
			log.error("loadWmlFile",err);
		} finally {
			if (reader != null) {
			    try {
					reader.close();
				} catch (IOException e) {}
			}
		}
		
		return null;
		
	}
	
	/**
	 * @deprecated
	 * @param paintElement
	 * @param subMap
	 */
	@SuppressWarnings({ "unused", "rawtypes" })
	private void createListObjectPaintByNode(Element paintElement, LinkedHashMap<Integer,Object> subMap){
		try {
			
			LinkedHashMap<Integer,LinkedHashMap> pointMap = new LinkedHashMap<Integer,LinkedHashMap>();
			Element pointElements = paintElement.element("points");
			Integer k = 0;
			
			for ( Iterator i = pointElements.elementIterator( "point" ); i.hasNext(); ) {
				Element pointElement = (Element) i.next();
				LinkedHashMap<Integer,Object> singlePoint = new LinkedHashMap<Integer,Object>();
				singlePoint.put(0, pointElement.getName());
				singlePoint.put(1, Integer.valueOf(pointElement.attribute("val1").getText()));
				singlePoint.put(2, Integer.valueOf(pointElement.attribute("val2").getText()));
				singlePoint.put(3, Integer.valueOf(pointElement.attribute("val3").getText()));
				singlePoint.put(4, Integer.valueOf(pointElement.attribute("val4").getText()));
				pointMap.put(k, singlePoint);
				log.debug("createListObjectPaintByNode"+singlePoint);
				k++;
			}
			subMap.put(1, pointMap);

			subMap.put(2, paintElement.element("fillstyle").getText());
			subMap.put(3, Integer.valueOf(paintElement.element("linewidth").getText()));
			subMap.put(4, Integer.valueOf(paintElement.element("strokestyle").getText()));
			subMap.put(5, Integer.valueOf(paintElement.element("counter").getText()));
			subMap.put(6, Float.valueOf(paintElement.element("x").getText()));
			subMap.put(7, Float.valueOf(paintElement.element("y").getText()));
			subMap.put(8, Float.valueOf(paintElement.element("width").getText()));
			subMap.put(9, Float.valueOf(paintElement.element("height").getText()));
			subMap.put(10, paintElement.element("layername").getText());		
			
		} catch (Exception err) {
			log.error("createListObjectPaintByNode",err);
		}
	}
	
	/**
	 * @deprecated
	 * @param paintElement
	 * @param subMap
	 */
	public void createListObjectLetterByNode(Element paintElement, LinkedHashMap<Integer,Object> subMap){
		try {

			subMap.put(1, paintElement.element("textforfield").getText());
			subMap.put(2, Integer.valueOf(paintElement.element("fgcolor").getText()));
			subMap.put(3, Integer.valueOf(paintElement.element("fontsize").getText()));
			subMap.put(4, paintElement.element("fontstyle").getText());
			subMap.put(5, Integer.valueOf(paintElement.element("counter").getText()));
			subMap.put(6, Float.valueOf(paintElement.element("x").getText()));
			subMap.put(7, Float.valueOf(paintElement.element("y").getText()));			
			subMap.put(8, Float.valueOf(paintElement.element("width").getText()));		
			subMap.put(9, Float.valueOf(paintElement.element("height").getText()));		
			subMap.put(10, paintElement.element("layername").getText());
			
		} catch (Exception err) {
			log.error("createListObjectLetterByNode",err);
		}
	}	
	
	/**
	 * @deprecated
	 * @param paintElement
	 * @param subMap
	 */
	public void createListObjectImageByNode(Element paintElement, LinkedHashMap<Integer,Object> subMap){
		try {

			subMap.put(1, paintElement.element("urlname").getText());
			subMap.put(2, paintElement.element("baseurl").getText());
			subMap.put(3, paintElement.element("filename").getText());
			subMap.put(4, paintElement.element("modulename").getText());
			subMap.put(5, paintElement.element("parentpath").getText());
			subMap.put(6, paintElement.element("room").getText());
			subMap.put(7, paintElement.element("domain").getText());
			subMap.put(8, Integer.valueOf(paintElement.element("counter").getText()));
			subMap.put(9, Float.valueOf(paintElement.element("x").getText()));
			subMap.put(10, Float.valueOf(paintElement.element("y").getText()));
			subMap.put(11, Float.valueOf(paintElement.element("width").getText()));
			subMap.put(12, Float.valueOf(paintElement.element("height").getText()));
			subMap.put(13, paintElement.element("layername").getText());		
			
		} catch (Exception err) {
			log.error("createListObjectImageByNode",err);
		}
	}	
	
	/**
	 * @deprecated
	 * @param paintElement
	 * @param subMap
	 */
	public void createListObjectObjecByNode(Element paintElement, LinkedHashMap<Integer,Object> subMap){
		try {	
			
			subMap.put(1, paintElement.element("fillstyle").getText());
			subMap.put(2, paintElement.element("linewidth").getText());
			subMap.put(3, Integer.valueOf(paintElement.element("strokestyle").getText()));
			subMap.put(4, Float.valueOf(paintElement.element("startx").getText()));
			subMap.put(5, Float.valueOf(paintElement.element("starty").getText()));
			subMap.put(6, Float.valueOf(paintElement.element("endx").getText()));
			subMap.put(7, Float.valueOf(paintElement.element("endy").getText()));
			subMap.put(8, Integer.valueOf(paintElement.element("counter").getText()));
			subMap.put(9, Float.valueOf(paintElement.element("x").getText()));
			subMap.put(10, Float.valueOf(paintElement.element("y").getText()));
			subMap.put(11, Float.valueOf(paintElement.element("width").getText()));
			subMap.put(12, Float.valueOf(paintElement.element("height").getText()));
			subMap.put(13, paintElement.element("layername").getText());
			
		} catch (Exception err) {
			log.error("createListObjectObjecByNode",err);
		}
	}		
	
	/**
	 * @deprecated
	 * @param paintElement
	 * @param subMap
	 */
	public void createListObjectRectAndEllipseByNode(Element paintElement, LinkedHashMap<Integer,Object> subMap){
		try {	
			
			subMap.put(1, Integer.valueOf(paintElement.element("stroke").getText()));
			subMap.put(2, paintElement.element("line").getText());
			subMap.put(3, Integer.valueOf(paintElement.element("counter").getText()));
			subMap.put(4, Float.valueOf(paintElement.element("x").getText()));		
			subMap.put(5, Float.valueOf(paintElement.element("y").getText()));
			subMap.put(6, Float.valueOf(paintElement.element("width").getText()));
			subMap.put(7, Float.valueOf(paintElement.element("height").getText()));
			subMap.put(8, paintElement.element("layername").getText());

		} catch (Exception err) {
			log.error("createListObjectObjecByNode",err);
		}
	}
	
	/**
	 * @deprecated
	 * @param paintElement
	 * @param subMap
	 */
	public void createListObjectSWFByNode(Element paintElement, LinkedHashMap<Integer,Object> subMap){
		try {

			subMap.put(1, paintElement.element("urlname").getText());
			subMap.put(2, paintElement.element("baseurl").getText());
			subMap.put(3, paintElement.element("filename").getText());
			subMap.put(4, paintElement.element("modulename").getText());
			subMap.put(5, paintElement.element("parentpath").getText());
			subMap.put(6, paintElement.element("room").getText());
			subMap.put(7, paintElement.element("domain").getText());
			subMap.put(8, Integer.valueOf(paintElement.element("slideNumber").getText()));
			subMap.put(9, Float.valueOf(paintElement.element("innerx").getText()));
			subMap.put(10, Float.valueOf(paintElement.element("innery").getText()));
			subMap.put(11, Float.valueOf(paintElement.element("innerwidth").getText()));
			subMap.put(12, Float.valueOf(paintElement.element("innerheight").getText()));
			subMap.put(13, Integer.valueOf(paintElement.element("zoomlevel").getText()));
			subMap.put(14, Float.valueOf(paintElement.element("initwidth").getText()));
			subMap.put(15, Float.valueOf(paintElement.element("initheight").getText()));
			subMap.put(16, Integer.valueOf(paintElement.element("currentzoom").getText()));
			subMap.put(17, Integer.valueOf(paintElement.element("counter").getText()));
			subMap.put(18, Float.valueOf(paintElement.element("x").getText()));
			subMap.put(19, Float.valueOf(paintElement.element("y").getText()));
			subMap.put(20, Float.valueOf(paintElement.element("width").getText()));
			subMap.put(21, Float.valueOf(paintElement.element("height").getText()));
			subMap.put(22, paintElement.element("layername").getText());		
			
		} catch (Exception err) {
			log.error("createListObjectImageByNode",err);
		}
	}		
	
}
