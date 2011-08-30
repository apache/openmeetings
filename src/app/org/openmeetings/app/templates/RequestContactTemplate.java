package org.openmeetings.app.templates;

import java.io.StringWriter;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class RequestContactTemplate extends VelocityLoader {

	private static final String tamplateName = "requestcontact.vm";

	private static final Logger log = Red5LoggerFactory
			.getLogger(RequestContactTemplate.class,
					ScopeApplicationAdapter.webAppRootKey);

	public String getRequestContactTemplate(String message, String accept_link,
			String deny_link, String openmeetings_link) {
		try {

			super.init();

			/* lets make a Context and put data into it */

			VelocityContext context = new VelocityContext();

			context.put("message", message);
			context.put("accept_link", accept_link);
			context.put("deny_link", deny_link);
			context.put("openmeetings_link", openmeetings_link);

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
