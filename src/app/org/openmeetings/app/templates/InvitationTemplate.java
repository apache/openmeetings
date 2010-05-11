package org.openmeetings.app.templates;

import java.io.StringWriter;

import org.slf4j.Logger;
import org.red5.logging.Red5LoggerFactory;
import org.red5.server.api.IScope;
import org.red5.server.api.Red5;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.openmeetings.app.data.basic.Configurationmanagement;
import org.openmeetings.app.data.basic.Fieldmanagment;
import org.openmeetings.app.hibernate.beans.lang.Fieldlanguagesvalues;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;

public class InvitationTemplate extends VelocityLoader{
	
	private static final String tamplateName = "invitation.vm";

	private static final Logger log = Red5LoggerFactory.getLogger(InvitationTemplate.class, ScopeApplicationAdapter.webAppRootKey);

	private InvitationTemplate() {
		super();
	}

	private static InvitationTemplate instance = null;

	public static synchronized InvitationTemplate getInstance() {
		if (instance == null) {
			instance = new InvitationTemplate();
		}
		return instance;
	}

	public String getRegisterInvitationTemplate(String user, 
			                                    String message, 
			                                    String invitation_link, 
			                                    Long default_lang_id,
			                                    String dStart,
			                                    String dEnd){
        try {
        	
        	Fieldlanguagesvalues labelid500 = Fieldmanagment.getInstance().getFieldByIdAndLanguage(new Long(500), default_lang_id);
        	Fieldlanguagesvalues labelid501 = Fieldmanagment.getInstance().getFieldByIdAndLanguage(new Long(501), default_lang_id);
        	Fieldlanguagesvalues labelid502 = Fieldmanagment.getInstance().getFieldByIdAndLanguage(new Long(502), default_lang_id);
        	Fieldlanguagesvalues labelid503 = Fieldmanagment.getInstance().getFieldByIdAndLanguage(new Long(503), default_lang_id);
        	Fieldlanguagesvalues labelid504 = Fieldmanagment.getInstance().getFieldByIdAndLanguage(new Long(504), default_lang_id);
        	Fieldlanguagesvalues labelid505 = Fieldmanagment.getInstance().getFieldByIdAndLanguage(new Long(505), default_lang_id);
        	Fieldlanguagesvalues labelid570 = Fieldmanagment.getInstance().getFieldByIdAndLanguage(new Long(570), default_lang_id);
        	Fieldlanguagesvalues labelid571 = Fieldmanagment.getInstance().getFieldByIdAndLanguage(new Long(571), default_lang_id);
        	
	        /* lets make a Context and put data into it */
	        VelocityContext context = new VelocityContext();
	
	        context.put("user", user);
	        context.put("message", message);
	        context.put("invitation_link", invitation_link);
	        context.put("invitation_link2", invitation_link);
	        if (dStart != "")
	        {
	        	context.put("invitation_times", labelid570.getValue() + ": " + dStart + " - " + labelid571.getValue() + ": " + dEnd + "<br/><br/>");
	        }
	        else
	        {
	        	context.put("invitation_times", "" );
	        }
	        context.put("labelid500", labelid500.getValue());
	        context.put("labelid501", labelid501.getValue());
	        context.put("labelid502", labelid502.getValue());
	        context.put("labelid503", labelid503.getValue());
	        context.put("labelid504", labelid504.getValue());
	        context.put("labelid505", labelid505.getValue());
	
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
	
	
	/**
	 * 
	 * @param user
	 * @param message
	 * @param invitation_link
	 * @param default_lang_id
	 * @return
	 */
	public String getReminderInvitationTemplate(String user, String message, String invitation_link, Long default_lang_id){
        
		log.debug("getReminderInvitationTemplate");
		
		try {
        	
        	Fieldlanguagesvalues labelid622 = Fieldmanagment.getInstance().getFieldByIdAndLanguage(new Long(622), default_lang_id);
        	Fieldlanguagesvalues labelid623 = Fieldmanagment.getInstance().getFieldByIdAndLanguage(new Long(623), default_lang_id);
        	Fieldlanguagesvalues labelid624 = Fieldmanagment.getInstance().getFieldByIdAndLanguage(new Long(624), default_lang_id);
        	Fieldlanguagesvalues labelid625 = Fieldmanagment.getInstance().getFieldByIdAndLanguage(new Long(625), default_lang_id);
        	Fieldlanguagesvalues labelid626 = Fieldmanagment.getInstance().getFieldByIdAndLanguage(new Long(626), default_lang_id);
        	Fieldlanguagesvalues labelid627 = Fieldmanagment.getInstance().getFieldByIdAndLanguage(new Long(627), default_lang_id);
        	
	        /* lets make a Context and put data into it */
	        VelocityContext context = new VelocityContext();
	
	        context.put("user", user);
	        context.put("message", message);
	        context.put("invitation_link", invitation_link);
	        context.put("invitation_link2", invitation_link);
	        context.put("labelid500", labelid622.getValue());
	        context.put("labelid501", labelid623.getValue());
	        context.put("labelid502", labelid624.getValue());
	        context.put("labelid503", labelid625.getValue());
	        context.put("labelid504", labelid626.getValue());
	        context.put("labelid505", labelid627.getValue());
	         
	
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
