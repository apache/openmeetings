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
package org.apache.openmeetings.test.emotes;

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

import java.util.LinkedList;

import org.junit.Test;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class TestChatParsing {
	private static final Logger log = Red5LoggerFactory.getLogger(TestChatParsing.class, webAppRootKey);
	public static LinkedList<LinkedList<String>> emotfilesList = new LinkedList<LinkedList<String>>();
	
	@Test
	public void testChatParser(){
		try {
			//FIXME
			String filePath = "/Users/sebastianwagner/Documents/workspace/xmlcrm/webapp/xmlcrm/public/";
			
			this.loadEmot(filePath);
			
		} catch (Exception err) {
			log.error("Error", err);
		}
	}
	
	public void loadEmot(String filePath){
		try {
//			String fileName = filePath + File.separatorChar + "emoticons" + File.separatorChar + "emotes.xml";
//			XStream xStream = new XStream(new XppDriver());
//			xStream.setMode(XStream.NO_REFERENCES);
//			BufferedReader reader = new BufferedReader(new FileReader(fileName));
//		    String xmlString = "";
//		    while (reader.ready()) {
//		    	xmlString += reader.readLine();
//		    }
//		    Application.setEmotfilesList((LinkedList<LinkedList<String>>) xStream.fromXML(xmlString));
//		    ChatString.getInstance().replaceAllRegExp();
//		    String messageText = "Hi :) how are you? whats going on B) today (T_T) ? with you ?";
//		    //messageText = ":) Hi :) how :( are :) you :( today >:O going on? :)";
//		    //messageText = "Hi :) how :( are :) you :( today :) ";
//		    //messageText = "Hi :) how";
//		    ChatString.getInstance().parseChatString(messageText);
		    
		    System.out.println("loadEmot completed");
		} catch (Exception err) {
			log.error("Error", err);
		}
	}	

}
