package org.openmeetings.app.remote.red5;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.LinkedList;

import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.openmeetings.utils.stringhandlers.ChatString;
import org.red5.server.api.IScope;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.XppDriver;

public class EmoticonsManager {
	
	private static final Logger log = Logger.getLogger(EmoticonsManager.class);
	
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
