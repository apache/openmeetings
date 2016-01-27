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
package org.apache.openmeetings.core.data.whiteboard;

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.openmeetings.util.OmFileHelper;
import org.apache.openmeetings.util.stringhandlers.ChatString;
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
	private static final Logger log = Red5LoggerFactory.getLogger(EmoticonsManager.class, webAppRootKey);
	private List<List<String>> emotfilesList = new CopyOnWriteArrayList<>();

	@SuppressWarnings("unchecked")
	public void loadEmot(){
		try {
		    StringBuilder xmlString = new StringBuilder();
			XStream xStream = new XStream(new XppDriver());
			xStream.setMode(XStream.NO_REFERENCES);
			try (InputStream is = new FileInputStream(new File(OmFileHelper.getPublicEmotionsDir(), "emotes.xml"));
					BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8)))
			{
			    while (reader.ready()) {
			    	xmlString.append(reader.readLine());
			    }
			}
		    emotfilesList = new CopyOnWriteArrayList<>(ChatString.replaceAllRegExp((List<List<String>>) xStream.fromXML(xmlString.toString())));
		    
		    log.debug("##### loadEmot completed");
		} catch (Exception err) {
			log.error("[loadEmot]",err);
		}
	}
	
	public List<List<String>> getEmotfilesList() {
		return emotfilesList;
	}
}
