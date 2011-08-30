package org.openmeetings.app.templates;

import java.io.StringWriter;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.openmeetings.app.data.basic.Fieldmanagment;
import org.openmeetings.app.persistence.beans.lang.Fieldlanguagesvalues;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class ResetPasswordTemplate extends VelocityLoader {

	@Autowired
	private Fieldmanagment fieldmanagment;

	private static final String tamplateName = "resetPass.vm";

	private static final Logger log = Red5LoggerFactory.getLogger(
			FeedbackTemplate.class, ScopeApplicationAdapter.webAppRootKey);

	public String getResetPasswordTemplate(String reset_link,
			Long default_lang_id) {
		try {

			super.init();

			Fieldlanguagesvalues labelid513 = fieldmanagment
					.getFieldByIdAndLanguage(new Long(513), default_lang_id);
			Fieldlanguagesvalues labelid514 = fieldmanagment
					.getFieldByIdAndLanguage(new Long(514), default_lang_id);
			Fieldlanguagesvalues labelid515 = fieldmanagment
					.getFieldByIdAndLanguage(new Long(515), default_lang_id);
			Fieldlanguagesvalues labelid516 = fieldmanagment
					.getFieldByIdAndLanguage(new Long(516), default_lang_id);

			/* lets make a Context and put data into it */
			VelocityContext context = new VelocityContext();
			context.put("reset_link", reset_link);
			context.put("reset_link2", reset_link);
			context.put("labelid513", labelid513.getValue());
			context.put("labelid514", labelid514.getValue());
			context.put("labelid515", labelid515.getValue());
			context.put("labelid516", labelid516.getValue());

			/* lets render a template */
			StringWriter w = new StringWriter();
			Velocity.mergeTemplate(tamplateName, "UTF-8", context, w);

			return w.toString();

		} catch (Exception e) {
			log.error("Problem merging template : ", e);
			// System.out.println("Problem merging template : " + e );
		}
		return null;
	}

}
