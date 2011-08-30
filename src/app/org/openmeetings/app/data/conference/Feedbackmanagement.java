package org.openmeetings.app.data.conference;

import org.openmeetings.app.data.basic.Configurationmanagement;
import org.openmeetings.app.data.basic.Fieldmanagment;
import org.openmeetings.app.persistence.beans.lang.Fieldlanguagesvalues;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.openmeetings.app.templates.FeedbackTemplate;
import org.openmeetings.utils.mail.MailHandler;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class Feedbackmanagement {
	private static final Logger log = Red5LoggerFactory.getLogger(
			Feedbackmanagement.class, ScopeApplicationAdapter.webAppRootKey);

	@Autowired
	private Configurationmanagement cfgManagement;
	@Autowired
	private Fieldmanagment fieldmanagment;
	@Autowired
	private MailHandler mailHandler;
	@Autowired
	private FeedbackTemplate feedbackTemplate;

	public String sendFeedback(String username, String email, String message) {
		try {
			Integer default_lang_id = Integer.valueOf(
					cfgManagement.getConfKey(3, "default_lang_id")
							.getConf_value()).intValue();

			String template = feedbackTemplate.getFeedBackTemplate(username,
					email, message, default_lang_id);

			Fieldlanguagesvalues fValue = fieldmanagment
					.getFieldByIdAndLanguage(new Long(499), new Long(
							default_lang_id));
			return mailHandler.sendMail("openmeetings-user@googlegroups.com",
					fValue.getValue(), template);

		} catch (Exception err) {
			log.error("sendInvitationLink", err);
		}
		return null;
	}
}
