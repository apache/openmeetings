package org.openmeetings.axis.services;

import javax.servlet.ServletContext;

import org.apache.axis2.context.MessageContext;
import org.apache.axis2.transport.http.HTTPConstants;
import org.openmeetings.app.OpenmeetingsVariables;
import org.openmeetings.app.persistence.beans.rooms.Rooms;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class JabberWebServiceFacade {
	private static final Logger log = Red5LoggerFactory
			.getLogger(JabberWebServiceFacade.class,
					OpenmeetingsVariables.webAppRootKey);

	private ServletContext getServletContext() throws Exception {
		MessageContext mc = MessageContext.getCurrentMessageContext();
		return ((ServletContext) mc
				.getProperty(HTTPConstants.MC_HTTP_SERVLETCONTEXT));
	}

	private JabberWebService getJabberServiceProxy() {
		try {
			ApplicationContext context = WebApplicationContextUtils
					.getWebApplicationContext(getServletContext());

			return ((JabberWebService) context.getBean("jabberWebService"));
		} catch (Exception err) {
			log.error("[getJabberServiceProxy]", err);
		}
		return null;
	}

	@SuppressWarnings("cast")
	public Rooms[] getAvailableRooms(String SID) {
		return ((Rooms[]) getJabberServiceProxy().getAvailableRooms(SID)
				.toArray(new Rooms[0]));
	}

	public int getUserCount(String SID, Long roomId) {
		return getJabberServiceProxy().getUserCount(SID, roomId);
	}

	public String getInvitationHash(String SID, String username, Long room_id) {
		return getJabberServiceProxy()
				.getInvitationHash(SID, username, room_id);
	}
}