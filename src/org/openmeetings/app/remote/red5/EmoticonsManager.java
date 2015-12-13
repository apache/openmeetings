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
package org.openmeetings.app.remote.red5;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.LinkedList;

import org.slf4j.Logger;
import org.red5.logging.Red5LoggerFactory;
import org.red5.server.api.scope.IScope;
import org.openmeetings.app.OpenmeetingsVariables;
import org.openmeetings.utils.stringhandlers.ChatString;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.XppDriver;

public class EmoticonsManager {
	
	private static final Logger log = Red5LoggerFactory.getLogger(EmoticonsManager.class, OpenmeetingsVariables.webAppRootKey);
	
	private static LinkedList<LinkedList<String>> emotfilesList = new LinkedList<LinkedList<String>>();

	private static EmoticonsManager instance = null;

	private EmoticonsManager() {
	}

	public static synchronized EmoticonsManager getInstance() {
		if (instance == null) {
			instance = new EmoticonsManager();
		}
		return instance;
	}

	

	
	@SuppressWarnings("unchecked")
	public void loadEmot(IScope scope){
		try {
			
			scope.getResource("public/").getFile().getParentFile().getAbsolutePath();
			String filePath = scope.getResource("public/").getFile().getAbsolutePath();
			
			String fileName = filePath + File.separatorChar + "emoticons" + File.separatorChar + "emotes.xml";
			XStream xStream = new XStream(new XppDriver());
			xStream.setMode(XStream.NO_REFERENCES);
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
		    String xmlString = "";
		    while (reader.ready()) {
		    	xmlString += reader.readLine();
		    }
		    reader.close();
		    emotfilesList = (LinkedList<LinkedList<String>>) xStream.fromXML(xmlString);
		    ChatString.getInstance().replaceAllRegExp();
		    
		    log.debug("##### loadEmot completed");
		    
		} catch (Exception err) {
			log.error("[loadEmot]",err);
		}
	}
	
	public static synchronized LinkedList<LinkedList<String>> getEmotfilesList() {
		return emotfilesList;
	}
	public static synchronized void setEmotfilesList(LinkedList<LinkedList<String>> emotfilesListNew) {
		emotfilesList = emotfilesListNew;
	}
	
}
