package org.openmeetings.app.templates;

import java.io.StringWriter;

import org.slf4j.Logger;
import org.red5.logging.Red5LoggerFactory;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.openmeetings.app.data.basic.Configurationmanagement;

public class ScreenCastTemplate extends VelocityLoader{
	
	private static final String tamplateName = "screencast_template.vm";

	private static final Logger log = Red5LoggerFactory.getLogger(ScreenCastTemplate.class, "openmeetings");

	private ScreenCastTemplate() {
		super();
	}

	private ScreenCastTemplate(String path) {
		super(path);
	}
	
	private static ScreenCastTemplate instance = null;

	public static synchronized ScreenCastTemplate getInstance() {
		if (instance == null) {
			instance = new ScreenCastTemplate();
		}
		return instance;
	}
	public static synchronized ScreenCastTemplate getInstance(String path) {
		if (instance == null) {
			instance = new ScreenCastTemplate(path);
		}
		return instance;
	}	

	public String getScreenTemplate(String rtmphostlocal, String red5httpport, String SID, String ROOM, String DOMAIN){
        try {
	        /* lets make a Context and put data into it */
	
	        VelocityContext context = new VelocityContext();
	
	        context.put("rtmphostlocal", rtmphostlocal); //rtmphostlocal
	        context.put("red5httpport", red5httpport); //red5httpport
	        context.put("webAppRootKey", "openmeetings"); //TODO: Query webAppRootKey by Servlet
	        context.put("SID", SID);
	        context.put("ROOM", ROOM);
	        context.put("DOMAIN", DOMAIN);
	
	        /* lets render a template */
	
	        StringWriter w = new StringWriter();
	        
            Velocity.mergeTemplate(tamplateName, "UTF-8", context, w );
            
            return w.toString();         
            
        } catch (Exception e ) {
        	log.error("Problem merging template : " , e );
            //System.out.println("Problem merging template : " + e );
        }
        return null;
	}
}
