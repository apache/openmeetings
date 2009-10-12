package org.openmeetings.app.documents;

import java.io.FileWriter;

import org.dom4j.Document;
import org.dom4j.io.XMLWriter;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

public class InstallationDocumentHandler {
	
	public static final String installFileName = "install.xml";
	
	private static InstallationDocumentHandler instance;

	private InstallationDocumentHandler() {}

	public static synchronized InstallationDocumentHandler getInstance() {
		if (instance == null) {
			instance = new InstallationDocumentHandler();
		}
		return instance;
	}
	
	public void createDocument(String filePath, Integer stepNo) throws Exception {
		
		Document document = DocumentHelper.createDocument();
		
		Element root = document.addElement( "install" );
		Element step = root.addElement("step");
		
		step.addElement("stepnumber").addText(stepNo.toString());
		step.addElement("stepname").addText("Step "+stepNo);
		
		XMLWriter writer = new XMLWriter( new FileWriter( filePath ) );
        writer.write( document );
        writer.close();
		
	}
	
	public int getCurrentStepNumber(String filePath) throws Exception{
		
	    SAXReader reader = new SAXReader();
        Document document = reader.read(filePath+InstallationDocumentHandler.installFileName);
        
        Node node = document.selectSingleNode( "//install/step/stepnumber" );
        
        return Integer.valueOf(node.getText()).intValue();
        
	}
	
	
}
