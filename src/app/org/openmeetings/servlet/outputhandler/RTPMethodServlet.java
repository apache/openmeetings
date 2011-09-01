package org.openmeetings.servlet.outputhandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openmeetings.app.data.basic.Sessionmanagement;
import org.openmeetings.app.persistence.beans.recording.RoomClient;
import org.openmeetings.app.remote.red5.ClientListManager;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.openmeetings.app.rtp.RTPScreenSharingSession;
import org.openmeetings.app.rtp.RTPStreamingHandler;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * 
 * @author o.becherer
 * 
 */
public class RTPMethodServlet extends HttpServlet {
	private static final long serialVersionUID = 6781441755140201613L;

	private static final Logger log = Red5LoggerFactory.getLogger(
			ScreenRequestHandler.class, ScopeApplicationAdapter.webAppRootKey);
	public static final String METHOD_START = "streamer_start";
	public static final String METHOD_STOP = "streamer_stop";

	public Sessionmanagement getSessionManagement() {
		try {
			if (ScopeApplicationAdapter.initComplete) {
				ApplicationContext context = WebApplicationContextUtils
						.getWebApplicationContext(getServletContext());
				return (Sessionmanagement) context.getBean("sessionManagement");
			}
		} catch (Exception err) {
			log.error("[getSessionManagement]", err);
		}
		return null;
	}

	public ScopeApplicationAdapter getScopeApplicationAdapter() {
		try {
			if (ScopeApplicationAdapter.initComplete) {
				ApplicationContext context = WebApplicationContextUtils
						.getWebApplicationContext(getServletContext());
				return (ScopeApplicationAdapter) context
						.getBean("web.handler");
			}
		} catch (Exception err) {
			log.error("[getScopeApplicationAdapter]", err);
		}
		return null;
	}

	public ClientListManager getClientListManager() {
		try {
			if (ScopeApplicationAdapter.initComplete) {
				ApplicationContext context = WebApplicationContextUtils
						.getWebApplicationContext(getServletContext());
				return (ClientListManager) context.getBean("openmeetings.ClientListManager");
			}
		} catch (Exception err) {
			log.error("[getClientListManager]", err);
		}
		return null;
	}

	public RTPStreamingHandler getRtpStreamingHandler() {
		try {
			if (ScopeApplicationAdapter.initComplete) {
				ApplicationContext context = WebApplicationContextUtils
						.getWebApplicationContext(getServletContext());
				return (RTPStreamingHandler) context
						.getBean("rtpStreamingHandler");
			}
		} catch (Exception err) {
			log.error("[getRtpStreamingHandler]", err);
		}
		return null;
	}

	@Override
	protected void service(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		// define Method
		String method = request.getParameter("method");

		// Streming Client says GO!
		if (method.equals(METHOD_START)) {
			startStreaming(request);
		} else if (method.equals(METHOD_STOP)) {
			stopStreaming(request);
		}

	}

	/**
	 * @param request
	 */
	private void stopStreaming(HttpServletRequest request) {
		try {

			if (getScopeApplicationAdapter() == null
					|| getRtpStreamingHandler() == null
					|| getClientListManager() == null
					|| getSessionManagement() == null) {
				return;
			}

			String room = request.getParameter("room");

			if (room == null || room.length() < 1)
				throw new ServletException(
						"RTPMethodServlet.startStreaming : no parameter room!");

			// TODO get Userdefinitions from ServletCall
			String sid = request.getParameter("sid");

			if (sid == null || sid.length() < 1)
				throw new ServletException(
						"RTPMethodServlet.startStreaming : no parameter sid!");

			String publicSID = request.getParameter("publicSID");
			if (publicSID == null)
				throw new ServletException(
						"RTPMethodServlet.startStreaming : no parameter publicSID!");

			RTPScreenSharingSession session = getRtpStreamingHandler()
					.getSessionForRoom(room, sid, publicSID);

			session.getReceiver().stop();

			/**
			 * Notify Clients, that user started streaming -> showing users
			 * button for Appletstart
			 */
			LinkedHashMap<String, Object> hs = new LinkedHashMap<String, Object>();
			hs.put("message", "stopStreaming");
			hs.put("session", session);

			getScopeApplicationAdapter().sendMessageByRoomAndDomain(
					Long.valueOf(room).longValue(), hs);

			// Remove Session
			getRtpStreamingHandler().removeSessionForRoom(room, sid);

		} catch (Exception err) {
			log.error("[startStreaming]", err);
		}
	}

