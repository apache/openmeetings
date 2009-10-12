package org.openmeetings.app.data.conference;

import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.openmeetings.app.hibernate.beans.lang.Fieldlanguagesvalues;
import org.openmeetings.app.data.basic.Configurationmanagement;
import org.openmeetings.app.data.basic.Fieldmanagment;
import org.openmeetings.app.templates.FeedbackTemplate;
import org.openmeetings.utils.mail.MailHandler;

public class Feedbackmanagement {
	private static final Logger log = Logger.getLogger(Feedbackmanagement.class);

	private static Feedbackmanagement instance;

	private Feedbackmanagement() {}

	public static synchronized Feedbackmanagement getInstance() {
		if (instance == null) {
			instance = new Feedbackmanagement();
		}
		return instance;
	}
	
	public String sendFeedback (String username, String email, String message){
		try {
			Integer default_lang_id = Integer.valueOf(Configurationmanagement.getInstance().
	        		getConfKey(3,"default_lang_id").getConf_value()).intValue();
				
			String template = FeedbackTemplate.getInstance().getFeedBackTemplate(username, email, message, default_lang_id);
		
			Fieldlanguagesvalues fValue = Fieldmanagment.getInstance().getFieldByIdAndLanguage(new Long(499), new Long(default_lang_id));
			return MailHandler.sendMail("openmeetings-user@googlegroups.com", fValue.getValue(), template);

		} catch (Exception err){
			log.error("sendInvitationLink",err);
		}
		return null;
	}
}
