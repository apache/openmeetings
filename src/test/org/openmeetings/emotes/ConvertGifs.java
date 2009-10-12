package org.openmeetings.emotes;

import java.util.LinkedList;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import junit.framework.TestCase;
import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.XppDriver;

public class ConvertGifs extends TestCase {
	
	private static final Logger log = Logger.getLogger(ConvertGifs.class);
	
	public ConvertGifs(String testname){
		super(testname);
	}	
	
	public void testConvertDir(){
		try {
			
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
	
	public void testXMLDir(){
		try {
			
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
