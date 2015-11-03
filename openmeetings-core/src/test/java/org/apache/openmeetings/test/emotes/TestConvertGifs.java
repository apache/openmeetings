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

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.LinkedList;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.XppDriver;

public class TestConvertGifs extends TestCase {
	
	private static final Logger log = Logger.getLogger(TestConvertGifs.class);

	@Test
	public void testConvertDir(){
		try {
			//FIXME
			String basePath = "/Users/sebastianwagner/Documents/workspace/xmlcrm/webapp/xmlcrm/public/emoticons/";
			
			System.out.println("basePath "+basePath);
			File baseDir = new File(basePath);
			
			System.out.println("basePath "+baseDir.exists());
			
			String batfilePath = "mybat.bat";
			String batString = "";
			
			String[] allfiles = baseDir.list();			
			if(allfiles!=null){
				for(int i=0; i<allfiles.length; i++){
					File file = new File(basePath+File.separatorChar+allfiles[i]);
					String fileName = file.getName();
					System.out.println("file "+file.getName());
					String fileNamePure = fileName.substring(0, fileName.length()-4);
					
					batString += "gif2swf -r 30 -o "+fileNamePure+".swf "+fileName+" \r\n";
				}
			}
			
			PrintWriter pw = new PrintWriter(new FileWriter(basePath+batfilePath));
		    pw.println(batString);
		    pw.flush();
		    pw.close();
		    
			System.out.println("batString "+batString);
			
		} catch (Exception err) {
			err.printStackTrace();
		}
	}
	
	@Test
	public void testXMLDir(){
		try {
			//FIXME
			String basePath = "/Users/sebastianwagner/Documents/workspace/xmlcrm/webapp/xmlcrm/upload/emoticons/";
			
			System.out.println("basePath "+basePath);
			File baseDir = new File(basePath);
			
			System.out.println("basePath "+baseDir.exists());
			
			String batfilePath = "emotes.xml";
			String batString = "";
			
			String[] allfiles = baseDir.list();	
			LinkedList<LinkedList<String>> filesList = new LinkedList<LinkedList<String>>();
			if(allfiles!=null){
				for(int i=0; i<allfiles.length; i++){
					File file = new File(basePath+File.separatorChar+allfiles[i]);
					LinkedList<String> singleFile = new LinkedList<String>();
					String fileName = file.getName();
					System.out.println("file "+file.getName());
					String fileNamePure = fileName.substring(0, fileName.length()-4);
					singleFile.add(fileNamePure+".swf");
					singleFile.add(":)");
					singleFile.add("(^_^)");
					filesList.add(singleFile);
				}
			}
			
			XStream xStream = new XStream(new XppDriver());
			xStream.setMode(XStream.NO_REFERENCES);
			String xmlString = xStream.toXML(filesList);

			log.error(xmlString);
			
			PrintWriter pw = new PrintWriter(new FileWriter(basePath+batfilePath));
		    pw.println(xmlString);
		    pw.flush();
		    pw.close();
		    
			System.out.println("batString "+batString);
			
		} catch (Exception err) {
			err.printStackTrace();
		}
	}
}
