package org.openmeetings.app.templates;

import java.io.StringWriter;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class RequestContactConfirmTemplate extends VelocityLoader {

	private static final String tamplateName = "requestcontactconfirm.vm";

	private static final Logger log = Red5LoggerFactory.getLogger(
			RequestContactConfirmTemplate.class,
			ScopeApplicationAdapter.webAppRootKey);

	public String getRequestContactTemplate(String message) {
		try {

			super.init();

			/* lets make a Context and put data into it */

			VelocityContext context = new VelocityContext();

			context.put("message", message);

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
