package org.openmeetings.app.templates;

import java.io.StringWriter;

import org.slf4j.Logger;
import org.red5.logging.Red5LoggerFactory;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.openmeetings.app.data.basic.Configurationmanagement;
import org.openmeetings.app.data.basic.Fieldmanagment;
import org.openmeetings.app.persistence.beans.lang.Fieldlanguagesvalues;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;

public class RequestContactTemplate extends VelocityLoader{
	
	private static final String tamplateName = "requestcontact.vm";

	private static final Logger log = Red5LoggerFactory.getLogger(RequestContactTemplate.class, ScopeApplicationAdapter.webAppRootKey);

	private RequestContactTemplate() {
		super();
	}

	private static RequestContactTemplate instance = null;

	public static synchronized RequestContactTemplate getInstance() {
		if (instance == null) {
			instance = new RequestContactTemplate();
		}
		return instance;
	}

	public String getRequestContactTemplate(String message, String accept_link, 
			String deny_link, String openmeetings_link){
        try {
        	
	        /* lets make a Context and put data into it */
	
	        VelocityContext context = new VelocityContext();
	
	        context.put("message", message);
	        context.put("accept_link", accept_link);
	        context.put("deny_link", deny_link);
    		context.put("openmeetings_link", openmeetings_link);
	
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
