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
package org.apache.openmeetings.data.whiteboard;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.LinkedList;

import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.utils.OmFileHelper;
import org.apache.openmeetings.utils.stringhandlers.ChatString;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.XppDriver;

/**
 * Load the list of available emoticons from the XML file and store them in
 * memory to load faster, {@link #loadEmot()} is only called once: during server
 * startup
 * 
 * @author sebawagner
 * 
 */
public class EmoticonsManager {

	private static final Logger log = Red5LoggerFactory.getLogger(EmoticonsManager.class, OpenmeetingsVariables.webAppRootKey);
	
	private LinkedList<LinkedList<String>> emotfilesList = new LinkedList<LinkedList<String>>();

	@SuppressWarnings("unchecked")
	public void loadEmot(){
		try {
			XStream xStream = new XStream(new XppDriver());
			xStream.setMode(XStream.NO_REFERENCES);
			BufferedReader reader = new BufferedReader(new FileReader(new File(OmFileHelper.getPublicEmotionsDir(), "emotes.xml")));
		    String xmlString = "";
		    while (reader.ready()) {
		    	xmlString += reader.readLine();
		    }
		    reader.close();
		    emotfilesList = ChatString.replaceAllRegExp((LinkedList<LinkedList<String>>) xStream.fromXML(xmlString));
		    
		    log.debug("##### loadEmot completed");
		    
		} catch (Exception err) {
			log.error("[loadEmot]",err);
		}
	}
	
	public synchronized LinkedList<LinkedList<String>> getEmotfilesList() {
		return emotfilesList;
	}
	
	public synchronized void setEmotfilesList(LinkedList<LinkedList<String>> emotfilesListNew) {
		emotfilesList = emotfilesListNew;
	}
}
