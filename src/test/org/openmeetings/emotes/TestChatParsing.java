package org.openmeetings.emotes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.LinkedList;

import org.openmeetings.utils.stringhandlers.ChatString;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.XppDriver;

import junit.framework.TestCase;

public class TestChatParsing extends TestCase {
	
	public static LinkedList<LinkedList<String>> emotfilesList = new LinkedList<LinkedList<String>>();
	
	public void testChatParser(){
		try {
			
			String filePath = "/Users/sebastianwagner/Documents/workspace/xmlcrm/webapp/xmlcrm/public/";
			
			this.loadEmot(filePath);
			
		} catch (Exception err) {
			err.printStackTrace();
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
			System.out.println("[loadEmot]"+err);
			err.printStackTrace();
		}
	}	

}
