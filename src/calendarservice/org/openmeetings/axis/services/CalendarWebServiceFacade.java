package org.openmeetings.axis.services;

import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.transport.http.HTTPConstants;
import org.openmeetings.app.persistence.beans.calendar.Appointment;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class CalendarWebServiceFacade {

	private static final Logger log = Red5LoggerFactory.getLogger(
			RoomWebServiceFacade.class, ScopeApplicationAdapter.webAppRootKey);

	private ServletContext getServletContext() throws Exception {
		MessageContext mc = MessageContext.getCurrentMessageContext();
		return (ServletContext) mc
				.getProperty(HTTPConstants.MC_HTTP_SERVLETCONTEXT);
	}

	private CalendarWebService getCalendarServiceProxy() {
		try {
			ApplicationContext context = WebApplicationContextUtils
					.getWebApplicationContext(getServletContext());
			return (CalendarWebService) context.getBean("calendarWebService");
		} catch (Exception err) {
			log.error("[getCalendarServiceProxy]", err);
		}
		return null;
	}


}
