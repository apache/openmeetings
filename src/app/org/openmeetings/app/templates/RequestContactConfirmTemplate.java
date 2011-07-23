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

public class RequestContactConfirmTemplate extends VelocityLoader{
	
	private static final String tamplateName = "requestcontactconfirm.vm";

	private static final Logger log = Red5LoggerFactory.getLogger(RequestContactConfirmTemplate.class, ScopeApplicationAdapter.webAppRootKey);

	private RequestContactConfirmTemplate() {
		super();
	}

	private static RequestContactConfirmTemplate instance = null;

	public static synchronized RequestContactConfirmTemplate getInstance() {
		if (instance == null) {
			instance = new RequestContactConfirmTemplate();
		}
		return instance;
	}

	public String getRequestContactTemplate(String message) {
        try {
        	
	        /* lets make a Context and put data into it */
	
	        VelocityContext context = new VelocityContext();
	
	        context.put("message", message);
	
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
