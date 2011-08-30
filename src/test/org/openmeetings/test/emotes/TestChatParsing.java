package org.openmeetings.test.emotes;

import java.util.LinkedList;

import junit.framework.TestCase;

import org.junit.Test;

public class TestChatParsing extends TestCase {
	
	public static LinkedList<LinkedList<String>> emotfilesList = new LinkedList<LinkedList<String>>();
	
	@Test
	public void testChatParser(){
		try {
			//FIXME
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
