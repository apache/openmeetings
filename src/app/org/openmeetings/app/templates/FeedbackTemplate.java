package org.openmeetings.app.templates;

import java.io.StringWriter;

import org.slf4j.Logger;
import org.red5.logging.Red5LoggerFactory;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.openmeetings.app.data.basic.Configurationmanagement;
import org.openmeetings.app.data.basic.Fieldmanagment;
import org.openmeetings.app.hibernate.beans.lang.Fieldlanguagesvalues;

public class FeedbackTemplate extends VelocityLoader{
	
	private static final String tamplateName = "feedback.vm";

	private static final Logger log = Red5LoggerFactory.getLogger(FeedbackTemplate.class, "openmeetings");

	private FeedbackTemplate() {
		super();
	}

	private static FeedbackTemplate instance = null;

	public static synchronized FeedbackTemplate getInstance() {
		if (instance == null) {
			instance = new FeedbackTemplate();
		}
		return instance;
	}

	public String getFeedBackTemplate(String username, String email, String message, Integer default_lang_id){
        try {
        	
        	//TODO: Finish Feedback - Template
        	//Fieldlanguagesvalues fValue = Fieldmanagment.getInstance().getFieldByIdAndLanguage(new Long(499), new Long(default_lang_id));
        	
	        /* lets make a Context and put data into it */
	
	        VelocityContext context = new VelocityContext();
	
	        context.put("username", username);
	        context.put("email", email);
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