	/**
	 * Notify Clients, that Sharing Clients starts streaming
	 */
	private void startStreaming(HttpServletRequest request)
			throws ServletException {

		try {

			String width, height, sid = null;

			String room = request.getParameter("room");

			if (room == null || room.length() < 1)
				throw new ServletException(
						"RTPMethodServlet.startStreaming : no parameter room!");

			sid = request.getParameter("sid");

			if (sid == null || sid.length() < 1)
				throw new ServletException(
						"RTPMethodServlet.startStreaming : no parameter SID!");

			String publicSID = request.getParameter("publicSID");

			if (sid == null || sid.length() < 1)
				throw new ServletException(
						"RTPMethodServlet.startStreaming : no parameter publicSid!");

			if (publicSID == null)
				throw new ServletException(
						"RTPMethodServlet.startStreaming : no parameter publicSID!");

			String sharerIP = request.getParameter("sharerIP");

			if (sharerIP == null || sharerIP.length() < 1)
				throw new ServletException(
						"RTPMethodServlet.startStreaming : no parameter sharerIP!");

			String hostIP = request.getParameter("host");

			if (hostIP == null || hostIP.length() < 1)
				throw new ServletException(
						"RTPMethodServlet.startStreaming : no parameter host!");

			String sharersPort = request.getParameter("rtpport");
			if (sharersPort == null || sharersPort.length() < 1)
				throw new ServletException(
						"RTPMethodServlet.startStreaming : no parameter rtpport!");

			RTPScreenSharingSession session = getRtpStreamingHandler()
					.getSessionForRoom(room, sid, publicSID);

			if (session == null) {
				Long users_id = getSessionManagement().checkSession(sid);
				session = getRtpStreamingHandler().storeSessionForRoom(room,
						users_id, publicSID, hostIP,
						Integer.parseInt(sharersPort));
			}

			width = request.getParameter("width");

			if (width != null && width.length() > 0) {
				try {
					int width_i = Integer.parseInt(width);
					session.setStreamWidth(width_i);
				} catch (NumberFormatException nfe) {
					log.error("Invalid parameter width as Servletparameter - ignored!");
				}
			}

			height = request.getParameter("height");

			if (height != null && height.length() > 0) {
				try {
					int height_i = Integer.parseInt(height);
					session.setStreamHeight(height_i);
				} catch (NumberFormatException nfe) {
					log.error("Invalid parameter height as Servletparameter - ignored!");
				}
			}

			// Starting ReceiverThread
			session.startReceiver(getClientListManager());

			log.debug("startStreaming values : IPAddress Sharer : " + sharerIP
					+ ", width=" + width + ", height=" + height + ",room="
					+ room);
			log.debug("startStreaming publicSID=" + publicSID);

			// we have to include the publicSID to get the RoomClient Object
			// also the HOST, PORT must be set correctly in the
			// RTPScreenSharingSession-Object
			RoomClient rcl = getClientListManager().getClientByPublicSID(
					publicSID);

			HashMap<String, Integer> viewers = session.getViewers();

			Iterator<String> iter = viewers.keySet().iterator();

			while (iter.hasNext()) {
				String publicSIDofViewer = iter.next();

				log.debug("Notifying Client with publicSid = "
						+ publicSIDofViewer);

				/**
				 * Notify Clients, that user started streaming -> showing users
				 * button for Appletstart
				 */
				LinkedHashMap<String, Object> hs = new LinkedHashMap<String, Object>();
				hs.put("message", "startStreaming");
				// Set the User Object

				hs.put("publicSID", publicSIDofViewer);
				hs.put("room", rcl.getRoom_id());

				hs.put("rcl", rcl);
				// Set the Screen Sharing Object
				hs.put("session", session);

				getScopeApplicationAdapter().sendMessageWithClientByPublicSID(
						hs, publicSIDofViewer);

			}

		} catch (Exception err) {
			log.error("[startStreaming]", err);
		}

	}

}
