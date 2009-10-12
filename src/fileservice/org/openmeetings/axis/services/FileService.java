package org.openmeetings.axis.services;

import java.util.LinkedList;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;

import org.openmeetings.app.data.basic.Sessionmanagement;
import org.openmeetings.app.data.basic.files.*;
import org.openmeetings.app.remote.ConferenceLibrary;
import javax.xml.stream.XMLStreamException;

public class FileService {
	
	private static final Logger log = Logger.getLogger(FileService.class);
	
	/**
	 * this Method does not work yet,
	 * as the Result has to be rewritten in Objects instead
	 * of a LinekdHashMap
	 * @param SID
	 * @param moduleName
	 * @param parentFolder
	 * @param room
	 * @param domain
	 * @return
	 */
	public LiberaryObject getListOfFiles(String SID, String moduleName,
			String parentFolder, Long room_id ) {
		try {
			log.debug("#############current_dir : "+"");			
			
			log.debug("#############SID : "+SID);
	        log.debug("#############moduleName : "+moduleName);
	        log.debug("#############parentFolder : "+parentFolder);
	        log.debug("#############room_id : "+room_id);
	        
	        return ConferenceLibrary.getInstance().getListOfFilesObjectByAbsolutePath(SID, moduleName, parentFolder, room_id);
		
	        
		} catch (Exception err) {
			log.error("[getListOfFiles]",err);
		}
		return null;
	}
	
	public Boolean deleteFile(String SID, String fileName, String moduleName, String parentFolder, Long room_id){
		try {
			return ConferenceLibrary.getInstance().deleteFile(SID, fileName, moduleName, parentFolder, room_id);
		} catch (Exception err) {
			log.error("[deleteFile]",err);
		}
		return null;
	}
	
	public TestObject getTestObject(){
		TestObject textO = new TestObject();
		textO.setList1(new LinkedList<String>());
		textO.setList2(new LinkedList<String>());
		return new TestObject();
	}
	
    public OMElement echo(OMElement element) throws XMLStreamException {
        //Praparing the OMElement so that it can be attached to another OM Tree.
        //First the OMElement should be completely build in case it is not fully built and still
        //some of the xml is in the stream.
        element.build();
        //Secondly the OMElement should be detached from the current OMTree so that it can be attached
        //some other OM Tree. Once detached the OmTree will remove its connections to this OMElement.
        element.detach();
        return element;
    }

    public void ping(OMElement element) throws XMLStreamException {
        //Do some processing
    	System.out.println("PING PING 1");
    	Long ch = Sessionmanagement.getInstance().checkSession("12312312");
    	System.out.println("PING PING 1 ch: "+ch);
    }
    
    public void pingF(OMElement element) throws AxisFault{
        throw new AxisFault("Fault being thrown");
    }
    
}
